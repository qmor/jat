/* JAT: Java Astrodynamics Toolkit
 * 
  Copyright 2012 Tobias Berthold

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package jat.core.cm;

import jat.core.ephemeris.DE405APL;
import jat.core.math.matvec.data.VectorN;
import jat.core.math.matvec.util.LabeledMatrix;
import jat.core.spacetime.TimeAPL;

import java.io.IOException;

public class porkChopPlot {
	public LabeledMatrix A;
	public double mintotaldv, maxtotaldv;
	public double DepartureDate[];
	public double ArrivalDate[];
	int departure_planet, arrival_planet;
	public int steps;
	public float step_size;

	public porkChopPlot(int departure_planet, int arrival_planet) {
		super();
		this.departure_planet = departure_planet;
		this.arrival_planet = arrival_planet;
	}

	public void make_porkchop_plot(TimeAPL search_depart_time_start, TimeAPL search_arrival_time_start, int days,
			int steps) throws IOException {
		double totaldv;
		this.steps = steps;
		step_size = 1.f / steps;

		DE405APL my_eph = new DE405APL();
		TimeAPL search_depart_time = new TimeAPL(search_depart_time_start.mjd_utc());
		TimeAPL search_arrival_time = new TimeAPL(search_arrival_time_start.mjd_utc());
		int search_time = 86400 * days;
		int time_increment = search_time / steps;

		// System.out.print(" days " + days);
		// System.out.print(" search_time " + search_time);
		// System.out.print(" steps " + steps);
		// System.out.println(" time_increment " + time_increment);
		// search_arrival_time_start.print();

		A = new LabeledMatrix(steps, steps);
		DepartureDate = new double[steps];
		ArrivalDate = new double[steps];
		mintotaldv = 1e9;
		maxtotaldv = 0;

		A.cornerlabel = "Dep / Arr";
		String dateformat = "%tD";
		for (int i = 0; i < steps; i++) {
			A.RowLabels[i] = String.format(dateformat, search_depart_time.getCalendar());
			DepartureDate[i] = search_depart_time.mjd_utc();
			//System.out.println(search_depart_time.mjd_utc());
			for (int j = 0; j < steps; j++) {

				A.ColumnLabels[j] = String.format(dateformat, search_arrival_time.getCalendar());
				ArrivalDate[j]=search_arrival_time.mjd_utc();
				//System.out.println(search_arrival_time.mjd_utc());

				double tof = TimeAPL.minus(search_arrival_time, search_depart_time) * 86400.0;

				Lambert lambert = new Lambert(Constants.GM_Sun / 1.e9);
				VectorN r0 = my_eph.get_planet_pos(departure_planet, search_depart_time);
				VectorN v0 = my_eph.get_planet_vel(departure_planet, search_depart_time);
				// r0.print("r0");
				// v0.print("v0");
				// System.out.println("orbital velocity of earth " + v0.mag());
				VectorN rf = my_eph.get_planet_pos(arrival_planet, search_arrival_time);
				VectorN vf = my_eph.get_planet_vel(arrival_planet, search_arrival_time);
				// rf.print("rf");
				// vf.print("vf");
				// System.out.println("orbital velocity of Mars " + vf.mag());

				try {
					totaldv = lambert.compute(r0, v0, rf, vf, tof);
				} catch (LambertException e) {
					totaldv = -1;
					// System.out.println(e.message);
					// e.printStackTrace();
				}
				if (totaldv > maxtotaldv)
					maxtotaldv = totaldv;
				if (totaldv > 0 && totaldv < mintotaldv)
					mintotaldv = totaldv;
				// lambert.deltav0.print("deltav0");
				// lambert.deltavf.print("deltavf");
				// System.out.println("Total DeltaV " + totaldv);
				A.A[i][j] = totaldv;
				search_arrival_time.step_seconds(time_increment);
			}
			search_arrival_time = new TimeAPL(search_arrival_time_start.mjd_utc());
			search_depart_time.step_seconds(time_increment);
		}
		 //A.print();

	}
}
