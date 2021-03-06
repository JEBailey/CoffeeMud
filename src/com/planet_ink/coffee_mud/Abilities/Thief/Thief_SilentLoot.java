package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
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
public class Thief_SilentLoot extends ThiefSkill {
	public String ID() {
		return "Thief_SilentLoot";
	}

	public String displayText() {
		return "(Silent AutoLoot)";
	}

	public String name() {
		return "Silent AutoLoot";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	private static final String[] triggerStrings = { "SILENTLOOT" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if ((affected != null) && (affected instanceof MOB)) {
			if ((msg.sourceMinor() == CMMsg.TYP_DEATH)
					&& (msg.source() != affected)
					&& (CMLib.flags().canBeSeenBy(msg.source(), (MOB) affected))
					&& (msg.source().location() == ((MOB) affected).location())
					&& ((msg.source().numItems()) > 0)) {
				int max = 1 + getXLEVELLevel((MOB) affected);
				Item item = msg.source().fetchItem(null,
						Wearable.FILTER_UNWORNONLY, "all");
				if (item == null)
					item = msg.source().fetchItem(null,
							Wearable.FILTER_WORNONLY, "all");
				while (((--max) >= 0) && (item != null)
						&& (msg.source().isMine(item))) {
					item.unWear();
					item.removeFromOwnerContainer();
					item.setContainer(null);
					MOB mob = (MOB) affected;
					mob.location().addItem(item,
							ItemPossessor.Expire.Monster_EQ);
					MOB victim = mob.getVictim();
					mob.setVictim(null);
					CMMsg msg2 = CMClass.getMsg(mob, item, this,
							CMMsg.MSG_THIEF_ACT,
							"You silently autoloot <T-NAME> from the corpse of "
									+ msg.source().name(mob),
							CMMsg.MSG_THIEF_ACT, null, CMMsg.NO_EFFECT, null);
					if (mob.location().okMessage(mob, msg2)) {
						mob.location().send(mob, msg2);
						CMLib.commands().postGet(mob, null, item, true);
					}
					if (victim != null)
						mob.setVictim(victim);
					item = msg.source().fetchItem(null,
							Wearable.FILTER_UNWORNONLY, "all");
					if (item == null)
						item = msg.source().fetchItem(null,
								Wearable.FILTER_WORNONLY, "all");
				}
			}
		}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((mob.fetchEffect(ID()) != null)) {
			mob.tell("You are no longer automatically looting items from corpses silently.");
			mob.delEffect(mob.fetchEffect(ID()));
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			mob.tell("You will now automatically loot items from corpses silently.");
			beneficialAffect(mob, mob, asLevel, 0);
			Ability A = mob.fetchEffect(ID());
			if (A != null)
				A.makeLongLasting();
		} else
			beneficialVisualFizzle(
					mob,
					null,
					"<S-NAME> attempt(s) to start silently looting items from corpses, but fail(s).");
		return success;
	}

}
