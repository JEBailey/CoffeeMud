package com.planet_ink.coffee_mud.Items.MiscMagic;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.Basic.StdFood;
import com.planet_ink.coffee_mud.Items.interfaces.Pill;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.SpellHolder;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class StdPill extends StdFood implements Pill {
	public String ID() {
		return "StdPill";
	}

	protected Ability theSpell;

	public StdPill() {
		super();

		setName("a pill");
		basePhyStats.setWeight(1);
		setDisplayText("A strange pill lies here.");
		setDescription("Large and round, with strange markings.");
		secretIdentity = "Surely this is a potent pill!";
		baseGoldValue = 200;
		recoverPhyStats();
		material = RawMaterial.RESOURCE_CORN;
	}

	public String secretIdentity() {
		return StdScroll.makeSecretIdentity("pill", super.secretIdentity(), "",
				getSpells(this));
	}

	public void eatIfAble(MOB mob) {
		List<Ability> spells = getSpells();
		if ((mob.isMine(this)) && (spells.size() > 0)) {
			MOB caster = CMLib.map().getFactoryMOB(mob.location());
			for (int i = 0; i < spells.size(); i++) {
				Ability thisOne = (Ability) spells.get(i).copyOf();
				int level = phyStats().level();
				int lowest = CMLib.ableMapper().lowestQualifyingLevel(
						thisOne.ID());
				if (level < lowest)
					level = lowest;
				caster.basePhyStats().setLevel(level);
				caster.phyStats().setLevel(level);
				thisOne.invoke(caster, mob, true, level);
			}
			caster.destroy();
		}
	}

	public String getSpellList() {
		return miscText;
	}

	public void setSpellList(String list) {
		miscText = list;
	}

	public static Vector getSpells(SpellHolder me) {
		Vector theSpells = new Vector();
		String names = me.getSpellList();
		List<String> parsedSpells = CMParms.parseSemicolons(names, true);
		for (String thisOne : parsedSpells) {
			thisOne = thisOne.trim();
			String parms = "";
			int x = thisOne.indexOf('(');
			if ((x > 0) && (thisOne.endsWith(")"))) {
				parms = thisOne.substring(x + 1, thisOne.length() - 1);
				thisOne = thisOne.substring(0, x).trim();
			}
			Ability A = CMClass.getAbility(thisOne);
			if ((A != null)
					&& ((A.classificationCode() & Ability.ALL_DOMAINS) != Ability.DOMAIN_ARCHON)) {
				A = (Ability) A.copyOf();
				A.setMiscText(parms);
				theSpells.addElement(A);
			}
		}
		me.recoverPhyStats();
		return theSpells;
	}

	public List<Ability> getSpells() {
		return getSpells(this);
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if (msg.amITarget(this)) {
			MOB mob = msg.source();
			switch (msg.targetMinor()) {
			case CMMsg.TYP_EAT:
				if ((msg.sourceMessage() == null)
						&& (msg.othersMessage() == null)) {
					eatIfAble(mob);
					super.executeMsg(myHost, msg);
				} else
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),
							msg.target(), msg.tool(), CMMsg.NO_EFFECT, null,
							msg.targetCode(), msg.targetMessage(),
							CMMsg.NO_EFFECT, null));
				break;
			default:
				super.executeMsg(myHost, msg);
				break;
			}
		} else
			super.executeMsg(myHost, msg);
	}
}
