package org.emmef.audio.noisereduction;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.emmef.audio.frame.FrameType;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.audio.noisedetection.NrMeasurementSettings;
import org.emmef.audio.noisereduction.BandSplitFilterSet.Direction;
import org.emmef.config.options.Builder;
import org.emmef.config.options.Options;
import org.emmef.config.options.SwitchBuilder;
import org.emmef.config.options.Value;
import org.emmef.config.program.Program;
import org.emmef.config.program.ProgramUtils;
import org.emmef.logging.Logger;

public class NoiseRemover implements Program {
	private static final Logger logger = Logger.getDefault();
	
	Builder cmd = Options.create("NoiseRemover");
	private final SwitchBuilder noiseMeasurement =
		cmd.optional("-n", "--noise-measurement").describedBy("Describes how noise is measured");
	
	private final Value<Integer> minSnRationDb =
		noiseMeasurement.optional("-m", "--min-snratio-db").describedBy("Minimum S/N ratio")
		.mandatory().integer().restrictTo(20, 80).defaults(30).name("Minimum S/N ratio")
		.describedBy("The S/N ratio in dB, relative to the maximum measured RMS value");
	private final Value<Integer> maxSnRationDb =
		noiseMeasurement.mandatory("-M", "--max-snratio-db").describedBy("Maximum S/N ratio")
		.mandatory().integer().restrictTo(40, 120).defaults(75).name("Maximum S/N ratio")
		.describedBy("The S/N ratio in dB, relative to the maximum measured RMS value.\n" +
				"Signal levels below this level are not taken into account for measuring\n" +
				"noise levels. See also --skip-rms-window.");
	private final Value<Double> maxRmsWindow =
		noiseMeasurement.optional("-rw", "--rms-window").describedBy("Window size for maximum RMS measurement")
		.mandatory().real().restrictTo(0.010, 0.1).defaults(0.050).name("RMS measurement window size" )
		.describedBy("The window size (in seconds)");
	private final Value<Double> noiseRmsWindow =
		noiseMeasurement.optional("-nw", "--noise-rms-window").describedBy("Window size for noise measurement")
		.mandatory().real().restrictTo(0.050, 5.0).defaults(0.300).name("Noise measurement window size")
		.describedBy(
				"The window size in seconds.\n" +
				"The noise level is determined by seeking a window of this size, with the minimum RMS value.");
	private final Value<Double> skipMarkRmsWindow =
		noiseMeasurement.optional("-sw", "--skip-rms-window").describedBy("The RMS window size to determine dropouts")
		.mandatory().real().restrictTo(0.010, 1.000).defaults(0.200).name("Dropout measurement window size")
		.describedBy(
				"Window size (in seconds).\n" +
				"If an area of this size drops below the maximum RMS/ratio, it is considered a dropout and " +
				"will not be taken into account for noise measuring.");
	private final Value<Double> skipStartSeconds =
		noiseMeasurement.optional("-ss", "--skip-from-start").describedBy("Skip seconds from start for noise measurement.")
		.mandatory().real().restrictTo(0.0, 60.0).defaults(0.1).name("Skip seconds from start")
		.describedBy("Seconds to wait before starting noise measurement");
	private final Value<Double> skipEndSeconds =
		noiseMeasurement.optional("-se", "--skip-from-end").describedBy("Skip seconds from end for noise measurement.")
		.mandatory().real().restrictTo(0.0, 60.0).defaults(0.1).name("Skip seconds from end")
		.describedBy("Seconds from the end of the song, to stop noise measurement");
	private final Value<Double> timeMeasurement =
		noiseMeasurement.optional("-tm", "--times-noise-measurement").describedBy("Noise measurement time(s)")
		.mandatory().real().restrictTo(0.001, 0.200).multiple().defaults(0.025, 0.075).name("Noise measurement times")
		.describedBy("These are the windows in seconds that are used to detect the sound level when " +
				"reducing noise. " +
				"The largest value is used for the lowest frequency, while the smallest " +
				"is used for the highest frequency. If only one value is defined, this will be the time " +
				"used for all frequency bands.");
	private final Value<Double> timeAttack =
		noiseMeasurement.optional("-ta", "--times-attack").describedBy("Noise reduction attack time(s)")
		.mandatory().real().restrictTo(0.0005, 0.100).multiple().defaults(0.0015, 0.010).name("Noise reduction attack time(s)")
		.describedBy("This is the attack time in seconds of the amplifier. The amplifier follows " +
				"the level as detected in the --times-noise-measurement with an attack (rising level) " +
				"and release (sinking level) to ensure smooth transition. " +
				"The largest value is used for the lowest frequency, while the smallest " +
				"is used for the highest frequency. If only one value is defined, this will be the time " +
				"used for all frequency bands.");
	private final Value<Double> timeRelease =
		noiseMeasurement.optional("-tr", "--times-release").describedBy("Noise reduction release time(s)")
		.mandatory().real().restrictTo(0.002, 0.400).multiple().defaults(0.075, 0.200).name("Noise reduction release time(s)")
		.describedBy("This is the release time in seconds of the amplifier. The amplifier follows " +
				"the level as detected in the --times-noise-measurement with an attack (rising level) " +
				"and release (sinking level) to ensure smooth transition. " +
				"The largest value is used for the lowest frequency, while the smallest " +
				"is used for the highest frequency. If only one value is defined, this will be the time " +
				"used for all frequency bands.");
	private final Value<Integer> irregularNoiseMeasurement =
		noiseMeasurement.optional("-i", "--irregular-noise").describedBy("Irregular noise level measurement. " +
				"The noise level is measured with a relatively large RMS window, to ensure an accurate measurement. " +
				"However, Some battered media have a very irregular noise level, which would cause wispering just " +
				"above the measured noise level. To prevent this, parts where we measure the determined noise level, " +
				"with the large window, will be analyzed further with a maximum measurement over a smaller window. ")
		.mandatory().integer().restrictTo(0, 4).defaults(3).name("Irregular noise measurement modus").describedBy("Modus:\n" +
				"0 means no extra measurement\n" +
				"1 take the window size of the RMS measurement (--noise-rms-window)\n" +
				"2 take the window size of the noise measurement (--times-noise-measurement)\n" +
				"3 take whichever of (1) and (2) is smaller\n" +
				"4 take arithmic average of (1) and (2)");
	private final SwitchBuilder expansionReduction =
		cmd.optional("-E", "--expansion-noise-reduction").describedBy("Parameters for noise reduction, based on expansion");
	
