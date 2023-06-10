package com.grinder.game.model.item.container;

import com.grinder.game.World;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.item.BrokenItems;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.task.TaskManager;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabaseEmptyLogs;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class ItemContainerUtil {

    public static void replaceAllItems(Player player, int toFind, int toReplace){
        replaceAllItems(player, toFind, toReplace, true);
    }
    public static void replaceAllItems(Player player, int toFind, int toReplace, boolean update){
        player.getInventory().replaceAll(toFind, toReplace, update);
        player.getEquipment().replaceAll(toFind, toReplace, update);
        player.getLootingBag().getContainer().replaceAll(toFind, toReplace);
        BankUtil.replaceAll(player.getBanks(), toFind, toReplace);
    }

    public static int getAccountItemsCount(Player player, int itemId) {
        int totalItems = 0;
        totalItems += player.getInventory().getAmount(itemId);
        totalItems += World.getGroundItems().stream().filter(itemOnGround -> itemOnGround.getItem() != null
                && itemOnGround.getItem().getId() != itemId
                && !itemOnGround.findOwner().orElse("").equals(player.getUsername()))
                .map(ItemOnGround::getItem)
                .mapToInt(Item::getAmount).sum();
        for (Bank bank : player.getBanks()) {
            if (bank != null)
                totalItems += bank.getValidItems().stream()
                        .filter(item -> item.getId() == itemId)
                        .mapToInt(Item::getAmount).sum();
        }
        return totalItems;
    }


    public static void dropUnder(Player player, int id, int amount) {
        if (player.getGameMode().isSpawn()) {
            return;
        }
        if (PlayerUtil.isDeveloper(player)/* || player.getUsername().equals("Mod Hellmage")*/) {
            return;
        }
        ItemOnGroundManager.register(player, new Item(id, amount), player.getPosition().copy());
    }

    /**
     * Gets the total wealth of this container's items as a string.
     * @param itemContainer
     */
    public static String readValueOfContents(ItemContainer itemContainer) {

        final long value = determineValueOfContents(itemContainer);

        if (value < Integer.MIN_VALUE) {
            return "Too high!";
        } else {
            return String.valueOf(value);
        }
    }

    public static long determineValueOfContents(ItemContainer itemContainer) {
        return determineValueOfContents(itemContainer, item -> true);
    }

    public static long determineValueOfContents(ItemContainer itemContainer, Predicate<Item> evaluationPredicate) {
        long value = 0;
        for (final Item item : itemContainer.getValidItems()) {

            if (!evaluationPredicate.test(item))
                continue;
            if (!ItemUtil.bypassPriceMultiplier(item.getId())) {
                if (item.getValue(ItemValueType.PRICE_CHECKER) <= 200_000) {
                    value += item.getValue(ItemValueType.PRICE_CHECKER) * item.getAmount();
                } else {
                    if (item.getValue(ItemValueType.PRICE_CHECKER) != 1)
                        value += (long) (item.getValue(ItemValueType.PRICE_CHECKER) * 0.935) * item.getAmount();
                }
                if (item.getValue(ItemValueType.PRICE_CHECKER) == 1) {
                    value += item.getValue(ItemValueType.PRICE_CHECKER) * item.getAmount();
                }
            } else value += item.getValue(ItemValueType.PRICE_CHECKER) * item.getAmount();
        }
        return value;
    }

    /**
     * Force adds an item to this container. If it failed to, simply drop the
     * item for the player.
     */
    public static void addOrDrop(ItemContainer itemContainer, Player player, Item item) {
        if (player.getGameMode().isSpawn()) {
            return;
        }
        if(itemContainer.canHold(item))
            itemContainer.add(item);
        else
            TaskManager.submit(1, () -> ItemOnGroundManager.register(player, item));
    }

    /**
     * Sets all valid items in this container to invalid items
     * and logs any high-valued or specific item.
     *
     * This method should only be used for player-invoked empties.
     *
     * @return this {@link ItemContainer} instance.
     * @param itemContainer
     */
    public static ItemContainer emptyItems(ItemContainer itemContainer) {
        for (int i = 0; i < itemContainer.items.length; i++) {
            final Item item = itemContainer.items[i];
            if(itemContainer.isValidItem(item)) {
                if (flagItem(item)) {
                    if (!itemContainer.player.getGameMode().isSpawn())
                    Logging.log("emptied", "" + itemContainer.player.getUsername() + " has emptied: " + Misc.insertCommasToNumber(item.getAmount()) + " x " + item.getDefinition().getName() + "");

                    // Database Logging
                    new DatabaseEmptyLogs(
                            SQLManager.Companion.getINSTANCE(),
                            itemContainer.player.getUsername(),
                            item.getDefinition().getName(),
                            item.getAmount()
                    ).schedule(itemContainer.player);
                }
                itemContainer.items[i] = new Item(-1, 0);
            }
        }
        return itemContainer;
    }

    /**
     * Check whether the argued item should be logged whenever emptied.
     *
     * @param item the {@link Item} to check.
     * @return {@code true} if the item should be logged,
     *          {@code false} if otherwise.
     */
    static boolean flagItem(Item item) {
        return item.getValue(ItemValueType.PRICE_CHECKER) * item.getAmount() > 2_000_000 || BrokenItems.breaksOnDeath(item.getId());
    }

    public static IntStream occupiedSlotStream(ItemContainer itemContainer) {
        return IntStream.range(0, itemContainer.capacity()).filter(itemContainer::isSlotOccupied);
    }

    /**
     * Switches an item from one item container to another.
     *
     * @param source
     * @param target   The item container to put item on.
     * @param item The item to put from one container to another.
     * @param sort This flag checks whether or not to sort items, such as for
     *             bank.
     * @return The ItemContainer instance.
     */
    public static ItemContainer switchItem(ItemContainer source, ItemContainer target, Item item, boolean sort, boolean refresh) {

        final ItemDefinition definition = item.getDefinition();
        final boolean stackable = definition.isStackable();

        if (target.countFreeSlots() <= 0 && !(target.contains(item.getId()) && stackable)) {
            target.full();
            return source;
        }

        final int amountInContainer = source.getAmount(item);

        if (item.getAmount() > amountInContainer)
            item.setAmount(amountInContainer);

        if (item.getAmount() <= 0)
            return source;

        if (reachedMaxCap(source.player, target, item, stackable))
            return source;

        source.delete(item, refresh);

        if (source.unNoteItems(target) && definition.isNoted()
                && !ItemDefinition.forId(definition.getNoteId()).isNoted()) {
            item.setId(definition.getNoteId());
        }

        target.add(item, refresh);

        if (sort && source.getAmount(item) <= 0)
            shiftValidItemsToLeft(source);

        if (refresh) {
            source.refreshItems();
            target.refreshItems();
        }
        return source;
    }

    /**
     * Shifts all invalid items to the right of the container,
     * effectively removing 'empty' space between valid items.
     * @param itemContainer
     */
    public static void shiftValidItemsToLeft(ItemContainer itemContainer) {
        for (Item item : itemContainer.items) {
            if (item != null) {
                for (int j = 0; j < itemContainer.items.length - 1; j++) {
                    final Item other = itemContainer.items[j];
                    if (!itemContainer.isValidAndExistingItem(other)) {
                        itemContainer.swap(j + 1, j);
                    }
                }
            }
        }
    }

    /**
     * Switches an item from one item container to another.
     *
     * @param source
     * @param target   The item container to put item on.
     * @param item The item to put from one container to another.
     * @param slot The slot of the item to switch from one container to another.
     * @param sort This flag checks whether or not to sort items, such as for
     *             bank.
     * @return The ItemContainer instance.
     */
    public static ItemContainer moveItemFromSlot(ItemContainer source, ItemContainer target, Item item, int slot, boolean sort, boolean refresh, boolean amountVerification) {

        final Item itemAtSlot = source.atSlot(slot);

        if (itemAtSlot.getId() != item.getId())
            return source;

        final ItemDefinition definition = item.getDefinition();
        final boolean stackable = definition.isStackable();

        if (target.countFreeSlots() <= 0 && !(target.contains(item.getId()) && stackable)) {
            target.full();
            return source;
        }

        if (source.validateSpace(target)) {
            if (item.getAmount() > target.countFreeSlots() && !stackable)
                item.setAmount(target.countFreeSlots());
            if (item.getAmount() > source.getAmount(item))
                item.setAmount(source.getAmount(item));
        }

        if (reachedMaxCap(source.player, target, item, stackable))
            return source;

        source.delete(item, slot, refresh, target);

        // Noted items should not be in bank. Un-note if it's noted..
        if (source.unNoteItems(target) && definition.isNoted()
                && !ItemDefinition.forId(definition.getNoteId()).isNoted()) {
            item.setId(definition.getNoteId());
        }

        target.add(item, refresh);

        if (sort && source.getAmount(item) <= 0)
            shiftValidItemsToLeft(source);

        if (refresh) {
            source.refreshItems();
            target.refreshItems();
        }

        return source;
    }

    static boolean reachedMaxCap(Player player, ItemContainer container, Item item, boolean stackable) {
        if (stackable) {
            final int amountInTarget = container.getAmount(item);
            final long totalAmount = (long) amountInTarget + item.getAmount();
            if (totalAmount > Integer.MAX_VALUE) {
                final int maxAmountToGain = Integer.MAX_VALUE - amountInTarget;
                if (player != null) {
                    if (maxAmountToGain > 0)
                        player.sendMessage("You can only store " + maxAmountToGain + " more of that item!");
                    else
                        player.sendMessage("You reached the maximum amount you can store of that item!");
                }
                return true;
            }
        }
        return false;
    }

    public static String listItems(ItemContainer items) {
        StringBuilder string = new StringBuilder();
        int item_counter = 0;
        List<Item> uniqueItems = new ArrayList<>();

        uniqueItemLoop: for (Item item : items.getValidItems()) {
            // Make sure the item isn't already in the list.
            for (Item item_ : uniqueItems) {
                if (item_.getId() == item.getId()) {
                    continue uniqueItemLoop;
                }
            }
            uniqueItems.add(new Item(item.getId(), items.getAmount(item)));
        }

        for (Item item : uniqueItems) {
            if (item_counter > 0) {
                string.append("\\n");
            }
            string.append(item.getDefinition().getName().replaceAll("_", " "));

            String amt = "" + Misc.format(item.getAmount());
            if (item.getAmount() >= 1000000000) {
                amt = "@gre@" + (item.getAmount() / 1000000000) + " billion @whi@(" + Misc.format(item.getAmount()) + ")";
            } else if (item.getAmount() >= 1000000) {
                amt = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Misc.format(item.getAmount()) + ")";
            } else if (item.getAmount() >= 1000) {
                amt = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Misc.format(item.getAmount()) + ")";
            }
            string.append(" x @red@").append(amt);

            item_counter++;
        }
        if (item_counter == 0) {
            string = new StringBuilder("Absolutely nothing!");
        }
        return string.toString();
    }
}
