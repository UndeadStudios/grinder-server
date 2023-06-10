package com.grinder.game.entity.agent.npc.monster.boss.impl.hydra;

import com.grinder.game.World;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.ForceMovement;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.areas.InstanceManager;
import com.grinder.game.model.areas.MapBuilder;
import com.grinder.game.model.areas.MapInstance;
import com.grinder.game.model.areas.MapInstancedBossArea;
import com.grinder.game.model.areas.instanced.HydraArea;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.ForceMovementTask;

import java.util.concurrent.TimeUnit;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-06-01
 */
public class AlchemicalHydraInstance {

    public static final Position LOCAL_REFERENCE_POINT = new Position(1336, 10232, 0);

    public static void climbRocks(Player player) {
        if (player.busy()) return;
        player.getMotion().clearSteps();
        if (player.getForceMovement() == null) {
            final boolean enteringInstance = player.getPosition().getY() == 10250;

            final Position crossDitch = new Position(0, enteringInstance ? 2 : -2);
            new ForceMovementTask(player, 3, new ForceMovement(player.getPosition().clone(),
                            crossDitch, 0, 70, crossDitch.getY() == 2 ? 0 : 2, 6132));

            if (enteringInstance) {
                MapInstance mapInstance = InstanceManager.getOrCreate(player, InstanceManager.SinglePlayerMapType.HYDRA);

                final HydraArea area = mapInstance.getArea() != null ? (HydraArea) mapInstance.getArea() : new HydraArea(mapInstance);

                if (!mapInstance.isMapBuilt()) {
                    RegionCoordinates start = LOCAL_REFERENCE_POINT.getRegionCoordinates();
                    mapInstance.copyPlane(start, 0, 0, 0, 0, MapBuilder.ChunkSizes.EIGHT, 0);
                }

                TaskManager.submit(3, () -> {
                    player.setForceMovement(null);

                    Position base = LOCAL_REFERENCE_POINT;
                    Position offset = player.getPosition().getDelta(base);
                    player.moveTo(mapInstance.getBasePosition().transform(offset.getX(), offset.getY()+2, 0));

                    start(player, mapInstance);
                });
            } else {
                TaskManager.submit(3, () -> {
                    player.setForceMovement(null);
                    if (player.getMapInstance() == null) {
                        player.moveTo(Teleporting.TeleportLocation.EDGEVILLE.getPosition());
                        return;
                    }
                    Position base = player.getMapInstance().getBasePosition();
                    Position offset = player.getPosition().getDelta(base);
                    player.moveTo(LOCAL_REFERENCE_POINT.transform(offset.getX(), offset.getY()-2, 0));
                });
            }
            player.getPacketSender().sendSound(Sounds.DITCH_JUMP);
        }
    }

    public static void start(final Player player, MapInstance mapInstance){

        final Position hydraPosition = mapInstance.getBasePosition().transform(36-8, 17+16, 0);
        final AlchemicalHydraBoss hydra = new AlchemicalHydraBoss(hydraPosition);
        hydra.setLocalReferencePoint(mapInstance.getBasePosition().transform(-8, 16, 0));
        hydra.setOwner(player);
        World.getNpcAddQueue().add(hydra);

    }

}
