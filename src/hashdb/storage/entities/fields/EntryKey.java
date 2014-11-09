/**
 *
 */
package hashdb.storage.entities.fields;

import hashdb.Settings;



/**
 * The Class EntryKey.
 */
public class EntryKey
		extends EntryKeyLink {

	/**
	 * Instantiates a new entry key.
	 */
	public EntryKey() {
		super(Settings.Fields.KEY);
	}
}
