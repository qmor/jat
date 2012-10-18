package jat.application.missionPlanTmp;

import jat.core.ephemeris.DE405Plus;
import jat.core.spacetime.TimeAPL;

public class MissionPlanParameters {
	TimeAPL simulationDate;
	int number = DE405Plus.body.values().length;
	boolean[] planetOnOff;

	public MissionPlanParameters() {
		planetOnOff=new boolean[number];
		simulationDate = new TimeAPL(2003, 3, 1, 12, 0, 0);

		for (int i = 0; i < number; i++)
			this.planetOnOff[i] = true;
	}

	public MissionPlanParameters(boolean[] planetOnOff) {
		this.planetOnOff = planetOnOff;
	}

}
