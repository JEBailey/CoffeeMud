package com.planet_ink.coffee_mud.Abilities.Traps;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.Trap;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Food;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Bomb_Spores extends StdBomb {
	public String ID() {
		return "Bomb_Spores";
	}

	public String name() {
		return "spore bomb";
	}

	protected int trapLevel() {
		return 15;
	}

	public String requiresToSet() {
		return "some diseased meat";
	}

	public List<Ability> returnOffensiveAffects(Physical fromMe) {
		Vector offenders = new Vector();

		for (final Enumeration<Ability> a = fromMe.effects(); a
				.hasMoreElements();) {
			final Ability A = a.nextElement();
			if ((A != null)
					&& ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_DISEASE))
				offenders.addElement(A);
		}
		return offenders;
	}

	public List<Item> getTrapComponents() {
		Vector V = new Vector();
		Item I = CMLib.materials().makeItemResource(RawMaterial.RESOURCE_MEAT);
		Ability A = CMClass.getAbility(text());
		if (A == null)
			A = CMClass.getAbility("Disease_Cold");
		I.addNonUninvokableEffect(A);
		V.addElement(I);
		return V;
	}

	public boolean canSetTrapOn(MOB mob, Physical P) {
		if (!super.canSetTrapOn(mob, P))
			return false;
		List<Ability> V = returnOffensiveAffects(P);
		if ((!(P instanceof Food)) || (V.size() == 0)) {
			if (mob != null)
				mob.tell("You need some diseased meat to make this out of.");
			return false;
		}
		return true;
	}

	public Trap setTrap(MOB mob, Physical P, int trapBonus,
			int qualifyingClassLevel, boolean perm) {
		if (P == null)
			return null;
		List<Ability> V = returnOffensiveAffects(P);
		if (V.size() > 0)
			setMiscText(V.get(0).ID());
		return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
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
						"<S-NAME> avoid(s) the poison gas!");
			else if (target.location().show(invoker(), target, this,
					CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE,
					affected.name() + " spews poison gas all over <T-NAME>!")) {
				super.spring(target);
				Ability A = CMClass.getAbility(text());
				if (A == null)
					A = CMClass.getAbility("Disease_Cold");
				if (A != null)
					A.invoke(invoker(), target, true, 0);
			}
		}
	}

}
