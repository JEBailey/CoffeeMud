package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class Chant_Fertilization extends Chant {
	public String ID() {
		return "Chant_Fertilization";
	}

	public String name() {
		return "Fertilization";
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected != null) && (affected instanceof Room)) {
			Room R = (Room) affected;
			if ((R.myResource() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_VEGETATION)
				for (int m = 0; m < R.numInhabitants(); m++) {
					MOB M = R.fetchInhabitant(m);
					if (M != null) {
						Ability A = M.fetchEffect("Farming");
						if (A == null)
							A = M.fetchEffect("Foraging");
						if (A != null)
							A.setAbilityCode(4);
					}
				}
		}
		return super.tick(ticking, tickID);

	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {

		int type = mob.location().domainType();
		if (((type & Room.INDOORS) > 0) || (type == Room.DOMAIN_OUTDOORS_AIR)
				|| (type == Room.DOMAIN_OUTDOORS_CITY)
				|| (type == Room.DOMAIN_OUTDOORS_SPACEPORT)
				|| (type == Room.DOMAIN_OUTDOORS_UNDERWATER)
				|| (type == Room.DOMAIN_OUTDOORS_WATERSURFACE)) {
			mob.tell("That magic won't work here.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							mob.location(),
							this,
							verbalCastCode(mob, mob.location(), auto),
							auto ? ""
									: "^S<S-NAME> chant(s) to make the land fruitful.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(
						mob,
						mob.location(),
						asLevel,
						(int) (CMLib.ableMapper().qualifyingClassLevel(mob,
								this) * (((CMProps.getMillisPerMudHour() * mob
								.location().getArea().getTimeObj()
								.getHoursInDay()) / CMProps.getTickMillis()))));
			}

		} else
			beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s) to make the land fruitful, but nothing happens.");

		// return whether it worked
		return success;
	}
}
