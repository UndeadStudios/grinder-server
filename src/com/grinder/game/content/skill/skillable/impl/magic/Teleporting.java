package com.grinder.game.content.skill.skillable.impl.magic;

import com.grinder.game.GameConstants;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.impl.BossInstances;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Handles teleporting interface
 * 
 * @author 2012
 *
 */
public class Teleporting {

	/**
	 * The string id
	 */
	private static final int PREVIOUS_TELEPORT_TEXT = 47006;

	/**
	 * The previous teleport
	 */
	private ArrayList<PreviousTeleport> previousTeleports = new ArrayList<PreviousTeleport>();

	/**
	 * The navigations
	 */
	public enum Navigation {
		WILDERNESS(47017, 46000),

		TRAINING(47023, 46001),

		MINIGAME(47041, 46002),

		SKILLING(47035, 46003),

		CITIES(47011, 46004),

		BOSSES(47029, 46005),

		SLAYER(47047, 46006)

		;

		/**
		 * The button id
		 */
		private int button;

		/**
		 * The interface id
		 */
		private int interfaceId;

		/**
		 * Represents a new teleport navigation
		 * 
		 * @param button
		 *            the button
		 * @param interfaceId
		 *            the interface id
		 */
		Navigation(int button, int interfaceId) {
			this.setButton(button);
			this.setInterfaceId(interfaceId);
		}

		/**
		 * Sets the button
		 *
		 * @return the button
		 */
		public int getButton() {
			return button;
		}

		/**
		 * Sets the button
		 * 
		 * @param button
		 *            the button
		 */
		public void setButton(int button) {
			this.button = button;
		}

		/**
		 * Sets the interfaceId
		 *
		 * @return the interfaceId
		 */
		public int getInterfaceId() {
			return interfaceId;
		}

		/**
		 * Sets the interfaceId
		 * 
		 * @param interfaceId
		 *            the interfaceId
		 */
		public void setInterfaceId(int interfaceId) {
			this.interfaceId = interfaceId;
		}

		/**
		 * Gets for button
		 * 
		 * @param button
		 *            the button
		 * @return the navigation
		 */
		public static Navigation forButton(int button) {
			return Arrays.stream(values()).filter(c -> c.getButton() == button).findFirst().orElse(null);
		}
	}

	/**
	 * Teleport location
	 */
	public enum TeleportLocation {


		/*
		* Combat Training
		 */

		LUMBRIDGE_FARM(89002, new Position(3254, 3266, 0), false),

		NEITIZNOT_YAKS(89010, new Position(2323, 3793), false),

		RELLEKA_CRABS(89018, new Position(2673, 3712), false),

		SOUTH_HOSIDIUS(89026, new Position(1715, 3462), false),

		FENKENSTRAIN_CASTLE(89034, new Position(3574, 9929), false),

		AL_KHARID_TEMPLE(89042, new Position(3293, 3181), false),

		STRONGHOLD_SECURITY(89050, new Position(1860, 5244), false),

		VARROCK_SEWERS(89058, new Position(3237, 9858), false),

		EDGEVILLE_DUNGEON(89066, new Position(3115, 3449), false),

		GNOME_TORTOISE(89074, new Position(2426, 3520), false),

		CANIFIS_WEREWOLVES(89082, new Position(3489, 3482), false),

		FALADOR_MOLE_LAIR(89090, new Position(2997, 3376), false),

		POTHOLE_DUNGEON(89098, new Position(2824, 3120), false),

		KHAZARD_BATTLEFIELD(89106, new Position(2605, 3153), false),

		KALPHITE_LAIR(89114, new Position(3312, 9503), false),

		TARNS_LAIR(89122, new Position(3186, 4637, 0), false),

		ICE_CANYON(89130, new Position(2886, 3759, 0), false),

		ASGARNIAN_ICE_CAVE(89138, new Position(3048, 9581), false),

		CROCODILE_CAMP(89146, new Position(3345, 2942), false), // Crocodiles

		KARAMJA_DUNGEON(89154, new Position(2856, 3167), false),

		FREMENNIK_ISLES(89162, new Position(2348, 3883), false),

		CRASH_ISLAND(89170, new Position(2894, 2724), false),

		SMOKE_TUNNELS(89178, new Position(3371, 2903), false),

		TAVERLEY_DUNGEON(89186, new Position(2884, 3401), false),

		BRIMHAVEN_DUNGEON(89194, new Position(2757, 3177), false), //2713, 9564

		DAGANNOTHS_LAIR(89202, new Position(2453, 10145), false),

		APEL_ATOLL_DUNGEON(89210, new Position(2766, 2703), false),

		BANDITS_CAMP(89218, new Position(3170, 2979), false),

		TZHAAR(89226, new Position(2443, 5171), false),

		MOR_UI_REK(89234, new Position(2457, 5124), false),

