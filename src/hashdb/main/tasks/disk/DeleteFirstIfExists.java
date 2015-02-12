package hashdb.main.tasks.disk;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.exceptions.DifferentSizeOfArrayException;
import hashdb.main.tasks.response.DeleteFirstResponseTask;
import hashdb.main.threads.DiskManager;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.EntryReaderWriter;
import hashdb.storage.entities.Entry;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/15/13
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteFirstIfExists
		extends DiskTask {

	public DeleteFirstIfExists(byte[] key, DeleteFirstResponseTask drt) {
		super(key, null, drt);
	}

	private DeleteFirstIfExists(byte[] key, int nextEntry, int primary, DeleteFirstResponseTask drt) {
		super(key, null, nextEntry, primary, drt);
	}

	public DeleteFirstIfExists(Entry e, DeleteFirstResponseTask drt) {
		super(e.getKey(), e, drt);
	}

	private DeleteFirstIfExists(Entry entry, int nextEntry, int primary, DeleteFirstResponseTask drt) {
		super(entry.getKey(), entry, nextEntry, primary, drt);
	}

	public void work() {
		EntryReaderWriter erw = DiskManager.getErw();
		DeleteFirstResponseTask drt = (DeleteFirstResponseTask) rt;
		Entry res = erw.read(entryNumber);
		try {
			final boolean empty = Utilities.checkMask(Settings.EMPTY, res.getStatus());
			final boolean jumped = Utilities.checkMask(Settings.JUMPED, res.getStatus());

			if (!empty) {
				if (Utilities.sameArray(res.getKey(), key)) {
					Utilities.setMask(Settings.EMPTY, res.getStatus());
					if (checkEntry(entry, res)) {
						erw.writePart(res.getEntryStatus(), entryNumber);
						drt.setStatus(true);
						WorkerThread.addTask(drt);
						return;
					}
				}
			} else if (!jumped) {
				drt.setStatus(false);
				WorkerThread.addTask(drt);
				return;
			}

			int nextEntry = iProtocol.getNextPosition(key, entryNumber);
            while (nextEntry<0)
                nextEntry+=DiskManager.getCap();
			if (nextEntry == primary) {
				drt.setStatus(false);
				WorkerThread.addTask(drt);
			} else {
				DeleteFirstIfExists dfie;
				if (entry == null) {
					dfie = new DeleteFirstIfExists(key, nextEntry, primary, drt);
				} else {
					dfie = new DeleteFirstIfExists(entry, nextEntry, primary, drt);
				}
				DiskManager.addDiskTask(dfie);
			}
		} catch (DifferentSizeOfArrayException e) {
			log.error(e.getMessage());
		}
	}
}
