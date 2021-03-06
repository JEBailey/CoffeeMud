package com.planet_ink.coffee_mud.Abilities.Songs;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.Thief.Thief_Mark;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Skill_MarkDisguise extends Skill_Disguise {
	public String ID() {
		return "Skill_MarkDisguise";
	}

	public String name() {
		return "Mark Disguise";
	}

	private static final String[] triggerStrings = { "MARKDISGUISE" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public MOB mark = null;

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
		Skill_Disguise A = (Skill_Disguise) mob.fetchEffect("Skill_Disguise");
		if (A == null)
			A = (Skill_Disguise) mob.fetchEffect("Skill_MarkDisguise");
		if (A != null) {
			A.unInvoke();
			mob.tell("You remove your disguise.");
			return true;
		}
		MOB target = getMark(mob);
		if (CMParms.combine(commands, 0).equalsIgnoreCase("!"))
			target = mark;

		if (target == null) {
			mob.tell("You need to have marked someone before you can disguise yourself as him or her.");
			return false;
		}
		if (target.charStats().getClassLevel("Archon") >= 0) {
			mob.tell("You may not disguise yourself as an Archon.");
			return false;
		}

		int ticksWaited = getMarkTicks(mob);
		if (ticksWaited < 15) {
			if (target == getMark(mob)) {
				mob.tell("You'll need to observe your mark a little longer ("
						+ ticksWaited
						+ "/15 ticks) before you can get the disguise right.");
				return false;
			}
		}

		mark = target;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass.getMsg(mob, mob, null,
					CMMsg.MSG_DELICATE_HANDS_ACT
							| (auto ? CMMsg.MASK_ALWAYS : 0),
					"<S-NAME> turn(s) away for a second.");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, mob, asLevel, 0);
				A = (Skill_Disguise) mob.fetchEffect("Skill_MarkDisguise");
				A.values[0] = "" + target.basePhyStats().weight();
				A.values[1] = "" + target.basePhyStats().level();
				A.values[2] = target.charStats().genderName();
				A.values[3] = target.charStats().raceName();
				A.values[4] = "" + target.phyStats().height();
				A.values[5] = target.name();
				A.values[6] = target.charStats().displayClassName();
				if (CMLib.flags().isGood(target))
					A.values[7] = "good";
				else if (CMLib.flags().isEvil(target))
					A.values[7] = "evil";
				A.makeLongLasting();

				mob.recoverCharStats();
				mob.recoverPhyStats();
				mob.location().recoverRoomStats();
			}
		} else
			return beneficialVisualFizzle(mob, null,
					"<S-NAME> turn(s) away and then back, but look(s) the same.");
		return success;
	}

}
