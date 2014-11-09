package hashdb.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/7/13
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
class NoSlaveConnectedException
		extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5603618658842350807L;

	public NoSlaveConnectedException() {
		super("No slave connected");
	}
}
