package com.grinder.game.content.skill.skillable.impl.crafting;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.ItemCreationSkillable;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.Craftable;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.CraftableItem;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.Amethyst;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.CraftableAmethyst;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.Leather;
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.LeatherShield;
import com.grinder.game.content.skill.skillable.impl.fletching.FletchableAmmo;
import com.grinder.game.content.skill.skillable.impl.fletching.FletchableItem;
import com.grinder.game.content.skill.skillable.impl.fletching.FletchableLog;
import com.grinder.game.content.skill.skillable.impl.fletching.Fletching;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.*;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.menu.impl.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.interfaces.menu.CreationMenu.CreationMenuAction;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.sound.SoundLoop;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import kotlin.Pair;

import java.util.*;

import static com.grinder.util.ItemID.*;

/**
 * A class that represents the Crafting skill.
 *
 * @author Blake
 */
public class Crafting {

	private static final HashMap<Integer, Craftable> CRAFTABLES = new HashMap<>();
	private static final int LEATHER_CRAFTING_INTERFACE_ID = 2311;
	private static final int CLOSE_BUTTON_ID = 2422;

	/**
	 * Attempts to craft a gem.
	 *
	 * @param player       the {@link Player} interacting with the items.
	 * @param itemUsed     the item that is used on
	 * @param itemUsedWith the item that is selected for use on
	 * @return {@code true} if the items could be used together,
	 * {@code false} otherwise.
	 */
	public static boolean itemOnItem(Player player, int itemUsed, int itemUsedWith) {

		if (itemUsed == NEEDLE && itemUsedWith == LEATHER || itemUsed == LEATHER && itemUsedWith == NEEDLE) {
			player.getPacketSender().sendInterface(LEATHER_CRAFTING_INTERFACE_ID);
			return true;
		}
		if (PlayerExtKt.tryRandomEventTrigger(player, 1F))
			return true;

		if ((itemUsed == CHISEL || itemUsedWith == CHISEL)) {
			if (itemUsed == CHISEL) {
				if (Snelms.cutShell(player, itemUsedWith)) {
					return true;
				}
			} else {
				if (Snelms.cutShell(player, itemUsed)) {
					return true;
				}
			}
		}

		if ((itemUsed == CHISEL || itemUsedWith == CHISEL) && (itemUsed == AMETHYST || itemUsedWith == AMETHYST)) {
			int oreId = itemUsed == CHISEL ? itemUsedWith : itemUsed;
			Amethyst list = Amethyst.ore.get(oreId);
			if (list != null) {
				Optional<CreationMenu> menu = Optional.empty();

				List<RequiredItem> requiredItemList = new LinkedList<>();
				// The action that will take place when a player
				// selects an amount to create on the interface.
				CreationMenuAction action = (index, item, amount) -> {
					for (CraftableAmethyst ca : list.getCraftable()) {
						if (ca.getProduct().getId() == item) {
							requiredItemList.add(new RequiredItem(new Item(CHISEL), false));

							requiredItemList.add(new RequiredItem(new Item(list.getOreId()), true));
							SkillUtil.startSkillable(player, new ItemCreationSkillable(
									requiredItemList,
									ca.getProduct(),
									amount,
									new AnimationLoop(ca.getAnimation(), 4),
									ca.getLevelRequired(),
									ca.getExperience(),
									Skill.CRAFTING, "You carefully cut the amethyst into "
									+ Misc.anOrA(ca.getProduct().getDefinition().getName()) + " " +
									ca.getProduct().getDefinition().getName().toLowerCase(), 1));
						}
					}
				};

				// Create the item creation menu interface..
				menu = Optional.of(new QuardrupleItemCreationMenu(player,
						ItemID.AMETHYST_BOLT_TIPS, ItemID.AMETHYST_ARROWTIPS,
						ItemID.AMETHYST_JAVELIN_HEADS, ItemID.AMETHYST_DART_TIP,
						"What would you like to make?", action));


				// Send the interface if present..
				if (menu.isPresent()) {
					player.setCreationMenu(menu);
					menu.get().open();
				}
				return true;
			}
			return false;
		}

		if ((itemUsed == CHISEL || itemUsedWith == CHISEL) && (itemUsed == FRESH_CRAB_SHELL || itemUsedWith == FRESH_CRAB_SHELL)) {
			Optional<CreationMenu> menu = Optional.empty();

			CreationMenuAction action = (index, item, amount) -> {
				SkillUtil.startSkillable(
						player, new ItemCreationSkillable(
								Arrays.asList(new RequiredItem[]{new RequiredItem(FRESH_CRAB_SHELL, true)}),
								new Item(CRAB_HELMET, 1),
								amount,
								new AnimationLoop(new Animation(887), 5),
								15,
								33,
								Skill.CRAFTING, null, 3));
				player.getPacketSender().sendInterfaceRemoval();
			};

			menu = Optional.of(new SingleItemCreationMenu(player,
					CRAB_HELMET,
					"How many do you wish to make?", action));

			if (menu.isPresent()) {
				player.setCreationMenu(menu);
				menu.get().open();
			}
			return true;
		}

		if ((itemUsed == CHISEL || itemUsedWith == CHISEL) && (itemUsed == FRESH_CRAB_CLAW || itemUsedWith == FRESH_CRAB_CLAW)) {
			Optional<CreationMenu> menu = Optional.empty();

			CreationMenuAction action = (index, item, amount) -> {
				SkillUtil.startSkillable(
						player, new ItemCreationSkillable(
								Arrays.asList(new RequiredItem[]{new RequiredItem(FRESH_CRAB_CLAW, true)}),
								new Item(CRAB_CLAW, 1),
								amount,
								new AnimationLoop(new Animation(887), 5),
								15,
								33,
								Skill.CRAFTING, null, 3));
				player.getPacketSender().sendInterfaceRemoval();
			};

			menu = Optional.of(new SingleItemCreationMenu(player,
					CRAB_CLAW,
					"How many do you wish to make?", action));

			if (menu.isPresent()) {
				player.setCreationMenu(menu);
				menu.get().open();
			}
			return true;
		}

		if ((itemUsed == CHISEL || itemUsedWith == CHISEL) || (itemUsed == NEEDLE || itemUsedWith == NEEDLE)
				|| (LeatherShield.isShieldMaterial(itemUsedWith) || LeatherShield.isShieldMaterial(itemUsed))) {

			final Craftable craftable = getCraftable(itemUsed, itemUsedWith);

			if (craftable == null) {
				return false;
			}

			if ((craftable.getUse().getId() != itemUsed || craftable.getWith().getId() != itemUsedWith) && (craftable.getUse().getId() != itemUsedWith || craftable.getWith().getId() != itemUsed)) {
				return false;
			}

			switch (craftable.getCraftableItems().length) {
				case 1:
						player.setCreationMenu(Optional.of(new SingleItemCreationMenu(player, craftable.getCraftableItems()[0].getProduct().getId(), "How many would you like to make?", new CreationMenuAction() {

							@Override
							public void execute(int index, int item, int amount) {
								SkillUtil.startSkillable(player, new ItemCreationSkillable(Arrays.asList(craftable.getRequiredItems(index)), craftable.getCraftableItems()[index].getProduct(), amount, craftable.getAnimationLoop(), craftable.getSoundLoop(), craftable.getCraftableItems()[0].getLevel(), (int) (craftable.getCraftableItems()[0].getExperience() / 1.2), Skill.CRAFTING, craftable.getCraftableItems()[index].getMessageLoop(), craftable.getCraftableItems()[index].getCyclesRequired()));
							}

						}).open()));
					break;
				case 2:
					player.setCreationMenu(Optional.of(new DoubleItemCreationMenu(player, craftable.getCraftableItems()[0].getProduct().getId(), craftable.getCraftableItems()[1].getProduct().getId(), "How many would you like to make?", new CreationMenuAction() {

						@Override
						public void execute(int index, int item, int amount) {
							SkillUtil.startSkillable(player, new ItemCreationSkillable(Arrays.asList(craftable.getRequiredItems(index)), craftable.getCraftableItems()[index].getProduct(), amount, craftable.getAnimationLoop(), craftable.getSoundLoop(), craftable.getCraftableItems()[0].getLevel(), (int) (craftable.getCraftableItems()[0].getExperience() / 1.2), Skill.CRAFTING, craftable.getCraftableItems()[index].getMessageLoop(), craftable.getCraftableItems()[index].getCyclesRequired()));
						}

					}).open()));
					break;
				case 3:
					player.setCreationMenu(Optional.of(new TripleItemCreationMenu(player, craftable.getCraftableItems()[0].getProduct().getId(), craftable.getCraftableItems()[1].getProduct().getId(), craftable.getCraftableItems()[2].getProduct().getId(), "What would you like to make?", new CreationMenuAction() {

						@Override
						public void execute(int index, int item, int amount) {
							SkillUtil.startSkillable(player, new ItemCreationSkillable(Arrays.asList(craftable.getRequiredItems(index)), craftable.getCraftableItems()[index].getProduct(), amount, craftable.getAnimationLoop(), craftable.getSoundLoop(), craftable.getCraftableItems()[index].getLevel(), (int) (craftable.getCraftableItems()[index].getExperience() / 1.2), Skill.CRAFTING, craftable.getCraftableItems()[index].getMessageLoop(), craftable.getCraftableItems()[index].getCyclesRequired()));
						}

					}).open()));
					break;
				case 4:
					player.setCreationMenu(Optional.of(new QuardrupleItemCreationMenu(player, craftable.getCraftableItems()[0].getProduct().getId(), craftable.getCraftableItems()[1].getProduct().getId(), craftable.getCraftableItems()[2].getProduct().getId(), craftable.getCraftableItems()[3].getProduct().getId(), "What would you like to make?", new CreationMenuAction() {

						@Override
						public void execute(int index, int item, int amount) {
							SkillUtil.startSkillable(player, new ItemCreationSkillable(Arrays.asList(craftable.getRequiredItems(index)), craftable.getCraftableItems()[index].getProduct(), amount, craftable.getAnimationLoop(), craftable.getSoundLoop(), craftable.getCraftableItems()[index].getLevel(), (int) (craftable.getCraftableItems()[index].getExperience() / 1.2), Skill.CRAFTING, craftable.getCraftableItems()[index].getMessageLoop(), craftable.getCraftableItems()[index].getCyclesRequired()));
						}

					}).open()));
					break;
			}
			return true;
		}

		if (itemUsed == HAMMER || itemUsedWith == HAMMER) {
			int rewardItem = 0;
			int itemNeeded = 0;
			int animation = 65535;
			int nailsToUse = BRONZE_NAILS;

			Inventory playerInventory = player.getInventory();

			if (playerInventory.contains(new Item(RUNE_NAILS, 8))) {
				nailsToUse = RUNE_NAILS;
			} else if (playerInventory.contains(new Item(ADAMANTITE_NAILS, 8))) {
				nailsToUse = ADAMANTITE_NAILS;
			} else if (playerInventory.contains(new Item(MITHRIL_NAILS, 8))) {
				nailsToUse = MITHRIL_NAILS;
			} else if (playerInventory.contains(new Item(BLACK_NAILS, 8))) {
				nailsToUse = BLACK_NAILS;
			} else if (playerInventory.contains(new Item(STEEL_NAILS, 8))) {
				nailsToUse = STEEL_NAILS;
			} else if (playerInventory.contains(new Item(IRON_NAILS, 8))) {
				nailsToUse = IRON_NAILS;
			}

			if (itemUsed == TRIBAL_MASK || itemUsedWith == TRIBAL_MASK) {
				itemNeeded = TRIBAL_MASK;
				rewardItem = BROODOO_SHIELD;
				animation = 2410;
			}
			else if (itemUsed == TRIBAL_MASK_3 || itemUsedWith == TRIBAL_MASK_3) {
				itemNeeded = TRIBAL_MASK_3;
				rewardItem = BROODOO_SHIELD_3;
				animation = 2411;
			}
			else if (itemUsed == TRIBAL_MASK_5 || itemUsedWith == TRIBAL_MASK_5) {
				itemNeeded = TRIBAL_MASK_5;
				rewardItem = BROODOO_SHIELD_5;
				animation = 2409;
			}

			if (rewardItem != 0) {
				Optional<CreationMenu> menu = Optional.empty();

				int finalRewardItem = rewardItem;
				int finalItemNeeded = itemNeeded;
				int finalAnimation = animation;
				int finalNailsToUse = nailsToUse;
				CreationMenuAction action = (index, item, amount) -> {
					SkillUtil.startSkillable(
							player, new ItemCreationSkillable(
									Arrays.asList(new RequiredItem[]{new RequiredItem(new Item(HAMMER, 1), false), new RequiredItem(finalItemNeeded, true), new RequiredItem(new Item(finalNailsToUse, 8), true), new RequiredItem(new Item(SNAKESKIN, 2), true)}),
									new Item(finalRewardItem, 1),
									amount,
									new AnimationLoop(new Animation(finalAnimation), 5),
									35,
									100,
									Skill.CRAFTING, null, 3));
					player.getPacketSender().sendInterfaceRemoval();
				};

				menu = Optional.of(new SingleItemCreationMenu(player,
						rewardItem,
						"How many do you wish to make?", action));

				if (menu.isPresent()) {
					player.setCreationMenu(menu);
					menu.get().open();
				}
				return true;
			}

		}

		if (itemUsed == BATTLESTAFF || itemUsedWith == BATTLESTAFF) {
			int itemNeeded = -1;
			int rewardItem = -1;
			int gfx = 0;
			int levelRequired = 0;
			int experienceGained = 0;

			if (itemUsed == AIR_ORB || itemUsedWith == AIR_ORB) {
				itemNeeded = AIR_ORB;
				rewardItem = AIR_BATTLESTAFF;
				levelRequired = 66;
				experienceGained = 138;
				gfx = 1371; //TODO: UPDATE TO REAL GFX
			}
			else if (itemUsed == EARTH_ORB || itemUsedWith == EARTH_ORB) {
				itemNeeded = EARTH_ORB;
				rewardItem = EARTH_BATTLESTAFF;
				levelRequired = 58;
				experienceGained = 112;
				gfx = 1371;
			}
			else if (itemUsed == WATER_ORB || itemUsedWith == WATER_ORB) {
				itemNeeded = WATER_ORB;
				rewardItem = WATER_BATTLESTAFF;
				levelRequired = 54;
				experienceGained = 100;
				gfx = 1370;
			}
			else if (itemUsed == FIRE_ORB || itemUsedWith == FIRE_ORB) {
				itemNeeded = FIRE_ORB;
				rewardItem = FIRE_BATTLESTAFF;
				levelRequired = 62;
				experienceGained = 125;
				gfx = 1372;
			} else {
				return false;
			}

			Optional<CreationMenu> menu = Optional.empty();

			int finalRewardItem = rewardItem;
			int finalItemNeeded = itemNeeded;
			int finalGfx = gfx;
			int finalExpereienceGained = experienceGained;
			CreationMenuAction action = (index, item, amount) -> {
				SkillUtil.startSkillable(
						player, new ItemCreationSkillable(
								Arrays.asList(new RequiredItem[]{new RequiredItem(BATTLESTAFF, true), new RequiredItem(finalItemNeeded, true)}),
								new Item(finalRewardItem, 1),
								amount,
								new AnimationLoop(new Animation(7531), 5),
								new GraphicsLoop(new Graphic(finalGfx), 5),
								15,
								finalExpereienceGained,
								Skill.CRAFTING, null, 3));
				player.getPacketSender().sendInterfaceRemoval();
			};

			menu = Optional.of(new SingleItemCreationMenu(player,
					rewardItem,
					"How many do you wish to make?", action));

			if (menu.isPresent()) {
				player.setCreationMenu(menu);
				menu.get().open();
			}

			return true;
		}

		return false;
	}

