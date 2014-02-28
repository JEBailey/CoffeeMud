package com.planet_ink.coffee_mud.Abilities.Skills;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Common.interfaces.Social;
import com.planet_ink.coffee_mud.Items.interfaces.Food;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Libraries.interfaces.SlaveryLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
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

@SuppressWarnings("rawtypes")
public class Skill_Enslave extends StdSkill {
	public String ID() {
		return "Skill_Enslave";
	}

	public String name() {
		return "Enslave";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "ENSLAVE" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String displayText() {
		return "(Enslaved)";
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_CRIMINAL;
	}

	protected String masterName = "";
	protected String oldLeige = "";
	protected List<Pair<Clan, Integer>> oldClans = null;
	protected MOB masterMOB = null;
	protected SlaveryLibrary.geasSteps STEPS = null;
	protected int masterAnger = 0;
	protected int speedDown = 0;
	protected final static int HUNGERTICKMAX = 4;
	protected final static int SPEEDMAX = 2;
	protected int hungerTickDown = HUNGERTICKMAX;
	protected Room lastRoom = null;

	public void setMiscText(String txt) {
		masterMOB = null;
		masterName = txt;
		super.setMiscText(txt);
	}

	public MOB getMaster() {
		if (masterMOB == null) {
			masterMOB = CMLib.players().getLoadPlayer(masterName);
			if (masterMOB != null) {
				oldLeige = masterMOB.getLiegeID();
				oldClans = new Vector<Pair<Clan, Integer>>();
				for (Pair<Clan, Integer> p : masterMOB.clans())
					oldClans.add(p);
			}
		}
		return masterMOB;
	}

	public void unMaster(MOB mob) {
		if ((masterMOB != null) && (mob != null)) {
			mob.setLiegeID(oldLeige);
			mob.setClan("", Integer.MIN_VALUE);
			for (Pair<Clan, Integer> p : oldClans)
				mob.setClan(p.first.clanID(), p.second.intValue());
		}
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		if ((msg.amITarget(mob))
				&& (msg.tool() instanceof Social)
				&& (msg.tool().Name().equals("WHIP <T-NAME>") || msg.tool()
						.Name().equals("BEAT <T-NAME>")))
			speedDown = SPEEDMAX;
		else if (msg.amITarget(mob) && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& ((msg.value()) > 0)) {
			masterAnger += 10;
			CMLib.combat().postPanic(mob, msg);
		} else if ((msg.sourceMinor() == CMMsg.TYP_SPEAK)
				&& (msg.sourceMessage() != null)
				&& (msg.sourceMessage().length() > 0)) {
			if (STEPS != null) {
				if ((msg.target() == null) || (msg.target() instanceof MOB)) {
					String response = CMStrings.getSayFromMessage(msg
							.sourceMessage());
					if (response != null) {
						if ((msg.target() == mob)
								&& (msg.source().Name()
										.equals(mob.getLiegeID()))) {
							Vector<String> V = CMParms.parse(response
									.toUpperCase());
							if (V.contains("STOP") || V.contains("CANCEL")) {
								CMLib.commands().postSay(mob, msg.source(),
										"Yes master.", false, false);
								STEPS = null;
								return;
							}
						}
						STEPS.sayResponse(msg.source(), (MOB) msg.target(),
								response);
					}
				}
			} else if ((msg.amITarget(mob)) && (mob.getLiegeID().length() > 0)) {
				if ((msg.tool() == null)
						|| ((msg.tool() instanceof Ability)
								&& ((((Ability) msg.tool())
										.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_LANGUAGE) && (mob
								.fetchAbility(msg.tool().ID()) != null))) {
					if (!msg.source().Name().equals(mob.getLiegeID())) {
						String response = CMStrings.getSayFromMessage(msg
								.sourceMessage());
						if (response != null) {
							if ((response.toUpperCase()
									.startsWith("I COMMAND YOU TO "))
									|| (response.toUpperCase()
											.startsWith("I ORDER YOU TO ")))
								CMLib.commands().postSay(mob, msg.source(),
										"I don't take orders from you. ",
										false, false);
						}
					} else {
						String response = CMStrings.getSayFromMessage(msg
								.sourceMessage());
						if (response != null) {
							if (response.toUpperCase().startsWith(
									"I COMMAND YOU TO "))
								response = response
										.substring(("I COMMAND YOU TO ")
												.length());
							else if (response.toUpperCase().startsWith(
									"I ORDER YOU TO "))
								response = response
										.substring(("I ORDER YOU TO ").length());
							else {
								CMLib.commands()
										.postSay(
												mob,
												msg.source(),
												"Master, please begin your instruction with the words 'I command you to '.  You can also tell me to 'stop' or 'cancel' any order you give.",
												false, false);
								return;
							}
							STEPS = CMLib.slavery().processRequest(
									msg.source(), mob, response);
							if ((STEPS != null) && (STEPS.size() > 0))
								CMLib.commands().postSay(mob, msg.source(),
										"Yes master.", false, false);
							else {
								STEPS = null;
								CMLib.commands().postSay(mob, msg.source(),
										"Huh? Wuh?", false, false);
							}
						}
					}
				} else if ((msg.tool() instanceof Ability)
						&& ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_LANGUAGE))
					CMLib.commands().postSay(mob, msg.source(),
							"I don't understand your words.", false, false);
			}
		} else if ((mob.location() != null) && (getMaster() != null)) {
			Room room = mob.location();
			if ((room != lastRoom)
					&& (CMLib.law().doesHavePriviledgesHere(getMaster(), room))
					&& (room.isInhabitant(mob))) {
				lastRoom = room;
				mob.basePhyStats().setRejuv(PhyStats.NO_REJUV);
				mob.setStartRoom(room);
			}
		} else if ((msg.sourceMinor() == CMMsg.TYP_SHUTDOWN)
				|| ((msg.targetMinor() == CMMsg.TYP_EXPIRE) && ((msg.target() == mob
						.location()) || (msg.target() == mob) || (msg.target() == mob
						.amFollowing())))
				|| ((msg.sourceMinor() == CMMsg.TYP_QUIT) && (msg.amISource(mob
						.amFollowing()))))
			mob.setFollowing(null);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!(affected instanceof MOB))
			return super.tick(ticking, tickID);
		if (tickID == Tickable.TICKID_MOB) {
			MOB mob = (MOB) ticking;
			if ((speedDown > -500) && ((--speedDown) >= 0)) {
				for (int a = mob.numEffects() - 1; a >= 0; a--) // personal
				{
					Ability A = mob.fetchEffect(a);
					if ((A != null)
							&& ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_COMMON_SKILL))
						if (!A.tick(ticking, tickID))
							mob.delEffect(A);
				}
			}
			if ((--hungerTickDown) <= 0) {
				hungerTickDown = HUNGERTICKMAX;
				mob.curState().expendEnergy(mob, mob.maxState(), false);
				if ((!mob.isInCombat()) && (CMLib.dice().rollPercentage() == 1)
						&& (CMLib.dice().rollPercentage() < (masterAnger / 10))) {
					MOB myMaster = getMaster();
					if ((myMaster != null)
							&& (mob.location().isInhabitant(myMaster))) {
						mob.location().show(mob, myMaster, null,
								CMMsg.MSG_OK_ACTION,
								"<S-NAME> rebel(s) against <T-NAMESELF>!");
						MOB master = getMaster();
						unMaster(mob);
						setMiscText("");
						mob.recoverCharStats();
						mob.recoverPhyStats();
						mob.resetToMaxState();
						mob.setFollowing(null);
						CMLib.combat().postAttack(mob, master,
								mob.fetchWieldedItem());
					} else if (CMLib.dice().rollPercentage() < 50) {
						mob.location().show(mob, myMaster, null,
								CMMsg.MSG_OK_ACTION,
								"<S-NAME> escape(s) <T-NAMESELF>!");
						CMLib.tracking().beMobile(mob, true, true, false,
								false, null, null);
					}
				}
				if (mob.curState().getHunger() <= 0) {
					Food f = null;
					for (int i = 0; i < mob.numItems(); i++) {
						Item I = mob.getItem(i);
						if (I instanceof Food) {
							f = (Food) I;
							break;
						}
					}
					if (f == null)
						CMLib.commands().postSay(mob, null, "I am hungry.",
								false, false);
					else {
						Command C = CMClass.getCommand("Eat");
						try {
							C.execute(mob,
									CMParms.parse("EAT \"" + f.Name() + "$\""),
									Command.METAFLAG_ORDER);
						} catch (Exception e) {
						}
					}
				}
				if (mob.curState().getThirst() <= 0) {
					Drink d = null;
					for (int i = 0; i < mob.numItems(); i++) {
						Item I = mob.getItem(i);
						if (I instanceof Drink) {
							d = (Drink) I;
							break;
						}
					}
					if (d == null)
						CMLib.commands().postSay(mob, null, "I am thirsty.",
								false, false);
					else {
						Command C = CMClass.getCommand("Drink");
						try {
							C.execute(mob, CMParms.parse("DRINK \"" + d.Name()
									+ "$\""), Command.METAFLAG_ORDER);
						} catch (Exception e) {
						}
					}
				}
			}
			if (!mob.getLiegeID().equals(masterName)) {
				mob.setLiegeID(masterName);
				MOB myMaster = getMaster();
				if (myMaster != null) {
					for (Pair<Clan, Integer> p : CMLib.clans()
							.findRivalrousClans(myMaster))
						mob.setClan(p.first.clanID(), p.first.getGovernment()
								.getAcceptPos());
				}
			}
			if ((STEPS == null) || (STEPS.size() == 0) || (STEPS.done)) {
				if (mob.isInCombat())
					return true; // let them finish fighting.
				if ((STEPS != null) && ((STEPS.size() == 0) || (STEPS.done)))
					mob.tell("You have completed your masters task.");
				else
					mob.tell("You have been released from your masters task.");
				if ((mob.isMonster()) && (!mob.amDead())
						&& (mob.location() != null)
						&& (mob.location() != mob.getStartRoom()))
					CMLib.tracking().wanderAway(mob, true, true);
				unInvoke();
				STEPS = null;
				return !canBeUninvoked();
			}
			if (STEPS != null) {
				STEPS.step();
			}
		}
		return super.tick(ticking, tickID);
	}

	public void unInvoke() {
		MOB mob = null;
		if (affected instanceof MOB)
			mob = (MOB) affected;
		super.unInvoke();
		if (this.masterMOB != null)
			unMaster(mob);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (mob.isMonster()) {
			mob.location()
					.show(mob, null, CMMsg.MSG_NOISE, "<S-NAME> sigh(s).");
			CMLib.commands()
					.postSay(
							mob,
							null,
							"You know, if I had any ambitions, I would enslave myself so I could do interesting things!",
							false, false);
			return false;
		}

		if (commands.size() < 1) {
			mob.tell("You need to specify a target to enslave.");
			return false;
		}
		MOB target = getTarget(mob, commands, givenTarget, false, true);
		if (target == null)
			return false;
		if (target.charStats().getStat(CharStats.STAT_INTELLIGENCE) < 5) {
			mob.tell(target.name(mob)
					+ " would be too stupid to understand your instructions!");
			return false;
		}

		if ((!CMLib.flags().isBoundOrHeld(target))
				&& (target.fetchEffect(ID()) == null)
				&& (!CMSecurity.isAllowed(mob, target.location(),
						CMSecurity.SecFlag.CMDMOBS))) {
			mob.tell(target.name(mob) + " must be bound first.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			invoker = mob;
			boolean peace1 = !mob.isInCombat();
			boolean peace2 = !target.isInCombat();
			CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_NOISE
					| CMMsg.MASK_MALICIOUS, auto ? ""
					: "^S<S-NAME> enslave(s) <T-NAMESELF>!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (peace2)
					target.makePeace();
				if (peace1)
					mob.makePeace();
				Ability A = target.fetchEffect(ID());
				if (A == null) {
					A = (Ability) copyOf();
					target.addNonUninvokableEffect(A);
				}
				A.setMiscText(mob.Name());
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> attempt(s) to enslave on <T-NAMESELF>, but fails.");

		// return whether it worked
		return success;
	}
}
