package hashdb.communication.protos.ClientToServer;

import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.ServerConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.ServerCommunicationException;
import hashdb.exceptions.UnrecoverableCommunicationException;
import hashdb.main.Server;
import hashdb.main.structures.balancers.Balancer;
import hashdb.storage.protocol.external.RemoteServerInfo;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/7/13
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SlaveDistribute
		extends BaseProto {

	private static final SlaveDistribute instance = new SlaveDistribute();
	private static final Balancer BALANCER_INSTANCE = Balancer.getInstance();

	public static SlaveDistribute getInstance() {
		return instance;
	}

	@Override
	public short getCode() {
		return 3;
	}

	@Override
	public String getName() {
		return "Slave distributor";
	}

	@Override
	public void invoke(short[] data, ConnectionInstance ci, Server server) {
		try {
			sanityCheck(data, ci);
			Utilities.sendAck(ci);
			RemoteServerInfo rsi = BALANCER_INSTANCE.getNext();
			ServerConnectionInstance sci = rsi.getServerConnectionInstance();
			byte[] raw_ip = sci.getAddress();
			ci.send(raw_ip);
			int port = sci.getPort();
			byte[] raw_port = Utilities.toByteArray(port);
			ci.send(raw_port);
			Utilities.receiveAck(ci);
		} catch (UnrecoverableCommunicationException e) {
			log.error(e.getMessage());
		} catch (ConnectionNotActiveException e) {
			log.error(e.getMessage());
		} catch (ServerCommunicationException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
