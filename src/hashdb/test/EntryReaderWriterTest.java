package hashdb.test;

import hashdb.Settings;
import hashdb.storage.EntryReaderWriter;
import hashdb.storage.PersistentStorage;
import hashdb.storage.entities.Entry;

import java.io.IOException;



/**
 * The Class EntryReaderWriterTest.
 */
public class EntryReaderWriterTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String args[]) {
		try {
			final PersistentStorage ps = new PersistentStorage("./test.hex", 10 * Settings.Fields.END.getOffset());
			final EntryReaderWriter erw = new EntryReaderWriter(ps);
			final Entry e = erw.read(0);
			final byte[] key = e.getKey();
			for (int i = 0; i < key.length; i++)
				key[i] = 64;
			erw.write(e, 0);
			erw.write(e, 5);
			erw.write(e, 7);
			System.out.println("OK");
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("KEY: " + Settings.Fields.KEY.getSize());
			System.out.println("STATUS: " + Settings.Fields.STATUS.getSize());
			System.out.println("LENGTH: " + Settings.Fields.LENGTH.getSize());
			System.out.println("DATA: " + Settings.Fields.DATA.getSize());
			System.out.println("LINK: " + Settings.Fields.LINK.getSize());
			System.out.println("-------------------------------");
			System.out.println("TOTAL: " + Settings.Fields.END.getOffset());
		}

	}

}
