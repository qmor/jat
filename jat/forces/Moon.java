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

//import jat.cm.Constants;
import jat.eph.DE405;
//import jat.math.MathUtils;
import jat.matvec.data.VectorN;
import jat.spacecraft.Spacecraft;
import jat.spacetime.BodyRef;
import jat.spacetime.Time;
import jat.timeRef.*;
import jat.util.FileUtil;
import jat.cm.Constants;

/**
 * Simple class to model the gravitational attraction of the moon.
 * 
 * @author Richard C. Page III
 *
 */
public class Moon extends GravitationalBody {

    
    protected DE405 jpl_ephemeris;
    
    /**
     * Default constructor
     */
    public Moon() {
        super();
        this.mu = Constants.GM_Moon; 
        String filesep = FileUtil.file_separator();
        String directory;
        try{
            directory = FileUtil.getClassFilePath("jat.eph","DE405");
        }catch(Exception e){
            directory = "C:/Code/Jat/jat/eph";
        }
        directory = directory+filesep+"DE405data"+filesep;
        jpl_ephemeris = new DE405(directory);
    }
    
    /**
     * For use with matlab
     *
     */
    public Moon(boolean usematlab) {
        super();
        this.mu = Constants.GM_Moon; 
        String filesep = FileUtil.file_separator();
        String directory;
        directory = "C:/Code/Jat/jat/eph";
        directory = directory+filesep+"DE405data"+filesep;
        jpl_ephemeris = new DE405(directory);
    }

    /**
     * Constructor
     * @param grav_mass [m^3/s^2]
     * @param r position [m]
     * @param v velocity [m/s]
     */
    public Moon(double grav_mass, VectorN r, VectorN v) {
        super(grav_mass, r, v);
        String filesep = FileUtil.file_separator();
        String directory;
        try{
            directory = FileUtil.getClassFilePath("jat.eph","DE405");
        }catch(Exception e){
            directory = "C:/Code/Jat/jat/eph";
        }
        directory = directory+filesep+"DE405data"+filesep;
        //jpl_ephemeris = new DE405(directory);
    }
    /**
     * Compute the acceleration.
     * @param eRef Earth Reference
     * @param sc Spacecraft parameters and state
     */
    public VectorN acceleration(EarthRef eRef, Spacecraft sc){
        double jd_tdb = Time.TTtoTDB(eRef.mjd_tt())+2400000.5;
        //double jd = eRef.mjd_utc()+2400000.5;
        //jpl_ephemeris.planetary_ephemeris(jd_tdb);
//        VectorN r_moonb = jpl_ephemeris.get_pos(DE405.MOON,jd);
//        VectorN r_earth = jpl_ephemeris.get_pos(DE405.EARTH,jd);
//        VectorN r_moon = r_moonb.minus(r_earth);
        //VectorN r_moon = jpl_ephemeris.get_Geocentric_Moon_pos();
        VectorN r_moon = eRef.get_JPL_Moon_Vector();
        this.r_body = r_moon;
        r_moon = r_moon.times(1000);
        VectorN d = sc.r().minus(r_moon);
        double dmag = d.mag();
        double dcubed = dmag * dmag *dmag;

        VectorN temp1 = d.divide(dcubed);

        double smag = r_moon.mag();
        double scubed = smag * smag * smag;

        VectorN temp2 = r_moon.divide(scubed);

        VectorN sum = temp1.plus(temp2);
        VectorN out = sum.times(-mu);

        // Acceleration
        
        return  out;
    }
    /** Call the relevant methods to compute the acceleration.
	 * @param t Time reference class
     * @param bRef Body reference class
     * @param sc Spacecraft parameters and state
     * @return the acceleration [m/s^s]
     */
    public VectorN acceleration(Time t, BodyRef bRef, Spacecraft sc){
        double jd_tdb = Time.TTtoTDB(t.mjd_tt())+2400000.5;
        //double jd = eRef.mjd_utc()+2400000.5;
        //jpl_ephemeris.planetary_ephemeris(jd_tdb);
//        VectorN r_moonb = jpl_ephemeris.get_pos(DE405.MOON,jd);
//        VectorN r_earth = jpl_ephemeris.get_pos(DE405.EARTH,jd);
//        VectorN r_moon = r_moonb.minus(r_earth);
        //VectorN r_moon = jpl_ephemeris.get_Geocentric_Moon_pos();
        VectorN r_moon = bRef.get_JPL_Moon_Vector();
        this.r_body = r_moon;
        r_moon = r_moon.times(1000);
        VectorN d = sc.r().minus(r_moon);
        double dmag = d.mag();
        double dcubed = dmag * dmag *dmag;

        VectorN temp1 = d.divide(dcubed);

        double smag = r_moon.mag();
        double scubed = smag * smag * smag;

        VectorN temp2 = r_moon.divide(scubed);

        VectorN sum = temp1.plus(temp2);
        VectorN out = sum.times(-mu);

        // Acceleration
        
        return  out;
    }
    public void print(EarthRef ref){
        jpl_ephemeris.planetary_ephemeris(ref.mjd_tt()+2400000.5);
        VectorN r = jpl_ephemeris.get_Geocentric_Moon_pos();
        r.print("JPL Moon  "+ref.mjd_utc());
    }
    
	public static void main(String[] args) throws java.io.IOException {
        String filesep = FileUtil.file_separator();
        String directory = FileUtil.getClassFilePath("jat.eph","DE405");
        directory = directory+filesep+"DE405data"+filesep;
        DE405 jpl_ephemeris = new DE405(directory);
        EarthRef eRef = new EarthRef(53157.5);
        //double jd = eRef.mjd_tt()+2400000.5;
        double mjd = 2453159.00000000-2400000.5;
        EarthRef mjd_to_tt = new EarthRef(mjd);
        double jd = mjd_to_tt.mjd_tt()+2400000.5;
        //jd = 2453523;
        jpl_ephemeris.planetary_ephemeris(jd);
        VectorN r_moon2 = jpl_ephemeris.get_Geocentric_Moon_pos();
        r_moon2 = r_moon2.times(1000);
        r_moon2.print("r_moon2 - get_Geocentric_Moon_pos()");
        
        VectorN r_moon = jpl_ephemeris.get_pos(DE405.MOON,jd);
        VectorN r_earth = jpl_ephemeris.get_pos(DE405.EARTH,jd);
        //VectorN r_body = r_moon.minus(r_earth);
        //double eps = Constants.eps*MathUtils.DEG2RAD;             // Obliquity of J2000 ecliptic
        //RotationMatrix R = new RotationMatrix(1, -eps);
        //VectorN r_new;
        //r_new = R.times(r_body);
        
        System.out.println("jd : "+jd);
        //r_body.print("r_body_bary");
        r_moon = r_moon.times(1000);
        r_moon.print("r_moon - get_pos(jd)");
        
        VectorN r_eci = new VectorN(36607358.256,  -20921.723703,  0);
        //VectorN r_eci = new VectorN(-4453783.586,  -5038203.756,  -426384.456);
        VectorN r_to_moon2 = r_eci.minus(r_moon2);
        VectorN r_to_moon = r_eci.minus(r_moon);
        r_to_moon.print("r_to_moon_from_sc");
        r_to_moon2.print("r_to_moon(2)_from_sc");
      
        
	}
    
}
