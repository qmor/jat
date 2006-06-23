package jat.gps;

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
 * File Created on Jun 20, 2003
 */
 
import jat.matvec.data.*;

/**
* The GPSutils.java Class provides general utility functions needed for GPS
* data processing.
*
* @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
* @version 1.0
*/

public class GPS_Utils {
	
    /** Speed of Light in m/s from IAU 1976.
     */
    public static final double c = 299792458.0;

	/** GPS L1 frequency */
	public static final double L1_freq = 1575.42E+06;
	
	/** GPS L1 wavelength in meters */
	public static final double lambda = c / L1_freq;
	
	
	
	/** compute the line of sight vector from a spacecraft to a GPS satellite
	 * @param r the spacecraft position vector
	 * @param rGPS the GPS SV position vector
	 * @return the line of sight vector
	 */	
	public static VectorN lineOfSight(VectorN r, VectorN rGPS) {
		VectorN los = rGPS.minus(r);
		return los;
	}
	
	/** Iterative solution for time of transmission
	 * @param t_mjd time of reception in MJD
	 * @param sv GPS_SV object
	 * @return time of transmission in MJD
	 * 
	 */	
	public static double transmitTime(double t_mjd, GPS_SV sv, VectorN r){
		int maxit = 500;
		int i = 0;
		double ts_new = 0.0;
		double ts_old = t_mjd;
		double eps = 1.0E-16;
		double diff = Math.abs(ts_new - ts_old);
		while ((diff > eps)&&(i < maxit)) {
			VectorN rGPS = sv.rECI(ts_old);
			VectorN los = GPS_Utils.lineOfSight(r, rGPS);
			double rho = los.mag();
			ts_new = t_mjd - rho/(86400.0*c);
			diff = Math.abs(ts_new - ts_old);
			ts_old = ts_new;
			i = i + 1;
			if (i >= maxit){
				System.out.println("transmitTime too many iterations, diff = "+ diff);
				if (diff > 1.0E-10) {
					throw new RuntimeException("GPS_Utils.transmitTime too many iterations at t_mjd = "+t_mjd+", diff = "+ diff);
				}
			}
		}
		return ts_new;		
	}
	
	/** Iterative solution for time of transmission
	 * @param t_mjd time of reception in MJD
	 * @param sv GPS_SV object
	 * @return time of transmission in MJD
	 */	
	public static double transmitTime(double t_mjd, GPS_SV sv, VectorN r, RotationMatrix ECF2ECI){
		int maxit = 500;
		int i = 0;
		double ts_new = 0.0;
		double ts_old = t_mjd;
		double eps = 1.0E-16;
		double diff = Math.abs(ts_new - ts_old);
		while ((diff > eps)&&(i < maxit)) {
			VectorN rGPS = sv.rWGS84(ts_old);
			rGPS = ECF2ECI.transform(rGPS);
			VectorN los = GPS_Utils.lineOfSight(r, rGPS);
			double rho = los.mag();
			ts_new = t_mjd - rho/(86400.0*c);
			diff = Math.abs(ts_new - ts_old);
			ts_old = ts_new;
			i = i + 1;
			if (i >= maxit){
				System.out.println("transmitTime too many iterations, diff = "+ diff);
				if (diff > 1.0E-10) {
					throw new RuntimeException("GPS_Utils.transmitTime too many iterations at t_mjd = "+t_mjd+", diff = "+ diff);
				}
			}
		}
		return ts_new;		
	}
	
	/** compute the range rate
	 * @param rho the range vector
	 * @param v the spacecraft ECI velocity vector at reception time
	 * @param vGPS the GPS SV ECI velocity vector at transmission time
	 */	
	public static double rangeRate(VectorN rho, VectorN v, VectorN vGPS) {
		
		// compute the relative velocity
		VectorN vrel = vGPS.minus(v);
		double num = rho.dotProduct(vrel);
		
		double timeCorrection = rho.dotProduct(vGPS)/c;
		double denom = rho.mag() + timeCorrection;
		
		double range_rate = num / rho.mag();
		return range_rate;
	}
	
	/** Compute the line of sight declination angle (angle from zenith).
	 * @param r spacecraft position vector
	 * @param rGPS GPS SV position vector
	 * @return the angle between the GPS line of sight and zenith in radians.
	 */ 	
	public static double declination(VectorN r, VectorN rGPS) {
		double dot = r.dotProduct(rGPS);
		double rmag = r.mag();
		double rGPSmag = rGPS.mag();
		double dec = Math.acos(dot/(rmag*rGPSmag));
		return dec;
	}

	/** Compute the line of sight elevation angle (angle from local horizontal).
	 * @param r spacecraft position vector
	 * @param rGPS GPS SV position vector
	 * @return the angle between the GPS line of sight and horizon in radians.
	 */ 		
	public static double elevation(VectorN r, VectorN rGPS) {
		double dec = GPS_Utils.declination(r, rGPS);
		double pi2 = Math.PI/2.0;
		double el = pi2 - dec;
		return el;
	}
		 
}
