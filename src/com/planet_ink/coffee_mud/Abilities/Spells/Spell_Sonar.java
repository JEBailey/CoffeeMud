package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Spell_Sonar extends Spell {
	public String ID() {
		return "Spell_Sonar";
	}

	public String name() {
		return "Sonar";
	}

	public String displayText() {
		return "(Sonar)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_TRANSMUTATION;
	}

	public void unInvoke() {
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();
		if (canBeUninvoked())
			if ((mob.location() != null) && (!mob.amDead()))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-YOUPOSS> sonar ears return to normal.");
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected instanceof MOB) {
			MOB mob = (MOB) affected;
			MOB victim = mob.getVictim();
			if ((victim == null)
					|| (CMLib.flags().canBeHeardMovingBy(victim, mob)))
				affectableStats.setSensesMask(affectableStats.sensesMask()
						| PhyStats.CAN_SEE_VICTIM);
			if (CMLib.flags().canHear(mob)) {
				affectableStats.setSensesMask(affectableStats.sensesMask()
						| PhyStats.CAN_SEE_DARK);
				if ((affectableStats.sensesMask() & PhyStats.CAN_NOT_SEE) > 0)
					affectableStats
							.setSensesMask(CMath.unsetb(
									affectableStats.sensesMask(),
									PhyStats.CAN_NOT_SEE));
			}
		}
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (CMLib.flags().canSee((MOB) target)) {
					if (CMLib.flags().canSeeVictims((MOB) target))
						return Ability.QUALITY_INDIFFERENT;
					if (CMLib.flags().canSeeInDark((MOB) target))
						return Ability.QUALITY_INDIFFERENT;
				}
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target, null, null, "<S-NAME> already <S-HAS-HAVE> sonar.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							null,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> gain(s) sonar capability!"
									: "^S<S-NAME> incant(s) softly, and <S-HIS-HER> ears become capable of sonar!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialVisualFizzle(mob, null,
					"<S-NAME> incant(s) softly and listen(s), but the spell fizzles.");

		return success;
	}
}
