package jat.jat3D.behavior;

import jat.jat3D.JatPlot3D;

import java.awt.event.*;
import java.awt.AWTEvent;
import javax.media.j3d.*;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.ViewingPlatform;

import java.util.Enumeration;

public class jat_MouseDownUpBehavior extends Behavior {
	WakeupOnAWTEvent w1 = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
	WakeupCriterion[] w2 = { w1 };
	WakeupCondition w = new WakeupOr(w2);
	WakeupOnAWTEvent wu1 = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
	WakeupCriterion[] wu2 = { wu1 };
	WakeupCondition wu = new WakeupOr(wu2);
	public ViewingPlatform myvp;
	JatPlot3D jatPlot3D;

	public jat_MouseDownUpBehavior(Bounds bound) {
		this.setSchedulingBounds(bound);
	}

	public jat_MouseDownUpBehavior(JatPlot3D jatPlot3D) {
		this.jatPlot3D = jatPlot3D;
	}

	public void setViewingPlatform(ViewingPlatform myvp) {
		this.myvp = myvp;
	}

	public void initialize() {
		// Establish initial wakeup criteria
		wakeupOn(wu);
	}

	/**
	 * Override Behavior's stimulus method to handle the event.
	 */
	public void processStimulus(Enumeration criteria) {
		WakeupOnAWTEvent ev;
		WakeupCriterion genericEvt;
		AWTEvent[] events;

		while (criteria.hasMoreElements()) {
			genericEvt = (WakeupCriterion) criteria.nextElement();
			if (genericEvt instanceof WakeupOnAWTEvent) {
				ev = (WakeupOnAWTEvent) genericEvt;
				events = ev.getAWTEvent();
				processSwitchEvent(events);
			}
		}
	}

	/**
	 * Process a mouse up or down event
	 */
	void processSwitchEvent(AWTEvent[] events) {

		for (int i = 0; i < events.length; ++i) {
			if (events[i] instanceof MouseEvent) {
				MouseEvent event = (MouseEvent) events[i];
				if (event.getID() == MouseEvent.MOUSE_PRESSED) {
					// default
					// Set wakeup criteria for next time
					System.out.println("mouse pressed");
					wakeupOn(w);
					break;
				} else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
					System.out.println("mouse released");
					Transform3D tf = new Transform3D();
					myvp.getViewPlatformTransform().getTransform(tf);
					Vector3f vf = new Vector3f();
					tf.get(vf);
					System.out.println("distance from center " + vf.length());
					jatPlot3D.adjustbox();
					// Set wakeup criteria for next time
					wakeupOn(wu);
					break;
				}
			}
		}
	}

}
