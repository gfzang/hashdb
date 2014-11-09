package hashdb.exceptions;

/**
 * The Class ConnectionActiveException.
 */
public class ConnectionActiveException
		extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5970548062409284268L;

	public ConnectionActiveException() {
		super("Cannot terminate active connection!");
	}

}
