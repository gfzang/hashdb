package hashdb.communication;

import hashdb.Utilities;
import hashdb.communication.protos.ServerToServer.PeerGreeting;
import hashdb.communication.protos.ServerToServer.SlaveGreeting;
import hashdb.exceptions.CannotCastException;
import hashdb.exceptions.ConnectionActiveException;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.ServerCommunicationException;
import hashdb.main.Server;
import hashdb.main.tasks.setup.PeerConnectionTask;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.protocol.external.RemoteServerInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import org.apache.log4j.Logger;



/**
 * The Class ServerConnectionInstance.
 */
public class ServerConnectionInstance {

	private static final Logger log = Logger.getLogger(ServerConnectionInstance.class);

	private ConnectionInstance incoming;
	private ConnectionInstance outgoing;

	/**
	 * The port.
	 */
	private int port;

	/**
	 * The Constant otherServers.
	 */
	private static final LinkedList<ServerConnectionInstance> otherServers = new LinkedList<ServerConnectionInstance>();

	/**
	 * Gets the collection.
	 *
	 * @return the collection
	 */
	@SuppressWarnings("unchecked")
	public static LinkedList<ServerConnectionInstance> getCollection() {
		return (LinkedList<ServerConnectionInstance>) ServerConnectionInstance.otherServers.clone();
	}

	/**
	 * Instantiates a new server connection instance.
	 *
	 * @param ci   the ci
	 * @param port the port
	 */
	public ServerConnectionInstance(final ConnectionInstance ci, final int port) {
        super();
		try {
			incoming = ci;
			incoming.setServer();
			this.port = port;
			outgoing = new ConnectionInstance(new Socket(ci.socket.getInetAddress(),port));
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		ServerConnectionInstance.otherServers.add(this);
		ConnectionInstance.log.info(
				"New slave server detected at" + ci.c.socket.getInetAddress().getHostAddress() + ":" + port);
	}

	/**
	 * Instantiates a new server connection instance.
	 *
	 * @param outgoing the socket
	 */
	public ServerConnectionInstance(final Socket outgoing) {
		this.outgoing = new ConnectionInstance(outgoing);
		ServerConnectionInstance.otherServers.add(this);
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public byte[] getAddress() {
		if (incoming!= null)
			return this.incoming.socket.getInetAddress().getAddress();
		if  (outgoing!=null)
			return this.outgoing.socket.getInetAddress().getAddress();
		return null;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	public void terminate() throws ConnectionActiveException {
		ServerConnectionInstance.otherServers.remove(this);
		if (incoming!=null)
			incoming.terminate();
		if (outgoing!=null)
			outgoing.terminate();

	}

	public void connectWithMaster() {

		try {
			Server server=Server.getInstance();
			outgoing.startUsing();
			outgoing.send(new short[]{SlaveGreeting.getInstance().getCode()});

			Utilities.receiveAck(outgoing);

			log.info("Received ACK from master");

			final byte[] numOfServersBinary = Utilities.toByteArray(0);
			outgoing.receive(numOfServersBinary);
			final int numOfServers = Utilities.byteArrayToInt(numOfServersBinary);
			RemoteServerInfo.newServerDetected(numOfServers + 1);
			log.info("There are " + numOfServers + " more slaves");
			server.setID(numOfServers + 1);
			log.info("ID now set");
			for (int i = 0; i < numOfServers; i++) {
				final byte[] server_ip = new byte[4];
				final byte[] portBinary = Utilities.toByteArray(0);
				outgoing.receive(server_ip);
				outgoing.receive(portBinary);
				WorkerThread.addTask(new PeerConnectionTask(server_ip, portBinary, server));
			}
			log.info("Sending message with my port number: " + server.getPort());
			outgoing.send(Utilities.toByteArray(server.getPort()));
			Utilities.receiveAck(outgoing);
			log.info("Received ACK from master");
		} catch (ConnectionNotActiveException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (ServerCommunicationException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (CannotCastException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} finally {
			outgoing.stopUsing();
		}
	}

	public void setIncoming(ConnectionInstance ci) {
		incoming = ci;
	}

	private int remoteId;

	public int getRemoteId() {
		return remoteId;
	}

	public void connectWithPeer() {

		try {
			Server server=Server.getInstance();
			outgoing.startUsing();
			outgoing.send(new short[]{PeerGreeting.getInstance().getCode()});
			final byte[] remoteIDbinary = Utilities.toByteArray(0);

			Utilities.receiveAck(outgoing);

			outgoing.receive(remoteIDbinary);
			remoteId = Utilities.byteArrayToInt(remoteIDbinary);

			outgoing.send(Utilities.toByteArray(server.getPort()));
			outgoing.send(Utilities.toByteArray(server.getID()));
			Utilities.receiveAck(outgoing);
		} catch (ConnectionNotActiveException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (ServerCommunicationException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (CannotCastException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}  finally {
			outgoing.stopUsing();
		}

	}

	public ConnectionInstance getOutgoing() {
		return outgoing;
	}
}
