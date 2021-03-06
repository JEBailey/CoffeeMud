package com.planet_ink.coffee_mud.Items.BasicTech;

import java.util.LinkedList;
import java.util.List;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Software;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
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
public class StdTriCorder extends StdElecContainer implements
		Electronics.Computer {
	public String ID() {
		return "StdTriCorder";
	}

	protected final static int POWER_RATE = 4; // how often (in ticks) an
												// activated tricorder loses a
												// tick of power. at 1000 power,
												// this is 1 hr/rate (4 hrs
												// total)

	protected MOB lastReader = null;
	protected volatile long nextSoftwareCheck = System.currentTimeMillis()
			+ (10 * 1000);
	protected List<Software> software = null;
	protected String currentMenu = "";
	protected int nextPowerCycleCtr = POWER_RATE + 1;

	public StdTriCorder() {
		super();
		setName("a tri-corder");
		basePhyStats.setWeight(2);
		setDisplayText("a personal scanning device sits here.");
		setDescription("For all your scanning and mobile computing needs.");
		baseGoldValue = 2500;
		basePhyStats().setLevel(1);
		setMaterial(RawMaterial.RESOURCE_STEEL);
		super.activate(true);
		setLidsNLocks(false, true, false, false);
		setCapacity(3);
		super.setPowerCapacity(1000);
		super.setPowerRemaining(1000);
		basePhyStats.setSensesMask(basePhyStats.sensesMask()
				| PhyStats.SENSE_ITEMREADABLE);
		recoverPhyStats();
	}

	@Override
	public TechType panelType() {
		return TechType.PERSONAL_SOFTWARE;
	}

	@Override
	public void setPanelType(TechType type) {
	}

	@Override
	public void setActiveMenu(String internalName) {
		currentMenu = internalName;
	}

	@Override
	public String getActiveMenu() {
		return currentMenu;
	}

	@Override
	public TechType getTechType() {
		return TechType.PERSONAL_SENSOR;
	}

	@Override
	public boolean canContain(Environmental E) {
		return (E instanceof Software)
				&& (((Software) E).getTechType() == TechType.PERSONAL_SOFTWARE);
	}

	public List<Software> getSoftware() {
		if ((software == null)
				|| (System.currentTimeMillis() > nextSoftwareCheck)) {
			final List<Item> list = getContents();
			final LinkedList<Software> softwareList = new LinkedList<Software>();
			for (Item I : list)
				if (I instanceof Software)
					softwareList.add((Software) I);
			nextSoftwareCheck = System.currentTimeMillis() + (10 * 1000);
			software = softwareList;
		}
		return software;
	}

	public void setReadableText(String text) {
		// important that this does nothing
	}

	public String readableText() {
		final StringBuilder str = new StringBuilder("");
		str.append("\n\r");
		if (!activated())
			str.append("The screen is blank.  Try activating/booting it first.");
		else {
			final List<Software> software = getSoftware();
			synchronized (software) {
				boolean isInternal = false;
				for (final Software S : software) {
					if (S.getInternalName().equals(currentMenu)) {
						str.append(S.getCurrentScreenDisplay());
						isInternal = true;
					} else if (S.getParentMenu().equals(currentMenu)) {
						str.append(S.getActivationMenu()).append("\n\r");
					}
				}
				if (isInternal) {
					str.append("\n\rEnter \"<\" to return to the previous menu.");
				} else if (software.size() > 0) {
					str.append("\n\rType in a command:");
				} else {
					str.append("\n\rThis system is ready to receive software.");
				}
			}
		}

		return str.toString();
	}

	@Override
	public List<MOB> getCurrentReaders() {
		List<MOB> readers = new LinkedList<MOB>();
		if (amDestroyed())
			return readers;
		if (owner() instanceof MOB)
			readers.add((MOB) owner());
		return readers;
	}

	@Override
	public void setOwner(ItemPossessor owner) {
		final ItemPossessor prevOwner = super.owner;
		super.setOwner(owner);
		if ((prevOwner != owner) && (owner != null)) {
			if (!CMLib.threads().isTicking(this, Tickable.TICKID_ELECTRONICS))
				CMLib.threads().startTickDown(this,
						Tickable.TICKID_ELECTRONICS, 1);
		}
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg) {
		if (msg.amITarget(this)) {
			switch (msg.targetMinor()) {
			case CMMsg.TYP_READ:
			case CMMsg.TYP_WRITE:
				if (this.amWearingAt(Item.IN_INVENTORY)) {
					msg.source().tell(name() + " needs to be held first.");
					return false;
				}
				if (!activated()) {
					msg.source().tell(name() + " is not activated/booted up.");
					return false;
				}
				return true;
			case CMMsg.TYP_ACTIVATE:
				if (this.amWearingAt(Item.IN_INVENTORY)
						&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG))) {
					msg.source().tell(name() + " needs to be held first.");
					return false;
				}
				if ((msg.targetMessage() == null) && (activated())) {
					msg.source().tell(name() + " is already booted up.");
					return false;
				} else if (powerRemaining() <= 0) {
					msg.source()
							.tell(name()
									+ " won't seem to power up. Perhaps it needs power?");
					return false;
				}
				break;
			case CMMsg.TYP_DEACTIVATE:
				if (this.amWearingAt(Item.IN_INVENTORY)
						&& (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG))) {
					msg.source().tell(name() + " needs to be held first.");
					return false;
				}
				if ((msg.targetMessage() == null) && (!activated())) {
					msg.source().tell(name() + " is already shut down.");
					return false;
				}
				break;
			}
		}
		return super.okMessage(host, msg);
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg) {
		if (msg.amITarget(this)) {
			switch (msg.targetMinor()) {
			case CMMsg.TYP_READ:
				if (msg.source().riding() != this)
					lastReader = msg.source();
				break;
			case CMMsg.TYP_WRITE: {
				if (msg.targetMessage() != null) {
					final List<Software> software = getSoftware();
					List<CMMsg> msgs = new LinkedList<CMMsg>();
					synchronized (software) {
						for (final Software S : software) {
							if (S.getInternalName().equals(currentMenu)) {
								if (msg.targetMessage().trim().equals("<")
										&& (currentMenu.length() > 0)) {
									msgs.add(CMClass.getMsg(msg.source(), S,
											null, CMMsg.NO_EFFECT,
											CMMsg.MASK_ALWAYS
													| CMMsg.TYP_DEACTIVATE,
											CMMsg.NO_EFFECT, null));
								} else if (S.isCommandString(
										msg.targetMessage(), true)) {
									msgs.add(CMClass.getMsg(
											msg.source(),
											S,
											null,
											CMMsg.NO_EFFECT,
											null,
											CMMsg.MASK_ALWAYS | CMMsg.TYP_WRITE,
											msg.targetMessage(),
											CMMsg.NO_EFFECT, null));
								}
							} else if ((S.getParentMenu().equals(currentMenu))
									&& (S.isCommandString(msg.targetMessage(),
											false))) {
								msgs.add(CMClass.getMsg(msg.source(), S, null,
										CMMsg.NO_EFFECT, null,
										CMMsg.MASK_ALWAYS | CMMsg.TYP_ACTIVATE,
										msg.targetMessage(), CMMsg.NO_EFFECT,
										null));
							}
						}
					}
					boolean readFlag = false;
					boolean menuRead = false;
					final MOB M = msg.source();
					if (msgs.size() == 0)
						M.location()
								.show(M,
										this,
										null,
										CMMsg.MASK_ALWAYS | CMMsg.TYP_OK_VISUAL,
										CMMsg.NO_EFFECT,
										CMMsg.NO_EFFECT,
										"<T-NAME> says '^N\n\rUnknown command. Please read the screen for a menu.\n\r^.^N'");
					else
						for (CMMsg msg2 : msgs) {
							if (msg2.target().okMessage(M, msg2)) {
								msg2.target().executeMsg(M, msg2);
								if (msg2.target() instanceof Software) {
									Software sw = (Software) msg2.target();
									if (msg2.targetMinor() == CMMsg.TYP_ACTIVATE) {
										setActiveMenu(sw.getInternalName());
										readFlag = true;
									} else if (msg2.targetMinor() == CMMsg.TYP_DEACTIVATE) {
										setActiveMenu(sw.getParentMenu());
										menuRead = true;
									} else {
										readFlag = true;
									}
								}
							}
						}
					if (readFlag)
						forceReadersSeeNew();
					if (menuRead)
						forceReadersMenu();
				}
				break;
			}
			case CMMsg.TYP_GET:
			case CMMsg.TYP_PUSH:
			case CMMsg.TYP_PULL:
			case CMMsg.TYP_PUT:
				nextSoftwareCheck = 0;
				break;
			case CMMsg.TYP_LOOK:
				super.executeMsg(host, msg);
				if (CMLib.flags().canBeSeenBy(this, msg.source())
						&& (!amWearingAt(Item.IN_INVENTORY)))
					msg.source()
							.tell(name()
									+ " is currently "
									+ (activated() ? "booted up and the screen ready to be read.\n\r"
											: "deactivated.\n\r"));
				return;
			case CMMsg.TYP_ACTIVATE:
				if (!activated()) {
					activate(true);
					setActiveMenu("");
					if ((msg.source().location() != null)
							&& (!CMath.bset(msg.targetMajor(),
									CMMsg.MASK_CNTRLMSG))) {
						msg.source()
								.location()
								.show(msg.source(), this, null,
										CMMsg.MSG_OK_VISUAL,
										"<S-NAME> boot(s) up <T-NAME>.");
						forceReadersMenu();
					}
				}
				if ((msg.targetMessage() != null) && (activated())) {
					final List<Software> software = getSoftware();
					List<CMMsg> msgs = new LinkedList<CMMsg>();
					synchronized (software) {
						for (final Software S : software) {
							if (S.isActivationString(msg.targetMessage())) {
								msgs.add(CMClass.getMsg(msg.source(), S, null,
										CMMsg.NO_EFFECT, null,
										CMMsg.MASK_ALWAYS | CMMsg.TYP_ACTIVATE,
										msg.targetMessage(), CMMsg.NO_EFFECT,
										null));
							}
						}
					}
					boolean readFlag = false;
					final MOB M = msg.source();
					if (msgs.size() == 0)
						M.location()
								.show(M,
										this,
										null,
										CMMsg.MASK_ALWAYS | CMMsg.TYP_OK_VISUAL,
										CMMsg.NO_EFFECT,
										CMMsg.NO_EFFECT,
										"<T-NAME> says '^N\n\rUnknown activation command. Please read the screen for a menu of TYPEable commands.\n\r^.^N'");
					else
						for (CMMsg msg2 : msgs)
							if (msg2.target().okMessage(M, msg2))
								msg2.target().executeMsg(M, msg2);
					if (readFlag)
						forceReadersSeeNew();
				}
				break;
			case CMMsg.TYP_DEACTIVATE:
				if ((msg.targetMessage() != null) && (activated())) {
					final List<Software> software = getSoftware();
					List<CMMsg> msgs = new LinkedList<CMMsg>();
					synchronized (software) {
						for (final Software S : software) {
							if (S.isActivationString(msg.targetMessage())) {
								msgs.add(CMClass.getMsg(msg.source(), S, null,
										CMMsg.NO_EFFECT, null,
										CMMsg.MASK_ALWAYS
												| CMMsg.TYP_DEACTIVATE,
										msg.targetMessage(), CMMsg.NO_EFFECT,
										null));
							}
						}
					}
					boolean readFlag = false;
					final MOB M = msg.source();
					if (msgs.size() == 0)
						M.location()
								.show(M,
										this,
										null,
										CMMsg.MASK_ALWAYS | CMMsg.TYP_OK_VISUAL,
										CMMsg.NO_EFFECT,
										CMMsg.NO_EFFECT,
										"<T-NAME> says '^N\n\rUnknown deactivation command. Please read the screen for a menu of TYPEable commands.\n\r^.^N'");
					else
						for (CMMsg msg2 : msgs)
							if (msg2.target().okMessage(M, msg2))
								msg2.target().executeMsg(M, msg2);
					if (readFlag)
						forceReadersSeeNew();
				} else if (activated()) {
					activate(false);
					if ((msg.source().location() != null)
							&& (!CMath.bset(msg.targetMajor(),
									CMMsg.MASK_CNTRLMSG)))
						msg.source()
								.location()
								.show(msg.source(), this, null,
										CMMsg.MSG_OK_VISUAL,
										"<S-NAME> shut(s) down <T-NAME>.");
					deactivateSystem();
				}
				break;
			}
		} else if ((msg.source() == this.lastReader)
				&& (msg.targetMinor() == CMMsg.TYP_READ)
				&& (msg.target() instanceof Electronics.ElecPanel))
			this.lastReader = null; // whats this do?
		super.executeMsg(host, msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if (tickID == Tickable.TICKID_ELECTRONICS) {
			if (activated() && owner() instanceof MOB) {
				MOB mob = (MOB) owner();
				final List<Software> software = getSoftware();
				CMMsg msg2 = CMClass.getMsg(mob, null, null, CMMsg.NO_EFFECT,
						null, CMMsg.MSG_POWERCURRENT, null, CMMsg.NO_EFFECT,
						null);
				synchronized (software) // this is how software ticks, even
										// tricorder software...
				{
					for (Software sw : software) {
						msg2.setTarget(sw);
						msg2.setValue(1 + (this.getActiveMenu().equals(
								sw.getInternalName()) ? 1 : 0));
						if (sw.okMessage(mob, msg2))
							sw.executeMsg(mob, msg2);
					}
				}
			}
			forceReadersSeeNew();
			if (--nextPowerCycleCtr <= 0) {
				nextPowerCycleCtr = POWER_RATE;
				setPowerRemaining(this.powerRemaining() - 1);
				if (powerRemaining() <= 0)
					deactivateSystem();
			}
		}
		return true;
	}

	@Override
	public void forceReadersSeeNew() {
		if (activated()) {
			final List<Software> software = getSoftware();
			synchronized (software) {
				final StringBuilder newMsgs = new StringBuilder();
				for (Software sw : software)
					newMsgs.append(sw.getScreenMessage());
				if (newMsgs.length() > 0) {
					List<MOB> readers = getCurrentReaders();
					for (MOB M : readers)
						if (CMLib.flags().canBeSeenBy(this, M))
							M.location().show(
									M,
									this,
									null,
									CMMsg.MASK_ALWAYS | CMMsg.TYP_OK_VISUAL,
									CMMsg.NO_EFFECT,
									CMMsg.NO_EFFECT,
									"<T-NAME> says '^N\n\r"
											+ newMsgs.toString() + "\n\r^.^N'");
				}
			}
		}
	}

	@Override
	public void forceReadersMenu() {
		if (activated()) {
			List<MOB> readers = getCurrentReaders();
			for (MOB M : readers)
				CMLib.commands().postRead(M, this, "", true);
		}
	}

	protected void deactivateSystem() {
		if (activated()) {
			final List<Software> software = getSoftware();
			final Room locR = CMLib.map().roomLocation(this);
			CMMsg msg2 = CMClass.getMsg(CMLib.map().getFactoryMOB(locR), null,
					null, CMMsg.NO_EFFECT, null, CMMsg.MSG_DEACTIVATE, null,
					CMMsg.NO_EFFECT, null);
			synchronized (software) {
				for (Software sw : software) {
					msg2.setTarget(sw);
					if (sw.okMessage(msg2.source(), msg2))
						sw.executeMsg(msg2.source(), msg2);
				}
			}
			activate(false);
			if (owner() instanceof MOB) {
				MOB M = (MOB) owner();
				if (CMLib.flags().canBeSeenBy(this, M))
					M.location().show(M, this, null,
							CMMsg.MASK_ALWAYS | CMMsg.TYP_OK_VISUAL,
							CMMsg.NO_EFFECT, CMMsg.NO_EFFECT,
							"The screen on <T-NAME> goes blank.");
			}
		}
	}
}
