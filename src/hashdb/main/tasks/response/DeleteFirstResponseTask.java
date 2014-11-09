package hashdb.main.tasks.response;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.ServerToServer.JobResponse;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.NoSuchServerException;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.main.Server;
import hashdb.main.structures.RemoteTaskPool;
import hashdb.main.tasks.forwarding.AckForwarding;
import hashdb.main.tasks.forwarding.ForwardingTask;
import hashdb.main.tasks.remote.RemoteDeleteFirstTask;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.protocol.external.RemoteServerInfo;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 5/15/13
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteFirstResponseTask
		extends BooleanResponseTask {

    private final boolean client;
    private final byte[] key;
    private final short replyTo;
    private ConnectionInstance ci;
    private short internalID;
    private boolean myClientRemoteTask=false;

    public DeleteFirstResponseTask(ConnectionInstance ci, short replyTo, short remoteID, byte[] key) {
        super();
        client = replyTo == Settings.CommunicationCodes.CLIENT;
        this.key=key;
        if (client) {
            this.replyTo = (short) Server.getInstance().getID();
            this.ci = ci;
        } else {
            this.replyTo = replyTo;
            if (replyTo != Server.getInstance().getID())
                this.ci = RemoteServerInfo.getFromID(replyTo).getServerConnectionInstance().getOutgoing();
            else {
                this.ci=null;
            }
            this.internalID = remoteID;
        }
    }

    public void work() {
        log.info("Delete First Response Task started");
        if (ci==null) {
            ForwardingTask ft = RemoteTaskPool.fetchTask(internalID);
            ci = ft.getCi();
            log.info("CI was null but now is set!");
            myClientRemoteTask = true;
        }
        if (status) {
            synchronized (ci) {
                if (ci.isBeingUsed()) {
                    log.info("CI is being used, will retry in a moment");
                    WorkerThread.addTask(this);
                    return;
                }
                ci.startUsing();
            }
            sucessfullReply(myClientRemoteTask);
            log.info("Everything is ok");
            return;
        } else {
            RemoteServerInfo nextInfo = RemoteServerInfo.getMyNext();
            if (nextInfo.getServerID() == Server.getInstance().getID()) {
                iAmOnlyOneAndICannot();
                return;
            }
            try {
                if (!client && nextInfo.getServerID() == RemoteServerInfo.findID(key).getServerID()) {
                    everyoneWasVisitedAndNobodyCan(myClientRemoteTask);
                    return;
                }
            } catch (NoSuchServerException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if (client || myClientRemoteTask) {
                internalID = RemoteTaskPool.addTask(new AckForwarding(ci));
            }
            WorkerThread.addTask(new RemoteDeleteFirstTask(RemoteServerInfo.getMyNext().getServerConnectionInstance().getOutgoing(),replyTo,internalID,key));
        }
	}

    private void sucessfullReply(boolean myClientRemote) {
        log.info("Sucessfull reply started");
        try {
            if (!client && !myClientRemote) {
                ci.send(JobResponse.getInstance().getCode());
                ci.send(internalID);
            }
            Utilities.sendAck(ci);
        } catch (ConnectionNotActiveException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            ci.stopUsing();
            log.info("Sucessfull reply finished");
        }
    }

    private void everyoneWasVisitedAndNobodyCan(boolean myClientRemoteTask) {
        synchronized (ci) {
            if (ci.isBeingUsed()) {
                WorkerThread.addTask(this);
                return;
            }
            ci.startUsing();
        }
        try {
            if (!myClientRemoteTask) {
                ci.send(JobResponse.getInstance().getCode());
                ci.send(internalID);
            }
            ci.send(Settings.CommunicationCodes.NACK);
            return;
        } catch (ConnectionNotActiveException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            ci.stopUsing();
        }
        return;
    }

    private void iAmOnlyOneAndICannot() {
        ci.startUsing();
        try {
            ci.send(Settings.CommunicationCodes.NACK);
        } catch (ConnectionNotActiveException e) {
            throw new SomethingWentHorriblyWrong();
        } finally {
            ci.stopUsing();		}
    }
}
