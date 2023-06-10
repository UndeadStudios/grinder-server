package com.grinder.game.model.item.container.player;

import java.util.Arrays;
import java.util.Objects;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.StackType;
import com.grinder.util.ItemID;

/**
 * Represents a player's equipment item container.
 *
 * @author Gabriel Hannason
 */
public class Equipment extends ItemContainer {

    public static final int CAPACITY = 17;

    /**
     * The Equipment constructor.
     *
     * @param player The player who's equipment is being represented.
     */
    public Equipment(Player player) {
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
    public ItemContainer refreshItems() {
        getPlayer().getPacketSender().sendItemContainer(this, EquipmentConstants.INVENTORY_INTERFACE_ID);
        getPlayer().updateCarriedWeight();
        return this;
    }

    @Override
    public Equipment full() {
        return this;
    }

	public boolean containsAny(Item[] items) {
		return Arrays.stream(items).filter(Objects::nonNull).anyMatch(item -> contains(item.getId()));
	}

    public Equipment copy(){
        final int capacity = capacity();
        final StackType stackType = stackType();
        final Equipment container = new Equipment(getPlayer()) {
            @Override public int capacity() { return capacity; }
            @Override public Equipment full() { return this; }
            @Override public StackType stackType() { return stackType; }
            @Override public ItemContainer refreshItems() { return this; }
        };
        for (int i = 0; i < getItems().length; i++) {
            Item item = getItems()[i];
            container.setItem(i, item.clone());
        }
        return container;
    }

    public Equipment copyNoAttributes(){
        final int capacity = capacity();
        final StackType stackType = stackType();
        final Equipment container = new Equipment(getPlayer()) {
            @Override public int capacity() { return capacity; }
            @Override public Equipment full() { return this; }
            @Override public StackType stackType() { return stackType; }
            @Override public ItemContainer refreshItems() { return this; }
        };
        for (int i = 0; i < getItems().length; i++) {
            Item item = getItems()[i];
            if (item.hasAttributes()) {
                player.sendMessage("You cannot use charged items in your equipment presets.");
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
}
