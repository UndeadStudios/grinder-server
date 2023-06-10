package com.grinder.game.model.commands.impl;

import java.util.concurrent.TimeUnit;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.util.time.TimeUnits;

public class FinishTasksCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Completes your pre-finished achievement tasks.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		if (!EntityExtKt.passedTime(player, Attribute.LAST_COMMAND, 60, TimeUnit.SECONDS, false, false)) {
			return;
		}
		if(!PlayerUtil.isDeveloper(player)) {
			if (player.getAchievements().getProgress()[AchievementType.SELF_SECURE.ordinal()] == 0) {
				player.sendMessage("You must have a bank PIN before using this command.");
				return;
			}
		}
		if (player.getTimePlayed(TimeUnits.DAY) < 1) {
			player.sendMessage("You must have at least a play time of 24 hours to use this command.");
			return;
		}
		EntityExtKt.markTime(player, Attribute.LAST_COMMAND);
		if (player.getBankpin().requireBankPin()) {
			AchievementManager.processFor(AchievementType.SELF_SECURE, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.ATTACK) == 99) {
			AchievementManager.processFor(AchievementType.PRECISE_DETAILS, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.STRENGTH) == 99) {
			AchievementManager.processFor(AchievementType.GOD_OF_WAR, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.DEFENCE) == 99) {
			AchievementManager.processFor(AchievementType.BRUTAL_ARMOR, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.RANGED) == 99) {
			AchievementManager.processFor(AchievementType.EXPERT_MARKSMAN, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.MAGIC) == 99) {
			AchievementManager.processFor(AchievementType.MAGIC_ARTIST, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.PRAYER) == 99) {
			AchievementManager.processFor(AchievementType.BLESSED_SPIRIT, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.HITPOINTS) == 99) {
			AchievementManager.processFor(AchievementType.SEVEN_LIVES, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.WOODCUTTING) == 99) {
			AchievementManager.processFor(AchievementType.MAKE_THE_CUT, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.AGILITY) == 99) {
			AchievementManager.processFor(AchievementType.AGILE_TASKS, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.COOKING) == 99) {
			AchievementManager.processFor(AchievementType.STAR_CHEF, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.FISHING) == 99) {
			AchievementManager.processFor(AchievementType.SURF_N_TURF, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.SLAYER) == 99) {
			AchievementManager.processFor(AchievementType.NIGHT_DIPLOMACY, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.HERBLORE) == 99) {
			AchievementManager.processFor(AchievementType.HERBALISM, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.MINING) == 99) {
			AchievementManager.processFor(AchievementType.NEVER_GIVE_UP, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.SMITHING) == 99) {
			AchievementManager.processFor(AchievementType.STAND_THE_HEAT, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) == 99) {
			AchievementManager.processFor(AchievementType.ESSENCE_ARTIST, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.CRAFTING) == 99) {
			AchievementManager.processFor(AchievementType.CRAFTSMANSHIP, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.FLETCHING) == 99) {
			AchievementManager.processFor(AchievementType.FEATHER_ARTISAN, player);
		}
		if (player.getSkillManager().getMaxLevel(Skill.FARMING) == 99) {
			AchievementManager.processFor(AchievementType.NATURE_PRESERVED, player);
		}
		if (player.getSkillManager().calculateCombatLevel() == 126) {
			AchievementManager.processFor(AchievementType.INSURGENT, player);
		}
		if (player.getSkillManager().countTotalLevel() >= 1500) {
			AchievementManager.processFor(AchievementType.ULTIMATE_MAX, player);
		}
		if (player.getSkillManager().countTotalLevel() >= 2179) {
			AchievementManager.processFor(AchievementType.END_OF_JOURNEY, player);
		}
		if (player.getSkillManager().countTotalExperience() >= 500_000_000) {
			AchievementManager.processFor(AchievementType.GRINDERSCAPE_IDOL, player);
		}

		if (player.getSkillManager().getExperience(Skill.ATTACK) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_PRECISION, player);
		}
		if (player.getSkillManager().getExperience(Skill.STRENGTH) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_WAR, player);
		}
		if (player.getSkillManager().getExperience(Skill.DEFENCE) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_ARMOR, player);
		}
		if (player.getSkillManager().getExperience(Skill.RANGED) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_MARKSMAN, player);
		}
		if (player.getSkillManager().getExperience(Skill.MAGIC) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_MAGE, player);
		}
		if (player.getSkillManager().getExperience(Skill.PRAYER) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_SPIRIT, player);
		}
		if (player.getSkillManager().getExperience(Skill.HITPOINTS) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_AGE, player);
		}
		if (player.getSkillManager().getExperience(Skill.WOODCUTTING) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_CUTTING, player);
		}
		if (player.getSkillManager().getExperience(Skill.AGILITY) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_AGILIY, player);
		}
		if (player.getSkillManager().getExperience(Skill.COOKING) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_CHEF, player);
		}
		if (player.getSkillManager().getExperience(Skill.FISHING) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.DEEP_FISHING, player);
		}
		if (player.getSkillManager().getExperience(Skill.SLAYER) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_DIPLOMACY, player);
		}
		if (player.getSkillManager().getExperience(Skill.HERBLORE) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_HERBALISM, player);
		}
		if (player.getSkillManager().getExperience(Skill.MINING) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.NOT_GIVING_UP, player);
		}
		if (player.getSkillManager().getExperience(Skill.SMITHING) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.SURVIVING_THE_HEAT, player);
		}
		if (player.getSkillManager().getExperience(Skill.RUNECRAFTING) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_ARTIST, player);
		}
		if (player.getSkillManager().getExperience(Skill.CRAFTING) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.ELITE_CRAFTSMANSHIP, player);
		}
		if (player.getSkillManager().getExperience(Skill.FLETCHING) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.SHAFTS_ARTISAN, player);
		}
		if (player.getSkillManager().getExperience(Skill.FARMING) >= 50_000_000) {
			AchievementManager.processFor(AchievementType.EVER_GREEN_AGAIN, player);
		}
		if (player.getSkillManager().getExperience(Skill.ATTACK) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_PRECISION, player);
		}
		if (player.getSkillManager().getExperience(Skill.STRENGTH) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_OF_WAR, player);
		}
		if (player.getSkillManager().getExperience(Skill.DEFENCE) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.UNBREAKABLE_ARMOR, player);
		}
		if (player.getSkillManager().getExperience(Skill.RANGED) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_MARKSMAN, player);
		}
		if (player.getSkillManager().getExperience(Skill.MAGIC) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MAGIC_MASTER, player);
		}
		if (player.getSkillManager().getExperience(Skill.PRAYER) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.ENCHANTED_SPIRIT, player);
		}
		if (player.getSkillManager().getExperience(Skill.HITPOINTS) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.IMMORTALITY, player);
		}
		if (player.getSkillManager().getExperience(Skill.WOODCUTTING) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_CUTTER, player);
		}
		if (player.getSkillManager().getExperience(Skill.AGILITY) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_NINJA, player);
		}
		if (player.getSkillManager().getExperience(Skill.COOKING) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_CHEF, player);
		}
		if (player.getSkillManager().getExperience(Skill.FISHING) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MARIANA_TRENCH, player);
		}
		if (player.getSkillManager().getExperience(Skill.SLAYER) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_DIPLOMACY, player);
		}
		if (player.getSkillManager().getExperience(Skill.HERBLORE) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_HERBALISM, player);
		}
		if (player.getSkillManager().getExperience(Skill.MINING) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.NEVER_GIVING_UP, player);
		}
		if (player.getSkillManager().getExperience(Skill.SMITHING) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.HEAT_ROUTINE, player);
		}
		if (player.getSkillManager().getExperience(Skill.RUNECRAFTING) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_ARTIST, player);
		}
		if (player.getSkillManager().getExperience(Skill.CRAFTING) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_CRAFTSMANSHIP, player);
		}
		if (player.getSkillManager().getExperience(Skill.FLETCHING) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.MASTER_FLETCHER, player);
		}
		if (player.getSkillManager().getExperience(Skill.FARMING) >= 100_000_000) {
			AchievementManager.processFor(AchievementType.FARM_MASTER, player);
		}
		if (EntityExtKt.getInt(player, Attribute.TIMES_PAID, 0) > 0) {
			AchievementManager.processFor(AchievementType.FRIENDLY_ATTIRE, player);
		}
		if (player.isDicer()) {
			AchievementManager.processFor(AchievementType.TRUSTED_MEMBER, player);
		}
		if (player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 50) {
			AchievementManager.processFor(AchievementType.SPREAD_LOVE, player);
		}
		if (player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 100) {
			AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, player);
		}
		if (player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 149) {
			AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, player);
		}
		if (player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 249) {
			AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, player);
		}
		if (player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 499) {
			AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, player);
		}
		if (player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 749) {
			AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, player);
		}
		if (player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 999) {
			AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, player);
		}
		if (MonsterKillTracker.getKillsCount(player, 3127) > 0) {
			AchievementManager.processFor(AchievementType.FIRE_WARRIOR, player);
		}

		// Tasks for Iron Man that cannot be done by them auto complete.
		if (player.getGameMode().isAnyIronman()) {
			AchievementManager.processFor(AchievementType.BEAST_SLAYER, player);
			AchievementManager.processFor(AchievementType.LUCKY_BEAST, player);
			AchievementManager.processFor(AchievementType.DAILY_SAVIOUR, player);
			AchievementManager.processFor(AchievementType.DAILY_ACTIVIST, player);
			AchievementManager.processFor(AchievementType.OVER_DICE, player);
			AchievementManager.processFor(AchievementType.LUCKY_DRAW, player);
			AchievementManager.processFor(AchievementType.DAILY_FEED, player);
			AchievementManager.processFor(AchievementType.CONTRACT_EXPERT, player);
			AchievementManager.processFor(AchievementType.CONTRACT_JUNIOR, player);
			AchievementManager.processFor(AchievementType.CONTRACT_MASTER, player);
			AchievementManager.processFor(AchievementType.TRUSTED_MEMBER, player);
		}

	}

	@Override
	public boolean canUse(Player player) {
		return true;
	}
}
