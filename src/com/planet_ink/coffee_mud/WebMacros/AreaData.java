package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Areas.interfaces.AutoGenArea;
import com.planet_ink.coffee_mud.Areas.interfaces.GridZones;
import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.Common.interfaces.TimeClock;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Resources;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.PhysicalAgent;
import com.planet_ink.miniweb.interfaces.HTTPRequest;

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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AreaData extends StdWebMacro {
	public String name() {
		return "AreaData";
	}

	public static StringBuffer behaves(PhysicalAgent E, HTTPRequest httpReq,
			java.util.Map<String, String> parms, int borderSize) {
		StringBuffer str = new StringBuffer("");
		if (parms.containsKey("BEHAVIORS")) {
			Vector theclasses = new Vector();
			Vector theparms = new Vector();
			if (httpReq.isUrlParameter("BEHAV1")) {
				int num = 1;
				String behav = httpReq.getUrlParameter("BEHAV" + num);
				String theparm = httpReq.getUrlParameter("BDATA" + num);
				while ((behav != null) && (theparm != null)) {
					if (behav.length() > 0) {
						theclasses.addElement(behav);
						String t = theparm;
						t = CMStrings.replaceAll(t, "\"", "&quot;");
						theparms.addElement(t);
					}
					num++;
					behav = httpReq.getUrlParameter("BEHAV" + num);
					theparm = httpReq.getUrlParameter("BDATA" + num);
				}
			} else
				for (Enumeration<Behavior> e = E.behaviors(); e
						.hasMoreElements();) {
					Behavior B = e.nextElement();
					if ((B != null) && (B.isSavable())) {
						theclasses.addElement(CMClass.classID(B));
						String t = B.getParms();
						t = CMStrings.replaceAll(t, "\"", "&quot;");
						theparms.addElement(t);
					}
				}
			str.append("<TABLE WIDTH=100% BORDER=\"" + borderSize
					+ "\" CELLSPACING=0 CELLPADDING=0>");
			HashSet<String> alreadyHave = new HashSet<String>();
			for (int i = 0; i < theclasses.size(); i++) {
				String theclass = (String) theclasses.elementAt(i);
				String theparm = (String) theparms.elementAt(i);
				str.append("<TR><TD WIDTH=50%>");
				str.append("<SELECT ONCHANGE=\"EditBehavior(this);\" NAME=BEHAV"
						+ (i + 1) + ">");
				str.append("<OPTION VALUE=\"\">Delete!");
				str.append("<OPTION VALUE=\"" + theclass + "\" SELECTED>"
						+ theclass);
				str.append("</SELECT>");
				str.append("</TD><TD WIDTH=50%>");
				str.append("<INPUT TYPE=TEXT SIZE=30 NAME=BDATA" + (i + 1)
						+ " VALUE=\"" + theparm + "\">");
				str.append("</TD></TR>");
				alreadyHave.add(theclass.toLowerCase());
			}
			str.append("<TR><TD WIDTH=50%>");
			str.append("<SELECT ONCHANGE=\"AddBehavior(this);\" NAME=BEHAV"
					+ (theclasses.size() + 1) + ">");
			str.append("<OPTION SELECTED VALUE=\"\">Select a Behavior");

			Object[] sortedB = null;
			Vector sortMeB = new Vector();
			for (Enumeration b = CMClass.behaviors(); b.hasMoreElements();) {
				Behavior B = (Behavior) b.nextElement();
				if (B.canImprove(E))
					sortMeB.addElement(CMClass.classID(B));
			}
			sortedB = (new TreeSet(sortMeB)).toArray();
			for (int r = 0; r < sortedB.length; r++)
				if (!alreadyHave.contains(((String) sortedB[r]).toLowerCase())) {
					String cnam = (String) sortedB[r];
					str.append("<OPTION VALUE=\"" + cnam + "\">" + cnam);
				}
			str.append("</SELECT>");
			str.append("</TD><TD WIDTH=50%>");
			str.append("<INPUT TYPE=TEXT SIZE=30 NAME=BDATA"
					+ (theclasses.size() + 1) + " VALUE=\"\">");
			str.append("</TD></TR>");
			str.append("</TABLE>");
		}
		return str;
	}

	public static StringBuffer affects(Physical P, HTTPRequest httpReq,
			java.util.Map<String, String> parms, int borderSize) {
		StringBuffer str = new StringBuffer("");
		if (parms.containsKey("AFFECTS")) {
			Vector theclasses = new Vector();
			Vector theparms = new Vector();
			if (httpReq.isUrlParameter("AFFECT1")) {
				int num = 1;
				String behav = httpReq.getUrlParameter("AFFECT" + num);
				String theparm = httpReq.getUrlParameter("ADATA" + num);
				while ((behav != null) && (theparm != null)) {
					if (behav.length() > 0) {
						theclasses.addElement(behav);
						String t = theparm;
						t = CMStrings.replaceAll(t, "\"", "&quot;");
						theparms.addElement(t);
					}
					num++;
					behav = httpReq.getUrlParameter("AFFECT" + num);
					theparm = httpReq.getUrlParameter("ADATA" + num);
				}
			} else
				for (int a = 0; a < P.numEffects(); a++) // personal effects
				{
					Ability Able = P.fetchEffect(a);
					if ((Able != null) && (Able.isSavable())) {
						theclasses.addElement(CMClass.classID(Able));
						String t = Able.text();
						t = CMStrings.replaceAll(t, "\"", "&quot;");
						theparms.addElement(t);
					}
				}
			str.append("<TABLE WIDTH=100% BORDER=\"" + borderSize
					+ "\" CELLSPACING=0 CELLPADDING=0>");
			HashSet<String> alreadyHave = new HashSet<String>();
			for (int i = 0; i < theclasses.size(); i++) {
				String theclass = (String) theclasses.elementAt(i);
				alreadyHave.add(theclass.toLowerCase());
				String theparm = (String) theparms.elementAt(i);
				str.append("<TR><TD WIDTH=50%>");
				str.append("<SELECT ONCHANGE=\"EditAffect(this);\" NAME=AFFECT"
						+ (i + 1) + ">");
				str.append("<OPTION VALUE=\"\">Delete!");
				str.append("<OPTION VALUE=\"" + theclass + "\" SELECTED>"
						+ theclass);
				str.append("</SELECT>");
				str.append("</TD><TD WIDTH=50%>");
				str.append("<INPUT TYPE=TEXT SIZE=30 NAME=ADATA" + (i + 1)
						+ " VALUE=\"" + theparm + "\">");
				str.append("</TD></TR>");
			}
			str.append("<TR><TD WIDTH=50%>");
			str.append("<SELECT ONCHANGE=\"AddAffect(this);\" NAME=AFFECT"
					+ (theclasses.size() + 1) + ">");
			str.append("<OPTION SELECTED VALUE=\"\">Select an Effect");
			for (Enumeration<Ability> a = CMClass.abilities(); a
					.hasMoreElements();) {
				Ability A = a.nextElement();
				if (((!A.canAffect(P)) || (alreadyHave.contains(A.ID()
						.toLowerCase())))
						|| ((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_ARCHON))
					continue;
				str.append("<OPTION VALUE=\"" + A.ID() + "\">" + A.ID());
			}
			str.append("</SELECT>");
			str.append("</TD><TD WIDTH=50%>");
			str.append("<INPUT TYPE=TEXT SIZE=30 NAME=ADATA"
					+ (theclasses.size() + 1) + " VALUE=\"\">");
			str.append("</TD></TR>");
			str.append("</TABLE>");
		}
		return str;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return CMProps.getVar(CMProps.Str.MUDSTATUS);

		java.util.Map<String, String> parms = parseParms(parm);
		if (parms.containsKey("AREATYPES")) {
			StringBuffer str = new StringBuffer("");
			for (Enumeration e = CMClass.areaTypes(); e.hasMoreElements();) {
				Area A = (Area) e.nextElement();
				str.append("<OPTION VALUE=\"" + A.ID() + "\">" + A.ID());
			}
			return str.toString();
		}
		if (parms.containsKey("AREAISGRID")) {
			Area A = CMLib.map().getArea("" + parms.get("AREAISGRID"));
			return "" + (A instanceof GridZones);
		}
		String last = httpReq.getUrlParameter("AREA");
		if (last == null)
			return " @break@";

		if (last.length() > 0) {
			Area A = CMLib.map().getArea(last);
			if (A != null) {
				StringBuffer str = new StringBuffer("");
				if (parms.containsKey("HELP")) {
					StringBuilder s = CMLib.help().getHelpText(
							"AREA_" + A.Name(), null, false);
					if (s == null)
						s = CMLib.help().getHelpText("AREAHELP_" + A.Name(),
								null, false);
					int limit = 78;
					if (parms.containsKey("LIMIT"))
						limit = CMath.s_int(parms.get("LIMIT"));
					str.append(helpHelp(s, limit));
				}
				if (parms.containsKey("CLIMATES")) {
					int climate = A.getClimateTypeCode();
					if (httpReq.isUrlParameter("CLIMATE")) {
						climate = CMath.s_int(httpReq
								.getUrlParameter("CLIMATE"));
						if (climate >= 0)
							for (int i = 1;; i++) {
								if (httpReq.isUrlParameter("CLIMATE"
										+ (Integer.toString(i)))) {
									int newVal = CMath.s_int(httpReq
											.getUrlParameter("CLIMATE"
													+ (Integer.toString(i))));
									if (newVal < 0) {
										climate = -1;
										break;
									}
									climate = climate | newVal;
								} else
									break;
							}
					}
					str.append("<OPTION VALUE=-1 "
							+ ((climate < 0) ? "SELECTED" : "") + ">Inherited");
					for (int i = 1; i < Area.NUM_CLIMATES; i++) {
						String climstr = Area.CLIMATE_DESCS[i];
						int mask = (int) CMath.pow(2, i - 1);
						str.append("<OPTION VALUE=" + mask);
						if ((climate >= 0) && ((climate & mask) > 0))
							str.append(" SELECTED");
						str.append(">" + climstr);
					}
				}
				if (parms.containsKey("ATMOSPHERE")) {
					String atmoVal = httpReq.getUrlParameter("ATMOSPHERE");
					if ((atmoVal == null) || (!CMath.isNumber(atmoVal)))
						atmoVal = "" + A.getAtmosphereCode();
					int atmo = CMath.s_int(atmoVal);
					str.append("<OPTION VALUE=\"-1\"")
							.append((atmo < 0) ? "SELECTED" : "")
							.append(">Inherited");
					for (int r : RawMaterial.CODES.ALL_SBN()) {
						str.append("<OPTION VALUE=\"" + r + "\"");
						if (r == atmo)
							str.append(" SELECTED");
						str.append(">" + RawMaterial.CODES.NAME(r));
					}
				}
				if (parms.containsKey("THEME")) {
					String level = httpReq.getUrlParameter("THEME");
					if ((level == null) || (level.length() == 0))
						level = "" + A.getThemeCode();
					for (int i = 1; i < Area.THEME_PHRASE.length; i++) {
						str.append("<OPTION VALUE=" + i);
						if (i == CMath.s_int(level))
							str.append(" SELECTED");
						str.append(">" + Area.THEME_PHRASE[i]);
					}
				}
				if (parms.containsKey("NAME")) {
					String name = httpReq.getUrlParameter("NAME");
					if ((name == null) || (name.length() == 0))
						name = A.Name();
					str.append(name);
				}
				if (parms.containsKey("IMAGE")) {
					String name = httpReq.getUrlParameter("IMAGE");
					if ((name == null) || (name.length() == 0))
						name = A.rawImage();
					str.append(name);
				}
				if ((parms.containsKey("GRIDX")) && (A instanceof GridZones)) {
					String name = httpReq.getUrlParameter("GRIDX");
					if ((name == null) || (name.length() == 0))
						name = "" + ((GridZones) A).xGridSize();
					str.append(name);
				}
				if ((parms.containsKey("GRIDY")) && (A instanceof GridZones)) {
					String name = httpReq.getUrlParameter("GRIDY");
					if ((name == null) || (name.length() == 0))
						name = "" + ((GridZones) A).yGridSize();
					str.append(name);
				}
				if (parms.containsKey("ISGRID"))
					str.append("" + (A instanceof GridZones));
				if (parms.containsKey("ISAUTOGEN"))
					str.append("" + (A instanceof AutoGenArea));
				if (parms.containsKey("AUTHOR")) {
					String author = httpReq.getUrlParameter("AUTHOR");
					if ((author == null) || (author.length() == 0))
						author = A.getAuthorID();
					str.append(author);
				}
				if (parms.containsKey("ARCHP")) {
					String path = httpReq.getUrlParameter("ARCHP");
					if ((path == null) || (path.length() == 0))
						path = A.getArchivePath();
					str.append(path);
				}
				if (parms.containsKey("CURRENCIES")) {
					str.append("<OPTION VALUE=\"\"");
					if (A.getCurrency().length() == 0)
						str.append(" SELECTED");
					str.append(">Default Currency");
					for (int i = 1; i < CMLib.beanCounter().getAllCurrencies()
							.size(); i++) {
						String s = CMLib.beanCounter().getAllCurrencies()
								.get(i);
						if (s.length() > 0) {
							str.append("<OPTION VALUE=\"" + s + "\"");
							if (s.equalsIgnoreCase(A.getCurrency()))
								str.append(" SELECTED");
							str.append(">" + s);
						}
					}
				}
				if (parms.containsKey("CURRENCY")) {
					String currency = httpReq.getUrlParameter("CURRENCY");
					if ((currency == null) || (currency.length() == 0))
						currency = A.getCurrency();
					str.append(currency);
				}
				if (parms.containsKey("SHOPPREJ")) {
					String val = httpReq.getUrlParameter("SHOPPREJ");
					if ((val == null) || (val.length() == 0))
						val = A.prejudiceFactors();
					str.append(val);
				}
				if (parms.containsKey("BUDGET")) {
					String val = httpReq.getUrlParameter("BUDGET");
					if ((val == null) || (val.length() == 0))
						val = A.budget();
					str.append(val);
				}
				if (parms.containsKey("DEVALRATE")) {
					String val = httpReq.getUrlParameter("DEVALRATE");
					if ((val == null) || (val.length() == 0))
						val = A.devalueRate();
					str.append(val);
				}
				if (parms.containsKey("INVRESETRATE")) {
					String val = httpReq.getUrlParameter("INVRESETRATE");
					if ((val == null) || (val.length() == 0))
						val = A.invResetRate() + "";
					str.append(val);
				}
				if (parms.containsKey("IGNOREMASK")) {
					String val = httpReq.getUrlParameter("IGNOREMASK");
					if ((val == null) || (val.length() == 0))
						val = A.ignoreMask();
					str.append(val);
				}
				if (parms.containsKey("PRICEFACTORS"))
					str.append(MobData.priceFactors(A, httpReq, parms, 0));
				if (parms.containsKey("CLASSES")) {
					String className = httpReq.getUrlParameter("CLASS");
					if ((className == null) || (className.length() == 0))
						className = CMClass.classID(A);
					Object[] sortedA = (Object[]) Resources
							.getResource("MUDGRINDER-AREAS");
					if (sortedA == null) {
						Vector sortMeA = new Vector();
						for (Enumeration a = CMClass.areaTypes(); a
								.hasMoreElements();)
							sortMeA.addElement(CMClass.classID(a.nextElement()));
						sortedA = (new TreeSet(sortMeA)).toArray();
						Resources.submitResource("MUDGRINDER-AREAS", sortedA);
					}
					for (int r = 0; r < sortedA.length; r++) {
						String cnam = (String) sortedA[r];
						str.append("<OPTION VALUE=\"" + cnam + "\"");
						if (className.equals(cnam))
							str.append(" SELECTED");
						str.append(">" + cnam);
					}
				}

				if (parms.containsKey("BLURBS")) {
					Vector theprices = new Vector();
					Vector themasks = new Vector();
					int num = 1;
					if (!httpReq.isUrlParameter("IPRIC" + num)) {
						for (Enumeration<String> f = A.areaBlurbFlags(); f
								.hasMoreElements();) {
							String flag = f.nextElement();
							theprices.addElement(flag);
							themasks.addElement(A.getBlurbFlag(flag));
						}
					} else
						while (httpReq.isUrlParameter("BLURBFLAG" + num)) {
							String PRICE = httpReq.getUrlParameter("BLURBFLAG"
									+ num);
							String MASK = httpReq
									.getUrlParameter("BLURB" + num);
							if ((PRICE != null) && (PRICE.length() > 0)) {
								theprices.addElement(PRICE);
								if (MASK != null)
									themasks.addElement(MASK);
								else
									themasks.addElement("");
							}
							num++;
						}
					str.append("<TABLE WIDTH=100% BORDER=\"1\" CELLSPACING=0 CELLPADDING=0>");
					str.append("<TR><TD WIDTH=20%>Flag</TD><TD>Description</TD></TR>");
					for (int i = 0; i < theprices.size(); i++) {
						String PRICE = (String) theprices.elementAt(i);
						String MASK = (String) themasks.elementAt(i);
						str.append("<TR><TD>");
						str.append("<INPUT TYPE=TEXT SIZE=5 NAME=BLURBFLAG"
								+ (i + 1) + " VALUE=\"" + PRICE + "\">");
						str.append("</TD><TD>");
						str.append("<INPUT TYPE=TEXT SIZE=50 NAME=BLURB"
								+ (i + 1) + " VALUE=\"" + MASK + "\">");
						str.append("</TD>");
						str.append("</TR>");
					}
					str.append("<TR><TD>");
					str.append("<INPUT TYPE=TEXT SIZE=5 NAME=BLURBFLAG"
							+ (theprices.size() + 1) + ">");
					str.append("</TD><TD>");
					str.append("<INPUT TYPE=TEXT SIZE=50 NAME=BLURB"
							+ (theprices.size() + 1) + ">");
					str.append("</TD></TR>");
					str.append("</TABLE>");

				}

				if (parms.containsKey("TESTSTUFF"))
					str.append(A.text());

				str.append(AreaData.affects(A, httpReq, parms, 1));
				str.append(AreaData.behaves(A, httpReq, parms, 1));

				if (parms.containsKey("SUBOPS")) {
					List<String> V = CMLib.database().getUserList();
					List<String> theSubOps = new LinkedList<String>();
					int num = 1;
					if (!httpReq.isUrlParameter("SUBOP" + num)) {
						List<String> subV = CMParms.parseSemicolons(
								A.getSubOpList(), true);
						for (String subOp : subV) {
							if (CMLib.players().playerExists(subOp))
								theSubOps.add(CMStrings
										.capitalizeAndLower(subOp));
						}
					} else
						while (httpReq.isUrlParameter("SUBOP" + num)) {
							String subOp = httpReq.getUrlParameter("SUBOP"
									+ num);
							if ((subOp != null) && (subOp.length() > 0)) {
								if (CMLib.players().playerExists(subOp))
									theSubOps.add(CMStrings
											.capitalizeAndLower(subOp));
							}
							num++;
						}
					str.append("<TABLE WIDTH=100% BORDER=\"1\" CELLSPACING=0 CELLPADDING=0>\n");
					int i = 0;
					for (String subOp : theSubOps) {
						str.append("<TR><TD>");
						str.append("<SELECT  ONCHANGE=\"EditSubOp(this);\" NAME=SUBOP"
								+ (i + 1) + " >");
						str.append("<OPTION VALUE=\"\" DELETE>Delete " + subOp);
						str.append("<OPTION VALUE=\"" + subOp + "\" SELECTED>"
								+ subOp);
						str.append("</SELECT></TD></TR>\n");
						i++;
					}
					str.append("<TR><TD>");
					str.append("<SELECT  ONCHANGE=\"AddSubOp(this);\" NAME=SUBOP"
							+ (i + 1) + " >");
					str.append("<OPTION VALUE=\"\" SELECTED>Select a Player");
					for (String subOp : V)
						if (!theSubOps.contains(subOp))
							str.append("<OPTION VALUE=\"" + subOp + "\">"
									+ subOp);
					str.append("</SELECT></TD></TR>\n");
					str.append("</TABLE>\n");
				}
				if (parms.containsKey("DESCRIPTION")) {
					String desc = httpReq.getUrlParameter("DESCRIPTION");
					if ((desc == null) || (desc.length() == 0))
						desc = A.description();
					str.append(desc);
				}

				if (parms.containsKey("PARENT")) {
					List<String> V = CMParms.toNameVector(CMLib.map().areas());
					List<String> theAreas = new LinkedList<String>();
					int num = 1;
					if (!httpReq.isUrlParameter("PARENT" + num)) {
						List<String> subV = CMParms
								.toNameVector(A.getParents());
						for (String areaName : subV) {
							Area findA = CMLib.map().getArea(areaName);
							if (findA != null)
								theAreas.add(findA.Name());
						}
					} else
						while (httpReq.isUrlParameter("PARENT" + num)) {
							String areaName = httpReq.getUrlParameter("PARENT"
									+ num);
							if ((areaName != null) && (areaName.length() > 0)) {
								Area findA = CMLib.map().getArea(areaName);
								if (findA != null)
									theAreas.add(findA.Name());
							}
							num++;
						}
					str.append("<TABLE WIDTH=100% BORDER=\"1\" CELLSPACING=0 CELLPADDING=0>\n");
					int i = 0;
					for (String areaName : theAreas) {
						str.append("<TR><TD>");
						str.append("<SELECT  ONCHANGE=\"EditAreaParent(this);\" NAME=PARENT"
								+ (i + 1) + " >");
						str.append("<OPTION VALUE=\"\" DELETE>Delete "
								+ areaName);
						str.append("<OPTION VALUE=\"" + areaName
								+ "\" SELECTED>" + areaName);
						str.append("</SELECT></TD></TR>\n");
						i++;
					}
					str.append("<TR><TD>");
					str.append("<SELECT  ONCHANGE=\"AddAreaParent(this);\" NAME=PARENT"
							+ (i + 1) + " >");
					str.append("<OPTION VALUE=\"\" SELECTED>Select an Area");
					for (String areaName : V)
						if (!theAreas.contains(areaName))
							str.append("<OPTION VALUE=\"" + areaName + "\">"
									+ areaName);
					str.append("</SELECT></TD></TR>\n");
					str.append("</TABLE>\n");
				}

				if (parms.containsKey("CHILDREN")) {
					Area defaultParentArea = CMLib.map().getDefaultParentArea();
					List<String> V = CMParms.toNameVector(CMLib.map().areas());
					List<String> theAreas = new LinkedList<String>();
					int num = 1;
					if (!httpReq.isUrlParameter("CHILDREN" + num)) {
						List<String> subV = CMParms.toNameVector(A
								.getChildren());
						if (defaultParentArea != A)
							for (String areaName : subV) {
								Area findA = CMLib.map().getArea(areaName);
								if (findA != null)
									theAreas.add(findA.Name());
							}
					} else
						while (httpReq.isUrlParameter("CHILDREN" + num)) {
							String areaName = httpReq
									.getUrlParameter("CHILDREN" + num);
							if ((areaName != null) && (areaName.length() > 0)) {
								Area findA = CMLib.map().getArea(areaName);
								if (findA != null)
									theAreas.add(findA.Name());
							}
							num++;
						}
					str.append("<TABLE WIDTH=100% BORDER=\"1\" CELLSPACING=0 CELLPADDING=0>\n");
					int i = 0;
					for (String areaName : theAreas) {
						str.append("<TR><TD>");
						str.append("<SELECT  ONCHANGE=\"EditAreaChild(this);\" NAME=CHILDREN"
								+ (i + 1) + " >");
						str.append("<OPTION VALUE=\"\" DELETE>Delete "
								+ areaName);
						str.append("<OPTION VALUE=\"" + areaName
								+ "\" SELECTED>" + areaName);
						str.append("</SELECT></TD></TR>\n");
						i++;
					}
					str.append("<TR><TD>");
					str.append("<SELECT  ONCHANGE=\"AddAreaChild(this);\" NAME=CHILDREN"
							+ (i + 1) + " >");
					str.append("<OPTION VALUE=\"\" SELECTED>Select an Area");
					for (String areaName : V)
						if (!theAreas.contains(areaName))
							str.append("<OPTION VALUE=\"" + areaName + "\">"
									+ areaName);
					str.append("</SELECT></TD></TR>\n");
					str.append("</TABLE>\n");
				}

				if (A instanceof AutoGenArea) {
					AutoGenArea AG = (AutoGenArea) A;

					if (parms.containsKey("AGAUTOVAR")) {
						String value = httpReq.getUrlParameter("AGAUTOVAR");
						if ((value == null) || (value.length() == 0))
							value = CMParms.toStringEqList(AG
									.getAutoGenVariables());
						str.append(value);
					}
					if (parms.containsKey("AGXMLPATH")) {
						String value = httpReq.getUrlParameter("AGXMLPATH");
						if ((value == null) || (value.length() == 0))
							value = AG.getGeneratorXmlPath();
						str.append(value);
					}
				}

				if (parms.containsKey("SEASON"))
					str.append(CMStrings.removeColors(TimeClock.SEASON_DESCS[A
							.getTimeObj().getSeasonCode()]) + ", ");
				if (parms.containsKey("TODCODE"))
					str.append(CMStrings.removeColors(TimeClock.TOD_DESC[A
							.getTimeObj().getTODCode()]) + ", ");
				if (parms.containsKey("WEATHER"))
					str.append(CMStrings.removeColors(A.getClimateObj()
							.getWeatherDescription(A)) + ", ");
				if (parms.containsKey("MOON"))
					str.append(CMStrings.removeColors(TimeClock.MOON_PHASES[A
							.getTimeObj().getMoonPhase()]) + ", ");
				if (parms.containsKey("STATS"))
					str.append(A.getAreaStats() + ", ");
				String strstr = str.toString();
				if (strstr.endsWith(", "))
					strstr = strstr.substring(0, strstr.length() - 2);
				return clearWebMacros(strstr);
			}
		}
		return "";
	}
}