package jat.core.astronomy;

import jat.core.ephemeris.DE405APL;

public class SolarSystemBody {
	DE405APL.body body;
	public double radius; // km
	public double mass; // kg
	public double orbitalPeriod; // days

	public SolarSystemBody(DE405APL.body body) {
		if (body == DE405APL.body.MERCURY) {
			this.radius = 4880.;
			this.mass = 328.5E21;
			this.orbitalPeriod = 88;
		}
	}

	public SolarSystemBody(double radius, double mass, double orbitalPeriod) {
		super();
		this.radius = radius;
		this.mass = mass;
		this.orbitalPeriod = orbitalPeriod;
	}

	public static SolarSystemBody mercuryData = new SolarSystemBody(4880., 328.5E21, 88);

}
