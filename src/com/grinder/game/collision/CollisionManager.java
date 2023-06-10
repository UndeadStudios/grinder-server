package com.grinder.game.collision;

import com.google.gson.GsonBuilder;
import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.content.minigame.barrows.BarrowsManager;
import com.grinder.game.definition.ObjectDefinition;
import com.grinder.game.definition.VarDefinition;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.util.Buffer;
import com.grinder.util.compress.CompressionUtil;
import com.grinder.util.io.FileUtil;
import kotlin.Deprecated;
import kotlin.Pair;

import javax.naming.OperationNotSupportedException;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import static com.grinder.game.collision.TileFlags.FLOOR;

/**
 * This manager handles all regions and their related functions, such as
 * clipping.
 *
 * @author Professor Oak
 *
 */
public class CollisionManager {

	public static final int UNKNOWN = 0x80000;
	public static final int BLOCKED_TILE = 0x200000;
	public static final int UNLOADED_TILE = 0x1000000;
	public static final int OCEAN_TILE = 2097152;

	private static String lastWarnMessage = "";
	private static boolean loading = true;

	private static Set<Integer> missingRegions = new HashSet<>();

	private static final Set<Integer> OLD_REGIONS = new HashSet<>(Arrays.asList(
			13117, 6722, 10039, 10388, 8521, 12848, 10568, 7994, 8250, 7993, 8249, 10314, 12953, 12342, 12343,
			12085, 12341, 12086, 10056, 11928, 10057, 11569, 10044, 14235, 11837, 12861, 11679, 13357, 11343, 14638,
			13209, 10537, 11304, 11816, 11328, 12616, 10558, 9275, 9886, 13133, 13641, 13642, 9512, 6965, 9000, 6966,
			8488, 9005, 5168, 5166, 5162, 5423, 4912, 4911, 5422, 5167, 11842, 4910, 5424, 6223, 11317, 12701, 13886
	));

	/**
	 * The map with all of our regions.
	 */
	public static Map<Integer, CollisionMap> regions = new HashMap<Integer, CollisionMap>();

	/**
	 * Loads the client's map_index file and constructs
	 * new regions based on the data it holds.
	 * @throws Exception
	 */
	public static void init() throws Exception {
		// Load object definitions..
		ObjectDefinition.init();
		VarDefinition.init();

		// Load regions..
		File map_index = new File(GameConstants.CLIPPING_DIRECTORY + "map_index");
		if (!map_index.exists()) {
			throw new OperationNotSupportedException("map_index was not found!");
		}
		byte[] data = Files.readAllBytes(map_index.toPath());
		Buffer stream = new Buffer(data);
		int size = stream.readUShort();
		for (int i = 0; i < size; i++) {
			int regionId = stream.readUShort();
			int terrainFile = stream.readUShort();
			int objectFile = stream.readUShort();
			CollisionManager.regions.put(regionId, new CollisionMap(regionId, terrainFile, objectFile));
		}

		loading = false;

		BarrowsManager.load();
	}

	/**
	 * Attempts to get a {@link CollisionMap} based on an id.
	 *
	 * @param regionId
	 * @return
	 */
	public static Optional<CollisionMap> getRegion(int regionId) {
		CollisionMap region = regions.get(regionId);
		if (region != null) {
			return Optional.of(region);
		}
		return Optional.empty();
	}

	/**
	 * Attempts to get a {@link CollisionMap} based on coordinates.
	 *
	 * @return
	 */
	public static Optional<CollisionMap> getRegion(int x, int y) {
		tryLazyLoadRegionAt(x, y);

		int regionId = getRegionId(x, y);
		return getRegion(regionId);
	}

	private static int getRegionId(int x, int y) {
		int regionX = x >> 3;
		int regionY = y >> 3;
		return ((regionX / 8) << 8) + (regionY / 8);
	}

	/**
	 * Adds an object to a region.
	 *
	 * @param objectId
	 * @param x
	 * @param y
	 * @param height
	 * @param type
	 * @param direction
	 */
	public static void addStaticObject(int objectId, int x, int y, int height, int type, int direction) {
		final Position position = new Position(x, y, height);
		if (objectId == -1) {
			ClippedMapObjects.removeAll(position);
		} else {
			final StaticGameObject gameObject = new StaticGameObject(objectId, position, type, direction);
			World.getRegions().fromPosition(position).addEntity(gameObject, false);
			addObjectClipping(gameObject);
		}
	}

