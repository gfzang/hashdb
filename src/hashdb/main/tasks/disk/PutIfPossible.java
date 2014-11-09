package hashdb.main.tasks.disk;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.storage.entities.Entry;
import hashdb.exceptions.DifferentSizeOfArrayException;
import hashdb.main.tasks.response.PutResponseTask;
import hashdb.main.threads.DiskManager;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.EntryReaderWriter;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/19/13
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class PutIfPossible
		extends DiskTask {

	public PutIfPossible(Entry e, PutResponseTask prt) {
		super(e.getKey(), e, prt);
	}

	public PutIfPossible(byte[] key, byte[] data, byte[] link, PutResponseTask prt) {
		super(key, Entry.fromRawInput(key, data, link), prt);
	}

	private PutIfPossible(Entry entry, int nextEntry, int primary, PutResponseTask prt) {
		super(entry.getKey(), entry, nextEntry, primary, prt);
	}

	public void work() {
		EntryReaderWriter erw = DiskManager.getErw();
		PutResponseTask prt = (PutResponseTask) rt;
		Entry res = erw.read(entryNumber);
		try {
			final boolean empty = Utilities.checkMask(Settings.EMPTY, res.getStatus());
			final boolean jumped = Utilities.checkMask(Settings.JUMPED, res.getStatus());
			if (empty) {
				if (jumped) Utilities.setMask(Settings.JUMPED, entry.getStatus());
				prt.setStatus(erw.write(entry, entryNumber));
				WorkerThread.addTask(prt);
				return;
			}
			if (!jumped) {
				Utilities.setMask(Settings.JUMPED, res.getStatus());
				erw.writePart(res.getEntryStatus(), entryNumber);
			}
			int nextEntry = iProtocol.getNextPosition(key, entryNumber);
            while (nextEntry<0)
                nextEntry+=DiskManager.getCap();
			if (nextEntry == primary) {
				prt.setStatus(false);
				WorkerThread.addTask(prt);
			} else {
				PutIfPossible pip = new PutIfPossible(entry, nextEntry, primary, prt);
				DiskManager.addDiskTask(pip);
			}
		} catch (DifferentSizeOfArrayException e) {
			log.error(e.getMessage());
		}
	}
}
