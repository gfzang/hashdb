package hashdb.communication;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.exceptions.ConnectionActiveException;
import hashdb.exceptions.ConnectionNotActiveException;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import org.apache.log4j.Logger;



/**
 * The Class ConnectionInstance.
 */
public class ConnectionInstance {

	/**
	 * The log.
	 */
	static final Logger log = Logger.getLogger(ConnectionInstance.class);

	/**
	 * The socket.
	 */
	Socket socket;

	/**
	 * The c.
	 */
	Communicator c;

	/**
	 * The last active.
	 */
    private long lastActive;

	/**
	 * The active.
	 */
    private boolean active;

	/**
	 * Instantiates a new connection instance.
	 *
	 * @param s the s
	 */
	public ConnectionInstance(final Socket s) {
		this.socket = s;
		this.c = new Communicator(s);
		this.lastActive = new Date().getTime();
	}

	/**
	 * Checks for request.
	 *
	 * @return true, if successful
	 */
	public boolean hasRequest() {
		return this.c.hasRequest();
	}

	/**
	 * Checks if is being used.
	 *
	 * @return true, if is being used
	 */
	public boolean isBeingUsed() {
		return this.active || this.reserved;
	}

	/**
	 * Receive.
	 *
	 * @param buffer the buffer
	 * @throws ConnectionNotActiveException the connection not active exception
	 */
	public void receive(final byte[] buffer) throws ConnectionNotActiveException {
		if (!this.active) throw new ConnectionNotActiveException();
		this.used();
		this.c.receive(buffer);
		this.used();
		ConnectionInstance.log.info("Received data");
	}

	/**
	 * Receive.
	 *
	 * @param buffer the buffer
	 * @throws ConnectionNotActiveException the connection not active exception
	 */
	public void receive(final char[] buffer) throws ConnectionNotActiveException {
		if (!this.active) throw new ConnectionNotActiveException();
		this.used();
		this.c.receive(buffer);
		this.used();
		ConnectionInstance.log.info("Received data");
	}

	/**
	 * Receive.
	 *
	 * @param buffer the buffer
	 * @throws ConnectionNotActiveException the connection not active exception
	 */
	public void receive(final short[] buffer) throws ConnectionNotActiveException {
		if (!this.active) throw new ConnectionNotActiveException();
		this.used();
		this.c.receive(buffer);
		this.used();
		ConnectionInstance.log.info("Received data");
	}

	/**
	 * Send.
	 *
	 * @param buffer the buffer
	 * @throws ConnectionNotActiveException the connection not active exception
	 */
	public void send(final byte[] buffer) throws ConnectionNotActiveException {
		if (!this.active) throw new ConnectionNotActiveException();
		this.used();
		this.c.send(buffer);
		this.used();
		ConnectionInstance.log.info("Sent data");
	}

	/**
	 * Send.
	 *
	 * @param buffer the buffer
	 * @throws ConnectionNotActiveException the connection not active exception
	 */
	public void send(final char[] buffer) throws ConnectionNotActiveException {
		if (!this.active) throw new ConnectionNotActiveException();
		this.used();
		this.c.send(buffer);
		this.used();
		ConnectionInstance.log.info("Sent data");
	}

	/**
	 * Send.
	 *
	 * @param buffer the buffer
	 * @throws ConnectionNotActiveException the connection not active exception
	 */
	public void send(final short[] buffer) throws ConnectionNotActiveException {
		if (!this.active) throw new ConnectionNotActiveException();
		this.used();
		this.c.send(buffer);
		this.used();
		ConnectionInstance.log.info("Sent data");
	}

	/**
	 * Start using.
	 */
	public void startUsing() {
		this.active = true;
		this.used();
	}

	/**
	 * Stop using.
	 */
	public void stopUsing() {
		this.active = false;
		this.used();
	}

	/**
	 * Terminate.
	 *
	 * @throws ConnectionActiveException the connection active exception
	 */
	public void terminate() throws ConnectionActiveException {
		if (shouldBeRemoved) return;
		if (this.active) throw new ConnectionActiveException();
		try {
			this.socket.close();
		} catch (final IOException e) {
			ConnectionInstance.log.error(e.getMessage());
		}
	}

	/**
	 * Time left.
	 *
	 * @return true, if successful
	 */
	public synchronized boolean timeLeft() {
		if (server) return true;
		final long timeInactive = new Date().getTime() - this.lastActive;
		ConnectionInstance.log.trace("Connection is inactive for " + timeInactive + " ms");
		return timeInactive < Settings.Server.INACTIVE_THRESHOLD;
	}

	/**
	 * Used.
	 */
	private synchronized void used() {
		this.lastActive = new Date().getTime();
	}

	private boolean shouldBeRemoved = false;

	public void markForRemoval() {
		shouldBeRemoved = true;
	}

	public boolean cannotAdd() {
		return shouldBeRemoved;
	}

	private boolean server=false;
	public void setServer() {
		server=true;
	}

	public void send(short code) throws ConnectionNotActiveException {
		send(new short[]{code});
	}

	public void send(int length) throws ConnectionNotActiveException {
		send(Utilities.toByteArray(length));
	}

    boolean reserved = false;
    public void reserve() {
        reserved = true;
    }
    public void free() {
        reserved = false;
    }
}
