package com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.task;

import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekConstants;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.TaskManager;

/**
 * Send out 8 fire traps based on the boss' position.
 *
 * @author Pea2nuts <ethan@pea2nuts.ru>
 */
public class GalvekFireBalls {

    private final GalvekBoss bossNPC;

    public GalvekFireBalls(final GalvekBoss bossNPC) {
        this.bossNPC = bossNPC;
    }

    private static final int[][] FIRE_TRAP_OFFSETS = {
            {0, -3}, // Nearest to Galvek
            {6, -3},

            {1, -7},
            {5, -7},

            {0, -11},
            {6, -11},

            {1, -15}, // Furthest from Galvek
            {5, -15},
    };

    /**
     * Get the positions of the fire traps based on an initial position
     *
     * @param initialBase Usually the position of the boss, the base position
     * @return
     */
    private Position[] getTrapPositions(Position initialBase) {
        Position[] spots = new Position[8];
        for(int i = 0; i < spots.length; i++) {
            spots[i] = initialBase.transform(FIRE_TRAP_OFFSETS[i][0], FIRE_TRAP_OFFSETS[i][1], 0);
        }
        return spots;

    }

    /**
     * Sends out galvek's fire ball trap attack
     *
     */
    public void spawnTraps() {

        // Grab the trap positions based on galvek's position
        final Position[] tiles = getTrapPositions(bossNPC.getPosition());

        if (tiles == null)
            return;

        // Send out a projectile for each trap and spawn the trap on the tile on arrival
        for(final Position tile : tiles) {
            final Projectile projectile = new Projectile(bossNPC.getPosition(), tile, GalvekConstants.FIRE_TRAP_ATTACK);
            projectile.sendProjectile();
            projectile.onArrival(() -> createFireTrapTile(tile, bossNPC));
        }

    }

    /**
     *
     * @param tile The tile to spawn the fire trap at
     * @param galvek The boss instance
     */
    private void createFireTrapTile(Position tile, GalvekBoss galvek) {
        TaskManager.submit(new FireTrapTileTask(galvek, tile));
    }

}
