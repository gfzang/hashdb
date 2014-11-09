package hashdb.communication;

import org.apache.log4j.Logger;
import hashdb.Utilities;
import hashdb.exceptions.CannotCastException;
import java.io.*;
import java.net.Socket;


/**
 * The Class Communicator.
 */
public class Communicator {

	/**
	 * The log.
	 */
	private static final Logger log = Logger.getLogger(Communicator.class);

	/**
	 * Gets the communicator.
	 *
	 * @param s the s
	 * @return the communicator
	 */
	public static Communicator getCommunicator(final Socket s) {
		return new Communicator(s);
	}

	/**
	 * Gets the communicator.
	 *
	 * @param ip   the ip
	 * @param port the port
	 * @return the communicator
	 */
	public static Communicator getCommunicator(final String ip, final int port) {
		return new Communicator(ip, port);
	}

	/**
	 * The socket.
	 */
	Socket socket;

	/**
	 * The input.
	 */
	private DataInputStream input;

	/**
	 * The output.
	 */
	private DataOutputStream output;

	/**
	 * Instantiates a new communicator.
	 *
	 * @param s the s
	 */
	public Communicator(final Socket s) {
		this.socket = s;
		try {
			this.createInputOutput(this.socket);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Instantiates a new communicator.
	 *
	 * @param ip   the ip
	 * @param port the port
	 */
	private Communicator(final String ip, final int port) {
		try {
			this.socket = new Socket(ip, port);
			this.createInputOutput(this.socket);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the input output.
	 *
	 * @param socket the socket
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void createInputOutput(final Socket socket) throws IOException {
		this.input = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(socket.getOutputStream());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		this.input.close();
		this.output.close();
		this.socket.close();
		super.finalize();
	}

	/**
	 * Checks for request.
	 *
	 * @return true, if successful
	 */
	public boolean hasRequest() {
		boolean result = false;
		try {
			result = this.input.available() != 0;
		} catch (final IOException e) {
			Communicator.log.error(e.getMessage());
		}
		return result;

	}

	/**
	 * Receive.
	 *
	 * @param cbuf the buffer
	 */
	public void receive(final byte[] cbuf) {
        try {
            int received = 0;
            int expected = cbuf.length;
            while (received< expected) {
                final byte[] partBuff = new byte[expected-received];
                int thisIter = this.input.read(partBuff);
                if (thisIter==-1) continue;
                System.arraycopy(partBuff,0,cbuf,received,thisIter);
                received+=thisIter;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Receive.
	 *
	 * @param cbuf the cbuf
	 */
	public void receive(char[] cbuf) {
		try {
            byte[] bytes = Utilities.toByteArray(cbuf);
            receive(bytes);
            char[] _res=Utilities.toCharArray(bytes);
            System.arraycopy(_res,0,cbuf,0,cbuf.length);
		} catch (CannotCastException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

	/**
	 * Receive.
	 *
	 * @param buffer the buffer
	 */
	public void receive(final short[] buffer) {
        try {
            byte[] bytes = Utilities.toByteArray(buffer);
            receive(bytes);
            short[] _res=Utilities.toShortArray(bytes);
            System.arraycopy(_res, 0, buffer, 0, buffer.length);
        } catch (CannotCastException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

	}

	/**
	 * Send.
	 *
	 * @param array the array
	 */
	public void send(final byte[] array) {
        try {
            this.output.write(array);
            this.output.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
	}

	/**
	 * Send.
	 *
	 * @param buf the buf
	 */
	public void send(final char[] buf) {

        try {
            this.output.write(Utilities.toByteArray(buf));
            this.output.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

	}

	/**
	 * Send.
	 *
	 * @param buffer the buffer
	 */
	public void send(short[] buffer) {
        try {
            this.output.write(Utilities.toByteArray(buffer));
            this.output.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
	}
}
