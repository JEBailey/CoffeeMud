package com.planet_ink.coffee_mud.Abilities.Common;
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

public class CraftingSkill extends GatheringSkill
{
	public String ID() { return "CraftingSkill"; }
	public String name(){ return "Crafting Skill";}
	public int classificationCode(){return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_CRAFTINGSKILL;}
    public String accountForYourself(){return name()+" requires: "+supportedResourceString();}
	protected Item building=null;
	protected boolean fireRequired=true;
	protected boolean mending=false;
	protected boolean refitting=false;
	protected boolean messedUp=false;
	protected static Room fakeRoom=null;

	public CraftingSkill(){super();}
	
	protected String replacePercent(String thisStr, String withThis)
	{
		if(withThis.length()==0)
		{
			int x=thisStr.indexOf("% ");
			if(x>=0) return new StringBuffer(thisStr).replace(x,x+2,withThis).toString();
			x=thisStr.indexOf(" %");
			if(x>=0) return new StringBuffer(thisStr).replace(x,x+2,withThis).toString();
			x=thisStr.indexOf("%");
			if(x>=0) return new StringBuffer(thisStr).replace(x,x+1,withThis).toString();
		}
		else
		{
			int x=thisStr.indexOf("%");
			if(x>=0) return new StringBuffer(thisStr).replace(x,x+1,withThis).toString();
		}
		return thisStr;
	}
	
	protected Vector addRecipes(MOB mob, Vector recipes)
	{
	    if(mob==null) return recipes;
	    Item I=null;
	    Vector V=null;
	    Vector V2=null;
	    boolean clonedYet=false;
	    for(int i=0;i<mob.inventorySize();i++)
	    {
	        I=mob.fetchInventory(i);
	        if((I instanceof Recipe)
	        &&(((Recipe)I).getCommonSkillID().equalsIgnoreCase(ID())))
	        {
	            if(!clonedYet){ recipes=(Vector)recipes.clone(); clonedYet=true;}
	            V=loadList(new StringBuffer(((Recipe)I).getRecipeCodeLine()));
	            for(int v=0;v<V.size();v++)
	            {
	                V2=(Vector)V.elementAt(v);
	                if((recipes.size()==0)||(((Vector)recipes.lastElement()).size()<=V2.size()))
	                    recipes.addElement(V2);
	                else
	                    Log.errOut(ID(),"Not enough parms ("+((Vector)recipes.lastElement()).size()+"<="+V2.size()+"): "+((Recipe)I).getRecipeCodeLine());
	                V2.trimToSize();
	            }
	        }
	    }
	    if(recipes!=null) recipes.trimToSize();
	    return recipes;
	}
	
	protected void dropAWinner(MOB mob, Item building)
	{
		Room R=mob.location();
		if(R==null)
			commonTell(mob,"You are NOWHERE?!");
		else
		if(building==null)
			commonTell(mob,"You have built NOTHING?!!");
		else
		{
			R.addItemRefuse(building,Item.REFUSE_PLAYER_DROP);
			R.recoverRoomStats();
			boolean foundIt=false;
			for(int r=0;r<R.numItems();r++)
				if(R.fetchItem(r)==building)
					foundIt=true;
			if(!foundIt)
			{
				commonTell(mob,"You have won the common-skill-failure LOTTERY! Congratulations!");
				CMLib.leveler().postExperience(mob, null, null,50,false);
			}
		}
	}
	
