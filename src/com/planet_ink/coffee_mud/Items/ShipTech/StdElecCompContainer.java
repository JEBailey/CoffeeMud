package com.planet_ink.coffee_mud.Items.ShipTech;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.BasicTech.StdElecContainer;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.ShipComponent;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;

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
public class StdElecCompContainer extends StdElecContainer implements
		ShipComponent {
	public String ID() {
		return "StdElecCompContainer";
	}

	protected float installedFactor = 1.0f;

	public StdElecCompContainer() {
		super();
		setName("a component container");
		basePhyStats.setWeight(500);
		setDisplayText("a component container sits here.");
		setDescription("");
		baseGoldValue = 500000;
		setUsesRemaining(100);
		basePhyStats().setLevel(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_STEEL);
	}

	@Override
	public float getInstalledFactor() {
		return installedFactor;
	}

	@Override
	public void setInstalledFactor(float pct) {
		installedFactor = pct;
	}

	@Override
	public boolean subjectToWearAndTear() {
		return true;
	}

	public boolean sameAs(Environmental E) {
		if (!(E instanceof StdElecCompContainer))
			return false;
		return super.sameAs(E);
	}

	protected volatile String circuitKey = null;

	public void destroy() {
		if ((!destroyed) && (circuitKey != null)) {
			CMLib.tech().unregisterElectronics(this, circuitKey);
			circuitKey = null;
		}
		super.destroy();
	}

	public void setOwner(ItemPossessor newOwner) {
		final ItemPossessor prevOwner = super.owner;
		super.setOwner(newOwner);
		if (prevOwner != newOwner) {
			if (newOwner instanceof Room)
				circuitKey = CMLib.tech().registerElectrics(this, circuitKey);
			else {
				CMLib.tech().unregisterElectronics(this, circuitKey);
				circuitKey = null;
			}
		}
	}

	protected static final boolean isThisPanelActivated(Electronics.ElecPanel E) {
		if (!E.activated())
			return false;
		if (E.container() instanceof Electronics.ElecPanel)
			return isThisPanelActivated((Electronics.ElecPanel) E.container());
		return true;
	}

	public static final boolean isAllWiringConnected(Electronics E) {
		if (E instanceof Electronics.ElecPanel)
			return isThisPanelActivated((Electronics.ElecPanel) E);
		if (E.container() instanceof Electronics.ElecPanel)
			return isThisPanelActivated((Electronics.ElecPanel) E.container());
		return true;
	}

	public boolean okMessage(Environmental host, CMMsg msg) {
		if (msg.amITarget(this)) {
			switch (msg.targetMinor()) {
			case CMMsg.TYP_ACTIVATE:
				if (!isAllWiringConnected(this)) {
					if (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG))
						msg.source().tell(
								"The panel containing " + name()
										+ " is not activated or connected.");
					return false;
				}
				break;
			case CMMsg.TYP_DEACTIVATE:
				break;
			case CMMsg.TYP_LOOK:
				break;
			case CMMsg.TYP_POWERCURRENT:
				if ((!(this instanceof Electronics.FuelConsumer))
						&& (!(this instanceof Electronics.PowerGenerator))
						&& activated() && (powerNeeds() > 0)
						&& (msg.value() > 0)) {
					double amtToTake = Math.min((double) powerNeeds(),
							(double) msg.value());
					msg.setValue(msg.value() - (int) Math.round(amtToTake));
					amtToTake *= getFinalManufacturer().getEfficiencyPct();
					if (subjectToWearAndTear() && (usesRemaining() <= 200))
						amtToTake *= CMath.div(usesRemaining(), 100.0);
					setPowerRemaining(Math.min(powerCapacity(),
							Math.round(amtToTake) + powerRemaining()));
				}
				break;
			}
		}
		return super.okMessage(host, msg);
	}

	public void executeMsg(Environmental host, CMMsg msg) {
		if (msg.amITarget(this)) {
			switch (msg.targetMinor()) {
			case CMMsg.TYP_ACTIVATE:
				if ((msg.source().location() != null)
						&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
					msg.source()
							.location()
							.show(msg.source(), this, CMMsg.MSG_OK_VISUAL,
									"<S-NAME> activate(s) <T-NAME>.");
				this.activate(true);
				break;
			case CMMsg.TYP_DEACTIVATE:
				if ((msg.source().location() != null)
						&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
					msg.source()
							.location()
							.show(msg.source(), this, CMMsg.MSG_OK_VISUAL,
									"<S-NAME> deactivate(s) <T-NAME>.");
				this.activate(false);
				break;
			case CMMsg.TYP_LOOK:
				super.executeMsg(host, msg);
				if (CMLib.flags().canBeSeenBy(this, msg.source()))
					msg.source().tell(
							name()
									+ " is currently "
									+ (activated() ? "connected.\n\r"
											: "deactivated/disconnected.\n\r"));
				return;
			}
		}
		super.executeMsg(host, msg);
	}
}
