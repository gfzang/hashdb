package hashdb.storage.entities.fields;

import hashdb.Settings.Field;
import hashdb.Utilities;
import hashdb.Utilities.Align;
import hashdb.Utilities.Strategy;
import hashdb.exceptions.DifferentSizeOfPayloadException;
import hashdb.storage.PersistentStorage;

import java.io.IOException;



/**
 * The Class EntryPart.
 */
public abstract class EntryPart {

	/**
	 * The descriptor.
	 */
	private final Field descriptor;

	/**
	 * The payload.
	 */
	private byte[] payload;

	/**
	 * Instantiates a new entry part.
	 *
	 * @param descriptor the descriptor
	 */
	EntryPart(final Field descriptor) {
		this.descriptor = descriptor;
		this.payload = new byte[descriptor.getSize()];
	}

	/**
	 * Compatible.
	 *
	 * @param toTest the to test
	 * @throws DifferentSizeOfPayloadException
	 *          the different size of payload exception
	 */
	private void compatible(final byte[] toTest) throws DifferentSizeOfPayloadException {
		if (toTest.length != this.descriptor.getSize()) throw new DifferentSizeOfPayloadException();
	}

	/**
	 * Extracts respective field from entry to payload.
	 *
	 * @param raw byte array from which to extract
	 */
	public void extract(final byte[] raw) {
		final int size = this.descriptor.getSize();
		final int offset = this.descriptor.getOffset();
		System.arraycopy(raw, offset, this.payload, 0, size);
	}

	/**
	 * Gets the align.
	 *
	 * @return the align
	 */
	protected abstract Align getAlign();

	/**
	 * Gets the offset from beginning from entry.
	 *
	 * @return the offset
	 */
	public int getOffset() {
		return this.descriptor.getOffset();
	}

	/**
	 * Gets the payload.
	 *
	 * @return the payload
	 */
	public byte[] getPayload() {
		return this.payload;
	}

	/**
	 * Gets the strategy.
	 *
	 * @return the strategy
	 */
	protected abstract Strategy getStrategy();

	/**
	 * Puts part of entry on it's respective positions in result.
	 *
	 * @param result the result
	 */
	public void inject(final byte[] result) {
		final int size = this.descriptor.getSize();
		final int offset = this.descriptor.getOffset();
		System.arraycopy(this.payload, 0, result, offset, size);
	}

	/**
	 * Load.
	 *
	 * @param source the source
	 */
	public void load(final byte[] source) {
		try {
			this.compatible(source);
			Utilities.copy(source, this.payload, Align.left, null);
		} catch (final DifferentSizeOfPayloadException e) {
			Utilities.copy(source, this.payload, this.getAlign(), this.getStrategy());
		}

	}

	/**
	 * Load.
	 *
	 * @param ps the ps
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void load(final PersistentStorage ps) throws IOException {
		ps.read(this.payload);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		this.payload = new byte[this.descriptor.getSize()];
	}

	/**
	 * Store.
	 *
	 * @param destination the destination
	 * @throws DifferentSizeOfPayloadException
	 *          the different size of payload exception
	 */
	public void store(final byte[] destination) throws DifferentSizeOfPayloadException {
		this.compatible(destination);
		Utilities.copy(this.payload, destination, this.getAlign(), this.getStrategy());
	}

	/**
	 * Store.
	 *
	 * @param ps the ps
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void store(final PersistentStorage ps) throws IOException {
		ps.write(this.payload);
	}

}