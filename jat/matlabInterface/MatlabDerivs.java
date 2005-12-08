/*
 * Created on Apr 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jat.matlabInterface;

import jat.alg.integrators.*;
import jat.matvec.data.VectorN;

import com.mathworks.jmi.Matlab;

/**
 * @author dgaylor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

public class MatlabDerivs implements Derivatives {

	private String cmd = null;
	private Object[] inputArgs = null;

	public MatlabDerivs(String command) {
		cmd = command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jat.alg.integrators.Derivatives#derivs(double, double[])
	 */
	public double[] derivs(double t, double[] x) {
		// Set up the inputs
		int nargs = 2;
		inputArgs = new Object[nargs];
		inputArgs[0] = new Double(t);
		Double[] temp = new Double[x.length];
		for (int i = 0; i < x.length; i++) {
			temp[i] = new Double(x[i]);
		}
		inputArgs[1] = temp;
		
		// Get the return values from matlab
		double[] returnVals = null;
		try {
			returnVals = (double[])Matlab.mtFevalConsoleOutput(cmd, inputArgs, 0);
//			returnVals = (double[])Matlab.mtFeval(cmd, inputArgs, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return returnVals;
		
	}
	
	public void test(String command) {
		MatlabDerivs d = new MatlabDerivs(command);
		System.out.println("entering test");
		double t = 0.1;
		double[] x = new double[2];
		x[0] = 1.0;
		x[1] = 2.0;
		System.out.println("calling derivs");
		d.derivs(t, x);
		double[] out = d.derivs(t, x);
		VectorN output = new VectorN(out);
		output.print("final output vector");
	}		

}
