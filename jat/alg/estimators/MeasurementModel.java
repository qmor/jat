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
import jat.matvec.data.*;

/**
* The MeasurementModel Interface provides a mechanism to pass measurements
* and the measurement model to an estimator, such as the ExtendedKalmanFilter.
*
* @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
* @version 1.0
*/
public interface MeasurementModel {

	/**
	 * Returns the H matrix
	 * @param xref VectorN containing the current state
	 * @return H matrix (measurement state relation)
	 */
	public VectorN H (VectorN xref);

	/**
	 * Returns the measurement noise value
	 * @return measurement noise (sigma^2)
	 */
	public double R ();

	/**
	 * Returns the predicted measurement based on the current state
	 * @param index measurement index
	 * @param t time of the measurement
	 * @param xref VectorN with the current state at the measurement time
	 */
	public double zPred (int index, double t, VectorN xref);

	/**
	 * Checks for remaining measurements. 
	 * True = more measurements left to be processed.
	 * @param index measurement index.
	 */

	
	/**
	 * Returns the resiudals from the measurement
	 * @param i Used in the case of a state-type measurement, and
	 * represents the position of the state currently being updated
	 * @param state  VectorN with the current state at the measurement time
	 */
	public double observedMinusPredicted(int i, VectorN state);
	
	
	/**
	 * Returns the current measurement noise as read from the
	 * property file
	 * @param measNumber which satellite this measurement is for 
	 * @param whichState which state (in a state type measurement) this 
	 * noise is for
	 * @return
	 */
	public double R(int measNumber, int whichState);
	
	
	/**
	 * returns the H vector for the current measurement
	 * @param whichState  (used in a state vector measurement)
	 * @param state current state
	 * @return
	 */
	public VectorN H(int whichState, VectorN state);
}
