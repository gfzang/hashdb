package hashdb.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/14/13
 * Time: 6:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultipleSingletonInstance
		extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5491456486207022070L;

	public String getMessage() {
		return "Cannot create more than one instance of singletons";
	}
}
