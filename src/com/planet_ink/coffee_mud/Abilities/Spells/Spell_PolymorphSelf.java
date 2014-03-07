package com.planet_ink.coffee_mud.Abilities.Spells;

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
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.XVector;
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
public class Spell_PolymorphSelf extends Spell {
	public String ID() {
		return "Spell_PolymorphSelf";
	}

	public String name() {
		return "Polymorph Self";
	}

	public String displayText() {
		return "(Polymorph Self)";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_TRANSMUTATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
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
		if (newRace != null) {
			int oldCat = affected.baseCharStats().ageCategory();
			affectableStats.setMyRace(newRace);
			if (affected.baseCharStats().getStat(CharStats.STAT_AGE) > 0)
				affectableStats.setStat(CharStats.STAT_AGE,
						newRace.getAgingChart()[oldCat]);
		}
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		super.unInvoke();
		if (canBeUninvoked())
			if ((mob.location() != null) && (!mob.amDead()))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> morph(s) back into <S-HIM-HERSELF> again.");
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((auto || mob.isMonster())
				&& ((commands.size() < 1) || (((String) commands.firstElement())
						.equals(mob.name())))) {
			commands.clear();
			XVector<Race> V = new XVector<Race>(CMClass.races());
			for (int v = V.size() - 1; v >= 0; v--)
				if (!CMath.bset(V.elementAt(v).availabilityCode(),
						Area.THEME_FANTASY))
					V.removeElementAt(v);
			if (V.size() > 0)
				commands.addElement(V.elementAt(
						CMLib.dice().roll(1, V.size(), -1)).name());
		}
		if (commands.size() == 0) {
			mob.tell("You need to specify what to turn yourself into!");
			return false;
		}
		String race = CMParms.combine(commands, 0);
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		Race R = CMClass.getRace(race);
		if ((R == null)
				|| (!CMath.bset(R.availabilityCode(), Area.THEME_FANTASY))) {
			mob.tell("You can't turn yourself into "
					+ CMLib.english().startWithAorAn(race) + "!");
			return false;
		}
		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> <S-IS-ARE> already polymorphed.");
			return false;
		}

		if (target.baseCharStats().getMyRace() != target.charStats()
				.getMyRace()) {
			mob.tell(target, null, null,
					"<S-NAME> <S-IS-ARE> already polymorphed.");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		int mobStatTotal = 0;
		for (int s : CharStats.CODES.BASE())
			mobStatTotal += mob.baseCharStats().getStat(s);

		MOB fakeMOB = CMClass.getFactoryMOB();
		for (int s : CharStats.CODES.BASE())
			fakeMOB.baseCharStats().setStat(s, mob.baseCharStats().getStat(s));
		fakeMOB.baseCharStats().setMyRace(R);
		fakeMOB.recoverCharStats();
		fakeMOB.recoverPhyStats();
		fakeMOB.recoverMaxState();
		int fakeStatTotal = 0;
		for (int s : CharStats.CODES.BASE())
			fakeStatTotal += fakeMOB.charStats().getStat(s);

		fakeMOB.destroy();
		int statDiff = mobStatTotal - fakeStatTotal;
		boolean success = proficiencyCheck(mob, (statDiff * 5), auto);
		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			invoker = mob;
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> whisper(s) to <T-NAMESELF> about "
									+ CMLib.english().makePlural(R.name())
									+ ".^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					newRace = R;
					mob.location().show(
							target,
							null,
							CMMsg.MSG_OK_VISUAL,
							"<S-NAME> become(s) a "
									+ CMLib.english().startWithAorAn(
											newRace.name()) + "!");
					success = beneficialAffect(mob, target, asLevel, 0);
					target.recoverCharStats();
					CMLib.utensils().confirmWearability(target);
				}
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> whisper(s) to <T-NAMESELF>, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}