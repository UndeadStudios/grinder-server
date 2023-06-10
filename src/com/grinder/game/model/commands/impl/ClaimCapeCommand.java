package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.content.GameMode;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

public class ClaimCapeCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Realism/One Life Game mode cape claim.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (EntityExtKt.getBoolean(player, Attribute.CLAIMED_GAMEMODE_CAPE, false)) {

            if (player.getInventory().countFreeSlots() <= 0) {
                player.sendMessage("Your inventory is currently full");
                return;
            }
            player.getInventory().add(new Item(ItemID.DEADMANS_CAPE));
            EntityExtKt.setBoolean(player, Attribute.CLAIMED_GAMEMODE_CAPE, true, false);
            player.sendMessage("@red@You have claimed your missing cape.");
        } else {
            player.sendMessage("You have already claimed your cape.");
        }

    }

    @Override
    public boolean canUse(Player player) {
        GameMode gameMode = player.getGameMode();
        return (gameMode == GameMode.REALISM || gameMode == GameMode.ONE_LIFE);
    }
}
