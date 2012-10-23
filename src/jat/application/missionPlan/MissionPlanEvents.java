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

package jat.application.missionPlan;

import jat.core.cm.TwoBodyAPL;
import jat.core.ephemeris.DE405Plus;
import jat.core.spacetime.TimeAPL;
import jat.coreNOSA.cm.Constants;
import jat.coreNOSA.cm.Lambert;
import jat.coreNOSA.cm.LambertException;
import jat.coreNOSA.cm.cm;
import jat.coreNOSA.math.MatrixVector.data.VectorN;
import jat.coreNOSA.spacetime.CalDate;
import jat.jat3D.Colors;
import jat.jat3D.Sphere3D;
import jat.jat3D.TwoBodyOrbit3D;
import jat.jat3D.behavior.jat_Rotate;
import jat.jat3D.plot3D.Rainbow3f;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.vecmath.Vector3f;

class MissionPlanEvents implements ActionListener, ItemListener {

	MissionPlanMain mpmain;
	MissionPlanGUI mpGUI;
	jat_Rotate jat_rotate;
	public Timer timer;
	int i;
	int time_advance = 10; // seconds
	DE405Plus myEph; // Ephemeris class
	Flight f;
	Rainbow3f rainbow = new Rainbow3f();
	ManageFlightsDialog myDialog;
	boolean directionDown;

	public MissionPlanEvents(MissionPlanMain mpmain) {
		this.mpmain = mpmain;
		timer = new Timer(50, this);
		// timer = new Timer(1000, this);
		// timer.start();
	}

