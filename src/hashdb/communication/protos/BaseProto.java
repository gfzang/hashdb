package hashdb.communication.protos;

import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.structures.ProtoMap;
import hashdb.exceptions.MultipleProtoCodeException;
import hashdb.exceptions.UnrecoverableCommunicationException;
import hashdb.main.Server;

import org.apache.log4j.Logger;


/**
 * Base class for all proto classes.
 */
public abstract class BaseProto {

	/**
	 * The Constant log.
	 */
	protected static final Logger log = Logger.getLogger(BaseProto.class);

	protected BaseProto() {
		try {
			ProtoMap.getInstance().putProto(this);
		} catch (MultipleProtoCodeException e) {
			log.error(getName() + " already loaded");
		}
		BaseProto.log.info(getName() + " loaded");
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
	 * Does actual job.
	 * 
	 * TODO: merge this with Task
	 *
	 * @param data   the data
	 * @param ci     the ci
	 * @param server the server
	 */
	public abstract void invoke(short[] data, ConnectionInstance ci, Server server);

	/**
	 * Sanity check to force 
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
