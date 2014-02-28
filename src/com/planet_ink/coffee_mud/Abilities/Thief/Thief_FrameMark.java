package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Behaviors.interfaces.LegalBehavior;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Thief_FrameMark extends ThiefSkill {
	public String ID() {
		return "Thief_FrameMark";
	}

	public String name() {
		return "Frame Mark";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "FRAME" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected int overrideMana() {
		return 50;
	}

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STREETSMARTS;
	}

	public MOB getMark(MOB mob) {
		Thief_Mark A = (Thief_Mark) mob.fetchEffect("Thief_Mark");
		if (A != null)
			return A.mark;
		return null;
	}

	public int getMarkTicks(MOB mob) {
		Thief_Mark A = (Thief_Mark) mob.fetchEffect("Thief_Mark");
		if ((A != null) && (A.mark != null))
			return A.ticks;
		return -1;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getMark(mob);
		if (target == null) {
			mob.tell("You need to have marked someone before you can frame him or her.");
			return false;
		}

		LegalBehavior B = null;
		if (mob.location() != null)
			B = CMLib.law().getLegalBehavior(mob.location());
		if ((B == null)
				|| (!B.hasWarrant(CMLib.law().getLegalObject(mob.location()),
						mob))) {
			mob.tell("You aren't wanted for anything here.");
			return false;
		}
		double goldRequired = target.phyStats().level() * 1000.0;
		String localCurrency = CMLib.beanCounter().getCurrency(mob.location());
		if (CMLib.beanCounter().getTotalAbsoluteValue(mob, localCurrency) < goldRequired) {
			String costWords = CMLib.beanCounter().nameCurrencyShort(
					localCurrency, goldRequired);
			mob.tell("You'll need at least " + costWords + " on hand to frame "
					+ target.name(mob) + ".");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		int levelDiff = (target.phyStats().level() - (mob.phyStats().level() + (2 * super
				.getXLEVELLevel(mob))) * 15);
		if (levelDiff < 0)
			levelDiff = 0;
		boolean success = proficiencyCheck(mob, -levelDiff, auto);
		if (!success) {
			maliciousFizzle(mob, target,
					"<S-NAME> attempt(s) frame <T-NAMESELF>, but <S-IS-ARE> way too obvious.");
			return false;
		}

		CMLib.beanCounter().subtractMoney(mob, localCurrency, goldRequired);

		CMMsg msg = CMClass.getMsg(mob, target, this,
				CMMsg.MSG_DELICATE_HANDS_ACT,
				"<S-NAME> frame(s) <T-NAMESELF>.", CMMsg.NO_EFFECT, null,
				CMMsg.NO_EFFECT, null);
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			B.frame(CMLib.law().getLegalObject(mob.location()), mob, target);
		}
		return success;
	}

}
