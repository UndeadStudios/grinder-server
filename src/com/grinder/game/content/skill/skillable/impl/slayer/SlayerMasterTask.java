package com.grinder.game.content.skill.skillable.impl.slayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a slayer task
 * 
 * @author 2012
 *
 */
public class SlayerMasterTask {

	/**
	 * All the tasks
	 */
	private static Map<SlayerMaster, List<SlayerMasterTask>> tasks = new HashMap<SlayerMaster, List<SlayerMasterTask>>();

	/**
	 * The slayer master
	 */
	public final SlayerMaster master;

	/**
	 * The monster type
	 */
	public final SlayerMonsterType monsterType;

	/**
	 * The min amount
	 */
	public final int minAmount;

	/**
	 * The max amount
	 */
	public final int maxAmount;

	/**
	 * The weight
	 */
	private final int weight;

	/**
	 * Represents a slayer task
	 * 
	 * @param master
	 *            the master
	 * @param monsterType
	 *            the type
	 * @param minAmount
	 *            the min amount
	 * @param maxAmount
	 *            the max amount
	 * @param weight
	 *            the weight
	 */
	public SlayerMasterTask(SlayerMaster master, SlayerMonsterType monsterType, int minAmount, int maxAmount, int weight) {
		this.master = master;
		this.monsterType = monsterType;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.weight = weight;
	}

	/**
	 * Adds a task
	 * 
	 * @param master
	 *            the master
	 * @param monsterType
	 *            the type
	 * @param minAmount
	 *            the min amount
	 * @param maxAmount
	 *            the max amount
	 * @param weight
	 *            the weight
	 */
	private static void addTask(SlayerMaster master, SlayerMonsterType monsterType, int minAmount, int maxAmount,
                                int weight) {
		if (!tasks.containsKey(master)) {
			tasks.put(master, new ArrayList<SlayerMasterTask>());
		}
		SlayerMasterTask task = new SlayerMasterTask(master, monsterType, minAmount, maxAmount, weight);
		tasks.get(master).add(task);
		master.addTask(task);
	}

	public static Map<SlayerMaster, List<SlayerMasterTask>> getTasks() {
		return tasks;
	}

