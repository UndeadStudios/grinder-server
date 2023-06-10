package com.grinder.game.entity.agent.npc.monster.boss.impl.hydra;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.task.FireTileTask;
import com.grinder.game.model.*;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * TODO: add documentation
 *
 * @author Blake
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-31
 */
public class HydraFireWall {

    private final AlchemicalHydraBoss bossNPC;
    private final Direction direction;

    HydraFireWall(final AlchemicalHydraBoss bossNPC, Direction direction) {
        this.bossNPC = bossNPC;
        this.direction = direction;
    }

    public void spawn(final Agent target){

        bossNPC.wallDelta = (System.currentTimeMillis());
        bossNPC.fireWallTicks = (10);

        final Position initialBase = getFirstBase();
        final Position[] nextTiles = getFireShift(initialBase, true);

        if (nextTiles == null)
            return;

        bossNPC.say(direction.name());
        bossNPC.setPositionToFace(bossNPC.getPosition().clone().move(direction));

        for(final Position next : nextTiles) {

            final Projectile projectile = new Projectile(bossNPC.getPosition(), next, FIRE_PROJECTILE);

            projectile.sendProjectile();

            projectile.onArrival(() -> createFireWall(next, true, bossNPC));
        }


        TaskManager.submit(new Task(3) {
            @Override
            protected void execute() {

                stop();

                bossNPC.setPositionToFace(getSecondDirection());
                bossNPC.performAnimation(HydraAttack.FIREWALL.getAttackAnimation(AttackType.SPECIAL));

                final Position nextBase = getSecondBase();
                final Position[] nextTiles = getFireShift(nextBase, false);

                if (nextTiles != null) {
                    for (final Position next : nextTiles) {

                        final Projectile projectile = new Projectile(bossNPC.getPosition(), next, FIRE_PROJECTILE);

                        projectile.sendProjectile();

                        projectile.onArrival(() -> createFireWall(next, false, bossNPC));
                    }
                }
                TaskManager.submit(new Task(3) {
                    @Override
                    protected void execute() {

                        stop();

                        bossNPC.performAnimation(HydraAttack.FIREWALL.getAttackAnimation(AttackType.SPECIAL));
                        bossNPC.setPositionToFace(target.getPosition());

                        if (!bossNPC.getCombat().isAttacking())
                            bossNPC.getCombat().target(target);

                        final Projectile projectile = new Projectile(bossNPC, target, FIRE_PROJECTILE);

                        projectile.sendProjectile();
                    }
                });
            }
        });
    }

    private void createFireWall(Position to, boolean first, AlchemicalHydraBoss hydra) {

        TaskManager.submit(new Task(1) {
            int count = 0;
            @Override
            public void execute() {

                final Position position = to.transform(first ? count * getFirstShift()[0] : count * getSecondShift()[0], first ? count * getFirstShift()[1] : count * getSecondShift()[1], 0);

                if (!hydra.isAlive() || !hydra.isActive() || hydra.isDying()) {
                    stop();
                    return;
                }

                TaskManager.submit(new FireTileTask(hydra, position));

                count++;

                if (count == 8) {
                    stop();
                }
            }
        });
    }


    private int[] getFirstShift() {
        switch (direction){
            default:
            case NONE:
                return null;
            case NORTH:
            case WEST:
                return new int[] { -1, 1 };
            case NORTH_EAST:
            case NORTH_WEST:
                return new int[] { 0, 1 };
            case EAST:
            case SOUTH:
                return new int[] { 1, -1 };
            case SOUTH_EAST:
                return new int[] { 0, -1 };
            case SOUTH_WEST:
                return new int[] { -1, 0 };
        }
    }

    private int[] getSecondShift() {
        switch (direction){
            default:
            case NONE:
                return null;
            case NORTH:
            case EAST:
                return new int[] { 1, 1 };
            case NORTH_EAST:
            case SOUTH_EAST:
                return new int[] { 1, 0 };
            case SOUTH:
            case WEST:
                return new int[] { -1, -1 };
            case SOUTH_WEST:
                return new int[] { 0, -1 };
            case NORTH_WEST:
                return new int[] { -1, 0 };
        }
    }

