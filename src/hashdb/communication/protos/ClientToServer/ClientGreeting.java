package hashdb.communication.protos.ClientToServer;

import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.MultipleProtoCodeException;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.main.Server;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/7/13
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientGreeting
		extends BaseProto {
	private static final ClientGreeting instance = new ClientGreeting();

	public static ClientGreeting getInstance() {
		return instance;
	}

	@Override
	public short getCode() {
		return 4;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getName() {
		return "Client greeting";  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void invoke(short[] data, ConnectionInstance ci, Server server) {
		try {
			sanityCheck(data, ci);
			Utilities.sendAck(ci);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}
}
