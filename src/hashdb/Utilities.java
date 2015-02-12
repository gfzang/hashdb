package hashdb;

import hashdb.communication.ConnectionInstance;
import hashdb.exceptions.CannotCastException;
import hashdb.exceptions.CannotReduceException;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.DifferentSizeOfArrayException;
import hashdb.exceptions.ServerCommunicationException;



/**
 * The class with static methods used all over the code.
 */
public final class Utilities {

    public static byte[] autoKey(byte[] data) {
        byte[] result = new byte[Settings.Fields.KEY.getSize()];
        Utilities.copy(data,result,Align.left,Strategy.XOR);
        return result;
    }

    public static short[] toShortArray(byte[] array) throws CannotCastException {
        if (array.length % 2 != 0) throw new CannotCastException();
        final short[] result = new short[array.length / 2];
        for (int i = 0; i < result.length; i++)
            result[i] = (short) (((short) array[i * 2] << 8) + (((short) array[i * 2 + 1]) & 0x00FF));
        return result;
    }

    /**
	 * The Class Align.
	 */
	public static class Align {

		/**
		 * The left.
		 */
		public static final Align left = new Align();

		/**
		 * The right.
		 */
		public static final Align right = new Align();

		/**
		 * Instantiates a new align.
		 */
		private Align() {
		}
	}

	/**
	 * The Class Strategy.
	 */
	public static abstract class Strategy {

		/**
		 * The unreducable.
		 */
		public static final Strategy UNREDUCABLE = new Strategy() {
			@Override
			public byte result(final byte a, final byte b) {
				throw new CannotReduceException();
			}
		};

        public static final Strategy COPY = new Strategy() {
            @Override
            public byte result(byte prev, byte current) {
                return prev;
            }
        }  ;

		/**
		 * The xor.
		 */
		public static final Strategy XOR = new Strategy() {
			@Override
			public byte result(final byte prev, final byte current) {
				return (byte) (prev ^ current);
			}
		};

		/**
		 * The leadingzeros.
		 */
		public static final Strategy LEADINGZEROS = new Strategy() {
			@Override
			public byte result(final byte prev, final byte next) {
				if (next != 0) throw new CannotReduceException();
				return prev;
			}
		};

		/**
		 * Result.
		 *
		 * @param prev    the prev
		 * @param current the current
		 * @return the byte
		 */
		public abstract byte result(byte prev, byte current);

    }

	/**
	 * Byte array to int.
	 *
	 * @param b the b
	 * @return the int
	 * @throws CannotCastException the cannot cast exception
	 */
	public static int byteArrayToInt(final byte[] b) throws CannotCastException {
		if (b.length != 4) throw new CannotCastException();
		int result = 0;
		for (int i = 0; i < 4; i++)
			result += ((int)b[i] & 0xFF) << 24 - i * 8;
		return result;
	}

	/**
	 * Check lengths.
	 *
	 * @param array0 the array0
	 * @param array1 the array1
	 * @throws DifferentSizeOfArrayException the different size of array exception
	 */
	private static void checkLengths(final byte[] array0, final byte[] array1) throws DifferentSizeOfArrayException {
		if (array0.length != array1.length) throw new DifferentSizeOfArrayException();
	}

	/**
	 * Check if all bits in destination masked by mask are set.
	 *
	 * @param mask        the mask
	 * @param destination the destination
	 * @return true if all 1; false if at least one 0
	 * @throws DifferentSizeOfArrayException the different size of array exception
	 */
	public static boolean checkMask(final byte[] mask, final byte[] destination) throws DifferentSizeOfArrayException {
		Utilities.checkLengths(mask, destination);
		for (int i = 0; i < mask.length; i++)
			if (mask[i] != (mask[i] & destination[i])) return false;
		return true;
	}

	/**
	 * Convert char to short.
	 *
	 * @param tmp    the tmp
	 * @param buffer the buffer
	 */
	public static void convertCharToShort(final char[] tmp, final short[] buffer) {
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = (short) tmp[i];
	}

	/**
	 * Copy.
	 *
	 * @param source      the source
	 * @param destination the destination
	 * @param a           the a
	 * @param s           the s
	 */
	public static void copy(final byte[] source, final byte[] destination, final Align a, final Strategy s) {
		int i;
		for (i = 0; i < destination.length && i < source.length; i++)
			if (a == Align.left) {
				destination[i] = source[i];
			} else {
				destination[destination.length - 1 - i] = source[source.length - 1 - i];
			}

		for (; i < source.length; i++)
			if (a == Align.left) {
				destination[i % destination.length] = s.result(destination[i % destination.length], source[i]);
			} else {
				destination[destination.length - 1 - i % destination.length] = s.result(
						destination[destination.length - 1 - i % destination.length], source[source.length - 1 - i]);
			}

	}

