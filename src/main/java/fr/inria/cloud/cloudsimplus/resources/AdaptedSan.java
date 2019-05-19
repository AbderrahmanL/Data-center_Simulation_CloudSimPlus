package org.scenario.cloudsimplus.resources;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.resources.SanStorage;
import org.scenario.autoadaptive.MetadataManager;

public class AdaptedSan extends SanStorage{
	
	
	private List<Host> hostsAccessingThisSan;
	
	public AdaptedSan(String name , long capacity, double bandwidth, double networkLatency) throws IllegalArgumentException {
		super(name, capacity, bandwidth, networkLatency);
		this.hostsAccessingThisSan = new ArrayList<>();
	}

	public void addAccessingHost(Host host) {
		hostsAccessingThisSan.add(host);
	}
	
	public List<Host> getAccessingHosts() {
		return hostsAccessingThisSan;
	}
	
	@Override
	public double addFile(File file) {
		((AdaptedMetadata)file.getAttribute()).setContainingDevice(this);
        MetadataManager.onFileCreate(file.getAttribute());
		return super.addFile(file);
		
	}
	
	 /**
     * {@inheritDoc}
     * The network latency is added to the transfer time.
     * @param fileSize {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public double getTransferTime(final int fileSize) {
        //Gets the time to read the from from the local storage device (such as an HD or SSD).
        final double storageDeviceReadTime = getTransferTime(fileSize, getMaxTransferRate()) + getLatency();
        int processesReading = 0;
        try {
        	int hostindex = 0 ; 
        	for(Host host : hostsAccessingThisSan) {
				// TODO here get 0 since we have one vm per host in scenario must change otherwise
        		processesReading += host.getVmList().get(0).getCloudletScheduler().getCloudletExecList().size();
        		if(processesReading > 7 + 8 * hostindex )
        			processesReading = 7 + 8 * hostindex;
        		
        	}
			
		} catch (Exception e) {
			System.out.print("");
		}
        
        //Gets the time to transfer the file through the network
        final double networkTransferTime = getTransferTime(fileSize, getBandwidth() / processesReading );
        // TODO test this
//        return Math.max(storageDeviceReadTime, networkTransferTime) + getNetworkLatency();
        return super.getTransferTime(fileSize);
    }


}
