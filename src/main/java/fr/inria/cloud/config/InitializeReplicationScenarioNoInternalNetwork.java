package org.scenario.config;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.DatacenterStorage;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.SanStorage;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.listeners.EventInfo;
import org.scenario.cloudsimplus.resources.AdaptedFile;


public class InitializeReplicationScenarioNoInternalNetwork extends InitializeReplicationScenario{
	
	
	protected List<DatacenterBroker> createBrokers(CloudSim simulation) {
		DatacenterBroker broker = new DatacenterBrokerSimple(simulation);
		List<DatacenterBroker> brokers = new ArrayList<>();
		brokers.add(broker);
		broker.getSimulation().addOnClockTickListener(this::func);
		return brokers;
	}
	
	public void func(EventInfo evt) {
		
	}
	
	protected Host createHost(int ram,long mips,int pes) {
        final long storage = 1000000; // host storage (MEGABYTE)
        final long bw = 10000; //in Megabits/s

        List<Pe> pesList = new ArrayList<>(); //List of CPU cores

        for (int i = 0; i < pes; i++) {
            pesList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return new HostSimple(ram, bw, storage, pesList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerSpaceShared());
    }

	protected Vm createVm(int id, int ram,long mips,int pes) {
        final long   storage = 10000; // vm image size (MEGABYTE)
        final long   bw = 1000; // vm bandwidth (Megabits/s)

        return new VmSimple(id, mips, pes)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    protected Cloudlet createCloudlet() {
        final long length = 10000; //in Million Structions (MI)
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution
        final int  numberOfCpuCores = 2; // cores used by cloudlet

        Cloudlet cloudlet
                = new CloudletSimple(
                        -1, length, numberOfCpuCores)
                        .setFileSize(fileSize)
                        .setOutputSize(outputSize)
                        .setUtilizationModelRam(new UtilizationModelDynamic(0.5))
                        .setUtilizationModelCpu(new UtilizationModelDynamic(1))
                        .setUtilizationModelBw(new UtilizationModelDynamic(0.5));
        cloudlet.addRequiredFile("file1.dat");
        cloudlet.addRequiredFile("file2.dat");
        cloudlet.addRequiredFile("file3.dat");
        return cloudlet;
    }
    
    protected Datacenter createDatacenter(CloudSim simulation, List<Host> hostList, VmAllocationPolicy vmAllocationPolicy){
    	Datacenter dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    	return dc;
    }

	@Override
	protected FileStorage createStorage(String name, int capacity, double Bandwidth, double networkLatency) {	
		return new SanStorage(capacity, Bandwidth, networkLatency);
	}
	
	@Override
	protected Datacenter createSuperDatacenter(CloudSim simulation) {
		List<Host> hostList3 = new ArrayList<>(SimulationParameters.HOST_SUPER);
        for(int j = 0; j < SimulationParameters.HOST_SUPER; j++) {
            Host host = createHost(32768,4000,8);
            hostList3.add(host);
        }
        List<FileStorage> storageList = new ArrayList<FileStorage>();
	    storageList.add(createStorage("name",1000000000, 10.0, 5));
		// TODO here get 0 since we have one san per DC must change otherwise
	    storageList.get(0).addFile(new AdaptedFile("file1.dat", 20));
	    storageList.get(0).addFile(new AdaptedFile("file2.dat", 100));
	    storageList.get(0).addFile(new AdaptedFile("file3.dat", 700));
	    DatacenterStorage datacenterStorage = new  DatacenterStorage();
        Datacenter dc = createDatacenter(simulation, hostList3, new VmAllocationPolicySimple());
        dc.setDatacenterStorage(datacenterStorage);
        datacenterStorage.setStorageList(storageList);
        return dc;
	}

	@Override
	protected Datacenter createMainDatacenter(CloudSim simulation) {
		List<Host> hostList2 = new ArrayList<>(SimulationParameters.HOST_MID);
        for(int j = 0; j < SimulationParameters.HOST_MID; j++) {
            Host host = createHost(16348,2500,6);
            hostList2.add(host);
        }
        List<FileStorage> storageList = new ArrayList<FileStorage>();
        DatacenterStorage datacenterStorage = new  DatacenterStorage();
	    storageList.add(new SanStorage(100000000, 10.0, 3));
	    datacenterStorage.setStorageList(storageList);
	    Datacenter dc = createDatacenter(simulation, hostList2, new VmAllocationPolicySimple());
	    dc.setDatacenterStorage(datacenterStorage);
	    return dc;
	}

	@Override
	protected Datacenter createOrdinaryDatacenter(CloudSim simulation) {
		List<Host> hostList = new ArrayList<>(SimulationParameters.HOST_STANDARD);
        for(int j = 0; j < SimulationParameters.HOST_STANDARD; j++) {
            Host host = createHost(8192,1000,4);
            hostList.add(host);
        }
        List<FileStorage> storageList = new ArrayList<FileStorage>();
        DatacenterStorage datacenterStorage = new  DatacenterStorage();
	    storageList.add(new SanStorage(10000000, 10.0, 4));
	    datacenterStorage.setStorageList(storageList);
	    Datacenter dc = createDatacenter(simulation, hostList, new VmAllocationPolicySimple());
	    dc.setDatacenterStorage(datacenterStorage);
	    return dc;
	}

}
