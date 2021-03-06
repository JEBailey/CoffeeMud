package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.Directions;
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
public class Prayer_Wave extends Prayer {
	public String ID() {
		return "Prayer_Wave";
	}

	public String name() {
		return "Wave";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CREATION;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
	}

	public String displayText() {
		return "(Waved)";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_EXITS;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Set<MOB> h = properTargets(mob, givenTarget, auto);
		if (h == null)
			return false;
		int dir = Directions.getGoodDirectionCode(CMParms.combine(commands, 0));
		if (dir < 0) {
			if (mob.isMonster()) {
				for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
					Room destRoom = mob.location().getRoomInDir(d);
					Exit exitRoom = mob.location().getExitInDir(d);
					if ((destRoom != null) || (exitRoom != null)
							|| (d != Directions.UP)) {
						dir = d;
						break;
					}
				}
				if (dir < 0)
					return false;
			} else {
				mob.tell("Wash your opponents which direction?");
				return false;
			}
		}
		Room destRoom = mob.location().getRoomInDir(dir);
		Exit exitRoom = mob.location().getExitInDir(dir);
		if ((destRoom == null) || (exitRoom == null) || (dir == Directions.UP)) {
			mob.tell("You can't wash your opponents that way!");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		int numEnemies = h.size();
		for (Iterator e = h.iterator(); e.hasNext();) {
			MOB target = (MOB) e.next();
			if (target != mob) {
				if (success) {
					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB. Then tell everyone else
					// what happened.
					final Room R = target.location();
					CMMsg msg = CMClass
							.getMsg(mob,
									target,
									this,
									verbalCastCode(mob, target, auto)
											| CMMsg.MASK_MALICIOUS,
									auto ? "<T-NAME> <T-IS-ARE> swept away by a great wave!"
											: "^S<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, "
													+ prayingWord(mob) + ".^?");
					CMMsg msg2 = CMClass.getMsg(mob, target, this,
							CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_WATER
									| (auto ? CMMsg.MASK_ALWAYS : 0), null);
					if ((R.okMessage(mob, msg)) && ((R.okMessage(mob, msg2)))) {
						R.send(mob, msg);
						R.send(mob, msg2);
						if ((msg.value() <= 0) && (msg2.value() <= 0)) {
							int harming = CMLib.dice().roll(1,
									adjustedLevel(mob, asLevel) / numEnemies,
									numEnemies);
							CMLib.combat().postDamage(mob, target, this,
									harming,
									CMMsg.MASK_ALWAYS | CMMsg.TYP_WATER,
									Weapon.TYPE_BURSTING,
									"A crashing wave <DAMAGE> <T-NAME>!");
							int chanceToStay = 10 + (target.charStats()
									.getStat(CharStats.STAT_STRENGTH) - (mob
									.phyStats().level() + (2 * super
									.getXLEVELLevel(mob))) * 4);
							int roll = CMLib.dice().rollPercentage();
							if ((roll != 1) && (roll > chanceToStay)) {
								CMLib.tracking().walk(target, dir, true, false);
								if ((!R.isInhabitant(target))
										&& (target.isMonster()))
									CMLib.tracking().markToWanderHomeLater(
											target);
							}
						}
					}
				} else
					maliciousFizzle(mob, target,
							"<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, "
									+ prayingWord(mob) + ", but "
									+ hisHerDiety(mob) + " does not heed.");
			}
		}
		return success;
	}
}
