package org.scenario.autoadaptive;

import java.util.Collections;
import java.util.List;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;
import org.scenario.Utils.Utils;
import org.scenario.cloudsimplus.AdaptedVm;

public class LoadBalancerWeightedLeastConnections implements LoadBalancer{

	
	/**
	 * This algorithm takes into consideration the number of current connections 
	 * each server/Vm has, and it introduces a "weight" component based on the 
	 * respective capacities of each server/Vm.
	 * Details about this algorithm and other load balancing algorithms 
	 * <a href="https://www.jscape.com/blog/load-balancing-algorithms"> here </a>
	 * 
	 * @author Lahiaouni Abderrahman
	 */

	/**
	 * Average lenght in terms of cpu utilization
	 * 
	 */
	public int avgCloudletLenghtInDc = 0;
	
	private Datacenter datacenter;

	@Override
	public Vm electVm(List<Vm> vmList) {
		Vm candidate = vmList.get(Utils.getuniformIntegerDist(0, vmList.size()-1).sample());
//		Vm candidate = Collections.min(vmList, (vm1,vm2) -> ((AdaptedVm) vm1).getOrUpdateRequestCount(0) > ((AdaptedVm) vm2).getOrUpdateRequestCount(0) ? 1 : -1);
//		Vm candidate = vmList.get(vmList.size()-1);
		// TODO connections to a dc should be ++
		// Also update avgCloudletLenght 
		return candidate;
	}
	
	public int updateAvgCloudletLenght() {
		List<Vm> vmList = this.datacenter.getVmList();
		
		for(Vm vm : this.datacenter.getVmList()) {
			avgCloudletLenghtInDc += ((AdaptedVm) vm).getOrUpdateAvgCloudletLenght(0);
		}
		avgCloudletLenghtInDc = Math.round((float)avgCloudletLenghtInDc / ((float)vmList.size() + 1));
		return avgCloudletLenghtInDc;
		
	}

	@Override
	public void setDatacenter(Datacenter datacenter) {
		this.datacenter = datacenter;
		
	}
}
