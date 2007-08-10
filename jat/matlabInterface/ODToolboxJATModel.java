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
package jat.matlabInterface;

import jat.spacecraft.*;
import jat.spacetime.UniverseModel;
import jat.matvec.data.VectorN;
import jat.alg.integrators.*;
import jat.cm.Constants;
import jat.forces.AtmosphericDrag;
import jat.forces.ForceModel;
import jat.forces.GravitationalBody;
import jat.forces.GravityModel;
import jat.forces.GravityModelType;
import jat.forces.HarrisPriester;
import jat.forces.Moon;
import jat.forces.NRLMSISE_Drag;
import jat.forces.SolarRadiationPressure;
import jat.forces.Sun;

/**
 * This is the primary class for the OD Toolbox interface. Each instantiation
 * encapsulates information about a spacecraft and the universe force models in
 * use.
 * 
 * @author Derek M. Surka
 * 
 */
public class ODToolboxJATModel implements Derivatives {

    public UniverseModel spacetime;
    /**
     * Spacecraft model used when propagating a single spacecraft
     */
    public Spacecraft sc;
    /**
     * The starting epoch in Modified Julian Date Universal Coordinated Time (UTC)
     */
    public double mjd_utc_start;
    
    /**
     * Flag for computing 2-body gravity partials
     */
    private boolean use_j2_gravity 		= false;

    /**
     * Flag for computing J2 gravity partials
     */
    private boolean use_2body_gravity 	= false;

    /**
     * Flag for computing drag partials
     */
    private boolean use_drag 			= false;
       
    /**
     * Index of drag atmosphere model for computing drag partials
     */
    private int drag_index;
       
    /**
     * Constructor initializes a single spacecraft simulation given relevant parameters
     * @param cr Coefficient of Reflectivity used for Solar Radiation Pressure
     * @param cd Coefficient of Drag used for Atmospheric Drag calculations
     * @param dragArea Cross sectional area used for drag
     * @param srpArea  Cross sectional area used for Solar Radiation Pressure
     * @param mass Mass of the spacecraft
     * @param utc Starting epoch
     */
    public ODToolboxJATModel(double cr, double cd, double dragArea, double srpArea, double mass, double utc){
        sc = new Spacecraft();
        sc.set_dragArea(dragArea);
        sc.set_srpArea(srpArea);
        sc.set_cd(cd);
        sc.set_cr(cr);
        sc.set_mass(mass);
        sc.set_use_params_in_state(false);
        spacetime = new UniverseModel(utc);
    }
     
