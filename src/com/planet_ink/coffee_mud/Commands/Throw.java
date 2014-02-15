package com.planet_ink.coffee_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.SpaceShip;
import com.planet_ink.coffee_mud.Items.interfaces.SpellHolder;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
@SuppressWarnings({"unchecked","rawtypes"})
public class Throw extends StdCommand
{
	public Throw(){}

	private final String[] access={"THROW","TOSS"};
	public String[] getAccessWords(){return access;}
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((commands.size()==2)&&(mob.isInCombat()))
			commands.addElement(mob.getVictim().location().getContextName(mob.getVictim()));
		if(commands.size()<3)
		{
			mob.tell("Throw what, where or at whom?");
			return false;
		}
		commands.removeElementAt(0);
		String str=(String)commands.lastElement();
		commands.removeElement(str);
		String what=CMParms.combine(commands,0);
		Item item=mob.fetchItem(null,Wearable.FILTER_WORNONLY,what);
		if(item==null) item=mob.findItem(null,what);
		if((item==null)||(!CMLib.flags().canBeSeenBy(item,mob)))
		{
			mob.tell("You don't seem to have a '"+what+"'!");
			return false;
		}
		if((!item.amWearingAt(Wearable.WORN_HELD))&&(!item.amWearingAt(Wearable.WORN_WIELD)))
		{
			mob.tell("You aren't holding or wielding "+item.name()+"!");
			return false;
		}

		int dir=Directions.getGoodDirectionCode(str);
		Environmental target=null;
		if(dir<0)
			target=mob.location().fetchInhabitant(str);
		else
		{
			target=mob.location().getRoomInDir(dir);
			if((target==null)
			||(mob.location().getExitInDir(dir)==null)
			||(!mob.location().getExitInDir(dir).isOpen()))
			{
				mob.tell("You can't throw anything that way!");
				return false;
			}
			boolean amOutside=((mob.location().domainType()&Room.INDOORS)==0);
			boolean isOutside=((((Room)target).domainType()&Room.INDOORS)==0);
			boolean isUp=(mob.location().getRoomInDir(Directions.UP)==target);
			boolean isDown=(mob.location().getRoomInDir(Directions.DOWN)==target);

			if(amOutside&&isOutside&&(!isUp)&&(!isDown)
			&&((((Room)target).domainType()&Room.DOMAIN_OUTDOORS_AIR)==0))
			{
				mob.tell("That's too far to throw "+item.name()+".");
				return false;
			}
		}
		if((dir<0)&&((target==null)||((target!=mob.getVictim())&&(!CMLib.flags().canBeSeenBy(target,mob)))))
		{
			mob.tell("You can't target "+item.name()+" at '"+str+"'!");
			return false;
		}

		if(!(target instanceof Room))
		{
			CMMsg newMsg=CMClass.getMsg(mob,item,null,CMMsg.MSG_REMOVE,null);
			if(mob.location().okMessage(mob,newMsg))
			{
				mob.location().send(mob,newMsg);
				int targetMsg=CMMsg.MSG_THROW;
				if(target instanceof MOB)
				{
					if(item instanceof Weapon)
						targetMsg=CMMsg.MSG_WEAPONATTACK;
					else
					if(item instanceof SpellHolder)
					{
						List<Ability> V=((SpellHolder)item).getSpells();
						for(int v=0;v<V.size();v++)
							if(V.get(v).abstractQuality()==Ability.QUALITY_MALICIOUS)
							{ 
								targetMsg=CMMsg.MSG_WEAPONATTACK; 
								break;
							}
					}
				}
				CMMsg msg=CMClass.getMsg(mob,target,item,CMMsg.MSG_THROW,targetMsg,CMMsg.MSG_THROW,"<S-NAME> throw(s) <O-NAME> at <T-NAMESELF>.");
				if(mob.location().okMessage(mob,msg))
					mob.location().send(mob,msg);
			}
		}
		else
		{
			final boolean useShipDirs=((mob.location() instanceof SpaceShip)||(mob.location().getArea() instanceof SpaceShip));
			final int opDir=Directions.getOpDirectionCode(dir);
			final String inDir=useShipDirs?Directions.getShipInDirectionName(dir):Directions.getInDirectionName(dir);
			final String fromDir=useShipDirs?Directions.getShipFromDirectionName(opDir):Directions.getFromDirectionName(opDir);
			CMMsg msg=CMClass.getMsg(mob,target,item,CMMsg.MSG_THROW,"<S-NAME> throw(s) <O-NAME> "+inDir.toLowerCase()+".");
			CMMsg msg2=CMClass.getMsg(mob,target,item,CMMsg.MSG_THROW,"<O-NAME> fl(ys) in from "+fromDir.toLowerCase()+".");
			if(mob.location().okMessage(mob,msg)&&((Room)target).okMessage(mob,msg2))
			{
				mob.location().send(mob,msg);
				((Room)target).sendOthers(mob,msg2);
			}
		}
		return false;
	}
	public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCombatActionCost(ID());}
	public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getActionCost(ID());}
	public boolean canBeOrdered(){return true;}

	
}
