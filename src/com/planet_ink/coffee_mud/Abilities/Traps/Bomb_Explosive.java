package com.planet_ink.coffee_mud.Abilities.Traps;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Bomb_Explosive extends StdBomb {
	public String ID() {
		return "Bomb_Explosive";
	}

	public String name() {
		return "explosive bomb";
	}

	protected int trapLevel() {
		return 10;
	}

	public String requiresToSet() {
		return "a pound of coal";
	}

	public List<Item> getTrapComponents() {
		Vector V = new Vector();
		V.addElement(CMLib.materials().makeItemResource(
				RawMaterial.RESOURCE_COAL));
		return V;
	}

	public boolean canSetTrapOn(MOB mob, Physical P) {
		if (!super.canSetTrapOn(mob, P))
			return false;
		if ((!(P instanceof Item))
				|| (((Item) P).material() != RawMaterial.RESOURCE_COAL)) {
			if (mob != null)
				mob.tell("You need some coal to make this out of.");
			return false;
		}
		return true;
	}

	public void spring(MOB target) {
		if (target.location() != null) {
			if ((!invoker().mayIFight(target))
					|| (isLocalExempt(target))
					|| (invoker().getGroupMembers(new HashSet<MOB>())
							.contains(target)) || (target == invoker())
					|| (doesSaveVsTraps(target)))
				target.location().show(target, null, null,
						CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
						"<S-NAME> avoid(s) the explosive!");
			else if (target.location().show(
					invoker(),
					target,
					this,
					CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
					(affected.name() + " explodes all over <T-NAME>!")
							+ CMLib.protocol().msp("explode.wav", 30))) {
				super.spring(target);
				CMLib.combat().postDamage(invoker(), target, null,
						CMLib.dice().roll(trapLevel() + abilityCode(), 10, 1),
						CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE,
						Weapon.TYPE_BURNING, "The blast <DAMAGE> <T-NAME>!");
			}
		}
	}

}
