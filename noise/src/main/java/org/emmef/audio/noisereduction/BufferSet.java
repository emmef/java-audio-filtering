package org.emmef.audio.noisereduction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.emmef.logging.Logger;

public class BufferSet {
	private static final Logger logger = Logger.getDefault();

	public interface Handle {
		Buffer get() throws InterruptedException; 
		void put(Buffer buf);
		void close();
		void panic();
	}
	
	private final Object[] mutex = new Object[0];
	private boolean first = true;
	private int sampleCount = -1;
	private final LinkedList<Buffer> shared = new LinkedList<Buffer>();
	private boolean panic = false;
	private int pendingBuffersRequested;

	Handle init(int sampleCount, int minimumBuffers, int preferredBuffers) throws InterruptedException {
		if (sampleCount < 1) {
			throw new IllegalStateException("Sample count should be at least one");
		}
		if (minimumBuffers < 1) {
			throw new IllegalArgumentException("Minimum buffers must be at least 1");
		}
		if (preferredBuffers < minimumBuffers) {
			throw new IllegalArgumentException("Preferred number of buffers (" + preferredBuffers + ") should be bigger than minimum (" + minimumBuffers + ")");
		}
		List<BufferAndState> newBuffers;
		synchronized (mutex) {
			pendingBuffersRequested += minimumBuffers;
			try {
				if (this.sampleCount != -1 && this.sampleCount != sampleCount) {
					throw new IllegalStateException("Already initialized with buffer size " + this.sampleCount);
				}
				newBuffers = unsafeTryAllocate(sampleCount, minimumBuffers, preferredBuffers);
				if (first && newBuffers == null) {
					throw new IllegalStateException("Cannot allocate enough buffers to work with");
				}
				 
				while (newBuffers == null) {
					waitForMutex();
					newBuffers = unsafeTryAllocate(sampleCount, minimumBuffers, preferredBuffers);
				}
				if (first) {
					this.sampleCount = sampleCount;
					this.first = false;
				}
			}
			finally {
				pendingBuffersRequested -= minimumBuffers;
				mutex.notifyAll();
			}
		}
		return new MyHandle(newBuffers);
	}

	private List<BufferAndState> unsafeTryAllocate(int sampleCount, int minimumBuffers, int preferredBuffers) {
		final List<BufferAndState> result = new ArrayList<BufferAndState>(preferredBuffers);
		try {
			for (int i = 0; i < preferredBuffers; i++) {
				result.add(new BufferAndState(new Buffer(sampleCount), true));
			}
		}
		catch (OutOfMemoryError e) {
			// We exactly know what allocate failed. 
		}
		if (result.size() > minimumBuffers) {
			logger.trace("Allocated %d buffers (min=%d)", result.size(), minimumBuffers);
			for (int i = result.size() - 1; i >= minimumBuffers; i--) {
				shared.add(result.remove(i).buffer);
			}
			return result;
		}
		else {
			logger.config("Couldn't allocate preferred number of buffers; processing performance might not be optimal");
		}
		int deficient = minimumBuffers - result.size();
		if (deficient == 0) {
			return result;
		}
		if (deficient > shared.size()) {
			logger.trace("Will have to wait");
			result.clear();
			return null;
		}
		logger.trace("Lending %d buffers from shared pool", deficient);
		for (int i = 0; i < deficient; i++) {
			result.add(new BufferAndState(shared.remove(), false));
		}
		
		return result;
	}

	private void waitForMutex() throws InterruptedException {
		mutex.wait();
		if (panic) {
			throw new IllegalStateException("There was panic: shutdown");
		}
	}
	
	private class MyHandle implements Handle {
		private final LinkedList<BufferAndState> buffers = new LinkedList<BufferAndState>();

		public MyHandle(List<BufferAndState> newBuffers) {
			for (BufferAndState b : newBuffers) {
				this.buffers.add(b);
			}
		}
		
		public void panic() {
			synchronized (mutex) {
				panic = true;
				mutex.notifyAll();
			}
		}
		
		public Buffer get() throws InterruptedException {
			Buffer buff;
			synchronized (mutex) {
				if (buffers.isEmpty()) {
					throw new IllegalStateException("Handle is already closed");
				}
				buff = unsafeGet();
				while (buff == null) {
					waitForMutex();
					buff = unsafeGet();
				}
			}
			synchronized (buff) {
				buff.clear();
			}
			return buff;
		}
		
		public void put(Buffer buf) {
			synchronized (mutex) {
				try {
					for (BufferAndState entry : buffers) {
						if (entry.buffer == buf) {
							entry.occupied = false;
							return;
						}
					}
					shared.add(buf);
				}
				finally {
					mutex.notifyAll();
				}
			}
		}
		
		public void close() {
			synchronized (mutex) {
				try {
					while (!buffers.isEmpty()) {
						final BufferAndState removed = buffers.remove();
						if (!removed.isPrivate || (shared.size() < pendingBuffersRequested)) {
							shared.add(removed.buffer);
						}
					}
					buffers.clear();
				}
				finally {
					mutex.notifyAll();
				}
			}
		}

		private Buffer unsafeGet() {
			for (BufferAndState entry : buffers) {
				if (!entry.occupied) {
					entry.occupied = true;
					return entry.buffer;
				}
			}
			if (!shared.isEmpty()) {
				return shared.remove();
			}
			return null;
		}
	}
	
	private class BufferAndState {
		final Buffer buffer;
		final boolean isPrivate; 
		boolean occupied = false;
		
		public BufferAndState(Buffer buffer, boolean isPrivate) {
			this.buffer = buffer;
			this.isPrivate = isPrivate;
		}
	}
}
