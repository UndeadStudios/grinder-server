package com.grinder.game.content.skill.skillable.impl.fletching;

import java.util.*;

import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.ItemCreationSkillable;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.AnimationLoop;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.menu.impl.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.menu.CreationMenu.CreationMenuAction;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import kotlin.Pair;

import static com.grinder.util.ItemID.*;

/**
 * Represents the Fletching skill which can be used to create bows, arrows and
 * other items.
 *
 * @author Professor Oak
 */
// TODO: Clean up, merge enums.
public class Fletching{

	/**
	 * Attempts to fletch ammo.
	 */
	public static boolean fletchAmmo(Player player, int itemUsed, int itemUsedWith) {
		// Making ammo such as bolts and arrows..
		for (FletchableAmmo ammo : FletchableAmmo.values()) {
			if ((ammo.getItem1() == itemUsed || ammo.getItem1() == itemUsedWith)
					&& (ammo.getItem2() == itemUsed || ammo.getItem2() == itemUsedWith)) {
				if (player.getSkillManager().getCurrentLevel(Skill.FLETCHING) >= ammo.getLevelReq()) {

					if (!ammo.hasRequirements(player))
						return false;

					int first = player.getInventory().getAmount(ammo.getItem1());
					int second = player.getInventory().getAmount(ammo.getItem2());
					int total = 1;

					if (first < second) {
						total = first;
					}

					if (second < first) {
						total = second;
					}
					if (first == second) {
						total = first;
					}

					if (total >= 10) {

						/**
						 * If you have 10 or more use the interface
						 */
						Optional<CreationMenu> menu;

						if (ItemDefinition.forId(ammo.getOutcome()).getName().toLowerCase().contains("arrow") || ItemDefinition.forId(ammo.getOutcome()).getName().toLowerCase().contains("bolt") ||
								ItemDefinition.forId(ammo.getOutcome()).getName().toLowerCase().contains("javelin") || ItemDefinition.forId(ammo.getOutcome()).getName().toLowerCase().contains("shaft") ||
								ItemDefinition.forId(ammo.getOutcome()).getName().toLowerCase().contains("headless")) {
							CreationMenu.CreationMenuAction action = (index, item, amount) -> {
								SkillUtil.startSkillable(
										player, new ItemCreationSkillable(
												Arrays.asList(new RequiredItem(new Item(ammo.getItem1(), 15), true), new RequiredItem(new Item(ammo.getItem2(), 15), true)),
												new Item(ammo.getOutcome(), 15),
												amount,
												new AnimationLoop(new Animation(65535), 5),
												ammo.getLevelReq(),
												ammo.getXp(),
												Skill.FLETCHING, "You make some " + ItemDefinition.forId(ammo.getOutcome()).getName() + ".", 3));
								player.getPacketSender().sendInterfaceRemoval();
							};

							menu = Optional.of(new SingleItemCreationMenu(player,
									ammo.getOutcome(),
									"How many sets of 15 do you wish to make?", action));


							if (menu.isPresent()) {
								player.setCreationMenu(menu);
								menu.get().open();
							}
							return true;
						} else {
							total = 10;
						}
					}
					int xpMultiplier = total / 10;
					if (!player.isJailed()) {
						if (PlayerExtKt.tryRandomEventTrigger(player, 2.8F))
							return true;
					}
					player.getInventory().delete(ammo.getItem1(), total);
					player.getInventory().delete(ammo.getItem2(), total);
					player.getInventory().add(ammo.getOutcome(), total);
					player.getSkillManager().addExperience(Skill.FLETCHING, xpMultiplier * ammo.getXp());

					String name = ItemDefinition.forId(ammo.getOutcome()).getName();

					if (!name.endsWith("s"))
						name += "s";

					player.sendMessage("You make some " + name + ".");

					// Skilling tasks
					SkillTaskManager.perform(player, ammo.getOutcome(), total, SkillMasterType.FLETCHING);
				} else {
					player.getPacketSender().sendMessage(
							"You need a Fletching level of at least " + ammo.getLevelReq() + " to fletch this.", 1000);
				}
				return true;
			}
		}

		if ((itemUsed == WOLF_BONES || itemUsedWith == WOLF_BONES) && (itemUsed == CHISEL || itemUsedWith == CHISEL)) {
			if (player.getSkills().getLevel(Skill.CRAFTING) < 5) {
				player.sendMessage("You need at least level 5 Crafting to do this!");
				return true;
			}
			if (player.getSkills().getLevel(Skill.FLETCHING) < 5) {
				player.sendMessage("You need at least level 5 Fletching to do this!");
				return true;
			}

			Optional<CreationMenu> menu;

			CreationMenu.CreationMenuAction action = (index, item, amount) -> {
				SkillUtil.startSkillable(
					player, new ItemCreationSkillable(
							Arrays.asList(new RequiredItem[]{new RequiredItem(WOLF_BONES, true)}),
							new Item(WOLFBONE_ARROWTIPS, 4),
							amount,
							new AnimationLoop(new Animation(65535), 5),
							5,
							3,
							Skill.CRAFTING, null, 3));
				player.getPacketSender().sendInterfaceRemoval();
			};

			menu = Optional.of(new SingleItemCreationMenu(player,
					WOLFBONE_ARROWTIPS,
					"How many would you like to make?", action));

			if (menu.isPresent()) {
				player.setCreationMenu(menu);
				menu.get().open();
			}
			return true;
		}
		return false;
	}

