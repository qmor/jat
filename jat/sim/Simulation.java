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
package jat.sim;

import jat.alg.integrators.LinePrinter;
import jat.matlabInterface.MatlabControl;
import jat.matlabInterface.MatlabFunc;
import jat.matvec.data.VectorN;
import jat.spacecraft.Spacecraft;
import jat.spacecraft.SpacecraftModel;
import jat.spacetime.EarthRef;
import jat.traj.RelativeTraj;
import jat.util.FileUtil;

/**
 * This is the primary class for a simulation scenario.  It obtains input from files
 * adhering to the specified XML Schema jat.sim.xml.input.sim_input.xsd, creates the necessary
 * objects, and propagates the trajectories.
 * 
 * @deprecated
 * @see jat.demo.simulation.Simulation
 * @author Richard C. Page III
 *
 */
public class Simulation {
    
    public SimModel sim;
    
    public Simulation(){
        sim = new SimModel();
    }
        
    public void runSimTwo(){
        SimModel sim = new SimModel();
        double start = System.currentTimeMillis();
        String fs = FileUtil.file_separator();
        String dir = FileUtil.getClassFilePath("jat.sim", "SimModel");
        
        String[] tests = {"jat_demo"};
        //* force_flag = {2-Body, Sun,   Moon, Harris Priester, Solar Radiation}
        boolean[][] force_flag = 
        {{false,true,true,true,true}};					//JGM3		0
        String[][] test_nums = 
        		{{""}};  							//JGM3		
        
        boolean plot_traj = true;
        int i=0,j=0;
        //*ISS
        VectorN r = new VectorN(-4453.783586,-5038.203756,-426.384456);
        VectorN v = new VectorN(3.831888,-2.887221,-6.018232);
        //*Molniya VectorN r = new VectorN(-1529.894287,-2672.877357,-6150.115340);
        //*Molniya VectorN v = new VectorN(8.717518,-4.989709,0);
        //*GEO VectorN r = new VectorN(-1529.894287,-2672.877357,-6150.115340);
        //*GEO VectorN v = new VectorN(8.717518,-4.989709,0);
        //*GPS VectorN r = new VectorN(5525.33668,-15871.18494,-20998.992446);
        //*GPS VectorN v = new VectorN(2.750341,2.434198,-1.068884);
        //*SunSync VectorN r = new VectorN(-2290.301063,-6379.471940,0);
        //*SunSync VectorN v = new VectorN(-0.883923,0.317338,7.610832);
        
        r = r.times(1000);
        //v = v.times(1755);
        v = v.times(1000);
        double t0 = 0, tf = 86400;
        double mjd_utc = 53157.5;  
        //double mjd_utc = 53683;
        double stepsize = 60;
        String out = dir+"output"+fs+tests[i]+test_nums[j][i]+".txt";
        SpacecraftModel sm = new SpacecraftModel(r,v,1.2,2.2,20,1000);
        for(j=0; j<1; j++){
            for(i=0; i<1; i++){
                sim.initialize(sm,t0,tf,mjd_utc, stepsize, 1, out);
                sim.set_showtimestep(true);
                boolean use_JGM2 = false;
                String test = tests[i]+test_nums[j][i];
                sim.initializeForces(force_flag[j], use_JGM2, test);
                sim.runloop();
            }	        
        }
        double elapsed = (System.currentTimeMillis()-start)*0.001/60;
        System.out.println("Elapsed time [min]: "+elapsed);
        if(plot_traj){
            	        jat.util.Celestia celestia = new jat.util.Celestia("C:/games/Celestia_Dev/my_celestia/");
            	        try{
            	            i--;
            	            j--;
            	            celestia.set_trajectory(sim.get_traj());
            	            String name = tests[i]+test_nums[j][i];
            	            celestia.write_trajectory(name,name,sim.mjd_utc_start+2400000.5);
            	            System.out.println("Wrote to Celestia");
            	        }catch(java.io.IOException ioe){}
            LinePrinter lp2 = new LinePrinter();
//            RelativeTraj rel = sim.get_rel_traj(lp2);
//            rel.setVerbose(false);
//            double err = rel.get_max_error()*1000;
//            System.out.println("error:  "+err);
//            rel.process();
        }
        System.out.println("Finished");    	
 
    }
    
