package com.grinder.game.model.item;

import com.grinder.game.definition.ItemDefinition;

public class ItemRepairUtil {

	public static final int MINIMUM_REPAIR_COST = 500_000;

	public static int getRepairCost(int itemId) {
		String name = ItemDefinition.forId(itemId).getName();
		if (name.contains("hood") || name.contains("helm") || name.contains("coif"))
			return 600_000;
		if (name.contains("robetop") || name.contains("body") || name.contains("top") || name.contains("brassard"))
			return 900_000;
		if (name.contains("skirt") || name.contains("legs"))
			return 800_000;
		if (name.contains("staff") || name.contains("axe") || name.contains("spear") || name.contains("bow") || name.contains("hammers") || name.contains("flail"))
			return 1000_000;
		return 500_000;//just in case
	}
	
}