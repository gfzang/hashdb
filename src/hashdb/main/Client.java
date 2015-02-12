package hashdb.main;

import hashdb.Settings;
import hashdb.Utilities;
import hashdb.communication.ConnectionInstance;
import hashdb.communication.protos.DataTransferRequest;
import hashdb.communication.protos.ClientToServer.ClientGoodbye;
import hashdb.communication.protos.ClientToServer.ClientGreeting;
import hashdb.communication.protos.ClientToServer.ConnectionKeepAlive;
import hashdb.communication.protos.ClientToServer.SlaveDistribute;
import hashdb.exceptions.CannotCastException;
import hashdb.exceptions.ConnectionActiveException;
import hashdb.exceptions.ConnectionNotActiveException;
import hashdb.exceptions.ServerCommunicationException;
import hashdb.exceptions.SomethingWentHorriblyWrong;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/22/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client {
	private final Logger log = Logger.getLogger(Client.class);
	private ConnectionInstance master;
	private ConnectionInstance slave;

	public boolean put(byte[] key, byte[] data, byte[] link) {
        if (connectionsInactive()) return false;
        try {
			slave.startUsing();
			slave.send(DataTransferRequest.getInstance().getCode());
			slave.send(Settings.CommunicationCodes.CLIENT);
			Utilities.receiveAck(slave);
			slave.send(Settings.CommunicationCodes.DataTransferCodes.PUT);
			slave.send(key);
			slave.send(Utilities.toByteArray(data.length));
			slave.send(data);
			slave.send(link);
			try {
				Utilities.receiveAck(slave);
				return true;
			} catch (ServerCommunicationException e) {
				return false;
			}
		} catch (ConnectionNotActiveException e) {
			log.error("Connection not active");
			return false;
		} catch (ServerCommunicationException e) {
			log.error("Server did not respond as it should have");
			return false;
		} finally {
			slave.stopUsing();
		}
	}

    public boolean check(byte[] key) {
        if (connectionsInactive()) return false;
        try {
            slave.startUsing();
            slave.send(DataTransferRequest.getInstance().getCode());
            slave.send(Settings.CommunicationCodes.CLIENT);
            Utilities.receiveAck(slave);
            slave.send(Settings.CommunicationCodes.DataTransferCodes.CHECK_KEY);
            slave.send(key);
            try {
                Utilities.receiveAck(slave);
                return true;
            } catch (ConnectionNotActiveException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                return false;
            } catch (ServerCommunicationException e) {
                return false;
            }
        } catch (ConnectionNotActiveException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        } catch (ServerCommunicationException e) {
            e.printStackTrace();
            log.error("Bad response");
            return false;
        } finally {
            slave.stopUsing();
        }
    }

    private boolean connectionsInactive() {
        if (master==null)
            if (!connect())
                return true;
        if (slave==null)
            if (!reRequestSlave())
                return true;
        return false;
    }

    public boolean deleteFirst(byte[] key) {
        if (connectionsInactive()) return false;
        try {
            slave.startUsing();

            slave.send(DataTransferRequest.getInstance().getCode());
            slave.send(Settings.CommunicationCodes.CLIENT);
            Utilities.receiveAck(slave);
            slave.send(Settings.CommunicationCodes.DataTransferCodes.DELETE_FIRST);
            slave.send(key);
            try {
                Utilities.receiveAck(slave);
                return true;
            } catch (ConnectionNotActiveException e) {
                log.error(e.getMessage());
                e.printStackTrace();
                return false;
            } catch (ServerCommunicationException e) {
                return false;
            }
        } catch (ConnectionNotActiveException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return  false;
        } catch (ServerCommunicationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        } finally {
            slave.stopUsing();
        }
    }

    public boolean getFirst(byte[] key, byte[] length, byte[] data, byte[] link) {
        if (connectionsInactive()) return false;
        try {
            slave.startUsing();
            slave.send(DataTransferRequest.getInstance().getCode());
            slave.send(Settings.CommunicationCodes.CLIENT);
            Utilities.receiveAck(slave);
            slave.send(Settings.CommunicationCodes.DataTransferCodes.GET_FIRST);
            slave.send(key);
            try {
                Utilities.receiveAck(slave);
            } catch (ConnectionNotActiveException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ServerCommunicationException e) {
                return false;
            }
            slave.receive(length);
            slave.receive(data);
            slave.receive(link);
            return true;
        } catch (ConnectionNotActiveException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        } catch (ServerCommunicationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        } finally {
            slave.stopUsing();
        }
    }

	public boolean connect() {

		while (master== null && masterRetry()) {
			try {
				master = new ConnectionInstance(new Socket(Settings.Server.Master.IP,Settings.Server.Master.PORT));
			} catch (IOException e) {
				log.error("Cannot connect to master! "+e.getMessage());
				master = null;
				continue;
			}
			master.startUsing();
			try {
				master.send(ClientGreeting.getInstance().getCode());
				Utilities.receiveAck(master);
			} catch (ConnectionNotActiveException e) {
				throw new SomethingWentHorriblyWrong();
			} catch (ServerCommunicationException e) {
				log.error("Did not receive appropriate response from master");
				master=null;
			}

		}
		if (master==null) return false;
		masterConnected();
		return connectWithSlave();
	}

	private boolean connectWithSlave() {
		byte ip_raw[] = {0,0,0,0};
		byte port_raw[] = Utilities.toByteArray(0);
		while (slave==null && slaveRetry()) {
			try {
				master.send(SlaveDistribute.getInstance().getCode());
				Utilities.receiveAck(master);
				master.receive(ip_raw);
				master.receive(port_raw);
				Utilities.sendAck(master);
			} catch (ConnectionNotActiveException e) {
				throw new SomethingWentHorriblyWrong();
			} catch (ServerCommunicationException e) {
				log.error("Something went wrong while communicating with master: "+e.getMessage());
				continue;
			}
			try {
				slave=new ConnectionInstance(new Socket(InetAddress.getByAddress(ip_raw),Utilities.byteArrayToInt(port_raw)));
				slave.startUsing();
				slave.send(ClientGreeting.getInstance().getCode());
				Utilities.receiveAck(slave);
			} catch (IOException e) {
				log.error("Cannot connect slave");
				slave=null;
				continue;
			} catch (CannotCastException e) {
				throw new SomethingWentHorriblyWrong();
			} catch (ConnectionNotActiveException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} catch (ServerCommunicationException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
		if (slave==null) return false;
		slaveConnected();
		return true;
	}

	public boolean reRequestSlave() {
		if (master== null) {
			return connect();
		}
        return slaveDisconnect() && connectWithSlave();
    }

	boolean slaveDisconnect() {
		if (slave!=null) {
			do {
				boolean success = disconnect(slave);
				if (success)
					slaveDisconnected();
			}while (slave!=null && slaveRetry());
		}
		return slave==null;
	}

	private void slaveDisconnected() {
		log.info("Slave is now disconnected");
		slave=null;
	}

	public boolean disconnect() {
        return slaveDisconnect() && masterDisconnect();
    }

	public boolean reconnect() {
        return disconnect() && connect();
    }

	boolean masterDisconnect() {
		if (master!=null) {
			do {
				boolean success = disconnect(master);
				if (success)
					masterDisconnected();
			}while (master!=null && slaveRetry());
		}
		return master==null;
	}

	private void masterDisconnected() {
		log.info("Master is now disconnected");
		master=null;
	}

	private boolean disconnect(ConnectionInstance instance) {
		instance.startUsing();
		try {
			instance.send(ClientGoodbye.getInstance().getCode());
			Utilities.receiveAck(instance);
			instance.stopUsing();
			instance.terminate();
			return true;
		} catch (ConnectionNotActiveException e) {
			log.error("Connection not active");
			throw new SomethingWentHorriblyWrong();
		} catch (ServerCommunicationException e) {
			log.error("Server did not respond as expected");
		} catch (ConnectionActiveException e) {
			log.error("Connection active");
			throw new SomethingWentHorriblyWrong();
		}
		return false;

	}

	private void slaveConnected() {
		log.info("Now connected to slave");
	}

	private boolean slaveRetry() {
		return true;
	}

	private void masterConnected() {
		log.info("Now connected to master!");
	}

	private boolean masterRetry() {
		return true;
	}

	public void keepAlive() {
		try {
			master.send(ConnectionKeepAlive.getInstance().getCode());
		} catch (ConnectionNotActiveException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		try {
			slave.startUsing();
			slave.send(ConnectionKeepAlive.getInstance().getCode());
		} catch (ConnectionNotActiveException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} finally {
			slave.stopUsing();
		}
	}
}
