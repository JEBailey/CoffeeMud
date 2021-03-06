package com.planet_ink.coffee_mud.Items.ClanItems;

import com.planet_ink.coffee_mud.Behaviors.interfaces.LegalBehavior;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Items.interfaces.ClanItem;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

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
public class StdClanCard extends StdClanItem {
	public String ID() {
		return "StdClanCard";
	}

	public StdClanCard() {
		super();

		setName("a clan membership card");
		basePhyStats.setWeight(1);
		setDisplayText("a membership card belonging to a clan is here.");
		setDescription("");
		secretIdentity = "";
		baseGoldValue = 1;
		setCIType(ClanItem.CI_ANTIPROPAGANDA);
		material = RawMaterial.RESOURCE_PAPER;
		recoverPhyStats();
	}

	public boolean okMessage(Environmental host, CMMsg msg) {
		if ((msg.target() == owner()) && (msg.tool() instanceof ClanItem)
				&& (owner() instanceof MOB) && (((MOB) owner()).isMonster())
				&& (((ClanItem) msg.tool()).ciType() == ClanItem.CI_PROPAGANDA)
				&& (!((ClanItem) msg.tool()).clanID().equals(clanID()))
				&& (CMLib.flags().isInTheGame((MOB) owner(), true))
				&& (msg.source() != owner())
				&& (CMLib.flags().isInTheGame(msg.source(), true))) {
			if (msg.source()
					.location()
					.show((MOB) msg.target(), msg.source(), msg.tool(),
							CMMsg.MSG_QUIETMOVEMENT,
							"<S-NAME> reject(s) <O-NAME> from <T-NAME>.")) {
				CMLib.commands().postSay((MOB) msg.target(), msg.source(),
						"How dare you!  Give me those!", false, true);
				if (msg.source()
						.location()
						.show((MOB) msg.target(),
								msg.source(),
								null,
								CMMsg.MSG_QUIETMOVEMENT,
								"<S-NAME> takes(s) "
										+ msg.tool().name()
										+ " away from <T-NAME> and destroys it!")) {
					Item I = null;
					for (int i = msg.source().numItems(); i >= 0; i--) {
						I = msg.source().getItem(i);
						if ((I instanceof ClanItem)
								&& (I != msg.tool())
								&& (((ClanItem) I).clanID()
										.equals(((ClanItem) msg.tool())
												.clanID())))
							I.destroy();
					}
				}
				return false;
			}

		}
		return super.okMessage(host, msg);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (!super.tick(ticking, tickID))
			return false;
		if ((tickID == Tickable.TICKID_CLANITEM)
				&& (owner() instanceof MOB)
				&& (clanID().length() > 0)
				&& (((MOB) owner()).isMonster())
				&& (!CMLib.flags().isAnimalIntelligence((MOB) owner()))
				&& (((MOB) owner()).getStartRoom() != null)
				&& (((MOB) owner()).location() != null)
				&& (((MOB) owner()).getStartRoom().getArea() == ((MOB) owner())
						.location().getArea())) {
			if (((MOB) owner()).getClanRole(clanID()) == null) {
				Room R = ((MOB) owner()).location();
				LegalBehavior B = CMLib.law().getLegalBehavior(R);
				if (B != null) {
					String rulingClan = B.rulingOrganization();
					if ((rulingClan != null) && (rulingClan.length() > 0)
							&& (rulingClan.equals(clanID()))) {
						int roleID = 0;
						Clan C = CMLib.clans().getClan(clanID());
						if (C != null)
							roleID = C.getGovernment().getAutoRole();
						((MOB) owner()).setClan(clanID(), roleID);
					}
				}
			}

		}
		return true;
	}
}
