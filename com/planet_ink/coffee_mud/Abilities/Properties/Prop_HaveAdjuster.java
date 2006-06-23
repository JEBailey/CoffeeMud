package com.planet_ink.coffee_mud.Abilities.Properties;
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
public class Prop_HaveAdjuster extends Property
{
	public String ID() { return "Prop_HaveAdjuster"; }
	public String name(){ return "Adjustments to stats when owned";}
	protected int canAffectCode(){return Ability.CAN_ITEMS;}
	public boolean bubbleAffect(){return true;}
	protected CharStats adjCharStats=null;
    protected CharState adjCharState=null;
    protected EnvStats adjEnvStats=null;
    protected boolean gotClass=false;
    protected boolean gotRace=false;
    protected boolean gotSex=false;
    protected Vector mask=new Vector();
    
	public int setAdjustments(String newText, 
                                     EnvStats adjEnvStats, 
                                     CharStats adjCharStats, 
                                     CharState adjCharState,
                                     Vector mask)
	{
		boolean gotClass=false;
		boolean gotRace=false;
		boolean gotSex=false;
        
        newText=buildMask(newText,mask);
        
		adjEnvStats.setAbility(CMParms.getParmPlus(newText,"abi"));
		adjEnvStats.setArmor(CMParms.getParmPlus(newText,"arm"));
		adjEnvStats.setAttackAdjustment(CMParms.getParmPlus(newText,"att"));
		adjEnvStats.setDamage(CMParms.getParmPlus(newText,"dam"));
		adjEnvStats.setDisposition(CMParms.getParmPlus(newText,"dis"));
		adjEnvStats.setLevel(CMParms.getParmPlus(newText,"lev"));
		adjEnvStats.setRejuv(CMParms.getParmPlus(newText,"rej"));
		adjEnvStats.setSensesMask(CMParms.getParmPlus(newText,"sen"));
		adjEnvStats.setSpeed(CMParms.getParmDoublePlus(newText,"spe"));
		adjEnvStats.setWeight(CMParms.getParmPlus(newText,"wei"));
		adjEnvStats.setHeight(CMParms.getParmPlus(newText,"hei"));

		String val=CMParms.getParmStr(newText,"gen","").toUpperCase();
		if((val.length()>0)&&((val.charAt(0)=='M')||(val.charAt(0)=='F')||(val.charAt(0)=='N')))
		{
            adjCharStats.setStat(CharStats.STAT_GENDER,val.charAt(0));
			gotSex=true;
		}

		val=CMParms.getParmStr(newText,"cla","").toUpperCase();
		if((val.length()>0)&&(CMClass.findCharClass(val)!=null)&&(!val.equalsIgnoreCase("Archon")))
		{
			gotClass=true;
			adjCharStats.setCurrentClass(CMClass.findCharClass(val));
		}
		val=CMParms.getParmStr(newText,"rac","").toUpperCase();
		if((val.length()>0)&&(CMClass.getRace(val)!=null))
		{
			gotRace=true;
			adjCharStats.setMyRace(CMClass.getRace(val));
		}
		adjCharStats.setStat(CharStats.STAT_STRENGTH,CMParms.getParmPlus(newText,"str"));
		adjCharStats.setStat(CharStats.STAT_WISDOM,CMParms.getParmPlus(newText,"wis"));
		adjCharStats.setStat(CharStats.STAT_CHARISMA,CMParms.getParmPlus(newText,"cha"));
		adjCharStats.setStat(CharStats.STAT_CONSTITUTION,CMParms.getParmPlus(newText,"con"));
		adjCharStats.setStat(CharStats.STAT_DEXTERITY,CMParms.getParmPlus(newText,"dex"));
		adjCharStats.setStat(CharStats.STAT_INTELLIGENCE,CMParms.getParmPlus(newText,"int"));
		adjCharStats.setStat(CharStats.STAT_MAX_STRENGTH_ADJ,CMParms.getParmPlus(newText,"maxstr"));
		adjCharStats.setStat(CharStats.STAT_MAX_WISDOM_ADJ,CMParms.getParmPlus(newText,"maxwis"));
		adjCharStats.setStat(CharStats.STAT_MAX_CHARISMA_ADJ,CMParms.getParmPlus(newText,"maxcha"));
		adjCharStats.setStat(CharStats.STAT_MAX_CONSTITUTION_ADJ,CMParms.getParmPlus(newText,"maxcon"));
		adjCharStats.setStat(CharStats.STAT_MAX_DEXTERITY_ADJ,CMParms.getParmPlus(newText,"maxdex"));
		adjCharStats.setStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ,CMParms.getParmPlus(newText,"maxint"));