	public static boolean clickButton(Player player, int buttonId) {

		if (player.getInterfaceId() != LEATHER_CRAFTING_INTERFACE_ID)
			return false;

		if (buttonId == CLOSE_BUTTON_ID) {
			player.getPacketSender().sendInterfaceRemoval();
			return true;
		}

		if (!player.isJailed()) {
			if (PlayerExtKt.tryRandomEventTrigger(player, 2.8F))
				return true;
		}

		final Craftable craftable = Leather.forButton(buttonId);

		if (craftable == null)
			return true;

		final List<RequiredItem> requiredItemList = Arrays.asList(craftable.getRequiredItems(0));
		final AnimationLoop animationLoop = craftable.getAnimationLoop();
		final SoundLoop soundLoop = craftable.getSoundLoop();

		final CraftableItem craftableItem = craftable.getCraftableItems()[0];
		final int requiredLevel = craftableItem.getLevel();
		final Item product = craftableItem.getProduct();
		final int experienceGain = (int) (craftableItem.getExperience() / 2.2);

		final int amount = getAmountForButtonId(buttonId);


		final String messageLoop = craftableItem.getMessageLoop();
		final int cyclesRequired = craftableItem.getCyclesRequired();

		if (amount > 0) {
			final ItemCreationSkillable skillable = new ItemCreationSkillable(requiredItemList, product, amount, animationLoop, soundLoop, requiredLevel, experienceGain, Skill.CRAFTING, messageLoop, cyclesRequired);
			SkillUtil.startSkillable(player, skillable);
			return true;
		}

		return false;
	}

	private static Craftable getCraftable(int use, int with) {

		if (Fletching.isAShield(with)) {
			return CRAFTABLES.get(with);
		}

		return CRAFTABLES.get(use) == null
				? CRAFTABLES.get(with)
				: CRAFTABLES.get(use);
	}

	public static void addCraftable(Craftable craftable) {
		if (CRAFTABLES.put(craftable.getWith().getId(), craftable) != null) {
			System.err.println("[Crafting] Conflicting item values: " + craftable.getWith().getId() + " Type: " + craftable.getName());
		}
	}

	private static int getAmountForButtonId(final int buttonId) {
		switch (buttonId) {
			/* Make 1 */
			case 8635:
			case 8638:
			case 8641:
			case 8644:
			case 8647:
			case 8650:
			case 8653:
				return 1;

			/* Make 5 */
			case 8634:
			case 8637:
			case 8640:
			case 8643:
			case 8646:
			case 8649:
			case 8652:
				return 5;

			/* Make 10 */
			case 8633:
			case 8636:
			case 8639:
			case 8642:
			case 8645:
			case 8648:
			case 8651:
				return 10;
		}
		return -1;
	}
}
