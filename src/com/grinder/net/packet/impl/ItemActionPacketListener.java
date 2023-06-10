package com.grinder.net.packet.impl;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.item.*;
import com.grinder.game.content.item.coloring.ItemColorCustomizer;
import com.grinder.game.content.item.coloring.ItemColorCustomizer.ColorfulItem;
import com.grinder.game.content.item.degrading.DegradableType;
import com.grinder.game.content.item.transforming.ItemTransforming;
import com.grinder.game.content.minigame.castlewars.BarricadeManager;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.ItemCreationSkillable;
import com.grinder.game.content.skill.skillable.impl.Prayer;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType;
import com.grinder.game.content.skill.skillable.impl.runecrafting.pouch.PouchType;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportTablets;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.message.decoder.ItemActionMessageDecoder;
import com.grinder.game.message.impl.ItemActionMessage;
import com.grinder.game.model.*;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.consumable.ConsumableUtil;
import com.grinder.game.model.interfaces.dialogue.*;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.menu.impl.SingleItemCreationMenu;
import com.grinder.game.model.interfaces.syntax.impl.StaffCommandSyntax;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemSetType;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.sound.Sounds;
import com.grinder.net.packet.PacketConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.net.packet.interaction.PacketInteractionManager;
import com.grinder.util.DiscordBot;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.debug.DebugManager;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.grinder.util.ItemID.*;

