package com.grinder.game.model.item.container;

import com.google.gson.annotations.Expose;
import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.AttributableItem;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.game.model.sound.Sounds;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a container which contains items.
 *
 * @author relex lawl
 */
public abstract class ItemContainer {

    protected Player player;

    @Expose
    Item[] items = new Item[capacity()];

    public boolean isDirtySinceLastDupeCheck = true;

    /**
     * Create a new {@link ItemContainer} instance.
     */
    public ItemContainer() {
        for (int i = 0; i < items.length; i++) {
            items[i] = new Item(-1, 0);
        }
    }


    /**
     * Create a new {@link ItemContainer} and store the argued items
     * inside the {@link #items} array.
     *
     * @param itemList the items to store.
     */
    public ItemContainer(List<Item> itemList) {
        items = new Item[itemList.size()];
        for (int i = 0; i < itemList.size(); i++) {
            items[i] = itemList.get(i).clone();
        }
    }

    /**
     * Create a new {@link ItemContainer} that is player-specific.
     * This means containers such as the inventory and equipment.
     *
     * @param player the {@link Player} who owns this instance.
     */
    public ItemContainer(Player player) {
        this.player = player;
        for (int i = 0; i < capacity(); i++) {
            items[i] = new Item(-1, 0);
        }
    }

    /**
     * Implement this method to define behaviour to execute whenever
     * this container is full, that is to say, when either all slots are occupied
     * and a new (non-stackable) item could not be added,
     * or that a stackable item of the same id is already present
     * but of which the quantity exceeds {@link Integer#MAX_VALUE}.
     *
     * @return this {@link ItemContainer} instance.
     */
    public abstract ItemContainer full();

    /**
     * The total amount of slots that will be available in this container.
     * This sets the size of the {@link #items} array.
     */
    public abstract int capacity();

    protected boolean isValidAndExistingItem(Item item){
        return isValidItem(item) && isExistingItem(item);
    }

    boolean isValidItem(Item item){
        return item != null && item.getId() >= 0;
    }

    private boolean isExistingItem(Item item){
        return (item.getAmount() > 0 || (this instanceof Bank && item.getAmount() == 0));
    }

    protected boolean isInvalidSlot(int slot){
        if(slot < 0 || slot >= items.length){
            log("Invalid slot["+slot+"]");
            return true;
        }
        return false;
    }

    private void log(String message){
        System.err.println("[Container-"+player+"]["+items.length+"]: "+message);
    }

    /**
     * Checks if the container is currently empty.
     */
    public boolean isEmpty() {
        return countFreeSlots() == items.length;
    }

    /**
     * Gets an item by their slot, that is to say,
     * index in the {@link #items} array.
     *
     * @param slot the index in the array.
     * @return the {@link Item} at the argued slot or {@code null} if the slot is invalid.
     */
    public Item atSlot(int slot) {

        if(isInvalidSlot(slot))
            return null;

        Item item = items[slot];
        if(item == null)
            item = items[slot] = new Item(-1, 0);
        return item;
    }

    public Optional<Item> findAtSlot(int slot){
        return Optional.ofNullable(atSlot(slot)).filter(Item::isValid);
    }

    /**
     * Set the {@link Item} at the specified slot.
     *
     * @param slot index in items array.
     * @param item the {@link Item} to set.
     * @return this {@link ItemContainer} instance.
     */
    public ItemContainer setItem(int slot, Item item) {
        items[slot] = item;
        isDirtySinceLastDupeCheck = true;
        return this;
    }

    /**
     * Sets this {@link #items} array to the argued array.
     *
     * @param items the new {@link Item[] array}.
     * @return this {@link ItemContainer} instance.
     */
    public ItemContainer setItems(Item[] items) {
        if (items.length < capacity()) {
            final Item[] expandedItems = new Item[capacity()];
            Arrays.fill(expandedItems, new Item(-1, 0));
            System.arraycopy(items, 0, expandedItems, 0, items.length);
            items = expandedItems;
        }
        this.items = items;
        isDirtySinceLastDupeCheck = true;
        return this;
    }

