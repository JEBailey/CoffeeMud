package com.planet_ink.coffee_mud.Items.MiscMagic;

import java.util.List;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Items.interfaces.ArchonOnly;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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
public class WandArchon extends StdWand implements ArchonOnly {
	public String ID() {
		return "WandArchon";
	}

	protected final static String[] MAGIC_WORDS = { "LEVEL", "RESTORE",
			"REFRESH", "BLAST", "BURN" };

	public WandArchon() {
		super();

		setName("a flashy wand");
		setDisplayText("a flashy wand has been left here.");
		setDescription("A wand made out of sparkling energy.");
		secretIdentity = "The Wand of the Archons!";
		this.setUsesRemaining(99999);
		baseGoldValue = 20000;
		material = RawMaterial.RESOURCE_OAK;
		recoverPhyStats();
		secretWord = "REFRESH, RESTORE, BLAST, LEVEL X UP, LEVEL X DOWN, BURN";
	}

	public void setSpell(Ability theSpell) {
		super.setSpell(theSpell);
		secretWord = "REFRESH, BLAST, LEVEL X UP, LEVEL X DOWN, BURN";
	}

	public void setMiscText(String newText) {
		super.setMiscText(newText);
		secretWord = "REFRESH, BLAST, LEVEL X UP, LEVEL X DOWN, BURN";
	}

	public void affectCharState(MOB mob, CharState affectableState) {
		if (!amWearingAt(Wearable.IN_INVENTORY)) {
			affectableState.setHunger(99999999);
			affectableState.setThirst(99999999);
			mob.curState().setHunger(9999999);
			mob.curState().setThirst(9999999);
		}
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;

		MOB mob = msg.source();
		if (mob.location() == null)
			return true;

		if (msg.amITarget(this))
			switch (msg.targetMinor()) {
			case CMMsg.TYP_HOLD:
			case CMMsg.TYP_WEAR:
			case CMMsg.TYP_WIELD:
			case CMMsg.TYP_GET:
			case CMMsg.TYP_PUSH:
			case CMMsg.TYP_PULL:
				if (!CMSecurity.isASysOp(msg.source())) {
					mob.location()
							.show(mob,
									null,
									CMMsg.MSG_OK_VISUAL,
									name()
											+ " flashes and falls out of <S-HIS-HER> hands!");
					return false;
				}
				break;
			}
		if ((msg.targetMinor() == CMMsg.TYP_SPEAK)
				&& (msg.sourceMessage() != null)) {
			String said = CMStrings.getSayFromMessage(msg.sourceMessage());
			if (said != null) {
				said = said.trim().toUpperCase();
				int x = said.indexOf(' ');
				if (x > 0)
					said = said.substring(0, x);
				if (CMParms.indexOf(MAGIC_WORDS, said) >= 0)
					super.secretWord = said;
			}
		}
		return true;
	}

	public boolean safetyCheck(MOB mob, String message) {
		if ((!mob.isMonster())
				&& (message.length() > 0)
				&& (mob.session().getPreviousCMD() != null)
				&& (CMParms.combine(mob.session().getPreviousCMD(), 0)
						.toUpperCase().indexOf(message) < 0)) {
			mob.tell("The wand fizzles in an irritating way.");
			return false;
		}
		return true;
	}

	public boolean checkWave(MOB mob, String message) {
		if (message == null)
			return false;
		List<String> parms = CMParms.paramParse(message.toUpperCase());
		for (int i = 0; i < MAGIC_WORDS.length; i++)
			if (parms.contains(MAGIC_WORDS[i])) {
				return (mob.isMine(this))
						&& (!amWearingAt(Wearable.IN_INVENTORY));
			}
		return super.checkWave(mob, message);
	}

