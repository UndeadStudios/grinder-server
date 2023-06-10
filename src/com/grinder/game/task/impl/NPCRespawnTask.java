package com.grinder.game.task.impl;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.task.Task;

/**
 * A {@link Task} implementation which handles the respawn of an npc.
 *
 * @author Professor Oak
 */
public class NPCRespawnTask extends Task {

    /**
     * The {@link NPC} which is going to respawn.
     */
    private final NPC npc;
    private final int tickCount;
    private int ticks = 0;

    public NPCRespawnTask(NPC npc, int tickCount) {
        super(1);
        bind(npc);
        this.npc = npc;
        this.tickCount = tickCount;
    }

    @Override
    public void execute() {

        if(npc instanceof Monster){
            if(((Monster) npc).skipRespawnSequence()){
                return;
            }
        }

        if(++ticks >= tickCount){
            npc.respawn();
            stop();
        }
    }
}