    public Item[] getItems() {
        return items;
    }

    public Item[] getCopiedItems() {
        Item[] it = new Item[items.length];
        for (int i = 0; i < it.length; i++) {
            it[i] = items[i].clone();
        }
        return it;
    }

    public List<Item> cloneItems() {
        return getValidItems().stream().map(Item::clone).collect(Collectors.toList());
    }

    /**
     * Get a list of {@link Item}s with only meaningful items.
     *
     * @return a mutable list of valid items.
     */
    public ArrayList<Item> getValidItems() {
        final ArrayList<Item> copyList = new ArrayList<>();
        for (Item item : items) {
            if (isValidAndExistingItem(item)) {
                copyList.add(item);
            }
        }
        return copyList;
    }

    /**
     * Get an array of {@link Item}s with only meaningful items.
     *
     * @return a mutable list of valid items.
     */
    public Item[] copyValidItemsArray() {
        List<Item> items = getValidItems();
        Item[] array = new Item[items.size()];
        for (int i = 0; i < items.size(); i++) {
            array[i] = new Item(items.get(i).getId(), items.get(i).getAmount());
        }
        return array;
    }

    /**
     * Swaps two item slots.
     *
     * @param fromSlot From slot.
     * @param toSlot   To slot.
     */
    public ItemContainer swap(int fromSlot, int toSlot) {
        Item temporaryItem = atSlot(fromSlot);
        if (temporaryItem == null || temporaryItem.getId() <= 0) {
            return this;
        }
        if(fromSlot != toSlot) {
            setItem(fromSlot, getItems()[toSlot]);
            setItem(toSlot, temporaryItem);
        }
        return this;
    }

    /**
     * Checks if the slot contains an item.
     *
     * @param slot The container slot to check.
     * @return items[slot] != null.
     */
    public boolean isSlotOccupied(int slot) {
        return items[slot] != null && items[slot].getId() > 0 && items[slot].getAmount() > 0;
    }


    /**
     * Counts the number of free slots in this container,
     * that is to say, indices in the {@link #items} array,
     * at which the element is an invalid item.
     *
     * @return an integer value that represents the number of free slots.
     */
    public int countFreeSlots() {
        int freeSlots = 0;
        for (Item item : items)
            if (!isValidItem(item))
                freeSlots++;
        return freeSlots;
    }

    /**
     * Gets the last slot occupied by an item in container.
     *
     * @return The last slot occupied by an item in container.
     */
    public int getLastItemSlot() {
        for (int i = items.length - 1; i >= 0; i--) {
            if (isValidAndExistingItem(items[i]))
                return i;
        }
        return 0;
    }

    /**
     * Gets the first slot found for an item with said id.
     *
     * @param id The id to loop through items to find.
     * @return The slot index the item is located in.
     */
    public int getSlot(int id) {
        for (int i = 0; i < items.length; i++) {
            final Item item = items[i];
            if (item.getId() == id) {
                if (isExistingItem(item))
                    return i;
            }
        }
        return -1;
    }
    public int verifyItem(final Item item, final int slot) {
        final ItemDefinition def = item.getDefinition();
        if (def == null || isEmpty()) {
            return 0;
        }
        if (!contains(item.getId())) {
            return 0;
        }
        final Item slotItem = get(slot);
        if (slotItem == null) {
            return 0;
        }
        if (slotItem.getId() != item.getId()) {
            return 0;
        }
        if (slotItem.getAmount() < 1 || item.getAmount() < 1) {
            return 0;
        }
        int amount = item.getAmount();
        final int maxAmount = item.getDefinition().isStackable() ? slotItem.getAmount() : getAmount(item.getId());
        if (amount > maxAmount) {
            amount = maxAmount;
        }
        return amount;
    }
    public int getSlot(Item item){
        if(item.hasAttributes()) {
            final AttributableItem attributableItem = item.getAsAttributable();
            int placeHolderSlot = -1;
            for (int i = 0; i < items.length; i++) {
                final Item other = items[i];
                if(other.getId() == item.getId()){
                    final int amountOfOther = other.getAmount();
                    // we want the first place holder slot
                    if(amountOfOther == 0 && placeHolderSlot == -1)
                        placeHolderSlot = i;
                    else if (amountOfOther > 0 && other.hasAttributes()) {
                        final AttributableItem otherAttributableItem = other.getAsAttributable();
                        // check if attribute map reference is the same
                        if(otherAttributableItem.getAttributes() == attributableItem.getAttributes())
                            return i;
                    }
                }
            }
            return placeHolderSlot;
        }
        return getSlot(item.getId());
    }

