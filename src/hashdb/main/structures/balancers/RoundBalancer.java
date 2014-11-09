package hashdb.main.structures.balancers;

import hashdb.storage.protocol.external.RemoteServerInfo;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/21/13
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoundBalancer
		extends Balancer {

	private int last=0;

	@Override
	public RemoteServerInfo getNext() {
		try {
			return RemoteServerInfo.get(last%RemoteServerInfo.numberOfConnectedServers());
		}
		finally {
			last++;
			last%=RemoteServerInfo.numberOfConnectedServers();
		}
	}
}
