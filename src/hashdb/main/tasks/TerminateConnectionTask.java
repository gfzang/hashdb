package hashdb.main.tasks;

import hashdb.communication.ConnectionInstance;
import hashdb.exceptions.ConnectionActiveException;
import hashdb.main.threads.ConnectionManager;
import hashdb.main.threads.WorkerThread;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/8/13
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class TerminateConnectionTask
		implements Task {
	private final ConnectionInstance ci;

	public TerminateConnectionTask(ConnectionInstance connectionInstance) {
		ci = connectionInstance;
	}


	public void work() {
		try {
			ci.terminate();
			ConnectionManager.remove(ci);
		} catch (ConnectionActiveException e) {
			WorkerThread.addTask(new TerminateConnectionTask(ci));
		}
	}
}
