/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2005 Emergent Space Technologies Inc. All rights reserved.
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
package jat.matlabInterface;

import com.mathworks.jmi.Matlab;

/**
 * @author Richard C. Page III
 *
 */
public class MatlabFunc {

    private String cmd;
    
    public MatlabFunc(String func){
        cmd = func;
    }
    
    public double[] call(Object[] inputArgs){
        double[] returnVals = null;
		try {
			returnVals = (double[])Matlab.mtFevalConsoleOutput(cmd, inputArgs, 0);
//			returnVals = (double[])Matlab.mtFeval(cmd, inputArgs, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return returnVals;
    }
    
    public static void main(String[] args) {
        Double test = new Double(1.0);
        double[] out = null;
        MatlabFunc test_func = new MatlabFunc("test_func");
        Double[] input = new Double[2];
        input[0] = test;
        input[1] = new Double(42);
        out = test_func.call(input);
        
    }
}
