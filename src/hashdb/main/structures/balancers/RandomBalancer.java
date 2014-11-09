package hashdb.main.structures.balancers;

import hashdb.storage.protocol.external.RemoteServerInfo;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/30/13
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomBalancer extends Balancer {
    Random r = new Random();
    @Override
    public RemoteServerInfo getNext() {
        return RemoteServerInfo.get(r.nextInt(RemoteServerInfo.numberOfConnectedServers()-1));
    }
}
