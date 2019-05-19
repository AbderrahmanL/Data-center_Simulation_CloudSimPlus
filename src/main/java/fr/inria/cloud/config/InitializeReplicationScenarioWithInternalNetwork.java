package org.scenario.config;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.network.CloudletExecutionTask;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.network.switches.Switch;
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.DatacenterStorage;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.EventInfo;
import org.scenario.Utils.Utils;
import org.scenario.autoadaptive.LoadBalancerWeightedLeastConnections;
import org.scenario.cloudsimplus.AdaptedCloudlet;
import org.scenario.cloudsimplus.AdaptedDatacenter;
import org.scenario.cloudsimplus.AdaptedDatacenterBroker;
import org.scenario.cloudsimplus.AdaptedDatacenterStorage;
import org.scenario.cloudsimplus.AdaptedHost;
import org.scenario.cloudsimplus.AdaptedVm;
import org.scenario.cloudsimplus.resources.AdaptedFile;
import org.scenario.cloudsimplus.resources.AdaptedSan;

public abstract class InitializeReplicationScenarioWithInternalNetwork extends InitializeReplicationScenario {

	/**
     * Creates internal Datacenter network.
     *
     * @param datacenter Datacenter where the network will be created
     */
    protected abstract void createNetwork(AdaptedDatacenter datacenter);
	
    public static int cloudletsCount = 0;
    
    @Override
    public List<DatacenterBroker> init() {
	    	brokers = super.init();
	    	populateBriteFile(brokers);
	    	NetworkTopology networkTopology = BriteNetworkTopology.getInstance("topology.brite");
			// TODO here get 0 since we have one broker must change otherwise
		  	brokers.get(0).getSimulation().setNetworkTopology(networkTopology);
		  	for (int i=1 ; i<brokers.get(0).getSimulation().getEntityList().size() ; i++)
		  	networkTopology.mapNode(i, i);
		  	brokers.get(0).getSimulation().addOnClockTickListener(this::func);
		  	return brokers;
    }
    
    public void func(EventInfo evt) {
    	if(Utils.getuniformRealDist().sample() <= 0.11 && evt.getTime()<20){
            System.out.printf("\n# Randomly creating 1 Cloudlet at time %.2f\n", evt.getTime());
            Cloudlet cloudlet = createCloudlet();
            int rand = Utils.getuniformIntegerDist(0, 2).sample();
        	((AdaptedCloudlet)cloudlet).setRequestedFileId(rand);
            cloudletList.add(cloudlet);
            brokers.get(0).submitCloudlet(cloudlet);
        }
	}

