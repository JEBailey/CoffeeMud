package com.planet_ink.coffee_mud.Items.Software;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.Basic.StdItem;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Software;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class StdProgram extends StdItem implements Software {
	public String ID() {
		return "StdProgram";
	}

	protected StringBuilder nextMsg = new StringBuilder("");
	protected String currentScreen = "";

	public StdProgram() {
		super();
		setName("a software disk");
		setDisplayText("a small disk sits here.");
		setDescription("It appears to be a general software program.");

		basePhyStats.setWeight(1);
		material = RawMaterial.RESOURCE_STEEL;
		baseGoldValue = 1000;
		recoverPhyStats();
	}

	public void setCircuitKey(String key) {
	}

	public int techLevel() {
		return phyStats().ability();
	}

	public void setTechLevel(int lvl) {
		basePhyStats.setAbility(lvl);
		recoverPhyStats();
	}

	public String getParentMenu() {
		return "";
	}

	public String getInternalName() {
		return "";
	}

	public boolean isActivationString(String word) {
		return false;
	}

	public boolean isDeActivationString(String word) {
		return false;
	}

	public boolean isCommandString(String word, boolean isActive) {
		return false;
	}

	@Override
	public TechType getTechType() {
		return TechType.PERSONAL_SOFTWARE;
	}

	public String getActivationMenu() {
		return "";
	}

	public String getCurrentScreenDisplay() {
		return currentScreen;
	}

	public void setCurrentScreenDisplay(String msg) {
		this.currentScreen = msg;
	}

	public String getScreenMessage() {
		synchronized (nextMsg) {
			final String msg = nextMsg.toString();
			nextMsg.setLength(0);
			return msg;
		}
	}

	public void addScreenMessage(String msg) {
		synchronized (nextMsg) {
			nextMsg.append(msg).append("\n\r");
		}
	}

	protected void forceUpMenu() {
		if ((container() instanceof Electronics.Computer)
				&& (((Electronics.Computer) container()).getActiveMenu()
						.equals(getInternalName())))
			((Electronics.Computer) container()).setActiveMenu(getParentMenu());
	}

	protected void forceNewMessageScan() {
		if (container() instanceof Electronics.Computer)
			((Electronics.Computer) container()).forceReadersSeeNew();
	}

	protected void forceNewMenuRead() {
		if (container() instanceof Electronics.Computer)
			((Electronics.Computer) container()).forceReadersMenu();
	}

	public boolean checkActivate(MOB mob, String message) {
		return true;
	}

	public boolean checkDeactivate(MOB mob, String message) {
		return true;
	}

	public boolean checkTyping(MOB mob, String message) {
		return true;
	}

	public boolean checkPowerCurrent(int value) {
		return true;
	}

	public boolean okMessage(Environmental host, CMMsg msg) {
		if (msg.amITarget(this)) {
			switch (msg.targetMinor()) {
			case CMMsg.TYP_ACTIVATE:
				if (!checkActivate(msg.source(), msg.targetMessage()))
					return false;
				break;
			case CMMsg.TYP_DEACTIVATE:
				if (!checkDeactivate(msg.source(), msg.targetMessage()))
					return false;
				break;
			case CMMsg.TYP_WRITE:
				if (!checkTyping(msg.source(), msg.targetMessage()))
					return false;
				break;
			case CMMsg.TYP_POWERCURRENT:
				if (!checkPowerCurrent(msg.value()))
					return false;
				break;
			}
		}
		return super.okMessage(host, msg);
	}

	public void onActivate(MOB mob, String message) {

	}

	public void onDeactivate(MOB mob, String message) {

	}

	public void onTyping(MOB mob, String message) {

	}

	public void onPowerCurrent(int value) {

	}

	public void executeMsg(Environmental host, CMMsg msg) {
		if (msg.amITarget(this)) {
			switch (msg.targetMinor()) {
			case CMMsg.TYP_ACTIVATE:
				onActivate(msg.source(), msg.targetMessage());
				break;
			case CMMsg.TYP_DEACTIVATE:
				onDeactivate(msg.source(), msg.targetMessage());
				break;
			case CMMsg.TYP_WRITE:
				onTyping(msg.source(), msg.targetMessage());
				break;
			case CMMsg.TYP_POWERCURRENT:
				onPowerCurrent(msg.value());
				break;
			}
		}
		super.executeMsg(host, msg);
	}
}
