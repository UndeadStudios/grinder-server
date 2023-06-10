package com.grinder.game.content.minigame.barrows;


import com.grinder.game.World;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.task.Task;

public class BarrowsTask extends Task {

    private final NPC npc;

    private final Player player;

    private int time;

    BarrowsTask(NPC npc, Player player) {
        this.npc = npc;
        this.player = player;
    }

    @Override
    protected void execute() {
        time++;
        if(player == null) {
            World.getNpcRemoveQueue().add(npc);
            this.stop();
            return;
        }
        if (!player.isRegistered()) {
            World.getNpcRemoveQueue().add(npc);
            this.stop();
            return;
        }
        if (npc.isDying()) {
            BarrowsBrother.getByNpcId(npc.getId()).ifPresent(barrowsBrother -> player.getKilledBarrows().add(barrowsBrother));
            player.getBarrowsManager().updateInterface();
            player.getPacketSender().sendEntityHintRemoval(true);
            player.setSpawnedBarrows(false);
            this.stop();
            return;
        }
        if (time >= 150) {
            World.getNpcRemoveQueue().add(npc);
            player.getPacketSender().sendEntityHintRemoval(true);
            player.setSpawnedBarrows(false);
            this.stop();
            return;
        }
        if (player.getPosition().getZ() != npc.getPosition().getZ()) {
            World.getNpcRemoveQueue().add(npc);
            player.getPacketSender().sendEntityHintRemoval(true);
            player.setSpawnedBarrows(false);
            this.stop();
            return;
        }
        if (player.getPosition().getDistance(npc.getPosition()) >= 60) {
            World.getNpcRemoveQueue().add(npc);
            player.getPacketSender().sendEntityHintRemoval(true);
            player.setSpawnedBarrows(false);
            this.stop();
            return;
        }

    }

    public NPC getNpc() {
        return npc;
    }

    public Player getPlayer() {
        return player;
    }
}
