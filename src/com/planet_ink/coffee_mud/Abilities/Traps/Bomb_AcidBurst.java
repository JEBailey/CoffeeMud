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
public class Bomb_AcidBurst extends StdBomb {
	public String ID() {
		return "Bomb_AcidBurst";
	}

	public String name() {
		return "acid burst bomb";
	}

	protected int trapLevel() {
		return 20;
	}

	public String requiresToSet() {
		return "some lemons";
	}

	public List<Item> getTrapComponents() {
		Vector V = new Vector();
		V.addElement(CMLib.materials().makeItemResource(
				RawMaterial.RESOURCE_LEMONS));
		return V;
	}

	public boolean canSetTrapOn(MOB mob, Physical P) {
		if (!super.canSetTrapOn(mob, P))
			return false;
		if ((!(P instanceof Item))
				|| (((Item) P).material() != RawMaterial.RESOURCE_LEMONS)) {
			if (mob != null)
				mob.tell("You need some lemons to make this out of.");
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
						"<S-NAME> avoid(s) the acid burst!");
			else if (target.location().show(invoker(), target, this,
					CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
					affected.name() + " sprays acid all over <T-NAME>!")) {
				super.spring(target);
				CMLib.combat().postDamage(invoker(), target, null,
						CMLib.dice().roll(trapLevel() + abilityCode(), 24, 1),
						CMMsg.MASK_ALWAYS | CMMsg.TYP_ACID,
						Weapon.TYPE_MELTING, "The acid <DAMAGE> <T-NAME>!");
			}
		}
	}

}
