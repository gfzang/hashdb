package hashdb.storage.entities.fields;

import hashdb.Settings;
import hashdb.Utilities.Align;
import hashdb.Utilities.Strategy;



/**
 * The Class EntryStatus.
 */
public class EntryStatus
		extends EntryPart {

	/**
	 * Instantiates a new entry status.
	 */
	public EntryStatus() {
		super(Settings.Fields.STATUS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.storage.entities.fields.EntryPart#getAlign()
	 */
	@Override
	public Align getAlign() {
		return Align.left;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.storage.entities.fields.EntryPart#getStrategy()
	 */
	@Override
	public Strategy getStrategy() {
		return Strategy.UNREDUCABLE;
	}

}
