package com.planet_ink.coffee_mud.Items.Basic;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.FalseLimb;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

/*
 Copyright 2008-2014 Bo Zimmerman

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
public class StdLimb extends StdItem implements FalseLimb {
	public String ID() {
		return "StdLimb";
	}

	protected int partnum = -1;
	protected long wearplace = -1;
	protected short layer = -10;
	protected short layerAttributes = 0;

	public StdLimb() {
		super();
		setName("a false limb");
		basePhyStats.setWeight(1);
		setDisplayText("a false limb is here.");
		setDescription("Looks like a false limb.");
		properWornBitmap = Wearable.WORN_HELD | Wearable.WORN_FLOATING_NEARBY;
		wornLogicalAnd = false;
		baseGoldValue = 10;
		material = RawMaterial.RESOURCE_MEAT;
		recoverPhyStats();
	}

	public void setName(String name) {
		super.setName(name);
		wearplace = -1;
		partnum = -1;
	}

	public boolean okMessage(Environmental host, CMMsg msg) {
		if (!super.okMessage(host, msg))
			return false;
		if ((msg.target() == this) && (msg.targetMinor() == CMMsg.TYP_REMOVE)
				&& (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
				&& (owner() instanceof MOB)) {
			MOB mob = (MOB) owner();
			Wearable.CODES codes = Wearable.CODES.instance();
			for (int w = 0; w < codes.total(); w++)
				if ((amWearingAt(codes.get(w)))
						&& (codes.get(w) != Wearable.IN_INVENTORY)
						&& (codes.dependency_masks()[w] > 0))
					for (int w2 = 0; w2 < codes.total(); w2++)
						if (!amWearingAt(codes.get(w2))
								&& (codes.get(w2) != Wearable.IN_INVENTORY)
								&& (CMath.bset(codes.dependency_masks()[w],
										codes.get(w2)))) {
							Item I = mob.fetchFirstWornItem(codes.get(w2));
							if ((I != null) && (I != this)) {
								msg.source()
										.tell(mob, I, null,
												"You'll need to remove <T-NAMESELF> first.");
								return false;
							}
						}
		}
		return true;
	}

	public void setSecretIdentity(String id) {
		super.setSecretIdentity(id);
		wearplace = -1;
		partnum = -1;
	}

	public int partNum() {
		if (partnum >= 0)
			return partnum;
		for (int i = 0; i < Race.BODYPARTSTR.length; i++)
			if ((name().toUpperCase().endsWith(Race.BODYPARTSTR[i]))
					|| (rawSecretIdentity().toUpperCase()
							.endsWith(Race.BODYPARTSTR[i]))) {
				partnum = i;
				break;
			}
		return partnum;
	}

	public void setPartNum(int partNumber) {
		partnum = partNumber;
	}

	public long wearPlace() {
		if (wearplace >= 0)
			return wearplace;
		int num = partNum();
		if (num < 0)
			wearplace = 0;
		else
			wearplace = Race.BODY_WEARVECTOR[num];
		return wearplace;
	}

	public void setWearPlace(long wearPlace) {
		wearplace = wearPlace;
	}

	public void affectCharStats(MOB affected, CharStats affectableStats) {
		super.affectCharStats(affected, affectableStats);

		if ((!amWearingAt(Wearable.IN_INVENTORY))
				&& (!amWearingAt(Wearable.WORN_HELD))
				&& (!amWearingAt(Wearable.WORN_FLOATING_NEARBY))
				&& (!amWearingAt(Wearable.WORN_WIELD))) {
			if (affected.charStats().getBodyPart(partNum()) < affected
					.charStats().getMyRace().bodyMask()[partNum()])
				affectableStats.alterBodypart(partNum(), phyStats().ability());
			else
				setRawWornCode(0);
		}
	}

	public boolean canWear(MOB mob, long where) {
		if (where == Wearable.WORN_HELD)
			return super.canWear(mob, where);
		if (where == Wearable.WORN_FLOATING_NEARBY)
			return false;
		if (partNum() < 0)
			return false;
		if ((where != 0) && (where != wearPlace()))
			return false;
		int numRacialTotal = mob.charStats().getMyRace().bodyMask()[partNum()];
		int numWorkingParts = mob.charStats().getBodyPart(partNum());
		// now add in other NON-FUNCTIONAL limb things worn
		// FUNCTIONAL limbs are already included in numWorkingParts
		for (int i = 0; i < mob.numItems(); i++) {
			Item I = mob.getItem(i);
			if ((I instanceof StdLimb)
					&& (((StdLimb) I).partNum() == partNum())
					&& (I.amWearingAt(wearPlace())) && (I.container() == null))
				numWorkingParts++;
		}
		if (numWorkingParts >= numRacialTotal)
			return false;
		return true;
	}

	public boolean fitsOn(long wornCode) {
		if (wornCode == Wearable.WORN_HELD)
			return super.fitsOn(wornCode);
		if (wornCode == Wearable.WORN_FLOATING_NEARBY)
			return false;
		if (wornCode <= 0)
			return true;
		return wearPlace() == wornCode;
	}

	protected boolean canWearComplete(MOB mob) {
		if (partNum() < 0) {
			mob.tell("This limb looks malformed.");
			return false;
		}
		if (!canWear(mob, 0)) {
			mob.tell("You don't have any empty sockets to wear " + name()
					+ " on.");
			return false;
		}
		return true;
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
}