	public static void addObjectClipping(int objectId, int x, int y, int height, int type, int direction) {
		addObjectClipping(StaticGameObjectFactory.produce(objectId, new Position(x, y, height), type, direction));
	}

	public static void addObjectClipping(GameObject object) {
		if(object.getId() == -1) {
			removeObjectClipping(object);
			return;
		}
		GameObjectCollision.modifyCollision(object, GameObjectCollision.ADD_MASK);
	}

	public static void removeObjectClipping(GameObject object) {
		GameObjectCollision.modifyCollision(object, GameObjectCollision.REMOVE_MASK);
	}

	/**
	 * Attempts to add clipping to a region.
	 *
	 * @param x the x coordinate in the world
	 * @param y the y coordinate in the world
	 * @param height the plane of the coordinates in the world.
	 * @param shift the shift value applied to the collision mask at the specified coordinates.
	 */
	public static void addClipping(int x, int y, int height, int shift) {

		final Optional<CollisionMap> optionalRegion = getRegion(x, y);

		optionalRegion.ifPresentOrElse(
				region -> region.addClip(x, y, height, shift),
				() -> {
					addMissingRegion(x, y);
					warn(false, "region not found -> could not add clip {" + x + ", " + y + ", " + height + ", " + shift + "} to");
				});
	}
    /**
     * Attempts to set clipping of a tile in a a region.
     *
     * @param x the x coordinate in the world
     * @param y the y coordinate in the world
     * @param height the plane of the coordinates in the world.
     * @param mask the shift value applied to the collision mask at the specified coordinates.
     */
    public static void setMask(int x, int y, int height, int mask) {

        final Optional<CollisionMap> optionalRegion = getRegion(x, y);

        optionalRegion.ifPresentOrElse(
        		region -> region.setClip(x, y, height, mask),
				() -> {
					addMissingRegion(x, y);
					warn(false, "region not found -> could not set mask(" + mask + ") at {" + x + ", " + y + "}");
				});
    }

