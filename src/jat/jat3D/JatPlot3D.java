package jat.jat3D;

import jat.jat3D.behavior.jat_MouseRotate;
import jat.jat3D.behavior.jat_MouseZoom;
import jat.jat3D.behavior.jat_MouseDownUpBehavior;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

//import org.freehep.j3d.plot.PlotKeyNavigatorBehavior;

import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public abstract class JatPlot3D extends Canvas3D {
	private static final long serialVersionUID = 160335685072272523L;
	protected boolean init = false;
	protected boolean parallelProjection = false;
	private SimpleUniverse universe;
	private Bounds bounds;

	protected JatPlot3D() {
		super(SimpleUniverse.getPreferredConfiguration());
	}

	protected void init() {

		universe = new SimpleUniverse(this);

		ViewingPlatform myvp = universe.getViewingPlatform();

		Node plot = createPlot();
		BranchGroup scene = defineMouseBehaviour(plot, myvp);
		setupLights(scene); // Surface plot wants an extra light
		scene.compile();

		universe.addBranchGraph(scene);

		// look at the right spot
		Transform3D lookAt = new Transform3D();
		lookAt.lookAt(new Point3d(1.5, 1.5, 1), new Point3d(0.0, 0.0, 0.0), new Vector3d(0, 0, 1.0));
		lookAt.invert();
		myvp.getViewPlatformTransform().setTransform(lookAt);

		if (parallelProjection) {
			setProjectionPolicy(universe, parallelProjection);
		}

		init = true;
	}

	// addNotify is called when the Canvas3D is added to a container
	public void addNotify() {
		if (!init)
			init();
		super.addNotify(); // must call for Java3D to operate properly when
							// overriding
	}

	public boolean getParallelProjection() {
		return parallelProjection;
	}

	public void setParallelProjection(boolean b) {
		if (parallelProjection != b) {
			parallelProjection = b;
			setProjectionPolicy(universe, parallelProjection);
		}
	}

	/**
	 * Override to provide plot content
	 */
	protected abstract Node createPlot();

	/**
	 * Override to provide different mouse behaviour
	 */
	protected BranchGroup defineMouseBehaviour(Node scene, ViewingPlatform myvp) {
		BranchGroup bg = new BranchGroup();
		Bounds bounds = getDefaultBounds();

		TransformGroup objTransform = new TransformGroup();
		objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTransform.addChild(scene);
		bg.addChild(objTransform);

		jat_MouseRotate mouseRotate = new jat_MouseRotate();
		mouseRotate.setTransformGroup(objTransform);
		mouseRotate.setSchedulingBounds(bounds);
		bg.addChild(mouseRotate);

		MouseTranslate mouseTranslate = new MouseTranslate();
		mouseTranslate.setTransformGroup(objTransform);
		mouseTranslate.setSchedulingBounds(bounds);
		bg.addChild(mouseTranslate);

		jat_MouseZoom mouseZoom = new jat_MouseZoom();
		mouseZoom.setTransformGroup(objTransform);
		mouseZoom.setSchedulingBounds(bounds);
		bg.addChild(mouseZoom);

		jat_MouseDownUpBehavior mouseDnUp = new jat_MouseDownUpBehavior(bounds);
		bg.addChild(mouseDnUp);

		mouseRotate.setViewingPlatform(myvp);
		mouseZoom.setViewingPlatform(myvp);
		mouseDnUp.setViewingPlatform(myvp);

		// Set initial transformation
		// Transform3D trans = createDefaultOrientation();
		// objTransform.setTransform(trans);

		// Behavior keyBehavior = new PlotKeyNavigatorBehavior(objTransform,
		// .1f, 10f);
		// objTransform.addChild(keyBehavior);
		// keyBehavior.setSchedulingBounds(bounds);

		// set up a rotation animating behavior
		// rotator = setupZRotator(dynamicXform);
		// rotator.setSchedulingBounds(bounds);
		// rotator.setEnable(false);
		// dynamicXform.addChild(rotator);

		return bg;
	}

	protected void setupLights(BranchGroup root) {
		DirectionalLight lightD = new DirectionalLight();
		lightD.setDirection(new Vector3f(0.0f, -0.7f, -0.7f));
		lightD.setInfluencingBounds(getDefaultBounds());
		root.addChild(lightD);

		// This second light is added for the Surface Plot, so you
		// can see the "under" surface
		DirectionalLight lightD1 = new DirectionalLight();
		lightD1.setDirection(new Vector3f(0.0f, 0.7f, 0.7f));
		lightD1.setInfluencingBounds(getDefaultBounds());
		root.addChild(lightD1);

		AmbientLight lightA = new AmbientLight();
		lightA.setInfluencingBounds(getDefaultBounds());
		root.addChild(lightA);
	}

	/**
	 * Override to set a different initial transformation
	 */
	protected Transform3D createDefaultOrientation() {
		Transform3D trans = new Transform3D();
		trans.setIdentity();
		// trans.rotY(-Math.PI / 4.);
		trans.setTranslation(new Vector3f(0f, 0f, 0.f));
		return trans;
	}

	/**
	 * Set the projection policy for the plot - either perspective or projection
	 */
	protected void setProjectionPolicy(SimpleUniverse universe, boolean parallelProjection) {
		View view = universe.getViewer().getView();
		if (parallelProjection)
			view.setProjectionPolicy(View.PARALLEL_PROJECTION);
		else
			view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
	}

	/**
	 * Returns a bounds object that can be used for most behaviours, lighting
	 * models, etc.
	 */
	protected Bounds getDefaultBounds() {
		if (bounds == null) {
			Point3d center = new Point3d(0, 0, 0);
			bounds = new BoundingSphere(center, 10);
		}
		return bounds;
	}

	public void print_vp_t() {
		Transform3D tf = new Transform3D();
		universe.getViewingPlatform().getViewPlatformTransform().getTransform(tf);
		Vector3f vf = new Vector3f();
		tf.get(vf);
		util.print("viewing platform", vf);
	}

	public Vector3f get_vp_t() {
		Transform3D tf = new Transform3D();
		universe.getViewingPlatform().getViewPlatformTransform().getTransform(tf);
		Vector3f vf = new Vector3f();
		tf.get(vf);
		return vf;
	}

}
