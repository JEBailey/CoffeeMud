package com.planet_ink.coffee_mud.Abilities.Spells;

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
public class Spell_Light extends Spell {
	public String ID() {
		return "Spell_Light";
	}

	public String name() {
		return "Light";
	}

	public String displayText() {
		return "(Light)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (!CMLib.flags().canBeSeenBy(mob.location(), mob))
				return super.castingQuality(mob, target,
						Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob, target);
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
		Room room = CMLib.map().roomLocation(affected);
		if (canBeUninvoked() && (room != null) && (affected instanceof MOB))
			room.show((MOB) affected, null, CMMsg.MSG_OK_VISUAL,
					"The light above <S-NAME> dims.");
		super.unInvoke();
		if (canBeUninvoked() && (room != null))
			room.recoverRoomStats();
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target, null, null, "<S-NAME> already <S-HAS-HAVE> light.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		final Room room = mob.location();
		if ((success) && (room != null)) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "^S<S-NAME> attain(s) a light above <S-HIS-HER> head!"
									: "^S<S-NAME> invoke(s) a white light above <S-HIS-HER> head!^?");
			if (room.okMessage(mob, msg)) {
				room.send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
				room.recoverRoomStats();
			}
		} else
			beneficialWordsFizzle(mob, mob.location(),
					"<S-NAME> attempt(s) to invoke light, but fail(s).");

		return success;
	}
}
