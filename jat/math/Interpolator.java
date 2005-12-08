/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 The JAT Project and the Center for Space Research (CSR),
 * The University of Texas at Austin. All rights reserved.
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

package jat.math;

/**
 * Linearly interpolates values in double arrays
 *
 * @author Tobias Berthold <P>
 * Date        :   11-19-2002
 */
public class Interpolator
{
    private double[] data;
	private double[] vari;      // var is actually a keyword, so can't use
	private int length;
	private int lower, upper;

    /**
     * Create an interpolator where data holds the given values of the function at variable 
	 * @param variable independent variable
	 * @param data dependent variable
	 */
	public Interpolator(double[] variable, double[] data)
    {
        this.data=data;
        this.vari=variable;
        length=data.length-1;
    }

    /**
     * Return the interpolated value of the function at x
	 * @param x independent variable
	 * @return interpolated value at x
	 */
	public double get_value(double x)
    {
        if(x<vari[0]) return 0.;    // too low: really should catch exception.. later
        if(x==vari[0]) return data[0];    // on lower boundary
        if(x==vari[length]) return data[length];    // on upper boundary
        if(x>vari[length]) return 0.;    // too high: really should catch exception..

        // Find bracketing values
        int i;
        for(i=0;i<length;i++)
            if(x>vari[i] && x<vari[i+1])
            {
                //System.out.println("x "+x+" i "+i);
                lower =i;
                upper =i+1;
                break;
            }

        double delta_y=data[upper]-data[lower];
        double delta_x=vari[upper]-vari[lower];
        // If two consecutive independent var. values are equal:
        if (delta_x<1.e-20)
            return data[lower];
        double slope=delta_y/delta_x;
        return data[lower]+slope*(x-vari[lower]);
    }
}
