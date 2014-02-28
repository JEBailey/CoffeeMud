package com.planet_ink.coffee_mud.Abilities.Traps;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.Trap;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
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
public class Trap_Ignition extends StdTrap {
	public String ID() {
		return "Trap_Ignition";
	}

	public String name() {
		return "ignition trap";
	}

	protected int canAffectCode() {
		return Ability.CAN_ITEMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int trapLevel() {
		return 8;
	}

	public String requiresToSet() {
		return "a container of lamp oil";
	}

	protected Item getPoison(MOB mob) {
		if (mob == null)
			return null;
		if (mob.location() == null)
			return null;
		for (int i = 0; i < mob.location().numItems(); i++) {
			Item I = mob.location().getItem(i);
			if ((I != null)
					&& (I instanceof Drink)
					&& (((((Drink) I).containsDrink()) && (((Drink) I)
							.liquidType() == RawMaterial.RESOURCE_LAMPOIL)) || (I
							.material() == RawMaterial.RESOURCE_LAMPOIL)))
				return I;
		}
		return null;
	}

	public Trap setTrap(MOB mob, Physical P, int trapBonus,
			int qualifyingClassLevel, boolean perm) {
		if (P == null)
			return null;
		Item I = getPoison(mob);
		if ((I != null) && (I instanceof Drink)) {
			((Drink) I).setLiquidHeld(0);
			I.destroy();
		}
		return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
	}

	public List<Item> getTrapComponents() {
		Vector V = new Vector();
		V.addElement(CMClass.getBasicItem("OilFlask"));
		return V;
	}

	public boolean canSetTrapOn(MOB mob, Physical P) {
		if (!super.canSetTrapOn(mob, P))
			return false;
		Item I = getPoison(mob);
		if ((I == null) && (mob != null)) {
			mob.tell("You'll need to set down a container of lamp oil first.");
			return false;
		}
		return true;
	}

	public void spring(MOB target) {
		if ((target != invoker()) && (target.location() != null)) {
			if ((doesSaveVsTraps(target))
					|| (invoker().getGroupMembers(new HashSet<MOB>())
							.contains(target)))
				target.location().show(target, null, null,
						CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
						"<S-NAME> avoid(s) setting off a trap!");
			else if (target.location().show(
					target,
					target,
					this,
					CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
					"<S-NAME> set(s) off a trap! "
							+ CMStrings.capitalizeAndLower(affected.name())
							+ " ignites!")) {
				super.spring(target);
				Ability B = CMClass.getAbility("Burning");
				if (B != null)
					B.invoke(invoker(), affected, true, (trapLevel() / 5)
							+ abilityCode());
				if (affected instanceof Item) {
					if (target.isMine(affected)) {
						target.location().show(target, affected, null,
								CMMsg.MSG_DROP,
								"<S-NAME> drop(s) the burning <T-NAME>!");
						if (target.isMine(affected))
							target.location().moveItemTo((Item) affected,
									ItemPossessor.Expire.Player_Drop);
					}
					if (canBeUninvoked())
						disable();
				}
			}
		}
	}
}
