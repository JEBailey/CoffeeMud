package com.planet_ink.coffee_mud.Locales;

import java.util.List;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
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
public class UnderWaterThinGrid extends StdThinGrid {
	public String ID() {
		return "UnderWaterThinGrid";
	}

	public UnderWaterThinGrid() {
		super();
		basePhyStats().setDisposition(
				basePhyStats().disposition() | PhyStats.IS_SWIMMING);
		basePhyStats.setWeight(3);
		recoverPhyStats();
		setDisplayText("Under the water");
		setDescription("");
		xsize = CMProps.getIntVar(CMProps.Int.SKYSIZE);
		ysize = CMProps.getIntVar(CMProps.Int.SKYSIZE);
		if (xsize < 0)
			xsize = xsize * -1;
		if (ysize < 0)
			ysize = ysize * -1;
		if ((xsize == 0) || (ysize == 0)) {
			xsize = 3;
			ysize = 3;
		}
		climask = Places.CLIMASK_WET;
		atmosphere = RawMaterial.RESOURCE_FRESHWATER;
	}

	public int domainType() {
		return Room.DOMAIN_OUTDOORS_UNDERWATER;
	}

	protected int baseThirst() {
		return 0;
	}

	public CMObject newInstance() {
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new UnderWaterGrid().newInstance();
	}

	public String getGridChildLocaleID() {
		return "UnderWater";
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_SWIMMING);
	}

	public List<Integer> resourceChoices() {
		return UnderWater.roomResources;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		switch (UnderWater.isOkUnderWaterAffect(this, msg)) {
		case -1:
			return false;
		case 1:
			return true;
		}
		return super.okMessage(myHost, msg);
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		UnderWater.sinkAffects(this, msg);
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
		if (R.rawDoors()[Directions.UP] == null) {
			if ((y == 0) && (rawDoors()[Directions.UP] != null)
					&& (exits[Directions.UP] != null))
				linkRoom(R, rawDoors()[Directions.UP], Directions.UP,
						exits[Directions.UP], exits[Directions.UP]);
			else if (y > 0) {
				R2 = getMakeSingleGridRoom(x, y - 1);
				if (R2 != null)
					linkRoom(R, R2, Directions.UP, ox, ox);
			} else if (x > 0) {
				R2 = getMakeSingleGridRoom(x - 1, yGridSize() - 1);
				if (R2 != null)
					linkRoom(R, R2, Directions.UP, ox, ox);
			} else {
				R2 = getMakeSingleGridRoom(xGridSize() - 1, yGridSize() - 1);
				if (R2 != null)
					linkRoom(R, R2, Directions.UP, ox, ox);
			}
		}

		if (R.rawDoors()[Directions.DOWN] == null) {
			if ((y == yGridSize() - 1) && (rawDoors()[Directions.DOWN] != null)
					&& (exits[Directions.DOWN] != null))
				linkRoom(R, rawDoors()[Directions.DOWN], Directions.DOWN,
						exits[Directions.DOWN], exits[Directions.DOWN]);
			else if (y < yGridSize() - 1) {
				R2 = getMakeSingleGridRoom(x, y + 1);
				if (R2 != null)
					linkRoom(R, R2, Directions.DOWN, ox, ox);
			} else if (x < xGridSize() - 1) {
				R2 = getMakeSingleGridRoom(x + 1, 0);
				if (R2 != null)
					linkRoom(R, R2, Directions.DOWN, ox, ox);
			}
		}

		if ((y == 0) && (R.rawDoors()[Directions.NORTH] == null)) {
			R2 = getMakeSingleGridRoom(x, yGridSize() - 1);
			if (R2 != null)
				linkRoom(R, R2, Directions.NORTH, ox, ox);
		} else if ((y == yGridSize() - 1)
				&& (R.rawDoors()[Directions.SOUTH] == null)) {
			R2 = getMakeSingleGridRoom(x, 0);
			if (R2 != null)
				linkRoom(R, R2, Directions.SOUTH, ox, ox);
		}

		if ((x == 0) && (R.rawDoors()[Directions.WEST] == null)) {
			R2 = getMakeSingleGridRoom(xGridSize() - 1, y);
			if (R2 != null)
				linkRoom(R, R2, Directions.WEST, ox, ox);
		} else if ((x == xGridSize() - 1)
				&& (R.rawDoors()[Directions.EAST] == null)) {
			R2 = getMakeSingleGridRoom(0, y);
			if (R2 != null)
				linkRoom(R, R2, Directions.EAST, ox, ox);
		}
	}
}
