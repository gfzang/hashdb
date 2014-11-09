package hashdb.test;

import hashdb.exceptions.PositionOutOfBoundsException;
import hashdb.storage.PersistentStorage;

import java.io.IOException;



/**
 * The Class PersistentStorageTest.
 */
public class PersistentStorageTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {
		try {
			final PersistentStorage ps = new PersistentStorage("./test.txt", 1024);
			final byte[] niz = new byte[256];

			for (int i = 0; i < niz.length; i++)
				niz[i] = (byte) i;

			ps.write(niz);

			ps.seek(900);

			ps.write(niz);
			ps.read(niz);
			for (int i = 0; i < niz.length; i++)
				if (niz[i] != (byte) i) throw new AssertionError();

			System.out.print("OK");
		} catch (final IOException e) {
			System.out.print("not OK");
			e.printStackTrace();
		}
		catch (final PositionOutOfBoundsException e) {
			e.printStackTrace();
		}

	}

}
