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
import jat.spacetime.BodyRef;
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

        VectorN r_sun = eRef.get_JPL_Sun_Vector();
        r_sun = r_sun.times(1000.0);
        
        //* July 7 2005 - buggy
        //r_body = eRef.sunVector();
//        double jd = eRef.mjd_utc()+2400000.5;
//        VectorN r_sun = eRef.get_JPL_Sun_Vector();
//        r_sun = r_sun.times(1000);
        
        VectorN d = sc.r().minus(r_sun);
        double dmag = d.mag();
        double dcubed = dmag * dmag *dmag;

        VectorN temp1 = d.divide(dcubed);

        double smag = r_sun.mag();
        double scubed = smag * smag * smag;

        VectorN temp2 = r_sun.divide(scubed);

        VectorN sum = temp1.plus(temp2);
        VectorN out = sum.times(-mu);
        
        return  out;
    }
    /** Call the relevant methods to compute the acceleration.
	 * @param t Time reference class
     * @param bRef Body reference class
     * @param sc Spacecraft parameters and state
     * @return the acceleration [m/s^s]
     */
    public VectorN acceleration(Time t, BodyRef bRef, Spacecraft sc){

        VectorN r_sun = bRef.get_JPL_Sun_Vector();
        r_sun = r_sun.times(1000.0);
        
        //* July 7 2005 - buggy
        //r_body = eRef.sunVector();
//        double jd = eRef.mjd_utc()+2400000.5;
//        VectorN r_sun = eRef.get_JPL_Sun_Vector();
//        r_sun = r_sun.times(1000);
        
        VectorN d = sc.r().minus(r_sun);
        double dmag = d.mag();
        double dcubed = dmag * dmag *dmag;

        VectorN temp1 = d.divide(dcubed);

        double smag = r_sun.mag();
        double scubed = smag * smag * smag;

        VectorN temp2 = r_sun.divide(scubed);

        VectorN sum = temp1.plus(temp2);
        VectorN out = sum.times(-mu);
        
        return  out;
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
        VectorN r_sun2 = eRef.get_JPL_Sun_Vector();
	    r_body.print("r");
	    r_sun2.print("r_sun ");
        
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
