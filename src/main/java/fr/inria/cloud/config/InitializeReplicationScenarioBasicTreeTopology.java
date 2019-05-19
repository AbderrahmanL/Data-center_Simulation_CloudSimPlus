package org.scenario.config;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.network.CloudletReceiveTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletSendTask;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.network.switches.Switch;
import org.cloudbus.cloudsim.vms.Vm;
import org.scenario.Utils.Utils;
import org.scenario.cloudsimplus.AdaptedCloudlet;
import org.scenario.cloudsimplus.AdaptedDatacenter;
import org.scenario.cloudsimplus.network.switches.AdaptedAggregateSwitch;
import org.scenario.cloudsimplus.network.switches.AdaptedEdgeSwitch;
import org.scenario.cloudsimplus.network.switches.AdaptedRootSwitch;

public class InitializeReplicationScenarioBasicTreeTopology extends InitializeReplicationScenarioWithInternalNetwork{

	private static final long TASK_RAM = 100;
	
	
	/**
     * Creates internal Datacenter network.
     *
     * @param datacenter Datacenter where the network will be created
     */
	@Override
	protected void createNetwork(AdaptedDatacenter datacenter) {
		AdaptedRootSwitch rootSwitch = new AdaptedRootSwitch((CloudSim) datacenter.getSimulation(), datacenter);
          AdaptedAggregateSwitch[] aggregateSwitches = new AdaptedAggregateSwitch[3];
          Switch[] edgeSwitches = new AdaptedEdgeSwitch[6];
        datacenter.addSwitch(rootSwitch);
        
        for (int i = 0; i < datacenter.getHostList().size()/SimulationParameters.HOSTS_PER_SWITCH; i++) {
        	int aggregateIndex = 0;
        	if(i <2) 
        		aggregateIndex = 0;
        	else if (i < 4 && i >= 2)
        		aggregateIndex = 1;
        	else
        		aggregateIndex = 2;
        	if(i == 0 || i == 2 || i == 4) {// only aggregate switches
        	aggregateSwitches[aggregateIndex] = new AdaptedAggregateSwitch((CloudSim) datacenter.getSimulation(), datacenter);
        	rootSwitch.getDownlinkSwitches().add(aggregateSwitches[aggregateIndex]);
        	aggregateSwitches[aggregateIndex].getUplinkSwitches().add(rootSwitch);
        	System.out.print(" root -> agg " + aggregateIndex);
        	}
        	
            edgeSwitches[i] = new AdaptedEdgeSwitch((CloudSim) datacenter.getSimulation(), datacenter);
            
            
            aggregateSwitches[aggregateIndex].getDownlinkSwitches().add(edgeSwitches[i]);
            edgeSwitches[i].getUplinkSwitches().add(aggregateSwitches[aggregateIndex]);
            System.out.print(" -> edge " + i);
            if(i == 0 || i == 2 || i == 4) {
            	datacenter.addSwitch(aggregateSwitches[aggregateIndex]);
            	
            }
            datacenter.addSwitch(edgeSwitches[i]);
        }
        
        int  hostPerSwitchcounter = 0;
        for (NetworkHost host : datacenter.<NetworkHost>getHostList()) {
        	
            int switchNum = (int) (host.getId() / SimulationParameters.HOSTS_PER_SWITCH);
            /**
            @TODO these two calls below are redundant.
            When connecting a host to a switch, the
            switch should be automatically linked to the Host
            to create the bi-directional association.
            */
            edgeSwitches[switchNum].connectHost(host);
            host.setEdgeSwitch((Switch) edgeSwitches[switchNum]);
            hostPerSwitchcounter++;
            if(hostPerSwitchcounter == SimulationParameters.HOSTS_PER_SWITCH){
            	hostPerSwitchcounter = 0;
            }
        }
    }
	
	@Override
	protected void createVmsAndCloudlets(){
		int cloudletCount = 0;
	    for (int i = 0; i < SimulationParameters.HOST_SUPER*SimulationParameters.DC_SUPER; i++) {
	        Vm vm = createVm(vmList.size(), 32768,4000,16);
	        vmList.add(vm);
	    }
//	    for (int j = 0; j < SimulationParameters.NO_CLOUDLETS; j++) {
//	    	AdaptedCloudlet cloudlet = (AdaptedCloudlet)createCloudlet();
//	    	setArrivalTimeForCloudlet(cloudlet);
//	    	cloudletList.add(cloudlet);
//	    }
		// TODO here get 0 since we have one broker must change otherwise
	    brokers.get(0).submitVmList(vmList);
//	    brokers.get(0).submitCloudletList(cloudletList);
	    
	}
	
	@SuppressWarnings("unused")
	private void addSendTask(
            NetworkCloudlet sourceCloudlet,
            NetworkCloudlet destinationCloudlet) {
        CloudletSendTask task = new CloudletSendTask(sourceCloudlet.getTasks().size());
        task.setMemory(TASK_RAM);
        sourceCloudlet.addTask(task);
        for (int i = 0; i < 4; i++) {
            task.addPacket(destinationCloudlet, 2000);
        }
    }

    /**
     * Adds a receive task to list of tasks of the given
     * {@link NetworkCloudlet}.
     *
     * @param cloudlet the {@link NetworkCloudlet} that the task will belong to
     * @param sourceCloudlet the cloudlet where it is expected to receive
     * packets from
     */
    @SuppressWarnings("unused")
	private void addReceiveTask(NetworkCloudlet cloudlet, NetworkCloudlet sourceCloudlet) {
        CloudletReceiveTask task = new CloudletReceiveTask(
                cloudlet.getTasks().size(), sourceCloudlet.getVm());
        task.setMemory(TASK_RAM);
        cloudlet.addTask(task);
    }
    
    @SuppressWarnings("unused")
	public void setArrivalTimeForCloudlet(Cloudlet cloudlet ) {
    	if(cloudlet.getSubmissionDelay() != 0) {
    		cloudlet.setSubmissionDelay(SimulationParameters.DEPLOY_NEW_FILE  + Utils.getuniformRealDist( ).sample() * (SimulationParameters.RANDOM_INTERVAL_RIGHT_LIMIT - SimulationParameters.DEPLOY_NEW_FILE));
    		return;
    	}
    	if( SimulationParameters.PERIODIC && !SimulationParameters.RANDOMIZED) {
    		cloudlet.setSubmissionDelay(SimulationParameters.RANDOM_INTERVAL_RIGHT_LIMIT * cloudlet.getId() / SimulationParameters.NO_CLOUDLETS);
    		return;
    	}
    	if(!SimulationParameters.PERIODIC && SimulationParameters.RANDOMIZED ) {
            cloudlet.setSubmissionDelay(Utils.getuniformRealDist( ).sample() * SimulationParameters.RANDOM_INTERVAL_RIGHT_LIMIT);
            return;
    	}
    	if(SimulationParameters.PERIODIC && SimulationParameters.RANDOMIZED) {
    		double leftLimit = SimulationParameters.RANDOM_INTERVAL_RIGHT_LIMIT * cloudlet.getId()/ SimulationParameters.NO_CLOUDLETS;
    		double rightLimit = SimulationParameters.RANDOM_INTERVAL_RIGHT_LIMIT * (cloudlet.getId() + 1) / SimulationParameters.NO_CLOUDLETS;
    		cloudlet.setSubmissionDelay( Utils.getuniformRealDist(leftLimit, rightLimit).sample());
    		return;
    	}
    	else
    		return;
    }

}
