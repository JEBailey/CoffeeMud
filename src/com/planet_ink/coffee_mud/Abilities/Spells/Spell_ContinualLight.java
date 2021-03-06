package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
@SuppressWarnings("rawtypes")
public class Spell_ContinualLight extends Spell {
	public String ID() {
		return "Spell_ContinualLight";
	}

	public String name() {
		return "Continual Light";
	}

	public String displayText() {
		return "(Continual Light)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	protected int canTargetCode() {
		return CAN_MOBS | CAN_ITEMS;
	}

	protected int canAffectCode() {
		return CAN_MOBS | CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		if (!(affected instanceof Room))
			affectableStats.setDisposition(affectableStats.disposition()
					| PhyStats.IS_LIGHTSOURCE);
		if (CMLib.flags().isInDark(affected))
			affectableStats.setDisposition(affectableStats.disposition()
					- PhyStats.IS_DARK);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		Room room = ((MOB) affected).location();
		if (canBeUninvoked())
			room.show(mob, null, CMMsg.MSG_OK_VISUAL,
					"The light above <S-NAME> dims.");
		super.unInvoke();
		if (canBeUninvoked())
			room.recoverRoomStats();
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if ((mob == target)
					&& (!CMLib.flags().canBeSeenBy(mob.location(), mob)))
				return super.castingQuality(mob, target,
						Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = null;
		if (commands.size() == 0)
			target = mob;
		else
			target = getAnyTarget(mob, commands, givenTarget,
					Wearable.FILTER_UNWORNONLY);

		if (target == null)
			return false;
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			String str = "^S<S-NAME> invoke(s) a continual light toward(s) <T-NAMESELF>!^?";
			if (!(target instanceof MOB))
				str = "^S<S-NAME> invoke(s) a continual light into <T-NAME>!^?";
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), str);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, Ability.TICKS_FOREVER);
				mob.location().recoverRoomStats(); // attempt to handle
													// followers
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke light, but fail(s).");

		return success;
	}
}
