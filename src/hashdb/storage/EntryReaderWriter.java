package hashdb.storage;

import hashdb.Settings;
import hashdb.storage.entities.Entry;
import hashdb.storage.entities.fields.EntryPart;
import hashdb.exceptions.PositionOutOfBoundsException;
import hashdb.exceptions.SomethingWentHorriblyWrong;

import java.io.IOException;



/**
 * The Class EntryReaderWriter.
 */
public class EntryReaderWriter {

	/**
	 * The persistent storage used.
	 */
	private final PersistentStorage ps;

	/**
	 * Instantiates a new entry reader writer.
	 *
	 * @param ps the ps
	 */
	public EntryReaderWriter(final PersistentStorage ps) {
		if (ps == null) throw new NullPointerException();
		this.ps = ps;
	}

	/**
	 * Reads entry with given number.
	 *
	 * @param position the position
	 * @return the entry
	 */
	public Entry read(final int position) {
		Entry result = null;
		try {
			this.ps.savePosition();
		} catch (final IOException ioe) {
			return null;
		}
		try {
			this.ps.seek(position * Settings.Fields.END.getOffset());
			result = Entry.readEntry(this.ps);
			this.ps.dropPosition();
		} catch (final PositionOutOfBoundsException e) {
			try {
				this.ps.restorePosition();
			} catch (final IOException e1) {
				throw new SomethingWentHorriblyWrong();
			}
		}
		return result;

	}

	/**
	 * Maximum number of entries that can be put in persistent storage.
	 *
	 * @return the long
	 */
	public long size() {
		return this.ps.capacity / Settings.Fields.END.getOffset();
	}

	/**
	 * Write.
	 *
	 * @param entry    the entry
	 * @param position the position
	 * @return true, if successful
	 */
	public boolean write(final Entry entry, final int position) {
		try {
			try {
				this.ps.savePosition();
			} catch (final IOException ioe) {
				return false;
			}
			this.ps.seek(position * Settings.Fields.END.getOffset());
			entry.writeEntry(this.ps);
			this.ps.dropPosition();
			return true;
		} catch (final Exception e) {
			try {
				this.ps.restorePosition();
			} catch (final IOException e1) {
				throw new SomethingWentHorriblyWrong();
			}
			return false;
		}
	}

	/**
	 * Write part of entry to storage having in mind it's offset.
	 *
	 * @param entryPart the entry part
	 * @param position  the position
	 * @return true, if successful
	 */
	public boolean writePart(final EntryPart entryPart, final int position) {
		try {
			try {
				this.ps.savePosition();
			} catch (final IOException ioe) {
				return false;
			}
			this.ps.seek(position * Settings.Fields.END.getOffset() + entryPart.getOffset());
			this.ps.write(entryPart.getPayload());
			this.ps.dropPosition();
		} catch (final Exception e) {
			try {
				this.ps.restorePosition();
			} catch (final IOException e1) {
				throw new SomethingWentHorriblyWrong();
			}
			return false;
		}
		return true;
	}

}
