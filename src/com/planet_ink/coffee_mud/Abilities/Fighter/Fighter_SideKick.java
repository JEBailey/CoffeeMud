package com.planet_ink.coffee_mud.Abilities.Fighter;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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

public class Fighter_SideKick extends MonkSkill {
	public String ID() {
		return "Fighter_SideKick";
	}

	public String name() {
		return "Side Kick";
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
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_KICKING;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public boolean canBeUninvoked() {
		return false;
	}

	protected Weapon naturalWeapon = null;

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((tickID == Tickable.TICKID_MOB) && (affected != null)
				&& (affected instanceof MOB)) {
			MOB mob = (MOB) affected;
			if ((mob.isInCombat())
					&& (CMLib.flags().aliveAwakeMobile(mob, true))
					&& (mob.rangeToTarget() == 0)
					&& (mob.charStats().getBodyPart(Race.BODY_LEG) > 1)
					&& (mob.location() != null) && (!anyWeapons(mob))) {
				if (CMLib.dice().rollPercentage() > 95)
					helpProficiency(mob, 0);
				MOB elligibleTarget = null;
				for (int m = 0; m < mob.location().numInhabitants(); m++) {
					MOB M = mob.location().fetchInhabitant(m);
					if ((M != null) && (M.getVictim() == mob)
							&& (mob.getVictim() != M)
							&& (M.rangeToTarget() == 0)) {
						elligibleTarget = M;
						break;
					}
				}
				if (elligibleTarget != null) {
					if ((naturalWeapon == null)
							|| (naturalWeapon.amDestroyed())) {
						naturalWeapon = CMClass.getWeapon("GenWeapon");
						naturalWeapon.setName("a side kick");
						naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
						naturalWeapon.setUsesRemaining(1000);
						naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
						naturalWeapon.basePhyStats().setDamage(5);
						naturalWeapon.recoverPhyStats();
					}
					naturalWeapon.setUsesRemaining(100);
					final MOB prevVictim = mob.getVictim();
					CMLib.combat().postAttack(mob, elligibleTarget,
							naturalWeapon);
					if ((prevVictim != mob.getVictim())
							&& (elligibleTarget == mob.getVictim())
							&& ((prevVictim == null) || (!prevVictim.amDead())))
						mob.setVictim(prevVictim);
				}
			}
		}
		return true;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;

		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if (msg.amISource(mob) && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.tool() instanceof Weapon)
				&& (msg.tool() == naturalWeapon))
			msg.setValue(msg.value() + naturalWeapon.basePhyStats().damage()
					+ super.getXLEVELLevel(mob));
		return true;
	}

}