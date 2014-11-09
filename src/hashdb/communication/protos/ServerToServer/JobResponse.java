package hashdb.communication.protos.ServerToServer;

import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.MultipleProtoCodeException;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.main.Server;
import hashdb.main.structures.RemoteTaskPool;
import hashdb.main.tasks.forwarding.ForwardingTask;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/22/13
 * Time: 11:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobResponse extends BaseProto {
	private static final JobResponse instance = new JobResponse();

	static {
		try {
			BaseProto.putProto(instance);
		} catch (final MultipleProtoCodeException e) {
			BaseProto.log.error(e);
			throw new SomethingWentHorriblyWrong();
		}
	}

	@Override
	public short getCode() {
		return 8;
	}

	@Override
	public String getName() {
		return "Remote job response";
	}

	@Override
	public void invoke(short[] data, ConnectionInstance ci, Server server) {
		try {
			sanityCheck(data,ci);
			short[] jobID=new short[1];
			ci.receive(jobID);
			ForwardingTask ft=RemoteTaskPool.fetchTask(jobID[0]);
			ft.setIncoming(ci);
            ci.reserve();
			ft.work();
            ci.free();
		} catch (ConnectionNotActiveException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}


	}

	public static BaseProto getInstance() {
		return instance;
	}
}
