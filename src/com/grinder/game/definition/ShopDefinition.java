package com.grinder.game.definition;

import com.grinder.game.model.item.Item;

/**
 * Represents a definition for a shop.
 *
 * @author Professor Oak
 */
public class ShopDefinition {

    private int id;
    private String name = "";
    private boolean newInterface;
    private Item[] originalStock;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isNewInterface() {
        return newInterface;
    }

    public Item[] getOriginalStock() {
        return originalStock;
    }

    public void setOriginalStock(Item[] newStock) {
        this.originalStock = newStock;
    }
}
