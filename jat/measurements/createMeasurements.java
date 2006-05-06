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
	
	public createMeasurements()
	{
		//Read in all of the required measurements
		HashMap hm = closedLoopSim.hm;
		numMeasurementTypes = initializer.parseInt(hm,"MEAS.types");
		measurementTypes = new String[numMeasurementTypes];
		mm = new MeasurementModel [numMeasurementTypes];
		
		for(int i = 0;i<numMeasurementTypes;i++)
		{
			String meas = "MEAS."+i+".desc";
			measurementTypes[i] = initializer.parseString(hm,meas);
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
				//mm[i] = new GPSMeasurementModel();
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
		return numMeasurementTypes;
	}
	

	

}

