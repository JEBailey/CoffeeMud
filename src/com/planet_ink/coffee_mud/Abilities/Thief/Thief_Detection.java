package com.planet_ink.coffee_mud.Abilities.Thief;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Thief_Detection extends ThiefSkill
{
	public String ID() { return "Thief_Detection"; }
	public String name(){ return "Detection";}
	public String displayText(){return "(Detecting hidden...)";}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return 0;}
	public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings = {"DETECT","DETECTION"};
	public int classificationCode(){	return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_ALERT;}
	public String[] triggerStrings(){return triggerStrings;}
	protected Room lastRoom=null;
	private int bonusThisRoom=0;
	
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_OVERLOOKING,bonusThisRoom+proficiency()+affectableStats.getStat(CharStats.STAT_SAVE_OVERLOOKING));
	}
	
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_HIDDEN);
	}

	public void unInvoke()
	{
		MOB M=(MOB)affected;
		super.unInvoke();
		if((M!=null)&&(!M.amDead()))
			M.tell("You stop detecting.");
	}
	
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			if(!CMLib.flags().aliveAwakeMobile((MOB)affected,true))
			{ unInvoke(); return false;}
			if(((MOB)affected).location()!=lastRoom)
			{
				lastRoom=((MOB)affected).location();
				bonusThisRoom=getXLEVELLevel((MOB)affected)*2;
				((MOB)affected).recoverCharStats();
			}
			else
			if(bonusThisRoom<affected.phyStats().level())
			{
				bonusThisRoom+=5;
				((MOB)affected).recoverCharStats();
			}
		}
		return true;
	}

	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.fetchEffect(this.ID())!=null)
				return Ability.QUALITY_INDIFFERENT;
			
			Room R=mob.location();
			if(R!=null)
				for(int r=0;r<R.numInhabitants();r++)
				{
					MOB M=R.fetchInhabitant(r);
					if((M!=null)&&(M!=mob)&&(CMLib.flags().isHidden(M)))
						return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
				}
		}
		return super.castingQuality(mob,target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,"<S-NAME> <S-IS-ARE> already detecting hidden things.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		CMMsg msg=CMClass.getMsg(mob,target,this,auto?CMMsg.MASK_ALWAYS:CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,CMMsg.MSG_OK_VISUAL,CMMsg.MSG_OK_VISUAL,auto?"<T-NAME> become(s) very observant.":"<S-NAME> start(s) examining <S-HIS-HER> surroundings carefully.");
		if(!success)
			return beneficialVisualFizzle(mob,null,auto?"":"<S-NAME> look(s) around carefully, but become(s) distracted.");
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,target,asLevel,0);
			target.recoverCharStats();
			target.recoverPhyStats();
			target.phyStats().setSensesMask(mob.phyStats().sensesMask()|PhyStats.CAN_SEE_HIDDEN);
			target.phyStats().setSensesMask(mob.phyStats().sensesMask()|PhyStats.CAN_SEE_SNEAKERS);
			CMLib.commands().postLook(target,false);
			target.recoverPhyStats();
		}
		return success;
	}
}
