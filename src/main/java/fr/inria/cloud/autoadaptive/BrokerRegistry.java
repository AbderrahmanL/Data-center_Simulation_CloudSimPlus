package org.scenario.autoadaptive;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.Datacenter;

public class BrokerRegistry {
	
	private final DatacenterBroker broker;
	
	private BrokerRegistry(DatacenterBroker broker) {
		this.broker = broker;
	}
	
	

}
