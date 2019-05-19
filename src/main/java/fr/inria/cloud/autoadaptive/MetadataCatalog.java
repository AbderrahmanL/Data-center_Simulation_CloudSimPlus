package org.scenario.autoadaptive;

import java.util.HashMap;
import java.util.LinkedList;

import org.cloudbus.cloudsim.resources.FileAttribute;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.scenario.cloudsimplus.resources.AdaptedMetadata;


public class MetadataCatalog extends HashMap<Integer,LinkedList<FileAttribute>> implements MetadataInterface {

	/**
	 * An implementation {@link #MetadataInterface } that is a hashMap
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	public static MetadataInterface getCatalogInstance(){
		return new MetadataCatalog();
	}

	private MetadataCatalog(){
		super();
	}
	
	public boolean hasEntry(int registrationId) {
		return this.containsKey(registrationId);
	}
	
	public void registerNewFile(FileAttribute attr) {
		LinkedList<FileAttribute> holder = new LinkedList<>();
		holder.add(attr);
		//TODO change Integer to long in the hashmap
		this.put((int) attr.getRegistrationID(), holder);
	}

	public void registerReplica(FileAttribute attr) {
		this.get(attr.getRegistrationID()).add(attr);
	}
	
	@Override
	public void removeReplica(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMetadataAfterMove(FileAttribute attr, FileStorage newContainingDevice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasEntry(String name) {
		 for(int i = 0 ; i  < this.size() ; i++){
			 //TODO Here get 0 since all metadata in linked list of index i
			 //carry to copies of the same file
			 if(((AdaptedMetadata) this.get(i).get(0)).getName().equals(name)) {
				 return true;
			 }
		 }
		 return false;
	}


}
