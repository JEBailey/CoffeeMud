package com.planet_ink.coffee_mud.Abilities.Prayers;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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
public class Prayer_MassHeal extends Prayer implements MendingSkill
{
	public String ID() { return "Prayer_MassHeal"; }
	public String name(){ return "Mass Heal";}
	public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_HEALING;}
	public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_HEALINGMAGIC;}

	public boolean supportsMending(Physical item)
	{ 
		return (item instanceof MOB)
				&&((((MOB)item).curState()).getHitPoints()<(((MOB)item).maxState()).getHitPoints());
	}
	
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(!supportsMending(target))
					return Ability.QUALITY_INDIFFERENT;
				if(((MOB)target).charStats().getMyRace().racialCategory().equals("Undead"))
					return Ability.QUALITY_MALICIOUS;
			}
		}
		return super.castingQuality(mob,target);
	}
	
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null) return false;
		for(Iterator e=h.iterator();e.hasNext();)
		{
			MOB target=(MOB)e.next();
			boolean undead=target.charStats().getMyRace().racialCategory().equals("Undead");
			if(success)
			{
				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB.  Then tell everyone else
				// what happened.
				CMMsg msg=CMClass.getMsg(mob,target,this,(!undead?0:CMMsg.MASK_MALICIOUS)|verbalCastCode(mob,target,auto),auto?"<T-NAME> become(s) surrounded by a white light.":"^S<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>.^?");
				if(mob.location().okMessage(mob,msg))
				{
					mob.location().send(mob,msg);
					int healing=CMLib.dice().roll(adjustedLevel(mob,asLevel),5,adjustedLevel(mob,asLevel));
					CMLib.combat().postHealing(mob,target,this,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,healing,null);
					target.tell("You feel tons better!");
				}
			}
			else
				beneficialWordsFizzle(mob,target,auto?"":"<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, but "+hisHerDiety(mob)+" does not heed.");
		}

		// return whether it worked
		return success;
	}
}