		SUQAH_DIPLOMACY(89242, new Position(2134, 3856), false),

		ANCIENT_DUNGEON(89250, new Position(1764, 5366, 1), false),

		MOUNT_FIREWAKE(89258, new Position(1311, 3804, 0), false),

		SMOKE_DEVIL_DUNGEON(89274, new Position(2412, 3056, 0), false), // SMOKE DEVIL DUNGEON & THERMO

		LITHKREN_VAULT(89282, new Position(1567, 5061, 0), false),

		SHADOW_DUNGEON(89290, new Position(2630, 5072), false),

		/*
		* Minigames
		 */

		BARROWS(47602, new Position(3564, 3288), false),

		DUEL_ARENA(47610, new Position(3367, 3275), false),

		FIGHT_CAVE(47618, new Position(2438, 5171), false),

		FIGHT_PITS(47626, new Position(2399, 5178), false),
		
		WARRIORS_GUILD(47634, new Position(2880, 3546, 0), false),

		PEST_CONTROL(47642, new Position(2659, 2676), false),

		FUN_PVP_ZONE(47650, new Position(3323, 4969, 0), false),

		MEDALLION_CASINO(47658, new Position(2848, 2585), false), // Gambling Zone

		BLAST_FURNACE(47666, new Position(1939, 4959, 0), false),

		MOTHERLORDE_MINE(47674, new Position(3755, 5662, 0), false),

		AQUAIS_NEIGE(47682, new Position(1324, 3013, 0), false),

		CASTLE_WARS(47690, new Position(2440, 3087, 0), false),

		WINTERTODT(47698, new Position(1633, 3946, 0), false),

		TEARS_OF_GUTHIX(47706, new Position(3241, 9498, 2), false),

		CHAMBERS_OF_XERIC(47714, new Position(1245, 3559, 0), false),

		THEATRE_OF_BLOOD(47722, new Position(3654, 3219, 0), false),

		PORAZDIR_MINIGAME(47730, new Position(3448, 4725, 0), true),

		PURO_PURO(47738, new Position(2594, 4318, 0), false),

		/*
		* Cities
		 */
		APE_ATOLL(45802, new Position(2757, 2780), false),

		AL_KHARID(45810, new Position(3266, 3228), false),

		ARDOUGNE(45818, new Position(2660, 3302), false),

		BURTHORPE(45826, new Position(2915, 3523), false),

		CAMELOT(45834, new Position(2756, 3476), false),

		CATCHERBY(45842, new Position(2803, 3432), false),

		CANIFIS(45850, new Position(3489, 3482), false),

		DRAYNOR(45858, new Position(3077, 3248), false),

		EDGEVILLE(45866, new Position(3087, 3463), false),

		FALADOR(45874, new Position(2962, 3382), false),

		GRAND_EXCHANGE(45882, new Position(3162, 3482), false),

		MARKET_PLACE(45890, new Position(3095, 3484), false),

		KARAMJA(45898, new Position(2936, 3146), false),

		LUMBRIDGE(45906, new Position(3221, 3213), false),

		LUNAR_ISLE(45914, new Position(2147, 3864), false),

		NEITZNOT(45922, new Position(2334, 3802), false),

		POLLNIVNEACH(45930, new Position(3360, 2970), false),

		TZHAAR_CITY(45938, new Position(2443, 5171), false),

		VARROCK(45946, new Position(3209, 3421), false),

		YANILLE(45954, new Position(2605, 3091), false),

		FREMENNIK(45962, new Position(2642, 3675), false),
		
		SHILO_VILLAGE(45970, new Position(2852, 2953), false),

		WEST_ARDOUGNE(45978, new Position(2524, 3305), false),

		TWO_BWO_WANNAI(45986, new Position(2794, 3066), false),

		TREE_GNOME_STRONGHOLD( 45994, new Position(2466, 3489), false),

		/*
		* Skilling
		 */
		DRAYNOR_FOREST(44702, new Position(3088, 3237), false),

		CAMELOT_JUNGLE(44710, new Position(2750, 3464), false),

		TEAK_WOODLAND(44718, new Position(2816, 3083), false),

		HARDWOOD_GROVE(44726, new Position(2816, 3083), false),

		SORCERERS_TOWER(44734, new Position(2718, 3462), false),

		NEITZNOT_PLANTATION(44742, new Position(2353, 3797), false),

		CATHERBY_SHORE(44750, new Position(2845, 3434), false),

		KARAMJAS_DOCK(44758, new Position(2925, 3173), false),

		LUMBRIDGE_SWAMP(44766, new Position(3166, 3172), false),

		BARBARIAN_VILLAGE(44774, new Position(3104, 3430), false),

		NEITIZNOT_MINE(44782, new Position(2317, 3823), false),

		RUNE_ESSENCE_MINE(44790, new Position(2910, 4832), false),

