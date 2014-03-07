package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class Thief_StripItem extends ThiefSkill {
	public String ID() {
		return "Thief_StripItem";
	}

	public String name() {
		return "Strip Item";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALING;
	}

	private static final String[] triggerStrings = { "STRIPITEM" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	public int code = 0;

	public int abilityCode() {
		return code;
	}

	public void setAbilityCode(int newCode) {
		code = newCode;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (!(target instanceof MOB))
				return Ability.QUALITY_INDIFFERENT;
			if (((MOB) target).amDead()
					|| (!CMLib.flags().canBeSeenBy(target, mob)))
				return Ability.QUALITY_INDIFFERENT;
			if (!((MOB) target).mayIFight(mob))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		String itemToSteal = "all";
		if (!auto) {
			if (commands.size() < 2) {
				mob.tell("Strip what off of whom?");
				return false;
			}
			itemToSteal = (String) commands.elementAt(0);
		}

		MOB target = null;
		if ((givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		else
			target = mob.location().fetchInhabitant(
					CMParms.combine(commands, 1));
		if ((target == null) || (target.amDead())
				|| (!CMLib.flags().canBeSeenBy(target, mob))) {
			mob.tell("You don't see '" + CMParms.combine(commands, 1)
					+ "' here.");
			return false;
		}
		int levelDiff = target.phyStats().level()
				- (mob.phyStats().level() + abilityCode() + (getXLEVELLevel(mob) * 2));
		if ((!target.mayIFight(mob)) || (levelDiff > 15)) {
			mob.tell("You cannot strip anything off of "
					+ target.charStats().himher() + ".");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		Item stolen = target.fetchItem(null, Wearable.FILTER_WORNONLY,
				itemToSteal);
		if ((stolen == null) || (!CMLib.flags().canBeSeenBy(stolen, mob))) {
			mob.tell(target.name(mob) + " doesn't seem to be wearing '"
					+ itemToSteal + "'.");
			return false;
		}
		if (stolen.amWearingAt(Wearable.WORN_WIELD)) {
			mob.tell(target.name(mob) + " is wielding " + stolen.name()
					+ "! Try disarm!");
			return false;
		}

		if (levelDiff > 0)
			levelDiff = -(levelDiff * ((!CMLib.flags().canBeSeenBy(mob, target)) ? 5
					: 15));
		else
			levelDiff = -(levelDiff * ((!CMLib.flags().canBeSeenBy(mob, target)) ? 1
					: 2));
		boolean success = proficiencyCheck(mob, levelDiff, auto);

		if (!success) {
			if ((target.isMonster()) && (mob.getVictim() == null))
				mob.setVictim(target);
			CMMsg msg = CMClass.getMsg(
					mob,
					target,
					this,
					CMMsg.MSG_NOISYMOVEMENT,
					auto ? "" : "You fumble the attempt to strip "
							+ stolen.name()
							+ " off <T-NAME>; <T-NAME> spots you!",
					CMMsg.MSG_NOISYMOVEMENT, auto ? ""
							: "<S-NAME> tries to strip " + stolen.name()
									+ " off you and fails!",
					CMMsg.MSG_NOISYMOVEMENT, auto ? ""
							: "<S-NAME> tries to strip " + stolen.name()
									+ " off <T-NAME> and fails!");
			if (mob.location().okMessage(mob, msg))
				mob.location().send(mob, msg);
		} else {
			String str = null;
			if (!auto)
				str = "<S-NAME> strip(s) " + stolen.name()
						+ " off <T-NAMESELF>.";

			boolean alreadyFighting = (mob.getVictim() == target)
					|| (target.getVictim() == mob);
			String hisStr = str;
			int hisCode = CMMsg.MSG_THIEF_ACT
					| ((target.mayIFight(mob)) ? CMMsg.MASK_MALICIOUS : 0);

			CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_THIEF_ACT,
					str, hisCode, hisStr, CMMsg.NO_EFFECT, null);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);

				if ((!target.isMonster()) && (mob.isMonster())
						&& (!alreadyFighting)) {
					if (target.getVictim() == mob)
						target.makePeace();
					if (mob.getVictim() == target)
						mob.makePeace();
				} else if (((hisStr == null) || mob.isMonster())
						&& (!alreadyFighting)
						&& (CMLib.dice().rollPercentage() > stolen.phyStats()
								.level())) {
					if (target.getVictim() == mob)
						target.makePeace();
				}
				msg = CMClass.getMsg(target, stolen, null, CMMsg.MSG_REMOVE,
						CMMsg.MSG_REMOVE, CMMsg.MSG_NOISE, null);
				if (target.location().okMessage(target, msg))
					target.location().send(mob, msg);
			}
		}
		return success;
	}
}