	private final Value<Integer> expansionThreshold =
		expansionReduction.mandatory("-t", "--threshold").describedBy("Expansion threshold")
		.mandatory().integer().restrictTo(0, 24).defaults(6).name("Expansion threshold")
		.describedBy(
				"Below this level in dB, expansion kicks in with the --expand-ratio.\n" +
				"Above this level, there is no change in dynamics whatsoever");
	private final Value<Double> expansionFactor =
		expansionReduction.optional("-r", "--expand-ratio").describedBy("Expansion factor")
		.mandatory().real().restrictTo(1.25, 4.0).defaults(2.0).name("Expansion factor")
		.describedBy(
				"An expansion factor of 1.0 means no expansion at al (linear).\n" +
				"The output level for signal below the threshold, is calculated as follows:\n" +
				"    db_out = db_in(below threshold) * --expand-ratio.");

	private final SwitchBuilder subtractiveReduction =
		cmd.optional("-S", "--subtractive-noise-reduction").describedBy("Parameters for noise reduction, based on subtraction");
	
	private final Value<Double> subtractionFactor =
		subtractiveReduction.mandatory("-f", "--subtraction-factor").describedBy("Subtraction factor (dB)")
		.mandatory().real().restrictTo(-20.0, 20.0).defaults(0.0).name("Subtracttion factor (dB)")
		.describedBy(
				"The ouput level is the input level minus the noise level times the subtraction factor.\n" +
				"Below the noise level times the subtraction factor, there is no output at all.");
	private final Value<Double> subtractionRatio =
		subtractiveReduction.optional("-r", "--subtract-ratio").describedBy("Subtract ratio")
		.mandatory().real().restrictTo(0.0, 0.9).defaults(0.25).name("Subtract ratio")
		.describedBy(
				"The subtraction factor can grow, based on the ratio between the signal level and " +
				"the signal level. In this way, a smoother transition between reduction and no " +
				"reduction is done, but sound me be a little numbed.");

	private final SwitchBuilder crossoverSettings =
		cmd.optional("-c", "--crossovers").describedBy("Crossover properties");
	private final Value<Double> crossovers =
		crossoverSettings.mandatory().real().restrictTo(20.0, 16000.0).multiple().name("Frequency")
		.describedBy("Crossover frequency/frequencies in Hz. The number of frequency bands is always 1 more than the nuber of crossovers.");
	private final Value<Integer> filterOrder =
		crossoverSettings.optional("-o", "--filter-order").describedBy("Filter order")
		.mandatory().integer().restrictTo(1, 4).defaults(4)
		.describedBy("Filter order. The filter will be applied twice to create zero-phase resonse, so the actual " +
				"order is twice as high");
	private final Value<String> crossoverDirection =
		crossoverSettings.optional("-d", "--direction").describedBy("Direction of crossover filtering")
		.mandatory().text().defaults("down").name("direction").describedBy("The direction indictaes in which direction" +
				" the crossovers will be evaluated. Down means that the highest crossover will be filtered first.");
	private final Value<String> crossoverPreset =
		crossoverSettings.optional("-p", "--preset").describedBy("Crossover preset frequency sets")
		.mandatory().text().defaults("dnl").name("Predefined set of crossovers")
		.describedBy("There are a few prestes for crossover frquencies:\n" +
				"dnl    the original dynamic noise limiter by Philips, had one cut off at 4500 Hz\n" +
				"tertz  from about 70 to 11000 Hz, all in tertz");
	private final Value<File> inputFile =
		cmd.mandatory().file().validatedBy(ProgramUtils.EXISTING_NORMAL_READABLE_FILE_FILTER).name("inputFile")
		.describedBy("An existing audio input file.");
	private final Value<File> outputDirectory =
		cmd.mandatory().file().validatedBy(ProgramUtils.EXISTING_WRITABLE_TARGET_DIRECTORY).name("outputDirectory")
		.describedBy("An existing Output directory, to which outp files will be written.");

