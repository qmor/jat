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
 * File Created on Jul 13, 2003
 */
 
import jat.matvec.data.*;
//import jat.math.*;
/**
 * <P>
 * The ISS_Blockage Class provides a model of GPS signal blockage due to 
 * a spherical ISS and a spherical earth.
 *
 * @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
 * @version 1.0
 */

public class ISS_Earth_Blockage implements Visible {
	
	private double elevationMask;
	private static final double issRadius = 50.0;
	private Earth_Blockage earth = new Earth_Blockage();
	
	
    /**
     * Determine if the GPS satellite is visible, including ISS blockage
     * Used by GPS Measurement Generator.
     * @param losu Line of sight unit vector
     * @param r    current position vector
     * @param rISS current position vector of the ISS
     * @return boolean true if the GPS satellite is visible
     */
	public boolean visible(VectorN losu, VectorN r, VectorN rISS) {
		
		// check elevation mask
		boolean visible = earth.visible(losu, r, rISS);
		
		// check ISS visibility
		VectorN rrel = rISS.minus(r);
		double dist = rrel.mag();
		double cone = Math.atan2(issRadius,dist);
		VectorN rel = rrel.unitVector();
		double cos_delta = rel.dotProduct(losu);
		double delta = Math.acos(cos_delta);
		if (delta < cone) {
			visible = false;
		}
		
		return visible;
	}	

}