    public void runSimMatlab() throws InterruptedException{
        MatlabControl input = new MatlabControl();
        input.eval("disp('runSimMatlab init')");
        SimModel sim = new SimModel();
        //double start = System.currentTimeMillis();
        //String fs = FileUtil.file_separator();
        //String dir = FileUtil.getClassFilePath("jat.sim", "SimModel");
        
        //* force_flag = {2-Body, Sun,   Moon, Harris Priester, Solar Radiation}
        boolean[][] force_flag = 
        {{false,false,false,false,false},						//JGM3		0
                {true,  true,  false,     false,          false},		//Sun		1
                {true,  false,  true,     false,          false},		//Moon		2
                {true,  false, false,     true,           false},		//HP		3
                {true,  false, false,     true,           false},		//NRL		4
                {true,  false, false,     false,          true},		//SRP		5
                {false, true, true, true, true},						//ALL HP	6
                {false, true, true, true, true},						//ALL NRL	7
                {true, false, false, false, false}};					//two body  8
        
        
        int force_case = 8;
        boolean plot_traj = true;
        String name = "Matlab";
        input.eval("disp('runSimMatlab initMatlab')");
        sim.initializeMatlab("initJAT_sc2","initJAT_integ",
                "output"+"/"+name+".txt");
        input.eval("disp('runSimMatlab initMatlab finished')");
        boolean use_JGM2 = false;
        String test = name;
        sim.initializeForces(force_flag[force_case], use_JGM2, test);
        input.eval("disp('runSimMatlab initMatlab finished')");
        sim.runloop();
        //double elapsed = (System.currentTimeMillis()-start)*0.001/60;
        //System.out.println("Elapsed time [min]: "+elapsed);
        VectorN K  = new VectorN(0,0,1);
        double[] X0 = sim.get_traj(1).get(0);
        VectorN r = new VectorN(X0[1],X0[2],X0[3]);
        r.times(1000);
        r.print("position: ");
        VectorN v = new VectorN(X0[4],X0[5],X0[6]);
        v.times(1000);
        v.print("velocity: ");
        VectorN hv = r.crossProduct(v);
        VectorN nv = K.crossProduct(hv);
        double n  = Math.sqrt(nv.mag()*nv.mag());
        double h2 = (hv.mag()*hv.mag());
        double v2 = v.mag()*v.mag();
        double rmag  = r.mag();
        System.out.println("rmag: "+rmag);
        //ev = 1/EarthRef.GM_Earth *( (v2-EarthRef.GM_Earth/r)*rv - (rv'*vv)*vv );
        VectorN ev = (r.times(v2-EarthRef.GM_Earth/rmag).minus(v.times(r.dotProduct(v)))).times(1.0/EarthRef.GM_Earth);
        double p  = h2/EarthRef.GM_Earth;
        double e = ev.mag();
        double a = p/(1-e*e);
        double period = 2*Math.PI*Math.sqrt(a*a*a/EarthRef.GM_Earth);
        System.out.println("Period [hr]: "+period+"  a: "+a+"  e: "+e);
        
        if(plot_traj){
            jat.util.Celestia celestia = new jat.util.Celestia("C:/games/Celestia_dev/celestia/");
            try{
                for(int k=0; k<sim.sc_formation.get_num_sc(); k++){
                    celestia.set_trajectory(sim.get_traj(k));
                    String celname = "formation_"+k;
                    celestia.write_trajectory(celname,celname,sim.mjd_utc_start);
                    System.out.println("Wrote to Celestia "+k);
                }
            }catch(java.io.IOException ioe){}
            LinePrinter lp2 = new LinePrinter();
            RelativeTraj rel = sim.get_rel_traj(lp2,0,1);
            rel.setVerbose(false);
            rel.process();
            rel = sim.get_rel_traj(lp2,0,2);
            rel.setVerbose(false);
            rel.process();
        }
        System.out.println("Finished");
    }
    
