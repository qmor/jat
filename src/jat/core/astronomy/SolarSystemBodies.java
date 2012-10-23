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

import jat.core.ephemeris.DE405Plus;

public class SolarSystemBodies {

	public static Body[] Bodies;

	public class Body {
		DE405Plus.body body;
		public double radius; // km
		public double mass; // kg
		public double orbitalPeriod; // days

		public Body(jat.core.ephemeris.DE405Plus.body body, double radius, double mass, double orbitalPeriod) {
			this.body = body;
			this.radius = radius;
			this.mass = mass;
			this.orbitalPeriod = orbitalPeriod;
		}


	}

	public SolarSystemBodies() {

		Bodies = new SolarSystemBodies.Body[12];

		Bodies[1] = new Body(DE405Plus.body.MERCURY, 2439.7, 330.2e21, 88);
		Bodies[2] = new Body(DE405Plus.body.VENUS, 6051.8, 4868.5e21, 225);
		Bodies[3] = new Body(DE405Plus.body.EARTH_MOON_BARY, 6378.1, 5972.2E21, 365);
		Bodies[4] = new Body(DE405Plus.body.MARS, 3396., 641.85e21, 687);
		Bodies[5] = new Body(DE405Plus.body.JUPITER, 69911, 1898600.0e21, 4332.59);
		Bodies[6] = new Body(DE405Plus.body.SATURN, 54364, 568460.0e21, 10759.22);
		Bodies[7] = new Body(DE405Plus.body.JUPITER, 1, 1, 1);
		Bodies[8] = new Body(DE405Plus.body.NEPTUNE, 1, 1, 1);
		Bodies[9] = new Body(DE405Plus.body.URANUS, 1, 1, 1);
		Bodies[10] = new Body(DE405Plus.body.PLUTO, 1, 1, 1);
		Bodies[11] = new Body(DE405Plus.body.MOON, 1737.1, 7.3477e22, 27.321582);

	}

}
