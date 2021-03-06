package com.planet_ink.coffee_mud.Behaviors;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class AlignHelper extends StdBehavior {
	public String ID() {
		return "AlignHelper";
	}

	public String accountForYourself() {
		return "same-aligned protecting";
	}

	public void executeMsg(Environmental affecting, CMMsg msg) {
		super.executeMsg(affecting, msg);
		if ((msg.target() == null) || (!(msg.target() instanceof MOB)))
			return;
		MOB source = msg.source();
		MOB observer = (MOB) affecting;
		MOB target = (MOB) msg.target();

		if ((source != observer)
				&& (target != observer)
				&& (source != target)
				&& (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
				&& (!observer.isInCombat())
				&& (CMLib.flags().canBeSeenBy(source, observer))
				&& (CMLib.flags().canBeSeenBy(target, observer))
				&& (!BrotherHelper.isBrother(source, observer, false))
				&& ((CMLib.flags().isEvil(target) && CMLib.flags().isEvil(
						observer))
						|| (CMLib.flags().isNeutral(target) && CMLib.flags()
								.isNeutral(observer)) || (CMLib.flags().isGood(
						target) && CMLib.flags().isGood(observer)))) {
			Aggressive.startFight(observer, source, true, false, CMLib.flags()
					.getAlignmentName(observer) + " PEOPLE UNITE! CHARGE!");
		}
	}
}