		adjCharState.setHitPoints(CMParms.getParmPlus(newText,"hit"));
		adjCharState.setHunger(CMParms.getParmPlus(newText,"hun"));
		adjCharState.setMana(CMParms.getParmPlus(newText,"man"));
		adjCharState.setMovement(CMParms.getParmPlus(newText,"mov"));
		adjCharState.setThirst(CMParms.getParmPlus(newText,"thi"));
		return ((gotClass?1:0)+(gotRace?2:0)+(gotSex?4:0));
	}

	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		this.adjCharStats=(CharStats)CMClass.getCommon("DefaultCharStats");
		this.adjCharState=(CharState)CMClass.getCommon("DefaultCharState");
		this.adjEnvStats=(EnvStats)CMClass.getCommon("DefaultEnvStats");
        this.mask=new Vector();
		int gotit=setAdjustments(newText,adjEnvStats,adjCharStats,adjCharState,mask);
		gotClass=((gotit&1)==1);
		gotRace=((gotit&2)==2);
		gotSex=((gotit&4)==4);
	}

	public void envStuff(EnvStats affectableStats, EnvStats adjEnvStats)
	{
		affectableStats.setAbility(affectableStats.ability()+adjEnvStats.ability());
		affectableStats.setArmor(affectableStats.armor()+adjEnvStats.armor());
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+adjEnvStats.attackAdjustment());
		affectableStats.setDamage(affectableStats.damage()+adjEnvStats.damage());
		affectableStats.setDisposition(affectableStats.disposition()|adjEnvStats.disposition());
		affectableStats.setLevel(affectableStats.level()+adjEnvStats.level());
		affectableStats.setRejuv(affectableStats.rejuv()+adjEnvStats.rejuv());
		affectableStats.setSensesMask(affectableStats.sensesMask()|adjEnvStats.sensesMask());
		affectableStats.setSpeed(affectableStats.speed()+adjEnvStats.speed());
		affectableStats.setWeight(affectableStats.weight()+adjEnvStats.weight());
		affectableStats.setHeight(affectableStats.height()+adjEnvStats.height());
	}

    public boolean canApply(MOB mob)
    {
        if((affected!=null)
        &&(affected instanceof Item)
        &&(!((Item)affected).amDestroyed())
        &&((mask.size()==0)||(CMLib.masking().maskCheck(mask,mob))))
            return true;
        return false;
    }
    
    public boolean canApply(Environmental E)
    {
        if(E instanceof MOB)
            return canApply((MOB)E);
        return false;
    }
    
    protected void ensureStarted()
	{
		if(adjCharStats==null)
			setMiscText(text());
	}

	public void affectEnvStats(Environmental host, EnvStats affectableStats)
	{
		ensureStarted();
        if(canApply(host))
			envStuff(affectableStats,adjEnvStats);
		super.affectEnvStats(host,affectableStats);
	}

	public void adjCharStats(CharStats affectedStats,
									boolean gotClass,
									boolean gotRace,
									boolean gotSex,
									CharStats adjCharStats)
	{
		affectedStats.setStat(CharStats.STAT_CHARISMA,affectedStats.getStat(CharStats.STAT_CHARISMA)+adjCharStats.getStat(CharStats.STAT_CHARISMA));
		affectedStats.setStat(CharStats.STAT_CONSTITUTION,affectedStats.getStat(CharStats.STAT_CONSTITUTION)+adjCharStats.getStat(CharStats.STAT_CONSTITUTION));
		affectedStats.setStat(CharStats.STAT_DEXTERITY,affectedStats.getStat(CharStats.STAT_DEXTERITY)+adjCharStats.getStat(CharStats.STAT_DEXTERITY));
		if(gotSex)
			affectedStats.setStat(CharStats.STAT_GENDER,adjCharStats.getStat(CharStats.STAT_GENDER));
		affectedStats.setStat(CharStats.STAT_INTELLIGENCE,affectedStats.getStat(CharStats.STAT_INTELLIGENCE)+adjCharStats.getStat(CharStats.STAT_INTELLIGENCE));
		if(gotClass)
			affectedStats.setCurrentClass(adjCharStats.getCurrentClass());
		if(gotRace)
			affectedStats.setMyRace(adjCharStats.getMyRace());
		affectedStats.setStat(CharStats.STAT_STRENGTH,affectedStats.getStat(CharStats.STAT_STRENGTH)+adjCharStats.getStat(CharStats.STAT_STRENGTH));
		affectedStats.setStat(CharStats.STAT_WISDOM,affectedStats.getStat(CharStats.STAT_WISDOM)+adjCharStats.getStat(CharStats.STAT_WISDOM));
	}

	public void adjCharState(CharState affectedState,
									CharState adjCharState)
	{
		affectedState.setHitPoints(affectedState.getHitPoints()+adjCharState.getHitPoints());
		affectedState.setHunger(affectedState.getHunger()+adjCharState.getHunger());
		affectedState.setMana(affectedState.getMana()+adjCharState.getMana());
		affectedState.setMovement(affectedState.getMovement()+adjCharState.getMovement());
		affectedState.setThirst(affectedState.getThirst()+adjCharState.getThirst());
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectedStats)
	{
		ensureStarted();
        if(canApply(affectedMOB))
    		adjCharStats(affectedStats,gotClass,gotRace,gotSex,adjCharStats);
		super.affectCharStats(affectedMOB,affectedStats);
	}
	public void affectCharState(MOB affectedMOB, CharState affectedState)
	{
		ensureStarted();
        if(canApply(affectedMOB))
    		adjCharState(affectedState,adjCharState);
		super.affectCharState(affectedMOB,affectedState);
	}

	public String fixAccoutingsWithMask(String id)
	{
        String[] strs=separateMask(id);
        id=strs[0];
		int x=id.toUpperCase().indexOf("ARM");
		for(StringBuffer ID=new StringBuffer(id);((x>0)&&(x<id.length()));x++)
			if(id.charAt(x)=='-')
			{
				ID.setCharAt(x,'+');
				id=ID.toString();
				break;
			}
			else
			if(id.charAt(x)=='+')
			{
				ID.setCharAt(x,'-');
				id=ID.toString();
				break;
			}
			else
			if(Character.isDigit(id.charAt(x)))
				break;
		x=id.toUpperCase().indexOf("DIS");
		if(x>=0)
		{
			long val=CMParms.getParmPlus(id,"dis");
			int y=id.indexOf(""+val,x);
			if((val!=0)&&(y>x))
			{
				StringBuffer middle=new StringBuffer("");
				for(int num=0;num<EnvStats.dispositionsVerb.length;num++)
					if(CMath.bset(val,CMath.pow(2,num)))
						middle.append(EnvStats.dispositionsVerb[num]+" ");
				id=id.substring(0,x)+middle.toString().trim()+id.substring(y+((""+val).length()));
			}
		}
		x=id.toUpperCase().indexOf("SEN");
		if(x>=0)
		{
			long val=CMParms.getParmPlus(id,"sen");
			int y=id.indexOf(""+val,x);
			if((val!=0)&&(y>x))
			{
				StringBuffer middle=new StringBuffer("");
				for(int num=0;num<EnvStats.sensesVerb.length;num++)
					if(CMath.bset(val,CMath.pow(2,num)))
						middle.append(EnvStats.sensesVerb[num]+" ");
				id=id.substring(0,x)+middle.toString().trim()+id.substring(y+((""+val).length()));
			}
		}
        if(strs[1].length()>0)
            id+="  Restrictions: "+CMLib.masking().maskDesc(strs[1]);
		return id;
	}

	public String accountForYourself()
	{
		return fixAccoutingsWithMask("Affects the owner: "+text());
	}
}