	/**
	 * Receive ack.
	 *
	 * @param ci the ci
	 * @throws ConnectionNotActiveException the connection not active exception
	 * @throws ServerCommunicationException the server communication exception
	 */
	public static void receiveAck(final ConnectionInstance ci) throws ConnectionNotActiveException, ServerCommunicationException {
		final short[] ack = new short[1];
		ci.receive(ack);
		if (ack[0] != Settings.CommunicationCodes.ACK) throw new ServerCommunicationException();
	}

	/**
	 * Resets bits in destination where bits in mask are set.
	 *
	 * @param mask        the mask
	 * @param destination the destination
	 * @throws DifferentSizeOfArrayException the different size of array exception
	 */
	public static void resetMask(final byte[] mask, final byte[] destination) throws DifferentSizeOfArrayException {
		Utilities.checkLengths(mask, destination);
		for (int i = 0; i < mask.length; i++)
			destination[i] &= ~mask[i];
	}

	/**
	 * Checks weather arrays are filled with same values.
	 *
	 * @param array0 the array0
	 * @param array1 the array1
	 * @return true, if successful
	 * @throws DifferentSizeOfArrayException the different size of array exception
	 */
	public static boolean sameArray(final byte[] array0, final byte[] array1) throws DifferentSizeOfArrayException {
		Utilities.checkLengths(array0, array1);
		for (int i = 0; i < array0.length; i++)
			if (array0[i] != array1[i]) return false;
		return true;
	}

	/**
	 * Send ack.
	 *
	 * @param ci the ci
	 * @throws ConnectionNotActiveException the connection not active exception
	 */
	public static void sendAck(final ConnectionInstance ci) throws ConnectionNotActiveException {
		final short[] ack = new short[]{Settings.CommunicationCodes.ACK};
		ci.send(ack);
	}

	/**
	 * Sets bits in destination where bits in mask are set.
	 *
	 * @param mask        the mask
	 * @param destination the destination
	 * @throws DifferentSizeOfArrayException the different size of array exception
	 */
	public static void setMask(final byte[] mask, final byte[] destination) throws DifferentSizeOfArrayException {
		Utilities.checkLengths(mask, destination);
		for (int i = 0; i < mask.length; i++)
			destination[i] |= mask[i];
	}

	/**
	 * Converts array of char to byte array.
	 *
	 * @param array the array
	 * @return the byte[]
	 */
	public static byte[] toByteArray(final char[] array) {
		final byte[] result = new byte[array.length * 2];
		for (int i = 0; i < array.length; i++) {
			result[2 * i] = (byte) ((array[i] & 0xFF00) >> 8);
			result[2 * i + 1] = (byte) (array[i] & 0x00FF);
		}
		return result;
	}

	/**
	 * Creates byte array with bit representation of int.
	 *
	 * @param size the size
	 * @return the byte[4]
	 */
	public static byte[] toByteArray(final int size) {
		final byte[] result = new byte[4];
		for (int i = 0; i < 4; i++)
			result[i] = (byte) (size >> 24 - i * 8);
		return result;
	}

	/**
	 * Creates byte array with bit representation of long.
	 *
	 * @param size the size
	 * @return the byte[8]
	 */
	public static byte[] toByteArray(final long size) {
		final byte[] result = new byte[8];
		for (int i = 0; i < 8; i++)
			result[i] = (byte) (size >> 56 - i * 8);
		return result;
	}

	/**
	 * Converts array of char to byte array.
	 *
	 * @param array the array
	 * @return the byte[]
	 */
	public static byte[] toByteArray(final short[] array) {
		final byte[] result = new byte[array.length * 2];
		for (int i = 0; i < array.length; i++) {
			result[2 * i] = (byte) ((array[i] & 0xFF00) >> 8);
			result[2 * i + 1] = (byte) (array[i] & 0x00FF);
		}
		return result;
	}

	/**
	 * Converts byte to char array.
	 *
	 * @param array the array
	 * @return the char[]
	 * @throws CannotCastException if there is odd number of bytes
	 */
	public static char[] toCharArray(final byte[] array) throws CannotCastException {
		if (array.length % 2 != 0) throw new CannotCastException();
		final char[] result = new char[array.length / 2];
		for (int i = 0; i < result.length; i++)
			result[i] = (char) (((char) array[i * 2] << 8) + (((char) array[i * 2 + 1]) & 0x00FF));
		return result;
	}

	/**
	 * To char array.
	 *
	 * @param buffer the buffer
	 * @return the char[]
	 */
	public static char[] toCharArray(final short[] buffer) {
		final char[] result = new char[buffer.length];
		for (int i = 0; i < buffer.length; i++)
			result[i] = (char) buffer[i];
		return result;
	}
}
