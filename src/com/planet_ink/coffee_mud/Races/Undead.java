package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.DeadBody;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMSecurity;
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
public class Undead extends StdRace {
	public String ID() {
		return "Undead";
	}

	public String name() {
		return "Undead";
	}

	public int shortestMale() {
		return 64;
	}

	public int shortestFemale() {
		return 60;
	}

	public int heightVariance() {
		return 12;
	}

	public int lightestWeight() {
		return 100;
	}

	public int weightVariance() {
		return 100;
	}

	public long forbiddenWornBits() {
		return 0;
	}

	public String racialCategory() {
		return "Undead";
	}

	public boolean fertile() {
		return false;
	}

	public boolean uncharmable() {
		return true;
	}

	public int[] getBreathables() {
		return breatheAnythingArray;
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1,
			1, 0, 0 };

	public int[] bodyMask() {
		return parts;
	}

	private int[] agingChart = { 0, 0, 0, 0, 0, YEARS_AGE_LIVES_FOREVER,
			YEARS_AGE_LIVES_FOREVER, YEARS_AGE_LIVES_FOREVER,
			YEARS_AGE_LIVES_FOREVER };

	public int[] getAgingChart() {
		return agingChart;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectCharState(MOB affectedMOB, CharState affectableState) {
		super.affectCharState(affectedMOB, affectableState);
		affectableState.setHunger(999999);
		affectedMOB.curState().setHunger(affectableState.getHunger());
		affectableState.setThirst(999999);
		affectedMOB.curState().setThirst(affectableState.getThirst());
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOLEM);
		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_SEE_INFRARED);
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if (msg.amITarget(myHost) && (msg.targetMinor() == CMMsg.TYP_SNIFF)
				&& (myHost instanceof MOB) && (ID().equals("Undead")))
			msg.source().tell(name() + " stinks of grime and decay.");
	}

	public String makeMobName(char gender, int age) {
		switch (age) {
		case Race.AGE_INFANT:
			return name().toLowerCase() + " of a baby";
		case Race.AGE_TODDLER:
			return name().toLowerCase() + " of a toddler";
		case Race.AGE_CHILD:
			return name().toLowerCase() + " of a child";
		default:
			return super.makeMobName('N', age);
		}
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((myHost != null) && (myHost instanceof MOB)) {
			MOB mob = (MOB) myHost;
			if (msg.amITarget(mob) && (msg.targetMinor() == CMMsg.TYP_HEALING)) {
				int amount = msg.value();
				if ((amount > 0)
						&& (msg.tool() instanceof Ability)
						&& (CMath.bset(((Ability) msg.tool()).flags(),
								Ability.FLAG_HEALINGMAGIC | Ability.FLAG_HOLY))
						&& (!CMath.bset(((Ability) msg.tool()).flags(),
								Ability.FLAG_UNHOLY))) {
					CMLib.combat()
							.postDamage(msg.source(), mob, msg.tool(), amount,
									CMMsg.MASK_ALWAYS | CMMsg.TYP_ACID,
									Weapon.TYPE_BURNING,
									"The healing magic from <S-NAME> <DAMAGES> <T-NAMESELF>.");
					if ((mob.getVictim() == null) && (mob != msg.source())
							&& (mob.isMonster()))
						mob.setVictim(msg.source());
				}
				return false;
			} else if ((msg.amITarget(mob))
					&& (msg.targetMinor() == CMMsg.TYP_DAMAGE)
					&& ((msg.targetMinor() == CMMsg.TYP_UNDEAD) || ((msg.tool() instanceof Ability)
							&& (CMath.bset(((Ability) msg.tool()).flags(),
									Ability.FLAG_UNHOLY)) && (!CMath.bset(
							((Ability) msg.tool()).flags(), Ability.FLAG_HOLY))))) {
				int amount = msg.value();
				if (amount > 0)
					msg.modify(msg.source(), mob, msg.tool(), CMMsg.MASK_ALWAYS
							| CMMsg.TYP_CAST_SPELL, CMMsg.MSG_HEALING,
							CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL,
							"The harming magic heals <T-NAMESELF>.");
			} else if ((msg.amITarget(mob))
					&& (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS) || (msg
							.targetMinor() == CMMsg.TYP_DAMAGE))
					&& ((msg.targetMinor() == CMMsg.TYP_DISEASE)
							|| (msg.targetMinor() == CMMsg.TYP_GAS)
							|| (msg.targetMinor() == CMMsg.TYP_MIND)
							|| (msg.targetMinor() == CMMsg.TYP_PARALYZE)
							|| (msg.targetMinor() == CMMsg.TYP_POISON)
							|| (msg.sourceMinor() == CMMsg.TYP_DISEASE)
							|| (msg.sourceMinor() == CMMsg.TYP_GAS)
							|| (msg.sourceMinor() == CMMsg.TYP_MIND)
							|| (msg.sourceMinor() == CMMsg.TYP_PARALYZE) || (msg
							.sourceMinor() == CMMsg.TYP_POISON))
					&& (!mob.amDead())) {
				String immunityName = "certain";
				if (msg.tool() != null)
					immunityName = msg.tool().name();
				if (mob != msg.source())
					mob.location().show(
							mob,
							msg.source(),
							CMMsg.MSG_OK_VISUAL,
							"<S-NAME> seem(s) immune to " + immunityName
									+ " attacks from <T-NAME>.");
				else
					mob.location().show(mob, msg.source(), CMMsg.MSG_OK_VISUAL,
							"<S-NAME> seem(s) immune to " + immunityName + ".");
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_POISON,
				affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_MIND,
				affectableStats.getStat(CharStats.STAT_SAVE_MIND) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_GAS,
				affectableStats.getStat(CharStats.STAT_SAVE_GAS) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_PARALYSIS,
				affectableStats.getStat(CharStats.STAT_SAVE_PARALYSIS) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_UNDEAD,
				affectableStats.getStat(CharStats.STAT_SAVE_UNDEAD) + 100);
		affectableStats.setStat(CharStats.STAT_SAVE_DISEASE,
				affectableStats.getStat(CharStats.STAT_SAVE_DISEASE) + 100);
	}

	public DeadBody getCorpseContainer(MOB mob, Room room) {
		DeadBody body = super.getCorpseContainer(mob, room);
		if ((body != null) && (mob != null)) {
			if (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE)) {
				if ((mob.name().toUpperCase().indexOf("DRACULA") >= 0)
						|| (mob.name().toUpperCase().indexOf("VAMPIRE") >= 0))
					body.addNonUninvokableEffect(CMClass
							.getAbility("Disease_Vampirism"));
				else if ((mob.name().toUpperCase().indexOf("GHOUL") >= 0)
						|| (mob.name().toUpperCase().indexOf("GHAST") >= 0))
					body.addNonUninvokableEffect(CMClass
							.getAbility("Disease_Cannibalism"));
			}
			if (ID().equals("Undead")) {
				Ability A = CMClass.getAbility("Prop_Smell");
				body.addNonUninvokableEffect(A);
				A.setMiscText(body.name() + " SMELLS HORRIBLE!");
			}
		}
		return body;
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is near destruction!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer)
					+ "^r is massively broken and damaged.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer) + "^r is very damaged.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer) + "^y is somewhat damaged.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer)
					+ "^y is very weak and slightly damaged.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer)
					+ "^p has lost stability and is weak.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer)
					+ "^p is unstable and slightly weak.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer) + "^g is unbalanced and unstable.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer) + "^g is somewhat unbalanced.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer)
					+ "^g is no longer in perfect condition.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect condition.^N";
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " blood",
						RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}