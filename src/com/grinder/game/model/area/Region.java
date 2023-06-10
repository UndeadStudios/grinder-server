package com.grinder.game.model.area;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.collision.CollisionMap;
import com.grinder.game.collision.TileFlags;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.message.impl.RegionUpdateMessage;
import com.grinder.game.message.impl.SetUpdatedRegionMessage;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.update.GroupableEntity;
import com.grinder.game.model.area.update.UpdateOperation;
import kotlin.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An 8x8 area of the map.
 *
 * @author Major
 */
public final class Region {

	public final static boolean USE_NEW_REGION_UPDATING = true;

	private final static Logger LOGGER = LogManager.getLogger(Region.class);

	/**
	 * A {@link RegionListener} for {@link UpdateOperation}s.
	 */
	private static final class UpdateRegionListener implements RegionListener {

		@Override
		public void execute(Region region, Entity entity, EntityUpdateType update) {
			EntityType type = entity.getEntityType();
			if (!type.isMob()) {
				region.record((Entity & GroupableEntity) entity, update);
			}
		}

	}

	/**
	 * The message of the exception thrown when a CollisionMatrix with an illegal height is requested.
	 */
	private static final String ILLEGAL_MATRIX_HEIGHT = "Matrix height level must be [0, %d), received %d.";

	/**
	 * The width and length of a Region, in tiles.
	 */
	public static final int SIZE = 8;

	/**
	 * The radius of viewable regions.
	 */
	public static final int VIEWABLE_REGION_RADIUS = 3;


	public static final int RENDERABLE_REGIONS = 13;

	/**
	 * The width of the viewport of every Player, in tiles.
	 */
	public static final int VIEWPORT_WIDTH = SIZE * 13;

	/**
	 * The default size of newly-created Lists, to reduce memory usage.
	 */
	private static final int DEFAULT_LIST_SIZE = 2;

	/**
	 * The RegionCoordinates of this Region.
	 */
	private final RegionCoordinates coordinates;

	private int rotation = 0;

	/**
	 * The Map of Positions to Entities in that Position.
	 */
	private final Map<Position, Set<Entity>> entities = new ConcurrentHashMap<>();

	/**
	 * A list of players inside the region chunk
	 */
	private final Set<Player> players = new HashSet<>();

	/**
	 * A list of npcs inside the region chunk
	 */
	private final Set<NPC> npcs = new HashSet<>(255);

	/**
	 * A List of RegionListeners registered to this Region.
	 */
	private final List<RegionListener> listeners = new ArrayList<>();


	/**
	 * A List of runnable actions to execute when this chunk loads for player
	 */
	private final List<Consumer<Player>> loadActions = new ArrayList<>();

	/**
	 * The List of Sets containing RegionUpdateMessages that specifically remove StaticGameObjects. The
	 * List is ordered based on the height level the RegionUpdateMessages concern.
	 */
	private final List<Set<RegionUpdateMessage>> removedObjects = new ArrayList<>(Position.HEIGHT_LEVELS);

	/**
	 * List of spawned GameObjects
	 * List is ordered based on the height level the GameObject is on.
	 */
	private final List<Set<GameObject>> gameObjects = new ArrayList<>(Position.HEIGHT_LEVELS);

	/**
	 * List of Static Objects that we cache for building dynamic region chunks
	 * List is ordered based on the height level the GameObject is on.
	 */
	private final List<Set<GameObject>> STATIC_OBJECTS = new ArrayList<>(Position.HEIGHT_LEVELS); //CONVERT HEIGHT TO OPTIONAL?

	/**
	 * List of spawned ItemOnGround
	 * List is ordered based on the height level the ItemOnGround is on.
	 */
	private final List<Set<ItemOnGround>> groundItems = new ArrayList<>(Position.HEIGHT_LEVELS);

	/**
	 * The List of Sets containing RegionUpdateMessages. The List is ordered based on the height level the
	 * RegionUpdateMessages concern. This only contains the updates to this Region that have occurred in the last
	 * pulse.
	 */
	private final List<List<RegionUpdateMessage>> updates = new ArrayList<>(Position.HEIGHT_LEVELS);

	/**
	 * List of players that have this region chunk rendered on the player's map
	 */
	private List<Player> subscribedPlayers = new ArrayList<Player>();

	public void subscribePlayer(Player player) {
		subscribedPlayers.add(player);
	}

	public void unsubPlayer(Player player) {
		subscribedPlayers.remove(player);
	}

