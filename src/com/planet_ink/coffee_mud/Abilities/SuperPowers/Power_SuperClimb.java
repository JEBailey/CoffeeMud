package com.planet_ink.coffee_mud.Abilities.SuperPowers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.Directions;
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
@SuppressWarnings("rawtypes")
public class Power_SuperClimb extends SuperPower {
	public String ID() {
		return "Power_SuperClimb";
	}

	public String name() {
		return "Super Climb";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "CLIMB", "SUPERCLIMB" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int usageType() {
		return USAGE_MANA | USAGE_MOVEMENT;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_CLIMBING);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		int dirCode = Directions.getDirectionCode(CMParms.combine(commands, 0));
		if (dirCode < 0) {
			mob.tell("Climb where?");
			return false;
		}
		if ((mob.location().getRoomInDir(dirCode) == null)
				|| (mob.location().getExitInDir(dirCode) == null)) {
			mob.tell("You can't climb that way.");
			return false;
		}
		if (CMLib.flags().isSitting(mob) || CMLib.flags().isSleeping(mob)) {
			mob.tell("You need to stand up first!");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_NOISYMOVEMENT,
				null);
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			success = proficiencyCheck(mob, 0, auto);

			if (mob.fetchEffect(ID()) == null) {
				mob.addEffect(this);
				mob.recoverPhyStats();
			}

			CMLib.tracking().walk(mob, dirCode, false, false);
			mob.delEffect(this);
			mob.recoverPhyStats();
			if (!success)
				mob.location().executeMsg(
						mob,
						CMClass.getMsg(mob, mob.location(), CMMsg.MASK_MOVE
								| CMMsg.TYP_GENERAL, null));
		}
		return success;
	}
}
