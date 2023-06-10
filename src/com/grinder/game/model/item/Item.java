package com.grinder.game.model.item;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import com.google.gson.annotations.Expose;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueDefinition;
import com.grinder.game.definition.ItemValueType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item.
 *
 * @author Professor Oak
 */
public class Item {

    /**
     * The item id.
     */
    @Expose
    private int id;
    /**
     * Amount of the item.
     */
    @Expose
    private int amount;

    /**
     * An Item object constructor.
     *
     * @param id     Item id.
     * @param amount Item amount.
     */
    public Item(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    /**
     * An Item object constructor.
     *
     * @param id Item id.
     */
    public Item(int id) {
        this(id, 1);
    }

    /**
     * Converts an {@link Item} array into an Integer array.
     * @param ids the array to convert into an Integer array.
     * @return the Integer array containing the values from the item array.
     */
    public static final int[] convert(Item... ids) {
        List<Integer> values = new ArrayList<>();
        for(Item identifier : ids) {
            values.add(identifier.getId());
        }
        return values.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Converts an int array into an {@link Item} array.
     * @param id the array to convert into an item array.
     * @return the item array containing the values from the int array.
     */
    public static final Item[] convert(int... id) {
        List<Item> items = new ArrayList<>();
        for(int identifier : id) {
            items.add(new Item(identifier));
        }
        return Iterables.toArray(items, Item.class);
    }

    /**
     * Gets the item's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the item's id.
     *
     * @param id New item id.
     */
    public Item setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Gets the amount of the item.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the item.
     */
    public Item setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Checks if this item is valid or not.
     *
     * @return
     */
    public boolean isValid() {
        return id >= 0 && amount > 0;
    }

    /**
     * Increment the amount by 1.
     */
    public void incrementAmount() {
        if ((amount + 1) > Integer.MAX_VALUE) {
            return;
        }
        amount++;
    }

    /**
     * Decrement the amount by 1.
     */
    public void decrementAmount() {
        if ((amount - 1) < 0) {
            return;
        }
        amount--;
    }

    /**
     * Increment the amount by the specified amount.
     */
    public void incrementAmountBy(int amount) {
        if (((long) this.amount + amount) > Integer.MAX_VALUE) {
            this.amount = Integer.MAX_VALUE;
        } else {
            this.amount += amount;
        }
    }

    /**
     * Decrement the amount by the specified amount.
     */
    public void decrementAmountBy(int amount) {
        if ((this.amount - amount) < 1) {
            this.amount = 0;
        } else {
            this.amount -= amount;
        }
    }

    public String getName() {
        return getDefinition().getName();
    }

    public ItemDefinition getDefinition() {
        return ItemDefinition.forId(id);
    }

    public long getValue(ItemValueType valueType){
        return ItemValueDefinition.Companion.getValue(id, valueType);
    }

    @Override
    public Item clone() {
        return new Item(id, amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item other = (Item) obj;
            return id == other.id && amount == other.amount;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return amount * prime + id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).add("amount", amount).toString();
    }

    public boolean hasAttributes() {
        return this instanceof AttributableItem && this.isValid() && this.amount >= 0 && this != null;
    }

    public AttributableItem getAsAttributable() {
        if(!hasAttributes()) return null;
        return (AttributableItem) this;
    }

}