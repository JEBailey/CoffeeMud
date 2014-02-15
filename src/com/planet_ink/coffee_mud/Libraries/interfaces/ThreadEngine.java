package com.planet_ink.coffee_mud.Libraries.interfaces;
import java.util.Iterator;
import java.util.List;

import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.TickClient;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;
import com.planet_ink.coffee_mud.core.interfaces.TickableGroup;
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
public interface ThreadEngine extends CMLibrary, Runnable
{
	// tick related
	public TickClient startTickDown(Tickable E, int tickID, long TICK_TIME, int numTicks);
	public TickClient startTickDown(Tickable E, int tickID, int numTicks);
	public boolean deleteTick(Tickable E, int tickID);
	public void deleteAllTicks(Tickable E);
	public void suspendTicking(Tickable E, int tickID);
	public void resumeTicking(Tickable E, int tickID);
	public void suspendResumeRecurse(CMObject O, boolean skipEmbeddedAreas, boolean suspend);
	public boolean isSuspended(Tickable E, int tickID);
	public void suspendAll();
	public void resumeAll();
	public boolean isAllSuspended();
	public void clearDebri(Room room, int taskCode);
	public String tickInfo(String which);
	public void tickAllTickers(Room here);
	public void rejuv(Room here, int tickID);
	public String systemReport(String itemCode);
	public boolean isTicking(Tickable E, int tickID);
	public  Iterator<TickableGroup> tickGroups();
	public String getTickStatusSummary(Tickable obj);
	public List<Tickable> getNamedTickingObjects(String name);
	public Runnable findRunnableByThread(final Thread thread);
	public void executeRunnable(Runnable R);
	public void executeRunnable(String threadGroupName, Runnable R);
	public void debugDumpStack(final String ID, Thread theThread);
}
