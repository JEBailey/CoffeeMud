package com.planet_ink.coffee_mud.Behaviors;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.ClanGovernment;
import com.planet_ink.coffee_mud.Libraries.interfaces.AbilityMapper.AbilityMapping;
import com.planet_ink.coffee_mud.Libraries.interfaces.ExpertiseLibrary;
import com.planet_ink.coffee_mud.Libraries.interfaces.ExpertiseLibrary.ExpertiseDefinition;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.PhysicalAgent;
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
public class MOBTeacher extends CombatAbilities {
	public String ID() {
		return "MOBTeacher";
	}

	protected MOB myMOB = null;
	protected boolean teachEverything = true;
	protected boolean noCommon = false;
	protected boolean noExpertises = false; // doubles as a "done ticking" flag
	protected boolean noHLExpertises = false;
	protected int tickDownToKnowledge = 4;
	protected List<ExpertiseDefinition> trainableExpertises = null;

	public String accountForYourself() {
		return "skill teaching";
	}

	public void startBehavior(PhysicalAgent forMe) {
		if (forMe instanceof MOB)
			myMOB = (MOB) forMe;
		setParms(parms);
	}

	protected void setTheCharClass(MOB mob, CharClass C) {
		if ((mob.baseCharStats().numClasses() == 1)
				&& (mob.baseCharStats().getMyClass(0).ID()
						.equals("StdCharClass"))
				&& (!C.ID().equals("StdCharClass"))) {
			mob.baseCharStats().setMyClasses(C.ID());
			mob.baseCharStats().setMyLevels("" + mob.phyStats().level());
			mob.recoverCharStats();
			return;
		}
		for (int i = 0; i < mob.baseCharStats().numClasses(); i++) {
			CharClass C1 = mob.baseCharStats().getMyClass(i);
			if ((C1 != null) && (mob.baseCharStats().getClassLevel(C1) > 0))
				mob.baseCharStats().setClassLevel(C1, 1);
		}
		mob.baseCharStats().setCurrentClass(C);
		mob.baseCharStats().setClassLevel(C, mob.phyStats().level());
		mob.recoverCharStats();
	}

	protected void classAbles(MOB mob, Map<String, Ability> myAbles, int pct) {
		boolean stdCharClass = mob.charStats().getCurrentClass().ID()
				.equals("StdCharClass");
		String className = mob.charStats().getCurrentClass().ID();
		Ability A = null;
		for (Enumeration<Ability> a = CMClass.abilities(); a.hasMoreElements();) {
			A = a.nextElement();
			if ((((stdCharClass && (CMLib.ableMapper().lowestQualifyingLevel(
					A.ID()) > 0))) || (CMLib.ableMapper().qualifiesByLevel(mob,
					A) && (!CMLib.ableMapper().getSecretSkill(className, true,
					A.ID()))))
					&& ((!noCommon) || ((A.classificationCode() & Ability.ALL_ACODES) != Ability.ACODE_COMMON_SKILL))
					&& ((!stdCharClass) || (CMLib.ableMapper()
							.availableToTheme(A.ID(), Area.THEME_FANTASY, true))))
				addAbility(mob, A, pct, myAbles);
		}
		for (ClanGovernment G : CMLib.clans().getStockGovernments()) {
			G.getClanLevelAbilities(Integer.valueOf(Integer.MAX_VALUE));
			for (final Enumeration<AbilityMapping> m = CMLib.ableMapper()
					.getClassAbles(G.getName(), false); m.hasMoreElements();) {
				AbilityMapping M = m.nextElement();
				Ability A2 = CMClass.getAbility(M.abilityID);
				addAbility(mob, A2, pct, myAbles);
			}
		}
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((tickID == Tickable.TICKID_MOB)
				&& (!CMSecurity.isDisabled(CMSecurity.DisFlag.MOBTEACHER))
				&& ((--tickDownToKnowledge) == 0) && (ticking instanceof MOB)) {
			if (!noExpertises) {
				noExpertises = true;
				MOB mob = (MOB) ticking;
				if (teachEverything) {
					for (Enumeration<ExpertiseLibrary.ExpertiseDefinition> e = CMLib
							.expertises().definitions(); e.hasMoreElements();)
						mob.addExpertise(e.nextElement().ID);
					trainableExpertises = null;
				} else {
					boolean someNew = true;
					CharStats oldBase = (CharStats) mob.baseCharStats()
							.copyOf();
					for (int i : CharStats.CODES.BASE())
						mob.baseCharStats().setStat(i, 100);
					for (int i = 0; i < mob.baseCharStats().numClasses(); i++)
						mob.baseCharStats().setClassLevel(
								mob.baseCharStats().getMyClass(i), 100);
					mob.recoverCharStats();
					while (someNew) {
						someNew = false;
						List<ExpertiseDefinition> V = CMLib.expertises()
								.myQualifiedExpertises(mob);
						ExpertiseLibrary.ExpertiseDefinition def = null;
						for (int v = 0; v < V.size(); v++) {
							def = V.get(v);
							if (mob.fetchExpertise(def.ID) == null) {
								mob.addExpertise(def.ID);
								someNew = true;
							}
						}
						if (someNew)
							trainableExpertises = null;
					}
					mob.setBaseCharStats(oldBase);
					mob.recoverCharStats();
				}
			}

		}
		return super.tick(ticking, tickID);
	}

