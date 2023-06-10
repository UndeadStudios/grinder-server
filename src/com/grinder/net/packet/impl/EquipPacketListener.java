package com.grinder.net.packet.impl;

import com.grinder.Server;
import com.grinder.game.content.GameMode;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.content.item.LootingBag;
import com.grinder.game.content.item.MemberItems;
import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.minigame.castlewars.FlagManager;
import com.grinder.game.content.minigame.warriorsguild.rooms.Jimmy;
import com.grinder.game.content.minigame.warriorsguild.rooms.catapult.Catapult;
import com.grinder.game.content.pvp.bountyhunter.PlayerKillItemUnlockManager;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.magic.SpellCasting;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.message.impl.ItemActionMessage;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.ItemActions;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.sound.Sounds;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.net.packet.interaction.PacketInteractionManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.debug.DebugManager;

import java.util.Optional;

import static com.grinder.game.entity.agent.player.equipment.EquipmentConstants.*;

/**
 * This packet listener manages the equip action a player executes when wielding
 * or equipping an item.
 *
 * @author relex lawl
 */
public class EquipPacketListener implements PacketListener {

	public static void resetWeapon(final Player player) {
		resetWeapon(player, true);
	}

	public static void resetWeapon(final Player player, boolean refreshInventory) {

		final Equipment equipment = player.getEquipment();

		if(player.isSpecialActivated())
			player.setSpecialActivatedAndSendState(false);

		if(refreshInventory) {
			player.setUpdateInventory(true);
			equipment.refreshItems();
		}

		EquipmentBonuses.update(player);
		WeaponInterfaces.INSTANCE.assign(player);

		player.updateAppearance();

		final Item equippedWeapon = equipment.get(WEAPON_SLOT);

		if (equippedWeapon.getId() == ItemID.TRIDENT_OF_THE_SEAS_FULL_ || equippedWeapon.getId() == ItemID.TRIDENT_OF_THE_SEAS || equippedWeapon.getId() == 22288)
			SpellCasting.setSpellToCastAutomatically(player, CombatSpellType.TRIDENT_OF_THE_SEAS);
		else if (equippedWeapon.getId() == ItemID.TRIDENT_OF_THE_SWAMP || equippedWeapon.getId() == 22292)
			SpellCasting.setSpellToCastAutomatically(player, CombatSpellType.TRIDENT_OF_THE_SWAMP);
		else if (equippedWeapon.getId() == ItemID.SANGUINESTI_STAFF)
			SpellCasting.setSpellToCastAutomatically(player, CombatSpellType.SANGUINESTI_STAFF);
		else if (equippedWeapon.getId() == ItemID.HOLY_SANGUINESTI_STAFF)
			SpellCasting.setSpellToCastAutomatically(player, CombatSpellType.HOLY_SANGUINESTI_STAFF);
		else if (equippedWeapon.getId() == 22555)
			SpellCasting.setSpellToCastAutomatically(player, CombatSpellType.THAMMARON_SCEPTRE);
		else if (player.getCombat().getAutocastSpell() != null)
			SpellCasting.setSpellToCastAutomatically(player, null);
	}
	@Override
	public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
		int id = packetReader.readShort();
		int slot = packetReader.readShortA();
		int interfaceId = packetReader.readShortA();

		// Validate player..
		if (player == null || player.getHitpoints() <= 0)
			return;

		// Validate slot..
		if (slot < 0 || slot >= player.getInventory().capacity())
			return;

		if (player.BLOCK_ALL_BUT_TALKING)
			return;

		if(player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME) {
			player.stopTeleporting();
		}
		if (player.isTeleporting()) {
			return;
		}

		DebugManager.debug(player, "equip", "Equip: "+id+" slot: "+slot+" "+ItemDefinition.getName(id));

