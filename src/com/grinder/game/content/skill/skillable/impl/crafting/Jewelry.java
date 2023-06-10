package com.grinder.game.content.skill.skillable.impl.crafting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;

public enum Jewelry {
	
	/* Rings */
	GOLD_RING(1635, 5, 15, 1592, new RequiredItem(2357, true)),
	SAPPHIRE_RING(1637, 20, 40, 1592, new RequiredItem(2357, true), new RequiredItem(1607, true)),
	EMERALD_RING(1639, 27, 55, 1592, new RequiredItem(2357, true), new RequiredItem(1605, true)),
	RUBY_RING(1641, 34, 70, 1592, new RequiredItem(2357, true), new RequiredItem(1603, true)),
	DIAMOND_RING(1643, 43, 85, 1592, new RequiredItem(2357, true), new RequiredItem(1601, true)),
	DRAGONSTONE_RING(1645, 55, 100, 1592, new RequiredItem(2357, true), new RequiredItem(1615, true)),
	ONYX_RING(6575, 67, 85, 1592, new RequiredItem(2357, true), new RequiredItem(6573, true)),
	ZENYTE_RING(19538, 89, 150, 1592, new RequiredItem(2357, true), new RequiredItem(19493, true)),
	
	/* Necklace */
	GOLD_NECKLACE(1654, 6, 20, 1597, new RequiredItem(2357, true)),
	SAPPHIRE_NECKLACE(1656, 22, 55, 1597, new RequiredItem(2357, true), new RequiredItem(1607, true)),
	EMERALD_NECKLACE(1658, 29, 60, 1597, new RequiredItem(2357, true), new RequiredItem(1605, true)),
	RUBY_NECKLACE(1660, 40, 75, 1597, new RequiredItem(2357, true), new RequiredItem(1603, true)),
	DIAMOND_NECKLACE(1662, 56, 90, 1597, new RequiredItem(2357, true), new RequiredItem(1601, true)),
	DRAGONSTONE_NECKLACE(1664, 72, 105, 1597, new RequiredItem(2357, true), new RequiredItem(1615, true)),
	ONYX_NECKLACE(6577, 82, 120, 1597, new RequiredItem(2357, true), new RequiredItem(6573, true)),
	ZENYTE_NECKLACE(19535, 92, 165, 1597, new RequiredItem(2357, true), new RequiredItem(19493, true)),
	
	/* Amulet */
	GOLD_AMULET(1673, 8, 30, 1595, new RequiredItem(2357, true)),
	SAPPHIRE_AMULET(1675, 24, 65, 1595, new RequiredItem(2357, true), new RequiredItem(1607, true)),
	EMERALD_AMULET(1677, 31, 61, 1595, new RequiredItem(2357, true), new RequiredItem(1605, true)),
	RUBY_AMULET(1679, 50, 85, 1595, new RequiredItem(2357, true), new RequiredItem(1603, true)),
	DIAMOND_AMULET(1681, 70, 100, 1595, new RequiredItem(2357, true), new RequiredItem(1601, true)),
	DRAGONSTONE_AMULET(1683, 80, 125, 1595, new RequiredItem(2357, true), new RequiredItem(1615, true)),
	ONYX_AMULET(6579, 90, 150, 1595, new RequiredItem(2357, true), new RequiredItem(6573, true)),
	ZENYTE_AMULET(19501, 98, 200, 1595, new RequiredItem(2357, true), new RequiredItem(19493, true)),
	
	/* Bracelets */
	GOLD_BRACELET(11068, 8, 30, 11065, new RequiredItem(2357, true)),
	SAPPHIRE_BRACELET(11071, 23, 60, 11065, new RequiredItem(2357, true), new RequiredItem(1607, true)),
	EMERALD_BRACELET(11076, 30, 65, 11065, new RequiredItem(2357, true), new RequiredItem(1605, true)),
	RUBY_BRACELET(11085, 34, 80, 11065, new RequiredItem(2357, true), new RequiredItem(1603, true)),
	DIAMOND_BRACELET(11092, 58, 95, 11065, new RequiredItem(2357, true), new RequiredItem(1601, true)),
	DRAGONSTONE_BRACELET(11115, 74, 110, 11065, new RequiredItem(2357, true), new RequiredItem(1615, true)),
	ONYX_BRACELET(11130, 84, 125, 11065, new RequiredItem(2357, true), new RequiredItem(6573, true)),
	ZENYTE_BRACELET(19532, 89, 180, 11065, new RequiredItem(2357, true), new RequiredItem(19493, true)),

	/* Silver */
	UNSTRUNG_SYMBOL(1714, 16, 50, 1599, new RequiredItem(2355, true)),
	UNSTRUNG_EMBLEM(1720, 17, 50, 1594, new RequiredItem(2355, true)),
	SILVER_SICKLE(2961, 18, 50, 2976, new RequiredItem(2355, true)),
	TIARA(5525, 23, 52, 5523, new RequiredItem(2355, true));

	private Item reward;
	private int levelRequired;
	private int experienceGain;
	private int mould;
	private RequiredItem[] requiredItems;
	
	private static final Map<Integer, Jewelry> JEWELRY = new HashMap<>();

	static {
		Arrays.stream(values()).forEach(v -> JEWELRY.put(v.getReward().getId(), v));
	}

	Jewelry(int rewardId, int levelRequired, int experienceGain, int mould, RequiredItem...requiredItems) {
		this.reward = new Item(rewardId);
		this.levelRequired = ((short) levelRequired);
		this.experienceGain = experienceGain;
		this.mould = mould;
		this.requiredItems = requiredItems;
	}

	public int getExperience() {
		return experienceGain;
	}
	
	public int getMould() {
		return mould;
	}

	public RequiredItem[] getRequiredItems() {
		return requiredItems;
	}

	public int getRequiredLevel() {
		return levelRequired;
	}

	public Item getReward() {
		return reward;
	}
	
	public static Jewelry forReward(int id) {
		return JEWELRY.get(id);
	}
	
}
