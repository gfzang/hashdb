package hashdb.exceptions;

/**
 * The Class SomethingWentHorriblyWrong.
 */
public class SomethingWentHorriblyWrong
		extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8240083009397943395L;

	public SomethingWentHorriblyWrong() {
		super("This should never be executed - something is horribly wrong");
	}

}
