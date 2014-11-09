package hashdb.storage.protocol.internal;

import hashdb.Utilities;
import hashdb.storage.entities.fields.EntryKeyLink;

import java.math.BigInteger;



/**
 * The Class DoubleHashProtocol.
 */
public class DoubleHashProtocol
		implements InternalProtocol {


	/**
	 * The size.
	 */
	private final int size;

	public DoubleHashProtocol(int size) {
		this.size = size;
	}

	/**
	 * Primary hash function.
	 *
	 * @param key the key
	 * @return the int
	 */
	private int primary(final byte[] key) {
		final BigInteger bi = new BigInteger(key);
		BigInteger size = new BigInteger(Utilities.toByteArray(this.size));
		size = bi.remainder(size);
		return size.intValue();
	}

	private int secondary(final byte[] key) {
		BigInteger bi = new BigInteger(key);
		BigInteger second = bi.remainder(new BigInteger(Utilities.toByteArray(this.size - 2)));
		return second.intValue() + 1;
	}

	public int getFirstPosition(EntryKeyLink key) {
		return primary(key.getPayload());
	}

	public int getNextPosition(EntryKeyLink key, int previous) {
		return (previous + secondary(key.getPayload())) % size;
	}

	public int getNthPosition(EntryKeyLink key, int n) {
		int result = primary(key.getPayload());
		for (int i = 1; i < n; i++)
			result = getNextPosition(key.getPayload(), result);
		return result;
	}

	public int getFirstPosition(byte[] key) {
		return primary(key);
	}

	public int getNextPosition(byte[] key, int previous) {
		return (previous + secondary(key)) % size;
	}

	public int getNthPosition(byte[] key, int n) {
		int result = primary(key);
		for (int i = 0; i < n; i++)
			result = getNextPosition(key, result) % size;
		return result;
	}
}
