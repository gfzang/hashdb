package hashdb.main.tasks.disk;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.storage.entities.Entry;
import hashdb.exceptions.DifferentSizeOfArrayException;
import hashdb.main.tasks.response.GetFirstResponseTask;
import hashdb.main.threads.DiskManager;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.EntryReaderWriter;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/19/13
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetFirstIfExist
		extends DiskTask {

	public GetFirstIfExist(byte[] key, GetFirstResponseTask grt) {
		super(key, null, grt);
	}

	public GetFirstIfExist(Entry e, GetFirstResponseTask grt) {
		super(e.getKey(), e, grt);
	}

	private GetFirstIfExist(byte[] key, Entry e, int entryNo, int primary, GetFirstResponseTask grt) {
		super(key, e, entryNo, primary, grt);
	}

	public void work() {
		EntryReaderWriter erw = DiskManager.getErw();
		GetFirstResponseTask grt = (GetFirstResponseTask) rt;
		Entry res = erw.read(entryNumber);
		try {

			final boolean empty = Utilities.checkMask(Settings.EMPTY, res.getStatus());
			final boolean jumped = Utilities.checkMask(Settings.JUMPED, res.getStatus());

			if (!empty) {
				if (Utilities.sameArray(res.getKey(), key)) {
					if (checkEntry(entry, res)) {
						grt.setEntry(res);
						WorkerThread.addTask(grt);
						return;
					}
				}
			} else if (!jumped) {
				grt.setEntry(null);
				WorkerThread.addTask(grt);
				return;
			}
			int nextEntry = iProtocol.getNextPosition(key, entryNumber);
            while (nextEntry<0)
                nextEntry+=DiskManager.getCap();
			if (nextEntry == primary) {
				grt.setEntry(null);
				WorkerThread.addTask(grt);
			} else {
				GetFirstIfExist gfie = new GetFirstIfExist(key, entry, nextEntry, primary, grt);
				DiskManager.addDiskTask(gfie);
			}
		} catch (DifferentSizeOfArrayException e) {
			log.error(e.getMessage());
		}
	}
}
