package vStrikerTestEngine.atmos;

import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;

import vStrikerEntities.Api;
import vStrikerEntities.ExecutionReportData;

import com.emc.vipr.atmos.atmosapi;

//@author Sanjeev Chauhan

public class AtmosReadWorker implements Callable<ExecutionReportData> {
	private String objectLocation;
	private Api api;
	ExecutionReportData reportData = new ExecutionReportData();
	
	/*
	 * Default Constructor
	 */
	public AtmosReadWorker(){
	}
	
	/*
	 * Constructor
	 */
	public AtmosReadWorker(String objectLocation, Api api) {
		this.api = api;
		this.objectLocation = objectLocation;
	}
	
	public ExecutionReportData call() throws Exception {
		long startTime = System.nanoTime();
		// ToDo - Atmos is expecting ObjectId as the last argument
		atmosapi.ReadStringObject(api.getSubtenant(), api.getSecretKey(),
				api.getUrl(), FilenameUtils.getName(objectLocation), null);
		long endTime = System.nanoTime();
		System.out.println("Atmos CreateObject execution time: " + (endTime - startTime));
		reportData.setDataKey("Atmos");
		reportData.setThreadValue(Thread.currentThread().getName());
		reportData.setCrudValue("Read");
		reportData.setDataValue(Long.toString((endTime-startTime)/1000000));
		return reportData;
	}
}
