package com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl;

import com.grinder.game.content.skill.skillable.impl.crafting.craftable.Craftable;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.CraftableItem;
import com.grinder.game.model.Animation;
import com.grinder.game.model.AnimationLoop;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.SoundLoop;
import com.grinder.util.ItemID;

import java.util.ArrayList;

public enum Leather implements Craftable {

	LEATHER_GLOVES(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(new Item(ItemID.LEATHER_GLOVES), 1, 13.8, "You make a pair of leather gloves.", 4, new Item(ItemID.LEATHER)
			)),
	LEATHER_BOOTS(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(new Item(ItemID.LEATHER_BOOTS), 7, 16.25, "You make a pair of leather boots.", 4, new Item(ItemID.LEATHER)
			)),
	LEATHER_VAMBRACES(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(new Item(ItemID.LEATHER_VAMBRACES), 11, 22.0, "You make a pair of leather vambraces.", 4, new Item(ItemID.LEATHER)
			)),
	LEATHER_BODY(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(new Item(ItemID.LEATHER_BODY), 14, 25.0, "You make a leather body.", 4, new Item(ItemID.LEATHER)
			)),
	LEATHER_CHAPS(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(new Item(ItemID.LEATHER_CHAPS), 18, 27.0, "You make a leather chaps.", 4, new Item(ItemID.LEATHER)
			)),
	HARDLEATHER_BODY(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.HARD_LEATHER),
			new CraftableItem(
					new Item(ItemID.HARDLEATHER_BODY),
					28, 35, "You make a hardleather body.", 4,
					new Item(ItemID.HARD_LEATHER))),
	SPIKY_VAMBRACES(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(
					new Item(ItemID.SPIKY_VAMBRACES),
					32, 6, "You make spiky vambraces.", 4,
					new Item(ItemID.LEATHER),
					new Item(ItemID.KEBBIT_CLAWS))),
	LEATHER_COIF(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(new Item(ItemID.COIF), 38, 37.0, "You make a leather coif.", 4, new Item(ItemID.LEATHER)
			)),
	LEATHER_COWL(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(
					new Item(ItemID.LEATHER_COWL),
					9, 18.5, "You make a leather cowl.", 4,
					new Item(ItemID.LEATHER)
			)),
	STUDDED_BODY(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(
					new Item(ItemID.STUDDED_BODY),
					42, 40, "You make a studded body.", 4,
					new Item(ItemID.LEATHER_BODY),
					new Item(ItemID.STEEL_STUDS))),
	STUDDED_CHAPS(
			new Item(ItemID.NEEDLE),
			new Item(ItemID.LEATHER),
			new CraftableItem(
					new Item(ItemID.STUDDED_CHAPS),
					44, 42, "You make studded chaps.", 4,
					new Item(ItemID.LEATHER_CHAPS),
					new Item(ItemID.STEEL_STUDS)));


	private final Item use;
	private final Item with;
	private final CraftableItem[] craftableItems;

	Leather(Item use, Item with, CraftableItem... items) {
		this.use = use;
		this.with = with;
		this.craftableItems = items;
	}
	
	@Override
	public AnimationLoop getAnimationLoop() {
		return new AnimationLoop(new Animation(1249), 6);
	}

	@Override
	public SoundLoop getSoundLoop() {
		return new SoundLoop(new Sound(2587), 6);
	}

	@Override
	public Item getUse() {
		return use;
	}

	@Override
	public Item getWith() {
		return with;
	}

	@Override
	public CraftableItem[] getCraftableItems() {
		return craftableItems;
	}

	@Override
	public RequiredItem[] getRequiredItems(int index) {
		final CraftableItem craftableItem = craftableItems[index];
		final Item[] required = craftableItem.getRequiredItems();
		final ArrayList<RequiredItem> requiredItems = new ArrayList<>();
		requiredItems.add(new RequiredItem(getUse()));
		for (Item item : required){
			requiredItems.add(new RequiredItem(new Item(ItemID.THREAD, item.getAmount()), true));
			requiredItems.add(new RequiredItem(item, true));
		}
		return requiredItems.toArray(new RequiredItem[]{});
	}

	@Override
	public String getName() {
		return "Leather";
	}
	
	public static Craftable forButton(int button) {
		if (button >= 8633 && button <= 8635) {
			return LEATHER_BODY;
		}
		
		if (button >= 8636 && button <= 8638) {
			return LEATHER_GLOVES;
		}
		
		if (button >= 8639 && button <= 8641) {
			return LEATHER_BOOTS;
		}
		
		if (button >= 8642 && button <= 8644) {
			return LEATHER_VAMBRACES;
		}
		
		if (button >= 8645 && button <= 8647) {
			return LEATHER_CHAPS;
		}
		
		if (button >= 8648 && button <= 8650) {
			return LEATHER_COIF;
		}
		
		if (button >= 8651 && button <= 8653) {
			return LEATHER_COWL;
		}
		
		return null;
	}
	
}