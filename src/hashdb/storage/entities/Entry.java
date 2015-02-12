package hashdb.storage.entities;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.storage.PersistentStorage;
import hashdb.storage.entities.fields.EntryData;
import hashdb.storage.entities.fields.EntryKey;
import hashdb.storage.entities.fields.EntryLength;
import hashdb.storage.entities.fields.EntryLink;
import hashdb.storage.entities.fields.EntryPart;
import hashdb.storage.entities.fields.EntryStatus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;



/**
 * The Class Entry.
 */
public class Entry {

	/**
	 * The dummy_.
	 */
	private static final Entry dummy_ = new Entry();

	/**
	 * Creates new entry out of byte array.
	 *
	 * @param b the raw byte array
	 * @return the entry
	 */
	private static Entry fromByteArray(final byte[] b) {
		final Entry result = new Entry();
		for (final EntryPart ep : result.fields)
			ep.extract(b);
		return result;
	}

	/**
	 * From raw input.
	 *
	 * @param key  the key
	 * @param data the data
	 * @param link the link
	 * @return the entry
	 */
	public static Entry fromRawInput(final byte[] key, final byte[] data, final byte[] link) {
		final Entry result = new Entry();
		result.data.load(data);
		result.key.load(key);
		result.link.load(link);
		result.length.load(Utilities.toByteArray(data.length));
		return result;
	}

	/**
	 * Gets the dummy.
	 *
	 * @return the dummy
	 */
	public static Entry getDummy() {
		return Entry.dummy_;
	}

	/**
	 * Read entry from given persistent storage.
	 *
	 * @param ps the persistent storage from which to read
	 * @return the read entry or null if error occurs
	 */
	public static Entry readEntry(final PersistentStorage ps) {
		try {
			final byte[] raw = new byte[Settings.Fields.END.getOffset()];
			ps.read(raw);
			return Entry.fromByteArray(raw);
		} catch (final IOException e) {
			return null;
		}
	}

	/**
	 * The key used for hash map.
	 */
	private final EntryKey key;

	/**
	 * The status.
	 */
	private final EntryStatus status;

	/**
	 * The length of data part.
	 */
	private final EntryLength length;

	/**
	 * The data.
	 */
	private final EntryData data;

	/**
	 * The link to next entry.
	 */
	private final EntryLink link;

	/**
	 * All fields in use. Sometimes convenient.
	 */
	private final List<EntryPart> fields;

	/**
	 * Instantiates a new entry.
	 */
	private Entry() {
		this.fields = new LinkedList<EntryPart>();

		this.key = new EntryKey();
		this.fields.add(this.key);

		this.status = new EntryStatus();
		this.fields.add(this.status);

		this.length = new EntryLength();
		this.fields.add(this.length);

		this.data = new EntryData();
		this.fields.add(this.data);

		this.link = new EntryLink();
		this.fields.add(this.link);
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public byte[] getData() {
		return this.data.getPayload();
	}

	/**
	 * Gets the whole entry status (not just payload).
	 *
	 * @return the entry status
	 */
	public EntryStatus getEntryStatus() {
		return this.status;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public byte[] getKey() {
		return this.key.getPayload();
	}

	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	public byte[] getLength() {
		return this.length.getPayload();
	}

	/**
	 * Gets the link.
	 *
	 * @return the link
	 */
	public byte[] getLink() {
		return this.link.getPayload();
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public byte[] getStatus() {
		return this.status.getPayload();
	}

	/**
	 * Returns raw, byte array representation of entry.
	 *
	 * @return the byte[]
	 */
	byte[] toByteArray() {
		final byte[] result = new byte[Settings.Fields.END.getOffset()];
		for (final EntryPart ep : this.fields)
			ep.inject(result);
		return result;
	}

	/**
	 * Write entry to persistent storage.
	 *
	 * @param ps the ps
	 */
	public void writeEntry(final PersistentStorage ps) {
		try {
			ps.write(this.toByteArray());
		} catch (final IOException ioe) {
			throw new SomethingWentHorriblyWrong();
		}
	}
}