    /**
     * Checks if this container has a set of certain items.
     *
     * @param item the item to check in this container for.
     * @return true if this container has the item with the correct amount.
     */
    public boolean contains(Item[] item) {
        if (item.length == 0)
            return false;

        for (Item nextItem : item) {
            if (nextItem == null)
                continue;
            if (this.getAmount(nextItem.getId()) < nextItem.getAmount())
                return false;
        }
        return true;
    }

    /**
     * Checks if the specified slot contains the specified item id.
     *
     * @param slot The slot.
     * @param id   The id.
     * @return <code>true</code> if the slot contains the id.
     */
    public boolean containsAtSlot(int slot, int id) {
        final Item item = atSlot(slot);
        return item != null && item.getId() == id;
    }

    /**
     * Checks if container contains a certain item id.
     *
     * @param id The item id to check for in container.
     * @return Container contains item with the specified id.
     */
    public boolean contains(int id) {
        for (Item items : items) {
            if (items == null) continue;
            if (items.getId() == id)
                return true;
        }
        return false;
    }

    /**
     * Gets the total amount of items in the container in the specified slot
     *
     * @param slot The slot of the item to search for.
     * @return The total amount of items in the container with said slot.
     */
    public int getAmountForSlot(int slot) {
        if(isInvalidSlot(slot))
            return 0;
        final Item item = items[slot];
        return isValidItem(item) ? item.getAmount() : 0;
    }

    /**
     * Sets all items in this container to invalid items.
     *
     * @return this {@link ItemContainer} instance.
     */
    public ItemContainer resetItems() {
        for (int i = 0; i < items.length; i++)
            items[i] = new Item(-1, 0);
        return this;
    }

    public void moveItemFromSlot(ItemContainer target, Item item, int slot, boolean sort, boolean refresh) {
        ItemContainerUtil.moveItemFromSlot(this, target, item, slot, sort, refresh, false);
    }

    boolean unNoteItems(ItemContainer to) {
        return to instanceof Bank;
    }

    /**
     * Adds an item to the item container.
     *
     * @param item The item to add.
     * @return The ItemContainer instance.
     */
    public ItemContainer add(Item item) {
        return add(item, true);
    }

    /**
     * Adds an item to the item container and refreshes this container for the {@link #player}.
     *
     * @param itemId     the id of the item being added.
     * @param itemAmount the amount of the item being added.
     * @return the amount of the item that was successfully added to this {@link ItemContainer}.
     */
    public ItemContainer add(int itemId, int itemAmount) {
        return add(new Item(itemId, itemAmount));
    }

    public ItemContainer add(Item item, boolean refresh) {
        return add(item, refresh, true);
    }

    /**
     * Adds an item to the item container.
     *
     * @param item    The item to add.
     * @param refresh If <code>true</code> the item container interface will be
     *                refreshed.
     * @return The ItemContainer instance.
     */
    public ItemContainer add(Item item, boolean refresh, boolean dropIfFull) {

        if (!isValidAndExistingItem(item))
            return this;

        ItemTransformer.transformItemIdAndAmount(item);

        final ItemDefinition definition = item.getDefinition();
        final boolean stackItem = definition.isStackable() || stackType() == StackType.STACKS;

        if (stackItem)
            stackOrAdd(item, dropIfFull);
        else
            addSeparately(item, dropIfFull);

        if (refresh)
            refreshItems();
        return this;
    }

