package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Chant_GrowForest extends Chant {
	public String ID() {
		return "Chant_GrowForest";
	}

	public String name() {
		return "Grow Forest";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		int type = mob.location().domainType();
		if (((type & Room.INDOORS) > 0) || (type == Room.DOMAIN_OUTDOORS_AIR)
				|| (type == Room.DOMAIN_OUTDOORS_CITY)
				|| (type == Room.DOMAIN_OUTDOORS_SPACEPORT)
				|| (type == Room.DOMAIN_OUTDOORS_UNDERWATER)
				|| (type == Room.DOMAIN_OUTDOORS_WATERSURFACE)) {
			mob.tell("This magic won't work here.");
			return false;
		}

		int material = -1;
		Vector choices = new Vector();
		String s = CMParms.combine(commands, 0);

		List<Integer> codes = RawMaterial.CODES
				.COMPOSE_RESOURCES(RawMaterial.MATERIAL_WOODEN);
		for (Integer code : codes)
			if (code.intValue() != RawMaterial.RESOURCE_WOOD) {
				choices.addElement(code);
				String desc = RawMaterial.CODES.NAME(code.intValue());
				if ((s.length() > 0)
						&& (CMLib.english().containsString(desc, s)))
					material = code.intValue();
			}
		if ((material < 0) && (s.length() > 0)) {
			mob.tell("'" + s + "' is not a recognized form of tree!");
			return false;
		}

		if ((material < 0) && (choices.size() > 0))
			material = ((Integer) choices.elementAt(CMLib.dice().roll(1,
					choices.size(), -1))).intValue();

		if (material < 0)
			return false;

		String shortName = RawMaterial.CODES.NAME(material);

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		// now see if it worked
		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto), auto ? ""
							: "^S<S-NAME> chant(s) to the ground.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().showHappens(
						CMMsg.MSG_OK_VISUAL,
						"A grove of " + shortName.toLowerCase()
								+ " trees sprout up.");
				mob.location().setResource(material);
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s) to the ground, but nothing happens.");

		// return whether it worked
		return success;
	}
}