    private Position getFirstBase() {
        final Position base = bossNPC.getLocalReferencePoint();
        switch (direction){
            default:
            case NONE:
                return null;
            case NORTH_EAST:
                return base.transform(37, 23, 0);
            case EAST:
            case SOUTH:
                return base.transform(42, 16, 0);
            case SOUTH_WEST:
                return base.transform(35, 18, 0);
            case SOUTH_EAST:
                return base.transform(40, 16, 0);
            case WEST:
            case NORTH:
                return base.transform(35, 23, 0);
            case NORTH_WEST:
                return base.transform(38, 29, 0);
        }
    }

    private Position getSecondBase() {
        final Position base = bossNPC.getLocalReferencePoint();
        switch (direction){
            default:
            case NONE:
                return null;
            case NORTH:
            case EAST:
                return base.transform(42, 23, 0);
            case NORTH_EAST:
            case SOUTH_EAST:
                return base.transform(42, 18, 0);
            case SOUTH:
            case WEST:
                return base.transform(35, 16, 0);
            case SOUTH_WEST:
                return base.transform(37, 16, 0);
            case NORTH_WEST:
                return base.transform(35, 18, 0);
        }
    }

    private Position getSecondDirection() {

        final Position base = bossNPC.getLocalReferencePoint();
        switch (direction){
            default:
            case NONE:
                return null;
            case NORTH:
            case EAST:
                return base.transform(42, 23, 0);
            case NORTH_EAST:
            case SOUTH_EAST:
                return base.transform(49, 18, 0);
            case SOUTH:
            case WEST:
                return base.transform(35, 16, 0);
            case SOUTH_WEST:
                return base.transform(37, 9, 0);
            case NORTH_WEST:
                return base.transform(29, 18, 0);
        }
    }

    private Position[] getFireShift(Position to, boolean first) {
        switch (direction){
            default:
            case NONE:
                return null;
            case NORTH:
                return new Position[] { to,
                        to.transform(first ? 0 : -1, first ? -1 : 0, 0),
                        to.transform(first ? 0 : -2, first ? -2 : 0, 0),
                        to.transform(first ? 1 : 0, first ? 0 : -1,0),
                        to.transform(first ? 2 : 0, first ?0 : -2, 0)
                };
            case NORTH_WEST:
            case NORTH_EAST:
                return new Position[] { to,
                        to.transform(first ? 1 : 0, first ? 0 : 1, 0),
                        to.transform(first ? 2 : 0, first ? 0 : 2, 0),
                        to.transform(first ? 3 : 0, first ? 0 : 3, 0)
                };
            case EAST:
                return new Position[] { to,
                        to.transform(first ? -1 : -1, first ? 0 : 0, 0),
                        to.transform(first ? -2 : -2, first ? 0 : 0, 0),
                        to.transform(0, first ? 1 : -1,0),
                        to.transform(0, first ?2 : -2, 0)
                };
            case SOUTH_EAST:
                return new Position[] { to,
                        to.transform(first ? -1 : 0, first ? 0 : 1, 0),
                        to.transform(first ? -2 : 0, first ? 0 : 2, 0),
                        to.transform(first ? -3 : 0, first ? 0 : 3, 0)
                };
            case SOUTH:
                return new Position[] { to,
                        to.transform(first ? -1 : 0, first ? 0 : 1, 0),
                        to.transform(first ? -2 : 0, first ? 0 : 2, 0),
                        to.transform(first ? 0 : 1, first ? 1 : 0,0),
                        to.transform(first ? 0 : 2, first ?2 : 0, 0)
                };
            case SOUTH_WEST:
                return new Position[] { to,
                        to.transform(first ? 0 : 1, first ? 1 : 0,0),
                        to.transform(first ? 0 : 2, first ? 2 : 0,0),
                        to.transform(first ? 0 : 3, first ? 3 : 0,0)
                };
            case WEST:
                return new Position[] { to, to.transform(0, first ? -1 : 1, 0),
                        to.transform(0, first ? -2 : 2, 0),
                        to.transform(1,0,0),
                        to.transform(2, 0, 0) };
        }
    }

    private final static ProjectileTemplate FIRE_PROJECTILE = ProjectileTemplate.builder(1667)
            .setSourceSize(5)
            .setStartHeight(70)
            .setEndHeight(20)
            .setSpeed(44)
            .setDelay(3)
            .build();

}
