package hashdb.main.structures.balancers;

import hashdb.storage.protocol.external.RemoteServerInfo;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/21/13
 * Time: 7:41 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Balancer {
	private static final Balancer instance = new RoundBalancer();

	public static Balancer getInstance() {
		return instance;
	}

	public abstract  RemoteServerInfo getNext();
}
