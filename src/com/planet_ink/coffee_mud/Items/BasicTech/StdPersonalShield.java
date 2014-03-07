package com.planet_ink.coffee_mud.Items.BasicTech;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Manufacturer;
import com.planet_ink.coffee_mud.Items.interfaces.Armor;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class StdPersonalShield extends StdElecItem implements Armor {
	public String ID() {
		return "StdPersonalShield";
	}

	short layer = 0;
	short layerAttributes = 0;

	public StdPersonalShield() {
		super();
		setName("a personal shield generator");
		basePhyStats.setWeight(2);
		setDisplayText("a personal shield generator sits here.");
		setDescription("The muting field generator is worn about the body and activated to use. It neutralizes weapon damage. ");
		baseGoldValue = 2500;
		basePhyStats().setLevel(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_STEEL);
		super.activate(true);
		super.setRawProperLocationBitmap(Wearable.WORN_ABOUT_BODY);
		super.setPowerCapacity(1000);
		super.setPowerRemaining(1000);
	}

	protected String fieldOnStr(MOB viewerM) {
		return (owner() instanceof MOB) ? "A field of energy surrounds <O-NAME>."
				: "A field of energy surrounds <T-NAME>.";
	}

	protected String fieldDeadStr(MOB viewerM) {
		return (owner() instanceof MOB) ? ""
				+ "The field around <O-NAME> flickers and dies out."
				: "The field around <T-NAME> flickers and dies out.";
	}

	@Override
	public TechType getTechType() {
		return TechType.PERSONAL_SHIELD;
	}

	protected boolean doShield(MOB mob, CMMsg msg, double successFactor) {
		if (mob.location() != null) {
			if (msg.tool() instanceof Weapon) {
				String s = "^F" + ((Weapon) msg.tool()).hitString(0) + "^N";
				if (s.indexOf("<DAMAGE> <T-HIM-HER>") > 0)
					mob.location().show(
							msg.source(),
							msg.target(),
							msg.tool(),
							CMMsg.MSG_OK_VISUAL,
							CMStrings.replaceAll(s, "<DAMAGE>",
									"it reflects off the shield around"));
				else if (s.indexOf("<DAMAGES> <T-HIM-HER>") > 0)
					mob.location().show(
							msg.source(),
							msg.target(),
							msg.tool(),
							CMMsg.MSG_OK_VISUAL,
							CMStrings.replaceAll(s, "<DAMAGES>",
									"reflects off the shield around"));
				else
					mob.location()
							.show(mob, msg.source(), msg.tool(),
									CMMsg.MSG_OK_VISUAL,
									"The field around <S-NAME> reflects the <O-NAMENOART> damage.");
			} else
				mob.location()
						.show(mob, msg.source(), msg.tool(),
								CMMsg.MSG_OK_VISUAL,
								"The field around <S-NAME> reflects the <O-NAMENOART> damage.");
		}
		return false;
	}

	protected boolean doesShield(MOB mob, CMMsg msg, double successFactor) {
		return (Math.random() >= successFactor) && activated();
	}

	public boolean sameAs(Environmental E) {
		if (!(E instanceof StdPersonalShield))
			return false;
		return super.sameAs(E);
	}

	public void setMiscText(String newText) {
		if (CMath.isInteger(newText))
			this.setPowerCapacity(CMath.s_int(newText));
		super.setMiscText(newText);
	}

	public boolean okMessage(Environmental myHost, CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		if (msg.amITarget(owner()) && (owner() instanceof MOB)) {
			MOB mob = (MOB) owner();
			switch (msg.targetMinor()) {
			case CMMsg.TYP_REMOVE:
			case CMMsg.TYP_DROP:
				if (activated())
					msg.addTrailerMsg(CMClass.getMsg(mob, this, null,
							CMMsg.MSG_OK_VISUAL, CMMsg.TYP_DEACTIVATE
									| CMMsg.MASK_ALWAYS, CMMsg.MSG_OK_VISUAL,
							fieldDeadStr(msg.source())));
				break;
			case CMMsg.TYP_DAMAGE: // remember 50% miss rate
				if ((activated()) && (powerRemaining() > 0)
						&& (!amWearingAt(Item.IN_INVENTORY))) {
					double successFactor = 0.5;
					final Manufacturer m = getFinalManufacturer();
					successFactor = m.getReliabilityPct() * successFactor;
					int weaponTech = CMLib.tech().getGlobalTechLevel();
					if (msg.tool() instanceof Electronics)
						weaponTech = ((Electronics) msg.tool()).techLevel();
					int myTech = techLevel();
					int techDiff = Math.max(Math.min(myTech - weaponTech, 10),
							-10);
					if (techDiff != 0)
						successFactor += (0.05) * techDiff;
					if (doesShield(mob, msg, successFactor)) {
						long powerConsumed = Math.round(msg.value()
								* m.getEfficiencyPct());
						if (powerRemaining() >= powerConsumed) {
							setPowerRemaining(powerRemaining() - powerConsumed);
							if (!doShield(mob, msg, successFactor))
								return false;
						} else
							setPowerRemaining(0);
					}
					if (powerRemaining() <= 0) {
						setPowerRemaining(0);
						CMMsg msg2 = CMClass
								.getMsg(mob, this, null, CMMsg.MSG_OK_VISUAL,
										CMMsg.TYP_DEACTIVATE
												| CMMsg.MASK_ALWAYS,
										CMMsg.MSG_OK_VISUAL,
										fieldDeadStr(msg.source()));
						if (mob.location() != null)
							mob.location().send(mob, msg2);
					}
				}
				break;
			}
		}
		return true;
	}

	public void executeMsg(Environmental host, CMMsg msg) {
		if (msg.amITarget(this)) {
			switch (msg.targetMinor()) {
			case CMMsg.TYP_LOOK:
				super.executeMsg(host, msg);
				if (CMLib.flags().canBeSeenBy(this, msg.source())) {
					msg.source().tell(
							name()
									+ " is currently "
									+ (activated() ? "activated"
											: "deactivated")
									+ " and is at "
									+ Math.round(CMath.div(powerRemaining(),
											powerCapacity()) * 100.0)
									+ "% power.");
				}
				return;
			case CMMsg.TYP_ACTIVATE: {
				if ((msg.source().location() != null)
						&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
					msg.source()
							.location()
							.show(msg.source(), this, owner(),
									CMMsg.MSG_OK_VISUAL, fieldOnStr(null));
				this.activate(true);
				Physical P = owner();
				if (P != null) {
					P.recoverPhyStats();
					if (P instanceof MOB) {
						((MOB) P).recoverCharStats();
						((MOB) P).recoverMaxState();
					}
				}
				break;
			}
			case CMMsg.TYP_DEACTIVATE: {
				if ((msg.source().location() != null)
						&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
					msg.source()
							.location()
							.show(msg.source(), this, owner(),
									CMMsg.MSG_OK_VISUAL, fieldDeadStr(null));
				this.activate(false);
				Physical P = owner();
				if (P != null) {
					P.recoverPhyStats();
					if (P instanceof MOB) {
						((MOB) P).recoverCharStats();
						((MOB) P).recoverMaxState();
					}
				}
				break;
			}
			default:
				super.executeMsg(host, msg);
				break;
			}
		} else if (msg.amITarget(owner()) && (owner() instanceof MOB)
				&& (!amWearingAt(Item.IN_INVENTORY))) {
			switch (msg.targetMinor()) {
			case CMMsg.TYP_LOOK:
				super.executeMsg(host, msg);
				if (CMLib.flags().canBeSeenBy(owner(), msg.source())
						&& (activated()) && (powerRemaining() > 0))
					msg.source().tell(msg.source(), this, owner(),
							fieldOnStr(msg.source()));
				return;
			}
		}
		super.executeMsg(host, msg);
	}

	@Override
	public short getClothingLayer() {
		return layer;
	}

	@Override
	public void setClothingLayer(short newLayer) {
		layer = newLayer;
	}

	@Override
	public short getLayerAttributes() {
		return layerAttributes;
	}

	@Override
	public void setLayerAttributes(short newAttributes) {
		layerAttributes = newAttributes;
	}

	@Override
	public boolean canWear(MOB mob, long where) {
		if (where == 0)
			return (whereCantWear(mob) == 0);
		if ((rawProperLocationBitmap() & where) != where)
			return false;
		return mob.freeWearPositions(where, getClothingLayer(),
				getLayerAttributes()) > 0;
	}

}