package hashdb.communication.protos.ServerToServer;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.*;
import hashdb.main.Server;
import hashdb.storage.protocol.external.RemoteServerInfo;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/20/13
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Callback extends BaseProto {
	/**
	 * The Constant instance.
	 */
	private static final Callback instance = new Callback();

	static {
		try {
			BaseProto.putProto(instance);
		} catch (final MultipleProtoCodeException e) {
			BaseProto.log.error(e);
			throw new SomethingWentHorriblyWrong();
		}
	}

	/**
	 * Gets the single instance of PeerGreeting.
	 *
	 * @return single instance of PeerGreeting
	 */
	public static Callback getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see hashdb.communication.protos.BaseProto#getCode()
	 */
	@Override
	public short getCode() {
		return Settings.CommunicationCodes.CALLBACK;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see hashdb.communication.protos.BaseProto#getName()
	 */
	@Override
	public String getName() {
		return "Callback";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see hashdb.communication.protos.BaseProto#invoke(short[],
	 * hashdb.communication.ConnectionInstance,
	 * hashdb.main.Server)
	 */
	@Override
	public void invoke(final short[] data, final ConnectionInstance ci, final Server server) {
		try {
			ci.setServer();
			byte[] id_raw = Utilities.toByteArray(0);
			ci.receive(id_raw);
			RemoteServerInfo.get(Utilities.byteArrayToInt(id_raw)).getServerConnectionInstance().setIncoming(ci);
			Utilities.receiveAck(ci);
			Utilities.sendAck(ci);
		} catch (final ConnectionNotActiveException e) {
			BaseProto.log.error(e.getMessage());
		} catch (final CannotCastException e) {
			BaseProto.log.error(e.getMessage());
		} catch (Exception e) {
			BaseProto.log.error(e.getMessage());
		}
	}
}
