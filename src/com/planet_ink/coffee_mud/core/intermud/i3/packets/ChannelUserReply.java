package com.planet_ink.coffee_mud.core.intermud.i3.packets;

import java.util.Vector;

import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.intermud.i3.server.I3Server;

/**
 * Copyright (c) 2008-2014 Bo Zimmerman Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
@SuppressWarnings("rawtypes")
public class ChannelUserReply extends Packet {
	public String userRequested = null;
	public String userVisibleName = null;
	public char gender = 'N';

	public ChannelUserReply() {
		super();
		type = Packet.CHAN_USER_REP;
	}

	public ChannelUserReply(Vector v) throws InvalidPacketException {
		super(v);
		try {
			type = Packet.CHAN_USER_REP;
			try {
				userRequested = (String) v.elementAt(6);
				userVisibleName = (String) v.elementAt(7);
				int gend = CMath.s_int(v.elementAt(8).toString());
				switch (gend) {
				case 0:
					gender = 'M';
					break;
				case 1:
					gender = 'F';
					break;
				case 2:
					gender = 'N';
					break;
				}
			} catch (Exception e) {
			}
		} catch (ClassCastException e) {
			throw new InvalidPacketException();
		}
	}

	public void send() throws InvalidPacketException {
		if (userRequested == null || userVisibleName == null) {
			throw new InvalidPacketException();
		}
		super.send();
	}

	public String toString() {
		int genderCode = 0;
		switch (gender) {
		case 'M':
			genderCode = 0;
			break;
		case 'F':
			genderCode = 1;
			break;
		case 'N':
			genderCode = 2;
			break;
		}
		String str = "({\"chan-user-req\",5,\"" + I3Server.getMudName()
				+ "\",0,\"" + target_mud + "\",0,\"" + userRequested + "\",\""
				+ userVisibleName + "\"," + genderCode + ",})";
		return str;

	}
}
