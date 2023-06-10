package com.grinder.game.model.item.container.player;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.StackType;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.ItemID;

/**
 * Represents a player's inventory item container.
 *
 * @author relex lawl
 */

public class Inventory extends ItemContainer {

    public static final int INTERFACE_ID = 3214;
    public static final int CAPACITY = 28;

    /**
     * The Inventory constructor.
     *
     * @param player The player who's inventory is being represented.
     */
    public Inventory(Player player) {
        super(player);
    }

    @Override
    public int capacity() {
        return CAPACITY;
    }

    @Override
    public StackType stackType() {
        return StackType.DEFAULT;
    }

    @Override
    public Inventory refreshItems() {
        getPlayer().getPacketSender().sendItemContainer(this, INTERFACE_ID);
        getPlayer().updateCarriedWeight();
        return this;
    }

    @Override
    public Inventory full() {
        getPlayer().getPacketSender().sendMessage("You don't have enough inventory space.", 1000);
		getPlayer().getPacketSender().sendSound(Sounds.INVENTORY_FULL_SOUND);
        getPlayer().updateCarriedWeight();
        return this;
    }

    public Inventory copy(){
        final int capacity = capacity();
        final StackType stackType = stackType();
        final Inventory container = new Inventory(getPlayer()) {
            @Override public int capacity() { return capacity; }
            @Override public Inventory full() { return this; }
            @Override public StackType stackType() { return stackType; }
        };
        for (int i = 0; i < getItems().length; i++) {
            Item item = getItems()[i];
            container.setItem(i, item.clone());
        }
        return container;
    }

    public Inventory copyNoAttributes(){
        final int capacity = capacity();
        final StackType stackType = stackType();
        final Inventory container = new Inventory(getPlayer()) {
            @Override public int capacity() { return capacity; }
            @Override public Inventory full() { return this; }
            @Override public StackType stackType() { return stackType; }
        };
        for (int i = 0; i < getItems().length; i++) {
            Item item = getItems()[i];
            if (item.hasAttributes()) {
                player.sendMessage("You cannot use charged items in your inventory presets.");
                continue;
            }
            if (item.getAsAttributable() != null) {
                if (item.getAsAttributable().hasAttributes()) {
                    player.sendMessage("You cannot use charged items in your inventory presets.");
                    continue;
                }
            }
            if (item.getId() == ItemID.SERPENTINE_HELM) {
                player.sendMessage("You cannot use charged items in your inventory presets.");
                continue;
            }
            container.setItem(i, item.clone());
        }
        return container;
    }

    @Override
    public boolean validateSpace(ItemContainer target) {
    	return target instanceof SafeDeposit;
    }
}