		DWARVEN_MINE(44798, new Position(3061, 3374), false),

		AL_KHARID_MINE(44806, new Position(3299, 3278), false),

		BRIMHAVEN_PENINSULA(44814, new Position(2733, 3221), false),

		MOTHERLODE_MINE(44822, new Position(3755, 5662, 0), false),

		LIMESTONE_QUARRY(44830, new Position(3369, 3494, 0), false),

		BANDIT_CAMP_QUARRY(44838, new Position(3172, 2898, 0), false),

		TRAHAEARN_MINE(44846, new Position(3295, 12438, 0), false),

		DORGESH_KAAN_MINE(44854, new Position(3318, 9602, 0), false),

		SALT_MINE(44862, new Position(2846, 10351, 0), false),

		DONDAKANS_MINE(44870, new Position(2391, 4933, 0), false),

		ARCEUUS_ESSENCE_MINE(44878, new Position(1754, 3857, 0), false),

		DAEYALT_ESSENCE_MINE(44886, new Position(1754, 3857, 0), false),

		VARROCK_MINE(44894, new Position(3285, 3371, 0), false),

		GEMS_MINE(44902, new Position(2826, 2998, 0), false),

		GNOME_STRONGHOLD(44910, new Position(2473, 3439), false),

		BARBARIAN_OUTPOST(44918, new Position(2552, 3563), false),

		DRAYNOR_ROOFTOP(44926, new Position(3107, 3279), false),

		AL_KHARID_ROOFTOP(44934, new Position(3273, 3199), false),

		VARROCK_ROOFTOP(44942, new Position(3228, 3415), false),

		CANIFIS_ROOFTOP(44950, new Position(3504, 3485), false),

		FALADOR_ROOFTOP(44958, new Position(3037, 3339), false),

		SEERS_ROOFTOP(44966, new Position(2728, 3485), false),

		POLLNIVNEACH_ROOFTOP(44974, new Position(3351, 2959), false),

		RELLEKKA_ROOFTOP(44982, new Position(2626, 3679), false),

		ARDOUGNE_ROOFTOP(44990, new Position(2673, 3295), false),

		BRIMHAVEN_ARENA(44998, new Position(2809, 3189, 0), false),

		PYRAMID_AGILITY(45006, new Position(3333, 2827), false),

		RELLEKKA_HUNTER(45014, new Position(2722, 3782, 0), false),

		PISCATORIS_HUNTER(45022, new Position(2338, 3584), false),

		UZER_HUNTER_AREA(45030, new Position(3403, 3105), false),

		FALCONRY(45038, new Position(2376, 3596), false),

		FALDORS_FARM(45046, new Position(3056, 3310, 0), false),

		CATHERBYS_FARM(45054, new Position(2857, 3431, 0), false),

		FALADORS_OVEN(45062, new Position(2971, 3374), false),

		FLAX_FIELD(45070, new Position(2735, 3443), false),

		AIR_ALTAR(45078, new Position(2842, 4828), false),

		THE_ABYSS(45086, new Position(3062, 4817), false),

		WRATH_ALTAR(45094, new Position(2336, 4825), false),

		OURANIA_ALTAR(45102, new Position(3017, 5624), false),

		ARDOUGNE_STALLS(45110, new Position(2660, 3302), false),

		MASTER_FARMER(45118, new Position(2588, 3100), false),

		WOODCUTTING_GUILD(45126, new Position(1660, 3505, 0), false),

		FISHING_GUILD(45134, new Position(2612, 3389), false),

		COOKING_GUILD(45142, new Position(3144, 3438), false),

		DWARF_MINING_GUILD(45150, new Position(3016, 3339), false),

		CRAFTING_GUILD(45158, new Position(2933, 3297), false),

		FARMING_GUILD(45166, new Position(1248, 3718), false),

		WIZARDS_GUILD(45174, new Position(2600, 3088), false),

		ARCHERS_GUILD(45182, new Position(2655, 3441), false),

		THE_DESERTED_REEF(45190, new Position(2145, 2587, 0), false), // Bronze members

		LA_ISLA_EBANA(45198, new Position(3676, 2982, 0), false), // Ruby members

		THE_NEW_PENINSULA(45206, new Position(2047, 3684, 0), false), // Platinum members

		/*
		* Slayer
		 */

		SLAYER_MASTER_TURAEL(77002, new Position(2920, 3520), false),

		SLAYER_MASTER_MAZCHNA(77010, new Position(3492, 3486), false),

		SLAYER_MASTER_VANNAKA(77018, new Position(3096, 9867), false),

		SLAYER_MASTER_CHAELDAR(77026, new Position(3361, 2993), false),

		SLAYER_MASTER_DURADEL(77034, new Position(2870, 2966, 0), false),

		SLAYER_MASTER_KRYSTILIA(77058, new Position(3110, 3514), false),

