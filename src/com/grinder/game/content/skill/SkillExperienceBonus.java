package com.grinder.game.content.skill;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.Skill;
import com.grinder.util.ItemID;

/**
 * Handles the experience bonuses for skills
 * 
 * @author 2012
 *
 */
public class SkillExperienceBonus {

	/**
	 * Gets the skill bonus
	 * 
	 * @param player
	 *            the player
	 * @param skill
	 *            the skill
	 * @return
	 */
	public static double getSkillBonus(Player player, Skill skill) {
		switch (skill) {
		case ATTACK:
			return getAttackBonus(player);
		case STRENGTH:
			return getStrengthBonus(player);
		case DEFENCE:
			return getDefenceBonus(player);
		case RANGED:
			return getRangeBonus(player);
		case MAGIC:
			return getMagicBonus(player);
		case HITPOINTS:
			return getHitPointsBonus(player);
		case WOODCUTTING:
			return getWoodcuttingBonus(player);
		case SMITHING:
			return getSmithingBonus(player);
		case FISHING:
			return getFishingBonus(player);
		case MINING:
			return getMiningBonus(player);
		case THIEVING:
			return getThievingBonus(player);
		case CRAFTING:
			return getCraftingBonus(player);
		case FIREMAKING:
			return getFiremakingBonus(player);
		case HERBLORE:
			return getHerbloreBonus(player);
		case PRAYER:
			return getPrayerBonus(player);
		case COOKING:
			return getCookingBonus(player);
		case AGILITY:
			return getAgilityBonus(player);
		case RUNECRAFTING:
			return getRunecraftingBonus(player);
		case FLETCHING:
			return getFletchingBonus(player);
		case FARMING:
			return getFarmingBonus(player);
		default:
			break;
		}
		return 1.0;
	}

