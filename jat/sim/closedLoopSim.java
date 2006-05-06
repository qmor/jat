package jat.sim;

import jat.measurements.*;
import java.io.*;
import java.util.HashMap;

import java.io.FileOutputStream;
import java.io.IOException;
import jat.matvec.data.*;
import jat.util.FileUtil;
import jat.alg.estimators.*;

public class closedLoopSim {
	
	//Read in the initial conditions from the text file
	public static HashMap hm;
	
	private static int numSpacecraft;
	public static SimModel[] truth = null;
	public static SimModel[] ref   = null;
	private static FileOutputStream[] trajectories;
	private static FileOutputStream[] truths;
	private static FileOutputStream[] ECIError;
	private static FileOutputStream[] covariance;
	public static createMeasurements cm;
	private static int simStep;
	public static EKF filter;
	private static boolean gravityModel;
	private static int dt;
	private static VectorN newState;
	private static int numStates;
	private static double simTime;
	 

	public closedLoopSim()
	{
		
        String fs, dir_in;
        fs = FileUtil.file_separator();
        try{
            dir_in = FileUtil.getClassFilePath("jat.sim","closedLoopSim")+"input"+fs;
        }catch(Exception e){
            dir_in = "";
        }
		
		hm = initializer.parse_file(dir_in+"initialConditions.txt");
		numSpacecraft = initializer.parseInt(hm,"prop.NumSpacecraft");
		numStates = initializer.parseInt(hm,"FILTER.states");
		dt = initializer.parseInt(hm,"init.dt");
		truth = new SimModel[numSpacecraft]; 
		ref   = new SimModel[numSpacecraft];
		cm = new createMeasurements();
		filter = new EKF();
	}
	
	public synchronized void runLoop() throws InterruptedException{
		
		
		//Main loop for Filtering Routine.  This includes a Kalman filter 
		//and, trajectory generation, measurement generation as well as
		//guidance and control hooks.
		
		
		//Open files for saving off the generated data.  
		openFiles();
		
			
		//Initialize
		//This step should read in all the variables and set up all the required
		//models and functionality accordingly.  One should try to be careful
		//to flag any inconsistancies in the input data to save crashes later on.
		
		Initialize();
		
		
		/*Cache off the simulation mode */
		int filterMode = initializer.parseInt(hm,"init.mode");
		
		//Compute the length of the simulation in seconds
		double MJD0 =  initializer.parseDouble(hm,"init.MJD0");
		double MJDF =  initializer.parseDouble(hm,"init.MJDF");
		double T0   =  initializer.parseDouble(hm,"init.T0");
		double TF   =  initializer.parseDouble(hm,"init.TF");
			
		double simLength = Math.round((MJDF - MJD0)*86400 + TF - T0);
			
		for( simStep = 0; simStep < simLength/dt; simStep ++)
		{
			if(simStep%100 == 0)
				System.out.println(simStep*5);
			
			Propagate(simStep);
			simTime = (simStep+1)*dt;
			
			if(filterMode > 0)
			{
				Filter();
		
				//Guidance();
		
				//Control();
			}
		}
		
		/*Close all output files*/
		closeFiles();
	}
	
	public static void main(String[] args) throws InterruptedException {
		closedLoopSim  Sim = new closedLoopSim ();
		Sim.runLoop();
	}

	public static boolean[]  CreateForceFlag(int i){
		boolean[] force_flag = new boolean[6];
		
		/*Determine if only two body EOMS should be used*/
		if(initializer.parseBool(hm,"jat."+i+".2body"))
			force_flag[0]=true;
		else
			force_flag[0]=false;
		
		/*Determine if Solar gravity*/
		if(initializer.parseBool(hm,"jat."+i+".solar"))
			force_flag[1]=true;
		else
			force_flag[1]=false;
		
		/*Determine if Lunar Gravity is to be modeled*/
		if(initializer.parseBool(hm,"jat."+i+".lunar"))
			force_flag[2]=true;
		else
			force_flag[2]=false;
		
		/*Derermine if Drag is to be modeled*/ 
		if(initializer.parseBool(hm,"jat."+i+".drag"))
			force_flag[3]=true;
		else
			force_flag[3]=false;
		
		/*Determine if solar radiation pressure should be modeled*/
		if(initializer.parseBool(hm,"jat."+i+".srp"))
			force_flag[4]=true;
		else
			force_flag[4]=false;
		
		/*Determine which Gravity model to use*/
		if(initializer.parseBool(hm,"jat."+i+".jgm2"))
			gravityModel = true;
		else
			gravityModel = false;
		
		return force_flag;
	}
	
