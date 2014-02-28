package com.planet_ink.coffee_mud.Abilities.Songs;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Behaviors.interfaces.LegalBehavior;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.LegalWarrant;
import com.planet_ink.coffee_mud.Libraries.interfaces.ListingLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
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
public class Skill_Warrants extends BardSkill {
	public String ID() {
		return "Skill_Warrants";
	}

	public String name() {
		return "Warrants";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "WARRANTS" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_LEGAL;
	}

	protected boolean disregardsArmorCheck(MOB mob) {
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		LegalBehavior B = null;
		if (mob.location() != null)
			B = CMLib.law().getLegalBehavior(mob.location());

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(
				mob,
				(-25 + mob.charStats().getStat(CharStats.STAT_CHARISMA) + (2 * getXLEVELLevel(mob))),
				auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					CMMsg.MSG_DELICATE_SMALL_HANDS_ACT
							| (auto ? CMMsg.MASK_ALWAYS : 0), null);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				List<LegalWarrant> V = new Vector();
				if (B != null)
					V = B.getWarrantsOf(
							CMLib.law().getLegalObject(mob.location()),
							(MOB) null);
				if (V.size() == 0) {
					mob.tell("No one is wanted for anything here.");
					return false;
				}
				StringBuffer buf = new StringBuffer("");
				int colWidth = ListingLibrary.ColFixer.fixColWidth(14,
						mob.session());
				buf.append(CMStrings.padRight("Name", colWidth) + " "
						+ CMStrings.padRight("Victim", colWidth) + " "
						+ CMStrings.padRight("Witness", colWidth)
						+ " Crime\n\r");
				for (int v = 0; v < V.size(); v++) {
					LegalWarrant W = V.get(v);
					buf.append(CMStrings
							.padRight(W.criminal().Name(), colWidth) + " ");
					buf.append(CMStrings.padRight(W.victim() != null ? W
							.victim().Name() : "N/A", colWidth)
							+ " ");
					buf.append(CMStrings.padRight(W.witness() != null ? W
							.witness().Name() : "N/A", colWidth)
							+ " ");
					buf.append(CMLib.coffeeFilter().fullOutFilter(
							mob.session(), mob, W.criminal(), W.victim(), null,
							W.crime(), false)
							+ "\n\r");
				}
				if (!mob.isMonster())
					mob.session().rawPrintln(buf.toString());
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> attempt(s) to gather warrant information, but fail(s).");

		return success;
	}

}
