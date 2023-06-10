package com.grinder.game.model.interfaces.menu.impl;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.syntax.impl.CreationMenuX;

/**
 * Represents a sub class of {@link CreationMenu}.
 * <p>
 * This class is used to handle creation menus which
 * display four items on the interface.
 *
 * @author Professor Oak
 */
public class QuardrupleItemCreationMenu extends CreationMenu {

    /**
     * The items to display on the interface.
     */
    private final int firstItem, secondItem, thirdItem, fourthItem;

    public QuardrupleItemCreationMenu(Player player, int firstItem, int secondItem, int thirdItem, int fourthItem, String title, CreationMenuAction action) {
        super(player, title, action);
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        this.thirdItem = thirdItem;
        this.fourthItem = fourthItem;
    }

    @Override
    public CreationMenu open() {
        getPlayer().getPacketSender().sendString(8922, getTitle());

        getPlayer().getPacketSender().sendInterfaceModel(8902, firstItem, 170);
        getPlayer().getPacketSender().sendString(8909, "" + ItemDefinition.forId(firstItem).getName() + "");

        getPlayer().getPacketSender().sendInterfaceModel(8903, secondItem, 170);
        getPlayer().getPacketSender().sendString(8913, "" + ItemDefinition.forId(secondItem).getName() + "");

        getPlayer().getPacketSender().sendInterfaceModel(8904, thirdItem, 170);
        getPlayer().getPacketSender().sendString(8917, "" + ItemDefinition.forId(thirdItem).getName() + "");

        getPlayer().getPacketSender().sendInterfaceModel(8905, fourthItem, 170);
        getPlayer().getPacketSender().sendString(8921, "" + ItemDefinition.forId(fourthItem).getName() + "");

        getPlayer().getPacketSender().sendChatboxInterface(8899);
        return this;
    }

    @Override
    public boolean handleButton(int id) {
        switch (id) {
            case 8909:
                getAction().execute(0, firstItem, 1);
                return true;
            case 8908:
                getAction().execute(0, firstItem, 5);
                return true;
            case 8907:
                getAction().execute(0, firstItem, 10);
                return true;
            case 8906:
                getPlayer().setEnterSyntax(new CreationMenuX(0, firstItem));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                return true;
            case 8913:
                getAction().execute(0, secondItem, 1);
                return true;
            case 8912:
                getAction().execute(0, secondItem, 5);
                return true;
            case 8911:
                getAction().execute(0, secondItem, 10);
                return true;
            case 8910:
                getPlayer().setEnterSyntax(new CreationMenuX(1, secondItem));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                return true;
            case 8917:
                getAction().execute(0, thirdItem, 1);
                return true;
            case 8916:
                getAction().execute(0, thirdItem, 5);
                return true;
            case 8915:
                getAction().execute(0, thirdItem, 10);
                return true;
            case 8914:
                getPlayer().setEnterSyntax(new CreationMenuX(2, thirdItem));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                return true;
            case 8921:
                getAction().execute(0, fourthItem, 1);
                return true;
            case 8920:
                getAction().execute(0, fourthItem, 5);
                return true;
            case 8919:
                getAction().execute(0, fourthItem, 10);
                return true;
            case 8918:
                getPlayer().setEnterSyntax(new CreationMenuX(3, fourthItem));
                getPlayer().getPacketSender().sendEnterAmountPrompt("Enter amount:");
                return true;
        }
        return false;
    }

}