    private void addSeparately(Item item, boolean dropIfFull) {
        int amount = item.getAmount();
        while (amount > 0) {
            final int slot = getEmptySlot();
            if (slot == -1) {
                relayContainerFullMessage(item.getId(), 1, dropIfFull);
                break;
            } else {
                items[slot] = item.clone().setAmount(1);
                isDirtySinceLastDupeCheck = true;
            }
            amount--;
        }
    }

    private void stackOrAdd(Item item, boolean dropIfFull) {

        int slot = getSlot(item);
        int amount;
        if (slot == -1)
            slot = getEmptySlot();
        if (slot == -1) {
            relayContainerFullMessage(item.getId(), item.getAmount(), dropIfFull);
            return;
        }
        final long totalAmount = (long) getAmountForSlot(slot) + (long) item.getAmount();
        amount = (int) Math.min(totalAmount, Integer.MAX_VALUE);
        items[slot] = item.clone().setAmount(amount);
    }

    private void relayContainerFullMessage(int itemId, int amount, boolean dropbeneath){
        if (player != null) {
            if (!player.getAttributes().bool(Attribute.IS_COOKING)) {
                //player.getPacketSender().sendMessage("You couldn't hold all those items.", 1000);
                player.getPacketSender().sendMessage("You don't have enough space in your inventory.", 1000);
                player.getPacketSender().sendSound(Sounds.INVENTORY_FULL_SOUND);
                if (dropbeneath && player.getInterfaceId() != Presetables.INTERFACE_ID) {
                    ItemContainerUtil.dropUnder(player, itemId, amount);
                }
            }
        }
    }

    /**
     * Deletes an item from the item container.
     *
     * @param item The item to delete.
     * @return The ItemContainer instance.
     */
    public ItemContainer delete(Item item) {
        return delete(item.getId(), item.getAmount());
    }

    /**
     * Deletes an item from the item container.
     *
     * @param item The item to delete.
     * @param slot The slot of the item (used to delete the item from said slot,
     *             not the first one found).
     * @return The ItemContainer instance.
     */
    public ItemContainer delete(Item item, int slot) {
        return delete(item, slot, true);
    }
    /**
     * Deletes all of an item from the item container.
     *
     * @param itemId The id of the item to delete.
     * @return The ItemContainer instance.
     */
    public ItemContainer deleteAll(int itemId) {
        return delete(itemId, getAmount(itemId));
    }

    /**
     * Deletes an item from the item container.
     *
     * @param item    The item to delete.
     * @param slot    The slot of the item to delete.
     * @param refresh If <code>true</code> the item container interface will
     *                refresh.
     * @return The ItemContainer instance.
     */
    public ItemContainer delete(Item item, int slot, boolean refresh) {
        return delete(item, slot, refresh, null);
    }

    /**
     * Deletes an item from the item container.
     *
     * @param item        The item to delete.
     * @param slot        The slot of the item to delete.
     * @param refresh     If <code>true</code> the item container interface will
     *                    refresh.
     * @param toContainer To check if other container has enough space to continue
     *                    deleting said amount from this container.
     * @return The ItemContainer instance.
     */
    public ItemContainer delete(Item item, int slot, boolean refresh, ItemContainer toContainer) {

        if (isInvalidSlot(slot) || !isValidAndExistingItem(item))
            return this;

        final ItemDefinition definition = item.getDefinition();
        final boolean stackItem = definition.isStackable() || stackType() == StackType.STACKS;
        final boolean sameClass = toContainer == null || this.getClass() == toContainer.getClass();
        final int amountInContainer = getAmount(item);

        if (item.getAmount() > amountInContainer)
            item.setAmount(amountInContainer);

        if (stackItem) {
            final Item itemAtSlot = atSlot(slot);
            itemAtSlot.setAmount(itemAtSlot.getAmount() - item.getAmount());
            if (itemAtSlot.getAmount() <= 0)
                removeAtSlot(slot, sameClass);
        } else {
            int amount = item.getAmount();
            while (amount > 0) {
                if (slot == -1 || (toContainer != null && toContainer.isFull()))
                    break;
                removeAtSlot(slot, sameClass);
                slot = getSlot(item);
                amount--;
            }
        }

        if (refresh)
            refreshItems();
        return this;
    }

