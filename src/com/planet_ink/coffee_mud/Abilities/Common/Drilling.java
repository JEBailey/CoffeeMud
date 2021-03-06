package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
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
public class Drilling extends GatheringSkill {
	public String ID() {
		return "Drilling";
	}

	public String name() {
		return "Drilling";
	}

	private static final String[] triggerStrings = { "DRILL", "DRILLING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_GATHERINGSKILL;
	}

	protected boolean allowedWhileMounted() {
		return false;
	}

	public String supportedResourceString() {
		return "LIQUID";
	}

	protected Item found = null;
	private Drink container = null;
	protected String foundShortName = "";

	public Drilling() {
		super();
		displayText = "You are drilling...";
		verb = "drilling";
	}

	protected int getDuration(MOB mob, int level) {
		return getDuration(35, mob, level, 10);
	}

	protected int baseYield() {
		return 1;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected != null) && (affected instanceof MOB)
				&& (tickID == Tickable.TICKID_MOB)) {
			MOB mob = (MOB) affected;
			if (tickUp == 6) {
				if (found != null) {
					commonTell(mob, "You have found some " + foundShortName
							+ "!");
					displayText = "You are drilling out some " + foundShortName;
					verb = "drilling out some " + foundShortName;
					playSound = "drill.wav";
				} else {
					StringBuffer str = new StringBuffer(
							"You can't seem to find anything worth drilling around here.\n\r");
					int d = lookingFor(RawMaterial.MATERIAL_LIQUID,
							mob.location());
					if (d < 0)
						str.append("You might try elsewhere.");
					else
						str.append("You might try "
								+ Directions.getInDirectionName(d) + ".");
					commonTell(mob, str.toString());
					unInvoke();
				}

			}
		}
		return super.tick(ticking, tickID);
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB)) {
				MOB mob = (MOB) affected;
				if ((found != null) && (!aborted)) {
					int amount = ((found.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_CLOTH) ? (CMLib
							.dice().roll(1, 10, 0) * (abilityCode())) : (CMLib
							.dice().roll(1, 3, 0) * (abilityCode()));
					String s = "s";
					if (amount == 1)
						s = "";
					if (amount > (container.liquidHeld() - container
							.liquidRemaining()))
						amount = (container.liquidHeld() - container
								.liquidRemaining());
					if (amount > ((Container) container).capacity())
						amount = ((Container) container).capacity();
					mob.location().show(
							mob,
							null,
							getActivityMessageType(),
							"<S-NAME> manage(s) to drill out " + amount
									+ " pound" + s + " of " + foundShortName
									+ ".");
					for (int i = 0; i < amount; i++) {
						Item newFound = (Item) found.copyOf();
						Room R = mob.location();
						if (R == null)
							break;
						R.addItem(newFound, ItemPossessor.Expire.Player_Drop);
						if ((container != null)
								&& (container instanceof Container)) {
							if (mob.isMine(container)) {
								CMLib.commands().postGet(mob, null, newFound,
										true);
								if (mob.isMine(newFound))
									newFound.setContainer((Container) container);
							} else if (R.isContent((Item) container))
								newFound.setContainer((Container) container);
						}
					}
				}
			}
		}
		super.unInvoke();
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (super.checkStop(mob, commands))
			return true;
		bundling = false;
		if ((!auto)
				&& (commands.size() > 0)
				&& (((String) commands.firstElement())
						.equalsIgnoreCase("bundle"))) {
			bundling = true;
			if (super.invoke(mob, commands, givenTarget, auto, asLevel))
				return super.bundle(mob, commands);
			return false;
		}

		Item I = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_ANY);
		if (I == null)
			return false;
		if ((!(I instanceof Container))
				|| (((Container) I).capacity() <= ((Container) I).phyStats()
						.weight())) {
			commonTell(mob, I.name(mob)
					+ " doesn't look like it can hold anything.");
			return false;
		}
		int resourceType = mob.location().myResource();
		if ((!(I instanceof Drink))
				|| ((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_LIQUID)) {
			commonTell(mob, I.name(mob)
					+ " doesn't look like it can hold a liquid.");
			return false;
		}
		List<Item> V = ((Container) I).getContents();
		if (((Drink) I).containsDrink()) {
			for (int v = 0; v < V.size(); v++) {
				Item I2 = V.get(v);
				if ((I2.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_LIQUID) {
					if (I2.material() != resourceType) {
						commonTell(mob, I.name(mob) + " needs to have the "
								+ I2.name(mob) + " removed first.");
						return false;
					}
				}
			}
			if (((Drink) I).liquidRemaining() > 0) {
				commonTell(mob, "You need to empty " + I.name(mob) + " first.");
				return false;
			}
		}

		verb = "drilling";
		found = null;
		playSound = null;
		if (!confirmPossibleMaterialLocation(RawMaterial.MATERIAL_LIQUID,
				mob.location())) {
			commonTell(mob, "You don't think this is a good place to drill.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		if ((proficiencyCheck(mob, 0, auto))
				&& (((resourceType & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_LIQUID))) {
			found = (Item) CMLib.materials().makeResource(resourceType,
					Integer.toString(mob.location().domainType()), false, null);
			foundShortName = "nothing";
			if (found != null)
				foundShortName = RawMaterial.CODES.NAME(found.material())
						.toLowerCase();
		}
		int duration = getDuration(mob, 1);
		CMMsg msg = CMClass.getMsg(mob, found, this, getActivityMessageType(),
				"<S-NAME> start(s) drilling.");
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			container = (Drink) I;
			found = (Item) msg.target();
			beneficialAffect(mob, mob, asLevel, duration);
		}
		return true;
	}
}
