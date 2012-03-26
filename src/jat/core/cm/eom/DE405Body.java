/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2003 National Aeronautics and Space Administration. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can
 * redistribute it and/or modify it under the terms of the
 * NASA Open Source Agreement
 * 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NASA Open Source Agreement for more details.
 *
 * You should have received a copy of the NASA Open Source Agreement
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package jat.core.cm.eom;

import jat.core.alg.integrators.*;

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
