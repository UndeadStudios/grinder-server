package com.grinder.game.content.minigame.chamberoxeric.room.vanguard;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.COXRoom;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class VanguardCOXRoom extends COXRoom {

    private static final int MELEE_VANGUARD = 7527;

    private static final int RANGE_VANGUARD = 7528;

    private static final int MAGIC_VANGUARD = 7529;

    private static final int SHUFFLE_TIME = 10;

    private static final Position[] SPAWNS = {
            new Position(3343, 5258),
            new Position(3341, 5262),
            new Position(3348, 5262),
    };

    private Player p;

    public VanguardCOXRoom(Player p) {
        this.p = p;
        init();
    }

    private void shuffle(Player p, NPC vanguard, int index) {
        Task task = new Task(SHUFFLE_TIME) {

            int slot = index;

            @Override
            protected void execute() {
                if (!vanguard.isActive() || !vanguard.isAlive()) {
                    stop();
                    return;
                }

                if (slot >= SPAWNS.length) {
                    slot = 0;
                }

                vanguard.getCombat().reset(true);
                vanguard.getMotion().clearSteps();
                vanguard.getMotion().traceTo(SPAWNS[slot]);

                slot++;
            }
        };

        p.instance.addTask(task);

        TaskManager.submit(task);
    }

    @Override
    public void init() {
        int height = p.getPosition().getZ();

        Position pos = SPAWNS[0].clone().transform(0, 0, height);

        NPC meleeVanguard = NPCFactory.INSTANCE.create(MELEE_VANGUARD, pos);

        World.getNpcAddQueue().add(meleeVanguard);

        p.instance.addAgent(meleeVanguard);

        shuffle(p, meleeVanguard, 0);

        pos = SPAWNS[1].clone().transform(0, 0, height);

        NPC magicVanguard = NPCFactory.INSTANCE.create(MAGIC_VANGUARD, pos);

        World.getNpcAddQueue().add(magicVanguard);

        p.instance.addAgent(magicVanguard);

        shuffle(p, magicVanguard, 1);

        pos = SPAWNS[2].clone().transform(0, 0, height);

        NPC rangeVanguard = NPCFactory.INSTANCE.create(RANGE_VANGUARD, pos);

        World.getNpcAddQueue().add(rangeVanguard);

        p.instance.addAgent(rangeVanguard);

        shuffle(p, rangeVanguard, 2);

        Task task = new Task(SHUFFLE_TIME) {
            @Override
            protected void execute() {
                if (meleeVanguard.getHitpoints() < rangeVanguard.getHitpoints() - 50
                        || meleeVanguard.getHitpoints() < magicVanguard.getHitpoints() - 50) {
                    healVanguard(meleeVanguard, rangeVanguard, magicVanguard);
                }

                if (rangeVanguard.getHitpoints() < meleeVanguard.getHitpoints() - 50
                        || rangeVanguard.getHitpoints() < magicVanguard.getHitpoints() - 50) {
                    healVanguard(meleeVanguard, rangeVanguard, magicVanguard);
                }

                if (magicVanguard.getHitpoints() < meleeVanguard.getHitpoints() - 50
                        || magicVanguard.getHitpoints() < rangeVanguard.getHitpoints() - 50) {
                    healVanguard(meleeVanguard, rangeVanguard, magicVanguard);
                }
            }
        };

        p.instance.addTask(task);

        TaskManager.submit(task);
    }

    private void healVanguard(NPC meleeVanguard, NPC rangeVanguard, NPC magicVanguard) {
        meleeVanguard.setHitpoints(meleeVanguard.getMaxHitpoints());
        rangeVanguard.setHitpoints(rangeVanguard.getMaxHitpoints());
        magicVanguard.setHitpoints(meleeVanguard.getMaxHitpoints());
    }
}
