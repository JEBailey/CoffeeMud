package com.planet_ink.coffee_mud.Abilities.Thief;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.Trap;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.collections.XVector;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Thief_DisassembleTrap extends ThiefSkill {
	public String ID() {
		return "Thief_DisassembleTrap";
	}

	public String name() {
		return "Disassemble Traps";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_ITEMS | Ability.CAN_EXITS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "DISTRAP",
			"DISASSEMBLETRAPS" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public Environmental lastChecked = null;

	public int classificationCode() {
		return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_DETRAP;
	}

	public int usageType() {
		return USAGE_MOVEMENT | USAGE_MANA;
	}

	public Vector lastDone = new Vector();

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Ability A = mob.fetchAbility("Thief_RemoveTraps");
		Hashtable traps = new Hashtable();
		if (A == null) {
			mob.tell("You don't know how to remove traps.");
			return false;
		}

		Vector cmds = new XVector(commands);
		cmds.addElement(new Boolean(true));
		CharState oldState = (CharState) mob.curState().copyOf();
		boolean worked = A.invoke(mob, cmds, givenTarget, auto, asLevel);
		oldState.copyInto(mob.curState());
		if (!worked)
			return false;
		for (int c = 0; c < cmds.size(); c++)
			if (cmds.elementAt(c) instanceof Trap) {
				Trap T = (Trap) cmds.elementAt(c);
				if (!traps.containsKey(T.ID()))
					traps.put(T.ID(), T);
			}
		if (traps.size() == 0) {
			mob.tell("Your attempt was unsuccessful.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		Trap T = (Trap) traps.elements().nextElement();
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, T, this, auto ? CMMsg.MSG_OK_ACTION
					: CMMsg.MSG_DELICATE_HANDS_ACT,
					CMMsg.MSG_DELICATE_HANDS_ACT, CMMsg.MSG_OK_ACTION,
					auto ? T.name() + " begins to glow."
							: "<S-NAME> attempt(s) to safely dissassemble the "
									+ T.name() + " trap.");
			Room R = mob.location();
			if (R.okMessage(mob, msg)) {
				R.send(mob, msg);
				List<Item> components = T.getTrapComponents();
				if (components.size() == 0) {
					mob.tell("You don't end up with any usable components.");
				} else {
					for (int i = 0; i < components.size(); i++) {
						Item I = components.get(i);
						I.text();
						I.recoverPhyStats();
						R.addItem(I, ItemPossessor.Expire.Resource);
					}
					R.recoverRoomStats();
					for (int i = 0; i < components.size(); i++) {
						Item I = components.get(i);
						if (R.isContent(I))
							if (!CMLib.commands().postGet(mob, null, I, true))
								break;
					}
					R.recoverRoomStats();
				}
			}
		} else
			beneficialVisualFizzle(mob, T,
					"<S-NAME> attempt(s) to disassemble the <T-NAME> trap, but fail(s).");

		return success;
	}
}
