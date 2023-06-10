package com.grinder.game.model.areas;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.collision.CollisionMap;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InstanceManager {

    //bases must be divisible by reserved sizes
    private static final int REGION_BASE_X = 64; // max region x = 16,777,215
    private static final int REGION_BASE_Y = 0; //max region y = 255

    public static final int WORLD_BASE_X = REGION_BASE_X << 6; // 3968
    private static final int WORLD_BASE_Y = REGION_BASE_Y << 6; // 0

    /**
     * Size in Regions(Regions are 64x64 tiles big) per instanced Map. Do not change this without changing bitshift for instance ids
     */
    public static final int RESERVE_SIZE = 4; // 256 size in tiles. If changing this value, then must change bitshifting for instance ids

    public static final int SIZE_IN_TILES = RESERVE_SIZE * 64;

    // RESERVE 256 TILES WITH SHIFTING 8 BITS INSTEAD OF 6

    private static final int MAX_Y = 256 / RESERVE_SIZE;
    private static final int MAX_X = 16_777_216 / RESERVE_SIZE;

    private static final int MAX_ID = MAX_X << 8 | MAX_Y;

    //the ID of the 256 region we start as base. Located in black maps to far east
    private static final int STARTING_ID = ((WORLD_BASE_X >> 8) << 8) | (WORLD_BASE_Y >> 8); // 3840

    private static int tick = 0;
    private static final int CHECK_TICK = 1500; // check for instances to discard every 15 minutes

    private static Queue<Runnable> buildQueue = new ConcurrentLinkedQueue();

    private static int nextSlot = 0;

    private static int getNextSlot() {
        if (nextSlot++ > MAX_ID) {
            nextSlot = 0;
        }
        int slot = nextSlot + STARTING_ID;
        if ((slot & 0xFF) >= 64) // if Y coordinate is 64+, go to next slot
            return getNextSlot();
        return nextSlot + STARTING_ID;
    }

    private static Map<Integer, MapInstance> instanceMap = new HashMap<>();

    public static MapInstance getById(int id) {
        return instanceMap.get(id);
    }

    private static Map<Integer, Integer> clanInstances = new HashMap<>();

    public static Map<Integer, Integer> getClanInstances() {
        return clanInstances;
    }

    public static MapInstance getOrCreateClanInstance(final int clanIndex) {
        Map<Integer, Integer> hashMap = clanInstances;

        int instanceId = hashMap.getOrDefault(clanIndex, -1);

        MapInstance mapInstace;
        if (instanceId == -1) {
            mapInstace = InstanceManager.createNewMapInstance();
            hashMap.put(clanIndex, mapInstace.getInstanceId());
        } else {
            mapInstace = InstanceManager.getById(instanceId);
            if (mapInstace == null) {
                mapInstace = InstanceManager.createNewMapInstance();
                hashMap.put(clanIndex, mapInstace.getInstanceId());
            }
        }
        return mapInstace;
    }

    public static MapInstance getOrCreate(Player p, SinglePlayerMapType type) {
        Map<String, Integer> playerMap = type.getHashMap();

        int instanceId = playerMap.getOrDefault(p.getUsername(), -1);

        MapInstance mapInstace;
        if (instanceId == -1) {
            mapInstace = InstanceManager.createNewMapInstance();
            playerMap.put(p.getUsername(), mapInstace.getInstanceId());
        } else {
            mapInstace = InstanceManager.getById(instanceId);
            if (mapInstace == null) {
                mapInstace = InstanceManager.createNewMapInstance();
                playerMap.put(p.getUsername(), mapInstace.getInstanceId());
            }
        }
        return mapInstace;
    }

    public static void discardInstance(MapInstance mapInstance) {
        mapInstance.destroyInstance();
        final int instanceId = mapInstance.getInstanceId();
        instanceMap.remove(instanceId);
        clanInstances.remove(instanceId);
        for (SinglePlayerMapType type : SinglePlayerMapType.VALUES) {
            type.getHashMap().remove(instanceId);
        }
    }

    /**
     * Creates a new empty map instance with tile size 256x256
     */
    public static MapInstance createNewMapInstance() {
        int instanceId = getNextSlot();
        boolean foundSlot = false;
        //check if instance is available
        for (int i = 0; i < MAX_ID; i++) {
            MapInstance inst = instanceMap.get(instanceId);
            if (inst != null) {
                instanceId = getNextSlot();
            } else {
                foundSlot = true;
                break;
            }
        }

        if (!foundSlot) { // error check
            System.out.println("ERROR: ALL MAP INSTANCES ARE RESERVED");
            return null;
        }

        final MapInstance mapI = new MapInstance(instanceId);

        instanceMap.put(instanceId, mapI);

        Position basePos = mapI.getBasePosition();

        final int regionBaseX = (basePos.getX() >> 6);
        final int regionBaseY = (basePos.getY() >> 6);

        //create the new clipmaps
        for (int x = 0, l = RESERVE_SIZE/2; x < l; x++) {
            for (int y = 0, l2 = RESERVE_SIZE/2; y < l2; y++) {

                int regionId = ((regionBaseX + x) << 8) | (regionBaseY + y);

                CollisionMap collisionMap = new CollisionMap(regionId, 0, 0).setLoaded(true);
                CollisionManager.regions.put(regionId, collisionMap);

                //add reference of region collision maps to mapInstance
                mapI.getClipMapRegionIds().add(regionId);
            }
        }
        return mapI;
    }

    public static boolean isInConstructedMap(Position position) {
        return position != null && position.getX() >= WORLD_BASE_X;
    }

    public static void onLogin(Player player) {
        Position pos = player.getTeleportPosition();
        if (isInConstructedMap(pos)) {
            int instanceId = MapInstance.toInstanceId(pos.getX(), pos.getY());
            MapInstance map = getById(instanceId);
            //Check if instance exists or is the current player's instance or not
            if (map == null || map.getCreationTick() != player.instancedMapTick) {
                player.moveTo(Teleporting.TeleportLocation.EDGEVILLE.getPosition());
                player.instancedMapTick = 0;
            }
        } else {
            if (player.instancedMapTick != 0)
                player.instancedMapTick = 0;
        }
    }

    public static void process() {
        executeBuildQueue();
        tick++;
        if (tick >= CHECK_TICK) {
            tick = 0;
            Map<Integer, MapInstance> map = new HashMap<>(instanceMap);
            Iterator<Map.Entry<Integer, MapInstance>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, MapInstance> entry = it.next();
                MapInstance mapInstance = entry.getValue();

                //test if we want to remove
                if (mapInstance.isExpired()) {
                    if (mapInstance.getArea().hasPlayers()) {
                        mapInstance.resetExpireTick();
                    } else {
                        discardInstance(mapInstance);
                    }
                }
            }
        }
    }

    private static void executeBuildQueue() {
        Runnable runnable;
        while ((runnable = buildQueue.poll()) != null) {
            runnable.run();
        }
    }

    public static void addToQueue(Runnable runnable) {
        buildQueue.add(runnable);
    }

    /**
     * Reduce building of the same instances for certain maps that are single player instances
     */
    public enum SinglePlayerMapType {
        INSTANT_DESTRUCT, // maps that we will instantly destroy
        BLUE_MOON_INN,
        CERBERUS,
        FIGHT_CAVES,
        HOUSE,
        HYDRA,
        NORA_THE_HAG,
        OBOR_CAVE,
        SKELETON_HELLHOUND,
        VARROCK_PALACE,
        VORKATH,
        ZULRAH;

        public static SinglePlayerMapType[] VALUES = SinglePlayerMapType.values();

        private final Map<String, Integer> playerMap;

        private SinglePlayerMapType() {
            playerMap = new HashMap<>(2000);
        }

        public Map<String, Integer> getHashMap() {
            return playerMap;
        }
    }
}
