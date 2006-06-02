package jat.sim;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import jat.alg.estimators.EKF;
import jat.alg.integrators.Derivatives;
import jat.cm.Constants;
import jat.forces.GravitationalBody;
import jat.forces.GravityModel;
import jat.forces.GravityModelType;
import jat.forces.HarrisPriester;
import jat.forces.Moon;
import jat.forces.NRLMSISE_Drag;
import jat.forces.SolarRadiationPressure;
import jat.forces.Sun;
import jat.matvec.data.Matrix;
import jat.matvec.data.VectorN;
import jat.measurements.createMeasurements;
import jat.spacecraft.Spacecraft;
import jat.spacecraft.SpacecraftModel;
import jat.spacetime.UniverseModel;
import jat.traj.Trajectory;
import jat.util.FileUtil;

public class EstimatorSimModel extends SimModel {
	
	//** Static Variables **//
	
	private boolean gravityModel;
	
	//** Object Variables **//
	
	private SpacecraftModel truth[],ref[];
	private Trajectory truth_traj[], ref_traj[];
	private static int numSpacecraft;
	//public SimModel[] truth = null;
	//public SimModel[] ref   = null;
	private FileOutputStream[] trajectories;
	private FileOutputStream[] truths;
	private FileOutputStream[] ECIError;
	private FileOutputStream[] covariance;
	
	public createMeasurements cm;
	private int simStep;
	public EKF filter;
	private double dt;
	private VectorN newState;
	private int numStates;
	public double simTime;
	public int numVis;
	
	//** Constructors **//
	
	public EstimatorSimModel(){
		super();
		String fs, dir_in;
        fs = FileUtil.file_separator();
        try{
            dir_in = FileUtil.getClassFilePath("jat.sim","closedLoopSim")+"input"+fs;
        }catch(Exception e){
            dir_in = "";
        }
		
		closedLoopSim.hm = initializer.parse_file(dir_in+"initialConditions.txt");
		numSpacecraft = initializer.parseInt(closedLoopSim.hm,"prop.NumSpacecraft");
		numStates = initializer.parseInt(closedLoopSim.hm,"FILTER.states");
		dt = initializer.parseInt(closedLoopSim.hm,"init.dt");
		truth = new SpacecraftModel[numSpacecraft]; 
		ref   = new SpacecraftModel[numSpacecraft];
		cm = new createMeasurements();
		filter = new EKF();
	}
	public EstimatorSimModel(double[] r, double[] v, double cr, double cd, double area, double mass){
		super(r, v, cr, cd, area, mass);
		String fs, dir_in;
        fs = FileUtil.file_separator();
        try{
            dir_in = FileUtil.getClassFilePath("jat.sim","closedLoopSim")+"input"+fs;
        }catch(Exception e){
            dir_in = "";
        }
		
		closedLoopSim.hm = initializer.parse_file(dir_in+"initialConditions.txt");
		numSpacecraft = initializer.parseInt(closedLoopSim.hm,"prop.NumSpacecraft");
		numStates = initializer.parseInt(closedLoopSim.hm,"FILTER.states");
		dt = initializer.parseInt(closedLoopSim.hm,"init.dt");
		truth = new SpacecraftModel[numSpacecraft]; 
		ref   = new SpacecraftModel[numSpacecraft];
		cm = new createMeasurements();
		filter = new EKF();
	}
	public EstimatorSimModel(double[][] r, double[][] v, double[] cr, double[] cd,
			double[] area, double[] mass){
		super(r, v, cr,cd,area, mass);
		String fs, dir_in;
        fs = FileUtil.file_separator();
        try{
            dir_in = FileUtil.getClassFilePath("jat.sim","closedLoopSim")+"input"+fs;
        }catch(Exception e){
            dir_in = "";
        }
		
		closedLoopSim.hm = initializer.parse_file(dir_in+"initialConditions.txt");
		numSpacecraft = initializer.parseInt(closedLoopSim.hm,"prop.NumSpacecraft");
		numStates = initializer.parseInt(closedLoopSim.hm,"FILTER.states");
		dt = initializer.parseInt(closedLoopSim.hm,"init.dt");
		truth = new SpacecraftModel[numSpacecraft]; 
		ref   = new SpacecraftModel[numSpacecraft];
		cm = new createMeasurements();
		filter = new EKF();
	}
	
