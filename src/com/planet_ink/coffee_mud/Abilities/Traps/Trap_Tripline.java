package com.planet_ink.coffee_mud.Abilities.Traps;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.Trap;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
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
public class Trap_Tripline extends StdTrap {
	public String ID() {
		return "Trap_Tripline";
	}

	public String name() {
		return "tripline";
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int trapLevel() {
		return 1;
	}

	public String requiresToSet() {
		return "a pound of cloth";
	}

	public int baseRejuvTime(int level) {
		return 2;
	}

	public Trap setTrap(MOB mob, Physical P, int trapBonus,
			int qualifyingClassLevel, boolean perm) {
		if (P == null)
			return null;
		if (mob != null) {
			Item I = findMostOfMaterial(mob.location(),
					RawMaterial.MATERIAL_CLOTH);
			if (I != null)
				super.destroyResources(mob.location(), I.material(), 1);
		}
		return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
	}

	public List<Item> getTrapComponents() {
		Vector V = new Vector();
		V.addElement(CMLib.materials().makeItemResource(
				RawMaterial.RESOURCE_COTTON));
		return V;
	}

	public boolean canSetTrapOn(MOB mob, Physical P) {
		if (!super.canSetTrapOn(mob, P))
			return false;
		if (mob != null) {
			if (findMostOfMaterial(mob.location(), RawMaterial.MATERIAL_CLOTH) == null) {
				mob.tell("You'll need to set down at least a pound of cloth first.");
				return false;
			}
		}
		return true;
	}

	public void spring(MOB target) {
		if ((target != invoker()) && (!CMLib.flags().isInFlight(target))
				&& (target.location() != null)) {
			if ((doesSaveVsTraps(target))
					|| (invoker().getGroupMembers(new HashSet<MOB>())
							.contains(target)))
				target.location().show(target, null, null,
						CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
						"<S-NAME> avoid(s) tripping on a taut rope!");
			else if (target.location().show(target, target, this,
					CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
					"<S-NAME> trip(s) on a taut rope!")) {
				super.spring(target);
				target.basePhyStats().setDisposition(
						target.basePhyStats().disposition()
								| PhyStats.IS_SITTING);
				target.recoverPhyStats();
			}
		}
	}
}
