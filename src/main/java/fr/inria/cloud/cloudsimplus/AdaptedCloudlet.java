package org.scenario.cloudsimplus;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.resources.FileAttribute;
import org.scenario.autoadaptive.MetadataCatalog;

public class AdaptedCloudlet extends NetworkCloudlet{
	
	/**
	 * @see #getSendTime()
	 */
	private double sendTime;
	
	/**
	 * @see #getDcReceiveTime()
	 */
	private double dcReceiveTime;
	
	/**
	 * @see #getVmReceiveTime()
	 */
	private double vmReceiveTime;
	
	/**
	 * @see #getFileRetrievalTime()
	 */
	private double fileRetrievalTime;

	/**
	 * @see #getLeftVmToBrokerTime()
	 */
	private double leftVmToBrokerTime;
	
	/**
	 * @see #getUplinkTime()
	 */
	private double uplinkTime = 0D;
	
	/**
	 * @see #getLeftDcToBrokerTime()
	 */
	private double leftDcToBrokerTime = 0D ;
	
	/**
	 * @see #getGotToBrokerTime()
	 */
	private double gotToBrokerTime;
	
	
	/**
	 * @see #getRequestedFileId()
	 */
	private int requestedFileId = -1;
	

	/**
	 * @param id
	 * @param cloudletLength
	 * @param pesNumber
	 */
	public AdaptedCloudlet(int id, long cloudletLength, int pesNumber) {
		super(id, cloudletLength, pesNumber);
		// TODO Auto-generated constructor stub
	}


	public double getSendTime() {
		return BigDecimal.valueOf(sendTime).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}


	public void setSendTime(double sendTime) {
		this.sendTime = sendTime;
	}

	
	public double getDcReceiveTime() {
		return BigDecimal.valueOf(dcReceiveTime).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}


	public void setDcReceiveTime(double dcReceiveTime) {
		this.dcReceiveTime = dcReceiveTime;
	}


	public double getVmReceiveTime() {
		return BigDecimal.valueOf(vmReceiveTime).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}


	public void setVmReceiveTime(double VmReceiveTime) {
		this.vmReceiveTime = VmReceiveTime;
	}
	
	
	public double getFileRetrievalTime() {
		return BigDecimal.valueOf(fileRetrievalTime).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}
	
	
	public void setFileRetrievalTime(double fileRetrievalTime) {
		this.fileRetrievalTime = fileRetrievalTime;
	}

	public double getLeftVmToBrokerTime() {
		return BigDecimal.valueOf(leftVmToBrokerTime).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}
	
	public double getLeftVmToBrokerTime(int flag) {
		return BigDecimal.valueOf(leftVmToBrokerTime).setScale(9, RoundingMode.HALF_UP).doubleValue();
	}


	public void setLeftVmToBrokerTime(double leftVmToBrockerTime) {
		this.leftVmToBrokerTime = leftVmToBrockerTime;
	}

	public double getUplinkTime() {
		return BigDecimal.valueOf(leftDcToBrokerTime-getFinishTime()).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}
	

	public double getLeftDcToBrokerTime() {
		return BigDecimal.valueOf(leftDcToBrokerTime).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}
	public double getLeftDcToBrokerTime(int flag) {
		return BigDecimal.valueOf(leftDcToBrokerTime).setScale(9, RoundingMode.HALF_UP).doubleValue();
	}


	public void setLeftDcToBrokerTime(double leftDcToBrokerTime) {
		this.leftDcToBrokerTime = leftDcToBrokerTime;
	}


	public double getGotToBrokerTime() {
		return BigDecimal.valueOf(gotToBrokerTime).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}


	public void setGotToBrokerTime(double gotToBrokerTime) {
		this.gotToBrokerTime = gotToBrokerTime;
	}	
	

	public int getRequestedFileId() {
		return requestedFileId;
	}
	
	
	public void setRequestedFileId(int requestedFileId) {
		this.requestedFileId = requestedFileId;
	}
	
	@Override
	public double getExecStartTime(){
		return BigDecimal.valueOf(super.getExecStartTime()).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}
	
	@Override
	public double getFinishTime(){
		return BigDecimal.valueOf(super.getFinishTime()).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}
	
	@Override
	public double getActualCpuTime(){
		return BigDecimal.valueOf(super.getActualCpuTime()).setScale(6, RoundingMode.HALF_UP).doubleValue();
	}
	
	public double getOverallTime() {
		return BigDecimal.valueOf(this.getLeftDcToBrokerTime()).subtract(BigDecimal.valueOf(this.getDcReceiveTime())).setScale(6, RoundingMode.HALF_UP).doubleValue(); 
	}
	
	/**
	 * Only used for dusplay not in simulation since it would be silly
	 * that a cloudlet can get a file metadata magically using a method
	 * @return
	 */
	public FileAttribute getRequestedFile(){
		return ((AdaptedDatacenterStorage)this.getLastDatacenter().getDatacenterStorage()).getMetadataManager().getFileMetadataWithId(this.getRequestedFileId(),null,false);
	}
}