	//** Object Methods **//
	
	private void initialize()
	{
		double[] r = new double[3];
		double[] tr = new double[3];  //variable for true trajectory
		double[] v = new double[3];
		double[] tv = new double[3];  //variable for true trajectory
		double cr,cd,area,mass,dt;
		
		double MJD0 =  initializer.parseDouble(closedLoopSim.hm,"init.MJD0");
		
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
			r[0] = initializer.parseDouble(closedLoopSim.hm,str);
			tr[0] = initializer.parseDouble(closedLoopSim.hm,strt);
			
			str  = refs+i+".Y";
			strt = tru+i+".Y";
			r[1] = initializer.parseDouble(closedLoopSim.hm,str);
			tr[1] = initializer.parseDouble(closedLoopSim.hm,strt);
			
			str  = refs+i+".Z";
			strt = tru+i+".Z";
			r[2] = initializer.parseDouble(closedLoopSim.hm,str);
			tr[2] = initializer.parseDouble(closedLoopSim.hm,strt);
			
			/*Velocity*/
			str  = refs+i+".VX";
			strt = tru+i+".VX";
			v[0] = initializer.parseDouble(closedLoopSim.hm,str);
			tv[0] = initializer.parseDouble(closedLoopSim.hm,strt);
			
			str  = refs+i+".VY";
			strt = tru+i+".VY";
			v[1] = initializer.parseDouble(closedLoopSim.hm,str);
			tv[1] = initializer.parseDouble(closedLoopSim.hm,strt);
			
			str  = refs+i+".VZ";
			strt = tru+i+".VZ";
			v[2] = initializer.parseDouble(closedLoopSim.hm,str);
			tv[2] = initializer.parseDouble(closedLoopSim.hm,strt);
			
			
			/*Solar Radiation Pressure Coefficient*/
			str = "jat."+i+".Cr";
			cr   = initializer.parseDouble(closedLoopSim.hm,str);
			
			/*Drag Coefficient*/
			str = "jat."+i+".Cd";
			cd   = initializer.parseDouble(closedLoopSim.hm,str);
			
			/*Initial Mass*/
			str = "jat."+i+".mass";
			mass = initializer.parseDouble(closedLoopSim.hm,str);
			
			/*Initial Area*/
			str = "jat."+i+".area";
			area = initializer.parseDouble(closedLoopSim.hm,str);
			
			/*Read in the appropriate model flags*/
			boolean[] force_flag = CreateForceFlag(i); 
			VectorN rr = new VectorN(r);
			VectorN vv = new VectorN(v);
			Spacecraft s = new Spacecraft(rr,vv,cr,cd,area,mass);
			s.set_use_params_in_state(false);
			ref[i] = new SpacecraftModel(s,createUniverseModel(MJD0,s,force_flag, gravityModel, "HP"));
			
			rr = new VectorN(tr);
			vv = new VectorN(tv);
			s = new Spacecraft(rr,vv,cr,cd,area,mass);
			s.set_use_params_in_state(false);
			truth[i] = new SpacecraftModel(s,createUniverseModel(MJD0,s,force_flag, gravityModel, "HP"));;
			
			
			/*Set the step size for the trajectory generation*/
			/*Set the integrator Step size*/
			dt = initializer.parseInt(closedLoopSim.hm,"init.dt");
			//ref[i].set_sc_dt(dt);
			//truth[i].set_sc_dt(dt);
		}		
	}
	
	public static UniverseModel createUniverseModel(double mjd_utc,Spacecraft sc, boolean[] force_flag, boolean use_JGM2, String drag_model){
		
		UniverseModel umodel = new UniverseModel(mjd_utc);
		//ForceModelList forces = new ForceModelList();
		VectorN zero = new VectorN(0,0,0);
		if(force_flag[0]){
			System.out.println("Earth");
			GravitationalBody earth =
				new GravitationalBody(398600.4415e+9);
			umodel.addForce(earth);
		} else {
			if(use_JGM2){
				System.out.println("JGM2");
				GravityModel earth_grav = new GravityModel(2,2,GravityModelType.JGM2);
				umodel.addForce(earth_grav);
				umodel.set_use_iers(true);
			}else{
				System.out.println("JGM3");
				GravityModel earth_grav = new GravityModel(20,20,GravityModelType.JGM3);
				umodel.addForce(earth_grav);
				umodel.set_use_iers(true);
			}
			
		}
		if(force_flag[1]){
			System.out.println("Sun");
			umodel.set_compute_sun(true);
			Sun sun =
				new Sun(Constants.GM_Sun,zero,zero);
			umodel.addForce(sun);
		}
		if(force_flag[2]){
			System.out.println("Moon");
			umodel.set_compute_moon(true);
			Moon moon =
				new Moon(Constants.GM_Moon,zero,zero);
			umodel.addForce(moon);
		}
		if(force_flag[3]){
			double ap_opt = 14.918648166;
			double f107_opt = 150;
			double n_param_opt = 6;
			umodel.set_compute_sun(true);
			if(drag_model.endsWith("NRL") || drag_model.endsWith("A") || drag_model.endsWith("C")){
				System.out.println("NRLMSISE");
				NRLMSISE_Drag drag = new NRLMSISE_Drag(sc);
				drag.ap_opt = ap_opt;
				drag.f107_opt = f107_opt;
				umodel.addForce(drag);
				umodel.set_use_iers(true);
			}else{
				umodel.set_compute_sun(true);
				System.out.println("HarrisPriester");
				HarrisPriester atmos = new HarrisPriester(sc,150);//145.8480085177176);
				//atmos.setF107(145.8480085177176);//148.715);//99.5);
				atmos.setParameter(n_param_opt);
//				if(drag_model.equalsIgnoreCase("Sun-Sync"))
//				atmos.setParameter(6);
//				else if(drag_model.equalsIgnoreCase("ISS"))
//				atmos.setParameter(4);
				umodel.addForce(atmos);
				umodel.set_use_iers(true);
				
			}
		}
		if(force_flag[4]){
			umodel.set_compute_sun(true);
			System.out.println("SolarRadiationPressure");
			SolarRadiationPressure srp = new SolarRadiationPressure(sc);
			umodel.addForce(srp);
		}
		return umodel;
	}
	
	/**
	 * Propagation method for an individual spacecraft.  Increments the 
	 * model held in the spacecraft flight computer by a time 'dt'.
	 * Updates the computer's models according to the progression of time.
	 * Updates the spacecraft state.
	 * @param dt Timestep in seconds
	 */
	public void step(SpacecraftModel sm){
		double t = sm.get_sc_t();
		if(verbose_timestep){
			System.out.println("step: "+t+" / "+tf+"    stepsize: "+dt);
		}
		
		//rk8.setStepSize(sm.get_sc_dt());
		rk8.setStepSize(dt);
		//* update models
		//double mjd_utc = spacetime.get_mjd_utc();
		double[] X = new double[6];
		double[] Xnew = new double[6];
		double[] thrust = new double[3];
		VectorN rnew;
		VectorN vnew;
		double[] tmp = new double[6];
		double num_sc = 1;
		for(int i=0; i<num_sc; i++){
			
			Spacecraft s = sm.get_spacecraft();
			X = s.toStateVector(false);
			Xnew = rk8.step(t, X, sm);
			//* store new values
			//rnew = new VectorN(Xnew[0],Xnew[1],Xnew[2]);
			//vnew = new VectorN(Xnew[3],Xnew[4],Xnew[5]);
			s.updateState(Xnew,false);
		}
		//* update simulation time
//		if(t > (tf-sm.get_sc_dt()) && t != tf){
//			sm.set_sc_dt(tf-t);
//		}
//		t=t+sm.get_sc_dt();
		if(t > (tf-dt) && t != tf){
			dt=(tf-t);
		}
		t=t+dt;
		
		//* update the universe
		sm.update(t);
		iteration++;
	}
	
	public boolean[]  CreateForceFlag(int i){
		boolean[] force_flag = new boolean[6];
		
		/*Determine if only two body EOMS should be used*/
		if(initializer.parseBool(closedLoopSim.hm,"jat."+i+".2body"))
			force_flag[0]=true;
		else
			force_flag[0]=false;
		
		/*Determine if Solar gravity*/
		if(initializer.parseBool(closedLoopSim.hm,"jat."+i+".solar"))
			force_flag[1]=true;
		else
			force_flag[1]=false;
		
		/*Determine if Lunar Gravity is to be modeled*/
		if(initializer.parseBool(closedLoopSim.hm,"jat."+i+".lunar"))
			force_flag[2]=true;
		else
			force_flag[2]=false;
		
		/*Derermine if Drag is to be modeled*/ 
		if(initializer.parseBool(closedLoopSim.hm,"jat."+i+".drag"))
			force_flag[3]=true;
		else
			force_flag[3]=false;
		
		/*Determine if solar radiation pressure should be modeled*/
		if(initializer.parseBool(closedLoopSim.hm,"jat."+i+".srp"))
			force_flag[4]=true;
		else
			force_flag[4]=false;
		
		/*Determine which Gravity model to use*/
		if(initializer.parseBool(closedLoopSim.hm,"jat."+i+".jgm2"))
			gravityModel = true;
		else
			gravityModel = false;
		
		return force_flag;
	}
	
	private void propagate(double simStep)
	{
		/*In the propagation step we want to move the orbits of all the
		 satellites forward the desired timestep.  Output the states of 
		 each vehicle*/
		
		for(int numSats = 0; numSats < numSpacecraft; numSats ++)
		{
			//Step the trajectory forward the indicated amount
			
			step(truth[numSats]);
			
			//Extract the State Vector
			//Only need to propagate the reference state if 
			//We aren't estimating it
			if(initializer.parseDouble(closedLoopSim.hm,"init.mode") == 0)
			{
				double [] true_state = truth[numSats].get_spacecraft().toStateVector();
				
				//Print out simStep + 1 because we didn't output the initial state
				VectorN vecTime =  new VectorN(1,(simStep+1)*dt);
				VectorN trueState = new VectorN(true_state);
				VectorN truthOut = new VectorN(vecTime,trueState);
				new PrintStream(truths[numSats]).println (truthOut.toString());
				
				
				step(ref[numSats]);
				double [] ref_state  = ref[numSats].get_spacecraft().toStateVector();
				VectorN vecState = new VectorN(ref_state);
				VectorN stateOut = new VectorN(vecTime,vecState);
				new PrintStream(trajectories[numSats]).println (stateOut.toString());
			}
			
			
		}
		
	}
	
	
	private void filter()
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
		
		int processedMeasurements = 0;
		for(int i = 0;i<numMeas;i++)
		{
			
			if(simTime%createMeasurements.frequency[i] ==0 )
			{
				//Run the measurements through the EKF
				/*				if(createMeasurements.measurementTypes[i].equals("position"))
				 {
				 this loop handles the case of the position measurement
				 * which is actually a vector measurement.  To keep the code
				 * in the form of scalar updates, the vector measurement is 
				 * broken into the appropriate number of scalar updates
				 
				 System.out.println("Processing STATE Updateat time: " + simTime);
				 String tmp = "MEAS."+i+".size";
				 for(int j = 0;j<initializer.parseInt(hm,tmp);j++)
				 {
				 newState = filter.estimate(simTime,i,j);
				 processedMeasurements ++;
				 }
				 
				 }*/
				if(createMeasurements.measurementTypes[i].equals("GPS"))
				{
					/*this loop spins through all the GPS satellites.
					 *Ranges near zero will be skipped in the filter
					 */
					numVis = 0;
					for(int j = 0; j<26;j++ )
					{
						newState = filter.estimate(simTime,i,j,true);
						processedMeasurements ++;
					}
				}
				else
				{
					String tmp = "MEAS."+i+".satellite";
					int sat = initializer.parseInt(closedLoopSim.hm,tmp);
					
					tmp = "MEAS."+i+".size";
					for(int j = 0;j<initializer.parseInt(closedLoopSim.hm,tmp);j++)
					{	
						newState = filter.estimate(simTime,i,j+(6*sat),true);
						processedMeasurements ++;
						System.out.println("Processing Measurement at time: " + simTime);
					}
				}
			}
			
			
			
		}
		
		//catch the case where there are no measurements, set the measurement
		//flag to false to tell the filter to just propagate
		if(processedMeasurements  == 0)
			newState = filter.estimate(simTime, 0,0,false);
		
		
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
			ref[numSats].get_spacecraft().updateMotion(tmpState);
			
			//Write out the current True States
			double [] true_state = truth[numSats].get_spacecraft().toStateVector();
			
			//Print out simStep + 1 because we didn't output the initial state
			VectorN vecTime =  new VectorN(1,(simStep+1)*dt);
			VectorN trueState = new VectorN(true_state);
			VectorN truthOut = new VectorN(vecTime,trueState);
			new PrintStream(truths[numSats]).println (truthOut.toString());
			
			//Write out the current State estimates
			double [] ref_state  = ref[numSats].get_spacecraft().toStateVector();
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
			
