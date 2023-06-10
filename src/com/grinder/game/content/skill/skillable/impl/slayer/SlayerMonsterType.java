package com.grinder.game.content.skill.skillable.impl.slayer;

import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.model.Position;
import com.grinder.util.Misc;

/**
 * The monster types for slayer
 */
public enum SlayerMonsterType {

	BANSHEE(15, false, true, false, false, new Position(3445, 3545, 0)),

	TWISTED_BANSHEE(15, false, true, false, false, new Position(1617, 9997, 0)),

	BAT(1, false, false, false, false, new Position(2913, 9833, 0)),

	HARPIE_BUG_SWARM(33, false, false, false, false, new Position(3577, 9927, 0)),

	BEAR(1, new Position(2704, 3311, 0), new String[]{"callisto"}, false, false, false),

	CAVE_BUG(5, false, false, false, false, new Position(3221, 9573, 0)),

	CAVE_SLIME(17, false, false, false, false, new Position(2779, 9999, 0)),

	COW(1, false, false, false, false, new Position(3258, 3260, 0)),

	MONK(1, false, false, false, false, new Position(3051, 3480, 0)),

	IORWERTH(1, new Position(2892, 2727, 0), new String[]{"iorwerth warrior", "iwerth archer"}, false, false, false),

	WILD_DOG(1, false, false, false, false, new Position(1766, 5325, 0)),

	ROCK_CRAB(1, false, false, false, false, new Position(2674, 3716, 0)),

	SAND_CRAB(1, false, false, false, false, new Position(1698, 3465, 0)),

	EXPERIMENT(1, false, false, false, false, new Position(3557, 9947, 0)),

	DWARF(1, false, false, false, false, new Position(3015, 3449, 0)),

	GHOST(1, false, true, false, false, new Position(2937, 9825, 0)),

	GOBLIN(1, false, false, false, false, new Position(3256, 3246, 0)),

	GHOUL(1, false, true, false, false, new Position(3440, 9887, 0)),

	ICEFIEND(1, false, false, false, false, new Position(2834, 3511, 0)),

	MINOTAUR(1, false, false, false, false, new Position(1859, 5243, 0)),

	FLESH_CRAWLER(1, false, false, false, false, new Position(2042, 5245, 0)),

	CATABLEPON(1, false, false, false, false, new Position(2122, 5251, 0)),

	KILLERWATT(1, false, false, false, false, new Position(2677, 5214, 2)),

	LAVA_DRAGON(1, true, new Position(3202, 3857, 0)),

	HYDRA(95, false, false, false, false, new Position(1329, 10206, 0)),

	LIZARDMAN(1, false, false, false, false, new Position(1464, 3674, 0)),

	DEMONIC_GORILLA(1, false, false, false, false, new Position(2026, 5611, 0)),

	JUNGLE_HORROR(1, false, false, false, false, new Position(3749, 2973, 0)),

	CAVE_HORROR(1, false, false, false, false, new Position(3749, 2973, 0)),

	ANKOU(1, false, true, false, false, new Position(2364, 5212, 0)),

	SHADE(1, false, true, false, false, new Position(2364, 5212, 0)),

	CHAOS_DRUID(1, false, false, false, false, new Position(3131, 9916, 0)),

	BANDIT(1, false, false, false, false, new Position(3175, 2981, 0)),

	AVIANSIE(1, new Position(2872, 5269, 2), new String[]{"kree'arra", "flight kilisa", "flockleader geerin", "wingman skree", "reanimated aviansie"}, false, false, false),

	MONKEY(1, new Position(2900, 3160, 0), new String[]{"demonic gorilla", "tortured gorilla", "monkey guard"}, false, false, false),

	SCORPION(1, new Position(3298, 3300, 0), new String[]{"scorpia"}, false, false, false),

	SKELETON(1, false, true, false, false, new Position(3139, 9872, 0)),

	SPIDER(1, new Position(3176, 9888, 0), new String[]{"venenatis"}, false, false, false),

	//TROLL(1, false, false, false, false, new Position(2551, 3257, 0)),

	WOLF(1, false, false, false, false, new Position(3006, 3476, 0)),