	/**
	 * Gets the weight
	 * 
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Loads the tasks
	 */
	public static void initializeTasks() {

		// TODO: DRAKES
		// TODO: WYRMS
		// TODO: Sulphur lizard

		tasks.clear();
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.ROCK_CRAB, 10, 25, 50);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.SAND_CRAB, 10, 25, 30);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.COW, 10, 25, 30);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.BAT, 10, 25, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.BEAR, 10, 25, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.CAVE_SLIME, 10, 25, 30);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.COW, 10, 25, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.CRAWLING_HAND, 10, 25, 30);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.DESERT_LIZARD, 10, 25, 30);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.MONKEY, 25, 30, 30);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.DWARF, 10, 25, 10);
		//addTask(SlayerMaster.TURAEL, SlayerMonsterType.HARPIE_BUG_SWARM, 10, 25, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.MONK, 10, 25, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.GHOST, 10, 25, 15);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.GOBLIN, 10, 25, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.ICEFIEND, 20, 30, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.SCORPION, 20, 30, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.SKELETON, 20, 30, 15);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.SPIDER, 20, 30, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.CHAOS_DRUID, 20, 35, 10);
		//addTask(SlayerMaster.TURAEL, SlayerMonsterType.TROLL, 10, 25, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.MINOTAUR, 15, 35, 10);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.WOLF, 10, 25, 15);
		addTask(SlayerMaster.TURAEL, SlayerMonsterType.EXPERIMENT, 20, 30, 15);

		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.BANSHEE, 20, 30, 20);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.BAT, 20, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.WILD_DOG, 20, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.CAVE_BUG, 20, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.BEAR, 20, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.HARPIE_BUG_SWARM, 20, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.IORWERTH, 25, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.CAVE_CRAWLER, 20, 45, 20);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.CAVE_SLIME, 20, 45, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.COCKATRICE, 20, 45, 20);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.CRAWLING_HAND, 20, 45, 20);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.DESERT_LIZARD, 20, 40, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.GHOST, 20, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.HILL_GIANT, 20, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.CYCLOPS, 35, 50, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.HOBGOBLIN, 20, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.ICE_WARRIOR, 20, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.KALPHITE, 25, 35, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.MOGRE, 40, 60, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.CHAOS_DRUID, 40, 60, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.BANDIT, 40, 80, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.JOGRE, 40, 60, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.PYREFIEND, 20, 30, 20);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.ROCKSLUG, 20, 30, 15);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.SKELETON, 20, 35, 10);
		//addTask(SlayerMaster.MAZCHNA, SlayerMonsters.VAMPYRE, 40, 70, 10);
		addTask(SlayerMaster.MAZCHNA, SlayerMonsterType.ZOMBIE, 20, 30, 10);

		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.ABERRANT_SPECTRE, 30, 55, 25);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.BANSHEE, 32, 45, 15);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.BASILISK, 35, 45, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.BLOODVELD, 35, 45, 20);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.JUNGLE_HORROR, 35, 45, 20);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.CAVE_HORROR, 35, 45, 20);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.CATABLEPON, 35, 45, 20);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.BANDIT, 50, 85, 20);
		//addTask(SlayerMaster.VANNAKA, SlayerMonsters.BRINE_RAT, 60, 120, 20);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.COCKATRICE, 40, 50, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.CROCODILE, 30, 40, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.DUST_DEVIL, 30, 45, 25);
		//addTask(SlayerMaster.VANNAKA, SlayerMonsters.EARTH_WARRIOR, 30, 60, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.GREEN_DRAGON, 20, 45, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.HARPIE_BUG_SWARM, 40, 50, 20);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.HILL_GIANT, 30, 35, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.CYCLOPS, 40, 50, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.IORWERTH, 35, 50, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.ICE_GIANT, 20, 35, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.ICE_WARRIOR, 25, 35, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.INFERNAL_MAGE, 20, 30, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.JELLY, 30, 55, 20);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.LESSER_DEMON, 30, 35, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.MOGRE, 35, 60, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.JOGRE, 35, 60, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.MONKEY_GUARD, 35, 60, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.GHOUL, 35, 60, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.MOSS_GIANT, 30, 40, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.OGRE, 50, 60, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.SHADE, 40, 50, 10);
		//addTask(SlayerMaster.VANNAKA, SlayerMonsters.SHADOW_WARRIOR, 60, 120, 10);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.TUROTH, 20, 35, 20);
		//addTask(SlayerMaster.VANNAKA, SlayerMonsters.VAMPYRE, 60, 120, 15);
		addTask(SlayerMaster.VANNAKA, SlayerMonsterType.WEREWOLF, 20, 30, 10);

		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.ABERRANT_SPECTRE, 20, 35, 15);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.BANSHEE, 20, 35, 5);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.BASILISK, 20, 35, 15);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.BLOODVELD, 20, 40, 15);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.BLUE_DRAGON, 20, 40, 10);
		//addTask(SlayerMaster.CHAELDAR, SlayerMonsters.BRINE_RAT, 110, 170, 10);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.BRONZE_DRAGON, 25, 40, 10);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.CAVE_CRAWLER, 25, 45, 5);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.GHOUL, 25, 45, 5);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.CRAWLING_HAND, 25, 45, 5);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.DAGANNOTH, 25, 35, 10);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.DUST_DEVIL, 30, 35, 15);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.FIRE_GIANT, 30, 40, 10);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.BANDIT, 50, 80, 10);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.GARGOYLE, 35, 35, 15);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.HARPIE_BUG_SWARM, 35, 50, 15);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.INFERNAL_MAGE, 35, 40, 10);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.JELLY, 35, 40, 15);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.KALPHITE, 30, 35, 10);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.KURASK, 35, 45, 15);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.LESSER_DEMON, 35, 40, 10);
		//addTask(SlayerMaster.CHAELDAR, SlayerMonsters.SHADOW_WARRIOR, 110, 170, 10);
		//addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.TROLL, 30, 55, 10);
		addTask(SlayerMaster.CHAELDAR, SlayerMonsterType.TUROTH, 30, 40, 15);


		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ABERRANT_SPECTRE, 50, 100, 6);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ABYSSAL_DEMON, 70, 120, 9);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ADAMANT_DRAGON, 30, 60, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ABERRANT_SPECTRE, 50, 100, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ANKOU, 50, 50, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.AVIANSIE, 80, 120, 6);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.BASILISK, 80, 120, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.BLACK_DEMON, 80, 120, 9);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.BLACK_DRAGON, 10, 15, 6);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.BLOODVELD, 80, 120, 9);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.BLUE_DRAGON, 80, 120, 4);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ABERRANT_SPECTRE, 50, 100, 6);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ZULRAH, 3, 15, 10);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ALCHEMICAL_HYDRA, 10, 25, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.KALPHITE_QUEEN, 10, 15, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.BARROWS, 25, 36, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.GENERAL_GRAARDOR, 10, 25, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.KREE_ARRA, 15, 20, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.COMMANDER_ZILYANA, 15, 20, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.KRIL_TSUTSAROTH, 10, 20, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.DAGANNOTH_KING, 9, 21, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.VORKATH, 10, 30, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.CERBERUS, 15, 20, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.BRONZE_DRAGON, 30, 50, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.DAGANNOTH, 80, 120, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.DARK_BEAST, 10, 15, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.DUST_DEVIL, 80, 120, 6);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.FIRE_GIANT, 80, 120, 9);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ANCIENT_WYVERN, 15, 30, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.GARGOYLE, 80, 120, 6);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.GREATER_DEMON, 80, 120, 7);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.HELLHOUND, 80, 120, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.HYDRA, 80, 140, 10);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.IRON_DRAGON, 30, 50, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.JELLY, 80, 120, 6);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.LIZARDMAN, 90, 110, 9);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.MITHRIL_DRAGON, 3, 6, 3);
		//addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.ZYGOMITES, 10, 25, 8);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.NECHRYAEL, 110, 110, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.RED_DRAGON, 30, 50, 2);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.RUNE_DRAGON, 30, 50, 7);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.SKELETAL_WYVERN, 5, 12, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.SMOKE_DEVIL, 80, 120, 5);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.STEEL_DRAGON, 30, 50, 5);
		//addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.TROLL, 80, 140, 6);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.TUROTH, 80, 140, 3);
		//addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.VAMPYRE, 100, 160, 4);
		addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.WATERFIEND, 80, 120, 2);
		//addTask(SlayerMaster.KONAR_QUO_MATEN, SlayerMonsterType.WYRMS, 100, 140, 10);

		addTask(SlayerMaster.NIEVE, SlayerMonsterType.KILLERWATT, 30, 40, 5);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.ABERRANT_SPECTRE, 30, 40, 6);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.ABYSSAL_DEMON, 30, 40, 9);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.ADAMANT_DRAGON, 30, 50, 2);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.ANKOU, 30, 50, 6);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.AVIANSIE, 30, 40, 4);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.BASILISK, 30, 40, 4);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.BLACK_DEMON, 30, 50, 7);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.BLACK_DRAGON, 35, 40, 7);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.BLOODVELD, 30, 40, 5);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.BLUE_DRAGON, 35, 45, 8);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.MONKEY_GUARD, 35, 45, 8);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.DAGANNOTH, 30, 40, 6);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.ANCIENT_WYVERN, 30, 40, 6);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.DARK_BEAST, 30, 40, 5);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.GARGOYLE, 40, 60, 7);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.DUST_DEVIL, 30, 55, 5);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.HELLHOUND, 30, 40, 6);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.CAVE_HORROR, 30, 45, 6);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.JUNGLE_HORROR, 30, 45, 6);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.FIRE_GIANT, 40, 55, 8);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.KALPHITE, 40, 50, 7);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.IRON_DRAGON, 30, 50, 7);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.RED_DRAGON, 40, 50, 5);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.SMOKE_DEVIL, 30, 50, 5);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.SKELETAL_WYVERN, 30, 40, 7);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.SUQAH, 30, 40, 8);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.RUNE_DRAGON, 25, 40, 5);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.MITHRIL_DRAGON, 20, 25, 5);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.GREATER_DEMON, 40, 55, 9);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.STEEL_DRAGON, 30, 40, 7);
		//addTask(SlayerMaster.NIEVE, SlayerMonsterType.TROLL, 50, 60, 6);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.TZHAAR, 40, 50, 7);
		addTask(SlayerMaster.NIEVE, SlayerMonsterType.TZTOK_JAD, 1, 2, 7);



		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ZULRAH, 10, 15, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ALCHEMICAL_HYDRA, 10, 25, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.KALPHITE_QUEEN, 10, 15, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BARROWS, 25, 36, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.GENERAL_GRAARDOR, 10, 25, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.KREE_ARRA, 15, 20, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.COMMANDER_ZILYANA, 15, 20, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.KRIL_TSUTSAROTH, 10, 20, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.DAGANNOTH_KING, 9, 21, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.VORKATH, 10, 30, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.CERBERUS, 15, 20, 10);



		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.LAVA_DRAGON, 30, 40, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.BANDIT_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.ENT_WILDERNESS, 40, 50, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.MAMMOTH_WILDERNESS, 40, 50, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.BLACK_KNIGHT_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.ROGUE_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.ZAMORAK_WIZARD_WILDERNESS, 40, 50, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.GHOST_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.SKELETON_WILDERNESS, 40, 80, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.RED_DRAGON_WILDERNESS, 40, 60, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.ZOMBIE_WILDERNESS, 40, 60, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.ICE_WARRIOR_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.ICE_GIANT_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.REVENANT_WILDERNESS, 50, 80, 10);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.HELLHOUND_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.GREEN_DRAGON_WILDERNESS, 60, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.GREATER_DEMON_WILDERNESS, 50, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.ANKOU_WILDERNESS, 50, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.BLACK_DEMON_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.BLACK_DRAGON_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.LESSER_DEMON_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.BATTLE_MAGE_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.SCORPION_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.SPIDER_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.CHAOS_DRUID_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.EARTH_WARRIOR_WILDERNESS, 40, 70, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.CALLISTO_WILDERNESS, 5, 10, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.CRAZY_ARCHAEOLOGIST_WILDERNESS, 5, 15, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.BARRELCHEST_WILDERNESS, 10, 25, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.CHAOS_FANATIC_WILDERNESS, 10, 20, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.VETION_WILDERNESS, 15, 20, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.SCORPION_WILDERNESS, 8, 18, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.CHAOS_ELEMENTAL_WILDERNESS, 15, 25, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.KING_BLACK_DRAGON_WILDERNESS, 15, 25, 5);
		addTask(SlayerMaster.KRYSTILIA, SlayerMonsterType.VENENATIS_WILDERNESS, 10, 25, 5);




		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ABERRANT_SPECTRE, 40, 55, 15);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ABYSSAL_DEMON, 40, 50, 10);
		//addTask(SlayerMaster.DURADEL, SlayerMonsters.AQUANITE, 120, 185, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BANSHEE, 40, 50, 15);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BASILISK, 40, 50, 15);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BLACK_DEMON, 30, 45, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ANCIENT_WYVERN, 30, 45, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BLOODVELD, 40, 50, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BLUE_DRAGON, 25, 40, 8);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.CAVE_CRAWLER, 40, 50, 15);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.MONKEY_GUARD, 40, 50, 15);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.DAGANNOTH, 40, 45, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.DUST_DEVIL, 40, 50, 15);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.FIRE_GIANT, 40, 50, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.CAVE_HORROR, 40, 50, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.JUNGLE_HORROR, 40, 50, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.GARGOYLE, 40, 50, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.GREATER_DEMON, 50, 60, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.HELLHOUND, 40, 50, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.IRON_DRAGON, 30, 45, 7);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.KALPHITE, 40, 50, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.KURASK, 40, 55, 15);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.NECHRYAEL, 55, 70, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.RED_DRAGON, 30, 65, 5);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ADAMANT_DRAGON, 30, 40, 5);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.RUNE_DRAGON, 30, 40, 5);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.HYDRA, 30, 45, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.DEMONIC_GORILLA, 30, 45, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.LIZARDMAN, 30, 55, 5);
		//addTask(SlayerMaster.DURADEL, SlayerMonsters.SPIRITUAL_MAGE, 120, 185, 10);
		//addTask(SlayerMaster.DURADEL, SlayerMonsters.SPIRITUAL_WARRIOR, 120, 185, 10);
		//addTask(SlayerMaster.DURADEL, SlayerMonsterType.TROLL, 50, 65, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.TUROTH, 40, 50, 15);

		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ABERRANT_SPECTRE, 45, 70, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ABYSSAL_DEMON, 70, 85, 15);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BLACK_DEMON, 40, 60, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BLACK_DRAGON, 40, 70, 9);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BLOODVELD, 70, 105, 20);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.DAGANNOTH, 70, 105, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.DARK_BEAST, 70, 90, 15);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.DUST_DEVIL, 65, 80, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.FIRE_GIANT, 80, 90, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.GARGOYLE, 70, 90, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.GORAK, 40, 80, 5);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.GREATER_DEMON, 85, 110, 11);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.HELLHOUND, 60, 110, 9);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.IRON_DRAGON, 40, 70, 9);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.MITHRIL_DRAGON, 20, 40, 7);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.NECHRYAEL, 130, 200, 5);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.SKELETAL_WYVERN, 40, 70, 5);
		//addTask(SlayerMaster.DURADEL, SlayerMonsters.SPIRITUAL_MAGE, 130, 200, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.STEEL_DRAGON, 35, 50, 7);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.SUQAH, 50, 65, 5);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.SMOKE_DEVIL, 60, 85, 5);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.WATERFIEND, 100, 130, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.MITHRIL_DRAGON, 30, 50, 10);
		//addTask(SlayerMaster.DURADEL, SlayerMonsters.SPIRITUAL_MAGE, 150, 240, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.STEEL_DRAGON, 40, 60, 9);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.SUQAH, 50, 100, 5);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.TZHAAR, 65, 105, 7);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.TZTOK_JAD, 1, 2, 7);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.WATERFIEND, 100, 120, 9);

		// Boss tasks when you unlock it for duradel and nieve
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ZULRAH, 10, 15, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.ALCHEMICAL_HYDRA, 10, 25, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.KALPHITE_QUEEN, 10, 15, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.BARROWS, 25, 36, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.GENERAL_GRAARDOR, 10, 25, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.KREE_ARRA, 15, 20, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.COMMANDER_ZILYANA, 15, 20, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.KRIL_TSUTSAROTH, 10, 20, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.DAGANNOTH_KING, 9, 21, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.VORKATH, 10, 30, 10);
		addTask(SlayerMaster.DURADEL, SlayerMonsterType.CERBERUS, 15, 20, 10);
	}

	@Override
	public String toString() {
		return "SlayerTask [master=" + master + ", monsterType=" + monsterType + ", minAmount=" + minAmount
				+ ", maxAmount=" + maxAmount + "]";
	}
}