	public static void main(String[] args) {
		ProgramUtils.main(new NoiseRemover(), args);
	}

	@Override
	public void run(String[] args) throws Exception {
		Logger.getDefault().setLevel(Level.FINEST);
		String[] fakeArguments = new String[] {
			"/home/michel/Music/high-definition/Test_File_2_0_STEREO_PCM.wav",
			"/tmp",
			};
		cmd.parse(fakeArguments);
		logger.setLevel(Level.FINE);
		
		final SoundSource soundSource = SourceAndSinkProvider.createSource(URI.create("file:" + inputFile.getValue().getAbsolutePath()));
		logger.config("Reading \"" + soundSource + "\"");
		final String absolutePath = new File(outputDirectory.getValue(), inputFile.getValue().getName()).getAbsolutePath();
	
		try {
			final SoundSink soundSink = SourceAndSinkProvider.createWithSameMetaData(soundSource, URI.create("file:" + absolutePath));
			try {
				applyNoiseFilter(soundSource, soundSink);
			}
			finally {
				soundSink.close();
			}
			
		}
		finally {
			soundSource.close();
		}
	}

	private void applyNoiseFilter(final SoundSource soundSource, final SoundSink soundSink) throws IOException, InterruptedException {
		float[] samples;
		final FrameType frameType = soundSource.getMetrics().getAudioFormat();
		final long frameCount = soundSource.getMetrics().getFrames();
		logger.fine("Frames=%d; channels=%d", frameCount, frameType.channels);
		
		if (frameCount > Integer.MAX_VALUE/ frameType.channels) {
			throw new IllegalStateException("Too many frames (" + frameCount + ") in file for number of channels (" + frameType.channels + ")");
		}
		
		samples = new float[frameType.channels * (int)frameCount];
		
		logger.config("Read from " + soundSink);
		long reads = soundSource.readFrames(samples);
		if (reads != frameCount) {
			throw new IllegalStateException("Couldn't read complete file!");
		}
		
		logger.config("Filtering...");
		createNoiseFilter(frameType, samples).filter();
		
		logger.config("Write to " + soundSink);
		soundSink.writeFrames(samples);
	}

	private MultiBandNoiseFilter createNoiseFilter(final FrameType frameType, float[] samples) {
		final MultiBandNoiseFilter splitter;
		final Direction direction = BandSplitFilterSet.Direction.effectiveValueOf(crossoverDirection.getValue());
		
		final List<Double> crossoverValues = getCrossovers(crossovers.values(), crossoverPreset.getValue());
		final CrossoverInfo crossoverInfo = new CrossoverInfo(direction, filterOrder.getValue(), crossoverValues);
		logger.config(crossoverInfo);
		NrDynamicsFactory nrDynamicsFactory;
		if (expansionReduction.present()) {
			nrDynamicsFactory = new NrDynamicsFactory.Expansion(expansionThreshold.getValue(), expansionFactor.getValue());
		}
		else {
			nrDynamicsFactory = new NrDynamicsFactory.Subtraction(subtractionFactor.getValue(), subtractionRatio.getValue());
		}
		logger.config(nrDynamicsFactory);
		NrMeasurementSettings nrMeasurement = new NrMeasurementSettings(
				minSnRationDb.getValue(), maxSnRationDb.getValue(),
				maxRmsWindow.getValue(), noiseRmsWindow.getValue(), skipMarkRmsWindow.getValue(),
				skipStartSeconds.getValue(), skipEndSeconds.getValue(), irregularNoiseMeasurement.getValue());
		
		logger.config(nrMeasurement);
		final DefaultTimings timings = new DefaultTimings(crossoverInfo, timeMeasurement.values(), timeAttack.values(), timeRelease.values());
		logger.config(timings);
		
		splitter = new MultiBandNoiseFilter(samples, frameType, nrMeasurement, nrDynamicsFactory, crossoverInfo, timings);
		return splitter;
	}

	private List<Double> getCrossovers(final List<Double> crossoverList, final String presetName) {
		if (!crossoverList.isEmpty()) {
			return crossoverList;
		}
		if ("tertz".equalsIgnoreCase(presetName)) {
			return Arrays.<Double>asList(70.0, 89.0, 112.0, 141.0, 177.0, 223.0, 281.0, 354.0, 446.0, 563.0, 709.0, 893.0, 1125.0, 1417.0, 1786.0, 2250.0, 2835.0, 3572.0, 4500.0, 5670.0, 7143.0, 9000.0, 11339.0, 14287.0);
		}
		
		return Collections.singletonList(4500.0);
	}

	@Override
	public String getSynopsis() {
		StringBuilder synopsis = new StringBuilder();
		
		synopsis.append("USAGE:\n\tNoiseRemover").append(cmd.getCommandLineSummary()).append("\n");
		synopsis.append("SYNOPSIS:\nOptions and description\n").append(cmd.getSynopsis()).append("\n");
		
		return synopsis.toString();
	}
}
