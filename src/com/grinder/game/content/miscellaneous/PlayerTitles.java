package com.grinder.game.content.miscellaneous;

import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.Skill;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.grinder.util.ItemID.BLOOD_MONEY;
import static com.grinder.util.ItemID.COINS;
import static com.grinder.util.ItemID.TOKKUL;
import static com.grinder.util.ItemID.VOTING_TICKET;

/**
 * Handles player titles
 * 
 * @author 2012
 *
 */
public final class PlayerTitles {

	private static final Pattern WORD_PATTERN = Pattern.compile("(\\s?)([^.\\s]+)");
	private static final int INTERFACE_ID = 78100;
	private static final int PANEL_INCREMENT = 3;
	private static final int TITLE_INCREMENT = 4;
	private static final int BUTTON_ID_OFFSET = 2;
	private static final int BUTTON_TEXT_ID_OFFSET = 3;
	public static final int PREVIEW_TEXT_ID = INTERFACE_ID + 12;
	public static final int NAV_BUTTON_START_ID = INTERFACE_ID + 13;
	public static final int SCROLL_START_ID = NAV_BUTTON_START_ID + TitleType.values().length * 2;

	/**
	 * The title types
	 */
	public enum TitleType {
		PLAYER_KILLING(0xab0000),
		MONSTER_KILLING(0x104ebe),
		SKILLING(0x23d500),
		MINIGAMES(0xff3301),
		ACHIEVEMENTS(0x23d500),
		BUYABLES(0xfc9b06),
		TASKS(0xff01e8),
		OTHER(0xd3cc12),
		CUSTOM(0xd8bc12);

		private final List<PlayerTitle> titles = new ArrayList<>();

		private final int color;

		TitleType(int color) {
			this.color = color;
		}

		public int getColor() {
			return color;
		}

		public List<PlayerTitle> getTitles() {
			return titles;
		}
	}

	/**
	 * The titles
	 */
	public enum PlayerTitle {

