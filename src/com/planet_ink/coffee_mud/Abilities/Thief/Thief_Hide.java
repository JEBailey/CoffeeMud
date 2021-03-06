package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Thief_Hide extends ThiefSkill {
	public String ID() {
		return "Thief_Hide";
	}

	public String name() {
		return "Hide";
	}

	public String displayText() {
		return "";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALTHY;
	}

	private static final String[] triggerStrings = { "HIDE" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int usageType() {
		return USAGE_MOVEMENT | USAGE_MANA;
	}

	public int code = 0;
	private int bonus = 0;

	public int abilityCode() {
		return code;
	}

	public void setAbilityCode(int newCode) {
		code = newCode;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return;

		MOB mob = (MOB) affected;

		if (msg.amISource(mob)) {
			if (((msg.sourceMinor() == CMMsg.TYP_ENTER)
					|| (msg.sourceMinor() == CMMsg.TYP_LEAVE)
					|| (msg.sourceMinor() == CMMsg.TYP_FLEE) || (msg
					.sourceMinor() == CMMsg.TYP_RECALL))
					&& (!msg.sourceMajor(CMMsg.MASK_ALWAYS))
					&& (msg.sourceMajor() > 0)) {
				unInvoke();
				mob.recoverPhyStats();
			} else if ((abilityCode() == 0)
					&& (!msg.sourceMajor(CMMsg.MASK_ALWAYS))
					&& (msg.othersMinor() != CMMsg.TYP_LOOK)
					&& (msg.othersMinor() != CMMsg.TYP_EXAMINE)
					&& (msg.othersMajor() > 0)) {
				if (msg.othersMajor(CMMsg.MASK_SOUND)) {
					unInvoke();
					mob.recoverPhyStats();
				} else
					switch (msg.othersMinor()) {
					case CMMsg.TYP_SPEAK:
					case CMMsg.TYP_CAST_SPELL: {
						unInvoke();
						mob.recoverPhyStats();
					}
						break;
					case CMMsg.TYP_OPEN:
					case CMMsg.TYP_CLOSE:
					case CMMsg.TYP_LOCK:
					case CMMsg.TYP_UNLOCK:
					case CMMsg.TYP_PUSH:
					case CMMsg.TYP_PULL:
						if ((msg.target() != null)
								&& ((msg.target() instanceof Exit) || ((msg
										.target() instanceof Item) && (!msg
										.source().isMine(msg.target()))))) {
							unInvoke();
							mob.recoverPhyStats();
						}
						break;
					}
			}
		}
		return;
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);
		affectableStats.setStat(
				CharStats.STAT_SAVE_DETECTION,
				proficiency()
						+ bonus
						+ affectableStats
								.getStat(CharStats.STAT_SAVE_DETECTION));
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_HIDDEN);
		if (CMLib.flags().isSneaking(affected))
			affectableStats.setDisposition(affectableStats.disposition()
					- PhyStats.IS_SNEAKING);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		final Room room = mob.location();
		if ((mob.fetchEffect(this.ID()) != null) || (room == null)) {
			mob.tell("You are already hiding.");
			return false;
		}

		if (mob.isInCombat()) {
			mob.tell("Not while in combat!");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		MOB highestMOB = getHighestLevelMOB(mob, null);
		int levelDiff = (mob.phyStats().level() + (2 * super
				.getXLEVELLevel(mob))) - getMOBLevel(highestMOB);

		String str = "You creep into a shadow and remain completely still.";

		boolean success = (highestMOB == null)
				|| proficiencyCheck(mob, levelDiff * 10, auto);

		if (!success) {
			if (highestMOB != null)
				beneficialVisualFizzle(mob, highestMOB,
						"<S-NAME> attempt(s) to hide from <T-NAMESELF> and fail(s).");
			else
				beneficialVisualFizzle(mob, null,
						"<S-NAME> attempt(s) to hide and fail(s).");
		} else {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					auto ? CMMsg.MSG_OK_ACTION
							: (CMMsg.MSG_DELICATE_HANDS_ACT | CMMsg.MASK_MOVE),
					str, CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null);
			if (room.okMessage(mob, msg)) {
				room.send(mob, msg);
				invoker = mob;
				Ability newOne = (Ability) this.copyOf();
				((Thief_Hide) newOne).bonus = getXLEVELLevel(mob) * 2;
				if (mob.fetchEffect(newOne.ID()) == null)
					mob.addEffect(newOne);
				mob.recoverPhyStats();
			} else
				success = false;
		}
		return success;
	}
}
