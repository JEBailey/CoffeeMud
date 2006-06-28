package com.planet_ink.coffee_mud.Abilities.Prayers;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


import java.util.*;

/* 
   Copyright 2000-2006 Bo Zimmerman

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

public class Prayer_DivineResistance extends Prayer
{
	public String ID() { return "Prayer_DivineResistance"; }
	public String name(){ return "Divine Resistance";}
	public String displayText(){ return "(Divine Resistance)";}
	protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected int canTargetCode(){return Ability.CAN_MOBS;}
	public int abstractQuality(){ return  Ability.QUALITY_BENEFICIAL_OTHERS;}
	public long flags(){return Ability.FLAG_HOLY;}
    protected HashSet permProts=new HashSet();
    protected int prots=4;

	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;

		affectableStats.setStat(CharStats.STAT_SAVE_WATER,100);
		affectableStats.setStat(CharStats.STAT_SAVE_UNDEAD,100);
		affectableStats.setStat(CharStats.STAT_SAVE_TRAPS,100);
		affectableStats.setStat(CharStats.STAT_SAVE_POISON,100);
		affectableStats.setStat(CharStats.STAT_SAVE_PARALYSIS,100);
		affectableStats.setStat(CharStats.STAT_SAVE_MIND,100);
		affectableStats.setStat(CharStats.STAT_SAVE_MAGIC,100);
		affectableStats.setStat(CharStats.STAT_SAVE_JUSTICE,100);
		affectableStats.setStat(CharStats.STAT_SAVE_GENERAL,100);
		affectableStats.setStat(CharStats.STAT_SAVE_GAS,100);
		affectableStats.setStat(CharStats.STAT_SAVE_FIRE,100);
		affectableStats.setStat(CharStats.STAT_SAVE_ELECTRIC,100);
		affectableStats.setStat(CharStats.STAT_SAVE_DISEASE,100);
		affectableStats.setStat(CharStats.STAT_SAVE_COLD,100);
		affectableStats.setStat(CharStats.STAT_SAVE_ACID,100);
	}

	public boolean okMessage(Environmental host, CMMsg msg)
	{

		if((msg.target()==affected)
		&&(affected instanceof MOB)
		&&((msg.tool()==null)||(!permProts.contains(msg.tool())))
		&&(prots>0)
		&&(msg.source().location()!=null))
		{
			boolean proceed=false;
			int sm=msg.sourceMinor();
			int tm=msg.targetMinor();
			for(int i=0;i<CharStats.STAT_MSG_MAP.length;i++)
				if((CharStats.STAT_MSG_MAP[i]>=0)
				&&((sm==CharStats.STAT_MSG_MAP[i])||(tm==CharStats.STAT_MSG_MAP[i])))
					proceed=true;
			if((msg.tool() instanceof Trap)||(proceed))
			{
				if(msg.tool()!=null)
					permProts.add(msg.tool());
				prots--;
				msg.source().location().show((MOB)msg.target(),msg.source(),this,CMMsg.MSG_OK_VISUAL,"<S-YOUPOSS> divine protection glows!");
				if(prots==0)
					unInvoke();
			}
		}
		return super.okMessage(host,msg);
	}

	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,"<S-YOUPOSS> divine resistance fades.");
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
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
			CMMsg msg=CMClass.getMsg(mob,target,this,somanticCastCode(mob,target,auto),(auto?"<T-NAME> become(s) protected by divine resistance.":"^S<S-NAME> "+prayWord(mob)+" for <T-NAMESELF> to be protected by divine resistance.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
				target.recoverEnvStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,target,"<S-NAME> "+prayWord(mob)+" for <T-NAMESELF> to have divine resistance, but nothing happens.");


		// return whether it worked
		return success;
	}
}
