package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
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
public class Chant_Moonbeam extends Chant {
	public String ID() {
		return "Chant_Moonbeam";
	}

	public String name() {
		return "Moonbeam";
	}

	public String displayText() {
		return "(Moonbeam)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_MOONSUMMONING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
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
					"The moonbeam shining down from above <S-NAME> dims.");
		super.unInvoke();
		room.recoverRoomStats();
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (!CMLib.flags().canBeSeenBy(mob.location(), mob))
				return super.castingQuality(mob, target,
						Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;

		if (target.fetchEffect(this.ID()) != null) {
			target.tell("The moonbeam is already with you.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (!success) {
			return beneficialWordsFizzle(mob, mob.location(),
					"<S-NAME> chant(s) for a moonbeam, but fail(s).");
		}

		CMMsg msg = CMClass
				.getMsg(mob,
						target,
						this,
						verbalCastCode(mob, target, auto),
						auto ? "A moonbeam begin(s) to follow <T-NAME> around!"
								: "^S<S-NAME> chant(s), causing a moonbeam to follow <S-HIM-HER> around!^?");
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			beneficialAffect(mob, target, asLevel, 0);
			target.location().recoverRoomStats(); // attempt to handle followers
		}

		return success;
	}
}
