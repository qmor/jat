package jat.measurements;

import java.io.*;
import java.util.HashMap;
import jat.sim.initializer.*;
import jat.sim.*;
import jat.matvec.data.*;
import jat.alg.estimators.*;


public class createMeasurements{

	public static String []measurementTypes;
	public static VectorN measurementValues;
	public static int numMeasurementTypes;
	public static MeasurementModel [] mm;
	public static double [] frequency;
	public createMeasurements()
	{
		//Read in all of the required measurements
		HashMap hm = closedLoopSim.hm;
		numMeasurementTypes = initializer.parseInt(hm,"MEAS.types");
		measurementTypes = new String[numMeasurementTypes];
		mm = new MeasurementModel [numMeasurementTypes];
		frequency = new double[numMeasurementTypes];
		
		for(int i = 0;i<numMeasurementTypes;i++)
		{
			String meas = "MEAS."+i+".desc";
			measurementTypes[i] = initializer.parseString(hm,meas);
			String freq = "MEAS."+i+".frequency";
			frequency[i] = initializer.parseDouble(hm,freq);
			System.out.println(measurementTypes[i]);
			if(measurementTypes[i].equals("position"))
			{
				mm[i] = new stateMeasurementModel();
				//MeasurementModel pp = new stateMeasurementModel();
			}
			else if(measurementTypes[i].equals("range"))
			{
				mm[i] = new rangeMeasurementModel();
			}
			else if(measurementTypes[i].equals("GPS"))
			{
				mm[i] = new GPSmeasurementModel();
			}
			else if(measurementTypes[i].equals("pseudoGPS"))
			{
				mm[i] = new GPSStateMeasurementModel();
			}
			else if(measurementTypes[i].equals("stateUpdate"))
			{
				mm[i] = new stateUpdateMeasurementModel();
			}
			else
			{
				System.out.println("Invalid measurement type.");
				System.exit(1);
				
			}
		}
	}
	
	public static int getNumberMeasurements()
	{	
		/*For most measurement types, there will be only
		 * one measurement per epoch.  Caution must be
		 * taken for measurement types with multiple
		 * measurements (such as GPS)
		 */

		
		return numMeasurementTypes;
	}
	

	

}

