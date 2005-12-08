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
import jat.alg.ScalarFunction;
import jat.alg.ZeroFinder;
import jat.traj.*;
import jat.audio.*;
import jat.util.Celestia;
//import java.io.FileOutputStream;
//import java.io.PrintWriter;
import java.lang.String;
import java.util.Date;


/**
 * This is the primary class for a simulation scenario.  It obtains input from files
 * adhering to the specified XML Schema jat.sim.xml.input.sim_input.xsd, creates the necessary
 * objects, and propagates the trajectories.
 * 
 * This class should be moved to jat.sim.* when verification is complete.
 *  
 * @author Richard C. Page III
 *
 */

public class SimulationXML implements ScalarFunction {
	// propogation parameters
    protected SimulationLoop loop;
    protected double mjd_utc;
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
	protected RungeKutta8 rk8;
	
	//protected double moon_gm = 4.903280184192842E12;
	protected double moon_gm = Constants.GM_Moon;
	
	protected double ap_opt = 15;
	protected double f107_opt = 150;
	protected boolean use_iers = false;
	public LinePrinter opt;
	public double n_param_opt = 6;
	
	protected Trajectory jat;
	protected Trajectory stk;
	protected RelativeTraj error;
	protected boolean plot_rel_error=false;
	
	protected Celestia celestia;
	
	protected boolean use_sun = true;
	protected boolean use_moon = true;
	protected boolean use_JGM2 = false;
	protected boolean use_update = true;
	
	public SimulationXML(){
	    jat = new Trajectory();
	    stk = new Trajectory();
	    rk8 = new RungeKutta8();
	    //celestia = new Celestia("C:/Celestia_dev/");
	}
	
	public static void main(String[] args) throws java.io.IOException {
		SimulationXML sim = new SimulationXML();
	    double start = System.currentTimeMillis();
//	    ZeroFinder solver = new ZeroFinder(sim);
//	    double x1 = 0;
//	    double x2 = 16;
//	    System.out.println("Running optimizer...");
//	    sim.opt = new LinePrinter("C:/ACME/Debug/opt_Sun-Sync_HP.txt");
//	    sim.moon_gm = solver.regulaFalsi(x1,x2);
//	    //sim.moon_gm = solver.secant(x1,x2);
//	    sim.opt.close();

	    sim.runSim();
	    
	    double elapsed = (System.currentTimeMillis()-start)*0.001/60;
		System.out.println("Elapsed time [min]: "+elapsed);
//		SoundPlayer.play("C:/Code/tada.wav");
//		SoundPlayer.play("C:/Code/tada.wav");
		
	}

    /* (non-Javadoc)
     * @see jat.alg.ScalarFunction#evaluate(double)
     */
    public double evaluate(double x) {
        this.n_param_opt = x;
        runSim();
        LinePrinter lp = new LinePrinter();
        jat = loop.getTrajectory();
        error = new RelativeTraj(jat,stk,this.opt);
        error.setVerbose(false);
        double out = error.get_max_error();
        System.out.println("ap_opt: "+x+"   error:  "+out);
        opt.println("ap_opt: "+x+"   error:  "+out);
        return out;
    }
	
