package com.planet_ink.coffee_mud.Abilities.Songs;
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
   Copyright 2000-2007 Bo Zimmerman

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
public class Skill_Puppeteer extends BardSkill
{
	public String ID() { return "Skill_Puppeteer"; }
	public String name(){ return "Puppeteer";}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return CAN_ITEMS;}
	public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings = {"PUPPETEER","PUPPET"};
	public String[] triggerStrings(){return triggerStrings;}
    public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_FOOLISHNESS;}

	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof Item)))
			return true;

		Item puppet=(Item)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if((msg.amISource(invoker()))
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
		&&((CMath.bset(msg.sourceMajor(),CMMsg.MASK_HANDS))
		||(CMath.bset(msg.sourceMajor(),CMMsg.MASK_MOVE)))
		&&(msg.targetMinor()!=CMMsg.TYP_SPEAK)
	    &&(msg.targetMinor()==CMMsg.TYP_ORDER)
		&&(msg.targetMinor()!=CMMsg.TYP_PANIC)
		&&(!((msg.tool()!=null)&&(msg.tool() instanceof Song)))
		&&(!((msg.tool()!=null)&&(msg.tool() instanceof Skill_Puppeteer)))
		&&(!((msg.tool()!=null)&&(msg.tool() instanceof Dance)))
		&&(!msg.amITarget(puppet)))
		{
		    if((!msg.source().isInCombat())&&(msg.target() instanceof MOB))
		    {
			    if((CMath.isSet(msg.sourceMajor(),CMMsg.MASK_MALICIOUS))
			    ||(CMath.isSet(msg.targetMajor(),CMMsg.MASK_MALICIOUS)))
			        msg.source().setVictim((MOB)msg.target());
		    }
			invoker().location().show(invoker(),puppet,CMMsg.MSG_OK_ACTION,"<S-NAME> animate(s) <T-NAMESELF>.");
			return false;
		}
		else
		if(msg.amITarget(puppet))
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_GET:
			case CMMsg.TYP_REMOVE:
				unInvoke();
				break;
			}
		return super.okMessage(myHost,msg);
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)
		&&(affected instanceof Item)
		&&(((Item)affected).owner()!=null)
		&&(((Item)affected).owner() instanceof Room)
		&&(invoker()!=null)
		&&(invoker().location().isContent((Item)affected)))
		{
			if(invoker().isInCombat())
			{
				boolean isHit=(CMLib.combat().rollToHit(invoker().adjustedAttackBonus(invoker().getVictim())+(5*getXLEVELLevel(invoker()))
                            +((Item)affected).envStats().attackAdjustment(),invoker().getVictim().adjustedArmor()));
				if(!isHit)
					invoker().location().show(invoker(),invoker().getVictim(),affected,CMMsg.MSG_OK_ACTION,"<O-NAME> attacks <T-NAME> and misses!");
				else
					CMLib.combat().postDamage(invoker(),invoker().getVictim(),affected,
											CMLib.dice().roll(1,affected.envStats().level()+(2*getXLEVELLevel(invoker())),1),
											CMMsg.MASK_ALWAYS|CMMsg.TYP_WEAPONATTACK,
											Weapon.TYPE_BASHING,affected.name()+" attacks and <DAMAGE> <T-NAME>!");
			}
			else
			if(CMLib.dice().rollPercentage()>75)
			switch(CMLib.dice().roll(1,5,0))
			{
			case 1:
				invoker().location().showHappens(CMMsg.MSG_OK_VISUAL,affected.name()+" walks around.");
				break;
			case 2:
				invoker().location().showHappens(CMMsg.MSG_OK_VISUAL,affected.name()+" waves its little arms.");
				break;
			case 3:
				invoker().location().showHappens(CMMsg.MSG_OK_VISUAL,affected.name()+" hugs you.");
				break;
			case 4:
				invoker().location().showHappens(CMMsg.MSG_OK_VISUAL,affected.name()+" makes a few fake attacks.");
				break;
			case 5:
				invoker().location().showHappens(CMMsg.MSG_OK_VISUAL,affected.name()+" dances around.");
				break;
			}
		}
		else
			unInvoke();
		return super.tick(ticking,tickID);
	}

	public void unInvoke()
	{
		if((affected!=null)
		&&(affected instanceof Item)
		&&(((Item)affected).owner()!=null)
		&&(((Item)affected).owner() instanceof Room))
			((Room)((Item)affected).owner()).showHappens(CMMsg.MSG_OK_ACTION,affected.name()+" stops moving.");
		super.unInvoke();
	}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		super.affectEnvStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|EnvStats.IS_FLYING);
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		Item target=getTarget(mob,mob.location(),givenTarget,commands,Item.WORNREQ_ANY);
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(target.name()+" is already animated!");
			return false;
		}
		if((!target.Name().toLowerCase().endsWith(" puppet"))
		&&(!target.Name().toLowerCase().endsWith(" marionette")))
		{
			mob.tell("That's not a puppet!");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_NOISYMOVEMENT,CMMsg.TYP_DELICATE_HANDS_ACT,CMMsg.TYP_DELICATE_HANDS_ACT,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				target.unWear();
				if(mob.isMine(target))
					mob.location().show(mob,target,CMMsg.MSG_DROP,"<S-NAME> start(s) animating <T-NAME>!");
				else
					mob.location().show(mob,target,CMMsg.MSG_OK_ACTION,"<S-NAME> start(s) animating <T-NAME>!");
				if(mob.location().isContent(target))
					beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			mob.location().show(mob,target,CMMsg.MSG_OK_ACTION,"<T-NAME> twitch(es) oddly, but does nothing more.");


		// return whether it worked
		return success;
	}
}
