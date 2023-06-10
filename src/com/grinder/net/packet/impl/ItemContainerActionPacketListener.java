package com.grinder.net.packet.impl;

import com.grinder.game.content.dueling.DuelConstants;
import com.grinder.game.content.gambling.GambleConstants;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.item.coloring.ItemColorCustomizer;
import com.grinder.game.content.item.coloring.ItemColorCustomizer.ColorfulItem;
import com.grinder.game.content.item.degrading.DegradableType;
import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.minigame.castlewars.FlagManager;
import com.grinder.game.content.minigame.warriorsguild.rooms.Jimmy;
import com.grinder.game.content.minigame.warriorsguild.rooms.catapult.Catapult;
import com.grinder.game.content.pvm.BossDropTables;
import com.grinder.game.content.pvm.NpcInformation;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.Smithing.EquipmentMaking;
import com.grinder.game.content.skill.skillable.impl.crafting.JewelryMaking;
import com.grinder.game.content.skill.skillable.impl.magic.SpellCasting;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.content.skill.skillable.impl.runecrafting.RunecraftingEvents;
import com.grinder.game.content.trading.TradeConstants;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.message.decoder.ItemContainerActionMessageDecoder;
import com.grinder.game.message.impl.ItemContainerActionMessage;
import com.grinder.game.model.ItemActions;
import com.grinder.game.model.MagicSpellbook;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.impl.FindCommand;
import com.grinder.game.model.commands.impl.SpawnGameModeFindCommand;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.game.model.interfaces.syntax.impl.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.game.model.item.container.player.*;
import com.grinder.game.model.item.container.shop.Shop;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.model.ui.UserInterfaceManager;
import com.grinder.net.packet.PacketConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.Misc;
import com.grinder.util.debug.DebugManager;

import static com.grinder.util.ItemID.*;

public class ItemContainerActionPacketListener implements PacketListener {
    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final ItemContainerActionMessage message = ItemContainerActionMessageDecoder.Companion.decode(packetOpcode, packetReader);

        final int itemId = message.getItemId();
        final int interfaceId = message.getInterfaceId();
        final int slot = message.getSlot();

        if (itemId < 0)
            return;

        final ItemDefinition definition = ItemDefinition.forId(itemId);

        if (definition == null)
            return;