	ZOMBIE(1, false, true, false, false, new Position(3144, 9891, 0)),

	CAVE_CRAWLER(10, new Position(2804, 10001, 0), new String[]{"chasm crawler"}, false, false, false),

	COCKATRICE(25, new Position(2798, 10033, 0), new String[]{"cockatrice", "cockathrice"}, false, false, false),

	CRAWLING_HAND(5, new Position(3411, 3538, 0), new String[]{"crushing hand"}, false, true, false),

	HILL_GIANT(1, new Position(3115, 9852, 0), new String[]{"obor"}, false, false, false),

	CYCLOPS(1, new Position(2878, 3546, 0), new String[]{"obor"}, false, false, false),

	HOBGOBLIN(1, false, false, false, false, new Position(2909, 3615, 0)),

	ICE_WARRIOR(1, false, false, false, false, new Position(3048, 9581, 0)),

	KALPHITE(1, false, false, false, false, new Position(3479, 9489, 2)),

	MOGRE(32, false, false, false, false, new Position(2996, 3120, 0)),

	JOGRE(1, false, false, false, false, new Position(2850, 5333, 2)),

	OGRE(1, false, false, false, false, new Position(1970, 8986, 1)),

	ADAMANT_DRAGON(1, false, false, false, false, new Position(1568, 5062, 0)),

	RUNE_DRAGON(1, false, false, false, false, new Position(1568, 5062, 0)),

	PYREFIEND(30, false, false, false, false, new Position(2759, 10005, 0)),

	ROCKSLUG(20, false, false, false, false, new Position(2799, 10017, 0)),

	//VAMPYRE(1, false, false, false, false, new Position(3579, 3469, 0)),

	ABERRANT_SPECTRE(60, new Position(3426, 3540, 1), new String[]{"abhorrent spectre", "deviant spectre", "repugnant spectre"}, false, false, false),

	DEVIANT_SPECRTRE(60, new Position(1608, 10005, 0), new String[]{"aberrant spectre", "abhorrent spectre", "repugnant spectre"}, false, false, false),

	BASILISK(39, false, false, false, false, new Position(2745, 10005, 0)),

	BLOODVELD(50, false, false, false, false, new Position(3421, 3561, 1)),

	MUTATED_BLOODVELD(50, false, false, false, false, new Position(1627, 10025, 0)),

	//BRINE_RAT(47, false, false, false, false, new Position(3292, 5450, 0)),

	CROCODILE(1, false, false, false, false, new Position(3340, 2935, 0)),

	DUST_DEVIL(65, false, false, false, false, new Position(3371, 2903, 0)),

	SMOKE_DEVIL(93, new Position(2404, 9415, 0), new String[]{"thermonuclear smoke devil"}, false, false, false),

	//EARTH_WARRIOR(1, false, false, false, false, new Position(3219, 5482, 0)),

	GREEN_DRAGON(1, false, false, false, false, new Position(2345, 3890, 0)),

	//HARPIE_BUG_SWARM(33, false, false, false, false, new Position(2866, 3107, 0)),

	ICE_GIANT(1, false, false, false, false, new Position(3050, 9581, 0)),

	INFERNAL_MAGE(45, false, false, false, false, new Position(3438, 3561, 1)),

	JELLY(52, false, false, false, false, new Position(2710, 10028, 0)),

	WARPED_JELLY(52, false, false, false, false, new Position(1694, 9997, 0)),

	LESSER_DEMON(1, false, false, false, false, new Position(2936, 9788, 0)),

	MOSS_GIANT(1, false, false, false, false, new Position(2660, 9553, 0)),

	TUROTH(55, false, false, false, false, new Position(2722, 10004, 0)),

	WEREWOLF(1, false, false, false, false, new Position(3490, 3487, 0)),

	BLUE_DRAGON(1, new Position(2903, 9804, 0), new String[]{"vorkath"}, false, false, false),

	BRONZE_DRAGON(1, false, false, false, false, new Position(2733, 9486, 0)),

	DAGANNOTH(1, false, false, false, false, new Position(2454, 10147, 0)),

