package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.ItemCraftor;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Armor;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.DoorKey;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Libraries.interfaces.ListingLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;
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
public class Wainwrighting extends CraftingSkill implements ItemCraftor {
	public String ID() {
		return "Wainwrighting";
	}

	public String name() {
		return "Wainwrighting";
	}

	private static final String[] triggerStrings = { "WAINWRIGHTING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String supportedResourceString() {
		return "WOODEN";
	}

	public String parametersFormat() {
		return "ITEM_NAME\tITEM_LEVEL\tBUILD_TIME_TICKS\tMATERIALS_REQUIRED\tITEM_BASE_VALUE\t"
				+ "ITEM_CLASS_ID\tLID_LOCK\tCONTAINER_CAPACITY\tRIDE_CAPACITY\tCONTAINER_TYPE\t"
				+ "CODED_SPELL_LIST";
	}

	// protected static final int RCP_FINALNAME=0;
	// protected static final int RCP_LEVEL=1;
	// protected static final int RCP_TICKS=2;
	protected static final int RCP_WOOD = 3;
	protected static final int RCP_VALUE = 4;
	protected static final int RCP_CLASSTYPE = 5;
	protected static final int RCP_MISCTYPE = 6;
	protected static final int RCP_CAPACITY = 7;
	protected static final int RCP_NUMRIDERS = 8;
	protected static final int RCP_CONTAINMASK = 9;
	protected static final int RCP_SPELL = 10;

