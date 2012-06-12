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

package jat.jat3D;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;

public class Line3D extends BranchGroup {

	public Point3f p1, p2;
	LineArray a = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);

	public Line3D() {
		super();
		p1 = new Point3f(0, 0, 0);
		p2 = new Point3f(1, 1, 1);
		a.setCoordinate(0, p1);
		a.setCoordinate(1, p2);
		a.setColor(0, Colors.gold);
		a.setColor(1, Colors.gray);
		common_to_all_constructors();
	}

	public Line3D(Point3f x1, Point3f x2) {
		super();
		a.setCoordinate(0, x1);
		a.setCoordinate(1, x2);
		a.setColor(0, Colors.gray);
		a.setColor(1, Colors.gray);
		common_to_all_constructors();
	}

	public Line3D(float x1, float x2, float x3) {
		super();
		p1 = new Point3f(0,0,0);
		p2 = new Point3f(x1, x2, x3);
		a.setCoordinate(0, p1);
		a.setCoordinate(1, p2);
		a.setColor(0, Colors.gold);
		a.setColor(1, Colors.gray);
		common_to_all_constructors();
	}

	void common_to_all_constructors() {
		setCapability(BranchGroup.ALLOW_DETACH);
		setUserData("Line");
		addChild(new Shape3D(a));
	}

	public void change(int i) {
		p1 = new Point3f(0, 0, i);
		p2 = new Point3f(1, 1, 1);
		a = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
		a.setCoordinate(0, p1);
		a.setCoordinate(1, p2);
		a.setColor(0, Colors.gold);
		a.setColor(1, Colors.gray);
		setChild(new Shape3D(a), 0);
	}

}
