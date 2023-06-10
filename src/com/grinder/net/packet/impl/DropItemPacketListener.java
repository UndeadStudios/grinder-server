package com.grinder.net.packet.impl;

import com.grinder.Config;
import com.grinder.game.content.item.charging.Chargeables;
import com.grinder.game.content.item.transforming.ItemTransforming;
import com.grinder.game.content.minigame.MinigameManager;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Chinchompas;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.message.decoder.DropItemMessageDecoder;
import com.grinder.game.message.impl.DropItemMessage;
import com.grinder.game.message.impl.ItemActionMessage;
import com.grinder.game.model.ItemActions;
import com.grinder.game.model.StaffLogRelay;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.impl.PublicMinigameLobby;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.sound.Sounds;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.staffpanel.DatabaseDropLogs;
import com.grinder.net.packet.PacketConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.time.TimeUnits;
import com.grinder.util.tools.DupeDetector;

/**
 * This packet listener is called when a player drops an item they have placed
 * in their inventory.
 *
 * @author relex lawl
 */
public class DropItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final DropItemMessageDecoder decoder = new DropItemMessageDecoder();
        final DropItemMessage message = decoder.decode(packetReader.getPacket());

        handleDropItemMessage(player, message);
    }

    private static final String INSERT_SQL = "INSERT INTO drop_logs (playerName, itemName, itemAmount, areaName, Date) VALUES (?, ?, ?, ?, ?);"; // the question marks will get filled with parameter indexs 1-5

    public static void handleDropLogging(Player player, Item item, String itemName, int itemAmount, long itemValue) {

        player.sendDevelopersMessage("Dropping item: " + item.getId() + ".");

        if (itemValue * itemAmount > 2_000_000) {
            Logging.log("itemdrops", "" + player.getUsername() + " dropped: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + "");

            new DatabaseDropLogs(
                    SQLManager.Companion.getINSTANCE(),
                    player.getUsername(),
                    itemName,
                    itemAmount,
                    (player.getArea() != null ? player.getArea().toString() : " None")
            ).schedule(player);
        }

        if (itemValue > 100_000 || itemAmount > 500 || ItemUtil.isHighValuedItem(itemName))
            StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.DROP, player.getUsername(), item);
    }

    public static void handleDropItemMessage(Player player, DropItemMessage message) {

        final int itemId = message.getItemId();
        final int interfaceId = message.getInterfaceId();
        final int itemSlot = message.getItemSlot();

        if (player == null || player.getHitpoints() <= 0) return;
        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false) ) return;
        if (player.BLOCK_ALL_BUT_TALKING) return;
        if (!Config.itemdropping_enabled) {
            player.sendMessage("The @red@[DROPPING]</col> system has been switched @red@OFF</col> by the server administrator.");
            return;
        }
        if (player.isTeleporting()) return;
        if (player.isInTutorial()) return;
        if (player.getHitpoints() <= 0) return;
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            player.getPacketSender().sendMessage("You can't drop items when you're AFK!", 1000);
            return;
        }
        if (player.isJailed()) {
            player.getPacketSender().sendMessage("You can't drop items when jailed!", 1000);
            return;
        }
/*        if (player.getUsername().equals("Mod Hellmage")) {
            player.sendMessage("Your account is not allowed to drop items.");
            return;
        }*/

