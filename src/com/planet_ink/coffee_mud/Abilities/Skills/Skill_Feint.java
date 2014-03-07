package com.planet_ink.coffee_mud.Abilities.Skills;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Skill_Feint extends StdSkill {
	public String ID() {
		return "Skill_Feint";
	}

	public String name() {
		return "Feint";
	}

	public String displayText() {
		return "";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	private static final String[] triggerStrings = { "FEINT" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_EVASIVE;
	}

	protected boolean done = false;

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		int lvl = adjustedLevel(invoker(), 0);
		affectableStats.setArmor(affectableStats.armor() + lvl);
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		affectableStats.setStat(CharStats.STAT_DEXTERITY, 0);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (done) {
			unInvoke();
			return false;
		}
		return true;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return;

		MOB mob = (MOB) affected;

		if (msg.amISource(invoker()) && (msg.amITarget(mob))
				&& (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK))
			done = true;
		super.executeMsg(myHost, msg);
	}

	public int castingQuality(MOB mob, Physical target) {
		if ((mob != null) && (target != null)) {
			if (!mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
			if (mob.rangeToTarget() > 0)
				return Ability.QUALITY_INDIFFERENT;
			if (target.fetchEffect(ID()) != null)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!mob.isInCombat()) {
			mob.tell("You must be in combat to do this!");
			return false;
		}
		if (mob.rangeToTarget() > 0) {
			mob.tell("You can't do that from this range.");
			return false;
		}
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob,
				-(target.charStats().getStat(CharStats.STAT_DEXTERITY)), auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							CMMsg.MSK_MALICIOUS_MOVE | CMMsg.TYP_JUSTICE
									| (auto ? CMMsg.MASK_ALWAYS : 0),
							auto ? ""
									: "^F^<FIGHT^><S-NAME> feint(s) at <T-NAMESELF>!^</FIGHT^>^?");
			CMLib.color().fixSourceFightColor(msg);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				done = false;
				maliciousAffect(mob, target, asLevel, 2, -1);
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> feint(s) at <T-NAMESELF>, but <T-HE-SHE> do(es)n't buy it.");
		return success;
	}
}