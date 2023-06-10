package com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl;

import java.util.ArrayList;
import java.util.Arrays;

import com.grinder.game.content.skill.skillable.impl.crafting.Crafting;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.Craftable;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.CraftableItem;
import com.grinder.game.model.Animation;
import com.grinder.game.model.AnimationLoop;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.SoundLoop;
import com.grinder.util.ItemID;

public enum Hide implements Craftable {
	
	GREEN_DRAGONHIDE(new Item(ItemID.NEEDLE), new Item(1745),
			new CraftableItem(new Item(1135), 63, 186.0, "You make a green dragonhide body.", 4, new Item(1745, 3)),
			new CraftableItem(new Item(1065), 57, 62.0, "You make a pair of green dragonhide vambraces.", 4, new Item(1745, 1)),
			new CraftableItem(new Item(1099), 60, 124.0, "You make a green dragonhide chaps.", 4, new Item(1745, 2))),
	BLUE_DRAGONHIDE(new Item(ItemID.NEEDLE), new Item(2505),
			new CraftableItem(new Item(2499), 71, 210.0, "You make a blue dragonhide body.", 4, new Item(2505, 3)),
			new CraftableItem(new Item(2487), 66, 70.0, "You make a pair of blue dragonhide vambraces.", 4, new Item(2505, 1)),
			new CraftableItem(new Item(2493), 68, 140.0, "You make a blue dragonhide chaps.", 4, new Item(2505, 2))),
	RED_DRAGONHIDE(new Item(ItemID.NEEDLE), new Item(2507),
			new CraftableItem(new Item(2501), 77, 234.0, "You make a red dragonhide body.", 4, new Item(2507, 3)),
			new CraftableItem(new Item(2489), 76, 78.0, "You make a pair of red dragonhide vambraces.", 4, new Item(2507, 1)),
			new CraftableItem(new Item(2495), 75, 156.0, "You make a red dragonhide chaps.", 4, new Item(2507, 2))),
	BLACK_DRAGONHIDE(new Item(ItemID.NEEDLE), new Item(2509),
			new CraftableItem(new Item(2503), 84, 258.0, "You make a black dragonhide body.", 4, new Item(2509, 3)),
			new CraftableItem(new Item(2491), 79, 86.0, "You make a pair of black dragonhide vambraces.", 4, new Item(2509, 1)),
			new CraftableItem(new Item(2497), 82, 172.0, "You make a black dragonhide chaps.", 4, new Item(2509, 2))),
	SNAKESKIN(new Item(ItemID.NEEDLE), new Item(6289),
			new CraftableItem(new Item(6322), 53, 55.0, "You make a snakeskin body.", 4, new Item(6289, 15)),
			new CraftableItem(new Item(6324), 51, 50.0, "You make a snakeskin chaps.", 4, new Item(6289, 12)),
			new CraftableItem(new Item(6330), 47, 35.0, "You make a pair of snakeskin vambraces.", 4, new Item(6289, 8)),
			new CraftableItem(new Item(6326), 48, 45.0, "You make a snakeskin bandana.", 4, new Item(6289, 5)),
			new CraftableItem(new Item(6328), 45, 30.0, "You make a pair of snakeskin boots.", 4, new Item(6289, 6))),
	YAK_HIDE(new Item(ItemID.NEEDLE), new Item(10818),
			new CraftableItem(new Item(10822), 43, 32.0, "You make a yak-hide armour.", 4, new Item(10818)),
			new CraftableItem(new Item(10824), 46, 32.0, "You make a yak-hide armour.", 4, new Item(10818, 2))),
	HARDLEATHER_BODY(new Item(ItemID.NEEDLE), new Item(1743),
			new CraftableItem(new Item(1131), 28, 35.0, "You make a hardleather body.", 4, new Item(1743))),
	XERICIAN_FABRIC(new Item(ItemID.NEEDLE), new Item(ItemID.XERICIAN_FABRIC),
			new CraftableItem(new Item(13385), 14, 66, "You make a xerician hat.", 4, new Item(13383, 3)),
			new CraftableItem(new Item(13389), 17, 88, "You make a xerician robe.", 4, new Item(13383, 4)),
			new CraftableItem(new Item(13387), 22, 110, "You make a xerician top", 4, new Item(13383, 5))
			);

	private final Item use;
	private final Item with;
	private final CraftableItem[] craftableItems;

	Hide(Item use, Item with, CraftableItem... craftableItems) {
		this.use = use;
		this.with = with;
		this.craftableItems = craftableItems;
	}

	static {
		Arrays.stream(values()).forEach(Crafting::addCraftable);
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
	public AnimationLoop getAnimationLoop() {
		return new AnimationLoop(new Animation(1249), 6);
	}

	@Override
	public SoundLoop getSoundLoop() {
		return new SoundLoop(new Sound(2587), 6);
	}

	@Override
	public String getName() {
		return "Hide";
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
	
}