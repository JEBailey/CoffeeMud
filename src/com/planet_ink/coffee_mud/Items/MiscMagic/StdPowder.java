package com.planet_ink.coffee_mud.Items.MiscMagic;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.Basic.StdItem;
import com.planet_ink.coffee_mud.Items.interfaces.MagicDust;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.SpellHolder;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

/**
 * <p>
 * Title: False Realities Flavored CoffeeMUD
 * </p>
 * <p>
 * Description: The False Realities Version of CoffeeMUD
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004 Jeremy Vyska
 * </p>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License.
 * <p>
 * You may obtain a copy of the License at
 * 
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * <p>
 * distributed under the License is distributed on an "AS IS" BASIS,
 * <p>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p>
 * See the License for the specific language governing permissions and
 * <p>
 * limitations under the License.
 * <p>
 * Company: http://www.falserealities.com
 * </p>
 * 
 * @author FR - Jeremy Vyska; CM - Bo Zimmerman
 * @version 1.0.0.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class StdPowder extends StdItem implements MagicDust {
	public String ID() {
		return "StdPowder";
	}

	public StdPowder() {
		super();

		setName("a pile of powder");
		basePhyStats.setWeight(1);
		setDisplayText("A small pile of powder sits here.");
		setDescription("A small pile of powder.");
		secretIdentity = "This is a pile of inert materials.";
		baseGoldValue = 0;
		material = RawMaterial.RESOURCE_ASH;
		recoverPhyStats();
	}

	public void spreadIfAble(MOB mob, Physical target) {
		List<Ability> spells = getSpells();
		if (spells.size() > 0)
			for (int i = 0; i < spells.size(); i++) {
				Ability thisOne = (Ability) spells.get(i).copyOf();
				if (thisOne.canTarget(target)) {
					if ((malicious(this)) || (!(target instanceof MOB)))
						thisOne.invoke(mob, target, true, phyStats().level());
					else
						thisOne.invoke((MOB) target, (MOB) target, true,
								phyStats().level());
				}
			}
		destroy();
	}

	// That which makes Powders work. They're an item that when successfully
	// dusted on a target, are 'cast' on the target
	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if (msg.sourceMinor() == CMMsg.TYP_THROW) {
			if ((msg.tool() == this) && (msg.target() instanceof Physical))
				spreadIfAble(msg.source(), (Physical) msg.target());
			else
				super.executeMsg(myHost, msg);
		} else
			super.executeMsg(myHost, msg);
	}

	public String getSpellList() {
		return miscText;
	}

	public void setSpellList(String list) {
		miscText = list;
	}

	public boolean malicious(SpellHolder me) {
		List<Ability> spells = getSpells();
		for (Ability checking : spells)
			if (checking.abstractQuality() == Ability.QUALITY_MALICIOUS)
				return true;
		return false;
	}

	public List<Ability> getSpells() {
		String names = getSpellList();

		Vector theSpells = new Vector();
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
		recoverPhyStats();
		return theSpells;
	}

	public String secretIdentity() {
		return description() + "\n\r" + super.secretIdentity();
	}

}