/*        if (player.getTimePlayed(TimeUnits.HOUR) < 1) {
            player.sendMessage("You must have at least a play time of 1 hour to be able to drop items.");
            return;
        }*/

        if (interfaceId != Inventory.INTERFACE_ID)
            return;

        final Inventory inventory = player.getInventory();

        if (itemSlot < 0 || itemSlot >= inventory.capacity())
            return;
        if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
            player.stopTeleporting();
        if (player.busy())
            player.getPacketSender().sendInterfaceRemoval();

        final Item item = inventory.get(itemSlot);

        if (item == null)
            return;

        if (item.getId() != itemId || item.getAmount() <= 0)
            return;

        if (!player.getInventory().contains(item))
            return;

        if (ItemActions.INSTANCE.handleClick(player, new ItemActionMessage(itemId, itemSlot, interfaceId, PacketConstants.DROP_ITEM_OPCODE)))
            return;

        final ItemDefinition itemDefinition = item.getDefinition();
        boolean isDroppable = itemDefinition.isDropable();

        if (item.hasAttributes()) isDroppable = false;

        final String itemName = itemDefinition.getName();
        final int itemAmount = item.getAmount();
        final long itemValue = item.getValue(ItemValueType.PRICE_CHECKER);

        /*if (UnloseableItem.isKeptOnDeath(item.getId())) {
            player.getPacketSender().sendMessage("You can't destroy this item.", 1000);
            return;
        }*/ // Requested to be removed by players.

        player.getPacketSender().sendInterfaceRemoval();
        SkillUtil.stopSkillable(player);

        DupeDetector.INSTANCE.check(player);

        // Check if we're dropping a pet..
        if (PetHandler.drop(player, itemId, false))
            return;

        if (player.getClueScrollManager().handleItemDrop(interfaceId, itemId, itemSlot))
            return;

        if (ItemTransforming.handle(player, itemId, PacketConstants.DROP_ITEM_OPCODE))
            return;

        if (itemId == 4045) {
            player.getCombat().queue(new Damage(Misc.random(2,3), DamageMask.REGULAR_HIT));
            player.getInventory().delete(new Item(4045, 1), itemSlot);
            player.getPacketSender().sendSound(player.getAppearance().isMale() ? (518 + Misc.random(4)) : 509);
            return;
        }

        if (item.getId() == ItemID.ROTTEN_POTATO) {
            Presetables.INSTANCE.open(player, Presetables.GLOBAL_PRESETS[0]);
            player.sendMessage("Note: You can also use ::preset to open the PvP presets system quickly.");
            //player.sendMessage("You cannot drop this item.");
            return;
        }

        if (itemId == ItemID.MYSTERY_BOX || itemId == ItemID.VOTING_TICKET) {
            if (player.getTimePlayed(TimeUnits.DAY) < 1) {
                player.getPacketSender().sendMessage("You must have at least a play time of 24 hours to drop this item.", 1000);
                return;
            }
        }

        if (player.getRights() == PlayerRights.DEVELOPER)
            player.sendMessage("Dropping item: " + item.getId() + ".");

        if (player.getMinigame() != null) {
            player.getPacketSender().sendSound(Sounds.DROP_ITEM);
            inventory.set(itemSlot, ItemUtil.createInvalidItem());
            inventory.refreshItems();
            Logging.log("minigamedrops", "" + player.getUsername() + " dropped: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + "");
            player.getPacketSender().sendMessage("The item vanishes as soon as it touches the ground.");
            return;
        }
        if (player.getGameMode().isSpawn() || player.getMinigame() != null || MinigameManager.BATTLE_ROYALE.contains(player) || MinigameManager.WEAPON_GAME.contains(player)) {
            player.getPacketSender().sendSound(Sounds.DROP_ITEM);
            inventory.set(itemSlot, ItemUtil.createInvalidItem());
            inventory.refreshItems();
            player.getPacketSender().sendMessage("The item vanishes as soon as it touches the ground.");
            return;
        }

        /**
         * Quick verification so players dont lose their coin
         */
        if (itemId == ItemID.COINS && !ItemOnGroundManager.canDropCoin(player.getPosition(), player.getInventory().get(itemSlot).getAmount())) {
            player.sendMessage("@red@You cannot drop this many coins here!");
            return;
        }

        // Handle Chinchompas dropping first
        if (ItemUtil.isValidItem(item) && Chinchompas.INSTANCE.isAnyChin(item)) {

            // Handle single chin without a warning
            if (item.getAmount() == 1) {
                inventory.set(itemSlot, ItemUtil.createInvalidItem());
                player.getPacketSender().sendSound(Sounds.CHIN_DROP);
                inventory.refreshItems();
                player.sendMessage("You release the chinchompa and it bounds away.");
                return;
            }
            Chinchompas.INSTANCE.handleChinDropping(player, itemSlot, item);
            Logging.log("chindrops", "" + player.getUsername() + " dropped: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + "");
            return;
        }

        if (isDroppable && itemValue >= 50_000_000
                && player.isShowDropWarning()
                && !item.hasAttributes() && !item.getDefinition().isNoted()) {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(itemDefinition.getId(), 200)
                    .setText("The item you are trying to drop is considered @dre@valueable</col>.", "Are you absolutely sure you want to drop it?")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("" + itemName + ": Really drop it?")
                    .firstOption("Drop it.", player2 -> {
                        if (!player.getInventory().contains(item)) {
                            return;
                        }

                        // Prevent players from dropping items from inside the minigame lobby! VERY IMPORTANT DO NOT REMOVE!
                        if (player.getArea() instanceof PublicMinigameLobby) {
                            Logging.log("publiclobbydrop", "" + player.getUsername() + " tried to drop: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + " inside minigame lobby!");
                            PlayerUtil.broadcastPlayerDeveloperMessage("" + player.getUsername() + " tried to drop: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + " inside minigame lobby!");
                            player.sendMessage("You cannot drop items over here.");
                            return;
                        }

                        // Iron man drops should not be visible to other players that's how we do it on GS, that's why this code is here uniquely handled for wilderness drops.
                        if (AreaManager.inWilderness(player) && !player.getGameMode().isAnyIronman()) {
                            if (item.getDefinition().isTradeable()) { // Untradeable items must not show even if dropped in the wilderness
                                ItemOnGroundManager.registerGlobal(player, item); // Everyone can see it instantly if the item is tradeable
                        } else {
                                ItemOnGroundManager.registerNonGlobal(player, item); // If untradeable no one will see it even if in the wilderness
                            }
                        } else { // Finally, if not in the Wilderness drop normally as in any place. EXRTA SAFETY HERE TO USE ELSE just in case.
                            if (item.getDefinition().isTradeable()) {
                                ItemOnGroundManager.registerLongDelay(player, item);
                        } else {
                                ItemOnGroundManager.registerNonGlobal(player, item);
                            }
                        }

                        player.getPacketSender().sendSound(Sounds.DROP_ITEM);

                        player.getPoints().increase(AttributeManager.Points.ITEMS_DROPPED, 1); // Increase points

                        inventory.setItem(player.getInventory().getSlot(item), ItemUtil.createInvalidItem());
                        player.getPacketSender().sendInterfaceRemoval();
                        inventory.refreshItems();

                        // Logging
                        if (player.getRights() == PlayerRights.DEVELOPER)
                            player.sendMessage("Dropping item: " + item.getId() + ".");

                        if (ItemUtil.logItemIfValuable(item)) {
                            Logging.log("itemdrops", "" + player.getUsername() + " dropped: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + "");
                            new DatabaseDropLogs(
                                    SQLManager.Companion.getINSTANCE(),
                                    player.getUsername(),
                                    itemName,
                                    itemAmount,
                                    (player.getArea() != null ? player.getArea().toString() : " null")
                            ).schedule(player);
                        }


                        if (item.getValue(ItemValueType.PRICE_CHECKER) * itemAmount >= 50_000_000 || itemAmount > 100_000_000 || ItemUtil.isHighValuedItem(itemName)) {
                            StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.DROP, player.getUsername(), "dropped: @red@" + itemName + "@bla@ x: @red@" + Misc.insertCommasToNumber(itemAmount) + "@bla@.");
                        }
                    })
                    .addCancel("No don't drop it.")
                    .thirdOption("Drop it. Disable Warnings for Current Session.", player2 -> {
                        if (!player.getInventory().contains(item)) {
                            return;
                        }
                        player.setShowDropWarning(false);
                        if (AreaManager.inWilderness(player))
                            ItemOnGroundManager.registerGlobal(player, item);
                        else
                            ItemOnGroundManager.register(player, item);

                        player.getPacketSender().sendSound(Sounds.DROP_ITEM);
                        player.getPoints().increase(AttributeManager.Points.ITEMS_DROPPED, 1); // Increase points
                        inventory.set(itemSlot, ItemUtil.createInvalidItem());
                        player.getPacketSender().sendInterfaceRemoval();
                        inventory.refreshItems();

                        // Logging
                        handleDropLogging(player, item, itemName, itemAmount, itemValue);


                        return;
                    }).start(player);
            return;
        } else if (isDroppable) {
            if (item.getId() == ItemID.FISHBOWL_3 || item.getId() == ItemID.FISHBOWL_4 || item.getId() == ItemID.FISHBOWL_5) {
                new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("If you drop your fishbowl it will break!")
                        .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("Drop it regardless.", player2 -> {
                            if (!player.getInventory().contains(item)) {
                                player.getPacketSender().sendInterfaceRemoval();
                                return;
                            }

                            // Prevent players from dropping items from inside the minigame lobby! VERY IMPORTANT DO NOT REMOVE!
                            if (player.getArea() instanceof PublicMinigameLobby) {
                                Logging.log("publiclobbydrop", "" + player.getUsername() + " tried to drop: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + " inside minigame lobby!");
                                PlayerUtil.broadcastPlayerDeveloperMessage("" + player.getUsername() + " tried to drop: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + " inside minigame lobby!");
                                player.sendMessage("You cannot drop items over here.");
                                return;
                            }
                            player.getPacketSender().sendSound(Sounds.DROP_ITEM);
                            player.getPoints().increase(AttributeManager.Points.ITEMS_DROPPED, 1); // Increase points
                            inventory.set(itemSlot, ItemUtil.createInvalidItem());
                            player.getPacketSender().sendInterfaceRemoval();
                            inventory.refreshItems();


                            return;
                        })
                        .secondOption("Keep hold.", player2 -> {
                            new DialogueBuilder(DialogueType.STATEMENT)
                                    .setText("You keep a hold of it for now.").start(player);
                            return;
                        }).start(player);
            } else {
                if (!player.getInventory().contains(item)) {
                    return;
                }

                // Prevent players from dropping items from inside the minigame lobby! VERY IMPORTANT DO NOT REMOVE!
                if (player.getArea() instanceof PublicMinigameLobby) {
                    Logging.log("publiclobbydrop", "" + player.getUsername() + " tried to drop: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + " inside minigame lobby!");
                    PlayerUtil.broadcastPlayerDeveloperMessage("" + player.getUsername() + " tried to drop: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + " inside minigame lobby!");
                    player.sendMessage("You cannot drop items over here.");
                    return;
                }
                // Iron man drops should not be visible to other players that's how we do it on GS, that's why this code is here uniquely handled for wilderness drops.
                if (AreaManager.inWilderness(player) && !player.getGameMode().isAnyIronman()) {
                    if (item.getDefinition().isTradeable()) { // Untradeable items must not show even if dropped in the wilderness
                        ItemOnGroundManager.registerGlobal(player, item); // Everyone can see it instantly if the item is tradeable
                    } else {
                        ItemOnGroundManager.registerNonGlobal(player, item); // If untradeable no one will see it even if in the wilderness
                    }
                } else { // Finally, if not in the Wilderness drop normally as in any place. EXRTA SAFETY HERE TO USE ELSE just in case.
                    if (item.getDefinition().isTradeable()) {
                        ItemOnGroundManager.register(player, item);
                    } else {
                        ItemOnGroundManager.registerNonGlobal(player, item);
                    }
                }

                player.getPacketSender().sendSound(Sounds.DROP_ITEM);
                player.getPoints().increase(AttributeManager.Points.ITEMS_DROPPED, 1); // Increase points
                inventory.set(itemSlot, ItemUtil.createInvalidItem());
                inventory.refreshItems();

                // Logging
                handleDropLogging(player, item, itemName, itemAmount, itemValue);
            }


        } else {

            if (item.getId() == ItemID.SARADOMINS_BLESSED_SWORD || item.getId() == ItemID.SARAS_BLESSED_SWORD_FULL_) {
                new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("Reverting Saradomin's blessed sword will destroy the sword", "leaving you with a Saradomin's tear only.")
                        .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("Revert it.", player2 -> {
                            if (!player.getInventory().contains(item)) {
                                return;
                            }
                            inventory.set(itemSlot, ItemUtil.createInvalidItem());
                            inventory.add(new Item(12804, 1));
                            player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                            player.getPacketSender().sendInterfaceRemoval();
                            inventory.refreshItems();
                        })
                        .addCancel("Don't revert.").start(player);
                return;
            }

            if (Chargeables.INSTANCE.handleDrop(player, item, itemSlot))
                return;

            ItemUtil.destroyItemInterface(player, item);
        }
    }
}
