package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Chant_SummonTree extends Chant_SummonPlants {
	public String ID() {
		return "Chant_SummonTree";
	}

	public String name() {
		return "Summon Tree";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int material = 0;
	protected int oldMaterial = -1;

	protected Item buildMyPlant(MOB mob, Room room) {
		int code = material & RawMaterial.RESOURCE_MASK;
		Item newItem = CMClass.getBasicItem("GenItem");
		String name = CMLib.english().startWithAorAn(
				RawMaterial.CODES.NAME(code).toLowerCase() + " tree");
		newItem.setName(name);
		newItem.setDisplayText(newItem.name() + " grows here.");
		newItem.setDescription("");
		newItem.basePhyStats().setWeight(10000);
		CMLib.flags().setGettable(newItem, false);
		newItem.setMaterial(material);
		newItem.setSecretIdentity(mob.Name());
		newItem.setMiscText(newItem.text());
		room.addItem(newItem);
		Chant_SummonTree newChant = new Chant_SummonTree();
		newItem.basePhyStats().setLevel(10 + newChant.getX1Level(mob));
		newItem.setExpirationDate(0);
		room.showHappens(CMMsg.MSG_OK_ACTION, "a tall, healthy "
				+ RawMaterial.CODES.NAME(code).toLowerCase()
				+ " tree sprouts up.");
		room.recoverPhyStats();
		newChant.PlantsLocation = room;
		newChant.littlePlants = newItem;
		if (CMLib.law().doesOwnThisProperty(mob, room)) {
			newChant.setInvoker(mob);
			newChant.setMiscText(mob.Name());
			newItem.addNonUninvokableEffect(newChant);
		} else
			newChant.beneficialAffect(mob, newItem, 0,
					(newChant.adjustedLevel(mob, 0) * 240) + 450);
		room.recoverPhyStats();
		return newItem;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((PlantsLocation == null) || (littlePlants == null))
			return false;
		if (PlantsLocation.myResource() != littlePlants.material()) {
			oldMaterial = PlantsLocation.myResource();
			PlantsLocation.setResource(littlePlants.material());
		}
		for (int i = 0; i < PlantsLocation.numInhabitants(); i++) {
			MOB M = PlantsLocation.fetchInhabitant(i);
			if (M.fetchEffect("Chopping") != null) {
				unInvoke();
				break;
			}
		}
		return true;
	}

	public void unInvoke() {
		if ((canBeUninvoked()) && (PlantsLocation != null)
				&& (oldMaterial >= 0))
			PlantsLocation.setResource(oldMaterial);
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {

		material = RawMaterial.RESOURCE_OAK;
		if ((mob.location().myResource() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_WOODEN)
			material = mob.location().myResource();
		else {
			List<Integer> V = mob.location().resourceChoices();
			Vector V2 = new Vector();
			if (V != null)
				for (int v = 0; v < V.size(); v++) {
					if (((V.get(v).intValue() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_WOODEN)
							&& ((V.get(v).intValue()) != RawMaterial.RESOURCE_WOOD))
						V2.addElement(V.get(v));
				}
			if (V2.size() > 0)
				material = ((Integer) V2.elementAt(CMLib.dice().roll(1,
						V2.size(), -1))).intValue();
		}

		return super.invoke(mob, commands, givenTarget, auto, asLevel);
	}
}
