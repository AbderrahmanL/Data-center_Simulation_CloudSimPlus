package org.scenario.autoadaptive;

import java.util.List;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

public interface LoadBalancer {

	public Vm electVm(List<Vm> list);
	
	public int updateAvgCloudletLenght();

	void setDatacenter(Datacenter datacenter);

}