public class ItemActionPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

		final ItemActionMessage message = ItemActionMessageDecoder.Companion.decode(packetOpcode, packetReader);

		final int itemId = message.getItemId();
		final int interfaceId = message.getInterfaceId();
		final int slot = message.getSlot();

		if(itemId < 0)
			return;

		final ItemDefinition definition = ItemDefinition.forId(itemId);

		if(definition == null)
			return;

		if (player == null || player.getHitpoints() <= 0)
			return;
		if(player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
			player.stopTeleporting();
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false) )
			return;
		if (player.BLOCK_ALL_BUT_TALKING)
			return;
		if (player.isInTutorial())
			return;
		if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD)
			return;

		if (EntityExtKt.getBoolean(player, Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
			player.sendMessage("Please finish your random event before doing anything else.");
			return;
		}

		if (!MorphItems.INSTANCE.notTransformed(player, "do this", true, true))
			return;

		final Inventory inventory = player.getInventory();

		if (slot < 0 || slot > inventory.capacity())
			return;

		if(inventory.get(slot).getId() != itemId)
			return;

		if (inventory.get(slot).getAmount() <= 0) {
			return;
		}

		player.getPacketSender().sendInterfaceRemoval();

		SkillUtil.stopSkillable(player);

		if(ItemActions.INSTANCE.handleClick(player, message))
			return;

		switch (packetOpcode) {
			case PacketConstants.SECOND_ITEM_ACTION_OPCODE:
				secondAction(player, interfaceId, slot, itemId);
				break;
			case PacketConstants.FIRST_ITEM_ACTION_OPCODE:
				firstAction(player, interfaceId, slot, itemId);
				break;
			case PacketConstants.THIRD_ITEM_ACTION_OPCODE:
				thirdClickAction(player, interfaceId, slot, itemId);
				break;
		}
	}

	private static void firstAction(final Player player, int interfaceId, final int slot, final int itemId) {

		if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 4084) {
			player.getPacketSender().sendMessage("You can't use items while you're on a sled!", 1000);
			return;
		}

		if(player.getClueScrollManager().handleItemAction(1, itemId, slot))
			return;

		if(PouchType.Companion.itemIsPouch(itemId)) {
			player.pouches.get(PouchType.Companion.getPouchForItem(itemId)).addEssence(player);
			return;
		}

		// Prayer
		if (Prayer.buryBone(player, itemId)) {
			return;
		}

		// Eating food..
		if (ConsumableUtil.consumeEdible(player, itemId, slot)) {
			return;
		}

		if (ConsumableUtil.drinkBeverage(player, itemId, slot))
			return;

		// Drinking potions..
		if (ConsumableUtil.drinkPotion(player, itemId, slot)) {
			return;
		}


		// Teleport tablets..
		if (TeleportTablets.init(player, itemId)) {
			return;
		}

		if(player.getRights().equals(PlayerRights.DEVELOPER)) {
			player.getPacketSender().sendMessage("first action: "+ itemId, 1000);
			//System.out.println("private static final Item "+ItemDefinition.getName(itemId).replaceAll(" ", "_").toUpperCase()+" = new Item("+itemId+");");
		}

		DebugManager.debug(player, "item-option", "1: "+itemId+" slot: "+slot+" interfaceId: "+interfaceId);

		if(PacketInteractionManager.handleItemInteraction(player, new Item(itemId), 1)) {
			return;
		}

		switch (itemId) {
			case CELASTRUS_BARK:
				Optional<CreationMenu> menu = Optional.empty();

				CreationMenu.CreationMenuAction action = (index, item, amount) -> {
					SkillUtil.startSkillable(
							player, new ItemCreationSkillable(
									Arrays.asList(new RequiredItem[]{new RequiredItem(CELASTRUS_BARK, true), new RequiredItem(KNIFE, false)}),
									new Item(BATTLESTAFF, 1),
									amount,
									new AnimationLoop(new Animation(1248), 5),
									40,
									80,
									Skill.FLETCHING, null, 3));
					player.getPacketSender().sendInterfaceRemoval();
				};

				menu = Optional.of(new SingleItemCreationMenu(player,
						BATTLESTAFF,
						"How many would you like to make?", action));

				if (menu.isPresent()) {
					player.setCreationMenu(menu);
					menu.get().open();
				}
				break;
			case GHOSTS_SKULL:
				player.sendMessage("EEEK! I don't want to inspect this.");
				break;
			case CACHE_OF_RUNES:
				if (player.getInventory().countFreeSlots() < 13) {
					player.sendMessage("You need at least 13 free inventory slots to do open this.", 1000);
					return;
				}
				player.getInventory().delete(new Item(CACHE_OF_RUNES, 1));
				int[] runes = new int[]{554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566};
				for (int i = 0; i < runes.length; i++) {
					player.getInventory().add(new Item(runes[i], Misc.random(2000) + 50));
				}
				break;
			case 22711:
				player.getCollectionLog().showInterface();
				break;
			case ECUMENICAL_KEY_SHARD:
				if (player.getInventory().getAmount(ECUMENICAL_KEY_SHARD) > 50) {
					if (player.getInventory().countFreeSlots() < 1) {
						player.sendMessage("You need at least 1 free inventory slots to do that.", 1000);
						return;
					}
					player.getInventory().delete(ECUMENICAL_KEY_SHARD, 50);
					player.getInventory().add(ECUMENICAL_KEY, 1);
					player.sendMessage("You succesfully combine 50 ecumenical shards for an ecumenical key.");
				} else {
					player.sendMessage("You need to have at least 50 shards to be able to combine them.");
				}
				break;
			case BARRICADE:
				BarricadeManager.setupBarricade(player, slot);
				break;
			case NEWCOMER_MAP:
				player.getPacketSender().sendInterface(5392);
				break;
			case RUNE_POUCH:
				player.getRunePouch().open();
				break;
			case 19564: // Royale seed pot
				TeleportHandler.dragonStoneJewerlyTeleport(player, new Position(2465 + Misc.random(2), 3494), TeleportType.ROYAL_SEED_POT, true, true);
				break;
			case 21907:
				player.sendMessage("This blue dragon smells like it's been dead for a remarkably long time. Even by my standards, it smells awful.");
				break;
			case 22477:
				player.getPacketSender().sendMessage("You raise the hilt, inspecting each section carefully. It looks as though it could combine with a powerful parrying dagger.", 1500);
				break;
			case 21043:
				player.getPacketSender().sendMessage("You sense a dark magic emanating from the insignia. It looks like this could be attached to a wand.", 1500);
				break;
			case 21730:
				player.getPacketSender().sendMessage("Fallen from the centre of a Grotesque Guardian. This could be attached to a pair of Bandos boots...", 1500);
				break;
			case 12955: // Starter pack
				StarterPack.openStarterPack(player);
				break;
			case NULODIONS_NOTES:
				player.sendMessage("I think it's better if I give these notes to Captain Lawgof.");
				break;
			case ROTTEN_POTATO:
				player.sendMessage("I better not be eating a rotten potato...");
				break;
			case LootingBag.LOOTING_BAG:
				LootingBag.check(player);
				break;
			case BIRD_NEST_4:
				BirdsNest.openSeedNest(player);
				break;
			case BIRD_NEST_5:
				BirdsNest.openRingNest(player);
				break;
			case ItemID.MITHRIL_SEEDS:
				player.sendMessage("Flower gambling has been disabled. Please use the gambling option.");
				break;
			case 2542: // Scrolls
			case 2543:
			case 2544:
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that right now.", 1000);
					return;
				}
				if (itemId == 2542 && player.isPreserveUnlocked() || itemId == 2543 && player.isRigourUnlocked()
						|| itemId == 2544 && player.isAuguryUnlocked()) {
					player.getPacketSender().sendMessage("You have already unlocked that prayer.", 1000);
					return;
				}
				DialogueManager.start(player, 9);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						if (option == 1) {
							player.getInventory().delete(itemId, 1);

							if (itemId == 2542)
								player.setPreserveUnlocked(true);
							else if (itemId == 2543)
								player.setRigourUnlocked(true);
							else if (itemId == 2544)
								player.setAuguryUnlocked(true);
							player.getPacketSender().sendConfig(709,
									PrayerHandler.canUse(player, PrayerType.PRESERVE, false) ? 1 : 0);
							player.getPacketSender().sendConfig(711,
									PrayerHandler.canUse(player, PrayerType.RIGOUR, false) ? 1 : 0);
							player.getPacketSender().sendConfig(713,
									PrayerHandler.canUse(player, PrayerType.AUGURY, false) ? 1 : 0);
							player.getPacketSender().sendMessage("@dre@You have unlocked a new prayer</col>.");
							player.getPacketSender().sendSound(Sounds.USING_LAMP_REWARD);
						}
						if (player.isAuguryUnlocked() && player.isRigourUnlocked() && player.isPreserveUnlocked()) {
							AchievementManager.processFor(AchievementType.DEEP_FOCUS, player);
						}
						player.getPacketSender().sendInterfaceRemoval();
					}
				});
				break;
			case 2545:
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that right now.", 1000);
					return;
				}
				if (player.isTargetTeleportUnlocked()) {
					player.getPacketSender().sendMessage("You have already unlocked that teleport.", 1000);
					return;
				}
				DialogueManager.start(player, 12);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						if (option == 1) {
							player.getInventory().delete(itemId, 1);
							player.setTargetTeleportUnlocked(true);
							player.getPacketSender().sendMessage("You have unlocked a new teleport.");
							player.getPacketSender().sendSound(Sounds.USING_LAMP_REWARD);
						}
						player.getPacketSender().sendInterfaceRemoval();
					}
				});
				break;
			case 15208:
				ItemEquipmentSets.openVoidKnightSet(player, itemId);
				break;
			case 15209:
				ItemEquipmentSets.openEliteVoidKnightSet(player, itemId);
				break;
			case 15726:
				ItemEquipmentSets.openSuperiorVoidKnightSet(player, itemId);
				break;
			case 15213:
				ItemEquipmentSets.openInfinityRobesSet(player, itemId);
				break;
			case 15214:
				ItemEquipmentSets.openCorruptedArmourSet(player, itemId);
				break;
			case 15216:
				ItemEquipmentSets.openStatiusArmourSet(player, itemId);
				break;
			case 15217:
				ItemEquipmentSets.openVestaArmourSet(player, itemId);
				break;
			case 15218:
				ItemEquipmentSets.openMorriganSet(player, itemId);
				break;
			case 15219:
				ItemEquipmentSets.openZurielSet(player, itemId);
				break;
			case 15265:
				ItemEquipmentSets.openSuperBootsSet(player, itemId);
				break;
			case 15266:
				ItemEquipmentSets.openSpiritShieldSet(player, itemId);
				break;
			case 15263:
				ItemEquipmentSets.openJusticiarSet(player, itemId);
				break;
			case 15264:
				ItemEquipmentSets.openSuperRingsSet(player, itemId);
				break;
			case 15210:
				ItemEquipmentSets.openThirdAgeMeleeSet(player, itemId);
				break;
			case 15211:
				ItemEquipmentSets.openThirdAgeRangeSet(player, itemId);
				break;
			case 15212:
				ItemEquipmentSets.openThirdAgeMageSet(player, itemId);
				break;
			case 21049:
				if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
					return;
				}
				if ((player.getInventory().countFreeSlots()) < 3) {
					player.getPacketSender().sendMessage(
							"You need at least 3 free inventory slots to do that.", 1000);
					return;
				}
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
					return;
				}
				if (player.getCombat().isInCombat()) {
					player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat to do this!", 1000);
					return;
				}
				EntityExtKt.markTime(player, Attribute.LAST_PRAY);
				new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(itemId, 200)
						.setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
						.add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
						.firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
							if (!player.getInventory().contains(itemId)) {
								return;
							}
							player.getInventory().delete(21049, 1);
							player.getInventory().add(21018, 1);
							player.getInventory().add(21021, 1);
							player.getInventory().add(21024, 1);
							player.getPacketSender()
									.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
							player.getPacketSender().sendSound(72);
							player.getPacketSender().sendInterfaceRemoval();
							return;
						})
						.addCancel().start(player);

				break;
			case 12873:
			case 12875:
			case 12879:
			case 12881:
			case 12883:
			case 12877:
			case 13036:
			case 13038:
			case 13173:
			case 13175:
			case 21279:
				ItemSetType set = ItemSetType.get(itemId);
				if (set != null) {
					if (!player.getInventory().contains(set.getSetId())) {
						return;
					}
					if ((player.getInventory().countFreeSlots() - 1) < set.getItems().length) {
						player.getPacketSender().sendMessage(
								"You need at least " + set.getItems().length + " free inventory slots to do that.", 1000);
						return;
					}
				}
				new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(itemId, 130)
						.setText("Are you absolutely sure you want to open the", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>?")
						.add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
						.firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
							if (!player.getInventory().contains(itemId)) {
								return;
							}
								player.getInventory().delete(set.getSetId(), 1);
								for (int item : set.getItems()) {
									player.getInventory().add(item, 1);
								}
								player.getPacketSender()
										.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
								player.getPacketSender().sendSound(72);
								player.getPacketSender().sendInterfaceRemoval();
							return;
						})
						.addCancel().start(player);
				break;
		}
	}

	private static void secondAction(Player player, int interfaceId, int slot, final int itemId) {

		DegradableType degradable = DegradableType.forItem(itemId);
		if (degradable != null && degradable.getCanCheckIntegrity()) {
			degradable.checkIntegrity(player);
			return;
		} else if (degradable != null) {
			degradable.checkBarrows(player, itemId);
			return;
		}

		if(player.getClueScrollManager().handleItemAction(2, itemId, slot))
			return;

		if (ItemTransforming.handle(player, itemId, PacketConstants.SECOND_ITEM_ACTION_OPCODE))
			return;

		if (interfaceId == Inventory.INTERFACE_ID && ColorfulItem.getItemIds().containsKey(itemId)) {
			ItemColorCustomizer.openInterface(player, itemId, true);
			return;
		}

		if(player.getRights().equals(PlayerRights.DEVELOPER)) {
			player.getPacketSender().sendMessage("second action: "+ itemId, 1000);
		}

		DebugManager.debug(player, "item-option", "2: "+itemId+" slot: "+slot+" interfaceId: "+interfaceId);

		if(PacketInteractionManager.handleItemInteraction(player, new Item(itemId), 2)) {
			return;
		}

		if(PouchType.Companion.itemIsPouch(itemId)) {
			player.pouches.get(PouchType.Companion.getPouchForItem(itemId)).check(player);
			return;
		}


		// Teleport tablets..
		if (TeleportTablets.init(player, itemId)) {
			return;
		}

		if (ItemDefinition.forId(itemId).getName() != null && ItemDefinition.forId(itemId).getName().contains("potion(")
		|| ItemDefinition.forId(itemId).getName().toLowerCase().contains("vial of") || ItemDefinition.forId(itemId).getName().toLowerCase().contains(" brew")
		|| ItemDefinition.forId(itemId).getName().toLowerCase().contains("olive oil") || ItemDefinition.forId(itemId).getName().toLowerCase().contains("coconut milk")
		|| ItemDefinition.forId(itemId).getName().toLowerCase().contains(" mix(") || ItemDefinition.forId(itemId).getName().toLowerCase().contains("guthix balance(")
		|| ItemDefinition.forId(itemId).getName().toLowerCase().contains("serum 207(") || ItemDefinition.forId(itemId).getName().toLowerCase().contains("sanfew serum(")
		) {
			if(player.getInventory().contains(itemId)) {
				player.getInventory().delete(itemId, 1);
				player.getInventory().add(229, 1);
				player.sendMessage("You empty the vial.", 1000);
				return;
			}
		}
		switch (itemId) {
			case ROTTEN_POTATO:
				if (!player.getGameMode().isSpawn() && !PlayerUtil.isDeveloper(player)) {
					player.sendMessage("What are you trying to do?");
					PlayerUtil.broadcastPlayerStaffMessage("@dre@" + player.getUsername() +"</col> tried to use rotten potato item which doesn't belong to his rank!");
					if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[SPAWN MODE ITEM] " + player.getUsername() +"</col> tried to use rotten potato item which doesn't belong to his rank!");
					return;
				}
				player.setEnterSyntax(new StaffCommandSyntax("spawn"));
				player.getPacketSender().sendEnterInputPrompt("Enter the name of the item you wish to spawn:");
				break;
			case RING_OF_FORGING:
				new DialogueBuilder(DialogueType.STATEMENT)
						.setText("You still have " + EntityExtKt.getInt(player, Attribute.RING_OF_FORGING_CHARGES, 140) +" charges before it breaks. Continue?")
						.add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
						.firstOption("Yes.", player2 -> {
							if (!player.getInventory().contains(itemId)) {
								return;
							}
							player.getInventory().delete(RING_OF_FORGING, 1);
							player.getAttributes().numAttr(Attribute.RING_OF_FORGING_CHARGES, 140).setValue(140);
							player.getPacketSender().sendMessage("Your Ring of forging has degraded.");
							player.getPacketSender().sendInterfaceRemoval();
							return;
						})
						.addCancel("No.").start(player);
				break;
			case ARDOUGNE_CLOAK_2:
			case ARDOUGNE_CLOAK_3:
			case ARDOUGNE_CLOAK_4:
			case ARDOUGNE_MAX_CAPE:
				if (TeleportHandler.checkReqs(player, new Position(2663, 3374), true, false, player.getSpellbook().getTeleportType())) {
					TeleportHandler.teleport(player, new Position(2663 + Misc.random(1), 3374 + Misc.random(1), 0), TeleportType.PURO_PURO, false, true);
				}
				break;
			case JUG_OF_WATER:
				if(player.getInventory().contains(itemId)) {
					player.getInventory().delete(itemId, 1);
					player.getInventory().add(JUG, 1);
					player.sendMessage("You empty the jug.", 1000);
				}
				break;
			case ANCHOVY_OIL:
				if(player.getInventory().contains(itemId)) {
					player.getInventory().delete(itemId, 1);
					player.getInventory().add(EMPTY_VIAL, 1);
					player.sendMessage("You empty the vial.", 1000);
				}
				break;
			case GUTHIX_REST_4_:
			case GUTHIX_REST_3_:
			case GUTHIX_REST_2_:
			case GUTHIX_REST_1_:
			case RUINED_HERB_TEA:
				if(player.getInventory().contains(itemId)) {
					player.getInventory().delete(itemId, 1);
					player.getInventory().add(EMPTY_CUP, 1);
					player.sendMessage("You empty the cup.", 1000);
				}
				break;
			case BOWL_OF_WATER:
				if(player.getInventory().contains(itemId)) {
					player.getInventory().delete(itemId, 1);
					player.getInventory().add(BOWL, 1);
					player.sendMessage("You empty the bowl.", 1000);
				}
				break;
			case BUCKET_OF_WATER:
				if(player.getInventory().contains(itemId)) {
					player.getInventory().delete(itemId, 1);
					player.getInventory().add(BUCKET, 1);
					player.sendMessage("You empty the bucket.", 1000);
				}
				break;
			case CUP_OF_WATER:
			case CUP_OF_HOT_WATER:
				if(player.getInventory().contains(itemId)) {
					player.getInventory().delete(itemId, 1);
					player.getInventory().add(EMPTY_CUP, 1);
					player.sendMessage("You empty the cup.", 1000);
				}
				break;
			case PINEAPPLE:
				player.sendMessage("I should consider cutting this before trying to eat it.");
				break;
			case KBD_HEADS:
				player.sendMessage("You need to use a knife on the heads to separate them.");
				break;
			case 9810:
			case 9811:
				DialogueManager.start(player, 2872);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
							case 1:
								TeleportHandler.teleport(player, new Position(3056, 3310, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 2:
								TeleportHandler.teleport(player, new Position(2815, 3463, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 3:
								TeleportHandler.teleport(player, new Position(2857, 3431, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 4:
								TeleportHandler.teleport(player, new Position(3599, 3522, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 5:
								TeleportHandler.teleport(player, new Position(2663, 3375, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
						}
					}
				});
				break;
			case 10498: // Ava's
			case 10499:
			case 22109:
			case 27363:
			case 27374:
				player.setHasCommuneEffect(!player.hasCommuneEffect());
				player.sendMessage("You have " + (player.hasCommuneEffect() ? "enabled" : "disabled") +" your device commune effect.");
				break;
			case 13342:
			case 21898:
			case 13329:
			case 13331:
			case 13333:
			case 13335:
			case 13337:
			case 21285:
			case 21784:
			case 21776:
			case 21780:
				DialogueManager.start(player, 2708);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
							case 1:
								TeleportHandler.teleport(player, new Position(2626, 4015, 1), TeleportType.PURO_PURO, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 2:
								TeleportHandler.teleport(player, new Position(3186, 4637, 0), TeleportType.PURO_PURO, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 3:
								TeleportHandler.teleport(player, new Position(1912, 4367, 0), TeleportType.PURO_PURO, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 4:
								TeleportHandler.teleport(player, new Position(2578, 9506, 0), TeleportType.PURO_PURO, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
						}
					}
				});
				break;

			case 11941:
				player.getPacketSender().sendMessage("The settings can't be changed at this time.");
				break;

			case 11126: // Combat bracelet
				player.sendMessage("You will need to recharge your combat bracelet before you can use it again.");
				break;
			case 11113: // Skills necklace
				player.sendMessage("You will need to recharge your skills necklace before you can use it again.");
				break;
			case 2572: // Ring of wealth
			case 12785: // Ring of wealth (i)
				player.sendMessage("You will need to recharge your ring at the Fountain of Rune before you can use it again.");
				break;
			case 1704: // Glory empty
			case 10362:
				player.getPacketSender().sendMessage("It will need to be recharged before you can use it again.");
				break;
			case 9013:
				DialogueManager.sendStatement(player, "Your sceptre currently has @dre@" + EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) + "</col> charges left before it vanishes.");
				break;
			default:
				player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
				break;
		}
	}

	private void thirdClickAction(Player player, int interfaceId, int slot, final int itemId) {

		if(player.getClueScrollManager().handleItemAction(3, itemId, slot))
			return;

		if (ItemSetType.pack(player, itemId)) {
			return;
		}
		DegradableType degradable = DegradableType.forItem(itemId);
		if (degradable != null && degradable.getCanCheckIntegrity()) {
			degradable.checkIntegrity(player);
			return;
		} else if (degradable != null) {
			degradable.checkBarrows(player, itemId);
			return;
		}
		if(player.getRights().equals(PlayerRights.DEVELOPER)) {
			player.getPacketSender().sendMessage("third action: "+ itemId);
		}

		DebugManager.debug(player, "item-option", "3: "+itemId+" slot: "+slot+" interfaceId: "+interfaceId);

		if(PacketInteractionManager.handleItemInteraction(player, new Item(itemId), 3)) {
			return;
		}

		if(PouchType.Companion.itemIsPouch(itemId)) {
			player.pouches.get(PouchType.Companion.getPouchForItem(itemId)).withdraw(player);
			return;
		}
		if (ItemTransforming.handle(player, itemId, PacketConstants.THIRD_ITEM_ACTION_OPCODE)) {
			return;
		}

		switch (itemId) {
			case 22684: // EEK
				new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.EEK)
						.setText("Hey, you've only got four legs. How do you manage?", "Don't you fall over?")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Actually, I've only got two legs.")
						.add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.EEK).setExpression(DialogueExpression.ANGRY)
						.setText("Someone has stolen your legs! This is a DISASTER!", "We've got to catch the leg thief!")
						.add(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.ANGRY)
						.setText("No one stole my legs.")
						.add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.EEK).setExpression(DialogueExpression.ANGRY)
						.setText("You gave your legs away? That is so heroic...", "giving your legs away to someone without legs.")
						.add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.EEK).setExpression(DialogueExpression.ANGRY)
						.setText("You're my hero!")
						.start(player);
				break;
			case RING_OF_THE_ELEMENTS:
				new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Choose an option.")
						.firstOption("Air Altar.", player3 -> {
							if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.AIR_ALTAR.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
								TeleportHandler.teleport(player, Teleporting.TeleportLocation.AIR_ALTAR.getPosition(), player.getSpellbook().getTeleportType(), false, true);
							}
						}).secondOption("Water Altar.", player3 -> {
							if (TeleportHandler.checkReqs(player, new Position(2726, 4832, 0), true, false, player.getSpellbook().getTeleportType())) {
								TeleportHandler.teleport(player, new Position(2726, 4832, 0), player.getSpellbook().getTeleportType(), false, true);
							}
						}).thirdOption("Earth Altar.", player3 -> {
							if (TeleportHandler.checkReqs(player, new Position(2655, 4830), true, false, player.getSpellbook().getTeleportType())) {
								TeleportHandler.teleport(player, new Position(2655, 4830, 0), player.getSpellbook().getTeleportType(), false, true);
							}
						}).fourthOption("Fire Altar.", player3 -> {
							if (TeleportHandler.checkReqs(player, new Position(2574, 4849), true, false, player.getSpellbook().getTeleportType())) {
								TeleportHandler.teleport(player, new Position(2574, 4849, 0), player.getSpellbook().getTeleportType(), false, true);
							}
						}).addCancel().start(player);
				break;

			case CRAFTING_CAPE:
			case CRAFTING_CAPE_T_:
				if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.CRAFTING_GUILD.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
					TeleportHandler.teleport(player, Teleporting.TeleportLocation.CRAFTING_GUILD.getPosition(), TeleportType.PURO_PURO, false, true);
				}
				break;
			case ROTTEN_POTATO:
				if (!player.getGameMode().isSpawn() && !PlayerUtil.isDeveloper(player)) {
					player.sendMessage("What are you trying to do?");
					PlayerUtil.broadcastPlayerStaffMessage("@dre@" + player.getUsername() +"</col> tried to use rotten potato item which doesn't belong to his rank!");
					if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[SPAWN MODE ITEM] " + player.getUsername() +"</col> tried to use rotten potato item which doesn't belong to his rank!");
					return;
				}
				player.sendMessage("Please click on the skill stat that you wish to modify its level.");
				break;
			case ARDOUGNE_CLOAK_1:
			case ARDOUGNE_CLOAK_2:
			case ARDOUGNE_CLOAK_3:
			case ARDOUGNE_CLOAK_4:
			case ARDOUGNE_MAX_CAPE:
				if (TeleportHandler.checkReqs(player, new Position(3051, 3490), true, false, player.getSpellbook().getTeleportType())) {
					TeleportHandler.teleport(player, new Position(3051 + Misc.random(3), 3490 + Misc.random(3), 0), TeleportType.PURO_PURO, false, true);
				}
				break;
			case 22114:
			case 24855: // Mythical max cape
				if (TeleportHandler.checkReqs(player, new Position(2457, 2849, 0), true, true, TeleportType.NORMAL)) {
					TeleportHandler.teleport(player, new Position(2457, 2849, 0), TeleportType.PURO_PURO, true, true);
				}
				break;
			case 9810:
			case 9811:
				DialogueManager.start(player, 2872);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
							case 1:
								TeleportHandler.teleport(player, new Position(3056, 3310, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 2:
								TeleportHandler.teleport(player, new Position(2815, 3463, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 3:
								TeleportHandler.teleport(player, new Position(2857, 3431, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 4:
								TeleportHandler.teleport(player, new Position(3599, 3522, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 5:
								TeleportHandler.teleport(player, new Position(2663, 3375, 0), TeleportType.NORMAL, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
						}
					}
				});
				break;
			//case 12931:
			case 13197:
			case 13199:
				player.getPacketSender().sendMessage("Your @dre@" + ItemDefinition.forId(itemId).getName() + "</col> is fully charged.");
				break;
			case 15195:
				DialogueManager.start(player, 2708);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
							case 1:
								TeleportHandler.teleport(player, new Position(2626, 4015, 1), TeleportType.PURO_PURO, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 2:
								TeleportHandler.teleport(player, new Position(3186, 4637, 0), TeleportType.PURO_PURO, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 3:
								TeleportHandler.teleport(player, new Position(1912, 4367, 0), TeleportType.PURO_PURO, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 4:
								TeleportHandler.teleport(player, new Position(2578, 9506, 0), TeleportType.PURO_PURO, false, true);
								player.getPacketSender().sendInterfaceRemoval();
								break;
						}
					}
				});
				break;
			case 9762:
			case 9763:

				if (EntityExtKt.passedTime(player, Attribute.LAST_MAGIC_CAPE_RESET, 1, TimeUnit.DAYS, false, true)) {
					EntityExtKt.setInt(player, Attribute.MAGIC_CAPE_CHARGES, 5, 5);
				}

				if (EntityExtKt.getInt(player, Attribute.MAGIC_CAPE_CHARGES, 5) <= 0) {
					DialogueManager.sendStatement(player, "No more charges left on cloak for today");
					return;
				}

				if (player.BLOCK_ALL_BUT_TALKING) {
					return;
				}
				if (player.isInTutorial()) {
					return;
				}
				if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
					return;
				}
				if (player.getHitpoints() <= 0) {
					return;
				}
				DialogueManager.start(player, 8);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
							case 1: // Normal spellbook option
								player.getPacketSender().sendInterfaceRemoval();
/*								if (player.getCombat().isInCombat()) {
									player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat to do this.", 1000);
									return;
								}*/
								MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
								EntityExtKt.decInt(player, Attribute.MAGIC_CAPE_CHARGES, 1, 0, 5);
								// DialogueManager.sendStatement(player,
								// "You have switched to modern spell
								// book.");
								break;
							case 2: // Ancient spellbook option
								player.getPacketSender().sendInterfaceRemoval();

/*								if (player.getCombat().isInCombat()) {
									player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat to do this.", 1000);
									return;
								}*/
								MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
								EntityExtKt.decInt(player, Attribute.MAGIC_CAPE_CHARGES, 1, 0, 5);
								break;
							case 3: // Lunar spellbook option
								player.getPacketSender().sendInterfaceRemoval();
/*								if (player.getCombat().isInCombat()) {
									player.getPacketSender().sendMessage("You must wait 10 seconds after being out of combat to do this.", 1000);
									return;
								}*/
								MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
								EntityExtKt.decInt(player, Attribute.MAGIC_CAPE_CHARGES, 1, 0, 5);
								break;
							case 4: // Cancel option
								player.getPacketSender().sendInterfaceRemoval();
								break;
						}
					}
				});
				break;
			case 19941:
				player.getPacketSender().sendMessage("You try with all of your might to open it, but you can't.", 1000);
				break;
			case 12691:
				if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
					return;
				}
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
					return;
				}
				EntityExtKt.markTime(player, Attribute.LAST_PRAY);
				DialogueManager.start(player, 2667);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
							case 1:
								if (player.getInventory().contains(12691)) {
									player.getPacketSender().sendMessage("You have uncharged your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
									player.getInventory().delete(12691, 1);
									player.getInventory().add(12603, 1);
								}
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 2:
								player.getPacketSender().sendInterfaceRemoval();
								break;
						}
					}
				});
				break;
			case 12692:
				if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
					return;
				}
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
					return;
				}
				EntityExtKt.markTime(player, Attribute.LAST_PRAY);
				DialogueManager.start(player, 2667);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
							case 1:
								if (player.getInventory().contains(12692)) {
									player.getPacketSender().sendMessage("You have uncharged your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
									player.getInventory().delete(12692, 1);
									player.getInventory().add(12605, 1);
								}
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 2:
								player.getPacketSender().sendInterfaceRemoval();
								break;
						}
					}
				});
				break;
			case 19710:
				if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
					return;
				}
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
					return;
				}
				EntityExtKt.markTime(player, Attribute.LAST_PRAY);
				DialogueManager.start(player, 2667);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
							case 1:
								if (player.getInventory().contains(19710)) {
									player.getPacketSender().sendMessage("You have uncharged your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
									player.getInventory().delete(19710, 1);
									player.getInventory().add(19550, 1);
								}
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 2:
								player.getPacketSender().sendInterfaceRemoval();
								break;
						}
					}
				});
				break;
			case 13202:
				if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
					return;
				}
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
					return;
				}
				EntityExtKt.markTime(player, Attribute.LAST_PRAY);
				DialogueManager.start(player, 2667);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
							case 1:
								if (player.getInventory().contains(13202)) {
									player.getPacketSender().sendMessage("You have uncharged your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
									player.getInventory().delete(13202, 1);
									player.getInventory().add(12601, 1);
								}
								player.getPacketSender().sendInterfaceRemoval();
								break;
							case 2:
								player.getPacketSender().sendInterfaceRemoval();
								break;
						}
					}
				});
				break;

			case LAVA_BLADE:
				player.getPacketSender().sendMessage("Your Lava blade has @dre@" + Misc.format(EntityExtKt.getInt(player, Attribute.LAVA_BLADE_CHARGES, 125)) + "</col> charges left.");
				break;
			case 15918:
				player.getPacketSender().sendMessage("Your Infernal blade has @dre@" + Misc.format(EntityExtKt.getInt(player, Attribute.INFERNAL_BLADE_CHARGES, 250)) + "</col> charges left.");
				break;
			case ARMADYL_GODSWORD:
				if (player.getInventory().countFreeSlots() < 2) {
					player.getPacketSender().sendMessage("You must have 2 free inventory slots before dismantling the godsword.", 1000);
					return;
				}
				player.getInventory().delete(ARMADYL_GODSWORD, 1);
				player.getInventory().add(ARMADYL_HILT, 1);
				player.getInventory().add(GODSWORD_BLADE, 1);
				player.getPacketSender().sendMessage("You have dismantled your @dre@" + ItemDefinition.forId(ARMADYL_GODSWORD).getName() + "</col>!");
				break;
			case BANDOS_GODSWORD:
				if (player.getInventory().countFreeSlots() < 2) {
					player.getPacketSender().sendMessage("You must have 2 free inventory slots before dismantling the godsword.", 1000);
					return;
				}
				player.getInventory().delete(BANDOS_GODSWORD, 1);
				player.getInventory().add(BANDOS_HILT, 1);
				player.getInventory().add(GODSWORD_BLADE, 1);
				player.getPacketSender().sendMessage("You have dismantled your " + ItemDefinition.forId(BANDOS_GODSWORD).getName() + "!");
				break;
			case SARADOMIN_GODSWORD:
				if (player.getInventory().countFreeSlots() < 2) {
					player.getPacketSender().sendMessage("You must have 2 free inventory slots before dismantling the godsword.");
					return;
				}
				player.getInventory().delete(SARADOMIN_GODSWORD, 1);
				player.getInventory().add(SARADOMIN_HILT, 1);
				player.getInventory().add(GODSWORD_BLADE, 1);
				player.getPacketSender().sendMessage("You have dismantled your @dre@" + ItemDefinition.forId(SARADOMIN_GODSWORD).getName() + "</col>!");
				break;
			case ZAMORAK_GODSWORD:
				if (player.getInventory().countFreeSlots() < 2) {
					player.getPacketSender().sendMessage("You must have 2 free inventory slots before dismantling the godsword.", 1000);
					return;
				}
				player.getInventory().delete(ZAMORAK_GODSWORD, 1);
				player.getInventory().add(ZAMORAK_HILT, 1);
				player.getInventory().add(GODSWORD_BLADE, 1);
				player.getPacketSender().sendMessage("You have dismantled your @dre@" + ItemDefinition.forId(ZAMORAK_GODSWORD).getName() + "</col>!");
				break;
			case DRAGON_BOOTS_G:
				if (player.getInventory().countFreeSlots() < 2) {
					player.getPacketSender().sendMessage("You must have 2 free inventory slots before dismantling your boots.", 1000);
					return;
				}
				player.getInventory().delete(DRAGON_BOOTS_G, 1);
				player.getInventory().add(DRAGON_BOOTS, 1);
				player.getInventory().add(DRAGON_BOOTS_ORNAMENT_KIT, 1);
				player.getPacketSender().sendMessage("You have dismantled your @dre@" + ItemDefinition.forId(DRAGON_BOOTS_G).getName() + "</col>!");
				break;
			case 10833:
			case 12898:
				player.sendMessage("Manual dice gambling has been disabled. Please use the gambling option.");
				break;
			case RING_OF_WEALTH:
			case RING_OF_WEALTH_5_:
			case RING_OF_WEALTH_4_:
			case RING_OF_WEALTH_3_:
			case RING_OF_WEALTH_2_:
			case RING_OF_WEALTH_1_:
			case RING_OF_WEALTH_I_:
			case RING_OF_WEALTH_I5_:
			case RING_OF_WEALTH_I4_:
			case RING_OF_WEALTH_I3_:
			case RING_OF_WEALTH_I2_:
			case RING_OF_WEALTH_I1_:
				//player.getPacketSender().sendMessage("The shine of the ring increases your chance of finding a rare drop by 30%!", 1000);
				new DialogueBuilder(DialogueType.OPTION)
						.setText("Select an Option").firstOption("View boss log.", player2 -> {
					if (!player.getInventory().contains(itemId)) {
						return;
					}
					MonsterKillTracker.sendSlayerLog(player);
				}).secondOption("Toggle currency collection " + (player.isRingofWealthActivated() ? "off" : "on") +".", player2 -> {
					if (!player.getInventory().contains(itemId)) {
						return;
					}
					player.setRingofWealthActivated(player.isRingofWealthActivated() ? false : true);
					player.sendMessage("Your ring of wealth will " + (player.isRingofWealthActivated() ? "now" : "no longer") +" collect coins, blood money, and tokkul for you.");
					player.getPacketSender().sendInterfaceRemoval();
				}).start(player);
				break;
			case SKULL_SCEPTRE:
				DialogueManager.start(player, 2575);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						if (!player.getInventory().contains(9013)) {
							return;
						}
						if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1500, TimeUnit.MILLISECONDS, false, false)) {
							return;
						}
						if (player.busy()) {
							player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
							return;
						}
						EntityExtKt.markTime(player, Attribute.LAST_PRAY);
						switch (option) {
							case 1: // Kamil
								player.getPacketSender().sendInterfaceRemoval();
								EntityExtKt.setInt(player, Attribute.SCEPTRE_CHARGES, EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) - 1, 5);
								TeleportHandler.teleport(player, new Position(2629, 4013, 1), TeleportType.PURO_PURO, false, true);
								if (EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) <= 0) {
									player.getInventory().delete(9013, 1);
									player.getPacketSender().sendMessage("Your skull sceptre vanished as it's out of charges!");
									EntityExtKt.setInt(player, Attribute.SCEPTRE_CHARGES, 5, 5);
								}
								break;
							case 2: // Ice Queen
								player.getPacketSender().sendInterfaceRemoval();
								EntityExtKt.setInt(player, Attribute.SCEPTRE_CHARGES, EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) - 1, 5);
								TeleportHandler.teleport(player, new Position(2858, 9917), TeleportType.PURO_PURO, false, true);
								if (EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) <= 0) {
									player.getInventory().delete(9013, 1);
									player.getPacketSender().sendMessage("Your skull sceptre vanished as it's out of charges!");
									EntityExtKt.setInt(player, Attribute.SCEPTRE_CHARGES, 5, 5);
								}
								break;
							case 3: // Dagannoth kings
								player.getPacketSender().sendInterfaceRemoval();
								EntityExtKt.setInt(player, Attribute.SCEPTRE_CHARGES, EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) - 1, 5);
								TeleportHandler.teleport(player, new Position(1912 + Misc.getRandomInclusive(3), 4367), TeleportType.PURO_PURO, false, true);
								if (EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) <= 0) {
									player.getInventory().delete(9013, 1);
									player.getPacketSender().sendMessage("Your skull sceptre vanished as it's out of charges!");
									EntityExtKt.setInt(player, Attribute.SCEPTRE_CHARGES, 5, 5);
								}
								break;
							case 4: // Black Knight Titan
								player.getPacketSender().sendInterfaceRemoval();
								EntityExtKt.setInt(player, Attribute.SCEPTRE_CHARGES, EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) - 1, 5);
								TeleportHandler.teleport(player, new Position(2578 + Misc.getRandomInclusive(1), 9503 + Misc.getRandomInclusive(3)), TeleportType.PURO_PURO, false, true);
								if (EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) <= 0) {
									player.getInventory().delete(9013, 1);
									player.getPacketSender().sendMessage("Your skull sceptre vanished as it's out of charges!");
									EntityExtKt.setInt(player, Attribute.SCEPTRE_CHARGES, 5, 5);
								}
								break;
						}
					}
				});
				break;
			case LootingBag.LOOTING_BAG:
				LootingBag.deposit(player);
				break;
			default:
				player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
				break;
		}
	}


}
