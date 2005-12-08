/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2005 Emergent Space Technologies Inc. All rights reserved.
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
 */
package jat.test.propagator;

import jat.cm.*;
import jat.forces.*;
import jat.sim.xml.input.parse.XMLInput;
import jat.spacecraft.Spacecraft;
import jat.timeRef.EarthRef;
import jat.util.FileUtil;
import jat.matvec.data.VectorN;
import jat.alg.integrators.*;
import jat.traj.*;
//import java.io.FileOutputStream;
//import java.io.PrintWriter;
import java.lang.String;
import java.util.Date;

import com.mathworks.jmi.Matlab;

/**
 * Primary file for simulation tests.
 * 
 * @author Richard C. Page III
 *
 */
public class SimulationMatlab {
	// propogation parameters
    protected double mjd_start;
    protected double t0;
	protected double tf;
	protected double stepsize;
	protected double thinning;
	protected EarthRef earthRef;
	protected ForceModelListOld forces;
	protected Spacecraft sc;
	protected LinePrinter lp;
	protected Date timer;
	protected String directory;
	protected String fs;
	protected String matlab_integrator;
	
	protected double moon_gm = 4.903280184192842E12;
	
	public SimulationMatlab(){
	    matlab_integrator = "@ode45";
	}
	
	public static void main(String[] args) throws java.io.IOException {
		SimulationMatlab sim = new SimulationMatlab();
	    double start = System.currentTimeMillis();
	    //ZeroFinder solver = new ZeroFinder(sim);
	    //double x1 = Constants.GM_Moon-1e11;
	    //double x2 = Constants.GM_Moon+1e11;
	    //sim.moon_gm = solver.regulaFalsi(x1,x2);
	    sim.runSim();
	    double elapsed = (System.currentTimeMillis()-start)*0.001/60;
		System.out.println("Elapsed time [min]: "+elapsed);
		//SoundPlayer.play("C:/Code/tada.wav");
	}

	/** Do it.
	 *
	 */
    public void runSim(){
        fs = FileUtil.file_separator();
		//sim.directory = FileUtil.getClassFilePath("jat.test.propagator","Simulation")+sim.fs ;
		String dir_in = FileUtil.getClassFilePath("jat.test.propagator","Simulation")+fs;
		String dir_out = FileUtil.getClassFilePath("jat.test.propagator","SimulationMatlab")+"matlab_output"+fs;
		String[] tests = {"ISS","Sun-Sync","GPS","Molniya","GEO"};
		String[] test_nums = {"1","8","15","22","29"}; //2body
//		String[] test_nums = {"6","13","20","27","34"}; //JGM3
//		String[] test_nums = {"6test","13test","20test","27test","34test"}; //JGM3test
//		String[] test_nums = {"6_old","13_old","20_old","27_old","34_old"}; //JGM3_old
//		String[] test_nums = {"_1","_2","_3","_4","_5"}; //J2
//        String[] test_nums = {"2","9","16","23","30"}; //moon
// 		String[] test_nums = {"3","10","17","24","31"}; //sun
//		String[] test_nums = {"5","12","19","26","33"}; //solarpressure
//		String[] test_nums = {"4","11","18","25","32"}; //HarrisPriester
//		String[] test_nums = {"4_nrl","11_nrl","18_nrl","25_nrl","32_nrl"}; //nrlmsise
		//* force_flag = {2-Body, Sun,   Moon, Harris Priester, Solar Radiation}
	    boolean[] force_flag = 
	                 {true,  false,  false,    false,          false};
		for(int i=0; i<1; i++){
		    try{
		        initialize(tests[i],test_nums[i],dir_in,dir_out,force_flag);
		    }catch(java.io.IOException e){System.out.println("Error! initialize failed.");}
			//runLoopMatlab(matlab_integrator);
		    runMatlabIntegrator();
		}
		

    }
	public void initialize(String test, String num, String dir_in, String dir_out,
	        boolean[] force_flag) 	throws java.io.IOException {
	    
		//* read the input file
		    XMLInput input = new XMLInput(
		            dir_in+test+".xml");
		    sc = input.getSpacecraft();
		    t0 = input.getT0();
		    tf = input.getTf();
		    mjd_start = input.getMJD();
		//*	Format Objects
		    VectorN zero = new VectorN(0,0,0);
		//*	Force Models
		    forces = new ForceModelListOld(sc,input.getMJD());
		    if(force_flag[0]){
		        System.out.println("Earth");
		        GravitationalBody earth = 
		            new GravitationalBody(398600.4415e+9,zero,zero);
		        forces.addGravForce(earth);
		    } else {
		        System.out.println("JGM3");
		        GravityModel earth_grav = new GravityModel(20,20,GravityModelType.JGM3);
		        forces.addOtherForce(earth_grav);

//		        System.out.println("J2");
//		        J2Gravity earth_j2 = new J2Gravity();
//		        forces.addOtherForce(earth_j2);
		        
//		        System.out.println("JGM3_old");
//		        JGM3 earth_jgm3 = new JGM3(18,18);
//		        forces.addOtherForce(earth_jgm3);
		    }
		    earthRef = new EarthRef(mjd_start);
		    if(force_flag[1]){
		        System.out.println("Sun");
		        Sun sun =
		            new Sun(Constants.GM_Sun,earthRef.sunVector(),zero);
		        forces.addGravForce(sun);
		    }
		    if(force_flag[2]){
		        System.out.println("Moon");
		        Moon moon = 
		            new Moon(moon_gm,earthRef.moonVector(),zero);
		        forces.addGravForce(moon);
		    }
		    if(force_flag[3]){
//		        System.out.println("HarrisPriester");
//		        HarrisPriester atmos = new HarrisPriester(sc);
//		        atmos.setF107(150);
//		        forces.addOtherForce(atmos);

		        System.out.println("NRLMSISE");
		        NRLMSISE_Drag drag = new NRLMSISE_Drag(sc);
		        forces.addOtherForce(drag);
		    
		    }
		    if(force_flag[4]){
		        System.out.println("SolarRadiationPressure");
		        SolarRadiationPressure srp = new SolarRadiationPressure(sc);
		        forces.addOtherForce(srp);
		    }
		//*	Integrator Setup
		    stepsize = input.getStepSize();
		    thinning = input.getThinning();
		    lp = new LinePrinter(dir_out,test+num+".txt");
		    lp.setThinning(thinning);

//		    Spacecraft[] sc_in = new Spacecraft[1];
//		    ForceModelList[] fm_in = new ForceModelList[1];
//		    sc_in[0] = sc;
//		    fm_in[0] = forces;
//		    double[] X = {sc.r().get(0),sc.r().get(1),sc.r().get(2),
//		            sc.v().get(0),sc.v().get(1),sc.v().get(2)};
//		//*	Execute the Loop
//		    SimulationLoop loop = 
//		        new SimulationLoop(mjd,stepsize,sc_in,fm_in,true);
//		    double duration = tf-t0;
//		    loop.loop(duration,lp);
//		    
//		//*	Formatting
//		    lp.close();
//		    System.out.println("Finished "+tests[i]+" "+test_nums[i]);
//		    jat.audio.SoundPlayer.play("C:/Code/tada.wav");
	}
	
