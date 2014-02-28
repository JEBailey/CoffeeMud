package com.planet_ink.coffee_mud.Races;

import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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
public class Shadow extends Spirit {
	public String ID() {
		return "Shadow";
	}

	public String name() {
		return "Shadow";
	}

	public long forbiddenWornBits() {
		return 0;
	}

	protected boolean destroyBodyAfterUse() {
		return true;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		if ((CMLib.flags().isInDark(affected))
				|| ((affected instanceof MOB)
						&& (((MOB) affected).location() != null) && (CMLib
							.flags().isInDark((((MOB) affected).location())))))
			affectableStats.setDisposition(affectableStats.disposition()
					| PhyStats.IS_INVISIBLE);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOLEM);
	}
}
