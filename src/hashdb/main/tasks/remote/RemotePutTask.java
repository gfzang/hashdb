package hashdb.main.tasks.remote;

import org.apache.log4j.Logger;
import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.DataTransferRequest;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.main.tasks.Task;
import hashdb.main.threads.WorkerThread;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/23/13
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemotePutTask
		implements Task {
	private final ConnectionInstance next;
	private final short replyTo;
	private final short internalID;
	private final byte[] key;
	private final byte[] data;
	private final byte[] link;
	public RemotePutTask(ConnectionInstance outgoing, short replyTo, short internalID, byte[] key, byte[] data, byte[] link) {
		this.next = outgoing;
		this.replyTo = replyTo;
		this.internalID = internalID;
		this.key = key;
		this.data = data;
		this.link = link;
	}
	private static final Logger log = Logger.getLogger(RemotePutTask.class);

	public void work() {
		log.info("Remote put task started");
		try {
			synchronized
			(next) {
				if (next.isBeingUsed()) {
					WorkerThread.addTask(this);
					log.info("Remote put task cannot continue, readded to queue");
					return;
				}
				next.startUsing();
			}
			next.send(DataTransferRequest.getInstance().getCode());
			//Utilities.receiveAck(next);
			next.send(replyTo);
			next.send(internalID);
			next.send(Settings.CommunicationCodes.DataTransferCodes.PUT);
			next.send(key);
			next.send(Utilities.toByteArray(data.length));
			next.send(data);
			next.send(link);
			log.info("Remote put task finished");
		} catch (ConnectionNotActiveException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} finally {
			next.stopUsing();
		}
	}
}
