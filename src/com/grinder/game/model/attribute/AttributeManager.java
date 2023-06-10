package com.grinder.game.model.attribute;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.Misc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Handles attributes for players
 * 
 * @author 2012
 *
 */
public class AttributeManager {

	/**
	 * The attribute
	 */
	private HashMap<Points, Integer> attribute = new HashMap<>();

	/**
	 * The player
	 */
	private Player player;

	/**
	 * The attribute mananger
	 * 
	 * @param player
	 *            the player
	 */
	public AttributeManager(Player player) {
		this.setPlayer(player);
	}

	/**
	 * The points
	 */
	public enum Points {
		/*
		 * Main points
		 */
		GRINDERSCAPE_POINTS, PREMIUM_POINTS(31643), PARTICIPATION_POINTS(31645), YELL_POINTS(31651), SKILLING_POINTS(
				31653), ACHIEVEMENT_POINTS, ACHIEVEMENT_POINTS_NEW, BOSS_CONTRACT_POINTS(31655),
		/*
		 * Misc
		 */

		COX_COMPLETIONS,

		RAID_TOTAL_KILLS(false),
		QUEST_POINTS,
		DRAYNOR_ROOFTOP_LAPS,
		BARROWS_CHEST(31677), PET, COMMENDATION, SLAYER_NPC_KILLS(31673), DUEL_WINS(31797), DUEL_LOSES(31799), DICE_WINS, DICE_LOSES, MINIGAME_POINTS,
		FINISHED_CLUE_SCROLLS(31801),
		CHOPPED_TREES(31679),
		ORES_MINED(31687),
		GEMS_CUT(31691),
		SEEDS_PLANTED(31693),
		PATCHES_RAKED(31695),
		HERBS_CLEANED(31697),
		POTIONS_CREATED(31699),
		LAPS_COMPLETED(31709),
		GNOME_LAPS(31701),
		BARABARIAN_LAPS(31703),
		WILDERNESS_LAPS(31705),
		PYRAMID_LAPS(31707),
		AL_KHARID_ROOFTOP_LAPS,
		VARROCK_ROOFTOP_LAPS,
		CANIFIS_ROOFTOP_LAPS,
		FALADOR_ROOFTOP_LAPS,
		SEERS_ROOFTAP_LAPS,
		POLLNIVEACH_ROOFTOP_LAPS,
		RELLEKKA_ROOFTOP_LAPS,
		ARDOUGNE_ROOFTOP_LAPS,
		WILDERNESS_SPIRIT_SLAIN(31675),
		PORAZDIR_BOSS_SLAY_COUNT,
		FAILED_PICKPOCKETS(31713),
		SUCCESFUL_PICKPOCKETS(31711),
		STALL_STEALS(31715),
		RUNES_CRAFTED(31717),
		BARS_SMELTED(31689),
		FISHES_CAUGHT(31719),
		SUCCESFULL_COOKS(31721),
		COOKING_FAILURES(31723),
		LOGS_BURNED(31725),
		TELETABS_USED(31727),
		ITEMS_PICKED_UP(31781),
		ITEMS_DROPPED(31779),
		RUN_OUT_OF_PRAYER_TIMES(31729),
		LOL_MESSAGE_TIMES,
		CLAN_CHAT_MESSAGES(31731),
		TELE_OTHER_CASTED(31733),
		BOLTS_ENCHANTED(31755),
		MAGIC_TREES_CUT,
		RUNE_ORES_MINED,
		TRADES_COMPLETED(31803),
		WIZARD_TELEPORTS(31735), // Total times teleported by the wizard
		GAMBLES_GAMBLED,
		GAMBLES_WON(31793),
		GAMBLES_LOST(31795),
		GAMBLE_WIN_STREAK,
		BONES_BURIED(31681),
		BONES_USED_ON_ALTAR(31683),
		RECHARGED_PRAYER_TIMES(31685),
		EMOTES_PLAYED(31775),
		YELLS_YELLED(31773),
		FOOD_EATEN(31757),
		POTIONS_DRANK(31759),
		GLORY_TELEPORTS(31777),
		LADDERS_CLIMBED(31771),
		CRYSTAL_CHESTS_OPENED(31765),
		MUDDY_CHESTS_OPENED(31761),
		BRIMSTONE_CHESTS_OPENED(31763),
		RARE_DROPS_RECEIVED(31767),
		RDT_DROPS_RECEIVED(31769),
		PETS_RECEIVED(31783),
		ALREADY_EXISTING_PET_COUNT,
		FIGHT_CAVES_COMPLETED(31751),
		BOSS_CONTRACTS_FINISHED(31785),
		BOSS_CONTACTS_FAILED(31787),
		MINIGAMES_WON(31789),
		MINIGAMES_LOST(31791),
		MINIGAMES_WON_STREAK,
		MINIGAMES_HIGHEST_WON_STREAK,
		//DUEL_LOSSES,
		DUEL_WIN_STREAK,
		DUEL_HIGHEST_WIN_STREAK,
		SPELLS_CASTED(31745),
		CHARGE_SPELL_CASTED(31747),
		BONES_SPELL_CASTS,
		LOW_ALCHEMY_CASTS,
		HIGH_ALCHEMY_CASTS(31737),
		VENG_CASTS(31741),
		SPELL_BOOK_TELEPORTS,
		WILDERNESS_BOOK_TELEPORTS,
		SUPERHEAT_SPELL_CASTS(31743),
		SPECIAL_ATTACKS_USED(31739),
		TOTAL_MYSTERY_BOXES_OPENED(31753),
		REGULAR_BOXES_COUNT,
		SUPER_BOXES_COUNT,

