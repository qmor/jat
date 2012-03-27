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

import jat.core.alg.*;
import jat.core.matvec.data.*;

/**
 * <P>
 * 
 * @author Tobias Berthold
 * @version 1.0
 */ 

public class ZeroVelCurve extends NLESolver
{
    double mu;
    double C;    // Jacobi Constant

    /** Runs the example.
     * @param args Arguments.
     */
    
    ZeroVelCurve(double mu, double C, VectorN xin)
    {
        super(xin);
        this.mu=mu;
        this.C=C;
    }

    // find zero velocity curve points
  	public VectorN evaluate(VectorN xin)
	{
	    double [] xout=new double[1];
	    xout[0]=ZV_func(xin.x[0],xin.x[1]);
	    return new VectorN(xout);
	}
    
    double ZV_func(double x, double y)
    {
        double t1=(-1.+x+mu);
        double t2=x+mu;
        double t1_1=Math.sqrt((-0.8+x)*(-0.8+x)+y*y);
        double t2_1=Math.sqrt((0.2+x)*(0.2+x)+y*y);

        double ret=x*x+y*y+0.4/t1_1+1.6/t2_1-C;
        
        
        //double ret=x*x+y*y+0.4/Math.sqrt((-0.8+x)*(-0.8+x)+y*y)+1.6/Math.sqrt((0.2+x)*(0.2+x)+y*y)-3.8;
        return ret;
    }
    
}
    /*
    // simple problem
  	public VectorN evaluate(VectorN xin)
	{
	    double [] xout=new double[1];
	    xout[0]=(xin.x[0]-2.)*(xin.x[0]-2.)-3.;
	    return new VectorN(xout);
	}
	*/

