package hashdb.communication.protos;

import org.apache.log4j.Logger;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.ClientToServer.ClientGoodbye;
import hashdb.communication.protos.ClientToServer.ClientGreeting;
import hashdb.communication.protos.ClientToServer.ConnectionKeepAlive;
import hashdb.communication.protos.ClientToServer.SlaveDistribute;
import hashdb.communication.protos.ServerToServer.*;
import hashdb.exceptions.MultipleProtoCodeException;
import hashdb.exceptions.UnrecoverableCommunicationException;
import hashdb.main.Server;

import java.util.concurrent.ConcurrentHashMap;


/**
 * The Class BaseProto.
 */
public abstract class BaseProto {

	/**
	 * The Constant log.
	 */
	protected static final Logger log = Logger.getLogger(BaseProto.class);

	/**
	 * The Constant allProtos.
	 */
	private static final ConcurrentHashMap<Short, BaseProto> allProtos = new ConcurrentHashMap<Short, BaseProto>();

	/**
	 * Gets the proto.
	 *
	 * @param req the req
	 * @return the proto
	 */
	public static BaseProto getProto(final short req) {
		return BaseProto.allProtos.get(req);
	}

	/**
	 * Inits the protos.
	 */
	public static void initProtos() {
		init(SlaveGreeting.getInstance());
		init(ClientGoodbye.getInstance());
		init(ClientGreeting.getInstance());
		init(ConnectionKeepAlive.getInstance());
		init(PeerGreeting.getInstance());
		init(SlaveDistribute.getInstance());
		init(BalancerUpdate.getInstance());
		init(Callback.getInstance());
		init(DataTransferRequest.getInstance());
		init(JobResponse.getInstance());
	}

	private static void init(BaseProto bp) {
		BaseProto.log.info(bp.getName() + " loaded");
	}

	/**
	 * Put proto.
	 *
	 * @param bp the bp
	 * @throws MultipleProtoCodeException the multiple proto code exception
	 */
	protected static void putProto(final BaseProto bp) throws MultipleProtoCodeException {
		final short key = bp.getCode();
		final BaseProto tmp = BaseProto.allProtos.get(key);
		if (tmp != null) {
			if (tmp != bp)
			// log.error("Same code used for multiple protos: "+(int)key+" for both "+bp.getName()+" and "+tmp.getName());
			{
				throw new MultipleProtoCodeException();
			} else {
				BaseProto.log.debug("Same proto added multiple times");
			}
		}

		BaseProto.allProtos.put(key, bp);
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public abstract short getCode();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Invoke.
	 *
	 * @param data   the data
	 * @param ci     the ci
	 * @param server the server
	 */
	public abstract void invoke(short[] data, ConnectionInstance ci, Server server);

	/**
	 * Sanity check.
	 *
	 * @param data the data
	 * @param ci   the ci
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	protected boolean sanityCheck(short[] data, ConnectionInstance ci) throws Exception {
		if (data == null) throw new UnrecoverableCommunicationException();
		if (data.length > 1) return false;
		if (ci == null) throw new UnrecoverableCommunicationException();
		return true;
	}
}