		if(PacketInteractionManager.handleEquipItem(player, new Item(id), slot)) {
			return;
		}
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return;
		}
		if (player.isInTutorial())
			return;
		if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
			return;
		}

		if(ItemActions.INSTANCE.handleClick(player, new ItemActionMessage(id, slot, interfaceId, packetOpcode)))
			return;

		equip(player, id, slot, interfaceId);
	}

	public static void equip(ItemActions.ItemClickAction clickAction) {
		final ItemActionMessage message = clickAction.getItemActionMessage();
		equip(clickAction.getPlayer(), message.getItemId(), message.getSlot(), message.getInterfaceId());
	}

	public static void equip(Player player, int id, int slot, int interfaceId) {

		final Inventory inventory = player.getInventory();
		final Equipment equipment = player.getEquipment();

		if (!inventory.containsAtSlot(slot, id))
			return;

		final Item item = inventory.atSlot(slot);
		final ItemDefinition definition = item.getDefinition();

		if (handleCustomEquipActions(player, id, item))
			return;

		if(Catapult.isShieldEquipped(player)){
			player.sendMessage("You can't do that right now!");
			return;
		}

		if (id == ItemID.GOBLIN_MAIL || id == ItemID.BLUE_GOBLIN_MAIL || id == ItemID.RED_GOBLIN_MAIL
		|| id == ItemID.BLACK_GOBLIN_MAIL || id == ItemID.GREEN_GOBLIN_MAIL || id == ItemID.ORANGE_GOBLIN_MAIL
	|| id == ItemID.PURPLE_GOBLIN_MAIL || id == ItemID.WHITE_GOBLIN_MAIL || id == ItemID.YELLOW_GOBLIN_MAIL) {
			player.sendMessage("This item cannot be equipped by players as it is too small to fit humans.");
			return;
		}

		if (id == ItemID.AVAS_ACCUMULATOR || id == ItemID.AVAS_ATTRACTOR || id == 22109 || id == 21898) {
			if (!QuestManager.hasCompletedQuest(player, "Ernest The Chicken")) {
				player.sendMessage("You must have completed the quest 'Ernest The Chicken' to be able to use wear this equipment.");
				return;
			}
		} else if (id == ItemID.DRAGON_LONGSWORD || id == 15306 || id == 15307 || id == 15308 || id == 15309) {
			if (!QuestManager.hasCompletedQuest(player, "Lost City")) {
				player.sendMessage("You must have completed the quest 'Lost City' to be able to use wear this equipment.");
				return;
			}
		} else if (id == ItemID.DRAGON_SCIMITAR || id == ItemID.DRAGON_SCIMITAR_OR_ || id == 15345 || id == 15349 || id == 15350
				|| id == 15351 || id == 20000 || id == 15346 || id == 15347 || id == 15348 || id == 15152) {
			if (!QuestManager.hasCompletedQuest(player, "Monkey Madness")) {
				player.sendMessage("You must have completed the quest 'Monkey Madness' to be able to use wear this equipment.");
				return;
			}
		} else if (id == ItemID.RUNE_PLATEBODY || id == ItemID.DRAGON_PLATEBODY
					|| id == ItemID.RUNE_PLATEBODY_G_ || id == ItemID.RUNE_PLATEBODY_T_
					|| id == ItemID.SARADOMIN_PLATEBODY || id == ItemID.GUTHIX_PLATEBODY
					|| id == ItemID.ZAMORAK_PLATEBODY || id == 22242 || id == 15173) {
			if (!QuestManager.hasCompletedQuest(player, "Dragon Slayer")) {
				player.sendMessage("You must have completed the quest 'Dragon Slayer' to be able to use wear this equipment.");
				return;
			}
		}

		if(Jimmy.isHoldingKeg(player)){
			player.sendMessage("You can't do that right now!");
			return;
		}

		if (player.getInterfaceId() != EQUIPMENT_SCREEN_INTERFACE_ID && player.getInterfaceId() != BankConstants.INTERFACE_ID) {
			player.getPacketSender().sendInterfaceRemoval();
		}
		player.setDialogue(null);
		player.setDialogueOptions(null);
		player.setDialogueContinueAction(null);
		player.setEnterSyntax(null);

		if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getDefinition().getName().toLowerCase().contains(" claws")
				&& id == ItemID.CRAB_CLAW || player.getEquipment().getItems()[HANDS_SLOT].getId() == ItemID.CRAB_CLAW
				&& item.getDefinition().getName().toLowerCase().contains(" claws")) {
			player.sendMessage("You need to remove your claws first.");
			return;
		}

		SkillUtil.stopSkillable(player);

		switch (interfaceId) {
		case Inventory.INTERFACE_ID:
		case BankConstants.INVENTORY_INTERFACE_ID:

			if (checkCustomInventoryEquipAction(player, item))
				return;

			final int targetEquipSlot = definition.getEquipmentType().getSlot();

			if (targetEquipSlot == -1) {
				Server.getLogger().info("Attempting to equip item " + item.getId() + " which has no defined equipment slot.");
				return;
			}

			// Handle duel arena settings..
			if (blockByDuelArenaSettings(player, item, targetEquipSlot))
				return;

			// Handle Castle Wars
			if (targetEquipSlot == CAPE_SLOT || targetEquipSlot == HEAD_SLOT) {
				if (CastleWars.isInCastleWarsLobby(player) || CastleWars.isInCastleWars(player)) {
					player.sendMessage("You can't wear hats, capes or helms in the arena.");
					return;
				}
			}
			if (targetEquipSlot == WEAPON_SLOT || targetEquipSlot == SHIELD_SLOT) {
				if (CastleWars.isInCastleWars(player) && FlagManager.isHoldingFlag(player)) {
					FlagManager.dropFlag(player);
					return;
				}
			}

			final Item itemEquippedAtSlot = equipment.atSlot(targetEquipSlot).clone();

			if (itemEquippedAtSlot.getId() == item.getId() && itemEquippedAtSlot.getDefinition().isStackable()) {

				final int amount = itemEquippedAtSlot.getAmount() + item.getAmount();
				final int inventorySlot = inventory.getSlot(item);

				inventory.delete(item, slot, false);
				equipment.get(targetEquipSlot).setAmount(amount);
				itemEquippedAtSlot.setAmount(amount);
				inventory.refreshItems();

				player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, -1, inventorySlot, 0);
				player.getPacketSender().sendItemOnInterface(INVENTORY_INTERFACE_ID, itemEquippedAtSlot.getId(), targetEquipSlot, amount);
			} else {

				if (targetEquipSlot == WEAPON_SLOT && definition.isDoubleHanded()) {

					final int requiredFreeSlots = equipment.isSlotOccupied(SHIELD_SLOT) && equipment.isSlotOccupied(WEAPON_SLOT) ? 1 : 0;

					if (inventory.countFreeSlots() < requiredFreeSlots) {
						inventory.full();
						return;
					}

					final Item equippedShield = equipment.get(SHIELD_SLOT);
					final Item equippedWeapon = equipment.get(WEAPON_SLOT);

					equipment.reset(SHIELD_SLOT);
					equipment.set(targetEquipSlot, item);

					player.getPacketSender().sendItemOnInterface(INVENTORY_INTERFACE_ID, -1, SHIELD_SLOT, 0);
					player.getPacketSender().sendItemOnInterface(INVENTORY_INTERFACE_ID, item.getId(), targetEquipSlot, item.getAmount());

					if (equippedWeapon.getId() != -1) {
						if (equippedWeapon.getDefinition().isStackable() && inventory.contains(equippedWeapon.getId())) {
							inventory.reset(slot);
							inventory.add(equippedWeapon);
						} else {
							inventory.set(slot, equippedWeapon);
						}

						player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, equippedWeapon, slot);
					} else {
						final int removalSlot = inventory.getSlot(item);
						inventory.delete(item, removalSlot, false);
						player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, -1, removalSlot, 0);
					}
					if (equippedShield.getId() != -1) {
						inventory.add(equippedShield);
						final int addedSlot = inventory.getSlot(equippedShield);
						player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, equippedShield, addedSlot);
					}

				} else if (targetEquipSlot == SHIELD_SLOT && equipment.get(WEAPON_SLOT).getDefinition().isDoubleHanded()) {

					inventory.set(slot, equipment.get(WEAPON_SLOT));
					player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, equipment.get(WEAPON_SLOT), slot);

					equipment.reset(WEAPON_SLOT);
					equipment.set(SHIELD_SLOT, item);
					resetWeapon(player, false);

					player.getPacketSender().sendItemOnInterface(INVENTORY_INTERFACE_ID, -1, WEAPON_SLOT, 0);
					player.getPacketSender().sendItemOnInterface(INVENTORY_INTERFACE_ID, item, SHIELD_SLOT);

				} else {
					if (itemEquippedAtSlot.getId() != -1 && targetEquipSlot == itemEquippedAtSlot.getDefinition().getEquipmentType().getSlot()) {
						if (inventory.contains(itemEquippedAtSlot.getId())) {
							final int removalSlot = inventory.getSlot(item);
							equipment.set(targetEquipSlot, item.clone());
							inventory.delete(item, removalSlot, false);
							inventory.add(itemEquippedAtSlot,false);
							final int addSlot = inventory.getSlot(itemEquippedAtSlot);
							player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, -1, removalSlot, 0);
							player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, itemEquippedAtSlot, addSlot);
						} else {
							inventory.set(slot, itemEquippedAtSlot);
							equipment.set(targetEquipSlot, item);
							player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, itemEquippedAtSlot, slot);
						}
					} else {
						inventory.reset(slot);
						equipment.set(targetEquipSlot, item);
						player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, -1, slot, 0);
					}
					player.getPacketSender().sendItemOnInterface(INVENTORY_INTERFACE_ID, equipment.get(targetEquipSlot), targetEquipSlot);
				}
			}

			if (targetEquipSlot == WEAPON_SLOT) {

				resetWeapon(player, false);

				if(!player.getCombat().retaliateAutomatically())
					player.getCombat().reset(false);

			}

        	/*player.carriedWeight = 0;
	        for (final Item equippedItem : equipment.getItems())
				player.carriedWeight += ItemDefinition.forId(equippedItem.getId()).getWeight();*/

	        if (targetEquipSlot == SHIELD_SLOT) {
	        	if (item.getId() == 21633) {
	        		player.performGraphic(new Graphic(1395, GraphicHeight.HIGH));
				}
			}

			EquipmentBonuses.update(player);
			inventory.refreshItems();

			if (interfaceId != Inventory.INTERFACE_ID)
				player.getPacketSender().sendItemContainer(inventory, interfaceId);

			Optional.ofNullable(Sounds.getEquipmentSounds(item, targetEquipSlot))
					.ifPresent(player.getPacketSender()::sendSound);

			player.updateAppearance();

			handleAchievements(player, item);
			break;
		}
	}

	private static void handleAchievements(Player player, Item item) {
		if (item.getId() == 12432 || item.getId() == 15238) {
			AchievementManager.processFor(AchievementType.HAT_TRICK, player);
		} else if(item.getId() == 11235 || item.getId() == 12765
				|| item.getId() == 12766 || item.getId() == 12767 || item.getId() == 12768
				|| item.getId() == 15223) {
			AchievementManager.processFor(AchievementType.DARK_AND_HOLLOW, player);
		} else if (item.getId() == 21295) {
			AchievementManager.processFor(AchievementType.BLAZING_HOT, player);
		} else if (item.getId() == 4084) {
			AchievementManager.processFor(AchievementType.SLIDER_STATION, player);
		} else if(EquipmentUtil.isWearingArmadylSet(player)) {
			AchievementManager.processFor(AchievementType.ARMADYL_RECRUIT, player);
		} else if(EquipmentUtil.isWearingProspectorSet(player)) {
			AchievementManager.processFor(AchievementType.PROSPECTOR_SETUP, player);
		} else if(EquipmentUtil.isWearingLumberJackSet(player)) {
			AchievementManager.processFor(AchievementType.LUMBERJACK_SETUP, player);
		} else if(EquipmentUtil.isWearingMummiesSet(player)) {
			AchievementManager.processFor(AchievementType.MUMMY_SETUP, player);
		} else if(EquipmentUtil.isWearingAnglerSet(player)) {
			AchievementManager.processFor(AchievementType.ANGLER_SETUP, player);
		} else if(EquipmentUtil.isWearingRogueSet(player)) {
			AchievementManager.processFor(AchievementType.ROGUE_SETUP, player);
		} else if(EquipmentUtil.isWearingAnkouSet(player)) {
			AchievementManager.processFor(AchievementType.ANKOU_SETUP, player);
		}
	}

	private static boolean blockByDuelArenaSettings(Player player, Item item, int targetEquipSlot) {
		if (player.getDueling().inDuel()) {
			for (int i = 11; i < player.getDueling().getRules().length; i++) {
				if (player.getDueling().getRules()[i]) {
					DuelRule duelRule = DuelRule.forId(i);
					if (targetEquipSlot == duelRule.getEquipmentSlot()
							|| duelRule == DuelRule.NO_SHIELD && item.getDefinition().isDoubleHanded()) {
						DialogueManager.sendStatement(player,
								"The rules that were set do not allow this item to be equipped.");
						return true;
					}
				}
			}
			if (targetEquipSlot == WEAPON_SLOT || item.getDefinition().isDoubleHanded()) {
				if (player.getDueling().getRules()[DuelRule.LOCK_WEAPON.ordinal()]) {
					DialogueManager.sendStatement(player, "Weapons have been locked in this duel!");
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkCustomInventoryEquipAction(Player player, Item inventoryItem) {
		if(player.getClueScrollManager().handleEquipAction(inventoryItem.getId()))
			return true;

		if(inventoryItem.getId() == 7671 || inventoryItem.getId() == 7673 || inventoryItem.getId() == 20056 || inventoryItem.getId() == 11705 || inventoryItem.getId() == 11706) {
			if (player.isRunning()) {
				player.setRunning(false);
				player.getPacketSender().sendRunStatus();
			}
		}

		if (inventoryItem.getId() == ItemID.SPICY_STEW) {
			player.sendMessage("The stew smells very spicy!");
			return true;
		}

		if (inventoryItem.getId() == ItemID.ROTTEN_POTATO) {
			DialogueManager.sendStatement(player, "Hmm this looks strange..maybe I should look further what I can do with it.");
			return true;
		}

		if (!PlayerKillItemUnlockManager.equipItem(player, inventoryItem)) {
			return true;
		}
		if (inventoryItem.getId() == 6570 || inventoryItem.getId() == 21295) {
			if (player.getAchievements().getProgress()[AchievementType.FIRE_WARRIOR.ordinal()] == 0) {
				player.sendMessage("You must complete the Fire Warrior task before trying to equip this item.");
				return true;
			}
		}
		if (inventoryItem.getDefinition().getName().contains(" (damaged)") && inventoryItem.getDefinition().getName().contains("Torva")) {
			player.sendMessage("You should repair your torva gear first by using Bandosian components to be able to wear it.");
			return true;
		}
		if (inventoryItem.getDefinition().getName().contains(" (damaged)") && inventoryItem.getDefinition().getName().contains("Pernix")) {
			player.sendMessage("You should repair your pernix gear first by using Armadyl components to be able to wear it.");
			return true;
		}
		if (inventoryItem.getDefinition().getName().contains(" (damaged)") && inventoryItem.getDefinition().getName().contains("Virtus")) {
			player.sendMessage("You should repair your virtus gear first by using Magical components to be able to wear it.");
			return true;
		}
		if (!player.getGameMode().equals(GameMode.IRONMAN) && EquipmentUtil.IRONMAN_ARMOR.contains(inventoryItem.getId())) {
			player.getPacketSender().sendMessage("Only Iron Man can wear this piece of equipment.", 1000);
			return true;
		}

		if (!player.getGameMode().equals(GameMode.HARDCORE_IRONMAN) && EquipmentUtil.HARDCORE_IRONMAN_ARMOR.contains(inventoryItem.getId())) {
			player.getPacketSender().sendMessage("Only Hardcore Iron Man can wear this piece of equipment.", 1000);
			return true;
		}

		if (!player.getGameMode().equals(GameMode.ULTIMATE_IRONMAN) && EquipmentUtil.ULTIMATE_IRONMAN_ARMOR.contains(inventoryItem.getId())) {
			player.getPacketSender().sendMessage("Only Ultimate Iron Man can wear this piece of equipment.", 1000);
			return true;
		}

		if (!player.getGameMode().equals(GameMode.ONE_LIFE) && EquipmentUtil.ONE_LIFE_ARMOR.contains(inventoryItem.getId())) {
			player.getPacketSender().sendMessage("Only One Life characters can wear this piece of equipment.", 1000);
			return true;
		}

		if (!player.getGameMode().equals(GameMode.REALISM) && EquipmentUtil.REALISM_ARMOR.contains(inventoryItem.getId())) {
			player.getPacketSender().sendMessage("Only Realism characters can wear this piece of equipment.", 1000);
			return true;
		}

		if (inventoryItem.getId() == ItemID.DEADMANS_CAPE && !player.getGameMode().isRealism() && !player.getGameMode().isOneLife()) {
			player.getPacketSender().sendMessage("Only Realism and One Life characters can wear this piece of equipment.", 1000);
			return true;
		}

		// Check if player can wield the item..
		if (inventoryItem.getDefinition().getRequirements() != null) {
			for (Skill skill : Skill.values()) {
				if (inventoryItem.getDefinition().getRequirements()[skill.ordinal()] > player.getSkillManager()
						.getMaxLevel(skill)) {
					StringBuilder vowel = new StringBuilder();
					if (skill.getName().startsWith("a") || skill.getName().startsWith("e")
							|| skill.getName().startsWith("i") || skill.getName().startsWith("o")
							|| skill.getName().startsWith("u")) {
						vowel.append("an ");
					} else {
						vowel.append("a ");
					}
					player.getPacketSender()
							.sendMessage("You need " + vowel.toString() + Misc.formatText(skill.getName())
									+ " level of at least "
									+ inventoryItem.getDefinition().getRequirements()[skill.ordinal()] + " to wear this.", 1000);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean handleCustomEquipActions(Player player, int id, Item inventoryItem) {
		// Members rank only handling
		if (MemberItems.isDiamondMemberItem(id) && (!PlayerUtil.isStaff(player) && !PlayerUtil.isDiamondMember(player))) {
			player.getPacketSender().sendMessage("You must have the Diamond member rank to be able to equip this item.", 1000);
			return true;
		} else if (MemberItems.isTitaniumMemberItem(id) && (!PlayerUtil.isStaff(player) && !PlayerUtil.isTitaniumMember(player))) {
			player.getPacketSender().sendMessage("You must have the Titanium member rank or higher to be able to equip this item.", 1000);
			return true;
		} else if (MemberItems.isPlatinumMemberItem(id) && (!PlayerUtil.isStaff(player) && !PlayerUtil.isPlatinumMember(player))) {
			player.getPacketSender().sendMessage("You must have the Platinum member rank or higher to be able to equip this item.", 1000);
			return true;
		} else if (MemberItems.isLegendaryMemberItem(id) && (!PlayerUtil.isStaff(player) && !PlayerUtil.isLegendaryMember(player))) {
			player.getPacketSender().sendMessage("You must have the Legendary member rank or higher to be able to equip this item.", 1000);
			return true;
		} else if (MemberItems.isAmethystMemberItem(id) && (!PlayerUtil.isStaff(player) && !PlayerUtil.isAmethystMember(player))) {
			player.getPacketSender().sendMessage("You must have the Amethyst member rank or higher to be able to equip this item.", 1000);
			return true;
		} else if (MemberItems.isTopazMemberItem(id) && (!PlayerUtil.isStaff(player) && !PlayerUtil.isTopazMember(player))) {
			player.getPacketSender().sendMessage("You must have the Topaz member rank or higher to be able to equip this item.", 1000);
			return true;
		} else if (MemberItems.isRubyMemberItem(id) && (!PlayerUtil.isStaff(player) && !PlayerUtil.isRubyMember(player))) {
			player.getPacketSender().sendMessage("You must have the Ruby member rank or higher to be able to equip this item.", 1000);
			return true;
		} else if (MemberItems.isBronzeMemberItem(id) && (!PlayerUtil.isStaff(player) && !PlayerUtil.isBronzeMember(player))) {
			player.getPacketSender().sendMessage("You must have the Bronze member rank or higher to be able to equip this item.", 1000);
			return true;
		} else if ((inventoryItem.getId() == ItemID.HYDRO_CAPE || inventoryItem.getId() == 15749) && player.getPoints().get(AttributeManager.Points.AQUAIS_NEIGE_GAMES_COMPLETED) <= 0) {
			player.sendMessage("You must have completed the Aquais Neige challenge before trying to equip this item.");
			return true;
		}

		if (inventoryItem.getId() == 11941) {
			LootingBag.check(player);
			return true;
		}
		return false;
	}
}