package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class SpawnNPCCommand implements Command {

    @Override
    public String getSyntax() {
        return "[npcId]";
    }

    @Override
    public String getDescription() {
        return "Spawns an NPC in your current position.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        NPC npc = NPCFactory.INSTANCE.create(Integer.parseInt(parts[1]), player.getPosition().clone().add(1, 0));
        npc.getMotion().update(MovementStatus.DISABLED);
        World.getNpcAddQueue().add(npc);

        //npc.setHitpoints(1000);
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
