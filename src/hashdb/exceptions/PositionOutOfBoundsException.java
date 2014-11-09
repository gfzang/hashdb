package hashdb.exceptions;



/**
 * The Class PositionOutOfBoundsException.
 */
public class PositionOutOfBoundsException
		extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1103681865074876160L;

	/**
	 * Instantiates a new position out of bounds exception.
	 *
	 * @param position the position
	 * @param capacity the capacity
	 */
	public PositionOutOfBoundsException(final long position, final long capacity) {
		super("Invalid position " + position + " requied for storage with " + capacity + " capacity");
	}
}