	private static void addMissingRegion(int x, int y) {
    	if(missingRegions.add(getRegionId(x, y))){
    		final File file = Paths.get("missing_regions.json").toFile();
			try {

				if(!file.exists()){
					file.createNewFile();
				}

				final FileWriter writer = new FileWriter(file);

				new GsonBuilder().create().toJson(missingRegions, writer);

				writer.flush();
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void warn(boolean dumpStack, String msg) {
//		LOGGER.warning(msg);
//		if(dumpStack && !lastWarnMessage.equals(msg))
//			Thread.dumpStack();
//		lastWarnMessage = msg;
	}

	public static void setMasksWidth(int startX, int startY, int height, int stepsX, int mask) {
    	if(stepsX > 0) {
			for (int x = startX; x < startX + stepsX; x++){
				setMask(x, startY, height, mask);
			}
		} else if(stepsX < 0) {
    		for (int x = startX + stepsX; x < startX; x++){
    			setMask(x, startY, height, mask);
    		}
		} else {
    		setMask(startX, startY, height, mask);
		}
	}
	public static void setMasksHeight(int startX, int startY, int height, int stepsY, int mask) {
		if(stepsY > 0) {
			for (int y = startY; y < startY + stepsY; y++){
				setMask(startX, y, height, mask);
			}
		} else if(stepsY < 0) {
			for (int y = startY + stepsY; y < startY; y++){
				setMask(startX, y, height, mask);
			}
		} else {
			setMask(startX, startY, height, mask);
		}
	}
	/**
	 * Attempts to clear clipping from a point in a region
	 * Note: Clear removes all clipping, to remove clipping of one object use {@link CollisionManager#removeClipping(Position, int)} instead
	 * @param x the x coordinate in the world
	 * @param y the y coordinate in the world
	 * @param height the plane of the coordinates in the world.
	 */
	public static void clearClipping(int x, int y, int height) {

		final Optional<CollisionMap> optionalRegion = getRegion(x, y);

		optionalRegion.ifPresent(region -> region.clearClip(x, y, height));
	}

	public static void clearClipping(Position position) {
		clearClipping(position.getX(), position.getY(), position.getZ());
	}

	/**
	 * Attempts to remove clipping from a point in a region
	 *
	 * @param x the x coordinate in the world
	 * @param y the y coordinate in the world
	 * @param height the plane of the coordinates in the world.
	 * @param mask the clipping mask to remove
	 */
	public static void removeClipping(int x, int y, int height, int mask) {

		final Optional<CollisionMap> optionalRegion = getRegion(x, y);

		optionalRegion.ifPresent(region -> region.removeClip(x, y, height, mask));
	}

	public static void removeClipping(Position position, int mask) {
		removeClipping(position.getX(), position.getY(), position.getZ(), mask);
	}

	public static int getClipping(Position position){
		return getClipping(position.getX(), position.getY(), position.getZ());
	}

	/**
	 * Attempts to get the clipping for a region.
	 *
	 * @param x the x coordinate in the world
	 * @param y the y coordinate in the world
	 * @param height the plane of the coordinates in the world.
	 * @return the collision mask at the specified coordinates.
	 */
	public static int getClipping(int x, int y, int height) {

		final Optional<CollisionMap> optionalRegion = getRegion(x, y);

		if(optionalRegion.isEmpty()) {
			addMissingRegion(x, y);
			warn(true, "region not found -> could not get clip at {"+x+", "+y+", "+height+"}");
		}

		return optionalRegion.map(region -> region.getClip(x, y, height)).orElse(-1);
	}

	public static int getClipping2(int x, int y, int height) {

		final Optional<CollisionMap> optionalRegion = getRegion(x, y);

		if(optionalRegion.isEmpty()) {
			addMissingRegion(x, y);
			warn(true, "region not found -> return 0x200000 clip at {"+x+", "+y+", "+height+"}");
		}

		return optionalRegion.map(region -> region.getClip(x, y, height)).orElse(0x200000);
	}
	/**
	 * Determines whether the tile in the {@link Direction} from the starting {@link Position} is open.
	 *
	 * @param pos the start {@link Position}.
	 * @param direction the {@link Direction} to move in.
	 *
	 * @return {@code true} if the next tile is open or else {@code false}.
	 */
	@Deprecated(message = "Use TraversalStrategies")
	public static boolean canMove(Position pos, int direction) {
		switch (direction) {
			case 0: return !blockedNorthWest(pos) && !blockedNorth(pos) && !blockedWest(pos);
			case 1: return !blockedNorth(pos);
			case 2: return !blockedNorthEast(pos) && !blockedNorth(pos) && !blockedEast(pos);
			case 3: return !blockedWest(pos);
			case 4: return !blockedEast(pos);
			case 5: return !blockedSouthWest(pos) && !blockedSouth(pos) && !blockedWest(pos);
			case 6: return !blockedSouth(pos);
			case 7: return !blockedSouthEast(pos) && !blockedSouth(pos) && !blockedEast(pos);
		}
		return false;
	}

	@Deprecated(message = "Use TraversalStrategies")
	public static boolean blocked(Position pos, Direction direction){
		switch (direction){
			case NONE:
				return false;
			case NORTH:
				return blockedNorth(pos);
			case NORTH_EAST:
				return blockedNorthEast(pos);
			case EAST:
				return blockedEast(pos);
			case SOUTH_EAST:
				return blockedSouthEast(pos);
			case SOUTH:
				return blockedSouth(pos);
			case SOUTH_WEST:
				return blockedSouthWest(pos);
			case WEST:
				return blockedWest(pos);
			case NORTH_WEST:
				return blockedNorthWest(pos);
		}
		return false;
	}

	public static boolean open(Position position){
		return !blocked(position);
	}

	public static boolean blocked(Position pos) {
		return (getClipping(pos.getX(), pos.getY(), pos.getZ()) & 0x1280120) != 0;
	}

	private static boolean blockedNorth(Position pos) {
		return (getClipping(pos.getX(), pos.getY() + 1, pos.getZ()) & 0x1280120) != 0;
	}

	private static boolean blockedEast(Position pos) {
		return (getClipping(pos.getX() + 1, pos.getY(), pos.getZ()) & 0x1280180) != 0;
	}

	private static boolean blockedSouth(Position pos) {
		return (getClipping(pos.getX(), pos.getY() - 1, pos.getZ()) & 0x1280102) != 0;
	}

	private static boolean blockedWest(Position pos) {
		return (getClipping(pos.getX() - 1, pos.getY(), pos.getZ()) & 0x1280108) != 0;
	}

	private static boolean blockedNorthEast(Position pos) {
		return (getClipping(pos.getX() + 1, pos.getY() + 1, pos.getZ()) & 0x12801e0) != 0;
	}

	private static boolean blockedNorthWest(Position pos) {
		return (getClipping(pos.getX() - 1, pos.getY() + 1, pos.getZ()) & 0x1280138) != 0;
	}

	private static boolean blockedSouthEast(Position pos) {
		return (getClipping(pos.getX() + 1, pos.getY() - 1, pos.getZ()) & 0x1280183) != 0;
	}

	private static boolean blockedSouthWest(Position pos) {
		return (getClipping(pos.getX() - 1, pos.getY() - 1, pos.getZ()) & 0x128010e) != 0;
	}

	private static boolean canMove3(final int startX, final int startY, final int endX, final int endY, int height, final int xLength, final int yLength) {
		if (height > 4) {
			height = height % 4;
		} else if (height < 0) {
			height = 0;
		}
		int diffX = endX - startX;
		int diffY = endY - startY;
		final int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int step = 0; step < max; step++) {
			final int currentX = endX - diffX;
			final int currentY = endY - diffY;
			for (int xStep = 0; xStep < xLength; xStep++) {
				for (int yStep = 0; yStep < yLength; yStep++) {
					if (diffX < 0 && diffY < 0) {
						if ((getClipping2(currentX + xStep - 1, currentY + yStep - 1, height) & 0x128010e) != 0 || (getClipping2(currentX + xStep - 1, currentY + yStep, height) & 0x1280108) != 0 || (getClipping2(currentX + xStep, currentY + yStep - 1, height) & 0x1280102) != 0)
							return false;
					} else if (diffX > 0 && diffY == 0) {
						if ((getClipping2(currentX + xStep + 1, currentY + yStep, height) & 0x1280180) != 0)
							return false;
					} else if (diffX < 0 && diffY == 0) {
						if ((getClipping2(currentX + xStep - 1, currentY + yStep, height) & 0x1280108) != 0)
							return false;
					} else if (diffX == 0 && diffY > 0) {
						if ((getClipping2(currentX + xStep, currentY + yStep + 1, height) & 0x1280120) != 0)
							return false;
					} else if (diffX == 0 && diffY < 0)
						if ((getClipping2(currentX + xStep, currentY + yStep - 1, height) & 0x1280102) != 0)
							return false;
				}
			}
			if (diffX < 0)
				diffX++;
			else if (diffX > 0)
				diffX--;
			if (diffY < 0)
				diffY++;
			else if (diffY > 0)
				diffY--;
		}
		return true;
	}

	private static boolean canMove(int startX, int startY, int endX, int endY, int height, int xLength, int yLength) {
		int diffX = endX - startX;
		int diffY = endY - startY;
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int ii = 0; ii < max; ii++) {
			int currentX = endX - diffX;
			int currentY = endY - diffY;
			for (int i = 0; i < xLength; i++) {
				for (int i2 = 0; i2 < yLength; i2++)
					if (diffX < 0 && diffY < 0) {
						if ((getClipping((currentX + i) - 1, (currentY + i2) - 1, height) & 0x128010e) != 0
								|| (getClipping((currentX + i) - 1, currentY + i2, height) & 0x1280108) != 0
								|| (getClipping(currentX + i, (currentY + i2) - 1, height) & 0x1280102) != 0)
							return false;
					} else if (diffX > 0 && diffY > 0) {
						if ((getClipping(currentX + i + 1, currentY + i2 + 1, height) & 0x12801e0) != 0
								|| (getClipping(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
								|| (getClipping(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0)
							return false;
					} else if (diffX < 0 && diffY > 0) {
						if ((getClipping((currentX + i) - 1, currentY + i2 + 1, height) & 0x1280138) != 0
								|| (getClipping((currentX + i) - 1, currentY + i2, height) & 0x1280108) != 0
								|| (getClipping(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0)
							return false;
					} else if (diffX > 0 && diffY < 0) {
						if ((getClipping(currentX + i + 1, (currentY + i2) - 1, height) & 0x1280183) != 0
								|| (getClipping(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
								|| (getClipping(currentX + i, (currentY + i2) - 1, height) & 0x1280102) != 0)
							return false;
					} else if (diffX > 0 && diffY == 0) {
						if ((getClipping(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0)
							return false;
					} else if (diffX < 0 && diffY == 0) {
						if ((getClipping((currentX + i) - 1, currentY + i2, height) & 0x1280108) != 0)
							return false;
					} else if (diffX == 0 && diffY > 0) {
						if ((getClipping(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0)
							return false;
					} else if (diffX == 0 && diffY < 0
							&& (getClipping(currentX + i, (currentY + i2) - 1, height) & 0x1280102) != 0)
						return false;

			}

			if (diffX < 0)
				diffX++;
			else if (diffX > 0)
				diffX--;
			if (diffY < 0)
				diffY++;
			else if (diffY > 0)
				diffY--;
		}

		return true;
	}

	@Deprecated(message = "Use TargetStrategies")
	public static boolean canMove(Position start, Position end, int xLength, int yLength) {
		return canMove(start.getX(), start.getY(), end.getX(), end.getY(), start.getZ(), xLength, yLength);
	}

	@Deprecated(message = "Use TargetStrategies")
	public static boolean canMove3(Position start, Position end, int xLength, int yLength) {
		return canMove3(start.getX(), start.getY(), end.getX(), end.getY(), start.getZ(), xLength, yLength);
	}
	/**
	 * Attemps to load the map files related to this region...
	 */
	public static void tryLazyLoadRegionAt(int x, int y) {

		int regionId = getRegionId(x, y);

		Optional<CollisionMap> r = getRegion(regionId);

		if (r.isEmpty())
			return;

		if (r.get().isLoaded()) {
			return;
		}

		r.get().setLoaded(true);

		{
			byte[] oFileData, gFileData;

			try {
				oFileData = CompressionUtil.gunzip(
						FileUtil.readFile(GameConstants.CLIPPING_DIRECTORY + "maps/" + r.get().getObjectFile() + ".gz"));
			} catch (IOException e) {
				e.printStackTrace();
				oFileData = null;
			}

			try {
				gFileData = CompressionUtil.gunzip(
						FileUtil.readFile(GameConstants.CLIPPING_DIRECTORY + "maps/" + r.get().getTerrainFile() + ".gz"));
			} catch (IOException e) {
				e.printStackTrace();
				gFileData = null;
			}

			// Don't allow ground file to be invalid..
			if (gFileData == null) {
				return;
			}

			readTile(r, oFileData, gFileData);
		}
		// CHEAP HAX: To be removed later once we have support for doors/gates to force movement.
		// This fixes the south entrance of Wilderness agility so you can walk across it.
		// The reason for this is we remove objects client/server sided using ObjectDefinitions, but yet the clipping is still there.
		// This fixes it so you can walk when there is no gate.


		switch (regionId) {
			/*case 11837: // Wilderness agility
				CollisionManager.clearClipping(2998, 3916, 0);
				break;*/
			/*case 11575: // Warrrior's guild 1st floor
				CollisionManager.clearClipping(2851, 3550, 0);
				break;
			case 11319: // Warrrior's guild 2nd floor
				CollisionManager.clearClipping(2838, 3539, 1);
				break;*/
			/*case 13623: // Slayer Tower 2nd Floor
				CollisionManager.clearClipping(3445, 3554, 2);
				break;*/
			/*case 12342: // Xmas event edge snow (Remove after xmas event)
				CollisionManager.addClipping(3090, 3493, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3092, 3488, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3095, 3488, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3096, 3488, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3103, 3481, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3099, 3472, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3096, 3470, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3084, 3471, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3088, 3478, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3083, 3476, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3112, 3476, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3114, 3480, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3111, 3496, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3108, 3496, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3110, 3509, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3114, 3512, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3112, 3517, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3106, 3516, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3110, 3503, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3099, 3503, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3110, 3512, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3086, 3502, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3081, 3503, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3088, 3506, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3073, 3503, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3082, 3506, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3083, 3508, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3084, 3508, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3079, 3508, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3079, 3509, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3078, 3510, 0, CollisionManager.BLOCKED_TILE);
				break;*/
			case 10142: // Dagannoth Near Ladder Area
				CollisionManager.clearClipping(2543, 10143, 0);
				CollisionManager.clearClipping(2545, 10145, 0);
				CollisionManager.clearClipping(2545, 10141, 0);
				break;
			case 9000: // Members bossing zone slash bash tile
				CollisionManager.addClipping(2267, 2579, 0, CollisionManager.BLOCKED_TILE);
				break;
			case 10388: // The Untouchable agility obstacle
				CollisionManager.addClipping(2580, 9513, 0, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(2580, 9519, 0, CollisionManager.BLOCKED_TILE);
				break;
			case 12850: // Lumbridge Top Bank
				CollisionManager.addClipping(3208, 3221, 2, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3209, 3221, 2, CollisionManager.BLOCKED_TILE);
				CollisionManager.addClipping(3209, 3216, 0, CollisionManager.BLOCKED_TILE); // Trapdoor
				break;
			/*case 9781: // South of gnome agility course gate
				CollisionManager.clearClipping(2461, 3384, 0);
				CollisionManager.clearClipping(2461, 3383, 0);
				break;*/

		}


	}

	private static void readTile(Optional<CollisionMap> r, byte[] oFileData, byte[] gFileData) {
		int regionId = r.get().getRegionId();
		boolean old = OLD_REGIONS.contains(r.get().getRegionId());
		// Read values using our streams..
		Buffer groundStream = new Buffer(gFileData);
		int absX = (r.get().getRegionId() >> 8) * 64;
		int absY = (r.get().getRegionId() & 0xff) * 64;
		byte[][][] heightMap = new byte[4][64][64];
		for (int z = 0; z < 4; z++) {
			for (int tileX = 0; tileX < 64; tileX++) {
				for (int tileY = 0; tileY < 64; tileY++) {
					while (true) {
						int tileType = old ? groundStream.readUnsignedByte() : groundStream.readUShort();
						if (tileType == 0) {
							break;
						} else if (tileType == 1) {
							groundStream.readUnsignedByte();
							break;
						} else if (tileType <= 49) {
							if (old) {
								groundStream.readSignedByte();
							} else {
								groundStream.readShort();
							}
						} else if (tileType <= 81) {
							heightMap[z][tileX][tileY] = (byte) (tileType - 49);
						}
					}
				}
			}
		}

		byte[] finalOFileData = oFileData;
		{
			for (int i = 0; i < 4; i++) {
				for (int i2 = 0; i2 < 64; i2++) {
					for (int i3 = 0; i3 < 64; i3++) {
						if ((heightMap[i][i2][i3] & 1) == 1) {
							int height = i;
							if ((heightMap[1][i2][i3] & 2) == 2) {
								height--;
							}
							if (height >= 0 && height <= 3) {
								CollisionManager.addClipping(absX + i2, absY + i3, height, FLOOR);
							}
						}
					}
				}
			}
			if (finalOFileData != null) {
				Buffer objectStream = new Buffer(finalOFileData);
				int objectId = -1;
				int incr;
				while ((incr = objectStream.readUnsignedIntSmartShortCompat()) != 0) {
					objectId += incr;
					int location = 0;
					int incr2;

					while ((incr2 = objectStream.readUnsignedShortSmart()) != 0) {
						location += incr2 - 1;

						int localX = (location >> 6 & 0x3f);
						int localY = (location & 0x3f);
						int height = location >> 12;
						int hash = objectStream.readUnsignedByte();
						int type = hash >> 2;
						int direction = hash & 0x3;

						if ((heightMap[1][localX][localY] & 2) == 2)
							height--;

						if (height >= 0 && height <= 3) {
							CollisionManager.addStaticObject(objectId, absX + localX, absY + localY, height, type, direction);
						}
					}
				}
			}
		}
	}

	@Deprecated(message= "Broken algorithm - use LineOfSight.kt")
	public static ArrayBlockingQueue<Point>LineOfSightTiles(Position p1, Position p2) {
		return calculateLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	private static ArrayBlockingQueue<Point> calculateLine(int x1, int y1, int x2, int y2) {
		// delta of exact value and rounded value of the dependant variable
		ArrayBlockingQueue<Point> steps = new ArrayBlockingQueue<Point>(100);
		int d = 0;

		int dy = Math.abs(y2 - y1);
		int dx = Math.abs(x2 - x1);

		int dy2 = (dy << 1); // slope scaling factors to avoid floating
		int dx2 = (dx << 1); // point

		int ix = x1 < x2 ? 1 : -1; // increment direction
		int iy = y1 < y2 ? 1 : -1;

		if (dy <= dx) {
			for (;;) {
				if (steps.size() > 99) {
					break;
				}
				steps.add(new Point(x1, y1));
				if (x1 == x2)
					break;
				x1 += ix;
				d += dy2;
				if (d > dx) {
					y1 += iy;
					d -= dx2;
				}
			}
		} else {
			for (;;) {
				if (steps.size() > 99) {
					break;
				}
				steps.add(new Point(x1, y1));
				if (y1 == y2)
					break;
				y1 += iy;
				d += dx2;
				if (d > dy) {
					x1 += ix;
					d -= dy2;
				}
			}
		}
		return steps;
	}

}
