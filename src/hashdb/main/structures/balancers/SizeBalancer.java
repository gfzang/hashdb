package hashdb.main.structures.balancers;

import hashdb.storage.protocol.external.RemoteServerInfo;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/21/13
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class SizeBalancer
		extends Balancer {

	private static final Random r= new Random();

	@Override
	public RemoteServerInfo getNext() {
		int size = RemoteServerInfo.getSumRelativeWeight();
		return RemoteServerInfo.getFromWeighted(r.nextInt(size));
	}
}