	/**
	 * Creates a new Region.
	 *
	 * @param x The x coordinate of the Region.
	 * @param y The y coordinate of the Region.
	 */
	public Region(int x, int y) {
		this(new RegionCoordinates(x, y));
	}

	/**
	 * Creates a new Region with the specified {@link RegionCoordinates}.
	 *
	 * @param coordinates The RegionCoordinates.
	 */
	public Region(RegionCoordinates coordinates) {
		this.coordinates = coordinates;
		listeners.add(new UpdateRegionListener());
		for (int height = 0; height < Position.HEIGHT_LEVELS; height++) {
			gameObjects.add(new HashSet<>());
			STATIC_OBJECTS.add(new HashSet<>());
			groundItems.add(new HashSet<>());
			updates.add(new ArrayList<>(DEFAULT_LIST_SIZE));
		}
	}

	public void updateEntity(Entity entity){
//		checkPosition(entity.getPosition());
		notifyListeners(entity, EntityUpdateType.CHANGED);
	}

	/**
	 * Adds player to this region chunk in use of region updates(objects/items/projectiles/gfx)
	 * @param player
	 * @param full
	 */
	public static void addPlayerToRegionChunks(Player player, Set<RegionCoordinates> full) {
		RegionRepository repository = World.getRegions();

		for (RegionCoordinates coordinates : full) {
			Region region = repository.get(coordinates);

			region.subscribePlayer(player);

			region.onLoad(player);
		}
	}

	/**
	 * Adds a Consumer that will run when a player has this chunk loaded. Used for things like farming, and anything else we can think of.
	 * @param consumer player that is loading this region chunk
	 */
	public void addRegionLoadAction(Consumer<Player> consumer) {
		loadActions.add(consumer);
	}

	/**
	 * Removes player from this region chunk because he left the area and this chunk is no longer rendered for the player
	 * @param player
	 * @param list
	 */
	public static void removePlayerFromChunks(Player player, Set<RegionCoordinates> list) {
		RegionRepository repository = World.getRegions();

		for (RegionCoordinates coordinates : list) {
			Region region = repository.get(coordinates);
			region.unsubPlayer(player);
		}
	}

	/**
	 * Runs a Consumer when a player has this chunk loaded.
	 * @param player
	 */
	public void onLoad(Player player) {
		if (player.isBotPlayer())
			return;
		for (Consumer<Player> consumer : loadActions) {
			consumer.accept(player);
		}
	}

	/**
	 * Adds a {@link Entity} to the Region. Note that this does not spawn the Entity, or do any other action other than
	 * register it to this Region.
	 *
	 * @param entity The Entity.
	 * @param notify Whether or not the {@link RegionListener}s for this Region should be notified.
	 * @throws IllegalArgumentException If the Entity does not belong in this Region.
	 */
	public void addEntity(Entity entity, boolean notify) {
		EntityType type = entity.getEntityType();
		Position position = entity.getPosition();
		checkPosition(position);

		if (!type.isTransient()) {
			switch (entity.getEntityType()) {
				case DYNAMIC_OBJECT: {
					GameObject asGameObject = entity.getAsGameObject();
					gameObjects.get(position.getZ() & 3).add(asGameObject);
					((DynamicGameObject)entity).isSpawned = true;
					break;
				}
				case ITEM: {
					ItemOnGround item = entity.getAsItem();
					item.setIndex(ItemOnGroundManager.getNextSlot());
					World.getGroundItems().add(item); // add to list to cycle ticks for this item
					groundItems.get(position.getZ() & 3).add(item);
					break;
				}
				case PLAYER: {
					players.add(entity.getAsPlayer());
					break;
				}
				case NPC: {
					npcs.add(entity.getAsNpc());
					break;
				}
				case STATIC_OBJECT: {
					STATIC_OBJECTS.get(position.getZ() & 3).add(entity.getAsGameObject());
					break;
				}
			}

			Set<Entity> local = entities.computeIfAbsent(position, key -> new HashSet<>(DEFAULT_LIST_SIZE));
			local.add(entity);
		}

		if (notify) {
			notifyListeners(entity, EntityUpdateType.ADD);
		}
	}

	/**
	 * Adds a {@link Entity} to the Region. Note that this does not spawn the Entity, or do any other action other than
	 * register it to this Region.
	 *
	 * By default, this method notifies RegionListeners for this region of the addition.
	 *
	 * @param entity The Entity.
	 * @throws IllegalArgumentException If the Entity does not belong in this Region.
	 */
	public void addEntity(Entity entity) {
		addEntity(entity, true);
	}

