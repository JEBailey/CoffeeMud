package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.Food;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Pill;
import com.planet_ink.coffee_mud.Items.interfaces.Potion;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.Decayable;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.ShopKeeper;

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
public class Prayer_Rot extends Prayer {
	public String ID() {
		return "Prayer_Rot";
	}

	public String name() {
		return "Rot";
	}

	public String displayText() {
		return "";
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS;
	}

	protected int canTargetCode() {
		return Ability.CAN_ITEMS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	public int classificationCode() {
		return ((affecting() instanceof Food) && (!canBeUninvoked())) ? Ability.ACODE_PROPERTY
				: Ability.ACODE_PRAYER | Ability.DOMAIN_CORRUPTION;
	}

	private long nextTry = System.currentTimeMillis();

	public void executeMsg(Environmental host, CMMsg msg) {
		super.executeMsg(host, msg);
		if ((host instanceof Item)
				&& (((msg.tool() instanceof ShopKeeper) && (msg.targetMinor() == CMMsg.TYP_GET)) || (msg
						.targetMinor() == CMMsg.TYP_ROOMRESET))
				&& (msg.target() == host)) {
			if (host instanceof Decayable) {
				((Decayable) host).setDecayTime(0);
				if (host instanceof Physical) {
					Ability A = ((Physical) host).fetchEffect("Poison_Rotten");
					if (A != null)
						((Physical) host).delEffect(A);
				}
			} else if (host instanceof Container) {
				List<Item> V = ((Container) host).getContents();
				for (int f = 0; f < V.size(); f++)
					if (V.get(f) != null) {
						if (V.get(f) instanceof Decayable)
							((Decayable) V.get(f)).setDecayTime(0);
						Ability A = V.get(f).fetchEffect("Poison_Rotten");
						if (A != null)
							V.get(f).delEffect(A);
					}
			}
		}
	}

	public void setRot(Item I) {
		if (((I instanceof Decayable) && (((Decayable) I).decayTime() == 0))
				&& (I.owner() != null)
				&& (I.fetchEffect("Poison_Rotten") == null)) {
			long newTime = 0;
			switch (I.material() & RawMaterial.MATERIAL_MASK) {
			case RawMaterial.MATERIAL_FLESH: {
				newTime = System.currentTimeMillis()
						+ (CMProps.getTickMillis() * CMProps
								.getIntVar(CMProps.Int.TICKSPERMUDDAY));
				break;
			}
			case RawMaterial.MATERIAL_VEGETATION: {
				newTime = System.currentTimeMillis()
						+ (CMProps.getTickMillis()
								* CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY) * 5);
				break;
			}
			}
			if (I instanceof Drink) {
				switch (((Drink) I).liquidType()) {
				case RawMaterial.RESOURCE_BLOOD:
					newTime = System.currentTimeMillis()
							+ (CMProps.getTickMillis() * CMProps
									.getIntVar(CMProps.Int.TICKSPERMUDDAY));
					break;
				case RawMaterial.RESOURCE_MILK:
					newTime = System.currentTimeMillis()
							+ (CMProps.getTickMillis()
									* CMProps
											.getIntVar(CMProps.Int.TICKSPERMUDDAY) * 5);
					break;
				}
			}
			switch (I.material()) {
			case RawMaterial.RESOURCE_BLOOD:
				newTime = System.currentTimeMillis()
						+ (CMProps.getTickMillis() * CMProps
								.getIntVar(CMProps.Int.TICKSPERMUDDAY));
				break;
			case RawMaterial.RESOURCE_MILK:
				newTime = System.currentTimeMillis()
						+ (CMProps.getTickMillis()
								* CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY) * 5);
				break;
			case RawMaterial.RESOURCE_HERBS:
			case RawMaterial.RESOURCE_WAX:
			case RawMaterial.RESOURCE_COFFEEBEANS:
			case RawMaterial.RESOURCE_SEAWEED:
			case RawMaterial.RESOURCE_SUGAR:
			case RawMaterial.RESOURCE_COCOA:
			case RawMaterial.RESOURCE_MUSHROOMS:
			case RawMaterial.RESOURCE_VINE:
			case RawMaterial.RESOURCE_FLOWERS:
			case RawMaterial.RESOURCE_NUTS:
			case RawMaterial.RESOURCE_CRACKER:
			case RawMaterial.RESOURCE_PIPEWEED:
			case RawMaterial.RESOURCE_GARLIC:
			case RawMaterial.RESOURCE_SOAP:
			case RawMaterial.RESOURCE_ASH:
				newTime = 0;
				break;
			}
			if (I instanceof Decayable)
				((Decayable) I).setDecayTime(newTime);
		}
		if ((((Decayable) I).decayTime() > 0)
				&& (System.currentTimeMillis() > ((Decayable) I).decayTime())
				&& (!CMLib.flags().isABonusItems(I))) {
			if (I.fetchEffect("Poison_Rotten") == null) {
				Ability A = CMClass.getAbility("Poison_Rotten");
				if (A != null)
					I.addNonUninvokableEffect(A);
			}
			if (I instanceof Food)
				((Food) I).setNourishment(0);
			else if (I instanceof Drink)
				((Drink) I).setThirstQuenched(0);
			((Decayable) I).setDecayTime(0);
		}
	}

	public boolean okMessage(Environmental host, CMMsg msg) {
		if ((affecting() == null) || (System.currentTimeMillis() > nextTry)) {
			nextTry = System.currentTimeMillis() + 60000;
			if (host instanceof Item)
				setRot(((Item) host));
			else if (host instanceof Container) {
				List<Item> V = ((Container) host).getContents();
				for (int v = 0; v < V.size(); v++)
					if (V.get(v) != null)
						setRot(V.get(v));
			}
		}
		return super.okMessage(host, msg);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Item target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;

		if ((!(target instanceof Food)) && (!(target instanceof Drink))) {
			mob.tell("You cannot rot " + target.name(mob) + "!");
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
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> rot <T-NAMESELF>" + inTheNameOf(mob)
									+ ".^?", auto ? ""
							: "^S<S-NAME> rots <T-NAMESELF>" + inTheNameOf(mob)
									+ ".^?", auto ? ""
							: "^S<S-NAME> rots <T-NAMESELF>" + inTheNameOf(mob)
									+ ".^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				boolean doneSomething = false;
				if ((target instanceof Drink)
						&& (((Drink) target).liquidType() != RawMaterial.RESOURCE_SALTWATER)) {
					((Drink) target)
							.setLiquidType(RawMaterial.RESOURCE_SALTWATER);
					doneSomething = true;
				}
				Ability A = CMClass.getAbility("Poison_Rotten");
				if (A != null)
					target.addNonUninvokableEffect(A);
				if ((target instanceof Pill)
						&& (!((Pill) target).getSpellList().equals(
								"Prayer_DrunkenStupor"))) {
					doneSomething = true;
					((Pill) target).setSpellList("Prayer_DrunkenStupor");
				}
				if ((target instanceof Potion)
						&& (!((Potion) target).getSpellList().equals(
								"Prayer_DrunkenStupor"))) {
					doneSomething = true;
					((Potion) target).setSpellList("Prayer_DrunkenStupor");
				}
				if (doneSomething)
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,
							target.name() + " appears rotted!");
				target.recoverPhyStats();
				mob.location().recoverRoomStats();
			}
		} else
			return beneficialWordsFizzle(mob, target, "<S-NAME> "
					+ prayWord(mob) + " for rotting, but nothing happens.");
		// return whether it worked
		return success;
	}
}
