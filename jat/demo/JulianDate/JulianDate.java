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

package jat.demo.JulianDate;

import jat.cm.*;

/**
 * @author Tobias Berthold
 *  Date        :   7-7-2002
 *  Description :   JAT Julian date demo
 *
 */
public class JulianDate
{
	public static void main(String argv[])
	{
		System.out.print("The Julian date on 1-1-2001 at 12:00pm is ");
		System.out.println(cm.juliandate(2001, 1, 1, 12, 0, 0));
	}
}
