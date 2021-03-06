package com.planet_ink.coffee_mud.Abilities.Properties;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;

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
public class Prop_ReqAlignments extends Property implements TriggeredAffect {
	public String ID() {
		return "Prop_ReqAlignments";
	}

	public String name() {
		return "Alignment Limitations";
	}

	protected int canAffectCode() {
		return Ability.CAN_ROOMS | Ability.CAN_AREAS | Ability.CAN_EXITS;
	}

	private boolean noFollow = false;
	private boolean noSneak = false;

	public long flags() {
		return Ability.FLAG_ZAPPER;
	}

	public int triggerMask() {
		return TriggeredAffect.TRIGGER_ENTER;
	}

	public void setMiscText(String txt) {
		noFollow = false;
		noSneak = false;
		Vector parms = CMParms.parse(txt.toUpperCase());
		String s;
		for (Enumeration p = parms.elements(); p.hasMoreElements();) {
			s = (String) p.nextElement();
			if ("NOFOLLOW".startsWith(s))
				noFollow = true;
			else if (s.startsWith("NOSNEAK"))
				noSneak = true;
		}
		super.setMiscText(txt);
	}

	public boolean passesMuster(MOB mob) {
		if (mob == null)
			return false;
		if (CMLib.flags().isATrackingMonster(mob))
			return true;
		if (CMLib.flags().isSneaking(mob) && (!noSneak))
			return true;
		int x = text().toUpperCase().indexOf("ALL");
		int y = text().toUpperCase().indexOf(
				CMLib.flags().getAlignmentName(mob));
		if (((x > 0) && (text().charAt(x - 1) == '-') && ((y <= 0) || ((y > 0) && (text()
				.charAt(y - 1) == '-'))))
				|| ((y > 0) && (text().charAt(y - 1) == '-')))
			return false;
		return true;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((affected != null)
				&& (msg.target() != null)
				&& (((msg.target() instanceof Room) && (msg.targetMinor() == CMMsg.TYP_ENTER)) || ((msg
						.target() instanceof Rideable) && (msg.targetMinor() == CMMsg.TYP_SIT)))
				&& (!CMLib.flags().isFalling(msg.source()))
				&& ((msg.amITarget(affected)) || (msg.tool() == affected) || (affected instanceof Area))) {
			HashSet H = new HashSet();
			if (noFollow)
				H.add(msg.source());
			else {
				msg.source().getGroupMembers(H);
				HashSet H2 = (HashSet) H.clone();
				for (Iterator e = H2.iterator(); e.hasNext();)
					((MOB) e.next()).getRideBuddies(H);
			}
			for (Iterator e = H.iterator(); e.hasNext();) {
				Environmental E = (Environmental) e.next();
				if ((E instanceof MOB) && (passesMuster((MOB) E)))
					return super.okMessage(myHost, msg);
			}
			msg.source().tell("You may not go that way.");
			return false;
		}
		return super.okMessage(myHost, msg);
	}
}
