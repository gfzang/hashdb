package hashdb.exceptions;

/**
 * The Class ConnectionNotActiveException.
 */
public class ConnectionNotActiveException
		extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8074118008334447422L;

	public ConnectionNotActiveException() {
		super("Cannot use inactive connection!");
	}

}
