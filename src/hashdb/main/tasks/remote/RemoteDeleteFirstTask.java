package hashdb.main.tasks.remote;

import hashdb.Settings;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.DataTransferRequest;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.main.tasks.Task;
import hashdb.main.threads.WorkerThread;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/26/13
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoteDeleteFirstTask implements Task {
    private final ConnectionInstance outgoing;
    private final short replyTo;
    private final short localID;
    private final byte[] key;
    public RemoteDeleteFirstTask(ConnectionInstance outgoing, short replyTo, short localID, byte[] key) {
        this.outgoing = outgoing;
        this.replyTo = replyTo;
        this.localID = localID;
        this.key = key;
    }

    public void work() {
        synchronized (outgoing) {
            if (outgoing.isBeingUsed()) {
                WorkerThread.addTask(this);
                return;
            }
            outgoing.startUsing();
        }
        try {
            outgoing.send(DataTransferRequest.getInstance().getCode());
            outgoing.send(replyTo);
            outgoing.send(localID);
            outgoing.send(Settings.CommunicationCodes.DataTransferCodes.DELETE_FIRST);
            outgoing.send(key);
        } catch (ConnectionNotActiveException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            outgoing.stopUsing();
        }
    }
}
