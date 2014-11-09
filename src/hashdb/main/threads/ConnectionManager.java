package hashdb.main.threads;

import org.apache.log4j.Logger;
import hashdb.communication.ConnectionInstance;
import hashdb.exceptions.ConnectionActiveException;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.main.Server;
import hashdb.main.tasks.RequestHandler;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



/**
 * The Class ConnectionManager.
 */
public class ConnectionManager
		extends Thread {

	/**
	 * The log.
	 */
	private static final Logger log = Logger.getLogger(ConnectionManager.class);

	/**
	 * The instance.
	 */
	private static ConnectionManager instance = null;

	/**
	 * Adds the new connection.
	 *
	 * @param connection the connection
	 */
	private static void addNewConnection(final ConnectionInstance connection) {
		if (connection.cannotAdd()) return;
		try {
			ConnectionManager.instance.connections.put(connection);
			ConnectionManager.log.trace("New connection added. Now " + instance.connections.size() + " connections");
		} catch (final InterruptedException e) {
			ConnectionManager.log.info(e.getMessage());
		}

	}

	/**
	 * Adds the new connection.
	 *
	 * @param tmp the tmp
	 */
	public static void addNewConnection(final Socket tmp) {
		try {
			ConnectionManager.instance.connections.put(new ConnectionInstance(tmp));
			ConnectionManager.log.info("New connection is now being watched");
		} catch (final InterruptedException e) {
			ConnectionManager.log.error(e.getMessage());
		}
	}

	/**
	 * Removes the.
	 *
	 * @param ci the ci
	 */
	public static void remove(final ConnectionInstance ci) {
		ConnectionManager.instance.connections.remove(ci);
		log.info("Connection removed. " + instance.connections.size() + " connections remaining");
	}

	/**
	 * The connections.
	 */
	private final BlockingQueue<ConnectionInstance> connections;

	/**
	 * The server.
	 */
	private final Server server;

	/**
	 * Instantiates a new connection manager.
	 *
	 * @param server the server
	 */
	public ConnectionManager(final Server server) {
		super("ConnManager");
		if (ConnectionManager.instance != null) throw new SomethingWentHorriblyWrong();
		ConnectionManager.instance = this;
		this.connections = new LinkedBlockingQueue<ConnectionInstance>();
		this.server = server;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		ConnectionManager.log.info("Started!");
		try {
			while (true) {
				final ConnectionInstance ci = this.connections.take();
				ConnectionManager.log.trace("Checking new connection");
				if (ci.hasRequest() && !ci.isBeingUsed()) {
					log.info(
							"Connection " + ci.toString() + " has request. There are " + connections.size() + " other connections");
					WorkerThread.addTask(new RequestHandler(ci, this.server));
					ConnectionManager.log.info("Task created!");
				} else if (!ci.timeLeft()) {
					ci.terminate();
					ConnectionManager.log.info(
							"Connection terminated! " + this.connections.size() + " connections remaining");
					continue;
				}
				ConnectionManager.addNewConnection(ci);
				ConnectionManager.log.trace("Connection prepared for processing.");
			}
		} catch (final ConnectionActiveException ie) {
			ConnectionManager.log.error(ie.getMessage());
		} catch (final InterruptedException ie) {
			ConnectionManager.log.error(ie.getMessage());
		}
	}

	public static void printStatus() {
		log.info(instance.connections.size() + " connections active");
	}

	public static int numberOfConnections() {
		return instance.connections.size();
	}
}
