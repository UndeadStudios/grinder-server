package com.grinder.game.entity.object;

import com.grinder.game.World;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.EntityUpdateType;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A simple object manager used to manage {@link GameObject}s which are spawned
 * by the server.
 * <p>
 * For client/map-objects, see {@link ClippedMapObjects}.
 *
 * @author Professor Oak
 * @author Stan van der Bend
 */
public final class ObjectManager {

    private final static int MAX_DISTANCE_FOR_UPDATE_ADMISSIBILITY = 104;


    public static void add(GameObject object) {
        add(object, false);
    }

    /**
     * Adds a {@link GameObject} to the world.
     *
     * @param objectToAdd              The object being registered.
     * @param updateForAllNearbyPlayer Should the send object packet be sent to nearby players.
     */
    public static void add(final GameObject objectToAdd, final boolean updateForAllNearbyPlayer) {
        ClippedMapObjects.add(objectToAdd);

        if (updateForAllNearbyPlayer)
            updateNearbyPlayers(objectToAdd, OperationType.ADD);
    }

    /**
     * Removes a {@link GameObject} from the world.
     *
     * @param objectToRemove           The object to deregister.
     * @param updateForAllNearbyPlayer Should the send object packet be sent to nearby players.
     */
    public static void remove(final GameObject objectToRemove, final boolean updateForAllNearbyPlayer) {
        //If old code is trying to remove a static object, then create dynamic object -1 instead
//        Optional<GameObject> optional = findDynamicObjectAt(objectToRemove.getId(), objectToRemove.getPosition());
//
//        if (optional.isEmpty()) {
//            DynamicGameObject removedObject = DynamicGameObject.createPublic(-1, objectToRemove.getPosition().clone(), objectToRemove.getObjectType(), objectToRemove.getFace());
//            removedObject.setOriginalObject(objectToRemove); // set the static object as reference to revert to
//            add(removedObject, updateForAllNearbyPlayer);
//        } else {
            ClippedMapObjects.removeAll(objectToRemove);

            if (updateForAllNearbyPlayer)
                updateNearbyPlayers(objectToRemove, OperationType.REMOVE);
//        }
    }

    public static void updateNearbyPlayers(final GameObject object, final OperationType operationType) {
        World.getRegions().get(object.getPosition().getRegionCoordinates()).notifyListeners(object, operationType == OperationType.REMOVE ? EntityUpdateType.REMOVE : EntityUpdateType.ADD);
    }

    /**
     * Performs the given {@link OperationType} on the given {@link GameObject}.
     * Used for spawning and despawning objects. If the object has an owner, it
     * will only be spawned for them. Otherwise, it will act as global.
     */
    private static void sendUpdate(final Player player, final GameObject object, OperationType type) {

        if (object.getId() == -1)
            type = OperationType.REMOVE;

        int objectHeight = object.getPosition().getZ();
        objectHeight = objectHeight >= 4 ? objectHeight % 4 : objectHeight;
        int playerHeight = player.getPosition().getZ();
        playerHeight = playerHeight >= 4 ? playerHeight % 4 : playerHeight;

        if(objectHeight == playerHeight) {
            //System.out.println(player.getUsername() + ": " + type.name() + " -> " + object.getId() + " at " + object.getPosition().compactString());
            switch (type) {
                case ADD:
                    player.getPacketSender().sendObject(object);
                    break;
                case REMOVE:
                    player.getPacketSender().sendObjectRemoval(object);
                    break;
            }
        }
    }

    /**
     * Checks if a {@link GameObject} exists at the given location.
     *
     * @param position the {@link Position} to check at.
     * @return {@code true} if a {@link GameObject} exists at the position,
     * {@code false} otherwise.
     */
    public static boolean existsAt(final Position position) {
        return !ClippedMapObjects.getObjectsAt(position).isEmpty();
    }

    /**
     * Checks if a {@link GameObject} exists at the given location with the
     * given id.
     *
     * @param id       the {@link GameObject#getId()}.
     * @param position the {@link Position} to check at.
     * @return {@code true} if a {@link GameObject} of the given id exists at the position,
     * {@code false} otherwise.
     */
    public static boolean existsAt(final int id, final Position position) {
        return ClippedMapObjects.findObject(id, position).isPresent();
    }

    /**
     * Locates spawned objects
     */
    public static Optional<GameObject> findDynamicObjectAt(Position position, Predicate<GameObject> predicate) {

        final Stream<GameObject> objectStream;

        objectStream = World.getRegions().fromPosition(position).getDynamicGameObjects(position.getZ()).stream();

        return objectStream
                .filter(predicate)
                .findAny();
    }

    public static Optional<GameObject> findDynamicObjectAt(Position position) {
        return findDynamicObjectAt(position, gameObject -> gameObject.getPosition().equals(position));
    }

    public static Optional<GameObject> findDynamicObjectAt(int id, Position position) {
        Predicate<GameObject> predicate = gameObject -> gameObject.getPosition().equals(position);
        return findDynamicObjectAt(position, predicate.and(gameObject -> gameObject.getId() == id));
    }

    public static Optional<GameObject> findStaticObjectAt(int id, Position position) {
        Predicate<GameObject> predicate = gameObject -> gameObject.getPosition().equals(position);
        return findStaticObjectAt(position, predicate.and(gameObject -> gameObject.getId() == id));
    }

    public static Optional<GameObject> findStaticObjectAt(Position position, Predicate<GameObject> predicate) {

        final Stream<GameObject> objectStream;

        objectStream = World.getRegions().fromPosition(position).getStaticGameObjects(position.getZ()).stream();

        return objectStream
                .filter(predicate)
                .findAny();
    }

    /**
     * The possible operation types.
     */
    public enum OperationType {
        ADD, REMOVE
    }
}