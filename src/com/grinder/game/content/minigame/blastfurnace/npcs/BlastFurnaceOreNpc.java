package com.grinder.game.content.minigame.blastfurnace.npcs;

import com.grinder.game.World;
import com.grinder.game.content.minigame.blastfurnace.BlastFurnace;
import com.grinder.game.content.minigame.blastfurnace.BlastFurnaceOre;
import com.grinder.game.content.minigame.blastfurnace.dispenser.BarDispenser;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.NpcID;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 8:36 AM
 * @discord L E G E N D#4380
 */
public class BlastFurnaceOreNpc extends NPC {

    private static final Position ORE_STARTING_POSITION = new Position(1942, 4966, 0);
    private static final Position ORE_END_POSITION = new Position(1942, 4963, 0);

    private Task task;
    protected BlastFurnaceOre ore;

    public BlastFurnaceOreNpc(int id, Position position) {
        super(id, position);
    }

    public void start() {
        if (task != null) {
            return;
        }
        TaskManager.submit(1, () -> TaskManager.submit(task = new Task(1, this,false) {
            @Override
            protected void execute() {
                if (!isActive()) {
                    start();
                    return;
                }
                getMotion().enqueuePathToWithoutCollisionChecks(ORE_END_POSITION.getX(), ORE_END_POSITION.getY());
                if (getPosition().equals(ORE_END_POSITION)) {
                    onArrival();
                    BlastFurnaceOreNpc.this.stop();
                }
            }
        }));
    }

    public void stop() {
        if (task != null) {
            task.stop();
            task = null;
        }
    }

    protected void setup(Player player, BlastFurnaceOre ore) {
        setOwner(player);
        this.ore = ore;
    }

    public void onArrival() {
        performAnimation(new Animation(2434));
        TaskManager.submit(1, () -> {
            getOwner().getBlastFurnace().sendToBelt(ore);
            BlastFurnace.removeNpc(getOwner(), ore);
            BarDispenser.melt(getOwner());
        });

    }

    public BlastFurnaceOre getOre() {
        return ore;
    }

    public static BlastFurnaceOreNpc create(BlastFurnaceOre ore, Player player) {
        var npc = new BlastFurnaceOreNpc(getNpcId(ore), ORE_STARTING_POSITION);
        npc.setup(player, ore);
        World.getNpcAddQueue().add(npc);
        return npc;
    }

    public static int getNpcId(BlastFurnaceOre ore) {
        switch (ore) {
            case TIN:
                return NpcID.TIN_ORE;
            case COPPER:
                return NpcID.COPPER_ORE;
            case IRON:
                return NpcID.IRON_ORE;
            case SILVER:
                return NpcID.SILVER_ORE;
            case COAL:
                return NpcID.COAL;
            case GOLD:
                return NpcID.GOLD_ORE;
            case MITHRIL:
                return NpcID.MITHRIL_ORE;
            case ADAMANTITE:
                return NpcID.ADAMANTITE_ORE;
            case RUNITE:
                return NpcID.RUNITE_ORE;
        }
        return -1;
    }
}
