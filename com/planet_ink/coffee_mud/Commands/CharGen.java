 package com.planet_ink.coffee_mud.Commands;
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
   Copyright 2000-2008 Bo Zimmerman

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
public class CharGen extends StdCommand
{
	public CharGen(){}

	private String[] access={"CHARGEN"};
	public String[] getAccessWords(){return access;}

	protected void equipPlayer(MOB M)
	{
		int level = M.baseEnvStats().level();
		CharClass C=M.baseCharStats().getCurrentClass();
		for(int pos=0;pos<Item.WORN_CODES.length;pos++)
		{
			long wornCode=Item.WORN_CODES[pos];
			if(wornCode == Item.IN_INVENTORY) continue;
			if(wornCode == Item.WORN_HELD) continue;
			if(wornCode==Item.WORN_WIELD)
			{
				Weapon W=CMClass.getWeapon("GenWeapon");
				W.setWeaponClassification(Weapon.CLASS_SWORD);
				W.setWeaponType(Weapon.TYPE_SLASHING);
				W.setMaterial(RawMaterial.RESOURCE_STEEL);
				W.setRawProperLocationBitmap(Item.WORN_WIELD|Item.WORN_HELD);
				W.setRawLogicalAnd(true);
				switch(C.allowedWeaponLevel())
				{
				case CharClass.WEAPONS_THIEFLIKE:
				case CharClass.WEAPONS_BURGLAR:
				case CharClass.WEAPONS_ANY:
				case CharClass.WEAPONS_EVILCLERIC:
				case CharClass.WEAPONS_NEUTRALCLERIC:
					break;
				case CharClass.WEAPONS_DAGGERONLY:
					W.setWeaponClassification(Weapon.CLASS_DAGGER);
					W.setWeaponType(Weapon.TYPE_PIERCING);
					break;
				case CharClass.WEAPONS_NATURAL:
					W.setMaterial(RawMaterial.RESOURCE_OAK);
					break;
				case CharClass.WEAPONS_ROCKY:
					W.setMaterial(RawMaterial.RESOURCE_STONE);
					break;
				case CharClass.WEAPONS_GOODCLERIC:
				case CharClass.WEAPONS_MAGELIKE:
					W.setMaterial(RawMaterial.RESOURCE_OAK);
					W.setWeaponClassification(Weapon.CLASS_STAFF);
					W.setWeaponType(Weapon.TYPE_BASHING);
					break;
				case CharClass.WEAPONS_ALLCLERIC:
					if(CMLib.flags().isGood(M))
					{
						W.setMaterial(RawMaterial.RESOURCE_OAK);
						W.setWeaponClassification(Weapon.CLASS_STAFF);
						W.setWeaponType(Weapon.TYPE_BASHING);
					}
					break;
				case CharClass.WEAPONS_FLAILONLY:
					W.setWeaponClassification(Weapon.CLASS_FLAILED);
					W.setWeaponType(Weapon.TYPE_BASHING);
					break;
				}
				W.baseEnvStats().setLevel(level);
				W.baseEnvStats().setWeight(8);
				W.recoverEnvStats();
				CMLib.itemBuilder().balanceItemByLevel(W);
				M.addInventory(W);
				W.wearIfPossible(M);
			}
			else
			if(wornCode != Item.WORN_FLOATING_NEARBY)
			{
				Armor A=CMClass.getArmor("GenArmor");
				A.setRawProperLocationBitmap(wornCode);
				A.setMaterial(RawMaterial.RESOURCE_STEEL);
				if((CharClass.ARMOR_WEARMASK & wornCode) > 0)
				switch(C.allowedArmorLevel())
				{
				case CharClass.ARMOR_ANY:
				case CharClass.ARMOR_METALONLY:
					break;
				case CharClass.ARMOR_CLOTH:
					A.setMaterial(RawMaterial.RESOURCE_COTTON);
					break;
				case CharClass.ARMOR_LEATHER:
					A.setMaterial(RawMaterial.RESOURCE_LEATHER);
					break;
				case CharClass.ARMOR_NONMETAL:
				case CharClass.ARMOR_VEGAN:
				case CharClass.ARMOR_OREONLY:
					A.setMaterial(RawMaterial.RESOURCE_OAK);
					break;
				}
				A.baseEnvStats().setLevel(level);
				A.baseEnvStats().setWeight(8);
				A.recoverEnvStats();
				CMLib.itemBuilder().balanceItemByLevel(A);
				M.addInventory(A);
				A.wearIfPossible(M);
			}
		}
	}
	
	
	protected MOB levelMOBup(int level, CharClass C)
	{
		MOB mob=CMClass.getMOB("StdMOB");
		CMLib.factions().setAlignment(mob,Faction.ALIGN_NEUTRAL);
		mob.setName("Average Joe");
		mob.baseCharStats().setMyRace(CMClass.getRace("Human"));
		mob.baseCharStats().setStat(CharStats.STAT_GENDER,'M');
		mob.baseCharStats().setStat(CharStats.STAT_STRENGTH,11);
		mob.baseCharStats().setStat(CharStats.STAT_WISDOM,11);
		mob.baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,10);
		mob.baseCharStats().setStat(CharStats.STAT_DEXTERITY,11);
		mob.baseCharStats().setStat(CharStats.STAT_CONSTITUTION,10);
		mob.baseCharStats().setStat(CharStats.STAT_CHARISMA,10);
		mob.baseCharStats().setCurrentClass(C);
		mob.baseCharStats().setClassLevel(C,1);
		mob.baseEnvStats().setArmor(100);
		mob.baseEnvStats().setLevel(1);
		mob.baseEnvStats().setSensesMask(0);
		mob.baseState().setHitPoints(20);
		mob.baseState().setMovement(100);
		mob.baseState().setMana(100);
		mob.baseCharStats().getMyRace().startRacing(mob,false);
		mob.recoverCharStats();
		mob.recoverEnvStats();
		mob.recoverMaxState();
		mob.baseCharStats().getCurrentClass().startCharacter(mob,false,false);

