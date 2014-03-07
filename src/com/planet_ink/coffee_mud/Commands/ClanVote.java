package com.planet_ink.coffee_mud.Commands;

import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.Clan.Function;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.collections.PairVector;

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
public class ClanVote extends StdCommand {
	public ClanVote() {
	}

	private final String[] access = { "CLANVOTE" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		StringBuffer msg = new StringBuffer("");
		String voteNumStr = (commands.size() > 1) ? (String) commands
				.get(commands.size() - 1) : "";
		String clanName = "";
		if (!CMath.isInteger(voteNumStr)) {
			clanName = (commands.size() > 2) ? CMParms.combine(commands, 1,
					commands.size()) : "";
			voteNumStr = "";
		} else
			clanName = (commands.size() > 2) ? CMParms.combine(commands, 1,
					commands.size() - 1) : "";

		Clan C = null;
		Integer clanRole = null;
		for (Pair<Clan, Integer> c : mob.clans())
			if ((clanName.length() == 0)
					|| (CMLib.english().containsString(c.first.getName(),
							clanName))) {
				C = c.first;
				clanRole = c.second;
				break;
			}

		if ((C == null) || (clanRole == null)) {
			mob.tell("You can't vote for anything in "
					+ ((clanName.length() == 0) ? "any clan" : clanName) + ".");
			return false;
		} else if (!mob.isMonster()) {
			Vector votesForYou = new Vector();
			for (Enumeration e = C.votes(); e.hasMoreElements();) {
				Clan.ClanVote CV = (Clan.ClanVote) e.nextElement();
				if (((CV.function == Clan.Function.ASSIGN.ordinal()) && (C
						.getAuthority(clanRole.intValue(),
								Clan.Function.VOTE_ASSIGN) != Clan.Authority.CAN_NOT_DO))
						|| ((CV.function != Clan.Function.ASSIGN.ordinal()) && (C
								.getAuthority(clanRole.intValue(),
										Clan.Function.VOTE_OTHER) != Clan.Authority.CAN_NOT_DO)))
					votesForYou.addElement(CV);
			}
			if (voteNumStr.length() == 0) {
				if (votesForYou.size() == 0)
					msg.append("Your " + C.getGovernmentName()
							+ " does not have anything up for your vote.");
				else {
					msg.append(" " + CMStrings.padRight("#", 3)
							+ CMStrings.padRight("Status", 15)
							+ "Command to execute\n\r");
					for (int v = 0; v < votesForYou.size(); v++) {
						Clan.ClanVote CV = (Clan.ClanVote) votesForYou
								.elementAt(v);
						boolean ivoted = ((CV.votes != null) && (CV.votes
								.containsFirst(mob.Name())));
						int votesCast = (CV.votes != null) ? CV.votes.size()
								: 0;
						msg.append((ivoted ? "*" : " ")
								+ CMStrings.padRight("" + (v + 1), 3)
								+ CMStrings
										.padRight(
												((CV.voteStatus == Clan.VSTAT_STARTED) ? (votesCast + " votes cast")
														: (Clan.VSTAT_DESCS[CV.voteStatus])),
												15)
								+ CMStrings.padRight(CV.matter, 55) + "\n\r");
					}
					msg.append("\n\rEnter CLANVOTE [#] to see details or place your vote.");
				}
			} else {
				int which = CMath.s_int(voteNumStr) - 1;
				Clan.ClanVote CV = null;
				if ((which >= 0) && (which < votesForYou.size()))
					CV = (Clan.ClanVote) votesForYou.elementAt(which);
				if (CV == null)
					msg.append("That vote does not exist.  Use CLANVOTE to see a list.");
				else {
					int yeas = 0;
					int nays = 0;
					Boolean myVote = null;
					if (CV.votes != null)
						for (int vs = 0; vs < CV.votes.size(); vs++) {
							if (CV.votes.getFirst(vs).equals(mob.Name()))
								myVote = CV.votes.getSecond(vs);
							if (CV.votes.getSecond(vs).booleanValue())
								yeas++;
							else
								nays++;
						}
					msg.append("Vote       : " + (which + 1) + "\n\r");
					msg.append("Started by : " + CV.voteStarter + "\n\r");
					if (CV.voteStatus == Clan.VSTAT_STARTED)
						msg.append("Started on : "
								+ CMLib.time().date2String(CV.voteStarted)
								+ "\n\r");
					else
						msg.append("Ended on   : "
								+ CMLib.time().date2String(CV.voteStarted)
								+ "\n\r");
					msg.append("Status     : "
							+ Clan.VSTAT_DESCS[CV.voteStatus] + "\n\r");
					switch (CV.voteStatus) {
					case Clan.VSTAT_STARTED:
						msg.append("If passed, the following command would be executed:\n\r");
						break;
					case Clan.VSTAT_PASSED:
						msg.append("Results    : " + yeas + " Yeas, " + nays
								+ " Nays\n\r");
						msg.append("The following command has been executed:\n\r");
						break;
					case Clan.VSTAT_FAILED:
						msg.append("Results    : " + yeas + " Yeas, " + nays
								+ " Nays\n\r");
						msg.append("The following command will not be executed:\n\r");
						break;
					}
					msg.append(CV.matter + "\n\r");
					if ((CV.voteStatus == Clan.VSTAT_STARTED)
							&& (myVote == null)) {
						mob.tell(msg.toString());
						msg = new StringBuffer("");
						StringBuffer prompt = new StringBuffer("");
						String choices = "";
						if (CV.votes == null)
							CV.votes = new PairVector<String, Boolean>();
						prompt.append("Y)EA N)AY ");
						choices = "YN";
						if (CV.voteStarter.equals(mob.Name())) {
							prompt.append("C)ANCEL ");
							choices += "C";
						}
						String enterWhat = "to skip";
						// if(myVote!=null)
						// enterWhat=("to keep ("+(myVote.booleanValue()?"Y":"N")+") ");
						// // no revote
						boolean updateVote = false;
						if ((prompt.length() > 0) && (mob.session() != null)) {
							String answer = mob.session().choose(
									"Choices: " + prompt.toString()
											+ "or ENTER " + enterWhat + ": ",
									choices, "");
							if (answer.length() > 0)
								switch (answer.toUpperCase().charAt(0)) {
								case 'Y':
									msg.append("Your YEA vote is recorded.");
									CV.votes.addElement(mob.Name(),
											Boolean.TRUE);
									updateVote = true;
									yeas++;
									break;
								case 'N':
									CV.votes.addElement(mob.Name(),
											Boolean.FALSE);
									msg.append("Your NAY vote is recorded.");
									updateVote = true;
									nays++;
									break;
								case 'C':
									if ((mob.session() != null)
											&& (mob.session()
													.confirm(
															"This will cancel this entire vote, are you sure (N/y)?",
															"N"))) {
										C.delVote(CV);
										CMLib.clans().clanAnnounce(
												mob,
												"A prior vote for "
														+ C.getGovernmentName()
														+ " " + C.clanID()
														+ " has been deleted.");
										msg.append("The vote has been deleted.");
										updateVote = true;
									}
									break;
								}
						}
						int numVotes = C
								.getNumVoters(Function.values()[CV.function]);
						if (numVotes <= (yeas + nays)) {
							updateVote = true;
							if (yeas <= nays)
								CV.voteStatus = Clan.VSTAT_FAILED;
							else {
								CV.voteStatus = Clan.VSTAT_PASSED;
								MOB mob2 = CMClass.getFactoryMOB();
								mob2.setName(C.clanID());
								mob2.setClan(C.clanID(),
										C.getTopRankedRoles(Function.ASSIGN)
												.get(0).intValue());
								mob2.basePhyStats().setLevel(1000);
								if (mob2.location() == null) {
									mob2.setLocation(mob2.getStartRoom());
									if (mob2.location() == null)
										mob2.setLocation(CMLib.map()
												.getRandomRoom());
								}
								Vector<String> V = CMParms.parse(CV.matter);
								mob2.doCommand(V, metaFlags
										| Command.METAFLAG_FORCED);
								mob2.destroy();
							}
						}
						if (updateVote)
							C.updateVotes();
					}
				}
			}
		}
		mob.tell(msg.toString());
		return false;
	}

	public boolean canBeOrdered() {
		return false;
	}

}