	/**
	 * Verifies the user has the amount of logs needed for the transaction
	 * @param player - The Player
	 * @param requiredItems - List of required items needed for fletching product
	 * @param hasItems -
	 * @return
	 */
	public static boolean checkShieldRequirements(Player player, List<RequiredItem> requiredItems, boolean hasItems) {
		Map<Integer, Integer> itemCount = new HashMap<>();
		for (RequiredItem item : requiredItems) {
			if (itemCount.containsKey(item.getItem().getId())) {
				int currentCount = itemCount.get(item.getItem().getId());
				itemCount.put(item.getItem().getId(), currentCount + 1);
			} else {
				itemCount.put(item.getItem().getId(), 1);
			}
			for (int keyId : itemCount.keySet()) {
				if (itemCount.get(keyId) >= 1) {
					if (player.getInventory().getAmount(keyId) < itemCount.get(keyId)) {
						String prefix = item.getItem().getAmount() > 1 ? Integer.toString(item.getItem().getAmount()) : "some";
						DialogueManager.sendStatement(player, "You " + (!hasItems ? "also need to have" : "need to have") + " " + prefix + " "
								+ item.getItem().getDefinition().getName().toLowerCase() + " to continue.");
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Attempts to fletch bolt tips.
	 */
	public static boolean fletchBoltTips(Player player, int itemUsed, int itemUsedWith) {
		// Making ammo such as bolts and arrows..
		for (FletchableBoltTips tip : FletchableBoltTips.values()) {

			final int boltTipId = tip.getItem().getId();

			if ((itemUsed == boltTipId && itemUsedWith == 1755)
					|| itemUsed == 1755 && itemUsedWith == boltTipId) {

				if (PlayerExtKt.tryRandomEventTrigger(player, 2.8F))
					return true;

				if ((tip.getItem().getId() == itemUsed || tip.getItem().getId() == itemUsedWith)
						&& (tip.getItem().getId() == itemUsedWith || tip.getItem().getId() == itemUsed)) {
					if (player.getSkillManager().getCurrentLevel(Skill.FLETCHING) < tip.getLevelReq()) {
						player.getPacketSender().sendMessage(
								"You need a Fletching level of at least " + tip.getLevelReq() + " to fletch this.", 1000);
						return true;
					}
					player.performAnimation(new Animation(891));
					player.getPacketSender().sendAreaPlayerSound(Sounds.CUT_GEM_SOUND);
					player.getInventory().delete(tip.getItem());
					player.getInventory().add(tip.getOutcome());
					player.getSkillManager().addExperience(Skill.FLETCHING, tip.getXp());
					player.getPacketSender().sendMessage("You fletch " + tip.getOutcome().getAmount() + " " + tip.getOutcome().getDefinition().getName().toLowerCase().replace("_", " ") + ".");

					// Skilling tasks
					SkillTaskManager.perform(player, tip.getOutcome().getId(), tip.getOutcome().getAmount(), SkillMasterType.FLETCHING);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Attempts to fletch crossbows.
	 */
	public static boolean fletchCrossbow(Player player, int itemUsed, int itemUsedWith) {
		for (FletchableCrossbow c : FletchableCrossbow.values()) {
			if ((c.getStock() == itemUsed || c.getStock() == itemUsedWith)
					&& (c.getLimbs() == itemUsed || c.getLimbs() == itemUsedWith)) {
				player.setCreationMenu(Optional.of(new SingleItemCreationMenu(player, c.getUnstrung(),
						"How many would you like to make?", (index, item, amount) -> SkillUtil
								.startSkillable(player, new ItemCreationSkillable(
										Arrays.asList(
												new RequiredItem(new Item(c.getStock()), true),
												new RequiredItem(new Item(c.getLimbs()), true)
										),
										new Item(c.getUnstrung()),
										amount,
										new AnimationLoop(c.getAnimation(), 4),
										c.getLevel(),
										c.getLimbsExp(),
										Skill.FLETCHING, "You attach the stock to the limbs and create an unstrung crossbow.", 2))).open()));
				return true;
			}
		}
		return false;
	}

	/**
	 * Attempts to string a bow.
	 */
	public static boolean stringBow(Player player, int itemUsed, int itemUsedWith) {
		if (itemUsed == BOW_STRING || itemUsedWith == BOW_STRING || itemUsed == CROSSBOW_STRING
				|| itemUsedWith == CROSSBOW_STRING) {
			int string = itemUsed == BOW_STRING || itemUsed == CROSSBOW_STRING ? itemUsed : itemUsedWith;
			int unstrung = itemUsed == BOW_STRING || itemUsed == CROSSBOW_STRING ? itemUsedWith : itemUsed;
			StringableBow bow = StringableBow.unstrungBows.get(unstrung);
			if (bow != null) {
				if (bow.getBowStringId() == string) {
					player.setCreationMenu(Optional.of(new SingleItemCreationMenu(player, bow.getResult(),
							"How many would you like to make?", (index, item, amount) -> SkillUtil
									.startSkillable(player, new ItemCreationSkillable(
											Arrays.asList(
													new RequiredItem(new Item(bow.getItemId()), true),
													new RequiredItem(new Item(bow.getBowStringId()), true)
											),
											new Item(bow.getResult()), amount,
											new AnimationLoop(bow.getAnimation(), 4),
											bow.getLevelReq(),
											bow.getExp(),
											Skill.FLETCHING, "You add a string to the bow.", 2))).open()));
					return true;
				} else {
					player.getPacketSender().sendMessage("This bow can't be strung with that.");
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Attempts to fletch logs.
	 */
	public static boolean fletchLog(Player player, int itemUsed, int itemUsedWith) {
		if (itemUsed == ItemID.KNIFE || itemUsedWith == ItemID.KNIFE) {
			int logId = itemUsed == ItemID.KNIFE ? itemUsedWith : itemUsed;
			FletchableLog list = FletchableLog.logs.get(logId);
			if (list != null) {
				int menuSize = list.getFletchable().length;
				Optional<CreationMenu> menu = Optional.empty();

				List<RequiredItem> requiredItemList = new LinkedList<>();
				// The action that will take place when a player
				// selects an amount to create on the interface.
				CreationMenuAction action = (index, item, amount) -> {
					for (FletchableItem fl : list.getFletchable()) {
						if (fl.getProduct().getId() == item) {
							requiredItemList.add(new RequiredItem(new Item(KNIFE), false));

							if (isAShield(fl.getProduct().getId())) { //Add extra log if its a shield
								requiredItemList.add(new RequiredItem(new Item(list.getLogId()), true));
							}

							requiredItemList.add(new RequiredItem(new Item(list.getLogId()), true));
							SkillUtil.startSkillable(player, new ItemCreationSkillable(
									requiredItemList,
									fl.getProduct(),
									amount,
									new AnimationLoop(fl.getAnimation(), 4),
									fl.getLevelRequired(),
									fl.getExperience(),
									Skill.FLETCHING, "You carefully cut the wood into "
									+ Misc.anOrA(fl.getProduct().getDefinition().getName()) +" " +
									fl.getProduct().getDefinition().getName().toLowerCase() +".", isAShield(fl.getProduct().getId()) ? 7: 2));
						}
					}
				};

				// Create the item creation menu interface..
				switch (menuSize) {
					case 5:
						menu = Optional.of(new FiveItemCreationMenu(player, "What would you like to make?", action,
								new Pair<>(list.getFletchable()[0].getProduct().getId(), list.getFletchable()[0].getProduct().getDefinition().getName()),
								new Pair<>(list.getFletchable()[1].getProduct().getId(), list.getFletchable()[1].getProduct().getDefinition().getName()),
								new Pair<>(list.getFletchable()[2].getProduct().getId(), list.getFletchable()[2].getProduct().getDefinition().getName()),
								new Pair<>(list.getFletchable()[3].getProduct().getId(), list.getFletchable()[3].getProduct().getDefinition().getName()),
								new Pair<>(list.getFletchable()[4].getProduct().getId(), list.getFletchable()[4].getProduct().getDefinition().getName())));
						break;
				case 4:
					menu = Optional.of(new QuardrupleItemCreationMenu(player,
							list.getFletchable()[0].getProduct().getId(), list.getFletchable()[1].getProduct().getId(),
							list.getFletchable()[2].getProduct().getId(), list.getFletchable()[3].getProduct().getId(),
							"What would you like to make?", action));
					break;
				case 3:
					menu = Optional.of(new TripleItemCreationMenu(player, list.getFletchable()[0].getProduct().getId(),
							list.getFletchable()[1].getProduct().getId(), list.getFletchable()[2].getProduct().getId(),
							"What would you like to make?", action));
					break;
				case 2:
					menu = Optional.of(new DoubleItemCreationMenu(player,
							list.getFletchable()[0].getProduct().getId(),
							list.getFletchable()[1].getProduct().getId(),
							"What would you like to make?", action));
						break;
				case 1:
					menu = Optional.of(new SingleItemCreationMenu(player,
							list.getFletchable()[0].getProduct().getId(),
							"How many would you like to make?", action));
						break;
				}

				// Send the interface if present..
				if (menu.isPresent()) {
					player.setCreationMenu(menu);
					menu.get().open();
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Simple method that checks if the ID provided is a shield
	 * @param id - the Item ID
	 * @return - True if its a shield, false if its anything else.
	 */
	public static boolean isAShield(int id) {
		if ((id == 22266) || (id == 22251) || (id == 22254) || (id == 22257) || (id == 22260) || (id == 22266) || (id == 22263)) return true;
		else return false;
	}

}
