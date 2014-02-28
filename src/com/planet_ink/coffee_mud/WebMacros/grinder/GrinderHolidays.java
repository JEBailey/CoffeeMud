package com.planet_ink.coffee_mud.WebMacros.grinder;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Libraries.interfaces.QuestManager;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.DVector;
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
public class GrinderHolidays {
	public String name() {
		return "GrinderHolidays";
	}

	protected static String setText(DVector sets, String var, String newVAL) {
		if (newVAL == null)
			newVAL = "";
		// var=var.toUpperCase().trim();
		int index = sets.indexOf(var);
		String oldVal = index >= 0 ? (String) sets.elementAt(index, 2) : "";
		if (index >= 0) {
			if (!newVAL.equals(oldVal))
				sets.setElementAt(index, 2, newVAL);
		} else
			sets.addElement(var, newVAL, Integer.valueOf(-1));
		return newVAL;
	}

	public static String createModifyHoliday(HTTPRequest httpReq,
			java.util.Map<String, String> parms, String holidayName) {
		int index = CMLib.quests().getHolidayIndex(holidayName);
		if (index <= 0) {
			String err = CMLib.quests().createHoliday(holidayName, "ALL", true);
			if ((err != null) && (err.trim().length() > 0))
				return err;
			index = CMLib.quests().getHolidayIndex(holidayName);
			if (index < 0)
				return "Error creating holiday file.";
		}
		List<String> steps = null;
		QuestManager.RawHolidayData encodedData = null;
		Object resp = CMLib.quests().getHolidayFile();
		if (resp instanceof List)
			steps = (List<String>) resp;
		else if (resp instanceof String)
			return (String) resp;
		if (steps != null)
			encodedData = CMLib.quests()
					.getEncodedHolidayData(steps.get(index));
		if ((encodedData == null) || (steps == null))
			return "Error reading holiday data (code: "
					+ ((resp instanceof List) ? "T" : "F") + ":"
					+ ((steps == null) ? "F" : "T") + ":"
					+ ((encodedData == null) ? "F" : "T") + ")";
		DVector settings = encodedData.settings;
		DVector behaviors = encodedData.behaviors;
		DVector properties = encodedData.properties;
		DVector stats = encodedData.stats;
		// List stepV=(List)encodedData.elementAt(4);
		// int pricingMobIndex=((Integer)encodedData.elementAt(5)).intValue();

		String name = setText(settings, "NAME", httpReq.getUrlParameter("NAME"));
		if ((name == null) || (name.trim().length() == 0))
			return "A name is required.";

		String duration = setText(settings, "DURATION",
				httpReq.getUrlParameter("DURATION"));
		if ((duration == null) || (!CMath.isMathExpression(duration)))
			return "Duration is mal-formed.";

		if (!httpReq.isUrlParameter("SCHEDULETYPE"))
			return "Schedule not found.";
		int typeIndex = CMath.s_int(httpReq.getUrlParameter("SCHEDULETYPE"));
		int mudDayIndex = settings.indexOf("MUDDAY");
		int dateIndex = settings.indexOf("DATE");
		int waitIndex = settings.indexOf("WAIT");
		String scheduleName = new String[] { "WAIT", "MUDDAY", "DATE" }[typeIndex];
		if ((typeIndex != 0) && (waitIndex >= 0))
			settings.removeElement("WAIT");
		if ((typeIndex != 1) && (mudDayIndex >= 0))
			settings.removeElement("MUDDAY");
		if ((typeIndex != 2) && (dateIndex >= 0))
			settings.removeElement("DATE");
		String newWait = setText(settings, scheduleName,
				httpReq.getUrlParameter(scheduleName));
		switch (typeIndex) {
		case 0: {
			if (!CMath.isMathExpression(newWait))
				return "Wait expression is invalid.";
			break;
		}
		case 1:
		case 2: {
			int dash = newWait.indexOf('-');
			if (dash < 0)
				return "Given date is invalid. Use Month#-Day# format";
			if (!CMath.isInteger(newWait.substring(0, dash).trim()))
				return "Month value in the given date is not valid.";
			if (!CMath.isInteger(newWait.substring(dash + 1).trim()))
				return "Day value in the given date is not valid.";
			break;
		}
		}

		StringBuffer areaGroup = new StringBuffer("");
		HashSet areaCodes = new HashSet();
		String id = "";
		for (int i = 0; httpReq.isUrlParameter("AREAGROUP" + id); id = Integer
				.toString(++i))
			areaCodes.add(httpReq.getUrlParameter("AREAGROUP" + id));
		if (areaCodes.contains("AREAGROUP1"))
			areaGroup.append("ANY");
		else {
			int areaNum = 2;
			boolean reallyAll = true;
			for (Enumeration e = CMLib.map().areas(); e.hasMoreElements(); areaNum++)
				if (areaCodes.contains("AREAGROUP" + areaNum))
					areaGroup.append(" \"" + ((Area) e.nextElement()).Name()
							+ "\"");
				else {
					reallyAll = false;
					e.nextElement();
				}
			if (reallyAll)
				areaGroup.setLength(0);
		}

		setText(settings, "AREAGROUP", areaGroup.toString().trim());
		setText(settings, "MOBGROUP", httpReq.getUrlParameter("MOBGROUP"));

		behaviors.clear();
		setText(behaviors, "AGGRESSIVE", httpReq.getUrlParameter("AGGRESSIVE"));
		for (int i = 1; httpReq.isUrlParameter("BEHAV" + i); i++)
			if (httpReq.getUrlParameter("BEHAV" + i).trim().length() > 0)
				setText(behaviors, httpReq.getUrlParameter("BEHAV" + i),
						httpReq.getUrlParameter("BDATA" + i));
		StringBuffer mudChats = new StringBuffer("");
		for (int i = 1; httpReq.isUrlParameter("MCWDS" + i); i++) {
			String words = httpReq.getUrlParameter("MCWDS" + i).trim();
			words = CMStrings.replaceAll(words, ",", "|");
			if ((words.length() > 0)
					&& (httpReq.isUrlParameter("MCSAYS" + i + "_1"))) {
				mudChats.append("(" + words + ");");
				for (int ii = 1; httpReq
						.isUrlParameter("MCSAYW" + i + "_" + ii); ii++)
					if (CMath.isInteger(httpReq.getUrlParameter("MCSAYW" + i
							+ "_" + ii)))
						mudChats.append(httpReq.getUrlParameter("MCSAYW" + i
								+ "_" + ii)
								+ httpReq.getUrlParameter("MCSAYS" + i + "_"
										+ ii) + ";");
				mudChats.append(";");
			}
		}
		setText(behaviors, "MUDCHAT", mudChats.toString());

		properties.clear();
		setText(properties, "MOOD", httpReq.getUrlParameter("MOOD"));
		for (int i = 1; httpReq.isUrlParameter("AFFECT" + i); i++)
			if (httpReq.getUrlParameter("AFFECT" + i).trim().length() > 0)
				setText(properties, httpReq.getUrlParameter("AFFECT" + i),
						httpReq.getUrlParameter("ADATA" + i));

		Vector priceFV = new Vector();
		for (int i = 1; httpReq.isUrlParameter("PRCFAC" + i); i++)
			if (CMath.isPct(httpReq.getUrlParameter("PRCFAC" + i).trim()))
				priceFV.add((CMath.s_pct(httpReq.getUrlParameter("PRCFAC" + i)
						.trim()) + " " + httpReq.getUrlParameter("PMASK" + i)
						.trim()).trim());
		setText(stats, "PRICEMASKS", CMParms.toStringList(priceFV));

		String err = CMLib.quests().alterHoliday(holidayName, encodedData);
		if (err.length() == 0)
			httpReq.addFakeUrlParameter("HOLIDAY", name);
		return err;
	}
}
