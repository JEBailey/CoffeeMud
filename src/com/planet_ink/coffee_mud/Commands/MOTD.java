package com.planet_ink.coffee_mud.Commands;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.Quest;
import com.planet_ink.coffee_mud.Libraries.interfaces.DatabaseEngine;
import com.planet_ink.coffee_mud.Libraries.interfaces.DatabaseEngine.PlayerData;
import com.planet_ink.coffee_mud.Libraries.interfaces.JournalsLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.MOBS.interfaces.PostOffice;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Resources;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.collections.ReadOnlyVector;
import com.planet_ink.coffee_mud.core.exceptions.HTTPRedirectException;

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
public class MOTD extends StdCommand {
	public MOTD() {
	}

	private final String[] access = { "MOTD", "NEWS" };

	public String[] getAccessWords() {
		return access;
	}

	private static Vector<String> DEFAULT_CMD = new ReadOnlyVector<String>(
			new String[] { "MOTD", "AGAIN" });

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		boolean pause = false;
		String what = "MOTD";
		if ((commands != null) && (commands.size() > 0)) {
			String firstWord = ((String) commands.firstElement()).toUpperCase();
			if (CMParms.indexOf(this.getAccessWords(), firstWord) > 0)
				what = firstWord;
			if ((((String) commands.lastElement()).equalsIgnoreCase("PAUSE"))) {
				pause = true;
				commands.removeElementAt(commands.size() - 1);
			}
			if (commands.size() == 1) {
				commands.add("AGAIN");
			}
		} else
			commands = DEFAULT_CMD;
		String parm = CMParms.combine(commands, 1);
		if ((mob.playerStats() != null)
				&& (parm.equalsIgnoreCase("AGAIN") || parm
						.equalsIgnoreCase("NEW"))) {
			StringBuffer buf = new StringBuffer("");
			try {
				String msg = new CMFile(Resources.buildResourcePath("text")
						+ "motd.txt", null).text().toString();
				if (msg.length() > 0) {
					if (msg.startsWith("<cmvp>"))
						msg = new String(CMLib.webMacroFilter()
								.virtualPageFilter(msg.substring(6).getBytes()));
					buf.append(msg
							+ "\n\r--------------------------------------\n\r");
				}

				List<JournalsLibrary.JournalEntry> journal = new LinkedList<JournalsLibrary.JournalEntry>();
				journal.addAll(CMLib.database().DBReadJournalMsgs(
						"CoffeeMud News")); // deprecated
				journal.addAll(CMLib.database()
						.DBReadJournalMsgs("SYSTEM_NEWS"));
				for (int which = 0; which < journal.size(); which++) {
					JournalsLibrary.JournalEntry entry = journal.get(which);
					String from = entry.from;
					long last = entry.date;
					String to = entry.to;
					String subject = entry.subj;
					String message = entry.msg;
					long compdate = entry.update;
					if (compdate > mob.playerStats().lastDateTime()) {
						boolean allMine = to.equalsIgnoreCase(mob.Name())
								|| from.equalsIgnoreCase(mob.Name());
						if (to.toUpperCase().trim().startsWith("MASK=")
								&& CMLib.masking().maskCheck(
										to.trim().substring(5), mob, true)) {
							allMine = true;
							to = CMLib.masking().maskDesc(
									to.trim().substring(5), true);
						}
						if (to.equalsIgnoreCase("ALL") || allMine) {
							if (message.startsWith("<cmvp>"))
								message = new String(
										CMLib.webMacroFilter()
												.virtualPageFilter(
														message.substring(6)
																.getBytes()));
							buf.append("\n\rNews: "
									+ CMLib.time().date2String(last)
									+ "\n\rFROM: "
									+ CMStrings.padRight(from, 15)
									+ "\n\rTO  : " + CMStrings.padRight(to, 15)
									+ "\n\rSUBJ: " + subject + "\n\r" + message);
							buf.append("\n\r--------------------------------------\n\r");
						}
					}
				}
				Vector postalChains = new Vector();
				Vector postalBranches = new Vector();
				PostOffice P = null;
				for (Enumeration e = CMLib.map().postOffices(); e
						.hasMoreElements();) {
					P = (PostOffice) e.nextElement();
					if (!postalChains.contains(P.postalChain()))
						postalChains.addElement(P.postalChain());
					if (!postalBranches.contains(P.postalBranch()))
						postalBranches.addElement(P.postalBranch());
				}
				if ((postalChains.size() > 0) && (P != null)) {
					List<PlayerData> V = CMLib.database().DBReadData(
							mob.Name(), postalChains);
					Map<PostOffice, int[]> res = getPostalResults(V, mob
							.playerStats().lastDateTime());
					for (Iterator<PostOffice> e = res.keySet().iterator(); e
							.hasNext();) {
						P = e.next();
						int[] ct = res.get(P);
						buf.append("\n\r" + report("You have", P, ct));
					}
					Map<PostOffice, int[]> res2 = new Hashtable();
					for (Pair<Clan, Integer> clanPair : CMLib.clans()
							.findPrivilegedClans(mob, Clan.Function.WITHDRAW)) {
						if (clanPair != null) {
							Clan C = clanPair.first;
							if (C.getAuthority(clanPair.second.intValue(),
									Clan.Function.WITHDRAW) != Clan.Authority.CAN_NOT_DO) {
								V = CMLib.database().DBReadData(C.name(),
										postalChains);
								if (V.size() > 0) {
									res2.putAll(getPostalResults(V, mob
											.playerStats().lastDateTime()));
								}
							}
							for (Iterator<PostOffice> e = res2.keySet()
									.iterator(); e.hasNext();) {
								P = e.next();
								int[] ct = res2.get(P);
								buf.append("\n\r"
										+ report(
												"Your " + C.getGovernmentName()
														+ " " + C.getName()
														+ " has", P, ct));
							}
						}
					}
					if ((res.size() > 0) || (res2.size() > 0))
						buf.append("\n\r--------------------------------------\n\r");
				}

				Vector<JournalsLibrary.CommandJournal> myEchoableCommandJournals = new Vector<JournalsLibrary.CommandJournal>();
				for (Enumeration<JournalsLibrary.CommandJournal> e = CMLib
						.journals().commandJournals(); e.hasMoreElements();) {
					JournalsLibrary.CommandJournal CMJ = e.nextElement();
					if ((CMJ.getFlag(JournalsLibrary.CommandJournalFlags.ADMINECHO) != null)
							&& ((CMSecurity.isJournalAccessAllowed(mob,
									CMJ.NAME())) || CMSecurity.isAllowed(mob,
									mob.location(),
									CMSecurity.SecFlag.LISTADMIN)))
						myEchoableCommandJournals.addElement(CMJ);
				}
				boolean CJseparator = false;
				for (int cj = 0; cj < myEchoableCommandJournals.size(); cj++) {
					JournalsLibrary.CommandJournal CMJ = myEchoableCommandJournals
							.elementAt(cj);
					List<JournalsLibrary.JournalEntry> items = CMLib.database()
							.DBReadJournalMsgs("SYSTEM_" + CMJ.NAME() + "S");
					if (items != null)
						for (int i = 0; i < items.size(); i++) {
							JournalsLibrary.JournalEntry entry = items.get(i);
							String from = entry.from;
							String message = entry.msg;
							long compdate = entry.update;
							if (compdate > mob.playerStats().lastDateTime()) {
								buf.append("\n\rNEW " + CMJ.NAME() + " from "
										+ from + ": " + message + "\n\r");
								CJseparator = true;
							}
						}
				}
				if (CJseparator)
					buf.append("\n\r--------------------------------------\n\r");

				if ((!CMath.bset(mob.getBitmap(), MOB.ATT_AUTOFORWARD))
						&& (CMProps.getVar(CMProps.Str.MAILBOX).length() > 0)) {
					List<JournalsLibrary.JournalEntry> msgs = CMLib.database()
							.DBReadJournalMsgs(
									CMProps.getVar(CMProps.Str.MAILBOX));
					int mymsgs = 0;
					for (int num = 0; num < msgs.size(); num++) {
						JournalsLibrary.JournalEntry thismsg = msgs.get(num);
						String to = thismsg.to;
						if (to.equalsIgnoreCase("all")
								|| to.equalsIgnoreCase(mob.Name())
								|| (to.toUpperCase().trim().startsWith("MASK=") && CMLib
										.masking().maskCheck(
												to.trim().substring(5), mob,
												true)))
							mymsgs++;
					}
					if (mymsgs > 0)
						buf.append("\n\r^ZYou have mail waiting. Enter 'EMAIL BOX' to read.^?^.\n\r");
				}

				List<Quest> qQVec = CMLib.quests().getPlayerPersistantQuests(
						mob);
				if (mob.session() != null)
					if (buf.length() > 0) {
						if (qQVec.size() > 0)
							buf.append("\n\r^HYou are on "
									+ qQVec.size()
									+ " quest(s).  Enter QUESTS to see them!.^?^.\n\r");
						mob.session().wraplessPrintln(
								"\n\r--------------------------------------\n\r"
										+ buf.toString());
						if (pause) {
							mob.session().prompt("\n\rPress ENTER: ", 10000);
							mob.session().println("\n\r");
						}
					} else if (qQVec.size() > 0)
						buf.append("\n\r^HYou are on "
								+ qQVec.size()
								+ " quest(s).  Enter QUESTS to see them!.^?^.\n\r");
					else if (CMParms.combine(commands, 1).equalsIgnoreCase(
							"AGAIN"))
						mob.session().println("No " + what + " to re-read.");
			} catch (HTTPRedirectException e) {
			}
			return false;
		}
		if (parm.equalsIgnoreCase("ON")) {
			if (CMath.bset(mob.getBitmap(), MOB.ATT_DAILYMESSAGE)) {
				mob.setBitmap(CMath.unsetb(mob.getBitmap(),
						MOB.ATT_DAILYMESSAGE));
				mob.tell("The daily messages have been turned on.");
			} else {
				mob.tell("The daily messages are already on.");
			}
		} else if (parm.equalsIgnoreCase("OFF")) {
			if (!CMath.bset(mob.getBitmap(), MOB.ATT_DAILYMESSAGE)) {
				mob.setBitmap(CMath.setb(mob.getBitmap(), MOB.ATT_DAILYMESSAGE));
				mob.tell("The daily messages have been turned off.");
			} else {
				mob.tell("The daily messages are already off.");
			}
		} else {
			mob.tell("'" + parm
					+ "' is not a valid parameter.  Try ON, OFF, or AGAIN.");
		}
		return false;
	}

	private String report(String whom, PostOffice P, int[] ct) {
		String branchName = P.postalBranch();
		if ((P instanceof MOB) && (((MOB) P).getStartRoom() != null))
			branchName = ((MOB) P).getStartRoom().getArea().Name();
		else {
			int x = branchName.indexOf('#');
			if (x >= 0)
				branchName = branchName.substring(0, x);
		}
		if (ct[0] > 0)
			return whom + " " + ct[0] + " new of " + ct[1] + " items at the "
					+ branchName + " branch of the " + P.postalChain()
					+ " post office.";
		return whom + " " + ct[1] + " items still waiting at the " + branchName
				+ " branch of the " + P.postalChain() + " post office.";
	}

	private Map<PostOffice, int[]> getPostalResults(List<PlayerData> mailData,
			long newTimeDate) {
		Hashtable<PostOffice, int[]> results = new Hashtable<PostOffice, int[]>();
		PostOffice P = null;
		for (int i = 0; i < mailData.size(); i++) {
			DatabaseEngine.PlayerData letter = mailData.get(i);
			String chain = letter.section;
			String branch = letter.key;
			int x = branch.indexOf(';');
			if (x < 0)
				continue;
			branch = branch.substring(0, x);
			P = CMLib.map().getPostOffice(chain, branch);
			if (P == null)
				continue;
			PostOffice.MailPiece pieces = P.parsePostalItemData(letter.xml);
			int[] ct = results.get(P);
			if (ct == null) {
				ct = new int[2];
				results.put(P, ct);
			}
			ct[1]++;
			if (CMath.s_long(pieces.time) > newTimeDate)
				ct[0]++;
		}
		return results;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
