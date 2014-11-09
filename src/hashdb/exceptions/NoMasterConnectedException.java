package hashdb.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/7/13
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
class NoMasterConnectedException
		extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5028938403787309508L;

	public NoMasterConnectedException() {
		super("No master connected");
	}
}
