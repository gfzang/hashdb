package hashdb.main.tasks;

import org.apache.log4j.Logger;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.ServerToServer.BalancerUpdate;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.ServerCommunicationException;
import hashdb.main.SlaveServer;
import hashdb.main.threads.ConnectionManager;
import hashdb.storage.protocol.external.RemoteServerInfo;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/22/13
 * Time: 1:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class SendBalancerInfo implements Task {
	private final Logger log = Logger.getLogger(SendBalancerInfo.class);

	public void work() {
		int numberOfConnections = ConnectionManager.numberOfConnections();
		short myID = (short)SlaveServer.getInstance().getID();
		RemoteServerInfo masterInfo =  RemoteServerInfo.getFromID(0);
		ConnectionInstance masterConnection = masterInfo.getServerConnectionInstance().getOutgoing();
		try {
			masterConnection.startUsing();
			masterConnection.send(new short[]{BalancerUpdate.getInstance().getCode()});
			masterConnection.send(new short[]{myID});
			masterConnection.send(Utilities.toByteArray(numberOfConnections));
			Utilities.receiveAck(masterConnection);
		} catch (ConnectionNotActiveException e) {
			log.error(e.getMessage());
		} catch (ServerCommunicationException e) {
			log.error(e.getMessage());
		} finally {
			masterConnection.stopUsing();
		}
	}
}
