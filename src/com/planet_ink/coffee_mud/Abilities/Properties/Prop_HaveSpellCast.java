package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Prop_HaveSpellCast extends Prop_SpellAdder {
	public String ID() {
		return "Prop_HaveSpellCast";
	}

	public String name() {
		return "Casting spells when owned";
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS;
	}

	protected Item myItem = null;

	public int triggerMask() {
		return TriggeredAffect.TRIGGER_GET;
	}

	public long flags() {
		return Ability.FLAG_CASTER;
	}

	public String accountForYourself() {
		return spellAccountingsWithMask("Casts ", " on the owner.");
	}

	public void setAffectedOne(Physical P) {
		if (P == null) {
			if ((lastMOB instanceof MOB)
					&& (((MOB) lastMOB).location() != null))
				removeMyAffectsFromLastMOB();
		}
		super.setAffectedOne(P);
	}

	public void executeMsg(Environmental host, CMMsg msg) {
	}

	public void affectPhyStats(Physical host, PhyStats affectableStats) {
		if (processing)
			return;
		processing = true;
		if (host instanceof Item) {
			myItem = (Item) host;

			if ((lastMOB instanceof MOB)
					&& ((myItem.owner() != lastMOB) || (myItem.amDestroyed()))
					&& (((MOB) lastMOB).location() != null))
				removeMyAffectsFromLastMOB();

			if ((lastMOB == null) && (myItem.owner() instanceof MOB)
					&& (((MOB) myItem.owner()).location() != null))
				addMeIfNeccessary(myItem.owner(), myItem.owner(), true, 0,
						maxTicks);
		}
		processing = false;
	}
}