	protected void addSpells(Environmental E, String spells)
	{
	    if(spells.length()==0) return;
	    if(spells.equalsIgnoreCase("bundle")) return;
	    if(spells.startsWith("*"))
	    {
	        spells=spells.substring(1);
	        int x=spells.indexOf(";");
	        if(x<0) x=spells.length();
	        Ability A=CMClass.getAbility(spells.substring(0,x));
	        if(A!=null)
	        {
	            if(x<spells.length())
		            A.setMiscText(spells.substring(x+1));
	            E.addNonUninvokableEffect(A);
	            return;
	        }
	    }
	    Vector V=CMParms.parseSemicolons(spells,true);
	    Ability lastSpell=null;
	    Ability A=null;
	    for(int v=0;v<V.size();v++)
	    {
	        spells=(String)V.elementAt(v); 
	        A=CMClass.getAbility(spells);
	        if(A==null)
	        {
	            if(lastSpell!=null)
	                lastSpell.setMiscText(spells);
	        }
	        else
	        {
	            lastSpell=A;
	            E.addNonUninvokableEffect(A);
	        }
	    }
	}
	protected Vector loadList(StringBuffer str)
	{
		Vector V=new Vector();
		if(str==null) return V;
		Vector V2=new Vector();
		boolean oneComma=false;
		int start=0;
		int longestList=0;
		for(int i=0;i<str.length();i++)
		{
			if(str.charAt(i)=='\t')
			{
				V2.addElement(str.substring(start,i));
				start=i+1;
				oneComma=true;
			}
			else
			if((str.charAt(i)=='\n')||(str.charAt(i)=='\r'))
			{
				if(oneComma)
				{
					V2.addElement(str.substring(start,i));
					if(V2.size()>longestList) longestList=V2.size();
					V.addElement(V2);
					V2=new Vector();
				}
				start=i+1;
				oneComma=false;
			}
		}
		if((oneComma)&&(str.substring(start).trim().length()>0))
			V2.addElement(str.substring(start));
		if(V2.size()>1)
		{
			if(V2.size()>longestList) longestList=V2.size();
			V.addElement(V2);
		}
		for(int v=0;v<V.size();v++)
		{
			V2=(Vector)V.elementAt(v);
			while(V2.size()<longestList)
				V2.addElement("");
		}
		return V;
	}

    protected Vector loadRecipes(String filename)
    {
        Vector V=(Vector)Resources.getResource("PARSED: "+filename);
        if(V==null)
        {
            StringBuffer str=new CMFile(Resources.buildResourcePath("skills")+filename,null,true).text();
            V=loadList(str);
            if(V.size()==0)
                Log.errOut(ID(),"Recipes not found!");
            Resources.submitResource("PARSED: "+filename,V);
        }
        return V;
    }
    
	protected static final int FOUND_CODE=0;
	protected static final int FOUND_AMT=1;
	protected int fixResourceRequirement(int resource, int amt)
	{
		if(amt<=0) return amt;
		switch(resource)
		{
		case RawMaterial.RESOURCE_MITHRIL:
			amt=amt/2;
			break;
		case RawMaterial.RESOURCE_ADAMANTITE:
			amt=amt/3;
			break;
		case RawMaterial.RESOURCE_BALSA:
			amt=amt/2;
			break;
		case RawMaterial.RESOURCE_IRONWOOD:
			amt=amt*2;
			break;
		}
		if(amt<=0) amt=1;
		return amt;
	}
	
	public Vector fetchRecipes(){return loadRecipes();}
	protected Vector loadRecipes(){ return new Vector();}
	