	/** Do it.
	 *
	 */
    public void runSim(){
        fs = FileUtil.file_separator();
		//sim.directory = FileUtil.getClassFilePath("jat.test.propagator","Simulation")+sim.fs ;
		String dir_in = FileUtil.getClassFilePath("jat.test.propagator","Simulation")+fs;
		String dir_out1 = FileUtil.getClassFilePath("jat.test.propagator","Simulation")+"output_update"+fs;
		String dir_out2 = FileUtil.getClassFilePath("jat.test.propagator","Simulation")+"output_noupdate"+fs;
		//String dir_out = FileUtil.getClassFilePath("jat.test.propagator","Simulation")+"output"+fs;
		String dir_out = dir_out1; 
		String[] tests = {"ISS","Sun-Sync","GPS","Molniya","GEO",
		        		  "ISS","Sun-Sync","GPS","Molniya","GEO"};
//		String[] test_nums = {"1","8","15","22","29"}; //2body
//		String[] test_nums = {"6","13","20","27","34"}; //JGM3
//		String[] test_nums = {"6test","13test","20test","27test","34test"}; //JGM3test
//		String[] test_nums = {"6_old","13_old","20_old","27_old","34_old"}; //JGM3_old
//		String[] test_nums = {"_1","_2","_3","_4","_5"}; //J2
//        String[] test_nums = {"2","9","16","23","30"}; //moon
// 		String[] test_nums = {"3","10","17","24","31"}; //sun
//		String[] test_nums = {"5","12","19","26","33"}; //solarpressure
//		String[] test_nums = {"3","10","17","24","31",
//		        			  "5","12","19","26","33"};
//		String[] test_nums = {"4_HP","11_HP","18_HP","25_HP","32_HP"}; //HarrisPriester
//		String[] test_nums = {"4_NRL","11_NRL","18_NRL","25_NRL","32_NRL"}; //nrlmsise
//		String[] test_nums = {"7_HP","14_HP","21_HP","28_HP","35_HP"}; //ALL
//		String[] test_nums = {"7_NRL","14_NRL","21_NRL","28_NRL","35_NRL"}; //ALL
//		String[] test_nums = {"7_HP","14_HP","21_HP","28_HP","35_HP",
//		        			  "7_NRL","14_NRL","21_NRL","28_NRL","35_NRL"};
		this.plot_rel_error = false;
		this.ap_opt = 14.918648166;//14.912091;//15.075709;
	    this.f107_opt = 150;//110; //149.952;
	    this.n_param_opt = 6;
	    //* force_flag = {2-Body, Sun,   Moon, Harris Priester, Solar Radiation}
//		boolean[][] force_flag = 
//					 {{true,  false,  false,     true,          false},{}};
//		boolean[][] force_flag = 
//		 			{{false,  false,  false,     false,          false},{}};
//		boolean[][] force_flag = 
//		 			{{false,  true,  true,     true,          true},{}};
//		boolean[][] force_flag = 
//					{{true,  false,  false,     false,          true},{}};
//	    boolean[][] force_flag = 
//	    			 {{false,false,false,false,false},						//JGM3
//	                  {true,  true,  false,     false,          false},		//Sun
//	            	  {true,  false,  true,     false,          false},		//Moon
//	            	  {true,  false, false,     true,           false},		//HP
//	            	  {true,  false, false,     true,           false},		//NRL
//	            	  {true,  false, false,     false,          true},		//SRP
//	            	  {false, true, true, true, true},						//ALL HP
//	            	  {false, true, true, true, true}};						//ALL NRL
//	    String[][] test_nums = {{"6","13","20","27","34"},  //JGM3
//	            				{"3","10","17","24","31"},  //Sun
//	            				{"2","9","16","23","30"},   //Moon
//	            				{"4_HP","11_HP","18_HP","25_HP","32_HP"}, //HP
//	            				{"4_NRL","11_NRL","18_NRL","25_NRL","32_NRL"},  //NRL
//	            				{"5","12","19","26","33"},	//SRP 
//	    						{"7_HP","14_HP","21_HP","28_HP","35_HP"},		//All HP
//	    						{"7_NRL","14_NRL","21_NRL","28_NRL","35_NRL"}};	//All NRL
	    
	    boolean[][] force_flag = 
	    	   {//{false,false,false,false,false},						//JGM3	with update
	            //{false,false,false,false,false},						//JGM2	with update
	            {false, true, true, true, true},						//ALL NRL JGM3 with update
	            //{false, true, true, true, true},						//ALL HP JGM3 with update
	            {false, true, true, true, true},						//ALL NRL JGM2 with update
	            //{false, true, true, true, true},						//ALL HP JGM2 with update
	            //{true,  false, false,     true,           false},		//HP with update 
          	  	//{true,  false, false,     true,           false}		//NRL with update
	    	   };
          	  		            
	    String[][] test_nums = {//{"6_JGM3","13_JGM3","20_JGM3","27_JGM3","34_JGM3"},  //JGM3
	            //{"6_JGM2","13_JGM2","20_JGM2","27_JGM2","34_JGM2"},  //JGM2
	            {"7C","14C","21C","28C","35C"},	//All NRL
	            //{"7D","14D","21D","28D","35D"},		//All HP
	            {"7A","14A","21A","28A","35A"},	//All NRL
	            //{"7B","14B","21B","28B","35B"},		//All HP
	            //{"4_HP","11_HP","18_HP","25_HP","32_HP"}, //HP
	            //{"4_NRL","11_NRL","18_NRL","25_NRL","32_NRL"}  //NRL
	    		};
	
	    this.use_JGM2 = false;
	    int j=0;
	    int numcase = 1;
	    for(int k=0; k<numcase*2; k++){
	        if(k<numcase){
	            j=k;
	            use_update = true;
	            dir_out = dir_out1;
	        } else {
	            use_update = false;
	            dir_out = dir_out2;
	            j=k-numcase;
	        }
	    //dir_out = dir_in+"output"+fs;
	    //for(int j=5; j<6; j++){
		for(int i=1; i<5; i++){
//		    if(j==0 || j==2 || j== 3) use_JGM2 = false;
//		    else use_JGM2 = true;
		    
		    if(!force_flag[j][0] || force_flag[j][3]){
			    this.use_iers = true;
		    } else this.use_iers = false;
		    if(force_flag[j][2]){
		        this.use_moon = true;
		    } else this.use_moon = false;
		    if(force_flag[j][1] || force_flag[j][3] || force_flag[j][4]){
		        this.use_sun = true;
		    } else this.use_sun = false;
			System.out.println("IERS: "+this.use_iers);
		    try{
		        if(i<5){
		            String stkfile = "C:/STK_Test_Files/delim/"+tests[i]+test_nums[j][i]+".txt";
			    	stk.readFromFile(stkfile);
		            initialize(tests[i],test_nums[j][i],dir_in,dir_out,force_flag[j]);
		        } else {
		            String stkfile = "C:/STK_Test_Files/delim/"+tests[i]+test_nums[j][i]+".txt";
			    	stk.readFromFile(stkfile);
		            initialize(tests[i],test_nums[j][i],dir_in,dir_out,force_flag[j]);
		        }
		    }catch(java.io.IOException e){System.out.println("Error! initialize failed.");}
			//integrate(); //***variable step integrator
		    runLoop();
			System.out.println(tests[i]+"  finished");
		
			if(plot_rel_error){
			    jat = loop.getTrajectory();
			    lp = new LinePrinter();
			    String title = tests[i]+test_nums[j][i];
			    error = new RelativeTraj(jat,stk,lp,title);
			    error.setVerbose(false);
			    double out = error.get_max_error();
			    System.out.println("error:  "+(out*1000));
			    error.process();
			}
			
//			try{
//		    celestia.readStateFile("C:/Code/Jat/jat/test/propagator/output/"+tests[i]+".txt");
//		    celestia.write_ssc(tests[i]+"_jat",tests[i]+"_jat",2453158);
//		    celestia.write_xyz(tests[i]+"_jat");
//	        celestia.readStateFile("C:/STK_Test_Files/delim/"+tests[i]+".txt");
//		    celestia.write_ssc(tests[i]+"_stk",tests[i]+"_stk",2453158);
//		    celestia.write_xyz(tests[i]+"_stk");
//			}catch(Exception ioe){System.out.println("Could not write to Celestia");}
		}
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
		    mjd_utc = input.getMJD();
		//*	Format Objects
		    VectorN zero = new VectorN(0,0,0);
		//*	Force Models
		    earthRef = new EarthRef(mjd_utc);
		    forces = new ForceModelListOld(sc,mjd_utc,earthRef);
		    forces.set_use_IERS(this.use_iers);
		    forces.set_use_sun(this.use_sun);
		    forces.set_use_moon(this.use_moon);
		    forces.set_use_update(this.use_update);
		    if(force_flag[0]){
		        System.out.println("Earth");
		        GravitationalBody earth = 
		            new GravitationalBody(398600.4415e+9,zero,zero);
		        forces.addGravForce(earth);
		    } else {
		        if(use_JGM2){
		            System.out.println("JGM2");
		            GravityModel earth_grav = new GravityModel(20,20,GravityModelType.JGM2);
		            forces.addOtherForce(earth_grav);
		        }else{
		            System.out.println("JGM3");
		            GravityModel earth_grav = new GravityModel(20,20,GravityModelType.JGM3);
		            forces.addOtherForce(earth_grav);
		        }

//		        System.out.println("J2");
//		        J2Gravity earth_j2 = new J2Gravity();
//		        forces.addOtherForce(earth_j2);
		        
//		        System.out.println("JGM3_old");
//		        JGM3 earth_jgm3 = new JGM3(18,18);
//		        forces.addOtherForce(earth_jgm3);
		    }
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
		        if(num.endsWith("NRL") || num.endsWith("A") || num.endsWith("C")){
		            System.out.println("NRLMSISE");
		            NRLMSISE_Drag drag = new NRLMSISE_Drag(sc);
		            drag.ap_opt = this.ap_opt;	//* debug ***********************
		            drag.f107_opt = this.f107_opt; //* debug ********************
		            forces.addOtherForce(drag);
		        }else{
		            System.out.println("HarrisPriester");
		            HarrisPriester atmos = new HarrisPriester(sc,150);//145.8480085177176);
		            //atmos.setF107(145.8480085177176);//148.715);//99.5);
		            atmos.setParameter(this.n_param_opt);
//		            if(test.equalsIgnoreCase("Sun-Sync"))
//		                atmos.setParameter(6);
//		            else if(test.equalsIgnoreCase("ISS"))
//		                atmos.setParameter(4);
		            forces.addOtherForce(atmos);

		        }
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
		    
//lp = new LinePrinter(dir_out,"ISS_variable"+".txt");		    
		    
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

	/**
	 *
	 */
	public void runLoop(){
		Spacecraft[] sc_in = new Spacecraft[1];
		ForceModelListOld[] fm_in = new ForceModelListOld[1];
		sc_in[0] = sc;
		fm_in[0] = forces;
		// Execute the Loop
		loop = new SimulationLoop(mjd_utc,stepsize,sc_in,fm_in,this.use_iers);
		double duration = tf-t0;
		loop.loop(duration,lp);
		// Formatting
		lp.close();
		//System.out.println("Finished");
	}
	
	public void integrate(){
	    // integrate
	    lp.setIsAdaptive(true);
	    lp.setThinning(1.0);
	    RungeKuttaFehlberg78 rk78 = new RungeKuttaFehlberg78();
	    rk78.setAccuracy(1.0e-12);
	    rk78.setMinimumStepSize(5.0);
	    rk78.setStepSize(5.0);
	    double[] X = sc.toStateVector(false);
	    rk78.integrate(t0,X,tf,forces,lp,true);
	    rk78.setVerbose(true);
		// Formatting
		lp.close();
		//System.out.println("Finished");
	}
	
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
		mjd_utc = input.getMJD();
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

}
