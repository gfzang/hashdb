package hashdb.storage.entities.fields;

import hashdb.Settings.Field;
import hashdb.Utilities.Align;
import hashdb.Utilities.Strategy;


/**
 * The Class EntryKeyLink.
 * <p/>
 * Both key and link fields contain keys so they might need to have common base.
 */
public abstract class EntryKeyLink
		extends EntryPart {

	/**
	 * Instantiates a new entry key link.
	 *
	 * @param f the f
	 */
	EntryKeyLink(final Field f) {
		super(f);
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
		return Strategy.XOR;
	}
}
