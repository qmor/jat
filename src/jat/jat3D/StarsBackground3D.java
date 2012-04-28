

package jat.jat3D;

import jat.core.astronomy.StarCatalog;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 * Planet class
 * 
 * @author Tobias Berthold
 */
public class StarsBackground3D extends BranchGroup {
	float radius;
	String Texturefilename;
	Appearance app;
	Color3f Starcolor; // Star color if texture not found
	public StarCatalog s;

	public StarsBackground3D(float scale) {
		super();
		Starcolor = Colors.blue;

		addChild(createStarSphere());

	}

	private BranchGroup createStarSphere() {

		BranchGroup bg = new BranchGroup();

		s = new StarCatalog();
		s.load();

		PointArray starfield = new PointArray(20000, PointArray.COORDINATES | PointArray.COLOR_3);

		return bg;

	}

	private BranchGroup createPointCloud() {

		BranchGroup bg = new BranchGroup();

		final java.util.Random rand = new java.util.Random();
		PointArray starfield = new PointArray(20000, PointArray.COORDINATES | PointArray.COLOR_3);
		float[] point = new float[3];
		float[] brightness = new float[3];
		for (int i = 0; i < 20000; i++) {
			point[0] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[1] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[2] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[0] *= 1.e8;
			point[1] *= 1.e8;
			point[2] *= 1.e8;

			starfield.setCoordinate(i, point);
			final float mag = rand.nextFloat();
			brightness[0] = mag;
			brightness[1] = mag;
			brightness[2] = mag;
			starfield.setColor(i, brightness);
		}
		bg.addChild(new Shape3D(starfield));

		return bg;

	}

	private Background createBackGraph() {
		Background background = new Background();
		final BoundingSphere infiniteBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
		background.setApplicationBounds(infiniteBounds);
		BranchGroup bg = new BranchGroup();

		final java.util.Random rand = new java.util.Random();
		PointArray starfield = new PointArray(20000, PointArray.COORDINATES | PointArray.COLOR_3);
		float[] point = new float[3];
		float[] brightness = new float[3];
		for (int i = 0; i < 20000; i++) {
			point[0] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[1] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[2] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			starfield.setCoordinate(i, point);
			final float mag = rand.nextFloat();
			brightness[0] = mag;
			brightness[1] = mag;
			brightness[2] = mag;
			starfield.setColor(i, brightness);
		}
		bg.addChild(new Shape3D(starfield));

		background.setGeometry(bg);
		return background;
	}

}
