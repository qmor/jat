package jat.traj;

/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2003 The JAT Project. All rights reserved.
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
 import java.io.*;
 
/**
* <P>
* The CoordinateSystem.java Class provides the means for specifying the 
* coordinate system used in creating a trajectory.
*
* @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
* @version 1.0
*/ 

public final class CoordinateSystem implements Serializable {

  private String name;

  private CoordinateSystem(String nm) { name = nm; }

  public String toString() { return name; }

  public final static CoordinateSystem
    INERTIAL = new CoordinateSystem("Inertial"),
    PLANETFIXED = new CoordinateSystem("PlanetFixed"),
    LLH = new CoordinateSystem("LLH"),
    OTHER = new CoordinateSystem("Other");


  public final static CoordinateSystem[] index =  {
    INERTIAL, PLANETFIXED, LLH, OTHER
  };


  public static void main(String[] args) {
    CoordinateSystem m = CoordinateSystem.INERTIAL;
    System.out.println(m);
    m = CoordinateSystem.index[1];
    System.out.println(m);
    System.out.println(m == CoordinateSystem.PLANETFIXED);
    System.out.println(m.equals(CoordinateSystem.INERTIAL));
  }
}