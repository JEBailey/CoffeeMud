package com.planet_ink.coffee_mud.Abilities.Fighter;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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

public class Fighter_TrueShot extends FighterSkill
{
	public String ID() { return "Fighter_TrueShot"; }
	public String name(){ return "True Shot";}
	public String displayText(){ return "";}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return 0;}
	public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	public boolean isAutoInvoked(){return true;}
	public boolean canBeUninvoked(){return false;}
	protected boolean gettingBonus=false;

	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(!(affected instanceof MOB)) return;
		Item w=((MOB)affected).fetchWieldedItem();
		if((w==null)||(!(w instanceof Weapon))) return;
		if((((Weapon)w).weaponClassification()==Weapon.CLASS_RANGED)
		||(((Weapon)w).weaponClassification()==Weapon.CLASS_THROWN))
		{
			gettingBonus=true;
			int bonus=(int)Math.round(CMath.mul(affectableStats.attackAdjustment(),(CMath.div(proficiency(),200.0-(10*getXLEVELLevel(invoker()))))));
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+bonus);
		}
		else
			gettingBonus=false;
	}
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if(!(affected instanceof MOB))
			return;

		MOB mob=(MOB)affected;

		if((msg.amISource(mob))
		&&(gettingBonus)
		&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		&&(CMLib.dice().rollPercentage()>95)
		&&(mob.isInCombat())
		&&(!mob.amDead())
		&&(msg.target() instanceof MOB))
			helpProficiency(mob, 0);
	}
}
