package com.planet_ink.coffee_mud.Abilities.Misc;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.collections.SLinkedList;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.MsgListener;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.StatsAffecting;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PresenceReaction extends StdAbility {
	public String ID() {
		return "PresenceReaction";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	protected MOB reactToM = null;
	protected boolean startedManaging = false;
	protected String previousMood = null;
	protected String reactToName = null;
	protected SLinkedList<Object[]> unmanagedYet = new SLinkedList<Object[]>();
	protected SLinkedList<CMObject> managed = new SLinkedList<CMObject>();

	public PresenceReaction() {
		super();
		super.makeLongLasting();
		super.savable = false;
		super.canBeUninvoked = false;
	}

	protected void cloneFix(Ability E) {
		reactToM = null;
		previousMood = null;
		reactToName = null;
		reactToM = null;
		affected = null;
		invoker = null;
		unmanagedYet = new SLinkedList<Object[]>();
		managed = new SLinkedList<CMObject>();
	}

	public void addAffectOrBehavior(String substr) {
		int x = substr.indexOf('=');
		if (x >= 0) {
			String nam = substr.substring(0, x);
			if (nam.trim().length() == 0) {
				reactToName = substr.substring(1);
				return;
			}
			Behavior B = CMClass.getBehavior(nam);
			if (B != null) {
				B.setSavable(false);
				Object[] SET = new Object[] { B, substr.substring(x + 1) };
				unmanagedYet.add(SET);
				return;
			}
			Ability A = CMClass.getAbility(nam);
			if (A != null) {
				A.setSavable(false);
				A.makeNonUninvokable();
				Object[] SET = new Object[] { A, substr.substring(x + 1) };
				unmanagedYet.add(SET);
				return;
			}
			Command C = CMClass.getCommand(nam);
			if (C != null) {
				Object[] SET = new Object[] { C, substr.substring(x + 1) };
				unmanagedYet.add(SET);
			}
		}
	}

	public void setMiscText(String parms) {
		if (parms.startsWith("+"))
			addAffectOrBehavior(parms.substring(1));
		else {
			List<String> parsed = CMParms.parseAny(parms, "~~", true);
			for (Iterator<String> e = parsed.iterator(); e.hasNext();)
				addAffectOrBehavior((String) e.next());
		}
	}

	public boolean okMessage(Environmental affecting, CMMsg msg) {
		for (CMObject O : managed)
			if (O instanceof MsgListener)
				if (!((MsgListener) O).okMessage(affecting, msg))
					return false;
		return super.okMessage(affecting, msg);
	}

	public void executeMsg(Environmental affecting, CMMsg msg) {
		for (CMObject O : managed)
			if (O instanceof MsgListener)
				((MsgListener) O).executeMsg(affecting, msg);
		super.executeMsg(affecting, msg);
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		for (CMObject O : managed)
			if (O instanceof StatsAffecting)
				((StatsAffecting) O).affectPhyStats(affected, affectableStats);
	}

	public void affectCharStats(MOB affectedMob, CharStats affectableStats) {
		for (CMObject O : managed)
			if (O instanceof StatsAffecting)
				((StatsAffecting) O).affectCharStats(affectedMob,
						affectableStats);
	}

	public void affectCharState(MOB affectedMob, CharState affectableMaxState) {
		for (CMObject O : managed)
			if (O instanceof StatsAffecting)
				((StatsAffecting) O).affectCharState(affectedMob,
						affectableMaxState);
	}

	protected synchronized boolean shutdownPresence(MOB affected) {
		Room R = affected.location();
		if (((R == null) || (reactToM == null) || (!R.isInhabitant(reactToM)))) {
			MOB M = (MOB) super.affected;
			for (CMObject O : managed) {
				if ((O != null) && (O.ID().equals("Mood"))) {
					if (previousMood != null) {
						try {
							Command C = CMClass.getCommand("Mood");
							if (C != null)
								C.execute(M,
										CMParms.parse("MOOD " + previousMood),
										0);
						} catch (Exception e) {
						}
					}
				} else if (O instanceof Environmental)
					((Environmental) O).destroy();
			}
			affected.delEffect(this);
		}
		unmanagedYet.clear();
		managed.clear();
		return false;
	}

	protected boolean initializeManagedObjects(MOB affected) {
		if (unmanagedYet.size() == 0)
			return false;
		boolean didAnything = false;
		SLinkedList<Object[]> commands = new SLinkedList<Object[]>();
		while (unmanagedYet.size() > 0) {
			Object[] thing = unmanagedYet.removeFirst();
			if (thing[0] instanceof Ability) {
				if (((Ability) thing[0]).ID().equalsIgnoreCase("Mood")) {
					previousMood = "";
					Ability A = affected.fetchEffect("Mood");
					if (A != null)
						previousMood = A.text();
					if (previousMood.trim().length() == 0)
						previousMood = "NORMAL";
				}
				Ability A = (Ability) thing[0];
				A.setAffectedOne(affected);
				A.setMiscText((String) thing[1]);
				managed.add(A);
				didAnything = true;
				continue;
			}
			if (thing[0] instanceof Behavior) {
				Behavior B = (Behavior) thing[0];
				B.startBehavior(affected);
				B.setParms((String) thing[1]);
				managed.add(B);
				didAnything = true;
				continue;
			}
			if (thing[0] instanceof Command) {
				commands.add(thing);
				continue;
			}
		}
		unmanagedYet = commands;
		if (didAnything) {
			affected.recoverCharStats();
			affected.recoverPhyStats();
			affected.recoverMaxState();
		}
		return didAnything;
	}

	protected void initializeAllManaged(MOB affected) {
		if (unmanagedYet.size() == 0)
			return;
		initializeManagedObjects(affected);
		while (unmanagedYet.size() > 0) {
			Object[] thing = unmanagedYet.removeFirst();
			if (thing[0] instanceof Command) {
				Command C = (Command) thing[0];
				try {
					String cmdparms = C.getAccessWords()[0]
							+ " "
							+ CMStrings.replaceAll((String) thing[1],
									"<TARGET>", reactToM.Name());
					affected.enqueCommand(CMParms.parse(cmdparms),
							Command.METAFLAG_FORCED, 0);
				} catch (Exception e) {
				}
				managed.add(C);
				continue;
			}
		}
	}

	public boolean tick(Tickable ticking, int tickID) {
		super.tick(ticking, tickID);
		if (tickID != Tickable.TICKID_MOB)
			return true;
		if (reactToM == null) {
			// dont combine this if with the above
			if ((affected instanceof MOB) && (reactToName != null))
				reactToM = ((MOB) affected).location().fetchInhabitant(
						reactToName);
			if (reactToM == null)
				return shutdownPresence((MOB) affected);
		} else if (this.affected instanceof MOB) {
			MOB affected = (MOB) this.affected;
			if ((affected.location() != reactToM.location())
					|| (affected.amDead()) || (reactToM.amDead())
					|| (affected.amDestroyed()) || (reactToM.amDestroyed())
					|| (!CMLib.flags().isInTheGame(affected, true))
					|| (!CMLib.flags().isInTheGame(reactToM, true)))
				return shutdownPresence(affected);
			initializeAllManaged(affected);
			for (CMObject O : managed)
				if (O instanceof Tickable)
					((Tickable) O).tick(ticking, tickID);
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Physical target,
			boolean auto, int asLevel) {
		if (target == null) {
			PresenceReaction A = (PresenceReaction) mob.fetchEffect(ID());
			if (A != null)
				A.shutdownPresence(mob);
			if (affected == mob)
				shutdownPresence(mob);
			return A != null;
		}
		if (!(target instanceof MOB))
			return false;
		PresenceReaction A = (PresenceReaction) this.copyOf();
		A.reactToM = (MOB) target;
		for (Object O : commands)
			A.addAffectOrBehavior((String) O);
		commands.clear();
		commands.addElement(A);
		if (auto) {
			synchronized (mob) {
				if (mob.fetchEffect(ID()) == null) {
					mob.addNonUninvokableEffect(A);
					A.initializeManagedObjects(mob);
				}
				return true;
			}
		} else {
			A.makeLongLasting();
			A.makeNonUninvokable();
			A.setAffectedOne(mob);
			A.initializeManagedObjects(mob);
			return true;
		}

	}
}
