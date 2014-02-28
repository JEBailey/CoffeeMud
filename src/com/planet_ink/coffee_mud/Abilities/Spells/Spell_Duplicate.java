package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.ArchonOnly;
import com.planet_ink.coffee_mud.Items.interfaces.ClanItem;
import com.planet_ink.coffee_mud.Items.interfaces.Coins;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Pill;
import com.planet_ink.coffee_mud.Items.interfaces.Potion;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wand;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
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
@SuppressWarnings("rawtypes")
public class Spell_Duplicate extends Spell {
	public String ID() {
		return "Spell_Duplicate";
	}

	public String name() {
		return "Duplicate";
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
	}

	protected int overridemana() {
		return Ability.COST_ALL;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Item target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;

		if (!mob.isMine(target)) {
			mob.tell("You'll need to pick it up first.");
			return false;
		}
		if (target instanceof ClanItem) {
			mob.tell("Clan items can not be duplicated.");
			return false;
		}
		if (target instanceof ArchonOnly) {
			mob.tell("That item can not be duplicated.");
			return false;
		}

		int value = (target instanceof Coins) ? (int) Math
				.round(((Coins) target).getTotalValue()) : target.value();
		int multiPlier = 5 + (((target.phyStats().weight()) + value) / 2);
		multiPlier += (target.numEffects() * 10);
		multiPlier += (target instanceof Potion) ? 10 : 0;
		multiPlier += (target instanceof Pill) ? 10 : 0;
		multiPlier += (target instanceof Wand) ? 5 : 0;

		int level = target.phyStats().level();
		if (level <= 0)
			level = 1;
		int expLoss = (level * multiPlier);
		if ((mob.getExperience() - expLoss) < 0) {
			mob.tell("You don't have enough experience to cast this spell.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		expLoss = getXPCOSTAdjustment(mob, -expLoss);
		mob.tell("You lose " + (-expLoss) + " experience points.");
		CMLib.leveler().postExperience(mob, null, null, expLoss, false);

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? ""
									: "^S<S-NAME> hold(s) <T-NAMESELF> and cast(s) a spell.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Item newTarget = (Item) target.copyOf();
				mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
						target.name() + " blurs and divides into two!");
				CMLib.utensils().disenchantItem(newTarget);
				if (newTarget.amDestroyed())
					mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
							"<T-NAME> fades away!");
				else {
					newTarget.recoverPhyStats();
					if (target.owner() instanceof MOB)
						((MOB) target.owner()).addItem(newTarget);
					else if (target.owner() instanceof Room)
						((Room) target.owner()).addItem(newTarget,
								ItemPossessor.Expire.Player_Drop);
					else
						mob.addItem(newTarget);
					if (newTarget instanceof Coins)
						((Coins) newTarget).putCoinsBack();
					else if (newTarget instanceof RawMaterial)
						((RawMaterial) newTarget).rebundle();
					target.recoverPhyStats();
					mob.recoverPhyStats();
				}
			}

		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> hold(s) <T-NAMESELF> tightly and incant(s), the spell fizzles.");

		// return whether it worked
		return success;
	}
}
