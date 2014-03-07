package com.planet_ink.coffee_mud.WebMacros;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.Enumeration;

import com.planet_ink.coffee_mud.Common.interfaces.PlayerAccount;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.B64Encoder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMSecurity;
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
public class UserName extends StdWebMacro {
	public String name() {
		return "UserName";
	}

	public boolean isAdminMacro() {
		return false;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		return getLogin(httpReq);
	}

	protected static byte[] FILTER = null;

	private static byte[] getFilter() {
		if (FILTER == null) {
			// this is coffeemud's unsophisticated xor(mac address) encryption
			// system.
			byte[] filterc = new String(
					"wrinkletellmetrueisthereanythingasnastyasyouwellmaybesothenumber7470issprettybad")
					.getBytes();
			FILTER = new byte[256];
			try {
				for (int i = 0; i < 256; i++)
					FILTER[i] = filterc[i % filterc.length];
				String domain = CMProps.getVar(CMProps.Str.MUDDOMAIN);
				if (domain.length() > 0)
					for (int i = 0; i < 256; i++)
						FILTER[i] ^= domain.charAt(i % domain.length());
				String name = CMProps.getVar(CMProps.Str.MUDNAME);
				if (name.length() > 0)
					for (int i = 0; i < 256; i++)
						FILTER[i] ^= name.charAt(i % name.length());
				String email = CMProps.getVar(CMProps.Str.ADMINEMAIL);
				if (email.length() > 0)
					for (int i = 0; i < 256; i++)
						FILTER[i] ^= email.charAt(i % email.length());
				NetworkInterface ni = NetworkInterface
						.getByInetAddress(InetAddress.getLocalHost());
				byte[] mac = ni.getHardwareAddress();
				if ((mac != null) && (mac.length > 0)) {
					for (int i = 0; i < 256; i++)
						FILTER[i] ^= Math.abs(mac[i % mac.length]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return FILTER;
	}

	private byte[] EnDeCrypt(byte[] bytes) {
		byte[] FILTER = getFilter();
		for (int i = 0, j = 0; i < bytes.length; i++, j++) {
			if (j >= FILTER.length)
				j = 0;
			bytes[i] = (byte) ((bytes[i] ^ FILTER[j]) & 0xff);
		}
		return bytes;
	}

	protected String Encrypt(String ENCRYPTME) {
		try {
			final byte[] buf = B64Encoder.B64encodeBytes(
					EnDeCrypt(ENCRYPTME.getBytes()),
					B64Encoder.DONT_BREAK_LINES).getBytes();
			final StringBuilder s = new StringBuilder("");
			for (byte b : buf) {
				String s2 = Integer.toHexString(b);
				while (s2.length() < 2)
					s2 = "0" + s2;
				s.append(s2);
			}
			return s.toString();
		} catch (Exception e) {
			return "";
		}
	}

	private  String Decrypt(String DECRYPTME) {
		try {
			byte[] buf = new byte[DECRYPTME.length() / 2];
			for (int i = 0; i < DECRYPTME.length(); i += 2)
				buf[i / 2] = (byte) (Integer.parseInt(
						DECRYPTME.substring(i, i + 2), 16) & 0xff);
			return new String(EnDeCrypt(B64Encoder.B64decode(new String(buf))));
		} catch (Exception e) {
			return "";
		}
	}

	private String getLogin(HTTPRequest httpReq) {
		String login = httpReq.getUrlParameter("LOGIN");
		if ((login != null) && (login.length() > 0)) {
			if (CMProps.getIntVar(CMProps.Int.COMMONACCOUNTSYSTEM) > 1) {
				PlayerAccount acct = CMLib.players().getLoadAccount(login);
				if (acct != null) {
					MOB highestM = null;
					final String playerName = acct.findPlayer(login);
					if (playerName != null) {
						login = playerName;
						highestM = CMLib.players().getLoadPlayer(login);
					} else
						for (Enumeration<MOB> m = acct.getLoadPlayers(); m
								.hasMoreElements();) {
							MOB M = m.nextElement();
							if ((highestM == null)
									|| ((M != null) && (M.basePhyStats()
											.level() > highestM.basePhyStats()
											.level())))
								highestM = M;
						}
					if (highestM != null) {
						if (!highestM.Name().equals(login))
							httpReq.addFakeUrlParameter("LOGIN",
									highestM.Name());
						return highestM.Name();
					}
				}
			}
			return login;
		}
		String auth = httpReq.getUrlParameter("AUTH");
		if (auth == null)
			return "";
		int x = auth.indexOf('-');
		if (x >= 0)
			login = Decrypt(auth.substring(0, x));
		return login;
	}

}