	protected int[][] fetchFoundResourceData(MOB mob,
											 int req1Required,
											 String req1Desc, int[] req1,
											 int req2Required,
											 String req2Desc, int[] req2,
											 boolean bundle,
											 int autoGeneration,
											 DVector eduMods)
	{
		int[][] data=new int[2][2];
		if((req1Desc!=null)&&(req1Desc.length()==0)) req1Desc=null;
		if((req2Desc!=null)&&(req2Desc.length()==0)) req2Desc=null;

		// the fake resource generation:
		if(autoGeneration>0)
		{
			data[0][FOUND_AMT]=req1Required;
			data[1][FOUND_AMT]=req2Required;
			data[0][FOUND_CODE]=autoGeneration;
            data[1][FOUND_CODE]=autoGeneration;
			return data;
		}

		Item firstWood=null;
		Item firstOther=null;
		if(req1!=null)
		{
			for(int i=0;i<req1.length;i++)
			{
				if((req1[i]&RawMaterial.RESOURCE_MASK)==0)
					firstWood=CMLib.materials().findMostOfMaterial(mob.location(),req1[i]);
				else
					firstWood=CMLib.materials().findFirstResource(mob.location(),req1[i]);
				
				if(firstWood!=null) break;
			}
		}
		else
		if(req1Desc!=null)
			firstWood=CMLib.materials().fetchFoundOtherEncoded(mob.location(),req1Desc);
		data[0][FOUND_AMT]=0;
		if(firstWood!=null)
		{
			data[0][FOUND_AMT]=CMLib.materials().findNumberOfResource(mob.location(),firstWood.material());
			data[0][FOUND_CODE]=firstWood.material();
		}

		if(req2!=null)
		{
			for(int i=0;i<req2.length;i++)
			{
				if((req2[i]&RawMaterial.RESOURCE_MASK)==0)
					firstOther=CMLib.materials().findMostOfMaterial(mob.location(),req2[i]);
				else
					firstOther=CMLib.materials().findFirstResource(mob.location(),req2[i]);
				if(firstOther!=null) break;
			}
		}
		else
		if(req2Desc!=null)
			firstOther=CMLib.materials().fetchFoundOtherEncoded(mob.location(),req2Desc);
		data[1][FOUND_AMT]=0;
		if(firstOther!=null)
		{
			data[1][FOUND_AMT]=CMLib.materials().findNumberOfResource(mob.location(),firstOther.material());
			data[1][FOUND_CODE]=firstOther.material();
		}
		if(req1Required>0)
		{
			if(data[0][FOUND_AMT]==0)
			{
				if(req1Desc!=null)
					commonTell(mob,"There is no "+req1Desc.toLowerCase()+" here to make anything from!  It might need to put it down first.");
				return null;
			}
			if(!bundle) req1Required=fixResourceRequirement(data[0][FOUND_CODE],req1Required);
		}
		if(req2Required>0)
		{
			if(((req2!=null)&&(data[1][FOUND_AMT]==0))
			||((req2==null)&&(req2Desc!=null)&&(req2Desc.length()>0)&&(data[1][FOUND_AMT]==0)))
			{
				if(req2Desc.equalsIgnoreCase("PRECIOUS"))
					commonTell(mob,"You need some sort of precious stones to make that.  There is not enough here.  Are you sure you set it all on the ground first?");
				else
					commonTell(mob,"You need some "+req2Desc.toLowerCase()+" to make that.  There is not enough here.  Are you sure you set it all on the ground first?");
				return null;
			}
			if(!bundle) req2Required=fixResourceRequirement(data[1][FOUND_CODE],req2Required);
		}

		if(req1Required>data[0][FOUND_AMT])
		{
			commonTell(mob,"You need "+req1Required+" pounds of "+RawMaterial.RESOURCE_DESCS[(data[0][FOUND_CODE]&RawMaterial.RESOURCE_MASK)].toLowerCase()+" to make that.  There is not enough here.  Are you sure you set it all on the ground first?");
			return null;
		}
		data[0][FOUND_AMT]=req1Required;
		if((req2Required>0)&&(req2Required>data[1][FOUND_AMT]))
		{
			commonTell(mob,"You need "+req2Required+" pounds of "+RawMaterial.RESOURCE_DESCS[(data[1][FOUND_CODE]&RawMaterial.RESOURCE_MASK)].toLowerCase()+" to make that.  There is not enough here.  Are you sure you set it all on the ground first?");
			return null;
		}
		data[1][FOUND_AMT]=req2Required;
		return data;
	}

	protected String applyLayers(Armor armor, String misctype)
	{
		int colon=misctype.indexOf(":");
		if(colon>=0)
		{
			short layer=0;
			short layerAtt=0;
			String layers=misctype.substring(0,colon).toUpperCase().trim();
			misctype=misctype.substring(colon+1).trim();
			if((layers.startsWith("MS"))
			||(layers.startsWith("SM")))
			{ layers=layers.substring(2); layerAtt=Armor.LAYERMASK_MULTIWEAR|Armor.LAYERMASK_SEETHROUGH;}
			else
			if(layers.startsWith("M"))
			{ layers=layers.substring(1); layerAtt=Armor.LAYERMASK_MULTIWEAR;}
			else
			if(layers.startsWith("S"))
			{ layers=layers.substring(1); layerAtt=Armor.LAYERMASK_SEETHROUGH;}
			layer=CMath.s_short(layers);
			armor.setClothingLayer(layer);
			armor.setLayerAttributes(layerAtt);
		}
		return misctype;
	}
	
	protected void randomRecipeFix(MOB mob, Vector recipes, Vector commands, int autoGeneration)
	{
		if(((mob.isMonster()&&(!CMLib.flags().isAnimalIntelligence(mob)))||(autoGeneration>0))
		&&(commands.size()==0)
		&&(recipes!=null)
		&&(recipes.size()>0))
		{
            int tries=0;
            int maxtries=100;
            while((++tries)<maxtries)
            {
    			Vector randomRecipe=(Vector)recipes.elementAt(CMLib.dice().roll(1,recipes.size(),-1));
                boolean proceed=true;
                if((randomRecipe.size()>1))
                {
                    int levelIndex=-1;
                    for(int i=1;i<randomRecipe.size();i++)
                    {
                        if(CMath.isInteger((String)randomRecipe.elementAt(i)))
                        {
                            levelIndex=i;
                            break;
                        }
                    }
                    if((levelIndex>0)
                    &&(xlevel(mob)<CMath.s_int((String)randomRecipe.elementAt(levelIndex))))
                        proceed=false;
                }
                if((proceed)||(tries==(maxtries-1)))
                {
        			commands.addElement(randomRecipe.firstElement());
                    break;
                }
            }
		}
	}

