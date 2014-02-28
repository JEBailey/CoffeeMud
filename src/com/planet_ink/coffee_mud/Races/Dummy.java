package com.planet_ink.coffee_mud.Races;

import java.util.List;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Dummy extends Doll {
	public String ID() {
		return "Dummy";
	}

	public String name() {
		return "Dummy";
	}

	public int shortestMale() {
		return 68;
	}

	public int shortestFemale() {
		return 64;
	}

	public int heightVariance() {
		return 12;
	}

	public int lightestWeight() {
		return 150;
	}

	public int weightVariance() {
		return 50;
	}

	public void affectCharState(MOB mob, CharState affectableMaxState) {
		super.affectCharState(mob, affectableMaxState);
		affectableMaxState.setHitPoints(99999);
	}

	public void affectPhyStats(Physical E, PhyStats affectableStats) {
		super.affectPhyStats(E, affectableStats);
		affectableStats.setArmor(100);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		if ((myHost instanceof MOB) && (msg.amISource((MOB) myHost))) {
			if (msg.sourceMinor() == CMMsg.TYP_DEATH) {
				msg.source().tell("You are not allowed to die.");
				return false;
			} else if (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS)) {
				msg.source().curState().setHitPoints(99999);
				((MOB) myHost).makePeace();
				Room room = ((MOB) myHost).location();
				if (room != null)
					for (int i = 0; i < room.numInhabitants(); i++) {
						MOB mob = room.fetchInhabitant(i);
						if ((mob.getVictim() != null)
								&& (mob.getVictim() == myHost))
							mob.makePeace();
					}
				return false;
			} else if ((msg.targetMinor() == CMMsg.TYP_GET)
					&& (msg.target() != null) && (msg.target() instanceof Item)) {
				msg.source().tell("Dummys cant get anything.");
				return false;
			}
		}
		return true;
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a pile of "
						+ name().toLowerCase() + " parts",
						RawMaterial.RESOURCE_WOOD));
			}
		}
		return resources;
	}
}
