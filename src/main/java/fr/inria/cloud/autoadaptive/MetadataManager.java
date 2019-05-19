package org.scenario.autoadaptive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.FileAttribute;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.scenario.cloudsimplus.resources.AdaptedMetadata;
import org.scenario.cloudsimplus.resources.AdaptedSan;


public class MetadataManager {

	private static final MetadataInterface instance = MetadataCatalog.getCatalogInstance();	

	public static MetadataInterface getCatalogInstance(){
		return instance;
	}
	
	/**
	 * A newly created file will be added as a new entry 
	 * in the catalog, and if it's a replica it will be 
	 * in the corresponding existing entry's linked list
	 * 
	 * @param attr file attributes
	 */
	public static void onFileCreate(FileAttribute attr) {
	
		MetadataInterface catalog = instance;
		//TODO change Integer to long in the hashmap
		if(! catalog.hasEntry((int) attr.getRegistrationID())){
			catalog.registerNewFile(attr);		
		}
		else {
			catalog.registerReplica(attr);
		}
	}

	/**
	 * Gets the metadata for a given file
	 * We make the assumption that we will never make replicas on the same device
	 * @param id
	 * @param containingSan
	 * @param plusOneAccess getting metadata means file has been accessed but 
	 * when the access is for system internal use number of accesses should not change
	 * @return
	 */
	public FileAttribute getFileMetadataWithId(int id, FileStorage containingSan ,boolean plusOneAccess) {
		FileAttribute metadata = null;
		if(containingSan != null) {
			metadata = ((HashMap<Integer, LinkedList<FileAttribute>>) instance).get(id).stream().filter(m -> ((AdaptedMetadata) m).getContainingDevice().getName().equals(((AdaptedSan)containingSan).getName())).findFirst().get();
		}
		else {
			//Here we just want metadata for a file to retreive some info that is the
			// same across all copies of it
			metadata = ((HashMap<Integer, LinkedList<FileAttribute>>) instance).get(id).get(0);			
		}
		if(plusOneAccess) {
			((AdaptedMetadata) metadata).incrementNoOfAccesses();			
		}
		return metadata;
	}
	
	/**
	 * Get total access to master copy and all replicas of that file
	 * @param id Registration id of the file, the one that is shared among
	 *  master copy and all replicas of that file
	 * @return total Access to the file on all sites
 	 */
	public int getTotalAccessCountForGivenFile(int id) {
		int temp = 0;
		for( FileAttribute attr : ((HashMap<Integer, LinkedList<FileAttribute>>) instance).get(id)) {
			temp += ((AdaptedMetadata) attr).getNoOfAccesses();
		}
		return temp;
		
	}
	
	public List<Host> getNodesThatHasFile(int requestedFileId) {
		List<Host> listToReturn = new ArrayList<Host>();
		if(!((HashMap<Integer, LinkedList<FileAttribute>>) instance).containsKey(requestedFileId))
			return listToReturn;
		for (FileAttribute fileMetaData : ((HashMap<Integer, LinkedList<FileAttribute>>) instance).get(requestedFileId)) {
			listToReturn.addAll(((AdaptedSan)((AdaptedMetadata)fileMetaData).getContainingDevice()).getAccessingHosts());
		}
		return listToReturn;
	}
	

}
