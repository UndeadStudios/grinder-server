package com.grinder.game.content.object;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.item.Item;
import com.grinder.net.packet.interaction.PacketInteraction;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class WindMill extends PacketInteraction {
    private static final Item GRAIN = new Item(1947);
    private static final Item POT = new Item(1931);
    private static final Item POT_OF_FLOUR = new Item(1933);

    private static final WindMill LUMBRIDGE_WIND_MILL = new WindMill();

    private int grain;

    public void deposit(Player p) {
        int amount = p.getInventory().getAmount(GRAIN);

        if (amount <= 0) {
            p.sendMessage("You need to have grain in order to load it into the hopper.");
            return;
        }

        p.sendMessage("You load the hopper with grain..");

        p.getInventory().delete(GRAIN.getId(), 28);

        grain += amount;
    }

    public void collect(Player p) {
        if(grain == 0) {
            p.sendMessage("There is no grain to collect.");
            return;
        }

        int amount = p.getInventory().getAmount(POT);

        if (amount == 0) {
            p.sendMessage("You don't have any empty pots to collect flour.");
            return;
        }

        if (amount > grain) {
            amount = grain;
        }

        p.getInventory().delete(POT.getId(), amount);

        p.getInventory().add(POT_OF_FLOUR.getId(), amount);

        p.sendMessage("You collect the wheat into your pot..");
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 1781:
                LUMBRIDGE_WIND_MILL.collect(player);
                return true;
            case 24961:
                LUMBRIDGE_WIND_MILL.deposit(player);
                return true;
        }
        return false;
    }
}
