package com.planet_ink.coffee_mud.Abilities.Songs;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Armor;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
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
public class Skill_Buffoonery extends BardSkill {
	public String ID() {
		return "Skill_Buffoonery";
	}

	public String name() {
		return "Buffoonery";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	private static final String[] triggerStrings = { "BUFFOONERY" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_FOOLISHNESS;
	}

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	protected Vector getFreeWearingPositions(MOB target) {
		Vector V = new Vector();
		Wearable.CODES codes = Wearable.CODES.instance();
		boolean[] pos = new boolean[codes.all_ordered().length];

		for (int i = 0; i < pos.length; i++)
			if (target.freeWearPositions(codes.all_ordered()[i], (short) 0,
					(short) 0) > 0)
				pos[i] = false;
			else
				pos[i] = true;

		for (int i = 0; i < pos.length; i++)
			if (!pos[i])
				V.addElement(Long.valueOf(codes.all_ordered()[i]));
		return V;
	}

	protected boolean freePosition(MOB target) {
		return getFreeWearingPositions(target).size() > 0;
	}

	public String correctItem(MOB mob) {
		for (int i = 0; i < mob.numItems(); i++) {
			Item I = mob.getItem(i);
			if ((I != null)
					&& (CMLib.flags().canBeSeenBy(I, mob))
					&& (I.amWearingAt(Wearable.IN_INVENTORY))
					&& (!((((I instanceof Armor) && (I.basePhyStats().armor() > 1)) || ((I instanceof Weapon) && (I
							.basePhyStats().damage() > 1))))))
				return I.Name();
		}
		return null;
	}

	public Item targetItem(MOB target) {
		Vector V = new Vector();
		for (int i = 0; i < target.numItems(); i++) {
			Item I2 = target.getItem(i);
			if ((!I2.amWearingAt(Wearable.IN_INVENTORY))
					&& (((I2 instanceof Weapon) && (I2.basePhyStats().damage() > 1)) || ((I2 instanceof Armor) && (I2
							.basePhyStats().armor() > 1)))
					&& (I2.container() == null))
				V.addElement(I2);
		}
		if (V.size() > 0)
			return (Item) V.elementAt(CMLib.dice().roll(1, V.size(), -1));
		return null;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			String parm = correctItem(mob);
			if (parm == null)
				return Ability.QUALITY_INDIFFERENT;
			if (target instanceof MOB) {
				Item targetItem = targetItem((MOB) target);
				if (targetItem == null) {
					if (!freePosition((MOB) target))
						return Ability.QUALITY_INDIFFERENT;
				}
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (commands.size() < 2) {
			if (mob.isMonster() && (commands.size() == 1)) {
				String parm = correctItem(mob);
				if (parm != null)
					commands.addElement(parm);
			}
			if (commands.size() < 2) {
				mob.tell("You must specify a target, and what item to swap on the target!");
				return false;
			}
		}
		Item I = mob.findItem(null, (String) commands.lastElement());
		if ((I == null) || (!CMLib.flags().canBeSeenBy(I, mob))) {
			mob.tell("You don't seem to have '"
					+ ((String) commands.lastElement()) + "'.");
			return false;
		}
		if (((I instanceof Armor) && (I.basePhyStats().armor() > 1))
				|| ((I instanceof Weapon) && (I.basePhyStats().damage() > 1))) {
			mob.tell(I.name(mob) + " is not buffoonish enough!");
			return false;
		}
		commands.removeElementAt(commands.size() - 1);

		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		Item targetItem = targetItem(target);
		if (targetItem == null) {
			if (!freePosition(target)) {
				mob.tell(target.name(mob) + " has no free wearing positions!");
				return false;
			}
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		int levelDiff = target.phyStats().level() - mob.phyStats().level();

		boolean success = proficiencyCheck(mob, 0, auto);
		if (levelDiff > 0)
			levelDiff = -(levelDiff * ((!CMLib.flags().canBeSeenBy(mob, target)) ? 5
					: 15));
		else
			levelDiff = -(levelDiff * ((!CMLib.flags().canBeSeenBy(mob, target)) ? 1
					: 2));

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob, target, this, (CMMsg.MSG_NOISYMOVEMENT
							| CMMsg.MASK_DELICATE | CMMsg.MASK_MALICIOUS)
							| (auto ? CMMsg.MASK_ALWAYS : 0), auto ? ""
							: "<S-NAME> do(es) buffoonery to <T-NAMESELF>.");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				long position = -1;
				if (targetItem != null) {
					position = targetItem.rawWornCode();
					targetItem.unWear();
				} else {
					Vector free = getFreeWearingPositions(target);
					if (free.size() < 1) {
						mob.tell(target.name(mob)
								+ " has no free wearing positions!");
						return false;
					}
					if ((free.contains(Long.valueOf(Wearable.WORN_WIELD)))
							&& ((I instanceof Weapon) || (!(I instanceof Armor))))
						position = Wearable.WORN_WIELD;
					else
						position = ((Long) free.elementAt(CMLib.dice().roll(1,
								free.size(), -1))).longValue();
				}
				if (position >= 0) {
					I.unWear();
					target.moveItemTo(I);
					I.wearAt(position);
				}
			}
		} else
			return beneficialVisualFizzle(mob, target,
					"<S-NAME> attempt(s) buffoonery on <T-NAMESELF>, but fail(s).");

		return success;
	}

}
