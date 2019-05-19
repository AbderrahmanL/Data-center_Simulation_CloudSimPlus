package org.scenario.config;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimEntity;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.vms.Vm;

public abstract class InitializeReplicationScenario {
	
			protected List<Datacenter> dcs;
			protected List<DatacenterBroker> brokers;
			protected List<Vm> vmList;
			public static List<Cloudlet> cloudletList;
			
			protected abstract List<DatacenterBroker> createBrokers(CloudSim simulation) ;

			protected abstract Vm createVm(int id, int ram,long mips,int pes);
			
			protected abstract Cloudlet createCloudlet();
			
			protected abstract  Host createHost(int ram,long mips,int pes);

			protected abstract Datacenter createDatacenter(CloudSim simulation, List<Host> hostList, VmAllocationPolicy vmAllocationPolicy);
			
			protected abstract FileStorage createStorage(String name, int capacity, double Bandwidth, double networkLatency) ;
			
			protected abstract Datacenter createSuperDatacenter(CloudSim simulation) ;
			
			protected abstract Datacenter createMainDatacenter(CloudSim simulation) ;
			
			protected abstract Datacenter createOrdinaryDatacenter(CloudSim simulation) ;
			
			public List<DatacenterBroker> init(){
				CloudSim simulation = new CloudSim();
			    dcs = createDatacenters(simulation);
			    simulation.getDatacenterList().addAll(dcs);
			    simulation.terminateAt(100);
			    brokers = createBrokers(simulation);
		
			    vmList = new ArrayList<>();
			    cloudletList = new ArrayList<>();
			    
			    createVmsAndCloudlets();
			    
				return brokers;
				}
			
			/**
		     * Creates VMs, one VM for each host and one Cloudlet for each VM.
		     */
			protected void createVmsAndCloudlets(){
				
//			    for (int i = 0; i < SimulationConstParameters.HOST_SUPER*SimulationConstParameters.DC_SUPER; i++) {
//			        Vm vm = createVm(vmList.size(), 32768,1000,16);
//			        vmList.add(vm);
//			        for (int j = 0; j < SimulationConstParameters.CLOUDLETS_PER_VM; j++) {
//			            Cloudlet cloudlet = createCloudlet(cloudletList.size(), vm);
//			            cloudletList.add(cloudlet);
//			        }
//			    }
//			    for (int i = 0; i < SimulationConstParameters.HOST_MID*SimulationConstParameters.DC_MID; i++) {
//			        Vm vm = createVm(vmList.size(), 16348,2500,6);
//			        vmList.add(vm);
//			        for (int j = 0; j < SimulationConstParameters.CLOUDLETS_PER_VM; j++) {
////			            Cloudlet cloudlet = createCloudlet(cloudletList.size(), broker, vm);
////			            cloudletList.add(cloudlet);
//			        }
//			    }
//			    for (int i = 0; i < SimulationConstParameters.HOST_STANDARD*SimulationConstParameters.DC_STANDARD; i++) {
//			        Vm vm = createVm(vmList.size(), 8192,1000,4);
//			        vmList.add(vm);
//			        for (int j = 0; j < SimulationConstParameters.CLOUDLETS_PER_VM; j++) {
////			            Cloudlet cloudlet = createCloudlet(cloudletList.size(), broker, vm);
////			            cloudletList.add(cloudlet);
//			        }
//			    }
//			    brokers.get(0).submitVmList(vmList);
			    
			}
			
			protected List<Datacenter> createDatacenters(CloudSim simulation){
					
					List<Datacenter> datacenters = new ArrayList<Datacenter>();
					    
				        for(int i=0 ; i<SimulationParameters.DC_SUPER; i++){
				        	Datacenter dc = createSuperDatacenter(simulation);
					        datacenters.add(dc);
				        } 
				        
				        for(int i=0 ; i<SimulationParameters.DC_MID; i++){
				        	Datacenter dc = createMainDatacenter(simulation);
						    datacenters.add(dc);
				        }
				        
				        for(int i=0 ; i<SimulationParameters.DC_STANDARD; i++){
				        	Datacenter dc = createOrdinaryDatacenter(simulation);
						    datacenters.add(dc);
				        }
				        
					return datacenters;
				}
			
}