	protected Item key = null;

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected != null) && (affected instanceof MOB)
				&& (tickID == Tickable.TICKID_MOB)) {
			if (buildingI == null)
				unInvoke();
		}
		return super.tick(ticking, tickID);
	}

	public String parametersFile() {
		return "wainwright.txt";
	}

	protected List<List<String>> loadRecipes() {
		return super.loadRecipes(parametersFile());
	}

	public boolean supportsDeconstruction() {
		return true;
	}

	public boolean mayICraft(final Item I) {
		if (I == null)
			return false;
		if (!super.mayBeCrafted(I))
			return false;
		if (CMLib.flags().isDeadlyOrMaliciousEffect(I))
			return false;
		if (isANativeItem(I.Name()))
			return true;
		if (I instanceof Container) {
			Container C = (Container) I;
			if ((C.containTypes() == Container.CONTAIN_BODIES)
					|| (C.containTypes() == Container.CONTAIN_CAGED)
					|| (C.containTypes() == (Container.CONTAIN_BODIES | Container.CONTAIN_CAGED)))
				return false;
		}
		if (I instanceof Rideable) {
			Rideable R = (Rideable) I;
			int rideType = R.rideBasis();
			switch (rideType) {
			case Rideable.RIDEABLE_AIR:
			case Rideable.RIDEABLE_LAND:
			case Rideable.RIDEABLE_WAGON:
				return true;
			default:
				return false;
			}
		}
		return (isANativeItem(I.Name()));
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB)) {
				MOB mob = (MOB) affected;
				if ((buildingI != null) && (!aborted)) {
					if (messedUp) {
						if (activity == CraftingActivity.LEARNING)
							commonEmote(mob,
									"<S-NAME> fail(s) to learn how to make "
											+ buildingI.name() + ".");
						else
							commonEmote(mob, "<S-NAME> mess(es) up building "
									+ buildingI.name() + ".");
						buildingI.destroy();
					} else if (activity == CraftingActivity.LEARNING) {
						deconstructRecipeInto(buildingI, recipeHolder);
						buildingI.destroy();
					} else {
						dropAWinner(mob, buildingI);
						if (key != null) {
							dropAWinner(mob, key);
							if (buildingI instanceof Container)
								key.setContainer((Container) buildingI);
						}
					}
				}
				buildingI = null;
				key = null;
			}
		}
		super.unInvoke();
	}

	public String getDecodedComponentsDescription(final MOB mob,
			final List<String> recipe) {
		return super.getComponentDescription(mob, recipe, RCP_WOOD);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (super.checkStop(mob, commands))
			return true;

		CraftParms parsedVars = super.parseAutoGenerate(auto, givenTarget,
				commands);
		givenTarget = parsedVars.givenTarget;

		randomRecipeFix(mob, addRecipes(mob, loadRecipes()), commands,
				parsedVars.autoGenerate);
		if (commands.size() == 0) {
			commonTell(
					mob,
					"Wainwright what? Enter \"wainwright list\" for a list, \"wainwright learn <item>\" to gain recipes, or \"wainwright stop\" to cancel.");
			return false;
		}
		if ((!auto)
				&& (commands.size() > 0)
				&& (((String) commands.firstElement())
						.equalsIgnoreCase("bundle"))) {
			bundling = true;
			if (super.invoke(mob, commands, givenTarget, auto, asLevel))
				return super.bundle(mob, commands);
			return false;
		}
		List<List<String>> recipes = addRecipes(mob, loadRecipes());
		String str = (String) commands.elementAt(0);
		String startStr = null;
		int duration = 4;
		if (str.equalsIgnoreCase("list")) {
			String mask = CMParms.combine(commands, 1);
			boolean allFlag = false;
			if (mask.equalsIgnoreCase("all")) {
				allFlag = true;
				mask = "";
			}
			int[] cols = {
					ListingLibrary.ColFixer.fixColWidth(25, mob.session()),
					ListingLibrary.ColFixer.fixColWidth(5, mob.session()),
					ListingLibrary.ColFixer.fixColWidth(8, mob.session()) };
			StringBuffer buf = new StringBuffer(CMStrings.padRight("Item",
					cols[0])
					+ " "
					+ CMStrings.padRight("Level", cols[1])
					+ " "
					+ CMStrings.padRight("Capacity", cols[2])
					+ " Wood required\n\r");
			for (int r = 0; r < recipes.size(); r++) {
				List<String> V = recipes.get(r);
				if (V.size() > 0) {
					String item = replacePercent(V.get(RCP_FINALNAME), "");
					int level = CMath.s_int(V.get(RCP_LEVEL));
					String wood = getComponentDescription(mob, V, RCP_WOOD);
					int capacity = CMath.s_int(V.get(RCP_CAPACITY));
					if (((level <= xlevel(mob)) || allFlag)
							&& ((mask.length() == 0)
									|| mask.equalsIgnoreCase("all") || CMLib
									.english().containsString(item, mask)))
						buf.append(CMStrings.padRight(item, cols[0]) + " "
								+ CMStrings.padRight("" + level, cols[1]) + " "
								+ CMStrings.padRight("" + capacity, cols[2])
								+ " " + wood + "\n\r");
				}
			}
			commonTell(mob, buf.toString());
			return true;
		} else if ((commands.firstElement() instanceof String)
				&& (((String) commands.firstElement()))
						.equalsIgnoreCase("learn")) {
			return doLearnRecipe(mob, commands, givenTarget, auto, asLevel);
		}
		activity = CraftingActivity.CRAFTING;
		buildingI = null;
		key = null;
		messedUp = false;
		int amount = -1;
		if ((commands.size() > 1)
				&& (CMath.isNumber((String) commands.lastElement()))) {
			amount = CMath.s_int((String) commands.lastElement());
			commands.removeElementAt(commands.size() - 1);
		}
		String recipeName = CMParms.combine(commands, 0);
		List<String> foundRecipe = null;
		List<List<String>> matches = matchingRecipeNames(recipes, recipeName,
				true);
		for (int r = 0; r < matches.size(); r++) {
			List<String> V = matches.get(r);
			if (V.size() > 0) {
				int level = CMath.s_int(V.get(RCP_LEVEL));
				if ((parsedVars.autoGenerate > 0) || (level <= xlevel(mob))) {
					foundRecipe = V;
					break;
				}
			}
		}
		if (foundRecipe == null) {
			commonTell(mob, "You don't know how to build a '" + recipeName
					+ "'.  Try \"list\" as your parameter for a list.");
			return false;
		}

		final String woodRequiredStr = foundRecipe.get(RCP_WOOD);
		final List<Object> componentsFoundList = getAbilityComponents(mob,
				woodRequiredStr,
				"make " + CMLib.english().startWithAorAn(recipeName),
				parsedVars.autoGenerate);
		if (componentsFoundList == null)
			return false;
		int woodRequired = CMath.s_int(woodRequiredStr);
		woodRequired = adjustWoodRequired(woodRequired, mob);

		if (amount > woodRequired)
			woodRequired = amount;
		int[] pm = { RawMaterial.MATERIAL_WOODEN };
		String misctype = foundRecipe.get(RCP_MISCTYPE);
		int[][] data = fetchFoundResourceData(mob, woodRequired, "wood", pm, 0,
				null, null, false, parsedVars.autoGenerate, null);
		if (data == null)
			return false;
		woodRequired = data[0][FOUND_AMT];
		bundling = misctype.equalsIgnoreCase("BUNDLE");
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		int lostValue = parsedVars.autoGenerate > 0 ? 0 : CMLib.materials()
				.destroyResourcesValue(mob.location(), woodRequired,
						data[0][FOUND_CODE], 0, null)
				+ CMLib.ableMapper().destroyAbilityComponents(
						componentsFoundList);
		buildingI = CMClass.getItem(foundRecipe.get(RCP_CLASSTYPE));
		if (buildingI == null) {
			commonTell(
					mob,
					"There's no such thing as a "
							+ foundRecipe.get(RCP_CLASSTYPE) + "!!!");
			return false;
		}
		duration = getDuration(CMath.s_int(foundRecipe.get(RCP_TICKS)), mob,
				CMath.s_int(foundRecipe.get(RCP_LEVEL)), 4);
		String itemName = replacePercent(foundRecipe.get(RCP_FINALNAME),
				RawMaterial.CODES.NAME(data[0][FOUND_CODE])).toLowerCase();
		if (bundling)
			itemName = "a " + woodRequired + "# " + itemName;
		else
			itemName = CMLib.english().startWithAorAn(itemName);
		buildingI.setName(itemName);
		startStr = "<S-NAME> start(s) building " + buildingI.name() + ".";
		displayText = "You are building " + buildingI.name();
		verb = "building " + buildingI.name();
		playSound = "hammer.wav";
		buildingI.setDisplayText(itemName + " lies here");
		buildingI.setDescription(itemName + ". ");
		buildingI.basePhyStats().setWeight(
				getStandardWeight(woodRequired, bundling));
		buildingI.setBaseValue(CMath.s_int(foundRecipe.get(RCP_VALUE)));
		buildingI.setMaterial(data[0][FOUND_CODE]);
		buildingI.basePhyStats().setLevel(
				CMath.s_int(foundRecipe.get(RCP_LEVEL)));
		buildingI.setSecretIdentity(getBrand(mob));
		int capacity = CMath.s_int(foundRecipe.get(RCP_CAPACITY));
		long canContain = getContainerType(foundRecipe.get(RCP_CONTAINMASK));
		int riders = CMath.s_int(foundRecipe.get(RCP_NUMRIDERS));
		String spell = (foundRecipe.size() > RCP_SPELL) ? foundRecipe.get(
				RCP_SPELL).trim() : "";
		addSpells(buildingI, spell);
		key = null;
		if (buildingI instanceof Rideable) {
			((Rideable) buildingI).setRideBasis(Rideable.RIDEABLE_WAGON);
			((Rideable) buildingI).setRiderCapacity(riders);
		}

		if ((buildingI instanceof Container) && (!(buildingI instanceof Armor))) {
			if (capacity > 0) {
				((Container) buildingI).setCapacity(capacity + woodRequired);
				((Container) buildingI).setContainTypes(canContain);
			}
			if (misctype.equalsIgnoreCase("LID"))
				((Container) buildingI)
						.setLidsNLocks(true, false, false, false);
			else if (misctype.equalsIgnoreCase("LOCK")) {
				((Container) buildingI).setLidsNLocks(true, false, true, false);
				((Container) buildingI).setKeyName(Double.toString(Math
						.random()));
				key = CMClass.getItem("GenKey");
				((DoorKey) key).setKey(((Container) buildingI).keyName());
				key.setName("a key");
				key.setDisplayText("a small key sits here");
				key.setDescription("looks like a key to " + buildingI.name());
				key.recoverPhyStats();
				key.text();
			}
		}
		if (bundling)
			buildingI.setBaseValue(lostValue);
		buildingI.recoverPhyStats();
		buildingI.text();
		buildingI.recoverPhyStats();

		messedUp = !proficiencyCheck(mob, 0, auto);

		if (bundling) {
			messedUp = false;
			duration = 1;
			verb = "bundling "
					+ RawMaterial.CODES.NAME(buildingI.material())
							.toLowerCase();
			startStr = "<S-NAME> start(s) " + verb + ".";
			displayText = "You are " + verb;
		}

		if (parsedVars.autoGenerate > 0) {
			commands.addElement(buildingI);
			return true;
		}

		CMMsg msg = CMClass.getMsg(mob, buildingI, this,
				getActivityMessageType(), startStr);
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			buildingI = (Item) msg.target();
			beneficialAffect(mob, mob, asLevel, duration);
		} else if (bundling) {
			messedUp = false;
			aborted = false;
			unInvoke();
		}
		return true;
	}
}
