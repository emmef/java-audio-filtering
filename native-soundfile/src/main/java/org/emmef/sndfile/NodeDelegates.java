package org.emmef.sndfile;

import java.io.IOException;

import org.emmef.audio.format.FormatConverter;
import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.frame.Whence;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.audio.nodes.SoundSourceAndSink;
import org.emmef.audio.nodes.SoundSourceOrSink;

public class NodeDelegates {
	public static <P, T> SoundSink<P> delegateSink(final SoundSink<T> delegate, final FormatConverter<P, T> convertor) {
		checkNotNull(delegate);
		
		return new SoundSink<P>() {
			@Override
			public long writeFrames(double[] buffer) throws IOException {
				return delegate.writeFrames(buffer);
			}

			@Override
			public long writeFrames(float[] buffer) throws IOException {
				return delegate.writeFrames(buffer);
			}

			@Override
			public long writeFrames(double[] buffer, int frameCount) throws IOException {
				return delegate.writeFrames(buffer, frameCount);
			}

			@Override
			public long writeFrames(float[] buffer, int frameCount) throws IOException {
				return delegate.writeFrames(buffer, frameCount);
			}

			@Override
			public long seekFrame(long framePosition, Whence whence) throws IOException {
				return delegate.seekFrame(framePosition, whence);
			}

			@Override
			public void close() throws IOException {
				delegate.close();
			}

			@Override
			public SoundMetrics getMetrics() {
				return delegate.getMetrics();
			}

			@Override
			public P getMetaData() {
				return convertor.publish(delegate.getMetaData());
			}
		};
	}
	
	public static <T, P> SoundSource<P> delegateSource(final SoundSource<T> delegate, final FormatConverter<P, T> convertor) {
		checkNotNull(delegate);
		
		return new SoundSource<P>() {

			@Override
			public long readFrames(double[] buffer) throws IOException {
				return delegate.readFrames(buffer);
			}

			@Override
			public long readFrames(float[] buffer) throws IOException {
				return delegate.readFrames(buffer);
			}

			@Override
			public long readFrames(double[] buffer, int frameCount)
					throws IOException {
				return delegate.readFrames(buffer, frameCount);
			}

			@Override
			public P getMetaData() {
				return convertor.publish(delegate.getMetaData());
			}

			
			@Override
			public long readFrames(float[] buffer, int frameCount)
					throws IOException {
				return delegate.readFrames(buffer, frameCount);
			}

			@Override
			public long seekFrame(long framePosition, Whence whence)
					throws IOException {
				return delegate.seekFrame(framePosition, whence);
			}

			@Override
			public void close() throws IOException {
				delegate.close();
			}

			@Override
			public SoundMetrics getMetrics() {
				return delegate.getMetrics();
			}
		};
	}
	
	public static <T, P> SoundSourceAndSink<P> delegateSourceAndSink(final SoundSourceAndSink<T> delegate, final FormatConverter<P, T> convertor) {
		checkNotNull(delegate);
		
		return new SoundSourceAndSink<P>() {
			public long writeFrames(double[] buffer) throws IOException {
				return delegate.writeFrames(buffer);
			}

			public long readFrames(double[] buffer) throws IOException {
				return delegate.readFrames(buffer);
			}

			public long writeFrames(float[] buffer) throws IOException {
				return delegate.writeFrames(buffer);
			}

			public long readFrames(float[] buffer) throws IOException {
				return delegate.readFrames(buffer);
			}

			public long writeFrames(double[] buffer, int frameCount)
					throws IOException {
				return delegate.writeFrames(buffer, frameCount);
			}

			public long readFrames(double[] buffer, int frameCount)
					throws IOException {
				return delegate.readFrames(buffer, frameCount);
			}

			public P getMetaData() {
				return convertor.publish(delegate.getMetaData());
			}

			public long readFrames(float[] buffer, int frameCount)
					throws IOException {
				return delegate.readFrames(buffer, frameCount);
			}

			public long writeFrames(float[] buffer, int frameCount)
					throws IOException {
				return delegate.writeFrames(buffer, frameCount);
			}

			public long seekFrame(long framePosition, Whence whence)
					throws IOException {
				return delegate.seekFrame(framePosition, whence);
			}

			public void close() throws IOException {
				delegate.close();
			}

			@Override
			public SoundMetrics getMetrics() {
				return delegate.getMetrics();
			}
		};
	}

	private static <T> void checkNotNull(final SoundSourceOrSink<T> delegate) {
		if (delegate == null) {
			throw new NullPointerException("delegate");
		}
	}
}
