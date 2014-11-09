package hashdb.main.tasks.response;

import hashdb.storage.entities.Entry;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/19/13
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataResponseTask
		extends ResponseTask {
	Entry entry;

	public void setEntry(Entry entry) {
		this.entry = entry;
	}
}
