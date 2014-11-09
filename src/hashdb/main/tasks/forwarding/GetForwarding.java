package hashdb.main.tasks.forwarding;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.ServerCommunicationException;
import hashdb.main.threads.WorkerThread;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/25/13
 * Time: 7:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetForwarding extends ForwardingTask {
    public GetForwarding(ConnectionInstance ci) {
        super(ci);
    }

    public void work() {
        synchronized (ci) {
            if (ci.isBeingUsed()) {
                WorkerThread.addTask(this);
                return;
            }
            ci.startUsing();
            incoming.startUsing();
        }
        try {
            byte[] len = new byte[Settings.Fields.LENGTH.getSize()];
            byte[] data = new byte[Settings.Fields.DATA.getSize()];
            byte[] link = new byte[Settings.Fields.LINK.getSize()];
            Utilities.receiveAck(incoming);
            Utilities.sendAck(ci);
            incoming.receive(len);
            ci.send(len);
            incoming.receive(data);
            ci.send(data);
            incoming.receive(link);
            ci.send(link);
        } catch (ConnectionNotActiveException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ServerCommunicationException e) {
            try {
                ci.send(Settings.CommunicationCodes.NACK);
            } catch (ConnectionNotActiveException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } finally
        {
            //incoming.stopUsing();
            ci.stopUsing();
        }
    }
}
