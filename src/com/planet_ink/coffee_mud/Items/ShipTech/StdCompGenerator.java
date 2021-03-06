package com.planet_ink.coffee_mud.Items.ShipTech;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class StdCompGenerator extends StdCompFuelConsumer implements
		Electronics.PowerGenerator {
	public String ID() {
		return "StdCompGenerator";
	}

	public StdCompGenerator() {
		super();
		setName("a generator");
		setDisplayText("a generator sits here.");
		setDescription("If you put the right fuel in it, I'll bet it makes power.");

		material = RawMaterial.RESOURCE_STEEL;
		setPowerCapacity(1000);
		setPowerRemaining(0);
		baseGoldValue = 0;
		recoverPhyStats();
	}

	protected int generatedAmtPerTick = 1;

	@Override
	public int getGeneratedAmountPerTick() {
		return generatedAmtPerTick;
	}

	@Override
	public void setGenerationAmountPerTick(int amt) {
		generatedAmtPerTick = amt;
	}

	@Override
	public TechType getTechType() {
		return TechType.SHIP_GENERATOR;
	}

	public void executeMsg(Environmental myHost, CMMsg msg) {
		super.executeMsg(myHost, msg);
		if (msg.amITarget(this)) {
			switch (msg.targetMinor()) {
			case CMMsg.TYP_GET:
				clearFuelCache();
				break;
			case CMMsg.TYP_PUT:
				clearFuelCache();
				break;
			case CMMsg.TYP_ACTIVATE:
				if ((msg.source().location() != null)
						&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
					msg.source()
							.location()
							.show(msg.source(), this, CMMsg.MSG_OK_VISUAL,
									"<S-NAME> power(s) up <T-NAME>.");
				this.activate(true);
				break;
			case CMMsg.TYP_DEACTIVATE:
				if ((msg.source().location() != null)
						&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
					msg.source()
							.location()
							.show(msg.source(), this, CMMsg.MSG_OK_VISUAL,
									"<S-NAME> shut(s) down <T-NAME>.");
				this.activate(false);
				break;
			case CMMsg.TYP_LOOK:
				if (CMLib.flags().canBeSeenBy(this, msg.source()))
					msg.source().tell(
							name()
									+ " is currently "
									+ (activated() ? "delivering power.\n\r"
											: "deactivated/shut down.\n\r"));
				return;
			case CMMsg.TYP_POWERCURRENT:
				if (msg.value() == 0) {
					if ((((powerCapacity() - powerRemaining()) >= getGeneratedAmountPerTick()) || (powerRemaining() < getGeneratedAmountPerTick()))
							&& (Math.random() < getFinalManufacturer()
									.getReliabilityPct())) {
						double generatedAmount = getGeneratedAmountPerTick();
						generatedAmount *= getFinalManufacturer()
								.getEfficiencyPct();
						generatedAmount *= getInstalledFactor();
						if (subjectToWearAndTear() && (usesRemaining() <= 200))
							generatedAmount *= CMath
									.div(usesRemaining(), 100.0);
						long newAmount = powerRemaining()
								+ Math.round(generatedAmount);
						if (newAmount > powerCapacity())
							newAmount = powerCapacity();
						setPowerRemaining(newAmount);
					}
				}
				break;
			}
		}
	}
}
