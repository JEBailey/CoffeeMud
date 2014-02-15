package com.planet_ink.coffee_mud.Abilities.Spells;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
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
@SuppressWarnings("rawtypes")
public class Spell_SummonArmy extends Spell
{
	public String ID() { return "Spell_SummonArmy"; }
	public String name(){return "Summon Army";}
	public String displayText(){return "(Monster Summoning)";}
	public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	public long flags(){return Ability.FLAG_SUMMONING;}
	public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}
	public boolean hasFought=false;

	public void unInvoke()
	{
		MOB mob=(MOB)affected;
		super.unInvoke();
		if((canBeUninvoked())&&(mob!=null))
		{
			if(mob.amDead()) mob.setLocation(null);
			mob.destroy();
		}
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected)||msg.amISource(((MOB)affected).amFollowing()))
		&&(msg.sourceMinor()==CMMsg.TYP_QUIT))
		{
			unInvoke();
			if(msg.source().playerStats()!=null) msg.source().playerStats().setLastUpdated(0);
		}
	}
	
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected==null)
		||(!(affected instanceof MOB))
		||(((MOB)affected).amDead())
		||(((MOB)affected).amFollowing()==null)
		||((hasFought)&&(!((MOB)affected).isInCombat())))
		{
			unInvoke();
			return false;
		}
		else
		if(!hasFought)
			hasFought=((MOB)affected).isInCombat();
		return super.tick(ticking,tickID);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			invoker=mob;
			CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":"^S<S-NAME> summon(s) help from the Java Plane.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				String[] choices={"Dog","Orc","Tiger","Troll","Chimp","BrownBear","Goblin","LargeBat","GiantScorpion","Rattlesnake","Ogre"};
				int num=(mob.phyStats().level()+(getXLEVELLevel(mob)+(2*getX1Level(mob))))/3;
				for(int i=0;i<num;i++)
				{
					MOB newMOB=CMClass.getMOB(choices[CMLib.dice().roll(1,choices.length,-1)]);
					newMOB.addNonUninvokableEffect(CMClass.getAbility("Prop_ModExperience"));
					newMOB.setLocation(mob.location());
					newMOB.basePhyStats().setRejuv(PhyStats.NO_REJUV);
					newMOB.recoverCharStats();
					newMOB.recoverPhyStats();
					newMOB.recoverMaxState();
					newMOB.resetToMaxState();
					newMOB.bringToLife(mob.location(),true);
					CMLib.beanCounter().clearZeroMoney(newMOB,null);
					newMOB.location().showOthers(newMOB,null,CMMsg.MSG_OK_ACTION,"<S-NAME> appears!");
					newMOB.setStartRoom(null); // keep before postFollow for Conquest
					newMOB.setVictim(mob.getVictim());
					CMLib.commands().postFollow(newMOB,mob,true);
					if(newMOB.amFollowing()!=mob)
						newMOB.setFollowing(mob);
					if(newMOB.getVictim()!=null)
						newMOB.getVictim().setVictim(newMOB);
					hasFought=false;
					beneficialAffect(mob,newMOB,asLevel,0);
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,"<S-NAME> call(s) for magical help, but chokes on the words.");

		// return whether it worked
		return success;
	}

}
