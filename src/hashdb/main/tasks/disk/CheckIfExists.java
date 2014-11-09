package hashdb.main.tasks.disk;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.storage.entities.Entry;
import hashdb.storage.entities.fields.EntryKeyLink;
import hashdb.exceptions.DifferentSizeOfArrayException;
import hashdb.main.tasks.response.ExistsResponseTask;
import hashdb.main.threads.DiskManager;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.EntryReaderWriter;

public class CheckIfExists
		extends DiskTask {

	public CheckIfExists(Entry entry, ExistsResponseTask ert) {
		super(entry.getKey(), entry, ert);
	}

	public CheckIfExists(EntryKeyLink key, ExistsResponseTask ert) {
		super(key.getPayload(), null, ert);
	}

    public CheckIfExists(byte[] key, ExistsResponseTask ert) {
        super(key, null, ert);
    }

	private CheckIfExists(byte[] key, int nextEntry, int primary, ExistsResponseTask ert) {
		super(key, null, nextEntry, primary, ert);
	}

	private CheckIfExists(Entry entry, int nextEntry, int primary, ExistsResponseTask ert) {
		super(entry.getKey(), entry, nextEntry, primary, ert);
	}

	public void work() {
		try {
			EntryReaderWriter erw = DiskManager.getErw();
			Entry res = erw.read(entryNumber);
			ExistsResponseTask ert = (ExistsResponseTask) rt;

			final boolean empty = Utilities.checkMask(Settings.EMPTY, res.getStatus());
			final boolean jumped = Utilities.checkMask(Settings.JUMPED, res.getStatus());
			if (!empty) {
				if (Utilities.sameArray(key, res.getKey())) {
					if (checkEntry(entry, res)) {
						ert.setStatus(true);
						WorkerThread.addTask(ert);
						return;
					}
				}
			} else if (!jumped) {
				ert.setStatus(false);
				WorkerThread.addTask(ert);
				return;
			}
			int nextEntry = iProtocol.getNextPosition(key, entryNumber);
            while (nextEntry<0)
                nextEntry+=DiskManager.getCap();
			if (nextEntry == primary) {
				ert.setStatus(false);
				WorkerThread.addTask(ert);
			} else {
				CheckIfExists cie;
				if (entry == null) {
					cie = new CheckIfExists(key, nextEntry, primary, ert);
				} else {
					cie = new CheckIfExists(entry, nextEntry, primary, ert);
				}
				DiskManager.addDiskTask(cie);
			}
		} catch (DifferentSizeOfArrayException e) {
			log.error(e);
		}
	}
}
