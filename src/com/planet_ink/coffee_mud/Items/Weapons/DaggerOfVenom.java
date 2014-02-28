package com.planet_ink.coffee_mud.Items.Weapons;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class DaggerOfVenom extends Dagger {
	public String ID() {
		return "DaggerOfVenom";
	}

	public DaggerOfVenom() {
		super();

		setName("a small dagger");
		setDisplayText("a sharp little dagger lies here.");
		setDescription("It has a wooden handle and a metal blade.");
		secretIdentity = "A Dagger of Venom (Periodically injects poison on a successful hit.)";
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats.setWeight(1);
		baseGoldValue = 1500;
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(4);
		material = RawMaterial.RESOURCE_STEEL;
		basePhyStats().setDisposition(
				basePhyStats().disposition() | PhyStats.IS_BONUS);
		weaponType = Weapon.TYPE_PIERCING;
		weaponClassification = Weapon.CLASS_DAGGER;
		recoverPhyStats();
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if ((msg.source().location() != null)
				&& (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& ((msg.value()) > 0) && (msg.tool() == this)
				&& (msg.target() instanceof MOB)) {
			int chance = (int) Math.round(Math.random() * 20.0);
			if (chance == 10) {
				Ability poison = CMClass.getAbility("Poison");
				if (poison != null)
					poison.invoke(msg.source(), (MOB) msg.target(), true,
							phyStats().level());
			}
		}
	}

}
