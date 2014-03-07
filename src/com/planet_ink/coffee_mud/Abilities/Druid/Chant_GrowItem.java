package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.ItemCraftor;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
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
public class Chant_GrowItem extends Chant {
	public String ID() {
		return "Chant_GrowItem";
	}

	public String name() {
		return "Grow Item";
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

	protected int overrideMana() {
		return 50;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((mob.location().domainType() != Room.DOMAIN_OUTDOORS_WOODS)
				&& ((mob.location().myResource() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN)
				&& (mob.location().domainType() != Room.DOMAIN_OUTDOORS_JUNGLE)) {
			mob.tell("This magic will not work here.");
			return false;
		}
		int material = RawMaterial.RESOURCE_OAK;
		if ((mob.location().myResource() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_WOODEN)
			material = mob.location().myResource();
		else {
			List<Integer> V = mob.location().resourceChoices();
			Vector V2 = new Vector();
			if (V != null)
				for (int v = 0; v < V.size(); v++) {
					if ((V.get(v).intValue() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_WOODEN)
						V2.addElement(V.get(v));
				}
			if (V2.size() > 0)
				material = ((Integer) V2.elementAt(CMLib.dice().roll(1,
						V2.size(), -1))).intValue();
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		// now see if it worked
		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto), auto ? ""
							: "^S<S-NAME> chant(s) to the trees.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				ItemCraftor A = (ItemCraftor) CMClass.getAbility("Carpentry");
				ItemCraftor.ItemKeyPair pair = null;
				if (A != null)
					pair = A.craftAnyItem(material);
				if (pair == null) {
					mob.tell("The chant failed for some reason...");
					return false;
				}
				Item building = pair.item;
				Item key = pair.key;
				mob.location().addItem(building, ItemPossessor.Expire.Resource);
				if (key != null)
					mob.location().addItem(key, ItemPossessor.Expire.Resource);
				mob.location().showHappens(CMMsg.MSG_OK_ACTION,
						building.name() + " grows out of a tree and drops.");
				mob.location().recoverPhyStats();
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s) to the trees, but nothing happens.");

		// return whether it worked
		return success;
	}
}