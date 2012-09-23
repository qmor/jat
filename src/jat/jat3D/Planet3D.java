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

import jat.core.cm.cm;
import jat.core.ephemeris.DE405_Body_APL;

import java.awt.Button;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.media.j3d.Appearance;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

/**
 * Planet3D class
 * 
 * @author Tobias Berthold
 */
public class Planet3D extends Body3D implements ImageObserver {
	// public static final int MERCURY = 1, VENUS = 2, EARTH = 3, MARS = 4,
	// JUPITER = 5, MOON = 11;
	float radius;
	String Texturefilename;
	Appearance app;
	Color3f Planetcolor; // planet color if texture not found
	int divisions = 60; // number of divisions for sphere
	Button b; // for ImageObserver if Applet not used
	Appearance appear;

	public Planet3D(DE405_Body_APL.body planet, float scale) {
		super.scale = scale;
		b = new Button();
		CreatePlanet(planet);
	}

	private void CreatePlanet(DE405_Body_APL.body planet) {

		switch (planet) {
		case MERCURY:
			Texturefilename = images_path + "mercury.jpg";
			radius = (float) cm.mercury_radius;
			Planetcolor = Colors.red;
			break;
		case VENUS:
			Texturefilename = images_path + "venus.jpg";
			radius = (float) cm.venus_radius;
			Planetcolor = Colors.green;
			break;
		case EARTH_MOON_BARY:
			Texturefilename = images_path + "earth.jpg";
			radius = (float) cm.earth_radius;
			Planetcolor = Colors.blue;
			break;
		case MARS:
			Texturefilename = images_path + "mars.jpg";
			radius = (float) cm.mars_radius;
			Planetcolor = Colors.blue;
			break;
		case JUPITER:
			Texturefilename = images_path + "jupiter.jpg";
			radius = (float) cm.jupiter_radius;
			Planetcolor = Colors.blue;
			break;
		case MOON:
			Texturefilename = images_path + "moon.jpg";
			radius = (float) cm.moon_radius;
			Planetcolor = Colors.blue;
			break;
		}

		if (Texturefilename == null) {
			app = createMatAppear_planet(Planetcolor, Colors.white, 10.0f);
		} else {

			appear = createAppearance();
		}

		addChild(new Sphere(radius, Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, divisions, appear));

		Transform3D transform2 = new Transform3D();
		transform2.rotX(Math.PI / 2);
		setTransform(transform2);

		set_scale(scale);

	}

	Appearance createAppearance() {

		Appearance planetAppear = new Appearance();

		TextureLoader loader = new TextureLoader(Texturefilename, b);
		ImageComponent2D image = loader.getImage();

		if (image == null) {
			System.out.println("load failed for texture: " + Texturefilename);
		}

		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);
		texture.setEnable(true);

		texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);

		planetAppear.setTexture(texture);

		return planetAppear;
	}

	protected static Appearance createMatAppear_planet(Color3f dColor, Color3f sColor, float shine) {
		Appearance appear = new Appearance();
		Material material = new Material();
		material.setDiffuseColor(dColor);
		material.setSpecularColor(sColor);
		material.setShininess(shine);
		material.setEmissiveColor(0.1f, 0.1f, 0.1f);
		appear.setMaterial(material);
		return appear;
	}

	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		return false;
	}
}
