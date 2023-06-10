package com.grinder.game.collision;

import com.grinder.Server;

/**
 * Represents the collision map for a region.
 *
 * @author Professor Oak
 */
public final class CollisionMap {

	private final int regionId;
	private final int regionAbsoluteX;
	private final int regionAbsoluteY;

	private final int terrainFile;
	private final int objectFile;

	public final int[][][] clips = new int[4][][];

	private boolean loaded;

	/**
	 * Creates a new region.
	 *
	 * @param regionId		the id of the region that this map contains clipping for.
	 * @param terrainFile	the file id that contain terrain data (landscape).
	 * @param objectFile	the file id that contains object data.
	 */
	public CollisionMap(int regionId, int terrainFile, int objectFile) {
		this.regionId = regionId;
		this.terrainFile = terrainFile;
		this.objectFile = objectFile;
		regionAbsoluteX = (regionId >> 8) * 64;
		regionAbsoluteY = (regionId & 0xff) * 64;
	}

	public int getRegionId() {
		return regionId;
	}

	public int getTerrainFile() {
		return terrainFile;
	}

	public int getObjectFile() {
		return objectFile;
	}

	private int checkHeight(int height) {
		height = height & 3;
		if (clips[height] == null)
			clips[height] = new int[64][64];
		return height;
	}

	/**
	 * Gets the collision mask at provided coordinates.
	 *
	 * @param x 		the absolute (world) x coordinate.
	 * @param y 		the absolute (world) y coordinate.
	 * @param height   	the relative plane (in 0..4).
	 * @return an integer representing various flags that determine pathfinding behaviour.
	 */
	public int getClip(int x, int y, int height) {
		height = checkHeight(height);
		if (x < 0) {
			return -1;
		}
		if (y < 0) {
			return -1;
		}

		if (x - regionAbsoluteX < 0
				|| y - regionAbsoluteY < 0
				|| x - regionAbsoluteX > 63
				|| y - regionAbsoluteY > 63) {
			Server.getLogger().warn(
					"Attempted to get clipping at ("+x+", "+y+", "+height+") " +
					"from outside region! ("+regionId+", "+regionAbsoluteX+", "+regionAbsoluteY+")");
			return -1;
		}

		if (clips[height] == null)
			return 0;

		return clips[height][x - regionAbsoluteX][y - regionAbsoluteY];
	}

	public void addClip(int x, int y, int height, int shift) {
		height = checkHeight(height);
		clips[height][x - regionAbsoluteX][y - regionAbsoluteY] |= shift;
	}

	public void setClip(int x, int y, int height, int mask){
		height = checkHeight(height);
		clips[height][x - regionAbsoluteX][y - regionAbsoluteY] = mask;
	}

	public void clearClip(int x, int y, int height) {
		height = checkHeight(height);
		clips[height][x - regionAbsoluteX][y - regionAbsoluteY] = 0;
	}

	public void removeClip(int x, int y, int height, int mask) {
		height = checkHeight(height);
        clips[height][x - regionAbsoluteX][y - regionAbsoluteY] &= ~mask;
    }

	public boolean isLoaded() {
		return loaded;
	}

	public CollisionMap setLoaded(boolean loaded) {
		this.loaded = loaded;
		return this;
	}
}