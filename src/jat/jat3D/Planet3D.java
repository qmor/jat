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

import jat.core.astronomy.SolarSystemBodies;
import jat.core.ephemeris.DE405Plus;
import jat.core.util.PathUtil;
import jat.core.util.jatMessages;
import jat.coreNOSA.cm.cm;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

/**
 * Planet3D class
 * 
 * @author Tobias Berthold
 */
public class Planet3D extends Body3D {
	float radius;
	Appearance app;
	Color3f Planetcolor; // planet color if texture not found
	Appearance appear;
	String images_path;
	//int divisions = 60; // number of divisions for sphere

	public Planet3D(PathUtil p, jatMessages messages, DE405Plus.body planet, float scale) {
		super();
		this.scale = scale;
		radius = (float) 1000f;
		this.messages = messages;
		images_path = p.root_path + "data/jat3D/images_hires/";
		

		
		
		
		String fileName = null;
		//String fileName= images_path + "moon.jpg";

		
		switch (planet) {
		case MERCURY:
			fileName = images_path + "mercury.jpg";
			radius = (float) cm.mercury_radius;
			Planetcolor = Colors.red;
			break;
		case VENUS:
			fileName = images_path + "venus.jpg";
			radius = (float) cm.venus_radius;
			Planetcolor = Colors.green;
			break;
		case EARTH_MOON_BARY:
			fileName = images_path + "earth.jpg";
			radius = (float) cm.earth_radius;
			Planetcolor = Colors.blue;
			break;
		case MARS:
			fileName = images_path + "mars.jpg";
			radius = (float) cm.mars_radius;
			Planetcolor = Colors.blue;
			break;
		case JUPITER:
			fileName = images_path + "jupiter.jpg";
			radius = (float) cm.jupiter_radius;
			Planetcolor = Colors.orange;
			break;
		case SATURN:
			fileName = images_path + "saturn.jpg";
			radius = (float) SolarSystemBodies.Bodies[DE405Plus.body.SATURN.ordinal()].radius;
			Planetcolor = Colors.orange;
			break;
		case MOON:
			fileName = images_path + "moon.jpg";
			radius = (float) cm.moon_radius;
			Planetcolor = Colors.blue;
			break;
		}

		
		
		
		
		
		// Create a URL for the desired page
		// If it is called from an applet, it starts with "file:" or "http:"
		// If it's an application, we need to add "file:" so that BufferReader works
		boolean application;
		if (fileName.startsWith("file") || fileName.startsWith("http"))
			application = false;
		else
			application = true;
		if (application)
			fileName = "file:" + fileName;
		messages.addln("[Planet3D] "+fileName);
		try {
			URL TextureURL = new URL(fileName);
			BufferedImage img = ImageIO.read(TextureURL);
			TextureLoader tex = new TextureLoader(img);
			TextureAttributes ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.MODULATE);
			app = createMatAppear_star(Colors.white, Colors.white, 10.0f);
			app.setTextureAttributes(ta);
			app.setTexture(tex.getTexture());
		} catch (MalformedURLException e) {
			app = createMatAppear_star(Colors.blue, Colors.white, 10.0f);
			//e.printStackTrace();
		} catch (IOException e) {
			app = createMatAppear_star(Colors.blue, Colors.white, 10.0f);
			//e.printStackTrace();
		}
		
		addChild(new Sphere(scale * radius, Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, 60, app));		
		
		
		//CreatePlanet(planet);

	
	}

	private void CreatePlanet(DE405Plus.body planet) {
		String TextureFileName = null;

		switch (planet) {
		case MERCURY:
			TextureFileName = images_path + "mercury.jpg";
			radius = (float) cm.mercury_radius;
			Planetcolor = Colors.red;
			break;
		case VENUS:
			TextureFileName = images_path + "venus.jpg";
			radius = (float) cm.venus_radius;
			Planetcolor = Colors.green;
			break;
		case EARTH_MOON_BARY:
			TextureFileName = images_path + "earth.jpg";
			radius = (float) cm.earth_radius;
			Planetcolor = Colors.blue;
			break;
		case MARS:
			TextureFileName = images_path + "mars.jpg";
			radius = (float) cm.mars_radius;
			Planetcolor = Colors.blue;
			break;
		case JUPITER:
			TextureFileName = images_path + "jupiter.jpg";
			radius = (float) cm.jupiter_radius;
			Planetcolor = Colors.orange;
			break;
		case SATURN:
			TextureFileName = images_path + "saturn.jpg";
			radius = (float) SolarSystemBodies.Bodies[DE405Plus.body.SATURN.ordinal()].radius;
			Planetcolor = Colors.orange;
			break;
		case MOON:
			TextureFileName = images_path + "moon.jpg";
			radius = (float) cm.moon_radius;
			Planetcolor = Colors.blue;
			break;
		}

//		if (Texturefilename == null) {
//			app = createMatAppear_planet(Planetcolor, Colors.white, 10.0f);
//		} else {
//
//			appear = createAppearance();
//		}

		// Create a URL for the desired page
		// If it is called from an applet, it starts with "file:" or "http:"
		// If it's an application, we need to add "file:" so that BufferReader works
		boolean application;
		if (TextureFileName.startsWith("file") || TextureFileName.startsWith("http"))
			application = false;
		else
			application = true;
		if (application)
			TextureFileName = "file:" + TextureFileName;
		messages.addln("[Planet3D] "+TextureFileName);
		try {
			URL TextureURL = new URL(TextureFileName);
			BufferedImage img = ImageIO.read(TextureURL);
			TextureLoader tex = new TextureLoader(img);
			TextureAttributes ta = new TextureAttributes();
			ta.setTextureMode(TextureAttributes.MODULATE);
			app = createMatAppear_planet(Colors.white, Colors.white, 10.0f);
			app.setTextureAttributes(ta);
			app.setTexture(tex.getTexture());
		} catch (MalformedURLException e) {
			app = createMatAppear_planet(Colors.blue, Colors.white, 10.0f);
			//e.printStackTrace();
		} catch (IOException e) {
			app = createMatAppear_planet(Colors.blue, Colors.white, 10.0f);
			//e.printStackTrace();
		}
		
		//addChild(new Sphere(scale * radius, Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, 60, app));
//		addChild(new Sphere(scale * radius, Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, 60, app));
//		addChild(new Sphere(radius, Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, divisions, appear));

//		Transform3D transform2 = new Transform3D();
//		transform2.rotX(Math.PI / 2);
//		setTransform(transform2);
//
//		set_scale(scale);

	}

//	Appearance createAppearance() {
//
//		Appearance planetAppear = new Appearance();
//
//		TextureLoader loader = new TextureLoader(TextureFileName, b);
//		ImageComponent2D image = loader.getImage();
//
//		if (image == null) {
//			System.out.println("load failed for texture: " + TextureFileName);
//		}
//
//		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
//		texture.setImage(0, image);
//		texture.setEnable(true);
//
//		texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
//		texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
//
//		planetAppear.setTexture(texture);
//
//		return planetAppear;
//	}

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

	
	static Appearance createMatAppear_star(Color3f dColor, Color3f sColor, float shine) {
		Appearance appear = new Appearance();
		Material material = new Material();
		material.setDiffuseColor(dColor);
		material.setSpecularColor(sColor);
		material.setShininess(shine);
		material.setEmissiveColor(1.f, 1.f, 1.f);
		appear.setMaterial(material);
		return appear;
	}
	
	
	// public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int
	// arg4, int arg5) {
	// return false;
	// }
}
