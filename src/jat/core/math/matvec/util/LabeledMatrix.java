/* JAT: Java Astrodynamics Toolkit
 * 
  Copyright 2012 Tobias Berthold

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package jat.core.math.matvec.util;

import jat.core.math.matvec.data.Matrix;
import jat.core.math.matvec.data.VectorN;

public class LabeledMatrix extends Matrix {

	String[] RowLabels;
	String[] ColumnLabels;
	
		
	public LabeledMatrix(int m, int n) {
		super(m, n);
		RowLabels=new String[m];
		ColumnLabels=new String[m];	
	}

	public LabeledMatrix(int n) {
		super(n);
		// TODO Auto-generated constructor stub
	}

	public LabeledMatrix(Matrix in) {
		super(in);
		// TODO Auto-generated constructor stub
	}

	public LabeledMatrix(int m, int n, double s) {
		super(m, n, s);
		// TODO Auto-generated constructor stub
	}

	public LabeledMatrix(VectorN X) {
		super(X);
		// TODO Auto-generated constructor stub
	}

	public LabeledMatrix(double[][] B) {
		super(B);
		// TODO Auto-generated constructor stub
	}

	public LabeledMatrix(double[][] B, int m, int n) {
		super(B, m, n);
		// TODO Auto-generated constructor stub
	}

	public LabeledMatrix(double[] vals, int m) {
		super(vals, m);
		// TODO Auto-generated constructor stub
	}

}
