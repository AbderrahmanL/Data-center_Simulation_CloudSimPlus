package org.scenario.cloudsimplus;

import java.util.List;
import java.util.Objects;

import org.cloudbus.cloudsim.resources.DatacenterStorage;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.util.DataCloudTags;
import org.scenario.autoadaptive.MetadataManager;
import org.scenario.cloudsimplus.resources.AdaptedMetadata;

public class AdaptedDatacenterStorage extends DatacenterStorage {
	
	private MetadataManager metadataManager;
	
	public AdaptedDatacenterStorage(final List<FileStorage> storageList){
    super(storageList);
    setMetadataManager(new MetadataManager());
    }
	
	@Override
	public int addFile(final File file) {
		 Objects.requireNonNull(file);

	        if (contains(file.getName())) {
	        	return DataCloudTags.FILE_ADD_ERROR_EXIST_READ_ONLY;
	        }

	        // check storage space first
	        if (getStorageList().isEmpty()) {
	        	return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
	        }

	        for (final FileStorage storage : getStorageList()) {
	            if (storage.isAmountAvailable((long) file.getSize())) {
	                storage.addFile(file);
	                ((AdaptedMetadata)file.getAttribute()).setContainingDevice(storage);
	                MetadataManager.onFileCreate(file.getAttribute());
	                return DataCloudTags.FILE_ADD_SUCCESSFUL;
	            }
	        }
	        return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
	}
	
	public File getFile(String name){
		File file = new File("a", 1);
		for(FileStorage storage : this.getStorageList()){
			if(storage.getFileNameList().contains(name)){
				file = storage.getFile(name);
			}
		}
		return file;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

}
