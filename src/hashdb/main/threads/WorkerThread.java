package hashdb.main.threads;

import hashdb.main.tasks.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;



/**
 * The Class WorkerThread.
 */
public class WorkerThread
		extends Thread {

	/**
	 * The queue.
	 */
	private static final BlockingQueue<Task> queue = new LinkedBlockingQueue<Task>();

	/**
	 * The log.
	 */
	private static final Logger log = Logger.getLogger(WorkerThread.class);

	/**
	 * Adds the task.
	 *
	 * @param t the t
	 */
	public static void addTask(final Task t) {
		try {
			WorkerThread.queue.put(t);
		} catch (final InterruptedException e) {
			WorkerThread.log.error(e.getMessage());
		}
	}

	/**
	 * The work.
	 */
	private final boolean work;

	/**
	 * Instantiates a new worker thread.
	 *
	 * @param name the name
	 */
	public WorkerThread(final String name) {
		super(name);
		this.work = true;
		WorkerThread.log.info("Worker thread named " + name + " created");
	}

    boolean retry=true;
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		WorkerThread.log.info("Started!");
		while (this.work) {
			WorkerThread.log.info("Waiting for next task");
			Task t;
			try {
				t = WorkerThread.queue.take();
				WorkerThread.log.info("Working on task: "+t.getClass().getName());

				t.work();
				WorkerThread.log.info("Task completed");

			} catch (final InterruptedException e) {
				WorkerThread.log.error("Worker interrupted while working...");
				WorkerThread.log.error(e.getMessage());
			}
		}
		WorkerThread.log.info("Finished!");
	}

}
