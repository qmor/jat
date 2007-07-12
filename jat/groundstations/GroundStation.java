/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2007 The JAT Project. All rights reserved.
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
 * File Created on May 20, 2007
 */
package jat.groundstations;

import jat.cm.Constants;
import jat.math.MathUtils;
import jat.matvec.data.Matrix;
import jat.matvec.data.RotationMatrix;
import jat.matvec.data.VectorN;

public class GroundStation {
	
	private String _STDN = null;
	
	private String _location = null;
	
	private String _NASA = null;
	
    /** latitude in radians.
     */
	private double _lat = -9999.9;
	
    /** longitude in radians.
     */
	private double _lon = -9999.9;
	
    /** height above the ellipsoid in m.
     */	
	private double _alt = -9999.9;
	
	// WGS-84
    private double R_equ = 6378.137e3;

    // WGS-84
    private double f = 1.0/298.257223563;	
	
    /** create a Ground Station from latitude, longitude, HAE using WGS-84 Geoid.
     * @param stdn String containing the STDN code.
     * @param loc String containing the location.
     * @param nasa String containing the NASA number.
     * @param lat  double containing latitude in radians.
     * @param lon  double containing longitude in radians.
     * @param alt  double containing height above ellipsoid in meters.
     */
	public GroundStation(String stdn, String loc, String nasa, double lat, double lon, double alt){
		_STDN = stdn;
		_location = loc;
		_NASA = nasa;
		_lat = lat;
		_lon = lon;
		_alt = alt;
	}
	
    /** create a Ground Station from an ECEF Position Vector.
     * @param stdn String containing the STDN code.
     * @param loc String containing the location.
     * @param nasa String containing the NASA number.
     * @param r Geocentric position in m.
     */
    public GroundStation(String stdn, String loc, String nasa, VectorN r){
    	_STDN = stdn;
    	_location = loc;
		_NASA = nasa;
    	
        double  eps = 1000.0 * MathUtils.MACHEPS;   // Convergence criterion
        //double  eps = MathUtils.MACHEPS;   // Convergence criterion
        double  epsRequ = eps*R_equ;
        double  e2      = f*(2.0-f);        // Square of eccentricity

        double  X = r.x[0];                   // Cartesian coordinates
        double  Y = r.x[1];
        double  Z = r.x[2];
        double  rho2 = X*X + Y*Y;           // Square of distance from z-axis

        // Iteration
        double  SinPhi;
        double  ZdZ = 0.0;
        double Nh = 0.0;
        double N = 0.0;
        double dZ = e2*Z;
        double dZ_new = 0.0;

        if (r.mag() > 0.0){

            while (Math.abs(dZ-dZ_new) > epsRequ ) {
                ZdZ    =  Z + dZ;
                Nh     =  Math.sqrt ( rho2 + ZdZ*ZdZ );
                SinPhi =  ZdZ / Nh;                    // Sine of geodetic latitude
                N      =  R_equ / Math.sqrt(1.0-e2*SinPhi*SinPhi);
                dZ = dZ_new;
                dZ_new =  N*e2*SinPhi;
            }

            // Longitude, latitude, altitude
            _lon = Math.atan2 ( Y, X );
            _lat = Math.atan2 ( ZdZ, Math.sqrt(rho2) );
            _alt = Nh - N;
        }
        else{
            _lon = -9999.9;
            _lat = -9999.9;
            _alt = -9999.9;
        }
    }

    /** Return the STDN code.
     * @return STDN code.
     */
    public String getSTDN(){
        return _STDN;
    }    
    /** Return the Ground Station Location.
     * @return location.
     */
    public String getLocation(){
        return _location;
    }    
    /** Return the NASA number.
     * @return NASA number.
     */
    public String getNASA(){
        return _NASA;
    }        
    /** Return the latitude.
     * @return latitude in radians.
     */
    public double getLatitude(){
        return _lat;
    }
    /** return the longitude.
     * @return longitude in radians.
     */
    public double getLongitude(){
        return _lon;
    }
    /** return the height above the ellipsoid.
     * @return height above the ellipsoid in m.
     */
    public double getHAE(){
        return _alt;
    }


    /** computes the ECEF position vector.
     * @return ECEF position in m.
     */
    public VectorN getECEFPosition () {
        double  e2     = f*(2.0-f);        // Square of eccentricity
        double  CosLat = Math.cos(_lat);         // (Co)sine of geodetic latitude
        double  SinLat = Math.sin(_lat);
        double  CosLon = Math.cos(_lon);         // (Co)sine of geodetic latitude
        double  SinLon = Math.sin(_lon);
        double  N;
        VectorN  r = new VectorN(3);

        // Position vector

        N = this.R_equ / Math.sqrt(1.0-e2*SinLat*SinLat);
        double Nh = N + _alt;
        r.x[0] =  Nh*CosLat*CosLon;
        r.x[1] =  Nh*CosLat*SinLon;
        r.x[2] =  ((1.0-e2)*Nh)*SinLat;
        return r;
    }
    /** computes the ECEF velocity vector.
     * @return ECEF velocity in m/s.
     */
    public VectorN getECEFVelocity () { 
    	VectorN  r = this.getECEFPosition();
        VectorN  w = new VectorN(0,0,Constants.omega_e);
       	VectorN  v = w.crossProduct(r);
        return v;
    }
    /** computes the ECI position vector.
     * @param eci2ecef transformation matrix between ECI and ECEF
     * @return ECI position in m.
     */    
    public VectorN getECIPosition(Matrix eci2ecef) {
    	VectorN ecef = this.getECEFPosition();
    	VectorN out = eci2ecef.transpose().times(ecef);
    	return out;
    }
    

    /** Compute the transformation from ECEF to SEZ (topocentric horizon) reference frame.
     * @return ECEF to SEZ transformation matrix
     */
    public Matrix ECEF2SEZ (){
        double lambda = _lon;
        double phi = _lat;

        RotationMatrix M = new RotationMatrix( 3, lambda, 2, (Constants.pi/2.0 - phi));
        Matrix out = new Matrix(M);
        return  out;
    }

}