		SLAYER_MASTER_KONAR(77042, new Position(1309, 3794), false),

		SLAYER_MASTER_NIEVE(77050, new Position(2449, 3421), false),

		SLAYER_CAVE(77066, new Position(2808, 10001), false),

		SLAYER_TOWER(77074, new Position(3429, 3524), false),

		SLAYER_STRONGHOLD(77082, new Position(2426, 9823, 0), false),

		CATACOMBS_OF_KOUREND(77090, new Position(1663, 10046), false),

		FELDIP_HILLS(77098, null, false),

		CHASM_OF_FIRE(77106, new Position(1435, 10077, 3), false),

		MOSLES_HARMLESS(77114, new Position(3733, 2972), false),

		KILLERWATT_PLANE(77122, new Position(2677, 5215, 2), false),

		KARUULM_CAVE(77130, new Position(1311, 3805, 0), false),

		LIZARDMAN_CAVE(77138, new Position(1305, 9973, 0), false),

		/*
		* Bosses
		 */
		THE_CURSED_VAULT(88002, new Position(2268, 2588), false), // Legendary boss dungeon

		OBOR(88010, new Position(3096, 9833), false),

		BRYOPHYTYA(88018, new Position(3174, 9899), false),

		CERBERUS(88026, new Position(2871, 9847), false),

		GENERAL_GRAARDOR(88034, new Position(2848, 5333, 2), false),

		KREE_ARRA(88042, new Position(2872, 5269, 2), false),

		KRIL_TSARUTH(88050, new Position(2886, 5349, 2), false),

		COMMANDER_ZILYANA(88058, new Position(2919, 5273, 0), false),

		DAGGANOTH_KINGS(88066, new Position(1915, 4367, 0), false),

		KALPHITE_QUEEN(88074, new Position(3229, 3110, 0), false),

		CORPOREAL_BEAST(88082, new Position(2967, 4382, 2), false),

		MUTANT_TARN(88090, new Position(3186, 4637, 0), false),

		BLACK_KNIGHT_TITAN(88098, new Position(2603, 3082), false),

		THE_UNTOUCHABLE(88106, new Position(2603, 3082), false),

		JUNGLE_DEMON(88114, new Position(2715, 9184, 0), false),

		ICE_TROLL_KING(88122, new Position(2838, 3803,1), false, BossInstances.ICE_TROLL),

		ICE_QUEEN(88130, new Position(2804, 3508), false),

		SEA_TROLL_QUEEN(88138, new Position(3087, 3484), false),

		GIANT_MOLE(88146, new Position(2997, 3376,0), false),

		GIANT_SEA_SNAKE(88154, new Position(2464, 4773,0), false),

		SLASH_BASH(88162, new Position(2843, 9635, 0), false),

		KAMIL(88170, new Position(2886, 3759, 0), false),

		SKELETON_HELLHOUND(88178, new Position(3303, 9375, 0), false),

		DEMONIC_GORILLAS(88186, new Position(2130, 5647), false),

		LIZARDMAN_SHAMAN(88194, new Position(1456, 3656), false),

		ALCHEMICAL_HYDRA(88202, new Position(1311, 3805, 0), false),

		VORKATH(88210, new Position(2276, 4035, 0), false),

		ZULRAH(88218, new Position(2200, 3056, 0), false),

		PORAZDIR(88226, new Position(3448, 4725, 0), true),

		GLOD(88234, new Position(2200, 3056, 0), false),

		FRAGMENT_OF_SEREN(88242, new Position(2200, 3056, 0), false),

		THE_NIGHTMARE(88250, new Position(2200, 3056, 0), false),

		NEX(88258, new Position(2904, 5203, 0), false),

		/*
		* Wilderness
		 */
		EDGEVILLE_PVP(47102, new Position(3087, 3520), false),

		MAGE_BANK(47110, new Position(2537, 4714, 0), false),

		DARK_FORTRESS(47118, new Position(3013, 3632, 0), true),

		REVENANTS_CAVE(47126, new Position(3075, 3648, 0), true),

		GREEN_DRAGS(47134, new Position(3110, 3678, 0), true),

		BANDIT_CAMP(47142, new Position(3030, 3699, 0), true),

		CRAZY_ARCHAEOLOGIST(47150, new Position(2965, 3695, 0), true),

		CHRONOZON(47158, new Position(3159, 3722, 0), true),

		CHAOS_FANATIC(47166, new Position(2978, 3830), true),

		MERODACH(47174, new Position(2978, 3941), true),

		BARRELCHEST(47182, new Position(3270, 3707, 0), true),

		VENENATIS(47190, new Position(3316, 3730), true),

		VETION(47198, new Position(3177, 3792), true),

		CALLISTO(47206, new Position(3308, 3824, 0), true),

		KING_BLACK_DRAGON(47214, new Position(3006, 3850), true),

