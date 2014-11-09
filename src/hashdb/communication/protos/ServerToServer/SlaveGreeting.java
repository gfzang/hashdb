package hashdb.communication.protos.ServerToServer;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.ServerConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.*;
import hashdb.main.Server;
import hashdb.main.tasks.setup.InitiateCallback;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.protocol.external.RemoteServerInfo;

import java.util.List;



/**
 * The Class SlaveGreeting.
 */
public class SlaveGreeting
		extends BaseProto {

	/**
	 * The Constant instance.
	 */
	private static final SlaveGreeting instance = new SlaveGreeting();

	static {
		try {
			BaseProto.putProto(SlaveGreeting.instance);
		} catch (final MultipleProtoCodeException e) {
			BaseProto.log.error(e);
			throw new SomethingWentHorriblyWrong();
		}
	}

	/**
	 * Gets the single instance of SlaveGreeting.
	 *
	 * @return single instance of SlaveGreeting
	 */
	public static SlaveGreeting getInstance() {
		return SlaveGreeting.instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.communication.protos.BaseProto#getCode()
	 */
	@Override
	public short getCode() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.communication.protos.BaseProto#getName()
	 */
	@Override
	public String getName() {
		return "Slave greeting";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.communication.protos.BaseProto#invoke(short[],
	 * hashdb.communication.ConnectionInstance,
	 * hashdb.main.Server)
	 */
	@Override
	public void invoke(final short[] data, final ConnectionInstance ci, final Server server) {
		try {
			this.sanityCheck(data, ci);
			final short[] ack = new short[]{Settings.CommunicationCodes.ACK};

			ci.send(ack);

			final List<ServerConnectionInstance> otherServers = ServerConnectionInstance.getCollection();
			ci.send(Utilities.toByteArray(otherServers.size()));
			for (final ServerConnectionInstance sci : otherServers) {
				ci.send(sci.getAddress());
				ci.send(Utilities.toByteArray(sci.getPort()));
			}

			final byte[] port_byte = Utilities.toByteArray(0);
			ci.receive(port_byte);
			ci.send(ack);

			ServerConnectionInstance sci = new ServerConnectionInstance(ci, Utilities.byteArrayToInt(port_byte));
			RemoteServerInfo.addConnectionInstance(ServerConnectionInstance.getCollection().size(), sci);
			WorkerThread.addTask(new InitiateCallback(sci.getOutgoing()));
		} catch (final UnrecoverableCommunicationException e) {
			BaseProto.log.error(e.getMessage());
			throw new SomethingWentHorriblyWrong();
		} catch (final ConnectionNotActiveException e) {
			BaseProto.log.error(e.getMessage());
			throw new SomethingWentHorriblyWrong();
		} catch (final CannotCastException e) {
			BaseProto.log.error(e.getMessage());
			throw new SomethingWentHorriblyWrong();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
