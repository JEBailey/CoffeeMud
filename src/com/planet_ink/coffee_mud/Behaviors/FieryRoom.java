package com.planet_ink.coffee_mud.Behaviors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

/**
 * <p>
 * Title: False Realities Presents FieryRoom
 * </p>
 * <p>
 * Description: False Realities - Discover your true destiny and change
 * history...
 * </p>
 * <p>
 * Company: http://www.falserealities.com
 * </p>
 * 
 * @author Tulath (a.k.a.) Jeremy Vyska
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class FieryRoom extends ActiveTicker {
	public String ID() {
		return "FieryRoom";
	}

	protected int canImproveCode() {
		return Behavior.CAN_ROOMS;
	}

	private String newDisplay = "";
	private String newDesc = "";
	private int directDamage = 10;
	private int eqChance = 0;
	private int burnTicks = 12;
	private boolean noStop = false;
	private boolean noNpc = false;
	private boolean noFireText = false;

	private String[] FireTexts = { "The fire here crackles and burns." };

	public FieryRoom() {
		super();
		minTicks = 5;
		maxTicks = 10;
		chance = 100;
		tickReset();
	}

	public String accountForYourself() {
		return "on fire";
	}

	public void setParms(String newParms) {
		super.setParms(newParms);
		newDisplay = CMParms.getParmStr(newParms, "Title", "A Charred Ruin");
		newDesc = CMParms.getParmStr(newParms, "Description",
				"Whatever was once here is now nothing more than ash.");
		directDamage = CMParms.getParmInt(newParms, "damage", 10);
		eqChance = CMParms.getParmInt(newParms, "eqchance", 0);
		burnTicks = CMParms.getParmInt(newParms, "burnticks", 12);
		Vector<String> V = CMParms.parse(newParms.toUpperCase());
		noStop = (V.contains("NOSTOP"));
		noNpc = (V.contains("NONPC"));
		noFireText = (V.contains("NOFIRETEXT"));
		setFireTexts();
	}

	private void setFireTexts() {
		String[] newFireTexts = {
				"The fire here crackles and burns.",
				"The intense heat of the fire here is "
						+ (directDamage > 0 ? "very painful"
								: "very unpleasant") + ".",
				"The flames dance around you"
						+ (eqChance > 0 ? ", licking at your clothes." : "."),
				"The fire is burning out of control. You fear for your safety"
						+ (noStop ? "."
								: " as it looks like this place is being completely consumed."),
				"You hear popping and sizzling as something burns.",
				"The smoke here is very thick and you worry about whether you will be able to breathe." };
		FireTexts = newFireTexts;
	}

	public boolean tick(Tickable ticking, int tickID) {
		super.tick(ticking, tickID);
		// on every tick, we may do damage OR eq handling.
		if (!(ticking instanceof Room))
			return super.tick(ticking, tickID);

		Room room = (Room) ticking;
		if (canAct(ticking, tickID)) {
			if ((directDamage > 0) || (eqChance > 0)) {
				// for each inhab, do directDamage to them.
				for (int i = 0; i < room.numInhabitants(); i++) {
					MOB inhab = room.fetchInhabitant(i);
					if (inhab == null)
						continue;
					if (inhab.isMonster()) {
						boolean reallyAffect = true;
						if (noNpc) {
							reallyAffect = false;
							Set<MOB> group = inhab
									.getGroupMembers(new HashSet<MOB>());
							for (Iterator e = group.iterator(); e.hasNext();) {
								MOB follower = (MOB) e.next();
								if (!(follower.isMonster())) {
									reallyAffect = true;
									break;
								}
							}
						}
						if (reallyAffect) {
							dealDamage(inhab);
							if (CMLib.dice().rollPercentage() > eqChance)
								eqRoast(inhab);
						}
					} else {
						if ((!CMSecurity.isAllowed(inhab, inhab.location(),
								CMSecurity.SecFlag.ORDER))
								&& (!CMSecurity.isAllowed(inhab,
										inhab.location(),
										CMSecurity.SecFlag.CMDROOMS))) {
							dealDamage(inhab);
							if (CMLib.dice().rollPercentage() > eqChance)
								eqRoast(inhab);
						}
					}
				}
			}
			// % chance of burning each item in the room.
			roastRoom(room);
			// The tick happened. If NOT NoFireText, Do flame emotes
			if (!noFireText) {
				String pickedText = FireTexts[CMLib.dice().roll(1,
						FireTexts.length, 0) - 1];
				room.showHappens(CMMsg.MSG_OK_ACTION, pickedText);
			}
		}
		if (!noStop) {
			if (burnTicks == 0) {
				// NOSTOP is false. This means the room gets set
				// to the torched text and the behavior goes away.
				room.setDisplayText(newDisplay);
				room.setDescription(newDesc);
				room.delBehavior(this);
			} else
				--burnTicks;
		}
		return super.tick(ticking, tickID);
	}

	private void dealDamage(MOB mob) {
		MOB M = CMLib.map().getFactoryMOB(mob.location());
		M.setName("fire");
		CMLib.combat().postDamage(M, mob, null, directDamage,
				CMMsg.MASK_ALWAYS | CMMsg.MASK_MALICIOUS | CMMsg.TYP_FIRE,
				Weapon.TYPE_BURNING, "The fire here <DAMAGE> <T-NAME>!");
		M.destroy();
	}

	private void eqRoast(MOB mob) {
		Item target = getSomething(mob);
		if (target != null) {
			MOB M = CMLib.map().getFactoryMOB(mob.location());
			M.setName("fire");
			switch (target.material() & RawMaterial.MATERIAL_MASK) {
			case RawMaterial.MATERIAL_GLASS:
			case RawMaterial.MATERIAL_METAL:
			case RawMaterial.MATERIAL_MITHRIL:
			case RawMaterial.MATERIAL_SYNTHETIC:
			case RawMaterial.MATERIAL_PRECIOUS:
			case RawMaterial.MATERIAL_ROCK:
			case RawMaterial.MATERIAL_UNKNOWN: {
				// all these we'll make get hot and be dropped.
				int damage = CMLib.dice().roll(1, 6, 1);
				CMLib.combat().postDamage(
						M,
						mob,
						null,
						damage,
						CMMsg.MASK_ALWAYS | CMMsg.MASK_MALICIOUS
								| CMMsg.TYP_FIRE, Weapon.TYPE_BURNING,
						target.name() + " <DAMAGE> <T-NAME>!");
				if (CMLib.dice().rollPercentage() < mob.charStats().getStat(
						CharStats.STAT_STRENGTH)) {
					CMLib.commands().postDrop(mob, target, false, false, false);
				}
				break;
			}
			default: {
				Ability burn = CMClass.getAbility("Burning");
				if (burn != null) {
					mob.location().showHappens(CMMsg.MSG_OK_ACTION,
							target.Name() + " begins to burn!");
					burn.invoke(M, target, true, 0);
					target.recoverPhyStats();
				}
				break;
			}
			}
			M.destroy();
		}
	}

	private static void roastRoom(Room which) {
		MOB mob = CMLib.map().getFactoryMOB(which);
		mob.setName("fire");
		for (int i = 0; i < which.numItems(); i++) {
			Item target = which.getItem(i);
			Ability burn = CMClass.getAbility("Burning");
			if ((burn != null) && (CMLib.dice().rollPercentage() > 60)) {
				which.showHappens(CMMsg.MSG_OK_ACTION, target.Name()
						+ " begins to burn!");
				burn.invoke(mob, target, true, 0);
				target.recoverPhyStats();
			}
		}
		mob.destroy();
	}

	private static Item getSomething(MOB mob) {
		Vector good = new Vector();
		Vector great = new Vector();
		Item target = null;
		for (int i = 0; i < mob.numItems(); i++) {
			Item I = mob.getItem(i);
			if (I.amWearingAt(Wearable.IN_INVENTORY))
				good.addElement(I);
			else
				great.addElement(I);
		}
		if (great.size() > 0)
			target = (Item) great.elementAt(CMLib.dice().roll(1, great.size(),
					-1));
		else if (good.size() > 0)
			target = (Item) good.elementAt(CMLib.dice()
					.roll(1, good.size(), -1));
		return target;
	}
}