		GALVEK(47222, new Position(3227, 3879), true),

		CHAOS_ELEMENTAL(47230, new Position(3308, 3909), true),

		SCORPIA(47238, new Position(3243, 3946), true),

		BLACK_CHINCHOMPA(47246, new Position(3143, 3772, 0), true),

		CHINCHOMPA_HILL(47254, new Position(3138, 3783, 0), true),

		CHAOS_ALTAR(47262, new Position(2959, 3821, 0), true),

		WILDERNESS_AGILITY(47270, new Position(2998, 3917, 0), true),

		WILDERNESS_THIEVING(47278, new Position(3285, 3943, 0), true),

		WILD_RESOURCE_AREA(47286, new Position(3184, 3945, 0), true),

		DEMONIC_RUINS(47294, new Position(3286, 3881, 0), true),

		DEEP_OBELISKS(47302, new Position(3307, 3912, 0), true),


		//EAST_DRAGONS(47150, new Position(3332, 3696, 0), true),

		//HILL_GIANT(47126, new Position(3292, 3644, 0), true),
		//AVATAR(47158, new Position(3244, 3789, 0), true),
		//MITHRIL_DRAGONS(47428, new Position(1777, 5344, 1), false),
		//CASTLE_WARS(47510, new Position(2440, 3083), false),
		//CLAN_WARS(47518, new Position(3366, 3168), false),
		//SCORPIA(47928, new Position(3286, 3931, 0), true),
		//MUTANT_TARN(47784, new Position(2911, 3611), false),
		//SLAYER_CAVES(44634, new Position(2808, 10001), false),
		//SLAYER_TOWERS(44642, new Position(3426, 3537), false),
		//BLACK_KNIGHT_TITAN(47824, new Position(3198, 3959), true),
		//JUNGLE_DEMON(47888, new Position(2226, 5045, 0), false),
		//CAMELOT_WOODCUTTING(44698, new Position(2725, 3485), false),


		/*
		* FUTURE TELEPORTS ROWS
		* FAVORITES
		* MONSTERS (CONTAINS ALL MONSTERS FROM LOW TO HIGHEST LEVEL ORDER)
		* DUNGEONS (DUNGEONS SORTED BY ALPHA)
		* BOSSING (SORTED BY COMBAT LEVEL)
		* CITIES (SORTED BY ALPHA)
		* SKILLING (SORTED BY SKILL TYPE)
		* MINIGAMES (SORTED BY ALPHA)
		* SKULL ICON (PLAYER VS. PLAYER)
		*
		*
		* WILDERNESS SKILLING (SORTED BY ALPHA SKILL NAME WITH DETAILS OF WHY ITS SKILL IN WILD)
		* SLAYER (SORTED BY ALPHA AND IT CONTAINS MASTERS AND DUNGEONS)
		 * GUILDS (SORTED BY ALHPA WITH REQUIREMENTS NOTED)
		 */

		;

		/**
		 * The button
		 */
		private int button;

		/**
		 * The position
		 */
		private Position position;

		/**
		 * Teleports to wild
		 */
		private boolean wild;

		/**
		 * Whether an instance can be created.
		 */
		private Optional<BossInstances> instance;

		/**
		 * Teleports to location
		 * 
		 * @param button
		 *            the button
		 * @param position
		 *            the position
		 */
		TeleportLocation(int button, Position position, boolean wild) {
			this.setButton(button);
			this.setPosition(position);
			this.setWild(wild);
			this.instance = Optional.empty();
		}

		TeleportLocation(int button, Position position, boolean wild, BossInstances instance) {
			this.button = button;
			this.position = position;
			this.wild = wild;
			this.instance = Optional.of(instance);
		}

		/**
		 * Sets the button
		 *
		 * @return the button
		 */
		public int getButton() {
			return button;
		}

		/**
		 * Sets the button
		 * 
		 * @param button
		 *            the button
		 */
		public void setButton(int button) {
			this.button = button;
		}

		/**
		 * Sets the position
		 *
		 * @return the position
		 */
		public Position getPosition() {
			return position;
		}

		/**
		 * Sets the position
		 * 
		 * @param position
		 *            the position
		 */
		public void setPosition(Position position) {
			this.position = position;
		}

		/**
		 * Sets the wild
		 *
		 * @return the wild
		 */
		public boolean isWild() {
			return wild;
		}

		/**
		 * Sets the wild
		 * 
		 * @param wild
		 *            the wild
		 */
		public void setWild(boolean wild) {
			this.wild = wild;
		}

		/**
		 * Gets for button
		 * 
		 * @param button
		 *            the button
		 * @return the location
		 */
		public static TeleportLocation forButton(int button) {
			return Arrays.stream(values()).filter(c -> c.getButton() == button).findFirst().orElse(null);
		}
	}

