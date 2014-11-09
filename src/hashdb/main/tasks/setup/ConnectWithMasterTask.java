package hashdb.main.tasks.setup;

import org.apache.log4j.Logger;
import hashdb.Settings;
import hashdb.communication.ServerConnectionInstance;
import hashdb.exceptions.*;
import hashdb.main.Server;
import hashdb.main.SlaveServer;
import hashdb.main.tasks.Task;
import hashdb.storage.protocol.external.RemoteServerInfo;

import java.io.IOException;
import java.net.Socket;



/**
 * The Class ConnectWithMasterTask.
 */
public class ConnectWithMasterTask
		implements Task {

	/**
	 * The log.
	 */
	private final Logger log = Logger.getLogger(ConnectWithMasterTask.class);

	/**
	 * The server.
	 */
	private final Server server;

	/**
	 * Instantiates a new connect with master task.
	 */
	public ConnectWithMasterTask() {
		this.server = SlaveServer.getInstance();
		if (!(server instanceof SlaveServer)) throw new NotSlaveServer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.main.tasks.Task#work()
	 */
	public void work() {
		this.log.info("Connecting with master server");
		ServerConnectionInstance sci;
		try {
			sci = new ServerConnectionInstance(new Socket(Settings.Server.Master.IP, Settings.Server.Master.PORT));
			if (Settings.Server.Slave.SETTINGS_LACKING) {
				this.log.error("Settings should be distributed");
				throw new RuntimeException("Not yet implemented"); // TODO
			}
		} catch (final IOException e) {
			this.log.error(e.getMessage());
			return;
		}
		try {
			RemoteServerInfo.getFromID(0).introduceServerConnectionInstance(sci);
		} catch (SettingIsNotIntroductionException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		sci.connectWithMaster();
	}
}
