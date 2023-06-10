package com.grinder.game.model.consumable.potion;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.model.consumable.ConsumableConstants;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;
import com.grinder.util.timing.TimerKey;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.grinder.util.ItemID.*;

/**
 * The enumerated type managing consumable potion types.
 *
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 * @author lare96 <http://github.com/lare96>
 * @author Professor oak
 * @author Stan van der Bend
 */
public enum Potion {
	
	AGILITY_POTION(3032, 3034, 3036, 3038) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.boostSkillLevelTemporarily(player, Skill.AGILITY);
		}
	},

	AGILITY_MIX(11461, 11463) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.boostSkillLevelTemporarily(player, Skill.AGILITY);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},

	FISHING_POTION(2438, 151, 153, 155) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.boostSkillLevelTemporarily(player, Skill.FISHING);
		}
	},

	SUPER_ATTACKMIX(11469, 11471) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.SUPER);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},

	FISHING_MIX(11477, 11479) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.boostSkillLevelTemporarily(player, Skill.FISHING);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},

	HUNTER_POTION(ItemID.HUNTER_POTION_4_, ItemID.HUNTER_POTION_3_, ItemID.HUNTER_POTION_2_, ItemID.HUNTER_POTION_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.boostSkillLevelTemporarily(player, Skill.HUNTER);
		}
	},

	HUNTING_MIX(ItemID.HUNTING_MIX_1_, HUNTING_MIX_2_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.boostSkillLevelTemporarily(player, Skill.HUNTER);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},

	BASTION_POTION(22461, 22464, 22467, 22470) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.RANGED, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
		}
	},

	DIVINE_BASTION_POTION(24635, 24638, 24641, 24644) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.RANGED, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
			player.divineRange.start(300);
			player.divineDefence.start(300);
		}
	},

	BATTLEMAGE_POTION(22449, 22452, 22455, 22458) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.MAGIC, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
		}
	},

	DIVINE_BATTLEMAGE_POTION(24623, 24626, 24629, 24632) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.MAGIC, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
			player.divineMagic.start(300);
			player.divineDefence.start(300);
		}
	},

	OVERLOAD_POTIONS(11730, 11731, 11732, 11733) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startOverloadTask(player);
		}
	},
	OVERLOAD_MINUS(OVERLOAD___4_, OVERLOAD___3_, OVERLOAD___2_, OVERLOAD___1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startOverloadTask(player);
			player.getTimerRepository().register(TimerKey.OVERLOAD_POTION, 100);
		}
	},
	OVERLOAD(OVERLOAD_4_2, OVERLOAD_3_2, OVERLOAD_2_2, OVERLOAD_1_2) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startOverloadTask(player);
			player.getTimerRepository().register(TimerKey.OVERLOAD_POTION, 200);
		}
	},
	OVERLOAD_PLUS(OVERLOAD__PLUS_4_, OVERLOAD__PLUS_3_, OVERLOAD__PLUS_2_, OVERLOAD__PLUS_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startOverloadTask(player);
		}
	},
	ANTIFIRE_POTIONS(2452, 2454, 2456, 2458) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startFireImmunity(player, (int) TimeUnit.MINUTES.toSeconds(6), false);
		}
	},
	ANTIFIRE_MIX(11505, 11507) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startFireImmunity(player, (int) TimeUnit.MINUTES.toSeconds(6), false);

			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	SUPER_ANTIFIRE_POTIONS(21978, 21981, 21984, 21987) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startSuperFireImmunity(player, (int) TimeUnit.MINUTES.toSeconds(6), false);
		}
	},
	SUPER_ANTIFIRE_MIX(21994, 21997) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startSuperFireImmunity(player, (int) TimeUnit.MINUTES.toSeconds(6), false);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	EXTENDED_SUPER_ANTIFIRE_POTIONS(22209, 22212, 22215, 22218) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startSuperFireImmunity(player, (int) TimeUnit.MINUTES.toSeconds(12), false);
		}
	},
	EXTENDED_SUPER_ANTIFIRE_MIX(22221, 22224) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startSuperFireImmunity(player, (int) TimeUnit.MINUTES.toSeconds(12), false);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	EXTENDED_ANTIFIRE_POTIONS(EXTENDED_ANTIFIRE_4_, EXTENDED_ANTIFIRE_3_, EXTENDED_ANTIFIRE_2_, EXTENDED_ANTIFIRE_1_){
		@Override
		public void onEffect(Player player) {
			PotionEffects.startFireImmunity(player, (int) TimeUnit.MINUTES.toSeconds(12), true);
		}
	},
	EXTENDED_ANTIFIRE_MIX(EXTENDED_ANTIFIRE_MIX_2_, EXTENDED_ANTIFIRE_MIX_1_){
		@Override
		public void onEffect(Player player) {
			PotionEffects.startFireImmunity(player, (int) TimeUnit.MINUTES.toSeconds(12), true);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	GUTHIX_REST(GUTHIX_REST_4_, GUTHIX_REST_3_, GUTHIX_REST_2_, GUTHIX_REST_1_) {
		@Override
		public void onEffect(Player player) {
			if (player.getPoisonDamage() > 0) {
				player.setPoisonDamage(player.getPoisonDamage()-1);
			}

			int maxLevel = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currLevel = player.getSkillManager().getCurrentLevel(Skill.HITPOINTS);
			if (currLevel < maxLevel) {
				player.getSkillManager().increaseLevelTemporarily(Skill.HITPOINTS, 5, maxLevel);
			}
			int energy = player.getRunEnergy();
			if (energy < 100) {
				energy = energy + 5;
				if (energy > 100) {
					energy = 100;
				}
				player.setRunEnergy(energy);
			}
		}
	},
	SUPER_ANTIPOISON_POTIONS(2448, 181, 183, 185) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, (int) TimeUnit.MINUTES.toSeconds(6));
		}
	},
	ANTIPOISON_MINUS(ANTIPOISON_4, ANTIPOISON_3, ANTIPOISON_2, ANTIPOISON_1) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, (int) TimeUnit.MINUTES.toSeconds(2));
		}
	},
	ANTIPOISON(ANTIPOISON_POTION_4, ANTIPOISON_POTION_3, ANTIPOISON_POTION_2, ANTIPOISON_POTION_1) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, (int) TimeUnit.MINUTES.toSeconds(5));
		}
	},
	ANTIPOISON_PLUS(ANTIPOISON_4_25765, ANTIPOISON_3_25764, ANTIPOISON_2_25763, ANTIPOISON_1_25762) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, (int) TimeUnit.MINUTES.toSeconds(9));
		}
	},
	SUPER_ANTIPOISON_MIX(11473, 11475) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, (int) TimeUnit.MINUTES.toSeconds(6));

			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	ANTIPOISON_POTIONS(2446, 175, 177, 179) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, 0);
		}
	},
	ANTIPOISON_MIX(11433, 11435) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, 0);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 3;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	COMBAT_POTION(9739, 9741, 9743, 9745) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.LOW);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.LOW);
		}
	},
	COMBAT_MIX(11445, 11447) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.LOW);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.LOW);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	MAGIC_POTIONS(3040, 3042, 3044, 3046) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.MAGIC, PotionBoostType.NORMAL);
		}
	},
	DIVINE_MAGIC_POTIONS(23745, 23748, 23751, 23754) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.MAGIC, PotionBoostType.NORMAL);
			player.divineMagic.start(300);
		}
	},
	MAGIC_MIX(11513, 11515) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.MAGIC, PotionBoostType.NORMAL);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	SUPER_MAGIC_POTIONS(11726, 11727, 11728, 11729) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.MAGIC, PotionBoostType.SUPER);
		}
	},
	DEFENCE_POTIONS(2432, 133, 135, 137) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.NORMAL);
		}
	},
	DEFENCE_MIX(11457, 11459) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.NORMAL);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	STRENGTH_POTIONS(113, 115, 117, 119) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.NORMAL);
		}
	},
	STRENGTH_MIX(11441, 11443) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.NORMAL);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 3;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	ATTACK_POTIONS(2428, 121, 123, 125) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.NORMAL);
		}
	},
	ELDER_MINUS(ELDER___4_, ELDER___3_, ELDER___2_, ELDER___1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.LOW);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.LOW);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.LOW);
		}
	},
	ELDER_POTION(ELDER_POTION_4_, ELDER_POTION_3_, ELDER_POTION_2_, ELDER_POTION_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.NORMAL);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.NORMAL);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.NORMAL);
		}
	},
	ELDER_PLUS(ELDER__PLUS_4_, ELDER__PLUS_3_, ELDER__PLUS_2_, ELDER__PLUS_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
		}
	},
	TWISTED_MINUS(TWISTED___4_, TWISTED___3_, TWISTED___2_, TWISTED___1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.RANGED, PotionBoostType.LOW);
		}
	},
	TWISTED_POTION(TWISTED_POTION_4_, TWISTED_POTION_3_, TWISTED_POTION_2_, TWISTED_POTION_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.RANGED, PotionBoostType.NORMAL);
		}
	},
	TWISTED_POTION_PLUS(TWISTED__PLUS_4_, TWISTED__PLUS_3_, TWISTED__PLUS_2_, TWISTED__PLUS_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.RANGED, PotionBoostType.SUPER);
		}
	},
	KODAI_MINUS(KODAI___4_, KODAI___3_, KODAI___2_, KODAI___1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.MAGIC, PotionBoostType.LOW);
		}
	},
	KODAI_POTION(KODAI_POTION_4_, KODAI_POTION_3_, KODAI_POTION_2_, KODAI_POTION_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.MAGIC, PotionBoostType.NORMAL);
		}
	},
	KODAI_PLUS(KODAI__PLUS_4_, KODAI__PLUS_3_, KODAI__PLUS_2_, KODAI__PLUS_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.MAGIC, PotionBoostType.SUPER);
		}
	},
	ATTACK_MIX(11429, 11431) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.NORMAL);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 3;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	SUPER_DEFENCE_POTIONS(2442, 163, 165, 167) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
		}
	},
	DIVINE_SUPER_DEFENCE_POTIONS(23721, 23724, 23727, 23730) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
			player.divineDefence.start(300);
		}
	},
	SUPER_DEFENCE_MIX(11497, 11499) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	SUPER_ATTACK_POTIONS(2436, 145, 147, 149) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.SUPER);
		}
	},
	DIVINE_SUPER_ATTACK_POTIONS(23697, 23700, 23703, 23706) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.SUPER);
			player.divineAttack.start(300);
		}
	},
	SUPER_ATTACK_MIX(11471, 11469) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.SUPER);

			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	SUPER_STRENGTH_POTIONS(2440, 157, 159, 161) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.SUPER);
		}
	},
	DIVINE_SUPER_STRENGTH_POTIONS(23709, 23712, 23715, 23718) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.SUPER);
			player.divineStrength.start(300);
		}
	},
	SUPER_STR_MIX(11485, 11487) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.SUPER);

			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	RANGE_POTIONS(2444, 169, 171, 173) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.RANGED, PotionBoostType.NORMAL);
		}
	},
	DIVINE_RANGE_POTIONS(23733, 23736, 23739, 23742) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.RANGED, PotionBoostType.NORMAL);
			player.divineRange.start(300);
		}
	},
	RANGING_MIX(11509, 11511) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.RANGED, PotionBoostType.NORMAL);

			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	SUPER_RANGE_POTIONS(11722, 11723, 11724, 11725) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.RANGED, PotionBoostType.SUPER);
		}
	},
	ZAMORAK_BREW(2450, 189, 191, 193) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.zamorakTemporarySkillLevelBoost(player);
		}
	},
	ZAMORAK_MIX(11521, 11523) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.zamorakTemporarySkillLevelBoost(player);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	SARADOMIN_BREW(6685, 6687, 6689, 6691) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.saradominTemporarySkillLevelBoost(player);
		}
	},
	XERIC_AID_MINUS(XERICS_AID___4_, XERICS_AID___3_, XERICS_AID___2_, XERICS_AID___1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.saradominTemporarySkillLevelBoost(player);
		}
	},
	XERIC_AID(XERICS_AID_4_, XERICS_AID_3_, XERICS_AID_2_, XERICS_AID_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.saradominTemporarySkillLevelBoost(player);
		}
	},
	XERIC_AID_PLUS(XERICS_AID__PLUS_4_, XERICS_AID__PLUS_3_, XERICS_AID__PLUS_2_, XERICS_AID__PLUS_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.saradominTemporarySkillLevelBoost(player);
		}
	},
	SUPER_RESTORE_POTIONS(3024, 3026, 3028, 3030) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.increasePrayerLevel(player, true);
			PotionEffects.superRestoreStats(player);
		}
	},
	SUPER_RESTORE_MIX(11493, 11495) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.increasePrayerLevel(player, true);
			PotionEffects.superRestoreStats(player);


			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	RESTORE_POTIONS(2430, 127, 129, 131) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.restoreStats(player);
		}
	},
	REVITALISATION(REVITALISATION___4_, REVITALISATION___3_, REVITALISATION___2_, REVITALISATION___1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.restoreStats(player);
		}
	},
	RESTORE_MIX(11449, 11451) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.restoreStats(player);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 3;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	PRAYER_POTIONS(2434, 139, 141, 143) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.increasePrayerLevel(player, false);
		}
	},
	PRAYER_ENHANCE_MINUS(PRAYER_ENHANCE___4_, PRAYER_ENHANCE___3_, PRAYER_ENHANCE___2_, PRAYER_ENHANCE___1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.increasePrayerLevel(player, false);
		}
	},
	PRAYER_ENHANCE(PRAYER_POTION_4_, PRAYER_POTION_3_, PRAYER_POTION_2_, PRAYER_POTION_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.increasePrayerLevel(player, false);
		}
	},
	PRAYER_ENHANCE_PLUS(PRAYER_ENHANCE__PLUS_4_, PRAYER_ENHANCE__PLUS_3_, PRAYER_ENHANCE__PLUS_2_, PRAYER_ENHANCE__PLUS_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.increasePrayerLevel(player, true);
		}
	},
	PRAYER_MIX(11465, 11467) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.increasePrayerLevel(player, false);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	ENERGY_POTION(3008, 3010, 3012, 3014) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.restoreEnergy(player, 1000, false);
		}
	},
	ENERGY_MIX(11453, 11455) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.restoreEnergy(player, 1000, false);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	SUPER_ENERGY(3016, 3018, 3020, 3022) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.restoreEnergy(player, 2000, false);
		}
	},
	SUPER_MIX(11453, 11455) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.restoreEnergy(player, 2000, false);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	STAMINA_POTION(STAMINA_POTION_4_, STAMINA_POTION_3_, STAMINA_POTION_2_, STAMINA_POTION_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.restoreEnergy(player, 2000, true);
		}
	},
	STAMINA_MIX(STAMINA_MIX_1_, STAMINA_MIX_2_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.restoreEnergy(player, 2000, true);
			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	
	SUPER_COMBAT_POTION(SUPER_COMBAT_POTION_4_, SUPER_COMBAT_POTION_3_, SUPER_COMBAT_POTION_2_, SUPER_COMBAT_POTION_1_) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
		}
	},

	DIVINE_SUPER_COMBAT_POTION(DIVINE_SUPER_COMBAT_4, DIVINE_SUPER_COMBAT_3, DIVINE_SUPER_COMBAT_2, DIVINE_SUPER_COMBAT_1) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.ATTACK, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.STRENGTH, PotionBoostType.SUPER);
			PotionEffects.genericTemporarySkillLevelBoost(player, Skill.DEFENCE, PotionBoostType.SUPER);
			player.divineAttack.start(300);
			player.divineStrength.start(300);
			player.divineDefence.start(300);
		}
	},

	ANTIDOTE_PLUS(5943, 5945, 5947, 5949) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, (int) TimeUnit.MINUTES.toSeconds(8) + 30);
		}
	},

	ANTIDOTE_PLUS_MIX(11501, 11503) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, (int) TimeUnit.MINUTES.toSeconds(8) + 30);

			int maxHp = player.getSkillManager().getMaxLevel(Skill.HITPOINTS);
			int currentHp = player.getSkills().getLevel(Skill.HITPOINTS);
			int healAmount = 6;
			if (healAmount + currentHp > maxHp) healAmount = maxHp - currentHp;

			if (healAmount < 0) healAmount = 0;


			player.setHitpoints(player.getHitpoints() + healAmount);
		}
	},
	
	ANTIDOTE_PLUS_PLUS(5952, 5954, 5956, 5958) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, (int) TimeUnit.MINUTES.toSeconds(12));
		}
	},

	ANTI_VENOM(12905, 12907, 12909, 12911) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startVenomImmunity(player, (int) TimeUnit.MINUTES.toSeconds(8));
		}
	},

	ANTI_VENOM_PLUS(12913, 12915, 12917, 12919) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startVenomImmunity(player, (int) TimeUnit.MINUTES.toSeconds(12));
		}
	},

	AGGRESSIVITY_POTION(15274) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startMonsterAggression(player, (int) TimeUnit.MINUTES.toSeconds(5));
		}
	},
	
	SANFEW_SERUM(10925, 10927, 10929, 10931) {
		@Override
		public void onEffect(Player player) {
			PotionEffects.startPoisonImmunity(player, (int) TimeUnit.MINUTES.toSeconds(6));
			PotionEffects.sanfewTemporarySkillLevelBoost(player);
			// TODO: DISEASE EFFECT
		}
	},
	
	;


    /**
	 * The identifiers which represent this potion type.
	 */
	private final int[] ids;

	/**
	 * Create a new {@link Potion}.
	 *
	 * @param ids
	 *            the identifiers which represent this potion type.
	 */
	Potion(int... ids) {
		this.ids = ids;
	}

	/**
	 * Retrieves the replacement item for {@code item}.
	 *
	 * @param item
	 *            the item to retrieve the replacement item for.
	 * @return the replacement item wrapped in an optional, or an empty optional
	 *         if no replacement item is available.
	 */
	public static Item getReplacementItem(int item) {
		Optional<Potion> potion = forId(item);
		if (potion.isPresent()) {
			int length = potion.get().getIds().length;
			for (int index = 0; index < length; index++) {
				if (potion.get().getIds()[index] == item && index + 1 < length) {
					return new Item(potion.get().getIds()[index + 1]);
				}
			}
		}
		return new Item(ConsumableConstants.VIAL);
	}

	/**
	 * Retrieves the potion consumable element for {@code id}.
	 *
	 * @param id
	 *            the id that the potion consumable is attached to.
	 * @return the potion consumable wrapped in an optional, or an empty
	 *         optional if no potion consumable was found.
	 */
	public static Optional<Potion> forId(int id) {
		for (Potion potion : Potion.values()) {
			for (int potionId : potion.getIds()) {
				if (id == potionId) {
					return Optional.of(potion);
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * The method executed when this potion type activated.
	 *
	 * @param player
	 *            the player to execute this effect for.
	 */
	public abstract void onEffect(Player player);

	/**
	 * Gets the identifiers which represent this potion type.
	 *
	 * @return the identifiers for this potion.
	 */
	public final int[] getIds() {
		return ids;
	}

}
