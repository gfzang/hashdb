package hashdb.storage.protocol.external;

import org.apache.log4j.Logger;

import hashdb.communication.ServerConnectionInstance;
import hashdb.exceptions.NoSuchServerException;
import hashdb.exceptions.SettingIsNotIntroductionException;
import hashdb.main.MasterServer;
import hashdb.main.Server;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/6/13
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoteServerInfo {

	private static final Logger log = Logger.getLogger(RemoteServerInfo.class);

	private static int greatestServerId = 0;

	public int getServerID() {
		return serverID;
	}

	private final int serverID;
	private BigInteger lowerBound;
	private BigInteger greaterBound;

	private static final Queue<RemoteServerInfo> remoteServerInfoQueue = new LinkedBlockingQueue<RemoteServerInfo>();
	private static final List<RemoteServerInfo> allRemoteServerInfos = new LinkedList<RemoteServerInfo>();
	private ServerConnectionInstance sci;
	private static final Random r = new Random();

	private RemoteServerInfo(int serverID, BigInteger lowerBound, BigInteger greaterBound) {
		this.lowerBound = lowerBound;
		this.greaterBound = greaterBound;
		this.serverID = serverID;
		if (Server.getInstance() instanceof MasterServer && serverID==0)
			return;
		allRemoteServerInfos.add(this);
	}

	public static RemoteServerInfo getBase(int byteSize) {
		byte[] array = new byte[byteSize];
		array[0] = (byte) 0x80;
		BigInteger lbound = new BigInteger(array);
		for (@SuppressWarnings("unused") byte x : array)
			x = (byte) 0xFF;
		array[0] = 0x7F;
		BigInteger rbound = new BigInteger(array);

		RemoteServerInfo result = new RemoteServerInfo(1, lbound, rbound);
		greatestServerId = 1;
		remoteServerInfoQueue.add(result);
		return result;
	}

	private static final BigInteger TWO = new BigInteger("2");
	private static final BigInteger ONE = BigInteger.ONE;

	public static RemoteServerInfo getNextRandom() {
		return allRemoteServerInfos.get(r.nextInt(allRemoteServerInfos.size()));
	}

	private RemoteServerInfo split(int id) {
		BigInteger sum = this.lowerBound.add(this.greaterBound);
		BigInteger half = sum.divide(TWO);
		RemoteServerInfo result = new RemoteServerInfo(id, half.add(ONE), this.greaterBound);
		this.greaterBound = half;
		return result;
	}

	public static void newServerDetected(int id) {
		if (id <= greatestServerId || greatestServerId == 0) return;
		while (greatestServerId < id) {
			greatestServerId++;
			RemoteServerInfo old = remoteServerInfoQueue.poll();
			RemoteServerInfo tmp = old.split(greatestServerId);
			remoteServerInfoQueue.add(old);
			remoteServerInfoQueue.add(tmp);
		}
		greatestServerId = id;
		report();
	}

	private static void report() {
		for (RemoteServerInfo rsi : allRemoteServerInfos)
			log.info("REPORT " + rsi.toString());
	}

	public static RemoteServerInfo findID(byte[] keyBinary) throws NoSuchServerException {
		BigInteger key = new BigInteger(keyBinary);
		for (RemoteServerInfo s : allRemoteServerInfos)
			if (s.contains(key)) return s;
		throw new NoSuchServerException();
	}

	private boolean contains(BigInteger key) {
		return key.compareTo(this.lowerBound) != -1 && key.compareTo(this.greaterBound) != 1;
	}

	public static void addConnectionInstance(int serverID, ServerConnectionInstance sci) {
		newServerDetected(serverID);
		log.info("New server detected with id " + serverID);
		report();
		for (RemoteServerInfo rsi : allRemoteServerInfos)
			if (rsi.serverID == serverID) rsi.sci = sci;
	}

	@Override
	public String toString() {
		return serverID + ": " + lowerBound.toString() + " - " + greaterBound.toString();
	}

	public ServerConnectionInstance getServerConnectionInstance() {
		return sci;
	}

	public static int numberOfConnectedServers() {
		return allRemoteServerInfos.size();
	}

	public static RemoteServerInfo get(int i) {
		return allRemoteServerInfos.get(i);
	}

	private static BigInteger findMaxKeySpace() {
		return remoteServerInfoQueue.peek().getKeySpace();
	}

	BigInteger getKeySpace() {
		return greaterBound.subtract(lowerBound).abs();
	}

	public static int getSumRelativeWeight() {
		BigInteger maximum = findMaxKeySpace();
		int i=allRemoteServerInfos.size();
		for (RemoteServerInfo rsi:allRemoteServerInfos)
			if (rsi.getKeySpace().equals(maximum))
				i++;
		return i;
	}

	public static RemoteServerInfo getFromWeighted(int i) {
		BigInteger maximum = findMaxKeySpace();
		i++;
		for (RemoteServerInfo rsi:allRemoteServerInfos)
		{
			i--;
			if (rsi.getKeySpace().equals(maximum))
				i--;
			if (i<=0)
				return rsi;
		}
		return null;
	}

	private static RemoteServerInfo masterRemoteServerInfo = new RemoteServerInfo(0, new BigInteger("0"), new BigInteger("0"));
	public static RemoteServerInfo getFromID(int i) {
		if (i==0)
			return masterRemoteServerInfo;
		for (RemoteServerInfo rsi:allRemoteServerInfos)
			if (rsi.serverID == i)
				return rsi;
		return null;
	}
	
	public void introduceServerConnectionInstance(ServerConnectionInstance sci) throws SettingIsNotIntroductionException {
		if (this.sci != null) throw new SettingIsNotIntroductionException();
		this.sci = sci;
	}

	public static RemoteServerInfo getMyNext() {
		int myID = Server.getInstance().getID();
		myID++;
		RemoteServerInfo result = getFromID(myID);
		if (result == null)
			result = getFromID(1);
		return result;
	}
}
