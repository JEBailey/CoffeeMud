package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMStrings;

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
public class Prop_StatAdjuster extends Property {
	public String ID() {
		return "Prop_StatAdjuster";
	}

	public String name() {
		return "Char Stats Adjusted MOB";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected static final int[] all25 = new int[CharStats.CODES.instance()
			.total()];
	static {
		for (int i : CharStats.CODES.BASE())
			all25[i] = 0;
	}
	protected int[] stats = all25;

	public boolean bubbleAffect() {
		return false;
	}

	public long flags() {
		return Ability.FLAG_ADJUSTER;
	}

	public int triggerMask() {
		return TriggeredAffect.TRIGGER_ALWAYS;
	}

	public String accountForYourself() {
		return "Stats Trainer";
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		for (int i : CharStats.CODES.BASE())
			if (stats[i] != 0) {
				int newStat = affectableStats.getStat(i) + stats[i];
				final int maxStat = affectableStats.getMaxStat(i);
				if (newStat > maxStat)
					newStat = maxStat;
				else if (newStat < 1)
					newStat = 1;
				affectableStats.setStat(i, newStat);
			}
	}

	public void setMiscText(String newMiscText) {
		super.setMiscText(newMiscText);
		if (newMiscText.length() > 0) {
			stats = new int[CharStats.CODES.TOTAL()];
			for (int i : CharStats.CODES.BASE())
				stats[i] = CMParms.getParmInt(newMiscText,
						CMStrings.limit(CharStats.CODES.NAME(i), 3), 0);
		}
	}

}
