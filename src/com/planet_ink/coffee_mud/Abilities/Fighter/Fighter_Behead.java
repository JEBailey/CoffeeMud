package com.planet_ink.coffee_mud.Abilities.Fighter;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Behaviors.interfaces.LegalBehavior;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.LegalWarrant;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
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
public class Fighter_Behead extends FighterSkill {
	public String ID() {
		return "Fighter_Behead";
	}

	public String name() {
		return "Behead";
	}

	private static final String[] triggerStrings = { "BEHEAD" };

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(0);
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_ANATOMY;
	}

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	public int castingQuality(MOB mob, Physical target) {
		if ((mob != null) && (target != null) && (target instanceof MOB)) {
			Race R = ((MOB) target).charStats().getMyRace();
			if (R.bodyMask()[Race.BODY_HEAD] <= 0)
				return Ability.QUALITY_INDIFFERENT;
			LegalBehavior B = null;
			if (mob.location() != null)
				B = CMLib.law().getLegalBehavior(mob.location());
			List<LegalWarrant> warrants = new Vector();
			if (B != null)
				warrants = B.getWarrantsOf(
						CMLib.law().getLegalObject(mob.location()),
						(MOB) target);
			if (warrants.size() == 0)
				return Ability.QUALITY_INDIFFERENT;
			Item w = mob.fetchWieldedItem();
			Weapon ww = null;
			if ((w == null) || (!(w instanceof Weapon)))
				return Ability.QUALITY_INDIFFERENT;
			ww = (Weapon) w;
			if (ww.weaponType() != Weapon.TYPE_SLASHING)
				return Ability.QUALITY_INDIFFERENT;
			if (mob.isInCombat() && (mob.rangeToTarget() > 0))
				return Ability.QUALITY_INDIFFERENT;
			if (!CMLib.flags().isBoundOrHeld(target))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = super.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;
		Race R = target.charStats().getMyRace();
		if (R.bodyMask()[Race.BODY_HEAD] <= 0) {
			mob.tell(target.name(mob) + " has no head!");
			return false;
		}

		LegalBehavior B = null;
		if (mob.location() != null)
			B = CMLib.law().getLegalBehavior(mob.location());
		List<LegalWarrant> warrants = new Vector();
		if (B != null)
			warrants = B.getWarrantsOf(
					CMLib.law().getLegalObject(mob.location()), target);
		if ((warrants.size() == 0)
				&& (!CMSecurity.isAllowed(mob, mob.location(),
						CMSecurity.SecFlag.ABOVELAW))) {
			mob.tell("You are not allowed to behead " + target.Name()
					+ " at this time.");
			return false;
		}

		Item w = mob.fetchWieldedItem();
		Weapon ww = null;
		if ((!auto) && (!CMSecurity.isASysOp(mob))) {
			if ((w == null) || (!(w instanceof Weapon))) {
				mob.tell("You cannot behead without a weapon!");
				return false;
			}
			ww = (Weapon) w;
			if (ww.weaponType() != Weapon.TYPE_SLASHING) {
				mob.tell("You cannot behead with a " + ww.name() + "!");
				return false;
			}
			if (mob.isInCombat() && (mob.rangeToTarget() > 0)) {
				mob.tell("You are too far away to try that!");
				return false;
			}
			if (!CMLib.flags().isBoundOrHeld(target)) {
				mob.tell(target.charStats().HeShe()
						+ " is not bound and would resist.");
				return false;
			}
		}

		if ((!super.invoke(mob, commands, givenTarget, auto, asLevel))
				|| (ww == null))
			return false;

		int levelDiff = target.phyStats().level()
				- (mob.phyStats().level() + (2 * super.getXLEVELLevel(mob)));
		if (levelDiff > 0)
			levelDiff = levelDiff * 3;
		else
			levelDiff = 0;
		boolean hit = (auto) || CMLib.combat().rollToHit(mob, target);
		boolean success = proficiencyCheck(mob, 0, auto) && (hit);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS
					| CMMsg.MASK_MOVE | CMMsg.MASK_SOUND | CMMsg.TYP_JUSTICE
					| (auto ? CMMsg.MASK_ALWAYS : 0), null);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				target.curState().setHitPoints(1);
				Ability A2 = target.fetchEffect("Injury");
				if (A2 != null)
					A2.setMiscText(mob.Name() + "/head");
				CMLib.combat()
						.postDamage(
								mob,
								target,
								ww,
								Integer.MAX_VALUE / 2,
								CMMsg.MSG_WEAPONATTACK,
								ww.weaponClassification(),
								auto ? ""
										: "^F^<FIGHT^><S-NAME> rear(s) back and behead(s) <T-NAME>!^</FIGHT^>^?"
												+ CMLib.protocol().msp(
														"decap.wav", 30));
				mob.location().recoverRoomStats();
				Item limb = CMClass.getItem("GenLimb");
				limb.setName(target.Name() + "`s head");
				limb.basePhyStats().setAbility(1);
				limb.setDisplayText("the bloody head of " + target.Name()
						+ " is sitting here.");
				limb.setSecretIdentity(target.name() + "`s bloody head.");
				int material = RawMaterial.RESOURCE_MEAT;
				for (int r = 0; r < R.myResources().size(); r++) {
					Item I = R.myResources().get(r);
					int mat = I.material() & RawMaterial.MATERIAL_MASK;
					if (((mat == RawMaterial.MATERIAL_FLESH))
							|| (r == R.myResources().size() - 1)) {
						material = I.material();
						break;
					}
				}
				limb.setMaterial(material);
				limb.basePhyStats().setLevel(1);
				limb.basePhyStats().setWeight(5);
				limb.recoverPhyStats();
				mob.location().addItem(limb, ItemPossessor.Expire.Player_Drop);
				for (int i = 0; i < warrants.size(); i++) {
					LegalWarrant W = warrants.get(i);
					W.setCrime("pardoned");
					W.setOffenses(0);
				}
			} else
				success = false;
			if (mob.getVictim() == target)
				mob.makePeace();
			if (target.getVictim() == mob)
				target.makePeace();
		} else
			maliciousFizzle(mob, target,
					"<S-NAME> attempt(s) a beheading and fail(s)!");
		return success;
	}
}