	public Vector craftAnyItem(int material){return craftItem(null,material);}
	public Vector craftItem(String recipe, int material)
	{
		Item building=null;
		Item key=null;
		int tries=0;
		if(fakeRoom==null){ fakeRoom=CMLib.map().getRandomRoom();}
		MOB mob=CMLib.map().god(fakeRoom);
		mob.baseEnvStats().setLevel(Integer.MAX_VALUE/2);
		mob.baseEnvStats().setSensesMask(mob.baseEnvStats().sensesMask()|EnvStats.CAN_SEE_DARK);
		mob.recoverEnvStats();
		while(((building==null)||(building.name().endsWith(" bundle")))&&(((++tries)<100)))
		{
			Vector V=new Vector();
			V.addElement(new Integer(material));
			if(recipe!=null) V.addElement(recipe);
			invoke(mob,V,this,true,-1);
			if((V.size()>0)&&(V.lastElement() instanceof Item))
			{
				if((V.size()>1)&&((V.elementAt(V.size()-2) instanceof Item)))
					key=(Item)V.elementAt(V.size()-2);
				else
					key=null;
				building=(Item)V.lastElement();
			}
			else
				building=null;
		}
		mob.destroy();
		if(building==null) return null;
		Vector items=new Vector();
		building.setSecretIdentity("");
		building.recoverEnvStats();
		building.text();
		building.recoverEnvStats();
		if(key!=null)
		{
			key.setSecretIdentity("");
			key.recoverEnvStats();
			key.text();
			key.recoverEnvStats();
		}
		items.addElement(building);
		if(key!=null) items.addElement(key);
		return items;
	}
	public Vector craftAllItemsVectors(int material)
	{
		Vector allItems=new Vector();
		Vector recipes=fetchRecipes();
		Item built=null;
		HashSet usedNames=new HashSet();
		Vector items=null;
		String s=null;
		for(int r=0;r<recipes.size();r++)
		{
			s=(String)(((Vector)recipes.elementAt(r)).firstElement());
			s=CMStrings.replaceAll(s,"%","").trim();
			items=craftItem(s,material);
			if((items==null)||(items.size()==0)) continue;
			built=(Item)items.firstElement();
			if(!usedNames.contains(built.Name()))
			{
				usedNames.add(built.Name());
				allItems.addElement(items);
			}
		}
		usedNames.clear();
		return allItems;
	}
	
	public Vector craftItem(String recipe)
	{
		Vector rscs=myResources();
		if(rscs.size()==0) return new Vector();
		int material=((Integer)rscs.elementAt(CMLib.dice().roll(1,rscs.size(),-1))).intValue();
		return craftItem(recipe,material);
	}
	
	public Vector craftAllItemsVectors()
	{
		Vector rscs=myResources();
		Vector allItems=new Vector();
		Vector items=null;
		for(int r=0;r<rscs.size();r++)
		{
			items=craftAllItemsVectors(((Integer)rscs.elementAt(r)).intValue());
			if((items==null)||(items.size()==0)) continue;
			allItems.addElement(items);
		}
		return allItems;
	}
	
	public Vector matchingRecipeNames(String recipeName, boolean beLoose){return matchingRecipeNames(fetchRecipes(),recipeName,beLoose);}
	protected Vector matchingRecipeNames(Vector recipes, String recipeName, boolean beLoose)
	{
		Vector matches=new Vector();
		if(recipeName.length()==0) return matches;
		for(int r=0;r<recipes.size();r++)
		{
			Vector V=(Vector)recipes.elementAt(r);
			if(V.size()>0)
			{
				String item=(String)V.elementAt(0);
				if(replacePercent(item,"").equalsIgnoreCase(recipeName))
					matches.addElement(V);
			}
		}
		if(matches.size()>0) return matches;
		for(int r=0;r<recipes.size();r++)
		{
			Vector V=(Vector)recipes.elementAt(r);
			if(V.size()>0)
			{
				String item=(String)V.elementAt(0);
				if((replacePercent(item,"").toUpperCase().indexOf(recipeName.toUpperCase())>=0))
					matches.addElement(V);
			}
		}
		if(matches.size()>0) return matches;
		if(beLoose)
		{
			for(int r=0;r<recipes.size();r++)
			{
				Vector V=(Vector)recipes.elementAt(r);
				if(V.size()>0)
				{
					String item=(String)V.elementAt(0);
					if((recipeName.toUpperCase().indexOf(replacePercent(item,"").toUpperCase())>=0))
						matches.addElement(V);
				}
			}
			if(matches.size()>0) return matches;
			String lastWord=(String)CMParms.parse(recipeName).lastElement();
			for(int r=0;r<recipes.size();r++)
			{
				Vector V=(Vector)recipes.elementAt(r);
				if(V.size()>0)
				{
					String item=(String)V.elementAt(0);
					if((replacePercent(item,"").toUpperCase().indexOf(lastWord.toUpperCase())>=0)
					||(lastWord.toUpperCase().indexOf(replacePercent(item,"").toUpperCase())>=0))
						matches.addElement(V);
				}
			}
		}
		return matches;
	}

