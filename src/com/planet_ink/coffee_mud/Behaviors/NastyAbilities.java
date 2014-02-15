package com.planet_ink.coffee_mud.Behaviors;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
@SuppressWarnings({"unchecked","rawtypes"})
public class NastyAbilities extends ActiveTicker
{
	public String ID(){return "NastyAbilities";}
	protected int canImproveCode(){return Behavior.CAN_MOBS;}
	protected boolean fightok=false;

	private List<Ability> mySkills=null;
	private int numAllSkills=-1;
	
	public NastyAbilities()
	{
		super();
		minTicks=10; maxTicks=20; chance=100;
		tickReset();
	}

	public String accountForYourself()
	{ 
		return "random malicious skill using";
	}

	public void setParms(String newParms)
	{
		super.setParms(newParms);
		fightok=newParms.toUpperCase().indexOf("FIGHTOK")>=0;
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if((canAct(ticking,tickID))&&(ticking instanceof MOB))
		{
			MOB mob=(MOB)ticking;
			Room thisRoom=mob.location();
			if(thisRoom==null) return true;

			double aChance=CMath.div(mob.curState().getMana(),mob.maxState().getMana());
			if((Math.random()>aChance)||(mob.curState().getMana()<50))
				return true;

			if(thisRoom.numPCInhabitants()>0)
			{
				final MOB target=thisRoom.fetchRandomInhabitant();
				MOB followMOB=target;
				if((target!=null)&&(target.amFollowing()!=null))
					followMOB=target.amUltimatelyFollowing();
				if((target!=null)
				&&(target!=mob)
				&&(followMOB.getVictim()!=mob)
				&&(!followMOB.isMonster()))
				{
					if((numAllSkills!=mob.numAllAbilities())||(mySkills==null))
					{
						numAllSkills=mob.numAbilities();
						mySkills=new ArrayList<Ability>();
						for(Enumeration<Ability> e=mob.allAbilities(); e.hasMoreElements();)
						{
							Ability tryThisOne=e.nextElement();
							if((tryThisOne!=null)
							&&(tryThisOne.abstractQuality()==Ability.QUALITY_MALICIOUS)
							&&(((tryThisOne.classificationCode()&Ability.ALL_ACODES)!=Ability.ACODE_PRAYER)
								||tryThisOne.appropriateToMyFactions(mob)))
							{
								mySkills.add(tryThisOne);
							}
						}
					}
					if(mySkills.size()>0)
					{
						Ability tryThisOne=mySkills.get(CMLib.dice().roll(1, mySkills.size(), -1));
						if((mob.fetchEffect(tryThisOne.ID())==null)
						&&(tryThisOne.castingQuality(mob,target)==Ability.QUALITY_MALICIOUS))
						{
							Map<MOB,MOB> H=new Hashtable<MOB,MOB>();
							for(int i=0;i<thisRoom.numInhabitants();i++)
							{
								MOB M=thisRoom.fetchInhabitant(i);
								if((M!=null)&&(M.getVictim()!=null))
									H.put(M,M.getVictim());
							}
							tryThisOne.setProficiency(CMLib.ableMapper().getMaxProficiency(mob,true,tryThisOne.ID()));
							Vector V=new Vector();
							V.addElement(target.name());
							if((tryThisOne.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SONG)
								tryThisOne.invoke(mob,new Vector(),null,false,0);
							else
								tryThisOne.invoke(mob,V,target,false,0);

							if(!fightok)
							for(int i=0;i<thisRoom.numInhabitants();i++)
							{
								MOB M=thisRoom.fetchInhabitant(i);
								if(H.containsKey(M))
									M.setVictim(H.get(M));
								else
									M.setVictim(null);
							}
						}
					}
				}
			}
		}
		return true;
	}
}
