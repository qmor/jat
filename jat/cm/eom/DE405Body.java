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
 */

package jat.cm.eom;

import jat.alg.integrators.*;

/**
 * <P>
 * Compute the acceleration of a body of negligible mass due to the gravitational force
 * of the bodies whose position in time is given by the DE405 Ephemerides. 
 * Reference frame and which bodies gravitate can be selected.
 *
 * @author Tobias Berthold
 * @version 1.0
 */

public class DE405Body implements Derivatives //, Printable
{

	public DE405Body()
	{
	}

	public double[] derivs(double t, double[] x)
	{

		double dxdt[] = new double[18];


		// Derivatives
		return dxdt;
	}

}
