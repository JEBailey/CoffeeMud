package com.planet_ink.coffee_mud.Locales;

import java.util.List;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Places;

/* 
 Copyright 2000-2014 Bo Zimmerman

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
@SuppressWarnings("unchecked")
public class EndlessThinOcean extends StdThinGrid {
	public String ID() {
		return "EndlessThinOcean";
	}

	public EndlessThinOcean() {
		super();
		name = "the ocean";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask = Places.CLIMASK_WET;
	}

	public int domainType() {
		return Room.DOMAIN_OUTDOORS_WATERSURFACE;
	}

	public CMObject newInstance() {
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new EndlessOcean().newInstance();
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		UnderWater.sinkAffects(this, msg);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		switch (WaterSurface.isOkWaterSurfaceAffect(this, msg)) {
		case -1:
			return false;
		case 1:
			return true;
		}
		return super.okMessage(myHost, msg);
	}

	public String getGridChildLocaleID() {
		return "SaltWaterThinSurface";
	}

	public List<Integer> resourceChoices() {
		return UnderSaltWater.roomResources;
	}

	protected void fillExitsOfGridRoom(Room R, int x, int y) {
		super.fillExitsOfGridRoom(R, x, y);

		if ((x < 0) || (y < 0) || (y >= yGridSize()) || (x >= xGridSize()))
			return;
		// the adjacent rooms created by this method should also take
		// into account the possibility that they are on the edge.
		// it does NOT
		if (ox == null)
			ox = CMClass.getExit("Open");
		Room R2 = null;

		if ((y == 0) && (R.rawDoors()[Directions.NORTH] == null)) {
			R2 = getMakeSingleGridRoom(x, yGridSize() / 2);
			if (R2 != null)
				linkRoom(R, R2, Directions.NORTH, ox, ox);
		} else if ((y == yGridSize() - 1)
				&& (R.rawDoors()[Directions.SOUTH] == null)) {
			R2 = getMakeSingleGridRoom(x, yGridSize() / 2);
			if (R2 != null)
				linkRoom(R, R2, Directions.SOUTH, ox, ox);
		}
		if ((x == 0) && (R.rawDoors()[Directions.WEST] == null)) {
			R2 = getMakeSingleGridRoom(xGridSize() / 2, y);
			if (R2 != null)
				linkRoom(R, R2, Directions.WEST, ox, ox);
		} else if ((x == xGridSize() - 1)
				&& (R.rawDoors()[Directions.EAST] == null)) {
			R2 = getMakeSingleGridRoom(xGridSize() / 2, y);
			if (R2 != null)
				linkRoom(R, R2, Directions.EAST, ox, ox);
		}
		if (Directions.NORTHEAST < Directions.NUM_DIRECTIONS()) {
			if (((x == 0) || (y == 0))
					&& (R.rawDoors()[Directions.NORTHWEST] == null)) {
				R2 = getMakeSingleGridRoom(xGridSize() / 2, yGridSize() / 2);
				if (R2 != null)
					linkRoom(R, R2, Directions.NORTHWEST, ox, ox);
			} else if (((x == xGridSize() - 1) || (y == yGridSize() - 1))
					&& (R.rawDoors()[Directions.SOUTHEAST] == null)) {
				R2 = getMakeSingleGridRoom(xGridSize() / 2, yGridSize() / 2);
				if (R2 != null)
					linkRoom(R, R2, Directions.SOUTHEAST, ox, ox);
			}
			if (((x == xGridSize() - 1) || (y == 0))
					&& (R.rawDoors()[Directions.NORTHEAST] == null)) {
				R2 = getMakeSingleGridRoom(xGridSize() / 2, yGridSize() / 2);
				if (R2 != null)
					linkRoom(R, R2, Directions.NORTHEAST, ox, ox);
			} else if (((x == 0) || (y == yGridSize() - 1))
					&& (R.rawDoors()[Directions.SOUTHWEST] == null)) {
				R2 = getMakeSingleGridRoom(xGridSize() / 2, yGridSize() / 2);
				if (R2 != null)
					linkRoom(R, R2, Directions.SOUTHWEST, ox, ox);
			}
		}
	}
}
