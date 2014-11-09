package hashdb.storage.entities.fields;

import hashdb.Settings;
import hashdb.Utilities.Align;
import hashdb.Utilities.Strategy;


/**
 * The Class EntryLength.
 */
public class EntryLength
		extends EntryPart {

	/**
	 * Instantiates a new entry length.
	 */
	public EntryLength() {
		super(Settings.Fields.LENGTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.storage.entities.fields.EntryPart#getAlign()
	 */
	@Override
	public Align getAlign() {
		return Align.right;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.storage.entities.fields.EntryPart#getStrategy()
	 */
	@Override
	public Strategy getStrategy() {
		return Strategy.LEADINGZEROS;
	}

}
