package hashdb.communication.protos.ServerToServer;

import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.main.Server;
import hashdb.main.structures.balancers.PreciseBalancer;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/22/13
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class BalancerUpdate extends BaseProto {

	private static final BalancerUpdate instance = new BalancerUpdate();

	public static BalancerUpdate getInstance() {return instance;}

	@Override
	public short getCode() {
		return 6;
	}

	@Override
	public String getName() {
		return "Balancer update";
	}

	@Override
	public void invoke(short[] data, ConnectionInstance ci, Server server) {
		try {
			sanityCheck(data, ci);
			short[] serverID = new short[1];
			byte[] numOfConnectionsByte = Utilities.toByteArray(0);
			ci.receive(serverID);
			ci.receive(numOfConnectionsByte);
			Utilities.sendAck(ci);
			int numOfConnections = Utilities.byteArrayToInt(numOfConnectionsByte);
			PreciseBalancer.update(serverID[0], numOfConnections);
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}
}
