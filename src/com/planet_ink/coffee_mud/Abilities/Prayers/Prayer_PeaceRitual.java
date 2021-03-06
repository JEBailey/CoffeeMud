package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Libraries.interfaces.ChannelsLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
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
@SuppressWarnings("rawtypes")
public class Prayer_PeaceRitual extends Prayer {
	public String ID() {
		return "Prayer_PeaceRitual";
	}

	public String name() {
		return "Peace Ritual";
	}

	public String displayText() {
		return "(Peace Ritual)";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_SELF;
	}

	public long flags() {
		return Ability.FLAG_HOLY;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public Clan clan1 = null;
	public Clan clan2 = null;
	public Iterable<Pair<Clan, Integer>> clan2Set = null;

	public boolean tick(Tickable ticking, int tickID) {
		if (!(affected instanceof MOB))
			return false;

		if (invoker == null)
			return false;

		MOB mob = (MOB) affected;
		if (clan2Set == null) {
			if (clan2 == null)
				return super.tick(ticking, tickID);
			Vector<Pair<Clan, Integer>> V = new Vector<Pair<Clan, Integer>>();
			V.add(new Pair<Clan, Integer>(clan2, Integer.valueOf(clan2
					.getGovernment().getAcceptPos())));
			clan2Set = V;
		}
		List<String> channels = CMLib.channels().getFlaggedChannelNames(
				ChannelsLibrary.ChannelFlag.CLANINFO);
		for (int i = 0; i < channels.size(); i++)
			CMLib.commands().postChannel(
					channels.get(i),
					clan2Set,
					mob.name() + " located in '"
							+ mob.location().displayText(mob)
							+ "' is performing a peace ritual on behalf of "
							+ clan1.name() + ".", false);
		return super.tick(ticking, tickID);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;
		if (affected == null)
			return true;
		if (!(affected instanceof MOB))
			return true;

		if ((msg.target() == affected) && (msg.source() != affected)
				&& (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))) {
			msg.source()
					.location()
					.show((MOB) affected, null, CMMsg.MSG_OK_VISUAL,
							"The peace ritual is disrupted!");
			clan1 = null;
			clan2 = null;
			unInvoke();
		} else if (msg.amISource((MOB) affected)
				&& ((msg.targetMinor() == CMMsg.TYP_ENTER) || (msg
						.targetMinor() == CMMsg.TYP_LEAVE))) {
			msg.source()
					.location()
					.show((MOB) affected, null, CMMsg.MSG_OK_VISUAL,
							"The peace ritual is disrupted!");
			clan1 = null;
			clan2 = null;
			unInvoke();
		}
		return true;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		super.unInvoke();

		if ((canBeUninvoked()) && (clan1 != null) && (clan2 != null)) {
			final Clan C1 = clan1;
			final Clan C2 = clan2;
			if ((C1 != null) && (C2 != null)) {
				if (C1.getClanRelations(C2.clanID()) == Clan.REL_WAR) {
					C1.setClanRelations(C2.clanID(), Clan.REL_HOSTILE,
							System.currentTimeMillis());
					C1.update();
				}
				if (C2.getClanRelations(C1.clanID()) == Clan.REL_WAR) {
					C2.setClanRelations(C1.clanID(), Clan.REL_HOSTILE,
							System.currentTimeMillis());
					C2.update();
				}
				List<String> channels = CMLib.channels()
						.getFlaggedChannelNames(
								ChannelsLibrary.ChannelFlag.CLANINFO);
				for (int i = 0; i < channels.size(); i++)
					CMLib.commands().postChannel(
							channels.get(i),
							CMLib.clans().clanRoles(),
							"There is now peace between " + C1.name() + " and "
									+ C2.name() + ".", false);
			}
		}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = mob;
		if ((auto) && (givenTarget != null))
			target = givenTarget;
		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(mob, target, null,
					"<T-NAME> <T-IS-ARE> already affected by " + name() + ".");
			return false;
		}
		clan1 = CMLib.clans().findRivalrousClan(mob);
		if (clan1 == null) {
			mob.tell("You must belong to a clan to use this prayer.");
			return false;
		}
		if (commands.size() < 1) {
			mob.tell("You must specify the clan you wish to see peace with.");
			return false;
		}
		String clan2Name = CMParms.combine(commands, 0);
		clan2 = CMLib.clans().findClan(clan2Name);
		if ((clan2 == null)
				|| ((clan1.getClanRelations(clan2.clanID()) != Clan.REL_WAR) && (clan2
						.getClanRelations(clan1.clanID()) != Clan.REL_WAR))) {
			mob.tell("Your " + clan1.getGovernmentName()
					+ " is not at war with " + clan2 + "!");
			return false;
		}
		boolean found = false;
		for (Enumeration e = CMLib.players().players(); e.hasMoreElements();) {
			MOB M = (MOB) e.nextElement();
			if (M.getClanRole(clan2.clanID()) != null) {
				found = true;
				break;
			}
		}
		if (!found) {
			mob.tell("You must wait until a member of " + clan2
					+ " is online before beginning the ritual.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(
					mob,
					target,
					this,
					verbalCastCode(mob, target, auto),
					auto ? "<T-NAME> begin(s) a peace ritual." : "^S<S-NAME> "
							+ prayWord(mob) + " for peace between "
							+ clan1.name() + " and " + clan2.name() + ".^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel,
						(int) CMProps.getTicksPerMinute() * 5);
			}
		} else
			return beneficialWordsFizzle(
					mob,
					null,
					"<S-NAME> " + prayWord(mob) + " for peace between "
							+ clan1.name() + " and " + clan2.name()
							+ ", but there is no answer.");

		// return whether it worked
		return success;
	}
}
