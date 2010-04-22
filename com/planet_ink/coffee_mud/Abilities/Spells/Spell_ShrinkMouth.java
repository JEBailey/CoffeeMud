package com.planet_ink.coffee_mud.Abilities.Spells;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
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
   Copyright 2000-2010 Bo Zimmerman

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
@SuppressWarnings("unchecked")
public class Spell_ShrinkMouth extends Spell
{
	public String ID() { return "Spell_ShrinkMouth"; }
	public String name(){return "Shrink Mouth";}
	public String displayText(){return "(Shrunken Mouth)";}
	public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return CAN_MOBS;}
	public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_TRANSMUTATION;}

	public void unInvoke()
	{
		// undo the affects of this spell
		if((canBeUninvoked())&&(affected!=null))
		{
			if(affected instanceof MOB)
			{
				MOB mob=(MOB)affected;
				if((mob.location()!=null)&&(!mob.amDead()))
					mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,"<S-YOUPOSS> mouth return(s) to its normal size.");
			}
		}
		super.unInvoke();
	}

	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if(!super.okMessage(myHost, msg))
			return false;
		if((msg.targetMinor()==CMMsg.TYP_EAT)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected)))
		{
			msg.source().tell("Your mouth is too tiny to eat!");
			return false;
		}
		return true;
	}
	
	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if((success)&&((target instanceof MOB)||(target instanceof Item)))
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"<T-YOUPOSS> mouth pucker(s) shut!":"^S<S-NAME> cast(s) a puckering spell on <T-NAMESELF>.^?");
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
                Ability A=target.fetchEffect("Spell_BigMouth");
                boolean isJustUnInvoking=false;
                if((A!=null)&&(A.canBeUninvoked()))
                {
                    A.unInvoke();
                    isJustUnInvoking=true;
                }
                if((!isJustUnInvoking)&&(msg.value()<=0))
                {
    				beneficialAffect(mob,target,asLevel,0);
    				if((!auto)&&(target.location()!=null))
	    				target.location().show(mob, target, CMMsg.MSG_OK_VISUAL, "<T-YOUPOSS> mouth shrinks!");
					CMLib.utensils().confirmWearability(target);
                }
			}
		}
		else
			beneficialWordsFizzle(mob,target,"<S-NAME> attempt(s) to cast a puckering spell, but fail(s).");

		return success;
	}
}
