package com.planet_ink.coffee_mud.Abilities.Skills;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class Skill_Attack2 extends StdSkill
{
	public String ID() { return "Skill_Attack2"; }
	public String name(){ return "Second Attack";}
	public String displayText(){ return "";}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return 0;}
	public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	public boolean isAutoInvoked(){return true;}
	public boolean canBeUninvoked(){return false;}
	protected int attackToNerf(){ return 2;}
	protected int roundToNerf(){ return 1;}
	protected double nerfAmount(){ return .8;}
	protected double numberOfFullAttacks(){ return 1.0;}
	protected int attacksSinceNerfing=0;
	protected int roundOfNerfing=1;
	protected volatile boolean freeToNerf=false;

	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if((affected instanceof MOB)&&(((MOB)affected).isInCombat()))
			affectableStats.setSpeed(affectableStats.speed()+(numberOfFullAttacks()*(proficiency()/100.0)));
		if((freeToNerf)&& (affectableStats.attackAdjustment()>0))
			affectableStats.setAttackAdjustment((int)Math.round(affectableStats.attackAdjustment() * nerfAmount()));
	}
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if(!(affected instanceof MOB))
			return;

		MOB mob=(MOB)affected;

		if((msg.amISource(mob))
		&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		&&(mob.isInCombat())
		&&(!mob.amDead())
		&&(msg.target() instanceof MOB))
		{
			attacksSinceNerfing++;
			freeToNerf=((attacksSinceNerfing==attackToNerf()) && (roundToNerf()==roundOfNerfing));
			if(freeToNerf)
			{
				if(CMLib.dice().rollPercentage()>97)
					helpProficiency(mob, 0);
				mob.recoverPhyStats();
				//chargen combat fighter iterations=100 skiplevels=4 export=output6.txt 1 91
			}
		}
	}
	
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID)) return false;
		roundOfNerfing++;
		if(roundOfNerfing>roundToNerf())
		{
			roundOfNerfing=1;
			attacksSinceNerfing=0;
			freeToNerf=false;
		}
		return true;
	}
}
