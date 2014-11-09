package hashdb.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/6/13
 * Time: 7:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoSuchServerException
		extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6901317050506882906L;

	public NoSuchServerException() {
		super("No such server");
	}
}
