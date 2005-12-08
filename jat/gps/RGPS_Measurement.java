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
 * File Created on May 19, 2003
 */
 
package jat.gps;
import java.io.*;


/**
* The GPS_Measurement.java Class represents a single GPS measurement.
*
* @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
* @version 1.0
*/
public class RGPS_Measurement implements Serializable {
	
	/** Epoch time of measurement in sim time (seconds) */
	private double t;
	
	/** Epoch time of measurement in MJD */
	private double t_mjd;
	
	/** Range to SV in meters */
	private double range;
	
	/** Measurement type */
	private int type;	
	
	/** SVID */
	public int svid;
	
	/** Constructor
	 * @param tsim Time of the measurement in sim time (sec).
	 * @param tmjd Time of the measurement in MJD
	 * @param r range to GPS SV in meters
	 * @param cpr carrier phase range to GPS SV in meters.
	 * @param sv SVID
	 */
	public RGPS_Measurement(double tsim, double tmjd, double r, int typ, int sv) {
		this.t = tsim;
		this.t_mjd = tmjd;
		this.range = r;
		this.type = typ;
		this.svid = sv;
	}
	
	/** Override toString()
	 * @return GPS measurement data on a single line
	 */
	public String toString() {
		String out = t+"\t"+t_mjd+"\t"+range+"\t"+type+"\t"+svid;
		return out;
	}
	
	/**
	 * Return the measurement time
	 * @return the measurement time in seconds (sim time)
	 */
	public double t(){
		return this.t;
	}
	
	/**
	 * Return the measurement time
	 * @return the measurement time in MJD
	 */
	public double t_mjd(){
		return this.t_mjd;
	}
	
	/**
	 * Return the range measurement
	 * @return double containing the range measurement
	 */
	public double range() {
		return this.range;
	}
	
	/**
	 * Return the measurement type
	 * @return int containing the measurement type
	 */
	public int type(){
		return this.type;
	}
	
	/**
	 * Return the SV index
	 * @return int containing the SV index
	 */
	public int svid() {
		return this.svid;
	}

}
