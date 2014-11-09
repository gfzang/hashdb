package hashdb.main.tasks;

import org.apache.log4j.Logger;
import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.exceptions.UnknownProtoException;
import hashdb.main.Server;



/**
 * The Class RequestHandler.
 */
public class RequestHandler
		implements Task {

	/**
	 * The ci.
	 */
	private final ConnectionInstance ci;

	/**
	 * The server.
	 */
	private final Server server;

	/**
	 * The Constant log.
	 */
	private static final Logger log = Logger.getLogger(RequestHandler.class);

	/**
	 * Instantiates a new request handler.
	 *
	 * @param ci     the ci
	 * @param server the server
	 */
	public RequestHandler(final ConnectionInstance ci, final Server server) {
		this.ci = ci;
		this.ci.startUsing();
		this.server = server;
	}
    private static final byte[] NACKBUFFER = new byte[Settings.Fields.END.getOffset()];
    static {
        Utilities.copy(new byte[]{Settings.CommunicationCodes.NACK},NACKBUFFER, Utilities.Align.left, Utilities.Strategy.COPY);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.main.tasks.Task#work()
	 */
	public void work() {
        BaseProto bp;
		final short[] req = new short[1];
		try {
            if (!ci.hasRequest())
                return;
			this.ci.receive(req);
			bp = BaseProto.getProto(req[0]);
			if (bp == null) throw new UnknownProtoException();
			log.info("Invocation of " + bp.getName() + " started!");
			bp.invoke(req, this.ci, this.server);
			log.info("Invocation of " + bp.getName() + " finished");
		} catch (final ConnectionNotActiveException e) {
            e.printStackTrace();
			RequestHandler.log.error(e.toString());
            throw new SomethingWentHorriblyWrong();
		} catch (final UnknownProtoException e) {
			RequestHandler.log.error(e.getMessage());
		} finally {
			this.ci.stopUsing();
		}
	}

}
