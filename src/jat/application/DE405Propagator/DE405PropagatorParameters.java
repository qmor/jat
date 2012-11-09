package jat.application.DE405Propagator;

import jat.core.ephemeris.DE405Body.body;
import jat.core.ephemeris.DE405Plus;
import jat.core.spacetime.TimeAPL;
import jat.core.util.PathUtil;
import jat.core.util.jatMessages;

public class DE405PropagatorParameters {
	TimeAPL simulationDate;
	int number = body.values().length;
	boolean[] planetOnOff;
	PathUtil path;
	jatMessages messages;
	DE405Plus Eph;
	// double x, y, z, vx, vy, vz;
	double[] y0 = { 200000000., 0, 0, 0, 24.2, 0 }; // initial state
	// double tf = 40000000.;
	double tf = 1000000.;

	public DE405PropagatorParameters() {
		messages = new jatMessages();
		planetOnOff = new boolean[number];
		simulationDate = new TimeAPL(2003, 3, 1, 12, 0, 0);

		for (int i = 0; i < number; i++)
			this.planetOnOff[i] = true;

		// earth orbit
		y0[0] = -1.394163164819393E8;
		y0[1] = 48928187.1;
		y0[2] = -6558.3;
		y0[4] = -30;

	}

	public DE405PropagatorParameters(boolean[] planetOnOff) {
		this.planetOnOff = planetOnOff;
	}

}
