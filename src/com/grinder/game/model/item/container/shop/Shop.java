package com.grinder.game.model.item.container.shop;

import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.util.ShopIdentifiers;

public class Shop {

    /**
     * The tax modifier which applies for players
     * selling items.
     */
    public static final double SALES_TAX_MODIFIER = 0.10;
    /**
     * The max amount of items a shop can have.
     */
    public static final int MAX_SHOP_ITEMS = 500;
    /**
     * The shop interface id.
     */
    public static final int INTERFACE_ID = 3824;
    /**
     * The new shop interface id.
     */
    public static final int NEW_INTERFACE_ID = 30900;
    /**
     * The starting interface child id of items.
     */
    public static final int ITEM_CHILD_ID = 3900;
    /**
     * The starting interface child id of items.
     */
    public static final int NEW_ITEM_CHILD_ID = NEW_INTERFACE_ID + 5;
    /**
     * The interface child id of the shop's name.
     */
    public static final int NAME_INTERFACE_CHILD_ID = 3901;
    /**
     * The new interface child id of the shop's name.
     */
    public static final int NEW_NAME_INTERFACE_CHILD_ID = NEW_INTERFACE_ID + 3;
    /**
     * The inventory interface id, used to set the items right click values
     * to 'sell'.
     */
    public static final int INVENTORY_INTERFACE_ID = 3823;
    /**
     * The new interface scroll bar id
     */
    public static final int NEW_SCROLL_BAR_INTERFACE_ID = NEW_INTERFACE_ID + 4;

    private final int id;
    private final String name;
    private boolean newInterface;
    private final Item[] originalStock;
    private final Item[] currentStock = new Item[MAX_SHOP_ITEMS];
    private boolean restocking, isLimitedShop;

    public Shop(int id, String name, boolean newInterface, Item[] originalStock) {
        this.id = id;
        this.name = name;
        this.newInterface = newInterface;
        this.originalStock = originalStock;
        for (int i = 0; i < originalStock.length; i++) {
            this.currentStock[i] = originalStock[i].clone();
        }
    }

    public void removeItem(int itemId, int amount) {
        for (int i = 0; i < currentStock.length; i++) {
            Item item = currentStock[i];
            if (item == null)
                continue;
            if (item.getId() == itemId) {
                item.setAmount(item.getAmount() - amount);
                if (item.getAmount() <= 0) { // was <= 1
                    if (id == ShopIdentifiers.GENERAL_STORE) {
                        currentStock[i] = null;
                    } else {
/*                        if (isLimitedShop())
                            item.setAmount(0);
                        else
                            item.setAmount(1);*/
                        item.setAmount(0);
                    }
                }
                break;
            }
        }
        sortItems();
    }

    public void addItem(int itemId, int amount) {
        boolean found = false;
        for (Item item : currentStock) {
            if (item == null)
                continue;
            if (item.getId() == itemId) {
                long amt = item.getAmount() + amount;
                if (amt < Integer.MAX_VALUE) {
                    item.setAmount(item.getAmount() + amount);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            for (int i = 0; i < currentStock.length; i++) {
                if (currentStock[i] == null) {
                    currentStock[i] = new Item(itemId, amount);
                    break;
                }
            }
        }
    }

    public boolean isFull() {
        int amount = 0;
        for (Item item : currentStock) {
            if (item == null)
                continue;
            amount++;
        }
        return (amount >= MAX_SHOP_ITEMS);
    }

    public int getAmount(int itemId, boolean fromOriginalStock) {
        if (!fromOriginalStock) {
            for (Item item : currentStock) {
                if (item == null)
                    continue;
                if (item.getId() == itemId) {
                    return item.getAmount();
                }
            }
        } else {
            for (Item item : originalStock) {
                if (item.getId() == itemId) {
                    return item.getAmount();
                }
            }
        }
        return 0;
    }

    public int getSlot(int itemId, boolean fromOriginalStock) {
        if (!fromOriginalStock) {
            for (int i = 0; i < currentStock.length; i++) {
                if (currentStock[i].getId() == itemId) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < originalStock.length; i++) {
                if (originalStock[i].getId() == itemId) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static boolean isInterfaceOpen(Player player) {
        return player.getInterfaceId() == Shop.INTERFACE_ID
                || player.getInterfaceId() == Shop.NEW_INTERFACE_ID
                || player.getInterfaceId() == SlayerManager.SHOP;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isNewInterface() {
        return newInterface;
    }

    public Item[] getCurrentStock() {
        return currentStock;
    }

    public Item[] getOriginalStock() {
        return originalStock;
    }

    public boolean isRestocking() {
        return restocking;
    }

    public void setRestocking(boolean restocking) {
        this.restocking = restocking;
    }

    /**
     * Sorts this shop's current stock array of items to leave no empty spaces.
     */
    public void sortItems() {
        for (int k = 0; k < currentStock.length; k++) {
            if (currentStock[k] == null) {
                continue;
            }
            for (int i = 0; i < (currentStock.length - 1); i++) {
                if (currentStock[i] == null || currentStock[i].getId() <= 0) {
                    swap((i + 1), i);
                }
            }
        }
    }

    /**
     * Swaps two item slots.
     *
     * @param fromSlot
     *            From slot.
     * @param toSlot
     *            To slot.
     */
    public void swap(int fromSlot, int toSlot) {
        Item temporaryItem = currentStock[fromSlot];
        if (temporaryItem == null || temporaryItem.getId() <= 0) {
            return;
        }
        currentStock[fromSlot] = currentStock[toSlot];
        currentStock[toSlot] = temporaryItem;
    }

    public Shop setAsLimitedShop() {
        this.isLimitedShop = true;
        this.restocking = true;//stops from restocking
        return this;
    }

    public boolean isLimitedShop() {
        return isLimitedShop;
    }
}
