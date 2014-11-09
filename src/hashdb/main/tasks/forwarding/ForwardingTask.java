package hashdb.main.tasks.forwarding;

import hashdb.communication.ConnectionInstance;
import hashdb.main.tasks.Task;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/22/13
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ForwardingTask implements Task {
	final ConnectionInstance ci;
	ConnectionInstance incoming;
	ForwardingTask(ConnectionInstance ci) {this.ci=ci;}
	public void setIncoming(ConnectionInstance incoming) {this.incoming=incoming;}
	public ConnectionInstance getCi() { return ci;}


}
