package com.planet_ink.coffee_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.Trap;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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

@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_GuardianHearth extends Prayer
{
	public String ID() { return "Prayer_GuardianHearth"; }
	public String name(){return "Guardian Hearth";}
	public String displayText(){return "(Guardian Hearth)";}
	public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_WARDING;}
	public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	protected int canAffectCode(){return CAN_ROOMS;}
	protected int canTargetCode(){return CAN_ROOMS;}
	protected int overridemana(){return Ability.COST_ALL;}
	public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	protected static HashSet prots=null;

	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof Room)))
			return super.okMessage(myHost,msg);

		if(prots==null)
		{
			prots=new HashSet();
			int[] CMMSGMAP=CharStats.CODES.CMMSGMAP();
			for(int i : CharStats.CODES.SAVING_THROWS())
				if(CMMSGMAP[i]>=0)
				   prots.add(Integer.valueOf(CMMSGMAP[i]));
		}
		Room R=(Room)affected;
		if(((msg.tool() instanceof Trap)
		||(prots.contains(Integer.valueOf(msg.sourceMinor())))
		||(prots.contains(Integer.valueOf(msg.targetMinor()))))
		   &&(msg.target() instanceof MOB)
		   &&((msg.source()!=msg.target())||(msg.sourceMajor(CMMsg.MASK_ALWAYS))))
		{
			Set<MOB> H=((MOB)msg.target()).getGroupMembers(new HashSet<MOB>());
			for(Iterator e=H.iterator();e.hasNext();)
			{
				MOB M=(MOB)e.next();
				if((CMLib.law().doesHavePriviledgesHere(M,R))
				||((text().length()>0)
					&&((M.Name().equals(text()))
						||(M.getClanRole(text())!=null))))
				{
					R.show(((MOB)msg.target()),null,this,CMMsg.MSG_OK_VISUAL,"The guardian hearth protect(s) <S-NAME>!");
					break;
				}
			}
		}
		return super.okMessage(myHost,msg);
	}


	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Physical target=mob.location();
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell("This place is already a guarded hearth.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":"^S<S-NAME> "+prayForWord(mob)+" to guard this place.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				setMiscText(mob.Name());
				
				if((target instanceof Room)
				&&(CMLib.law().doesOwnThisProperty(mob,((Room)target))))
				{
					String landOwnerName=CMLib.law().getLandOwnerName((Room)target);
					if(CMLib.clans().getClan(landOwnerName)!=null)
						setMiscText(landOwnerName);
					target.addNonUninvokableEffect((Ability)this.copyOf());
					CMLib.database().DBUpdateRoom((Room)target);
				}
				else
					beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,"<S-NAME> "+prayForWord(mob)+" to guard this place, but <S-IS-ARE> not answered.");

		return success;
	}
}
