package com.planet_ink.coffee_mud.Abilities.Misc;

import java.util.List;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.collections.PairVector;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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

public class TemporaryImmunity extends StdAbility {
	public String ID() {
		return "TemporaryImmunity";
	}

	public String name() {
		return "Temporary Immunity";
	}

	public String displayText() {
		return "";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL;
	}

	public boolean canBeUninvoked() {
		return true;
	}

	public boolean isAutoInvoked() {
		return true;
	}

	public final static long IMMUNITY_TIME = 36000000;
	protected PairVector<String, Long> set = new PairVector<String, Long>();

	public TemporaryImmunity() {
		super();

		tickDown = 10;
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((affected instanceof MOB) && (tickID == Tickable.TICKID_MOB)
				&& ((--tickDown) == 0)) {
			tickDown = 10;
			makeLongLasting();
			for (int s = set.size() - 1; s >= 0; s--) {
				Long L = set.elementAt(s).second;
				if ((System.currentTimeMillis() - L.longValue()) > IMMUNITY_TIME)
					set.removeElementAt(s);
			}

			if (set.size() == 0) {
				unInvoke();
				return false;
			}
		}
		return super.tick(ticking, tickID);
	}

	public String text() {
		if (set.size() == 0)
			return "";
		StringBuffer str = new StringBuffer("");
		for (int s = 0; s < set.size(); s++)
			str.append(set.elementAt(s).first + "/"
					+ set.elementAt(s).second.longValue() + ";");
		return str.toString();
	}

	public void setMiscText(String str) {
		if (str.startsWith("+")) {
			str = str.substring(1);
			if (set.indexOf(str) >= 0)
				set.setElementAt(
						new Pair<String, Long>(str, Long.valueOf(System
								.currentTimeMillis())), set.indexOfFirst(str));
			else
				set.addElement(str, Long.valueOf(System.currentTimeMillis()));
		} else {
			set.clear();
			List<String> V = CMParms.parseSemicolons(str, true);
			for (int v = 0; v < V.size(); v++) {
				String s = V.get(v);
				int x = s.indexOf('/');
				if (x > 0)
					set.addElement(s.substring(0, x),
							Long.valueOf(CMath.s_long(s.substring(x + 1))));
			}
		}
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((msg.amITarget(mob)) && (!mob.amDead())
				&& (msg.tool() instanceof Ability)
				&& (set.contains(msg.tool().ID()))) {
			if (msg.source() != msg.target())
				mob.location()
						.show(mob,
								msg.source(),
								CMMsg.MSG_OK_VISUAL,
								"<S-NAME> seem(s) immune to "
										+ msg.tool().name() + ".");
			return false;
		}
		return true;
	}
}
