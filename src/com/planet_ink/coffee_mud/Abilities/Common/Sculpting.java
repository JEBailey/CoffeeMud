package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.ItemCraftor;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Common.interfaces.Session.InputCallback;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.DoorKey;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Light;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Libraries.interfaces.ListingLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.PairVector;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Sculpting extends EnhancedCraftingSkill implements ItemCraftor,
		MendingSkill {
	public String ID() {
		return "Sculpting";
	}

	public String name() {
		return "Sculpting";
	}

	private static final String[] triggerStrings = { "SCULPT", "SCULPTING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String supportedResourceString() {
		return "ROCK-BONE|STONE";
	}

	public String parametersFormat() {
		return "ITEM_NAME\tITEM_LEVEL\tBUILD_TIME_TICKS\tMATERIALS_REQUIRED\tITEM_BASE_VALUE\t"
				+ "ITEM_CLASS_ID\tSTATUE||LID_LOCK||RIDE_BASIS\tCONTAINER_CAPACITY||LIGHT_DURATION\t"
				+ "CONTAINER_TYPE\tCODED_SPELL_LIST";
	}

	// protected static final int RCP_FINALNAME=0;
	// protected static final int RCP_LEVEL=1;
	// protected static final int RCP_TICKS=2;
	protected static final int RCP_WOOD = 3;
	protected static final int RCP_VALUE = 4;
	protected static final int RCP_CLASSTYPE = 5;
	protected static final int RCP_MISCTYPE = 6;
	protected static final int RCP_CAPACITY = 7;
	protected static final int RCP_CONTAINMASK = 8;
	protected static final int RCP_SPELL = 9;

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
		return "sculpting.txt";
	}

	protected List<List<String>> loadRecipes() {
		return super.loadRecipes(parametersFile());
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB)) {
				MOB mob = (MOB) affected;
				if ((buildingI != null) && (!aborted)) {
					if (messedUp) {
						if (activity == CraftingActivity.MENDING)
							messedUpCrafting(mob);
						else if (activity == CraftingActivity.LEARNING) {
							commonEmote(mob,
									"<S-NAME> fail(s) to learn how to make "
											+ buildingI.name() + ".");
							buildingI.destroy();
						} else
							commonTell(mob, "<S-NAME> mess(es) up sculpting "
									+ buildingI.name(mob) + ".");
					} else {
						if (activity == CraftingActivity.MENDING)
							buildingI.setUsesRemaining(100);
						else if (activity == CraftingActivity.LEARNING) {
							deconstructRecipeInto(buildingI, recipeHolder);
							buildingI.destroy();
						} else {
							dropAWinner(mob, buildingI);
							if (key != null) {
								dropAWinner(mob, key);
								if (key instanceof Container)
									key.setContainer((Container) buildingI);
							}
						}
					}
				}
				buildingI = null;
				key = null;
				activity = CraftingActivity.CRAFTING;
			}
		}
		super.unInvoke();
	}

	public boolean mayICraft(final Item I) {
		if (I == null)
			return false;
		if (!super.mayBeCrafted(I))
			return false;
		if (I.material() == RawMaterial.RESOURCE_BONE)
			return false;
		if ((I.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_ROCK)
			return false;
		if (CMLib.flags().isDeadlyOrMaliciousEffect(I))
			return false;
		return true;
	}

	public boolean supportsMending(Physical item) {
		return canMend(null, item, true);
	}

	protected boolean canMend(MOB mob, Environmental E, boolean quiet) {
		if (!super.canMend(mob, E, quiet))
			return false;
		Item IE = (Item) E;
		if ((IE.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_ROCK) {
			if (!quiet)
				commonTell(mob,
						"That's not made of stone.  That can't be mended.");
			return false;
		}
		return true;
	}

	public String getDecodedComponentsDescription(final MOB mob,
			final List<String> recipe) {
		return super.getComponentDescription(mob, recipe, RCP_WOOD);
	}

	public boolean invoke(final MOB mob, Vector commands, Physical givenTarget,
			final boolean auto, final int asLevel) {
		final Vector originalCommands = (Vector) commands.clone();
		if (super.checkStop(mob, commands))
			return true;

		CraftParms parsedVars = super.parseAutoGenerate(auto, givenTarget,
				commands);
		givenTarget = parsedVars.givenTarget;

		PairVector<Integer, Integer> enhancedTypes = enhancedTypes(mob,
				commands);
		randomRecipeFix(mob, addRecipes(mob, loadRecipes()), commands,
				parsedVars.autoGenerate);
		if (commands.size() == 0) {
			commonTell(
					mob,
					"Sculpt what? Enter \"sculpt list\" for a list, \"sculpt scan\", \"sculpt learn <item>\", \"sculpt mend <item>\", or \"sculpt stop\" to cancel.");
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
		bundling = false;
		if (str.equalsIgnoreCase("list")) {
			String mask = CMParms.combine(commands, 1);
			boolean allFlag = false;
			if (mask.equalsIgnoreCase("all")) {
				allFlag = true;
				mask = "";
			}
			int[] cols = {
					ListingLibrary.ColFixer.fixColWidth(20, mob.session()),
					ListingLibrary.ColFixer.fixColWidth(3, mob.session()) };
			StringBuffer buf = new StringBuffer(CMStrings.padRight("Item",
					cols[0])
					+ " "
					+ CMStrings.padRight("Lvl", cols[1])
					+ " Stone required\n\r");
			for (int r = 0; r < recipes.size(); r++) {
				List<String> V = recipes.get(r);
				if (V.size() > 0) {
					String item = replacePercent(V.get(RCP_FINALNAME), "");
					int level = CMath.s_int(V.get(RCP_LEVEL));
					String wood = getComponentDescription(mob, V, RCP_WOOD);
					if (((level <= xlevel(mob)) || allFlag)
							&& ((mask.length() == 0)
									|| mask.equalsIgnoreCase("all") || CMLib
									.english().containsString(item, mask)))
						buf.append(CMStrings.padRight(item, cols[0]) + " "
								+ CMStrings.padRight("" + level, cols[1]) + " "
								+ wood + "\n\r");
				}
			}
			commonTell(mob, buf.toString());
			enhanceList(mob);
			return true;
		} else if ((commands.firstElement() instanceof String)
				&& (((String) commands.firstElement()))
						.equalsIgnoreCase("learn")) {
			return doLearnRecipe(mob, commands, givenTarget, auto, asLevel);
		} else if (str.equalsIgnoreCase("scan"))
			return publicScan(mob, commands);
		else if (str.equalsIgnoreCase("mend")) {
			buildingI = null;
			activity = CraftingActivity.CRAFTING;
			key = null;
			messedUp = false;
			Vector newCommands = CMParms.parse(CMParms.combine(commands, 1));
			buildingI = getTarget(mob, mob.location(), givenTarget,
					newCommands, Wearable.FILTER_UNWORNONLY);
			if (!canMend(mob, buildingI, false))
				return false;
			activity = CraftingActivity.MENDING;
			if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
				return false;
			startStr = "<S-NAME> start(s) mending " + buildingI.name() + ".";
			displayText = "You are mending " + buildingI.name();
			verb = "mending " + buildingI.name();

			if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
				return false;
		} else {
			buildingI = null;
			activity = CraftingActivity.CRAFTING;
			key = null;
			messedUp = false;
			aborted = false;
			String statue = null;
			if ((commands.size() > 1)
					&& ((String) commands.lastElement()).startsWith("STATUE=")) {
				statue = (((String) commands.lastElement()).substring(7))
						.trim();
				if (statue.length() == 0)
					statue = null;
				else
					commands.removeElementAt(commands.size() - 1);
			}
			int amount = -1;
			if ((commands.size() > 1)
					&& (CMath.isNumber((String) commands.lastElement()))) {
				amount = CMath.s_int((String) commands.lastElement());
				commands.removeElementAt(commands.size() - 1);
			}
			String recipeName = CMParms.combine(commands, 0);
			String rest = "";
			List<String> foundRecipe = null;
			List<List<String>> matches = matchingRecipeNames(recipes,
					recipeName, true);
			if (matches.size() == 0) {
				matches = matchingRecipeNames(recipes,
						(String) commands.firstElement(), true);
				if (matches.size() > 0) {
					recipeName = (String) commands.firstElement();
					rest = CMParms.combine(commands, 1);
				}
			}
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
				commonTell(mob, "You don't know how to sculpt a '" + recipeName
						+ "'.  Try \"sculpt list\" for a list.");
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
			String misctype = foundRecipe.get(RCP_MISCTYPE);
			bundling = misctype.equalsIgnoreCase("BUNDLE");
			int[] pm = { RawMaterial.MATERIAL_ROCK };
			int[][] data = fetchFoundResourceData(mob, woodRequired, "stone",
					pm, 0, null, null, bundling, parsedVars.autoGenerate,
					enhancedTypes);
			if (data == null)
				return false;
			fixDataForComponents(data, componentsFoundList);
			woodRequired = data[0][FOUND_AMT];
			buildingI = CMClass.getItem(foundRecipe.get(RCP_CLASSTYPE));
			if (buildingI == null) {
				commonTell(
						mob,
						"There's no such thing as a "
								+ foundRecipe.get(RCP_CLASSTYPE) + "!!!");
				return false;
			}
			duration = getDuration(CMath.s_int(foundRecipe.get(RCP_TICKS)),
					mob, CMath.s_int(foundRecipe.get(RCP_LEVEL)), 4);
			String itemName = replacePercent(foundRecipe.get(RCP_FINALNAME),
					RawMaterial.CODES.NAME(data[0][FOUND_CODE])).toLowerCase();
			if (bundling)
				itemName = "a " + woodRequired + "# " + itemName;
			else
				itemName = CMLib.english().startWithAorAn(itemName);
			buildingI.setName(itemName);
			startStr = "<S-NAME> start(s) sculpting " + buildingI.name() + ".";
			displayText = "You are sculpting " + buildingI.name();
			verb = "sculpting " + buildingI.name();
			playSound = "metalbat.wav";
			buildingI.setDisplayText(itemName + " lies here");
			buildingI.setDescription(itemName + ". ");
			buildingI.basePhyStats().setWeight(
					getStandardWeight(woodRequired, bundling));
			buildingI.setBaseValue(CMath.s_int(foundRecipe.get(RCP_VALUE))
					+ (woodRequired * (RawMaterial.CODES
							.VALUE(data[0][FOUND_CODE]))));
			buildingI.setMaterial(data[0][FOUND_CODE]);
			buildingI.basePhyStats().setLevel(
					CMath.s_int(foundRecipe.get(RCP_LEVEL)));
			buildingI.setSecretIdentity(getBrand(mob));
			String spell = (foundRecipe.size() > RCP_SPELL) ? foundRecipe.get(
					RCP_SPELL).trim() : "";
			addSpells(buildingI, spell);
			int capacity = CMath.s_int(foundRecipe.get(RCP_CAPACITY));
			long canContain = getContainerType(foundRecipe.get(RCP_CONTAINMASK));
			key = null;
			final Session session = mob.session();
			if ((misctype.equalsIgnoreCase("statue"))
					&& ((session != null)
							|| ((statue != null) && (statue.trim().length() > 0)) || (rest
							.trim().length() > 0))) {
				if (((statue == null) || (statue.trim().length() == 0))
						&& (rest.trim().length() == 0)) {
					final Ability me = this;
					final Physical target = givenTarget;
					if (session != null)
						session.prompt(new InputCallback(
								InputCallback.Type.PROMPT, "", 0) {
							@Override
							public void showPrompt() {
								session.promptPrint("What is this a statue of?\n\r: ");
							}

							@Override
							public void timedOut() {
							}

							@Override
							public void callBack() {
								String of = this.input;
								if ((of.trim().length() == 0)
										|| (of.indexOf('<') >= 0))
									return;
								Vector newCommands = (Vector) originalCommands
										.clone();
								newCommands.add("STATUE=" + of);
								me.invoke(mob, newCommands, target, auto,
										asLevel);
							}
						});
					return false;
				} else {
					if ((statue == null) || (statue.trim().length() == 0))
						statue = rest;
					buildingI.setName(itemName + " of " + statue.trim());
					buildingI.setDisplayText(itemName + " of " + statue.trim()
							+ " is here");
					buildingI.setDescription(itemName + " of " + statue.trim()
							+ ". ");
				}
			} else if (buildingI instanceof Container) {
				if (buildingI instanceof Drink) {
					if (CMLib.flags().isGettable(buildingI)) {
						((Drink) buildingI).setLiquidHeld(capacity * 50);
						((Drink) buildingI).setThirstQuenched(250);
						if ((capacity * 50) < 250)
							((Drink) buildingI)
									.setThirstQuenched(capacity * 50);
						((Drink) buildingI).setLiquidRemaining(0);
					}
				}
				if (capacity > 0) {
					((Container) buildingI)
							.setCapacity(capacity + woodRequired);
					((Container) buildingI).setContainTypes(canContain);
				}
				if (misctype.equalsIgnoreCase("LID"))
					((Container) buildingI).setLidsNLocks(true, false, false,
							false);
				else if (misctype.equalsIgnoreCase("LOCK")) {
					((Container) buildingI).setLidsNLocks(true, false, true,
							false);
					((Container) buildingI).setKeyName(Double.toString(Math
							.random()));
					key = CMClass.getItem("GenKey");
					((DoorKey) key).setKey(((Container) buildingI).keyName());
					key.setName("a key");
					key.setDisplayText("a small key sits here");
					key.setDescription("looks like a key to "
							+ buildingI.name());
					key.recoverPhyStats();
					key.text();
				}
			}
			if (buildingI instanceof Rideable) {
				setRideBasis((Rideable) buildingI, misctype);
			}
			if (buildingI instanceof Light) {
				((Light) buildingI).setDuration(capacity);
				if (buildingI instanceof Container)
					((Container) buildingI).setCapacity(0);
			}
			buildingI.recoverPhyStats();
			if ((!CMLib.flags().isGettable(buildingI))
					&& (!CMLib.law().doesOwnThisProperty(mob, mob.location()))) {
				commonTell(mob, "You are not allowed to build that here.");
				return false;
			}
			if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
				return false;
			int lostValue = parsedVars.autoGenerate > 0 ? 0 : CMLib.materials()
					.destroyResourcesValue(mob.location(), woodRequired,
							data[0][FOUND_CODE], 0, buildingI)
					+ CMLib.ableMapper().destroyAbilityComponents(
							componentsFoundList);
			if (bundling)
				buildingI.setBaseValue(lostValue);
			buildingI.text();
			buildingI.recoverPhyStats();

		}

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
			enhanceItem(mob, buildingI, enhancedTypes);
		} else if (bundling) {
			messedUp = false;
			aborted = false;
			unInvoke();
		}
		return true;
	}
}
