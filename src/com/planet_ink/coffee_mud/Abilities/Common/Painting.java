package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Common.interfaces.Session.InputCallback;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class Painting extends CommonSkill {
	public String ID() {
		return "Painting";
	}

	public String name() {
		return "Painting";
	}

	private static final String[] triggerStrings = { "PAINT", "PAINTING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_ARTISTIC;
	}

	protected Item building = null;
	protected boolean messedUp = false;

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected != null) && (affected instanceof MOB)
				&& (tickID == Tickable.TICKID_MOB)) {
			if (building == null)
				unInvoke();
		}
		return super.tick(ticking, tickID);
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB)) {
				MOB mob = (MOB) affected;
				if ((building != null) && (!aborted)) {
					if (messedUp)
						commonTell(mob, "<S-NAME> mess(es) up painting "
								+ building.name() + ".");
					else
						mob.location().addItem(building,
								ItemPossessor.Expire.Player_Drop);
				}
				building = null;
			}
		}
		super.unInvoke();
	}

	public boolean invoke(final MOB mob, Vector commands, Physical givenTarget,
			final boolean auto, final int asLevel) {
		final Vector originalCommands = (Vector) commands.clone();
		if (super.checkStop(mob, commands))
			return true;
		if (commands.size() == 0) {
			commonTell(mob,
					"Paint on what? Enter \"paint [canvas name]\" or paint \"wall\".");
			return false;
		}
		String paintingKeyWords = null;
		String paintingDesc = null;
		while ((commands.size() > 1)
				&& (commands.lastElement() instanceof String)) {
			String last = ((String) commands.lastElement());
			if (last.startsWith("PAINTINGKEYWORDS=")) {
				paintingKeyWords = last.substring(17).trim();
				if (paintingKeyWords.length() > 0)
					commands.remove(commands.size() - 1);
				else
					paintingKeyWords = null;
			} else if (last.startsWith("PAINTINGDESC=")) {
				paintingDesc = last.substring(13).trim();
				if (paintingDesc.length() > 0)
					commands.remove(commands.size() - 1);
				else
					paintingDesc = null;
			} else
				break;
		}

		String str = CMParms.combine(commands, 0);
		building = null;
		messedUp = false;
		Session S = mob.session();
		if ((S == null) && (mob.amFollowing() != null))
			S = mob.amFollowing().session();
		if (S == null) {
			commonTell(mob, "I can't work! I need a player to follow!");
			return false;
		}

		Item canvasI = null;
		if (str.equalsIgnoreCase("wall")) {
			if (!CMLib.law().doesOwnThisProperty(mob, mob.location())) {
				commonTell(mob,
						"You need the owners permission to paint the walls here.");
				return false;
			}
		} else {
			canvasI = mob.location().findItem(null, str);
			if ((canvasI == null) || (!CMLib.flags().canBeSeenBy(canvasI, mob))) {
				commonTell(mob, "You don't see any canvases called '" + str
						+ "' sitting here.");
				return false;
			}
			if ((canvasI.material() != RawMaterial.RESOURCE_COTTON)
					&& (canvasI.material() != RawMaterial.RESOURCE_SILK)
					&& (!canvasI.Name().toUpperCase().endsWith("CANVAS"))
					&& (!canvasI.Name().toUpperCase().endsWith("SILKSCREEN"))) {
				commonTell(mob, "You cannot paint on '" + str + "'.");
				return false;
			}
		}

		int duration = 25;
		final Session session = mob.session();
		final Ability me = this;
		final Physical target = givenTarget;
		if (str.equalsIgnoreCase("wall")) {
			if ((paintingKeyWords != null) && (paintingDesc != null)) {
				building = CMClass.getItem("GenWallpaper");
				building.setName(paintingKeyWords);
				building.setDescription(paintingDesc);
				building.setSecretIdentity(getBrand(mob));
			} else {
				session.prompt(new InputCallback(InputCallback.Type.PROMPT, "",
						0) {
					@Override
					public void showPrompt() {
						session.promptPrint("Enter the key words (not the description) for this work.\n\r: ");
					}

					@Override
					public void timedOut() {
					}

					@Override
					public void callBack() {
						final String name = input.trim();
						if (name.length() == 0)
							return;
						Vector<String> V = CMParms.parse(name.toUpperCase());
						for (int v = 0; v < V.size(); v++) {
							String vstr = " " + (V.elementAt(v)) + " ";
							for (int i = 0; i < mob.location().numItems(); i++) {
								Item I = mob.location().getItem(i);
								if ((I != null)
										&& (I.displayText().length() == 0)
										&& (!CMLib.flags().isGettable(I))
										&& ((" " + I.name().toUpperCase() + " ")
												.indexOf(vstr) >= 0)) {
									final Item dupI = I;
									final String dupWord = vstr.trim()
											.toLowerCase();
									session.prompt(new InputCallback(
											InputCallback.Type.CONFIRM, "N", 0) {
										@Override
										public void showPrompt() {
											session.promptPrint("\n\r'"
													+ dupI.name()
													+ "' already shares one of these key words ('"
													+ dupWord
													+ "').  Would you like to destroy it (y/N)? ");
										}

										@Override
										public void timedOut() {
										}

										@Override
										public void callBack() {
											if (this.input.equals("Y")) {
												dupI.destroy();
											}
										}
									});
									return;
								}
							}
						}
						session.prompt(new InputCallback(
								InputCallback.Type.PROMPT, "", 0) {
							@Override
							public void showPrompt() {
								session.promptPrint("\n\rEnter a description for this.\n\r:");
							}

							@Override
							public void timedOut() {
							}

							@Override
							public void callBack() {
								final String desc = this.input.trim();
								if (desc.length() == 0)
									return;
								session.prompt(new InputCallback(
										InputCallback.Type.CONFIRM, "N", 0) {
									@Override
									public void showPrompt() {
										session.promptPrint("Wall art key words: '"
												+ name
												+ "', description: '"
												+ desc + "'.  Correct (Y/n)? ");
									}

									@Override
									public void timedOut() {
									}

									@Override
									public void callBack() {
										if (this.input.equals("Y")) {
											@SuppressWarnings("unchecked")
											Vector<String> newCommands = (Vector<String>) originalCommands
													.clone();
											newCommands.add("PAINTINGKEYWORDS="
													+ name);
											newCommands.add("PAINTINGDESC="
													+ desc);
											me.invoke(mob, newCommands, target,
													auto, asLevel);
										}
									}
								});
							}
						});
					}
				});
				return true;
			}
		} else if (canvasI != null) {
			if ((paintingKeyWords != null) && (paintingDesc != null)) {
				building = CMClass.getItem("GenItem");
				building.setName("a painting of " + paintingKeyWords);
				building.setDisplayText("a painting of " + paintingKeyWords
						+ " is here.");
				building.setDescription(paintingDesc);
				building.basePhyStats().setWeight(
						canvasI.basePhyStats().weight());
				building.setBaseValue(canvasI.baseGoldValue()
						* (CMLib.dice().roll(1, 5, 0)));
				building.setMaterial(canvasI.material());
				building.basePhyStats()
						.setLevel(canvasI.basePhyStats().level());
				building.setSecretIdentity(getBrand(mob));
				canvasI.destroy();
			} else {
				session.prompt(new InputCallback(InputCallback.Type.PROMPT, "",
						0) {
					@Override
					public void showPrompt() {
						session.promptPrint("\n\rIn brief, what is this a painting of?\n\r: ");
					}

					@Override
					public void timedOut() {
					}

					@Override
					public void callBack() {
						final String name = this.input.trim();
						if (name.length() == 0)
							return;
						session.prompt(new InputCallback(
								InputCallback.Type.PROMPT, "", 0) {
							@Override
							public void showPrompt() {
								session.promptPrint("\n\rPlease describe this painting.\n\r: ");
							}

							@Override
							public void timedOut() {
							}

							@Override
							public void callBack() {
								final String desc = this.input.trim();
								if (desc.length() == 0)
									return;
								@SuppressWarnings("unchecked")
								Vector<String> newCommands = (Vector<String>) originalCommands
										.clone();
								newCommands.add("PAINTINGKEYWORDS=" + name);
								newCommands.add("PAINTINGDESC=" + desc);
								me.invoke(mob, newCommands, target, auto,
										asLevel);
							}
						});
					}
				});
				return true;
			}
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel)) {
			building.destroy();
			building = null;
			return false;
		}

		String startStr = "<S-NAME> start(s) painting " + building.name() + ".";
		displayText = "You are painting " + building.name();
		verb = "painting " + building.name();
		building.recoverPhyStats();
		building.text();
		building.recoverPhyStats();

		messedUp = !proficiencyCheck(mob, 0, auto);
		duration = getDuration(25, mob, 1, 2);

		CMMsg msg = CMClass.getMsg(mob, building, this,
				getActivityMessageType(), startStr);
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			building = (Item) msg.target();
			beneficialAffect(mob, mob, asLevel, duration);
		}
		return true;
	}
}