	public void addAbility(MOB mob, Ability A, int pct,
			Map<String, Ability> myAbles) {
		if (CMLib.dice().rollPercentage() <= pct) {
			Ability A2 = myAbles.get(A.ID());
			if (A2 == null) {
				A = (Ability) A.copyOf();
				A.setSavable(false);
				A.setProficiency(CMLib.ableMapper().getMaxProficiency(mob,
						true, A.ID()));
				myAbles.put(A.ID(), A);
				mob.addAbility(A);
			} else
				A2.setProficiency(CMLib.ableMapper().getMaxProficiency(mob,
						true, A2.ID()));
		}
	}

	protected void ensureCharClass() {
		myMOB.baseCharStats().setMyClasses("StdCharClass");
		myMOB.baseCharStats().setMyLevels("" + myMOB.phyStats().level());
		myMOB.recoverCharStats();

		Hashtable myAbles = new Hashtable();
		Ability A = null;
		for (Enumeration<Ability> a = myMOB.allAbilities(); a.hasMoreElements();) {
			A = a.nextElement();
			if (A != null)
				myAbles.put(A.ID(), A);
		}
		myMOB.baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 19);
		myMOB.baseCharStats().setStat(CharStats.STAT_WISDOM, 19);

		int pct = 100;
		Vector V = null;
		A = CMClass.getAbility(getParms());
		if (A != null) {
			addAbility(myMOB, A, pct, myAbles);
			teachEverything = false;
		} else
			V = CMParms.parse(getParms());

		if (V != null)
			for (int v = V.size() - 1; v >= 0; v--) {
				String s = (String) V.elementAt(v);
				if (s.equalsIgnoreCase("NOCOMMON")) {
					noCommon = true;
					V.removeElementAt(v);
				}
				if (s.equalsIgnoreCase("NOEXPS") || s.equalsIgnoreCase("NOEXP")) {
					noExpertises = true;
					V.removeElementAt(v);
				}
				if (s.equalsIgnoreCase("NOHLEXPS")
						|| s.equalsIgnoreCase("NOHLEXP")) {
					noHLExpertises = true;
					V.removeElementAt(v);
				}
			}

