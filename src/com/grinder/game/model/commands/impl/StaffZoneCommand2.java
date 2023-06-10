package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.util.Misc;

import static com.grinder.game.entity.agent.player.PlayerRights.*;

public class StaffZoneCommand2 implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Teleports you to the staff's zone.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (PlayerUtil.isDeveloper(player)) {
            TeleportHandler.teleportNoReq(player, new Position(1866, 5322 + Misc.getRandomInclusive(2), 0), TeleportType.FAIRY_RING, false, false);
        } else {
            TeleportHandler.teleport(player, new Position(1866, 5322 + Misc.getRandomInclusive(2), 0), TeleportType.NORMAL, false, true);
        }
        player.getPacketSender().sendJinglebitMusic(273, 0);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER)
                || EntityExtKt.getBoolean(player, Attribute.MIDDLEMAN, false) || PlayerUtil.isStaff(player);
    }

}
