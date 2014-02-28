package com.planet_ink.coffee_mud.Abilities.Properties;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Social;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Libraries.interfaces.MaskingLibrary;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.Resources;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class Prop_Socials extends Property {
	public String ID() {
		return "Prop_Socials";
	}

	public String name() {
		return "Local Social creating property";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_ROOMS
				| Ability.CAN_EXITS | Ability.CAN_AREAS;
	}

	protected final Map<String, List<Social>> socials = new TreeMap<String, List<Social>>();

	protected boolean wornOnly = false;
	protected MaskingLibrary.CompiledZapperMask mask = null;

	public void setMiscText(String newText) {
		super.setMiscText(newText);
		socials.clear();
		wornOnly = false;
		mask = null;
		List<String> socialsV = CMParms.parseAny(newText, ';', false);
		List<String> lines = new Vector<String>();
		for (String social : socialsV) {
			boolean forgive = false;
			String load = CMParms.getParmStr(social, "LOAD", "");
			String maskStr = CMParms.getParmStr(social, "MASK", "");
			String wornonly = CMParms.getParmStr(social, "WORNONLY", "");
			if (load.length() > 0) {
				List<String> flines = Resources.getFileLineVector(Resources
						.getFileResource(social.substring(4).trim()
								.substring(1).trim(), true));
				if ((flines != null) && (flines.size() > 0))
					lines.addAll(flines);
				forgive = true;
			}
			if (maskStr.length() > 0) {
				mask = CMLib.masking().getPreCompiledMask(maskStr);
				forgive = true;
			}
			if (wornonly.length() > 0) {
				wornOnly = CMath.s_bool(wornonly)
						|| wornonly.equalsIgnoreCase("T")
						|| wornonly.toUpperCase().startsWith("Y");
				forgive = true;
			}

			String name = CMParms.getParmStr(social, "NAME", "");
			String target = CMParms.getParmStr(social, "TARGET", "");
			String srcCode = CMParms.getParmStr(social, "SRCCODE",
					CMParms.getParmStr(social, "SOURCECODE", "M"));
			String othCode = CMParms.getParmStr(social, "OTHCODE", CMParms
					.getParmStr(social, "TGTCODE", CMParms.getParmStr(social,
							"OTHERCODE",
							CMParms.getParmStr(social, "TARGETCODE", "V"))));
			String youSee = CMParms.getParmStr(social, "YOUSEE", "");
			String othersSee = CMParms.getParmStr(
					social,
					"OTHSEE",
					CMParms.getParmStr(social, "OTHERSEE",
							CMParms.getParmStr(social, "OTHERSSEE", "")));
			String targetSee = CMParms.getParmStr(
					social,
					"TARGSEE",
					CMParms.getParmStr(social, "TARGETSEE",
							CMParms.getParmStr(social, "TGTSEE", "")));
			String seeNoTargetSee = CMParms.getParmStr(
					social,
					"NOTARGSEE",
					CMParms.getParmStr(social, "NOTARGETSEE",
							CMParms.getParmStr(social, "NOTGTSEE", "")));
			String mspFile = CMParms.getParmStr(social, "MSPFILE", "");
			if (name.length() == 0) {
				if (!forgive)
					Log.errOut("Prop_Socials", "NAME not found in: " + social);
			} else if ((srcCode.length() == 0) || (srcCode.length() > 1)) {
				if (!forgive)
					Log.errOut("Prop_Socials", "Bad SRCCODE letter '" + srcCode
							+ "': " + social);
			} else if ((othCode.length() == 0) || (othCode.length() > 1)) {
				if (!forgive)
					Log.errOut("Prop_Socials", "Bad OTHCODE letter '" + othCode
							+ "': " + social);
			} else if (youSee.length() == 0) {
				if (!forgive)
					Log.errOut("Prop_Socials", "Missing YOUSEE: " + social);
			} else {
				StringBuilder tabLine = new StringBuilder("");
				tabLine.append(srcCode).append(othCode).append("\t");
				tabLine.append(name.toUpperCase().trim());
				if (target.trim().length() > 0)
					tabLine.append(" ").append(target.toUpperCase().trim());
				tabLine.append("\t");
				tabLine.append(youSee).append("\t");
				tabLine.append(othersSee).append("\t");
				tabLine.append(targetSee).append("\t");
				tabLine.append(seeNoTargetSee).append("\t");
				tabLine.append(mspFile);
				lines.add(tabLine.toString());
			}
		}
		CMLib.socials().putSocialsInHash(socials, lines);
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (affected == null)
			return true;
		if ((msg.targetMinor() == CMMsg.TYP_HUH)
				&& (socials.size() > 0)
				&& (msg.targetMessage() != null)
				&& ((mask == null) || (CMLib.masking().maskCheck(mask,
						msg.source(), true)))
				&& ((!(affected instanceof Item)) || ((msg.source() == ((Item) affected)
						.owner()) && ((!wornOnly) || (!((Item) affected)
						.amWearingAt(Wearable.IN_INVENTORY)))))) {
			Vector<String> V = CMParms.parse(msg.targetMessage());
			Social S = CMLib.socials().fetchSocialFromSet(socials, V, true,
					true);
			if (S == null)
				S = CMLib.socials().fetchSocialFromSet(socials, V, false, true);
			if (S != null) {
				S.invoke(msg.source(), V, null, false);
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

}
