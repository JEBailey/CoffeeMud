package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

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
public class Spell_Phantasm extends Spell {
	public String ID() {
		return "Spell_Phantasm";
	}

	public String name() {
		return "Phantasm";
	}

	public String displayText() {
		return "(Phantasm)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public int enchantQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	MOB myTarget = null;

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ILLUSION;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (tickID == Tickable.TICKID_MOB) {
			if ((affected != null) && (affected instanceof MOB)) {
				MOB mob = (MOB) affected;
				if (myTarget == null)
					myTarget = mob.getVictim();

				if ((myTarget != mob.getVictim()) || (myTarget == null)) {
					if (mob.amDead())
						mob.setLocation(null);
					else if (mob.location() != null)
						mob.location()
								.show(mob, null, CMMsg.MSG_QUIETMOVEMENT,
										"<S-NAME> look(s) around for someone to fight...");
					((MOB) affected).destroy();
				}
			}
		}
		return super.tick(ticking, tickID);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		super.executeMsg(myHost, msg);
		if ((affected != null) && (affected instanceof MOB)) {
			MOB mob = (MOB) affected;
			if (msg.amITarget(mob)
					&& (msg.sourceMinor() == CMMsg.TYP_CAST_SPELL)) {
				msg.source().tell(
						mob.name(msg.source())
								+ " seems strangely unaffected by your magic.");
				return false;
			}
		}
		return true;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if ((affected != null) && (affected instanceof MOB)) {
			MOB mob = (MOB) affected;
			if ((msg.amISource(mob) || msg.amISource(mob.amFollowing()))
					&& (msg.sourceMinor() == CMMsg.TYP_QUIT)) {
				unInvoke();
				if (msg.source().playerStats() != null)
					msg.source().playerStats().setLastUpdated(0);
			} else if (msg.amITarget(mob)
					&& (msg.targetMinor() == CMMsg.TYP_DAMAGE))
				msg.addTrailerMsg(CMClass.getMsg(mob, null, CMMsg.MSG_QUIT, msg
						.source().name()
						+ "'s attack somehow went THROUGH <T-NAMESELF>."));
		}
	}

	public void unInvoke() {
		MOB mob = (MOB) affected;
		super.unInvoke();
		if ((canBeUninvoked()) && (mob != null)) {
			if (mob.amDead())
				mob.setLocation(null);
			mob.destroy();
		}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		String type = null;
		if (mob.isMonster()) {
			Race R = CMClass.randomRace();
			for (int i = 0; i < 10; i++) {
				if ((R != null) && (CMProps.isTheme(R.availabilityCode()))
						&& (R != mob.charStats().getMyRace()))
					break;
				R = CMClass.randomRace();
			}
			if (R != null) {
				type = R.name();
			} else
				return false;
		} else {
			if (commands.size() == 0) {
				mob.tell("You must specify the type of creature to create a phantasm of!");
				return false;
			}
			type = CMStrings.capitalizeAndLower(CMParms.combine(commands, 0));
		}
		Race R = CMClass.getRace(type);
		if ((R == null) || (!CMProps.isTheme(R.availabilityCode()))) {
			mob.tell("You don't know how to create a phantasm of a '" + type
					+ "'.");
			return false;
		}

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			invoker = mob;
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto), auto ? ""
							: "^S<S-NAME> incant(s), calling on the name of "
									+ type + ".^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				MOB myMonster = determineMonster(mob, R, mob.phyStats().level()
						+ (2 * getXLEVELLevel(mob)));
				myMonster.setVictim(mob.getVictim());
				CMLib.commands().postFollow(myMonster, mob, true);
				if (myMonster.getVictim() != null)
					myMonster.getVictim().setVictim(myMonster);
				invoker = mob;
				beneficialAffect(mob, myMonster, asLevel, 0);
				if (myMonster.amFollowing() != mob)
					mob.tell(myMonster.name()
							+ " seems unwilling to follow you.");
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> incant(s) to summon a " + type + ", but fails.");

		// return whether it worked
		return success;
	}

	public MOB determineMonster(MOB caster, Race R, int level) {
		MOB newMOB = CMClass.getMOB("GenMob");
		newMOB.basePhyStats().setAbility(11);
		CharClass C = CMClass.getCharClass("Fighter");
		newMOB.baseCharStats().setCurrentClass(C);
		newMOB.basePhyStats().setLevel(level);
		CMLib.factions().setAlignment(newMOB, Faction.Align.EVIL);
		newMOB.basePhyStats().setWeight(850);
		newMOB.basePhyStats().setRejuv(PhyStats.NO_REJUV);
		newMOB.baseCharStats().setStat(CharStats.STAT_STRENGTH, 25);
		newMOB.baseCharStats().setStat(CharStats.STAT_DEXTERITY, 25);
		newMOB.baseCharStats().setStat(CharStats.STAT_CONSTITUTION, 25);
		newMOB.baseCharStats().setMyRace(R);
		newMOB.baseCharStats().getMyRace().startRacing(newMOB, false);
		newMOB.recoverPhyStats();
		newMOB.recoverCharStats();
		newMOB.basePhyStats().setAttackAdjustment(
				CMLib.leveler().getLevelAttack(newMOB));
		newMOB.basePhyStats()
				.setArmor(CMLib.leveler().getLevelMOBArmor(newMOB));
		newMOB.basePhyStats().setDamage(
				CMLib.leveler().getLevelMOBDamage(newMOB));
		newMOB.basePhyStats()
				.setSpeed(CMLib.leveler().getLevelMOBSpeed(newMOB));
		newMOB.baseCharStats().setStat(CharStats.STAT_GENDER, 'M');
		newMOB.addNonUninvokableEffect(CMClass.getAbility("Prop_ModExperience"));
		newMOB.setName("a ferocious " + R.name().toLowerCase());
		newMOB.setDisplayText("a ferocious " + R.name().toLowerCase()
				+ " is stalking around here");
		newMOB.setDescription("");
		newMOB.recoverCharStats();
		newMOB.recoverPhyStats();
		newMOB.recoverMaxState();
		newMOB.resetToMaxState();
		newMOB.text();
		newMOB.bringToLife(caster.location(), true);
		CMLib.beanCounter().clearZeroMoney(newMOB, null);
		newMOB.location().showOthers(newMOB, null, CMMsg.MSG_OK_ACTION,
				"<S-NAME> appears!");
		caster.location().recoverRoomStats();
		newMOB.setStartRoom(null);
		return (newMOB);
	}
}
