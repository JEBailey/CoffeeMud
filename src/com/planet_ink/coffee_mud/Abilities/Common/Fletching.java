package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.ItemCraftor;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Ammunition;
import com.planet_ink.coffee_mud.Items.interfaces.AmmunitionWeapon;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class Fletching extends EnhancedCraftingSkill implements ItemCraftor,
		MendingSkill {
	public String ID() {
		return "Fletching";
	}

	public String name() {
		return "Fletching";
	}

	private static final String[] triggerStrings = { "FLETCH", "FLETCHING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String supportedResourceString() {
		return "WOODEN";
	}

	public String parametersFormat() {
		return "ITEM_NAME\tITEM_LEVEL\tBUILD_TIME_TICKS\tMATERIALS_REQUIRED\tITEM_BASE_VALUE\t"
				+ "ITEM_CLASS_ID\tAMMO_TYPE\tAMMO_CAPACITY\tBASE_DAMAGE\tMAXIMUM_RANGE\t"
				+ "OPTIONAL_RESOURCE_OR_MATERIAL\tCODED_SPELL_LIST";
	}

	// protected static final int RCP_FINALNAME=0;
	// protected static final int RCP_LEVEL=1;
	// protected static final int RCP_TICKS=2;
	protected static final int RCP_WOOD = 3;
	protected static final int RCP_VALUE = 4;
	protected static final int RCP_CLASSTYPE = 5;
	protected static final int RCP_AMMOTYPE = 6;
	protected static final int RCP_AMOCAPACITY = 7;
	protected static final int RCP_ARMORDMG = 8;
	protected static final int RCP_MAXRANGE = 9;
	protected static final int RCP_EXTRAREQ = 10;
	protected static final int RCP_SPELL = 11;

	public String parametersFile() {
		return "fletching.txt";
	}

	protected List<List<String>> loadRecipes() {
		return super.loadRecipes(parametersFile());
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB)) {
				MOB mob = (MOB) affected;
				if ((buildingI != null) && (!aborted) && (!helping)) {
					if (messedUp) {
						if (activity == CraftingActivity.MENDING)
							messedUpCrafting(mob);
						else if (activity == CraftingActivity.LEARNING) {
							commonEmote(mob,
									"<S-NAME> fail(s) to learn how to make "
											+ buildingI.name() + ".");
							buildingI.destroy();
						} else
							commonEmote(mob, "<S-NAME> mess(es) up making "
									+ buildingI.name() + ".");
					} else {
						if (activity == CraftingActivity.MENDING)
							buildingI.setUsesRemaining(100);
						else if (activity == CraftingActivity.LEARNING) {
							deconstructRecipeInto(buildingI, recipeHolder);
							buildingI.destroy();
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

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected != null) && (affected instanceof MOB)
				&& (tickID == Tickable.TICKID_MOB)) {
			if (buildingI == null)
				unInvoke();
		}
		return super.tick(ticking, tickID);
	}

	public boolean mayICraft(final Item I) {
		if (I == null)
			return false;
		if (!super.mayBeCrafted(I))
			return false;
		if ((I.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN)
			return false;
		if (I instanceof Ammunition)
			return true;
		if (!(I instanceof Weapon))
			return false;
		if ((((Weapon) I).weaponClassification() == Weapon.CLASS_RANGED)
				|| (((Weapon) I).weaponClassification() == Weapon.CLASS_THROWN))
			return true;
		return false;
	}

	public boolean supportsMending(Physical item) {
		return canMend(null, item, true);
	}

	protected boolean canMend(MOB mob, Environmental E, boolean quiet) {
		if (!super.canMend(mob, E, quiet))
			return false;
		if ((!(E instanceof Item)) || (!mayICraft((Item) E))) {
			if (!quiet)
				commonTell(mob, "That's not a fletched item.");
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
					"Make what? Enter \"fletch list\" for a list, \"fletch scan\", \"fletch learn <item>\", \"fletch mend <item>\", or \"fletch stop\" to cancel.");
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
			int toggler = 1;
			int toggleTop = 2;
			StringBuffer buf = new StringBuffer("");
			int[] cols = {
					ListingLibrary.ColFixer.fixColWidth(27, mob.session()),
					ListingLibrary.ColFixer.fixColWidth(3, mob.session()),
					ListingLibrary.ColFixer.fixColWidth(5, mob.session()) };
			for (int r = 0; r < toggleTop; r++)
				buf.append((r > 0 ? " " : "")
						+ CMStrings.padRight("Item", cols[0]) + " "
						+ CMStrings.padRight("Lvl", cols[1]) + " "
						+ CMStrings.padRight("Wood", cols[2]));
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
			buf.append("\n\rSome items may require additional material.");
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
		} else {
			buildingI = null;
			activity = CraftingActivity.CRAFTING;
			messedUp = false;
			aborted = false;
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
						+ "'.  Try \"fletch list\" for a list.");
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

			if ((amount > woodRequired) && (woodRequired > 0))
				woodRequired = amount;
			String otherRequired = foundRecipe.get(RCP_EXTRAREQ);
			int[] pm = { RawMaterial.MATERIAL_WOODEN };
			int[][] data = fetchFoundResourceData(mob, woodRequired, "wood",
					pm, (otherRequired.length() > 0) ? 1 : 0, otherRequired,
					null, false, parsedVars.autoGenerate, enhancedTypes);
			if (data == null)
				return false;
			fixDataForComponents(data, componentsFoundList);
			woodRequired = data[0][FOUND_AMT];
			if (((data[1][FOUND_CODE] & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_METAL)
					|| ((data[1][FOUND_CODE] & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_MITHRIL)) {
				Item fire = null;
				for (int i = 0; i < mob.location().numItems(); i++) {
					Item I2 = mob.location().getItem(i);
					if ((I2 != null) && (I2.container() == null)
							&& (CMLib.flags().isOnFire(I2))) {
						fire = I2;
						break;
					}
				}
				if ((fire == null) || (!mob.location().isContent(fire))) {
					commonTell(mob, "You'll need to build a fire first.");
					return false;
				}
			}
			String spell = (foundRecipe.size() > RCP_SPELL) ? foundRecipe.get(
					RCP_SPELL).trim() : "";
			bundling = spell.equalsIgnoreCase("BUNDLE");
			if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
				return false;
			int hardness = RawMaterial.CODES.HARDNESS(data[0][FOUND_CODE]) - 3;
			int lostValue = parsedVars.autoGenerate > 0 ? 0 : CMLib.materials()
					.destroyResourcesValue(mob.location(), woodRequired,
							data[0][FOUND_CODE], data[1][FOUND_CODE], null)
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
					mob, CMath.s_int(foundRecipe.get(RCP_LEVEL)), 4);
			String itemName = replacePercent(foundRecipe.get(RCP_FINALNAME),
					RawMaterial.CODES.NAME(data[0][FOUND_CODE])).toLowerCase();
			itemName = CMLib.english().startWithAorAn(itemName);
			buildingI.setName(itemName);
			startStr = "<S-NAME> start(s) making " + buildingI.name() + ".";
			displayText = "You are making " + buildingI.name();
			verb = "making " + buildingI.name();
			playSound = "sanding.wav";
			buildingI.setDisplayText(itemName + " lies here");
			buildingI.setDescription(itemName + ". ");
			buildingI.basePhyStats().setWeight(
					getStandardWeight(woodRequired, bundling));
			buildingI.setBaseValue(CMath.s_int(foundRecipe.get(RCP_VALUE)));
			if ((woodRequired == 0) && (data[1][FOUND_CODE] > 0))
				buildingI.setMaterial(data[1][FOUND_CODE]);
			else
				buildingI.setMaterial(data[0][FOUND_CODE]);
			int level = CMath.s_int(foundRecipe.get(RCP_LEVEL));
			if (woodRequired == 0)
				hardness = 0;
			buildingI.basePhyStats().setLevel(level + hardness);
			buildingI.setSecretIdentity(getBrand(mob));
			String ammotype = foundRecipe.get(RCP_AMMOTYPE);
			int capacity = CMath.s_int(foundRecipe.get(RCP_AMOCAPACITY));
			int maxrange = CMath.s_int(foundRecipe.get(RCP_MAXRANGE));
			int armordmg = CMath.s_int(foundRecipe.get(RCP_ARMORDMG));
			if (bundling)
				buildingI.setBaseValue(lostValue);
			addSpells(buildingI, spell);
			if (buildingI instanceof Weapon) {
				if (buildingI instanceof AmmunitionWeapon) {
					if (ammotype.length() > 0) {
						((AmmunitionWeapon) buildingI)
								.setAmmoCapacity(capacity);
						((AmmunitionWeapon) buildingI).setAmmoRemaining(0);
						((AmmunitionWeapon) buildingI)
								.setAmmunitionType(ammotype);
					}
				}
				buildingI.basePhyStats().setAttackAdjustment(
						(abilityCode() - 1 + (hardness * 5)));
				buildingI.basePhyStats().setDamage(armordmg + hardness);
				((Weapon) buildingI).setRanges(((Weapon) buildingI).minRange(),
						maxrange);
			} else if ((ammotype.length() > 0)
					&& (buildingI instanceof Ammunition)) {
				((Ammunition) buildingI).setAmmunitionType(ammotype);
				buildingI.setUsesRemaining(capacity);
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
