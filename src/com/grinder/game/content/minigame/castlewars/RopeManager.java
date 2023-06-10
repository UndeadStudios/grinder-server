package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.TaskManager;

import java.util.HashMap;
import java.util.Map;

public class RopeManager {
    private static Map<Position, Battlement> battlementsMap = new HashMap<Position, Battlement>();

    private static void addBattlement(Battlement battlement) {
        battlementsMap.put(new Position(battlement.wallX, battlement.wallY, 0), battlement);
    }

    public static void removeAllRopes() {
        for (Battlement battlement : battlementsMap.values()) {
            battlement.removeRope();
        }
    }

    static {
        loadBattlements();
    }

    public static void throwRope(Player player, int objectID, int itemSlot, int objectX, int objectY) {
        if (player.getCurrentParty() == null) {
            return;
        }
        if (player.getInventory().get(itemSlot).getId() == CastleWarsConstants.ITEM_ROPE) {
            Battlement battlement = battlementsMap.get(new Position(objectX, objectY, 0));

            if (battlement == null) {
                return;
            }
            if (battlement.isScalable()) {
                player.sendMessage("There's already a climbing rope here.");
                return;
            }

            if (battlement.getOffsetX() != 0) {
                if (player.getPosition().getX() != battlement.wallX) {
                    player.sendMessage("You can't use rope from here");
                    return;
                }
            }
            if (battlement.getOffsetY() != 0) {
                if (player.getPosition().getY() != battlement.wallY) {
                    player.sendMessage("You can't use rope from here");
                    return;
                }
            }
            player.getInventory().delete(new Item(CastleWarsConstants.ITEM_ROPE, 1), itemSlot);
            battlement.attachRope();
        }
    }

    public static class Battlement {

        private Battlement(int wallX, int wallY, int wallRotation, int offsetX, int offsetY, int wallID, int scalableWallID) {
            this.wallID = wallID;
            this.scalableWallID = scalableWallID;
            this.wallX = wallX;
            this.wallY = wallY;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.wallRotation = wallRotation;
        }

        private final int wallID;
        private final int scalableWallID;
        private final int wallRotation;
        private final int WALL_TYPE = 0;
        private final int wallX;
        private final int wallY;
        private final int offsetX;
        private final int offsetY;
        private boolean scalable;

        public void attachRope() {
            DynamicGameObject scalableWall = DynamicGameObject.createPublic(scalableWallID, new Position(wallX, wallY, 0), WALL_TYPE, wallRotation);

            DynamicGameObject climbRope = DynamicGameObject.createPublic(CastleWarsConstants.OBJECT_WALL_ROPE, new Position(wallX, wallY, 0), 4, wallRotation);

            TaskManager.submit(100, () -> {
                removeRope();
            });

            World.addObject(scalableWall);
            World.addObject(climbRope);
            setScalable(true);
        }

        public void removeRope() {
            /*
            RegionObject climbRope = new RegionObject(CastleWarsConstants.OBJECT_WALL_ROPE, wallX, wallY, 0, wallRotation, 4);
            ObjectHandler.removeSpawn(climbRope, null, true);
            RegionObject scalableWall = new RegionObject(scalableWallID, wallX, wallY, 0, wallRotation, WALL_TYPE);
            RegionObject battlement = new RegionObject(wallID, wallX, wallY, 0, wallRotation, WALL_TYPE);
            ObjectHandler.removeSpawn(scalableWall, battlement, true);
            */

            setScalable(false);
        }

        private boolean isScalable() {
            return scalable;
        }

        private void setScalable(boolean scalable) {
            this.scalable = scalable;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

    }

