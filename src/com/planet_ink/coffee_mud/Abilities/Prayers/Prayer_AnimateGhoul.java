package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.DeadBody;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
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

@SuppressWarnings("rawtypes")
public class Prayer_AnimateGhoul extends Prayer {
	public String ID() {
		return "Prayer_AnimateGhoul";
	}

	public String name() {
		return "Animate Ghoul";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_DEATHLORE;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public int enchantQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = getAnyTarget(mob, commands, givenTarget,
				Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;

		if (target == mob) {
			mob.tell(target.name(mob) + " doesn't look dead yet.");
			return false;
		}
		if (!(target instanceof DeadBody)) {
			mob.tell("You can't animate that.");
			return false;
		}

		DeadBody body = (DeadBody) target;
		if (body.playerCorpse()
				|| (body.mobName().length() == 0)
				|| ((body.charStats() != null)
						&& (body.charStats().getMyRace() != null) && (body
						.charStats().getMyRace().racialCategory()
							.equalsIgnoreCase("Undead")))) {
			mob.tell("You can't animate that.");
			return false;
		}
		String race = "a";
		if ((body.charStats() != null)
				&& (body.charStats().getMyRace() != null))
			race = CMLib.english()
					.startWithAorAn(body.charStats().getMyRace().name())
					.toLowerCase();
		String description = body.mobDescription();
		if (description.trim().length() == 0)
			description = "It looks dead.";
		else
			description += "\n\rIt also looks dead.";

		if (body.basePhyStats().level() < 5) {
			mob.tell("This creature is too weak to create a ghoul from.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> " + prayForWord(mob)
									+ " to animate <T-NAMESELF> as a ghoul.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				MOB newMOB = CMClass.getMOB("GenUndead");
				newMOB.setName(race + " ghoul");
				newMOB.setDescription(description);
				newMOB.setDisplayText(race + " ghoul is here");
				newMOB.basePhyStats().setLevel(
						4 + (super.getX1Level(mob) * 2)
								+ super.getXLEVELLevel(mob));
				newMOB.baseCharStats().setStat(CharStats.STAT_GENDER,
						body.charStats().getStat(CharStats.STAT_GENDER));
				newMOB.baseCharStats().setMyRace(CMClass.getRace("Undead"));
				newMOB.baseCharStats().setBodyPartsFromStringAfterRace(
						body.charStats().getBodyPartsAsString());
				Ability P = CMClass.getAbility("Prop_StatTrainer");
				if (P != null) {
					P.setMiscText("NOTEACH STR=20 INT=10 WIS=10 CON=10 DEX=15 CHA=2");
					newMOB.addNonUninvokableEffect(P);
				}
				newMOB.basePhyStats().setSensesMask(PhyStats.CAN_SEE_DARK);
				newMOB.recoverCharStats();
				newMOB.basePhyStats().setAttackAdjustment(
						CMLib.leveler().getLevelAttack(newMOB));
				newMOB.basePhyStats().setDamage(
						CMLib.leveler().getLevelMOBDamage(newMOB));
				CMLib.factions().setAlignment(newMOB, Faction.Align.EVIL);
				newMOB.baseState().setHitPoints(
						25 * newMOB.basePhyStats().level());
				newMOB.baseState().setMovement(
						CMLib.leveler().getLevelMove(newMOB));
				newMOB.basePhyStats().setArmor(
						CMLib.leveler().getLevelMOBArmor(newMOB));
				newMOB.baseState().setMana(100);
				newMOB.addNonUninvokableEffect(CMClass
						.getAbility("Prop_ModExperience"));
				newMOB.recoverCharStats();
				newMOB.recoverPhyStats();
				newMOB.recoverMaxState();
				newMOB.resetToMaxState();
				newMOB.addAbility(CMClass.getAbility("WeakParalysis"));
				Behavior B = CMClass.getBehavior("CombatAbilities");
				newMOB.addBehavior(B);
				B = CMClass.getBehavior("Aggressive");
				if (B != null) {
					B.setParms("+NAMES \"-" + mob.Name() + "\"");
					newMOB.addBehavior(B);
				}
				newMOB.addNonUninvokableEffect(CMClass
						.getAbility("Spell_CauseStink"));
				newMOB.text();
				newMOB.bringToLife(mob.location(), true);
				CMLib.beanCounter().clearZeroMoney(newMOB, null);
				newMOB.location().showOthers(newMOB, null, CMMsg.MSG_OK_ACTION,
						"<S-NAME> appears!");
				int it = 0;
				while (it < newMOB.location().numItems()) {
					Item item = newMOB.location().getItem(it);
					if ((item != null) && (item.container() == body)) {
						CMMsg msg2 = CMClass.getMsg(newMOB, body, item,
								CMMsg.MSG_GET, null);
						newMOB.location().send(newMOB, msg2);
						CMMsg msg4 = CMClass.getMsg(newMOB, item, null,
								CMMsg.MSG_GET, null);
						newMOB.location().send(newMOB, msg4);
						CMMsg msg3 = CMClass.getMsg(newMOB, item, null,
								CMMsg.MSG_WEAR, null);
						newMOB.location().send(newMOB, msg3);
						if (!newMOB.isMine(item))
							it++;
						else
							it = 0;
					} else
						it++;
				}
				body.destroy();
				newMOB.setStartRoom(null);
				mob.location().show(newMOB, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> begin(s) to rise!");
				mob.location().recoverRoomStats();
			}
		} else
			return beneficialWordsFizzle(mob, target, "<S-NAME> "
					+ prayForWord(mob)
					+ " to animate <T-NAMESELF>, but fail(s) miserably.");

		// return whether it worked
		return success;
	}
}