	FIRE_GIANT(1, false, false, false, false, new Position(2660, 9481, 0)),

	GARGOYLE(75, false, false, false, false, new Position(3442, 3541, 2)),

	KURASK(70, false, false, false, false, new Position(2699, 9994, 0)),

	ABYSSAL_DEMON(85, false, false, false, false, new Position(3423, 3570, 2)),

	//AQUANITE(78, false, false, false, false, new Position(2721, 9970, 0)),

	BLACK_DEMON(1, new Position(2876, 9772, 0),
			new String[]{"skotizo", "demonic gorilla", "balfrug kreeyath", "porazdir", "kolodion"}, false, false, false),

	//Demonic gorillas, Skotizo, Porazdir, and Kolodion's final

	GREATER_DEMON(1, new Position(2840, 9634, 0),
			new String[]{" tsutsaroth", "tstanon", "karlak", "skotizo"}, false, false, false),

	HELLHOUND(1, new Position(1644, 10064, 0), new String[]{"cerberus"}, false, false, false),

	IRON_DRAGON(1, false, false, false, false, new Position(2737, 9442, 0)),

	NECHRYAEL(80, false, false, false, false, new Position(3439, 3567, 2)),

	RED_DRAGON(1, false, false, false, false, new Position(2717, 9520, 0)),

	//SPIRITUAL_MAGE(83), // TODO: FIXME

	//SPIRITUAL_WARRIOR(68),

	BLACK_DRAGON(1, false, false, false, false, new Position(2835, 9827, 0)),

	DARK_BEAST(90, false, false, false, false, new Position(3423, 3570, 2)),

	GORAK(1, false, false, false, false, new Position(2905, 5351, 2)),

	MONKEY_GUARD(1, false, false, false, false, new Position(2754, 2776, 0)),

	MITHRIL_DRAGON(1, false, false, false, false, new Position(1777, 5344, 1)),

	SKELETAL_WYVERN(72, false, false, false, false, new Position(3055, 9555, 0)),

	ANCIENT_WYVERN(82, false, false, false, false, new Position(3055, 9555, 0)),

	STEEL_DRAGON(1, false, false, false, false, new Position(2735, 9458, 0)),

	SUQAH(1, false, false, false, false, new Position(2121, 3854, 0)),

	TZHAAR(1, false, false, false, false, new Position(2456, 5157, 0)),

	TZTOK_JAD(1, false, false, false, false, new Position(2456, 5157, 0)),

	WATERFIEND(1, false, false, false, false, new Position(1741, 5344, 0)),

	DESERT_LIZARD(22, false, false, false, false, new Position(3158, 2899, 0), new Position(3192, 2894, 0)),

	/**
	 * BOSS TASKS
	 */

	ZULRAH(1, false, false, true, false, new Position(2204, 3056, 0)),

	ALCHEMICAL_HYDRA(1, false, false, true, false, new Position(1329, 10206, 0)),

	GIANT_MOLE(1, false, false, true, false, Teleporting.TeleportLocation.GIANT_MOLE.getPosition()),

	KALPHITE_QUEEN(1, false, false, true, false, Teleporting.TeleportLocation.KALPHITE_QUEEN.getPosition()),

	GENERAL_GRAARDOR(1, false, false, true, false, Teleporting.TeleportLocation.GENERAL_GRAARDOR.getPosition()),

	KREE_ARRA(1, Teleporting.TeleportLocation.KREE_ARRA.getPosition(), new String[]{"kree'arra"}, false, false, true),

	COMMANDER_ZILYANA(1, false, false, true, false, Teleporting.TeleportLocation.COMMANDER_ZILYANA.getPosition()),

	KRIL_TSUTSAROTH(1, Teleporting.TeleportLocation.KRIL_TSARUTH.getPosition(), new String[]{"tsutsaroth"}, false, false, true),

	DAGANNOTH_KING(1, Teleporting.TeleportLocation.DAGGANOTH_KINGS.getPosition(), new String[]{"dagannoth rex", "dagannoth supreme", "dagannoth prime"}, false, false, true),