	public void waveIfAble(MOB mob, Physical afftarget, String message) {
		if ((mob.isMine(this)) && (message != null)
				&& (!this.amWearingAt(Wearable.IN_INVENTORY))) {
			if ((mob.location() != null) && (afftarget != null)
					&& (afftarget instanceof MOB)) {
				MOB target = (MOB) afftarget;
				message = message.toUpperCase().trim();
				if (message.equals("LEVEL ALL UP")) {
					if (!safetyCheck(mob, message.toUpperCase()))
						return;
					mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
							this.name() + " glows brightly at <T-NAME>.");
					int destLevel = CMProps
							.getIntVar(CMProps.Int.LASTPLAYERLEVEL);
					if (destLevel == 0)
						destLevel = 30;
					if (destLevel <= target.basePhyStats().level())
						destLevel = 100;
					if ((target.charStats().getCurrentClass().leveless())
							|| (target.charStats().isLevelCapped(target
									.charStats().getCurrentClass()))
							|| (target.charStats().getMyRace().leveless())
							|| (CMSecurity
									.isDisabled(CMSecurity.DisFlag.LEVELS)))
						mob.tell("The wand will not work on such as "
								+ target.name(mob) + ".");
					else
						while (target.basePhyStats().level() < destLevel) {
							if ((target.getExpNeededLevel() == Integer.MAX_VALUE)
									|| (target.charStats().getCurrentClass()
											.expless())
									|| (target.charStats().getMyRace()
											.expless()))
								CMLib.leveler().level(target);
							else
								CMLib.leveler().postExperience(target, null,
										null, target.getExpNeededLevel() + 1,
										false);
						}
				} else if (message.startsWith("LEVEL ")
						&& message.endsWith(" UP")) {
					if (!safetyCheck(mob, message))
						return;
					message = message.substring(6).trim();
					message = message.substring(0, message.length() - 2).trim();
					int num = 1;
					if (CMath.isInteger(message))
						num = CMath.s_int(message);
					mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
							this.name() + " glows brightly at <T-NAME>.");
					if ((target.charStats().getCurrentClass().leveless())
							|| (target.charStats().isLevelCapped(target
									.charStats().getCurrentClass()))
							|| (target.charStats().getMyRace().leveless())
							|| (CMSecurity
									.isDisabled(CMSecurity.DisFlag.LEVELS)))
						mob.tell("The wand will not work on such as "
								+ target.name(mob) + ".");
					else
						for (int i = 0; i < num; i++) {
							if ((target.getExpNeededLevel() == Integer.MAX_VALUE)
									|| (target.charStats().getCurrentClass()
											.expless())
									|| (target.charStats().getMyRace()
											.expless()))
								CMLib.leveler().level(target);
							else
								CMLib.leveler().postExperience(target, null,
										null, target.getExpNeededLevel() + 1,
										false);
						}
					return;
				} else if (message.startsWith("LEVEL ")
						&& message.endsWith(" DOWN")) {
					if (!safetyCheck(mob, message))
						return;
					message = message.substring(6).trim();
					message = message.substring(0, message.length() - 4).trim();
					int num = 1;
					if (CMath.isInteger(message))
						num = CMath.s_int(message);
					mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
							this.name() + " glows brightly at <T-NAME>.");
					if ((target.charStats().getCurrentClass().leveless())
							|| (target.charStats().isLevelCapped(target
									.charStats().getCurrentClass()))
							|| (target.charStats().getMyRace().leveless())
							|| (CMSecurity
									.isDisabled(CMSecurity.DisFlag.LEVELS)))
						mob.tell("The wand will not work on such as "
								+ target.name(mob) + ".");
					else
						for (int i = 0; i < num; i++) {
							if ((target.getExpNeededLevel() == Integer.MAX_VALUE)
									|| (target.charStats().getCurrentClass()
											.expless())
									|| (target.charStats().getMyRace()
											.expless()))
								CMLib.leveler().unLevel(target);
							else {
								int xpLevelBelow = CMLib
										.leveler()
										.getLevelExperience(
												target.basePhyStats().level() - 2);
								int levelDown = (target.getExperience() - xpLevelBelow) + 1;
								CMLib.leveler().postExperience(target, null,
										null, -levelDown, false);
							}
						}
					return;
				} else if (message.equals("RESTORE")) {
					if (!safetyCheck(mob, message))
						return;
					mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
							this.name() + " glows brightly at <T-NAME>.");
					List<Ability> diseaseV = CMLib.flags().domainAffects(
							target, Ability.ACODE_DISEASE);
					if (diseaseV.size() > 0) {
						Ability A = CMClass.getAbility("Prayer_CureDisease");
						if (A != null)
							A.invoke(mob, target, true, 0);
					}
					List<Ability> poisonV = CMLib.flags().domainAffects(target,
							Ability.ACODE_POISON);
					if (poisonV.size() > 0) {
						Ability A = CMClass.getAbility("Prayer_RemovePoison");
						if (A != null)
							A.invoke(mob, target, true, 0);
					}
					Ability bleed = target.fetchEffect("Bleeding");
					if (bleed != null) {
						bleed.unInvoke();
						target.delEffect(bleed);
					}
					Ability injury = target.fetchEffect("Injury");
					if (injury != null) {
						injury.unInvoke();
						target.delEffect(injury);
					}
					Ability ampu = target.fetchEffect("Amputation");
					if (ampu != null) {
						ampu.unInvoke();
						target.delEffect(ampu);
					}
					target.recoverMaxState();
					target.resetToMaxState();
					target.tell("You feel refreshed!");
					return;
				} else if (message.equals("REFRESH")) {
					if (!safetyCheck(mob, message))
						return;
					mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL,
							this.name() + " glows brightly at <T-NAME>.");
					Ability bleed = target.fetchEffect("Bleeding");
					if (bleed != null) {
						bleed.unInvoke();
						target.delEffect(bleed);
					}
					target.recoverMaxState();
					target.resetToMaxState();
					target.tell("You feel refreshed!");
					return;
				} else if (message.equals("BLAST")) {
					if (!safetyCheck(mob, message))
						return;
					mob.location().show(
							mob,
							target,
							CMMsg.MSG_OK_VISUAL,
							this.name()
									+ " zaps <T-NAME> with unworldly energy.");
					target.curState().setHitPoints(1);
					target.curState().setMana(1);
					target.curState().setMovement(1);
					return;
				} else if (message.equals("BURN")) {
					if (!safetyCheck(mob, message))
						return;
					mob.location()
							.show(mob,
									target,
									CMMsg.MSG_OK_VISUAL,
									this.name()
											+ " wielded by <S-NAME> shoots forth magical green flames at <T-NAME>.");
					int flameDamage = (int) Math.round(Math.random() * 6);
					flameDamage *= 3;
					CMLib.combat().postDamage(
							mob,
							target,
							null,
							(++flameDamage),
							CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE,
							Weapon.TYPE_BURNING,
							(this.name() + " <DAMAGE> <T-NAME>!")
									+ CMLib.protocol().msp("fireball.wav", 30));
					return;
				}
			}
		}
		super.waveIfAble(mob, afftarget, message);
	}
}
