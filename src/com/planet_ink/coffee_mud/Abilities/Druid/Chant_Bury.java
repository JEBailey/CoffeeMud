package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.DeadBody;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor.Expire;
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
public class Chant_Bury extends Chant {
	public String ID() {
		return "Chant_Bury";
	}

	public String name() {
		return "Earthfeed";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_DEEPMAGIC;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_ITEMS;
	}

	public static Item getBody(Room R) {
		if (R != null)
			for (int i = 0; i < R.numItems(); i++) {
				Item I = R.getItem(i);
				if ((I != null) && (I instanceof DeadBody)
						&& (!((DeadBody) I).playerCorpse())
						&& (((DeadBody) I).mobName().length() > 0))
					return I;
			}
		return null;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((mob.location().domainType() & Room.INDOORS) > 0) {
			mob.tell("You must be outdoors for this chant to work.");
			return false;
		}
		if ((mob.location().domainType() == Room.DOMAIN_OUTDOORS_CITY)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_SPACEPORT)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_UNDERWATER)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_AIR)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_WATERSURFACE)) {
			mob.tell("This chant does not work here.");
			return false;
		}
		Item hole = mob.location().findItem("HoleInTheGround");
		if ((hole != null) && (!hole.text().equalsIgnoreCase(mob.Name()))) {
			mob.tell("This chant will not work on desecrated ground.");
			return false;
		}
		Item target = null;
		if ((commands.size() == 0) && (!auto) && (givenTarget == null))
			target = getBody(mob.location());
		if (target == null)
			target = getTarget(mob, mob.location(), givenTarget, commands,
					Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;

		if ((!(target instanceof DeadBody))
				|| (((DeadBody) target).rawSecretIdentity().toUpperCase()
						.indexOf("FAKE") >= 0)) {
			mob.tell("You may only feed the dead to the earth.");
			return false;
		}

		if ((((DeadBody) target).playerCorpse())
				&& (!((DeadBody) target).mobName().equals(mob.Name()))
				&& (((DeadBody) target).getContents().size() > 0)) {
			mob.tell("You are not allowed to bury that corpse.");
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
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> bur(ys) <T-HIM-HERSELF>."
									: "^S<S-NAME> chant(s) to <T-NAMESELF>, returning dust to dust.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (CMLib.flags().isNeutral(mob))
					mob.curState()
							.adjMana(
									3
											* target.phyStats().level()
											+ (3 * target.phyStats().level() * super
													.getXLEVELLevel(mob)),
									mob.maxState());
				if (hole == null) {
					CMMsg holeMsg = CMClass.getMsg(mob, mob.location(), null,
							CMMsg.MSG_DIG | CMMsg.MASK_ALWAYS, null);
					mob.location().send(mob, holeMsg);
					hole = mob.location().findItem("HoleInTheGround");
				}
				hole.basePhyStats().setDisposition(
						hole.basePhyStats().disposition() | PhyStats.IS_HIDDEN);
				hole.recoverPhyStats();
				if (!mob.location().isContent(target))
					mob.location().moveItemTo(hole, Expire.Player_Drop);
				else
					target.setContainer((Container) hole);
				CMLib.flags().setGettable(target, false);
				mob.location().recoverRoomStats();
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}