    public static void climbWall(Player player, int wallX, int wallY) {
        if (player.getCurrentParty() == null) {
            return;
        }
        if (player.isWithinDistance(new Position(wallX, wallY), 1)) {
            return;
        }
        Battlement battlement = battlementsMap.get(new Position(wallX, wallY, 0));

        if (battlement == null) {
            return;
        }

        if (!battlement.isScalable()) {
            return;
        }

        if ((battlement.getOffsetX() != 0 && player.getPosition().getX() != wallX) || (battlement.getOffsetY() != 0 && player.getPosition().getY() != wallY)) {
            player.sendMessage("You can't reach that!");
            return;
        }
        player.BLOCK_ALL_BUT_TALKING = true;
        player.setPositionToFace(new Position(wallX + (battlement.getOffsetX() * -1), wallY + (battlement.getOffsetY() * -1)));
        player.updateAppearance();

        TaskManager.submit(2, () -> {
            player.moveTo(new Position(battlement.wallX + (battlement.getOffsetX() * -1), battlement.wallY + (battlement.getOffsetY() * -1), 0));
            player.BLOCK_ALL_BUT_TALKING = false;
        });
    }

    public static void cutRope(Player player, int wallX, int wallY) {
        if (player.getCurrentParty() == null) {
            return;
        }

        Battlement battlement = battlementsMap.get(new Position(wallX, wallY, 0));

        if (battlement == null) {
            return;
        }

        if (!battlement.isScalable()) {
            player.sendMessage("There's no rope there.");
            return;
        }

        if ((battlement.getOffsetX() != 0 && player.getPosition().getX() != wallX + (battlement.getOffsetX() * -1)) || (battlement.getOffsetY() != 0 && player.getPosition().getY() != wallY + (battlement.getOffsetY() * -1))) {
            player.sendMessage("You can't cut the rope from here.");
            return;
        }

        player.sendMessage("You cut the rope.");

        battlement.removeRope();
    }

    private static void loadBattlements() {
        addBattlement(new Battlement(2413, 3076, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2413, 3077, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2413, 3078, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2413, 3079, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2413, 3080, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2413, 3081, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2413, 3082, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2413, 3083, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2413, 3084, 2, -1, 0, 4446, 36313));

        addBattlement(new Battlement(2411, 3087, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2411, 3088, 2, -1, 0, 4446, 36313));
        addBattlement(new Battlement(2411, 3089, 2, -1, 0, 4446, 36313));

        addBattlement(new Battlement(2414, 3092, 3, 0, 1, 4446, 36313));
        addBattlement(new Battlement(2415, 3092, 3, 0, 1, 4446, 36313));
        addBattlement(new Battlement(2416, 3092, 3, 0, 1, 4446, 36313));

        addBattlement(new Battlement(2419, 3090, 3, 0, 1, 4446, 36313));
        addBattlement(new Battlement(2420, 3090, 3, 0, 1, 4446, 36313));
        addBattlement(new Battlement(2421, 3090, 3, 0, 1, 4446, 36313));
        addBattlement(new Battlement(2422, 3090, 3, 0, 1, 4446, 36313));

        addBattlement(new Battlement(2386, 3132, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2386, 3131, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2386, 3130, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2386, 3129, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2386, 3128, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2386, 3127, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2386, 3126, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2386, 3125, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2386, 3124, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2386, 3123, 0, 1, 0, 4447, 36314));

        addBattlement(new Battlement(2388, 3120, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2388, 3119, 0, 1, 0, 4447, 36314));
        addBattlement(new Battlement(2388, 3118, 0, 1, 0, 4447, 36314));

        addBattlement(new Battlement(2385, 3115, 1, 0, -1, 4447, 36314));
        addBattlement(new Battlement(2384, 3115, 1, 0, -1, 4447, 36314));
        addBattlement(new Battlement(2383, 3115, 1, 0, -1, 4447, 36314));

        addBattlement(new Battlement(2380, 3117, 1, 0, -1, 4447, 36314));
        addBattlement(new Battlement(2379, 3117, 1, 0, -1, 4447, 36314));
        addBattlement(new Battlement(2378, 3117, 1, 0, -1, 4447, 36314));
        addBattlement(new Battlement(2377, 3117, 1, 0, -1, 4447, 36314));
    }
}