		FIFTY_BOXES_COUNT,
		HUNDRED_BOXES_COUNT,
		BARROWS_BOXES_COUNT,
		PVP_BOXES_COUNT,
		VOTING_BOXES_COUNT,
		EXTREME_BOXES_COUNT,
		QUESTION_BOXES_COUNT, // Renamed to Sacred mystery box
		FIFTY_DOLLARS_BOXES_COUNT, // Renamed to VIP Mystery box
		HUNDRED_DOLLARS_BOXES_COUNT,
		GILDED_BOXES_COUNT,
		LEGENDARY_BOXES_COUNT,
		COMMANDS_USED_COUNT,
		TOTAL_PARTICIPATION_POINTS_RECEIVED,
		TOTAL_SKILLING_POINTS_RECEIVED,
		TOTAL_SLAYER_POINTS_RECEIVED,
		AQUAIS_NEIGE_GAMES_COMPLETED(31749),
		LARRAN_CHESTS_OPENED(31805),
		ITEMS_ON_TABLE,
		TELEKINITIC_CASTS,
		OPENED_GIFT_OF_PEACE,
		OPENED_GRAIN_OF_PLENTY,
		OPENED_BOX_OF_HEALTH,
		OPENED_CRADLE_OF_LIFE,
		BRIMHAVEN_AGILITY_TAGS_COMPLETED,
		CASTLEWARS_WON_GAMES,
		CASTLEWARS_LOST_GAMES,
		PEST_CONTROL_WINS,
		PEST_CONTROL_LOSSES,
		/*
		 * Minigame attributes
		 */
		WEAPON_MINIGAME(false), MINIGAME_DEATH_STREAK(false),
		/*
		 * PvP
		 */
		KILLS(31621), DEATHS(31623), KILLSTREAK(31627), HIGHEST_KILLSTREAK(31629),
		TOTAL_BM, // UNUSED
		/*
		 * Slayer
		 */
		SLAYER_POINTS(31647), SLAYER_STREAK(31649),
		/*
		 * Voting
		 */
		VOTING_POINTS(31634), TOTAL_VOTES, VOTING_STREAK(31636),

		;

		/**
		 * The string id
		 */
		private int id;

		/**
		 * Whether to save
		 */
		private boolean save;

		/**
		 * Sets the id
		 * 
		 * @param id
		 *            the id
		 */
		Points(int id) {
			this.setId(id);
			this.setSave(true);
		}

		/**
		 * No id
		 */
		Points() {
			this.setId(0);
			this.setSave(true);
		}

		/**
		 * Saving setting
		 */
		Points(boolean safe) {
			this.setId(0);
			this.setSave(safe);
		}

		/**
		 * Sets the id
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Sets the id
		 * 
		 * @param id
		 *            the id
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * Sets the save
		 *
		 * @return the save
		 */
		public boolean isSave() {
			return save;
		}

		/**
		 * Sets the save
		 * 
		 * @param save
		 *            the save
		 */
		public void setSave(boolean save) {
			this.save = save;
		}
	}

	/**
	 * Send info on login
	 * 
	 * @param player
	 *            the player
	 */
	public static void onLogin(Player player) {

	}

	/**
	 * The kdr format
	 */
	private static final NumberFormat KDR = new DecimalFormat("#0.00");

	/**
	 * Gets the KDR
	 * 
	 * @return the KDR
	 */
	public String getKDR() {
		int kills = get(Points.KILLS);
		int deaths = get(Points.DEATHS);
		if (kills == 0 && deaths > 0) {
			return KDR.format((double) 1 / (double) deaths);
		}
		if (deaths == 0 && kills > 0) {
			return KDR.format((double) kills / (double) 1);
		}
		if (deaths == 0 && kills == 0) {
			return "1.00";
		}
		return KDR.format((double) kills / (double) deaths);
	}

