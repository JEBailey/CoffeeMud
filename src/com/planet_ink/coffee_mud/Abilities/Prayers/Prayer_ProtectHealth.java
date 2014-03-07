package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
@SuppressWarnings("rawtypes")
public class Prayer_ProtectHealth extends Prayer {
	public String ID() {
		return "Prayer_ProtectHealth";
	}

	public String name() {
		return "Protect Health";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_HOLYPROTECTION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
	}

	public String displayText() {
		return "(Protection of Health)";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("Your bodies natural defences take over.");
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
		super.affectCharStats(affectedMOB, affectedStats);
		affectedStats.setStat(CharStats.STAT_SAVE_POISON,
				affectedStats.getStat(CharStats.STAT_SAVE_POISON) + 50);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		if (affected == null)
			return true;
		if (!(affected instanceof MOB))
			return true;

		if (msg.target() == affected) {
			if ((msg.tool() != null) && (CMLib.dice().rollPercentage() > 50)
					&& (msg.tool() instanceof DiseaseAffect)) {
				msg.source()
						.location()
						.show((MOB) affected, null, CMMsg.MSG_OK_VISUAL,
								"An unhealthy assault against <S-NAME> is magically repelled.");
				return false;
			}

		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;

		if (target.fetchEffect(ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> already <S-HAS-HAVE> protected health.");
			return false;
		}
		Room R = CMLib.map().roomLocation(target);
		if (R == null)
			R = mob.location();

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto),
					auto ? "<T-NAME> attain(s) a healthy mind and body."
							: "^S<S-NAME> " + prayWord(mob)
									+ " for a healthy mind and body.^?");
			if (R.okMessage(mob, msg)) {
				R.send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialWordsFizzle(mob, target, "<S-NAME> "
					+ prayWord(mob)
					+ " for a healthy body and mind, but nothing happens.");

		// return whether it worked
		return success;
	}
}