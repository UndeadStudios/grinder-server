package com.grinder.game.entity.object;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.EntityType;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.Region;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class stores both map and content loaded {@link GameObject objects}.
 *
 * You can use this class to add/remove objects or find an object
 * (or all objects) at some position (for some given id).
 *
 * @author Professor Oak
 * @author Stan van der Bend
 */
public final class ClippedMapObjects {

    /**
     * TODO: remove or properly implement region based object updating
     */
    public static boolean USE_NEW_OBJECT_UPDATES = true;

    /**
     * A map with as keys hashed coordinates and as values a list of objects
     * located at the hashed coordinates.
     *
     * @see #getCoordinatesHash(Position) for the hashing.
     */
    public static final Map<Long, ArrayList<GameObject>> mapObjects = new HashMap<>();

    /**
     * Attempts to find an object of the argued id, at the argued position.
     *
     * Note that this does not work for objects spawned in an instance (z >= 4).
     *
     * @param id        the id of the object to find.
     * @param position  the {@link Position} of the object to find.
     */
	public static Optional<GameObject> findObject(final int id, final Position position) {

        CollisionManager.tryLazyLoadRegionAt(position.getX(), position.getY());

        final Region region = World.getRegions().fromPosition(position);
        final Set<GameObject> list = new HashSet<>();
        list.addAll(region.getStaticGameObjects(position.getZ()));
        list.addAll(region.getDynamicGameObjects(position.getZ()));

        return list.stream().filter(object -> object.getId() == id && object.getPosition().equals(position)).findAny();

    }

    /**
     * Checks if an object exists with the same id and position as the argued object.
     */
    public static boolean exists(GameObject object) {
        return findObject(object.getId(), object.getPosition()).isPresent();
    }

    /**
     * Retrieves all objects at the argued position.
     *
     * @param position  the {@link Position} of the object to find.
     */
	public static List<GameObject> getObjectsAt(Position position) {
        // Load region..
        CollisionManager.tryLazyLoadRegionAt(position.getX(), position.getY());

        final Region region = World.getRegions().fromPosition(position);

        return new ArrayList<>(region.getEntities(position, EntityType.DYNAMIC_OBJECT, EntityType.STATIC_OBJECT));
    }

    @NotNull
    private static Position normalizePlane(Position position) {
        if (position.getZ() >= 4) {
            position = position.clone().setZ(0);
            System.err.println("Attempted to find instanced object {"+ position.compactString()+"}!");
        }
        return position;
    }

    /**
     * Add a new object to {@link #mapObjects}, given that it is not already present in {@link #mapObjects}.
     *
     * @param object the {@link GameObject} to add.
     */
    public static void add(GameObject object) {
        World.addObject(object);

        CollisionManager.addObjectClipping(object);
    }

    /**
     * Removes all objects with the same id and position as the argued object.
     */
    public static void removeAll(GameObject object) {
        World.deSpawn(object);

        CollisionManager.removeObjectClipping(object);
    }

    /**
     * Removes all objects at the argued position.
     */
    public static void removeAll(Position position) {
        World.deSpawnAllAt(position, EntityType.STATIC_OBJECT, EntityType.DYNAMIC_OBJECT);

        // Remove clipping from this area..
        CollisionManager.clearClipping(position.getX(), position.getY(), position.getZ());
    }

    private static long getCoordinatesHash(Position position) {
        return getCoordinatesHash(position.getX(), position.getY(), position.getZ());
    }

    private static long getCoordinatesHash(int x, int y, int z) {
        return (z + ((long) x << 24) + ((long) y << 48));
    }
}
