/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 The JAT Project and the Center for Space Research (CSR),
 * The University of Texas at Austin. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
/*
*  File        :   time_control.java
*  Author      :   Tobias Berthold
*  Date        :   11-13-2002
*  Change      :
*  Description :
*/

package jat.demo.vr.SolarSystem;

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;

/**
* This class is a simple behavior that invokes the KeyNavigator
* to modify the time.
*/
public class time_control extends Behavior
{
	private WakeupOnAWTEvent wakeupOne = null;
	private WakeupCriterion[] wakeupArray = new WakeupCriterion[1];
	private WakeupCondition wakeupCondition = null;
	private final float TRANSLATE = 1.e6f;
	Constellation sr;

	public time_control(Constellation sr)
	{
		this.sr = sr;
		wakeupOne = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
		wakeupArray[0] = wakeupOne;
		wakeupCondition = new WakeupOr(wakeupArray);
	}

	/**
	*  Override Behavior's initialize method to setup wakeup criteria.
	*/
	public void initialize()
	{
		// Establish initial wakeup criteria
		wakeupOn(wakeupCondition);
	}

	/**
	*  Override Behavior's stimulus method to handle the event.
	*/
	public void processStimulus(Enumeration criteria)
	{
		WakeupOnAWTEvent ev;
		WakeupCriterion genericEvt;
		AWTEvent[] events;

		while (criteria.hasMoreElements())
		{
			genericEvt = (WakeupCriterion) criteria.nextElement();

			if (genericEvt instanceof WakeupOnAWTEvent)
			{
				ev = (WakeupOnAWTEvent) genericEvt;
				events = ev.getAWTEvent();
				processAWTEvent(events);
			}
		}

		// Set wakeup criteria for next time
		wakeupOn(wakeupCondition);
	}

	/**
	*  Process a keyboard event
	*/
	private void processAWTEvent(AWTEvent[] events)
	{
		for (int n = 0; n < events.length; n++)
		{
			if (events[n] instanceof KeyEvent)
			{
				KeyEvent eventKey = (KeyEvent) events[n];

				if (eventKey.getID() == KeyEvent.KEY_PRESSED)
				{

					int keyCode = eventKey.getKeyCode();
					int keyChar = eventKey.getKeyChar();

					switch (keyCode)
					{
						case KeyEvent.VK_ADD :
							//sr.SimClock.jd+=1.;
							//sr.SimClock.dd.setTime(sr.SimClock.dd.getTime() + 86400000);
							//sr.dd.setTime(sr.dd.getTime()+86400000);
							//sr.SimClock.dd+=86400.;
							break;
						case KeyEvent.VK_SUBTRACT :
							//sr.SimClock.dd.setTime(sr.SimClock.dd.getTime() - 86400000);
							//sr.dd.setTime(sr.dd.getTime()-86400000);
							break;
					}

					//System.out.println( "Steering: "  );
				}
			}
		}
	}
}
