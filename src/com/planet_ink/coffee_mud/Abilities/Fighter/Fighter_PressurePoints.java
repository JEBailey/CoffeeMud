package com.planet_ink.coffee_mud.Abilities.Fighter;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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

public class Fighter_PressurePoints extends MonkSkill
{
	public String ID() { return "Fighter_PressurePoints"; }
	public String name(){ return "Pressure Points";}
	public String displayText(){ return "";}
	public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected int canTargetCode(){return 0;}
	public boolean isAutoInvoked(){return true;}
	public boolean canBeUninvoked(){return false;}
	public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}

	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(!(affected instanceof MOB))
			return true;

		MOB mob=(MOB)affected;
		if(msg.amISource(mob)
		&&(CMLib.flags().aliveAwakeMobile(mob,true))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.target()!=null)
		&&(mob.getVictim()==msg.target())
		&&(mob.rangeToTarget()==0)
		&&(msg.tool() instanceof Weapon)
		&&(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_NATURAL)
		&&((mob.fetchAbility(ID())==null)||proficiencyCheck(null,(-90)+mob.charStats().getStat(CharStats.STAT_STRENGTH)+(2*getXLEVELLevel(mob)),false))
		&&(!anyWeapons(msg.source())))
		{
			double pctRecovery=(CMath.div(proficiency(),100.0)*Math.random());
			int bonus=(int)Math.round(CMath.mul((msg.value()),pctRecovery));
			msg.setValue(msg.value()+bonus);
			helpProficiency(mob, 0);
		}
		return true;
	}
}
