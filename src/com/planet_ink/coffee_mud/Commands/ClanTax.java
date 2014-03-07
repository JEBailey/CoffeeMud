package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.Clan.Authority;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Common.interfaces.Session.InputCallback;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.Pair;

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
public class ClanTax extends StdCommand {
	public ClanTax() {
	}

	private final String[] access = { "CLANTAX" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(final MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		String taxStr = (commands.size() > 1) ? (String) commands.get(commands
				.size() - 1) : "";
		String clanName = "";
		if (!CMath.isInteger(taxStr)) {
			clanName = (commands.size() > 2) ? CMParms.combine(commands, 1,
					commands.size()) : "";
			taxStr = "";
		} else
			clanName = (commands.size() > 2) ? CMParms.combine(commands, 1,
					commands.size() - 1) : "";

		Clan chkC = null;
		final boolean skipChecks = mob.getClanRole(mob.Name()) != null;
		if (skipChecks)
			chkC = mob.getClanRole(mob.Name()).first;

		if (chkC == null)
			for (Pair<Clan, Integer> c : mob.clans())
				if ((clanName.length() == 0)
						|| (CMLib.english().containsString(c.first.getName(),
								clanName))
						&& (c.first.getAuthority(c.second.intValue(),
								Clan.Function.TAX) != Authority.CAN_NOT_DO)) {
					chkC = c.first;
					break;
				}

		commands.setElementAt(getAccessWords()[0], 0);

		final Clan C = chkC;
		if (C == null) {
			mob.tell("You aren't allowed to tax anyone from "
					+ ((clanName.length() == 0) ? "anything" : clanName) + ".");
			return false;
		}
		if ((!skipChecks)
				&& (!CMLib.clans().goForward(mob, chkC, commands,
						Clan.Function.TAX, false))) {
			mob.tell("You aren't in the right position to set the experience tax rate for your "
					+ C.getGovernmentName() + ".");
			return false;
		}
		final Session S = mob.session();
		if ((skipChecks) && (commands.size() > 1))
			setClanTaxRate(mob, chkC, skipChecks, commands,
					CMath.div(CMath.s_int(CMParms.combine(commands, 1)), 100));
		else if (S != null) {
			if ((taxStr.length() == 0) || (!CMath.isNumber(taxStr))) {
				S.prompt(new InputCallback(InputCallback.Type.PROMPT, "", 0) {
					@Override
					public void showPrompt() {
						S.promptPrint("Enter your " + C.getGovernmentName()
								+ "'s new tax rate (0-25)\n\r: ");
					}

					@Override
					public void timedOut() {
					}

					@Override
					public void callBack() {
						possiblySetClanTaxRate(mob, C, skipChecks, this.input);
					}
				});
			} else
				possiblySetClanTaxRate(mob, chkC, skipChecks, taxStr);
		}
		return false;
	}

	public void possiblySetClanTaxRate(MOB mob, Clan C, boolean skipChecks,
			String t) {
		if (t.length() == 0)
			return;
		int intt = CMath.s_int(t);
		if ((intt < 0) || (intt > 25)) {
			if (mob.session() != null)
				mob.session().println(
						"'" + t + "' is not a valid value.  Try 0-25.");
			return;
		}
		Vector commands = new Vector();
		commands.addElement(getAccessWords()[0]);
		commands.addElement(t);
		setClanTaxRate(mob, C, skipChecks, commands,
				CMath.div(CMath.s_int(t), 100));
	}

	public void setClanTaxRate(MOB mob, Clan C, boolean skipChecks,
			Vector commands, double newRate) {
		if (skipChecks
				|| CMLib.clans().goForward(mob, C, commands, Clan.Function.TAX,
						true)) {
			C.setTaxes(newRate);
			C.update();
			CMLib.clans().clanAnnounce(
					mob,
					"The experience tax rate of " + C.getGovernmentName() + " "
							+ C.clanID() + " has been changed to "
							+ ((int) Math.round(C.getTaxes() * 100.0) + "%."));
		}
	}

	public boolean canBeOrdered() {
		return false;
	}

}