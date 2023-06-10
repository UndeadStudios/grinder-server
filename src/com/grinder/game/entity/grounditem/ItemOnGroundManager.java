package com.grinder.game.entity.grounditem;

import com.grinder.game.World;
import com.grinder.game.content.item.degrading.DegradableType;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGround.State;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ItemOnGroundRespawnTask;
import com.grinder.game.task.impl.ItemOnGroundSequenceTask;
import com.grinder.util.ItemID;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Manages all {@link ItemOnGround}s.
 *
 * @author Professor Oak
 */
public class ItemOnGroundManager {

    /**
     * The delay between every {@link ItemOnGround} state update.
     */
    public static final int STATE_UPDATE_DELAY = 100; // One minute

    /**
     * The delay between every {@link ItemOnGround} state update. (on death)
     */
    public static final int STATE_UPDATE_DELAY_ON_DEATH = 1_000; // 10 Minutes

    private static int nextSlot = 1;

    public static int getNextSlot() {
        if (++nextSlot == Integer.MAX_VALUE)
            nextSlot = 1;
        return nextSlot;
    }

    /**
     * Removes all items on the ground within the provided boundaries.
     */
    public static void removeAllInArea(List<Boundary> bounds) {
        for (Boundary bound : bounds) {
            int startX = bound.getX() >> 3, startY = bound.getY() >> 3, endX = bound.getX2() >> 3, endY = bound.getY2() >> 3;

            for (int i = 0, l = endX - startX; i <= l; i++) {
                for (int k = 0, kL = endY - startY; k <= kL; k++) {
                    int chunkX = startX + i;
                    int chunkY = startY + k;

                    Region region = World.getRegions().get(new RegionCoordinates(chunkX, chunkY));

                    if (region.isEntitiesEmpty())
                        continue;

                    for (int z = 0; z < 4; z++) {

                        final Iterator<ItemOnGround> iterator = region.getGroundItems(z).iterator();

                        while (iterator.hasNext()) {
                            final ItemOnGround itemOnGround = iterator.next();

                            // Remove the item if it lies within the boundary
                            if (bound.contains(itemOnGround.getPosition())) { // may not be necessary to use boundary check, since we are using 8x8 tile chunks
                                itemOnGround.setPendingRemoval(true);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * Processes all active {@link ItemOnGround}.
     */
    public static void process() {

        final Iterator<ItemOnGround> iterator = World.getGroundItems().iterator();

        while (iterator.hasNext()) {

            final ItemOnGround itemOnGround = iterator.next();

            itemOnGround.process();

            if (itemOnGround.isPendingRemoval()) {

                if (itemOnGround.respawns())
                    TaskManager.submit(new ItemOnGroundRespawnTask(itemOnGround, itemOnGround.getRespawnTimer()));

                if (!itemOnGround.isRemovedFromRegion())
                    World.deSpawn(itemOnGround);

                iterator.remove();
            }
        }
    }

    /**
     * Registers the given {@link ItemOnGround} to the world.
     */
    public static void register(ItemOnGround itemOnGround) {
        final Item item = itemOnGround.getItem();
        final ItemDefinition definition = item.getDefinition();

        if (definition.isStackable() && merge(itemOnGround))
            return;
        World.spawn(itemOnGround);
    }

    /**
     * Will verify the GroundItems that are already at this destination arent taken
     *
     * @param amountOfCoin - The amount requested for dropping
     * @return TRUE if we should be able to drop coins; false if it would be over max value.
     */
    public static boolean canDropCoin(Position pos, int amountOfCoin) {
        final Region region = World.getRegions().get(pos.getRegionCoordinates());
        final Set<ItemOnGround> list = region.getGroundItems(pos.getZ() & 3);
        for (ItemOnGround groundItem : list) {
            if (groundItem.getPosition().equals(pos)) {
                if (groundItem.getItem().getId() == ItemID.COINS) {

                    int totalAmount = amountOfCoin + groundItem.getItem().getAmount();

                    if (totalAmount < 0 ||totalAmount > Integer.MAX_VALUE) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Attempts to merge an item with one that already exists in the same position.
     * <p>
     * This is mostly used for stackable ground items.
     */
    public static boolean merge(ItemOnGround item) {

        Region region = World.getRegions().fromPosition(item.getPosition());

        Set<ItemOnGround> items = region.getGroundItems(item.getPosition().getZ());

        for (ItemOnGround otherItemOnGround : items) {

            if (otherItemOnGround == null || otherItemOnGround.isPendingRemoval() || otherItemOnGround.equals(item))
                continue;

            if (!otherItemOnGround.getPosition().equals(item.getPosition()))
                continue;

            final Optional<String> optionalOwnerName = item.findOwner();
            final Optional<Player> optionalOwner = optionalOwnerName.flatMap(World::findPlayerByName);

            if (optionalOwner.isPresent()) {
                final Player player = optionalOwner.get();
                if ((player.getGameMode().isAnyIronman() || player.getGameMode().isSpawn()) && !otherItemOnGround.findOwner().get().equalsIgnoreCase(optionalOwnerName.get())) {
                    continue;
                }
            }

            // Check if the ground item is private...
            // If we aren't the owner, we shouldn't modify it.
            if (otherItemOnGround.getState() == State.SEEN_BY_PLAYER) {
                boolean flag = true;
                if (otherItemOnGround.findOwner().isPresent() && item.findOwner().isPresent()) {
                    if (otherItemOnGround.findOwner().get().equals(item.findOwner().get())) {
                        flag = false;
                    }
                }
                if (flag)
                    continue;
            }

            // Modify the existing item.
            if (otherItemOnGround.getItem().getId() == item.getItem().getId()) {
                final int oldAmount = otherItemOnGround.getItem().getAmount();
                otherItemOnGround.getItem().incrementAmountBy(item.getItem().getAmount());
                otherItemOnGround.setOldAmount(oldAmount);
                otherItemOnGround.setTick(0);
                region.updateEntity(otherItemOnGround);
                return true;
            }
        }
        return false;
    }

    /**
     * Deregisters the given {@link ItemOnGround} from the world by flagging it as
     * deleted. The iterator in {@link ItemOnGroundSequenceTask} will pick this up and
     * remove it.
     */
    public static void deregister(ItemOnGround item) {
        item.setPendingRemoval(true);
        World.deSpawn(item);
    }

    /**
     * A utility method which quickly registers a default {@link ItemOnGround} which
     * goes global once the item's counter hits {@code STATE_UPDATE_DELAY}, unless the GameMode is IronMan.
     */
    public static ItemOnGround register(Player player, Item item) {
        return register(player, item, player.getPosition().clone());
    }

    /**
     * A utility method which quickly registers a default {@link ItemOnGround} which
     * goes global once the item's counter hits {@code STATE_UPDATE_DELAY}, unless the GameMode is IronMan.
     */
    public static ItemOnGround registerLongDelay(Player player, Item item) {
        return registerLongDelay(player, item, player.getPosition().clone());
    }

    public static int changeItem(int id) {
        return DegradableType.transformIdOnDrop(id);
    }

    /**
     * A utility method which quickly registers a default {@link ItemOnGround} which
     * goes global once the item's counter hits {@code STATE_UPDATE_DELAY}, unless the GameMode is IronMan.
     */
    public static ItemOnGround register(Player player, Item item, Position position) {
        item.setId(changeItem(item.getId()));
        final boolean goesGlobal = !player.getGameMode().isAnyIronman() && !player.getGameMode().isSpawn();
        final int ticksTillStateUpdate = player.isDying() ? STATE_UPDATE_DELAY_ON_DEATH : STATE_UPDATE_DELAY;
        final ItemOnGround itemOnGround = new ItemOnGround(State.SEEN_BY_PLAYER, Optional.of(player.getUsername()), position, item, goesGlobal, -1, ticksTillStateUpdate);
        register(itemOnGround);
        return itemOnGround;
    }

    /**
     * A utility method which quickly registers a default {@link ItemOnGround} which
     * goes global once the item's counter hits {@code STATE_UPDATE_DELAY}, unless the GameMode is IronMan.
     */
    public static ItemOnGround registerLongDelay(Player player, Item item, Position position) {
        item.setId(changeItem(item.getId()));
        final boolean goesGlobal = !player.getGameMode().isAnyIronman() && !player.getGameMode().isSpawn();
        final int ticksTillStateUpdate = player.isDying() ? STATE_UPDATE_DELAY_ON_DEATH : STATE_UPDATE_DELAY;
        final ItemOnGround itemOnGround = new ItemOnGround(State.SEEN_BY_PLAYER, Optional.of(player.getUsername()), position, item, goesGlobal, -1, ticksTillStateUpdate);
        register(itemOnGround);
        return itemOnGround;
    }

    /**
     * A utility method which quickly registers a default {@link ItemOnGround} which
     * does not go global
     */
    public static void registerNonGlobal(Player player, Item item) {
        item.setId(changeItem(item.getId()));
        registerNonGlobal(player, item, player.getPosition().clone());
    }

    /**
     * A utility method which quickly registers a default {@link ItemOnGround} which
     * does not go global
     */
    public static void registerNonGlobal(Player player, Item item, Position position) {
        item.setId(changeItem(item.getId()));
        register(new ItemOnGround(State.SEEN_BY_PLAYER, Optional.of(player.getUsername()), position, item, false, -1, ItemOnGroundManager.STATE_UPDATE_DELAY));
    }

    public static void registerNonGlobalOnDeath(Player player, Item item, Position position) {
        item.setId(changeItem(item.getId()));
        register(new ItemOnGround(State.SEEN_BY_PLAYER, Optional.of(player.getUsername()), position, item, false, -1, ItemOnGroundManager.STATE_UPDATE_DELAY_ON_DEATH));
    }

    /**
     * A utility method which quickly registers a default {@link ItemOnGround} which
     * is global.
     */
    public static void registerGlobal(Player player, Item item) {
        registerGlobal(player, item, player.getPosition().clone());
    }

    public static void registerGlobal(Player player, Item item, Position position) {
        item.setId(changeItem(item.getId()));
        register(new ItemOnGround(State.SEEN_BY_EVERYONE, Optional.of(player.getUsername()), position.clone(), item, !player.getGameMode().isAnyIronman() && !player.getGameMode().isSpawn(), -1, ItemOnGroundManager.STATE_UPDATE_DELAY * 10));
    }

    public static void registerGlobalDefaultDelay(Player player, Item item, Position position) {
        item.setId(changeItem(item.getId()));
        register(new ItemOnGround(State.SEEN_BY_EVERYONE, Optional.of(player.getUsername()), position.clone(), item, !player.getGameMode().isAnyIronman() && !player.getGameMode().isSpawn(), -1, ItemOnGroundManager.STATE_UPDATE_DELAY));
    }

    /**
     * A utility method used to find a {@link ItemOnGround} with the specified
     * {@link Position}.
     */
    public static Optional<ItemOnGround> getItemOnGround(Optional<String> owner, int id, Position position) {

        final Region region = World.getRegions().get(position.getRegionCoordinates());
        final Set<ItemOnGround> list = region.getGroundItems(position.getZ());

        for (ItemOnGround item : list) {

            if (item == null || item.isPendingRemoval())
                continue;

            if (item.getState() == State.SEEN_BY_PLAYER)
                if (owner.isEmpty() || !isOwner(owner.get(), item))
                    continue;

            if (id != item.getItem().getId())
                continue;

            if (!item.getPosition().equals(position))
                continue;

            return Optional.of(item);
        }
        return Optional.empty();
    }

    public static Optional<ItemOnGround> findVisibleItemOnGround(Player player, int id, Position position) {

        final Region region = World.getRegions().get(position.getRegionCoordinates());
        final Set<ItemOnGround> list = region.getGroundItems(position.getZ());

        for (ItemOnGround item : list) {

            if (item == null || item.isPendingRemoval())
                continue;

            if (!item.isPublic() && !item.isOwnedBy(player))
                continue;

            if (id != item.getItem().getId())
                continue;

            if (!item.getPosition().equals(position))
                continue;

            return Optional.of(item);
        }
        return Optional.empty();
    }

    /**
     * Checks if this gound item exists.
     */
    public static boolean exists(ItemOnGround i) {
        return getItemOnGround(i.findOwner(), i.getItem().getId(), i.getPosition()).isPresent();
    }

    /**
     * A utitily method used to check if the given {@link Player} is the owner of
     * the given {@link ItemOnGround}.
     */
    public static boolean isOwner(String playerName, ItemOnGround i) {
        return i.findOwner()
                .filter(owner -> owner.equalsIgnoreCase(playerName))
                .isPresent();
    }

    /**
     * Represents the different types of packet-operations related to ground items
     * that are currently supported.
     *
     * @author Professor Oak
     */
    protected enum OperationType {
        CREATE, DELETE, ALTER;
    }
}