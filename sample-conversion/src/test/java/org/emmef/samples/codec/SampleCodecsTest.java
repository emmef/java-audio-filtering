package org.emmef.samples.codec;

import static org.junit.Assert.*;

import java.nio.BufferUnderflowException;

import org.emmef.samples.codec.SampleCodec.Scheme;
import org.junit.Test;

/**
 * Tests {@link SampleCodec}s.
 * <p>
 * First, the limiting behavior is tested. For floating point serialization
 * schemes there is no  limiting behavior and that is tested. For integer based schemes
 * the codec contract specified that the minimum value of the integer is interpreted a {@code -1} and the maximum
 * value as <em>slightly less than</em> {@code +1}, the latter because of the asymmetry in
 * two's complement. So sample values are in fact confined to the range [{@code -1} .. {@code 1}&#x27E9;.
 * <p>
 * Second, the idempotency is tested, which
 * means that the result of an encoded value which is then decoded should be the
 * original value. For the integer base serialization schemes the tested values
 * are [{@code -1} .. {@code 1}&#x27E9;.
 */
public class SampleCodecsTest {
	private static final String HEXDIGITS = "0123456789abcdef";

	public SampleCodecsTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testIdempotencyForDoubles() {
		for (SampleCodec codec : SampleCodecs.values()) {
			CodecHelper helper = new CodecHelper(codec, true);
			testIdempotencyFor(helper);
		}
	}
	
	@Test
	public void testIdempotencyForFloats() {
		for (SampleCodec codec : SampleCodecs.values()) {
			CodecHelper helper = new CodecHelper(codec, false);
			testIdempotencyFor(helper);
		}
	}
	
	@Test
	public void testLimitingBehaviorForDouble() {
		for (SampleCodec codec : SampleCodecs.values()) {
			CodecHelper helper = new CodecHelper(codec, true);
			testLimitingFor(helper);
		}
	}
	
	@Test
	public void testLimitingBehaviorForFloats() {
		for (SampleCodec codec : SampleCodecs.values()) {
			CodecHelper helper = new CodecHelper(codec, false);
			testLimitingFor(helper);
		}
	}


	private void testIdempotencyFor(CodecHelper helper) {
		StringBuilder message = new StringBuilder();
		
		long min = Integer.MIN_VALUE;
		long max = -(long)Integer.MIN_VALUE;
		long steps = Math.min(helper.bitsmax, -SampleScales.MIN_VALUE_24_BIT);
		long step = Math.max(1, -(long)Integer.MIN_VALUE / steps);
		
		for (long i = min ; i <= max; i += step) {
			double input;
			if (i != max) {
				input = -1.0 * i / Integer.MIN_VALUE;
			}
			else {
				input = helper.ceiling;
			}
			double output = helper.encodeDecodeInSameSpace(input);
			double delta = input - output;

			if (Math.abs(delta) > helper.epsilon) {
				message.setLength(0);
				message.append("Codec=").append(helper.codec).append("; input=").append(input).append("; encoded=");
				helper.resetBuffer();
				for (int bit = 0; bit < helper.codec.bytesPerSample(); bit++) {
					int byteValue = helper.get();
					message.append(HEXDIGITS.charAt(0xf & byteValue >>> 4));
					message.append(HEXDIGITS.charAt(0xf & byteValue));
				}
				message.append("; decoded=").append(output).append("; delta=").append(delta).append("; epsilon-").append(helper.epsilon);
				message.insert(0, "Delta too big: ");
				System.err.println(message);
				output = helper.encodeDecodeInSameSpace(input);
				fail(message.toString());
			}
		}
	}
	private void testLimitingFor(CodecHelper helper) {
		
		double input = 0.1;
		
		for (input = 0.1; input < SampleScales.MAX_VALUE_24_BIT; input *= 1.5) {
			assertExpectedOutput(helper, input);
			assertExpectedOutput(helper, -input);
		}
	}

	private void assertExpectedOutput(CodecHelper helper, double input) {
		double output = helper.encodeDecodeInSameSpace(input);
		double epsilon = helper.epsilon * 2; // double faults of non-integer float values can give double faults
		double expected = input;
		boolean scalingType = helper.codec.getScheme() == Scheme.FLOAT;
		
		if (input > helper.ceiling) {
			if (scalingType) {
				epsilon *= input;
			}
			else {
				expected = helper.ceiling;
			}
		}
		else if (input < helper.floor) {
			if (scalingType) {
				epsilon *= -input;
			}
			else {
				expected = helper.floor;
			}
		}
		double delta = Math.abs(expected - output);
		int deltaFactor = (int)(100 * delta / epsilon);
		boolean condition = delta < epsilon;
		if (!condition) {
			String message = helper.codec + ": with input " + input + ", output (" + output + ") should be " + expected + "(" + deltaFactor + "% epsilon)";
			System.err.println(message);
			System.err.println(" " + output * helper.bitsmax + " <-> " + expected * helper.bitsmax + "\n" + helper.floor * helper.bitsmax + ".." + helper.ceiling * helper.bitsmax);
			fail(message);
		}
	}
	
	static class CodecHelper {
		public final double floor;
		public final double ceiling;
		public final double epsilon;
		public final double rescaling;
		public final SampleCodec codec;
		public final byte[] buffer;
		public final long bitsmax;
		private final boolean useDoubles;
		private int position;
		
		public CodecHelper(SampleCodec codec, boolean useDoubles) {
			this.codec = codec;
			this.useDoubles = useDoubles;
			bitsmax = 1L << Math.min(3, codec.bytesPerSample())*8 - 1;
			ceiling = codec.getScheme() == Scheme.TWOS_COMPLEMENT ? 1.0 * (bitsmax - 1) / bitsmax : 1.0;
			epsilon = 0.50001 / bitsmax; // double faults: to and fro
			floor = -1.0;
			buffer = new byte[16];
			rescaling = codec == SampleCodecs.FLOAT_COOLEDIT ? SampleScales.SCALE_24_FROM_DOUBLE : 1.0;
		}
		
		double encodeDecodeInSameSpace(double input) {
			if (useDoubles) {
				resetBuffer();
				codec.encodeDouble(input, buffer, 0);
				resetBuffer();
				return rescaling * codec.decodeDouble(buffer, 0);
			}
			else {
				resetBuffer();
				codec.encodeFloat((float)input, buffer, 0);
				resetBuffer();
				return rescaling *codec.decodeFloat(buffer, 0);
			}
		}
		
		public double getRescaling() {
			return rescaling;
		}
		
		void resetBuffer() {
			position = 0;
		}
		
		byte get() {
			if (position < buffer.length) {
				return buffer[position++];
			}
			
			throw new BufferUnderflowException();
		}
	}
}
