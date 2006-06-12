/*
 * JAT: Java Astrodynamics Toolkit
 * 
 * Created on Mar 1, 2006
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
package jat.spacetime;

import jat.matvec.data.Matrix;
import jat.matvec.data.VectorN;

/**
 * TODO Javadoc
 * @author Richard C. Page III
 */
public class SolarBarycenterRef implements BodyRef {

	/* (non-Javadoc)
	 * @see jat.spacetime.BodyRef#get_spin_rate(jat.spacetime.Time)
	 */
	public double get_spin_rate(Time t) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see jat.spacetime.BodyRef#get_mean_radius()
	 */
	public double get_mean_radius() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see jat.spacetime.BodyRef#get_grav_const()
	 */
	public double get_grav_const() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see jat.spacetime.BodyRef#inertial_to_body(jat.spacetime.Time)
	 */
	public Matrix inertial_to_body(Time t) {
		// TODO Auto-generated method stub
		return new Matrix(3);
	}

	/* (non-Javadoc)
	 * @see jat.spacetime.BodyRef#body_to_inertial(jat.spacetime.Time)
	 */
	public Matrix body_to_inertial(Time t) {
		// TODO Auto-generated method stub
		return new Matrix(3);
	}

	/* (non-Javadoc)
	 * @see jat.spacetime.BodyRef#trueOfDate(jat.spacetime.Time)
	 */
	public Matrix trueOfDate(Time t) {
		// TODO Auto-generated method stub
		return new Matrix(3);
	}

	/* (non-Javadoc)
	 * @see jat.spacetime.BodyRef#get_JPL_Sun_Vector()
	 */
	public VectorN get_JPL_Sun_Vector() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jat.spacetime.BodyRef#get_JPL_Moon_Vector()
	 */
	public VectorN get_JPL_Moon_Vector() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
	}
}