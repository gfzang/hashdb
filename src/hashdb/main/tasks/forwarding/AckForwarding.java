package hashdb.main.tasks.forwarding;

import org.apache.log4j.Logger;
import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.ServerCommunicationException;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.main.threads.WorkerThread;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/22/13
 * Time: 11:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class AckForwarding
		extends ForwardingTask {

	public AckForwarding(ConnectionInstance ci) {
		super(ci);
	}

	public void work() {
		Logger log = Logger.getLogger(AckForwarding.class);
		log.info("Ack forwarding started");
		synchronized (ci) {
			if (ci.isBeingUsed()) {
				WorkerThread.addTask(this);
				log.info("ACK forwarding cannot continue readded to queue");
				return;
			}
			ci.startUsing();
			incoming.startUsing();
		}
		try {
			Utilities.receiveAck(incoming);
			Utilities.sendAck(ci);
		} catch (ConnectionNotActiveException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (ServerCommunicationException e) {
			try {
				ci.send(Settings.CommunicationCodes.NACK);
			} catch (ConnectionNotActiveException e1) {
				throw new SomethingWentHorriblyWrong();
			}
		} finally {
			ci.stopUsing();
			//incoming.stopUsing();
		}
		log.info("ACK forwarding finished");

	}
}
