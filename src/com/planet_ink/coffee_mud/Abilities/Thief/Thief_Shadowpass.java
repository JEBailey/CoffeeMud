package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Thief_Shadowpass extends ThiefSkill {
	public String ID() {
		return "Thief_Shadowpass";
	}

	public String name() {
		return "Shadowpass";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "SHADOWPASS" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	public long flags() {
		return Ability.FLAG_TRANSPORTING | super.flags();
	}

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALTHY;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Room R = mob.location();
		if ((!auto) && (!CMLib.flags().isInDark(R)) || (R == null)) {
			mob.tell("You can only shadowpass from the shadows to the shadows.");
			return false;
		}
		List<Integer> trail = new Vector<Integer>();
		int v = 0;
		for (; v < commands.size(); v++) {
			int num = 1;
			String s = (String) commands.elementAt(v);
			if (CMath.s_int(s) > 0) {
				num = CMath.s_int(s);
				v++;
				if (v < commands.size())
					s = (String) commands.elementAt(v);
			} else if (CMath.isNumberFollowedByString(s)) {
				Entry<Integer, String> pair = CMath
						.getNumberFollowedByString(s);
				num = pair.getKey().intValue();
				s = pair.getValue();
			}

			int direction = Directions.getGoodDirectionCode(s);
			if (direction < 0)
				break;
			if ((R.getRoomInDir(direction) == null)
					|| (R.getExitInDir(direction) == null))
				break;
			for (int i = 0; i < num; i++) {
				R = R.getRoomInDir(direction);
				if (!CMLib.flags().canAccess(mob, R))
					break;
				trail.add(Integer.valueOf(direction));
			}
		}
		boolean kaplah = ((v == commands.size()) && (R != null)
				&& (mob.location() != R) && (CMLib.flags().isInDark(R)));

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success && (mob.location() != R)) {
			CMMsg msg = CMClass.getMsg(mob, R, this, auto ? CMMsg.MSG_OK_VISUAL
					: CMMsg.MSG_DELICATE_HANDS_ACT,
					"You begin the shadowpass ...", CMMsg.NO_EFFECT, null,
					CMMsg.NO_EFFECT, null);
			if ((mob.location().okMessage(mob, msg)) && (R != null)
					&& (R.okMessage(mob, msg))) {
				mob.location().send(mob, msg);
				msg.setSourceMessage(null);
				R.send(mob, msg);
				R = mob.location();
				for (int i = 0; i < trail.size(); i++) {
					int dir = trail.get(i).intValue();
					if (!kaplah) {
						if ((!CMLib.tracking()
								.walk(mob, dir, false, true, true))
								|| (!CMLib.flags().isInDark(mob.location()))) {
							CMLib.commands().postLook(mob, true);
							return beneficialVisualFizzle(mob, null,
									"<S-NAME> do(es) not know <S-HIS-HER> way through shadowpass.");
						}
						mob.curState().expendEnergy(mob, mob.maxState(), true);
					} else {
						R = R.getRoomInDir(dir);
						R.bringMobHere(mob, false);
					}
					mob.curState().expendEnergy(mob, mob.maxState(), true);
				}
				CMLib.commands().postLook(mob, true);
			}
		} else
			for (int i = 0; i < trail.size(); i++) {
				int dir = trail.get(i).intValue();
				if ((!CMLib.tracking().walk(mob, dir, false, true, true))
						|| (!CMLib.flags().isInDark(mob.location()))) {
					CMLib.commands().postLook(mob, true);
					return beneficialVisualFizzle(mob, null,
							"<S-NAME> lose(s) <S-HIS-HER> way through shadowpass.");
				}
				mob.curState().expendEnergy(mob, mob.maxState(), true);
				mob.curState().expendEnergy(mob, mob.maxState(), true);
			}
		return success;
	}

}