        if (player == null || player.getHitpoints() <= 0)
            return;
        if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
            player.stopTeleporting();
        if (player.isTeleporting())
            return;
        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false))
            return;

        if (!MorphItems.INSTANCE.notTransformed(player, "do this", true, true))
            return;

        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD)
            return;
        if (player.BLOCK_ALL_BUT_TALKING)
            return;

        /**
         * Wasn't sure how to bind just InterfaceID -> Actions
         */
        if (interfaceId == 56_304 && player.getGameMode().isSpawn()) {
            SpawnGameModeFindCommand.handleInterfaceClick(player, message);
            return;
        }
        if (interfaceId == 56_304) {
            FindCommand.handleInterfaceClick(player, message);
            return;
        }

        if (ItemActions.INSTANCE.handleClick(player, message))
            return;

        if (definition.getEquipmentType().getSlot() == EquipmentConstants.CAPE_SLOT || definition.getEquipmentType().getSlot() == EquipmentConstants.HEAD_SLOT) {
            if (CastleWars.isInCastleWars(player) || CastleWars.isInCastleWarsLobby(player)) {
                player.sendMessage("You can't remove your team's colours.");
                return;
            }
        }

        if (definition.getEquipmentType().getSlot() == EquipmentConstants.WEAPON_SLOT) {
            if (CastleWars.isInCastleWars(player) && FlagManager.isHoldingFlag(player)) {
                FlagManager.dropFlag(player);
                return;
            }
        }

        switch (packetOpcode) {
            case PacketConstants.FIRST_ITEM_CONTAINER_ACTION_OPCODE:
                firstAction(player, interfaceId, slot, itemId);
                break;
            case PacketConstants.SECOND_ITEM_CONTAINER_ACTION_OPCODE:
                secondAction(player, interfaceId, slot, itemId);
                break;
            case PacketConstants.THIRD_ITEM_CONTAINER_ACTION_OPCODE:
                thirdAction(player, interfaceId, slot, itemId);
                break;
            case PacketConstants.FOURTH_ITEM_CONTAINER_ACTION_OPCODE:
                fourthAction(player, interfaceId, slot, itemId);
                break;
            case PacketConstants.FIFTH_ITEM_CONTAINER_ACTION_OPCODE:
                fifthAction(player, interfaceId, slot, itemId);
                break;
            case PacketConstants.SIXTH_ITEM_CONTAINER_ACTION_OPCODE:
                sixthAction(player, interfaceId, slot, itemId);
                break;
            case PacketConstants.SEVENTH_ITEM_CONTAINER_ACTION_OPCODE:
                seventhAction(player, interfaceId, slot, itemId);
                break;
            case PacketConstants.EIGTH_ITEM_CONTAINER_ACTION_OPCODE:
                eighthAction(player, interfaceId, slot, itemId);
                break;
        }
    }

    private static void firstAction(Player player, int interfaceId, int slot, int itemId) {

        if (Jimmy.isKeg(itemId)) {
            player.sendMessage("You can't do that right now!");
            return;
        }

        if (Catapult.isShieldEquipped(player)) {
            player.sendMessage("You can't do that right now!");
            return;
        }

        // Bank withdrawal..
        if (interfaceId >= BankConstants.CONTAINER_START && interfaceId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {
            Banking.withdraw(player, itemId, slot, 1, interfaceId - BankConstants.CONTAINER_START);
            return;
        }

        if (interfaceId >= 43023 && interfaceId <= 43080 || interfaceId == BossDropTables.ITEM_CONTAINER_ID) {
            NpcInformation.checkDropRate(player, interfaceId, new Item(itemId));
            return;
        }

        if (UserInterfaceManager.handleContainer(player, interfaceId, itemId, slot, 1)) {
            return;
        }
        DebugManager.debug(player, "item-container", "1: "+interfaceId+", "+itemId+", "+slot);
        switch (interfaceId) {

            case SafeDeposit.ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.getSafeDeposit().withdrawToInventory(itemId, slot, 1);
                }
                break;

            case GambleConstants.FIRST_ITEM_CONTAINER_ID:
                if (player.getGambling().inGambleWindow()) {
                    player.getGambling().withdraw(itemId, slot, 1);
                }
                break;

            case RunePouch.RUNE_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.getRunePouch().withdraw(itemId, slot, 1);
                }
                break;

            case RunePouch.INVENTORY_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.getRunePouch().deposit(itemId, slot, 1);
                }
                break;

            case 58018:
            case 58019:
            case 58020:
            case 58021:
                JewelryMaking.start(player, itemId, 1);
                break;

            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_1:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_2:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_3:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_4:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_5:
                if (player.getInterfaceId() == EquipmentMaking.EQUIPMENT_CREATION_INTERFACE_ID) {
                    EquipmentMaking.initialize(player, itemId, interfaceId, slot, 1);
                }
                break;
            // Withdrawing items from duel
            case DuelConstants.MAIN_INTERFACE_CONTAINER:
                if (player.getStatus() == PlayerStatus.DUELING) {
                    player.getDueling().switchItem(itemId, 1, slot, player.getDueling().getContainer(), player.getInventory());
                }
                break;

            case TradeConstants.INVENTORY_CONTAINER_INTERFACE: // Duel/Trade inventory
                if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
                    player.getPriceChecker().deposit(itemId, 1, slot);
                } else if (player.getStatus() == PlayerStatus.TRADING) {
                    player.getTrading().moveItem(itemId, 1, slot, player.getInventory(), player.getTrading().getContainer());
                } else if (player.getStatus() == PlayerStatus.DUELING) {
                    player.getDueling().switchItem(itemId, 1, slot, player.getInventory(), player.getDueling().getContainer());
                } else if (player.getGambling().inGambleWindow()) {
                    player.getGambling().deposit(itemId, slot, 1);
                }
                break;
            case TradeConstants.CONTAINER_INTERFACE_ID:
                if (player.getStatus() == PlayerStatus.TRADING) {
                    player.getTrading().moveItem(itemId, 1, slot, player.getTrading().getContainer(), player.getInventory());
                }
                break;
            case PriceChecker.CONTAINER_ID:
                player.getPriceChecker().withdraw(itemId, 1, slot);
                break;

            case BankConstants.INVENTORY_INTERFACE_ID:
            case BankConstants.DEPOSIT_BOX_ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.getSafeDeposit().depositFromInventory(itemId, slot, 1);
                } else {
                    Banking.depositFromInventory(player, itemId, slot, 1, false);
                }
                break;

            case Shop.ITEM_CHILD_ID:
            case Shop.NEW_ITEM_CHILD_ID:
            case Shop.INVENTORY_INTERFACE_ID:
            case 60121:
                if (player.getStatus() == PlayerStatus.SHOPPING) {
                    if (interfaceId == 60121) {
                        ShopManager.priceCheck(player, itemId, slot, true);
                    } else {
                        ShopManager.priceCheck(player, itemId, slot, (interfaceId == Shop.ITEM_CHILD_ID || interfaceId == Shop.NEW_ITEM_CHILD_ID));
                    }
                }
                break;

            case EquipmentConstants.INVENTORY_INTERFACE_ID: // Unequip

                final Item item = player.getEquipment().atSlot(slot);

                if (item == null || item.getId() != itemId)
                    return;

                if (item.getAmount() == 0) {
                    PlayerUtil.broadcastPlayerMediumStaffMessage("Player " + player.getUsername() + " tried to unequip " + ItemDefinition.forId(item.getId()).getName() + " with zero count possibly to create a dupe.");
                    return;
                }
                if (player.busy()) {
                    player.sendMessage("You can't do that when you are busy.");
                    return;
                }

                if (player.getMinigame() != null) {
                    if (!player.getMinigame().canUnEquip()) {
                        player.getPacketSender().sendMessage("You're not allowed to unequip items in this minigame.", 1000);
                        return;
                    }
                }
