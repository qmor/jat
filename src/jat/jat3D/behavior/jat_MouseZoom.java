package jat.jat3D.behavior;

import jat.jat3D.util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * MouseZoom is a Java3D behavior object that lets users control the Z axis
 * translation of an object via a mouse drag motion with the second mouse
 * button. See MouseRotate for similar usage info.
 */

public class jat_MouseZoom extends MouseBehavior {

	float z_factor = .004f;
	Vector3d translation = new Vector3d();
	public ViewingPlatform myvp;
	public TransformGroup myvpt;

	private MouseBehaviorCallback callback = null;

	public jat_MouseZoom(ViewingPlatform myvp) {
		super(0);
		this.myvp = myvp;
	}

	/**
	 * Creates a zoom behavior given the transform group.
	 * 
	 * @param transformGroup
	 *            The transformGroup to operate on.
	 */
	public jat_MouseZoom(TransformGroup transformGroup) {
		super(transformGroup);
	}

	/**
	 * Creates a default mouse zoom behavior.
	 **/
	public jat_MouseZoom() {
		super(0);
	}

	/**
	 * Creates a zoom behavior. Note that this behavior still needs a transform
	 * group to work on (use setTransformGroup(tg)) and the transform group must
	 * add this behavior.
	 * 
	 * @param flags
	 */
	public jat_MouseZoom(int flags) {
		super(flags);
	}

	/**
	 * Creates a zoom behavior that uses AWT listeners and behavior posts rather
	 * than WakeupOnAWTEvent. The behavior is added to the specified Component.
	 * A null component can be passed to specify the behavior should use
	 * listeners. Components can then be added to the behavior with the
	 * addListener(Component c) method.
	 * 
	 * @param c
	 *            The Component to add the MouseListener and MouseMotionListener
	 *            to.
	 * @since Java 3D 1.2.1
	 */
	public jat_MouseZoom(Component c) {
		super(c, 0);
	}

	/**
	 * Creates a zoom behavior that uses AWT listeners and behavior posts rather
	 * than WakeupOnAWTEvent. The behaviors is added to the specified Component
	 * and works on the given TransformGroup.
	 * 
	 * @param c
	 *            The Component to add the MouseListener and MouseMotionListener
	 *            to. A null component can be passed to specify the behavior
	 *            should use listeners. Components can then be added to the
	 *            behavior with the addListener(Component c) method.
	 * @param transformGroup
	 *            The TransformGroup to operate on.
	 * @since Java 3D 1.2.1
	 */
	public jat_MouseZoom(Component c, TransformGroup transformGroup) {
		super(c, transformGroup);
	}

	/**
	 * Creates a zoom behavior that uses AWT listeners and behavior posts rather
	 * than WakeupOnAWTEvent. The behavior is added to the specified Component.
	 * A null component can be passed to specify the behavior should use
	 * listeners. Components can then be added to the behavior with the
	 * addListener(Component c) method. Note that this behavior still needs a
	 * transform group to work on (use setTransformGroup(tg)) and the transform
	 * group must add this behavior.
	 * 
	 * @param flags
	 *            interesting flags (wakeup conditions).
	 * @since Java 3D 1.2.1
	 */
	public jat_MouseZoom(Component c, int flags) {
		super(c, flags);
	}

	public void setViewingPlatform(ViewingPlatform myvp) {
		this.myvp = myvp;
	}

	public void initialize() {
		super.initialize();
		if ((flags & INVERT_INPUT) == INVERT_INPUT) {
			z_factor *= -1;
			invert = true;
		}
	}

	/**
	 * Return the y-axis movement multipler.
	 **/
	public double getFactor() {
		return z_factor;
	}

	/**
	 * Set the y-axis movement multipler with factor.
	 **/
	public void setFactor(float factor) {
		z_factor = factor;
	}

	public void processStimulus(Enumeration criteria) {
		WakeupCriterion wakeup;
		AWTEvent[] events;
		MouseEvent evt;
		// int id;
		// int dx, dy;

		while (criteria.hasMoreElements()) {
			wakeup = (WakeupCriterion) criteria.nextElement();
			if (wakeup instanceof WakeupOnAWTEvent) {
				events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
				if (events.length > 0) {
					evt = (MouseEvent) events[events.length - 1];
					doProcess(evt);
				}
			}

			else if (wakeup instanceof WakeupOnBehaviorPost) {
				while (true) {
					synchronized (mouseq) {
						if (mouseq.isEmpty())
							break;
						evt = (MouseEvent) mouseq.remove(0);
						// consolodate MOUSE_DRAG events
						while ((evt.getID() == MouseEvent.MOUSE_DRAGGED) && !mouseq.isEmpty()
								&& (((MouseEvent) mouseq.get(0)).getID() == MouseEvent.MOUSE_DRAGGED)) {
							evt = (MouseEvent) mouseq.remove(0);
						}
					}
					doProcess(evt);
				}
			}

		}
		wakeupOn(mouseCriterion);
	}

	void doProcess(MouseEvent evt) {
		int id;
		int dx, dy;

		processMouseEvent(evt);

		if (((buttonPress) && ((flags & MANUAL_WAKEUP) == 0)) || ((wakeUp) && ((flags & MANUAL_WAKEUP) != 0))) {
			id = evt.getID();
			if ((id == MouseEvent.MOUSE_DRAGGED) && evt.isAltDown() && !evt.isMetaDown()) {

				x = evt.getX();
				y = evt.getY();

				dx = x - x_last;
				dy = y - y_last;

				if (!reset) {
					transformGroup.getTransform(currXform);

					// translation.z = dy*z_factor;

					float zoom;
					if(dy>0)
					zoom= 1.1f;
					else
						zoom= 0.9f;
					// transformX.set(translation);
					myvpt = myvp.getViewPlatformTransform();
					Transform3D Trans = new Transform3D();
					myvpt.getTransform(Trans);
					Vector3f v = new Vector3f();
					Trans.get(v);
					util.print("v", v);
					Point3d p=new Point3d();
					p.x = zoom * v.x;
					p.y = zoom * v.y;
					p.z = zoom * v.z;
					util.print("p", p);
					Transform3D lookAt = new Transform3D();
					lookAt.lookAt(p, new Point3d(0.0, 0.0, 0.0), new Vector3d(0, 0, 1.0));
					lookAt.invert();

					myvpt.setTransform(lookAt);

					if (invert) {
						// currXform.mul(currXform, transformX);
					} else {
						// currXform.mul(transformX, currXform);
					}

					transformGroup.setTransform(currXform);

					transformChanged(currXform);

					if (callback != null)
						callback.transformChanged(MouseBehaviorCallback.ZOOM, currXform);

				} else {
					reset = false;
				}

				x_last = x;
				y_last = y;
			} else if (id == MouseEvent.MOUSE_PRESSED) {
				x_last = evt.getX();
				y_last = evt.getY();
			}
		}
	}

	/**
	 * Users can overload this method which is called every time the Behavior
	 * updates the transform
	 * 
	 * Default implementation does nothing
	 */
	public void transformChanged(Transform3D transform) {
	}

	/**
	 * The transformChanged method in the callback class will be called every
	 * time the transform is updated
	 */
	public void setupCallback(MouseBehaviorCallback callback) {
		this.callback = callback;
	}

}
