package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.DeadBody;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class Prayer_Desecrate extends Prayer {
	public String ID() {
		return "Prayer_Desecrate";
	}

	public String name() {
		return "Desecrate";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_DEATHLORE;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	protected int canTargetCode() {
		return Ability.CAN_ITEMS;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Item target = null;
		if ((commands.size() == 0) && (!auto) && (givenTarget == null))
			target = Prayer_Sacrifice.getBody(mob.location());
		if (target == null)
			target = getTarget(mob, mob.location(), givenTarget, commands,
					Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;

		if ((!(target instanceof DeadBody))
				|| (target.rawSecretIdentity().toUpperCase().indexOf("FAKE") >= 0)) {
			mob.tell("You may only desecrate the dead.");
			return false;
		}
		if ((((DeadBody) target).playerCorpse())
				&& (!((DeadBody) target).mobName().equals(mob.Name()))
				&& (((DeadBody) target).getContents().size() > 0)) {
			mob.tell("You are not allowed to desecrate a players corpse.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto),
					auto ? "<T-NAME> feel(s) desecrated!"
							: "^S<S-NAME> desecrate(s) <T-NAMESELF> before "
									+ hisHerDiety(mob) + ".^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (CMLib.flags().isEvil(mob)) {
					double exp = 5.0;
					int levelLimit = CMProps.getIntVar(CMProps.Int.EXPRATE);
					int levelDiff = (mob.phyStats().level())
							- target.phyStats().level();
					if (levelDiff > levelLimit)
						exp = 0.0;
					if (exp > 0.0)
						CMLib.leveler().postExperience(
								mob,
								null,
								null,
								(int) Math.round(exp)
										+ super.getXPCOSTLevel(mob), false);
				}
				target.destroy();
				mob.location().recoverRoomStats();
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to desecrate <T-NAMESELF>, but fail(s).");

		// return whether it worked
		return success;
	}
}
