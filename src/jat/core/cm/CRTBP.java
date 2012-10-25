package jat.core.cm;

import java.util.ArrayList;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

/**
 * @author Tobias Berthold
 * 
 *         Central Restricted Three Body Problem
 */
public class CRTBP implements FirstOrderDifferentialEquations {
	public double mu; // CRTBP parameter
	public static ArrayList<Double> time = new ArrayList<Double>();
	public ArrayList<Double> xsol = new ArrayList<Double>();
	public ArrayList<Double> ysol = new ArrayList<Double>();
	public ArrayList<Double> zsol = new ArrayList<Double>();

	
	
	public CRTBP(double mu) {
		this.mu = mu;
	}

	public void computeDerivatives(double t, double[] y, double[] yDot) {
		double xc, yc, zc, xdot, ydot, zdot;
		double r1, r2;
		double r1cubed, r2cubed;
		double fac1, fac2;

		xc = y[0];
		yc = y[1];
		zc = y[2];
		xdot = y[3];
		ydot = y[4];
		zdot = y[5];

		r1 = Math.sqrt((xc + mu) * ((xc + mu)) + yc * yc + zc * zc);
		r2 = Math.sqrt((xc - 1 + mu) * (xc - 1 + mu) + yc * yc + zc * zc);
		r1cubed = r1 * r1 * r1;
		r2cubed = r2 * r2 * r2;
		fac1 = -(1 - mu) / r1cubed;
		fac2 = -mu / r2cubed;

		// Derivatives
		yDot[0] = y[3];
		yDot[1] = y[4];
		yDot[2] = y[5];
		yDot[3] = fac1 * (xc + mu) + fac2 * (xc - 1 + mu) + 2 * ydot + xc;
		yDot[4] = fac1 * (yc) + fac2 * (yc) - 2 * xdot + yc;
		yDot[5] = fac1 * (zc) + fac2 * (zc);
	}

	
	public StepHandler stepHandler = new StepHandler() {
		public void init(double t0, double[] y0, double t) {
		}

		public void handleStep(StepInterpolator interpolator, boolean isLast) {
			double t = interpolator.getCurrentTime();
			double[] y = interpolator.getInterpolatedState();
			System.out.println(t + " " + y[0] + " " + y[1]+ " " + y[2]);

			time.add(t);
			xsol.add(y[0]);
			ysol.add(y[1]);
		}
	};
	
	@Override
	public int getDimension() {
		return 6;
	}

}
