package jat.jat3D;

import java.awt.event.KeyEvent;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * A behaviour for 3d plots which defines certain keyboard events This is used
 * instead of the default KeyNavigatorBehavior to work around bug 4376368 which
 * causes the CPU used to go to 100% see
 * http://developer.java.sun.com/developer/bugParade/bugs/4376368.html
 * 
 * Use the arrow keys and page up/page down to move. Hold the shift key to
 * rotate. Use the Home key to restore the default rotation.
 * 
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: PlotKeyNavigatorBehavior.java 8584 2006-08-10 23:06:37Z duns $
 * 
 */
public class PlotKeyBehavior extends Behavior {
	private Transform3D init, tgr;
	public TransformGroup transformGroup;
	private WakeupOnAWTEvent wup;
	private float step;
	private float angle;
	Transform3D transformZ=new Transform3D();
	Transform3D currXform=new Transform3D();
	public ViewingPlatform myvp;
	public TransformGroup myvpt;


	public PlotKeyBehavior(TransformGroup transformGroup, float moveStep, float rotStep) {
		super();
		this.tgr = new Transform3D();
		this.init = new Transform3D();
		this.transformGroup = transformGroup;
		this.wup = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
		this.step = moveStep;
		this.angle = (float) Math.toRadians(rotStep);
}

	public void initialize() {
		wakeupOn(wup);
		transformGroup.getTransform(init);
	}

	public void setViewingPlatform(ViewingPlatform myvp) {
		this.myvp = myvp;
	}
	
	public void processStimulus(java.util.Enumeration criteria) {
		KeyEvent event = (KeyEvent) (wup.getAWTEvent())[0];
		int keyCode = event.getKeyCode();
		boolean shift = (event.getModifiers() & event.SHIFT_MASK) != 0;

		switch (keyCode) {
		case KeyEvent.VK_UP:
			move(0f, -1f, 0f, shift);
			break;
		case KeyEvent.VK_DOWN:
			move(0f, 1f, 0f, shift);
			break;
		case KeyEvent.VK_LEFT:
			jat_rotate(1,2,3);
			//move(-1f, 0f, 0f, shift);
			break;
		case KeyEvent.VK_RIGHT:
			move(1f, 0f, 0f, shift);
			break;
		case KeyEvent.VK_PAGE_UP:
			move(0f, 0f, 1f, shift);
			break;
		case KeyEvent.VK_PAGE_DOWN:
			move(0f, 0f, -1f, shift);
			break;
		case KeyEvent.VK_HOME:
			transformGroup.setTransform(init);
		}
		wakeupOn(wup);
	}

	private void move(float x, float y, float z, boolean shift) {
		if (!shift)
			translate(x * step, y * step, z * step);
		else
			rotate(x * angle, y * angle, z * angle);
	}

	private void translate(float x, float y, float z) {
		Transform3D tr = new Transform3D();
		Vector3f vec = new Vector3f(x, y, z);
		tr.setTranslation(vec);
		transformGroup.getTransform(tgr);
		tgr.mul(tr);
		transformGroup.setTransform(tgr);
	}

	private void rotate(float x, float y, float z) {
		Transform3D tr = new Transform3D();
		if (x != 0)
			tr.rotX(x);
		if (y != 0)
			tr.rotY(y);
		if (z != 0)
			tr.rotZ(z);
		transformGroup.getTransform(tgr);
		tgr.mul(tr);
		transformGroup.setTransform(tgr);
	}

	private void jat_rotate(float x, float y, float z) {
			double x_angle = 0.1;
			double y_angle = 0.01;
			
						
			transformZ.rotZ(x_angle);

			transformGroup.getTransform(currXform);

			Matrix4d mat = new Matrix4d();
			// Remember old matrix
			currXform.get(mat);

			// Translate to origin
			currXform.setTranslation(new Vector3d(0.0, 0.0, 0.0));

//			if (invert) {
				// currXform.mul(currXform, transformX);
				// currXform.mul(currXform, transformY);
				// currXform.mul(currXform, transformZ);
	//		} else {
				// currXform.mul(transformZ, currXform);
				currXform.mul(transformZ);
				// currXform.mul(transform_axis, currXform);
				// currXform.mul(transform_axis);
		//	}

			// Set old translation back
			Vector3d translation = new Vector3d(mat.m03, mat.m13, mat.m23);
			currXform.setTranslation(translation);

			// Update xform
			transformGroup.setTransform(currXform);

			// The view position
			myvpt = myvp.getViewPlatformTransform();
			Transform3D Trans = new Transform3D();
			myvpt.getTransform(Trans);

			Vector3f v_current_cart = new Vector3f();
			Trans.get(v_current_cart);

			Vector3f v_current_spher;
			v_current_spher = CoordTransform3D.Cartesian_to_Spherical(v_current_cart);

			v_current_spher.y -= y_angle;
			Vector3f v = CoordTransform3D.Spherical_to_Cartesian(v_current_spher);

			Transform3D lookAt = new Transform3D();
			lookAt.lookAt(new Point3d(v.x, v.y, v.z), new Point3d(0.0, 0.0, 0.0), new Vector3d(0, 0, 1.0));
			lookAt.invert();

			myvpt.setTransform(lookAt);

			//transformChanged(currXform);

			//if (callback != null)
				//callback.transformChanged(MouseBehaviorCallback.ROTATE, currXform);

	}

}
