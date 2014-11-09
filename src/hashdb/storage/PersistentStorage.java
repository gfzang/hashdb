package hashdb.storage;

import hashdb.exceptions.PositionOutOfBoundsException;
import hashdb.exceptions.SomethingWentHorriblyWrong;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Stack;



/**
 * The Class PersistentStorage.
 */
public class PersistentStorage {

	/**
	 * The file on persistent storage medium.
	 */
	private RandomAccessFile raf = null;

	/**
	 * The maximum allowed capacity.
	 */
	long capacity;

	/**
	 * The saved positions. Can be used to implement semi-transaction mechanism
	 */
	private final Stack<Long> savedPositions = new Stack<Long>();

	/**
	 * Instantiates a new persistent storage.
	 *
	 * @param path     the path
	 * @param capacity the capacity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PersistentStorage(final String path, final long capacity) throws IOException {
		this.raf = new RandomAccessFile(path, "rws");
		this.raf.setLength(capacity);
		this.capacity = capacity;
	}

	/**
	 * Drop position when we are sure there will be no need for it to be
	 * restored.
	 */
	public void dropPosition() {
		if (this.savedPositions.size() == 0) return;
		this.savedPositions.pop();
	}

	/**
	 * Reads array from the file.
	 *
	 * @param destination where content from file will be stored.
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public long read(final byte[] destination) throws IOException {
		if (destination == null) throw new NullPointerException();
		int toRead;
		if (destination.length + this.raf.getFilePointer() > this.capacity) {
			toRead = (int) (this.capacity - this.raf.getFilePointer());
		} else {
			toRead = destination.length;
		}
		int result = 0;
		do {
			final int tmp = this.raf.read(destination, result, toRead - result);
			if (tmp == -1) throw new SomethingWentHorriblyWrong();
			result += tmp;
		} while (result < toRead);

		if (this.raf.getFilePointer() == this.capacity) this.raf.seek(0);
		return toRead;
	}

	/**
	 * Restore previous position.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void restorePosition() throws IOException {
		if (this.savedPositions.size() == 0) return;
		this.raf.seek(this.savedPositions.pop());
	}

	/**
	 * Saves current position.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void savePosition() throws IOException {
		this.savedPositions.push(this.raf.getFilePointer());
	}

	/**
	 * Seek.
	 *
	 * @param position the position
	 * @throws PositionOutOfBoundsException when seeking negative or greater or equal than capacity
	 */
	public void seek(final long position) throws PositionOutOfBoundsException {
		if (position >= this.capacity || position < 0) throw new PositionOutOfBoundsException(position, this.capacity);
		try {
			this.raf.seek(position);
		} catch (final IOException e) {
			throw new SomethingWentHorriblyWrong();
		}
	}

	/**
	 * Writes array of bytes to file.
	 *
	 * @param source array to be written
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public long write(final byte[] source) throws IOException {
		if (source == null) return 0;
		int toWrite;
		if (source.length + this.raf.getFilePointer() > this.capacity) {
			toWrite = (int) (this.capacity - this.raf.getFilePointer());
		} else {
			toWrite = source.length;
		}
		this.raf.write(source, 0, toWrite);
		if (this.raf.getFilePointer() == this.capacity) {
			this.raf.seek(0);
		} else if (this.raf.getFilePointer() > this.capacity) throw new SomethingWentHorriblyWrong();
		return toWrite;
	}
}
