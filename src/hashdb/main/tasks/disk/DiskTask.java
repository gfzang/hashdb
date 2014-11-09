package hashdb.main.tasks.disk;

import org.apache.log4j.Logger;
import hashdb.Utilities;
import hashdb.storage.entities.Entry;
import hashdb.exceptions.DifferentSizeOfArrayException;
import hashdb.main.tasks.Task;
import hashdb.main.tasks.response.ResponseTask;
import hashdb.main.threads.DiskManager;
import hashdb.storage.protocol.internal.InternalProtocol;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/14/13
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DiskTask
		implements Task, Comparable<DiskTask> {

	final byte[] key;
	final Entry entry;
	int primary;
	final ResponseTask rt;

	static final Logger log = Logger.getLogger(DiskTask.class);

	static final InternalProtocol iProtocol = DiskManager.getIprotocol();

	final int entryNumber;
	private static int order = -1;

	public static void invertOrder() {
		order *= -1;
	}

	Integer getNumber() {
		return entryNumber;
	}

	DiskTask(byte[] key, Entry e, ResponseTask rt) {
		this.key = key;
		this.entry = e;
		this.rt = rt;
		primary = iProtocol.getFirstPosition(key);
        while (primary<0)
            primary+=DiskManager.getCap();
		entryNumber = primary;
	}

	DiskTask(byte[] key, Entry e, int entryNo, int primary, ResponseTask rt) {
		this.key = key;
		this.entry = e;
		this.primary = primary;
		this.rt = rt;
		this.entryNumber = entryNo;
	}


	public int compareTo(DiskTask diskTask) {
		return this.getNumber().compareTo(diskTask.getNumber()) * order;
	}

	boolean checkEntry(Entry e, Entry res) {
		try {
			if (entry == null) return true;
			if (Utilities.sameArray(e.getStatus(), res.getStatus()) &&
			    Utilities.sameArray(e.getLength(), res.getLength()) &&
			    Utilities.sameArray(e.getLink(), res.getLink())) {
				return Utilities.sameArray(e.getData(), res.getData());
			}
		} catch (DifferentSizeOfArrayException e1) {
			log.error(e1.getMessage());
			return false;
		}
		return false;
	}
}
