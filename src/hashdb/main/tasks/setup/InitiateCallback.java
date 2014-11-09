package hashdb.main.tasks.setup;

import org.apache.log4j.Logger;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.ServerToServer.Callback;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.ServerCommunicationException;
import hashdb.main.Server;
import hashdb.main.tasks.Task;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/21/13
 * Time: 12:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class InitiateCallback
		implements Task {
	private final ConnectionInstance outgoing;
	private final Logger log = Logger.getLogger(InitiateCallback.class);
	public InitiateCallback(ConnectionInstance outgoing) {
		this.outgoing=outgoing;
		outgoing.setServer();
	}

	public void work() {
		log.info("Callback started");
		outgoing.startUsing();
		try {
			outgoing.send(new short[]{Callback.getInstance().getCode()});
			outgoing.send(Utilities.toByteArray(Server.getInstance().getID()));
			Utilities.sendAck(outgoing);
			Utilities.receiveAck(outgoing);
		} catch (ConnectionNotActiveException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (ServerCommunicationException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		outgoing.stopUsing();
		log.info("Callback ended");
	}
}
