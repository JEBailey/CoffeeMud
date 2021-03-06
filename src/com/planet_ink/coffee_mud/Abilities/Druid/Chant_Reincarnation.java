package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Chant_Reincarnation extends Chant {
	public String ID() {
		return "Chant_Reincarnation";
	}

	public String name() {
		return "Reincarnation";
	}

	public String displayText() {
		return "(Reincarnation Geas)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	protected int overrideMana() {
		return 200;
	}

	Race newRace = null;

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (newRace != null) {
			if (affected.name().indexOf(' ') > 0)
				affectableStats.setName(CMLib.english().startWithAorAn(
						newRace.name())
						+ " called " + affected.name());
			else
				affectableStats.setName(affected.name() + " the "
						+ newRace.name());
			int oldAdd = affectableStats.weight()
					- affected.basePhyStats().weight();
			newRace.setHeightWeight(affectableStats, 'M');
			if (oldAdd > 0)
				affectableStats.setWeight(affectableStats.weight() + oldAdd);
		}
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		if (newRace != null)
			affectableStats.setMyRace(newRace);
	}

	public void unInvoke() {
		super.unInvoke();
		if ((!this.canBeUninvoked) && (affected != null)
				&& (affected.fetchEffect(ID()) == this))
			this.unInvoked = false;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((tickID == Tickable.TICKID_MOB) && (tickDown != Integer.MAX_VALUE)) {
			if ((tickDown <= 1) && (!unInvoked)) {
				tickDown = -1;
				// undo the affects of this spell
				if (!(affected instanceof MOB))
					return super.tick(ticking, tickID);
				MOB mob = (MOB) affected;
				mob.tell("Your reincarnation geas is lifted as your form solidifies.");
				if (newRace != null) {
					mob.baseCharStats().setMyRace(newRace);
					newRace.setHeightWeight(mob.basePhyStats(), (char) mob
							.charStats().getStat(CharStats.STAT_GENDER));
					mob.recoverPhyStats();
					mob.recoverCharStats();
					mob.recoverMaxState();
				}
				unInvoke();
				if (mob.location() != null)
					mob.location().recoverRoomStats();
			}
			if (!super.canBeUninvoked) // called during bring-to-life, which is
										// why its down here
			{
				if (CMLib.flags().isInTheGame(affected, true))
					super.canBeUninvoked = true;
				else
					tickDown--;
			}
		}
		return true;
	}

	public boolean isGolem(Race R) {
		MOB M = CMClass.getFactoryMOB();
		R.affectPhyStats(M, M.phyStats());
		boolean golem = CMLib.flags().isGolem(M);
		M.destroy();
		return golem;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return true;
		MOB mob = (MOB) affected;
		if ((msg.sourceMinor() == CMMsg.TYP_DEATH) && (msg.amISource(mob))) {
			newRace = null;
			while ((newRace == null)
					|| (isGolem(newRace))
					|| (!newRace.canBreedWith(newRace))
					|| (!CMath.bset(newRace.availabilityCode(),
							Area.THEME_FANTASY))
					|| (newRace == mob.charStats().getMyRace())
					|| (newRace.ID().equals("StdRace")))
				newRace = CMClass.randomRace();
			if (newRace != null)
				mob.tell("You are being reincarnated as a " + newRace.name()
						+ "!!");
			msg.source().recoverCharStats();
			msg.source().recoverPhyStats();
			super.canBeUninvoked = false; // without this, bring to life removes
											// it
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget, false, true);
		if (target == null)
			return false;
		if (target.fetchEffect(ID()) != null) {
			if (mob.location().show(mob, target, null, CMMsg.MSG_CAST,
					"<S-NAME> lift(s) the reincarnation geas on <T-NAMESELF>."))
				target.delEffect(target.fetchEffect(ID()));
			else
				mob.location()
						.show(mob, target, null, CMMsg.MSG_OK_VISUAL,
								"<S-NAME> fail(s) to lift the reincarnation geas on <T-NAMESELF>.");
			return false;
		}
		if (target.isMonster()) {
			mob.tell("Your chant would have no effect on such a creature.");
			return false;
		}

		boolean success = proficiencyCheck(mob, 0, auto);
		Set<MOB> groupMembers = mob.getGroupMembers(new HashSet<MOB>());
		if (success && (!auto) && (mob != target) && (!mob.mayIFight(target))
				&& (!groupMembers.contains(target))) {
			mob.tell(target.name(mob)
					+ " is a player, so you must be group members, and your playerkill flags must be on for this to work.");
			success = false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		if (success) {
			int modifier = 0;
			if ((target != mob) && (!groupMembers.contains(target)))
				modifier = CMMsg.MASK_MALICIOUS;
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							modifier | verbalCastCode(mob, target, auto),
							(auto ? "^S<S-NAME> get(s) put under a reincarnation geas!^?"
									: "^S<S-NAME> chant(s) a reincarnation geas upon <T-NAMESELF>.^?"));
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 1800);
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) for a reincarnation geas, but nothing happens.");

		return success;
	}
}
