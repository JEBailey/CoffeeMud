package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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

@SuppressWarnings("rawtypes")
public class Lacquerring extends CommonSkill {
	public String ID() {
		return "Lacquerring";
	}

	public String name() {
		return "Lacquering";
	}

	private static final String[] triggerStrings = { "LACQUERING", "LACQUER" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_ARTISTIC;
	}

	protected Item found = null;
	protected String writing = "";

	public Lacquerring() {
		super();
		displayText = "You are lacquering...";
		verb = "lacquering";
	}

	protected String fixColor(String name, String colorWord) {
		int end = name.indexOf("^?");
		if ((end > 0) && (end <= name.length() - 3)) {
			int start = name.substring(0, end).indexOf('^');
			if ((start >= 0) && (start < (end - 3)))
				name = name.substring(0, start) + name.substring(end + 3);
		}
		colorWord = "^" + colorWord.charAt(0) + colorWord + "^?";
		Vector<String> V = CMParms.parse(name);
		for (int v = 0; v < V.size(); v++) {
			String word = V.elementAt(v);
			if ((word.equalsIgnoreCase("an")) || (word.equalsIgnoreCase("a"))) {
				String properPrefix = CMLib.english().properIndefiniteArticle(
						colorWord);
				V.insertElementAt(colorWord, v + 1);
				if (word.toLowerCase().equals(word))
					V.set(v, properPrefix.toLowerCase());
				else
					V.set(v, CMStrings.capitalizeAndLower(properPrefix));
				return CMParms.combine(V, 0);
			} else if ((word.equalsIgnoreCase("of"))
					|| (word.equalsIgnoreCase("some"))
					|| (word.equalsIgnoreCase("the"))) {
				V.insertElementAt(colorWord, v + 1);
				return CMParms.combine(V, 0);
			}
		}
		V.insertElementAt(colorWord, 0);
		return CMParms.combine(V, 0);
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB) && (!aborted)
					&& (!helping)) {
				MOB mob = (MOB) affected;
				if (writing.length() == 0)
					commonEmote(mob, "<S-NAME> mess(es) up the lacquering.");
				else {
					StringBuffer desc = new StringBuffer(found.description());
					for (int x = 0; x < (desc.length() - 1); x++) {
						if ((desc.charAt(x) == '^')
								&& (desc.charAt(x + 1) != '?')
								&& (desc.charAt(x + 1) != '_')
								&& (desc.charAt(x + 1) != '#'))
							desc.setCharAt(x + 1, writing.charAt(0));
					}
					String d = desc.toString();
					if (!d.endsWith("^?"))
						desc.append("^?");
					if (!d.startsWith("^" + writing.charAt(0)))
						desc.insert(0, "^" + writing.charAt(0));
					found.setDescription(desc.toString());
					found.setName(fixColor(found.Name(), writing));
					found.setDisplayText(fixColor(found.displayText(), writing));
					found.text();
				}
			}
		}
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (super.checkStop(mob, commands))
			return true;
		if (commands.size() < 2) {
			commonTell(mob,
					"You must specify what you want to lacquer, and the color to lacquer it in.");
			return false;
		}
		Item target = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY,
				(String) commands.firstElement());
		if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
			target = mob.location().findItem(null,
					(String) commands.firstElement());
			if ((target != null) && (CMLib.flags().canBeSeenBy(target, mob))) {
				Set<MOB> followers = mob.getGroupMembers(new TreeSet<MOB>());
				boolean ok = false;
				for (MOB M : followers) {
					if (target.secretIdentity().indexOf(getBrand(M)) >= 0)
						ok = true;
				}
				if (!ok) {
					commonTell(mob, "You aren't allowed to work on '"
							+ ((String) commands.firstElement()) + "'.");
					return false;
				}
			}
		}
		if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
			commonTell(
					mob,
					"You don't seem to have a '"
							+ ((String) commands.firstElement()) + "'.");
			return false;
		}
		commands.remove(commands.firstElement());

		if ((((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_GLASS)
				&& ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_METAL)
				&& ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_MITHRIL)
				&& ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_SYNTHETIC)
				&& ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_PRECIOUS)
				&& ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_ROCK) && ((target
				.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN))
				|| (!target.isGeneric())) {
			commonTell(mob, "You can't lacquer that material.");
			return false;
		}

		writing = CMParms.combine(commands, 0).toLowerCase();
		boolean darkFlag = false;
		if (writing.startsWith("dark ")) {
			darkFlag = true;
			writing = writing.substring(5).trim();
		}
		if ((" white green blue red yellow cyan purple ".indexOf(" "
				+ writing.trim() + " ") < 0)
				|| (writing.trim().indexOf(' ') >= 0)) {
			commonTell(
					mob,
					"You can't lacquer anything '"
							+ writing
							+ "'.  Try white, green, blue, red, yellow, cyan, or purple. You can also prefix the colors with the word 'dark'.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		verb = "lacquering " + target.name() + " " + writing;
		displayText = "You are " + verb;
		found = target;
		if (darkFlag)
			writing = CMStrings.capitalizeAndLower(writing);
		if (!proficiencyCheck(mob, 0, auto))
			writing = "";
		int duration = getDuration(60, mob, 1, 12);
		CMMsg msg = CMClass.getMsg(mob, target, this, getActivityMessageType(),
				"<S-NAME> start(s) lacquering <T-NAME>.");
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			beneficialAffect(mob, mob, asLevel, duration);
		}
		return true;
	}
}
