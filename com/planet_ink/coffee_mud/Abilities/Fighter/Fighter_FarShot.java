package com.planet_ink.coffee_mud.Abilities.Fighter;
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
public class Fighter_FarShot extends FighterSkill
{
	public String ID() { return "Fighter_FarShot"; }
	public String name(){ return "Far Shot";}
	public String displayText(){ return "";}
	public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected int canTargetCode(){return 0;}
	public boolean isAutoInvoked(){return true;}
	public boolean canBeUninvoked(){return false;}
	public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	public int checkDown=4;

	protected List<Weapon> qualifiedWeapons=new Vector<Weapon>();

	protected void cloneFix(Ability E)
	{
		super.cloneFix(E);
		qualifiedWeapons=new XVector<Weapon>(((Fighter_FarShot)E).qualifiedWeapons);
	}

	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		qualifiedWeapons=new Vector();
	}
	
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if((affected instanceof Weapon)
		&&((Weapon)affected).amWearingAt(Wearable.IN_INVENTORY))
		{
			Weapon targetW=(Weapon)affected;
			qualifiedWeapons.remove(targetW);
			targetW.delEffect(targetW.fetchEffect(ID()));
			targetW.recoverPhyStats();
		}
		else
		if((msg.source()==affected)
		&&(msg.target() instanceof AmmunitionWeapon))
		{
			AmmunitionWeapon targetW=(AmmunitionWeapon)msg.target();
			if((targetW.weaponClassification()==Weapon.CLASS_RANGED)
			&&(targetW.ammunitionType().length()>0))
			{
				if(((msg.targetMinor()==CMMsg.TYP_WEAR)
					||(msg.targetMinor()==CMMsg.TYP_WIELD)
					||(msg.targetMinor()==CMMsg.TYP_HOLD))
				&&(!qualifiedWeapons.contains(msg.target()))
				&&((msg.source().fetchAbility(ID())==null)||proficiencyCheck(null,0,false)))
				{
					qualifiedWeapons.add(targetW);
					Ability A=(Ability)this.copyOf();
					A.setSavable(false);
					A.setInvoker(invoker());
					targetW.addEffect(A);
					A.makeLongLasting();
					targetW.recoverPhyStats();
				}
				else
				if(((msg.targetMinor()==CMMsg.TYP_REMOVE)
					||(msg.targetMinor()==CMMsg.TYP_DROP))
				&&(qualifiedWeapons.contains(targetW)))
				{
					qualifiedWeapons.remove(targetW);
					targetW.delEffect(targetW.fetchEffect(ID()));
					targetW.recoverPhyStats();
				}
			}
		}
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof Item)
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.SENSE_ITEMNOMAXRANGE);
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if(!(affected instanceof MOB))
			return true;

		MOB mob=(MOB)affected;
		if(--checkDown<=0)
		{
			checkDown=5;
			Item w=mob.fetchWieldedItem();
			if((w!=null)
			&&(w instanceof AmmunitionWeapon)
			&&(((Weapon)w).weaponClassification()==Weapon.CLASS_RANGED)
			&&(((AmmunitionWeapon)w).ammunitionType().length()>0)
			&&((mob.fetchAbility(ID())==null)||proficiencyCheck(null,0,false)))
			{
				if((CMLib.dice().rollPercentage()<10)&&(mob.isInCombat()) && (mob.rangeToTarget() > 0))
					helpProficiency(mob, 0);
				if(!qualifiedWeapons.contains(w))
				{
					qualifiedWeapons.add((Weapon)w);
					Ability A=(Ability)this.copyOf();
					A.setSavable(false);
					A.setInvoker(invoker());
					w.addEffect(A);
					A.makeLongLasting();
					w.recoverPhyStats();
				}
			}
			for(int i=qualifiedWeapons.size()-1;i>=0;i--)
			{
				Item I=qualifiedWeapons.get(i);
				if((I.amWearingAt(Wearable.IN_INVENTORY))||(I.owner()!=affected))
				{
					qualifiedWeapons.remove(I);
					I.delEffect(I.fetchEffect(ID()));
					I.recoverPhyStats();
				}
			}
		}
		return true;
	}
}
