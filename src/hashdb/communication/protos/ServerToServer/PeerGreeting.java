package hashdb.communication.protos.ServerToServer;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.ServerConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.CannotCastException;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.UnrecoverableCommunicationException;
import hashdb.main.Server;
import hashdb.main.tasks.setup.InitiateCallback;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.protocol.external.RemoteServerInfo;



/**
 * User: filip Date: 5/5/13 Time: 4:22 PM.
 */
public class PeerGreeting
		extends BaseProto {

	/**
	 * The Constant instance.
	 */
	private static final PeerGreeting instance = new PeerGreeting();

	/**
	 * Gets the single instance of PeerGreeting.
	 *
	 * @return single instance of PeerGreeting
	 */
	public static PeerGreeting getInstance() {
		return PeerGreeting.instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.communication.protos.BaseProto#getCode()
	 */
	@Override
	public short getCode() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.communication.protos.BaseProto#getName()
	 */
	@Override
	public String getName() {
		return "Peer greeting";
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
			final byte[] portBinary = Utilities.toByteArray(0);
			final byte[] idBinary = Utilities.toByteArray(0);

			ci.send(ack);
			ci.send(Utilities.toByteArray(server.getID()));
			ci.receive(portBinary);
			ci.receive(idBinary);
			ci.send(ack);

			ServerConnectionInstance sci = new ServerConnectionInstance(ci, Utilities.byteArrayToInt(portBinary));
			RemoteServerInfo.addConnectionInstance(Utilities.byteArrayToInt(idBinary), sci);
			WorkerThread.addTask(new InitiateCallback(sci.getOutgoing()));
		} catch (final UnrecoverableCommunicationException e) {
			BaseProto.log.error(e.getMessage());
		} catch (final ConnectionNotActiveException e) {
			BaseProto.log.error(e.getMessage());
		} catch (final CannotCastException e) {
			BaseProto.log.error(e.getMessage());
		} catch (Exception e) {
			BaseProto.log.error(e.getMessage());
		}
	}
}

