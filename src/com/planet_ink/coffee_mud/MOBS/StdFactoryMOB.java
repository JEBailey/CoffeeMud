package com.planet_ink.coffee_mud.MOBS;

import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;

/*
 Copyright 2000-2014 Bo Zimmerman

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, e\ither express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
public class StdFactoryMOB extends StdMOB {
	public String ID() {
		return "StdFactoryMOB";
	}

	public CMObject newInstance() {
		try {
			return this.getClass().newInstance();
		} catch (Exception e) {
			Log.errOut(ID(), e);
		}
		return new StdFactoryMOB();
	}

	protected void finalize() throws Throwable {
		if (!amDestroyed)
			destroy();
		amDestroyed = false;
		if (!CMClass.returnMob(this)) {
			amDestroyed = true;
			super.finalize();
		}
	}

	public void destroy() {
		try {
			CharStats savedCStats = charStats;
			if (charStats == baseCharStats)
				savedCStats = (CharStats) CMClass.getCommon("DefaultCharStats");
			PhyStats savedPStats = phyStats;
			if (phyStats == basePhyStats)
				savedPStats = (PhyStats) CMClass.getCommon("DefaultPhyStats");
			CharState savedCState = curState;
			if ((curState == baseState) || (curState == maxState))
				curState = (CharState) CMClass.getCommon("DefaultCharState");
			super.destroy();
			removeFromGame = false;
			charStats = savedCStats;
			phyStats = savedPStats;
			curState = savedCState;
			baseCharStats.reset();
			basePhyStats.reset();
			baseState.reset();
			maxState.reset();
			curState.reset();
			phyStats.reset();
			charStats.reset();
			finalize();
		} catch (Throwable t) {
			Log.errOut(ID(), t);
		}
	}
}
