package com.planet_ink.coffee_mud.Items.ClanItems;

import com.planet_ink.coffee_mud.Behaviors.interfaces.LegalBehavior;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.ClanItem;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class StdClanPamphlet extends StdClanItem {
	public String ID() {
		return "StdClanPamphlet";
	}

	protected int tradeTime = -1;

	public StdClanPamphlet() {
		super();

		setName("a clan pamphlet");
		basePhyStats.setWeight(1);
		setDisplayText("a pamphlet belonging to a clan is here.");
		setDescription("");
		secretIdentity = "";
		baseGoldValue = 1;
		setCIType(ClanItem.CI_PROPAGANDA);
		material = RawMaterial.RESOURCE_PAPER;
		recoverPhyStats();
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
			Room R = ((MOB) owner()).location();
			if ((((MOB) owner()).getClanRole(clanID()) != null)
					|| (((--tradeTime) <= 0))) {
				LegalBehavior B = CMLib.law().getLegalBehavior(R);
				if (B != null) {
					String rulingClan = B.rulingOrganization();
					if ((rulingClan != null)
							&& (rulingClan.length() > 0)
							&& (!rulingClan.equals(clanID()))
							&& (((MOB) owner()).getClanRole(rulingClan) != null))
						((MOB) owner()).setClan(rulingClan, -1);
					if (tradeTime <= 0) {
						MOB mob = (MOB) owner();
						if ((rulingClan != null)
								&& (rulingClan.length() > 0)
								&& (!rulingClan.equals(clanID()))
								&& (mob.getClanRole(rulingClan) == null)
								&& (mob.getClanRole(clanID()) == null)
								&& (CMLib.flags().canSpeak(mob))
								&& (CMLib.flags().aliveAwakeMobileUnbound(mob,
										true)) && (R != null)) {
							MOB M = R.fetchRandomInhabitant();
							if ((M != null)
									&& (M != mob)
									&& (M.isMonster())
									&& (M.getClanRole(rulingClan) != null)
									&& (!CMLib.flags().isAnimalIntelligence(M))
									&& (CMLib.flags().canBeSeenBy(M, mob))
									&& (CMLib.flags()
											.canBeHeardMovingBy(M, mob))) {
								CMLib.commands().postSay(mob, M,
										"Hey, take a look at this.", false,
										false);
								ClanItem I = (ClanItem) copyOf();
								mob.addItem(I);
								CMMsg newMsg = CMClass
										.getMsg(mob, M, I, CMMsg.MSG_GIVE,
												"<S-NAME> give(s) <O-NAME> to <T-NAMESELF>.");
								if (mob.location().okMessage(mob, newMsg)
										&& (!((Item) I).amDestroyed()))
									mob.location().send(mob, newMsg);
								if (!M.isMine(I))
									((Item) I).destroy();
								else if (mob.isMine(I))
									((Item) I).destroy();
							}
						}
					}
				}
				if (tradeTime <= 0)
					tradeTime = CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
			}
		}
		return true;
	}
}