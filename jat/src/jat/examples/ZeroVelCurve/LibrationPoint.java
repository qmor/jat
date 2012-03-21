/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 National Aeronautics and Space Administration and the Center for Space Research (CSR),
 * The University of Texas at Austin. All rights reserved.
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

package jat.examples.ZeroVelCurve;

import jat.alg.*;
import jat.matvec.data.*;

/**
 * <P>
 * 
 * @author Tobias Berthold
 * @version 1.0
 */ 

public class LibrationPoint extends NLESolver
{
    double mu;

    /** Runs the example.
     * @param args Arguments.
     */
    
    LibrationPoint(double mu, VectorN xin)
    {
        super(xin);
        this.mu=mu;
    }

    // find libration points
  	public VectorN evaluate(VectorN xin)
	{
	    double [] xout=new double[1];
	    xout[0]=Ux(mu,xin.x[0]);
	    return new VectorN(xout);
	}
    
    static double Ux(double mu,double x)
    {
        double t1=(-1.+x+mu);
        double t2=x+mu;
        double t1_cubed=t1*t1*t1;
        double t2_cubed=t2*t2*t2;
        
        double ret_val=x-mu*t1/Math.abs(t1_cubed)-(1-mu)*t2/Math.abs(t2_cubed);
        return ret_val;        
    }
}
