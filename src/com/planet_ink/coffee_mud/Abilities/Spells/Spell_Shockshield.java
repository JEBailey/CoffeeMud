package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Spell_Shockshield extends Spell {
	public String ID() {
		return "Spell_Shockshield";
	}

	public String name() {
		return "Shockshield";
	}

	public String displayText() {
		return "(Shockshield)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
	}

	public long flags() {
		return Ability.FLAG_AIRBASED;
	}

	final static String msgStr = "The shock shield around <S-NAME> sparks and <DAMAGES> <T-NAME>!";
	protected long oncePerTickTime = 0;

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked())
			if ((mob.location() != null) && (!mob.amDead()))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-YOUPOSS> electric shield fizzles out.");
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if (affected == null)
			return;
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		if (msg.target() == null)
			return;
		if (msg.source() == null)
			return;
		MOB source = msg.source();
		if (source.location() == null)
			return;

		if (msg.amITarget(mob) && (!msg.amISource(mob))) {
			if ((CMath.bset(msg.targetMajor(), CMMsg.MASK_HANDS) || (msg
					.targetMajor(CMMsg.MASK_MOVE)))
					&& (msg.source().rangeToTarget() == 0)
					&& (oncePerTickTime != mob.lastTickedDateTime())) {
				if ((CMLib.dice().rollPercentage() > (source.charStats()
						.getStat(CharStats.STAT_DEXTERITY) * 3))) {
					CMMsg msg2 = CMClass.getMsg(mob, source, this,
							verbalCastCode(mob, source, true), null);
					if (source.location().okMessage(source, msg2)) {
						source.location().send(mob, msg2);
						if (invoker == null)
							invoker = source;
						if (msg2.value() <= 0) {
							int damage = CMLib
									.dice()
									.roll(1,
											(int) Math.round((invoker
													.phyStats().level()
													+ super.getXLEVELLevel(invoker()) + (2.0 * super
													.getX1Level(invoker()))) / 3.0),
											1);
							CMLib.combat().postDamage(
									mob,
									source,
									this,
									damage,
									CMMsg.MASK_ALWAYS | CMMsg.MASK_MALICIOUS
											| CMMsg.TYP_ELECTRIC,
									Weapon.TYPE_STRIKING, msgStr);
						}
					}
					oncePerTickTime = mob.lastTickedDateTime();
				}
			}
		}
		return;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected == null)
			return;
		if (!(affected instanceof MOB))
			return;
		affectableStats.setArmor(affectableStats.armor()
				- (getXLEVELLevel(invoker())));
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							((auto ? ""
									: "^S<S-NAME> incant(s) and wave(s) <S-HIS-HER> arms.  ") + "A field of sparks erupt(s) around <T-NAME>!^?")
									+ CMLib.protocol().msp("lightning.wav", 10));
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> incant(s) and wave(s) <S-HIS-HER> arms, but only sparks emerge.");

		// return whether it worked
		return success;
	}
}
