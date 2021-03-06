package com.planet_ink.coffee_mud.Abilities.Traps;

import java.util.Iterator;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.collections.PairVector;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class Trap_ExitRoom extends Trap_Trap {
	public String ID() {
		return "Trap_ExitRoom";
	}

	public String name() {
		return "Exit Trap";
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public PairVector<MOB, Integer> safeDirs = new PairVector<MOB, Integer>();

	protected boolean mayNotLeave() {
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CMObject copyOf() {
		Trap_ExitRoom obj = (Trap_ExitRoom) super.copyOf();
		obj.safeDirs = (PairVector) safeDirs.clone();
		return obj;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (sprung)
			return super.okMessage(myHost, msg);
		if (!super.okMessage(myHost, msg))
			return false;

		if (msg.amITarget(affected) && (affected instanceof Room)
				&& (msg.tool() instanceof Exit)) {
			final Room room = (Room) affected;
			if ((msg.targetMinor() == CMMsg.TYP_LEAVE)
					|| (msg.targetMinor() == CMMsg.TYP_FLEE)) {
				final int movingInDir = CMLib.map().getExitDir(room,
						(Exit) msg.tool());
				if ((movingInDir != Directions.DOWN)
						&& (movingInDir != Directions.UP)) {
					synchronized (safeDirs) {
						for (Iterator<Pair<MOB, Integer>> i = safeDirs
								.iterator(); i.hasNext();) {
							Pair<MOB, Integer> p = i.next();
							if (p.first == msg.source()) {
								i.remove();
								if (movingInDir == p.second.intValue())
									return true;
								spring(msg.source());
								return !mayNotLeave();
							}
						}
					}
				}
			} else if (msg.targetMinor() == CMMsg.TYP_ENTER) {
				final int movingInDir = CMLib.map().getExitDir((Room) affected,
						(Exit) msg.tool());
				if ((movingInDir != Directions.DOWN)
						&& (movingInDir != Directions.UP)) {
					synchronized (safeDirs) {
						int dex = safeDirs.indexOf(msg.source());
						if (dex >= 0)
							safeDirs.remove(dex);
						while (safeDirs.size() > room.numInhabitants() + 1)
							safeDirs.remove(0);
						safeDirs.add(new Pair<MOB, Integer>(msg.source(),
								Integer.valueOf(movingInDir)));
					}
				}
			}
		}
		return true;
	}
}
