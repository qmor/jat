package jat.measurements;


import java.util.HashMap;

import jat.sim.*;
import jat.gps.Visible;
import jat.matvec.data.*;
import java.util.Random;
import jat.alg.estimators.*;

public class GPSStateMeasurementModel implements MeasurementModel{
	
	public static VectorN R;
	public static int numStates;
	HashMap hm = closedLoopSim.hm;
	Random generator;
	
	public GPSStateMeasurementModel() {
		/*Add a sleep in here to insure that the Random Number
		 * Seeds don't allign with any other random number generator
		 */
		try {
			Thread.sleep(20);
			generator = new Random();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	public VectorN getMeasurement()
	{
		String tmp;
		VectorN pointSolution = new VectorN(3);
		
		tmp = "MEAS."+EKF.measNum+".satellite";
		int sat = initializer.parseInt(hm,tmp);
		
		double[] truth = closedLoopSim.truth[sat].sc.get_spacecraft().toStateVector();
		
		//Add in the measurement noise read out of the file
		for(int j = 0; j < 3; j++)
		{
			tmp = "MEAS."+EKF.measNum+".R."+j;
			double R = initializer.parseDouble(hm,tmp);
			
			/*Scale the error as Gaussian noise times the 
			 square of the measurement noise*/
			pointSolution.x[j] =  truth[j] +generator.nextGaussian()*R*R; 
		}	
		return pointSolution;
	}
	
	public VectorN predictMeasurement(VectorN state){
		
		
		String tmp = "MEAS."+EKF.measNum+".satellite";
		int sat = initializer.parseInt(hm,tmp);
		
		VectorN range = new VectorN(3);
		range.set(0,state.get(0+(6*sat)));
		range.set(1,state.get(1+(6*sat)));
		range.set(2,state.get(2+(6*sat)));
		
		return range;
		
	}
	
	public double  zPred(int i, double time, VectorN state){
		String tmp = "MEAS."+EKF.measNum+".satellite";
		int sat = initializer.parseInt(hm,tmp);
		
		VectorN oMinusC;
		VectorN pred = predictMeasurement(state);
		VectorN obs  = getMeasurement();
		oMinusC      = obs.minus(pred);
		
		//Ensure we are returning the correct state when there is more than
		//one satellite
		int j = i - 6*sat; 
		return oMinusC.get(j);
	}
	
	/** Return the measurement noise value for this measurement
	 * 
	 *   
	 */
	public double R()
	{
		int whichState = EKF.stateNum;
		int measNum    = EKF.measNum;
		
		
		//Ensure we are returning the correct state when there is more than
		//one satellite
		String tmp = "MEAS."+EKF.measNum+".satellite";
		int sat = initializer.parseInt(hm,tmp);
		int j = whichState - 6*sat; 
		

		tmp = "MEAS."+measNum+".R."+j;
		double R = initializer.parseDouble(hm,tmp);
		return R;
	}
	
	public VectorN H(VectorN state)
	{
		/*Determine the number of states*/
		int whichState = EKF.stateNum;
		int numStates = initializer.parseInt(hm,"FILTER.states");
		
	
		/*for a Range measurement, the current state has H = 1, all other states H = 0 */
		VectorN H = new VectorN(numStates);
		H.set(0.0);
		H.set(whichState,1.0);
		return H;
	}

}