	private static void Initialize()
	{
		double[] r = new double[3];
		double[] tr = new double[3];  //variable for true trajectory
		double[] v = new double[3];
		double[] tv = new double[3];  //variable for true trajectory
		double cr,cd,area,mass,dt;
		
		
		//For each spacecraft extract the initial vector and force information
		//Use this information to create a sim model
		System.out.println("Propagating "+numSpacecraft+" Spacecraft");
		for(int i = 0;i < numSpacecraft; i++)
		{	
			/*Position*/
			String refs = "REF_STATE.";
			String tru = "TRUE_STATE.";
			
			String str  = refs+i+".X";
			String strt = tru+i+".X";
			r[0] = initializer.parseDouble(hm,str);
			tr[0] = initializer.parseDouble(hm,strt);
			
			str  = refs+i+".Y";
			strt = tru+i+".Y";
			r[1] = initializer.parseDouble(hm,str);
			tr[1] = initializer.parseDouble(hm,strt);
			
			str  = refs+i+".Z";
			strt = tru+i+".Z";
			r[2] = initializer.parseDouble(hm,str);
			tr[2] = initializer.parseDouble(hm,strt);
		
			/*Velocity*/
			str  = refs+i+".VX";
			strt = tru+i+".VX";
			v[0] = initializer.parseDouble(hm,str);
			tv[0] = initializer.parseDouble(hm,strt);
			
			str  = refs+i+".VY";
			strt = tru+i+".VY";
			v[1] = initializer.parseDouble(hm,str);
			tv[1] = initializer.parseDouble(hm,strt);
			
			str  = refs+i+".VZ";
			strt = tru+i+".VZ";
			v[2] = initializer.parseDouble(hm,str);
			tv[2] = initializer.parseDouble(hm,strt);
			
		
			/*Solar Radiation Pressure Coefficient*/
			str = "jat."+i+".Cr";
			cr   = initializer.parseDouble(hm,str);
		
			/*Drag Coefficient*/
			str = "jat."+i+".Cd";
			cd   = initializer.parseDouble(hm,str);
		
			/*Initial Mass*/
			str = "jat."+i+".mass";
			mass = initializer.parseDouble(hm,str);
		
			/*Initial Area*/
			str = "jat."+i+".area";
			area = initializer.parseDouble(hm,str);
			
			/*Read in the appropriate model flags*/
			boolean[] force_flag = CreateForceFlag(i); 
			ref[i] = new SimModel(r,v,cr,cd,area,mass);
			ref[i].initializeForces(force_flag, gravityModel, "HP");
			
			truth[i] = new SimModel(tr,tv,cr,cd,area,mass);
			truth[i].initializeForces(force_flag, gravityModel, "HP");
			
			
			/*Set the step size for the trajectory generation*/
			/*Set the integrator Step size*/
			dt = initializer.parseInt(hm,"init.dt");
			ref[i].set_stepsize(dt);
			truth[i].set_stepsize(dt);
		}		
	}
	
