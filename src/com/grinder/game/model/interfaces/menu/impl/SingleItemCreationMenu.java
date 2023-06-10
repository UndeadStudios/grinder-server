package com.grinder.game.model.interfaces.menu.impl;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.syntax.impl.CreationMenuX;

/**
 * Represents a sub class of {@link CreationMenu}.
 * <p>
 * This class is used to handle creation menus which
 * only display one item on the interface.
 * An example of this would be Cooking.
 *
 * @author Professor Oak
 */
public class SingleItemCreationMenu extends CreationMenu {

    /**
     * The item to display on the creation menu
     * interface.
     */
    private final int item;

    /**
     * Creates a new {@link SingleItemCreationMenu} with the given data.
     *
     * @param player
     * @param title
     * @param action
     * @param item
     */
    public SingleItemCreationMenu(Player player, int item, String title, CreationMenuAction action) {
        super(player, title, action);
        this.item = item;
    }

    /**
     * Opens the interface for a singular creation menu.
     */
    @Override
    public CreationMenu open() {
        getPlayer().getPacketSender().sendInterfaceModel(1746, item, 140);
        getPlayer().getPacketSender().sendString(2799, ItemDefinition.forId(item).getName());
        getPlayer().getPacketSender().sendString(2800, getTitle());
        getPlayer().getPacketSender().sendChatboxInterface(4429);
        return this;
    }

    /**
     * Handles buttons related to the singular creation menu.
     */
    @Override
    public boolean handleButton(int id) {
        switch (id) {
            case 2799: //Make 1
                getAction().execute(0, item, 1);
                return true;
            case 2798: //Make 5
                getAction().execute(0, item, 5);
                return true;
            case 1748: //Make X
                getPlayer().setEnterSyntax(new CreationMenuX(0, item));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                break;
            case 1747: //Make all
                getAction().execute(0, item, 28);
                return true;
        }
        return false;
    }

}
