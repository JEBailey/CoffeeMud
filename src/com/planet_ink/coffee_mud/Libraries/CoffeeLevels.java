package com.planet_ink.coffee_mud.Libraries;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.CoffeeTableRow;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Libraries.interfaces.ChannelsLibrary;
import com.planet_ink.coffee_mud.Libraries.interfaces.ExpLevelLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.collections.XVector;

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
public class CoffeeLevels extends StdLibrary implements ExpLevelLibrary {
	public String ID() {
		return "CoffeeLevels";
	}

	public int getManaBonusNextLevel(MOB mob) {
		CharClass charClass = mob.baseCharStats().getCurrentClass();
		double[] variables = {
				mob.phyStats().level(),
				mob.charStats().getStat(CharStats.STAT_WISDOM),
				CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)
						+ mob.charStats().getStat(
								CharStats.CODES
										.toMAXBASE(CharStats.STAT_WISDOM)),
				mob.charStats().getStat(CharStats.STAT_INTELLIGENCE),
				CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)
						+ mob.charStats()
								.getStat(
										CharStats.CODES
												.toMAXBASE(CharStats.STAT_INTELLIGENCE)),
				mob.charStats().getStat(charClass.getAttackAttribute()),
				CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)
						+ mob.charStats().getStat(
								CharStats.CODES.toMAXBASE(charClass
										.getAttackAttribute())),
				mob.charStats().getStat(CharStats.STAT_CHARISMA),
				mob.charStats().getStat(CharStats.STAT_CONSTITUTION) };
		return (int) Math.round(CMath.parseMathExpression(
				charClass.getManaFormula(), variables));
	}

	public int getLevelMana(MOB mob) {
		return CMProps.getIntVar(CMProps.Int.STARTMANA)
				+ ((mob.basePhyStats().level() - 1) * getManaBonusNextLevel(mob));
	}

	public int getAttackBonusNextLevel(MOB mob) {
		CharClass charClass = mob.baseCharStats().getCurrentClass();
		int rawAttStat = mob.charStats()
				.getStat(charClass.getAttackAttribute());
		int attStat = rawAttStat;
		int maxAttStat = (CMProps.getIntVar(CMProps.Int.BASEMAXSTAT) + mob
				.charStats().getStat(
						CharStats.CODES.toMAXBASE(charClass
								.getAttackAttribute())));
		if (attStat >= maxAttStat)
			attStat = maxAttStat;
		int attGain = (int) Math.floor(CMath.div(attStat, 18.0))
				+ charClass.getBonusAttackLevel();
		if (attStat >= 25)
			attGain += 2;
		else if (attStat >= 22)
			attGain += 1;
		return attGain;
	}

	public int getLevelAttack(MOB mob) {
		return ((mob.basePhyStats().level() - 1) * getAttackBonusNextLevel(mob))
				+ mob.basePhyStats().level();
	}

	public int getLevelMOBArmor(MOB mob) {
		return 100 - (int) Math.round(CMath
				.mul(mob.basePhyStats().level(), 3.0));
	}

	public int getLevelMOBDamage(MOB mob) {
		return (mob.basePhyStats().level());
	}

	public double getLevelMOBSpeed(MOB mob) {
		return 1.0 + Math.floor(CMath.div(mob.basePhyStats().level(), 30.0));
	}

	public int getMoveBonusNextLevel(MOB mob) {
		CharClass charClass = mob.baseCharStats().getCurrentClass();
		double[] variables = {
				mob.phyStats().level(),
				mob.charStats().getStat(CharStats.STAT_STRENGTH),
				CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)
						+ mob.charStats().getStat(
								CharStats.CODES
										.toMAXBASE(CharStats.STAT_STRENGTH)),
				mob.charStats().getStat(CharStats.STAT_DEXTERITY),
				CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)
						+ mob.charStats().getStat(
								CharStats.CODES
										.toMAXBASE(CharStats.STAT_DEXTERITY)),
				mob.charStats().getStat(CharStats.STAT_CONSTITUTION),
				CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)
						+ mob.charStats()
								.getStat(
										CharStats.CODES
												.toMAXBASE(CharStats.STAT_CONSTITUTION)),
				mob.charStats().getStat(CharStats.STAT_WISDOM),
				mob.charStats().getStat(CharStats.STAT_INTELLIGENCE) };
		return (int) Math.round(CMath.parseMathExpression(
				charClass.getMovementFormula(), variables));
	}

	public int getLevelMove(MOB mob) {
		int move = CMProps.getIntVar(CMProps.Int.STARTMOVE);
		if (mob.basePhyStats().level() > 1)
			move += (mob.basePhyStats().level() - 1)
					* getMoveBonusNextLevel(mob);
		return move;
	}

	public int getPlayerHPBonusNextLevel(MOB mob) {
		CharClass charClass = mob.baseCharStats().getCurrentClass();
		double[] variables = {
				mob.phyStats().level(),
				mob.charStats().getStat(CharStats.STAT_STRENGTH),
				CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)
						+ mob.charStats().getStat(
								CharStats.CODES
										.toMAXBASE(CharStats.STAT_STRENGTH)),
				mob.charStats().getStat(CharStats.STAT_DEXTERITY),
				CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)
						+ mob.charStats().getStat(
								CharStats.CODES
										.toMAXBASE(CharStats.STAT_DEXTERITY)),
				mob.charStats().getStat(CharStats.STAT_CONSTITUTION),
				CMProps.getIntVar(CMProps.Int.BASEMAXSTAT)
						+ mob.charStats()
								.getStat(
										CharStats.CODES
												.toMAXBASE(CharStats.STAT_CONSTITUTION)),
				mob.charStats().getStat(CharStats.STAT_WISDOM),
				mob.charStats().getStat(CharStats.STAT_INTELLIGENCE) };
		int newHitPointGain = (int) Math.round(CMath.parseMathExpression(
				charClass.getHitPointsFormula(), variables));
		if (newHitPointGain <= 0) {
			if (mob.charStats().getStat(CharStats.STAT_CONSTITUTION) >= 1)
				return 1;
			return 0;
		}
		return newHitPointGain;
	}

	public int getPlayerHitPoints(MOB mob) {
		int hp = CMProps.getIntVar(CMProps.Int.STARTHP);
		return hp
				+ ((mob.phyStats().level() - 1) * getPlayerHPBonusNextLevel(mob));
	}

	public MOB fillOutMOB(CharClass C, int level) {
		MOB mob = CMClass.getFactoryMOB();
		mob.baseCharStats().setCurrentClass(C);
		mob.charStats().setCurrentClass(C);
		mob.baseCharStats().setCurrentClassLevel(level);
		mob.charStats().setCurrentClassLevel(level);
		mob.basePhyStats().setLevel(level);
		mob.phyStats().setLevel(level);
		fillOutMOB(mob, level);
		return mob;
	}

	public boolean isFilledOutMOB(MOB mob) {
		if (!mob.isMonster())
			return false;
		PhyStats mobP = mob.basePhyStats();

		MOB filledM = fillOutMOB((MOB) null, mobP.level());
		PhyStats filP = filledM.basePhyStats();
		if ((mobP.speed() == filP.speed()) && (mobP.armor() == filP.armor())
				&& (mobP.damage() == filP.damage())
				&& (mobP.attackAdjustment() == filP.attackAdjustment())) {
			filledM.destroy();
			return true;
		}
		filledM.destroy();
		return false;
	}

	public MOB fillOutMOB(MOB mob, int level) {
		if (mob == null)
			mob = CMClass.getFactoryMOB();
		if (!mob.isMonster())
			return mob;

		long rejuv = CMProps.getTicksPerMinute() + CMProps.getTicksPerMinute()
				+ (level * CMProps.getTicksPerMinute() / 2);
		if (rejuv > (CMProps.getTicksPerMinute() * 20))
			rejuv = (CMProps.getTicksPerMinute() * 20);
		mob.basePhyStats().setLevel(level);
		mob.basePhyStats().setRejuv((int) rejuv);
		mob.basePhyStats().setSpeed(getLevelMOBSpeed(mob));
		mob.basePhyStats().setArmor(getLevelMOBArmor(mob));
		mob.basePhyStats().setDamage(getLevelMOBDamage(mob));
		mob.basePhyStats().setAttackAdjustment(getLevelAttack(mob));
		mob.setMoney(CMLib.dice().roll(1, level, 0)
				+ CMLib.dice().roll(1, 10, 0));
		mob.baseState().setHitPoints(
				CMLib.dice().rollHP(mob.basePhyStats().level(),
						mob.basePhyStats().ability()));
		mob.baseState().setMana(getLevelMana(mob));
		mob.baseState().setMovement(getLevelMove(mob));
		if (mob.getWimpHitPoint() > 0)
			mob.setWimpHitPoint((int) Math.round(CMath.mul(mob.curState()
					.getHitPoints(), .10)));
		mob.setExperience(CMLib.leveler().getLevelExperience(
				mob.phyStats().level()));
		return mob;
	}

	public StringBuffer baseLevelAdjuster(MOB mob, int adjuster) {
		mob.basePhyStats().setLevel(mob.basePhyStats().level() + adjuster);
		CharClass curClass = mob.baseCharStats().getCurrentClass();
		mob.baseCharStats().setClassLevel(curClass,
				mob.baseCharStats().getClassLevel(curClass) + adjuster);
		int classLevel = mob.baseCharStats().getClassLevel(
				mob.baseCharStats().getCurrentClass());
		int gained = mob.getExperience() - mob.getExpNextLevel();
		if (gained < 50)
			gained = 50;

		StringBuffer theNews = new StringBuffer("");

		mob.recoverCharStats();
		mob.recoverPhyStats();
		theNews.append("^HYou are now a "
				+ mob.charStats().displayClassLevel(mob, false) + ".^N\n\r");

		int newHitPointGain = getPlayerHPBonusNextLevel(mob) * adjuster;
		mob.baseState().setHitPoints(
				mob.baseState().getHitPoints() + newHitPointGain);
		if (mob.baseState().getHitPoints() < 20)
			mob.baseState().setHitPoints(20);
		mob.curState().setHitPoints(
				mob.curState().getHitPoints() + newHitPointGain);
		theNews.append("^NYou have gained ^H" + newHitPointGain + "^? hit "
				+ (newHitPointGain != 1 ? "points" : "point") + ", ^H");

		int mvGain = getMoveBonusNextLevel(mob) * adjuster;
		mob.baseState().setMovement(mob.baseState().getMovement() + mvGain);
		mob.curState().setMovement(mob.curState().getMovement() + mvGain);
		theNews.append(mvGain + "^N move " + (mvGain != 1 ? "points" : "point")
				+ ", ^H");

		int attGain = getAttackBonusNextLevel(mob) * adjuster;
		mob.basePhyStats().setAttackAdjustment(
				mob.basePhyStats().attackAdjustment() + attGain);
		mob.phyStats().setAttackAdjustment(
				mob.phyStats().attackAdjustment() + attGain);
		if (attGain > 0)
			theNews.append(attGain + "^N attack "
					+ (attGain != 1 ? "points" : "point") + ", ^H");

		int manaGain = getManaBonusNextLevel(mob) * adjuster;
		mob.baseState().setMana(mob.baseState().getMana() + manaGain);
		theNews.append(manaGain + "^N " + (manaGain != 1 ? "points" : "point")
				+ " of mana,");

		if (curClass.getLevelsPerBonusDamage() != 0) {
			if ((adjuster < 0)
					&& (((classLevel + 1) % curClass.getLevelsPerBonusDamage()) == 0))
				mob.basePhyStats().setDamage(mob.basePhyStats().damage() - 1);
			else if ((adjuster > 0)
					&& ((classLevel % curClass.getLevelsPerBonusDamage()) == 0))
				mob.basePhyStats().setDamage(mob.basePhyStats().damage() + 1);
		}
		mob.recoverMaxState();
		return theNews;
	}

	public void unLevel(MOB mob) {
		if ((mob.basePhyStats().level() < 2)
				|| (CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS))
				|| (mob.charStats().getCurrentClass().leveless())
				|| (mob.charStats().getMyRace().leveless()))
			return;
		mob.tell("^ZYou have ****LOST A LEVEL****^.^N\n\r\n\r"
				+ CMLib.protocol().msp("doh.wav", 60));
		if (!mob.isMonster()) {
			List<String> channels = CMLib.channels().getFlaggedChannelNames(
					ChannelsLibrary.ChannelFlag.LOSTLEVELS);
			if (!CMLib.flags().isCloaked(mob))
				for (int i = 0; i < channels.size(); i++)
					CMLib.commands().postChannel(channels.get(i), mob.clans(),
							mob.Name() + " has just lost a level.", true);
		}

		CharClass curClass = mob.baseCharStats().getCurrentClass();
		int oldClassLevel = mob.baseCharStats().getClassLevel(curClass);
		baseLevelAdjuster(mob, -1);
		int prac2Stat = mob.charStats().getStat(CharStats.STAT_WISDOM);
		int maxPrac2Stat = (CMProps.getIntVar(CMProps.Int.BASEMAXSTAT) + mob
				.charStats().getStat(
						CharStats.CODES.toMAXBASE(CharStats.STAT_WISDOM)));
		if (prac2Stat > maxPrac2Stat)
			prac2Stat = maxPrac2Stat;
		int practiceGain = (int) Math.floor(CMath.div(prac2Stat, 6.0))
				+ curClass.getBonusPracLevel();
		if (practiceGain <= 0)
			practiceGain = 1;
		mob.setPractices(mob.getPractices() - practiceGain);
		int trainGain = 0;
		if (trainGain <= 0)
			trainGain = 1;
		mob.setTrains(mob.getTrains() - trainGain);

		mob.recoverPhyStats();
		mob.recoverCharStats();
		mob.recoverMaxState();
		mob.tell("^HYou are now a level "
				+ mob.charStats().getClassLevel(
						mob.charStats().getCurrentClass())
				+ " "
				+ mob.charStats().getCurrentClass()
						.name(mob.charStats().getCurrentClassLevel())
				+ "^N.\n\r");
		curClass.unLevel(mob);
		Ability A = null;
		Vector<Ability> lose = new Vector<Ability>();
		for (int a = 0; a < mob.numAbilities(); a++) {
			A = mob.fetchAbility(a);
			if ((CMLib.ableMapper().getQualifyingLevel(curClass.ID(), false,
					A.ID()) == oldClassLevel)
					&& (CMLib.ableMapper().getDefaultGain(curClass.ID(), false,
							A.ID()))
					&& (CMLib.ableMapper()
							.classOnly(mob, curClass.ID(), A.ID())))
				lose.addElement(A);
		}
		for (int l = 0; l < lose.size(); l++) {
			A = lose.elementAt(l);
			mob.delAbility(A);
			mob.tell("^HYou have forgotten " + A.name() + ".^N.\n\r");
			A = mob.fetchEffect(A.ID());
			if ((A != null) && (A.isNowAnAutoEffect())) {
				A.unInvoke();
				mob.delEffect(A);
			}
		}
		fixMobStatsIfNecessary(mob, -1);
	}

	public void loseExperience(MOB mob, int amount) {
		if ((mob == null) || (mob.soulMate() != null))
			return;
		if (Log.combatChannelOn()) {
			String room = CMLib.map().getExtendedRoomID(
					(mob.location() != null) ? mob.location() : null);
			String mobName = mob.Name();
			Log.killsOut("-EXP", room + ":" + mobName + ":" + amount);
		}
		if ((mob.getLiegeID().length() > 0) && (amount > 2)
				&& (!mob.isMonster())) {
			MOB sire = CMLib.players().getPlayer(mob.getLiegeID());
			if ((sire != null) && (CMLib.flags().isInTheGame(sire, true))) {
				int sireShare = (int) Math.round(CMath.div(amount, 10.0));
				amount -= sireShare;
				if (postExperience(sire, null, "", -sireShare, true))
					sire.tell("^N^!You lose ^H" + sireShare
							+ "^N^! experience points from " + mob.Name()
							+ ".^N");
			}
		}
		if ((amount > 2) && (!mob.isMonster())) {
			for (Pair<Clan, Integer> p : mob.clans()) {
				Clan C = p.first;
				if (C.getTaxes() > 0.0) {
					int clanshare = (int) Math.round(CMath.mul(amount,
							C.getTaxes()));
					if (clanshare > 0) {
						amount -= clanshare;
						C.adjExp(clanshare * -1);
						C.update();
					}
				}
			}
		}
		mob.setExperience(mob.getExperience() - amount);
		int neededLowest = getLevelExperience(mob.basePhyStats().level() - 2);
		if ((mob.getExperience() < neededLowest)
				&& (mob.basePhyStats().level() > 1)) {
			unLevel(mob);
			neededLowest = getLevelExperience(mob.basePhyStats().level() - 2);
		}
	}

	public boolean postExperience(MOB mob, MOB victim, String homage,
			int amount, boolean quiet) {
		if ((mob == null)
				|| (CMSecurity.isDisabled(CMSecurity.DisFlag.EXPERIENCE))
				|| mob.charStats().getCurrentClass().expless()
				|| mob.charStats().getMyRace().expless())
			return false;
		CMMsg msg = CMClass.getMsg(mob, victim, null, CMMsg.MASK_ALWAYS
				| CMMsg.TYP_EXPCHANGE, null, CMMsg.NO_EFFECT, homage,
				CMMsg.NO_EFFECT, "" + quiet);
		msg.setValue(amount);
		Room R = mob.location();
		if (R != null) {
			if (R.okMessage(mob, msg))
				R.send(mob, msg);
			else
				return false;
		} else if (amount >= 0)
			gainExperience(mob, victim, homage, amount, quiet);
		else
			loseExperience(mob, -amount);
		return true;
	}

	public int getLevelExperience(int level) {
		if (level < 0)
			return 0;
		int[] levelingChart = CMProps
				.getListFileIntList(CMProps.ListFile.EXP_CHART);
		if (level < levelingChart.length)
			return levelingChart[level];
		int lastDiff = levelingChart[levelingChart.length - 1]
				- levelingChart[levelingChart.length - 2];
		return levelingChart[levelingChart.length - 1]
				+ ((1 + (level - levelingChart.length)) * lastDiff);
	}

	public int getLevelExperienceJustThisLevel(int level) {
		if (level < 0)
			return 0;
		int[] levelingChart = CMProps
				.getListFileIntList(CMProps.ListFile.EXP_CHART);
		if (level == 0)
			return levelingChart[0];
		else if (level < levelingChart.length)
			return levelingChart[level] - levelingChart[level - 1];
		int lastDiff = levelingChart[levelingChart.length - 1]
				- levelingChart[levelingChart.length - 2];
		return ((1 + (level - levelingChart.length)) * lastDiff);
	}

	public void level(MOB mob) {
		if ((CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS))
				|| (mob.charStats().getCurrentClass().leveless())
				|| (mob.charStats().isLevelCapped(mob.charStats()
						.getCurrentClass()))
				|| (mob.charStats().getMyRace().leveless()))
			return;
		Room room = mob.location();
		CMMsg msg = CMClass.getMsg(mob, CMMsg.MSG_LEVEL, null, mob
				.basePhyStats().level() + 1);
		if (!CMLib.map().sendGlobalMessage(mob, CMMsg.TYP_LEVEL, msg))
			return;
		if (room != null) {
			if (!room.okMessage(mob, msg))
				return;
			room.send(mob, msg);
		}

		if (mob.getGroupMembers(new HashSet<MOB>()).size() > 1) {
			Command C = CMClass.getCommand("GTell");
			try {
				if (C != null)
					C.execute(mob, new XVector<String>("GTELL",
							",<S-HAS-HAVE> gained a level."),
							Command.METAFLAG_FORCED);
			} catch (Exception e) {
			}
		}
		StringBuffer theNews = new StringBuffer(
				"^xYou have L E V E L E D ! ! ! ! ! ^.^N\n\r\n\r"
						+ CMLib.protocol().msp("level_gain.wav", 60));
		CharClass curClass = mob.baseCharStats().getCurrentClass();
		theNews.append(baseLevelAdjuster(mob, 1));
		if (mob.playerStats() != null) {
			mob.playerStats().setLeveledDateTime(mob.basePhyStats().level(),
					room);
			List<String> channels = CMLib.channels().getFlaggedChannelNames(
					ChannelsLibrary.ChannelFlag.DETAILEDLEVELS);
			List<String> channels2 = CMLib.channels().getFlaggedChannelNames(
					ChannelsLibrary.ChannelFlag.LEVELS);
			if (!CMLib.flags().isCloaked(mob))
				for (int i = 0; i < channels.size(); i++)
					CMLib.commands()
							.postChannel(
									channels.get(i),
									mob.clans(),
									mob.Name()
											+ " has just gained a level at "
											+ CMLib.map().getExtendedRoomID(
													room) + ".", true);
			if (!CMLib.flags().isCloaked(mob))
				for (int i = 0; i < channels2.size(); i++)
					CMLib.commands().postChannel(channels2.get(i), mob.clans(),
							mob.Name() + " has just gained a level.", true);
			if (mob.soulMate() == null)
				CMLib.coffeeTables()
						.bump(mob, CoffeeTableRow.STAT_LEVELSGAINED);
		}

		int prac2Stat = mob.charStats().getStat(CharStats.STAT_WISDOM);
		int maxPrac2Stat = (CMProps.getIntVar(CMProps.Int.BASEMAXSTAT) + mob
				.charStats().getStat(
						CharStats.CODES.toMAXBASE(CharStats.STAT_WISDOM)));
		if (prac2Stat > maxPrac2Stat)
			prac2Stat = maxPrac2Stat;
		int practiceGain = (int) Math.floor(CMath.div(prac2Stat, 6.0))
				+ curClass.getBonusPracLevel();
		if (practiceGain <= 0)
			practiceGain = 1;
		mob.setPractices(mob.getPractices() + practiceGain);
		theNews.append(" ^H" + practiceGain + "^N practice "
				+ (practiceGain != 1 ? "points" : "point") + ", ");

		int trainGain = 1;
		if (trainGain <= 0)
			trainGain = 1;
		mob.setTrains(mob.getTrains() + trainGain);
		theNews.append("and ^H" + trainGain + "^N training "
				+ (trainGain != 1 ? "sessions" : "session") + ".\n\r^N");

		mob.tell(theNews.toString());
		curClass = mob.baseCharStats().getCurrentClass();
		HashSet<String> oldAbilities = new HashSet<String>();
		for (Enumeration<Ability> a = mob.allAbilities(); a.hasMoreElements();) {
			Ability A = a.nextElement();
			if (A != null)
				oldAbilities.add(A.ID());
		}

		curClass.grantAbilities(mob, false);

		// check for autoinvoking abilities
		for (int a = 0; a < mob.numAbilities(); a++) {
			Ability A = mob.fetchAbility(a);
			if ((A != null) && (CMLib.ableMapper().qualifiesByLevel(mob, A))) {
				Ability eA = mob.fetchEffect(A.ID());
				if ((eA == null) || (!eA.isNowAnAutoEffect()))
					A.autoInvocation(mob);
			}
		}

		List<String> newAbilityIDs = new Vector<String>();
		for (Enumeration<Ability> a = mob.allAbilities(); a.hasMoreElements();) {
			Ability A = a.nextElement();
			if ((A != null) && (!oldAbilities.contains(A.ID())))
				newAbilityIDs.add(A.ID());
		}

		for (int a = 0; a < newAbilityIDs.size(); a++)
			if (!oldAbilities.contains(newAbilityIDs.get(a))) {
				Ability A = mob.fetchAbility(newAbilityIDs.get(a));
				if (A != null) {
					String type = Ability.ACODE_DESCS[(A.classificationCode() & Ability.ALL_ACODES)]
							.toLowerCase();
					mob.tell("^NYou have learned the " + type + " ^H"
							+ A.name() + "^?.^N");
				}
			}

		fixMobStatsIfNecessary(mob, 1);

		// wrap it all up
		mob.recoverPhyStats();
		mob.recoverCharStats();
		mob.recoverMaxState();

		curClass.level(mob, newAbilityIDs);
		mob.charStats().getMyRace().level(mob, newAbilityIDs);

	}

	protected boolean fixMobStatsIfNecessary(MOB mob, int direction) {
		if ((mob.playerStats() == null)
				&& (mob.baseCharStats().getCurrentClass().name().equals("mob"))) // mob
																					// leveling
		{
			mob.basePhyStats().setSpeed(getLevelMOBSpeed(mob));
			mob.basePhyStats().setArmor(getLevelMOBArmor(mob));
			mob.basePhyStats().setDamage(getLevelMOBDamage(mob));
			mob.basePhyStats().setAttackAdjustment(getLevelAttack(mob));
			mob.baseState().setHitPoints(
					mob.baseState().getHitPoints()
							+ ((mob.basePhyStats().ability() / 2) * direction));
			mob.baseState().setMana(getLevelMana(mob));
			mob.baseState().setMovement(getLevelMove(mob));
			if (mob.getWimpHitPoint() > 0)
				mob.setWimpHitPoint((int) Math.round(CMath.mul(mob.curState()
						.getHitPoints(), .10)));
			return true;
		}
		return false;
	}

	public int adjustedExperience(MOB mob, MOB victim, int amount) {
		int highestLevelPC = 0;
		Room R = mob.location();
		if (R != null)
			for (int m = 0; m < R.numInhabitants(); m++) {
				MOB M = R.fetchInhabitant(m);
				if ((M != null) && (M != mob) && (M != victim)
						&& (!M.isMonster())
						&& (M.phyStats().level() > highestLevelPC))
					highestLevelPC = M.phyStats().level();
			}

		Set<MOB> group = mob.getGroupMembers(new HashSet<MOB>());
		CharClass charClass = null;
		Race charRace = null;

		for (Iterator<MOB> i = group.iterator(); i.hasNext();) {
			MOB allyMOB = i.next();
			charClass = allyMOB.charStats().getCurrentClass();
			charRace = allyMOB.charStats().getMyRace();
			if (charClass != null)
				amount = charClass.adjustExperienceGain(allyMOB, mob, victim,
						amount);
			if (charRace != null)
				amount = charRace.adjustExperienceGain(allyMOB, mob, victim,
						amount);
		}

		if (victim != null) {
			double levelLimit = CMProps.getIntVar(CMProps.Int.EXPRATE);
			double levelDiff = victim.phyStats().level()
					- mob.phyStats().level();

			if (levelDiff < (-levelLimit))
				amount = 0;
			else if ((levelLimit > 0)
					&& ((highestLevelPC - mob.phyStats().level()) <= levelLimit)) {
				double levelFactor = levelDiff / levelLimit;
				if (levelFactor > levelLimit)
					levelFactor = levelLimit;
				amount += (int) Math.round(levelFactor * amount);
			}
		}

		return amount;
	}

	public void gainExperience(MOB mob, MOB victim, String homageMessage,
			int amount, boolean quiet) {
		if (mob == null)
			return;
		if ((Log.combatChannelOn())
				&& ((mob.location() != null) || ((victim != null) && (victim
						.location() != null)))) {
			String room = CMLib.map().getExtendedRoomID(
					(mob.location() != null) ? mob.location()
							: (victim == null) ? null : victim.location());
			String mobName = mob.Name();
			String vicName = (victim != null) ? victim.Name() : "null";
			Log.killsOut("+EXP", room + ":" + mobName + ":" + vicName + ":"
					+ amount + ":" + homageMessage);
		}

		amount = adjustedExperience(mob, victim, amount);

		if (mob.phyStats().level() >= CMProps
				.getIntVar(CMProps.Int.MINCLANLEVEL)) {
			for (Pair<Clan, Integer> p : mob.clans())
				if (amount > 2)
					amount = p.first.applyExpMods(amount);
		}

		if ((mob.getLiegeID().length() > 0) && (amount > 2)) {
			MOB sire = CMLib.players().getPlayer(mob.getLiegeID());
			if ((sire != null) && (CMLib.flags().isInTheGame(sire, true))) {
				int sireShare = (int) Math.round(CMath.div(amount, 10.0));
				if (sireShare <= 0)
					sireShare = 1;
				amount -= sireShare;
				CMLib.leveler().postExperience(sire, null,
						" from " + mob.name(sire), sireShare, quiet);
			}
		}

		mob.setExperience(mob.getExperience() + amount);
		if (homageMessage == null)
			homageMessage = "";
		if (!quiet) {
			if (amount > 1)
				mob.tell("^N^!You gain ^H" + amount + "^N^! experience points"
						+ homageMessage + ".^N");
			else if (amount > 0)
				mob.tell("^N^!You gain ^H" + amount + "^N^! experience point"
						+ homageMessage + ".^N");
		}

		if ((mob.getExperience() >= mob.getExpNextLevel())
				&& (mob.getExpNeededLevel() < Integer.MAX_VALUE))
			level(mob);
	}

	public void handleExperienceChange(CMMsg msg) {
		MOB mob = msg.source();
		if (!CMSecurity.isDisabled(CMSecurity.DisFlag.EXPERIENCE)
				&& !mob.charStats().getCurrentClass().expless()
				&& !mob.charStats().getMyRace().expless()) {
			MOB expFromKilledmob = null;
			if (msg.target() instanceof MOB)
				expFromKilledmob = (MOB) msg.target();

			if (msg.value() >= 0)
				gainExperience(mob, expFromKilledmob, msg.targetMessage(),
						msg.value(), CMath.s_bool(msg.othersMessage()));
			else
				loseExperience(mob, -msg.value());
		}
	}

}
