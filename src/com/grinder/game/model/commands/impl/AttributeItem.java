package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.item.AttributableItem;
import com.grinder.game.model.item.AttributeKey;
import com.grinder.game.model.item.Item;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AttributeItem implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Item Attribute testing for DFS.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        AttributableItem item = new AttributableItem(11284, 1);
        AttributeKey charges = new AttributeKey("charges");
        item.setAttribute(charges, 100);
        if(parts.length > 1) {
            player.getInventory().add(item);
        }

        Optional<Item> first = Arrays.stream(player.getInventory().getItems())
                .filter(Objects::nonNull)
                .filter(Item::hasAttributes)
                .findFirst();

        player.getPacketSender().sendMessage("Spawned it");
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || player.getUsername().equals("Stan");
    }
}