    /**
     * Initialize the forces present in the universe model during the Simulation.
     * @param force_flag An array of boolean values indicating the forces in order:
     *  [0] true: Solar gravity 
     *  [1] true: Lunar gravity 
     *  [2] true: Atmospheric drag
     *  [3] true: Solar Radiation Pressure
     * @param grav_model String specifiying the gravity model to use
     * @param degree of the gravity model
     * @param order of the gravity model
     * @param atm_model "NRL" for NRLMSISE2000 or "HP" for Harris Priester
     * @param atm_params Array of doubles containing parameters for atmosphere models
     *  [0] n:         HP inclincation parameter ranging from 2(low inclination) to 6(high)
     *  [1] f107Daily: Daily value of 10.7cm solar flux
     *  [2] f107Avg:   81-day aveage of 10.7cm solar flux
     *  [3] ap:		   Geomagnetic flux index
     */
    public void initializeForces(boolean[] force_flag, String grav_model, int degree, int order,
			String atm_model, double[] atm_params) {

		VectorN zero = new VectorN(0, 0, 0);

		if (grav_model.equalsIgnoreCase("2Body")) {
			GravitationalBody earth = new GravitationalBody(Constants.GM_Earth);
			spacetime.addForce(earth);
			use_2body_gravity = true;
			
		} else { // DMS Is there a better way to find the gravity type?
			Boolean foundModel = false;
			GravityModelType[] index = GravityModelType.index;
			for (int i = 0; i < GravityModelType.num_models; i++) {
				if (grav_model.equalsIgnoreCase(index[i].toString())) {
					GravityModel earth_grav = new GravityModel(degree, order,
							index[i]);
					spacetime.addForce(earth_grav);
					spacetime.set_use_iers(true);
					foundModel = true;
					use_j2_gravity = true;
					break;
				}
			}
			if (!foundModel) {
				System.out
						.println("Warning: GravityModel "
								+ grav_model
								+ " type not found. No Earth gravity forces will be used.");

			}
		}
        
		if (force_flag[0]) {
	        spacetime.set_compute_sun(true);
	        Sun sun =
	            new Sun(Constants.GM_Sun,zero,zero);
	        spacetime.addForce(sun);
	    }
	    if(force_flag[1]){
	        spacetime.set_compute_moon(true);
	        Moon moon =
	            new Moon(Constants.GM_Moon,zero,zero);
	        spacetime.addForce(moon);
	    }
	    if (force_flag[2]) {
			double n_param 		= atm_params[0];  
			double f107Daily 	= atm_params[1];
			double f107Average 	= atm_params[2];
			double ap 			= atm_params[3];
			
			spacetime.set_compute_sun(true);
			
			if (atm_model.equalsIgnoreCase("NRL")) {
				NRLMSISE_Drag drag = new NRLMSISE_Drag(sc);
				
				drag.setF107Daily(f107Daily);
				drag.setF107Average(f107Average);
				drag.setAP(ap);
				
				spacetime.addForce(drag);
				spacetime.set_use_iers(true);

				use_drag 	= true;
				drag_index 	= spacetime.getForceSize() - 1;
				
			} else if (atm_model.equalsIgnoreCase("HP")) {
				HarrisPriester atmos = new HarrisPriester(sc, 150);
				atmos.setParameter(n_param);
				atmos.setF107(f107Daily);

				spacetime.addForce(atmos);
				spacetime.set_use_iers(true);

				use_drag 	= true;
				drag_index 	= spacetime.getForceSize() - 1;
				
			} else {
				System.out
						.println("Warning: AtmosphereModel "
								+ atm_model
								+ " type not found. No atmospheric drag forces will be used.");
	        	
	        }
	        	
	    }
	    if(force_flag[3]){
	        spacetime.set_compute_sun(true);
	        SolarRadiationPressure srp = new SolarRadiationPressure(sc);
	        spacetime.addForce(srp);
	    }
    }

    /**
     * Implements the Derivatives interface for use with the RungeKutta integrator.
     * Given a time in seconds since epoch and a state vector, return the derivatives
     * of the state vector.
     *
     * @param t Time since epoch [sec]
     * @param X State vector. [x y z xdot ydot zdot]
     */
    public double[] derivs(double t, double[] x) {
        //* Update spacecraft
    	sc.updateState(x);    
        spacetime.update(t);  //DMS this is necessary because updates time, earthRef, and IERS
        //* Get non-control derivatives
        double[] xdot = spacetime.derivs(t,sc); //DMS this only updates time (duplicate)
        return xdot;
    }

    public double[][] gravityPartials(double[] r){
    	return new double[3][3];
    }
    
    public double[][] dragPartials(){
    	return new double[3][3];
    }
    
    /**
     * Set the starting epoch.
     * @param mjd_utc UTC Epoch in MJD
     */
    public void set_epoch(double mjd_utc){
        spacetime.set_time(mjd_utc);
    }

    /**
     * Get the 2-body partials flag
     * @return 2-body partials flag
     */
    public boolean get_2body_gravity_flag(){
    	return use_2body_gravity;
    }

    /**
     * Get the J2 partials flag
     * @return J2 partials flag
     */
    public boolean get_j2_gravity_flag(){
    	return use_j2_gravity;
    }

    /**
     * Get the drag partials flag
     * @return drag partials flag
     */
    public boolean get_drag_flag(){
    	return use_drag;
    }

}
