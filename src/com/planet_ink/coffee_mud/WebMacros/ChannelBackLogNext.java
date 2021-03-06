package com.planet_ink.coffee_mud.WebMacros;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Libraries.interfaces.ChannelsLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.miniweb.interfaces.HTTPRequest;

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
public class ChannelBackLogNext extends StdWebMacro {
	public String name() {
		return "ChannelBackLogNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("CHANNELBACKLOG");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("CHANNELBACKLOG");
			return "";
		}
		String channel = httpReq.getUrlParameter("CHANNEL");
		if (channel == null)
			return " @break@";
		int channelInt = CMLib.channels().getChannelIndex(channel);
		if (channelInt < 0)
			return " @break@";
		MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		if (mob != null) {
			if (CMLib.channels().mayReadThisChannel(mob, channelInt, true)) {
				@SuppressWarnings("unchecked")
				List<ChannelsLibrary.ChannelMsg> que = (List<ChannelsLibrary.ChannelMsg>) httpReq
						.getRequestObjects().get(
								"CHANNELMSG_" + channelInt + " QUE");
				if (que == null) {
					List<ChannelsLibrary.ChannelMsg> oldQue = CMLib.channels()
							.getChannelQue(channelInt);
					que = new Vector<ChannelsLibrary.ChannelMsg>(oldQue.size());
					que.addAll(oldQue);
					httpReq.getRequestObjects().put(
							"CHANNELMSG_" + channelInt + " QUE", que);
				}

				while (true) {
					int num = CMath.s_int(last);
					last = "" + (num + 1);
					httpReq.addFakeUrlParameter("CHANNELBACKLOG", last);
					if ((num < 0) || (num >= que.size())) {
						httpReq.addFakeUrlParameter("CHANNELBACKLOG", "");
						if (parms.containsKey("EMPTYOK"))
							return "<!--EMPTY-->";
						return " @break@";
					}
					boolean areareq = CMLib.channels().getChannel(channelInt).flags
							.contains(ChannelsLibrary.ChannelFlag.SAMEAREA);

					final ChannelsLibrary.ChannelMsg cmsg = que.get(num);
					final CMMsg msg = cmsg.msg;
					String str = null;
					if ((mob == msg.source()) && (msg.sourceMessage() != null))
						str = msg.sourceMessage();
					else if ((mob == msg.target())
							&& (msg.targetMessage() != null))
						str = msg.targetMessage();
					else if (msg.othersMessage() != null)
						str = msg.othersMessage();
					else
						str = "";
					str = CMStrings.removeColors(str);
					str += " ("
							+ CMLib.time()
									.date2SmartEllapsedTime(
											Math.round((System
													.currentTimeMillis() - cmsg.ts) / 1000) * 1000,
											false) + " ago)";
					if (CMLib.channels().mayReadThisChannel(msg.source(),
							areareq, mob, channelInt, true))
						return clearWebMacros(CMLib.coffeeFilter()
								.fullOutFilter(mob.session(), mob,
										msg.source(), msg.target(), msg.tool(),
										CMStrings.removeColors(str), false));
				}
			}
			return "";
		}
		return "";
	}
}
