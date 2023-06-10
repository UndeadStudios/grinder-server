package com.grinder.net.packet.impl;

import com.grinder.Config;
import com.grinder.game.content.minigame.warriorsguild.rooms.shotput.ShotPut;
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022;
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022State;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.combat.LineOfSight;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.message.decoder.PickupItemMessageDecoder;
import com.grinder.game.message.impl.PickupItemMessage;
import com.grinder.game.model.Animation;
import com.grinder.game.model.StaffLogRelay;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.impl.PublicMinigameLobby;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.BrokenItems;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.sound.Sounds;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabasePickupLogs;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.time.TimeUnits;
import com.grinder.util.tools.DupeDetector;

import java.util.Optional;

/**
 * This packet listener is used to pick up ground items that exist in the world.
 *
 * @author relex lawl
 */

public class PickupItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(final Player player, PacketReader packetReader, int packetOpcode) {

        final PickupItemMessageDecoder pickupItemMessageDecoder = new PickupItemMessageDecoder();
        final PickupItemMessage pickupItemMessage = pickupItemMessageDecoder.decode(packetReader.getPacket());

        handleMessage(player, pickupItemMessage);
    }

    public static void handleMessage(Player player, PickupItemMessage pickupItemMessage) {
        final int y = pickupItemMessage.getY();
        final int itemId = pickupItemMessage.getItemId();
        final int x = pickupItemMessage.getX();

        final Position position = new Position(x, y, player.getPosition().getZ());

        if (!Config.itempicking_enabled) {
            player.sendMessage("The @red@[PICKUP]</col> system has been switched @red@OFF</col> by the server administrator.");
            return;
        }
        if (player.getRights() == PlayerRights.DEVELOPER) {
            player.getPacketSender().sendMessage("Pick up item: " + Integer.toString(itemId) + ". " + position.toString());
        }
        if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME) {
            player.stopTeleporting();
        }
        if (player.busy()) {
            return;
        }
        if (player.isJailed()) {
            player.sendMessage("You can't pickup items when you're jailed!");
            return;
        }
        if (player.BLOCK_ALL_BUT_TALKING) {
            return;
        }
        if (player.isInTutorial()) {
            return;
        }
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            player.getPacketSender().sendMessage("You can't pickup items when you're AFK!", 1000);
            return;
        }
        if (!player.getLastItemPickup().elapsed(300))
            return;
        // last pickup
        if (itemId == ItemID.MYSTERY_BOX || itemId == ItemID.VOTING_TICKET) {
            if (player.getTimePlayed(TimeUnits.DAY) < 1) {
                player.getPacketSender().sendMessage("You must have at least a play time of 24 hours to pickup this item.", 1000);
                return;
            }
        }
        if(ShotPut.isBall(itemId)){
            ShotPut.sendPickupItemDialogue(player);
            return;
        }

        SkillUtil.stopSkillable(player);
        player.getCombat().resetTarget();
        player.setWalkToTask(new WalkToAction<>(player, position, 1, () -> {

            // Make sure distance isn't way off..
            if (Math.abs(player.getPosition().getX() - x) > 25 || Math.abs(player.getPosition().getY() - y) > 25) {
                player.getMotion().clearSteps();
                return;
            }

            final boolean isItemOnObject = !player.getPosition().sameAs(position);

            if (isItemOnObject && !player.getPosition().isWithinDistance(position, 1)) {
                player.sendMessage("I can't reach that.");
                player.setPositionToFace(position);
                return;
            }
            if (player.getGameMode().isSpawn() && itemId != ItemID.BLOOD_MONEY && itemId != ItemID.COINS
                    && itemId != 12746
                    && itemId != 12747
                    && itemId != 12748
                    && itemId != 12749
                    && itemId != 12750
                    && itemId != 12751
            ) {
                player.getPacketSender().sendMessage("You can't pick up items in spawn game mode.", 1000);
                return;
            }

            final Inventory inventory = player.getInventory();
            final ItemDefinition itemDefinition = ItemDefinition.forId(itemId);
            final int freeSlots = inventory.countFreeSlots();

            // Check if we can hold it..
            if (!(freeSlots > 0 || (freeSlots == 0 && itemDefinition.isStackable() && inventory.contains(itemId)))) {
                player.getInventory().full();
                return;
            }

            final Optional<ItemOnGround> optionalItemOnGround = ItemOnGroundManager.getItemOnGround(Optional.of(player.getUsername()), itemId, position);

            if (optionalItemOnGround.isPresent()) {

                final ItemOnGround groundItem = optionalItemOnGround.get();
                final Optional<String> optionalOwner = groundItem.findOwner();

                if (optionalOwner.isPresent()) {

                    final String owner = optionalOwner.get();

                    if (player.getGameMode().isAnyIronman() && !owner.equalsIgnoreCase(player.getUsername()) && player.getMinigame() == null) {
                        player.getPacketSender().sendMessage("You can't pick up this item as an Iron Man.", 1000);
                        return;
                    }
                }

                final Item item = groundItem.getItem();
                final int itemAmount = item.getAmount();
                long wouldBeAmount = (long) inventory.getAmount(item) + (long) itemAmount;

                if (itemAmount <= 0) {
                    return;
                }
                if (!item.isValid()) {
                    return;
                }
                if (item.getId() != itemId) {
                    return;
                }

                // Prevent players from picking items from inside the minigame lobby! VERY IMPORTANT DO NOT REMOVE!
                if (player.getArea() instanceof PublicMinigameLobby) {
                    if (!player.getGameMode().isSpawn())
                    Logging.log("publiclobbypickup", "" + player.getUsername() + " tried to pickup: " + Misc.insertCommasToNumber(itemAmount) + " x " + item.getDefinition().getName() + " inside the Minigame lobby!");
                    PlayerUtil.broadcastPlayerDeveloperMessage("" + player.getUsername() + " tried to drop: " + Misc.insertCommasToNumber(itemAmount) + " x " + item.getDefinition().getName() + " inside the Minigame lobby!");
                    player.setPositionToFace(groundItem.getPosition());
                    player.sendMessage("I can't reach that.");
                    return;
                }

                /*
                 * OSRS doesn't let you pick up if wouldBeAmount is over max INT
                 * @see wouldBeAmount
                 */
                if (wouldBeAmount > Integer.MAX_VALUE || wouldBeAmount <= 0) {
                    /*int space = Integer.MAX_VALUE - player.getInventory().getAmount(item);
                    groundItem.getItem().setAmount(space);
                    item.setAmount(space);*/
                    player.getPacketSender().sendMessage("You can't hold that amount of this item. Clear your inventory!", 1000);
                    return;
                }

                // Logging
                final long highAlchValue = item.getValue(ItemValueType.HIGH_ALCHEMY);
                final long priceEstValue = item.getValue(ItemValueType.PRICE_CHECKER);
                final long tokenValue = item.getValue(ItemValueType.OSRS_STORE);

                if ((highAlchValue * item.getAmount() >= 5000000) || (item.getAmount() * priceEstValue >= 50000000)
                        || (item.getAmount() * tokenValue > 5000000) || BrokenItems.breaksOnDeath(item.getId())
                        || (item.getId() >= 15200 && item.getId() <= 15350)
                        || item.getValue(ItemValueType.ITEM_PRICES) > 2_000_000 || ItemUtil.isHighValuedItem(item.getDefinition().getName())
                        || item.getValue(ItemValueType.PRICE_CHECKER) > 50_000_000 && item.getId() != 8851) {
                    if (player.getMinigame() == null)
                        logSuspiciousItemPickup(item, player);
                }

                if (isItemOnObject) {
                    if (!LineOfSight.withinSight(player.getPosition(), position, true)) {
                        player.sendMessage("I can't reach that.");
                        return;
                    }
                    player.setPositionToFace(groundItem.getPosition());
                    player.performAnimation(new Animation(832));
                }

                player.getLastItemPickup().reset();

                ItemOnGroundManager.deregister(groundItem);

                inventory.add(item, true, false);

                DupeDetector.INSTANCE.check(player);

                player.getPoints().increase(AttributeManager.Points.ITEMS_PICKED_UP, 1); // Increase points

                if (itemId == ItemID.MARK_OF_GRACE) // Process for Agility skill tasks
                SkillTaskManager.perform(player, ItemID.MARK_OF_GRACE, itemAmount, SkillMasterType.AGILITY);

                player.getPacketSender().sendSound(Sounds.PICKUP_ITEM);

                if (itemId == ItemID.BOOK_ON_CHEMICALS)
                    Christmas2022.INSTANCE.setState(player, Christmas2022State.MAKE_CURE);
            }
        }, WalkToAction.Policy.EXECUTE_ON_PARTIAL));
    }

    private static final String INSERT_SQL = "INSERT INTO pickup_logs (playerName, itemName, itemAmount, areaName, Date) VALUES (?, ?, ?, ?, ?);";

    private static void logSuspiciousItemPickup(Item item, Player player) {
        final ItemDefinition itemDefinition = item.getDefinition();
        StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.PICKUP, player.getUsername(), item);
        if (!player.getGameMode().isSpawn())
        Logging.log("pickup", "" + player.getUsername() + " picked: " + Misc.insertCommasToNumber(item.getAmount()) + " x " + itemDefinition.getName() + "");

        // Database Logging
        new DatabasePickupLogs(
                SQLManager.Companion.getINSTANCE(),
                player.getUsername(),
                itemDefinition.getName(),
                item.getAmount(),
                (player.getArea() != null ? player.getArea().toString() : " null")
        ).schedule(player);

//        SQLManager.Companion.getINSTANCE().execute(()-> {
//            try (Connection connection = SQLManager.Companion.getINSTANCE().getConnection(SQLDataSource.STAFF_PANEL);
//                 PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
//                stmt.setString(1, player.getUsername());
//                stmt.setString(2, itemDefinition.getName());
//                stmt.setInt(3, item.getAmount());
//                stmt.setString(4, (player.getArea() != null ? player.getArea().toString() : " null"));
//                stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
//                stmt.execute();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
    }
}
