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
 * 
 * File Created on Aug 25, 2003
 */
package jat.matvec.data;

import java.util.ArrayList;
 
/**
 * <P>
 * The VectorList Class provides a type-conscious ArrayList for VectorN objects.
 *
 * @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
 * @version 1.0
 */
 
public class VectorList {
	private ArrayList list = new ArrayList();
	
	/**
	 * Add a vector to the list
	 * @param x VectorN to be added
	 */
	public void add(VectorN x) {
		list.add(x);
	}
	
	/**
	 * Get a VectorN from the list
	 * @param index index of the VectorN to be returned.
	 * @return VectorN
	 */
	public VectorN get(int index) {
		return (VectorN) list.get(index);
	}
	
	/**
	 * Return the size of the list.
	 * @return the size of the list.
	 */
	public int size() {
		return list.size();
	}
	
	public boolean hasNext(int index) {
		boolean out = false;
		if (index < (this.size())) {
			out = true;
		}
		return out;
	}

}
