package hashdb.main.threads;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.exceptions.DifferentSizeOfArrayException;
import hashdb.exceptions.InconsistentSettingsException;
import hashdb.exceptions.MultipleSingletonInstance;
import hashdb.main.tasks.disk.DiskTask;
import hashdb.storage.EntryReaderWriter;
import hashdb.storage.PersistentStorage;
import hashdb.storage.entities.Entry;
import hashdb.storage.protocol.internal.DoubleHashProtocol;
import hashdb.storage.protocol.internal.InternalProtocol;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/14/13
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
public final class DiskManager
		extends Thread {
	private static final Logger log = Logger.getLogger(DiskManager.class);
	private static PersistentStorage ps;
	private static boolean formatRequest = true;

	public static EntryReaderWriter getErw() {
		return erw;
	}

	private static EntryReaderWriter erw;
	private static DiskManager instance = null;

	public static InternalProtocol getIprotocol() {
		return iprotocol;
	}

	private static InternalProtocol iprotocol;

	private static final BlockingQueue<DiskTask> queue = new LinkedBlockingDeque<DiskTask>();
	private static final LinkedList<DiskTask> bucket = new LinkedList<DiskTask>();

	public static void startDiskManager(String path, int cap) throws MultipleSingletonInstance {
		if (instance != null) throw new MultipleSingletonInstance();
		instance = new DiskManager(path, cap);
		log.info("Starting disk access manager");
		instance.start();
		log.info("Disk access manager now started!");
	}

    private int cap;

    public static int getCap() {
        return instance.cap;
    }

    private DiskManager(String path, int cap) {
		super("Disk manager");
		try {
			ps = new PersistentStorage(path, cap * Settings.Fields.END.getOffset());
            this.cap=cap;
			iprotocol = new DoubleHashProtocol(cap);
			erw = new EntryReaderWriter(ps);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	private boolean format() {
		final Entry dummy = Entry.getDummy();
		try {
			Utilities.setMask(Settings.EMPTY, dummy.getStatus());
		} catch (final DifferentSizeOfArrayException e) {
			throw new InconsistentSettingsException();
		}
		try {
			for (int i = 0; i < erw.size(); i++)
				erw.write(dummy, i);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		log.info("Started");
		try {
			while (true) {
				log.trace("Waiting for next task");
				while (formatRequest) {
					log.info("Formatting on request");
					formatRequest = !format();
					log.info("Formatting completed");
				}
				DiskTask dt = getNextTask();
				while (dt == null) {
					sleep(100);
					dt = getNextTask();
				}
				log.trace("Working on task");
				dt.work();
				log.info("Task "+dt.getClass().getSimpleName()+" finished!");
			}
		} catch (InterruptedException e) {
			log.error(e);
		}
	}

	private synchronized DiskTask getNextTask() {
		if (bucket.size() != 0) return bucket.poll();
		if (queue.size() == 0) return null;
		DiskTask tmp = queue.poll();
		while (tmp != null) {
			bucket.add(tmp);
			tmp = queue.poll();
		}
		DiskTask.invertOrder();
		Collections.sort(bucket);
		return bucket.poll();
	}

	public static void addDiskTask(DiskTask dt) {
		try {
			synchronized (instance) {

				queue.put(dt);
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}

	public static void formatRequested() {
		formatRequest = true;
	}
}
