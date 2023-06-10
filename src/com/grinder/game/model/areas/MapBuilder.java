package com.grinder.game.model.areas;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.constructed.ConstructedChunk;
import com.grinder.game.model.areas.instanced.VorkathArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Author: Lesik
 */
public class MapBuilder {

    private final static Logger LOGGER = LogManager.getLogger(MapBuilder.class);

    public static int[] getRotatedPosition(int x, int y, int mapSize, int chunkRotation) {
        int size = mapSize - 1;
        if (chunkRotation == 0) {
            return new int[]{x, y};
        }
        if (chunkRotation == 1) {
            return new int[]{y, size - x};
        }
        if (chunkRotation == 2) {
            return new int[]{size - x, size - y};
        }
        return new int[]{size - y, x};
    }

    public static final Position MAP_BASE = new Position(2176, 3968);

    public static void buildVorkathMap(Player player) {
        final MapInstance mapInstance = InstanceManager.getOrCreate(player, InstanceManager.SinglePlayerMapType.VORKATH);
        final Position base = mapInstance.getBasePosition();

        Position MAP_BASE = new Position(2176, 3968);

        if (!mapInstance.isMapBuilt())
            mapInstance.copyPlane(MAP_BASE.getRegionCoordinates(), 0, 0, 0, 0, MapBuilder.ChunkSizes.TWENTY_FOUR, 0);

        //VorkathArea.buildArea(mapInstance, player);

//        MapInstance mapInstace = InstanceManager.getOrCreate(player);
//
//        Position vorkathBase = new Position(2208, 4000);
//
//        mapInstace.copyPlane(vorkathBase.getRegionCoordinates(), 0, 0,0, 0, MapBuilder.ChunkSizes.SIXTEEN, 0);
//
//        player.moveTo(mapInstace.getBasePosition().transform(69, 35, 0));
//
//        VorkathArea.buildArea(mapInstace, player);
    }

    static {
        for (int i = 0; i < Position.HEIGHT_LEVELS; i++) {
            World.getConstructedMapChunks().add(i, new HashMap<Integer, ConstructedChunk>(4000));
        }

    }

    /**
     * One chunk is 8x8 tiles
     */
    public enum ChunkSizes {
        ONE(1), //8x8 used in Construction skill
        TWO(2), //16x16
        FOUR(4), //32x32 used in Raids 1
        EIGHT(8), //64x64 copies the whole region(64 tiles)
        SIXTEEN(16), //128x128
        TWENTY_FOUR(24), //192X192
        THIRTY_TWO(32), //256x256 // max size for instances at the moment
        //SIXTY_FOUR(64), //512x512
        //HUNDRED_TWENTY_EIGHT(128) //1024x1024
        ;

        private final int size;

        private ChunkSizes(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }
}
