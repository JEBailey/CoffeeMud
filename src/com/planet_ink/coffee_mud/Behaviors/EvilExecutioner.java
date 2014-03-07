package com.planet_ink.coffee_mud.Behaviors;

import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class EvilExecutioner extends StdBehavior {
	public String ID() {
		return "EvilExecutioner";
	}

	public long flags() {
		return Behavior.FLAG_POTENTIALLYAGGRESSIVE;
	}

	protected boolean doPlayers = false;
	protected long deepBreath = System.currentTimeMillis();
	protected boolean noRecurse = true;

	public void setParms(String newParms) {
		super.setParms(newParms);
		newParms = newParms.toUpperCase();
		Vector<String> V = CMParms.parse(newParms);
		doPlayers = V.contains("PLAYERS") || V.contains("PLAYER");
	}

	public String accountForYourself() {
		return "aggression to goodness and paladins";
	}

	public boolean grantsAggressivenessTo(MOB M) {
		if (M == null)
			return false;
		if (CMLib.flags().isBoundOrHeld(M))
			return false;
		if ((!M.isMonster()) && (!doPlayers))
			return false;
		for (Enumeration<Behavior> e = M.behaviors(); e.hasMoreElements();) {
			Behavior B = e.nextElement();
			if ((B != null) && (B.grantsAggressivenessTo(M)))
				return true;
		}
		return ((CMLib.flags().isGood(M)) || (M.baseCharStats()
				.getCurrentClass().baseClass().equalsIgnoreCase("Paladin")));
	}

	public void executeMsg(Environmental affecting, CMMsg msg) {
		super.executeMsg(affecting, msg);
		MOB source = msg.source();
		if (!canFreelyBehaveNormal(affecting)) {
			deepBreath = System.currentTimeMillis();
			return;
		}
		if ((deepBreath == 0)
				|| ((System.currentTimeMillis() - deepBreath) > 60000)
				&& (!noRecurse)) {
			noRecurse = true;
			deepBreath = 0;
			MOB observer = (MOB) affecting;
			// base 90% chance not to be executed
			if ((source.isMonster() || doPlayers) && (source != observer)
					&& (grantsAggressivenessTo(source))) {
				String reason = "GOOD";
				if (source.baseCharStats().getCurrentClass().baseClass()
						.equalsIgnoreCase("Paladin"))
					reason = "A PALADIN";
				MOB oldFollowing = source.amFollowing();
				source.setFollowing(null);
				boolean yep = Aggressive.startFight(observer, source, true,
						false, source.name().toUpperCase() + " IS " + reason
								+ ", AND MUST BE DESTROYED!");
				if (!yep)
					if (oldFollowing != null)
						source.setFollowing(oldFollowing);
			}
			noRecurse = false;
		}
	}
}