package com.planet_ink.coffee_mud.WebMacros;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.planet_ink.coffee_mud.Libraries.interfaces.ChannelsLibrary;
import com.planet_ink.coffee_mud.Libraries.interfaces.JournalsLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Log;
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
public class INIModify extends StdWebMacro {
	public String name() {
		return "INIModify";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public void updateINIFile(List<String> page) {
		StringBuffer buf = new StringBuffer("");
		for (int p = 0; p < page.size(); p++)
			buf.append((page.get(p)) + "\r\n");
		new CMFile(CMProps.getVar(CMProps.Str.INIPATH), null,
				CMFile.FLAG_FORCEALLOW).saveText(buf);
	}

	public boolean modified(Set<String> H, String s) {
		if (s.endsWith("*"))
			for (Iterator<String> i = H.iterator(); i.hasNext();) {
				if (i.next().startsWith(s.substring(0, s.length() - 1)))
					return true;
			}
		return H.contains(s);
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		MOB authM = Authenticate.getAuthenticatedMob(httpReq);
		if ((authM == null) || (!CMSecurity.isASysOp(authM)))
			return " @break@ ";
		java.util.Map<String, String> parms = parseParms(parm);
		if (parms == null)
			return "";
		List<String> page = CMProps.loadEnumerablePage(CMProps
				.getVar(CMProps.Str.INIPATH));
		if (parms.containsKey("ADDKEY")) {
			String key = parms.get("KEY");
			if ((key == null) || (key.trim().length() == 0))
				return "";
			key = key.trim().toUpperCase();
			CMProps ipage = CMProps.loadPropPage(CMProps
					.getVar(CMProps.Str.INIPATH));
			if ((ipage == null) || (!ipage.isLoaded()))
				return "";
			if (ipage.containsKey(key))
				return "";
			int where = 0;
			if (parms.containsKey("NEAR")) {
				boolean found = false;
				String near = parms.get("NEAR");
				if (near.endsWith("*"))
					near = near.substring(0, near.length() - 1);
				for (int p = 0; p < page.size(); p++) {
					String s = page.get(p).trim();
					int x = s.indexOf(near);
					if (x == 0)
						found = true;
					else if ((x > 0) && (!Character.isLetter(s.charAt(x - 1))))
						found = true;
					if ((!s.startsWith("#")) && (!s.startsWith("!")) && (found)) {
						where = p;
						break;
					}
				}
			}
			if (where >= 0)
				page.add(where, key + "=");
			else
				page.add(key + "=");
			Log.sysOut("INIModify", "Key '" + key + "' added.");
			updateINIFile(page);
			return "";
		} else if (parms.containsKey("DELKEY")) {
			String key = parms.get("KEY");
			if ((key == null) || (key.trim().length() == 0))
				return "";
			key = key.trim().toUpperCase();
			for (int p = 0; p < page.size(); p++) {
				String s = page.get(p).trim();
				if (s.startsWith("!") || s.startsWith("#"))
					continue;
				int x = s.indexOf('=');
				if (x < 0)
					x = s.indexOf(':');
				if (x < 0)
					continue;
				String thisKey = s.substring(0, x).trim().toUpperCase();
				if (thisKey.equals(key)) {
					page.remove(p);
					Log.sysOut("INIModify", "Key '" + thisKey + "' removed.");
					updateINIFile(page);
					break;
				}
			}
			return "";
		} else if (parms.containsKey("UPDATEFACTIONPRELOAD")) {
			String factionID = parms.get("FACTION");
			if ((factionID != null) && (factionID.length() > 0)) {
				CMProps ipage = CMProps.loadPropPage(CMProps
						.getVar(CMProps.Str.INIPATH));
				if ((ipage == null) || (!ipage.isLoaded()))
					return "";
				for (int p = 0; p < page.size(); p++) {
					String s = page.get(p).trim();
					if (s.startsWith("!") || s.startsWith("#"))
						continue;
					int x = s.indexOf('=');
					if (x < 0)
						x = s.indexOf(':');
					if (x < 0)
						continue;
					String thisKey = s.substring(0, x).trim().toUpperCase();
					if (thisKey.equals("FACTIONS")) {
						StringBuilder newVal = new StringBuilder("");
						final String oldVal = CMProps
								.getVar(CMProps.Str.PREFACTIONS);
						List<String> oldList = CMParms.parseSemicolons(oldVal,
								true);
						boolean done = false;
						for (String facID : oldList) {
							if (facID.equalsIgnoreCase(factionID)) {
								done = true;
							} else {
								if (newVal.length() > 0)
									newVal.append("; ");
								newVal.append(facID);
							}
						}
						if (!done) {
							if (newVal.length() > 0)
								newVal.append("; ");
							newVal.append(factionID);
						}
						if (!oldVal.equals(newVal.toString())) {
							Log.sysOut("INIModify", "Key '" + thisKey
									+ "' modified.");
							page.set(p, thisKey + "=" + newVal.toString());
							CMProps.setVar(CMProps.Str.PREFACTIONS,
									newVal.toString());
							updateINIFile(page);
						}
						break;
					}
				}
			}
			return "";
		} else if (parms.containsKey("UPDATE")) {
			Set<String> modified = new HashSet<String>();
			List<String> iniBuildVars = CMParms
					.parseCommas(CMStrings.s_uppercase(httpReq
							.getUrlParameter("INIBUILDVARS")), true);
			if (iniBuildVars.contains("CHANNELS"))
				httpReq.addFakeUrlParameter("CHANNELS",
						buildChannelsVar(httpReq));
			if (iniBuildVars.contains("COMMANDJOURNALS"))
				httpReq.addFakeUrlParameter("COMMANDJOURNALS",
						buildCommandJournalsVar(httpReq));
			if (iniBuildVars.contains("FORUMJOURNALS"))
				httpReq.addFakeUrlParameter("FORUMJOURNALS",
						buildForumJournalsVar(httpReq));
			if (iniBuildVars.contains("ICHANNELS"))
				httpReq.addFakeUrlParameter("ICHANNELS",
						buildIChannelsVar(httpReq));
			if (iniBuildVars.contains("IMC2CHANNELS"))
				httpReq.addFakeUrlParameter("IMC2CHANNELS",
						buildIMC2ChannelsVar(httpReq));

			CMProps ipage = CMProps.loadPropPage(CMProps
					.getVar(CMProps.Str.INIPATH));
			if ((ipage == null) || (!ipage.isLoaded()))
				return "";
			for (int p = 0; p < page.size(); p++) {
				String s = page.get(p).trim();
				if (s.startsWith("!") || s.startsWith("#"))
					continue;
				int x = s.indexOf('=');
				if (x < 0)
					x = s.indexOf(':');
				if (x < 0)
					continue;
				String thisKey = s.substring(0, x).trim().toUpperCase();

				if (httpReq.isUrlParameter(thisKey)
						&& (ipage.containsKey(thisKey))
						&& (!modified.contains(thisKey))
						&& (!httpReq.getUrlParameter(thisKey).equals(
								ipage.getStr(thisKey)))) {
					modified.add(thisKey);
					Log.sysOut("INIModify", "Key '" + thisKey + "' modified.");
					page.set(p,
							thisKey + "=" + httpReq.getUrlParameter(thisKey));
				}
			}
			if (modified.size() > 0) {
				if (modified.contains("JSCRIPTS"))
					return ""; // never modified through this
				updateINIFile(page);
				ipage = CMProps.loadPropPage(CMProps
						.getVar(CMProps.Str.INIPATH));
				if ((ipage == null) || (!ipage.isLoaded()))
					return "";
				ipage.resetSystemVars();
				if (modified(modified, "SYSOPMASK"))
					CMSecurity.setSysOp(ipage.getStr("SYSOPMASK"));
				if (modified(modified, "GROUP_*"))
					CMSecurity.parseGroups(ipage);
				if (modified(modified, "START")
						|| (modified(modified, "START_*")))
					CMLib.login().initStartRooms(ipage);
				if (modified(modified, "DEATH")
						|| (modified(modified, "DEATH_*")))
					CMLib.login().initDeathRooms(ipage);
				if (modified(modified, "MORGUE")
						|| (modified(modified, "MORGUE_*")))
					CMLib.login().initBodyRooms(ipage);
				if (modified(modified, "FACTIONS"))
					CMLib.factions().reloadFactions(
							CMProps.getVar(CMProps.Str.PREFACTIONS));
				if (modified(modified, "CHANNELS")
						|| (modified(modified, "ICHANNELS"))
						|| (modified(modified, "COMMANDJOURNALS"))
						|| (modified(modified, "FORUMJOURNALS"))
						|| (modified(modified, "IMC2CHANNELS"))) {
					CMLib.channels().loadChannels(ipage.getStr("CHANNELS"),
							ipage.getStr("ICHANNELS"),
							ipage.getStr("IMC2CHANNELS"));
					CMLib.journals().loadCommandJournals(
							ipage.getStr("COMMANDJOURNALS"));
					CMLib.journals().loadForumJournals(
							ipage.getStr("FORUMJOURNALS"));
				}
				CMLib.time().globalClock().initializeINIClock(ipage);
			}
			return "";
		}
		return "";
	}

	protected String getChannelsValue(final HTTPRequest httpReq,
			final String index) {
		final String name = httpReq.getUrlParameter("CHANNEL_" + index
				+ "_NAME");
		final String mask = httpReq.getUrlParameter("CHANNEL_" + index
				+ "_MASK");
		final String colors = httpReq.getUrlParameter("CHANNEL_" + index
				+ "_COLORS");
		if ((name != null) && (name.trim().length() > 0)
				&& (!name.trim().equalsIgnoreCase("auction"))) {
			final StringBuilder str = new StringBuilder("");
			str.append(name.trim().replace(',', ' ').toUpperCase()).append(" ");
			if (colors.trim().length() > 0)
				str.append(colors.trim().replace(',', ' ').toUpperCase())
						.append(" ");
			String flagid = "";
			for (int i = 0; httpReq.isUrlParameter("CHANNEL_" + index
					+ "_FLAG_" + flagid); flagid = "" + (++i)) {
				String flagName = httpReq.getUrlParameter("CHANNEL_" + index
						+ "_FLAG_" + flagid);
				ChannelsLibrary.ChannelFlag flag = (ChannelsLibrary.ChannelFlag) CMath
						.s_valueOf(ChannelsLibrary.ChannelFlag.values(),
								flagName);
				if (flag != null)
					str.append(flag.name()).append(" ");
			}
			if (mask.trim().length() > 0)
				str.append(mask.trim().replace(',', ' ')).append(" ");
			str.setLength(str.length() - 1);
			return str.toString();
		}
		return null;
	}

	protected void addChannelsVar(final HTTPRequest httpReq,
			final String index, final StringBuilder str) {
		final String firstPart = getChannelsValue(httpReq, index);
		if (firstPart != null) {
			final String i3Name = httpReq.getUrlParameter("CHANNEL_" + index
					+ "_I3NAME");
			final String imc2Name = httpReq.getUrlParameter("CHANNEL_" + index
					+ "_IMC2NAME");
			if (((i3Name != null) && (i3Name.trim().length() > 0))
					|| ((imc2Name != null) && (imc2Name.trim().length() > 0)))
				return;
			if (str.length() > 0)
				str.append(", ");
			str.append(firstPart);
		}
	}

	protected String buildChannelsVar(final HTTPRequest httpReq) {
		final StringBuilder str = new StringBuilder("");
		for (int index = 0; httpReq
				.isUrlParameter("CHANNEL_" + index + "_NAME"); index++)
			addChannelsVar(httpReq, Integer.toString(index), str);
		addChannelsVar(httpReq, "", str);
		return str.toString();
	}

	protected void addIChannelsVar(final HTTPRequest httpReq,
			final String index, final StringBuilder str) {
		final String firstPart = getChannelsValue(httpReq, index);
		if (firstPart != null) {
			final String i3Name = httpReq.getUrlParameter("CHANNEL_" + index
					+ "_I3NAME");
			if ((i3Name != null) && (i3Name.trim().length() > 0)) {
				if (str.length() > 0)
					str.append(", ");
				str.append(firstPart).append(" ").append(i3Name);
			}
		}
	}

	protected String buildIChannelsVar(HTTPRequest httpReq) {
		final StringBuilder str = new StringBuilder("");
		for (int index = 0; httpReq
				.isUrlParameter("CHANNEL_" + index + "_NAME"); index++)
			addIChannelsVar(httpReq, Integer.toString(index), str);
		addIChannelsVar(httpReq, "", str);
		return str.toString();
	}

	protected void addIMC2ChannelsVar(final HTTPRequest httpReq,
			final String index, final StringBuilder str) {
		final String firstPart = getChannelsValue(httpReq, index);
		if (firstPart != null) {
			final String imc2Name = httpReq.getUrlParameter("CHANNEL_" + index
					+ "_IMC2NAME");
			if ((imc2Name != null) && (imc2Name.trim().length() > 0)) {
				if (str.length() > 0)
					str.append(", ");
				str.append(firstPart).append(" ").append(imc2Name);
			}
		}
	}

	protected String buildIMC2ChannelsVar(HTTPRequest httpReq) {
		final StringBuilder str = new StringBuilder("");
		for (int index = 0; httpReq
				.isUrlParameter("CHANNEL_" + index + "_NAME"); index++)
			addIMC2ChannelsVar(httpReq, Integer.toString(index), str);
		addIMC2ChannelsVar(httpReq, "", str);
		return str.toString();
	}

	protected void addCommandJournalsVar(final HTTPRequest httpReq,
			final String index, final StringBuilder str) {
		final String name = httpReq.getUrlParameter("COMMANDJOURNAL_" + index
				+ "_NAME");
		final String mask = httpReq.getUrlParameter("COMMANDJOURNAL_" + index
				+ "_MASK");
		if ((name != null) && (name.trim().length() > 0)
				&& (!name.trim().equalsIgnoreCase("auction"))) {
			if (str.length() > 0)
				str.append(", ");
			str.append(name.trim().replace(',', ' ').toUpperCase()).append(" ");
			for (JournalsLibrary.CommandJournalFlags flag : JournalsLibrary.CommandJournalFlags
					.values()) {
				String val = httpReq.getUrlParameter("COMMANDJOURNAL_" + index
						+ "_FLAG_" + flag.name());
				if ((val != null) && (val.trim().length() > 0))
					str.append(flag.name()).append("=")
							.append(val.replace(',', ' ')).append(" ");
			}
			if (mask.trim().length() > 0)
				str.append(mask.trim().replace(',', ' ')).append(" ");
			str.setLength(str.length() - 1);
		}
	}

	protected String buildCommandJournalsVar(HTTPRequest httpReq) {
		final StringBuilder str = new StringBuilder("");
		for (int index = 0; httpReq.isUrlParameter("COMMANDJOURNAL_" + index
				+ "_NAME"); index++)
			addCommandJournalsVar(httpReq, Integer.toString(index), str);
		addCommandJournalsVar(httpReq, "", str);
		return str.toString();
	}

	protected void addForumJournalsVar(final HTTPRequest httpReq,
			final String index, final StringBuilder str) {
		final String name = httpReq.getUrlParameter("FORUMJOURNAL_" + index
				+ "_NAME");
		if ((name != null) && (name.trim().length() > 0)
				&& (!name.trim().equalsIgnoreCase("auction"))) {
			if (str.length() > 0)
				str.append(", ");
			str.append(name.trim().replace(',', ' ')).append(" ");

			for (JournalsLibrary.ForumJournalFlags flag : JournalsLibrary.ForumJournalFlags
					.values()) {
				String val = httpReq.getUrlParameter("FORUMJOURNAL_" + index
						+ "_" + flag.name());
				if ((val != null) && (val.trim().length() > 0))
					str.append(flag.name()).append("=")
							.append(val.trim().replace(',', ' ')).append(" ");
			}
			str.setLength(str.length() - 1);
		}
	}

	protected String buildForumJournalsVar(HTTPRequest httpReq) {
		final StringBuilder str = new StringBuilder("");
		for (int index = 0; httpReq.isUrlParameter("FORUMJOURNAL_" + index
				+ "_NAME"); index++)
			addForumJournalsVar(httpReq, Integer.toString(index), str);
		addForumJournalsVar(httpReq, "", str);
		return str.toString();
	}

}
