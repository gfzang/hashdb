package hashdb.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/22/13
 * Time: 1:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class NotSlaveServer
		extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1407663131977857465L;

	public String getMessage() {
		return "This task should only be invoked on slave server side!";
	}
}
