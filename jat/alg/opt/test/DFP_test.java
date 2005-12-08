/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 The JAT Project. All rights reserved.
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

package jat.alg.opt.test;

import jat.alg.opt.*;
import jat.alg.opt.test.functions.*;

/**
 * Davidon-Fletcher-Powell variable metric method
 * @author Tobias Berthold
 *
 */
public class DFP_test
{

	public static void main(String argv[])
	{
		double[] x_init=new double[2];
		
		System.out.println("Rosenbrock function, Numerical derivs, DFP");

		// create instances of the classes
		DFP_test dt = new DFP_test();
		x_init[0] = -1.2;
		x_init[1] = 1.;
		DFP dfp = new DFP(new Rosenbrock(), x_init);
		dfp.err_ods=1.e-6;
		dfp.err_dfp=1.e-6;
		dfp.eps_CD=1.e-5;
		dfp.max_it=50;
		double[] x=dfp.find_min_DFP();
		
	}
}