    private void removeAtSlot(int slot, boolean sameClass) {
        if(sameClass || !hasPlaceHolders())
            set(slot, new Item(-1, 0));
        else
            atSlot(slot).setAmount(0);
        isDirtySinceLastDupeCheck = true;
    }

    public boolean hasPlaceHolders(){
        return false;
    }

    /**
     * Gets the owner's player instance.
     *
     * @return player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player viewing the container, used for containers such as Shops.
     */
    public ItemContainer setPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Gets the total amount of items in the container with the specified id.
     *
     * @param id The id of the item to search for.
     * @return The total amount of items in the container with said id.
     */
    public int getAmount(int id) {
        int totalAmount = 0;
        for (Item item : items) {
            if(item == null)
                continue;
            if (item.getId() == id) {
                if(item.hasAttributes() && item.getAmount() > 0)
                    return 1;
                totalAmount += item.getAmount();

                if (item.getDefinition().isStackable()) {
                    break;
                }
            }
        }
        return totalAmount;
    }

    public int getAmount(Item item) {
        if(item.hasAttributes()) {
            int count = 0;
            for (final Item other : items) {
                if (other.hasAttributes()) {
                    if (item.getId() == other.getId()) {
                        if (item.getAsAttributable().getAttributes() == other.getAsAttributable().getAttributes()) {
                            count += other.getAmount();

                            if (item.getDefinition().isStackable()) {
                                break;
                            }
                        }
                    }
                }
            }
            return count;
        }
        return getAmount(item.getId());
    }

    /**
     * The container's type enum, see enum for information.
     */
    public abstract StackType stackType();

    /**
     * Checks if the container is out of available slots.
     *
     * @return No free slot available.
     */
    public boolean isFull() {
        return getEmptySlot() == -1;
    }

    /**
     * The refresh method to send the container's interface on addition or
     * deletion of an item.
     */
    public abstract ItemContainer refreshItems();

