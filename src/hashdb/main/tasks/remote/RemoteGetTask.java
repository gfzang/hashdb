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
 * Date: 6/25/13
 * Time: 7:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoteGetTask implements Task {

    private final ConnectionInstance ci;
    private final short replyTo;
    private final short localID;
    private final byte[] key;

    public RemoteGetTask(ConnectionInstance ci, short replyTo, short localID, byte[] key) {
        this.ci = ci;
        this.replyTo = replyTo;
        this.localID = localID;
        this.key = key;
    }

    public void work() {
        synchronized (ci) {
            if (ci.isBeingUsed()) {
                WorkerThread.addTask(this);
                return;
            }
            ci.startUsing();
        }
        try {
            ci.send(DataTransferRequest.getInstance().getCode());
            ci.send(replyTo);
            ci.send(localID);
            ci.send(Settings.CommunicationCodes.DataTransferCodes.GET_FIRST);
            ci.send(key);
        } catch (ConnectionNotActiveException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            ci.stopUsing();
        }
    }
}

