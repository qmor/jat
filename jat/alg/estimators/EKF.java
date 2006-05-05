package jat.alg.estimators;

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
 * File Created on May 7, 2003
 */
import java.util.HashMap;
import jat.alg.estimators.*;
import jat.sim.*;
import jat.matvec.data.*;
import jat.alg.estimators.*;
import jat.alg.integrators.LinePrinter;
import jat.measurements.*;
import jat.sim.initializer.*;
import jat.util.FileUtil;

//import jat.audio.*;

/**
 * The ExtendedKalmanFilter Class processes measurements using an EKF algorith,
 * given the measurements, measurement model and dynamics (or process) model.
* Assumes a scalar measurement update.
*
* @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
* @version 1.0
*/
public class EKF {


	/** Dynamics or Process Model */
	public ProcessModel  process;

	/** Number of states or unknowns */
	public int n;

	/** Nominal time step */
	private double dtNominal;
	
	/** Time in the filter*/
	 private double filterTime;
	 
	 /**Filter State**/
	 private double[] xprev;
	 
	 /**Previous Time**/
	 private double tprev;
	 
	 /**State Transition Matrix*/
	 public EstSTM xref;
	 
	 /**Need to keep track of the new and old covariances*/
	 public static Matrix pold;
	 public static Matrix pnew;
	 
	 public static LinePrinter residuals; 

	/**
	 * Constructor.
	 * @param mm MeasurementModel
	 * @param pm ProcessModel
	 * @param rp LinePrinter
	 */
	public EKF() {
		HashMap hm = closedLoopSim.hm;
		
        String fs, dir_in;
        fs = FileUtil.file_separator();
        try{
            dir_in = FileUtil.getClassFilePath("jat.sim","closedLoopSim")+"output"+fs;
        }catch(Exception e){
            dir_in = "";
        }
        residuals = new LinePrinter(dir_in+"Residuals.txt");
		this.n = initializer.parseInt(hm,"FILTER.states");
		String stringPm = initializer.parseString(hm,"FILTER.pm");
		dtNominal = initializer.parseInt(hm,"FILTER.dt");
		if(stringPm.equals("JGM4x4SRPProcess15state"))
		{
			LinePrinter lp1 = new LinePrinter(dir_in+"geom1_1.txt");
	 		LinePrinter lp2 = new LinePrinter(dir_in+"geom1_2.txt");
			this.process= new JGM4x4SRPProcess15state(lp1, lp2);
	
		}
		else
		{
			System.out.println("Process model not recognized.  Aborting");
			System.exit(1);
		}
		double[] X = new double[n];
		filterInitialize();
		
	}

	private Matrix updateCov(VectorN k, VectorN h, Matrix p) {
		Matrix eye = new Matrix(this.n);
		Matrix kh = k.outerProduct(h);
		Matrix i_kh = eye.minus(kh);
		Matrix out = i_kh.times(p);
		return out;
	}

	private Matrix updateCov(Matrix k, Matrix h, Matrix p){
		Matrix eye = new Matrix(this.n);
		Matrix kh = k.times(h);
		Matrix i_kh = eye.minus(kh);
		Matrix out = i_kh.times(p);
		return out;
	}
	
	private Matrix updateCovJoseph(VectorN k, VectorN h, Matrix p, double measurementNoise) {
		Matrix eye = new Matrix(this.n);
		Matrix kh = k.outerProduct(h);
		Matrix i_kh = eye.minus(kh);
		Matrix i_khT = i_kh.transpose();
		double r = measurementNoise;
		Matrix kkT = k.outerProduct(k);
		Matrix krkT = kkT.times(r);

		Matrix part1 = i_kh.times(p);
		part1 = part1.times(i_khT);
		Matrix out = part1.plus(krkT);
		return out;
	}

	private VectorN updateState(VectorN k, double z) {
		VectorN xhat = k.times(z);
		return xhat;
	}

	private Matrix propCov(Matrix p, Matrix phi, Matrix q) {
		Matrix phitrans = phi.transpose();
		Matrix temp1 = p.times(phitrans);
		Matrix temp2 = phi.times(temp1);
		Matrix out = temp2.plus(q);
		return out;
	}

