package hashdb.main;

import org.apache.log4j.Logger;
import hashdb.Settings;
import hashdb.exceptions.MultipleSingletonInstance;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.main.tasks.setup.ConnectWithMasterTask;
import hashdb.main.threads.DiskManager;
import hashdb.main.threads.WorkerThread;

import java.util.Date;


/**
 * The Class MasterServer.
 */
public class SlaveServer
		extends Server {

	/**
	 * The log.
	 */
	private static final Logger log = Logger.getLogger(SlaveServer.class);

	/**
	 * The worker.
	 */
	static WorkerThread worker[] = null;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String args[]) {
		new SlaveServer(args);
	}

	/**
	 * Instantiates a new slave server.
	 *
	 * @param args the args
	 */
	private SlaveServer(final String args[]) {
		super(args, Settings.Server.Slave.PORT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.main.Server#getName()
	 */
	@Override
	protected String getName() {
		return "Slave";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.main.Server#getNumberOfWorkers()
	 */
	@Override
	protected int getNumberOfWorkers() {
		return Settings.Server.Slave.WORKERS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hashdb.main.Server#serverSpecificInitialisation(java.lang.String
	 * [])
	 */
	@Override
	protected void serverSpecificInitialisation(final String[] args) {
		try {
			WorkerThread.addTask(new ConnectWithMasterTask());
			DiskManager.startDiskManager(this.getName()+new Date().getTime() % 100 + "_storage.hex", Settings.Server.Slave.ENTRIES_PER_SERVER);
		} catch (MultipleSingletonInstance multipleSingletonInstance) {
			log.error(multipleSingletonInstance.getMessage());
			throw new SomethingWentHorriblyWrong();
		}
	}
}
