package jat.examples.Lambert;

import java.io.IOException;

import jat.core.cm.Constants;
import jat.core.cm.Lambert;
import jat.core.cm.LambertException;
import jat.core.ephemeris.DE405APL;
import jat.core.math.matvec.data.VectorN;
import jat.core.spacetime.Time;

public class mars_odyssey {

	public void make_lambert() throws IOException {
		DE405APL my_eph = new DE405APL();
		// Mars Odyssey Mission

		Time departure_time = new Time(2001, 4, 7, 1, 1, 1);
		Time arrival_time = new Time(2001, 10, 24, 1, 1, 1);
		double tof = 200. * 86400.0;

		Lambert lambert = new Lambert(Constants.GM_Sun / 1.e9);
		VectorN r0 = my_eph.get_planet_pos(DE405APL.body.EARTH_MOON_BARY, departure_time);
		VectorN v0 = my_eph.get_planet_vel(DE405APL.body.EARTH_MOON_BARY, departure_time);
		r0.print("r0");
		v0.print("v0");
		System.out.println("orbital velocity of earth " + v0.mag());
		VectorN rf = my_eph.get_planet_pos(DE405APL.body.MARS, arrival_time);
		VectorN vf = my_eph.get_planet_vel(DE405APL.body.MARS, arrival_time);
		rf.print("rf");
		vf.print("vf");
		System.out.println("orbital velocity of Mars " + vf.mag());

		double totaldv;
		try {
			totaldv = lambert.compute(r0, v0, rf, vf, tof);
		} catch (LambertException e) {
			totaldv = -1;
			e.printStackTrace();
		}
		lambert.deltav0.print("deltav0");
		lambert.deltavf.print("deltavf");
		System.out.println("Total DeltaV " + totaldv);

	}

	public static void main(String args[]) {

		mars_odyssey p = new mars_odyssey();
		try {
			p.make_lambert();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
