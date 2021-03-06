package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
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
public class Chant_LocatePlants extends Chant {
	public String ID() {
		return "Chant_LocatePlants";
	}

	public String name() {
		return "Locate Plants";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTCONTROL;
	}

	public String displayText() {
		return "(Locating Plants)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	public long flags() {
		return Ability.FLAG_TRACKING;
	}

	protected List<Room> theTrail = null;
	public int nextDirection = -2;

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (tickID == Tickable.TICKID_MOB) {
			if (nextDirection == -999)
				return true;

			if ((theTrail == null) || (affected == null)
					|| (!(affected instanceof MOB)))
				return false;

			MOB mob = (MOB) affected;

			if (nextDirection == 999) {
				mob.tell(plantsHere(mob, mob.location()));
				nextDirection = -2;
				unInvoke();
			} else if (nextDirection == -1) {
				if (plantsHere(mob, mob.location()).length() == 0)
					mob.tell("The plant life trail fizzles out here.");
				nextDirection = -999;
				unInvoke();
			} else if (nextDirection >= 0) {
				mob.tell("Your sense plant life "
						+ Directions.getDirectionName(nextDirection) + ".");
				nextDirection = -2;
			}

		}
		return true;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);

		if (!(affected instanceof MOB))
			return;

		MOB mob = (MOB) affected;
		if ((msg.amISource(mob)) && (msg.amITarget(mob.location()))
				&& (CMLib.flags().canBeSeenBy(mob.location(), mob))
				&& (msg.targetMinor() == CMMsg.TYP_LOOK))
			nextDirection = CMLib.tracking().trackNextDirectionFromHere(
					theTrail, mob.location(), false);
	}

	public String plantsHere(MOB mob, Room R) {
		StringBuffer msg = new StringBuffer("");
		if (R == null)
			return msg.toString();
		Room room = R;
		if ((room.domainType() == Room.DOMAIN_OUTDOORS_WOODS)
				|| (room.domainType() == Room.DOMAIN_OUTDOORS_PLAINS)
				|| (room.domainType() == Room.DOMAIN_OUTDOORS_HILLS)
				|| ((room.myResource() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_WOODEN)
				|| ((room.myResource() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_VEGETATION)
				|| (room.domainType() == Room.DOMAIN_OUTDOORS_JUNGLE)
				|| (room.domainType() == Room.DOMAIN_OUTDOORS_SWAMP))
			msg.append("There seem to be a large number of plants all around you!\n\r");
		return msg.toString();
	}

	public void affectPhyStats(Physical affectedEnv, PhyStats affectableStats) {
		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_NOT_WORK);
		super.affectPhyStats(affectedEnv, affectableStats);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;

		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> <S-IS-ARE> already trying to find plant life.");
			return false;
		}
		List<Ability> V = CMLib.flags().flaggedAffects(mob,
				Ability.FLAG_TRACKING);
		for (Ability A : V)
			A.unInvoke();

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		String here = plantsHere(target, target.location());
		if (here.length() > 0) {
			target.tell(here);
			return true;
		}

		boolean success = proficiencyCheck(mob, 0, auto);

		TrackingLibrary.TrackingFlags flags;
		flags = new TrackingLibrary.TrackingFlags().plus(
				TrackingLibrary.TrackingFlag.NOAIR).plus(
				TrackingLibrary.TrackingFlag.NOWATER);
		Vector rooms = new Vector();
		List<Room> checkSet = CMLib.tracking().getRadiantRooms(mob.location(),
				flags, 50);
		for (Iterator<Room> r = checkSet.iterator(); r.hasNext();) {
			Room R = r.next();
			if (plantsHere(mob, R).length() > 0)
				rooms.addElement(R);
		}

		if (rooms.size() > 0) {
			// TrackingLibrary.TrackingFlags flags;
			flags = new TrackingLibrary.TrackingFlags().plus(
					TrackingLibrary.TrackingFlag.NOEMPTYGRIDS).plus(
					TrackingLibrary.TrackingFlag.NOAIR);
			theTrail = CMLib.tracking().findBastardTheBestWay(
					target.location(), rooms, flags, 50);
		}

		if ((success) && (theTrail != null)) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> begin(s) to sense plant life!"
									: "^S<S-NAME> chant(s) for a route to plant life.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Chant_LocatePlants newOne = (Chant_LocatePlants) this.copyOf();
				if (target.fetchEffect(newOne.ID()) == null)
					target.addEffect(newOne);
				target.recoverPhyStats();
				newOne.nextDirection = CMLib.tracking()
						.trackNextDirectionFromHere(newOne.theTrail,
								target.location(), false);
			}
		} else
			beneficialVisualFizzle(mob, null,
					"<S-NAME> chant(s) to find plant life, but fail(s).");

		return success;
	}
}
