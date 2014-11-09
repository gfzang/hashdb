package hashdb.main.structures.balancers;

import hashdb.storage.protocol.external.RemoteServerInfo;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/21/13
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreciseBalancer
		extends Balancer {

	private static final PriorityQueue<Elem> preciseQueue = new PriorityQueue<Elem>(0,new Comparator<Elem> (){
		public int compare(Elem elem, Elem elem2) {
			return new Integer(elem.getValue()).compareTo(elem2.getValue());
		}
	});

	@Override
	public RemoteServerInfo getNext() {
		Elem e = preciseQueue.peek();
		e.addNewConnection();
		return RemoteServerInfo.get(e.getID());
	}

	private static class Elem {

		private final int id;
        private int num;

		public Elem(int id, int num) {
			this.id = id;
			this.num=num;
		}

		public int getValue() { return num;}
		public void setValue(int a) { num = a;}

		public int getID() { return id;}

		public void addNewConnection() { num++;}
	}

	public static void update(short i, int numOfConnection) {
		for(Elem e:preciseQueue)
			if (e.getID()==i)
			{
				e.setValue(numOfConnection);
				return;
			}
		preciseQueue.add(new Elem(i,numOfConnection));
	}
}