    public void runSimFormation(){
        SimModel sim = new SimModel();
        double start = System.currentTimeMillis();
        String fs = FileUtil.file_separator();
        String dir = FileUtil.getClassFilePath("jat.sim", "SimModel");
        
        String[] tests = {"ISS","Sun-Sync","GPS","Molniya","GEO"};
        //* force_flag = {2-Body, Sun,   Moon, Harris Priester, Solar Radiation}
        boolean[][] force_flag = 
        {{false,false,false,false,false},						//JGM3		0
                {true,  true,  false,     false,          false},		//Sun		1
                {true,  false,  true,     false,          false},		//Moon		2
                {true,  false, false,     true,           false},		//HP		3
                {true,  false, false,     true,           false},		//NRL		4
                {true,  false, false,     false,          true},		//SRP		5
                {false, true, true, true, true},						//ALL HP	6
                {false, true, true, true, true},						//ALL NRL	7
                {true, false, false, false, false}};					//two body  8
        String[][] test_nums = 
        {{"6","13","20","27","34"},  							//JGM3		
                {"3","10","17","24","31"},  							//Sun		
                {"2","9","16","23","30"},   							//Moon		
                {"4_HP","11_HP","18_HP","25_HP","32_HP"}, 				//HP
                {"4_NRL","11_NRL","18_NRL","25_NRL","32_NRL"},  		//NRL
                {"5","12","19","26","33"},								//SRP 
                {"7_HP","14_HP","21_HP","28_HP","35_HP"},				//All HP
                {"7_NRL","14_NRL","21_NRL","28_NRL","35_NRL"},			//All NRL
                {"1","8","15","22","29"}};								//two body
        
        boolean plot_traj = true;
        int i=0,j=8;
        VectorN r0 = new VectorN(8948.2,  0.0,   0.0);
        VectorN r1 = new VectorN(9198.2,    -4.2328e-012,       -1.075e-012);
        VectorN r2 = new VectorN(9198.2,    -4.2328e-012,       -1.075e-012);
        VectorN v0 = new VectorN(0,           5.8667,	            3.1854);
        VectorN v1 = new VectorN(3.0093e-015,   5.7027,             3.0963);
        VectorN v2 = new VectorN(3.0093e-015,   5.7027,             3.0964);
        double acr = 1.2;
        double acd = 2.2;
        double aa = 20;
        double am = 1000;
        Spacecraft s0 = new Spacecraft(r0.times(1000), v0.times(1000), acr, acd, aa, am);
        Spacecraft s1 = new Spacecraft(r1.times(1000), v1.times(1000), acr, acd, aa, am);
        Spacecraft s2 = new Spacecraft(r2.times(1000), v2.times(1000), acr, acd, aa, am);
        SpacecraftModel[] sc_arg = new SpacecraftModel[3];
        sc_arg[0] = new SpacecraftModel(s0);
        sc_arg[1] = new SpacecraftModel(s1);
        sc_arg[2] = new SpacecraftModel(s2);
        String out = dir+"formation"+fs+"formation.txt";
        
        sim.initialize(sc_arg,0,60000,53157.5,60, 1, out);
        String test = tests[i]+test_nums[j][i];
        boolean use_JGM2 = false;
        sim.initializeForces(force_flag[j], use_JGM2, test);
//        if(plot_traj){
//            String stkfile = "C:/STK_Test_Files/delim/"+tests[i]+test_nums[j][i]+".txt";
//            sim.set_truth_traj(stkfile);
//        }	            
        sim.runloop();	        
        double elapsed = (System.currentTimeMillis()-start)*0.001/60;
        System.out.println("Elapsed time [min]: "+elapsed);
        if(plot_traj){
            jat.util.Celestia celestia = new jat.util.Celestia("C:/games/Celestia_dev/celestia/");
            try{
                for(int k=0; k<3; k++){
                    celestia.set_trajectory(sim.get_traj(k));
                    String name = "formation_"+k;
                    celestia.write_trajectory(name,name,sim.mjd_utc_start);
                    System.out.println("Wrote to Celestia "+k);
                }
            }catch(java.io.IOException ioe){}
            LinePrinter lp2 = new LinePrinter();
            RelativeTraj rel = sim.get_rel_traj(lp2,0,1);
            rel.setVerbose(false);
            double err = rel.get_max_error()*1000;
            System.out.println("error:  "+err);
            rel.process();
        }
        System.out.println("Finished");            
    }
    
    public void test(){
        jat.matlabInterface.MatlabControl test = new MatlabControl();
        test.eval("disp('testing')");
    }
    
      
    public static void main(String[] args) throws InterruptedException {
        Simulation sim = new Simulation();
        sim.runSimTwo();
        //sim.runSimMatlab();
        //sim.runSimFormation();
    }
}
