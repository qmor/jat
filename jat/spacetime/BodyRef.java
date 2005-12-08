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
package jat.spacetime;

import jat.matvec.data.Matrix;
import jat.matvec.data.VectorN;

/**
 * Interface used as a standard for central body reference frame objects.  Classes
 * which implement BodyRef should represent the coordinate rotation, transformation,
 * and other reference functions commonly used in astrodynamics.
 * 
 * @author Richard C. Page III
 *
 */
public interface BodyRef {
     
    /**
     * Spin rate
     * @param t Time object
     * @return scalar spin rate [rad/s]
     */
    public double get_spin_rate(Time t);
    /**
     * Get mean radius
     * @return radius [m]
     */
    public double get_mean_radius();
    /**
     * Get gravitational constant
     * @return grav_constant [m^3/s^2]
     */
    public double get_grav_const();
    /**
     * Transformation - inertial to body frame
     * @param t Time object
     * @return Transformation matrix
     */
    public Matrix inertial_to_body(Time t);
    /**
     * Transformation - body to inertial
     * @param t Time object
     * @return Transformation matrix
     */
    public Matrix body_to_inertial(Time t);
    /**
     * Transformation from J2000 to true of date equinox
     * @param t Time object
     * @return Transformation matrix
     */
    public Matrix trueOfDate(Time t);
    /**
     * Get the current JPL vector to the Sun [km]
     * @return Vector [km]
     */
    public VectorN get_JPL_Sun_Vector();
    /**
     * Get the current JPL vector to the Moon [km]
     * @return Vector [km]
     */
    public VectorN get_JPL_Moon_Vector();
    
}