	private VectorN kalmanGain(Matrix p, VectorN h, double r) {
		VectorN ph = p.times(h);
		VectorN hp = h.times(p);
		double hph = hp.dotProduct(h);
		double hphr_inv = 1.0 / (hph + r);
		VectorN out = ph.times(hphr_inv);
		
		return out;
	}
	
	private Matrix kalmanGain(Matrix p, Matrix h, Matrix r) {
		Matrix ph = p.times(h);
		Matrix hp = h.times(p);
		Matrix hph = hp.times(h);
		Matrix hphTranspose = hph.transpose();
		Matrix hphr = (hphTranspose.plus(r));
		Matrix hphr_inv = hphTranspose.inverse();
		Matrix out = ph.times(hphr_inv);
		return out;
	}
	
	
	public void filterInitialize() {
		
		System.out.println("Initializing the Filter .  .  . ");

		// initialize
		tprev = 0.0;
		pold = process.P0();
		pnew = pold.copy();
		xref = new EstSTM(process.xref0());
		xprev = xref.longarray();
		VectorN k = new VectorN(process.numberOfStates());
		
		
		
	}

	/** Process the measurements
	 */
	public VectorN estimate(double simTime, int measNum, int whichMeas) {
		
		
		/*If necessary move to  a new time*/
		double dt = simTime- filterTime;
		
		// detect backwards time jump
		if (dt < 0.0) {
			System.out.println("backwards time jump");
			System.exit(1);
		}
		
		// propagate state and covariance to new time
		if (dt > 0.0) {
			
			while (filterTime < simTime) {
				
				double tnext = filterTime + this.dtNominal;
				
				if (tnext > simTime) {
					tnext = simTime;
					System.out.println(
					"measurement gap not an integer number of time steps");
				}
				
				//Propagate the state forward using the process model
				double[] xnew = process.propagate(filterTime, xprev, tnext);
				
				//Get the state transition matrix for the current state
				xref = new EstSTM(xnew, this.n);
				Matrix phi = xref.phi();
				
				//Calculate the process noise matrix
				Matrix q = process.Q(tnext, this.dtNominal, xref);
				
				//Propagate the covariance matrix forward
				pnew = this.propCov(pold, phi, q);
				
				//Update the filter time and reset required variables
				filterTime = tnext;
				xref.resetPhi();
				xprev = xref.longarray();
				pold = pnew.copy();
				
			}
		} 
		else {
			
			//The time is the same so don't move the covariance
			pnew = pold.copy(); // dt = 0, no change to covariance
		}
	
		
		/* perform the measurement update  Currently we feed in the position that the measurement
		 * is in the state.  This is probably not used by truly scalar measurements
		 * and can be safely set to zero in those cases.
		 */
		double y = createMeasurements.mm[measNum].observedMinusPredicted(whichMeas,xref.get(0,n));
		double r = createMeasurements.mm[measNum].R(measNum,whichMeas);
		String residualsOut = "Time:  " + simTime + "  Residual:  " + y + " Measurement Type:  " + createMeasurements.measurementTypes[measNum] + " State " + whichMeas;
		residuals.println(residualsOut);
		
		//Use the current reference trajectory to form the H matrix
		VectorN  H = createMeasurements.mm[measNum].H(whichMeas,xref.get(0,n));
		
		// compute the Kalman gain
		VectorN k = this.kalmanGain(pnew, H, r);
		
		// compute new best estimate
		VectorN xhat = k.times(y);

		// update state and covariance
		xref.update(xhat); 
		pold = this.updateCov(k, H, pnew);

		// check the update
		//double zafter = measModel.zPred(i, t, xref.state());
		//double yafter = z - zafter;
		//process.printResiduals(simTime, y, yafter);
		
		xref.resetPhi(); // re-linearize
		filterTime = simTime;
		xprev = xref.longarray();
		VectorN out = new VectorN(xref.get(0,n));
		return out;
		
	}	
}

