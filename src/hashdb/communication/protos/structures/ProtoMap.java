package hashdb.communication.protos.structures;

import hashdb.communication.protos.BaseProto;
import hashdb.communication.protos.ClientToServer.ClientGoodbye;
import hashdb.communication.protos.ClientToServer.ClientGreeting;
import hashdb.communication.protos.ClientToServer.ConnectionKeepAlive;
import hashdb.exceptions.MultipleProtoCodeException;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class ProtoMap{


	protected static final Logger log = Logger.getLogger(BaseProto.class);
	
	private final ConcurrentHashMap<Short, BaseProto> allProtos = new ConcurrentHashMap<Short, BaseProto>();
	
	public BaseProto getProto(final short req) {
		return allProtos.get(req);
	}
	
	public void putProto(final BaseProto bp) throws MultipleProtoCodeException {
		final short key = bp.getCode();
		final BaseProto tmp = allProtos.get(key);
		if (tmp != null) {
			if (tmp != bp)
			// log.error("Same code used for multiple protos: "+(int)key+" for both "+bp.getName()+" and "+tmp.getName());
			{
				throw new MultipleProtoCodeException();
			} else {
				log.debug("Same proto added multiple times");
			}
		}

		allProtos.put(key, bp);
	}

	static ProtoMap instance = new ProtoMap();
	
	public static ProtoMap getInstance() {
		return instance;
	}
	
	private ProtoMap()
	{
	}
}
