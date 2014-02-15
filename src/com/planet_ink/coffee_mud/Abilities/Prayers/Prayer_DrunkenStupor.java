package com.planet_ink.coffee_mud.Abilities.Prayers;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
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
public class Prayer_DrunkenStupor extends Prayer
{
	public String ID() { return "Prayer_DrunkenStupor"; }
	public String name(){ return "Drunken Stupor";}
	public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CURSING;}
	public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY|Ability.FLAG_INTOXICATING;}
	public String displayText(){ return "(Drunken Stupor)";}
	protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected int canTargetCode(){return Ability.CAN_MOBS;}
	public Ability inebriation=null;

	protected Ability getInebriation()
	{
		if(inebriation==null)
		{
			inebriation=CMClass.getAbility("Inebriation");
			inebriation.makeLongLasting();
			inebriation.makeNonUninvokable();
			inebriation.setAffectedOne(affected);
		}
		return inebriation;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof MOB)
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
													-((MOB)affected).phyStats().level()
													-(2*super.getXLEVELLevel(invoker())));
	}


	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_DEXTERITY,(affectableStats.getStat(CharStats.STAT_DEXTERITY)-3));
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		Ability A=getInebriation();
		if(A!=null)
			A.tick(ticking,tickID);

		return true;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		Ability A=getInebriation();
		if(A!=null) A.executeMsg(myHost, msg);
		super.executeMsg(myHost, msg);
	}


	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		Ability A=getInebriation();
		if((A==null)||(!A.okMessage(myHost, msg)))
			return false;

		if(msg.source()!=affected)
			return true;
		if(msg.source().location()==null)
			return true;
		if((!msg.targetMajor(CMMsg.MASK_ALWAYS))
		&&(msg.targetMajor()>0))
		{
			if((msg.target() !=null)
				&&(msg.target() instanceof MOB))
					msg.modify(msg.source(),msg.source().location().fetchInhabitant(CMLib.dice().roll(1,msg.source().location().numInhabitants(),0)-1),msg.tool(),msg.sourceCode(),msg.sourceMessage(),msg.targetCode(),msg.targetMessage(),msg.othersCode(),msg.othersMessage());
		}
		return true;
	}

	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
			mob.tell("You feel sober now.");
	}


	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?"":"^S<S-NAME> "+prayForWord(mob)+" to inflict a drunken stupor upon <T-NAMESELF>.^?");
			CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				if((msg.value()<=0)&&(msg2.value()<=0))
				{
					invoker=mob;
					maliciousAffect(mob,target,asLevel,0,-1);
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,"<S-NAME> look(s) a bit tipsy!");
				}
			}
		}
		else
			return maliciousFizzle(mob,target,"<S-NAME> "+prayForWord(mob)+" to inflict a drunken stupor upon <T-NAMESELF>, but flub(s) it.");


		// return whether it worked
		return success;
	}
}
