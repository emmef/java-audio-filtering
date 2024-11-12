package org.emmef.sndfile;

import java.io.IOException;

import org.emmef.audio.format.SoundMetrics;
import org.emmef.audio.format.conversion.FormatConverter;
import org.emmef.audio.frame.Whence;
import org.emmef.audio.nodes.SoundSink;
import org.emmef.audio.nodes.SoundSource;
import org.emmef.audio.nodes.SoundSourceAndSink;
import org.emmef.audio.nodes.SoundSourceOrSink;

public class NodeDelegates {
	public static <T, P> SoundSink delegateSink(final SoundSink delegate, final FormatConverter<T, P> convertor) {
		checkNotNull(delegate);
		
		return new SoundSink() {
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
			public Object getMetaData() {
				@SuppressWarnings("unchecked")
				P cast = (P)delegate.getMetaData();
				return convertor.publish(cast);
			}
			
			@Override
			public String toString() {
				return delegate.toString();
			}
		};
	}
	
	public static <T, P> SoundSource delegateSource(final SoundSource delegate, final FormatConverter<T, P> convertor) {
		checkNotNull(delegate);
		
		return new SoundSource() {

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
			public Object getMetaData() {
				@SuppressWarnings("unchecked")
				P cast = (P)delegate.getMetaData();
				return convertor.publish(cast);
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
			
			@Override
			public String toString() {
				return delegate.toString();
			}
		};
	}
	
	public static <T, P> SoundSourceAndSink delegateSourceAndSink(final SoundSourceAndSink delegate, final FormatConverter<T, P> convertor) {
		checkNotNull(delegate);
		
		return new SoundSourceAndSink() {
			@Override
			public long writeFrames(double[] buffer) throws IOException {
				return delegate.writeFrames(buffer);
			}

			@Override
			public long readFrames(double[] buffer) throws IOException {
				return delegate.readFrames(buffer);
			}

			@Override
			public long writeFrames(float[] buffer) throws IOException {
				return delegate.writeFrames(buffer);
			}

			@Override
			public long readFrames(float[] buffer) throws IOException {
				return delegate.readFrames(buffer);
			}

			@Override
			public long writeFrames(double[] buffer, int frameCount)
					throws IOException {
				return delegate.writeFrames(buffer, frameCount);
			}

			@Override
			public long readFrames(double[] buffer, int frameCount)
					throws IOException {
				return delegate.readFrames(buffer, frameCount);
			}

			@Override
			public Object getMetaData() {
				@SuppressWarnings("unchecked")
				P cast = (P)delegate.getMetaData();
				return convertor.publish(cast);
			}

			@Override
			public long readFrames(float[] buffer, int frameCount)
					throws IOException {
				return delegate.readFrames(buffer, frameCount);
			}

			@Override
			public long writeFrames(float[] buffer, int frameCount)
					throws IOException {
				return delegate.writeFrames(buffer, frameCount);
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
			
			@Override
			public String toString() {
				return delegate.toString();
			}
		};
	}

	private static <T> void checkNotNull(final SoundSourceOrSink delegate) {
		if (delegate == null) {
			throw new NullPointerException("delegate");
		}
	}
}
