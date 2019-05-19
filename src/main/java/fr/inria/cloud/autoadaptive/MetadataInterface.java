package org.scenario.autoadaptive;

import org.cloudbus.cloudsim.resources.FileAttribute;
import org.cloudbus.cloudsim.resources.FileStorage;

/**
 * A replica catalog is holding a list of all replicas
 * and their locations.
 */

public interface MetadataInterface {


	public void registerNewFile(FileAttribute attr) ;
	
	public boolean hasEntry(int identifier) ;
	
	public boolean hasEntry(String name) ;
	
	public void registerReplica(FileAttribute attr);
	
	public void removeReplica(String path);
	
	public void updateMetadataAfterMove(FileAttribute attr , FileStorage newContainingDevice);

}
