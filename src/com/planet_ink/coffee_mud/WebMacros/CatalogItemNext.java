package com.planet_ink.coffee_mud.WebMacros;

import java.util.Arrays;
import java.util.Comparator;

import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Libraries.interfaces.CatalogLibrary;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
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
public class CatalogItemNext extends StdWebMacro {
	public String name() {
		return "CatalogItemNext";
	}

	public boolean isAdminMacro() {
		return true;
	}

	static final String[] DATA = { "CATALOG_ITEM_NAME", "CATALOG_ITEM_USAGE",
			"CATALOG_ITEM_LEVEL", "CATALOG_ITEM_CLASS", "CATALOG_ITEM_VALUE",
			"CATALOG_ITEM_RATE", "CATALOG_ITEM_MASK", "CATALOG_ITEM_LIVE",
			"CATALOG_ITEM_AREA", };

	public static String getCataStat(Item I, CatalogLibrary.CataData data,
			int x, String optionalColumn) {
		if ((I == null) || (data == null))
			return "";
		boolean dataRate = (data.getRate() > 0.0);
		switch (x) {
		case 0:
			return I.Name();
		case 1:
			return "" + data.numReferences();
		case 2:
			return "" + I.basePhyStats().level();
		case 3:
			return I.ID();
		case 4:
			return "" + I.baseGoldValue();
		case 5:
			return (dataRate) ? CMath.toPct(data.getRate()) : "";
		case 6:
			return (dataRate) ? (data.getMaskStr() == null ? "" : data
					.getMaskStr()) : "";
		case 7:
			return (dataRate) ? ("" + data.getWhenLive()) : "";
		case 8:
			return "" + data.mostPopularArea();
		default:
			if ((optionalColumn != null) && (optionalColumn.length() > 0)) {
				if (I.isStat(optionalColumn))
					return I.getStat(optionalColumn);
				if (I.basePhyStats().isStat(optionalColumn))
					return I.basePhyStats().getStat(optionalColumn);
			}
			break;
		}
		return "";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("ITEM");
		String catagory = httpReq.getUrlParameter("CATACAT");
		if (catagory != null) {
			if (catagory.equalsIgnoreCase("UNCATEGORIZED"))
				catagory = "";
			else if (catagory.length() == 0)
				catagory = null;
			else
				catagory = catagory.toUpperCase().trim();
		}
		String optCol = httpReq.getUrlParameter("OPTIONALCOLUMN");
		final String optionalColumn;
		if (optCol == null)
			optionalColumn = "";
		else
			optionalColumn = optCol.trim().toUpperCase();
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("ITEM");
			for (int d = 0; d < DATA.length; d++)
				httpReq.removeUrlParameter(DATA[d]);
			if (optionalColumn.length() > 0)
				httpReq.removeUrlParameter("CATALOG_ITEM_" + optionalColumn);
			return "";
		}
		String lastID = "";
		Item I = null;
		String name = null;
		CatalogLibrary.CataData data = null;
		String[] names = CMLib.catalog().getCatalogItemNames(catagory);
		String sortBy = httpReq.getUrlParameter("SORTBY");
		if ((sortBy != null) && (sortBy.length() > 0)) {
			String[] sortedNames = (String[]) httpReq.getRequestObjects().get(
					"CATALOG_ITEM_" + catagory + "_" + sortBy.toUpperCase());
			if (sortedNames != null)
				names = sortedNames;
			else {
				final int sortIndex = CMParms.indexOf(DATA, "CATALOG_ITEM_"
						+ sortBy.toUpperCase());
				if ((sortIndex >= 0)
						|| (sortBy.equalsIgnoreCase(optionalColumn))) {
					Object[] sortifiable = new Object[names.length];
					for (int s = 0; s < names.length; s++)
						sortifiable[s] = new Object[] { names[s],
								CMLib.catalog().getCatalogItem(names[s]),
								CMLib.catalog().getCatalogItemData(names[s]) };
					Arrays.sort(sortifiable, new Comparator() {
						public int compare(Object o1, Object o2) {
							Object[] O1 = (Object[]) o1;
							Object[] O2 = (Object[]) o2;
							String s1 = getCataStat((Item) O1[1],
									(CatalogLibrary.CataData) O1[2], sortIndex,
									optionalColumn);
							String s2 = getCataStat((Item) O2[1],
									(CatalogLibrary.CataData) O2[2], sortIndex,
									optionalColumn);
							if (CMath.isNumber(s1) && CMath.isNumber(s2))
								return Double.valueOf(CMath.s_double(s1))
										.compareTo(
												Double.valueOf(CMath
														.s_double(s2)));
							else
								return s1.toLowerCase().compareTo(
										s2.toLowerCase());
						}
					});
					for (int s = 0; s < names.length; s++)
						names[s] = (String) ((Object[]) sortifiable[s])[0];
					httpReq.getRequestObjects().put(
							"CATALOG_ITEM_" + catagory + "_"
									+ sortBy.toUpperCase(), names);
				}
			}
		}
		for (int s = 0; s < names.length; s++) {
			name = "CATALOG-" + names[s].toUpperCase().trim();
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!name
							.equalsIgnoreCase(lastID)))) {
				data = CMLib.catalog().getCatalogItemData(names[s]);
				I = CMLib.catalog().getCatalogItem(names[s]);
				if (I == null)
					continue;
				httpReq.addFakeUrlParameter("ITEM", name);
				for (int d = 0; d < DATA.length; d++)
					httpReq.addFakeUrlParameter(DATA[d],
							getCataStat(I, data, d, null));
				if (optionalColumn.length() > 0)
					httpReq.addFakeUrlParameter("CATALOG_ITEM_"
							+ optionalColumn,
							getCataStat(I, data, -1, optionalColumn));
				return "";
			}
			lastID = name;
		}
		httpReq.addFakeUrlParameter("ITEM", "");
		for (int d = 0; d < DATA.length; d++)
			httpReq.addFakeUrlParameter(DATA[d], "");
		if (optionalColumn.length() > 0)
			httpReq.addFakeUrlParameter("CATALOG_ITEM_" + optionalColumn, "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
