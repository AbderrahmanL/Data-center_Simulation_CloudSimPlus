package org.scenario.cloudsimplus.resources;

import org.cloudbus.cloudsim.resources.File;

public class AdaptedFile extends File{

	
	public AdaptedFile(File file) throws IllegalArgumentException {
		super(file);
	}
	
	public AdaptedFile(String fileName, int fileSize) {
		super(fileName, fileSize);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void createAttribute(final int fileSize) {
        this.setAttribute(new AdaptedMetadata(this, fileSize));
    }
	
	@Override
	public File makeReplica() {
		final AdaptedFile file = new AdaptedFile(this.getName(), this.getSize());

        this.getAttribute().copyValue(file.getAttribute());
        file.getAttribute().setMasterCopy(false);   // set this file as a replica

        return file;
    }
	

}
