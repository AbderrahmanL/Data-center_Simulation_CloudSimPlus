package org.scenario.autoadaptive;

/**
 * Contains additional tags for DataCloud features, such as file
 * information retrieval, file transfers, and storage info.
 *
 * @author Abderrahman Lahiaouni
 */
public final class CloudDataTags {
	
	/**
     * Base value used for Delegate Broker tags.
     */
    public static final int DB_BASE = 400;
    
    /**
     * Base value used for Replica Manager tags.
     */
    public static final int RM_BASE = 500;

    /**
     * Base value for catalogue tags.
     */
    public static final int CTLG_BASE = 600;

    /**
     * Default Maximum Transmission Unit (MTU) of a link in bytes.
     */
    public static final int DEFAULT_MTU = 1500;
    
    /**
     * Default ICMP packet size in bytes.
     */
    public static final int ICMP = 1500;

    /**
     * The default packet size (in byte) for sending events to other entity.
     */
    public static final int PKT_SIZE = DEFAULT_MTU * 1000;  // in bytes

    /**
     * Denotes a broadcast message to select datacenter for cloudlet.
     */
    public static final int ELECTION_BROADCAST = DB_BASE + 1;
    
    /**
     * Denotes that file addition is successful.
     */
    public static final int FILE_ADD_SUCCESSFUL = RM_BASE + 20;

    /**
     * Denotes that file addition is failed because the storage is full.
     */
    public static final int FILE_ADD_ERROR_STORAGE_FULL = RM_BASE + 21;

    /**
     * Denotes that file addition is failed because the file already exists in
     * the catalogue and it is read-only file.
     */
    public static final int FILE_ADD_ERROR_EXIST_READ_ONLY = RM_BASE + 23;

    /**
     * Denotes that file deletion is successful.
     */
    public static final int FILE_DELETE_SUCCESSFUL = RM_BASE + 40;

    /**
     * Denotes that file deletion is failed due to an unknown error.
     */
    public static final int FILE_DELETE_ERROR = RM_BASE + 41;

    /**
     * Denotes the request to de-register / delete a master file from the
     * Replica Catalogue.
     * <p>
     * The format of this request is Object[2] = {String lfn, Integer
     * resourceID}.
     * </p>
     *
     * The reply tag name is {@link #CTLG_DELETE_MASTER_RESULT}.
     */
    public static final int CTLG_DELETE_MASTER = CTLG_BASE + 20;

    /**
     * Sends the result of de-registering a master file back to sender.
     * <p>
     * The format of the reply is Object[2] = {String lfn, Integer resultID}.
     * </p>NOTE: The result id is in the form of CTLG_DELETE_MASTER_XXXX where
     * XXXX means the error/success message
     */
    public static final int CTLG_DELETE_MASTER_RESULT = CTLG_BASE + 21;

    /**
     * A private constructor to avoid class instantiation.
     */
    private CloudDataTags(){}
}

