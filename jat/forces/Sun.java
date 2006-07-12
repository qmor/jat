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
package jat.forces;

import jat.math.MathUtils;
import jat.matvec.data.RotationMatrix;
import jat.matvec.data.VectorN;
//import jat.matvec.data.Matrix;
import jat.spacecraft.Spacecraft;
import jat.spacetime.BodyCenteredInertialRef;
import jat.spacetime.BodyRef;
import jat.spacetime.ReferenceFrame;
import jat.spacetime.ReferenceFrameTranslater;
import jat.spacetime.Time;
import jat.timeRef.EarthRef;
//import jat.timeRef.Time;
import jat.util.FileUtil;
import jat.cm.Constants;
import jat.eph.DE405;

/**
 * Simple class to obtain the gravitational effect of the Sun.
 * 
 * @author Richard C. Page III
 *
 */
public class Sun extends GravitationalBody {

    protected DE405 jpl_ephemeris;
    
    /** The reference frame in which the Sun computes its forces.
     * It is a sun-centered J2000 inertial reference frame. */
    private ReferenceFrame sunRef = new BodyCenteredInertialRef(DE405.SUN);
    
    /**
     * Default constructor. 
     */
    public Sun() {
        super();
        this.mu = Constants.GM_Sun;
    }

    /**
     * Constructor.
     * @param grav_mass [m^3/s^2]
     * @param r position [m]
     * @param v velocity [m/s]
     */
    public Sun(double grav_mass, VectorN r, VectorN v) {
        super(grav_mass, r, v);
        String filesep = FileUtil.file_separator();
        String directory = FileUtil.getClassFilePath("jat.eph","DE405");
        directory = directory+filesep+"DE405data"+filesep;
        //jpl_ephemeris = new DE405(directory);
    }

    /**
     * Compute the acceleration.
     * @param eRef Earth Reference
     * @param sc Spacecraft Parameters and state
     */
    public VectorN acceleration(EarthRef eRef, Spacecraft sc){

      ReferenceFrameTranslater xlater = 
        new ReferenceFrameTranslater(eRef, sunRef, new Time(eRef.mjd_utc()));

      // Get a vector (in the passed in reference frame) to the
      // spacecraft and to the sun.
      VectorN r_sun = xlater.translatePointBack(new VectorN(3));
      VectorN r = sc.r();
      VectorN d = r.minus(r_sun);
      
      
      // Divide the sun vector and the sunn-to-spacecraft vector
      // by their magnitude cubed, add them, and scale by mu.
      double dmag = d.mag();
      double dcubed = dmag * dmag *dmag;
      VectorN temp1 = d.divide(dcubed);
      double smag = r_sun.mag();
      double scubed = smag * smag * smag;
      VectorN temp2 = r_sun.divide(scubed);
      VectorN sum = temp1.plus(temp2);
      VectorN accel = sum.times(-mu);
        
      return  accel;
    }
    /** Call the relevant methods to compute the acceleration.
	 * @param t Time reference class
     * @param bRef Body reference class
     * @param sc Spacecraft parameters and state
     * @return the acceleration [m/s^s]
     */
    public VectorN acceleration(Time t, BodyRef bRef, Spacecraft sc){

      ReferenceFrameTranslater xlater = 
        new ReferenceFrameTranslater(bRef, sunRef, t);

      // Get a vector (in the passed in reference frame) to the
      // spacecraft and to the sun.
      VectorN r_sun = xlater.translatePointBack(new VectorN(3));
      VectorN r = sc.r();
      VectorN d = r.minus(r_sun);
      
      
      // Divide the sun vector and the sunn-to-spacecraft vector
      // by their magnitude cubed, add them, and scale by mu.
      double dmag = d.mag();
      double dcubed = dmag * dmag *dmag;
      VectorN temp1 = d.divide(dcubed);
      double smag = r_sun.mag();
      double scubed = smag * smag * smag;
      VectorN temp2 = r_sun.divide(scubed);
      VectorN sum = temp1.plus(temp2);
      VectorN accel = sum.times(-mu);
        
      return  accel;
    }    
    
	public static void main(String[] args) throws java.io.IOException {
        String filesep = FileUtil.file_separator();
        String directory = FileUtil.getClassFilePath("jat.eph","DE405");
        directory = directory+filesep+"DE405data"+filesep;
        DE405 jpl_ephemeris = new DE405(directory);
        EarthRef eRef = new EarthRef(53157.5);
        double jd = eRef.mjd_utc()+2400000.5;
        jpl_ephemeris.planetary_ephemeris(jd);
        VectorN r_sun = jpl_ephemeris.get_pos(DE405.SUN,jd);
        VectorN r_earth = jpl_ephemeris.get_pos(DE405.EARTH,jd);
        VectorN r_body = r_sun.minus(r_earth);
        double eps = Constants.eps*MathUtils.DEG2RAD;             // Obliquity of J2000 ecliptic
        RotationMatrix R = new RotationMatrix(1, -eps);
        VectorN r_new;
        r_new = R.times(r_body);
	    r_body.print("r");
	    r_sun.print("r_sun ");
        
	    jd = eRef.mjd_utc()+2400000.5;
        jpl_ephemeris.planetary_ephemeris(jd);
//        VectorN r_sunb = jpl_ephemeris.get_pos(DE405.SUN,jd);
//        VectorN r_earth = jpl_ephemeris.get_pos(DE405.EARTH,jd);
//        VectorN r_moon = jpl_ephemeris.get_pos(DE405.MOON,jd);
//        VectorN r_sun = r_sunb.minus(r_earth);
        VectorN r_sun3 = jpl_ephemeris.get_Geocentric_Sun_pos(jd);
        r_sun3 = r_sun3.times(1000);
        r_sun3.print("r_sun3");
        
	}
    
}
