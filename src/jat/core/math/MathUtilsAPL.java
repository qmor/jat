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
package jat.core.math;

public class MathUtilsAPL {

	
	/** Machine precision for float.
	 */
	public final static float MACHEPS = 1.1920929E-7f;

	
    public static void calculateMachineEpsilonFloat() {
        float machEps = 1.0f;
 
        do {
           machEps /= 2.0f;
        }
        while ((float)(1.0 + (machEps/2.0)) != 1.0);
 
        System.out.println( "Calculated machine epsilon: " + machEps );
    }
}
