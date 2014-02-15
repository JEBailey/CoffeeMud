package com.planet_ink.coffee_mud.Abilities.Diseases;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
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

public class Disease_Lockjaw extends Disease
{
	public String ID() { return "Disease_Lockjaw"; }
	public String name(){ return "Lockjaw";}
	public String displayText(){ return "(Lockjaw)";}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return CAN_MOBS;}
	public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	public boolean putInCommandlist(){return false;}
	public int difficultyLevel(){return 2;}

	protected int DISEASE_TICKS(){return 9999999;}
	protected int DISEASE_DELAY(){return CMProps.getIntVar( CMProps.Int.TICKSPERMUDDAY );}
	protected String DISEASE_DONE(){return "Your lockjaw is cured.";}
	protected String DISEASE_START(){return "^G<S-NAME> get(s) lockjaw!^?";}
	protected String DISEASE_AFFECT(){return "";}
	public int spreadBitmap(){return DiseaseAffect.SPREAD_CONSUMPTION;}

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		MOB mob=(MOB)affected;
		if((!mob.amDead())&&((--diseaseTick)<=0))
		{
			diseaseTick=DISEASE_DELAY();
			if(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_DISEASE))
			{
				unInvoke();
				return false;
			}
			return true;
		}
		return true;
	}
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return super.okMessage(myHost,msg);

		MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if((msg.amISource(mob))
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
		&&((msg.sourceMinor()==CMMsg.TYP_EAT)||(msg.sourceMinor()==CMMsg.TYP_DRINK)))
		{
			mob.tell("You can't open your mouth!");
			return false;
		}

		return super.okMessage(myHost,msg);
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(affected==null) return;
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SPEAK);
	}
}
