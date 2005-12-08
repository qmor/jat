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
 */
package jat.demo.vr.EarthMoon;

import java.awt.*;
import javax.media.j3d.*;

public class ControlPanel extends java.awt.Panel
{
    //public Label label;

    //private TextArea instructions;


    public ControlPanel(Group group)
    {
        super();

        setName("ControlPanel");
        setLayout(null);
        setBackground(java.awt.Color.lightGray);
        setSize(568, 150);
        /*
        label = new java.awt.Label();
        label.setName("sites");
        label.setText("Sites:");
        //label.setBounds(8, 2, 125, 30);
        add(label, label.getName());
        label.setAlignment(Label.LEFT);
        */

		//{{INIT_CONTROLS
		setLayout(new BorderLayout(0,0));
		setBackground(java.awt.Color.lightGray);
		setSize(0,0);
		label.setText("text");
		add(label);
		//}}
	}

   public void initialize()
   {
   }
	//{{DECLARE_CONTROLS
	java.awt.Label label = new java.awt.Label();
	//}}
}