	public void addListener(RegionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Checks if this Region contains the specified Entity.
	 *
	 * This method operates in constant time.
	 *
	 * @param entity The Entity.
	 * @return {@code true} if this Region contains the Entity, otherwise {@code false}.
	 */
	public boolean contains(Entity entity) {

		switch (entity.getEntityType()) {
			case PLAYER: {
				return players.contains(entity.getAsPlayer());
			}
			case NPC: {
				return npcs.contains(entity.getAsNpc());
			}
			case ITEM: {
				return groundItems.get(entity.getPlane()).contains((entity.getAsItem()));
			}
			case DYNAMIC_OBJECT: {
				return gameObjects.get(entity.getPlane()).contains((DynamicGameObject)entity.getAsGameObject());
			}
		}

		Position position = entity.getPosition();
		Set<Entity> local = entities.get(position);

		return local != null && local.contains(entity);
	}

	/**
	 * Returns whether or not the specified {@link Position} is inside this Region.
	 *
	 * @param position The Position.
	 * @return {@code true} iff the specified Position is inside this Region.
	 */
	public boolean contains(Position position) {
		return coordinates.equals(position.getRegionCoordinates());
	}

	/**
	 * Encodes the contents of this Region into a {@link Set} of {@link RegionUpdateMessage}s, to be sent to a client.
	 *
	 * fuck this isn't kotlin
	 * @return The Set of RegionUpdateMessages.
	 */
	public List<RegionUpdateMessage> encode(int height, Player player) {

		final int finalHeight = Math.abs(height);

		//only need ground item and object packets

		final ArrayList<RegionUpdateMessage> updates = new ArrayList<>();
		for (GameObject gameObject : gameObjects.get(finalHeight)){
			final UpdateOperation<?>  operation = gameObject.toUpdateOperation(this, EntityUpdateType.ADD);
			final RegionUpdateMessage updateMessage = operation.toMessage();
			if (operation.canBeViewed(player))
				updates.add(updateMessage);
		}

		for (ItemOnGround item : groundItems.get(finalHeight)) {
			final UpdateOperation<?> operation = item.toUpdateOperation(this, EntityUpdateType.ADD);
			final RegionUpdateMessage message = operation.toMessage();

			if (operation.canBeViewed(player))
				updates.add(message);
		}
		return updates;
	}

	/**
	 * Gets this Region's {@link RegionCoordinates}.
	 *
	 * @return The RegionCoordinates.
	 */
	public RegionCoordinates getCoordinates() {
		return coordinates;
	}

	/**
	 * Gets an intermediate {@link Stream} from the {@link Set} of
	 * {@link Entity}s with the specified {@link EntityType} (s). Type will be
	 * inferred from the call, so ensure that the Entity type and the reference
	 * correspond, or this method will fail at runtime.
	 *
	 * @param types The {@link EntityType}s.
	 * @return The Stream of Entity objects.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> Stream<T> getEntities(EntityType... types) {
		Set<EntityType> set = ImmutableSet.copyOf(types);
		Stream.Builder<T> entityStreamBuilder = Stream.builder();
		for (Set<Entity> entitySet : entities.values()) {
			for (Entity entity : entitySet) {
				if(set.contains(entity.getEntityType()))
					entityStreamBuilder.add((T) entity);
			}
		}
		return entityStreamBuilder.build();
//		return (Stream<T>) entities.values().stream().flatMap(Collection::stream)
//			.filter(entity -> set.contains(entity.getEntityType()));
	}

	public <T extends Entity> Stream<T> getPlayersNpcs() {
		Stream.Builder streamBuilder = Stream.builder();
		for (Player player : players) {
			streamBuilder.add((T)player);
		}
		for (NPC npc : npcs) {
			streamBuilder.add((T)npc);
		}
		return streamBuilder.build();
	}

	public <T extends Entity> Stream<T> getPlayersStream() {
		Stream.Builder entityStreamBuilder = Stream.builder();
		for (Player player : players) {
				entityStreamBuilder.add((T)player);
		}
		return entityStreamBuilder.build();
	}

	/**
	 * Gets a shallow copy of the {@link Set} of {@link Entity} objects at the specified {@link Position}. The returned
	 * type will be immutable.
	 *
	 * @param position The Position containing the entities.
	 * @return The Set. Will be immutable.
	 */
	public Set<Entity> getEntities(Position position) {
		Set<Entity> set = entities.get(position);
		return (set == null) ? ImmutableSet.of() : ImmutableSet.copyOf(set);
	}

	/**
	 * Gets a shallow copy of the {@link Set} of {@link Entity}s with the specified {@link EntityType}(s). The returned
	 * type will be immutable. Type will be inferred from the call, so ensure that the Entity type and the reference
	 * correspond, or this method will fail at runtime.
	 *
	 * @param position The {@link Position} containing the entities.
	 * @param types The {@link EntityType}s.
	 * @return The Set of Entity objects.
	 */
	public <T extends Entity> Set<T> getEntities(Position position, EntityType... types) {
		Set<Entity> local = entities.get(position);
		if (local == null) {
			return ImmutableSet.of();
		}

		Set<EntityType> set = ImmutableSet.copyOf(types);
		@SuppressWarnings("unchecked")
		Set<T> filtered = (Set<T>) local.stream().filter(entity -> set.contains(entity.getEntityType()))
			.collect(Collectors.toSet());
		return ImmutableSet.copyOf(filtered);
	}

	public Set<GameObject> getDynamicGameObjects(int plane) {
		return gameObjects.get(plane & 3);
	}

	public Set<GameObject> getStaticGameObjects(int plane) {
		return STATIC_OBJECTS.get(plane & 3);
	}

	public Set<ItemOnGround> getGroundItems(int plane) {
		return groundItems.get(plane & 3);
	}

	public Set<Player> getPlayers() {
		return players;
	}

	public Set<NPC> getNpcs() {
		return npcs;
	}

	/**
	 * Gets the {@link Set} of {@link RegionCoordinates} of Regions that are viewable from the specified
	 * {@link Position}.
	 *
	 * @return The Set of RegionCoordinates.
	 */
	public Set<RegionCoordinates> getSurrounding() {
		int localX = coordinates.getX(), localY = coordinates.getY();
		int maxX = localX + VIEWABLE_REGION_RADIUS, maxY = localY + VIEWABLE_REGION_RADIUS;

		Set<RegionCoordinates> viewable = new HashSet<>();
		for (int x = localX - VIEWABLE_REGION_RADIUS; x < maxX; x++) {
			for (int y = localY - VIEWABLE_REGION_RADIUS; y < maxY; y++) {
				viewable.add(new RegionCoordinates(x, y));
			}
		}

		return viewable;
	}

	/**
	 * Gets the 13x13 Region Chunks with this region chunk being the center.
	 * @return Region Chunks in a 13x13 map
	 */
	public Set<RegionCoordinates> getRenderedRegionsFromCenter() {
		int startX = coordinates.getX() - 6, startY = coordinates.getY() - 6;

		Set<RegionCoordinates> list = new HashSet<>();
		for (int x = startX, lX = startX + RENDERABLE_REGIONS; x < lX; x++) {
			for (int y = startY, lY = startY + RENDERABLE_REGIONS; y < lY; y++) {
				list.add(new RegionCoordinates(x, y));
			}
		}

		return list;
	}

	/**
	 * Gets the {@link Set} of {@link RegionUpdateMessage}s that have occurred in the last pulse. This method can
	 * only be called <strong>once</strong> per pulse.
	 *
	 * @param height The height level to get the RegionUpdateMessages for.
	 * @return The Set of RegionUpdateMessages.
	 */
	public List<RegionUpdateMessage> getUpdates(int height) {
		height = Math.abs(height & 3);
		List<RegionUpdateMessage> updates = this.updates.get(height);
		List<RegionUpdateMessage> copy = ImmutableList.copyOf(updates);

		updates.clear();
		return copy;
	}

	/**
	 * Notifies the {@link RegionListener}s registered to this Region that an update has occurred.
	 *
	 * @param entity The {@link Entity} that was updated.
	 * @param type The {@link EntityUpdateType} that occurred.
	 */
	public void notifyListeners(Entity entity, EntityUpdateType type) {
		listeners.forEach(listener -> listener.execute(this, entity, type));
	}

	/**
	 * Removes an {@link Entity} from this Region.
	 *
	 * @param entity The Entity.
	 * @throws IllegalArgumentException If the Entity does not belong in this Region, or if it was never added.
	 */
	public void removeEntity(Entity entity) {
		removeEntity(entity, true);
	}

	public void removeEntity(Entity entity, boolean notify) {
		EntityType type = entity.getEntityType();
		if (type.isTransient()) {
			LOGGER.error("Tried to remove a transient Entity (" + entity + ") from " + "(" + this + ").");
			return;
		}

		Position position = entity.getPosition();
		checkPosition(position);
		Set<Entity> local = entities.get(position);

		switch (entity.getEntityType()) {
			case PLAYER: {
				players.remove(entity);
				break;
			}
			case NPC: {
				npcs.remove(entity);
				break;
			}
			case DYNAMIC_OBJECT: {
				if (!gameObjects.get(position.getZ() & 3).remove(entity)) {
					LOGGER.error("Unable to remove GameObject:" + entity + " in Chunk: " + this);
				}
				//add back original object in this spot
				DynamicGameObject removedObject = (DynamicGameObject)entity;
				removedObject.setIsActive(false);
				if (removedObject.getOriginalObject() != null) {
					GameObject originalObject = removedObject.getOriginalObject();
					if (notify) {
						notifyListeners(originalObject, originalObject.getId() == -1 ? EntityUpdateType.REMOVE : EntityUpdateType.ADD);
						notify = false;
					}
				} else {
					if (notify) {
						notifyListeners(entity, EntityUpdateType.REMOVE);
						notify = false;
					}
				}
				removedObject.isSpawned = false;
				break;
			}
			case ITEM: {
				entity.getAsItem().setRemovedFromRegion(true).setPendingRemoval(true);
				if (!groundItems.get(position.getZ() & 3).remove(entity)) {
					LOGGER.error("Unable to remove Ground_Item:" + entity + " Z:" + (position.getZ() & 3) + " in Chunk: " + this);
				}
				break;
			}
			case STATIC_OBJECT: {
				if (!STATIC_OBJECTS.get(position.getZ() & 3).remove(entity.getAsGameObject())) {
					LOGGER.error("Unable to remove STATIC_OBJECT:" + entity + " Z:" + (position.getZ() & 3) + " in Chunk: " + this);
				}
				break;
			}
		}

		if ((local == null || !local.remove(entity)) && entity.inPestControl()) {
			LOGGER.error("Entity (" + entity + ") belongs in (" + this + ") but does not exist.");
			return;
		}

		if (notify)
			notifyListeners(entity, EntityUpdateType.REMOVE);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("coordinates", coordinates).toString();
	}


	/**
	 * Checks that the specified {@link Position} is included in this Region.
	 *
	 * @param position The position.
	 * @throws IllegalArgumentException If the specified position is not included in this Region.
	 */
	private void checkPosition(Position position) {
		Preconditions.checkArgument(coordinates.equals(RegionCoordinates.fromPosition(position)),
			"Position is not included in this Region.");
	}

	/**
	 * Records the specified {@link GroupableEntity} as being updated this pulse.
	 *
	 * @param entity The GroupableEntity.
	 * @param update The {@link EntityUpdateType}.
	 * @throws UnsupportedOperationException If the specified Entity cannot be operated on in this manner.
	 */
	private <T extends Entity & GroupableEntity> void record(T entity, EntityUpdateType update) {
		final UpdateOperation<?> operation = entity.toUpdateOperation(this, update);
		final RegionUpdateMessage message = operation.toMessage();

		//Loop players who have this region rendered. If height level matches, add region coordinate to update list, if not, then add reset region if it's an object/item
		final boolean isTransient = entity.getEntityType().isTransient();
		for (int i = 0, l = subscribedPlayers.size(); i < l; i++) {
			Player player = subscribedPlayers.get(i);

			if (player.getPlane() == entity.getPlane()) {
				//send update to player
				//player.getUpdateRegionChunks().add(coordinates); // send to queue to update later, also need to add to updates list
				sendUpdate(player, operation, message); // instantly update
			} else {
				// if object or item, add to reset chunk if on different height
				if (!isTransient) { //ignore projectiles and gfx on diff heights
					player.getResetRegions().get(entity.getPlane() & 3).add(coordinates);
				}
			}
		}
	}

	public void displayPublicItem(Entity entity, EntityUpdateType update) {
		final UpdateOperation<?> operation = ((Entity & GroupableEntity)entity).toUpdateOperation(this, update);
		final RegionUpdateMessage message = operation.toMessage();

		for (int i = 0, l = subscribedPlayers.size(); i < l; i++) {
			Player player = subscribedPlayers.get(i);

			//ignore owner and display the new item to everyone else that rendered this region chunk
			if (player.getPlane() == entity.getPlane()) {
				sendUpdate(player, operation, message); // instantly update
			}
		}
	}

	/**
	 * Instantly sends UpdateOperation to the player, with use of canBeViewed(player)
	 * @param player
	 * @param operation
	 */
	private void sendUpdate(Player player, UpdateOperation<?> operation, RegionUpdateMessage message) {
		if(operation.canBeViewed(player)) {
			player.send(new SetUpdatedRegionMessage(
					player.getLastKnownRegion(),
					coordinates
			));
			player.send(message);
		}
	}

	private void sendUpdate(UpdateOperation<?> operation){
		for (RegionCoordinates coords : getSurrounding()) {
			final Region region = World.getRegions().get(coords);
			region.<Player>getEntities(EntityType.PLAYER).forEach(player -> {
				if(operation.canBeViewed(player)){
					player.send(new SetUpdatedRegionMessage(
							player.getLastKnownRegion(),
							coordinates
					));
					player.send(operation.toMessage());
				}
			});
		}
	}

	public boolean isEntitiesEmpty() {
		return this.entities.isEmpty();
	}

	/**
	 * Gets the new coordinates for an object/chunk tile when rotating.
	 *
	 * @param x             The current x-coordinate.
	 * @param y             The current y-coordinate.
	 * @param sizeX         The x-size of the object.
	 * @param sizeY         The y-size of the object.
	 * @param rotation      The object rotation.
	 * @param chunkRotation The chunk rotation.
	 * @return The new chunk position [x, y].
	 */
	public static int[] getRotatedPosition(int x, int y, int sizeX, int sizeY, int rotation, int chunkRotation) {
		if ((rotation & 0x1) == 1) {
			int s = sizeX;
			sizeX = sizeY;
			sizeY = s;
		}
		if (chunkRotation == 0) {
			return new int[]{x, y};
		}
		if (chunkRotation == 1) {
			return new int[]{y, 7 - x - (sizeX - 1)};
		}
		if (chunkRotation == 2) {
			return new int[]{7 - x - (sizeX - 1), 7 - y - (sizeY - 1)};
		}
		return new int[]{7 - y - (sizeY - 1), x};
	}

	public void copyFromChunk(Region from) {
		copyFromChunk(from, 0, 0, 0);
	}

	public void copyFromChunk(Region from, int z) {
		copyFromChunk(from, z, z, 0);
	}

	public void copyFromChunk(Region from, int fromZ, final int toZ, final int orientation) {
		this.rotation = orientation;
		fromZ = fromZ & 3;

		Iterator<GameObject> it = from.STATIC_OBJECTS.get(fromZ).iterator();

		//Floor clipping shitty way
		final int fromWorldX = from.getCoordinates().getX() << 3;
		final int fromWorldY = from.getCoordinates().getY() << 3;
		final int fromZFinal = fromZ;
		final Optional<CollisionMap> fromCollisionMapOptional = CollisionManager.getRegion(fromWorldX, fromWorldY);

		final int toChunkWorldX = coordinates.getX() << 3;
		final int toChunkWorldY = coordinates.getY() << 3;

		final Optional<CollisionMap> toCollisionMapOptional = CollisionManager.getRegion(toChunkWorldX, toChunkWorldY);

		if (fromCollisionMapOptional.isPresent() && toCollisionMapOptional.isPresent()) {
			CollisionMap fromCollMap = fromCollisionMapOptional.get();
			CollisionMap toCollMap = toCollisionMapOptional.get();
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					final int absX = fromWorldX + x;
					final int absY = fromWorldY + y;
					boolean isFloor = (fromCollMap.getClip(absX, absY, fromZFinal) & TileFlags.FLOOR) != 0;

					if (isFloor) {
						int[] newPos = getRotatedPosition(x, y, 1, 1, 0, rotation);
						final int newX = toChunkWorldX + newPos[0];
						final int newY = toChunkWorldY + newPos[1];

						toCollMap.addClip(newX, newY, toZ, TileFlags.FLOOR);
					}
				}
			}
		}

		//Object clipping
		while (it.hasNext()) {
			GameObject object = it.next();
			int[] newPos = getRotatedPosition(object.getPosition().getChunkOffsetX(), object.getPosition().getChunkOffsetY(), object.getWidth(), object.getHeight(), object.getFace(), rotation);
			int newX = (coordinates.getX() << 3) + newPos[0];
			int newY = (coordinates.getY() << 3) + newPos[1];
			CollisionManager.addStaticObject(object.getId(), newX, newY, toZ, object.getObjectType(), (object.getFace() + rotation) & 3);
		}
	}

}