	/**
	 * Gets the Woodcutting bonus
	 * 
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getWoodcuttingBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.LUMBERJACK_BOOTS)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.LUMBERJACK_HAT)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.LUMBERJACK_LEGS)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.LUMBERJACK_TOP)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.WOODCUTTING_CAPE)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.WOODCUT_CAPE_T_)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.WOODCUTTING_HOOD)) {
			bonus += 0.05;
		}
		return bonus;
	}

	/**
	 * Gets the Agility bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getAgilityBonus(Player player) {
		double bonus = 1.0;
		if (EquipmentUtil.isWearingAnyGracefulSet(player)) {
			bonus += 0.25;
		}
		if (EquipmentUtil.isWearingArceuusGracefulSet(player.getEquipment())) {
			bonus += 0.30;
		}
		if (EquipmentUtil.isWearingPiscariliusGracefulSet(player.getEquipment())) {
			bonus += 0.35;
		}
		if (EquipmentUtil.isWearingLovakengjGracefulSet(player.getEquipment())) {
			bonus += 0.40;
		}
		if (EquipmentUtil.isWearingShayzienGracefulSet(player.getEquipment())) {
			bonus += 0.42;
		}
		if (EquipmentUtil.isWearingHosidiusGracefulSet(player.getEquipment())) {
			bonus += 0.45;
		}
		if (EquipmentUtil.isWearingKourendGracefulSet(player.getEquipment())) {
			bonus += 0.50;
		}
		if (EquipmentUtil.isWearingBrimhavenGracefulSet(player.getEquipment())) {
			bonus += 0.55;
		}
		if (EquipmentUtil.isWearingHallowedGracefulSet(player.getEquipment())) {
			bonus += 0.60;
		}
		if (EquipmentUtil.isWearingTrailblazerGracefulSet(player.getEquipment())) {
			bonus += 0.70;
		}
		if (player.getEquipment().contains(ItemID.BOOTS_OF_LIGHTNESS)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.PENANCE_GLOVES)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.SPOTTED_CAPE) || player.getEquipment().contains(ItemID.SPOTTIER_CAPE)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.AGILITY_CAPE) && !EquipmentUtil.isWearingAnyGracefulSet(player)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.AGILITY_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.AGILITY_CAPE_T_) && !EquipmentUtil.isWearingAnyGracefulSet(player)) {
			bonus += 0.2;
		}
		return bonus;
	}

	/**
	 * Gets the Herblore bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getHerbloreBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.HERBLORE_CAPE)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.HERBLORE_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.HERBLORE_CAPE_T_)) {
			bonus += 0.2;
		}
		return bonus;
	}

	/**
	 * Gets the Prayer bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getPrayerBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.PRAYER_CAPE)) {
			bonus += 0.15;
		}
		if (player.getEquipment().contains(ItemID.PRAYER_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.PRAYER_CAPE_T_)) {
			bonus += 0.20;
		}
		if (player.getEquipment().contains(ItemID.ZEALOTS_BOOTS)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.ZEALOTS_ROBE_TOP)) {
			bonus += 0.10;
		}
		if (player.getEquipment().contains(ItemID.ZEALOTS_ROBE_TOP)) {
			bonus += 0.10;
		}
		return bonus;
	}

	/**
	 * Gets the Magic bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getMagicBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.MAGIC_CAPE)) {
			bonus += 0.15;
		}
		if (player.getEquipment().contains(ItemID.MAGIC_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.MAGIC_CAPE_T_)) {
			bonus += 0.20;
		}
		return bonus;
	}

	public static double getRangeBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.RANGING_CAPE)) {
			bonus += 0.15;
		}
		if (player.getEquipment().contains(ItemID.RANGING_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.RANGING_CAPE_T_)) {
			bonus += 0.20;
		}
		return bonus;
	}

	public static double getAttackBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.ATTACK_CAPE)) {
			bonus += 0.15;
		}
		if (player.getEquipment().contains(ItemID.ATTACK_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.ATTACK_CAPE_T_)) {
			bonus += 0.20;
		}
		return bonus;
	}

	public static double getStrengthBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.STRENGTH_CAPE)) {
			bonus += 0.15;
		}
		if (player.getEquipment().contains(ItemID.STRENGTH_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.STRENGTH_CAPE_T_)) {
			bonus += 0.20;
		}
		return bonus;
	}

	public static double getDefenceBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.DEFENCE_CAPE)) {
			bonus += 0.15;
		}
		if (player.getEquipment().contains(ItemID.DEFENCE_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.DEFENCE_CAPE_T_)) {
			bonus += 0.20;
		}
		return bonus;
	}

	public static double getHitPointsBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.HITPOINTS_CAPE)) {
			bonus += 0.15;
		}
		if (player.getEquipment().contains(ItemID.HITPOINTS_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.HITPOINTS_CAPE_T_)) {
			bonus += 0.20;
		}
		return bonus;
	}

	/**
	 * Gets the Cooking bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getCookingBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.CHEFS_HAT)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.GOLDEN_CHEFS_HAT)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.WHITE_APRON) || player.getEquipment().contains(ItemID.BROWN_APRON) || player.getEquipment().contains(ItemID.GOLDEN_APRON)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.COOKING_CAPE)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.COOKING_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.COOKING_CAPE_T_)) {
			bonus += 0.1;
		}
		return bonus;
	}

	/**
	 * Gets the Runecrafting bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getRunecraftingBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.RUNECRAFT_CAPE)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.RUNECRAFTING_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.RUNECRAFT_CAPE_T_)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.DECORATIVE_ARMOUR_11)) {
			bonus += 0.08;
		}
		if (player.getEquipment().contains(ItemID.DECORATIVE_ARMOUR_12)) {
			bonus += 0.008;
		}
		if (player.getEquipment().contains(ItemID.DECORATIVE_ARMOUR_13)) {
			bonus += 0.05;
		}
		return bonus;
	}

	/**
	 * Gets the Fletching bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getFletchingBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.FLETCHING_CAPE)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.FLETCHING_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.FLETCHING_CAPE_T_)) {
			bonus += 0.1;
		}
		return bonus;
	}

	/**
	 * Gets the Farming bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getFarmingBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.FARMERS_STRAWHAT)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.FARMERS_BORO_TROUSERS)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.FARMERS_JACKET)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.FARMERS_BOOTS)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.FARMING_CAPE)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.FARMING_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.FARMING_CAPE_T_)) {
			bonus += 0.1;
		}
		return bonus;
	}

	/**
	 * Gets the Smithing bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getSmithingBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(23101)) {
			bonus += 0.05;
		}
		/*if (player.getInventory().contains(ItemID.GOLDEN_HAMMER) && !player.getInventory().contains(ItemID.HAMMER)) {
			bonus += 0.1;
		}*/
		if (player.getEquipment().contains(23097)) {
			bonus += 0.08;
		}
		if (player.getEquipment().contains(23095)) {
			bonus += 0.07;
		}
		if (player.getEquipment().contains(23091)) {
			bonus += 0.025;
		}
		if (player.getEquipment().contains(23099)) {
			bonus += 0.5;
		}
		if (player.getEquipment().contains(23093)) {
			bonus += 0.025;
		}
		if (player.getEquipment().contains(ItemID.SMITHING_CAPE)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.SMITHING_CAPE_T_)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.SMITHING_HOOD)) {
			bonus += 0.05;
		}
		return bonus;
	}

	/**
	 * Gets the Crafting bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getCraftingBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.CRAFTING_HOOD)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.CRAFTING_CAPE)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.CRAFTING_CAPE_T_)) {
			bonus += 0.2;
		}
		return bonus;
	}

	/**
	 * Gets the Firemaking bonus
	 *
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	public static double getFiremakingBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.PYROMANCER_HOOD)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.PYROMANCER_ROBE)) {
			bonus += 0.08;
		}
		if (player.getEquipment().contains(ItemID.PYROMANCER_GARB)) {
			bonus += 0.08;
		}
		if (player.getEquipment().contains(ItemID.PYROMANCER_BOOTS)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.FIREMAKING_CAPE)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.FIREMAKING_CAPE_T_)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.FIREMAKING_HOOD)) {
			bonus += 0.1;
		}
		return bonus;
	}

	
	/**
	 * Gets the Fishing bonus
	 * 
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	private static double getFishingBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.ANGLER_TOP)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.ANGLER_WADERS)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.ANGLER_BOOTS)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.ANGLER_HAT)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.FISHING_CAPE)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.FISHING_CAPE_T_)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.FISHING_HOOD)) {
			bonus += 0.05;
		}
		return bonus;
	}
	
	/**
	 * Gets the Mining bonus
	 * 
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	private static double getMiningBonus(Player player) {
		double bonus = 1.0;
		if (player.getEquipment().contains(ItemID.PROSPECTOR_HELMET)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.PROSPECTOR_JACKET)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.PROSPECTOR_LEGS)) {
			bonus += 0.1; 
		}
		if (player.getEquipment().contains(ItemID.PROSPECTOR_BOOTS)) {
			bonus += 0.025;
		}
		if (player.getEquipment().contains(ItemID.GOLDEN_PROSPECTOR_HELMET)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.GOLDEN_PROSPECTOR_JACKET)) {
			bonus += 0.15;
		}
		if (player.getEquipment().contains(ItemID.GOLDEN_PROSPECTOR_LEGS)) {
			bonus += 0.15;
		}
		if (player.getEquipment().contains(ItemID.GOLDEN_PROSPECTOR_BOOTS)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.MINING_CAPE)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.MINING_CAPE_T_)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.MINING_HOOD)) {
			bonus += 0.05;
		}
		return bonus;
	}
	
	/**
	 * Thieving bonus
	 * 
	 * @param player
	 *            the player
	 * @return the bonus
	 */
	private static double getThievingBonus(Player player) {
		double bonus = 1.0;
		/**
		 * Rogue equipment
		 */
		if (player.getEquipment().contains(ItemID.ROGUE_MASK)) {
			bonus += 0.05;
		}
		if (player.getEquipment().contains(ItemID.ROGUE_TROUSERS)) {
			bonus += 0.1;
		}
		if (player.getEquipment().contains(ItemID.ROGUE_GLOVES)) {
			bonus += 0.025;
		}
		if (player.getEquipment().contains(ItemID.ROGUE_BOOTS)) {
			bonus += 0.025;
		}
		if (player.getEquipment().contains(ItemID.ROGUE_TOP)) {
			bonus += 0.1;
		}
		/**
		 * Dodgy necklace bonus
		 */
		if (player.getEquipment().contains(ItemID.DODGY_NECKLACE)) {
			bonus += 0.1;
		}
		/**
		 * Gloves of silence
		 */
		if (player.getEquipment().contains(ItemID.GLOVES_OF_SILENCE)) {
			bonus += 0.05;
		}
		/**
		 * Thieving cape
		 */
		if (player.getEquipment().contains(ItemID.THIEVING_CAPE)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.THIEVING_CAPE_T_)) {
			bonus += 0.2;
		}
		if (player.getEquipment().contains(ItemID.THIEVING_HOOD)) {
			bonus += 0.2;
		}
		return bonus;
	}

	/**
	 * Gets the members rank global bonus experience multiplier bonus
	 *
	 * @param player
	 *            the player
	 * @return
	 */
	public static double getMembersRankBonusExperience(Player player) {
		switch (PlayerUtil.getMemberRights(player)) {
			case DIAMOND_MEMBER:
				return 1.20;
			case TITANIUM_MEMBER:
				return 1.15;
			case PLATINUM_MEMBER:
				return 1.10;
			default:
				break;
		}
		return 1.0;
	}
}
