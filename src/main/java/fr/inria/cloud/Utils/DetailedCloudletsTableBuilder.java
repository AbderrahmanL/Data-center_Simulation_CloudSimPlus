package org.scenario.Utils;

import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Identifiable;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.scenario.autoadaptive.MetadataCatalog;
import org.scenario.cloudsimplus.AdaptedCloudlet;
import org.scenario.cloudsimplus.AdaptedDatacenterStorage;

public class DetailedCloudletsTableBuilder extends CloudletsTableBuilder{

    private static final String TIME_FORMAT = "%d";
    private static final String TIME_CLOCK = "clock(s)";
    private static final String TIME_DELAY = "delay(s)";
    private static final String CPU_CORES = "CPU cores";
    private static final String MEGABYTE = "MB";

	public DetailedCloudletsTableBuilder(List<? extends Cloudlet> list) {
		super(list);
		// TODO Auto-generated constructor stub
	}

	 @Override
	    protected void createTableColumns() {
	        final String ID = "ID";
	        addColumnDataFunction(getTable().addColumn("Cloudlet", ID), Identifiable::getId);
	        addColumnDataFunction(getTable().addColumn("DC", ID), c -> c.getVm().getHost().getDatacenter().getId());
	        addColumnDataFunction(getTable().addColumn("Host", ID), c -> c.getVm().getHost().getId());
	        addColumnDataFunction(getTable().addColumn("VM", ID), c -> c.getVm().getId());
	        addColumnDataFunction(getTable().addColumn("File Id", ID), c -> ((AdaptedDatacenterStorage)c.getLastDatacenter().getDatacenterStorage()).getMetadataManager().getFileMetadataWithId(((AdaptedCloudlet)c).getRequestedFileId(),null,false).getRegistrationID());
	        addColumnDataFunction(getTable().addColumn("SendTime", TIME_CLOCK), c -> Double.toString(((AdaptedCloudlet)c).getSendTime()));
	        addColumnDataFunction(getTable().addColumn("ReceivedByDC", TIME_CLOCK), c -> Double.toString(((AdaptedCloudlet)c).getDcReceiveTime()));
	        addColumnDataFunction(getTable().addColumn("ReceivedByVM", TIME_CLOCK), c -> Double.toString(((AdaptedCloudlet)c).getVmReceiveTime()));
	        addColumnDataFunction(getTable().addColumn("StartTime", TIME_CLOCK), c -> Double.toString(((AdaptedCloudlet)c).getExecStartTime()));
	        addColumnDataFunction(getTable().addColumn("RetreivedFile", TIME_DELAY), c -> Double.toString(((AdaptedCloudlet)c).getFileRetrievalTime()));
	        addColumnDataFunction(getTable().addColumn("FinishTime", TIME_CLOCK), c -> Double.toString(((AdaptedCloudlet)c).getFinishTime()));
	        addColumnDataFunction(getTable().addColumn("LeftVmToBroker", TIME_CLOCK), c -> Double.toString(((AdaptedCloudlet)c).getLeftVmToBrokerTime()));
	        addColumnDataFunction(getTable().addColumn("ResponseUplinkTime", TIME_DELAY), c -> Double.toString(((AdaptedCloudlet)c).getUplinkTime()));
	        addColumnDataFunction(getTable().addColumn("LeftDcToBroker", TIME_CLOCK), c -> Double.toString(((AdaptedCloudlet)c).getLeftDcToBrokerTime()));
	        addColumnDataFunction(getTable().addColumn("RequestReturn", TIME_CLOCK), c -> Double.toString(((AdaptedCloudlet)c).getGotToBrokerTime()));
	        addColumnDataFunction(getTable().addColumn("ActualCpuTime", TIME_CLOCK), c -> Double.toString(((AdaptedCloudlet)c).getActualCpuTime()));
	        addColumnDataFunction(getTable().addColumn("OverallTime", TIME_CLOCK), c -> Double.toString( ((AdaptedCloudlet)c).getOverallTime() ));
	        addColumnDataFunction(getTable().addColumn("OveraaallTime", TIME_CLOCK), c -> Double.toString( ((AdaptedCloudlet)c).getGotToBrokerTime() - ((AdaptedCloudlet)c).getSendTime()));
	    }
	 
}
