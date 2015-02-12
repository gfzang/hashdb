package hashdb.communication.protos;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.main.Server;
import hashdb.main.structures.RemoteTaskPool;
import hashdb.main.tasks.disk.CheckIfExists;
import hashdb.main.tasks.disk.DeleteFirstIfExists;
import hashdb.main.tasks.disk.GetFirstIfExist;
import hashdb.main.tasks.disk.PutIfPossible;
import hashdb.main.tasks.forwarding.AckForwarding;
import hashdb.main.tasks.forwarding.GetForwarding;
import hashdb.main.tasks.remote.RemoteCheckIfExists;
import hashdb.main.tasks.remote.RemoteDeleteFirstTask;
import hashdb.main.tasks.remote.RemoteGetTask;
import hashdb.main.tasks.remote.RemotePutTask;
import hashdb.main.tasks.response.DeleteFirstResponseTask;
import hashdb.main.tasks.response.ExistsResponseTask;
import hashdb.main.tasks.response.GetFirstResponseTask;
import hashdb.main.tasks.response.PutResponseTask;
import hashdb.main.threads.DiskManager;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.protocol.external.RemoteServerInfo;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/22/13
 * Time: 7:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataTransferRequest extends BaseProto{

	private static final DataTransferRequest instance = new DataTransferRequest();

    public static DataTransferRequest getInstance() {
		return instance;
	}

	@Override
	public short getCode() {
		return 7;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getName() {
		return "Data transfer request";  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void invoke(short[] _data, ConnectionInstance ci, Server server) {
		try {
            ci.startUsing();
			sanityCheck(_data,ci);
			short[] identifier = new short[1];
			short[] remoteID = new short[1];
			ci.receive(identifier);
			boolean client;
			client = identifier[0]== Settings.CommunicationCodes.CLIENT;
			if (client){
				Utilities.sendAck(ci);
			} else {
				ci.receive(remoteID);
			}

			short[] typeOfTransfer = new short[1];
			ci.receive(typeOfTransfer);
			switch (typeOfTransfer[0]) {
				case Settings.CommunicationCodes.DataTransferCodes.PUT: {
                    log.info("It's put");
					byte[] key = new byte[Settings.Fields.KEY.getSize()];
					byte[] dataLength = Utilities.toByteArray(0);
					byte[] link = new byte[Settings.Fields.LINK.getSize()];

					ci.receive(key);
					ci.receive(dataLength);
					byte[] data= new byte[Utilities.byteArrayToInt(dataLength)];
					ci.receive(data);
					ci.receive(link);

					RemoteServerInfo rs = RemoteServerInfo.findID(key);
					if (rs.getServerID()==Server.getInstance().getID() || !client) {
                        PutResponseTask prt = new PutResponseTask(ci,identifier[0],remoteID[0],key,data,link);
						DiskManager.addDiskTask(new PutIfPossible(key,data,link,prt));
					} else {
						short internalID = RemoteTaskPool.addTask(new AckForwarding(ci));
						WorkerThread.addTask(new RemotePutTask(rs.getServerConnectionInstance().getOutgoing(),(short)Server.getInstance().getID(),internalID,key,data,link));
					}
					break;
                }
                case Settings.CommunicationCodes.DataTransferCodes.GET_FIRST:
                {
                    log.info("It's get");
                    byte[] key = new byte[Settings.Fields.KEY.getSize()];

                    ci.receive(key);

                    RemoteServerInfo rs = RemoteServerInfo.findID(key);
                    if (rs.getServerID()==Server.getInstance().getID() || !client) {
                        GetFirstResponseTask gfrt = new GetFirstResponseTask(ci,identifier[0],remoteID[0],key);
                        DiskManager.addDiskTask(new GetFirstIfExist(key, gfrt));
                    } else {
                        short internalID = RemoteTaskPool.addTask(new GetForwarding(ci));
                        WorkerThread.addTask(new RemoteGetTask(rs.getServerConnectionInstance().getOutgoing(), (short)Server.getInstance().getID(), internalID, key));
                    }
                    break;
                }
                case Settings.CommunicationCodes.DataTransferCodes.DELETE_FIRST :
                {
                    log.info("It's delete");

                    byte[] key = new byte[Settings.Fields.KEY.getSize()];

                    ci.receive(key);
                    RemoteServerInfo rs=RemoteServerInfo.findID(key);
                    if (rs.getServerID() == Server.getInstance().getID() || !client) {
                        DeleteFirstResponseTask dfrt = new DeleteFirstResponseTask(ci,identifier[0],remoteID[0],key);
                        DiskManager.addDiskTask(new DeleteFirstIfExists(key, dfrt));
                    } else {
                        short internalID = RemoteTaskPool.addTask(new AckForwarding(ci));
                        WorkerThread.addTask(new RemoteDeleteFirstTask(rs.getServerConnectionInstance().getOutgoing(), (short)Server.getInstance().getID(), internalID,key));
                    }
                    break;
                }
                case Settings.CommunicationCodes.DataTransferCodes.CHECK_KEY:
                {
                    log.info("It's check");
                    byte[] key = new byte[Settings.Fields.KEY.getSize()];

                    ci.receive(key);

                    RemoteServerInfo rs = RemoteServerInfo.findID(key);
                    if (rs.getServerID()==Server.getInstance().getID() || !client) {
                        ExistsResponseTask ert = new ExistsResponseTask(ci,identifier[0],remoteID[0],key);
                        DiskManager.addDiskTask(new CheckIfExists(key, ert));
                    } else {
                        short internalID = RemoteTaskPool.addTask(new AckForwarding(ci));
                        WorkerThread.addTask(new RemoteCheckIfExists(rs.getServerConnectionInstance().getOutgoing(), (short)Server.getInstance().getID(), internalID, key));
                    }

                    break;
                }

			}


		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

	}
}