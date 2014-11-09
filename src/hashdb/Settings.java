package hashdb;



/**
 * The Class Settings.
 * <p/>
 * Used for basic settings of system. Constants should be kept at minimum.
 *
 * @author filip
 */
public final class Settings {

	/**
	 * The Class CommunicationCodes.
	 */
	public static final class CommunicationCodes {

		/**
		 * The Constant ACK.
		 */
		public static final short ACK = (byte) 0xFFFF;
		public static final short KEEPALIVE = (short) 0xFFFE;
		public static final short CLIENT = (short) -1;
		public static final short CALLBACK = (short) 0xFFFD;
		public static final short NACK = 0x0000;

        public static final class DataTransferCodes {
            public static final short PUT = 0;
            public static final short GET_FIRST = 1;
            public static final short DELETE_FIRST = 2;
            public static final short CHECK_KEY = 3;
        }
    }

    public static final class Fields{
        /**
         * One of the constant field that exists in entries.
         */
        public static final Field KEY = new Field(0, Settings.KEY_SIZE);
        /**
         * One of the constant field that exists in entries.
         */
        public static final Field STATUS = new Field(KEY.getEnd(), Settings.STATUS_SIZE);
        /**
         * One of the constant field that exists in entries.
         */
        public static final Field LENGTH = new Field(STATUS.getEnd(),
                                                     (int) Math.ceil(Math.log(Settings.DATA_SIZE) / (Math.log(2) * 8)));
        /**
         * One of the constant field that exists in entries.
         */
        public static final Field DATA = new Field(LENGTH.getEnd(), Settings.DATA_SIZE);
        /**
         * One of the constant field that exists in entries.
         */
        public static final Field LINK = new Field(DATA.getEnd(), Settings.KEY_SIZE);
        /**
         * One special field used to determine size of entry.
         */
        public static final Field END = new Field(LINK.getEnd(), 0);
    }

	/**
	 * The Class Field.
	 */
	public static final class Field {

		/**
		 * The offset in bytes from beginning of entry.
		 */
		private final int offset;

		/**
		 * The size in bytes of field in bytes.
		 */
		private final int size;

		/**
		 * Instantiates a new field.
		 *
		 * @param offset the offset
		 * @param size   the size
		 */
		public Field(final int offset, final int size) {
			this.offset = offset;
			this.size = size;
		}

		/**
		 * Gets the end.
		 *
		 * @return the end
		 */
		public final int getEnd() {
			return this.size + this.offset;
		}

		/**
		 * Gets the offset.
		 *
		 * @return the offset
		 */
		public final int getOffset() {
			return this.offset;
		}

		/**
		 * Gets the size.
		 *
		 * @return the size
		 */
		public final int getSize() {
			return this.size;
		}
	}

	/**
	 * The Class Server.
	 */
	public static final class Server {

		/**
		 * The Class Master.
		 */
		public static final class Master {
			/**
			 * The Constant PORT.
			 */
			public static final int PORT = 9999;

			/**
			 * The Constant WORKERS.
			 */
			public static final int WORKERS = 1;

			/**
			 * The Constant IP.
			 */
			public static final String IP = "localhost";
		}

		/**
		 * The Class Master.
		 */
		public static final class Slave {
			/**
			 * The Constant PORT.
			 */
			public static final int PORT = 10000;

			/**
			 * The Constant WORKERS.
			 */
			public static final int WORKERS = 1;

			/**
			 * The Constant SETTINGS_LACKING.
			 */
			public static final boolean SETTINGS_LACKING = false;
			public static final int ENTRIES_PER_SERVER = 19;
		}

		/**
		 * The Constant IP_SIZE.
		 */
		public static final short IP_SIZE = 4; // in bytes; IPv4

		/**
		 * The Constant INACTIVE_THRESHOLD.
		 */
		public static final long INACTIVE_THRESHOLD = 10*60*60* 1000;

		/**
		 * The Constant RECEPTIONIST_RETRY.
		 */
		public static final boolean RECEPTIONIST_RETRY = true;

		/**
		 * The worker names.
		 */
		public static final String[] workerNames = {"Andrija", "Branislav", "Cvetko", "Časlav", "Ćirilo", "Dejan", "Džordž", "Đorđe", "Emir", "Filip", "Goran", "Hristivoje", "Ilija", "Jovan", "Kosta", "Lazar", "Ljubivoje", "Milan", "Novak", "Njegomir", "Ostoja", "Petar", "Radovan", "Stevan", "Špira", "Trifun", "Uros", "Veroljub", "Zoran", "Živorad"};

	}

	/**
	 * The Constant EMPTY.
	 */
	public static final byte[] EMPTY = {0x01};

	/**
	 * Size of data part in bytes.
	 */
	private static final int DATA_SIZE = 10*1024*1024; //10 MB

	/**
	 * Number of status bytes.
	 */
	private static final int STATUS_SIZE = 1;

	/**
	 * The key size.
	 */
	private static final int KEY_SIZE = 4;

    /**
	 * The Constant JUMPED.
	 */
	public static final byte[] JUMPED = {0x02};
}
