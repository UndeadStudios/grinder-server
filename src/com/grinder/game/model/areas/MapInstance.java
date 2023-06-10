package com.grinder.game.model.areas;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.collision.CollisionMap;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.area.RegionRepository;
import com.grinder.game.model.areas.constructed.ConstructedChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: Lesik
 */
public class MapInstance {
    /**
     * Reference list for clip maps we created. Can use for clearing
     */
    private final List<Integer> clipMaps = new ArrayList<>(InstanceManager.RESERVE_SIZE);

    /**
     * Reference list of region coordinate IDs of the chunks we added in World.constructedChunks. Note to self, remove all floors 0-3
     */
    private final List<Integer> constructedChunks = new ArrayList<>();

    /**
     * Reference of regions created in this instance in use for removal.
     */
    private final List<Region> regionsCreated = new ArrayList<>();

    public List<Integer> getConstructedChunks() {
        return constructedChunks;
    }

    /**
     * Tests if instance needs to be destroyed on extended based on certain circumstances.
     */
    private int expireTick, creationTick;

    private final int instanceId;
    private final Position basePosition;
    private final RegionCoordinates baseCoordinate;

    private boolean mapBuilt = false;

    public boolean isMapBuilt() {
        return mapBuilt;
    }

    private InstanceManager.SinglePlayerMapType singlePlayerMapType;

    public void setSinglePlayerMapType(InstanceManager.SinglePlayerMapType type) {
        this.singlePlayerMapType = type;
    }

    public InstanceManager.SinglePlayerMapType getSinglePlayerMapType() {
        return this.singlePlayerMapType;
    }

    private Area area;

    public Area setArea(Area area) {
        this.area = area;
        return this.area;
    }

    public Area getArea() {
        return area;
    }

    public MapInstance(int id) {
        this.instanceId = id;
        this.basePosition = toBasePosition(id);
        this.baseCoordinate = basePosition.getRegionCoordinates();
        this.creationTick = World.getTick();
        setExpireTime();
    }

    private void setExpireTime() {
        this.expireTick = World.getTick() + 1600; // 16 minute checks
    }

    public boolean isExpired() {
        return expireTick >= World.getTick();
    }

    public void resetExpireTick() {
        setExpireTime();
    }

    public int getCreationTick() { return creationTick; }

    public int getInstanceId() {
        return instanceId;
    }

    public Position getBasePosition() {
        return basePosition;
    }

    public RegionCoordinates getBaseCoordinate() {
        return baseCoordinate;
    }

    public List<Integer> getClipMapRegionIds() {
        return clipMaps;
    }

    public static Position toBasePosition(int instanceId) {
        return new Position((instanceId >> 8) << 8, (instanceId & 0xFF) << 8);
    }

    public static int toInstanceId(int x, int y) {
        return ((x >> 8) << 8) | (y >> 8);
    }

    /**
     * Important to clear the instance after it is no longer in use.
     */
    public void destroyInstance() {
        for (int regionIds : clipMaps) {
            CollisionManager.regions.remove(regionIds);
        }
        clipMaps.clear();

        for (int z = 0; z < Position.HEIGHT_LEVELS; z++) {
            Map<Integer, ConstructedChunk> list = World.getConstructedMapChunks().get(z);
            for (int chunks : constructedChunks) {
                list.remove(chunks);
            }
        }
        constructedChunks.clear();

        RegionRepository repo = World.getRegions();
        for (Region region : regionsCreated) {
            repo.remove(region);
        }
        regionsCreated.clear();

        clearRegionSections();
//        System.out.println("Destructed map instance. " + instanceId);
    }

    /**
     * If we don't want to discard the map instance, but reset it
     */
    public MapInstance reset() {
        for (int regionId : clipMaps) {
            CollisionManager.regions.put(regionId, new CollisionMap(regionId, 0, 0).setLoaded(true));
        }
        for (int z = 0; z < Position.HEIGHT_LEVELS; z++) {
            Map<Integer, ConstructedChunk> list = World.getConstructedMapChunks().get(z);
            for (int chunks : constructedChunks) {
                list.remove(chunks);
            }
        }

        RegionRepository repo = World.getRegions();
        for (Region region : regionsCreated) {
            repo.remove(region);
        }
        regionsCreated.clear();

        clearRegionSections();

        mapBuilt = false;

        setExpireTime(); // reset expire time also
//        System.out.println("Reset map instance. " + instanceId);
        return this;
    }

    public void clearRegionSections() {
        int size = InstanceManager.RESERVE_SIZE;
        RegionRepository repo = World.getRegions();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int regionId = ((baseCoordinate.getX() >> 3) + x) << 8 | ((baseCoordinate.getY() >> 3) + y);
                repo.removeSection(regionId);
            }
        }
    }

    /**
     * Copy a piece of the map on real world at the end of the current tick. Move players in one tick after creating map
     * @param fromChunkCoord from which chunk base coordinate we are copying the map.
     * @param fromZ the plane we are copying from
     * @param chunkOffsetX the offset to place this copied chunk in the instanced map
     * @param chunkOffsetY the offset to place this copied chunk in the instanced map
     * @param toZ the plane to place this chunk in an instanced map
     * @param size How many chunks we want to copy starting from the chunk base coordinate.
     * @param newOrientation The orientation of the chunks we are copying
     */
    public void copyPlane(RegionCoordinates fromChunkCoord, int fromZ, int chunkOffsetX, int chunkOffsetY, int toZ, MapBuilder.ChunkSizes size, int newOrientation) {
        copyPlane(fromChunkCoord, fromZ, new RegionCoordinates(baseCoordinate.getX() + chunkOffsetX, baseCoordinate.getY() + chunkOffsetY), toZ, size, newOrientation);
    }

    /**
     * Creates a map on real world at the end of the current tick. Move players in one tick after creating map
     */
    public void copyPlane(RegionCoordinates fromChunkCoord, int fromZ, RegionCoordinates toChunkCoord, int toZ, MapBuilder.ChunkSizes size, int newOrientation) {
        InstanceManager.addToQueue(()-> {
            try {
                int mapSize = size.getSize();

                for (int x1 = 0; x1 < mapSize; x1++) {
                    for (int y1 = 0; y1 < mapSize; y1++) {

                        int[] offset = MapBuilder.getRotatedPosition(x1, y1, mapSize, newOrientation);

                        Region newRegion = World.getRegions().get(new RegionCoordinates(toChunkCoord.getX() + offset[0], toChunkCoord.getY() + offset[1]));

                        Region copyFrom = World.getRegions().get(new RegionCoordinates(fromChunkCoord.getX() + x1, fromChunkCoord.getY() + y1));

                        newRegion.copyFromChunk(copyFrom, fromZ, toZ, newOrientation);

                        ConstructedChunk constructedChunk = new ConstructedChunk(newRegion.getCoordinates(), toZ, copyFrom.getCoordinates(), fromZ, newOrientation);

                        World.getConstructedMapChunks().get(toZ).put(constructedChunk.getChunkId(), constructedChunk);

                        constructedChunks.add(constructedChunk.getChunkId());

                        regionsCreated.add(newRegion);
                    }
                }
                mapBuilt = true;
            } catch (Exception e) {
                System.out.println("CopyPlane Error - fromChunkCoord:" + fromChunkCoord.toString() + " toChunkCoord:" + toChunkCoord.toString() + " size:" + size.toString() + " newOrientation:" + newOrientation);
                e.printStackTrace();
            }
        });
    }
}
