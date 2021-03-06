package com.planet_ink.coffee_mud.Abilities.Fighter;

import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Shield;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

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
public class Fighter_AutoBash extends FighterSkill {
	public String ID() {
		return "Fighter_AutoBash";
	}

	public String name() {
		return "AutoBash";
	}

	public String displayText() {
		return "";
	}

	private static final String[] triggerStrings = { "AUTOBASH" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_SHIELDUSE;
	}

	protected volatile int numberOfShields = -1;

	public boolean tick(Tickable ticking, int tickID) {
		if (!(affected instanceof MOB))
			return super.tick(ticking, tickID);
		if (!super.tick(ticking, tickID))
			return false;

		MOB mob = (MOB) affected;

		if ((numberOfShields < 0) && (tickID == Tickable.TICKID_MOB)) {
			numberOfShields = 0;
			for (Enumeration<Item> i = mob.items(); i.hasMoreElements();) {
				final Item I = i.nextElement();
				if ((I instanceof Shield)
						&& (I.amWearingAt(Wearable.WORN_HELD) || I
								.amWearingAt(Wearable.WORN_WIELD))
						&& (I.owner() == ticking) && (I.container() == null))
					numberOfShields++;
			}
			mob.recoverPhyStats();
		}

		for (int i = 0; i < numberOfShields; i++) {
			if (mob.isInCombat() && (mob.rangeToTarget() == 0)
					&& (CMLib.flags().aliveAwakeMobileUnbound(mob, true))
					&& (proficiencyCheck(null, 0, false))) {
				Ability A = mob.fetchAbility("Skill_Bash");
				if (A != null)
					A.invoke(mob, mob.getVictim(), false, adjustedLevel(mob, 0));
				if (CMLib.dice().rollPercentage() < (10 / numberOfShields))
					helpProficiency(mob, 0);
			}
		}
		return true;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);

		if (!(affected instanceof MOB))
			return;

		MOB mob = (MOB) affected;

		if (msg.amISource(mob) && (msg.target() instanceof Shield))
			numberOfShields = -1;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((mob.fetchEffect(ID()) != null)) {
			mob.tell("You are no longer automatically bashing opponents.");
			mob.delEffect(mob.fetchEffect(ID()));
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			mob.tell("You will now automatically bash opponents when you fight.");
			beneficialAffect(mob, mob, asLevel, 0);
			Ability A = mob.fetchEffect(ID());
			if (A != null)
				A.makeLongLasting();
		} else
			beneficialVisualFizzle(mob, null,
					"<S-NAME> attempt(s) to get into <S-HIS-HER> bashing mood, but fail(s).");
		return success;
	}

	public boolean autoInvocation(MOB mob) {
		numberOfShields = -1;
		return super.autoInvocation(mob);
	}
}