	protected Vector getAllMendable(MOB mob, Environmental from, Item contained)
	{
		Vector V=new Vector();
		if(from==null) return V;
		if(from instanceof Room)
		{
			Room R=(Room)from;
			for(int i=0;i<R.numItems();i++)
			{
				Item I=R.fetchItem(i);
				if((I!=null)
				&&(I.container()==contained)
				&&(canMend(mob,I,true))
				&&(CMLib.flags().canBeSeenBy(I,mob)))
					V.addElement(I);
			}
		}
		else
		if(from instanceof MOB)
		{
			MOB M=(MOB)from;
			for(int i=0;i<M.inventorySize();i++)
			{
				Item I=M.fetchInventory(i);
				if((I!=null)
				&&(I.container()==contained)
				&&(canMend(mob,I,true))
				&&(CMLib.flags().canBeSeenBy(I,mob))
				&&((mob==from)||(!I.amWearingAt(Item.IN_INVENTORY))))
					V.addElement(I);
			}
		}
		else
		if(from instanceof Item)
		{
			if(from instanceof Container)
				V=getAllMendable(mob,((Item)from).owner(),(Item)from);
			if(canMend(mob,from,true))
				V.addElement(from);
		}
		return V;
	}

	public boolean publicScan(MOB mob, Vector commands)
	{
		String rest=CMParms.combine(commands,1);
		Environmental scanning=null;
		if(rest.length()==0)
			scanning=mob;
		else
		if(rest.equalsIgnoreCase("room"))
			scanning=mob.location();
		else
		{
			scanning=mob.location().fetchInhabitant(rest);
			if((scanning==null)||(!CMLib.flags().canBeSeenBy(scanning,mob)))
			{
				commonTell(mob,"You don't see anyone called '"+rest+"' here.");
				return false;
			}
		}
		Vector allStuff=getAllMendable(mob,scanning,null);
		if(allStuff.size()==0)
		{
			if(mob==scanning)
				commonTell(mob,"You don't seem to have anything that needs mending with "+name()+".");
			else
				commonTell(mob,"You don't see anything on "+scanning.name()+" that needs mending with "+name()+".");
			return false;
		}
		StringBuffer buf=new StringBuffer("");
		if(scanning==mob)
			buf.append("The following items could use some "+name()+":\n\r");
		else
			buf.append("The following items on "+scanning.name()+" could use some "+name()+":\n\r");
		for(int i=0;i<allStuff.size();i++)
		{
			Item I=(Item)allStuff.elementAt(i);
			buf.append(CMStrings.padRight(I.usesRemaining()+"%",5)+I.name());
			if(!I.amWearingAt(Item.IN_INVENTORY))
				buf.append(" ("+CMLib.flags().wornLocation(I.rawWornCode())+")");
			if(i<(allStuff.size()-1))
				buf.append("\n\r");
		}
		commonTell(mob,buf.toString());
		return true;
	}


	protected boolean canMend(MOB mob, Environmental E, boolean quiet)
	{
		if(E==null) return false;
		if(!(E instanceof Item))
		{
			if(!quiet)
				commonTell(mob,"You can't mend "+E.name()+".");
			return false;
		}
		Item IE=(Item)E;
		if(!IE.subjectToWearAndTear())
		{
			if(!quiet)
				commonTell(mob,"You can't mend "+IE.name()+".");
			return false;
		}
		if(IE.usesRemaining()>=100)
		{
			if(!quiet)
				commonTell(mob,IE.name()+" is in good condition already.");
			return false;
		}
		return true;
	}

}
