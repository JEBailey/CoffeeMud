package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.ItemCraftor;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Armor;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.FalseLimb;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Shield;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Libraries.interfaces.ListingLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.PairVector;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Armorsmithing extends EnhancedCraftingSkill implements
		ItemCraftor, MendingSkill {
	public String ID() {
		return "Armorsmithing";
	}

	public String name() {
		return "Armorsmithing";
	}

	private static final String[] triggerStrings = { "ARMORSMITH",
			"ARMORSMITHING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String supportedResourceString() {
		return "METAL|MITHRIL";
	}

	public String parametersFormat() {
		return "ITEM_NAME\tITEM_LEVEL\tBUILD_TIME_TICKS\tMATERIALS_REQUIRED\t"
				+ "ITEM_BASE_VALUE\tITEM_CLASS_ID\tCODED_WEAR_LOCATION\tCONTAINER_CAPACITY\t"
				+ "BASE_ARMOR_AMOUNT\tCONTAINER_TYPE\tCODED_SPELL_LIST";
	}

	// protected static final int RCP_FINALNAME=0;
	// protected static final int RCP_LEVEL=1;
	// protected static final int RCP_TICKS=2;
	protected static final int RCP_WOOD = 3;
	protected static final int RCP_VALUE = 4;
	protected static final int RCP_CLASSTYPE = 5;
	protected static final int RCP_MISCTYPE = 6;
	protected static final int RCP_CAPACITY = 7;
	protected static final int RCP_ARMORDMG = 8;
	protected static final int RCP_CONTAINMASK = 9;
	protected static final int RCP_SPELL = 10;

	protected String primeMaterialDesc() {
		return "metal";
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected != null) && (affected instanceof MOB)
				&& (tickID == Tickable.TICKID_MOB)) {
			MOB mob = (MOB) affected;
			if ((buildingI == null) || (getRequiredFire(mob, 0) == null)) {
				messedUp = true;
				unInvoke();
			}
		}
		return super.tick(ticking, tickID);
	}

	public String parametersFile() {
		return "armorsmith.txt";
	}

	protected List<List<String>> loadRecipes() {
		return super.loadRecipes(parametersFile());
	}

	protected boolean doLearnRecipe(MOB mob, Vector commands,
			Physical givenTarget, boolean auto, int asLevel) {
		fireRequired = false;
		return super.doLearnRecipe(mob, commands, givenTarget, auto, asLevel);
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
						} else if (activity == CraftingActivity.REFITTING)
							commonEmote(mob, "<S-NAME> mess(es) up refitting "
									+ buildingI.name() + ".");
						else
							commonEmote(mob, "<S-NAME> mess(es) up smithing "
									+ buildingI.name() + ".");
					} else {
						if (activity == CraftingActivity.MENDING)
							buildingI.setUsesRemaining(100);
						else if (activity == CraftingActivity.LEARNING) {
							deconstructRecipeInto(buildingI, recipeHolder);
							buildingI.destroy();
						} else if (activity == CraftingActivity.REFITTING) {
							buildingI.basePhyStats().setHeight(0);
							buildingI.recoverPhyStats();
						} else
							dropAWinner(mob, buildingI);
					}
				}
				buildingI = null;
				activity = CraftingActivity.CRAFTING;
			}
		}
		super.unInvoke();
	}

	protected boolean masterCraftCheck(final Item I) {
		if (I.basePhyStats().level() > 30)
			return false;
		return true;
	}

	public boolean mayICraft(final Item I) {
		if (I == null)
			return false;
		if (!super.mayBeCrafted(I))
			return false;
		if (((I.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_METAL)
				&& ((I.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_MITHRIL))
			return false;
		if (CMLib.flags().isDeadlyOrMaliciousEffect(I))
			return false;
		if (!(I instanceof Armor))
			return false;
		if (I instanceof Shield)
			return true;
		if (!masterCraftCheck(I))
			return isANativeItem(I.Name());
		if (I.fitsOn(Wearable.WORN_RIGHT_FINGER)
				|| I.fitsOn(Wearable.WORN_LEFT_FINGER)
				|| I.fitsOn(Wearable.WORN_EARS) || I.fitsOn(Wearable.WORN_HELD)
				|| I.fitsOn(Wearable.WORN_EYES))
			return isANativeItem(I.Name());
		return true;
	}

	public boolean supportsDeconstruction() {
		return true;
	}

	public boolean supportsMending(Physical I) {
		return canMend(null, I, true);
	}

	protected boolean canMend(MOB mob, Environmental E, boolean quiet) {
		if (!super.canMend(mob, E, quiet))
			return false;
		if ((!(E instanceof Item)) || (!mayICraft((Item) E))) {
			if (!quiet)
				commonTell(
						mob,
						"That's not "
								+ CMLib.english().startWithAorAn(
										Name().toLowerCase()) + " item.");
			return false;
		}
		return true;
	}

	public String getDecodedComponentsDescription(final MOB mob,
			final List<String> recipe) {
		return super.getComponentDescription(mob, recipe, RCP_WOOD);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (super.checkStop(mob, commands))
			return true;
		fireRequired = true;

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
					"Make what? Enter \"armorsmith list\" for a list, \"armorsmith refit <item>\" to resize, \"armorsmith learn <item>\", \"armorsmith scan\", \"armorsmith mend <item>\", or \"armorsmith stop\" to cancel.");
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
		bundling = false;
		int duration = 4;
		if (str.equalsIgnoreCase("list")) {
			String mask = CMParms.combine(commands, 1);
			boolean allFlag = false;
			if (mask.equalsIgnoreCase("all")) {
				allFlag = true;
				mask = "";
			}
			StringBuffer buf = new StringBuffer("");
			int[] cols = {
					ListingLibrary.ColFixer.fixColWidth(29, mob.session()),
					ListingLibrary.ColFixer.fixColWidth(3, mob.session()),
					ListingLibrary.ColFixer.fixColWidth(3, mob.session()) };
			int toggler = 1;
			int toggleTop = 2;
			for (int r = 0; r < toggleTop; r++)
				buf.append((r > 0 ? " " : "")
						+ CMStrings.padRight("Item", cols[0]) + " "
						+ CMStrings.padRight("Lvl", cols[1]) + " "
						+ CMStrings.padRight("Amt", cols[2]));
			buf.append("\n\r");
			for (int r = 0; r < recipes.size(); r++) {
				List<String> V = recipes.get(r);
				if (V.size() > 0) {
					String item = replacePercent(V.get(RCP_FINALNAME), "");
					int level = CMath.s_int(V.get(RCP_LEVEL));
					String wood = getComponentDescription(mob, V, RCP_WOOD);
					if (wood.length() > 5) {
						if (toggler > 1)
							buf.append("\n\r");
						toggler = toggleTop;
					}
					if (((level <= xlevel(mob)) || allFlag)
							&& ((mask.length() == 0)
									|| mask.equalsIgnoreCase("all") || CMLib
									.english().containsString(item, mask))) {
						buf.append(CMStrings.padRight(item, cols[0])
								+ " "
								+ CMStrings.padRight("" + level, cols[1])
								+ " "
								+ CMStrings
										.padRightPreserve("" + wood, cols[2])
								+ ((toggler != toggleTop) ? " " : "\n\r"));
						if (++toggler > toggleTop)
							toggler = 1;
					}
				}
			}
			if (toggler != 1)
				buf.append("\n\r");
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
			messedUp = false;
			Item fire = getRequiredFire(mob, parsedVars.autoGenerate);
			if (fire == null)
				return false;
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
		} else if (str.equalsIgnoreCase("refit")) {
			buildingI = null;
			activity = CraftingActivity.CRAFTING;
			messedUp = false;
			Item fire = getRequiredFire(mob, parsedVars.autoGenerate);
			if (fire == null)
				return false;
			Vector newCommands = CMParms.parse(CMParms.combine(commands, 1));
			buildingI = getTarget(mob, mob.location(), givenTarget,
					newCommands, Wearable.FILTER_UNWORNONLY);
			if (buildingI == null)
				return false;
			if (((buildingI.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_METAL)
					&& ((buildingI.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_MITHRIL)) {
				commonTell(mob,
						"That's not made of metal.  That can't be refitted.");
				return false;
			}
			if (!(buildingI instanceof Armor)) {
				commonTell(mob,
						"You don't know how to refit that sort of thing.");
				return false;
			}
			if (buildingI.phyStats().height() == 0) {
				commonTell(mob, buildingI.name(mob)
						+ " is already the right size.");
				return false;
			}
			activity = CraftingActivity.REFITTING;
			if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
				return false;
			startStr = "<S-NAME> start(s) refitting " + buildingI.name() + ".";
			displayText = "You are refitting " + buildingI.name();
			verb = "refitting " + buildingI.name();
		} else {
			buildingI = null;
			activity = CraftingActivity.CRAFTING;
			messedUp = false;
			aborted = false;
			Item fire = getRequiredFire(mob, parsedVars.autoGenerate);
			if (fire == null)
				return false;
			int amount = -1;
			if ((commands.size() > 1)
					&& (CMath.isNumber((String) commands.lastElement()))) {
				amount = CMath.s_int((String) commands.lastElement());
				commands.removeElementAt(commands.size() - 1);
			}

			String recipeName = CMParms.combine(commands, 0);
			List<String> foundRecipe = null;
			List<List<String>> matches = matchingRecipeNames(recipes,
					recipeName, true);
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
				commonTell(mob, "You don't know how to make a '" + recipeName
						+ "'.  Try \"armorsmith list\" for a list.");
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
			int[] pm = { RawMaterial.MATERIAL_METAL,
					RawMaterial.MATERIAL_MITHRIL };
			bundling = misctype.equalsIgnoreCase("BUNDLE");
			int[][] data = fetchFoundResourceData(mob, woodRequired, "metal",
					pm, 0, null, null, bundling, parsedVars.autoGenerate,
					enhancedTypes);
			if (data == null)
				return false;
			fixDataForComponents(data, componentsFoundList);
			woodRequired = data[0][FOUND_AMT];

			if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
				return false;
			int lostValue = parsedVars.autoGenerate > 0 ? 0 : CMLib.materials()
					.destroyResourcesValue(mob.location(), data[0][FOUND_AMT],
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
			duration = getDuration(CMath.s_int(foundRecipe.get(RCP_TICKS)),
					mob, CMath.s_int(foundRecipe.get(RCP_LEVEL)), 6);
			String itemName = replacePercent(foundRecipe.get(RCP_FINALNAME),
					RawMaterial.CODES.NAME(data[0][FOUND_CODE])).toLowerCase();
			if (itemName.endsWith("s"))
				itemName = "some " + itemName;
			else
				itemName = CMLib.english().startWithAorAn(itemName);
			buildingI.setName(itemName);
			startStr = "<S-NAME> start(s) smithing " + buildingI.name() + ".";
			displayText = "You are smithing " + buildingI.name();
			verb = "smithing " + buildingI.name();
			playSound = "ratchet.wav";
			buildingI.setDisplayText(itemName + " lies here");
			buildingI.setDescription(itemName + ". ");
			buildingI.basePhyStats().setWeight(
					getStandardWeight(woodRequired, bundling));
			buildingI.setBaseValue(CMath.s_int(foundRecipe.get(RCP_VALUE)));
			buildingI.setMaterial(data[0][FOUND_CODE]);
			int hardness = RawMaterial.CODES.HARDNESS(data[0][FOUND_CODE]) - 6;
			buildingI.basePhyStats().setLevel(
					CMath.s_int(foundRecipe.get(RCP_LEVEL)) + (hardness * 3));
			if (buildingI.basePhyStats().level() < 1)
				buildingI.basePhyStats().setLevel(1);
			int capacity = CMath.s_int(foundRecipe.get(RCP_CAPACITY));
			long canContain = getContainerType(foundRecipe.get(RCP_CONTAINMASK));
			int armordmg = CMath.s_int(foundRecipe.get(RCP_ARMORDMG));
			buildingI.setSecretIdentity(getBrand(mob));
			String spell = (foundRecipe.size() > RCP_SPELL) ? foundRecipe.get(
					RCP_SPELL).trim() : "";
			if (bundling)
				buildingI.setBaseValue(lostValue);
			addSpells(buildingI, spell);
			if ((buildingI instanceof Armor)
					&& (!(buildingI instanceof FalseLimb))) {
				((Armor) buildingI).basePhyStats().setArmor(0);
				if (armordmg != 0)
					((Armor) buildingI).basePhyStats().setArmor(
							armordmg + (abilityCode() - 1));
				setWearLocation(buildingI, misctype, hardness);
			}
			if (buildingI instanceof Container)
				if (capacity > 0) {
					((Container) buildingI)
							.setCapacity(capacity + woodRequired);
					((Container) buildingI).setContainTypes(canContain);
				}
			buildingI.recoverPhyStats();
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
