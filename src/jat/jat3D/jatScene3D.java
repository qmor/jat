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

package jat.jat3D;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.SceneGraphObject;
import javax.media.j3d.TransformGroup;

public class jatScene3D extends TransformGroup {
	public BranchGroup jatBranchGroup;

	public jatScene3D() {
		super();
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		jatBranchGroup = new BranchGroup();
		jatBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		jatBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		jatBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		jatBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		addChild(jatBranchGroup);
	}

	/**
	 * @param b
	 *            body to add
	 * @param name
	 *            name of added body; used if body is removed
	 */
	public void add(Body3D b, String name) {
		BranchGroup bg = new BranchGroup();
		bg.setUserData(name);
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		bg.addChild(b);
		jatBranchGroup.addChild(bg);
	}

	public void remove(String name) {
		try {
			java.util.Enumeration enum1 = jatBranchGroup.getAllChildren();
			int index = 0;

			while (enum1.hasMoreElements() != false) {
				SceneGraphObject sgObject = (SceneGraphObject) enum1.nextElement();
				Object userData = sgObject.getUserData();

				if (userData instanceof String && ((String) userData).compareTo(name) == 0) {
					System.out.println("Removing: " + sgObject.getUserData());
					jatBranchGroup.removeChild(index);
				}
				index++;
			}
		} catch (Exception e) {
			// the scenegraph may not have yet been synchronized...
		}
	}
	
}
