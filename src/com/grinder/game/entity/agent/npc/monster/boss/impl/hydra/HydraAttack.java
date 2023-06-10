package com.grinder.game.entity.agent.npc.monster.boss.impl.hydra;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.movement.NPCMovementCoordinator;
import com.grinder.game.entity.agent.movement.task.MovementTask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.task.LightningTileTask;
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.task.PoisonedTileTask;
import com.grinder.game.model.*;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.Priority;
import com.grinder.util.area.Polygon;
import com.grinder.util.area.Rectangle;
import com.grinder.util.area.Shape;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-31
 */
public enum HydraAttack implements AttackProvider {

    RANGED(AttackType.RANGED, HydraState.values()) {

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(RANGED_PROJECTILE, RANGED_PROJECTILE_2);
        }
    },
    MAGIC(AttackType.MAGIC, HydraState.values()){

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(MAGIC_PROJECTILE, MAGIC_PROJECTILE_2);
        }
    },
    POISON(AttackType.SPECIAL, HydraState.POISON){
        @Override
        public void sequence(AlchemicalHydraBoss hydraBossNPC, Agent target) {
            sendPoisonProjectiles(hydraBossNPC, findRandomSurroundingTiles(hydraBossNPC.getMapInstance().getBasePosition(), target.getPosition(),  Misc.random(4, 6)));
        }
    },
    LIGHTNING(AttackType.SPECIAL, HydraState.LIGHTNING){
        @Override
        public void sequence(AlchemicalHydraBoss hydraBossNPC, Agent target) {

            final Position tile = hydraBossNPC.getLocalReferencePoint().transform(39, 14, 0);

            final Projectile projectile = new Projectile(hydraBossNPC.getPosition(), tile, SPECIAL_PROJECTILE);

            projectile.onArrival(() -> TaskManager.submit(new LightningTileTask(hydraBossNPC, tile)));
        }
    },
    FIREWALL(AttackType.SPECIAL, HydraState.FLAME){

        @Override
        public boolean canAttack(AlchemicalHydraBoss hydraBossNPC, Agent target) {
            return System.currentTimeMillis() - hydraBossNPC.wallDelta >= 25_000 && hydraBossNPC.transformDurationInGameTicks <= 0;
        }

        @Override
        public void sequence(AlchemicalHydraBoss hydraBossNPC, Agent target) {
             {
                hydraBossNPC.fireWallTicks = (10);
                final HashMap<Direction, Shape> sectionMap = new HashMap<>();
                final Position base = hydraBossNPC.getLocalReferencePoint();

                sectionMap.put(Direction.SOUTH_WEST, new Rectangle(base.transform(37, 18, 0), base.transform(27, 8, 0)));
                sectionMap.put(Direction.NORTH_WEST, new Rectangle(base.transform(36, 30, 0), base.transform(28, 21, 0)));
                sectionMap.put(Direction.NORTH_EAST, new Rectangle(base.transform(48, 29, 0), base.transform(40, 21, 0)));
                sectionMap.put(Direction.SOUTH_EAST, new Rectangle(base.transform(50, 18, 0), base.transform(40, 8, 0)));
                sectionMap.put(Direction.WEST, new Polygon(base.transform(28, 29, 0), base.transform(36, 21, 0), base.transform(36, 18, 0), base.transform(28, 12, 0)));
                sectionMap.put(Direction.NORTH, new Polygon(base.transform(39, 30, 0), base.transform(37, 22, 0), base.transform(40, 22, 0), base.transform(47, 30, 0)));
                sectionMap.put(Direction.EAST, new Polygon(base.transform(49, 29, 0), base.transform(41, 21, 0), base.transform(41, 18, 0), base.transform(49, 10, 0)));
                sectionMap.put(Direction.SOUTH, new Polygon(base.transform(49, 9, 0), base.transform(40, 17, 0), base.transform(37, 17, 0), base.transform(28, 9, 0)));

                final Optional<Map.Entry<Direction, Shape>> op = sectionMap.entrySet().stream().filter(entry -> entry.getValue().inside(target.getPosition())).findAny();

                if (op.isEmpty())
                    return;

                final Map.Entry<Direction, Shape> entry = op.get();

                final HydraFireWall hydraFireWall = new HydraFireWall(hydraBossNPC, entry.getKey());

                hydraFireWall.spawn(target);

                target.ifPlayer(player -> player.sendMessage("The Alchemical Hydra temporarily stuns you."));
            }
        }

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return new Animation(8248);
        }
    },
    SPIT(AttackType.SPECIAL, HydraState.ENRAGED){
        @Override
        public void sequence(AlchemicalHydraBoss hydraBossNPC, Agent target) {
            sendPoisonProjectiles(hydraBossNPC, findRandomSurroundingTiles(hydraBossNPC.getMapInstance().getBasePosition(), target.getPosition(),  Misc.random(1, 3)));
        }
    };

    private final AttackType type;
    private final HydraState[] states;

    HydraAttack(AttackType type, HydraState... states) {
        this.type = type;
        this.states = states;
    }

    public boolean isApplicable(AlchemicalHydraBoss hydraBossNPC){
        return Arrays.stream(states).anyMatch(hydraState -> hydraState == hydraBossNPC.getState());
    }

    public boolean canAttack(AlchemicalHydraBoss hydraBossNPC, Agent target){
        return true;
    }

    public void sequence(AlchemicalHydraBoss hydraBossNPC, Agent target){ }

    public AttackType getType() {
        return type;
    }

    @Override
    public int fetchAttackDuration(AttackType type) {
        return 6;
    }

    @Override
    public Animation getAttackAnimation(AttackType type) {
        return new Animation(8234);
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        return Stream.empty();
    }

    private static void sendPoisonProjectiles(AlchemicalHydraBoss hydraBossNPC, Position[] tiles) {
        int split = tiles.length / 2;
        int count = (2 * split) < tiles.length ? Misc.random(split, tiles.length - split) : split;

        for (final Position tile : tiles) {

            if (--count == 0) {

                TaskManager.submit(new Task(1) {
                    @Override
                    protected void execute() {
                        stop();
                        final Projectile projectile = new Projectile(hydraBossNPC.getCenterPosition(), tile, SPECIAL_PROJECTILE);

                        projectile.sendProjectile();
                        projectile.onArrival(() -> TaskManager.submit(new PoisonedTileTask(hydraBossNPC, projectile.getTarget())));
                    }
                });

            } else {

                final Projectile projectile = new Projectile(hydraBossNPC.getCenterPosition(), tile, SPECIAL_PROJECTILE);

                projectile.sendProjectile();
                projectile.onArrival(() -> TaskManager.submit(new PoisonedTileTask(hydraBossNPC, projectile.getTarget())));
            }
        }
    }
    private static Position[] findRandomSurroundingTiles(final Position mapBase, final Position targetPosition, int amount){

        final Position[] tiles = new Position[amount];

        int added = 0;

        int distanceToWallEast = mapBase.getX() + 41 - targetPosition.getX();
        int distanceToWallWest = targetPosition.getX() - mapBase.getX() - 20;
        int distanceToWallNorth = mapBase.getY() + 46 - targetPosition.getY();
        int distanceToWallSouth = targetPosition.getY() - mapBase.getY() - 25;

        do {

            final boolean invertX = Misc.randomChance(50.0F);
            final boolean invertY = Misc.randomChance(50.0F);

            int diffX, diffY;

            if(invertX){
                diffX = Misc.getRandomInclusive(Math.min(7, distanceToWallEast));
            } else
                diffX = -Misc.getRandomInclusive(Math.min(7, distanceToWallWest));

            if(invertY){
                diffY = -Misc.getRandomInclusive(Math.min(7, distanceToWallSouth));
            } else
                diffY = Misc.getRandomInclusive(Math.min(7, distanceToWallNorth));


            tiles[added] = targetPosition.copy().transform(diffX, diffY, 0);

            added++;

        } while (added != amount);
        return tiles;
    }


    private final static ProjectileTemplate RANGED_PROJECTILE = ProjectileTemplate.builder(1663)
        .setSourceSize(6)
        .setStartHeight(70)
        .setEndHeight(34)
        .setCurve(8)
        .setSpeed(20)
        .setDelay(3)
        .build();

    private final static ProjectileTemplate RANGED_PROJECTILE_2 = ProjectileTemplate.builder(1663)
            .setSourceSize(3)
            .setStartHeight(70)
            .setEndHeight(34)
            .setCurve(8)
            .setSpeed(20)
            .setDelay(10)
            .build();

    private final static ProjectileTemplate MAGIC_PROJECTILE = ProjectileTemplate.builder(1662)
            .setSourceSize(6)
            .setStartHeight(70)
            .setEndHeight(15)
            .setSpeed(20)
            .setDelay(3)
            .build();

    private final static ProjectileTemplate MAGIC_PROJECTILE_2 = ProjectileTemplate.builder(1662)
            .setSourceSize(6)
            .setStartHeight(70)
            .setEndHeight(15)
            .setSpeed(20)
            .setDelay(10)
            .build();

    private final static ProjectileTemplate SPECIAL_PROJECTILE = ProjectileTemplate.builder(1644)
            .setSourceSize(1)
            .setStartHeight(70)
            .setEndHeight(5)
            .setCurve(10)
            .setSpeed(28)
            .setDelay(3)
            .build();

}