//                player.getPacketSender().sendInterfaceRemoval();
                player.setDialogue(null);
                player.setDialogueOptions(null);
                player.setDialogueContinueAction(null);
                player.setEnterSyntax(null);

                SkillUtil.stopSkillable(player);


                switch (item.getId()) {
                    case 20005:
                    case 20017:
                        //player.resetTransformation();
                        player.setNpcTransformationId(-1);
                        break;
                }


                final Inventory inventory = player.getInventory();
                final int amountInInventory = inventory.getAmount(item);

                final ItemDefinition definition = item.getDefinition();
                final boolean stackItem = definition.isStackable() && amountInInventory > 0;
                final int emptySlotInInventory = inventory.getEmptySlot();

                if (emptySlotInInventory == -1) {
                    inventory.full();
                    return;
                }

                final Equipment equipment = player.getEquipment();
                equipment.reset(slot);

                if (stackItem)
                    inventory.add(item);
                else
                    inventory.setItem(emptySlotInInventory, item);

                /*if (player.carriedWeight > 0) {
                    if (definition.getWeight() != 0)
                        player.carriedWeight -= definition.getWeight();
                }*/


                EquipmentBonuses.update(player);

                if (definition.getEquipmentType().getSlot() == EquipmentConstants.WEAPON_SLOT) {
                    EquipPacketListener.resetWeapon(player);
                    player.setSpecialActivatedAndSendState(false);
                    SpecialAttackType.updateBar(player);
                    if (player.getCombat().getAutocastSpell() != null)
                        SpellCasting.setSpellToCastAutomatically(player, null);
                }

                equipment.refreshItems();
                inventory.refreshItems();

                player.updateAppearance();

                final Sound sound = Sounds.getEquipmentSounds(item, slot);
                if (sound != null)
                    player.getPacketSender().sendSound(sound);

                break;
        }
    }

    private static void secondAction(Player player, int interfaceId, int slot, int itemId) {

        RunecraftingEvents.INSTANCE.clickTiara(player, itemId);

        // Bank withdrawal..
        if (interfaceId >= BankConstants.CONTAINER_START && interfaceId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {
            Banking.withdraw(player, itemId, slot, 5, interfaceId - BankConstants.CONTAINER_START);
            return;
        }

        if (player.isInTutorial()) {
            return;
        }

        if (interfaceId == EquipmentConstants.INVENTORY_INTERFACE_ID) {
            DegradableType degradable = DegradableType.forItem(itemId);
            if (degradable != null && degradable.getCanCheckIntegrity()) {
                degradable.checkIntegrity(player);
                return;
            } else if (degradable != null) {
                degradable.checkBarrows(player, itemId);
                return;
            }
        }

        if (interfaceId == EquipmentConstants.INVENTORY_INTERFACE_ID && ColorfulItem.getItemIds().containsKey(itemId)) {
            ItemColorCustomizer.openInterface(player, itemId, false);
            return;
        }
        if (UserInterfaceManager.handleContainer(player, interfaceId, itemId, slot, 2)) {
            return;
        }

        DebugManager.debug(player, "item-container", "2: "+interfaceId+", "+itemId+", "+slot);
        switch (interfaceId) {

            case SafeDeposit.ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.getSafeDeposit().withdrawToInventory(itemId, slot, 5);
                }
                break;

            case GambleConstants.FIRST_ITEM_CONTAINER_ID:
                if (player.getGambling().inGambleWindow()) {
                    player.getGambling().withdraw(itemId, slot, 5);
                }
                break;
            case RunePouch.RUNE_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.getRunePouch().withdraw(itemId, slot, 5);
                }
                break;

            case RunePouch.INVENTORY_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.getRunePouch().deposit(itemId, slot, 5);
                }
                break;

            case 58018:
            case 58019:
            case 58020:
            case 58021:
                JewelryMaking.start(player, itemId, 5);
                break;

            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_1:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_2:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_3:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_4:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_5:
                if (player.getInterfaceId() == EquipmentMaking.EQUIPMENT_CREATION_INTERFACE_ID) {
                    EquipmentMaking.initialize(player, itemId, interfaceId, slot, 5);
                }
                break;
            case Shop.INVENTORY_INTERFACE_ID:
                if (player.getStatus() == PlayerStatus.SHOPPING) {
                    ShopManager.sellItem(player, slot, itemId, 1);
                }
                break;
            case Shop.ITEM_CHILD_ID:
            case Shop.NEW_ITEM_CHILD_ID:
            case 60121:
                if (player.getStatus() == PlayerStatus.SHOPPING) {
                    ShopManager.buyItem(player, slot, itemId, 1);
                }
                break;
            case BankConstants.INVENTORY_INTERFACE_ID:
            case BankConstants.DEPOSIT_BOX_ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.getSafeDeposit().depositFromInventory(itemId, slot, 5);
                } else {
                    Banking.depositFromInventory(player, itemId, slot, 5, false);
                }
                break;
            case DuelConstants.MAIN_INTERFACE_CONTAINER:
                if (player.getStatus() == PlayerStatus.DUELING) {
                    player.getDueling().switchItem(itemId, 5, slot, player.getDueling().getContainer(), player.getInventory());
                }
                break;
            case TradeConstants.INVENTORY_CONTAINER_INTERFACE: // Duel/Trade inventory
                if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
                    player.getPriceChecker().deposit(itemId, 5, slot);
                } else if (player.getStatus() == PlayerStatus.TRADING) {
                    player.getTrading().moveItem(itemId, 5, slot, player.getInventory(), player.getTrading().getContainer());
                } else if (player.getStatus() == PlayerStatus.DUELING) {
                    player.getDueling().switchItem(itemId, 5, slot, player.getInventory(), player.getDueling().getContainer());
                } else if (player.getGambling().inGambleWindow()) {
                    player.getGambling().deposit(itemId, slot, 5);
                }
                break;
            case TradeConstants.CONTAINER_INTERFACE_ID:
                if (player.getStatus() == PlayerStatus.TRADING) {
                    player.getTrading().moveItem(itemId, 5, slot, player.getTrading().getContainer(), player.getInventory());
                }
                break;
            case PriceChecker.CONTAINER_ID:
                player.getPriceChecker().withdraw(itemId, 5, slot);
                break;
            case EquipmentConstants.INVENTORY_INTERFACE_ID:
                if (player.busy()) {
                    return;
                }
                if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
                    return;
                }
                if (!player.isRegistered()) {
                    return;
                }
                if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME) {
                    player.stopTeleporting();
                }
                if (player.isTeleporting()) {
                    return;
                }
                if (player.getHitpoints() <= 0) {
                    return;
                }
                SkillUtil.stopSkillable(player);
                switch (itemId) { // Operate packet
                    case RING_OF_FORGING:
                        player.sendMessage("Your @dre@Ring of forging</col> currenctly has @dre@" + EntityExtKt.getInt(player, Attribute.RING_OF_FORGING_CHARGES, 140) + "</col> charges left.");
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

                    case 10501: // Snow ball
                        player.sendMessage("I should throw it at someone!");
                        break;
                    case AVAS_ATTRACTOR: // Ava's
                    case AVAS_ACCUMULATOR:
                    case 22109:
                    case 27363:
                    case 27374:
                        player.setHasCommuneEffect(!player.hasCommuneEffect());
                        player.sendMessage("You have " + (player.hasCommuneEffect() ? "enabled" : "disabled") + " your device commune effect.");
                        break;
                    case MAGIC_CAPE:
                    case MAGIC_CAPE_T_:
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
                                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
                                        break;
                                    case 2: // Ancient spellbook option
                                        player.getPacketSender().sendInterfaceRemoval();
                                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
                                        break;
                                    case 3: // Lunar spellbook option
                                        player.getPacketSender().sendInterfaceRemoval();
                                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
                                        break;
                                    case 4: // Cancel option
                                        player.getPacketSender().sendInterfaceRemoval();
                                        break;
                                }
                            }
                        });
                        break;


                    //case 12931:
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
                    case 13197:
                    case 13199:
                        player.getPacketSender().sendMessage("Your @dre@" + ItemDefinition.forId(itemId).getName() + "</col> is fully charged.");
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
                    case 15152:
                        player.getPacketSender().sendMessage("Your @dre@Lava blade</col> has @dre@" + EntityExtKt.getInt(player, Attribute.LAVA_BLADE_CHARGES, 125) + "</col> charges left.");
                        break;
                    case 15918:
                        player.getPacketSender().sendMessage("Your @dre@Infernal blade</col> has @dre@" + EntityExtKt.getInt(player, Attribute.INFERNAL_BLADE_CHARGES, 250) + "</col> charges left.");
                        break;
                    case 1704: // Glory empty
                    case 10362:
                        player.getPacketSender().sendMessage("It will need to be recharged before you can use it again.");
                        break;
                    case 9013:
                        DialogueManager.sendStatement(player, "Your @dre@Skull sceptre</col> currenctly has @dre@" + EntityExtKt.getInt(player, Attribute.SCEPTRE_CHARGES, 5) + "</col> charges left before it vanishes.");
                        break;
                    default:
                        player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                }
                break;
        }

    }

    private static void thirdAction(Player player, int interfaceId, int slot, int id) {

        // Bank withdrawal..
        if (interfaceId >= BankConstants.CONTAINER_START && interfaceId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {
            Banking.withdraw(player, id, slot, 10, interfaceId - BankConstants.CONTAINER_START);
            return;
        }
        if (UserInterfaceManager.handleContainer(player, interfaceId, id, slot, 3)) {
            return;
        }
        DebugManager.debug(player, "item-container", "3: "+interfaceId+", "+id+", "+slot);
        switch (interfaceId) {
            case SafeDeposit.ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.getSafeDeposit().withdrawToInventory(id, slot, 10);
                }
                break;

            case GambleConstants.FIRST_ITEM_CONTAINER_ID:
                if (player.getGambling().inGambleWindow()) {
                    player.getGambling().withdraw(id, slot, 10);
                }
                break;

            case RunePouch.RUNE_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.getRunePouch().withdraw(id, slot, 10);
                }
                break;

            case RunePouch.INVENTORY_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.getRunePouch().deposit(id, slot, 10);
                }
                break;

            case 58018:
            case 58019:
            case 58020:
            case 58021:
                JewelryMaking.start(player, id, 10);
                break;

            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_1:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_2:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_3:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_4:
            case EquipmentMaking.EQUIPMENT_CREATION_COLUMN_5:
                if (player.getInterfaceId() == EquipmentMaking.EQUIPMENT_CREATION_INTERFACE_ID) {
                    EquipmentMaking.initialize(player, id, interfaceId, slot, 10);
                }
                break;
            case Shop.INVENTORY_INTERFACE_ID:
                if (player.getStatus() == PlayerStatus.SHOPPING) {
                    ShopManager.sellItem(player, slot, id, 5);
                }
                break;
            case Shop.ITEM_CHILD_ID:
            case 60121:
            case Shop.NEW_ITEM_CHILD_ID:
                if (player.getStatus() == PlayerStatus.SHOPPING) {
                    ShopManager.buyItem(player, slot, id, 5);
                }
                break;
            case BankConstants.INVENTORY_INTERFACE_ID:
            case BankConstants.DEPOSIT_BOX_ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.getSafeDeposit().depositFromInventory(id, slot, 10);
                } else {
                    Banking.depositFromInventory(player, id, slot, 10, false);
                }
                break;
            // Withdrawing items from duel
            case DuelConstants.MAIN_INTERFACE_CONTAINER:
                if (player.getStatus() == PlayerStatus.DUELING) {
                    player.getDueling().switchItem(id, 10, slot, player.getDueling().getContainer(), player.getInventory());
                }
                break;
            case TradeConstants.INVENTORY_CONTAINER_INTERFACE: // Duel/Trade inventory
                if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
                    player.getPriceChecker().deposit(id, 10, slot);
                } else if (player.getStatus() == PlayerStatus.TRADING) {
                    player.getTrading().moveItem(id, 10, slot, player.getInventory(), player.getTrading().getContainer());
                } else if (player.getStatus() == PlayerStatus.DUELING) {
                    player.getDueling().switchItem(id, 10, slot, player.getInventory(), player.getDueling().getContainer());
                } else if (player.getGambling().inGambleWindow()) {
                    player.getGambling().deposit(id, slot, 10);
                }
                break;
            case TradeConstants.CONTAINER_INTERFACE_ID:
                if (player.getStatus() == PlayerStatus.TRADING) {
                    player.getTrading().moveItem(id, 10, slot, player.getTrading().getContainer(), player.getInventory());
                }
                break;
            case PriceChecker.CONTAINER_ID:
                player.getPriceChecker().withdraw(id, 10, slot);
                break;
        }
    }

    private static void fourthAction(Player player, int interfaceId, final int slot, final int id) {

        // Bank withdrawal..
        if (interfaceId >= BankConstants.CONTAINER_START && interfaceId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {
            player.setEnterSyntax(new WithdrawBankX(id, slot, interfaceId - BankConstants.CONTAINER_START));
            player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
            return;
        }
        if (UserInterfaceManager.handleContainer(player, interfaceId, id, slot, 4)) {
            return;
        }
        DebugManager.debug(player, "item-container", "4: "+interfaceId+", "+id+", "+slot);
        switch (interfaceId) {
            case SafeDeposit.ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.getSafeDeposit().withdrawToInventory(id, slot, player.getSafeDeposit().getAmount(id));
                }
                break;

            case GambleConstants.FIRST_ITEM_CONTAINER_ID:
                if (player.getGambling().inGambleWindow()) {
                    player.getGambling().withdraw(id, slot, Integer.MAX_VALUE);
                }
                break;
            case RunePouch.RUNE_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.getRunePouch().withdraw(id, slot, player.getRunePouch().getAmount(id));
                }
                break;

            case RunePouch.INVENTORY_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.getRunePouch().deposit(id, slot, player.getInventory().getAmount(id));
                }
                break;
            case Shop.INVENTORY_INTERFACE_ID:
                if (player.getStatus() == PlayerStatus.SHOPPING) {
                    ShopManager.sellItem(player, slot, id, 10);
                }
                break;
            case Shop.ITEM_CHILD_ID:
            case 60121:
            case Shop.NEW_ITEM_CHILD_ID:
                if (player.getStatus() == PlayerStatus.SHOPPING) {
                    ShopManager.buyItem(player, slot, id, 10);
                }
                break;
            case BankConstants.INVENTORY_INTERFACE_ID:
            case BankConstants.DEPOSIT_BOX_ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.setEnterSyntax(new EnterSyntax() {

                        @Override
                        public void handleSyntax(Player player, int input) {
                            player.getSafeDeposit().depositFromInventory(id, slot, input);
                        }

                        @Override
                        public void handleSyntax(Player player, String input) {

                        }
                    });
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to deposit?");
                } else {
                    player.setEnterSyntax(new BankX(id, slot));
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to bank?");
                }
                break;
            // Withdrawing items from duel
            case DuelConstants.MAIN_INTERFACE_CONTAINER:
                if (player.getStatus() == PlayerStatus.DUELING) {
                    player.getDueling().switchItem(id, player.getDueling().getContainer().getAmount(id), slot,
                            player.getDueling().getContainer(), player.getInventory());
                }
                break;
            case TradeConstants.INVENTORY_CONTAINER_INTERFACE: // Duel/Trade inventory
                if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
                    player.getPriceChecker().deposit(id, player.getInventory().getAmount(id), slot);
                } else if (player.getStatus() == PlayerStatus.TRADING) {
                    player.getTrading().moveItem(id, player.getInventory().getAmount(id), slot, player.getInventory(),
                            player.getTrading().getContainer());
                } else if (player.getStatus() == PlayerStatus.DUELING) {
                    player.getDueling().switchItem(id, player.getInventory().getAmount(id), slot, player.getInventory(),
                            player.getDueling().getContainer());
                } else if (player.getGambling().inGambleWindow()) {
                    player.getGambling().deposit(id, slot, player.getInventory().getAmount(id));
                }
                break;
            case TradeConstants.CONTAINER_INTERFACE_ID:
                if (player.getStatus() == PlayerStatus.TRADING) {
                    player.getTrading().moveItem(id, player.getTrading().getContainer().getAmount(id), slot,
                            player.getTrading().getContainer(), player.getInventory());
                }
                break;
            case PriceChecker.CONTAINER_ID:
                player.getPriceChecker().withdraw(id, player.getPriceChecker().getAmount(id), slot);
                break;
        }
    }

    private static void fifthAction(Player player, int interfaceId, final int slot, final int id) {

        // Bank withdrawal..
        if (interfaceId >= BankConstants.CONTAINER_START && interfaceId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {
            Banking.withdraw(player, id, slot, -1, interfaceId - BankConstants.CONTAINER_START);
            return;
        }
        if (UserInterfaceManager.handleContainer(player, interfaceId, id, slot, 5)) {
            return;
        }
        DebugManager.debug(player, "item-container", "5: " + interfaceId + ", " + id + ", " + slot);
        switch (interfaceId) {

            case SafeDeposit.ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.setEnterSyntax(new EnterSyntax() {

                        @Override
                        public void handleSyntax(Player player, int input) {
                            player.getSafeDeposit().withdrawToInventory(id, slot, input);
                        }

                        @Override
                        public void handleSyntax(Player player, String input) {

                        }
                    });
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
                }
                break;

            case RunePouch.RUNE_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.setEnterSyntax(new EnterSyntax() {

                        @Override
                        public void handleSyntax(Player player, int input) {
                            player.getRunePouch().withdraw(id, slot, input);
                        }

                        @Override
                        public void handleSyntax(Player player, String input) {

                        }
                    });
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
                }
                break;

            case RunePouch.INVENTORY_CONTAINER_ID:
                if (player.getInterfaceId() == RunePouch.INTERFACE_ID) {
                    player.setEnterSyntax(new EnterSyntax() {

                        @Override
                        public void handleSyntax(Player player, int input) {
                            player.getRunePouch().deposit(id, slot, input);
                        }

                        @Override
                        public void handleSyntax(Player player, String input) {

                        }
                    });
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to deposit?");
                }
                break;

            case 58018:
            case 58019:
            case 58020:
            case 58021:
                player.setEnterSyntax(new EnterSyntax() {

                    @Override
                    public void handleSyntax(Player player, int input) {
                        JewelryMaking.start(player, id, input);
                    }

                    @Override
                    public void handleSyntax(Player player, String input) {

                    }
                });
                player.getPacketSender().sendEnterAmountPrompt("How many would you like to smelt?");
                break;
            case Shop.INVENTORY_INTERFACE_ID:
                if (player.getStatus() == PlayerStatus.SHOPPING) {
                    ShopManager.sellItem(player, slot, id, 50);
                }
                break;
            case Shop.ITEM_CHILD_ID:
            case Shop.NEW_ITEM_CHILD_ID:
                if (player.getStatus() == PlayerStatus.SHOPPING) {
                    ShopManager.buyItem(player, slot, id, 50);
                }
                break;

            case BankConstants.INVENTORY_INTERFACE_ID:
            case BankConstants.DEPOSIT_BOX_ITEM_CONTAINER_ID:
                if (player.getInterfaceId() == SafeDeposit.INTERFACE_ID) {
                    player.getSafeDeposit().depositFromInventory(id, slot, player.getInventory().getAmount(id));
                } else {
                    Banking.depositFromInventory(player, id, slot, -1, false);
                }
                break;
            case TradeConstants.INVENTORY_CONTAINER_INTERFACE: // Duel/Trade inventory
                if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
                    player.setEnterSyntax(new PriceCheckX(id, slot, true));
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to deposit?");
                } else if (player.getStatus() == PlayerStatus.TRADING) {
                    player.setEnterSyntax(new TradeX(id, slot, true));
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to offer?");
                } else if (player.getStatus() == PlayerStatus.DUELING) {
                    player.setEnterSyntax(new StakeX(id, slot, true));
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to offer?");
                } else if (player.getGambling().inGambleWindow()) {
                    player.setEnterSyntax(new DiceX(id, slot, true));
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to offer?");
                }
                break;
            case TradeConstants.CONTAINER_INTERFACE_ID:
                if (player.getStatus() == PlayerStatus.TRADING) {
                    player.setEnterSyntax(new TradeX(id, slot, false));
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
                }
                break;
            case DuelConstants.MAIN_INTERFACE_CONTAINER:
                if (player.getStatus() == PlayerStatus.DUELING) {
                    player.setEnterSyntax(new StakeX(id, slot, false));
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
                }
                break;
            case PriceChecker.CONTAINER_ID:
                player.setEnterSyntax(new PriceCheckX(id, slot, false));
                player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
                break;
            case GambleConstants.FIRST_ITEM_CONTAINER_ID:
                if (player.getGambling().inGambleWindow()) {
                    player.setEnterSyntax(new DiceX(id, slot, false));
                    player.getPacketSender().sendEnterAmountPrompt("How many would you like to offer?");
                }
                break;
        }
    }

    private static void sixthAction(Player player, int interfaceId, int slot, int id) {

        // Bank withdrawal..
        if (interfaceId >= BankConstants.CONTAINER_START && interfaceId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {
            // All but one
            int amount = player.getBank(interfaceId - BankConstants.CONTAINER_START).getAmount(id) - 1;
            if (amount > 0) {
                Banking.withdraw(player, id, slot, amount, interfaceId - BankConstants.CONTAINER_START);
            }
            return;
        }
        if (UserInterfaceManager.handleContainer(player, interfaceId, id, slot, 6)) {
            return;
        }

        DebugManager.debug(player, "item-container", "6: " + interfaceId + ", " + id + ", " + slot);

        switch (interfaceId) {
            case Shop.INVENTORY_INTERFACE_ID:
                player.setEnterSyntax(new SellX(id, slot));
                player.getPacketSender().sendEnterAmountPrompt("How many would you like to sell?");
                break;
            case Shop.ITEM_CHILD_ID:
            case Shop.NEW_ITEM_CHILD_ID:
                player.setEnterSyntax(new BuyX(id, slot));
                player.getPacketSender().sendEnterAmountPrompt("How many would you like to buy?");
                break;
        }
    }

    private static void seventhAction(Player player, int interfaceId, int slot, int id) {

        // Bank withdrawal..
        if (interfaceId >= BankConstants.CONTAINER_START && interfaceId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {
            // Withdraw custom
            Banking.withdraw(player, id, slot, player.getModifiableXValue(), interfaceId - BankConstants.CONTAINER_START);
            return;
        }

        switch (interfaceId) {
            case BankConstants.INVENTORY_INTERFACE_ID:
                Banking.depositFromInventory(player, id, slot, player.getModifiableXValue(), false);
                break;
        }
    }

    private static void eighthAction(Player player, int interfaceId, int slot, int id) {

        //Bank withdrawal..
        if (interfaceId >= BankConstants.CONTAINER_START && interfaceId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {
            boolean temp = player.hasPlaceHoldersEnabled();
            player.setPlaceholders(true);
            Banking.withdraw(player, id, slot, player.getBank(interfaceId - BankConstants.CONTAINER_START).getAmount(id), interfaceId - BankConstants.CONTAINER_START);
            player.setPlaceholders(temp);
            return;
        }
    }


}