		int max=CMProps.getIntVar(CMProps.SYSTEMI_BASEMAXSTAT);
		for(int lvl=1;lvl<level;lvl++)
		{
			switch(lvl % 6)
			{
			case 0:
				if(mob.baseCharStats().getStat(CharStats.STAT_STRENGTH)<(max+mob.baseCharStats().getStat(CharStats.STAT_MAX_STRENGTH_ADJ)+5))
					mob.baseCharStats().setStat(CharStats.STAT_STRENGTH,mob.baseCharStats().getStat(CharStats.STAT_STRENGTH)+1);
				break;
			case 1:
				if(mob.baseCharStats().getStat(CharStats.STAT_DEXTERITY)<(max+mob.baseCharStats().getStat(CharStats.STAT_MAX_DEXTERITY_ADJ)+5))
					mob.baseCharStats().setStat(CharStats.STAT_DEXTERITY,mob.baseCharStats().getStat(CharStats.STAT_DEXTERITY)+1);
				break;
			case 2:
				if(mob.baseCharStats().getStat(CharStats.STAT_INTELLIGENCE)<(max+mob.baseCharStats().getStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ)))
					mob.baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,mob.baseCharStats().getStat(CharStats.STAT_INTELLIGENCE)+1);
				break;
			case 3:
				if(mob.baseCharStats().getStat(CharStats.STAT_CONSTITUTION)<(max+mob.baseCharStats().getStat(CharStats.STAT_MAX_CONSTITUTION_ADJ)))
					mob.baseCharStats().setStat(CharStats.STAT_CONSTITUTION,mob.baseCharStats().getStat(CharStats.STAT_CONSTITUTION)+1);
				break;
			case 4:
				if(mob.baseCharStats().getStat(CharStats.STAT_CHARISMA)<(max+mob.baseCharStats().getStat(CharStats.STAT_MAX_CHARISMA_ADJ)))
					mob.baseCharStats().setStat(CharStats.STAT_CHARISMA,mob.baseCharStats().getStat(CharStats.STAT_CHARISMA)+1);
				break;
			case 5:
				if(mob.baseCharStats().getStat(CharStats.STAT_WISDOM)<(max+mob.baseCharStats().getStat(CharStats.STAT_MAX_WISDOM_ADJ)))
					mob.baseCharStats().setStat(CharStats.STAT_WISDOM,mob.baseCharStats().getStat(CharStats.STAT_WISDOM)+1);
				break;
			}
			if(mob.getExpNeededLevel()==Integer.MAX_VALUE)
				CMLib.leveler().level(mob);
			else
				CMLib.leveler().postExperience(mob,null,null,mob.getExpNeededLevel()+1,false);
			mob.recoverEnvStats();
			mob.recoverCharStats();
			mob.recoverMaxState();
		}
        equipPlayer(mob);
		mob.recoverCharStats();
		mob.recoverEnvStats();
		mob.recoverMaxState();
		mob.resetToMaxState();
		return mob;
	}

	public void averageout(MOB avgMob, int tries)
	{
		avgMob.baseCharStats().setStat(CharStats.STAT_STRENGTH,(int)Math.round(CMath.div(avgMob.baseCharStats().getStat(CharStats.STAT_STRENGTH),tries)));
		avgMob.baseCharStats().setStat(CharStats.STAT_WISDOM,(int)Math.round(CMath.div(avgMob.baseCharStats().getStat(CharStats.STAT_WISDOM),tries)));
		avgMob.baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,(int)Math.round(CMath.div(avgMob.baseCharStats().getStat(CharStats.STAT_INTELLIGENCE),tries)));
		avgMob.baseCharStats().setStat(CharStats.STAT_DEXTERITY,(int)Math.round(CMath.div(avgMob.baseCharStats().getStat(CharStats.STAT_DEXTERITY),tries)));
		avgMob.baseCharStats().setStat(CharStats.STAT_CONSTITUTION,(int)Math.round(CMath.div(avgMob.baseCharStats().getStat(CharStats.STAT_CONSTITUTION),tries)));
		avgMob.baseCharStats().setStat(CharStats.STAT_CHARISMA,(int)Math.round(CMath.div(avgMob.baseCharStats().getStat(CharStats.STAT_CHARISMA),tries)));
		avgMob.baseEnvStats().setArmor((int)Math.round(CMath.div(avgMob.baseEnvStats().armor(),tries)));
		avgMob.baseState().setHitPoints((int)Math.round(CMath.div(avgMob.baseState().getHitPoints(),tries)));
		avgMob.baseState().setMovement((int)Math.round(CMath.div(avgMob.baseState().getMovement(),tries)));
		avgMob.baseState().setMana((int)Math.round(CMath.div(avgMob.baseState().getMana(),tries)));
		avgMob.recoverCharStats();
		avgMob.recoverEnvStats();
		avgMob.recoverMaxState();
		avgMob.resetToMaxState();
		avgMob.setTrains(0);
	}

	public void addHimIn(MOB avgMob, MOB mob2)
	{
		avgMob.baseCharStats().setStat(CharStats.STAT_STRENGTH,avgMob.baseCharStats().getStat(CharStats.STAT_STRENGTH)+mob2.baseCharStats().getStat(CharStats.STAT_STRENGTH));
		avgMob.baseCharStats().setStat(CharStats.STAT_WISDOM,avgMob.baseCharStats().getStat(CharStats.STAT_WISDOM)+mob2.baseCharStats().getStat(CharStats.STAT_WISDOM));
		avgMob.baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,avgMob.baseCharStats().getStat(CharStats.STAT_INTELLIGENCE)+mob2.baseCharStats().getStat(CharStats.STAT_INTELLIGENCE));
		avgMob.baseCharStats().setStat(CharStats.STAT_DEXTERITY,avgMob.baseCharStats().getStat(CharStats.STAT_DEXTERITY)+mob2.baseCharStats().getStat(CharStats.STAT_DEXTERITY));
		avgMob.baseCharStats().setStat(CharStats.STAT_CONSTITUTION,avgMob.baseCharStats().getStat(CharStats.STAT_CONSTITUTION)+mob2.baseCharStats().getStat(CharStats.STAT_CONSTITUTION));
		avgMob.baseCharStats().setStat(CharStats.STAT_CHARISMA,avgMob.baseCharStats().getStat(CharStats.STAT_CHARISMA)+mob2.baseCharStats().getStat(CharStats.STAT_CHARISMA));
		avgMob.baseEnvStats().setArmor(avgMob.baseEnvStats().armor()+mob2.baseEnvStats().armor());
		avgMob.baseState().setHitPoints(avgMob.baseState().getHitPoints()+mob2.baseState().getHitPoints());
		avgMob.baseState().setMovement(avgMob.baseState().getMovement()+mob2.baseState().getMovement());
		avgMob.baseState().setMana(avgMob.baseState().getMana()+mob2.baseState().getMana());
		avgMob.recoverCharStats();
		avgMob.recoverEnvStats();
		avgMob.recoverMaxState();
		avgMob.resetToMaxState();
	}

	public MOB AverageClassMOB(MOB mob, int level, CharClass C, int numTries)
	{
		MOB avgMob=levelMOBup(level,C);
		int tries=0;
		for(;tries<numTries;tries++)
		{
			if((tries % 20)==0)
				mob.session().print(".");
			MOB mob2=levelMOBup(level,C);
			addHimIn(avgMob,mob2);
		}
		averageout(avgMob,tries);
		return avgMob;
	}

	public MOB AverageAllClassMOB(MOB mob, int level, int numTriesClass, int numTriesMOB)
	{
		MOB avgMob=null;
		int tries=0;
		int numClasses=0;
		for(;tries<numTriesClass;tries++)
		{
			for(Enumeration c=CMClass.charClasses();c.hasMoreElements();)
			{
				CharClass C=(CharClass)c.nextElement();
				if(C.availabilityCode()!=0)
				{
					numClasses++;
					MOB mob2=AverageClassMOB(mob,level,C,numTriesMOB);
					if(avgMob==null)
					{
						avgMob=mob2;
						numClasses--;
					}
					else
						addHimIn(avgMob,mob2);
                    if(avgMob!=mob2) mob2.destroy();
				}
			}
		}
		averageout(avgMob,numClasses);
		return avgMob;
	}

	public void combatRun(MOB mob, Vector commands) {
	    int levelStart=1;
	    int levelEnd=91;
	    if(commands.size()==0)
	    {
	        mob.tell("USAGE: CHARGEN COMBAT ([CHARCLASS(S)]...) (FAILCHECK) (ITERATIONS=[X])([START LEVEL]) ([END LEVEL])");
	        return;
	    }
	    Hashtable failSkillCheck=null;
	    String[][] CAMATCH={
	            {"Commoner","CombatAbilities"},
	            {"Bard","Bardness"},
                {"Cleric","Clericness"},
                {"Druid","Druidness"},
                {"Mage","Mageness"},
                {"Thief","Thiefness"},
                {"Fighter","Fighterness"},
	    };
	    int TOTAL_ITERATIONS=1000;
	    
	    DVector classSet=new DVector(2);
	    for(Enumeration e=CMClass.charClasses();e.hasMoreElements();)
	    {
	        CharClass C=(CharClass)e.nextElement();
	        if((CMath.bset(C.availabilityCode(),Area.THEME_FANTASY)
                ||CMath.bset(C.availabilityCode(),Area.THEME_HEROIC)
                ||CMath.bset(C.availabilityCode(),Area.THEME_TECHNOLOGY))
	        &&(!CMath.bset(C.availabilityCode(),Area.THEME_SKILLONLYMASK)))
	        {
	            String behav="CombatAbilities";
	            for(int c=0;c<CAMATCH.length;c++)
	                if(C.baseClass().equalsIgnoreCase(CAMATCH[c][0]))
	                    behav=CAMATCH[c][1];
	            classSet.addElement(C,behav);
	        }
	    }
	    
	    // set the parameters
	    boolean classCleared=false;
	    boolean nextLevel=false;
	    for(int i=0;i<commands.size();i++)
	    {
	        String s=(String)commands.elementAt(i);
	        if(CMath.isInteger(s))
	        {
	            int x=CMath.s_int(s);
	            if(x>=0)
	            {
	                if((nextLevel)&&(x>=levelStart))
	                    levelEnd=x;
	                else
	                if(!nextLevel)
	                {
	                    levelStart=x;
	                    levelEnd=x;
	                    nextLevel=true;
	                }
	            }
	        }
	        else
            if(s.toUpperCase().startsWith("ITERATIONS="))
            {
                s=s.substring("ITERATIONS=".length());
                if(CMath.isInteger(s))
                    TOTAL_ITERATIONS=CMath.s_int(s);
            }
            else
	        if(CMClass.findCharClass(s)!=null)
	        {
	            CharClass C=CMClass.findCharClass(s);
	            if(!classCleared)
	            {
	                classCleared=true;
	                classSet=new DVector(2);
	            }
	            String behav="CombatAbilities";
                for(int c=0;c<CAMATCH.length;c++)
                    if(C.baseClass().equalsIgnoreCase(CAMATCH[c][0]))
                        behav=CAMATCH[c][1];
                classSet.addElement(C,behav);
	        }
            else
            if(s.endsWith("s"))
            {
                s=s.substring(0,s.length()-1);
                for(Enumeration e=CMClass.charClasses();e.hasMoreElements();)
                {
                    CharClass C=(CharClass)e.nextElement();
                    if((CMath.bset(C.availabilityCode(),Area.THEME_FANTASY)
                        ||CMath.bset(C.availabilityCode(),Area.THEME_HEROIC)
                        ||CMath.bset(C.availabilityCode(),Area.THEME_TECHNOLOGY))
                    &&(!CMath.bset(C.availabilityCode(),Area.THEME_SKILLONLYMASK))
                    &&(C.baseClass().equalsIgnoreCase(s)))
                    {
                        if(!classCleared)
                        {
                            classCleared=true;
                            classSet=new DVector(2);
                        }
                        String behav="CombatAbilities";
                        for(int c=0;c<CAMATCH.length;c++)
                            if(C.baseClass().equalsIgnoreCase(CAMATCH[c][0]))
                                behav=CAMATCH[c][1];
                        classSet.addElement(C,behav);
                    }
                }
            }
	        else
	        if(s.equalsIgnoreCase("FAILCHECK"))
	            failSkillCheck=new Hashtable();
	    }
	    
        Area A=CMClass.getAreaType("StdArea");
        A.setName("UNKNOWNAREA");
        CMLib.map().addArea(A);
        
        /*final int DATUM_CLASS=0; 
        final int DATUM_LEVEL=1;
        final int DATUM_BESTSINGLEHITSCORE=2;
        final int DATUM_BESTSINGLEHITSKILL=3;
        final int DATUM_BESTHITSCORE=4;
        final int DATUM_BESTHITSKILLS=5;
        final int DATUM_BESTITERSCORE=6;
        final int DATUM_BESTITERSKILLS=7;
        final int DATUM_LOSSES=8;
        
        DVector datum=new DVector(9);*/
        
        for(int charClassDex=0;charClassDex<classSet.size();charClassDex++)
        {
            CharClass C=(CharClass)classSet.elementAt(charClassDex,1);
    	    for(int level=levelStart;level<=levelEnd;level++)
    	    {
                mob.tell(C.ID()+": "+level);
                int roomRobin=0;
                Room R=null;
                
                int[] bestSingleHitScore=new int[]{0};
                String[] bestSingleHitSkill=new String[]{""};
                int[] bestHitScore=new int[]{0};
                String[] bestHitSkill=new String[]{""};
                int[] bestIterScore=new int[]{Integer.MAX_VALUE};
                String[] bestIterSkill=new String[]{""};
                
                int[] closeIterScore=new int[]{0};
                String[] closeIterSkill=new String[]{""};
                
                int[] losses=new int[]{0};
                long[] avgHits=new long[]{0};
                int[] avgIters=new int[]{0};
                int[] lossIters=new int[]{0};
                
                /*datum.addElements(new Object[]{
                        C,
                        new Integer(level),
                        bestSingleHitScore,
                        bestSingleHitSkill,
                        bestHitScore,
                        bestHitSkill,
                        bestIterScore,
                        bestIterSkill,
                        losses
                });*/
                int H1=0;
                int H2=0;
	            for(int tries=0;tries<TOTAL_ITERATIONS;tries++)
	            {
	                Behavior B=CMClass.getBehavior((String)classSet.elementAt(charClassDex,2));
                    B.setParms(C.ID());
	                switch(roomRobin)
	                {
	                case 0: R=CMClass.getLocale("Woods"); break; 
                    case 1: R=CMClass.getLocale("CaveRoom"); break; 
                    case 2: R=CMClass.getLocale("CityStreet"); break; 
	                }
	                if((++roomRobin)>2) roomRobin=0;
	                R.setRoomID("UNKNOWNAREA#0");
	                R.setArea(A);
	                A.getTimeObj().setTimeOfDay(CMLib.dice().roll(1,A.getTimeObj().getHoursInDay(),-1));
	                
	                MOB M1=CMClass.getMOB("StdMOB");  // player stats
	                M1.baseEnvStats().setLevel(level);
                    M1.baseCharStats().setMyRace(CMClass.getRace("Human"));
	                M1.setName("GOODGUY");
	                M1.recoverCharStats();
                    M1.recoverEnvStats();
                    M1.setLocation(R);
	                M1.baseCharStats().getMyRace().setHeightWeight(M1.baseEnvStats(),(char)M1.baseCharStats().getStat(CharStats.STAT_GENDER));
	                M1.recoverCharStats();
	                M1.recoverEnvStats();
	                M1.recoverMaxState();
	                M1.resetToMaxState();
	                M1.bringToLife(M1.location(),true);
	                C.fillOutMOB(M1,level); // ill advised
	                M1.baseEnvStats().setSpeed(1.0);
	                M1.baseEnvStats().setArmor(100);
	                M1.baseEnvStats().setDamage(0+((level-1)/C.getLevelsPerBonusDamage()));
	                M1.baseState().setHitPoints(C.getLevelPlayerHP(M1));
	                M1.setWimpHitPoint(0);
	                M1.recoverMaxState();
	                M1.recoverCharStats();
	                M1.recoverEnvStats();
	                M1.resetToMaxState();
	                M1.addBehavior(B);
	                equipPlayer(M1);
	                M1.recoverMaxState();
	                M1.recoverCharStats();
	                M1.recoverEnvStats();
                    B.setStat("RECORD"," ");
                    B.setStat("PROF","true");
                    B.setStat("LASTSPELL","");
	                
                    MOB M2=CMClass.getMOB("StdMOB");  // MOB stat
                    M2.baseCharStats().setMyRace(CMClass.getRace("Human"));
                    M2.baseEnvStats().setLevel(level);
                    M2.setName("BADGUY");
                    M2.recoverCharStats();
                    M2.recoverEnvStats();
                    M2.setLocation(R);
                    M2.baseCharStats().getMyRace().setHeightWeight(M2.baseEnvStats(),(char)M2.baseCharStats().getStat(CharStats.STAT_GENDER));
                    M2.recoverCharStats();
                    M2.recoverEnvStats();
                    M2.recoverMaxState();
                    M2.resetToMaxState();
                    M2.bringToLife(M2.location(),true);
                    M2.baseCharStats().getCurrentClass().fillOutMOB(M2,level);
                    int hp=M2.baseCharStats().getCurrentClass().getLevelPlayerHP(M2);
                    if(hp>M2.baseState().getHitPoints())
                        M2.baseState().setHitPoints(hp);
                    M2.setWimpHitPoint(0);
                    M2.recoverMaxState();
                    M2.recoverCharStats();
                    M2.recoverEnvStats();
                    M2.resetToMaxState();
                    
                    M1.setVictim(M2);
                    M2.setVictim(M1);
	                
                    H1=M1.curState().getHitPoints();
                    H2=M2.curState().getHitPoints();
                    
                    int iterations=0;
                    int cumScore=0;
                    long t1=System.currentTimeMillis();
                    while((M1.getVictim()==M2)
                         &&(M2.getVictim()==M1)
                         &&(!M1.amDead())
                         &&(!M2.amDead())
                         &&(M1.location()==M2.location())
                         &&((System.currentTimeMillis()-t1)<20000))
                    {
                        iterations++;
                        int h1=M1.curState().getHitPoints();
                        int h2=M2.curState().getHitPoints();
                        CMLib.threads().tickAllTickers(R);
                        
                        int h=h2-(M2.amDead()?0:M2.curState().getHitPoints());
                        h=h-(h1-(M1.amDead()?0:M1.curState().getHitPoints()));
                        if(h>bestSingleHitScore[0])
                        {
                            bestSingleHitScore[0]=h;
                            bestSingleHitSkill[0]=B.getStat("LASTSPELL");
                        }
                        cumScore+=h;
                    }
                    if((System.currentTimeMillis()-t1)>=20000)
                        Log.errOut("CharGen",level+"/"+tries+"/"+iterations+"/"+B.getStat("LASTSPELL"));
                    else
                    if(M1.amDead()||M2.amDead())
                    {
                    	if(M1.amDead())
                            losses[0]++;
                        avgHits[0]+=cumScore;
                        if(M1.amDead())
                        	lossIters[0]+=iterations;
                        else
	                        avgIters[0]+=iterations;
                        if(cumScore>bestHitScore[0])
                        {
                            bestHitScore[0]=cumScore;
                            bestHitSkill[0]=B.getStat("RECORD");
                        }
                        if(M2.amDead())
                        {
                        	if(!M1.amDead())
	                        if(iterations<bestIterScore[0])
	                        {
	                            bestIterScore[0]=iterations;
	                            bestIterSkill[0]=B.getStat("RECORD");
	                        }
                        }
                        else
                        {
	                        if(iterations>closeIterScore[0])
	                        {
	                            closeIterScore[0]=iterations;
	                            closeIterSkill[0]=B.getStat("RECORD");
	                        }
                        }
                        if(failSkillCheck!=null)
                        {
                            Vector V=CMParms.parseSemicolons(B.getStat("RECORD"),true);
                            for(int v=0;v<V.size();v++)
                            {
                                String s=((String)V.elementAt(v)).trim();
                                boolean failed=false;
                                if(s.startsWith("!"))
                                {
                                    failed=true;
                                    s=s.substring(1);
                                }
                                int[] times=(int[])failSkillCheck.get(s);
                                if(times==null)
                                {
                                    times=new int[2];
                                    failSkillCheck.put(s,times);
                                }
                                times[0]++;
                                if(failed)
                                    times[1]++;
                            }
                        }
                    }
                    M1.destroy();
                    M2.destroy();
	                A.delProperRoom(R);
	                R.destroy();
	            }
                avgHits[0]/=TOTAL_ITERATIONS;
                avgIters[0]/=TOTAL_ITERATIONS;
                lossIters[0]/=TOTAL_ITERATIONS;
                mob.tell("HITPOINTS: "+H1+" vs "+H2);
                mob.tell("BEST ITER: "+bestIterScore[0]+": "+bestIterSkill[0]);
                mob.tell("BEST HITS: "+bestHitScore[0]+": "+bestHitSkill[0]);
                mob.tell("BEST HIT : "+bestSingleHitScore[0]+": "+bestSingleHitSkill[0]);
                mob.tell("CLOS ITER: "+closeIterScore[0]+": "+closeIterSkill[0]);
                mob.tell("AVERAGES : HITS: "+avgHits[0]+", LOSS ITERS: "+lossIters[0]+", WIN ITERS: "+avgIters[0]);
                mob.tell("LOSSES   : "+losses[0]+"/"+TOTAL_ITERATIONS);
                if((failSkillCheck!=null)&&(failSkillCheck.size()>0))
                {
                    StringBuffer fails=new StringBuffer("SKILLFAILS: ");
                    for(Enumeration i=failSkillCheck.keys();i.hasMoreElements();)
                    {
                        String s=(String)i.nextElement();
                        int[] times=(int[])failSkillCheck.get(s);
                        if(times[1]>0) {
                            int pct=(int)Math.round(100.0*CMath.div(times[1],times[0]));
                            if(pct>20)
                                fails.append(s+"("+pct+"%) ");
                        }
                        
                    }
                    mob.tell(fails.toString());
                    failSkillCheck.clear();
                }
                mob.tell("");
	        }
	    }
        CMLib.map().delArea(A);
        A.destroy();
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.isMonster())
			return false;
		commands.removeElementAt(0);
		if(commands.size()>0)
		{
		    if(((String)commands.firstElement()).equalsIgnoreCase("COMBAT"))
		    {
		        commands.removeElementAt(0);
		        combatRun(mob,commands);
		        return true;
		    }
		}
		CharClass C=null;
		int level=-1;
		String ClassName="";
		if(commands.size()>0)
		{
			ClassName=(String)commands.elementAt(0);
			C=CMClass.findCharClass(ClassName);
			level=CMath.s_int(CMParms.combine(commands,1));
		}

		if((C==null)&&(ClassName.toUpperCase().indexOf("ALL")<0))
		{
			mob.tell("Enter 'ALL' for all classes.");
			try
			{
				ClassName=mob.session().prompt("Enter a class name:");
			}
			catch(Exception e){return false;}

			C=CMClass.findCharClass(ClassName);
			if((C==null)&&(ClassName.toUpperCase().indexOf("ALL")<0))
				return false;
		}

		if(level<=0)
		{
			try
			{
				level=CMath.s_int(mob.session().prompt("Enter a level (1-"+CMProps.getIntVar(CMProps.SYSTEMI_LASTPLAYERLEVEL)+"): "));
			}
			catch(Exception e){return false;}
			if(level<=0)
				return false;
		}

		if(C!=null)
			mob.session().print("\n\rAverage "+C.name()+"...");
		else
			mob.session().print("\n\rAverage MOB stats, across all classes...");

		MOB avgMob=null;
		if(C!=null)
			avgMob=AverageClassMOB(mob, level,C, 100);
		else
			avgMob=AverageAllClassMOB(mob,level, 20, 40);

		mob.session().println("\n\r");

		if(avgMob!=null)
		{
			StringBuffer msg=CMLib.commands().getScore(avgMob);
			if(!mob.isMonster())
				mob.session().wraplessPrintln(msg.toString());
		}
        avgMob.destroy();
		return false;
	}
	
	public boolean canBeOrdered(){return true;}
	public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),"CHARGEN");}

	
}
