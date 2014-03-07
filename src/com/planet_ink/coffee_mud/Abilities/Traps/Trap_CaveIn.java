package com.planet_ink.coffee_mud.Abilities.Traps;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.Trap;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Trap_CaveIn extends StdTrap {
	public String ID() {
		return "Trap_CaveIn";
	}

	public String name() {
		return "cave-in";
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int trapLevel() {
		return 22;
	}

	public String requiresToSet() {
		return "100 pounds of wood";
	}

	public int baseRejuvTime(int level) {
		return 6;
	}

	public List<Item> getTrapComponents() {
		Vector V = new Vector();
		for (int i = 0; i < 100; i++)
			V.addElement(CMLib.materials().makeItemResource(
					RawMaterial.RESOURCE_WOOD));
		return V;
	}

	public Trap setTrap(MOB mob, Physical P, int trapBonus,
			int qualifyingClassLevel, boolean perm) {
		if (P == null)
			return null;
		if (mob != null) {
			Item I = findMostOfMaterial(mob.location(),
					RawMaterial.MATERIAL_WOODEN);
			if (I != null)
				super.destroyResources(mob.location(), I.material(), 100);
		}
		return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
	}

	public boolean canSetTrapOn(MOB mob, Physical P) {
		if (!super.canSetTrapOn(mob, P))
			return false;
		if (mob != null) {
			Item I = findMostOfMaterial(mob.location(),
					RawMaterial.MATERIAL_WOODEN);
			if ((I == null)
					|| (super
							.findNumberOfResource(mob.location(), I.material()) < 100)) {
				mob.tell("You'll need to set down at least 100 pounds of wood first.");
				return false;
			}
		}
		if (P instanceof Room) {
			Room R = (Room) P;
			if (R.domainType() != Room.DOMAIN_INDOORS_CAVE) {
				if (mob != null)
					mob.tell("You can only set this trap in caves.");
				return false;
			}
		}
		return true;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((sprung) && (affected != null) && (!disabled()) && (tickDown >= 0)) {
			if (((msg.targetMinor() == CMMsg.TYP_LEAVE)
					|| (msg.targetMinor() == CMMsg.TYP_ENTER) || (msg
					.targetMinor() == CMMsg.TYP_FLEE))
					&& (msg.amITarget(affected))) {
				msg.source().tell(
						"The cave-in prevents entry or exit from here.");
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public void spring(MOB target) {
		if ((target != invoker()) && (target.location() != null)) {
			if ((doesSaveVsTraps(target))
					|| (invoker().getGroupMembers(new HashSet<MOB>())
							.contains(target)))
				target.location().show(target, null, null,
						CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
						"<S-NAME> avoid(s) setting off a cave-in!");
			else if (target.location().show(target, target, this,
					CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
					"<S-NAME> trigger(s) a cave-in!")) {
				super.spring(target);
				if ((affected != null) && (affected instanceof Room)) {
					Room R = (Room) affected;
					for (int i = 0; i < R.numInhabitants(); i++) {
						MOB M = R.fetchInhabitant(i);
						if ((M != null) && (M != invoker()))
							if (invoker().mayIFight(M)) {
								int damage = CMLib.dice().roll(
										trapLevel() + abilityCode(), 20, 1);
								CMLib.combat().postDamage(
										invoker(),
										M,
										this,
										damage,
										CMMsg.MASK_MALICIOUS
												| CMMsg.MASK_ALWAYS
												| CMMsg.TYP_JUSTICE,
										Weapon.TYPE_BASHING,
										"The cave-in <DAMAGE> <T-NAME>!");
							}
					}
				}
			}
		}
	}
}