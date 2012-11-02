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

package jat.core.astronomy;

import jat.core.ephemeris.DE405Body.body;

public class SolarSystemBodies {

	public static Body[] Bodies;

	public class Body {
		body body;
		public double radius; // km
		public double mass; // kg
		public double orbitalPeriod; // earth days

		public Body(jat.core.ephemeris.DE405Body.body body, double radius, double mass, double orbitalPeriod) {
			this.body = body;
			this.radius = radius; // km
			this.mass = mass; // kg
			this.orbitalPeriod = orbitalPeriod; // earth days
		}


	}

	public SolarSystemBodies() {

		Bodies = new SolarSystemBodies.Body[12];

		Bodies[1] = new Body(body.MERCURY, 2439.7, 330.2e21, 88);
		Bodies[2] = new Body(body.VENUS, 6051.8, 4868.5e21, 225);
		Bodies[3] = new Body(body.EARTH, 6378.1, 5972.2E21, 365);
		Bodies[4] = new Body(body.MARS, 3396., 641.85e21, 687);
		Bodies[5] = new Body(body.JUPITER, 69911, 1898600.0e21, 4332.59);
		Bodies[6] = new Body(body.SATURN, 54364, 568460.0e21, 10759.22);
		Bodies[7] = new Body(body.NEPTUNE, 24764, 1.0243e26, 60190.03);
		Bodies[8] = new Body(body.URANUS, 25559, 8.6810e25, 30799.095);
		Bodies[9] = new Body(body.PLUTO, 1153, 1.305e22, 89865);
		Bodies[10] = new Body(body.MOON, 1737.1, 7.3477e22, 27.321582);

	}

}
