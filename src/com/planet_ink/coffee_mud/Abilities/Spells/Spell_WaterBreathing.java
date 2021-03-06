package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Arrays;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Spell_WaterBreathing extends Spell {
	public String ID() {
		return "Spell_WaterBreathing";
	}

	public String name() {
		return "Water Breathing";
	}

	public String displayText() {
		return "(Water Breathing)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_TRANSMUTATION;
	}

	protected int[] lastSet = null;
	protected int[] newSet = null;

	public void unInvoke() {
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();
		if (canBeUninvoked())
			if ((mob.location() != null) && (!mob.amDead()))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-YOUPOSS> ability to breathe underwater fades.");
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		final int[] breatheables = affectableStats.getBreathables();
		if (breatheables.length == 0)
			return;
		if ((lastSet != breatheables) || (newSet == null)) {
			newSet = Arrays.copyOf(affectableStats.getBreathables(),
					affectableStats.getBreathables().length + 2);
			newSet[newSet.length - 1] = RawMaterial.RESOURCE_SALTWATER;
			newSet[newSet.length - 2] = RawMaterial.RESOURCE_FRESHWATER;
			Arrays.sort(newSet);
			lastSet = breatheables;
		}
		affectableStats.setBreathables(newSet);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> whistle(s) to <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> attain(s) an aquatic aura!");
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> whistle(s) to <T-NAMESELF>, but nothing happens.");

		return success;
	}
}
