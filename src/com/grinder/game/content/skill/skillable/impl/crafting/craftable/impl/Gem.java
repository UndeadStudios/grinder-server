package com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl;

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

public enum Gem implements Craftable {

//	GOLD_AMULET_U(
//			new Item(ItemID.BALL_OF_WOOL),
//			new Item(ItemID.GOLD_AMULET_U_),
//			new CraftableItem(
//					new Item(ItemID.GOLD_AMULET),
//					new Item(1759),
//					8,
//					30.00,
//					"You put some string on your amulet.",
//					2)){
//		@Override
//		public RequiredItem[] getRequiredItems(int index) {
//			return new RequiredItem[] {
//					new RequiredItem(getUse(), true),
//					new RequiredItem(getWith(), true)
//			};
//		}
//	},
	
	OPAL(new Item(1755), new Item(1625),
			new CraftableItem(new Item(1609), 1, 15.0, "You cut the opal.", 2, new Item(1625))),
	LIMESTONE(new Item(1755), new Item(ItemID.LIMESTONE), new CraftableItem(new Item(ItemID.LIMESTONE_BRICK), 12, 6.0, "You use the chisel on the limestone and carve it into a building block.", 2, new Item(ItemID.LIMESTONE))),
	GRANITE_5KG(new Item(1755), new Item(ItemID.GRANITE_5KG_), new CraftableItem(new Item(ItemID.GRANITE_2KG_, 2), 1, 0, "You use the chisel on the granite and carve it into a smaller blocks.", 2, new Item(ItemID.GRANITE_5KG_))),
	GRANITE_2KG(new Item(1755), new Item(ItemID.GRANITE_2KG_), new CraftableItem(new Item(ItemID.GRANITE_500G_, 4), 1, 0, "You use the chisel on the granite and carve it into a smaller blocks.", 2, new Item(ItemID.GRANITE_2KG_))),
	JADE(new Item(1755), new Item(1627), new CraftableItem(new Item(1611), 13, 20.0, "You cut the jade.", 2, new Item(1627))),
	RED_TOPAZ(new Item(1755), new Item(1629), new CraftableItem(new Item(1613), 16, 25.0, "You cut the red topaz.", 2, new Item(1629))),
	SAPPHIRE(new Item(1755), new Item(1623), new CraftableItem(new Item(1607), 20, 50.0, "You cut the sapphire.", 2, new Item(1623))),
	EMERALD(new Item(1755), new Item(1621), new CraftableItem(new Item(1605), 27, 67.5, "You cut the emerald.", 2, new Item(1621))),
	RUBY(new Item(1755), new Item(1619), new CraftableItem(new Item(1603), 34, 85.0, "You cut the ruby.", 2, new Item(1619))),
	DIAMOND(new Item(1755), new Item(1617), new CraftableItem(new Item(1601), 43, 107.5, "You cut the diamond.", 2, new Item(1617))),
	DRAGONSTONE(new Item(1755), new Item(1631),
			new CraftableItem(new Item(1615), 55, 137.5, "You cut the dragonstone.", 2, new Item(1631))),
	ONYX(new Item(1755), new Item(6571), new CraftableItem(new Item(6573), 67, 167.5, "You cut the onyx.", 2, new Item(6571))),
	ZENYTE(new Item(1755), new Item(19496), new CraftableItem(new Item(19493), 89, 200.0, "You cut the zenyte.", 2, new Item(19496)));

	private final Item use;
	private final Item with;
	private final CraftableItem[] craftableItems;

	Gem(Item use, Item with, CraftableItem... craftableItems) {
		this.use = use;
		this.with = with;
		this.craftableItems = craftableItems;
	}

	static {
		Arrays.stream(values()).forEach(Crafting::addCraftable);
	}

	@Override
	public AnimationLoop getAnimationLoop() {
		switch (this) {
		case OPAL:
		case JADE:
			return new AnimationLoop(new Animation(890), 4);
		case RED_TOPAZ:
			return new AnimationLoop(new Animation(892), 4);
		case LIMESTONE:
		case GRANITE_5KG:
		case GRANITE_2KG:
			return new AnimationLoop(new Animation(1309), 4);
		case SAPPHIRE:
			return new AnimationLoop(new Animation(888), 4);
		case EMERALD:
			return new AnimationLoop(new Animation(889), 4);
		case RUBY:
			return new AnimationLoop(new Animation(887), 4);
		case DIAMOND:
			return new AnimationLoop(new Animation(886), 4);
		case DRAGONSTONE:
			return new AnimationLoop(new Animation(885), 4);
		case ONYX:
			return new AnimationLoop(new Animation(2717), 4);
		case ZENYTE:
			return new AnimationLoop(new Animation(7185), 4);
		default:
			return new AnimationLoop(new Animation(891), 4);
		}
	}

	@Override
	public SoundLoop getSoundLoop() {
		switch (this) {
			case OPAL:
			case JADE:
			case ONYX:
			case DRAGONSTONE:
			case ZENYTE:
			case DIAMOND:
			case RUBY:
			case RED_TOPAZ:
			case SAPPHIRE:
			case EMERALD:
				return new SoundLoop(new Sound(2586), 4);
			default:
				return null;
		}
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
		return new RequiredItem[] { new RequiredItem(use), new RequiredItem(with, true) };
	}
	
	@Override
	public String getName() {
		return "Gem";
	}

}