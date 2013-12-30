package com.planet_ink.coffee_mud.WebMacros;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;
import com.planet_ink.coffee_mud.WebMacros.AreaScriptNext.AreaScriptInstance;
import com.planet_ink.miniweb.interfaces.*;

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
public class AbilityData extends StdWebMacro
{
	public String name() { return "AbilityData"; }

	// valid parms include help, ranges, quality, target, alignment, domain,
	// qualifyQ, auto
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		java.util.Map<String,String> parms=parseParms(parm);
		MOB mob=Authenticate.getAuthenticatedMob(httpReq);
		
		String replaceCommand=httpReq.getUrlParameter("REPLACE");
		if((replaceCommand != null) 
		&& (replaceCommand.length()>0)
		&& (replaceCommand.indexOf('=')>0))
		{
			int eq=replaceCommand.indexOf('=');
			String field=replaceCommand.substring(0,eq);
			String value=replaceCommand.substring(eq+1);
			httpReq.addFakeUrlParameter(field, value);
			httpReq.addFakeUrlParameter("REPLACE","");
		}
		
		String last=httpReq.getUrlParameter("ABILITY");
		if(last==null) return " @break@";
		Ability A=null;
		String newAbilityID=httpReq.getUrlParameter("NEWABILITY");
		String newLanguageID=httpReq.getUrlParameter("NEWLANGUAGE");
		String newCraftSkillID=httpReq.getUrlParameter("NEWCRAFTSKILL");
		A=(Ability)httpReq.getRequestObjects().get("ABILITY-"+last);
		if((A==null)
		&&(newAbilityID!=null)
		&&(newAbilityID.length()>0)
		&&(CMClass.getAbility(newAbilityID)==null))
		{
			A=(Ability)CMClass.getAbility("GenAbility").copyOf();
			A.setStat("CLASS9",newAbilityID);
			last=newAbilityID;
			httpReq.addFakeUrlParameter("ABILITY",newAbilityID);
		}
		if((A==null)
		&&(newLanguageID!=null)
		&&(newLanguageID.length()>0)
		&&(CMClass.getAbility(newLanguageID)==null))
		{
			A=(Ability)CMClass.getAbility("GenLanguage").copyOf();
			A.setStat("CLASS9",newLanguageID);
			last=newLanguageID;
			httpReq.addFakeUrlParameter("ABILITY",newLanguageID);
		}
		if((A==null)
		&&(newCraftSkillID!=null)
		&&(newCraftSkillID.length()>0)
		&&(CMClass.getAbility(newCraftSkillID)==null))
		{
			A=(Ability)CMClass.getAbility("GenCraftSkill").copyOf();
			A.setStat("CLASS9",newCraftSkillID);
			last=newCraftSkillID;
			httpReq.addFakeUrlParameter("ABILITY",newCraftSkillID);
		}
		if(last.length()>0)
		{
			if(A==null)
				A=CMClass.getAbility(last);
			if(parms.containsKey("ISNEWABILITY"))
				return ""+(CMClass.getAbility(last)==null);
			if(A!=null)
			{
				StringBuffer str=new StringBuffer("");
				if(parms.containsKey("ISGENERIC"))
				{
					Ability A2=CMClass.getAbility(A.ID());
					return ""+((A2!=null)&&(A2.isGeneric()));
				}
				if(parms.containsKey("ISLANGUAGE"))
				{
					return Boolean.toString(A instanceof Language);
				}
				if(parms.containsKey("ISCRAFTSKILL"))
				{
					return Boolean.toString(A instanceof ItemCraftor);
				}
				if(parms.containsKey("NAME"))
				{
					String old=httpReq.getUrlParameter("NAME");
					if(old==null) old=A.name();
					str.append(old+", ");
				}
				if(parms.containsKey("GENHELP"))
				{
					String old=httpReq.getUrlParameter("GENHELP");
					if(old==null) old=A.getStat("HELP");
					str.append(old+", ");
				}
				
				if(A instanceof Language)
				{
					if(parms.containsKey("WORDLISTS"))
					{
						List<String[]> wordLists=((Language)A).translationVector(A.ID());
						if(httpReq.isUrlParameter("WORDLIST1"))
						{
							wordLists=new Vector<String[]>();
							int x=1;
							while(httpReq.isUrlParameter("WORDLIST"+x))
							{
								wordLists.add(CMParms.parseCommas(httpReq.getUrlParameter("WORDLIST"+x), true).toArray(new String[0]));
								x++;
							}
						}
						else
						{
							for(int i=wordLists.size()-1;i>=0;i--)
								httpReq.addFakeUrlParameter("WORDLIST"+(i+1), CMParms.toStringList(wordLists.get(i)));
							httpReq.removeUrlParameter("WORDLIST"+(wordLists.size()+1));
						}
						
						for(int i=wordLists.size()-1;i>=0;i--)
							if(wordLists.get(i).length==0)
								wordLists.remove(i);
							else
								break;
						
						if(parms.containsKey("RESET"))
						{
							httpReq.removeUrlParameter("WORDLISTNUM");
							httpReq.removeUrlParameter("WORDLISTNEXT");
							return "";
						}
						else
						if(parms.containsKey("NEXT"))
						{
							String lastID="";
							String lastNum = httpReq.getUrlParameter("WORDLISTNUM");
							String nextName = "WORDLIST1";
							for(int i=0;i<wordLists.size();i++)
							{
								String thisName="WORDLIST"+Integer.toString(i+1);
								nextName="WORDLIST"+Integer.toString(i+2);
								if((lastNum==null)||((lastNum.length()>0)&&(lastNum.equals(lastID))&&(!thisName.equals(lastID))))
								{
									httpReq.addFakeUrlParameter("WORDLISTNUM",thisName);
									last=thisName;
									return "";
								}
								lastID=thisName;
							}
							httpReq.addFakeUrlParameter("WORDLISTNUM","");
							httpReq.addFakeUrlParameter("WORDLISTNEXT",nextName);
							if(parms.containsKey("EMPTYOK"))
								return "<!--EMPTY-->";
							return " @break@";
						}
					}
					
					if(parms.containsKey("HASHWORDS"))
					{
						Map<String,String> hashWords=((Language)A).translationHash(A.ID());
						if(httpReq.isUrlParameter("HASHWORD1"))
						{
							hashWords=new Hashtable<String,String>();
							int x=1;
							while(httpReq.isUrlParameter("HASHWORD"+x))
							{
								String word=httpReq.getUrlParameter("HASHWORD"+x).toUpperCase().trim();
								String def=httpReq.getUrlParameter("HASHWORDDEF"+x);
								if((def!=null)&&(def.length()>0)&&(word.length()>0))
									hashWords.put(word,def);
								x++;
							}
						}
						else
						{
							int x=1;
							for(String key : hashWords.keySet()) 
							{
								httpReq.addFakeUrlParameter("HASHWORD"+x, key);
								httpReq.addFakeUrlParameter("HASHWORDDEF"+x, hashWords.get(key));
								x++;
							}
							httpReq.removeUrlParameter("HASHWORD"+x);
							httpReq.removeUrlParameter("HASHWORDDEF"+x);
						}
						
						if(parms.containsKey("RESET"))
						{
							httpReq.removeUrlParameter("HASHWORDNUM");
							httpReq.removeUrlParameter("HASHWORDDEFNUM");
							httpReq.removeUrlParameter("HASHWORDNEXT");
							httpReq.removeUrlParameter("HASHWORDDEFNEXT");
							return "";
						}
						else
						if(parms.containsKey("NEXT"))
						{
							String lastID="";
							String lastNum = httpReq.getUrlParameter("HASHWORDNUM");
							String nextName = "HASHWORD1";
							String nextDefName = "HASHWORDDEF1";
							for(int i=1;i<=hashWords.keySet().size();i++) 
							{
								String thisName="HASHWORD"+Integer.toString(i);
								String thisDefName="HASHWORDDEF"+Integer.toString(i);
								nextName="HASHWORD"+Integer.toString(i+1);
								nextDefName="HASHWORDDEF"+Integer.toString(i+1);
								if((lastNum==null)||((lastNum.length()>0)&&(lastNum.equals(lastID))&&(!thisName.equals(lastID))))
								{
									httpReq.addFakeUrlParameter("HASHWORDNUM",thisName);
									httpReq.addFakeUrlParameter("HASHWORDDEFNUM",thisDefName);
									last=thisName;
									return "";
								}
								lastID=thisName;
								i++;
							}
							httpReq.addFakeUrlParameter("HASHWORDNUM","");
							httpReq.addFakeUrlParameter("HASHWORDDEFNUM","");
							httpReq.addFakeUrlParameter("HASHWORDNEXT",nextName);
							httpReq.addFakeUrlParameter("HASHWORDDEFNEXT",nextDefName);
							if(parms.containsKey("EMPTYOK"))
								return "<!--EMPTY-->";
							return " @break@";
						}
					}
				}
				
				// here starts CLASSIFICATION
				if(parms.containsKey("CLASSIFICATION_ACODE"))
				{
					String old=httpReq.getUrlParameter("CLASSIFICATION_ACODE");
					if(old==null) old=""+(A.classificationCode()&Ability.ALL_ACODES);
					for(int i=0;i<Ability.ACODE_DESCS.length;i++)
					{
						if(A instanceof ItemCraftor)
						{
							if(i==Ability.ACODE_COMMON_SKILL)
								str.append("<OPTION VALUE=\""+i+"\""+((CMath.s_int(old)==i)?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.ACODE_DESCS[i]));
						}
						else
						if(A instanceof Language)
						{
							if(i==Ability.ACODE_LANGUAGE)
								str.append("<OPTION VALUE=\""+i+"\""+((CMath.s_int(old)==i)?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.ACODE_DESCS[i]));
						}
						else
							str.append("<OPTION VALUE=\""+i+"\""+((CMath.s_int(old)==i)?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.ACODE_DESCS[i]));
					}
					str.append(", ");
				}
				if(parms.containsKey("CLASSIFICATION_DOMAIN"))
				{
					String old=httpReq.getUrlParameter("CLASSIFICATION_DOMAIN");
					if(old==null) old=""+((A.classificationCode()&Ability.ALL_DOMAINS)>>5);
					for(int i=0;i<Ability.DOMAIN_DESCS.length;i++)
						str.append("<OPTION VALUE=\""+i+"\""+((CMath.s_int(old)==i)?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.DOMAIN_DESCS[i]));
					str.append(", ");
				}
				// here ends CLASSIFICATION
				
				if(parms.containsKey("TRIGSTR"))
				{
					String old=httpReq.getUrlParameter("TRIGSTR");
					if(old==null) old=CMParms.toStringList(A.triggerStrings());
					// remember to sort by longest->shortest on put-back
					str.append(old.toUpperCase().trim()+", ");
				}
				if(parms.containsKey("MINRANGE"))
				{
					String old=httpReq.getUrlParameter("MINRANGE");
					if(old==null) old=""+A.minRange();
					for(int i=0;i<Ability.RANGE_CHOICES.length;i++)
						str.append("<OPTION VALUE=\""+i+"\""+((CMath.s_int(old)==i)?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.RANGE_CHOICES[i]));
					str.append(", ");
				}
				if(parms.containsKey("MAXRANGE"))
				{
					String old=httpReq.getUrlParameter("MAXRANGE");
					if(old==null) old=""+A.maxRange();
					for(int i=0;i<Ability.RANGE_CHOICES.length;i++)
						str.append("<OPTION VALUE=\""+i+"\""+((CMath.s_int(old)==i)?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.RANGE_CHOICES[i]));
					str.append(", ");
				}
				if(parms.containsKey("TICKSBETWEENCASTS"))
				{
					String old=httpReq.getUrlParameter("TICKSBETWEENCASTS");
					if(old==null) old=""+A.getTicksBetweenCasts();
					str.append(old+", ");
				}
				if(parms.containsKey("DISPLAY")) // affected string
				{
					String old=httpReq.getUrlParameter("DISPLAY");
					if(old==null) old=A.displayText();
					str.append(old+", ");
				}
				if(parms.containsKey("AUTOINVOKE"))
				{
					String old=httpReq.getUrlParameter("AUTOINVOKE");
					if(old==null) 
						old=A.getStat("AUTOINVOKE");
					else
						old=""+old.equalsIgnoreCase("on");
					str.append(CMath.s_bool(old)?"CHECKED":"");
					str.append(", ");
				}
				if(parms.containsKey("ABILITY_FLAGS"))
				{
					Vector<String> list=new Vector<String>();
					if(httpReq.isUrlParameter("ABILITY_FLAGS"))
					{
						String id="";
						int num=0;
						for(;httpReq.isUrlParameter("ABILITY_FLAGS"+id);id=""+(++num))
							list.addElement(httpReq.getUrlParameter("ABILITY_FLAGS"+id));
					} 
					else 
						list=CMParms.parseCommas(A.getStat("FLAGS"),true);
					for(int i=0;i<Ability.FLAG_DESCS.length;i++)
						str.append("<OPTION VALUE=\""+Ability.FLAG_DESCS[i]+"\""+(list.contains(Ability.FLAG_DESCS[i])?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.FLAG_DESCS[i]));
					str.append(", ");
				}
				if(parms.containsKey("CUSTOMOVERRIDEMANA"))
				{
					String old=httpReq.getUrlParameter("OVERRIDEMANA");
					if(old==null) old=""+A.getStat("OVERRIDEMANA");
					int x=CMath.s_int(old);
					if((x>0) && (x<Ability.COST_PCT))
						str.append(old+", ");
				}
				if(parms.containsKey("OVERRIDEMANA"))
				{
					String old=httpReq.getUrlParameter("OVERRIDEMANA");
					if(old==null) old=""+A.getStat("OVERRIDEMANA");
					int o=CMath.s_int(old);
					str.append("<OPTION VALUE=\"-1\""+((o==-1)?" SELECTED":"")+">Use Default");
					str.append("<OPTION VALUE=\"0\""+((o==0)?" SELECTED":"")+">None (free skill)");
					str.append("<OPTION VALUE=\"\""+(((o>0)&&(o<Ability.COST_PCT))?" SELECTED":"")+"\">Custom Value");
					str.append("<OPTION VALUE=\""+Ability.COST_ALL+"\""+((o==Ability.COST_ALL)?" SELECTED":"")+">All Mana");
					for(int v=Ability.COST_ALL-5;v>=Ability.COST_ALL-95;v-=5) {
						str.append("<OPTION VALUE=\""+v+"\""+(((o>(v-5))&&(o<=v))?" SELECTED":"")+">"+(Ability.COST_ALL-v)+"%");
					}
					str.append(", ");
				}
				if(parms.containsKey("USAGEMASK"))
				{
					Vector<String> list=new Vector<String>();
					if(httpReq.isUrlParameter("USAGEMASK"))
					{
						String id="";
						int num=0;
						for(;httpReq.isUrlParameter("USAGEMASK"+id);id=""+(++num))
							list.addElement(httpReq.getUrlParameter("USAGEMASK"+id));
					} 
					else 
						list=CMParms.parseCommas(A.getStat("USAGEMASK"),true);
					for(int i=0;i<Ability.USAGE_DESCS.length;i++)
						str.append("<OPTION VALUE=\""+Ability.USAGE_DESCS[i]+"\""+(list.contains(Ability.USAGE_DESCS[i])?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.USAGE_DESCS[i]));
					str.append(", ");
				}
				if(parms.containsKey("CANAFFECTMASK"))
				{
					Vector<String> list=new Vector<String>();
					if(httpReq.isUrlParameter("CANAFFECTMASK"))
					{
						String id="";
						int num=0;
						for(;httpReq.isUrlParameter("CANAFFECTMASK"+id);id=""+(++num))
							list.addElement(httpReq.getUrlParameter("CANAFFECTMASK"+id));
					} 
					else 
						list=CMParms.parseCommas(A.getStat("CANAFFECTMASK"),true);
					for(int i=0;i<Ability.CAN_DESCS.length;i++)
						str.append("<OPTION VALUE=\""+Ability.CAN_DESCS[i]+"\""+(list.contains(Ability.CAN_DESCS[i])?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.CAN_DESCS[i]));
					str.append(", ");
				}
				if(parms.containsKey("CANTARGETMASK"))
				{
					Vector<String> list=new Vector<String>();
					if(httpReq.isUrlParameter("CANTARGETMASK"))
					{
						String id="";
						int num=0;
						for(;httpReq.isUrlParameter("CANTARGETMASK"+id);id=""+(++num))
							list.addElement(httpReq.getUrlParameter("CANTARGETMASK"+id));
					} 
					else 
						list=CMParms.parseCommas(A.getStat("CANTARGETMASK"),true);
					for(int i=0;i<Ability.CAN_DESCS.length;i++)
						str.append("<OPTION VALUE=\""+Ability.CAN_DESCS[i]+"\""+(list.contains(Ability.CAN_DESCS[i])?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.CAN_DESCS[i]));
					str.append(", ");
				}
				if(parms.containsKey("VQUALITY")) //QUALITY
				{
					String old=httpReq.getUrlParameter("VQUALITY");
					if(old==null) old=""+A.abstractQuality();
					for(int i=0;i<Ability.QUALITY_DESCS.length;i++)
						str.append("<OPTION VALUE=\""+i+"\""+((CMath.s_int(old)==i)?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(Ability.QUALITY_DESCS[i]));
					str.append(", ");
				}
				if(parms.containsKey("HERESTATS")) // affect adj: Prop_HereAdjuster
				{
					String old=httpReq.getUrlParameter("HERESTATS");
					if(old==null) old=A.getStat("HERESTATS");
					str.append(old+", ");
				}
				if(parms.containsKey("SCRIPT"))
				{
					String old=httpReq.getUrlParameter("SCRIPT");
					if(old==null) old=A.getStat("SCRIPT");
					str.append(old+", ");
				}
				if(parms.containsKey("CASTMASK"))
				{
					String old=httpReq.getUrlParameter("CASTMASK");
					if(old==null) old=A.getStat("CASTMASK");
					str.append(old+", ");
				}
				if(parms.containsKey("TARGETMASK"))
				{
					String old=httpReq.getUrlParameter("TARGETMASK");
					if(old==null) old=A.getStat("TARGETMASK");
					str.append(old+", ");
				}
				if(parms.containsKey("FIZZLEMSG"))
				{
					String old=httpReq.getUrlParameter("FIZZLEMSG");
					if(old==null) old=A.getStat("FIZZLEMSG");
					str.append(old+", ");
				}
				if(parms.containsKey("AUTOCASTMSG"))
				{
					String old=httpReq.getUrlParameter("AUTOCASTMSG");
					if(old==null) old=A.getStat("AUTOCASTMSG");
					str.append(old+", ");
				}
				if(parms.containsKey("CASTMSG"))
				{
					String old=httpReq.getUrlParameter("CASTMSG");
					if(old==null) old=A.getStat("CASTMSG");
					str.append(old+", ");
				}
				if(parms.containsKey("FILENAME"))
				{
					String old=httpReq.getUrlParameter("FILENAME");
					if(old==null) old=A.getStat("FILENAME");
					str.append(old+", ");
				}
				if(parms.containsKey("VERB"))
				{
					String old=httpReq.getUrlParameter("VERB");
					if(old==null) old=A.getStat("VERB");
					str.append(old+", ");
				}
				if(parms.containsKey("SOUND"))
				{
					String old=httpReq.getUrlParameter("SOUND");
					if(old==null) old=A.getStat("SOUND");
					str.append(old+", ");
				}
				if(parms.containsKey("POSTCASTMSG"))
				{
					String old=httpReq.getUrlParameter("POSTCASTMSG");
					if(old==null) old=A.getStat("POSTCASTMSG");
					str.append(old+", ");
				}
				if(parms.containsKey("ATTACKCODE"))
				{
					String old=httpReq.getUrlParameter("ATTACKCODE");
					if(old==null) old=""+CMParms.indexOf(CMMsg.TYPE_DESCS,A.getStat("ATTACKCODE"));
					for(int i=0;i<CMMsg.TYPE_DESCS.length;i++)
						str.append("<OPTION VALUE=\""+i+"\""+((CMath.s_int(old)==i)?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(CMMsg.TYPE_DESCS[i]));
					str.append(", ");
				}
				if(parms.containsKey("CANMEND"))
				{
					String old=httpReq.getUrlParameter("CANMEND");
					if(old==null) 
						old=A.getStat("CANMEND");
					else
						old=old.equalsIgnoreCase("on")?"true":"false";
					str.append(old.equalsIgnoreCase("true")?"checked":"");
				}
				if(parms.containsKey("CANREFIT"))
				{
					String old=httpReq.getUrlParameter("CANREFIT");
					if(old==null) 
						old=A.getStat("CANREFIT");
					else
						old=old.equalsIgnoreCase("on")?"true":"false";
					str.append(old.equalsIgnoreCase("true")?"checked":"");
				}
				if(parms.containsKey("CANBUNDLE"))
				{
					String old=httpReq.getUrlParameter("CANBUNDLE");
					if(old==null) 
						old=A.getStat("CANBUNDLE");
					else
						old=old.equalsIgnoreCase("on")?"true":"false";
					str.append(old.equalsIgnoreCase("true")?"checked":"");
				}
				if(parms.containsKey("CANSIT"))
				{
					String old=httpReq.getUrlParameter("CANSIT");
					if(old==null) 
						old=A.getStat("CANSIT");
					else
						old=old.equalsIgnoreCase("on")?"true":"false";
					str.append(old.equalsIgnoreCase("true")?"checked":"");
				}
				
				if(parms.containsKey("MATLIST"))
				{
					Vector<String> list=new Vector<String>();
					if(httpReq.isUrlParameter("MATLIST"))
					{
						String id="";
						int num=0;
						for(;httpReq.isUrlParameter("MATLIST"+id);id=""+(++num))
							list.addElement(httpReq.getUrlParameter("MATLIST"+id).toUpperCase().trim());
					} 
					else 
						list=CMParms.parseCommas(A.getStat("MATLIST"),true);
					for(RawMaterial.Material m : RawMaterial.Material.values())
						str.append("<OPTION VALUE=\""+m.name()+"\""+(list.contains(m.name())?" SELECTED":"")+">"+m.noun());
					for(int i=0;i<RawMaterial.CODES.NAMES().length;i++)
						str.append("<OPTION VALUE=\""+RawMaterial.CODES.NAMES()[i]+"\""+(list.contains(RawMaterial.CODES.NAMES()[i])?" SELECTED":"")+">"+CMStrings.capitalizeAndLower(RawMaterial.CODES.NAMES()[i]));
					str.append(", ");
				}
				
				if(parms.containsKey("POSTCASTAFFECT"))
				{
					Vector<String> list=new Vector<String>();
					if(httpReq.isUrlParameter("POSTCASTAFFECT"))
					{
						String id="";
						int num=0;
						for(;httpReq.isUrlParameter("POSTCASTAFFECT"+id);id=""+(++num))
							list.addElement(httpReq.getUrlParameter("POSTCASTAFFECT"+id).toUpperCase());
					} 
					else 
						list=CMParms.parseSemicolons(A.getStat("POSTCASTAFFECT").toUpperCase(),true);
					for(Enumeration<Ability> e=CMClass.abilities();e.hasMoreElements();)
					{
						Ability A2=e.nextElement();
						if(((A2.classificationCode()&Ability.ALL_DOMAINS)==Ability.DOMAIN_ARCHON)&&(!CMSecurity.isASysOp(mob)))
							continue;
						String AID=A2.ID();
						String ANAME=A2.name();
						str.append("<OPTION VALUE=\""+AID+"\""+(list.contains(AID.toUpperCase())?" SELECTED":"")+">"+ANAME);
					}
					str.append(", ");
				}
				if(parms.containsKey("POSTCASTABILITY"))
				{
					Vector<String> list=new Vector<String>();
					if(httpReq.isUrlParameter("POSTCASTABILITY"))
					{
						String id="";
						int num=0;
						for(;httpReq.isUrlParameter("POSTCASTABILITY"+id);id=""+(++num))
							list.addElement(httpReq.getUrlParameter("POSTCASTABILITY"+id).toUpperCase());
					} 
					else 
						list=CMParms.parseSemicolons(A.getStat("POSTCASTABILITY").toUpperCase(),true);
					for(Enumeration<Ability> e=CMClass.abilities();e.hasMoreElements();)
					{
						Ability A2=e.nextElement();
						if(((A.classificationCode()&Ability.ALL_DOMAINS)==Ability.DOMAIN_ARCHON)&&(!CMSecurity.isASysOp(mob)))
							continue;
						String AID=A2.ID();
						String ANAME=A2.name();
						str.append("<OPTION VALUE=\""+AID+"\""+(list.contains(AID.toUpperCase())?" SELECTED":"")+">"+ANAME);
					}
					str.append(", ");
				}
				if(parms.containsKey("POSTCASTDAMAGE"))
				{
					/*
						Enter a damage or healing formula.
						Use +/-*()?. @x1=caster level, @x2=target level.
						Formula evaluates >0 for damage, <0 for healing. Requires Can Target!"
					*/
					String old=httpReq.getUrlParameter("POSTCASTDAMAGE");
					if(old==null) old=A.getStat("POSTCASTDAMAGE");
					str.append(old+", ");
				}
				
				
				
				/*********************************************************************************/
				/*********************************************************************************/
				// here begins the old display data parms
				
				if(parms.containsKey("HELP"))
				{
					StringBuilder s=CMLib.help().getHelpText(A.ID(),null,false);
					if(s==null)
						s=CMLib.help().getHelpText(A.Name(),null,false);
					int limit=78;
					if(parms.containsKey("LIMIT")) limit=CMath.s_int(parms.get("LIMIT"));
					str.append(helpHelp(s,limit));
				}
				if(parms.containsKey("RANGES"))
				{
					int min=A.minRange();
					int max=A.maxRange();
					if(min+max==0)
						str.append("Touch, or not applicable, ");
					else
					{
						if(min==0)
							str.append("Touch");
						else
							str.append("Range "+min);
						if(max>0)
							str.append(" - Range "+max);
						str.append(", ");
					}
				}
				if(parms.containsKey("QUALITY"))
				{
					switch(A.abstractQuality())
					{
					case Ability.QUALITY_MALICIOUS:
						str.append("Malicious, ");
						break;
					case Ability.QUALITY_BENEFICIAL_OTHERS:
					case Ability.QUALITY_BENEFICIAL_SELF:
						str.append("Always Beneficial, ");
						break;
					case Ability.QUALITY_OK_OTHERS:
					case Ability.QUALITY_OK_SELF:
						str.append("Sometimes Beneficial, ");
						break;
					case Ability.QUALITY_INDIFFERENT:
						str.append("Circumstantial, ");
						break;
					}
				}
				if(parms.containsKey("AUTO"))
				{
					if(A.isAutoInvoked())
						str.append("Automatic, ");
					else
						str.append("Requires invocation, ");
				}
				if(parms.containsKey("TARGET"))
				{
					switch(A.abstractQuality())
					{
					case Ability.QUALITY_INDIFFERENT:
						str.append("Item or Room, ");
						break;
					case Ability.QUALITY_MALICIOUS:
						str.append("Others, ");
						break;
					case Ability.QUALITY_BENEFICIAL_OTHERS:
					case Ability.QUALITY_OK_OTHERS:
						str.append("Caster or others, ");
						break;
					case Ability.QUALITY_BENEFICIAL_SELF:
					case Ability.QUALITY_OK_SELF:
						str.append("Caster only, ");
						break;
					}
				}

				if(parms.containsKey("ALIGNMENT"))
				{
					String rangeDesc=null;
					for(Enumeration<Faction> e=CMLib.factions().factions();e.hasMoreElements();)
					{
						Faction F=e.nextElement();
						rangeDesc=F.usageFactorRangeDescription(A);
						if(rangeDesc.length()>0)
							str.append(rangeDesc+", ");
					}
				}
				if(parms.containsKey("ALLOWS"))
				{
					Ability A2=null;
					ExpertiseLibrary.ExpertiseDefinition def=null;
					for(Iterator<String> i=CMLib.ableMapper().getAbilityAllowsList(A.ID());i.hasNext();)
					{
						String allowStr=i.next();
						def=CMLib.expertises().getDefinition(allowStr);
						if(def!=null)
							str.append(def.name+", ");
						else
						{
							A2=CMClass.getAbility(allowStr);
							if(A2!=null)
								str.append(A2.Name()+", ");
						}
					}
				}
				if(parms.containsKey("DOMAIN"))
				{
					StringBuffer thang=new StringBuffer("");
					if((A.classificationCode()&Ability.ALL_DOMAINS)!=0)
					{
						int domain=A.classificationCode()&Ability.ALL_DOMAINS;
						domain=domain>>5;
						thang.append(Ability.DOMAIN_DESCS[domain].toLowerCase().replace('_',' '));
					}
					else
						thang.append(Ability.ACODE_DESCS[A.classificationCode()&Ability.ALL_ACODES].toLowerCase());
					if(thang.length()>0)
					{
						thang.setCharAt(0,Character.toUpperCase(thang.charAt(0)));

						int x=thang.toString().indexOf('/');
						if(x>0) thang.setCharAt(x+1,Character.toUpperCase(thang.charAt(x+1)));
						str.append(thang.toString()+", ");
					}
				}
				if(parms.containsKey("TYPENDOMAIN"))
				{
					StringBuffer thang=new StringBuffer("");
					thang.append(CMStrings.capitalizeAndLower(Ability.ACODE_DESCS[A.classificationCode()&Ability.ALL_ACODES]));
					if((A.classificationCode()&Ability.ALL_DOMAINS)!=0)
					{
						int domain=A.classificationCode()&Ability.ALL_DOMAINS;
						domain=domain>>5;
						thang.append(": "+CMStrings.capitalizeAndLower(Ability.DOMAIN_DESCS[domain]).replace('_',' '));
					}
					
					if(thang.length()>0)
					{
						thang.setCharAt(0,Character.toUpperCase(thang.charAt(0)));

						int x=thang.toString().indexOf('/');
						while(x>0){
							thang.setCharAt(x+1,Character.toUpperCase(thang.charAt(x+1)));
							x=thang.toString().indexOf('/',x+1);
						}
						str.append(thang.toString()+", ");
					}
				}
				if(parms.containsKey("QLEVEL"))
				{
					String className=httpReq.getUrlParameter("CLASS");
					int level=0;
					if((className!=null)&&(className.length()>0))
						level=CMLib.ableMapper().getQualifyingLevel(className,true,A.ID());
					else
						level=CMLib.ableMapper().getQualifyingLevel("Archon",true,A.ID());
					str.append(level+", ");
				}
				if(parms.containsKey("QUALIFYQ")&&(httpReq.isUrlParameter("CLASS")))
				{
					String className=httpReq.getUrlParameter("CLASS");
					if((className!=null)&&(className.length()>0))
					{
						boolean defaultGain=CMLib.ableMapper().getDefaultGain(className,true,A.ID());
						if(!defaultGain)
							str.append("(Qualify), ");
					}
				}
				String strstr=str.toString();
				if(strstr.endsWith(", "))
					strstr=strstr.substring(0,strstr.length()-2);
				return clearWebMacros(strstr);
			}
		}
		return "";
	}
}
