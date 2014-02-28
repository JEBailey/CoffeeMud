package com.planet_ink.coffee_mud.Items.MiscMagic;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.Basic.StdDrink;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Potion;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.SpellHolder;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class StdPotion extends StdDrink implements Potion {
	public String ID() {
		return "StdPotion";
	}

	public StdPotion() {
		super();

		setName("a potion");
		basePhyStats.setWeight(1);
		setDisplayText("An empty potion sits here.");
		setDescription("An empty potion with strange residue.");
		secretIdentity = "What was once a powerful potion.";
		capacity = 1;
		containType = Container.CONTAIN_LIQUID;
		liquidType = RawMaterial.RESOURCE_DRINKABLE;
		baseGoldValue = 200;
		material = RawMaterial.RESOURCE_GLASS;
		recoverPhyStats();
	}

	public int liquidType() {
		return RawMaterial.RESOURCE_DRINKABLE;
	}

	public boolean isDrunk() {
		return (getSpellList().toUpperCase().indexOf(";DRUNK") >= 0);
	}

	public int value() {
		if (isDrunk())
			return 0;
		return super.value();
	}

	public void setDrunk(boolean isTrue) {
		if (isTrue && isDrunk())
			return;
		if ((!isTrue) && (!isDrunk()))
			return;
		if (isTrue)
			setSpellList(getSpellList() + ";DRUNK");
		else {
			String list = "";
			List<Ability> theSpells = getSpells();
			for (int v = 0; v < theSpells.size(); v++)
				list += theSpells.get(v).ID() + ";";
			setSpellList(list);
		}
	}

	public void drinkIfAble(MOB owner, Physical drinkerTarget) {
		List<Ability> spells = getSpells();
		if (owner.isMine(this))
			if ((!isDrunk()) && (spells.size() > 0)) {
				MOB caster = CMLib.map().getFactoryMOB(owner.location());
				MOB finalCaster = (owner != drinkerTarget) ? owner : caster;
				for (int i = 0; i < spells.size(); i++) {
					Ability thisOne = (Ability) spells.get(i).copyOf();
					if ((drinkerTarget instanceof Item)
							&& ((!thisOne.canTarget(drinkerTarget)) && (!thisOne
									.canAffect(drinkerTarget))))
						continue;
					int level = phyStats().level();
					int lowest = CMLib.ableMapper().lowestQualifyingLevel(
							thisOne.ID());
					if (level < lowest)
						level = lowest;
					caster.basePhyStats().setLevel(level);
					caster.phyStats().setLevel(level);
					thisOne.invoke(finalCaster, drinkerTarget, true, level);
					setDrunk(true);
					setLiquidRemaining(0);
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

	public static List<Ability> getSpells(SpellHolder me) {
		int baseValue = 200;
		Vector<Ability> theSpells = new Vector<Ability>();
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
				baseValue += (100 * CMLib.ableMapper().lowestQualifyingLevel(
						A.ID()));
				theSpells.addElement(A);
			}
		}
		me.setBaseValue(baseValue);
		me.recoverPhyStats();
		return theSpells;
	}

	public List<Ability> getSpells() {
		return getSpells(this);
	}

	public String secretIdentity() {
		return StdScroll.makeSecretIdentity("potion", super.secretIdentity(),
				"", getSpells(this));
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((msg.amITarget(this)) && (msg.targetMinor() == CMMsg.TYP_DRINK)
				&& (msg.othersMessage() == null)
				&& (msg.sourceMessage() == null))
			return true;
		else if ((msg.tool() == this) && (msg.targetMinor() == CMMsg.TYP_POUR))
			return true;
		return super.okMessage(myHost, msg);
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if (msg.amITarget(this)) {
			MOB mob = msg.source();
			switch (msg.targetMinor()) {
			case CMMsg.TYP_DRINK:
				if ((msg.sourceMessage() == null)
						&& (msg.othersMessage() == null)) {
					drinkIfAble(mob, mob);
					mob.tell(name() + " vanishes!");
					destroy();
					mob.recoverPhyStats();
				} else {
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),
							msg.target(), msg.tool(), CMMsg.NO_EFFECT, null,
							msg.targetCode(), msg.targetMessage(),
							CMMsg.NO_EFFECT, null));
					super.executeMsg(myHost, msg);
				}
				break;
			default:
				super.executeMsg(myHost, msg);
				break;
			}
		} else if ((msg.tool() == this)
				&& (msg.targetMinor() == CMMsg.TYP_POUR)
				&& (msg.target() instanceof Physical)) {
			if ((msg.sourceMessage() == null) && (msg.othersMessage() == null)) {
				drinkIfAble(msg.source(), (Physical) msg.target());
				msg.source().tell(name() + " vanishes!");
				destroy();
				msg.source().recoverPhyStats();
				((Physical) msg.target()).recoverPhyStats();
			} else {
				msg.addTrailerMsg(CMClass.getMsg(msg.source(), msg.target(),
						msg.tool(), CMMsg.NO_EFFECT, null, msg.targetCode(),
						msg.targetMessage(), CMMsg.NO_EFFECT, null));
				super.executeMsg(myHost, msg);
			}
		} else
			super.executeMsg(myHost, msg);
	}

}
