package org.scenario.cloudsimplus.network.switches;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.network.HostPacket;
import org.cloudbus.cloudsim.network.switches.AbstractSwitch;
import org.cloudbus.cloudsim.network.switches.AggregateSwitch;
import org.cloudbus.cloudsim.network.switches.RootSwitch;
import org.cloudbus.cloudsim.network.switches.Switch;
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.scenario.cloudsimplus.AdaptedCloudlet;
import org.scenario.cloudsimplus.AdaptedDatacenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdaptedRootSwitch extends AdaptedAbstractSwitch {
	
	private static final Logger logger = LoggerFactory.getLogger(RootSwitch.class.getSimpleName());

    /**
     * The level (layer) of the switch in the network topology.
     */
    public static final int LEVEL = 0;

    /**
     * Default number of root switch ports that defines the number of
     * {@link AggregateSwitch} that can be connected to it.
     */
    public static final int PORTS = 1;

    /**
     * Default switching delay in the order of microseconds.
     */
    public double SWITCHING_DELAY = 0.00000157;

    /**
     * The downlink bandwidth of RootSwitch in Megabits/s.
     * It also represents the uplink bandwidth of connected aggregation Datacenter.
     */
    public static final long DOWNLINK_BW =  10000 ;// 10000 Megabits (10 Gigabits)


	public AdaptedRootSwitch(CloudSim simulation, NetworkDatacenter dc) {
		 super(simulation, dc);
	        setDownlinkBandwidth(DOWNLINK_BW);
	        setSwitchingDelay(SWITCHING_DELAY);
	        setPorts(PORTS);
	}
	
	@Override
    protected void processPacketUp(SimEvent ev) {
        
        final HostPacket netPkt = (HostPacket) ev.getData();

        final long srcID = this.getDatacenter().getId();
        final Vm receiverVm = netPkt.getVmPacket().getDestination();
        long destID = 0;
        if(receiverVm == null){
        	destID = netPkt.getVmPacket().getSource().getBroker().getId();        	
        }
        else{        	
        	destID = receiverVm.getHost().getDatacenter().getId();    
        }
        	
        double transferDelay = this.getSimulation().getNetworkTopology().getDelay(srcID, destID);
        
        if(receiverVm == null){
        	//dc to broker
        	try {
        		AdaptedCloudlet cl = (AdaptedCloudlet) netPkt.getVmPacket().getReceiverCloudlet();
        		cl.setLeftDcToBrokerTime(this.getSimulation().clock());
        		cl.setGotToBrokerTime(cl.getLeftDcToBrokerTime() + transferDelay);        		
        		send(netPkt.getVmPacket().getSource().getBroker() ,transferDelay, CloudSimTags.CLOUDLET_RETURN, netPkt.getVmPacket().getReceiverCloudlet());
        	}catch(NullPointerException ex) { 
        		return;
        	}
        }
        else {
        	// broker submit cloudlet to dc or cloudlet to cloudlet same dc
        	final Switch edgeSwitch = getVmEdgeSwitch(netPkt);
        	final Switch aggSwitch = findAggregateSwitchConnectedToGivenEdgeSwitch(edgeSwitch);
        	if (destID == srcID ){	
	        	if (aggSwitch == Switch.NULL) {
	                logger.error("No destination switch for this packet");
	                return;
	            }
        	addPacketToBeSentToDownlinkSwitch(aggSwitch, netPkt);
        	}
	        else {
	        	// cloudlet to cloudlet diff dc
				// TODO here get 0 since the order of creation begins with a core switch
	        	send(((AdaptedDatacenter)netPkt.getVmPacket().getDestination().getHost().getDatacenter()).getSwitchMap().get(0) ,transferDelay, CloudSimTags.NETWORK_EVENT_UP, netPkt);        	
	        }
        }
        super.processPacketUp(ev);
    }
	
	private Switch findAggregateSwitchConnectedToGivenEdgeSwitch(Switch edgeSwitch) {
        for (final Switch aggregateSw : getDownlinkSwitches()) {
            for (final Switch edgeSw : aggregateSw.getDownlinkSwitches()) {
                if (edgeSw.getId() == edgeSwitch.getId()) {
                    return aggregateSw;
                }
            }
        }

        return Switch.NULL;
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

}
