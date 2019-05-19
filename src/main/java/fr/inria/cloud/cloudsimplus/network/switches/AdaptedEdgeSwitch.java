package org.scenario.cloudsimplus.network.switches;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.events.PredicateType;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.HostPacket;
import org.cloudbus.cloudsim.network.switches.AbstractSwitch;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.network.switches.Switch;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.vms.Vm;

public class AdaptedEdgeSwitch extends AdaptedAbstractSwitch {
	
	/**
     * The level (layer) of the switch in the network topology.
     */
    public static final int LEVEL = 2;

    /**
     * Default downlink bandwidth of EdgeSwitch in Megabits/s.
     * It also represents the uplink bandwidth of connected hosts.
     */
    public static final long DOWNLINK_BW = 10000;
    
    /**
     * Default number of ports that defines the number of
     * {@link Host} that can be connected to the switch.
     */
    public static final int PORTS = 4;

    /**
     * Default switching delay in the order of microseconds.
     */
    public static final double SWITCHING_DELAY = 0.00000285;
    
    /**
     * Counter to assign id to switch among the switches of the same level
     */
    private static int counter = 0;

    /**
     * Instantiates a EdgeSwitch specifying Datacenter that are connected to its
     * downlink and uplink ports, and corresponding bandwidths. In this switch,
     * downlink ports aren't connected to other switch but to hosts.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param dc The Datacenter where the switch is connected to
     */
    public AdaptedEdgeSwitch(CloudSim simulation, NetworkDatacenter dc) {
        super(simulation, dc);
        this.setIdAmongSameLevel(this.counter++);
        setUplinkBandwidth(AdaptedAggregateSwitch.DOWNLINK_BW);
        setDownlinkBandwidth(DOWNLINK_BW);
        setSwitchingDelay(SWITCHING_DELAY);
        setPorts(PORTS);
    }
    
    @Override
    protected void processPacketDown(SimEvent ev) {

        final HostPacket netPkt = (HostPacket) ev.getData();
        final Vm receiverVm = netPkt.getVmPacket().getDestination();
        // packet is to be received by host
        final NetworkHost host = getVmHost(receiverVm);
        netPkt.setDestination(host);
        addPacketToBeSentToHost(host, netPkt);
        super.processPacketDown(ev);
        send(this.getDatacenter(),
                0,
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING);	
    }

	 @Override
	    protected void processPacketUp(SimEvent ev) {

		 super.processPacketUp(ev);
	        final HostPacket hostPkt = (HostPacket) ev.getData();
	        
	        final Vm receiverVm = hostPkt.getVmPacket().getDestination();

	        // packet is received from host
	        // packet is to be sent to aggregate level or to another host in the same level
	        if (receiverVm != null){
	        	
	        	final NetworkHost host = getVmHost(receiverVm);
	        	hostPkt.setDestination(host);
	        	
	        	// packet needs to go to a host which is connected directly to switch
	        	if (host != null && host != Host.NULL && host.getDatacenter().getId() == this.getDatacenter().getId()) {
	        		addPacketToBeSentToHost(host, hostPkt);
	        		return;
	        	}
	        }

	        // otherwise, packet is to be sent to upper switch
	        /*
	         * ASSUMPTION: Each Edge is connected to one Aggregate Switch.
	         * If there are more than one Aggregate Switch, the following code has to be modified.
	        */
	        final Switch aggregateSwitch = getUplinkSwitches().get(0);
	        addPacketToBeSentToUplinkSwitch(aggregateSwitch, hostPkt);
	    }

	@Override
	public int getLevel() {
		// TODO Auto-generated method stub
		return 2;
	}
}