	private static void Propagate(double simStep)
	{
		/*In the propagation step we want to move the orbits of all the
		 satellites forward the desired timestep.  Output the states of 
		 each vehicle*/
		
		for(int numSats = 0; numSats < numSpacecraft; numSats ++)
		{
			//Step the trajectory forward the indicated amount
			
			truth[numSats].step(dt);
			
			//Extract the State Vector
			//Only need to propagate the reference state if 
			//We aren't estimating it
			if(initializer.parseDouble(hm,"init.mode") == 0)
			{
				double [] true_state = truth[numSats].sc.get_spacecraft().toStateVector();
				
				//Print out simStep + 1 because we didn't output the initial state
				VectorN vecTime =  new VectorN(1,(simStep+1)*dt);
				VectorN trueState = new VectorN(true_state);
				VectorN truthOut = new VectorN(vecTime,trueState);
				new PrintStream(truths[numSats]).println (truthOut.toString());
				
				
				ref[numSats].step(dt);
				double [] ref_state  = ref[numSats].sc.get_spacecraft().toStateVector();
				VectorN vecState = new VectorN(ref_state);
				VectorN stateOut = new VectorN(vecTime,vecState);
				new PrintStream(trajectories[numSats]).println (stateOut.toString());
			}

		
		}
		
	}
	
	
	private static void Filter()
	{
		
		/*Provide the algorithm to run the Kalman filter one step at
		a time.  This may need to be placed into another file later on*/
		
		
		/*Determine the num ber of measurements that we will be processing.
		 * Since this is a scalar update, we can simply loop over the 
		 * measurement update step that many times
		 */
		int numMeas = createMeasurements.getNumberMeasurements();
		
		
		/*Loop over the number of measurements being carefull to 
		 * omit measurements of 0
		 */
		
		
		for(int i = 0;i<numMeas;i++)
		{
		
			//Run the measurements through the EKF
			if(createMeasurements.measurementTypes[i].equals("position"))
			{
				/*this loop handles the case of the position measurement
				 * which is actually a vector measurement.  To keep the code
				 * in the form of scalar updates, the vector measurement is 
				 * broken into the appropriate number of scalar updates
				 */
				String tmp = "MEAS."+i+".size";
				for(int j = 0;j<initializer.parseInt(hm,tmp);j++)
				{
					 newState = filter.estimate(simTime,i,j);
				}
				
			}
			else
			{
				 newState = filter.estimate(simTime, i,0);
			}

			
			

		}
		
		//catch the case where there are no measurements
		if(numMeas == 0)
			 newState = filter.estimate(simTime, 0,0);
	
		
		//Update the current state with the output of the filter
		//Write the current state information to files
		
		double tmpState[] = new double[6];
		for(int numSats = 0; numSats < numSpacecraft; numSats ++)
		{
			//Extract the state of the current satellite
			for(int i = 0;i < 6; i++)
			{
				tmpState[i]=newState.x[numSats*6 + i];
			}
			ref[numSats].sc.get_spacecraft().updateMotion(tmpState);
		   
			//Write out the current True States
			double [] true_state = truth[numSats].sc.get_spacecraft().toStateVector();
			
			//Print out simStep + 1 because we didn't output the initial state
			VectorN vecTime =  new VectorN(1,(simStep+1)*dt);
			VectorN trueState = new VectorN(true_state);
			VectorN truthOut = new VectorN(vecTime,trueState);
			new PrintStream(truths[numSats]).println (truthOut.toString());
		
			//Write out the current State estimates
			double [] ref_state  = ref[numSats].sc.get_spacecraft().toStateVector();
			VectorN vecState = new VectorN(ref_state);
			VectorN stateOut = new VectorN(vecTime,vecState);
			new PrintStream(trajectories[numSats]).println (stateOut.toString());
		
			//Output the current ECI error
			VectorN error_out = new VectorN(6);
			for(int i = 0; i < 6; i++)
			{
				error_out.x[i] = true_state[i] - ref_state[i];
			}
			VectorN ErrState = new VectorN(error_out);
			stateOut = new VectorN(vecTime,ErrState);
			new PrintStream(ECIError[numSats]).println (stateOut.toString());
			
			//Output the current Covariances
			Matrix Covariance = EKF.pnew;
			double[] tmp = new double[6];
			for(int i = 0;i<6;i++)
			{
				//Extract only the covariance for the satellite of interest
				tmp[i] = Covariance.get(6*numSats + i,6*numSats + i);
			}
			VectorN ErrCov = new VectorN(tmp);
			stateOut = new VectorN(vecTime,ErrCov);
			new PrintStream(covariance[numSats]).println (stateOut.toString());
		}
		
	}
	
	private static void openFiles()
	{
		/*The number and types of files that are created are based
		 * upon the number of spacecraft and the simulation mode*/
		trajectories = new FileOutputStream [numSpacecraft];
		truths       = new FileOutputStream [numSpacecraft]; 
		ECIError     = new FileOutputStream [numSpacecraft];
		covariance   = new FileOutputStream [numSpacecraft];
        
		String fs, dir_in;
        fs = FileUtil.file_separator();
        try{
            dir_in = FileUtil.getClassFilePath("jat.sim","closedLoopSim")+"output"+fs;
        }catch(Exception e){
            dir_in = "";
        }
		
		
		for(int numSats = 0; numSats < numSpacecraft; numSats++)
		{
			try
			{
				// Open an output stream
				String fileName = dir_in+"Sat"+numSats+"ECI.txt";
				String fileName2 = dir_in+"TRUE"+numSats+"ECI.txt";
				String fileName3 = dir_in+"Error"+numSats+"ECI.txt";
				String fileName4 = dir_in+"Covariance"+numSats+"ECI.txt";
				trajectories[numSats] = new FileOutputStream (fileName);
				truths[numSats]       = new FileOutputStream (fileName2);
				ECIError[numSats]     = new FileOutputStream(fileName3);
				covariance[numSats]   = new FileOutputStream(fileName4);
			}
			catch (IOException e)
			{
				System.err.println ("Unable to write to file");
				System.exit(-1);
			}
		}
	}
		
		
		
	private static void closeFiles()
	{
		/*The number and types of files that are created are based
		 * upon the number of spacecraft and the simulation mode*/
		
		for(int numFiles = 0; numFiles < numSpacecraft; numFiles++)
		{
			{
			    // Close the files
				try 
				{
					trajectories[numFiles].close();
					truths[numFiles].close();
					ECIError[numFiles].close();
					covariance[numFiles].close();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}	
			EKF.residuals.close();
		}
	
	System.out.println("Closing files and exiting . . ");
	}
	
}