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
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoteCheckIfExists implements Task {

    private final ConnectionInstance outgoing;
    private final short replyTo;
    private final short internalID;
    private final byte[] key;

    public RemoteCheckIfExists(ConnectionInstance outgoing, short id, short internalID, byte[] key) {
        this.outgoing = outgoing;
        this.replyTo = id;
        this.internalID = internalID;
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
            outgoing.send(internalID);
            outgoing.send(Settings.CommunicationCodes.DataTransferCodes.CHECK_KEY);
            outgoing.send(key);
        } catch (ConnectionNotActiveException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            outgoing.stopUsing();
        }
    }
}
