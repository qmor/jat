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

package jat.application.EarthMoon;

import jat.core.astronomy.SolarSystemBodies;
import jat.core.ephemeris.DE405APL;
import jat.core.ephemeris.DE405Plus;
import jat.jat3D.BodyGroup3D;
import jat.jat3D.Ephemeris3D;
import jat.jat3D.Planet3D;
import jat.jat3D.RGBAxes3D;
import jat.jat3D.Star3D;
import jat.jat3D.StarsBackground3D;
import jat.jat3D.jatScene3D;
import jat.jat3D.plot3D.BoundingBox3D;
import jat.jat3D.plot3D.JatPlot3D;

import javax.media.j3d.Group;
import javax.media.j3d.Node;

public class EarthMoonPlot extends JatPlot3D {
	private static final long serialVersionUID = 599884902601254854L;
	Star3D sun;
	Planet3D moon;
	EarthMoonMain emMain;
	DE405Plus myEph; // Ephemeris class
	Planet3D[] bodies;
	Ephemeris3D[] ephemerisPlanet;

	public EarthMoonPlot(EarthMoonMain emMain) {
		super();
		this.emMain = emMain;
	}

	public Node createScene() {
		Group g = new Group();
		jatScene = new jatScene3D();
		ephemerisPlanet = new Ephemeris3D[emMain.emParam.numberOfBodies];
		bodies = new Planet3D[emMain.emParam.numberOfBodies];

		// Ephemeris data
		myEph = new DE405Plus();

		// which planets
		emMain.emParam.planetOnOff[DE405Plus.body.EARTH_MOON_BARY.ordinal()] = true;
		emMain.emParam.planetOnOff[DE405Plus.body.MOON.ordinal()] = true;

		DE405Plus.body body[] = DE405Plus.body.values();
		SolarSystemBodies sb = new SolarSystemBodies();

		// planet[0] = new Planet3D(DE405Plus.body.EARTH_MOON_BARY, 10.f);
		// jatScene.add(planet[0], DE405Plus.name[3]);
		// planet[1] = new Planet3D(DE405Plus.body.MOON, 10.f);
		// jatScene.add(planet[1], DE405Plus.name[10]);

		
		//sun = new Star3D(emMain.emParam.path,emMain.emParam.messages, .001f);
		//jatScene.add(sun, "sun");

		moon = new Planet3D(emMain.emParam.path,emMain.emParam.messages, DE405APL.body.EARTH_MOON_BARY, 1.f);
		jatScene.add(moon, "moon");

				
		
//		for (int i = 1; i < emMain.emParam.numberOfBodies; i++) {
//			if (emMain.emParam.planetOnOff[i]) {
//				bodies[i] = new Planet3D(emMain.emParam.path,emMain.emParam.messages,DE405Plus.body.fromInt(i), 1.f);
//				jatScene.add(bodies[i], DE405Plus.name[i]);
//				// if (i == 3)
//				{
//					ephemerisPlanet[i] = new Ephemeris3D(myEph, body[i], emMain.emParam.simulationDate,
//							SolarSystemBodies.Bodies[i].orbitalPeriod);
//					jatScene.add(ephemerisPlanet[i], "ephemeris" + DE405Plus.name[i]);
//				}
//			}
//		}

		jatScene.add(new RGBAxes3D(10e3), "Axis");
		// jatScene.InitialRotation.rotX(-cm.Rad(Constants.eps));
		g.addChild(jatScene);
		// initial zoom: exponent of ten times kilometers
		exponent = 4;
		StarsBackground3D s = new StarsBackground3D(emMain.emParam.path,15f);
		g.addChild(s);
		bbox = new BoundingBox3D(-.5f, .5f);
		bbox.createAxes(exponent);
		bboxgroup = new BodyGroup3D(bbox, "Box");
		g.addChild(bboxgroup);

		return g;
	}

	public void update_user() {
		// mpGUI.viewdistancefield.setText("" + mpGUI.mpp.get_vp_t().length());
	}
}
