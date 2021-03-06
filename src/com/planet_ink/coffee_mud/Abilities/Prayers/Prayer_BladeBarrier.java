package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class Prayer_BladeBarrier extends Prayer {
	public String ID() {
		return "Prayer_BladeBarrier";
	}

	public String name() {
		return "Blade Barrier";
	}

	public String displayText() {
		return "(Blade Barrier)";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CREATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public long flags() {
		return Ability.FLAG_HOLY;
	}

	protected long oncePerTickTime = 0;

	protected String startStr() {
		return "A barrier of blades begin to spin around <T-NAME>!^?";
	}

	protected void doDamage(MOB srcM, MOB targetM, int damage) {
		CMLib.combat()
				.postDamage(
						srcM,
						targetM,
						this,
						damage,
						CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS
								| CMMsg.TYP_CAST_SPELL, Weapon.TYPE_SLASHING,
						"The blade barrier around <S-NAME> slices and <DAMAGE> <T-NAME>.");
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
						"<S-YOUPOSS> " + name().toLowerCase() + " disappears.");
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if (!(affected instanceof MOB))
			return;
		if ((msg.target() == affected) && (!msg.amISource((MOB) affected))) {
			if ((CMLib.dice().rollPercentage() > 60 + msg.source().charStats()
					.getStat(CharStats.STAT_DEXTERITY))
					&& (msg.source().rangeToTarget() == 0)
					&& (oncePerTickTime != ((MOB) affected)
							.lastTickedDateTime())
					&& ((msg.targetMajor(CMMsg.MASK_HANDS)) || (msg
							.targetMajor(CMMsg.MASK_MOVE)))) {
				MOB meM = (MOB) msg.target();
				int damage = CMLib.dice().roll(1,
						(int) Math.round(adjustedLevel(meM, 0) / 5.0), 1);
				StringBuffer hitWord = new StringBuffer(CMLib.combat()
						.standardHitWord(-1, damage));
				if (hitWord.charAt(hitWord.length() - 1) == ')')
					hitWord.deleteCharAt(hitWord.length() - 1);
				if (hitWord.charAt(hitWord.length() - 2) == '(')
					hitWord.deleteCharAt(hitWord.length() - 2);
				if (hitWord.charAt(hitWord.length() - 3) == '(')
					hitWord.deleteCharAt(hitWord.length() - 3);
				this.doDamage(meM, msg.source(), damage);
				oncePerTickTime = ((MOB) msg.target()).lastTickedDateTime();
			}
		}
		return;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setArmor(affectableStats.armor() - 1
				- (adjustedLevel(invoker(), 0) / 10));
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;

		if (target.fetchEffect(ID()) != null) {
			mob.tell(target, null, null, "<S-NAME> already <S-HAS-HAVE> "
					+ name().toLowerCase() + ".");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), (auto ? ""
							: "^S<S-NAME> " + prayWord(mob)
									+ " for divine protection!  ")
							+ startStr());
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialWordsFizzle(mob, target, "<S-NAME> "
					+ prayWord(mob)
					+ " for divine protection, but nothing happens.");

		// return whether it worked
		return success;
	}
}