        /**
         * Player Killing
         */
		NEWBIE("Newbie", "Reach a kill count of 1.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 1;
			}
		},
		LETHAL("Lethal", "Reach a kill count of 5.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 5;
			}
		},
		VITAL("Vital", "Reach a kill count of 15.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 15;
			}
		},
		Scout("Scout", "Reach a kill count of 25.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 25;
			}
		},
		FIGHTER("Fighter", "Reach a kill count of 50.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 50;
			}
		},
		EMPEROR("Emperor", "Reach a kill count of 100.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 100;
			}
		},
		ASSASIN("Assasin", "Reach a kill count of 150.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 150;
			}
		},
		SERGEANT("Sergeant", "Reach a kill count of 200.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 200;
			}
		},
		REAPER("Reaper", "Reach a kill count of 300.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 300;
			}
		},
		MURDERER("Murderer", "Reach a kill count of 400.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 400;
			}
		},
		SERIAL_KILLER("Serial Killer", "Reach a kill count of 500.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 500;
			}
		},
		GLADIATOR("Gladiator", "Reach a kill count of 700.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 700;
			}
		},
		LEGEND("Legend", "Reach a kill count of 900.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 900;
			}
		},
		SAVAGE("Savage", "Reach a kill count of 1,100.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 1100;
			}
		},
		DEADLY("Deadly", "Reach a kill count of 1,300.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 1300;
			}
		},
		DAUNTLESS("Dauntless", "Reach a kill count of 1,500.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 1500;
			}
		},
		WAR_HERO("War Hero", "Reach a kill count of 1,700.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 1700;
			}
		},
		BUTCHER("Butcher", "Reach a kill count of 1,900.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 1900;
			}
		},
		PUNISHER("Punisher", "Reach a kill count of 2,000.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 2000;
			}
		},
		LEGENDARY("Legendary", "Reach a kill count of 3,000.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 3000;
			}
		},
		SUPERNATURAL("Supernatural", "Reach a kill count of 4,500.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 4500;
			}
		},
		INSANE("Insane", "Reach a kill count of 5,000.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 5000;
			}
		},
		WARCHIEF("War Chief", "Reach a kill count of 6,000.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 6000;
			}
		},
		DESTROYER("Destroyer", "Reach a kill count of 6,500.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 6500;
			}
		},
		ULTIMATE("Ultimate", "Reach a kill count of 7,000.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 7000;
			}
		},
		PREDATOR("Predator", "Reach a kill count of 7,500.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 7500;
			}
		},
		SAIYAN("Saiyan", "Reach a kill count of 8,000.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 8000;
			}
		},
		SATAN("Satan", "Reach a kill count of 9,000.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 9000;
			}
		},
		DEMOGORGAN("Demogorgan", "Reach a kill count of 10,000.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLS) >= 10000;
			}
		},
		UNSTOPPABLE("Unstoppable", "Reach a kill streak of 10.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLSTREAK) >= 10;
			}
		},
		RELENTLESS("Relentless", "Reach a kill streak of 25.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLSTREAK) >= 25;
			}
		},
		INVINCIBLE("Invincible", "Reach a kill streak of 50.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLSTREAK) >= 50;
			}
		},
		GODLIKE("Godlike", "Reach a kill streak of 75.", TitleType.PLAYER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.KILLSTREAK) >= 75;
			}
		},

        /**
         * Monster Killing
         */

		ALCHEMIST_HYDRA("Alchemist Hydra", "Defeat the Alchemical Hydra.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, NpcID.ALCHEMICAL_HYDRA) >= 1;
			}
		},
		SLASHER("Slasher", "Defeat Slash bash 50 times.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 882) >= 50;
			}
		},
		THE_BLIGHTED("The Blighted", "Defeat Ahrim's the Blighted 50 times.", TitleType.MONSTER_KILLING, true) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 1672) >= 50;
			}
		},
		THE_CORRUPTED("The Corrupted", "Defeat Torag's the Corrupted 50 times.", TitleType.MONSTER_KILLING, true) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 1676) >= 50;
			}
		},
		THE_DEFILED("The Defiled", "Defeat Verac's the Defiled 50 times.", TitleType.MONSTER_KILLING, true) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 1677) >= 50;
			}
		},
		THE_INFESTED("The Infested", "Defeat Guthan's the Infested 50 times.", TitleType.MONSTER_KILLING, true) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 1674) >= 50;
			}
		},
		THE_TAINTED("The Tainted", "Defeat Karil's the Tainted 50 times.", TitleType.MONSTER_KILLING, true) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 1675) >= 50;
			}
		},
		THE_WRETCHED("The Wretched", "Defeat Dharok's the Wretched 50 times.", TitleType.MONSTER_KILLING, true) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 1673) >= 50;
			}
		},
		UNTOUCHABLE("Untouchable", "Defeat the Untouchable 50 times.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 3475) >= 50;
			}
		},
		BEAST_SLAYER("Beast Slayer", "Defeat the Corporeal beast 50 times.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 319) >= 50;
			}
		},
		THE_MUTANT("The Mutant", "Defeat the Mutant Tarn 50 times.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 6477) >= 50;
			}
		},
		TITAN("Titan", "Defeat the Black Knight Titan 50 times.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 4067) >= 50;
			}
		},
		VORKATH("Vorkath", "Defeat Vorkath 50 times.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return MonsterKillTracker.getKillsCount(player, 8061) >= 50;
			}
		},
		AMBASSADOR("Ambassador", "Defeat a total of 500 bosses.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getKillTracker().getBossesKilled() >= 500;
			}
		},
		BOSS_HUNTER("Boss Hunter", "Defeat a total of 1,000 bosses.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getKillTracker().getBossesKilled() >= 1000;
			}
		},
		KING_SLAYER("King Slayer", "Slay over 2,500 Slayer assigned monsters.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.SLAYER_NPC_KILLS) >= 2500;
			}
		},
		LUCKY("Lucky", "Get a rare drop from any monster.", TitleType.MONSTER_KILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.LUCKY_JOURNEY.ordinal()] > 0;
			}
		},

        /**
         * Skilling
         */

        MAXED("Maxed", "Get all of your skill levels to max.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.END_OF_JOURNEY.ordinal()] > 0;
			}
		},
		AGRICULURIST("Agriculturist", "Reach 50M XP in Farming skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.FARMING) > 50_000_000;
			}
		},
		ARTISAN("Artisan", "Reach 50M XP in Crafting skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.CRAFTING) > 50_000_000;
			}
		},
		BANDIT("Bandit", "Reach 50M XP in Thieving skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.THIEVING) > 50_000_000;
			}
		},
		BLACKSMITH("Blacksmith", "Reach 50M XP in Smithing skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.SMITHING) > 50_000_000;
			}
		},
		BOTANIST("Botanist", "Reach 50M XP in Herblore skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.HERBLORE) > 50_000_000;
			}
		},
		BRUTE("Brute", "Reach 50M XP in Strength skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.STRENGTH) > 50_000_000;
			}
		},
		CHEF("Chef", "Reach 50M XP in Cooking skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.COOKING) > 50_000_000;
			}
		},
		DIVINE("Divine", "Reach 50M XP in Prayer skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.PRAYER) > 50_000_000;
			}
		},
		ELEMENTAL("Elemental", "Reach 50M XP in Runecrafting skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.RUNECRAFTING) > 50_000_000;
			}
		},
		EXECUTIONER("Executioner", "Reach 50M XP in Slayer skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.SLAYER) > 50_000_000;
			}
		},
		FISHERMAN("Fisherman", "Reach 50M XP in Fishing skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.FISHING) > 50_000_000;
			}
		},
		FLETCHER("Fletcher", "Reach 50M XP in Fletching skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.FLETCHING) > 50_000_000;
			}
		},
		HEALTHY("Healthy", "Reach 50M XP in Hitpoints skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.HITPOINTS) > 50_000_000;
			}
		},
		INCINERATOR("Incinerator", "Reach 50M XP in Firemaking skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.FIREMAKING) > 50_000_000;
			}
		},
		LUMBERJACK("Lumberjack", "Reach 50M XP in Woodcutting skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.WOODCUTTING) > 50_000_000;
			}
		},
		MAGICIAN("Magician", "Reach 50M XP in Magic skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.MAGIC) > 50_000_000;
			}
		},
		PRECISE("Precise", "Reach 50M XP in Attack skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.ATTACK) > 50_000_000;
			}
		},
		PROSPECTOR("Prospector", "Reach 50M XP in Mining skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.MINING) > 50_000_000;
			}
		},
		ROBIN_HOOD("Robin Hood", "Reach 50M XP in Ranged skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.RANGED) > 50_000_000;
			}
		},
		HUNTER("Hunter", "Reach 50M XP in Hunter skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.HUNTER) > 50_000_000;
			}
		},
		TANK("Tank", "Reach 50M XP in Defence skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.DEFENCE) > 50_000_000;
			}
		},
		THE_AGILE("The Agile", "Reach 50M XP in Agilty skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.AGILITY) > 50_000_000;
			}
		},
		SKILLER("Skiller", "Reach 50M XP in all skills.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().countTotalExperience() >= 1_100_000_000;
			}
		},
		ELITE_SKILLER("Elite Skiller", "Reach 100M XP in all skills.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().countSkillsWithExperienceAbove(100_000_000) > 21;
			}
		},
		EXPERT_SKILLER("Expert Skiller", "Reach 150M XP in all skills.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().countSkillsWithExperienceAbove(150_000_000) > 21;
			}
		},
		MASTER_SKILLER("Master Skiller", "Reach 200M XP in all skills.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().countSkillsWithExperienceAbove(200_000_000) > 21;
			}
		},/*
		GOD_SKILLER("God Skiller", "Reach 250M XP in all skills.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().countSkillsWithExperienceAbove(250_000_000) > 21;
			}
		},*/
		ARCHER("Archer", "Reach a level of 99 in Ranged skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getMaxLevel(Skill.RANGED) == 99;
			}
		},
		SNIPER("Sniper", "Reach 200M XP in Ranged skill.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getSkillManager().getExperience(Skill.RANGED) > 200_000_000;
			}
		},
		SLAYER_EXPERT("Slayer Expert", "Get a streak of 50 Slayer tasks completed.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.SLAYER_STREAK) >= 50;
			}
		},
		SLAYER_MASTER("Bounty Hunter", "Get a streak of 100 Slayer tasks completed.", TitleType.SKILLING) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.SLAYER_STREAK) >= 100;
			}
		},
		/**
		 * Minigames
		 */
		DEULIST("Duelist", "Win 50 Duel Arena battles.", TitleType.MINIGAMES) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.DUEL_WINS) >= 50;
			}
		},
		CHAMPION("Champion", "Win 250 Duel Arena battles.", TitleType.MINIGAMES) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.DUEL_WINS) >= 250;
			}
		},
		WARRIOR("Warrior", "Win 500 Duel Arena battles.", TitleType.MINIGAMES) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.DUEL_WINS) >= 500;
			}
		},
		CHALLENGER("Challenger", "Win 500 Duel Arena battles.", TitleType.MINIGAMES) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.DUEL_WINS) >= 500;
			}
		},
		MASTER_DUELIST("Master Duelist", "Win 1,000 Duel Arena battles.", TitleType.MINIGAMES) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.DUEL_WINS) >= 1000;
			}
		},
		GAMBLER("Gambler", "Win over 200 dice bets.", TitleType.MINIGAMES) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.DICE_WINS) >= 200;
			}
		},
		MERCENARY("Mercenary", "Complete 50 barrows rounds.", TitleType.MINIGAMES) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.BARROWS_CHEST) >= 50;
			}
		},

		/**
		 * Achievements
		 */
		COMPLETIONIST("Completionist", "Aquire the Max Cape.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.COLOR_MAX.ordinal()] > 0;
			}
		},
		TASK_MASTER("Task Master", "Complete 50 tasks.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.OVER_ACHIEVER.ordinal()] >= 50;
			}
		},
		VOTER("Voter", "Vote 50 or more times.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.TOTAL_VOTES) >= 50;
			}
		},
		INFERNAL("Inferno", "Equip the Infernal Cape.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.BLAZING_HOT.ordinal()] > 0;
			}
		},
		DEVOTED("Devoted", "Vote 100 or more times.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.TOTAL_VOTES) >= 100;
			}
		},
		LOYAL("Loyal", "Acquire 250 participation points.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.STRAIGHT_UP.ordinal()] >= 250;
			}
		},
		PIONEER("Pioneer", "Complete 10 Clue Scrolls.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.TREASURE_MASTER.ordinal()] >= 10;
			}
		},
		TREASURE_HUNTER("Treasure Hunter", "Complete 25 Clue Scrolls.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.TREASURE_MASTER.ordinal()] >= 25;
			}
		},
		PIRATE("Pirate", "Complete 50 Clue Scrolls.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.TREASURE_MASTER.ordinal()] >= 50;
			}
		},
		THE_REAL("The Real", "Add a secure bank PIN to your account.", TitleType.ACHIEVEMENTS, true) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.SELF_SECURE.ordinal()] > 0;
			}
		},
		EXPLORER("Explorer", "Have 250 Achievement points.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getPoints().get(AttributeManager.Points.ACHIEVEMENT_POINTS_NEW) >= 250;
			}
		},
		JAD_SLAYER("Jad Slayer", "Have 250 Achievement points.", TitleType.ACHIEVEMENTS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.FIRE_WARRIOR.ordinal()] >= 1;
			}
		},
		/**
		 * Tasks
		 */
		FEARLESS("Fearless", "Finish the 'Fearless' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.FEARLESS.ordinal()] >= 1;
			}
		},
		GODLORD("Godlord", "Finish the 'Golden Mountain' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.GOLDEN_MOUNTAIN.ordinal()] >= 500000000;
			}
		},
		MEDIC("Medic", "Finish the 'Safety First' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.SAFETY_FIRST.ordinal()] >= 8000;
			}
		},
		MINER("Miner", "Finish the 'Never Give Up' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.NEVER_GIVE_UP.ordinal()] > 0;
			}
		},
		REBORN("Reborn", "Finish the 'Born To Die' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.BORN_TO_DIE.ordinal()] >= 50;
			}
		},
		SLAYER("Slayer", "Finish the 'Slayer Novice' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.SLAYER_NOVICE.ordinal()] >= 5;
			}
		},
		SNEAKY("Sneaky", "Finish the 'Hiding' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.HIDDEN.ordinal()] >= 1;
			}
		},
		SURVIVOR("Survivor", "Finish the 'Daily Saviour' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.DAILY_SAVIOUR.ordinal()] >= 10;
			}
		},
		WOODSMAN("Woodsman", "Finish the 'Make the Cut' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.MAKE_THE_CUT.ordinal()] >= 1;
			}
		},
		BRUTAL("Brutal", "Finish the 'Brutal' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.BRUTAL_ARMOR.ordinal()] >= 1;
			}
		},
		SEVEN_LIVES("7 Lives", "Finish the 'Seven Lives' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.SEVEN_LIVES.ordinal()] >= 1;
			}
		},
		STAR("Star", "Finish the 'Spray and Pray' task.", TitleType.TASKS) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getAchievements().getProgress()[AchievementType.SPRAY_AND_PRAY.ordinal()] >= 500;
			}
		},
		/**
		 * Buyables (UNIQUE COLORS)
		 * Common titles: 50M / Rare: 100M / Epic: 500M / Legendary: 1000M
		 */
		THE_BRAVE("The Brave", 50000000, COINS, TitleType.BUYABLES), // Common
		ANNOUNCER("Announcer", 50000000, COINS, TitleType.BUYABLES), // Common
		ADORABLE("Adorable", 50000000, COINS, TitleType.BUYABLES), // Common
		PROMOTER("Promoter", 50000000, COINS, TitleType.BUYABLES), // Common
		ZODIAC("Zodiac", 50000000, COINS, TitleType.BUYABLES),  // Common
		VIPER("Viper", 50000000, COINS, TitleType.BUYABLES),  // Common
		HUSKAR("Huskar", 50000000, COINS, TitleType.BUYABLES),  // Common
		LUNA("Luna", 50000000, COINS, TitleType.BUYABLES),  // Common
		KUNKKA("Kunnka", 50000000, COINS, TitleType.BUYABLES), // Common
		LEGION("Legion", 1000000000, COINS, TitleType.BUYABLES), // Rare
		FABULOUS("Fabulous", 100000000, COINS, TitleType.BUYABLES),  // Rare
		ELEGANT("Elegant", 100000000, COINS, TitleType.BUYABLES),  // Rare
		LIONHEART("Lionheart", 100000000, COINS, TitleType.BUYABLES),  // Rare
		MR("Mr", 100000000, COINS, TitleType.BUYABLES),  // Rare
		MRS("Mrs", 100000000, COINS, TitleType.BUYABLES),  // Rare
		SIR("Sir", 100000000, COINS, TitleType.BUYABLES),  // Rare
		MADAM("Madam", 100000000, COINS, TitleType.BUYABLES),  // Rare
		LORD("Lord", 50000000, COINS, TitleType.BUYABLES), // Epic
		LADY("Lady", 50000000, COINS, TitleType.BUYABLES), // Epic
		PRINCE("Prince", 500000000, COINS, TitleType.BUYABLES), // Epic
		PRINCESS("Princess", 500000000, COINS, TitleType.BUYABLES), // Epic
		KING("King", 500000000, COINS, TitleType.BUYABLES), // Epic
		QUEEN("Queen", 500000000, COINS, TitleType.BUYABLES), // Epic
		THE("The", 50000000, COINS, TitleType.BUYABLES), // Epic
		THE_NOTORIOUS("<col=FF8080>The Notorious", 500000000, COINS, TitleType.BUYABLES), // Epic
		PULP_FICTION("<col=ff08b1>Pulp Fiction", 500000000, COINS, TitleType.BUYABLES), // Epic
		THE_HITMAN("<col=5640F8>The Hitman", 500000000, COINS, TitleType.BUYABLES), // Epic
		TROLL("<col=2273EF>Troll", 500000000, COINS, TitleType.BUYABLES), // Epic
		AFK("<col=01E8FF>AFK", 500000000, COINS, TitleType.BUYABLES), // Epic
		VIP("<col=FF01E8>VIP", 1000000000, COINS, TitleType.BUYABLES), // Legendary
		MONTANA("<col=08ff6f>Montana", 1000000000, COINS, TitleType.BUYABLES, true), // Legendary
		AL_CAPONE("<col=ffff57>Al Capone", 1000000000, COINS, TitleType.BUYABLES, true), // Legendary
		EL_PATRON("<col=8527e8>El Patron", 1000000000, COINS, TitleType.BUYABLES), // Legendary
		DEATH_PROPHET("<col=2a0000>Death Prophet", 1000000000, COINS, TitleType.BUYABLES), // Legendary
		COMMUNIST("<col=ddfa00>Communist", 1000000000, COINS, TitleType.BUYABLES), // Legendary
		THE_BETRAYER("<col=7F07C3>The Betrayer", 2000000000, COINS, TitleType.BUYABLES), // Legendary
		GODFATHER("<col=f2f2f2>The Godfather", 2000000000, COINS, TitleType.BUYABLES, true), // Special
		CORLEONE("<col=f2f2f2>Corleone", 2000000000, COINS, TitleType.BUYABLES, true), // Special
		RICHIE("<col=FEFEFE>Richie", 2000000000, COINS, TitleType.BUYABLES), // Special
		VAINGLORIOUS("<col=<col=94930>Vainglorious", 50_000, BLOOD_MONEY, TitleType.BUYABLES), // Special
		MMA("<col=F80808>MMA", 1_000_000, BLOOD_MONEY, TitleType.BUYABLES), // Special
		UFC("<col=F80808>UFC", 1_000_000, BLOOD_MONEY, TitleType.BUYABLES), // Special
		ASIAN("<col=F0EEFF>Asian", 25, VOTING_TICKET, TitleType.BUYABLES), // Special
		TZHAAR("<col=9c8722>TzHaar", 25_000, TOKKUL, TitleType.BUYABLES), // Special
        /**
         * Other
         */
		ONE_LIFE("<col=cf2e02>One Life", "Available to One life mode.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getGameMode().isOneLife();
			}
		},
		REALISM("<col=e69202>Realism", "Available to Realism mode.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getGameMode().isRealism();
			}
		},
		CLASSIC("<col=7a7874>Classic", "Available to Classic mode.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getGameMode().isClassic();
			}
		},
		PURE("<col=911313>Pure", "Available to Pure mode.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getGameMode().isPure();
			}
		},
		MASTER("<col=b015d6>Master", "Available to Master mode.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getGameMode().isMaster();
			}
		},
		SPAWN("<col=fcbd00>Spawn", "Available to Spawn mode.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getGameMode().isSpawn();
			}
		},
		SERVER_SUPPORTER("@blu@Server Support", "Available to Server supporter rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getRights() == PlayerRights.SERVER_SUPPORTER;
			}
		},
		MODERATOR("<col=696969>Moderator", "Available to game Moderator rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getRights() == PlayerRights.MODERATOR;
			}
		},
		GLOBAL_MOD("@blu@Global Mod", "Available to Global moderator rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getRights() == PlayerRights.GLOBAL_MODERATOR;
			}
		},
		ADMIN("@yel@Admin", "Available to Administrator rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return PlayerUtil.isHighStaff(player);
			}
		},
		CO_OWNER("@red@Co Owner", "Available to Co-owner rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getRights() == PlayerRights.CO_OWNER;
			}
		},
		OWNER("@red@Owner", "Available to Owner rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getRights() == PlayerRights.OWNER || player.getRights() == PlayerRights.DEVELOPER;
			}
		},
		DEVELOPER("@blu@Developer", "Available to Developer rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getRights() == PlayerRights.DEVELOPER;
			}
		},
		CAMPAIGN_DEV("@blu@Campaign Dev", "Available to Campaign developer rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.CAMPAIGN_DEVELOPER, false);
			}
		},
		CONTRIBUTOR("@mag@Contributor", "Available to Contributor rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.VETERAN, false);
			}
		},
		MOTM("<col=28998d>MOTM", "Available to Member of the month.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.MOTM, false);
			}
		},
		BRONZE_MEMBER("<col=873600>Bronze member", "Available to Bronze member.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return PlayerUtil.isBronzeMember(player);
			}
		},
		RUBY_MEMBER("@red@Ruby member", "Available to Ruby member.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return PlayerUtil.isRubyMember(player);
			}
		},
		TOPAZ_MEMBER("@blu@Topaz member", "Available to Topaz member.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return PlayerUtil.isTopazMember(player);
			}
		},
		AMETHYST_MEMBER("<col=ff00ff>Amethyst member", "Available to Amethyst member.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return PlayerUtil.isAmethystMember(player);
			}
		},
		LEGENDARY_MEMBER("<col=F3D200>Legendary member", "Available to Legendary member.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return PlayerUtil.isLegendaryMember(player);
			}
		},
		PLATINUM_MEMBER("<col=dfe6f2>Platinum member", "Available to Platinum member.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return PlayerUtil.isPlatinumMember(player);
			}
		},
		TITANIUM_MEMBER("<col=00FFFF>Titanium member", "Available to Titanium member.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return PlayerUtil.isTitaniumMember(player);
			}
		},
		DIAMOND_MEMBER("<col=D3DADB>Diamond member", "Available to Diamond member.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return PlayerUtil.isDiamondMember(player);
			}
		},
		DICER("@whi@Dicer", "Available to Dicer rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.isDicer();
			}
		},
		YOUTUBER("@red@Youtuber", "Available to Youtuber rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.YOUTUBER, false);
			}
		},
		WIKI_EDITOR("@whi@Wiki Editor", "Available to Wiki editor rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.WIKI_EDITOR, false);
			}
		},
		DESIGNER("@cya@Designer", "Available to Designer rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.DESIGNER, false);
			}
		},
		MIDDLEMAN("@whi@Middleman", "Available to Middleman rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.MIDDLEMAN, false);
			}
		},
		EVENT_HOST("Event Host", "Available to Event host rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.EVENT_HOST, false);
			}
		},
		VETERAN("Veteran", "Available to Veteran rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.VETERAN, false);
			}
		},
		EX_STAFF("Ex-Staff", "Available to Ex staff rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.EX_STAFF, false);
			}
		},
		RESPECTED("@or2@Respected", "Available to Respected rank.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return EntityExtKt.getBoolean(player, Attribute.RESPECTED, false);
			}
		},
		IRONMAN("@bla@Ironman", "Available to Iron Man.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getGameMode().isIronman();
			}
		},
		HARDCORE_IRONMAN("@bla@HCIM", "Available to Hardcore Iron Man.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getGameMode().isHardcore();
			}
		},
		ULTIMATE_IRONMAN("@bla@UIM", "Available to Ultimate Iron Man.", TitleType.OTHER) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getGameMode().isUltimate();
			}
		},
		/**
		 * Custom Paid Titles
		 */
		ONE_HP("<col=00FFFF>BIGSHOT</col>", "Custom made title for Runestake.", TitleType.CUSTOM) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getUsername().equals("Runestake");
			}
		},
		/*DENCH_GANG("@bla@Dench Gang", "Custom made title for Meta Knight.", TitleType.CUSTOM) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getUsername().equals("Meta Knight");
			}
		},*/
		BLACK_BULLS("<col=513958>B</col><col=000000>lack <col=513958>B</col><col=000000>ulls</col>", "Custom made title for Nacht.", TitleType.CUSTOM) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getUsername().equals("Nacht");
			}
		},
		//SHERLOCK("@red@S</col>@whi@H</col>@red@E</col>@whi@R</col>@red@L</col>@whi@O</col>@red@C</col>@whi@K", "Custom made title for Dkick.", TitleType.CUSTOM) {
		SHERLOCK("@whi@S H E R</col> @red@L O C K</col>", "Custom made title for Dkick.", TitleType.CUSTOM) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getUsername().equals("Dkick");
			}
		},
		PAPI("<col=ff007f>PAPI</col>", "Custom made title for Pingas.", TitleType.CUSTOM) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getUsername().equals("Pingas");
			}
		}

		/*ROLL_TIDE_ROLL("<col=DC143C>Roll Tide Roll", "Custom made title for Alabama.", TitleType.CUSTOM) {
			@Override
			public boolean isUnlocked(Player player) {
				return player.getUsername().equals("Alabama");
			}
		}*/
		;

		private String title;
		private String description;
		private String splitDescription;
        private int cost;
		private int currency; // item id (e.g. 995 = coins)
		private TitleType type;
		private boolean isSuffix;

		PlayerTitle(String title, String description, TitleType type) {
			this.setTitle(title);
			this.setDescription(description);
			this.setType(type);
		}

		PlayerTitle(String title, String description, TitleType type, boolean isSuffix) {
			this.setTitle(title);
			this.setDescription(description);
			this.setType(type);
			this.setSuffix(isSuffix);
		}

		PlayerTitle(String title, int cost, int currency, TitleType type) {
			this.setTitle(title);
			this.setDescription("Purchasable for " + Misc.getTotalAmount(cost) + " " + ItemDefinition.forId(currency).getName().toLowerCase() + ".");
			this.setCost(cost);
			this.setCurrency(currency);
			this.setType(type);
		}

        PlayerTitle(String title, int cost, int currency, TitleType type, boolean isSuffix) {
            this.setTitle(title);
            this.setDescription("Purchasable for " + Misc.getTotalAmount(cost) + " " + ItemDefinition.forId(currency).getName().toLowerCase() + ".");
            this.setCost(cost);
            this.setCurrency(currency);
            this.setType(type);
			this.setSuffix(isSuffix);
        }

		public String getTitle() {
			return title;
		}

		public String getTitleWithColor() {
			return "<col=" + String.format("%06X", (0xFFFFFF & getType().getColor())) + ">" + title + "</col>";
		}

		public String getTitleWithColorWithSuffix() {
			return (isSuffix() ? "@su" : "") + "<col=" + String.format("%06X", (0xFFFFFF & getType().getColor())) + ">" + title + "</col>";
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getSplitDescription() {
			return splitDescription;
		}

		public void setSplitDescription(String splitDescription) {
			this.splitDescription = splitDescription;
		}

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public int getCurrency() {
            return currency;
        }

        public void setCurrency(int currency) {
            this.currency = currency;
        }

        public TitleType getType() {
			return type;
		}

		public void setType(TitleType type) {
			this.type = type;
		}

		public boolean isSuffix() {
			return isSuffix;
		}

		public void setSuffix(boolean suffix) {
			isSuffix = suffix;
		}

		// Override this for non buyable titles with their requirement boolean
		public boolean isUnlocked(Player player) {
			return player.getPurchasedTitles().contains(getTitle());
		}

		public boolean isBuyable() {
		    return cost > 0;
        }

        public boolean isSelected(Player player) {
		    return player.getTitle().equalsIgnoreCase(getTitleWithColorWithSuffix());
        }

        public void buy(Player player) {
			if (!isBuyable() || player.getInterfaceId() != INTERFACE_ID)
				return;

			if (player.getGameMode().isSpawn()) {
				player.sendMessage("You are not allowed to buy a title on spawn game mode.");
				return;
			}
			if (player.getGameMode().isPure()) {
				player.sendMessage("You are not allowed to buy a title on pure game mode.");
				return;
			}
			if (player.getGameMode().isOneLife()) {
				player.sendMessage("You are not allowed to buy a title on One life game mode.");
				return;
			}
			if (player.getGameMode().isMaster()) {
				player.sendMessage("You are not allowed to buy a title on master game mode.");
				return;
			}

			int cost = getCost();

			// Get the player's currency amount..
			int currencyAmount = player.getInventory().getAmount(getCurrency());

			// Check if we can afford the item or not.
			if (currencyAmount < cost) {
				player.getPacketSender().sendMessage("You don't have enough " + ItemDefinition.forId(getCurrency()).getName().toLowerCase() + " to buy that.", 1000);
				return;
			}

			// Deduct player's currency..
			player.getInventory().delete(getCurrency(), getCost());

			// Unlock and select the title
			player.getPurchasedTitles().add(getTitle());
			select(player);

			// Send a message to the player
			player.getPacketSender().sendMessage("You bought the title '" + getTitleWithColor() + "' for " + getCost() + " " + ItemDefinition.forId(getCurrency()).getName() + ".");
		}

		public void deselect(Player player) {
			if (player.getInterfaceId() != INTERFACE_ID)
				return;

			if (player.getGameMode().isSpawn() && title.equals("<col=fcbd00>Spawn")) {
				player.sendMessage("You are not allowed to change your title on spawn game mode.");
				return;
			}
			if (player.getGameMode().isPure() && title.equals("<col=911313>Pure")) {
				player.sendMessage("You are not allowed to change your title on pure game mode.");
				return;
			}
			if (player.getGameMode().isOneLife() && title.equals("<col=cf2e02>One Life")) {
				player.sendMessage("You are not allowed to change your title on One life game mode.");
				return;
			}
			if (player.getGameMode().isMaster() && title.equals("<col=b015d6>Master")) {
				player.sendMessage("You are not allowed to change your title on master game mode.");
				return;
			}

			if (player.hasTitle() && getTitlesWithColorWithSuffix().get(player.getTitle()) != null) {
				int id = getTitlesWithColorWithSuffix().get(player.getTitle()).getInterfaceId();
				player.getPacketSender().sendTooltip(id + BUTTON_ID_OFFSET, "Select");
				player.getPacketSender().sendString(id + BUTTON_TEXT_ID_OFFSET, "Select");
			}

			player.setTitle("");
			player.updateAppearance();
			player.sendMessage("You have deselected your title.");
			player.getPacketSender().sendString(PREVIEW_TEXT_ID, PlayerUtil.getNameWithTitle(player));
		}

		public void select(Player player) {
			if (player.getInterfaceId() != INTERFACE_ID)
				return;

			if (player.getGameMode().isSpawn() && !title.equals("<col=fcbd00>Spawn")) {
				player.sendMessage("You are not allowed to change your title on spawn game mode.");
				return;
			}
			if (player.getGameMode().isPure() && title.equals("<col=911313>Pure")) {
				player.sendMessage("You are not allowed to change your title on pure game mode.");
				return;
			}
			if (player.getGameMode().isOneLife() && title.equals("<col=cf2e02>One Life")) {
				player.sendMessage("You are not allowed to change your title on One life game mode.");
				return;
			}
			if (player.getGameMode().isMaster() && title.equals("<col=b015d6>Master")) {
				player.sendMessage("You are not allowed to change your title on master game mode.");
				return;
			}

			if (player.hasTitle() && getTitlesWithColorWithSuffix().get(player.getTitle()) != null) {
				int id = getTitlesWithColorWithSuffix().get(player.getTitle()).getInterfaceId();
				player.getPacketSender().sendTooltip(id + BUTTON_ID_OFFSET, "Select");
				player.getPacketSender().sendString(id + BUTTON_TEXT_ID_OFFSET, "Select");
			}

			player.getPacketSender().sendTooltip(getInterfaceId() + BUTTON_ID_OFFSET, "Deselect");
			player.getPacketSender().sendString(getInterfaceId() + BUTTON_TEXT_ID_OFFSET, "Deselect");

			player.sendMessage("You have changed your title to " + getTitleWithColor() +".");
            player.setTitle(getTitleWithColorWithSuffix());
            player.updateAppearance();
			player.getPacketSender().sendString(PREVIEW_TEXT_ID, PlayerUtil.getNameWithTitle(player));
		}

		// id of the title text
		public int getInterfaceId() {
			int id = SCROLL_START_ID;
			for (int i = 0; i < getType().ordinal(); i++)
				id += TitleType.values()[i].getTitles().size() * TITLE_INCREMENT + PANEL_INCREMENT;
			return id + (getType().getTitles().indexOf(this) * TITLE_INCREMENT + PANEL_INCREMENT);
		}

		public static Map<String, PlayerTitle> titlesWithColorWithSuffix = new HashMap<>();

		public static Map<String, PlayerTitle> getTitlesWithColorWithSuffix() {
			return titlesWithColorWithSuffix;
		}

		public static void load() {
			for (PlayerTitle title : PlayerTitle.values()) {
				// Generate split descriptions
				StringBuffer result = new StringBuffer();
				Matcher matcher = WORD_PATTERN.matcher(title.getDescription());
				int lastSplitIndex = 0;
				while (matcher.find()) {
					if ((matcher.end() - lastSplitIndex) * 5 > 85) {
						matcher.appendReplacement(result, "\\\\n" + matcher.group(2));
						lastSplitIndex = matcher.start();
					}
				}
				matcher.appendTail(result);
				title.setSplitDescription(result.toString());

				// Build title lists
				title.getType().getTitles().add(title);

				titlesWithColorWithSuffix.put(title.getTitleWithColorWithSuffix(), title);
			}
		}

	}

	/**
	 * Displays the interface
	 * 
	 * @param player
	 *            the player
	 */
	public static void display(Player player) {
		int id = SCROLL_START_ID;
		for (TitleType type : TitleType.values()) {
			player.getPacketSender().sendScrollbarHeight(id,  Math.max(273, 111 * ((int) Math.ceil(type.getTitles().size() / 3.0)) + 5 + 46));
			id += PANEL_INCREMENT;
			for (PlayerTitle title : type.getTitles()) {
			    boolean selected = title.isSelected(player);
                boolean unlocked = title.isUnlocked(player);
                boolean buyable = title.isBuyable();
				player.getPacketSender().sendString(id++, title.getTitleWithColor());
				player.getPacketSender().sendString(id++, title.getSplitDescription());
				player.getPacketSender().sendTooltip(id++, selected ? "Deselect" : unlocked ? "Select" : buyable ? "Buy" : "");
				player.getPacketSender().sendString(id++, selected ? "Deselect" : unlocked ? "Select" : buyable ? "Buy" : "Locked");
			}
		}
		player.getPacketSender().sendString(PREVIEW_TEXT_ID, PlayerUtil.getNameWithTitle(player));
		player.getPacketSender().sendInterface(INTERFACE_ID);
	}

	public static boolean handleButton(Player player, int button) {
		int id = SCROLL_START_ID + BUTTON_ID_OFFSET;
		for (TitleType type : TitleType.values()) {
			id += PANEL_INCREMENT;
			if (button >= id && button < id + type.getTitles().size() * TITLE_INCREMENT + PANEL_INCREMENT) {
				int index = (button - id) / TITLE_INCREMENT;
                PlayerTitle title = type.getTitles().get(index);
                boolean selected = title.isSelected(player);
                boolean unlocked = title.isUnlocked(player);
                boolean buyable = title.isBuyable();
                if (selected) {
                	title.deselect(player);
                } else if (unlocked) {
					title.select(player);
				} else if (buyable) {
					title.buy(player);
				}
				return true;
			}
			id += type.getTitles().size() * TITLE_INCREMENT;
		}
		return false;
	}
}
