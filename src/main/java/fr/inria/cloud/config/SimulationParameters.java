package org.scenario.config;

public class SimulationParameters {
	
	/**
	 * Workload configuration
	 */
	public static final double DEPLOY_NEW_FILE = 5D;
	public static final int LOAD_HISTORY_UPDATE_INTERVAL = 65;
	public static final int SCALE_FACTOR = 1;
	public static final int NO_CLOUDLETS = 700 * SCALE_FACTOR;
	public static final int SINGLE_WORKER = 0;
	public static final double RANDOM_INTERVAL_RIGHT_LIMIT = 10D;
	public static final boolean RANDOMIZED = true;
	public static final boolean PERIODIC = false;
	/**
	 * Architecture configuration
	 */
    public static final int DC_SUPER = 1;
	public static final int DC_MID = 0;
	public static final int DC_STANDARD = 0;
	public static final int HOST_SUPER = 18;
	public static final int HOST_MID = 0;
	public static final int HOST_STANDARD = 0;
	public static final int STORAGE_NODES_SUPER = 1;
	public static final int HOSTS_PER_SWITCH = 3;

}