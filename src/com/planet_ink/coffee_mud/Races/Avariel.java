package com.planet_ink.coffee_mud.Races;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Armor;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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
public class Avariel extends StdRace {
	public String ID() {
		return "Avariel";
	}

	public String name() {
		return "Avariel";
	}

	public int shortestMale() {
		return 59;
	}

	public int shortestFemale() {
		return 59;
	}

	public int heightVariance() {
		return 12;
	}

	public int lightestWeight() {
		return 80;
	}

	public int weightVariance() {
		return 80;
	}

	public long forbiddenWornBits() {
		return 0;
	}

	public String racialCategory() {
		return "Elf";
	}

	private String[] culturalAbilityNames = { "Elvish" };
	private int[] culturalAbilityProficiencies = { 75 };

	public String[] culturalAbilityNames() {
		return culturalAbilityNames;
	}

	public int[] culturalAbilityProficiencies() {
		return culturalAbilityProficiencies;
	}

	private String[] racialAbilityNames = { "WingFlying" };
	private int[] racialAbilityLevels = { 1 };
	private int[] racialAbilityProficiencies = { 100 };
	private boolean[] racialAbilityQuals = { false };

	protected String[] racialAbilityNames() {
		return racialAbilityNames;
	}

	protected int[] racialAbilityLevels() {
		return racialAbilityLevels;
	}

	protected int[] racialAbilityProficiencies() {
		return racialAbilityProficiencies;
	}

	protected boolean[] racialAbilityQuals() {
		return racialAbilityQuals;
	}

	// an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts = { 0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1,
			1, 0, 2 };

	public int[] bodyMask() {
		return parts;
	}

	protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

	public int availabilityCode() {
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_SEE_INFRARED);
	}

	public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_DEXTERITY,
				affectableStats.getStat(CharStats.STAT_DEXTERITY) + 1);
		affectableStats.setStat(CharStats.STAT_MAX_DEXTERITY_ADJ,
				affectableStats.getStat(CharStats.STAT_MAX_DEXTERITY_ADJ) + 1);
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,
				affectableStats.getStat(CharStats.STAT_CONSTITUTION) - 1);
		affectableStats
				.setStat(CharStats.STAT_MAX_CONSTITUTION_ADJ, affectableStats
						.getStat(CharStats.STAT_MAX_CONSTITUTION_ADJ) - 1);
		affectableStats.setStat(CharStats.STAT_SAVE_MAGIC,
				affectableStats.getStat(CharStats.STAT_SAVE_MAGIC) + 10);
	}

	public String makeMobName(char gender, int age) {
		switch (age) {
		case Race.AGE_INFANT:
		case Race.AGE_TODDLER:
			return name().toLowerCase() + " chick";
		case Race.AGE_CHILD:
			return "young " + name().toLowerCase();
		default:
			return super.makeMobName(gender, age);
		}
	}

	public List<Item> outfit(MOB myChar) {
		if (outfitChoices == null) {
			outfitChoices = new Vector();
			// Have to, since it requires use of special constructor
			Armor s1 = CMClass.getArmor("GenShirt");
			s1.setName("a delicate green shirt");
			s1.setDisplayText("a delicate green shirt sits gracefully here.");
			s1.setDescription("Obviously fine craftmenship, with sharp folds and intricate designs.");
			s1.text();
			outfitChoices.add(s1);

			Armor s2 = CMClass.getArmor("GenShoes");
			s2.setName("a pair of sandals");
			s2.setDisplayText("a pair of sandals lie here.");
			s2.setDescription("Obviously fine craftmenship, these light leather sandals have tiny woodland drawings in them.");
			s2.text();
			outfitChoices.add(s2);

			Armor p1 = CMClass.getArmor("GenPants");
			p1.setName("some delicate leggings");
			p1.setDisplayText("a pair delicate brown leggings sit here.");
			p1.setDescription("Obviously fine craftmenship, with sharp folds and intricate designs.  They look perfect for dancing in!");
			p1.text();
			outfitChoices.add(p1);

			Armor s3 = CMClass.getArmor("GenBelt");
			outfitChoices.add(s3);
		}
		return outfitChoices;
	}

	public Weapon myNaturalWeapon() {
		return funHumanoidWeapon();
	}

	public String healthText(MOB viewer, MOB mob) {
		double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState()
				.getHitPoints()));

		if (pct < .10)
			return "^r" + mob.name(viewer) + "^r is facing mortality!^N";
		else if (pct < .20)
			return "^r" + mob.name(viewer) + "^r is covered in blood.^N";
		else if (pct < .30)
			return "^r" + mob.name(viewer)
					+ "^r is bleeding badly from lots of wounds.^N";
		else if (pct < .40)
			return "^y" + mob.name(viewer)
					+ "^y has numerous bloody wounds and gashes.^N";
		else if (pct < .50)
			return "^y" + mob.name(viewer)
					+ "^y has some bloody wounds and gashes.^N";
		else if (pct < .60)
			return "^p" + mob.name(viewer) + "^p has a few bloody wounds.^N";
		else if (pct < .70)
			return "^p" + mob.name(viewer) + "^p is cut and bruised.^N";
		else if (pct < .80)
			return "^g" + mob.name(viewer)
					+ "^g has some minor cuts and bruises.^N";
		else if (pct < .90)
			return "^g" + mob.name(viewer)
					+ "^g has a few bruises and scratches.^N";
		else if (pct < .99)
			return "^g" + mob.name(viewer) + "^g has a few small bruises.^N";
		else
			return "^c" + mob.name(viewer) + "^c is in perfect health.^N";
	}

	public List<RawMaterial> myResources() {
		synchronized (resources) {
			if (resources.size() == 0) {
				resources.addElement(makeResource("a pair of "
						+ name().toLowerCase() + " ears",
						RawMaterial.RESOURCE_MEAT));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " feathers",
						RawMaterial.RESOURCE_FEATHERS));
				resources.addElement(makeResource("some "
						+ name().toLowerCase() + " blood",
						RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource("a pile of "
						+ name().toLowerCase() + " bones",
						RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
