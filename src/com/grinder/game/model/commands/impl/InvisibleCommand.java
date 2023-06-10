package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;

public class InvisibleCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Turns your account on/off invisible mode.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        EntityExtKt.setBoolean(player, Attribute.INVISIBLE, !EntityExtKt.getBoolean(player, Attribute.INVISIBLE, false), false);
		player.sendMessage(EntityExtKt.getBoolean(player, Attribute.INVISIBLE, false) ? "@gre@You're now invisisble." : "@red@You are no longer invisible.");
		player.getRelations().setPrivateStatus(EntityExtKt.getBoolean(player, Attribute.INVISIBLE, false) ? 2 : 0, true);
		player.updateAppearance();
		return;
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