	/**
	 * Stores a teleport location
	 * 
	 * @param player
	 *            the player
	 * @param loc
	 *            the location
	 */
	private static void addPreviousTeleport(Player player, TeleportLocation loc) {
		/*
		 * Adds since doesnt exist
		 */
		if (!contains(player, loc.name())) {
			/*
			 * Previous teleport
			 */
			PreviousTeleport previous = new PreviousTeleport(loc.getPosition(), loc.isWild(),
					Misc.formatName(loc.name().toLowerCase()));
			/*
			 * Adds to list shifting the others
			 */
			player.getTeleport().getPreviousTeleports().add(0, previous);
			/*
			 * Removes others
			 */
			if (player.getTeleport().getPreviousTeleports().size() >= 4) {
				player.getTeleport().getPreviousTeleports().subList(4,
						player.getTeleport().getPreviousTeleports().size());
			}
		}
	}

	/**
	 * Teleports to previous teleport location
	 * 
	 * @param player
	 *            the player
	 * @param button
	 *            the button
	 */
	private static void teleportToPreviousTeleport(Player player, int button) {
		/*
		 * The slot
		 */
		int slot = button - PREVIOUS_TELEPORT_TEXT;
		/*
		 * Empty
		 */
		if (slot >= player.getTeleport().getPreviousTeleports().size()) {
			player.getPacketSender().sendMessage("This previous teleport location is empty.", 1000);
			return;
		}
		/*
		 * The previous loc
		 */
		PreviousTeleport loc = player.getTeleport().getPreviousTeleports().get(slot);
		/*
		 * Invalid loc
		 */
		if (loc == null) {
			return;
		}
		/*
		 * Teleports to loc
		 */
		TeleportHandler.teleport(player, loc.getPosition(), TeleportType.AUBURY, loc.isWild(), true);
	}

	/**
	 * Displays the previous teleports
	 * 
	 * @param player
	 *            the player
	 */
	private static void displayPrevious(Player player) {
		/*
		 * Clears
		 */
		for (int i = PREVIOUS_TELEPORT_TEXT; i < PREVIOUS_TELEPORT_TEXT + 4; i++) {
			player.getPacketSender().sendString(i, (i - 47005) + ". None");
		}
		/*
		 * Clears the teleports
		 */
		for (int i = 0; i < player.getTeleport().getPreviousTeleports().size(); i++) {
			/*
			 * The loc
			 */
			PreviousTeleport loc = player.getTeleport().getPreviousTeleports().get(i);
			/*
			 * Invalid loc
			 */
			if (loc == null) {
				continue;
			}
			/*
			 * Break
			 */
			if (i >= 4) {
				break;
			}
			/*
			 * The loc name
			 */
			String name = Misc.formatName(loc.getName().toLowerCase()).replaceAll("_", " ");
			/*
			 * Display loc
			 */
			player.getPacketSender().sendString(PREVIOUS_TELEPORT_TEXT + i, name);
		}
	}

