package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Climate;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Spell_WeaknessElectricity extends Spell {
	public String ID() {
		return "Spell_WeaknessElectricity";
	}

	public String name() {
		return "Weakness to Electricity";
	}

	public String displayText() {
		return "(Weakness/Electricity)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_TRANSMUTATION;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		if (canBeUninvoked())
			mob.tell("Your electric weakness is now gone.");

		super.unInvoke();

	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;

		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((msg.amITarget(mob)) && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.sourceMinor() == CMMsg.TYP_ELECTRIC)) {
			int recovery = (int) Math.round(CMath.mul((msg.value()), 1.5));
			msg.setValue(msg.value() + recovery);
		}
		return true;
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
		super.affectCharStats(affectedMOB, affectedStats);
		affectedStats.setStat(CharStats.STAT_SAVE_ELECTRIC,
				affectedStats.getStat(CharStats.STAT_SAVE_ELECTRIC) - 100);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (tickID != Tickable.TICKID_MOB)
			return false;
		if ((affecting() != null) && (affecting() instanceof MOB)) {
			MOB M = (MOB) affecting();
			Room room = M.location();
			if ((room != null)
					&& (room.getArea().getClimateObj().weatherType(room) == Climate.WEATHER_THUNDERSTORM)
					&& (CMLib.dice().rollPercentage() > M.charStats().getSave(
							CharStats.STAT_SAVE_ELECTRIC))) {
				int damage = CMLib.dice().roll(1, 3, 0);
				CMLib.combat().postDamage(
						invoker,
						M,
						null,
						damage,
						CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS
								| CMMsg.TYP_ELECTRIC, Weapon.TYPE_STRIKING,
						"The electricity in the air <DAMAGE> <T-NAME>!");
				if ((!M.isInCombat()) && (M.isMonster()) && (M != invoker)
						&& (invoker != null)
						&& (M.location() == invoker.location())
						&& (M.location().isInhabitant(invoker))
						&& (CMLib.flags().canBeSeenBy(invoker, M)))
					CMLib.combat().postAttack(M, invoker, M.fetchWieldedItem());
			}
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "A shimmering conductive field appears around <T-NAMESELF>."
									: "^S<S-NAME> invoke(s) a shimmering conductive field around <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0)
					success = maliciousAffect(mob, target, asLevel, 0, -1);
			}
		} else
			maliciousFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke weakness to electricity, but fail(s).");

		return success;
	}
}
