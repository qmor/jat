package jat.application.DE405Propagator.scenario;

import jat.application.DE405Propagator.DE405PropagatorParameters;
import jat.core.astronomy.SolarSystemBodies;
import jat.core.ephemeris.DE405Body.body;
import jat.core.ephemeris.DE405Frame.frame;
import jat.core.spacetime.TimeAPL;

public class earthOrbitECI extends DE405PropagatorParameters {

	public earthOrbitECI() {
		super();

		// bodyGravOnOff[body.SUN.ordinal()] = true;
		bodyGravOnOff[body.EARTH.ordinal()] = true;
		// bodyGravOnOff[body.MOON.ordinal()] = true;

		Frame = frame.ECI;

		simulationDate = new TimeAPL(2003, 3, 1, 12, 0, 0);

		// earth orbit
		SolarSystemBodies sb = new SolarSystemBodies();
		double altitude = 150.;

		y0[0] = sb.Bodies[body.EARTH.ordinal()].radius+altitude;
		y0[1] = 0;
		y0[2] = 0;
		y0[3] = 0;
		y0[4] = 10;
		y0[5] = 0;
		tf = 60*60;

	}

}
