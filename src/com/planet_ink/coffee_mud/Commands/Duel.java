package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB.Tattoo;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.collections.XVector;

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
public class Duel extends StdCommand {
	public Duel() {
	}

	private final String[] access = { "DUEL" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		MOB target = null;
		if (commands.size() < 2) {
			mob.tell("Duel whom?");
			return false;
		}

		String whomToKill = CMParms.combine(commands, 1);
		target = mob.location().fetchInhabitant(whomToKill);
		if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
			mob.tell("I don't see '" + whomToKill + "' here.");
			return false;
		}

		if (mob == target)
			mob.tell("You may not duel yourself.");
		else if ((mob.isMonster()))
			mob.tell("You are not allowed to duel " + target.name(mob) + ".");
		else {
			Tattoo uiT = target.findTattoo("IDUEL");
			Tattoo uuT = target.findTattoo("UDUEL");
			Tattoo iiT = mob.findTattoo("IDUEL");
			Tattoo iuT = mob.findTattoo("UDUEL");
			if ((uiT == null) && (iiT == null) && (uuT == null)
					&& (iuT == null)) {
				int duelTicks = CMProps.getIntVar(CMProps.Int.DUELTICKDOWN);
				mob.addTattoo(new Tattoo("IDUEL", duelTicks));
				target.addTattoo(new Tattoo("UDUEL", duelTicks));
				long time = CMProps.getTickMillis() * duelTicks;
				mob.location()
						.show(mob,
								target,
								CMMsg.MSG_DUELCHALLENGE,
								"^X<S-NAME> <S-HAS-HAVE> challenged <T-NAME> to a duel, which <T-HE-SHE> <T-HAS-HAVE> "
										+ (time / 1000)
										+ " seconds to consider.^.^N");
				target.tell("^NEnter ^HDUEL " + mob.name(target)
						+ "^N to accept this challenge and begin fighting.");
				return true;
			} else if ((uiT != null) && (iuT != null)) {
				target.tell(mob, target, null,
						"^X<T-NAME> <T-HAS-HAVE> ACCEPTED <T-YOUPOSS> CHALLENGE!^.^N");
				Item weapon = mob.fetchWieldedItem();
				if (weapon == null) {
					Item possibleOtherWeapon = mob.fetchHeldItem();
					if ((possibleOtherWeapon != null)
							&& (possibleOtherWeapon instanceof Weapon)
							&& possibleOtherWeapon.fitsOn(Wearable.WORN_WIELD)
							&& (CMLib.flags().canBeSeenBy(possibleOtherWeapon,
									mob))
							&& (CMLib.flags().isRemovable(possibleOtherWeapon))) {
						CMLib.commands().postRemove(mob, possibleOtherWeapon,
								false);
						if (possibleOtherWeapon
								.amWearingAt(Wearable.IN_INVENTORY)) {
							Command C = CMClass.getCommand("Wield");
							if (C != null)
								C.execute(mob, new XVector("WIELD",
										possibleOtherWeapon), metaFlags);
						}
					}
				}
				Ability A = CMClass.getAbility("Dueler");
				if (A != null)
					A.invoke(target, mob, true, 0);
			} else if (uiT != null) {
				mob.tell(
						mob,
						target,
						null,
						"<T-NAME> is awaiting a response to a previous challenge and cannot be challenged at this time.");
				return false;
			} else if (uuT != null) {
				mob.tell(
						mob,
						target,
						null,
						"<T-NAME> is considering a response to a previous challenger and cannot be challenged at this time.");
				return false;
			} else if ((iuT != null) || (iiT != null)) {
				int duelTicks = CMProps.getIntVar(CMProps.Int.DUELTICKDOWN);
				long time = CMProps.getTickMillis() * duelTicks;
				mob.tell(mob, target, null,
						"Your previous challenge has not yet expired.  Please wait "
								+ (time / 1000)
								+ " seconds longer and try again.");
				return false;
			}
		}
		return false;
	}

	public double combatActionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getCombatActionCost(ID());
	}

	public double actionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getActionCost(ID());
	}

	public boolean canBeOrdered() {
		return true;
	}

}
