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

// Original Code under LGPL
// http://java.freehep.org/freehep-java3d/license.html

package jat.jat3D;
import javax.media.j3d.*;
import javax.vecmath.*;


public class YAxisBuilder extends AxisBuilder
{	

	public YAxisBuilder()
	{
	}
	public YAxisBuilder(String label, String[] tickLabels, double[] tickLocations)
	{
		setLabel(label);
		setTickLabels(tickLabels);
		setTickLocations(tickLocations);
	}
	public Node getNode()
	{
		Transform3D t3d = new Transform3D();
		t3d.set(1/scale,new Vector3f(lo,-hi,lo));
		Transform3D rot = new Transform3D();
		rot.rotZ(Math.PI/2);
		t3d.mul(rot);
		TransformGroup tg = new TransformGroup(t3d);
		tg.addChild(super.getNode());
		return tg;		
	}
}
