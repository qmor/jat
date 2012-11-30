package jat.application.DE405Propagator.ParameterSet;

import jat.application.DE405Propagator.DE405PropagatorParameters;
import jat.core.astronomy.SolarSystemBodies;
import jat.core.ephemeris.DE405Body.body;
import jat.core.ephemeris.DE405Frame.frame;
import jat.core.spacetime.TimeAPL;

public class earthMoonMEOP extends DE405PropagatorParameters {

	public earthMoonMEOP() {
		super();

		Name = "earthMoonMEOP";
		// bodyGravOnOff[body.SUN.ordinal()] = true;
		bodyGravOnOff[body.EARTH.ordinal()] = true;
		bodyGravOnOff[body.MOON.ordinal()] = true;

		// Frame = frame.HEE;
		// Frame = frame.ECI;
		Frame = frame.MEOP;

		simulationDate = new TimeAPL(2003, 2, 28, 13, 30, 0);

		// earth orbit to moon and back
		y0[0] = -7000.;
		y0[1] = 0.;
		y0[2] = 0;
		y0[3] = 0.;
		y0[4] = -10.6;
		y0[5] = 0;
		tf = 700000.;

	}

}

// y0[0] = 0;
// y0[1] = 7000.;
// y0[2] = 0;
// y0[3] = -10.47;
// y0[4] = 0;
// y0[5] = -1.5;
// tf = 1000000.;

// y0[0] = 0;
// y0[1] = 6927;
// y0[2] = 3839.69;
// SolarSystemBodies sb = new SolarSystemBodies();
// double altitude = 150.;
// y0[0] = sb.Bodies[body.EARTH.ordinal()].radius+altitude;