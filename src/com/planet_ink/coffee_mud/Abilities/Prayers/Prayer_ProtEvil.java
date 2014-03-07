package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class Prayer_ProtEvil extends Prayer {
	public String ID() {
		return "Prayer_ProtEvil";
	}

	public String name() {
		return "Protection Evil";
	}

	public String displayText() {
		return "(Protection from Evil)";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_HOLYPROTECTION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	public long flags() {
		return Ability.FLAG_HOLY;
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!(affected instanceof MOB))
			return false;
		if (invoker == null)
			return false;

		MOB mob = (MOB) affected;

		if ((!CMLib.flags().isReallyGood(mob)) && CMLib.flags().isEvil(mob)) {
			int damage = (int) Math.round(CMath.div(mob.phyStats().level()
					+ (2 * super.getXLEVELLevel(invoker())), 3.0));
			CMLib.combat().postDamage(
					invoker,
					mob,
					this,
					damage,
					CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS
							| CMMsg.TYP_JUSTICE, Weapon.TYPE_BURSTING,
					"<T-HIS-HER> protective aura <DAMAGE> <T-NAME>!");
		}
		return super.tick(ticking, tickID);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		if (affected == null)
			return true;
		if (!(affected instanceof MOB))
			return true;

		if ((msg.target() == affected) && (msg.source() != affected)) {
			if ((CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
					&& (msg.targetMinor() == CMMsg.TYP_CAST_SPELL)
					&& (msg.tool() != null)
					&& (msg.tool() instanceof Ability)
					&& (!CMath.bset(((Ability) msg.tool()).flags(),
							Ability.FLAG_HOLY))
					&& (CMath.bset(((Ability) msg.tool()).flags(),
							Ability.FLAG_UNHOLY))) {
				msg.source()
						.location()
						.show((MOB) affected,
								null,
								CMMsg.MSG_OK_VISUAL,
								"The holy field around <S-NAME> protect(s) <S-HIM-HER> from the evil magic attack of "
										+ msg.source().name() + ".");
				return false;
			}

		}
		return true;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected == null)
			return;
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		if (mob.isInCombat()) {
			MOB victim = mob.getVictim();
			if (CMLib.flags().isEvil(victim))
				affectableStats.setArmor(affectableStats.armor() - 10
						- (2 * super.getXLEVELLevel(invoker())));
		}
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("Your protection from evil fades.");
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			MOB victim = mob.getVictim();
			if ((victim != null) && CMLib.flags().isEvil(victim)
					&& (!CMLib.flags().isEvil(mob)))
				return super.castingQuality(mob, target,
						Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = mob;
		if ((auto) && (givenTarget != null))
			target = givenTarget;
		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(mob, target, null,
					"<T-NAME> <T-IS-ARE> already affected by " + name() + ".");
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
					verbalCastCode(mob, target, auto),
					auto ? "<T-NAME> become(s) protected from evil."
							: "^S<S-NAME> " + prayWord(mob)
									+ " for protection from evil.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialWordsFizzle(mob, null, "<S-NAME> " + prayWord(mob)
					+ " for protection, but there is no answer.");

		// return whether it worked
		return success;
	}
}