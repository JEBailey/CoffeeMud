package com.planet_ink.coffee_mud.Abilities.Traps;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.Trap;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Scroll;
import com.planet_ink.coffee_mud.Items.interfaces.SpellHolder;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Trap_SpellBlast extends StdTrap {
	public String ID() {
		return "Trap_SpellBlast";
	}

	public String name() {
		return "spell blast";
	}

	protected int canAffectCode() {
		return Ability.CAN_EXITS | Ability.CAN_ITEMS;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int trapLevel() {
		return 23;
	}

	public String requiresToSet() {
		return "a spell scroll";
	}

	protected Item getPoison(MOB mob) {
		if (mob == null)
			return null;
		if (mob.location() == null)
			return null;
		for (int i = 0; i < mob.location().numItems(); i++) {
			Item I = mob.location().getItem(i);
			if ((I != null) && (I instanceof Scroll)
					&& (((SpellHolder) I).getSpells() != null)
					&& (((SpellHolder) I).getSpells().size() > 0)
					&& (I.usesRemaining() > 0))
				return I;
		}
		return null;
	}

	public List<Item> getTrapComponents() {
		Vector V = new Vector();
		Scroll I = (Scroll) CMClass.getMiscMagic("StdScroll");
		Ability A = CMClass.getAbility(text());
		if (A == null)
			A = CMClass.getAbility("Spell_Fireball");
		I.setSpellList(A.ID());
		V.addElement(I);
		return V;
	}

	public Trap setTrap(MOB mob, Physical P, int trapBonus,
			int qualifyingClassLevel, boolean perm) {
		if (P == null)
			return null;
		Item I = getPoison(mob);
		if ((I != null) && (I instanceof SpellHolder)) {
			List<Ability> V = ((SpellHolder) I).getSpells();
			if (V.size() > 0)
				setMiscText(V.get(0).ID());
			I.setUsesRemaining(I.usesRemaining() - 1);
		}
		return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
	}

	public boolean canSetTrapOn(MOB mob, Physical P) {
		if (!super.canSetTrapOn(mob, P))
			return false;
		Item I = getPoison(mob);
		if ((I == null) && (mob != null)) {
			mob.tell("You'll need to set down a scroll with a spell first.");
			return false;
		}
		return true;
	}

	public void spring(MOB target) {
		if ((target != invoker()) && (target.location() != null)) {
			if ((!invoker().mayIFight(target))
					|| (isLocalExempt(target))
					|| (invoker().getGroupMembers(new HashSet<MOB>())
							.contains(target)) || (target == invoker())
					|| (doesSaveVsTraps(target)))
				target.location().show(target, null, null,
						CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
						"<S-NAME> avoid(s) setting off a trap!");
			else if (target.location().show(target, target, this,
					CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
					"<S-NAME> set(s) off a trap!")) {
				super.spring(target);
				Ability A = CMClass.getAbility(text());
				if (A == null)
					A = CMClass.getAbility("Spell_Fireball");
				if (A != null)
					A.invoke(invoker(), target, true, 0);
				if ((canBeUninvoked()) && (affected instanceof Item))
					disable();
			}
		}
	}
}
