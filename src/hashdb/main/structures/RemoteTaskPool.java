package hashdb.main.structures;

import hashdb.main.tasks.forwarding.ForwardingTask;

import java.util.HashMap;
import java.util.PriorityQueue;

public class RemoteTaskPool {
	private static short firstUnusedTaskId = Short.MIN_VALUE;
	private static final PriorityQueue<Short> free = new PriorityQueue<Short>();
	private static final HashMap<Short, ForwardingTask> remoteTasks = new HashMap<Short, ForwardingTask>();
	public static synchronized short addTask(ForwardingTask t){
		short value;
		if (free.size()>0) {
			value = free.poll();
		} else {
			value = firstUnusedTaskId++;
		}
		remoteTasks.put(value, t);
		return value;
	}
	public static synchronized ForwardingTask fetchTask(short id) {
		ForwardingTask result = remoteTasks.remove(id);
		free.offer(id);
		while (free.peek()==firstUnusedTaskId-1) {
			firstUnusedTaskId = free.poll();
			if (free.peek()==null)
				break;
		}
		return result;
	}
}
