/**
 * 
 */
package hashdb.communication.protos.structures;

import hashdb.communication.protos.DataTransferRequest;
import hashdb.communication.protos.ClientToServer.ClientGoodbye;
import hashdb.communication.protos.ClientToServer.ClientGreeting;
import hashdb.communication.protos.ClientToServer.ConnectionKeepAlive;
import hashdb.communication.protos.ClientToServer.SlaveDistribute;
import hashdb.communication.protos.ServerToServer.BalancerUpdate;
import hashdb.communication.protos.ServerToServer.Callback;
import hashdb.communication.protos.ServerToServer.JobResponse;
import hashdb.communication.protos.ServerToServer.PeerGreeting;
import hashdb.communication.protos.ServerToServer.SlaveGreeting;
import hashdb.main.tasks.Task;

/**
 * @author Filip
 *
 */
public class ProtoInitializationForcer implements Task{

	@Override
	public void work() {
		ClientGoodbye.getInstance();
		ClientGreeting.getInstance();
		ConnectionKeepAlive.getInstance();
		SlaveDistribute.getInstance();
		BalancerUpdate.getInstance();
		Callback.getInstance();
		JobResponse.getInstance();
		PeerGreeting.getInstance();
		SlaveGreeting.getInstance();
		DataTransferRequest.getInstance();
	}
}
