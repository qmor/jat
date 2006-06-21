/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2003 The JAT Project. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can 
 * redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software 
 * Foundation; either version 2 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * 
 * Emergent Space Technologies
 * Created by Richard C. Page III
 * */
package jat.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.StringTokenizer;

import jat.alg.estimators.EKF;
import jat.alg.integrators.Derivatives;
import jat.alg.integrators.LinePrinter;
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
import jat.measurements.ObservationMeasurement;
import jat.measurements.ObservationMeasurementList;
import jat.measurements.createMeasurements;
import jat.spacecraft.Spacecraft;
import jat.spacecraft.SpacecraftModel;
import jat.spacetime.Time;
import jat.spacetime.TimeUtils;
import jat.spacetime.UniverseModel;
import jat.traj.RelativeTraj;
import jat.traj.Trajectory;
import jat.util.Celestia;
import jat.util.FileUtil;

public class EstimatorSimModel extends SimModel {
	
	//** Static Variables **//
	
	//* TODO Cheating
	//** Externally referenced Static Variables **//
	public static SpacecraftModel truth[];
	public static int JAT_case = 0;
	public static String MEAS_GPSSTATE,MEAS_GPS;
	public static String GEONS_Truth,GEONS_Ref;
	public static boolean PlotJAT = false;
	public static boolean PlotGEONSTruth = false;
	public static boolean PlotGEONSRef = false;
	public static boolean PlotMeasurements = false;
	public static boolean Flag_GPS = false;
	public static boolean Flag_GPSState = false;
	public static boolean Flag_Cross = false;
	
	//** Object Variables **//
	
	private boolean gravityModel;
	private HashMap input;
	private createMeasurements created_meas;
	private ObservationMeasurementList obs_list;
	
	//private SpacecraftModel truth[];
	private SpacecraftModel ref[];
	private Trajectory truth_traj[], ref_traj[];
	private static int numSpacecraft;
	//public SimModel[] truth = null;
	//public SimModel[] ref   = null;
	private FileOutputStream[] trajectories;
	private FileOutputStream[] truths;
	private FileOutputStream[] ECIError;
	private FileOutputStream[] covariance;
	
	private int simStep;
	public EKF filter;
	private double dt;
	private VectorN newState;
	private int numStates;
	//public double simTime;
	private Time simTime;
	public int numVis;

	private boolean verbose_estimation=false;
	private boolean useFilter=true;
	private boolean obsFromFile=false;
	
	//** Constructors **//
	
	public EstimatorSimModel(){
		super();
		initializeConst();
		obsFromFile = false;
	}
	public EstimatorSimModel(boolean filter_is_on){
		super();
		initializeConst();
		useFilter = filter_is_on;
		
	}
	public EstimatorSimModel(double[] r, double[] v, double cr, double cd, double area, double mass){
		super(r, v, cr, cd, area, mass);
		initializeConst();
		obsFromFile = false;
	}
	public EstimatorSimModel(double[][] r, double[][] v, double[] cr, double[] cd,
			double[] area, double[] mass){
		super(r, v, cr,cd,area, mass);
		initializeConst();
		obsFromFile = false;
	}
	
