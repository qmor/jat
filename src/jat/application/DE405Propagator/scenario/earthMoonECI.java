package jat.application.DE405Propagator.scenario;

import jat.application.DE405Propagator.DE405PropagatorParameters;
import jat.core.astronomy.SolarSystemBodies;
import jat.core.ephemeris.DE405Body.body;
import jat.core.ephemeris.DE405Frame.frame;
import jat.core.spacetime.TimeAPL;

public class earthMoonECI extends DE405PropagatorParameters {

	public earthMoonECI() {
		super();

		// bodyGravOnOff[body.SUN.ordinal()] = true;
		bodyGravOnOff[body.EARTH.ordinal()] = true;
		bodyGravOnOff[body.MOON.ordinal()] = true;

		//Frame = frame.ECI;
		Frame = frame.MEOP;

		simulationDate = new TimeAPL(2003, 3, 1, 12, 0, 0);

		// earth orbit
//		SolarSystemBodies sb = new SolarSystemBodies();
	//	double altitude = 150.;

		
		//y0[0] = sb.Bodies[body.EARTH.ordinal()].radius+altitude;

		y0[0] = 0;
		y0[1] = 6927;
		y0[2] = 3839.69;
		y0[3] = -9.85;
		y0[4] = 0;
		y0[5] = 0;
		tf = 200000.;
		//tf = 60*60;

	}

}
