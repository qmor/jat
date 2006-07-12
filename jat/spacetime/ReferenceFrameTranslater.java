/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2005 Emergent Space Technologies Inc. All rights reserved.
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
package jat.spacetime;

import jat.matvec.data.Matrix;
import jat.matvec.data.VectorN;

/**
 * Class used to perform translation or transformation between two
 * reference frames.  Whereas the ReferenceFrame class understands and 
 * simulates the complex dynamics of the reference frame, the translater
 * only works in a given time instant.
 */
public class ReferenceFrameTranslater {
    
    /** The matrix to transform from the source reference frame
     * to the target reference frame. */
    private Matrix xform;
    
    /** The origin of the target reference frame in terms of the
     * source reference frame. */
    private VectorN origin;
    
    /**
     * Constructs a translater that translates from the source
     * reference frame to the target.
     * @param source the reference frame that you have coordinates in
     * @param target the reference frame you want to translate to
     * @param t the time at which translation will be done
     * @throws IllegalArgumentException if there is no translation
     * between the two reference frames
     */
    public ReferenceFrameTranslater(ReferenceFrame source, 
        ReferenceFrame target, Time t)
    {
      // We check with both the source and the target to see if
      // they know how to translate.
      ReferenceFrameTranslater xlater = source.getTranslater(target, t);
      if (xlater == null) {
        xlater = target.getTranslater(source, t);
        if (xlater == null) {
          throw new IllegalArgumentException("No known translation from " +
              source.getClass().getName() + " to " + 
              target.getClass().getName());
        }
        xlater = xlater.reverse();
      }
      
      // This is a constructor.  Copy the found values into this class.
      xform = xlater.xform;
      origin = xlater.origin;
    }
    
    /**
     * Construct a translater that does no translation.  Just spits
     * out what it is given.  This is useful when translating into
     * the same reference frame.
     */
    public ReferenceFrameTranslater()
    {
      xform = null;
      origin = null;
    }

    /**
     * Construct a translater
     * @param transformationMatrix the transformation matrix for 
     * transforming directions from the source reference frame to
     * the target reference frame
     * @param originDifference the origin of the target reference frame
     * in terms of the source reference frame
     * @param eulerAngles the euler angles of the target reference frame
     * in terms of the source reference frame (in radians)
     */
    public ReferenceFrameTranslater(Matrix transformationMatrix,
        VectorN originDifference)
    {
      xform = transformationMatrix;
      origin = originDifference;
    }
    
    /**
     * Translates a given position's coordinates from the source 
     * reference frame to the coordinates in the target reference frame
     * @param coords x,y, and z coordinates in the source reference frame
     * (in meters)
     * @return x,y, and z coordinates in the target reference frame
     * (in meters)
     */
    public VectorN translatePoint(VectorN coords)
    {
      if ((origin == null) && (xform == null)) {
        coords = coords.copy();
      }
      else {
        coords = (origin == null ? coords : coords.minus(origin));
        coords = (xform == null ? coords : xform.times(coords));
      }
      return coords;
    }
    
    /**
     * Translates a given position's coordinates from the TARGET 
     * reference frame to the coordinates in the SOURCE reference frame
     * @param coords x,y, and z coordinates in the target reference frame
     * (in meters)
     * @return x,y, and z coordinates in the source reference frame
     * (in meters)
     */
    public VectorN translatePointBack(VectorN coords)
    {
      if ((origin == null) && (xform == null)) {
        coords = coords.copy();
      }
      else {
        coords = (xform == null ? coords : xform.transpose().times(coords));
        coords = (origin == null ? coords : coords.plus(origin));
      }
      return coords;
    }
    
    /**
     * Transforms a given direction's coordinates in the source
     * reference frame to the coordinates in the target reference frame.
     * There is no translation to account for difference in
     * reference frames' origins  
     * @param coords x,y, and z coordinates in the source reference frame
     * @return x,y, and z coordinates in the target reference frame
     */
    public VectorN transformDirection(VectorN coords) 
    {
      return (xform == null ? coords.copy() : xform.times(coords));
    }
    
    /**
     * Transforms a given direction's coordinates from the TARGET
     * reference frame to the coordinates in the SOURCE reference frame.
     * There is no translation to account for difference in
     * reference frames' origins  
     * @param coords x,y, and z coordinates in the target reference frame
     * @return x,y, and z coordinates in the source reference frame
     */
    public VectorN transformDirectionBack(VectorN coords) 
    {
      return (xform == null ? coords.copy() : xform.transpose().times(coords));
    }
    
    /**
     * Transforms a given quaternian's angles in the source
     * reference frame to the angles in the target reference frame.
     * @param quat quaternian in the source reference frame
     * @return quaternian in the target reference frame
     */
    public double[] transformAngles(double[] quat)
    {
      // TODO: How do I transform quaternians?
      return null;
    }
    
    /**
     * Transforms a given quaternian's angles in the TARGET
     * reference frame to the angles in the SOURCE reference frame.
     * @param quat quaternian in the target reference frame
     * @return quaternian in the source reference frame
     */
    public double[] transformAnglesBack(double[] quat)
    {
      // TODO: How do I transform quaternians?
      return null;
    }   
    
    /**
     * Return a translater that translates from TARGET reference frame
     * to SOURCE reference frame, instread of source to target.
     * translater.translatePoint() == translater.reverse().translatePointBack()
     * @return a reverse translater
     */
    public ReferenceFrameTranslater reverse() {
      VectorN newOrigin = null;
      Matrix newXform = (xform == null ? null : xform.transpose());
      if (origin != null) {
        // We have to reverse and transform the origin
        newOrigin = origin.times(-1);
        if (xform != null) {
          newOrigin = xform.times(newOrigin);
        }
      }
      return new ReferenceFrameTranslater(newXform, newOrigin);
    }
}