package com.planet_ink.coffee_mud.Abilities.Properties;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

/**
 * <p>Title: False Realities Flavored CoffeeMUD</p>
 * <p>Description: The False Realities Version of CoffeeMUD</p>
 * <p>Copyright: Copyright (c) 2003 Jeremy Vyska</p>
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * <p>you may not use this file except in compliance with the License.
 * <p>You may obtain a copy of the License at
 *
 * <p>  	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * <p>distributed under the License is distributed on an "AS IS" BASIS,
 * <p>WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p>See the License for the specific language governing permissions and
 * <p>limitations under the License.
 * <p>Company: http://www.falserealities.com</p>
 * @author FR - Jeremy Vyska; CM - Bo Zimmerman
 * @version 1.0.0.0
 */

@SuppressWarnings("rawtypes")
public class Prop_ScrapExplode extends Property {

	public String ID() { return "Prop_ScrapExplode"; }
	public String name() { return "Scrap Explode"; }
	protected int canAffectCode() { return Ability.CAN_ITEMS; }

	public void executeMsg(Environmental myHost, CMMsg affect)
	{
		super.executeMsg(myHost, affect);
		if((affect.target()!=null)&&(affect.target().equals(affected))
		   &&(affect.tool()!=null)&&(affect.tool().ID().equals("Scrapping")))
		{
			Item item=(Item)affect.target();
			MOB mob = affect.source();
			if (mob != null)
			{
				Room room = mob.location();
				int damage = 3 * item.phyStats().weight();
				CMLib.combat().postDamage(mob, mob, item, damage*2,  CMMsg.MASK_ALWAYS|CMMsg.TYP_FIRE, Weapon.TYPE_PIERCING,
						"Scrapping " + item.Name() + " causes an explosion which <DAMAGE> <T-NAME>!!!");
				Set<MOB> theBadGuys=mob.getGroupMembers(new HashSet<MOB>());
				for(Iterator e=theBadGuys.iterator();e.hasNext();)
				{
					MOB inhab=(MOB)e.next();
					if (mob != inhab)
						CMLib.combat().postDamage(inhab, inhab, item, damage, CMMsg.MASK_ALWAYS|CMMsg.TYP_FIRE, Weapon.TYPE_PIERCING,
								"Fragments from " + item.Name() + " <DAMAGE> <T-NAME>!");
				}
				room.recoverRoomStats();
			}
			item.destroy();
		}
	}
}
