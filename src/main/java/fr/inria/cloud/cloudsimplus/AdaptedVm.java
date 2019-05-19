package org.scenario.cloudsimplus;

import org.cloudbus.cloudsim.vms.network.NetworkVm;

public class AdaptedVm extends NetworkVm{
	
	private int currentRequestsCount = 0 ;
	
	private int avgCloudletLenghtInVm = 0;
	
	
	public AdaptedVm(final int id, final long mipsCapacity, final int numberOfPes) {
        super(id, mipsCapacity, numberOfPes);
        currentRequestsCount = 0;
    }
	
	/**
	 * Gets or updates the {@link #currentRequestsCount} 
	 * Returns the current value if the parameter is 0
	 * Updates (increments/decrements) if the parameter is 1/-1
	 * 
	 * @param length
	 * @return {@link #currentRequestsCount}
	 */
	public int getOrUpdateRequestCount(int step ) {
		if(step == 1)
			return ++currentRequestsCount;
		else if(step == -1)
			return --currentRequestsCount;
		else
			return currentRequestsCount;
	}
	
	/**
	 * Return the {@link #avgCloudletLenghtInVm} if the 
	 * length is equal or less than 0
	 * Return new average if greater than 0 
	 * 
	 * @param length
	 * @return {@link #avgCloudletLenghtInVm}
	 */
	public int getOrUpdateAvgCloudletLenght(long length ) {
		if(length <= 0)
			return avgCloudletLenghtInVm;
		avgCloudletLenghtInVm = (int) Math.round((float)(avgCloudletLenghtInVm + length) / 2.0);
		((AdaptedDatacenter) this.getHost().getDatacenter()).getBalancer().updateAvgCloudletLenght();
		return avgCloudletLenghtInVm;
	}
	
}
