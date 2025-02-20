package com.grinder.game.model.interfaces.menu.impl;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.syntax.impl.CreationMenuX;

/**
 * Represents a sub class of {@link CreationMenu}.
 * <p>
 * This class is used to handle creation menus which
 * display two items on the interface.
 *
 * @author Professor Oak
 */
public class DoubleItemCreationMenu extends CreationMenu {

    /**
     * The items to draw on the interface.
     */
    private final int firstItem, secondItem;

    public DoubleItemCreationMenu(Player player, int firstItem, int secondItem, String title, CreationMenuAction action) {
        super(player, title, action);
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    @Override
    public CreationMenu open() {
        getPlayer().getPacketSender().sendString(8879, getTitle());

        getPlayer().getPacketSender().sendInterfaceModel(8869, firstItem, 250);
        getPlayer().getPacketSender().sendString(8874, "" + ItemDefinition.forId(firstItem).getName() + "");

        getPlayer().getPacketSender().sendInterfaceModel(8870, secondItem, 250);
        getPlayer().getPacketSender().sendString(8878, "" + ItemDefinition.forId(secondItem).getName() + "");

        getPlayer().getPacketSender().sendChatboxInterface(8866);
        return this;
    }

    @Override
    public boolean handleButton(int id) {
        switch (id) {
            case 8874:
                getAction().execute(0, firstItem, 1);
                return true;
            case 8873:
                getAction().execute(0, firstItem, 5);
                return true;
            case 8872:
                getAction().execute(0, firstItem, 10);
                return true;
            case 8871:
                getPlayer().setEnterSyntax(new CreationMenuX(0, firstItem));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                return true;
            case 8878:
                getAction().execute(1, secondItem, 1);
                return true;
            case 8877:
                getAction().execute(1, secondItem, 5);
                return true;
            case 8876:
                getAction().execute(1, secondItem, 10);
                return true;
            case 8875:
                getPlayer().setEnterSyntax(new CreationMenuX(1, secondItem));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                return true;
        }
        return false;
    }

}
