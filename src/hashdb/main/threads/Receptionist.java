package hashdb.main.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;



/**
 * The Class Receptionist.
 */
public class Receptionist
		extends Thread {

	/**
	 * The log.
	 */
	private final Logger log = Logger.getLogger(Receptionist.class);

	/**
	 * The work.
	 */
	private final boolean work;

	/**
	 * The port.
	 */
	private final int port;

	/**
	 * The socket.
	 */
	private final ServerSocket socket;

	/**
	 * Instantiates a new receptionist.
	 *
	 * @param port the port
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Receptionist(final int port) throws IOException {
		super("Receptionist");
		this.work = true;
		this.port = port;
		this.socket = new ServerSocket(this.port);
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		this.log.info("Running!");
		try {
			while (this.work) {
				this.log.info("Waiting for connection");
				final Socket tmp = this.socket.accept();
				this.log.info("New incoming connection!");
				ConnectionManager.addNewConnection(tmp);
				this.log.info("Connection sent to ConnectionManager!");
				ConnectionManager.printStatus();
			}
		} catch (final IOException e) {
			this.log.error(e.getMessage());
		}
	}

}
