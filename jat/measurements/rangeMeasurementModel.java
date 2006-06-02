package jat.measurements;


import java.util.HashMap;

import jat.sim.closedLoopSim;
import jat.matvec.data.*;
import java.util.Random;
import jat.alg.estimators.*;
import jat.sim.*;

public class rangeMeasurementModel implements MeasurementModel{
	
	public static VectorN R;
	public static int numStates;
	HashMap hm = closedLoopSim.hm;
	Random generator;
	
	
	
	public rangeMeasurementModel() {
		
		/*Add a sleep in here to insure that the Random Number
		 * Seeds don't allign with any other random number generator
		 */
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		generator = new Random();
	}
	
	public double getMeasurement()
	{
		String tmp;
		double[] truth0 = closedLoopSim.truth[0].sc.get_spacecraft().toStateVector();
		double[] truth1 = closedLoopSim.truth[1].sc.get_spacecraft().toStateVector();

		double x2 = (truth0[0] - truth1[0])*(truth0[0] - truth1[0]);
		double y2 = (truth0[1] - truth1[1])*(truth0[1] - truth1[1]);
		double z2 = (truth0[2] - truth1[2])*(truth0[2] - truth1[2]);
		
		double range = Math.sqrt(x2 + y2 + z2);
		
		//Add in the measurement noise read out of the file
		tmp = "MEAS."+EKF.measNum+".R.0";
		double R = initializer.parseDouble(hm,tmp);
			
		/*Scale the error as Gaussian noise times the 
		 square of the measurement noise*/
		range += generator.nextGaussian()*R*R; 
			
		
		return range;
	}
	
	public double predictMeasurement(VectorN state){
		
		double x2 = (state.get(0) - state.get(6))*(state.get(0) - state.get(6));
		double y2 = (state.get(1) - state.get(7))*(state.get(1) - state.get(7));
		double z2 = (state.get(2) - state.get(8))*(state.get(2) - state.get(8));
		
		double range = Math.sqrt(x2 + y2 + z2);
		
		
		return range;
		
	}
	
	public double zPred(int i, double time, VectorN state){
		double oMinusC;
		double pred = predictMeasurement(state);
		double obs  = getMeasurement();
		oMinusC      = obs-pred;
		return oMinusC;
	}
	
	public double R()
	{
		
		String tmp = "MEAS."+EKF.measNum+".R."+0;
		double R = initializer.parseDouble(hm,tmp);
		return R;
	}
	
	public VectorN H(VectorN state)
	{
		/*NOTE:  Relative state are computed by differencing
		 		 the host (states 6 - 11) minus the local 
		 		 (states 0-5)  */
			
		
		/*Determine the number of states*/
		int numStates = initializer.parseInt(hm,"FILTER.states");
		VectorN H = new VectorN(numStates);
		
		double x2 = (state.get(0) - state.get(6))*(state.get(0) - state.get(6));
		double y2 = (state.get(1) - state.get(7))*(state.get(1) - state.get(7));
		double z2 = (state.get(2) - state.get(8))*(state.get(2) - state.get(8));
		
		double range = Math.sqrt(x2 + y2 + z2);
		
		H.set(0,(state.get(0)-state.get(6))/range);
		H.set(1,(state.get(1)-state.get(7))/range);
		H.set(2,(state.get(2)-state.get(8))/range);
		H.set(3,0.0);
		H.set(4,0.0);
		H.set(5,0.0);
		H.set(6,(state.get(6)-state.get(0))/range);
		H.set(7,(state.get(7)-state.get(1))/range);
		H.set(8,(state.get(8)-state.get(2))/range);
		H.set(9,0.0);
		H.set(10,0.0);
		H.set(11,0.0);
		H.set(12,0.0);
		H.set(13,0.0);
		H.set(14,0.0);
		return H;
	}

}