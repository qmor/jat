package jat.jat3D;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;

public class BoundingBox3D extends BranchGroup{
	public float lo = -.5f, hi = .5f;

	public BoundingBox3D(float size) {
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
	}

	
		
}