    /**
     * Gets the next empty slot for an item to equip.
     *
     * @return The next empty slot index.
     */
    public int getEmptySlot() {
        for (int i = 0; i < capacity(); i++) {
            if (!isValidAndExistingItem(items[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Deletes an item from the item container.
     *
     * @param id     The id of the item to delete.
     * @param amount The amount of the item to delete.
     * @return The ItemContainer instance.
     */
    public ItemContainer delete(int id, int amount) {
        return delete(id, amount, true);
    }

    public ItemContainer delete(Item item, boolean refresh) {
        return delete(item.getId(), item.getAmount(), refresh);
    }

    /**
     * Deletes an item from the item container.
     *
     * @param id      The id of the item to delete.
     * @param amount  The amount of the item to delete.
     * @param refresh If <code>true</code> the item container interface will
     *                refresh.
     * @return The ItemContainer instance.
     */
    public ItemContainer delete(int id, int amount, boolean refresh) {
        return delete(new Item(id, amount), getSlot(id), refresh);
    }

    /**
     * Gets an item id by its index.
     *
     * @param id the item id
     * @return the item id on this index.
     */
    public Item getById(int id) {
        for (Item item : items) {
            if (item == null)
                continue;
            if (item.getId() == id)
                return item;
        }
        return null;
    }

    public void replaceAll(int id, int replace) {
        replaceAll(id, replace, true);
    }

    public void replace(Item original, Item replacement) {
        int slot = getSlot(original);

        if(slot < 0) {
            throw new IllegalArgumentException("Cannot replace non-existant item " + original.getId());
        }

        items[slot] = replacement;
    }

    public void replace(int slot, Item replacement) {
        items[slot] = replacement;
    }

    public void replaceAll(int id, int replace, boolean update) {
        for(int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].isValid()) {
                if(items[i].getId() == id) {
                    items[i] = new Item(replace, 1);
                    isDirtySinceLastDupeCheck = true;
                }
            }
        }

        if (update)
            refreshItems();
    }

    public void replaceFirst(int id, int replace) {
        replaceFirst(id, replace, true);
    }

    public void replaceFirst(int id, int replace, boolean update) {
        for (Item item : items) {

            if (item == null || !item.isValid())
                continue;

            if (item.getId() == id) {
                item.setId(replace);
                isDirtySinceLastDupeCheck = true;
                break;
            }
        }
        if (update)
            refreshItems();
    }

    public boolean containsAll(int... ids) {
        return Arrays.stream(ids).allMatch(this::contains);
    }

    public boolean contains(Item item) {
        return getAmount(item) >= item.getAmount();
    }

    public boolean contains(List<Item> items) {
        for(Item item : items) {
            if(!contains(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(int id, int amount) {
        return getAmount(id) >= amount;
    }

    public boolean containsOnly(int... ids) {
        Set<Integer> req = IntStream.of(ids).boxed().collect(Collectors.toSet());

        for (Item item : items) {
            if (item != null) {
                if (!req.contains(item.getId()))
                    return false;
            }
        }
        return true;
    }

    public boolean containsAny(int... ids) {
        return Arrays.stream(ids).anyMatch(this::contains);
    }

    public boolean containsAny(List<Integer> ids) {
        return ids.stream().anyMatch(this::contains);
    }

    public boolean containsAny(Item... items) {
        return Arrays.stream(items).filter(Objects::nonNull).anyMatch(item -> contains(item.getId()));
    }
    public boolean containsAnyAtSlot(int slot, int... ids) {
        return IntStream.of(ids).anyMatch(id -> containsAtSlot(slot, id));
    }
    public void set(int slot, Item item) {
        if (item == null)
            item = new Item(-1);
        items[slot] = item;
    }

    public void combine(Item use, Item usedWith, Item result) {
        if(!contains(use) || !contains(usedWith)) {
            return;
        }

        delete(use);
        delete(usedWith);
        add(result);
    }

    public void reset(int slot) {
        items[slot] = new Item(-1, 0);
    }

    public Item get(int slot) {
        return items[slot];
    }

    public ItemContainer addItems(Item[] items, boolean refresh) {
        if (items == null)
            return this;
        for (Item item : items) {
            if (isValidAndExistingItem(item))
                add(item, false);
        }
        if(refresh)
            refreshItems();
        return this;
    }

    /**
     * Adds a set of items into the inventory.
     *
     * @param item the set of items to add.
     */
    public void addItemSet(Item[] item) {
        for (Item addItem : item) {
            if (addItem == null) {
                continue;
            }
            add(addItem.clone());
        }
    }

    /**
     * Deletes a set of items from the inventory.
     *
     * @param item the set of items to delete.
     */
    public void delete(Item[] item) {
        for (Item deleteItem : item) {
            if (deleteItem == null) {
                continue;
            }

            delete(deleteItem);
        }
    }

    public void delete(List<Item> item) {
        for (Item deleteItem : item) {
            if (deleteItem == null) {
                continue;
            }

            delete(deleteItem);
        }
    }

    public boolean canDeposit(int id, int amount) {
        final ItemDefinition definition = ItemDefinition.forId(id);
        final boolean isStackable = this.stackType() == StackType.STACKS || (definition != null && (definition.isStackable() || definition.isNoted()));
        final int slotsTaken = isStackable ? contains(id) ? 0 : 1 : amount;
        final int freeSlots = countFreeSlots();

        if (slotsTaken == 0) {
            final long amountPresent = getAmount(id);
            final long combinedAmount = amountPresent + amount;
            return combinedAmount <= Integer.MAX_VALUE;
        }

        return slotsTaken <= freeSlots;
    }

    public boolean canHold(Item item) {
        return canDeposit(item.getId(), item.getAmount());
    }

    public boolean canHold(int id, int amount) {
        return canDeposit(id, amount);
    }

    public boolean validateSpace(ItemContainer target) {
        return false;
    }
}
