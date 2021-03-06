package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
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
public class Embroidering extends CommonSkill {
	public String ID() {
		return "Embroidering";
	}

	public String name() {
		return "Embroidering";
	}

	private static final String[] triggerStrings = { "EMBROIDER",
			"EMBROIDERING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_CALLIGRAPHY;
	}

	protected Item found = null;
	protected String writing = "";

	public Embroidering() {
		super();
		displayText = "You are embroidering...";
		verb = "embroidering";
	}

	protected boolean canBeDoneSittingDown() {
		return true;
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB) && (!aborted)
					&& (!helping)) {
				MOB mob = (MOB) affected;
				if (writing.length() == 0)
					commonTell(mob, "You mess up your embroidery.");
				else {
					String desc = found.description();
					int x = desc.indexOf(" Embroidered on it are the words `");
					int y = desc.lastIndexOf('`');
					if ((x >= 0) && (y > x))
						desc = desc.substring(0, x);
					found.setDescription(desc
							+ " Embroidered on it are the words `" + writing
							+ "`.");
				}
			}
		}
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (super.checkStop(mob, commands))
			return true;
		if (commands.size() < 2) {
			commonTell(
					mob,
					"You must specify what you want to embroider onto, and what words to embroider on it.");
			return false;
		}
		Item target = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY,
				(String) commands.firstElement());
		if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
			commonTell(
					mob,
					"You don't seem to have a '"
							+ ((String) commands.firstElement()) + "'.");
			return false;
		}
		commands.remove(commands.firstElement());

		Ability write = mob.fetchAbility("Skill_Write");
		if (write == null) {
			commonTell(mob, "You must know how to write to embroider.");
			return false;
		}

		if ((((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_CLOTH) && ((target
				.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_LEATHER))
				|| (!target.isGeneric())) {
			commonTell(mob, "You can't embroider onto that material.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		writing = CMParms.combine(commands, 0);
		verb = "embroidering on " + target.name();
		displayText = "You are " + verb;
		found = target;
		if ((!proficiencyCheck(mob, 0, auto))
				|| (!write.proficiencyCheck(mob, 0, auto)))
			writing = "";
		int duration = getDuration(30, mob, 1, 3);
		CMMsg msg = CMClass.getMsg(mob, target, this, getActivityMessageType(),
				"<S-NAME> start(s) embroidering on <T-NAME>.");
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			beneficialAffect(mob, mob, asLevel, duration);
		}
		return true;
	}
}
