package com.grinder.game.model.ui;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;

import java.util.List;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public abstract class UserContainerInterface {

    public int id;

    public int inventory;

    public List<Item> items;

    public UserContainerInterface(int id, int inventory) {
        this.id = id;
        this.inventory = inventory;
        UserInterfaceManager.CONTAINER.put(id, this);
    }

    public UserContainerInterface(int id) {
        this(id, -1);
    }

    public void update(Player player, List<Item> items) {
        this.items = items;
        if (inventory != -1) {
            player.getPacketSender().sendItemContainer(player.getInventory(), inventory);
        } else {
            player.getInventory().refreshItems();
        }
        player.getPacketSender().sendItemContainer(items, id);
    }

    public abstract boolean handleOption(Player player, int id, int slot, int option);

    public int getAmount(int option) {
        switch (option) {
            case 1:
                return 1;
            case 2:
                return 5;
            case 3:
                return 10;
            case 4:
                return 4;
            case 5:
                return Integer.MAX_VALUE;
        }
        return 0;
    }
}
