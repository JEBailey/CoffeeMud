package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CoffeeTableRow;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Libraries.interfaces.ChannelsLibrary;
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
public class Prayer_Annul extends Prayer {
	public String ID() {
		return "Prayer_Annul";
	}

	public String name() {
		return "Annul";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;
		if (!target.isMarriedToLiege()) {
			mob.tell(target.name(mob) + " is not married!");
			return false;
		}
		if (target.fetchItem(null, Wearable.FILTER_WORNONLY, "wedding band") != null) {
			mob.tell(target.name(mob) + " must remove the wedding band first.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? ""
									: "^S<S-NAME> annul(s) the marriage between <T-NAMESELF> and "
											+ target.getLiegeID() + ".^?");
			if (mob.location().okMessage(mob, msg)) {
				if ((!target.isMonster()) && (target.soulMate() == null))
					CMLib.coffeeTables().bump(target,
							CoffeeTableRow.STAT_DIVORCES);
				mob.location().send(mob, msg);
				List<String> channels = CMLib.channels()
						.getFlaggedChannelNames(
								ChannelsLibrary.ChannelFlag.DIVORCES);
				for (int i = 0; i < channels.size(); i++)
					CMLib.commands().postChannel(
							channels.get(i),
							mob.clans(),
							target.name() + " and " + target.getLiegeID()
									+ " just had their marriage annulled.",
							true);
				MOB M = CMLib.players().getPlayer(target.getLiegeID());
				if (M != null)
					M.setLiegeID("");
				target.setLiegeID("");
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> clear(s) <S-HIS-HER> throat.");

		return success;
	}
}
