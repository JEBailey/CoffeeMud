package com.planet_ink.coffee_mud.Abilities.Common;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Shearing extends CommonSkill {
	public String ID() {
		return "Shearing";
	}

	public String name() {
		return "Shearing";
	}

	private static final String[] triggerStrings = { "SHEAR", "SHEARING" };

	public int classificationCode() {
		return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_ANIMALAFFINITY;
	}

	public String[] triggerStrings() {
		return triggerStrings;
	}

	private MOB sheep = null;
	protected boolean failed = false;

	public Shearing() {
		super();
		displayText = "You are shearing something...";
		verb = "shearing";
	}

	protected int getDuration(MOB mob, int weight) {
		int duration = ((weight / (10 + getXLEVELLevel(mob))));
		duration = super.getDuration(duration, mob, 1, 10);
		if (duration > 40)
			duration = 40;
		return duration;
	}

	protected int baseYield() {
		return 1;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((sheep != null) && (affected instanceof MOB)
				&& (((MOB) affected).location() != null)
				&& ((!((MOB) affected).location().isInhabitant(sheep))))
			unInvoke();
		return super.tick(ticking, tickID);
	}

	public Vector getMyWool(MOB M) {
		Vector wool = new Vector();
		if ((M != null) && (M.charStats().getMyRace() != null)
				&& (M.charStats().getMyRace().myResources() != null)
				&& (M.charStats().getMyRace().myResources().size() > 0)) {
			List<RawMaterial> V = M.charStats().getMyRace().myResources();
			for (int v = 0; v < V.size(); v++)
				if ((V.get(v) != null)
						&& (V.get(v).material() == RawMaterial.RESOURCE_WOOL))
					wool.addElement(V.get(v));
		}
		return wool;
	}

	public void unInvoke() {
		if (canBeUninvoked()) {
			if ((affected != null) && (affected instanceof MOB)) {
				MOB mob = (MOB) affected;
				if ((sheep != null) && (!aborted)) {
					if ((failed) || (!mob.location().isInhabitant(sheep)))
						commonTell(mob,
								"You messed up your shearing completely.");
					else {
						mob.location().show(mob, null, sheep,
								getActivityMessageType(),
								"<S-NAME> manage(s) to shear <O-NAME>.");
						spreadImmunity(sheep);
						int yield = abilityCode() <= 0 ? 1 : abilityCode();
						for (int i = 0; i < yield; i++) {
							Vector V = getMyWool(sheep);
							for (int v = 0; v < V.size(); v++) {
								RawMaterial I = (RawMaterial) V.elementAt(v);
								I = (RawMaterial) I.copyOf();
								mob.location().addItem(I,
										ItemPossessor.Expire.Monster_EQ);
							}
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
		MOB target = null;
		Room R = mob.location();
		if (R == null)
			return false;
		sheep = null;
		if ((mob.isMonster() && (!CMLib.flags().isAnimalIntelligence(mob)))
				&& (commands.size() == 0)) {
			for (int i = 0; i < R.numInhabitants(); i++) {
				MOB M = R.fetchInhabitant(i);
				if ((M != mob) && (CMLib.flags().canBeSeenBy(M, mob))
						&& (getMyWool(M).size() > 0)) {
					target = M;
					break;
				}
			}
		} else if (commands.size() == 0)
			mob.tell("Shear what?");
		else
			target = super.getTarget(mob, commands, givenTarget);

		if (target == null)
			return false;
		if ((getMyWool(target).size() <= 0)
				|| (!target.okMessage(target, CMClass.getMsg(target, target,
						this, CMMsg.MSG_OK_ACTION, null)))) {
			commonTell(mob, target, null,
					"You can't shear <T-NAME>, there's no wool left on <T-HIM-HER>.");
			return false;
		}
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;
		failed = !proficiencyCheck(mob, 0, auto);
		CMMsg msg = CMClass.getMsg(mob, target, this, getActivityMessageType(),
				getActivityMessageType(), getActivityMessageType(),
				"<S-NAME> start(s) shearing <T-NAME>.");
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			sheep = target;
			verb = "shearing " + target.name();
			playSound = "scissor.wav";
			int duration = getDuration(mob, target.phyStats().weight());
			beneficialAffect(mob, mob, asLevel, duration);
		}
		return true;
	}
}