	//** Object Methods **//
	private void initializeConst(){
		String fs, dir_in;
		fs = FileUtil.file_separator();
		try{
			dir_in = FileUtil.getClassFilePath("jat.sim","SimModel")+"input"+fs;
		}catch(Exception e){
			dir_in = "";
		}
		this.input = initializer.parse_file(dir_in+"initialConditions.txt");
		this.obsFromFile = initializer.parseBool(this.input,"init.fromfile");
		if(obsFromFile){
			obs_list = new ObservationMeasurementList(this.input);
			String path = FileUtil.getClassFilePath("jat.measurements","ObservationMeasurementList");
			if(useFilter){
				try {
					//obs_list.processRINEX(path+fs+"ExampleRINEXGEONS.rnx");
//					if(Flag_GPS || Flag_Cross) 
//						obs_list.processRINEX(path+fs+"Case-820.rnx");
//					if(Flag_GPSState) 
//						obs_list.processStateUpdateFile(path+fs+"test1_8.rnx");
					if(Flag_GPS || Flag_Cross) 
						obs_list.processRINEX(MEAS_GPS);
					if(Flag_GPSState) 
						obs_list.processStateUpdateFile(MEAS_GPSSTATE);
				} catch (IOException e) {
					System.out.println("Error finding Observation RINEX file: "+path+fs+"ExampleRINEXGEONS.rnx");
					e.printStackTrace();
				}
			}
			numSpacecraft = initializer.parseInt(input,"prop.NumSpacecraft");
			numStates = initializer.parseInt(input,"FILTER.states");
			dt = initializer.parseInt(input,"init.dt");
			truth = new SpacecraftModel[numSpacecraft]; 
			ref   = new SpacecraftModel[numSpacecraft];
			truth_traj = new Trajectory[numSpacecraft];
			ref_traj = new Trajectory[numSpacecraft];
			for(int i=0; i<numSpacecraft; i++){
				truth_traj[i] = new Trajectory();
				ref_traj[i] = new Trajectory();
			}
			filter = new EKF(obs_list,input);
		}else{
			
			numSpacecraft = initializer.parseInt(input,"prop.NumSpacecraft");
			numStates = initializer.parseInt(input,"FILTER.states");
			dt = initializer.parseInt(input,"init.dt");
			truth = new SpacecraftModel[numSpacecraft]; 
			ref   = new SpacecraftModel[numSpacecraft];
			truth_traj = new Trajectory[numSpacecraft];
			ref_traj = new Trajectory[numSpacecraft];
			for(int i=0; i<numSpacecraft; i++){
				truth_traj[i] = new Trajectory();
				ref_traj[i] = new Trajectory();
			}
			created_meas = new createMeasurements(input);
			filter = new EKF(input);
		}
	}
	private void initialize()
	{
		double[] r = new double[3];
		double[] tr = new double[3];  //variable for true trajectory
		double[] v = new double[3];
		double[] tv = new double[3];  //variable for true trajectory
		double cr,cd,area,mass,dt;
		
		double MJD0 =  initializer.parseDouble(input,"init.MJD0");
		
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
			r[0] = initializer.parseDouble(this.input,str);
			tr[0] = initializer.parseDouble(this.input,strt);
			
			str  = refs+i+".Y";
			strt = tru+i+".Y";
			r[1] = initializer.parseDouble(this.input,str);
			tr[1] = initializer.parseDouble(this.input,strt);
			
			str  = refs+i+".Z";
			strt = tru+i+".Z";
			r[2] = initializer.parseDouble(this.input,str);
			tr[2] = initializer.parseDouble(this.input,strt);
			
			/*Velocity*/
			str  = refs+i+".VX";
			strt = tru+i+".VX";
			v[0] = initializer.parseDouble(this.input,str);
			tv[0] = initializer.parseDouble(this.input,strt);
			
			str  = refs+i+".VY";
			strt = tru+i+".VY";
			v[1] = initializer.parseDouble(this.input,str);
			tv[1] = initializer.parseDouble(this.input,strt);
			
			str  = refs+i+".VZ";
			strt = tru+i+".VZ";
			v[2] = initializer.parseDouble(this.input,str);
			tv[2] = initializer.parseDouble(this.input,strt);
			
			
			/*Solar Radiation Pressure Coefficient*/
			str = "jat."+i+".Cr";
			cr   = initializer.parseDouble(this.input,str);
			
			/*Drag Coefficient*/
			str = "jat."+i+".Cd";
			cd   = initializer.parseDouble(this.input,str);
			
			/*Initial Mass*/
			str = "jat."+i+".mass";
			mass = initializer.parseDouble(this.input,str);
			
			/*Initial Area*/
			str = "jat."+i+".area";
			area = initializer.parseDouble(this.input,str);
						
			/*Read in the appropriate model flags*/
			boolean[] force_flag = createForceFlag(i); 
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
			dt = initializer.parseInt(this.input,"init.dt");
			//ref[i].set_sc_dt(dt);
			//truth[i].set_sc_dt(dt);
			
			/* Read in the value for the spacecraft receiver noise */
			double range_noise=0;
			double state_noise[] = new double[3];
			str = "MEAS.types";
			String str2;
			int numMeas = initializer.parseInt(this.input,str);
			for(int m=0; m<numMeas; m++){
				str = "MEAS."+m+".desc";
				str2 = initializer.parseString(this.input,str);
				if(str2.equalsIgnoreCase("GPS")){
					str = "MEAS."+m+".R";
					range_noise = initializer.parseDouble(input,str);					
				}else if(str2.equalsIgnoreCase("pseudoGPS")){
					int end = initializer.parseInt(input,"MEAS."+m+".size");
					state_noise = new double[end];
					for(int snum=0; snum<end; snum++){
						str = "MEAS."+m+".R."+snum;
						state_noise[snum]=initializer.parseDouble(input,str);
					}						
				}
			}
			ref[i].set_GPS_noise(state_noise,range_noise);
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
	 * 
	 */
	public void step(SpacecraftModel sm, Trajectory traj){
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
//		sm.set_sc_dt(tf-t);
//		}
//		t=t+sm.get_sc_dt();
		if(t > (tf-dt) && t != tf){
			dt=(tf-t);
		}
		t=t+dt;
		//* add to trajectory
		traj.add(sm.get_sc_mjd_utc(),sm.get_spacecraft().toStateVector());
		//* update the universe
		sm.update(t);
		iteration++;
	}
	/**
	 * Propagation method for an individual spacecraft.  Increments the 
	 * model held in the spacecraft flight computer by a time 'dt'.
	 * Updates the computer's models according to the progression of time.
	 * Updates the spacecraft state.
	 * 
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
//		sm.set_sc_dt(tf-t);
//		}
//		t=t+sm.get_sc_dt();
		if(t > (tf-dt) && t != tf){
			dt=(tf-t);
		}
		t=t+dt;
		//* add to trajectory
		//traj.add(sm.get_sc_mjd_utc(),sm.get_spacecraft().toStateVector());
		//* update the universe
		sm.update(t);
		iteration++;
	}
	public boolean[]  createForceFlag(int i){
		boolean[] force_flag = new boolean[6];
		
		/*Determine if only two body EOMS should be used*/
		if(initializer.parseBool(this.input,"jat."+i+".2body"))
			force_flag[0]=true;
		else
			force_flag[0]=false;
		
		/*Determine if Solar gravity*/
		if(initializer.parseBool(this.input,"jat."+i+".solar"))
			force_flag[1]=true;
		else
			force_flag[1]=false;
		
		/*Determine if Lunar Gravity is to be modeled*/
		if(initializer.parseBool(this.input,"jat."+i+".lunar"))
			force_flag[2]=true;
		else
			force_flag[2]=false;
		
		/*Derermine if Drag is to be modeled*/ 
		if(initializer.parseBool(this.input,"jat."+i+".drag"))
			force_flag[3]=true;
		else
			force_flag[3]=false;
		
		/*Determine if solar radiation pressure should be modeled*/
		if(initializer.parseBool(this.input,"jat."+i+".srp"))
			force_flag[4]=true;
		else
			force_flag[4]=false;
		
		/*Determine which Gravity model to use*/
		if(initializer.parseBool(this.input,"jat."+i+".jgm2"))
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
			
			//step(truth[numSats],truth_traj[numSats]);
			step(truth[numSats]);
			
			//Extract the State Vector
			//Only need to propagate the reference state if 
			//We aren't estimating it
			//* NOTE * originally == 0  ???
			//* TODO Add flag to avoid running the 'truth' trajectory
			if(initializer.parseDouble(this.input,"init.mode") == 0)
			{
				double [] true_state = truth[numSats].get_spacecraft().toStateVector();
				
				//Print out simStep + 1 because we didn't output the initial state
				VectorN vecTime =  new VectorN(1,(simStep)*dt);
				VectorN trueState = new VectorN(true_state);
				VectorN truthOut = new VectorN(vecTime,trueState);
				new PrintStream(truths[numSats]).println (truthOut.toString());
				
				
				//step(ref[numSats],ref_traj[numSats]);
				step(ref[numSats]);
				double [] ref_state  = ref[numSats].get_spacecraft().toStateVector();
				VectorN vecState = new VectorN(ref_state);
				VectorN stateOut = new VectorN(vecTime,vecState);
				new PrintStream(trajectories[numSats]).println (stateOut.toString());
			}
			
		}
		
	}
	
	private void filter_fromfile(){
		/*Provide the algorithm to run the Kalman filter one step at
		 a time.  This may need to be placed into another file later on*/
		
		
		/*Determine the num ber of measurements that we will be processing.
		 * Since this is a scalar update, we can simply loop over the 
		 * measurement update step that many times
		 */
		//int numMeas = created_meas.getNumberMeasurements();
		
		
		/*Loop over the number of measurements being carefull to 
		 * omit measurements of 0
		 */
		
		int processedMeasurements = 0;
		
		//Update the current state with the output of the filter
		//Write the current state information to files
		
		double tmpState[] = new double[6];
		for(int numSats = 0; numSats < numSpacecraft; numSats ++)
		{

			newState = filter.estimate(simTime,ref[numSats],obs_list,this.useFilter);
			processedMeasurements++;

//			if(Double.isNaN(newState.x[0])){
//				int donothing = 0;
//			}
			
			//Extract the state of the current satellite
			for(int i = 0;i < 6; i++)
			{
				tmpState[i]=newState.x[numSats*6 + i];
			}
			ref[numSats].get_spacecraft().updateMotion(tmpState);
			ref[numSats].update(simTime.get_sim_time());
			
			//Write out the current True States
			double [] true_state = truth[numSats].get_spacecraft().toStateVector();
			
			//Print out simStep + 1 because we didn't output the initial state
			VectorN vecTime =  new VectorN(1,(simStep)*dt);
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
			new PrintStream(ECIError[numSats]).println(stateOut.toString());
			
//			Output the current Covariances
			//Matrix Covariance = EKF.pold;
			Matrix Covariance = filter.get_pold();
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
	
	/**
	 * 
	 */
	private void filter()
	{
		
		/*Provide the algorithm to run the Kalman filter one step at
		 a time.  This may need to be placed into another file later on*/
		
		
		/*Determine the num ber of measurements that we will be processing.
		 * Since this is a scalar update, we can simply loop over the 
		 * measurement update step that many times
		 */
		int numMeas = created_meas.getNumberMeasurements(); 
		
		/*Loop over the number of measurements being carefull to 
		 * omit measurements of 0
		 */
		
		int processedMeasurements = 0;
		for(int i = 0;i<numMeas;i++)
		{
			
			if(simTime.get_sim_time()%(created_meas.frequency[i]) ==0 )
			{
				//Run the measurements through the EKF
				/*				if(createMeasurements.measurementTypes[i].equals("position"))
				 {
				 this loop handles the case of the position measurement
				 * which is actually a vector measurement.  To keep the code
				 * in the form of scalar updates, the vector measurement is 
				 * broken into the appropriate number of scalar updates
				 
				 System.out.println("Processing STATE Updateat time: " + simTime.get_sim_time());
				 String tmp = "MEAS."+i+".size";
				 for(int j = 0;j<initializer.parseInt(input,tmp);j++)
				 {
				 newState = filter.estimate(simTime.get_sim_time(),i,j);
				 processedMeasurements ++;
				 }
				 
				 }*/
				if(created_meas.measurementTypes[i].equals("GPS"))
				{
					/*this loop spins through all the GPS satellites.
					 *Ranges near zero will be skipped in the filter
					 */
					numVis = 0;
					for(int j = 0; j<26;j++ )
					{
						newState = filter.estimate(simTime.get_sim_time(),i,j,true);
						processedMeasurements ++;
					}
				}
				else
				{
					String tmp = "MEAS."+i+".satellite";
					int sat = initializer.parseInt(this.input,tmp);
					
					tmp = "MEAS."+i+".size";
					for(int j = 0;j<initializer.parseInt(this.input,tmp);j++)
					{	
						newState = filter.estimate(simTime.get_sim_time(),i,j+(6*sat),this.obsFromFile);
						processedMeasurements ++;
						//System.out.println("Processing Measurement at time: " + simTime.get_sim_time());
					}
				}
			}	
			
		}
		
		//catch the case where there are no measurements, set the measurement
		//flag to false to tell the filter to just propagate
		if(processedMeasurements  == 0)
			newState = filter.estimate(simTime.get_sim_time(), 0,0,false);
		
		
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
			VectorN vecTime =  new VectorN(1,(simStep)*dt);
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
//			Matrix Covariance = EKF.pold;
			Matrix Covariance = filter.get_pold();
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
		double start = System.currentTimeMillis();
		
		//Open files for saving off the generated data.  
		openFiles();
		
		
		//Initialize
		//This step should read in all the variables and set up all the required
		//models and functionality accordingly.  One should try to be careful
		//to flag any inconsistancies in the input data to save crashes later on.
		
		initialize();
		
		for(int i=0; i<numSpacecraft; i++){
			truth_traj[i].add(truth[i].get_sc_mjd_utc(),truth[i].get_spacecraft().toStateVector());
			ref_traj[i].add(ref[i].get_sc_mjd_utc(),ref[i].get_spacecraft().toStateVector());
		}
		
		/*Cache off the simulation mode */
		int filterMode = initializer.parseInt(this.input,"init.mode");
		
		//Compute the length of the simulation in seconds
		double MJD0 =  initializer.parseDouble(this.input,"init.MJD0");
		double MJDF =  initializer.parseDouble(this.input,"init.MJDF");
		double T0   =  initializer.parseDouble(this.input,"init.T0");
		double TF   =  initializer.parseDouble(this.input,"init.TF");
		//simTime = 0; //* this is done in call to "initialize()"
		simTime = new Time(MJD0);
		double simLength = Math.round((MJDF - MJD0)*86400 + TF - T0);
		this.tf = simLength;
		set_verbose(this.verbose_estimation);
		if(!Flag_GPS && !Flag_GPSState && !Flag_Cross ) this.useFilter = false;
		//double simLength = Math.round(TF - T0);
		//ObservationMeasurement obs = obs_list.getFirst();
		
		for(int i=0; i<numSpacecraft; i++){
			new PrintStream(truths[i]).println (truth[i].get_spacecraft().toStateVector().toString());
			new PrintStream(trajectories[i]).println (ref[i].get_spacecraft().toStateVector().toString());
		}
		for( simStep = 1; simStep < simLength/dt; simStep ++)
		{
			//if(this.verbose_estimation) 
				//System.out.println("running..."+(dt*simStep)+" / "+simLength);
			//if(simStep%100 == 0)
			//	System.out.println(simStep*5);
			propagate(simStep*dt);
			//simTime = simStep*dt;
			simTime.update(simStep*dt);
				
			if(this.obsFromFile)
				filter_fromfile();
			else
				filter();
			
			if(Double.isNaN(ref[0].get_spacecraft().toStateVector()[0])){// || simTime.get_sim_time()>4620){
				int donothing = 0;
				donothing++;
			}
			//System.out.println("SimTime: " + simTime.get_sim_time() + " SimStep: " + simStep);
			
			for(int i=0; i<numSpacecraft; i++){
				truth_traj[i].add(truth[i].get_sc_mjd_utc(),truth[i].get_spacecraft().toStateVector());
				ref_traj[i].add(ref[i].get_sc_mjd_utc(),ref[i].get_spacecraft().toStateVector());
			}
			
		}
		
		/*Close all output files*/
		closeFiles();
		System.gc();
		
		double elapsed = (System.currentTimeMillis()-start)*0.001/60;
		System.out.println("Elapsed time [min]: "+elapsed);
		
		/* Post Processing */
		
		Trajectory geons_truth = new Trajectory(); 
		if(PlotGEONSTruth || PlotMeasurements) geons_truth = parseGEONSTruth(simTime.get_epoch_mjd_utc(),simTime.mjd_utc());
		Trajectory geons_ref = new Trajectory();
		if(PlotGEONSRef || PlotMeasurements) geons_ref = parseGEONSRef(simTime.get_epoch_mjd_utc(),simTime.mjd_utc());
		Trajectory measurements = new Trajectory();
		measurements = this.obs_list.get_meas_traj();
		LinePrinter lp = new LinePrinter();
		RelativeTraj[] reltraj = new RelativeTraj[numSpacecraft];
		double mismatch_tol = 0.00001;
		Celestia cel = new Celestia("C:/Code/Celestia/");
		//* TODO Plot marker
		for(int i=0; i<numSpacecraft; i++){
			if(PlotJAT){
				reltraj[i] = new RelativeTraj(ref_traj[i],truth_traj[i],lp,"Jat(Ref) v Jat(Truth)");
				reltraj[i].setVerbose(false);
				reltraj[i].process(mismatch_tol);
			}
			if(PlotGEONSRef){
				reltraj[i] = new RelativeTraj(ref_traj[i],geons_ref,lp,"Jat(Ref) v Geons(Ref)");
				reltraj[i].setVerbose(false);
				reltraj[i].process(mismatch_tol);
				//reltraj[i] = new RelativeTraj(truth_traj[i],geons_ref,lp,"Jat(Truth) v Geons(Ref)");
				//reltraj[i].setVerbose(false);
				//reltraj[i].process(mismatch_tol);
			}
			if(PlotGEONSTruth){
				reltraj[i] = new RelativeTraj(truth_traj[i],geons_truth,lp,"Jat(Truth) v Geons(Truth)");
				reltraj[i].setVerbose(false);
				reltraj[i].process(mismatch_tol);
				reltraj[i] = new RelativeTraj(ref_traj[i],geons_truth,lp,"Jat(Ref) v Geons(Truth)");
				reltraj[i].setVerbose(false);
				reltraj[i].process(mismatch_tol);
			}
			if(PlotGEONSTruth && PlotGEONSRef){
				reltraj[i] = new RelativeTraj(geons_ref,geons_truth,lp,"Geons(Ref) v Geons(Truth)");
				reltraj[i].setVerbose(false);
				reltraj[i].process(mismatch_tol);
			}
			if(PlotMeasurements){
				reltraj[i] = new RelativeTraj(measurements,geons_truth,lp,"GEONS(Truth) v Measurements");
				reltraj[i].setVerbose(false);
				reltraj[i].process(mismatch_tol);
				reltraj[i] = new RelativeTraj(measurements,geons_ref,lp,"GEONS(Ref) v Measurements");
				reltraj[i].setVerbose(false);
				reltraj[i].process(mismatch_tol);
				reltraj[i] = new RelativeTraj(measurements,ref_traj[i],lp,"JAT(Ref) v Measurements");
				reltraj[i].setVerbose(false);
				reltraj[i].process(mismatch_tol);
			}
			try {
				cel.set_trajectory_meters(ref_traj[i]);
				cel.write_trajectory("jat_ref_"+JAT_case,"jat_ref_"+JAT_case,TimeUtils.MJDtoJD(this.mjd_utc_start));
				cel.set_trajectory_meters(truth_traj[i]);
				cel.write_trajectory("jat_truth_"+JAT_case,"jat_truth_"+JAT_case,TimeUtils.MJDtoJD(this.mjd_utc_start));
				if(PlotGEONSRef){
					cel.set_trajectory_meters(geons_ref);
					cel.write_trajectory("jat_geons_ref_"+JAT_case,"jat_geons_ref_"+JAT_case,TimeUtils.MJDtoJD(this.mjd_utc_start));
				}
				if(PlotGEONSTruth){
					cel.set_trajectory_meters(geons_truth);
					cel.write_trajectory("jat_geons_truth_"+JAT_case,"jat_geons_truth_"+JAT_case,TimeUtils.MJDtoJD(this.mjd_utc_start));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
			dir_in = FileUtil.getClassFilePath("jat.sim","EstimatorSimModel")+"output"+fs;
		}catch(Exception e){
			dir_in = "";
		}
		
		//String fileName5 = dir_in+"Visible.txt";
		/*	try {
		 visableSats = new FileOutputStream(fileName5);
		 } catch (FileNotFoundException e1) {
		  e1.printStackTrace();
		  }*/
		for(int numSats = 0; numSats < numSpacecraft; numSats++)
		{
			try
			{
				// Open an output stream
//				String fileName = dir_in+"Sat"+numSats+"ECI.txt";
//				String fileName2 = dir_in+"TRUE"+numSats+"ECI.txt";
//				String fileName3 = dir_in+"Error"+numSats+"ECI.txt";
//				String fileName4 = dir_in+"Covariance"+numSats+"ECI.txt";
				String fileName = dir_in+"JAT_"+JAT_case+".eci";
				String fileName2 = dir_in+"JAT_true_"+JAT_case+".txt";
				String fileName3 = dir_in+"JAT_error_"+JAT_case+".txt";
				String fileName4 = dir_in+"JAT_"+JAT_case+".cov";
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
					e.printStackTrace();
				}	
			}	
			//EKF.residuals.close();
			filter.closeFiles();
			/*try {
			 //visableSats.close();
			  } catch (IOException e) {
			   e.printStackTrace();
			   }*/
		}
		
		System.out.println("Closing files and exiting . . ");
	}
	
	public void set_verbose(boolean b){
		this.verbose_estimation = b;
		if(filter != null)
			filter.set_verbose(b,this.tf);
	}
	
	public Trajectory parseGEONSTruth(double start_mjd,double end_mjd){
		//String filename = "C:/Code/Misc/GEONS/CASE"+JAT_case+"/GEONS_"+JAT_case+".eci";
		String filename = GEONS_Truth;
		Trajectory traj = new Trajectory();
		try{
		File inputFile = new File(filename);
		FileReader fr = new FileReader(inputFile);
		BufferedReader in = new BufferedReader(fr);
		String line="start";
		StringTokenizer tok;
		double year = 0;
		double sec = 0;
		double mjd = 0.0;
		double x[] = new double[6];
		for(int i=0; i<6; i++) line=in.readLine();
		while(!line.equalsIgnoreCase(" ")){
			tok = new StringTokenizer(line, " ");
			year = Double.parseDouble(tok.nextToken());
			sec = Double.parseDouble(tok.nextToken());
			mjd = year+sec/86400.0;
			if(mjd>end_mjd) break;
			if(mjd>=start_mjd){
				for(int j=0; j<6; j++)
					x[j] = Double.parseDouble(tok.nextToken());
				traj.add(mjd,x);
			}
			line=in.readLine();
		}
		in.close();
		}catch(IOException ioe){		}
		return traj;
	}
	public Trajectory parseGEONSRef(double start_mjd,double end_mjd){
		//String filename = "C:/Code/Misc/GEONS/CASE"+JAT_case+"/GEONS_"+JAT_case+".eci";
		String filename = GEONS_Ref;
		Trajectory traj = new Trajectory();
		try{
		File inputFile = new File(filename);
		FileReader fr = new FileReader(inputFile);
		BufferedReader in = new BufferedReader(fr);
		String line="start";
		StringTokenizer tok;
		double year = 0;
		double sec = 0;
		double mjd = 0.0;
		double x[] = new double[6];
		for(int i=0; i<5; i++) line=in.readLine();
		while(!line.equalsIgnoreCase(" ")){
			tok = new StringTokenizer(line, " ");
			year = Double.parseDouble(tok.nextToken());
			sec = Double.parseDouble(tok.nextToken());
			mjd = year+sec/86400.0;
			if(mjd>end_mjd) break;
			if(mjd>=start_mjd){
				for(int j=0; j<6; j++)
					x[j] = Double.parseDouble(tok.nextToken());
				traj.add(mjd,x);
			}
			line=in.readLine();
		}
		in.close();
		}catch(IOException ioe){		}
		return traj;
	}
	
	
//	** Main **//
	
	public static void main(String[] args) {
		
		boolean useFilter = true;
		int jat_case = 2;
		EstimatorSimModel.JAT_case = jat_case;
		
		if(jat_case==1){
			EstimatorSimModel.MEAS_GPSSTATE = "C:/Code/Jat/jat/measurements/test1_8.rnx";
			EstimatorSimModel.GEONS_Ref = "C:/Code/Misc/GEONS/Case1_8/test1_8.rel26a.sta";
			EstimatorSimModel.GEONS_Truth = "C:/Code/Misc/GEONS/Case1_8/test1_8.j2k.ascii";
					
		}else if(jat_case==2){
			EstimatorSimModel.MEAS_GPS = "C:/Code/Jat/jat/measurements/test1_1.rnx";			
			//EstimatorSimModel.GEONS_Ref = "C:/Code/Misc/GEONS/Case1_1/test1_1.rel26a.sta";
			//EstimatorSimModel.GEONS_Truth = "C:/Code/Misc/GEONS/Case1_1/test1_1.j2k.ascii";
			EstimatorSimModel.GEONS_Truth = "C:/Code/Misc/GEONS/Case1_1/GEONS_2a.j2k.ascii";
			EstimatorSimModel.GEONS_Ref = "C:/Code/Misc/GEONS/Case1_1/GEONS_2a.eci";
				
		}else if(jat_case==3){
			EstimatorSimModel.MEAS_GPS = "C:/Code/Jat/jat/measurements/test1_6.rnx";
			EstimatorSimModel.GEONS_Ref = "C:/Code/Misc/GEONS/Case1_6/test1_6.rel26a.sta";
			EstimatorSimModel.GEONS_Truth = "C:/Code/Misc/GEONS/Case1_6/test1_6.j2k.ascii";
		}
		//EstimatorSimModel.MEAS_GPS = "C:/Code/Jat/jat/measurements/Case-820.rnx";
		
		EstimatorSimModel.PlotJAT = true;
		EstimatorSimModel.PlotGEONSRef = true;
		EstimatorSimModel.PlotGEONSTruth = true;
		EstimatorSimModel.PlotMeasurements = false;
		EstimatorSimModel.Flag_GPSState = false;
		EstimatorSimModel.Flag_GPS = false;
		EstimatorSimModel.Flag_Cross = false;
		
		EstimatorSimModel Sim = new EstimatorSimModel(useFilter);
		Sim.set_verbose(true);
		Sim.runloop();
        
        System.out.println("Finished.");
	}
}