	private void populateBriteFile(List<DatacenterBroker> brokers) {
		try {
			int edgeCounter = 0;
			FileWriter wr = new FileWriter("topology.brite") ;
			wr.write("Nodes: \n");
			for(SimEntity ent : brokers.get(0).getSimulation().getEntityList()) {
			wr.write(ent.getId() + "	1	1	0	0	-1	RT_NODE " + ent.getName() + "\n");
			}
			wr.write("\n");
			wr.write("Edges: \n");
			Iterator<Datacenter> itr = brokers.get(0).getSimulation().getDatacenterList().iterator();
			while(itr.hasNext() ) {
				AdaptedDatacenter dc = (AdaptedDatacenter) itr.next();
				List<Switch> switches = dc.getSwitchMap();
				for(Switch sw : switches) {
					if(sw.getLevel() == 0 ) {
						wr.write( edgeCounter +"	"+ (sw.getId()) +"	"+ (sw.getId()+ 1) +  "	1.0		0.00005		0.0	-1	-1	E_RT	U\n");
						edgeCounter++;
						wr.write( edgeCounter +"	"+ (sw.getId()) +"	"+ (sw.getId()+ 4) +  "	1.0		0.00005		0.0	-1	-1	E_RT	U\n");
						edgeCounter++;
						wr.write( edgeCounter +"	"+ (sw.getId()) +"	"+ (sw.getId()+ 7) +  "	1.0		0.00005		0.0	-1	-1	E_RT	U\n");
						edgeCounter++;
						for(DatacenterBroker br : brokers ) {
						wr.write( edgeCounter +"	"+ (sw.getId()) +"	"+ (br.getId()) +  "	1.0		0.2		11.0	-1	-1	E_RT	U\n");
						wr.write( edgeCounter +"	"+ (dc.getId()) +"	"+ (br.getId()) +  "	1.0		0.2		11.0	-1	-1	E_RT	U\n");
						edgeCounter++;	
						}
						
					}
					if(sw.getLevel() == 1 ) {
						wr.write( edgeCounter +"	"+ (sw.getId()) +"	"+ (sw.getId()+ 1) +  "	1.0		0.00005		0.0	-1	-1	E_RT	U\n");
						edgeCounter++;
						wr.write( edgeCounter +"	"+ (sw.getId()) +"	"+ (sw.getId()+ 2) +  "	1.0		0.00005		0.0	-1	-1	E_RT	U\n");
						edgeCounter++;
					}
					
				}
					
			}
			wr.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected List<DatacenterBroker> createBrokers(CloudSim simulation) {
		DatacenterBroker broker = new AdaptedDatacenterBroker(simulation);
		List<DatacenterBroker> brokers = new ArrayList<>();
		brokers.add(broker);
		return brokers;
	}

	@Override
	protected Vm createVm(int id, int ram, long mips, int pes) {
		final long   storage = 10000; // vm image size (MEGABYTE)
        final long   bw = 1000; // vm bandwidth (Megabits/s)

        return new AdaptedVm(id, mips, pes)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerCompletelyFair());
	}

	@Override
	protected Cloudlet createCloudlet() {
	        final long fileSize = 300; //Size (in bytes) before execution
	        final long outputSize = 300; //Size (in bytes) after execution
	        final int  numberOfCpuCores = 1; // cores used by cloudlet

	        AdaptedCloudlet cloudlet
	                = (AdaptedCloudlet) new AdaptedCloudlet(
			        -1, 100, numberOfCpuCores)
			        .setFileSize(fileSize)
			        .setOutputSize(outputSize)
			        .setUtilizationModelRam(new UtilizationModelFull())
			        .setUtilizationModelCpu(new UtilizationModelFull())
			        .setUtilizationModelBw(new UtilizationModelFull());
	        cloudlet.setSubmissionDelay(0);
	        	/*TODO Identification mechanism for files needs to change 
	        	 to something more usable */
	        	
	        if(cloudletsCount <  150 * SimulationParameters.SCALE_FACTOR) {
	        	cloudlet.setRequestedFileId(0);
	        }
	        else if(cloudletsCount < 200 * SimulationParameters.SCALE_FACTOR && cloudletsCount >= 150* SimulationParameters.SCALE_FACTOR) {
	        	cloudlet.setRequestedFileId(1);
	        }
	        else if(cloudletsCount < 350* SimulationParameters.SCALE_FACTOR  && cloudletsCount >= 250* SimulationParameters.SCALE_FACTOR ) {
	        	cloudlet.setRequestedFileId(2);
	        }
//	        else {
//	        	int rand = Utils.getuniformIntegerDist(3, 9).sample();
//	        	cloudlet.setRequestedFileId(rand);
//	        	cloudlet.setSubmissionDelay(rand);
//	        }
//	        if(debugCount < 400* SimulationParameters.SCALE_FACTOR  && debugCount >= 300* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(3);
//	        }
//	        if(debugCount < 500* SimulationParameters.SCALE_FACTOR  && debugCount >= 400* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(4);
//	        }
//	        if(debugCount < 600* SimulationParameters.SCALE_FACTOR  && debugCount >= 500* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(5);
//	        }
//	        if(debugCount < 700* SimulationParameters.SCALE_FACTOR  && debugCount >= 600* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(6);
//	        }
//	        if(debugCount < 800* SimulationParameters.SCALE_FACTOR  && debugCount >= 700* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(7);
//	        }
//	        if(debugCount < 900* SimulationParameters.SCALE_FACTOR  && debugCount >= 800* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(8);
//	        }
//	        if(debugCount < 1000* SimulationParameters.SCALE_FACTOR  && debugCount >= 900* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(9);
//	        }
//	        if(debugCount < 1100* SimulationParameters.SCALE_FACTOR  && debugCount >= 1000* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(9);
//	        }
//	        if(debugCount < 1200* SimulationParameters.SCALE_FACTOR  && debugCount >= 1100* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(9);
//	        }
//	        if(debugCount < 1300* SimulationParameters.SCALE_FACTOR  && debugCount >= 1200* SimulationParameters.SCALE_FACTOR) {
//	        	cloudlet.setRequestedFileId(9);
//	        }
	        cloudletsCount++;
	        cloudlet.addTask(new CloudletExecutionTask(numberOfCpuCores, 2));
	        return cloudlet;
	}

	@Override
	protected Host createHost(int ram, long mips, int pes) {
		 final long storage = 1000000; // host storage (MEGABYTE)
	        final long bw = 10000; //in Megabits/s

	        List<Pe> pesList = new ArrayList<>(); //List of CPU cores

	        for (int i = 0; i < pes; i++) {
	            pesList.add(new PeSimple(mips, new PeProvisionerSimple()));
	        }

	        return (AdaptedHost) new AdaptedHost(ram, bw, storage, pesList)
	                .setRamProvisioner(new ResourceProvisionerSimple())
	                .setBwProvisioner(new ResourceProvisionerSimple())
	                .setVmScheduler(new VmSchedulerSpaceShared());
	}

	@Override
	protected Datacenter createDatacenter(CloudSim simulation,
			List<Host> hostList, VmAllocationPolicy vmAllocationPolicy) {
		AdaptedDatacenter dc = new AdaptedDatacenter(simulation, hostList, new VmAllocationPolicySimple(), new LoadBalancerWeightedLeastConnections());
		createNetwork(dc);
    	return dc;
	}

	@Override
	protected FileStorage createStorage(String name, int capacity, double Bandwidth, double networkLatency) {	
		return new AdaptedSan(name,capacity, Bandwidth, networkLatency);
	}

	@Override
	protected Datacenter createSuperDatacenter(CloudSim simulation) {
		List<Host> hostList = new ArrayList<>(SimulationParameters.HOST_SUPER);
		List<FileStorage> storageList = new ArrayList<FileStorage>();
		FileStorage san = new AdaptedSan("temporary", 1, 1, 1);
        for(int j = 0; j < SimulationParameters.HOST_SUPER; j++) {
        	Host host = createHost(32768,4000,16);
            hostList.add(host);
            	if(j % SimulationParameters.HOSTS_PER_SWITCH == 0) {
            		san = createStorage("San" + j / SimulationParameters.HOSTS_PER_SWITCH,1000000000, 16000.0, 0.003);
            		storageList.add(san);  
            	}
            	((AdaptedSan)san).addAccessingHost(host);
            	((AdaptedHost)host).setStorage(san);   	
        }
        DatacenterStorage datacenterStorage = new  AdaptedDatacenterStorage(storageList);
        Datacenter dc = createDatacenter(simulation, hostList, new VmAllocationPolicySimple());
        
        File file1 = new AdaptedFile("file1.dat", 100);
        File file2 = new AdaptedFile("file2.dat", 100);
        File file3 = new AdaptedFile("file3.dat", 100);
        
        datacenterStorage.getStorageList().get(0).addFile(file1);
        datacenterStorage.getStorageList().get(2).addFile(file2);
        datacenterStorage.getStorageList().get(4).addFile(file3);
          
//        datacenterStorage.getStorageList().get(3).addFile(new AdaptedFile("file9.dat", 100));
//        datacenterStorage.getStorageList().get(1).addFile(new AdaptedFile("file10.dat", 100));
//        datacenterStorage.getStorageList().get(3).addFile(new AdaptedFile("file11.dat", 100));
//        datacenterStorage.getStorageList().get(5).addFile(new AdaptedFile("file12.dat", 100));
//        datacenterStorage.getStorageList().get(3).addFile(new AdaptedFile("file13.dat", 100));
        dc.setDatacenterStorage(datacenterStorage);
//        MetadataCatalog catalog = ReplicaCatalog.getCatalogInstance();
//        System.out.println(((HashMap<Integer, LinkedList<FileAttribute>>)catalog).get(0).get(0).getFileSize());
        return dc;
	}

	@Override
	protected Datacenter createMainDatacenter(CloudSim simulation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Datacenter createOrdinaryDatacenter(CloudSim simulation) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
	