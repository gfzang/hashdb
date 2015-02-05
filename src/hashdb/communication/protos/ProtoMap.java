package hashdb.communication.protos;

import hashdb.exceptions.MultipleProtoCodeException;

import java.util.concurrent.ConcurrentHashMap;

public class ProtoMap {

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
				BaseProto.log.debug("Same proto added multiple times");
			}
		}

		allProtos.put(key, bp);
	}

	static ProtoMap instance = new ProtoMap();
	
	public static ProtoMap getInstance() {
		return instance;
	}
	
	private ProtoMap()
	{}
}
