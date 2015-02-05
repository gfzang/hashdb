package hashdb.main;

import org.apache.log4j.Logger;
import hashdb.Settings;
import hashdb.communication.protos.BaseProto;
import hashdb.exceptions.SomethingWentHorriblyWrong;
import hashdb.main.threads.ConnectionManager;
import hashdb.main.threads.Receptionist;
import hashdb.main.threads.WorkerThread;
import hashdb.storage.protocol.external.RemoteServerInfo;

import java.util.Date;



/**
 * The Class MasterServer.
 */
public abstract class Server {

	private static Server instance = null;

	public static Server getInstance() { return instance;}

	/**
	 * The log.
	 */
	private static final Logger log = Logger.getLogger(Server.class);

	/**
	 * The worker.
	 */
	private static WorkerThread[] worker = null;

	/**
	 * The listening on port.
	 */
	private int listeningOnPort;

	/**
	 * The r.
	 */
	private Receptionist r;

	/**
	 * The id.
	 */
	private int id;

	/**
	 * Instantiates a new server.
	 *
	 * @param args the args
	 * @param port the port
	 */
    Server(final String args[], final int port) {
		if (instance != null) throw new SomethingWentHorriblyWrong();
		instance = this;
		try {
			final String name = this.getName();
			Server.log.info(name + " started! Point zero:" + new Date());

			Server.log.info("Protos should now be initialised!");

			this.initialiseWorkerThreads(args);
			Server.log.info("Worker threads initialised");

			this.initialiseDedicatedThreads(args, port);
			Server.log.info("Dedicated threads initialised");

			this.serverSpecificInitialisation(args);

			this.startWorkers(args);
			Server.log.info(name + " operational");

			RemoteServerInfo.getBase(Settings.Fields.KEY.getSize());

		} finally {
			Server.log.info("Initialisation complete");
		}
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	protected abstract String getName();

	/**
	 * Gets the number of workers.
	 *
	 * @return the number of workers
	 */
	protected abstract int getNumberOfWorkers();

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return this.r.getPort();
	}

	/**
	 * Initialise dedicated threads.
	 *
	 * @param args the args
	 * @param port the port
	 */
	void initialiseDedicatedThreads(final String[] args, int port) {
		boolean receptionistCreated;
		do try {
			this.r = new Receptionist(port);
			this.r.start();
			Server.log.info("Receptionist started");
			receptionistCreated = true;
			this.listeningOnPort = port;
			Server.log.info("Listening on port: " + this.listeningOnPort);
		} catch (final Exception e) {
			Server.log.error("Cannot listen on port: " + port);
			receptionistCreated = false;
			port++;
		} while (!receptionistCreated && Settings.Server.RECEPTIONIST_RETRY);
		new ConnectionManager(this).start();
		Server.log.info("ConnectionManager started");
	}

	/**
	 * Initialise worker threads.
	 *
	 * @param args the args
	 */
	void initialiseWorkerThreads(final String[] args) {
		final int workers = this.getNumberOfWorkers();
		Server.worker = new WorkerThread[workers];
		Server.log.info("Array for workers created");
		for (int i = 0; i < workers; i++)
			Server.worker[i] = new WorkerThread(Settings.Server.workerNames[i % Settings.Server.workerNames.length]);
		Server.log.info("All workers created");
	}

	/**
	 * Server specific initialisation.
	 *
	 * @param args the args
	 */
	protected abstract void serverSpecificInitialisation(String[] args);

	/**
	 * Sets the id.
	 *
	 * @param i the new id
	 */
	public void setID(final int i) {
		this.id = i;
	}

	/**
	 * Start workings.
	 *
	 * @param args the args
	 */
	void startWorkers(final String[] args) {
		for (final WorkerThread w : Server.worker)
			w.start();
	}
}
