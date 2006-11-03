package com.planet_ink.coffee_mud.Abilities.Songs;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
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
public class Dance extends StdAbility
{
	public String ID() { return "Dance"; }
	public String name(){ return "a Dance";}
	public String displayText(){ return "("+danceOf()+")";}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return CAN_MOBS;}
	private static final String[] triggerStrings = {"DANCE","DA"};
	public String[] triggerStrings(){return triggerStrings;}
	public int classificationCode(){return Ability.ACODE_SONG|Ability.DOMAIN_DANCING;}
	public int usageType(){return USAGE_MOVEMENT;}
	public int maxRange(){return 2+(10*super.getExpertiseLevel(invoker(),"LONGDANCE"));}
	protected int invokerManaCost=-1;
    protected int steadyDown=-1;

	protected boolean skipStandardDanceInvoke(){return false;}
	protected boolean mindAttack(){return abstractQuality()==Ability.QUALITY_MALICIOUS;}
	protected boolean skipStandardDanceTick(){return false;}
	protected String danceOf(){return name();}

    protected boolean HAS_QUANTITATIVE_ASPECT(){return true;}
    
    private static final int EXPERTISE_STAGES=10;
    private static final String[] EXPERTISE={"FASTDANCE","SLOWDANCE","BEATDANCE","LONGDANCE","STEADYDANCE"};
    private static final int[] EXPERTISE_SET_NADA={3,4};
    private static final int[] EXPERTISE_SET_MALICIOUS={0,3,4};
    private static final int[] EXPERTISE_SET_BENEFICIAL_SELF={1,3,4};
    private static final int[] EXPERTISE_SET_BENEFICIAL_OTHERS={2,3,4};
    private static String[] EXPERTISE_NAMES_NADA=null;
    private static String[] EXPERTISE_NAMES_MALICIOUS=null;
    private static String[] EXPERTISE_NAMES_BENEFICIAL_SELF=null;
    private static String[] EXPERTISE_NAMES_BENEFICIAL_OTHERS=null;
    private static final String[] EXPERTISE_NAME={"Fast Dancing","Slow Dancing","Beat Dancing","Distant Dancing","Steady Dancing"};
    private static final String[][] EXPERTISE_STATS={{"STR","CHA"},
                                                     {"STR","CHA"},
                                                     {"STR","CHA"},
                                                     {"STR","CHA"}
    };
    private static final int[] EXPERTISE_LEVELS={14,16,17,18,19};
    public void initializeClass()
    {
        super.initializeClass();
        if(!ID().equals("Dance"))
        {
            int[] MY_INDEX=get_EXPERTISE_SET();
            for(int i=0;i<MY_INDEX.length;i++)
            {
                int e=MY_INDEX[i];
                if(CMLib.expertises().getDefinition(EXPERTISE[e]+EXPERTISE_STAGES)==null)
                    for(int s=1;s<=EXPERTISE_STAGES;s++)
                        CMLib.expertises().addDefinition(EXPERTISE[e]+i,EXPERTISE_NAME[e]+" "+CMath.convertToRoman(i),
                                ((i==1)?"":"-EXPERTISE \"+"+EXPERTISE[e]+(i-1)+"\""),
                                    " +"+EXPERTISE_STATS[e][0]+" "+(16+i)
                                   +((EXPERTISE_STATS[e][1].length()>0)?" +"+EXPERTISE_STATS[e][1]+" "+(16+i):"")
                                   +" -LEVEL +>="+(EXPERTISE_LEVELS[e]+(5*i))
                                   ,0,1,0,0,0);
            }
            super.registerExpertiseUsage(get_EXPERTISE_NAMES(),EXPERTISE_STAGES,false,null);
        }
    }
    
    protected int[] get_EXPERTISE_SET(){
        if(!HAS_QUANTITATIVE_ASPECT())
            return EXPERTISE_SET_NADA;
        switch(super.abstractQuality())
        {
        case Ability.QUALITY_MALICIOUS:
            return EXPERTISE_SET_MALICIOUS;     
        case Ability.QUALITY_BENEFICIAL_SELF:
            return EXPERTISE_SET_BENEFICIAL_SELF;     
        default:
            return EXPERTISE_SET_BENEFICIAL_OTHERS;     
        }
    }
    protected String[] get_EXPERTISE_NAMES(){
        String[] MINE=null;
        int[] MY_SET=get_EXPERTISE_SET();
        if(!HAS_QUANTITATIVE_ASPECT())
        {
            if(EXPERTISE_NAMES_NADA==null) EXPERTISE_NAMES_NADA=new String[MY_SET.length];
            MINE=EXPERTISE_NAMES_NADA;
        }
        else
        switch(super.abstractQuality())
        {
        case Ability.QUALITY_MALICIOUS:
            if(EXPERTISE_NAMES_MALICIOUS==null) EXPERTISE_NAMES_MALICIOUS=new String[MY_SET.length];
            MINE=EXPERTISE_NAMES_MALICIOUS;
            break;
        case Ability.QUALITY_BENEFICIAL_SELF:
            if(EXPERTISE_NAMES_BENEFICIAL_SELF==null) EXPERTISE_NAMES_BENEFICIAL_SELF=new String[MY_SET.length];
            MINE=EXPERTISE_NAMES_BENEFICIAL_SELF;
            break;
        default:
            if(EXPERTISE_NAMES_BENEFICIAL_OTHERS==null) EXPERTISE_NAMES_BENEFICIAL_OTHERS=new String[MY_SET.length];
            MINE=EXPERTISE_NAMES_BENEFICIAL_OTHERS;
            break;
        }
        if(MINE[0]!=null) return MINE;
        for(int i=0;i<MY_SET.length;i++)
            MINE[i]=EXPERTISE[MY_SET[i]];
        return MINE;
    }
    
    protected int getXLevel(MOB mob){
        switch(super.abstractQuality())
        {
        case Ability.QUALITY_MALICIOUS:
            return getExpertiseLevel(mob,EXPERTISE[0]);     
        case Ability.QUALITY_BENEFICIAL_SELF:
            return getExpertiseLevel(mob,EXPERTISE[1]);     
        default:
            return getExpertiseLevel(mob,EXPERTISE[2]);     
        }
    }
    
	public int prancerQClassLevel()
	{
		if(invoker()==null) return CMLib.ableMapper().lowestQualifyingLevel(ID());
		int x=CMLib.ableMapper().qualifyingClassLevel(invoker(),this);
		if(x<=0) x=CMLib.ableMapper().lowestQualifyingLevel(ID());
		int charisma=(invoker().charStats().getStat(CharStats.STAT_CHARISMA)-10);
		if(charisma>0)
			return x+(charisma/3)+(getXLevel(invoker())*2);
		return x+(getXLevel(invoker())*2);
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		if(skipStandardDanceTick())
			return true;

		if(affected==null) return false;
		if(affected instanceof MOB)
		{
			MOB mob=(MOB)affected;
			if((invoker==null)
			||(invoker.location()!=mob.location())
			||(CMLib.flags().isSitting(invoker()))
			||(!CMLib.flags().aliveAwakeMobile(mob,true))
			||(!CMLib.flags().aliveAwakeMobile(invoker(),true))
			||(invoker.fetchEffect(ID())==null)
			||(!CMLib.flags().canBeSeenBy(invoker,mob)))
			{
                if(steadyDown<0) steadyDown=(invoker()!=null)?super.getExpertiseLevel(invoker(),"STEADYDANCE"):0;
                if(steadyDown==0)
                {
    				undance(mob,null,false);
    				return false;
                }
                steadyDown--;
                return true;
			}
			if(invokerManaCost<0) invokerManaCost=usageCost(invoker())[1];
			if(!mob.curState().adjMovement(-(invokerManaCost/15),mob.maxState()))
			{
				mob.tell("The dancing exhausts you.");
				undance(mob,null,false);
				return false;
			}
		}
		return true;
	}

	protected void undance(MOB mob, MOB invoker, boolean notMe)
	{
		if(mob==null) return;
		for(int a=mob.numEffects()-1;a>=0;a--)
		{
			Ability A=mob.fetchEffect(a);
			if((A!=null)
			&&(A instanceof Dance)
			&&((!notMe)||(!A.ID().equals(ID())))
			&&((invoker==null)||(A.invoker()==null)||(A.invoker()==invoker)))
            {
                if((!(A instanceof Dance))||(((Dance)A).steadyDown<=0))
    				A.unInvoke();
            }
		}
	}

	public void executeMsg(Environmental host, CMMsg msg)
	{
		if((invoker()!=null)
		&&(!unInvoked)
		&&(affected==invoker())
		&&(msg.amISource(invoker()))
		&&(msg.target() instanceof Armor)
		&&(msg.targetMinor()==CMMsg.TYP_WEAR))
			unInvoke();
		super.executeMsg(host,msg);
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
        steadyDown=-1;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if((!auto)
		&&(!mob.isMonster())
		&&(!disregardsArmorCheck(mob))
		&&(!CMLib.utensils().armorCheck(mob,CharClass.ARMOR_LEATHER))
		&&(mob.isMine(this))
		&&(mob.location()!=null)
		&&(CMLib.dice().rollPercentage()<50))
		{
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,"<S-NAME> fumble(s) the "+name()+" due to <S-HIS-HER> armor!");
			return false;
		}

		if(skipStandardDanceInvoke())
			return true;

		if((!auto)&&(!CMLib.flags().aliveAwakeMobile(mob,false)))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		undance(mob,null,true);

		if(success)
		{
			String str=auto?"^SThe "+danceOf()+" begins!^?":"^S<S-NAME> begin(s) to dance the "+danceOf()+".^?";
			if((!auto)&&(mob.fetchEffect(this.ID())!=null))
				str="^S<S-NAME> start(s) the "+danceOf()+" over again.^?";

			CMMsg msg=CMClass.getMsg(mob,null,this,somanticCastCode(mob,null,auto),str);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;
				Dance newOne=(Dance)this.copyOf();
				newOne.invoker=mob;
				newOne.invokerManaCost=-1;

				HashSet h=properTargets(mob,givenTarget,auto);
				if(h==null) return false;
				if(!h.contains(mob)) h.add(mob);

				Room R=mob.location();
				for(Iterator f=h.iterator();f.hasNext();)
				{
					MOB follower=(MOB)f.next();
					Room R2=follower.location();

					// malicious dances must not affect the invoker!
					int affectType=CMMsg.MSG_CAST_SOMANTIC_SPELL;
					if((castingQuality(mob,follower)==Ability.QUALITY_MALICIOUS)&&(follower!=mob))
						affectType=affectType|CMMsg.MASK_MALICIOUS;
					if(auto) affectType=affectType|CMMsg.MASK_ALWAYS;

					if((R!=null)&&(R2!=null)&&(CMLib.flags().canBeSeenBy(invoker,follower)&&(follower.fetchEffect(this.ID())==null)))
					{
						CMMsg msg2=CMClass.getMsg(mob,follower,this,affectType,null);
						CMMsg msg3=msg2;
						if((mindAttack())&&(follower!=mob))
							msg2=CMClass.getMsg(mob,follower,this,CMMsg.MSK_CAST_MALICIOUS_SOMANTIC|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
						if((R.okMessage(mob,msg2))&&(R.okMessage(mob,msg3)))
						{
							R2.send(follower,msg2);
							if(msg2.value()<=0)
							{
								R2.send(follower,msg3);
								if((msg3.value()<=0)&&(follower.fetchEffect(newOne.ID())==null))
								{
									undance(follower,null,false);
									newOne.setSavable(false);
									if(follower!=mob)
										follower.addEffect((Ability)newOne.copyOf());
									else
										follower.addEffect(newOne);
								}
							}
						}
					}
				}
				R.recoverRoomStats();
			}
		}
		else
			mob.location().show(mob,null,CMMsg.MSG_NOISE,"<S-NAME> make(s) a false step.");

		return success;
	}
}
