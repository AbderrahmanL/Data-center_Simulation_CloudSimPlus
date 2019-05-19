package org.scenario.cloudsimplus.network.switches;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.network.HostPacket;
import org.cloudbus.cloudsim.network.switches.AggregateSwitch;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.network.switches.Switch;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * This class represents an Aggregate AbstractSwitch in a Datacenter network. It
 * interacts with other Datacenter in order to exchange packets.
 *
 *@author Lahiaouni Abderrahman
 */
public class AdaptedAggregateSwitch extends AdaptedAbstractSwitch {
	
	/**
     * The level (layer) of the switch in the network topology.
     */
    public static final int LEVEL = 1;

    /**
     * Default delay of {@link AggregateSwitch} in the order of microseconds.
     */
    public static final double SWITCHING_DELAY = 0.00000245 ;
	
    /**
     * Default downlink bandwidth of {@link AggregateSwitch} in Megabits/s.
     * It also represents the uplink bandwidth of connected edge Datacenter.
     */
    public static long DOWNLINK_BW =  10000 ;
    
    /**
     * Default number of aggregation switch ports that defines the number of
     * {@link EdgeSwitch} that can be connected to it.
     */
    public static final int PORTS = 1;
    
    /**
     * Counter to assign id to switch among the switches of the same level
     */
    private static int counter = 0;

    public AdaptedAggregateSwitch(CloudSim simulation, NetworkDatacenter dc) {
        super(simulation, dc);
        this.setIdAmongSameLevel(this.counter++);
        setUplinkBandwidth(AdaptedRootSwitch.DOWNLINK_BW);
        setDownlinkBandwidth(DOWNLINK_BW);
        setSwitchingDelay(SWITCHING_DELAY);
        setPorts(PORTS);

    }
    
	    @Override
	    protected void processPacketDown(SimEvent ev) {
	        super.processPacketDown(ev);

	        final HostPacket netPkt = (HostPacket) ev.getData();
	        final Vm receiverVm = netPkt.getVmPacket().getDestination();
	
	        // packet is coming from root so need to be sent to edgelevel swich
	        // find the id for edgelevel switch
	        final Switch netSwitch = getVmEdgeSwitch(netPkt);
	        addPacketToBeSentToDownlinkSwitch(netSwitch, netPkt);
	    }

	 @Override
	    protected void processPacketUp(SimEvent ev) {
	        super.processPacketUp(ev);

	        final HostPacket netPkt = (HostPacket) ev.getData();
	        final Vm receiverVm = netPkt.getVmPacket().getDestination();
	        // packet is coming from edge level router so need to be sent to
	        // either root or another edge level swich
	        // find the id for edge level switch
	        if(  receiverVm != null){
	        	
	        	final Switch edgeSwitch = getVmEdgeSwitch(netPkt);
	        	if (findConnectedEdgeSwitch(edgeSwitch)) {
	        		addPacketToBeSentToDownlinkSwitch(edgeSwitch, netPkt);
	        	}
	        	} else { // send to up
					// TODO here get 0 since we have one core switch must change otherwise
	        		final Switch sw = getUplinkSwitches().get(0);
	        		addPacketToBeSentToUplinkSwitch(sw, netPkt);
	        	}
	    }
	 
	 private boolean findConnectedEdgeSwitch(Switch edgeSwitch) {
	        return getDownlinkSwitches().stream().anyMatch(edgeSwitch::equals);
	    }

	    @Override
	    public int getLevel() {
	        return LEVEL;
	    }
}
