package hashdb.communication.protos.ClientToServer;

import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.MultipleProtoCodeException;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.main.Server;
import hashdb.main.tasks.TerminateConnectionTask;
import hashdb.main.threads.WorkerThread;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/8/13
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClientGoodbye
		extends BaseProto {

	private static final ClientGoodbye instance = new ClientGoodbye();


	public static ClientGoodbye getInstance() {
		return instance;
	}

	@Override
	public short getCode() {
		return 5;
	}

	@Override
	public String getName() {
		return "Client goodbye";
	}

	@Override
	public void invoke(short[] data, ConnectionInstance ci, Server server) {
		try {
			sanityCheck(data, ci);
			Utilities.sendAck(ci);
			WorkerThread.addTask(new TerminateConnectionTask(ci));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
