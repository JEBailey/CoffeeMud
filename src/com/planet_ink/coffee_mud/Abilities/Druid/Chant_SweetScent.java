package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Libraries.interfaces.TrackingLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Directions;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Chant_SweetScent extends Chant {
	public String ID() {
		return "Chant_SweetScent";
	}

	public String name() {
		return "Sweet Scent";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTCONTROL;
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS;
	}

	protected int canTargetCode() {
		return Ability.CAN_ITEMS;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((affected != null) && (affected instanceof Item)) {
			Item I = (Item) affected;
			if (I.owner() instanceof Room) {
				Room room = (Room) I.owner();
				Vector rooms = new Vector();
				TrackingLibrary.TrackingFlags flags;
				flags = new TrackingLibrary.TrackingFlags()
						.plus(TrackingLibrary.TrackingFlag.OPENONLY);
				CMLib.tracking().getRadiantRooms(room, rooms, flags, null, 10,
						null);
				for (int i = 0; i < room.numInhabitants(); i++) {
					MOB M = room.fetchInhabitant(i);
					if ((M != null) && (CMLib.flags().isAnimalIntelligence(M))
							&& (CMLib.flags().canSmell(M)))
						M.tell(M, I, null,
								"<T-NAME> smell(s) absolutely intoxicating!");
				}
				for (int r = 0; r < rooms.size(); r++) {
					Room R = (Room) rooms.elementAt(r);
					if (R != room) {
						int dir = CMLib.tracking().radiatesFromDir(R, rooms);
						if (dir >= 0) {
							for (int i = 0; i < R.numInhabitants(); i++) {
								MOB M = R.fetchInhabitant(i);
								if ((M != null)
										&& (CMLib.flags()
												.isAnimalIntelligence(M))
										&& (!M.isInCombat())
										&& ((!M.isMonster()) || (CMLib.flags()
												.isMobile(M)))
										&& (CMLib.flags().canSmell(M))) {
									M.tell(M,
											null,
											null,
											"You smell something irresistable "
													+ Directions
															.getInDirectionName(dir)
													+ ".");
									if (CMLib.dice().rollPercentage() > M
											.charStats().getSave(
													CharStats.STAT_SAVE_MIND))
										CMLib.tracking().walk(M, dir, false,
												false);
								}
							}
						}
					}

				}
			}
		}
		return true;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if ((msg.amITarget(affected)) && (msg.targetMinor() == CMMsg.TYP_SNIFF)
				&& (CMLib.flags().canSmell(msg.source())))
			msg.source().tell(msg.source(), affected, null,
					"<T-NAME> smell(s) absolutely intoxicating!");
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((mob.location().domainType() == Room.DOMAIN_OUTDOORS_UNDERWATER)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_AIR)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_WATERSURFACE)
				|| (mob.location().domainType() == Room.DOMAIN_INDOORS_UNDERWATER)
				|| (mob.location().domainType() == Room.DOMAIN_INDOORS_AIR)
				|| (mob.location().domainType() == Room.DOMAIN_INDOORS_WATERSURFACE)) {
			mob.tell("This magic will not work here.");
			return false;
		}

		Item target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;
		if (!Druid_MyPlants.isMyPlant(target, mob)) {
			mob.tell(target.name(mob) + " is not one of your plants!");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		// now see if it worked
		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> chant(s) to <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) to the <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}
