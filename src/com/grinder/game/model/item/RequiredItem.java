package com.grinder.game.model.item;

/**
 * Represents a required item. Used when skilling.
 *
 * @author Professor Oak
 */
public class RequiredItem {

    /**
     * The {@link Item}.
     */
    private final Item item;

    /**
     * Should this item be deleted eventually?
     */
    private final boolean delete;
    
    public RequiredItem(int item, boolean delete) {
    	this.item = new Item(item);
    	this.delete = delete;
    }

    public RequiredItem(Item item, boolean delete) {
        this.item = item;
        this.delete = delete;
    }
    
    public RequiredItem(int item) {
        this.item = new Item(item);
        this.delete = false;
    }

    public RequiredItem(Item item) {
        this.item = item;
        this.delete = false;
    }

    public Item getItem() {
        return item;
    }

    public boolean isDelete() {
        return delete;
    }
}
