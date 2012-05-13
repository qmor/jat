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

import javax.media.j3d.BranchGroup;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;

public class BoundingBox3D extends Body3D{
	float boxsize = 1.f;
	float lo = -boxsize/2; //-.5f
	float hi = boxsize/2;    //.5f;
	public AxisBuilder xAxis;
	private AxisBuilder yAxis;
	private ZAxisBuilder zAxis;
	public String xAxisLabel = "X [10^0 km]";
	private String yAxisLabel = "Y Axis";
	private String zAxisLabel = "Z Axis";	 

	public BoundingBox3D(float size, int exponent) {
		IndexedLineArray xCube = new IndexedLineArray(8, IndexedLineArray.COORDINATES, 24);
		lo=-size/2;
		hi=size/2;
		
		// Set coordinates for the cube //
		xCube.setCoordinate(0, new Point3d(lo, hi, lo));
		xCube.setCoordinate(1, new Point3d(hi, hi, lo));
		xCube.setCoordinate(2, new Point3d(hi, lo, lo));
		xCube.setCoordinate(3, new Point3d(lo, lo, lo));
		xCube.setCoordinate(4, new Point3d(lo, hi, hi));
		xCube.setCoordinate(5, new Point3d(hi, hi, hi));
		xCube.setCoordinate(6, new Point3d(hi, lo, hi));
		xCube.setCoordinate(7, new Point3d(lo, lo, hi));

		// Construct the vertical //
		xCube.setCoordinateIndex(0, 0);
		xCube.setCoordinateIndex(1, 1);
		xCube.setCoordinateIndex(2, 3);
		xCube.setCoordinateIndex(3, 2);
		xCube.setCoordinateIndex(4, 4);
		xCube.setCoordinateIndex(5, 5);
		xCube.setCoordinateIndex(6, 7);
		xCube.setCoordinateIndex(7, 6);

		// Construct the lower side //
		xCube.setCoordinateIndex(8, 0);
		xCube.setCoordinateIndex(9, 4);
		xCube.setCoordinateIndex(10, 4);
		xCube.setCoordinateIndex(11, 7);
		xCube.setCoordinateIndex(12, 7);
		xCube.setCoordinateIndex(13, 3);
		xCube.setCoordinateIndex(14, 3);
		xCube.setCoordinateIndex(15, 0);

		// Construct the upper side //
		xCube.setCoordinateIndex(16, 1);
		xCube.setCoordinateIndex(17, 5);
		xCube.setCoordinateIndex(18, 5);
		xCube.setCoordinateIndex(19, 6);
		xCube.setCoordinateIndex(20, 6);
		xCube.setCoordinateIndex(21, 2);
		xCube.setCoordinateIndex(22, 2);
		xCube.setCoordinateIndex(23, 1);
		setCapability(BranchGroup.ALLOW_DETACH);
		setUserData("BoundingBox");
		addChild(new Shape3D(xCube));
		
		double[] tick = { 0, boxsize / 4f, boxsize / 2f, 3 * boxsize / 4f, boxsize };
		String[] labels = { String.valueOf(-boxsize / 2f), String.valueOf(-boxsize / 4f), "0",
				String.valueOf(3 * -boxsize / 4f), String.valueOf(boxsize / 2f) };
		xAxis = new XAxisBuilder(xAxisLabel, labels, tick);
		yAxis = new YAxisBuilder(yAxisLabel, labels, tick);
		zAxis = new ZAxisBuilder(zAxisLabel, labels, tick);
		xAxis.lo = -boxsize / 2f;
		xAxis.hi = boxsize / 2f;
		yAxis.lo = -boxsize / 2f;
		yAxis.hi = boxsize / 2f;
		zAxis.lo = -boxsize / 2f;
		zAxis.hi = boxsize / 2f;
		xAxis.setLabel("X 10^" + exponent + " km");
		xAxis.apply();

		xAxis.apply();
		yAxis.apply();
		zAxis.apply();

		addChild(xAxis.getNode());
		addChild(yAxis.getNode());
		addChild(zAxis.getNode());

	
	}

}
