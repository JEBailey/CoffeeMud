package com.planet_ink.coffee_mud.Locales;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Common.interfaces.PlayerStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Places;

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
public class UnderWater extends StdRoom implements Drink {
	public String ID() {
		return "UnderWater";
	}

	public UnderWater() {
		super();
		name = "the water";
		basePhyStats().setDisposition(
				basePhyStats().disposition() | PhyStats.IS_SWIMMING);
		basePhyStats.setWeight(3);
		recoverPhyStats();
		climask = Places.CLIMASK_WET;
		atmosphere = RawMaterial.RESOURCE_FRESHWATER;
	}

	public int domainType() {
		return Room.DOMAIN_OUTDOORS_UNDERWATER;
	}

	protected int baseThirst() {
		return 0;
	}

	public long decayTime() {
		return 0;
	}

	public void setDecayTime(long time) {
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected instanceof MOB)
			affectableStats.setDisposition(affectableStats.disposition()
					| PhyStats.IS_SWIMMING);
	}

	public static void makeSink(Physical P, Room room, int avg) {
		if ((P == null) || (room == null))
			return;

		Room R = room.getRoomInDir(Directions.DOWN);
		if (avg > 0)
			R = room.getRoomInDir(Directions.UP);
		if ((R == null)
				|| ((R.domainType() != Room.DOMAIN_INDOORS_UNDERWATER) && (R
						.domainType() != Room.DOMAIN_OUTDOORS_UNDERWATER)))
			return;

		if (((P instanceof MOB) && (!CMLib.flags().isWaterWorthy(P))
				&& (!CMLib.flags().isInFlight(P)) && (P.phyStats().weight() >= 1))
				|| ((P instanceof Item)
						&& (!CMLib.flags().isInFlight(
								((Item) P).ultimateContainer(null))) && (!CMLib
						.flags().isWaterWorthy(
								((Item) P).ultimateContainer(null)))))
			if (P.fetchEffect("Sinking") == null) {
				Ability sinking = CMClass.getAbility("Sinking");
				if (sinking != null) {
					sinking.setProficiency(avg);
					sinking.setAffectedOne(room);
					sinking.invoke(null, null, P, true, 0);
				}
			}
	}

	public static void sinkAffects(Room room, CMMsg msg) {
		if (msg.amITarget(room) && (msg.targetMinor() == CMMsg.TYP_DRINK)
				&& (room instanceof Drink)) {
			MOB mob = msg.source();
			boolean thirsty = mob.curState().getThirst() <= 0;
			boolean full = !mob.curState().adjThirst(
					((Drink) room).thirstQuenched(),
					mob.maxState().maxThirst(mob.baseWeight()));
			if (thirsty)
				mob.tell("You are no longer thirsty.");
			else if (full)
				mob.tell("You have drunk all you can.");
		}

		CMLib.commands().handleHygienicMessage(msg, 100,
				PlayerStats.HYGIENE_WATERCLEAN);

		if (CMLib.flags().isSleeping(room))
			return;
		boolean foundReversed = false;
		boolean foundNormal = false;
		Vector<Physical> needToSink = new Vector<Physical>();
		Vector<Physical> mightNeedAdjusting = new Vector<Physical>();

		if ((room.domainType() != Room.DOMAIN_OUTDOORS_UNDERWATER)
				&& (room.domainType() != Room.DOMAIN_INDOORS_UNDERWATER))
			for (int i = 0; i < room.numInhabitants(); i++) {
				MOB mob = room.fetchInhabitant(i);
				if ((mob != null)
						&& ((mob.getStartRoom() == null) || (mob.getStartRoom() != room))
						&& (mob.riding() == null)) {
					Ability A = mob.fetchEffect("Sinking");
					if (A != null) {
						if (A.proficiency() >= 100) {
							foundReversed = true;
							mightNeedAdjusting.addElement(mob);
						}
						foundNormal = foundNormal || (A.proficiency() <= 0);
					} else if ((!CMath.bset(mob.basePhyStats().disposition(),
							PhyStats.IS_SWIMMING))
							&& (!mob.charStats().getMyRace().racialCategory()
									.equals("Amphibian"))
							&& (!mob.charStats().getMyRace().racialCategory()
									.equals("Fish")))
						needToSink.addElement(mob);
				}
			}
		for (int i = 0; i < room.numItems(); i++) {
			Item item = room.getItem(i);
			if ((item != null) && (item.container() == null)) {
				Ability A = item.fetchEffect("Sinking");
				if (A != null) {
					if (A.proficiency() >= 100) {
						foundReversed = true;
						mightNeedAdjusting.addElement(item);
					}
					foundNormal = foundNormal || (A.proficiency() <= 0);
				} else
					needToSink.addElement(item);
			}
		}
		int avg = ((foundReversed) && (!foundNormal)) ? 100 : 0;
		for (Physical P : mightNeedAdjusting) {
			Ability A = P.fetchEffect("Sinking");
			if (A != null)
				A.setProficiency(avg);
		}
		for (Physical P : needToSink)
			makeSink(P, room, avg);
	}

	public static int isOkUnderWaterAffect(Room room, CMMsg msg) {
		if (CMLib.flags().isSleeping(room))
			return 0;

		if ((msg.targetMinor() == CMMsg.TYP_FIRE)
				|| (msg.sourceMinor() == CMMsg.TYP_FIRE)
				|| (msg.targetMinor() == CMMsg.TYP_GAS)
				|| (msg.sourceMinor() == CMMsg.TYP_GAS)) {
			if ((!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
					&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_ALWAYS)))
				msg.source().tell("That won't work underwater.");
			return -1;
		} else if ((msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.tool() != null) && (msg.tool() instanceof Weapon)) {
			Weapon w = (Weapon) msg.tool();
			if ((w.weaponType() == Weapon.TYPE_SLASHING)
					|| (w.weaponType() == Weapon.TYPE_BASHING)) {
				int damage = msg.value();
				damage = damage / 3;
				damage = damage * 2;
				msg.setValue(msg.value() - damage);
			}
		} else if (msg.amITarget(room)
				&& (msg.targetMinor() == CMMsg.TYP_DRINK)
				&& (room instanceof Drink)) {
			if (((Drink) room).liquidType() == RawMaterial.RESOURCE_SALTWATER) {
				msg.source().tell("You don't want to be drinking saltwater.");
				return -1;
			}
			return 1;
		}
		return 0;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		switch (UnderWater.isOkUnderWaterAffect(this, msg)) {
		case -1:
			return false;
		case 1:
			return true;
		}
		return super.okMessage(myHost, msg);
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		UnderWater.sinkAffects(this, msg);
	}

	public int thirstQuenched() {
		return 500;
	}

	public int liquidHeld() {
		return Integer.MAX_VALUE - 1000;
	}

	public int liquidRemaining() {
		return Integer.MAX_VALUE - 1000;
	}

	public int liquidType() {
		return RawMaterial.RESOURCE_FRESHWATER;
	}

	public void setLiquidType(int newLiquidType) {
	}

	public void setThirstQuenched(int amount) {
	}

	public void setLiquidHeld(int amount) {
	}

	public void setLiquidRemaining(int amount) {
	}

	public boolean disappearsAfterDrinking() {
		return false;
	}

	public boolean containsDrink() {
		return true;
	}

	public int amountTakenToFillMe(Drink theSource) {
		return 0;
	}

	public static final Integer[] resourceList = {
			Integer.valueOf(RawMaterial.RESOURCE_SEAWEED),
			Integer.valueOf(RawMaterial.RESOURCE_FISH),
			Integer.valueOf(RawMaterial.RESOURCE_CATFISH),
			Integer.valueOf(RawMaterial.RESOURCE_SALMON),
			Integer.valueOf(RawMaterial.RESOURCE_CARP),
			Integer.valueOf(RawMaterial.RESOURCE_TROUT),
			Integer.valueOf(RawMaterial.RESOURCE_SAND),
			Integer.valueOf(RawMaterial.RESOURCE_CLAY),
			Integer.valueOf(RawMaterial.RESOURCE_LIMESTONE) };
	public static final Vector roomResources = new Vector(
			Arrays.asList(resourceList));

	public List<Integer> resourceChoices() {
		return UnderWater.roomResources;
	}
}