	public void actionPerformed(ActionEvent ev) {
		this.mpGUI = mpmain.mpGUI;
		this.jat_rotate = mpmain.mpPlot.jat_rotate;
		i++;

		if (ev.getSource() == mpGUI.btn_stop) {
			time_advance = 0;
		}

		if (ev.getSource() == mpGUI.btn_rewind) {
			int sign = (int) Math.signum(time_advance);
			switch (sign) {
			case -1:
				time_advance *= 2;
				break;
			case -0:
				time_advance = -10;
				break;
			case 1:
				time_advance /= 2;
				break;
			}
		}
		if (ev.getSource() == mpGUI.btn_forward) {
			int sign = (int) Math.signum(time_advance);
			switch (sign) {
			case -1:
				time_advance /= 2;
				break;
			case -0:
				time_advance = 10;
				break;
			case 1:
				time_advance *= 2;
				break;
			}
		}

		if (ev.getSource() == mpGUI.btnAddFlight) {
			// System.out.println("add flight");
			AddFlightDialog myDialog = new AddFlightDialog(mpmain);
			// Get the resulting dates and delta-v's and add trajectory to
			// plot
			if (myDialog.p.pReturn.DepartureDate > 0.) {
				try {
					f = new Flight();
					f.flightName = "flight" + i;
					// retrieve selected values from dialog and store
					f.departure_planet = myDialog.p.pReturn.departure_planet;
					f.departurePlanetName = DE405Plus.name[f.departure_planet.ordinal()];
					f.arrival_planet = myDialog.p.pReturn.arrival_planet;
					f.arrivalPlanetName = DE405Plus.name[f.arrival_planet.ordinal()];
					f.departureDate = new TimeAPL(myDialog.p.pReturn.DepartureDate);
					f.arrivalDate = new TimeAPL(myDialog.p.pReturn.ArrivalDate);

					f.mu = Constants.GM_Sun / 1.e9;

					f.tof = TimeAPL.minus(f.arrivalDate, f.departureDate) * 86400.0;

					f.lambert = new Lambert(Constants.GM_Sun / 1.e9);
					myEph = mpmain.mpPlot.myEph;
					f.r0 = myEph.get_planet_pos(f.departure_planet, f.departureDate);
					f.v0 = myEph.get_planet_vel(f.departure_planet, f.departureDate);
					f.rf = myEph.get_planet_pos(f.arrival_planet, f.arrivalDate);
					f.vf = myEph.get_planet_vel(f.arrival_planet, f.arrivalDate);
					try {
						f.totaldv = f.lambert.compute(f.r0, f.v0, f.rf, f.vf, f.tof);
						// apply the first delta-v
						f.dv0 = f.lambert.deltav0;
						f.v0 = f.v0.plus(f.dv0);
						// System.out.println(ssmain.flightList.size());

						TwoBodyAPL temp = new TwoBodyAPL(f.mu, f.r0, f.v0);
						f.t0_on_orbit = temp.t_from_ta();
						VectorN rot_r0 = ecliptic_obliquity_rotate(f.r0);
						VectorN rot_v0 = ecliptic_obliquity_rotate(f.v0);
						f.color = rainbow.colorFor(10 * mpmain.flightList.size());
						f.orbit = new TwoBodyOrbit3D(f.mu, rot_r0, rot_v0, f.t0_on_orbit, f.t0_on_orbit + f.tof,
								f.color);
						mpmain.mpPlot.jatScene.add(f.orbit, f.flightName);
						f.satellite = new Sphere3D(5000000.f, Colors.gold);
						mpmain.mpPlot.jatScene.add(f.satellite, f.satelliteName);
						mpmain.flightList.add(f);

					} catch (LambertException e) {
						// totaldv = -1;
						// System.out.println(e.message);
						// e.printStackTrace();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		} // End of button Add Flight pressed

		if (ev.getSource() == mpGUI.btnManageFlights) {
			// System.out.println("manage flights");
			myDialog = new ManageFlightsDialog(mpmain);
		}

		// Periodic timer events
		//System.out.println("alive");
		CalDate caldate;
		if (mpGUI.realtime_chk.isSelected()) {
			// Date related functions
			// sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
			// dd = new Date(java.lang.System.currentTimeMillis());
			// dd.setTime(java.lang.System.currentTimeMillis());
			// cal = new GregorianCalendar();
			// cal.setTime(dd);
			// jd = cm.juliandate(cal);
			// long cur_mil;
			// cur_mil = System.currentTimeMillis();
			// cal.setTimeInMillis(cur_mil);

			Calendar cal = Calendar.getInstance();
			int Y, M, D, h, m, s;
			Y = cal.get(Calendar.YEAR);
			M = cal.get(Calendar.MONTH);
			D = cal.get(Calendar.DAY_OF_MONTH);
			h = cal.get(Calendar.HOUR_OF_DAY);
			m = cal.get(Calendar.MINUTE);
			s = cal.get(Calendar.SECOND);
			caldate = new CalDate(Y, M, D, h, m, s);
			mpmain.mpParam.simulationDate = new TimeAPL(caldate);
		} else {
			mpmain.mpParam.simulationDate.step_seconds(time_advance);
			mpGUI.timestepfield.setText("" + time_advance);
			caldate = new CalDate(mpmain.mpParam.simulationDate.mjd_utc());
		}

		mpGUI.yearfield.setText("" + caldate.year());
		mpGUI.monthfield.setText("" + caldate.month());
		mpGUI.dayfield.setText("" + caldate.day());
		mpGUI.hourfield.setText("" + caldate.hour());
		mpGUI.minutefield.setText("" + caldate.min());
		mpGUI.secondfield.setText("" + (int) caldate.sec());

		update_scene(mpmain.mpParam.simulationDate);

		if (mpGUI.chckbxCameraRotate.isSelected()) {
			Vector3f sphereCoord = jat_rotate.getV_current_sphere();
			// System.out.println(sphereCoord.x + " " + sphereCoord.y + " " +
			// sphereCoord.z);

			if (sphereCoord.z > 1)
				directionDown = true;
			if (sphereCoord.z < -1)
				directionDown = false;
			if (directionDown)
				jat_rotate.jat_rotate(.01f, -.002f);
			else
				jat_rotate.jat_rotate(.01f, .002f);
		}
	}// End of ActionPerformed

	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();

		if (source == mpGUI.realtime_chk) {
			if (mpGUI.realtime_chk.isSelected()) {
				mpGUI.btn_stop.setEnabled(false);
				mpGUI.btn_forward.setEnabled(false);
				mpGUI.btn_rewind.setEnabled(false);
			} else {
				mpGUI.btn_stop.setEnabled(true);
				mpGUI.btn_forward.setEnabled(true);
				mpGUI.btn_rewind.setEnabled(true);
			}
		}
	}

	void update_scene(TimeAPL mytime) {
		myEph = mpmain.mpPlot.myEph;
		myEph.setFrame(DE405Plus.frame.HEE);
		// myEph.setFrame(DE405Plus.frame.ICRF);
		DE405Plus.body body[] = DE405Plus.body.values();
		
		try {
			
			for (int i = 1; i < 5; i++) {
				// mpmain.mpPlot.planet[i].set_position(ecliptic_obliquity_rotate(myEph.get_planet_pos(body[i],
				// mytime)));
				mpmain.mpPlot.planet[i].set_position(myEph.get_planet_pos(body[i], mytime));
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(mpGUI, "DE405 Ephemeris data file not found.");
			e.printStackTrace();
			System.exit(0);
			// e.printStackTrace();
		}

		for (int i = 0; i < mpmain.flightList.size(); i++) {

			double satelliteTime;
			Flight f = mpmain.flightList.get(i);
			satelliteTime = TimeAPL.minus(mytime, f.departureDate);

			mpmain.mpGUI.viewdistancefield.setText("" + satelliteTime);
			if (satelliteTime > 0 && satelliteTime < f.tof / 86400.) {
				f.satellite.set_position(f.orbit.sat.position(satelliteTime * 86400));

			} else
				f.satellite.set_position(0, 0, 0);

		}
	}

	VectorN ecliptic_obliquity_rotate(VectorN r) {
		VectorN returnval = new VectorN(3);
		double x, y, z, eps, c, s;
		x = r.get(0);
		y = r.get(1);
		z = r.get(2);
		eps = cm.Rad(Constants.eps);
		c = Math.cos(eps);
		s = Math.sin(eps);
		returnval.x[0] = x;
		returnval.x[1] = c * y + s * z;
		returnval.x[2] = -s * y + c * z;
		return returnval;
	}
}