	/**
	 * Checks whether contains teleport
	 * 
	 * @param player
	 *            the player
	 * @param name
	 *            the name
	 * @return contains
	 */
	private static boolean contains(Player player, String name) {
		for (PreviousTeleport teleport : player.getTeleport().getPreviousTeleports()) {
			if (teleport == null) {
				continue;
			}
			if (teleport.getName().equalsIgnoreCase(name.replaceAll("_", " "))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Showing the teleport menu
	 * 
	 * @param player
	 *            the player
	 * @param navigation
	 *            the navigation menu
	 */
	private static void showMenu(Player player, Navigation navigation) {
		if (player.isInTutorial() && EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) != 4) {
			return;
		}
		if (player.getCombat().isInCombat() && !PlayerUtil.isDeveloper(player)) {
			player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
			return;
		}
		player.getPacketSender().sendString(47003, Misc.ucFirst(navigation.name()) + " Teleports");
		player.getPacketSender().sendInterface(navigation.getInterfaceId());
		displayPrevious(player);
	}

	/**
	 * Handles button interaction
	 * 
	 * @param player
	 *            the player
	 * @param button
	 *            the button
	 */
	public static boolean handleButton(Player player, int button) {

		if (button == TeleportLocation.THE_CURSED_VAULT.getButton()) {
			if (!player.getTheCursedVaultDelayTimer().finished()) {
				if (player.getTheCursedVaultDelayTimer().secondsRemaining() == 1 || player.getTheCursedVaultDelayTimer().secondsRemaining() / 60 <= 1) {
					player.sendMessage("@red@You can use this teleport again after waiting for " + (player.getTheCursedVaultDelayTimer().secondsRemaining() >= 59 ? "one more minute" : "one more second") +".");
				} else {
					player.sendMessage("@red@You can use this teleport again after waiting for " + (player.getTheCursedVaultDelayTimer().secondsRemaining() >= 59 ? "" + player.getTheCursedVaultDelayTimer().secondsRemaining() / 60 + " more minutes" : "" + player.getTheCursedVaultDelayTimer().secondsRemaining() + " more seconds") + ".");
				}
				return false;
			}
			if (player.getCombat().isInCombat()) {
				player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
				return false;
			}
			if (!player.getRights().isHighStaff() && !PlayerUtil.isLegendaryMember(player)) {
				player.getPacketSender()
						.sendMessage("<img=1026>@red@ This teleport is only available to Legendary ranked members or above.", 1000);
				return false;
			}
			player.getTheCursedVaultDelayTimer().start(900);
			player.getPacketSender().sendJinglebitMusic(253, 25);
		} else if (button == TeleportLocation.THE_DESERTED_REEF.getButton()) {
			if (!player.getRights().isHighStaff() && !PlayerUtil.isBronzeMember(player)) {
				player.getPacketSender()
						.sendMessage("<img=1025>@red@ This teleport is only available to Bronze ranked members or higher.", 1000);
				return false;
			}
		} else if (button == TeleportLocation.LA_ISLA_EBANA.getButton()) {
				if (!player.getRights().isHighStaff() && !PlayerUtil.isRubyMember(player)) {
					player.getPacketSender()
							.sendMessage("<img=745>@red@ This teleport is only available to Ruby ranked members or higher.", 1000);
					return false;
				}
		} else if (button == TeleportLocation.THE_NEW_PENINSULA.getButton()) {
			if (!player.getRights().isHighStaff() && !PlayerUtil.isPlatinumMember(player)) {
				player.getPacketSender()
						.sendMessage("<img=1027>@red@ This teleport is only available to Platinum ranked members or higher.", 1000);
				return false;
			}
		} else if (button == TeleportLocation.FELDIP_HILLS.getButton()) {
			player.getPacketSender().sendMessage("You should use your Mythical cape to teleport to this location.", 1000);
			return false;
		}


		if (player.isInTutorial() && EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 5) {
			player.getPacketSender().sendMessage("You must close the interface to continue.");
			return false;
		}

		final TeleportLocation teleportLocation = TeleportLocation.forButton(button);

		if (teleportLocation != null) {

			if (player.getCombat().isInCombat() && !PlayerUtil.isDeveloper(player)) {
				player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
				return false;
			}

			if(teleportLocation.getPosition() == null) {
				player.getPacketSender().sendMessage("This teleport is currently unavailable.", 1000);
				return false;
			}

			if (button == TeleportLocation.GLOD.getButton() || button == TeleportLocation.FRAGMENT_OF_SEREN.getButton() || button == TeleportLocation.THE_NIGHTMARE.getButton()) {
				player.getPacketSender().sendMessage("This teleport is currently under construction. Check again later!");
				return false;
			}
			if (button == TeleportLocation.RUNE_ESSENCE_MINE.getButton()) {
				player.sendMessage("You need to complete the quest 'Rune Mysteries' to be able to do this.");
				return false;
			}
			if (button == TeleportLocation.WINTERTODT.getButton() || button == TeleportLocation.TEARS_OF_GUTHIX.getButton() || button == TeleportLocation.CHAMBERS_OF_XERIC.getButton()) {
				player.getPacketSender().sendMessage("Note: The minigame feature will be fully released in the upcoming update!");
			}


			if (button == TeleportLocation.BLAST_FURNACE.getButton()) {
				if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 60) {
					player.sendMessage("You need a Smithing level of at least 60 to teleport to the Blast Furnace minigame.");
					return false;
				}
			}

			if (button == TeleportLocation.PORAZDIR.getButton() || button == TeleportLocation.PORAZDIR_MINIGAME.getButton()) {
					if (EntityExtKt.passedTime(player, Attribute.PORAZDIR_BOSS_TELEPORT_TIMER, 300, TimeUnit.SECONDS, true, true)) {
					PlayerUtil.broadcastMessage("<img=792> " + player.getUsername() +" has just teleported to Porazdir boss in the wilderness. The boss respawns every 8 hours.");
					PlayerUtil.broadcastMessage("<img=792> You can teleport to the boss event using the teleport menu. All players who deal damage are eligible for a drop reward.");
			} else {
					//player.sendMessage("You can only teleport to Porazdir Event minigame boss once every five minutes.");
					return false;
				}
			}

			if (button == TeleportLocation.SEA_TROLL_QUEEN.getButton()) {
				player.getPacketSender().sendMessage("You must speak to Sailor Greg in Edgeville to travel to the Sea Troll Queen's island.");
			}
			if (button == TeleportLocation.THE_DESERTED_REEF.getButton() || button == TeleportLocation.LA_ISLA_EBANA.getButton() || button  == TeleportLocation.THE_NEW_PENINSULA.getButton()) {
				player.getPacketSender().sendJinglebitMusic(253, 25);
			}

			if(teleportLocation.instance.isPresent()) {
				BossInstances.Companion.instanceDialogue(player, teleportLocation.getPosition(), teleportLocation.instance.get(), false);
			} else {
				TeleportHandler.teleport(player, teleportLocation.getPosition(), TeleportType.AUBURY, teleportLocation.isWild(), true);
			}

			if(teleportLocation == TeleportLocation.SHADOW_DUNGEON) {

			}


			addPreviousTeleport(player, teleportLocation);
			return true;
		}

		/**
		 * Opening
		 */
		switch (button) {
		case PREVIOUS_TELEPORT_TEXT:
		case PREVIOUS_TELEPORT_TEXT + 1:
		case PREVIOUS_TELEPORT_TEXT + 2:
		case PREVIOUS_TELEPORT_TEXT + 3:
			teleportToPreviousTeleport(player, button);
			break;
		case 19210:
			/*if (player.getCombat().isInCombat() && !PlayerUtil.isDeveloper(player)) {
				player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
				return false;
			}*/

			TeleportHandler.teleport(player, GameConstants.DEFAULT_POSITION.randomize(3), TeleportType.HOME_QUICK, false, true);
			break;
		case 21741:
			/*if (player.getCombat().isInCombat() && !PlayerUtil.isDeveloper(player)) {
				player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
				return false;
			}*/

			TeleportHandler.teleport(player, GameConstants.DEFAULT_POSITION.randomize(3), TeleportType.ANCIENT, false, true);
			break;
		case 30016:
			/*if (player.getCombat().isInCombat() && !PlayerUtil.isDeveloper(player)) {
				player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
				return false;
			}*/
//			TeleportHandler.teleport(player, GameConstants.DEFAULT_POSITION, TeleportType.HOME, false, true);
			TeleportHandler.teleport(player, GameConstants.DEFAULT_POSITION.randomize(3), TeleportType.LUNAR, false, true);
			break;

		case 55555: // Random generated button for wizard teleport
			showMenu(player, Navigation.CITIES);
			break;
		/*case 1164:
		case 13035:
		case 30064:
		case 47022:
			showMenu(player, Navigation.TRAINING);
			break;
		case 1167:
		case 13045:
		case 30075:
		case 47040:
			showMenu(player, Navigation.MINIGAME);
			break;
		case 1170:
		case 13053:
		case 30083:
		case 47016:
			showMenu(player, Navigation.WILDERNESS);
			break;
		case 1174:
		case 13061:
		case 30106:
		case 47034:
			showMenu(player, Navigation.SKILLING);
			break;
		case 1540:
		case 13079:
		case 30146:
		//case 30162:
		case 47010:
			showMenu(player, Navigation.CITIES);
			break;
		case 7455:
		case 13087:
		case 30138:
		case 47028:
			showMenu(player, Navigation.BOSSES);
			break;
		case 1541:
		case 13069:
		case 30114:
			showMenu(player, Navigation.SLAYER);
			break;*/
		}
		return false;
	}


	/**
	 * Sets the previousTeleports
	 *
	 * @return the previousTeleports
	 */
	public ArrayList<PreviousTeleport> getPreviousTeleports() {
		return previousTeleports;
	}

	/**
	 * Sets the previousTeleports
	 * 
	 * @param previousTeleports
	 *            the previousTeleports
	 */
	public void setPreviousTeleports(ArrayList<PreviousTeleport> previousTeleports) {
		this.previousTeleports = previousTeleports;
	}

	/**
	 * Previous teleport
	 */
	public static class PreviousTeleport {
		
		/**
		 * The position
		 */
		private Position position;

		/**
		 * Teleports to wild
		 */
		private boolean wild;

		/**
		 * The teleport name
		 */
		private String name;

		/**
		 * Represents a previous teleport
		 * 
		 * @param position
		 *            the position
		 * @param wild
		 *            the wild
		 * @param name
		 *            the name
		 */
		PreviousTeleport(Position position, boolean wild, String name) {
			this.setPosition(position);
			this.setWild(wild);
			this.setName(name);
		}

		/**
		 * Sets the position
		 *
		 * @return the position
		 */
		public Position getPosition() {
			return position;
		}

		/**
		 * Sets the position
		 * 
		 * @param position
		 *            the position
		 */
		public void setPosition(Position position) {
			this.position = position;
		}

		/**
		 * Sets the wild
		 *
		 * @return the wild
		 */
		public boolean isWild() {
			return wild;
		}

		/**
		 * Sets the wild
		 * 
		 * @param wild
		 *            the wild
		 */
		public void setWild(boolean wild) {
			this.wild = wild;
		}

		/**
		 * Sets the name
		 *
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name
		 * 
		 * @param name
		 *            the name
		 */
		public void setName(String name) {
			this.name = name;
		}
	}
}