	/**
	 * Sends the chat
	 * 
	 * @param player
	 *            the player
	 * @param button
	 *            the button
	 * @return the chat
	 */
	public static boolean handleButton(Player player, int button) {
		/*
		 * Loops through the points
		 */
		for (Points points : Points.values()) {
			/*
			 * No display
			 */
			if (points.getId() - 1 == button) {
				/*
				 * Name
				 */
				String name = Misc.formatName(points.name().toLowerCase());
				/*
				 * Suffix
				 */
				String suffix = name.endsWith("s") ? "are" : "is";
				/*
				 * Chat
				 */
				String string = "My " + name + " " + suffix + " " + player.getPoints().display(points);
				player.getPacketSender().sendQuickChat(string);
				player.getPacketSender().sendString(points.getId(), player.getPoints().display(points));
				return true;
			}
		}
		return false;
	}

	/**
	 * Sends the tab
	 * 
	 * @param player
	 *            the player
	 */
	public static void sendTab(Player player) {
		/*
		 * Loops through the points
		 */
		for (Points points : Points.values()) {
			/*
			 * No display
			 */
			if (points.getId() == 0) {
				continue;
			}
			player.getPacketSender().sendString(points.getId(), player.getPoints().display(points));
		}
	}

	/**
	 * Displays in format
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	public String display(Points key) {
		return NumberFormat.getInstance().format(get(key));
	}

	/**
	 * Creats a point
	 * 
	 * @param key
	 *            the key
	 */
	public void create(Points key) {
		attribute.put(key, 0);
	}

	/**
	 * Gets the points
	 * 
	 * @param key
	 *            the key
	 * @return the points
	 */
	public int get(Points key) {
		return attribute.get(key) == null ? 0 : attribute.get(key);
	}

	/**
	 * Checking whether its true
	 * 
	 * @param key
	 *            the key
	 * @return true
	 */
	public boolean check(Points key) {
		return attribute.get(key) == null ? false : attribute.get(key).intValue() == 1;
	}

	/**
	 * Swaps the conditions around
	 * 
	 * @param key
	 *            the key
	 */
	public void toggle(Points key) {
		set(key, !check(key));
	}

	/**
	 * Sets the key state
	 * 
	 * @param key
	 *            the key
	 * @param state
	 *            the state
	 */
	public void set(Points key, boolean state) {
		if (attribute.get(key) == null) {
			create(key);
		}
		attribute.put(key, state ? 1 : 0);
	}

	/**
	 * Sets the points
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void set(Points key, int value) {
		if (attribute.get(key) == null) {
			create(key);
		}
		attribute.put(key, value);
	}

	/**
	 * Increases points
	 * 
	 * @param key
	 *            the key
	 */
	public void increase(Points key) {
		increase(key, 1);
	}

	/**
	 * Increases points
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void increase(Points key, int value) {
		if (attribute.get(key) == null) {
			create(key);
		}
		switch (key) {
		//case ACHIEVEMENT_POINTS:
		//case ACHIEVEMENT_POINTS_NEW:
		case GRINDERSCAPE_POINTS:
		//case SLAYER_POINTS:
		case YELL_POINTS:
		case BOSS_CONTRACT_POINTS:
		//case MINIGAME_POINTS:
			player.getPacketSender().sendMessage("<img=766> You have received @dre@" + value + " " + Misc.formatName(key.name().toLowerCase()) + "</col>.");
			break;
		case VOTING_POINTS:
			player.getPacketSender().sendMessage("<img=766> You have redeemed @dre@" + value + " " + Misc.formatName(key.name().toLowerCase()) + "</col>.");
			default:
			break;
		
		}
		attribute.put(key, attribute.get(key) + value);
	}

	/**
	 * Decreases points
	 * 
	 * @param key
	 *            the key
	 * @return the decrease
	 */
	public boolean decrease(Points key) {
		if (attribute.get(key) == null) {
			create(key);
		}
		if (attribute.get(key) < 1) {
			return false;
		}
		set(key, attribute.get(key) - 1);
		return true;
	}

	/**
	 * Decreases points per amount
	 * 
	 * @param key
	 *            the key
	 * @param amount
	 *            the amount
	 */
	public void decrease(Points key, long amount) {
		for (int i = 1; i <= amount; i++) {
			if (!decrease(key)) {
				break;
			}
		}
	}

	/**
	 * Clears a selection of keys
	 * 
	 * @param keys
	 *            the keys
	 */
	public void clear(Points[] keys) {
		for (Points key : keys) {
			attribute.remove(key);
		}
	}

	/**
	 * Sets the points
	 *
	 * @return the points
	 */
	public HashMap<Points, Integer> get() {
		return attribute;
	}

	/**
	 * Sets the points
	 * 
	 * @param points
	 *            the points
	 */
	public void set(HashMap<Points, Integer> points) {
		this.attribute = points;
	}

	/**
	 * Sets the player
	 *
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the player
	 * 
	 * @param player
	 *            the player
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
}
