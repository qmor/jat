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
* The DistanceUnits.java Class provides the means for specifying the 
* distance units used in creating a trajectory.
*
* @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
* @version 1.0
*/ 

public final class DistanceUnits implements Serializable {

  private String name;

  private DistanceUnits(String nm) { name = nm; }

  public String toString() { return name; }

  public final static DistanceUnits
    METERS = new DistanceUnits("meters"),
    KILOMETERS = new DistanceUnits("km"),
    OTHER = new DistanceUnits("Other");


  public final static DistanceUnits[] index =  {
    METERS, KILOMETERS, OTHER
  };


  public static void main(String[] args) {
    DistanceUnits m = DistanceUnits.METERS;
    System.out.println(m);
    m = DistanceUnits.index[1];
    System.out.println(m);
    System.out.println(m == DistanceUnits.METERS);
    System.out.println(m.equals(DistanceUnits.KILOMETERS));
  }
}