//			Output the current Covariances
			Matrix Covariance = EKF.pold;
			double[] tmp = new double[numStates*numStates];
			int k = 0;
			for(int i = 0; i < numStates; i++)
			{
				for(int j = 0; j < numStates; j++)
				{
					tmp[k] = Covariance.get(i,j);
					k++;
				}
			}
			VectorN ErrCov = new VectorN(tmp);
			stateOut = new VectorN(vecTime,ErrCov);
			new PrintStream(covariance[numSats]).println (stateOut.toString());
			
			//Output the Number of Visible Satellites
			//stateOut =  new VectorN(2);
			//stateOut.set(0,(simStep+1)*dt);
			//stateOut.set(1,(double)numVis);
			//System.out.println("Number of visible Satellites   " + numVis);
			
		}
		
	}
	
	public void runloop(){
		//Main loop for Filtering Routine.  This includes a Kalman filter 
		//and, trajectory generation, measurement generation as well as
		//guidance and control hooks.
		
		
		//Open files for saving off the generated data.  
		openFiles();
		
		
		//Initialize
		//This step should read in all the variables and set up all the required
		//models and functionality accordingly.  One should try to be careful
		//to flag any inconsistancies in the input data to save crashes later on.
		
		initialize();
		
		
		/*Cache off the simulation mode */
		int filterMode = initializer.parseInt(closedLoopSim.hm,"init.mode");
		
		//Compute the length of the simulation in seconds
		double MJD0 =  initializer.parseDouble(closedLoopSim.hm,"init.MJD0");
		double MJDF =  initializer.parseDouble(closedLoopSim.hm,"init.MJDF");
		double T0   =  initializer.parseDouble(closedLoopSim.hm,"init.T0");
		double TF   =  initializer.parseDouble(closedLoopSim.hm,"init.TF");
		simTime = 0;
		double simLength = Math.round((MJDF - MJD0)*86400 + TF - T0);
		
		for( simStep = 1; simStep < simLength/dt; simStep ++)
		{
			//if(simStep%100 == 0)
			//	System.out.println(simStep*5);
			
			propagate(simStep*dt);
			simTime = simStep*dt;
			
			System.out.println("SimTime: " + simTime + " SimStep: " + simStep);
			
			if(filterMode > 0)
			{
				filter();
				
				//Guidance();
				
				//Control();
			}
		}
		
		/*Close all output files*/
		closeFiles();
	}
	
	private void openFiles()
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
		
		//String fileName5 = dir_in+"Visible.txt";
		/*	try {
		 visableSats = new FileOutputStream(fileName5);
		 } catch (FileNotFoundException e1) {
		 // TODO Auto-generated catch block
		  e1.printStackTrace();
		  }*/
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
	private void closeFiles()
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
			/*try {
			 //visableSats.close();
			  } catch (IOException e) {
			  // TODO Auto-generated catch block
			   e.printStackTrace();
			   }*/
		}
		
		System.out.println("Closing files and exiting . . ");
	}
	
//	** Main **//
	
	public static void main(String[] args) {
		EstimatorSimModel Sim = new EstimatorSimModel ();
		Sim.runloop();
	}
}