	public void runMatlabIntegrator(){
        double[] returnVals;
        Object[] inputArgs = new Object[3];
        String path = FileUtil.getClassFilePath("jat.matlabInterface", "MatlabDerivs");
        String derivs = path+"derivs.m"; 
        inputArgs[0] = "@"+derivs;
        int num_output_steps = (int)Math.ceil((tf-t0)/stepsize+1);
        double[] tspan = new double[num_output_steps];
        for(int i=0; i<num_output_steps; i++)
            tspan[i] = mjd_start+sec2days(t0+stepsize*i);
        inputArgs[1] = tspan;
        double[] X = new double[6];
        X[0] = sc.r().get(0);
        X[1] = sc.r().get(1);
        X[2] = sc.r().get(2);
        X[3] = sc.v().get(0);
        X[4] = sc.v().get(1);
        X[5] = sc.v().get(2);
        inputArgs[2] = X; 
        try {
			returnVals = (double[])Matlab.mtFevalConsoleOutput(
			        matlab_integrator, inputArgs, 0);
//			returnVals = (double[])Matlab.mtFeval(cmd, inputArgs, 0);
			print(returnVals);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void print(double[] X){
	    for(int i=0; i<X.length; i++)
	        System.out.println(""+X[0]+"  "+X[1]+"  "+X[2]);
	}

//	public void runLoopMatlab(String cmd){
//	    Spacecraft[] sc_in = new Spacecraft[1];
//		ForceModelList[] fm_in = new ForceModelList[1];
//		sc_in[0] = sc;
//		fm_in[0] = forces;
//		// Execute the Loop
//		loop = new SimulationLoop(mjd,stepsize,sc_in,fm_in,true);
//		double duration = tf-t0;
//		loop.loopMatlab(duration,lp,cmd);
//		// Formatting
//		lp.close();
//		System.out.println("Finished");
//	}
	
	public void twoBody(){
		// read the input file
		String fs = FileUtil.file_separator();
		String directory;
		String test_case = "GEO";
		String test_num = "";
		directory = FileUtil.getClassFilePath("jat.test.propagator","Simulation")+fs ;
		XMLInput input = new XMLInput(
		        "C:/Code/Jat/jat/test/propagator/"+test_case+".xml");
		VectorN zero = new VectorN(0,0,0);
		// Format Objects
		sc = input.getSpacecraft();
		forces = new ForceModelListOld(sc,input.getMJD());
		t0 = input.getT0();
		tf = input.getTf();
		mjd_start = input.getMJD();
		thinning = input.getThinning();
		directory = FileUtil.getClassFilePath("jat.test.propagator","Simulation")+"output"+fs;
		lp = new LinePrinter(directory,test_case+test_num+".txt");
		lp.setThinning(thinning);
        // Force Models
		
		TwoBody grav = new TwoBody(sc.r(),sc.v());
		grav.propagate(t0,tf,lp,true);
		
		System.out.println("Finished");
	    
	}
	
	public ForceModelListOld getForces(){ return forces; }
	
	private double sec2days(double s){
        return s*0.000011574074;
    }

}
