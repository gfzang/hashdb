package hashdb.main.tasks.setup;

import hashdb.Utilities;
import hashdb.communication.ServerConnectionInstance;
import hashdb.exceptions.CannotCastException;
import hashdb.main.Server;
import hashdb.main.tasks.Task;
import hashdb.storage.protocol.external.RemoteServerInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;



/**
 * The Class PeerConnectionTask.
 */
public class PeerConnectionTask
		implements Task {

	/**
	 * The ip binary.
	 */
	private final byte[] ipBinary;

	/**
	 * The port binary.
	 */
	private final byte[] portBinary;

	/**
	 * The log.
	 */
	private final Logger log = Logger.getLogger(PeerConnectionTask.class);

	/**
	 * Instantiates a new peer connection task.
	 *
	 * @param server_ip  the server_ip
	 * @param portBinary the port binary
	 * @param server     the server
	 */
	public PeerConnectionTask(final byte[] server_ip, final byte[] portBinary, final Server server) {
		this.ipBinary = server_ip;
		this.portBinary = portBinary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.main.tasks.Task#work()
	 */
	public void work() {
		ServerConnectionInstance ci;
		try {
			ci = new ServerConnectionInstance(
					new Socket(InetAddress.getByAddress(this.ipBinary), Utilities.byteArrayToInt(this.portBinary)));
			ci.connectWithPeer();

			RemoteServerInfo.addConnectionInstance(ci.getRemoteId(), ci);

		} catch (final UnknownHostException uhe) {
			this.log.error(uhe.getMessage());
		} catch (final CannotCastException e) {
			this.log.error(e.getMessage());
		} catch (final IOException e) {
			this.log.error(e.getMessage());
		}
    }
}