	VORKATH(1, false, false, true, false, Teleporting.TeleportLocation.VORKATH.getPosition()),

	BARROWS(1, Teleporting.TeleportLocation.BARROWS.getPosition(), new String[]{"ahrim", "dharok", "guthan", "karil", "torag", "verac"}, false, true, true),

	CERBERUS(1, false, false, true, false, Teleporting.TeleportLocation.CERBERUS.getPosition()),

	/**
	* WILDERNESS TASKS
	 */

	BANDIT_WILDERNESS(1, new Position(3038, 3704, 0), new String[]{"bandit"}, true, false, false),

	ENT_WILDERNESS(1, new Position(3227, 3662, 0), new String[]{"ent"}, true, false, false),

	MAMMOTH_WILDERNESS(1, new Position(3090, 3956, 0), new String[]{"mammoth"}, true, false, false),

	BLACK_KNIGHT_WILDERNESS(1, new Position(3286, 3921, 0), new String[]{"black knight"}, true, false, false),

	ROGUE_WILDERNESS(1, new Position(3286, 3921, 0), new String[]{"rogue"}, true, false, false),

	ZAMORAK_WIZARD_WILDERNESS(1, new Position(3202, 3949, 0), new String[]{"zamorak wizard"}, true, false, false),

	GHOST_WILDERNESS(1, new Position(2977, 3735, 0), new String[]{"ghost"}, true, true, false),

	SKELETON_WILDERNESS(1, new Position(3016, 3582, 0), new String[]{"skeleton"}, true, true, false),

	RED_DRAGON_WILDERNESS(1, new Position(3348, 3666, 0), new String[]{"red dragon"}, true, false, false),

	ZOMBIE_WILDERNESS(1, new Position(3146, 3669, 0), new String[]{"zombie"}, true, true, false),

	ICE_WARRIOR_WILDERNESS(1, new Position(2992, 3912, 0), new String[]{"ice warrior"}, true, false, false),

	ICE_GIANT_WILDERNESS(1, new Position(2992, 3912, 0), new String[]{"ice giant"}, true, false, false),

	REVENANT_WILDERNESS(1, new Position(3196, 10056, 0), new String[]{"revenant"}, true, true, false),

	HELLHOUND_WILDERNESS(1, new Position(3196, 10056, 0), new String[]{"hellhound"}, true, false, false),

	GREEN_DRAGON_WILDERNESS(1, new Position(3196, 10056, 0), new String[]{"green dragon"}, true, false, false),

	GREATER_DEMON_WILDERNESS(1, new Position(3196, 10056, 0), new String[]{"greater demon"}, true, false, false),

	ANKOU_WILDERNESS(1, new Position(3196, 10056, 0), new String[]{"ankou"}, true, true, false),

	BLACK_DEMON_WILDERNESS(1, new Position(3196, 10056, 0), new String[]{"black demon"}, true, false, false),

	BLACK_DRAGON_WILDERNESS(1, new Position(3196, 10056, 0), new String[]{"black dragon"}, true, false, false),

	LESSER_DEMON_WILDERNESS(1, new Position(3196, 10056, 0), new String[]{"lesser demon"}, true, false, false),

	BATTLE_MAGE_WILDERNESS(1, new Position(3105, 3959, 0), new String[]{"battle mage"}, true, false, false),

	SCORPION_WILDERNESS(1, new Position(3261, 3952, 0), new String[]{"scorpion"}, true, false, false),

	SPIDER_WILDERNESS(1, new Position(3336, 3720, 0), new String[]{"spider"}, true, false, false),

	CHAOS_DRUID_WILDERNESS(1, new Position(3131, 9916, 0), new String[]{"chaos druid"}, true, false, false),

	EARTH_WARRIOR_WILDERNESS(1, new Position(3121, 9963, 0), new String[]{"earth warrior"}, true, false, false),

	CALLISTO_WILDERNESS(1, new Position(3313, 3830, 0), new String[]{"callisto"}, true, false, true),

	CRAZY_ARCHAEOLOGIST_WILDERNESS(1, new Position(2982, 3683, 0), new String[]{"crazy archaeologist"}, true, false, true),

