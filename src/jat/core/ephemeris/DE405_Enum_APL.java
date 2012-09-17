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

package jat.core.ephemeris;

public class DE405_Enum_APL {

	public enum body {
		MERCURY, VENUS, EARTH_MOON_BARY, MARS, JUPITER, SATURN, URANUS, NEPTUNE, PLUTO;
	};

	public static String[] name = { "Mercury", "Venus", "Earth_Moon_Bary", "Mars", "Jupiter", "Saturn", "Uranus",
			"Neptune", "Pluto" };

}
