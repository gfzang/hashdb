package hashdb.main;

import hashdb.Settings;



/**
 * The Class MasterServer.
 */
public class MasterServer
		extends Server {
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String args[]) {
		new MasterServer(args);
	}

	/**
	 * Instantiates a new master server.
	 *
	 * @param args the args
	 */
	private MasterServer(final String args[]) {
		super(args, Settings.Server.Master.PORT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.main.Server#getName()
	 */
	@Override
	protected String getName() {
		return "Master";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hashdb.main.Server#getNumberOfWorkers()
	 */
	@Override
	protected int getNumberOfWorkers() {
		return Settings.Server.Master.WORKERS;
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
		setID(0);
	}
}