		if (V != null)
			for (int v = 0; v < V.size(); v++) {
				String s = (String) V.elementAt(v);
				if (s.endsWith("%")) {
					pct = CMath.s_int(s.substring(0, s.length() - 1));
					continue;
				}

				A = CMClass.getAbility(s);
				CharClass C = CMClass.findCharClass(s);
				if ((C != null) && (C.availabilityCode() != 0)) {
					teachEverything = false;
					setTheCharClass(myMOB, C);
					classAbles(myMOB, myAbles, pct);
					myMOB.recoverCharStats();
				} else if (A != null) {
					addAbility(myMOB, A, pct, myAbles);
					teachEverything = false;
				} else {
					ExpertiseLibrary.ExpertiseDefinition def = CMLib
							.expertises().getDefinition(s);
					if (def != null) {
						myMOB.addExpertise(def.ID);
						teachEverything = false;
					}
				}
			}
		myMOB.recoverCharStats();
		if ((myMOB.charStats().getCurrentClass().ID().equals("StdCharClass"))
				&& (teachEverything))
			classAbles(myMOB, myAbles, pct);
		int lvl = myMOB.phyStats().level() / myMOB.baseCharStats().numClasses();
		if (lvl < 1)
			lvl = 1;
		for (int i = 0; i < myMOB.baseCharStats().numClasses(); i++) {
			CharClass C = myMOB.baseCharStats().getMyClass(i);
			if ((C != null) && (myMOB.baseCharStats().getClassLevel(C) >= 0))
				myMOB.baseCharStats().setClassLevel(C, lvl);
		}
		myMOB.recoverCharStats();
	}

	public void setParms(String newParms) {
		super.setParms(newParms);
		if (myMOB == null)
			return;
		teachEverything = true;
		noCommon = false;
		noExpertises = false;
		tickDownToKnowledge = 4;
		trainableExpertises = null;
		ensureCharClass();
	}

	public boolean okMessage(Environmental host, CMMsg msg) {
		if (host instanceof MOB) {
			if (CMath.bset(((MOB) host).getBitmap(), MOB.ATT_NOTEACH))
				((MOB) host).setBitmap(CMath.unsetb(((MOB) host).getBitmap(),
						MOB.ATT_NOTEACH));
		}
		return super.okMessage(host, msg);
	}

	public void executeMsg(Environmental affecting, CMMsg msg) {
		if (myMOB == null)
			return;
		super.executeMsg(affecting, msg);
		if (!canFreelyBehaveNormal(affecting))
			return;
		MOB monster = myMOB;
		MOB student = msg.source();

		if ((!msg.amISource(monster)) && (!student.isMonster())
				&& (msg.sourceMessage() != null)
				&& ((msg.target() == null) || msg.amITarget(monster))
				&& (msg.targetMinor() == CMMsg.TYP_SPEAK)
				&& (!CMSecurity.isDisabled(CMSecurity.DisFlag.MOBTEACHER))) {
			String sayMsg = CMStrings.getSayFromMessage(msg.sourceMessage());
			if (sayMsg == null) {
				int start = msg.sourceMessage().indexOf('\'');
				if (start > 0)
					sayMsg = msg.sourceMessage().substring(start + 1);
				else
					sayMsg = msg.sourceMessage();
			}
			int x = sayMsg.toUpperCase().indexOf("TEACH");
			if (x < 0)
				x = sayMsg.toUpperCase().indexOf("GAIN ");
			if (x >= 0) {
				boolean giveABonus = false;
				String s = sayMsg.substring(x + 5).trim();
				x = s.lastIndexOf("\'");
				if (x > 0)
					s = s.substring(0, x);
				else {
					x = s.lastIndexOf('`');
					if (x > 0)
						s = s.substring(0, x);
				}

				if (s.startsWith("\""))
					s = s.substring(1).trim();
				if (s.endsWith("\""))
					s = s.substring(0, s.length() - 1);
				if (s.toUpperCase().endsWith("PLEASE"))
					s = s.substring(0, s.length() - 6).trim();
				if (s.startsWith("\""))
					s = s.substring(1).trim();
				if (s.endsWith("\""))
					s = s.substring(0, s.length() - 1);
				if (s.toUpperCase().startsWith("PLEASE ")) {
					giveABonus = true;
					s = s.substring(6).trim();
				}
				if (s.startsWith("\""))
					s = s.substring(1).trim();
				if (s.endsWith("\""))
					s = s.substring(0, s.length() - 1);
				if (s.toUpperCase().startsWith("ME "))
					s = s.substring(3).trim();
				if (s.startsWith("\""))
					s = s.substring(1).trim();
				if (s.endsWith("\""))
					s = s.substring(0, s.length() - 1);
				if (s.toUpperCase().startsWith("PLEASE ")) {
					giveABonus = true;
					s = s.substring(6).trim();
				}
				if (s.toUpperCase().startsWith("ME "))
					s = s.substring(3).trim();
				if (s.startsWith("\""))
					s = s.substring(1).trim();
				if (s.endsWith("\""))
					s = s.substring(0, s.length() - 1);
				if (s.trim().equalsIgnoreCase("LIST")) {
					CMLib.commands().postSay(monster, student,
							"Try the QUALIFY command.", true, false);
					return;
				}
				if (s.trim().toUpperCase().equals("ALL")) {
					CMLib.commands()
							.postSay(
									monster,
									student,
									"I can't teach you everything at once. Try the QUALIFY command.",
									true, false);
					return;
				}
				Ability myAbility = CMClass.findAbility(s.trim().toUpperCase(),
						monster);
				if (myAbility == null) {
					ExpertiseLibrary.ExpertiseDefinition theExpertise = null;
					if (trainableExpertises == null) {
						trainableExpertises = new LinkedList<ExpertiseLibrary.ExpertiseDefinition>();
						trainableExpertises.addAll(CMLib.expertises()
								.myListableExpertises(monster));
						for (Enumeration<String> exi = monster.expertises(); exi
								.hasMoreElements();) {
							Entry<String, Integer> EXI = monster
									.fetchExpertise(exi.nextElement());
							if (EXI.getValue() == null) {
								ExpertiseLibrary.ExpertiseDefinition def = CMLib
										.expertises().getDefinition(
												EXI.getKey());
								if ((def != null)
										&& (!trainableExpertises.contains(def)))
									trainableExpertises.add(def);
							} else {
								List<String> childrenIDs = CMLib.expertises()
										.getStageCodes(EXI.getKey());
								for (String experID : childrenIDs) {
									ExpertiseLibrary.ExpertiseDefinition def = CMLib
											.expertises()
											.getDefinition(experID);
									if ((def != null)
											&& (!trainableExpertises
													.contains(def)))
										trainableExpertises.add(def);
								}
							}
						}
					}
					for (ExpertiseLibrary.ExpertiseDefinition def : trainableExpertises) {
						if ((def.name.equalsIgnoreCase(s))
								&& (theExpertise == null))
							theExpertise = def;
					}
					if (theExpertise == null)
						for (ExpertiseLibrary.ExpertiseDefinition def : trainableExpertises) {
							if ((CMLib.english().containsString(def.name, s) && (theExpertise == null)))
								theExpertise = def;
						}
					if (theExpertise != null) {
						if (!CMLib.expertises().postTeach(monster, student,
								theExpertise))
							return;
					} else if ((CMClass.findCharClass(s.trim()) != null))
						CMLib.commands()
								.postSay(
										monster,
										student,
										"I've heard of "
												+ s
												+ ", but that's an class-- try TRAINing  for it.",
										true, false);
					else {
						for (Enumeration e = CMLib.expertises().definitions(); e
								.hasMoreElements();) {
							ExpertiseLibrary.ExpertiseDefinition def = (ExpertiseLibrary.ExpertiseDefinition) e
									.nextElement();
							if (def.name.equalsIgnoreCase(s)) {
								theExpertise = def;
								break;
							}
						}
						if (theExpertise == null)
							CMLib.commands().postSay(monster, student,
									"I'm sorry, but I've never heard of " + s,
									true, false);
						else
							CMLib.commands().postSay(
									monster,
									student,
									"I'm sorry, but I do not know "
											+ theExpertise.name + ".");
					}
					return;
				}
				if (giveABonus) {
					monster.baseCharStats().setStat(
							CharStats.STAT_INTELLIGENCE, 25);
					monster.baseCharStats().setStat(CharStats.STAT_WISDOM, 25);
					monster.recoverCharStats();
				}

				int prof75 = (int) Math
						.round(CMath.mul(
								CMLib.ableMapper().getMaxProficiency(student,
										true, myAbility.ID()), 0.75));
				myAbility.setProficiency(prof75 / 2);
				CMLib.expertises().postTeach(monster, student, myAbility);
				monster.baseCharStats()
						.setStat(CharStats.STAT_INTELLIGENCE, 19);
				monster.baseCharStats().setStat(CharStats.STAT_WISDOM, 19);
				monster.recoverCharStats();
			}
		}

	}
}