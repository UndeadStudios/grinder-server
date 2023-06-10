package com.grinder.game.model.areas.instanced;

import com.grinder.game.content.miscellaneous.TravelSystem;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahBoss;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ObjectActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.InstanceManager;
import com.grinder.game.model.areas.MapBuilder;
import com.grinder.game.model.areas.MapInstance;
import com.grinder.game.model.areas.MapInstancedBossArea;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ObjectID;
import com.grinder.util.timing.TimerKey;

public class ZulrahShrine extends MapInstancedBossArea {

    private ZulrahShrine(MapInstance mapInstance) {
        super(mapInstance);
    }

    @Override
    public boolean isSafeForHardcore() {
        return false;
    }

    @Override
    public boolean hasLargeViewPort() {
        return false;
    }

    static {
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.SACRIFICIAL_BOAT}, clickAction -> {
            final Player player = clickAction.getPlayer();
            player.setPositionToFace(clickAction.getObjectActionMessage().getGameObject().getCenterPosition());
            new DialogueBuilder(DialogueType.OPTION)
                    .setOptionTitle("Return to Zulrah's shrine?")
                    .firstOption("Yes.", ZulrahShrine::handleBoatFairing)
                    .addCancel("No.")
                    .start(player);
            return true;
        });
    }

    public static final Position MAP_BASE = new Position(2240, 3049);

    /**
     * Position offsets based on the base of the map
     */
    public static final Position PLAYER_TELE = new Position(2268 - MAP_BASE.getX(), 3070 - MAP_BASE.getY());
    public static final Position ZULRAH_SPAWN = new Position(2266 - MAP_BASE.getX(), 3074 - MAP_BASE.getY());

    public static void handleBoatFairing(Player player) {
        if (player.getTimerRepository().has(TimerKey.BUTTON_DELAY))
            return;

        player.getTimerRepository().replaceIfLongerOrRegister(TimerKey.BUTTON_DELAY, 1);

        if (player.getArea() != null) {
            if (player.getArea() instanceof ZulrahShrine) {
                return;
            }
        }

        player.getPacketSender().sendInterfaceRemoval();

        final MapInstance mapInstance = InstanceManager.getOrCreate(player, com.grinder.game.model.areas.InstanceManager.SinglePlayerMapType.ZULRAH);
        final Position base = mapInstance.getBasePosition();

        if(!TravelSystem.INSTANCE.fadeTravelAction(player, true, true, "Welcome to Zulrah's shrine", 2, 5, 4, () -> {
            final Position cameraFacePosition = player.getPosition().clone().add(1, -5);
            final int localX = player.getPosition().getLocalX(cameraFacePosition);
            final int localY = player.getPosition().getLocalY(cameraFacePosition);
            player.getPacketSender().sendCameraAngle(localX, localY, 500, 1, 120);
            return null;
        })) return;

        final ZulrahShrine area = mapInstance.getArea() != null ? (ZulrahShrine) mapInstance.getArea() : new ZulrahShrine(mapInstance);

        if (!mapInstance.isMapBuilt()) {
            mapInstance.copyPlane(MAP_BASE.getRegionCoordinates(), 0, 0, 0, 0, MapBuilder.ChunkSizes.EIGHT, 0);
        }

        TaskManager.submit(3, () -> {
            player.getPacketSender().sendCameraNeutrality();
            if (mapInstance.isMapBuilt()) {
                player.moveTo(base.transform(PLAYER_TELE.getX(), PLAYER_TELE.getY(), 0));
            }
        });
        TaskManager.submit(8, () -> {
            if (mapInstance.isMapBuilt())
                area.spawnBoss(player, mapInstance);
            else
                player.sendMessage("Failed building a zulrah map. Report this message to developers.");
        });
        TaskManager.submit(12, () -> player.getPacketSender().sendCameraNeutrality());
    }

    public void spawnBoss(Player player, MapInstance mapInstance) {
        Position base = mapInstance.getBasePosition();
        final ZulrahBoss zulrahBoss = new ZulrahBoss(base.transform(ZULRAH_SPAWN.getX(), ZULRAH_SPAWN.getY(), 0), mapInstance.getBasePosition()/*new Position(2266, 3073, height)*/);

        zulrahBoss.performAnimation(new Animation(5071));
        zulrahBoss.getMotion().update(MovementStatus.DISABLED);
        zulrahBoss.setOwner(player);
        zulrahBoss.spawn();
        add(zulrahBoss);
    }

    @Override
    public boolean isCannonProhibited() {
        return true;
    }
}