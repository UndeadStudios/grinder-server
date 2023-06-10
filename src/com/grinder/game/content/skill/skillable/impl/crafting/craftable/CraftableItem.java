package com.grinder.game.content.skill.skillable.impl.crafting.craftable;

import com.grinder.game.model.item.Item;

public final class CraftableItem {
	
	private final Item product;
	private final Item[] requiredItem;
	private final int level;
	private final double experience;
	private final String messageLoop;
	private final int cyclesRequired;

	public CraftableItem(Item product, int level, double experience, String messageLoop, int cyclesRequired, Item... requiredItems) {
		this.product = product;
		this.requiredItem = requiredItems;
		this.level = level;
		this.experience = experience;
		this.messageLoop = messageLoop;
		this.cyclesRequired = cyclesRequired;
	}

	public Item getProduct() {
		return product;
	}
	
	public Item[] getRequiredItems() {
		return requiredItem;
	}

	public int getLevel() {
		return level;
	}

	public double getExperience() {
		return experience;
	}

	public String getMessageLoop() {
		return messageLoop;
	}

	public int getCyclesRequired() {
		return cyclesRequired;
	}
}