	BARRELCHEST_WILDERNESS(1, new Position(3270, 3707, 0), new String[]{"barrelchest"}, true, false, true),

	CHAOS_FANATIC_WILDERNESS(1, new Position(2978, 3830, 0), new String[]{"chaos fanatic"}, true, false, true),

	VETION_WILDERNESS(1, new Position(3186, 3776, 0), new String[]{"vet'ion"}, true, false, true),

	SCORPIA_WILDERNESS(1, new Position(3261, 3952, 0), new String[]{"scorpia"}, true, false, true),

	CHAOS_ELEMENTAL_WILDERNESS(1, new Position(3308, 3909, 0), new String[]{"chaos elemental"}, true, false, true),

	KING_BLACK_DRAGON_WILDERNESS(1, new Position(2271, 4680, 0), new String[]{"king black dragon"}, true, false, true),

	VENENATIS_WILDERNESS(1, new Position(3336, 3720, 0), new String[]{"venenatis"}, true, false, true),
	;

	/**
	 * The monster name
	 */
	private String monsterName;

	/**
	 * The level required
	 */
	private final int requiredSlayerLevel;

	/**
	 * The assignment required
	 */
	private final boolean requireAssignment;

	/**
	 * The hash code
	 */
	private int hashCode;

	/**
	 * Whether undead
	 */
	private final boolean undead;

	/**
	 * The location
	 */
	private final Position[] locations;
	
	/**
	 * The other names
	 */
	private final String[] otherNames;

	/**
	 * Whether undead
	 */
	private final boolean inWilderness;

	/**
	 * Whether boss
	 */
	private final boolean boss;

	/**
	 * Represent a slayer monster
	 * 
	 * @param requiredSlayerLevel
	 *            the level required
	 * @param requireAssignment
	 *            the assignment required
	 * @param undead
	 *            the undead
	 * @param locations
	 *            the locations
	 */
	SlayerMonsterType(int requiredSlayerLevel, boolean requireAssignment, boolean undead, boolean boss, boolean inWilderness, Position... locations) {
		this.requiredSlayerLevel = requiredSlayerLevel;
		this.requireAssignment = requireAssignment;
		this.hashCode = Math.abs(name().hashCode());
		this.undead = undead;
		this.inWilderness = inWilderness;
		this.boss = boss;
		this.locations = locations;
		this.otherNames = null;
	}
	
	/**
	 * Represent a slayer monster
	 */
	SlayerMonsterType(int requiredSlayerLevel, Position position, String[] otherNames, boolean inWilderness, boolean undead, boolean boss) {
		this.requiredSlayerLevel = requiredSlayerLevel;
		this.requireAssignment = false;
		this.hashCode = Math.abs(name().hashCode());
		this.undead = false;
		this.boss = boss;
		this.locations = new Position[]{ position};
		this.otherNames = otherNames;
		this.inWilderness = inWilderness;
	}

	public final Position[] getLocations() {
		return locations;
	}

	SlayerMonsterType(int requiredSlayerLevel, boolean inWilderness, Position... locations) {
		this(requiredSlayerLevel, false, false, false, false, locations);
	}

	public int hash() {
		return hashCode;
	}

	public String getName() {
		return toString();
	}

	public String toString() {
		if (monsterName == null)
			return monsterName = Misc.formatName(super.toString().replaceAll("_WILDERNESS", "").replaceAll("_", " ").toLowerCase());
		return monsterName;
	}

	public static SlayerMonsterType forHashCode(int hashCode) {
		for (SlayerMonsterType monster : values()) {
			if (monster.hash() == hashCode) {
				return monster;
			}
		}
		return null;
	}

	public int getRequiredSlayerLevel() {
		return requiredSlayerLevel;
	}

	public boolean isRequireAssignment() {
		return requireAssignment;
	}

	public boolean isUndead() {
		return undead;
	}

	public boolean isInWilderness() {
		return inWilderness;
	}

	public boolean isBoss() {
		return boss;
	}

	/**
	 * Sets the otherNames
	 *
	 * @return the otherNames
	 */
	public String[] getOtherNames() {
		return otherNames;
	}

}
