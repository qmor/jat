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

import jat.core.ephemeris.DE405APL;
import jat.jat3D.BodyGroup3D;
import jat.jat3D.Ephemeris3D;
import jat.jat3D.Planet3D;
import jat.jat3D.RGBAxes3D;
import jat.jat3D.Star3D;
import jat.jat3D.StarsBackground3D;
import jat.jat3D.jatScene3D;
import jat.jat3D.plot3D.BoundingBox3D;
import jat.jat3D.plot3D.JatPlot3D;
import jat.core.astronomy.SolarSystemBodies;

import javax.media.j3d.Group;
import javax.media.j3d.Node;

public class MissionPlanPlot extends JatPlot3D {
	private static final long serialVersionUID = 599884902601254854L;
	Star3D sun;
	MissionPlanMain mpmain;
	DE405APL myEph; // Ephemeris class
	Planet3D[] planet;
	Ephemeris3D[] ephemerisPlanet;

	public MissionPlanPlot(MissionPlanMain mpmain) {
		super();
		this.mpmain = mpmain;
	}

	public Node createScene() {
		Group g = new Group();
		jatScene = new jatScene3D();
		sun = new Star3D(10.f);
		jatScene.add(sun, "sun");
		ephemerisPlanet = new Ephemeris3D[10];
		planet = new Planet3D[10];

		// Ephemeris data
		myEph = new DE405APL();

		DE405APL.body body[] = DE405APL.body.values();
		SolarSystemBodies sb = new SolarSystemBodies();

		for (int i = 1; i < 7; i++) {
			planet[i] = new Planet3D(body[i], 1000.f);
			jatScene.add(planet[i], DE405APL.name[i]);
			//if (i == 3)
			{
				ephemerisPlanet[i] = new Ephemeris3D(myEph, body[i], mpmain.mpParam.simulationDate,
						SolarSystemBodies.Bodies[i].orbitalPeriod);
				jatScene.add(ephemerisPlanet[i], "ephemeris" + DE405APL.name[i]);
			}
		}

		jatScene.add(new RGBAxes3D(100000000), "Axis");
		// jatScene.InitialRotation.rotX(-cm.Rad(Constants.eps));
		g.addChild(jatScene);
		// initial zoom: exponent of ten times kilometers
		exponent = 8;
		StarsBackground3D s = new StarsBackground3D(15f);
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
