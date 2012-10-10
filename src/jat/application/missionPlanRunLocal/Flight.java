package jat.application.missionPlanRunLocal;

import javax.vecmath.Color3f;

import jat.core.ephemeris.DE405APL.body;
import jat.core.spacetime.TimeAPL;
import jat.coreNOSA.cm.Lambert;
import jat.coreNOSA.math.MatrixVector.data.VectorN;
import jat.jat3D.Sphere3D;
import jat.jat3D.TwoBodyOrbit3D;

public class Flight {
	String flightName;
	Color3f color;
	String satelliteName;
	public double mu;
	body departure_planet;
	String departurePlanetName;
	String arrivalPlanetName;
	body arrival_planet;
	TimeAPL departureDate;
	TimeAPL arrivalDate;
	double tof;
	Lambert lambert;
	VectorN r0, v0, rf, vf, dv0, dvf;
	public double totaldv;
	double t0_on_orbit;
	TwoBodyOrbit3D orbit;
	Sphere3D satellite;

}
