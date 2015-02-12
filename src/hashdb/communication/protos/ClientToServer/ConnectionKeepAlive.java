package hashdb.communication.protos.ClientToServer;

import hashdb.Settings;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.exceptions.UnrecoverableCommunicationException;
import hashdb.main.Server;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/7/13
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionKeepAlive
		extends BaseProto {

	/**
	 * The Constant instance.
	 */
	private static final ConnectionKeepAlive instance = new ConnectionKeepAlive();

	public static ConnectionKeepAlive getInstance() {
		return instance;
	}

	@Override
	public short getCode() {
		return Settings.CommunicationCodes.KEEPALIVE;
	}

	@Override
	public String getName() {
		return "Keep alive connection";
	}

	@Override
	public void invoke(short[] data, ConnectionInstance ci, Server server) {
		try {
			sanityCheck(data, ci);
		} catch (UnrecoverableCommunicationException e) {
			log.error(e.getMessage());
			throw new SomethingWentHorriblyWrong();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
