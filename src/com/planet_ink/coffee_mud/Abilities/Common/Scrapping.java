package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Libraries.interfaces.ExpertiseLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
import com.planet_ink.coffee_mud.core.interfaces.LandTitle;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Scrapping extends CommonSkill {
	public String ID() {
		return "Scrapping";
	}

	public String name() {
		return "Scrapping";
	}

	private static final String[] triggerStrings = { "SCRAP", "SCRAPPING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected ExpertiseLibrary.SkillCostDefinition getRawTrainingCost() {
		return CMProps.getSkillTrainCostFormula(ID());
	}

	public int classificationCode() {
		return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_NATURELORE;
	}

	protected Item found = null;
	boolean fireRequired = false;
	protected int amount = 0;
	protected String oldItemName = "";
	protected String foundShortName = "";
	protected boolean messedUp = false;

	public Scrapping() {
		super();
		displayText = "You are scrapping...";
		verb = "scrapping";
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected != null) && (affected instanceof MOB)
				&& (tickID == Tickable.TICKID_MOB)) {
			MOB mob = (MOB) affected;
			if ((found == null)
					|| (fireRequired && (getRequiredFire(mob, 0) == null))) {
				messedUp = true;
				unInvoke();
			}
		}
		return super.tick(ticking, tickID);
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB)) {
				MOB mob = (MOB) affected;
				if ((found != null) && (!aborted)) {
					if (messedUp)
						commonTell(mob, "You've messed up scrapping "
								+ oldItemName + "!");
					else {
						amount = amount * abilityCode();
						String s = "s";
						if (amount == 1)
							s = "";
						mob.location().show(
								mob,
								null,
								getActivityMessageType(),
								"<S-NAME> manage(s) to scrap " + amount
										+ " pound" + s + " of "
										+ foundShortName + ".");
						for (int i = 0; i < amount; i++) {
							Item newFound = (Item) found.copyOf();
							mob.location().addItem(newFound,
									ItemPossessor.Expire.Player_Drop);
							CMLib.commands().postGet(mob, null, newFound, true);
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
		verb = "scrapping";
		String str = CMParms.combine(commands, 0);
		Item I = mob.location().findItem(null, str);
		if ((I == null) || (!CMLib.flags().canBeSeenBy(I, mob))) {
			commonTell(mob, "You don't see anything called '" + str + "' here.");
			return false;
		}
		boolean okMaterial = true;
		oldItemName = I.Name();
		switch (I.material() & RawMaterial.MATERIAL_MASK) {
		case RawMaterial.MATERIAL_FLESH:
		case RawMaterial.MATERIAL_LIQUID:
		case RawMaterial.MATERIAL_PAPER:
		case RawMaterial.MATERIAL_ENERGY:
		case RawMaterial.MATERIAL_GAS:
		case RawMaterial.MATERIAL_VEGETATION: {
			okMaterial = false;
			break;
		}
		}
		if (!okMaterial) {
			commonTell(mob, "You don't know how to scrap " + I.name(mob) + ".");
			return false;
		}

		if (I instanceof RawMaterial) {
			commonTell(mob, I.name(mob) + " already looks like scrap.");
			return false;
		}

		if (CMLib.flags().enchanted(I)) {
			commonTell(mob, I.name(mob)
					+ " is enchanted, and can't be scrapped.");
			return false;
		}

		Vector V = new Vector();
		int totalWeight = 0;
		for (int i = 0; i < mob.location().numItems(); i++) {
			Item I2 = mob.location().getItem(i);
			if ((I2 != null) && (I2.sameAs(I))) {
				totalWeight += I2.phyStats().weight();
				V.addElement(I2);
			}
		}

		LandTitle t = CMLib.law().getLandTitle(mob.location());
		if ((t != null)
				&& (!CMLib.law().doesHavePriviledgesHere(mob, mob.location()))) {
			mob.tell("You are not allowed to scrap anything here.");
			return false;
		}

		for (int i = 0; i < mob.location().numItems(); i++) {
			Item I2 = mob.location().getItem(i);
			if ((I2.container() != null) && (V.contains(I2.container()))) {
				commonTell(mob,
						"You need to remove the contents of " + I2.name(mob)
								+ " first.");
				return false;
			}
		}
		amount = totalWeight / 5;
		if (amount < 1) {
			commonTell(mob, "You don't have enough here to get anything from.");
			return false;
		}
		fireRequired = false;
		if (((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_GLASS)
				|| ((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_METAL)
				|| ((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_SYNTHETIC)
				|| ((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_MITHRIL)) {
			Item fire = getRequiredFire(mob, 0);
			fireRequired = true;
			if (fire == null)
				return false;
		}

		found = null;
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		int duration = getDuration(45, mob, 1, 10);
		messedUp = !proficiencyCheck(mob, 0, auto);
		found = CMLib.materials().makeItemResource(I.material());
		foundShortName = "nothing";
		playSound = "ripping.wav";
		if (found != null)
			foundShortName = RawMaterial.CODES.NAME(found.material())
					.toLowerCase();
		CMMsg msg = CMClass.getMsg(mob, I, this, getActivityMessageType(),
				"<S-NAME> start(s) scrapping " + I.name() + ".");
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			for (int v = 0; v < V.size(); v++) {
				if (((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_PRECIOUS)
						|| ((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_METAL)
						|| ((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_MITHRIL))
					duration += ((Item) V.elementAt(v)).phyStats().weight();
				else
					duration += ((Item) V.elementAt(v)).phyStats().weight() / 2;
				((Item) V.elementAt(v)).destroy();
			}
			beneficialAffect(mob, mob, asLevel, duration);
		}
		return true;
	}
}
