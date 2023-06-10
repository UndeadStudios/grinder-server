package com.grinder.game.model.interfaces.menu.impl;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.syntax.impl.CreationMenuX;

/**
 * Represents a sub class of {@link CreationMenu}.
 * <p>
 * This class is used to handle creation menus which
 * display three items on the interface.
 *
 * @author Professor Oak
 */
public class TripleItemCreationMenu extends CreationMenu {

    /**
     * The items to display on the interface.
     */
    private final int firstItem, secondItem, thirdItem;
    
    public TripleItemCreationMenu(Player player, int firstItem, int secondItem, int thirdItem, String title, CreationMenuAction action) {
        super(player, title, action);
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        this.thirdItem = thirdItem;
    }

    @Override
    public CreationMenu open() {
        getPlayer().getPacketSender().sendString(8898, getTitle());

        getPlayer().getPacketSender().sendInterfaceModel(8883, firstItem, (firstItem == 13385 ? 100  : 200));
        getPlayer().getPacketSender().sendString(8889, "" + ItemDefinition.forId(firstItem).getName() + "");

        getPlayer().getPacketSender().sendInterfaceModel(8884, secondItem, 200);
        getPlayer().getPacketSender().sendString(8893, "" + ItemDefinition.forId(secondItem).getName() + "");

        getPlayer().getPacketSender().sendInterfaceModel(8885, thirdItem, 200);
        getPlayer().getPacketSender().sendString(8897, "" + ItemDefinition.forId(thirdItem).getName() + "");

        getPlayer().getPacketSender().sendChatboxInterface(8880);
        return this;
    }

    @Override
    public boolean handleButton(int id) {
        switch (id) {
            case 8889:
                getAction().execute(0, firstItem, 1);
                return true;
            case 8888:
                getAction().execute(0, firstItem, 5);
                return true;
            case 8887:
                getAction().execute(0, firstItem, 10);
                return true;
            case 8886:
                getPlayer().setEnterSyntax(new CreationMenuX(0, firstItem));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                return true;
            case 8893:
                getAction().execute(1, secondItem, 1);
                return true;
            case 8892:
                getAction().execute(1, secondItem, 5);
                return true;
            case 8891:
                getAction().execute(1, secondItem, 10);
                return true;
            case 8890:
                getPlayer().setEnterSyntax(new CreationMenuX(1, secondItem));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                return true;
            case 8897:
                getAction().execute(2, thirdItem, 1);
                return true;
            case 8896:
                getAction().execute(2, thirdItem, 5);
                return true;
            case 8895:
                getAction().execute(2, thirdItem, 10);
                return true;
            case 8894:
                getPlayer().setEnterSyntax(new CreationMenuX(2, thirdItem));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                return true;
        }
        return false;
    }

}
