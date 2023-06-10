package com.grinder.game.model;

import com.google.gson.annotations.Expose;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.util.Misc;
import com.grinder.util.math.Vector2i;
import com.grinder.util.oldgrinder.Area;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single world tile position.
 *
 * @author relex lawl
 */
public class Position {

	/**
	 * The number of height levels, (0, 3] inclusive.
	 */
	public static final int HEIGHT_LEVELS = 4;

	/**
	 * The maximum distance players/NPCs can 'see'.
	 */
	public static final int MAX_DISTANCE = 15;


	/**
	 * The x coordinate of the position.
	 */
	@Expose private int x;
	/**
	 * The y coordinate of the position.
	 */
	@Expose private int y;
	/**
	 * The height level of the position.
	 */
	@Expose private int z;

	/**
	 * The Position constructor.
	 *
	 * @param x
	 *            The x-type coordinate of the position.
	 * @param y
	 *            The y-type coordinate of the position.
	 * @param z
	 *            The height of the position.
	 */
	public Position(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * The Position constructor.
	 *
	 * @param x
	 *            The x-type coordinate of the position.
	 * @param y
	 *            The y-type coordinate of the position.
	 */
	public Position(int x, int y) {
		this(x, y, 0);
	}

	/**
	 * Gets the x coordinate of this position.
	 *
	 * @return The associated x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x coordinate of this position.
	 *
	 * @return The Position instance.
	 */
	public Position setX(int x) {
		this.x = x;
		return this;
	}

	/**
	 * Gets the y coordinate of this position.
	 *
	 * @return The associated y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y coordinate of this position.
	 *
	 * @return The Position instance.
	 */
	public Position setY(int y) {
		this.y = y;
		return this;
	}

	/**
	 * Gets the height level of this position.
	 *
	 * @return The associated height level.
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Sets the height level of this position.
	 *
	 * @return The Position instance.
	 */
	public Position setZ(int z) {
		this.z = z;
		return this;
	}

	/**
	 * Sets the player's associated Position values.
	 *
	 * @param x
	 *            The new x coordinate.
	 * @param y
	 *            The new y coordinate.
	 * @param z
	 *            The new height level.
	 */
	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		setZ(z);
	}

	public void setAs(Position other) {
		this.x = other.x;
		this.y = other.y;
		setZ(other.z);
	}

	/**
	 * Gets the local x coordinate relative to a specific region.
	 *
	 * @param position
	 *            The region the coordinate will be relative to.
	 * @return The local x coordinate.
	 */
	public int getLocalX(Position position) {
		return x - 8 * position.getRegionX();
	}

	/**
	 * Gets the local y coordinate relative to a specific region.
	 *
	 * @param position
	 *            The region the coordinate will be relative to.
	 * @return The local y coordinate.
	 */
	public int getLocalY(Position position) {
		return y - 8 * position.getRegionY();
	}

	/**
	 * Gets the local x coordinate relative to a specific region.
	 *
	 * @return The local x coordinate.
	 */
	public int getLocalX() {
		return x - 8 * getRegionX();
	}

	/**
	 * Gets the local y coordinate relative to a specific region.
	 *
	 * @return The local y coordinate.
	 */
	public int getLocalY() {
		return y - 8 * getRegionY();
	}

	/**
	 * Gets the region chunk's x coordinate based on player's perspective.
	 *
	 * @return The region x coordinate.
	 */
	public int getRegionX() {
		return (x >> 3) - 6;
	}

	/**
	 * Gets the region chunk's y coordinate based on player's perspective.
	 *
	 * @return The region y coordinate.
	 */
	public int getRegionY() {
		return (y >> 3) - 6;
	}
	
	/**
	 * Gets the region id.
	 * @return
	 */
	public int getRegionId() {
		return  ((x >> 6) << 8) | (y >> 6);
	}

	/**
	 * Gets the x of the position inside a region chunk
	 * @return x of an 8x8 region chunk
	 */
	public int getChunkOffsetX() {
		return x - ((x >> 3) << 3);
	}

	/**
	 * Gets the y of the position inside a region chunk
	 * @return y of an 8x8 region chunk
	 */
	public int getChunkOffsetY() {
		return y - ((y >> 3) << 3);
	}

	/**
	 * Gets the corner tile of the current chunk in world position.
	 * @return Corner position of the chunk
	 */
	public Position getChunkCorner() {
		return new Position((x >> 3) << 3, (y >> 3) << 3);
	}

	/**
	 * Gets the corner tile of the current region(64x64)
	 * @return Corner position of the region
	 */
	public Position getRegionCorner() {
		return new Position((x >> 6) << 6, (y >> 6) << 6);
	}

	/**
	 * Moves this position by the argued amounts.
	 *
	 * @param amountX
	 *            the amount to move the <code>x</code> coordinate.
	 * @param amountY
	 *            the amount to move the <code>y</code> coordinate.
	 * @return this position with the new coordinates.
	 */
	public Position move(int amountX, int amountY) {
		return move(amountX, amountY, 0);
	}
	public Position move(Direction direction) {
		Objects.requireNonNull(direction);
		return move(direction.getDirectionVector());
	}
	public Position move(Vector2i direction) {
		Objects.requireNonNull(direction);
		return move(direction.getX(), direction.getY());
	}

	/**
	 * Moves this position by the argued amounts.
	 *
	 * @param amountX
	 *            the amount to move the <code>x</code> coordinate.
	 * @param amountY
	 *            the amount to move the <code>y</code> coordinate.
	 * @param amountZ
	 *            the amount to move the <code>z</code> coordinate.
	 * @return this position with the new coordinates.
	 */
	public Position move(int amountX, int amountY, int amountZ) {
		this.x += amountX;
		this.y += amountY;
		setZ(this.z + amountZ);
//		dispatchEvent();
		return this;
	}

	public static void main(String[] args){
		Position position = new Position(10, 10, 10);
		position.add(0, -5);
		//System.out.println(position);
	}
	/**
	 * Adds steps/coordinates to this position.
	 */
	public Position add(int x, int y) {
		return add(x, y, 0);
	}

	/**
	 * Adds steps/coordinates to this position.
	 */
	public Position add(int x, int y, int z) {
		return move(x, y, z);
	}

	public Position addX(int x) {
		setX(this.x + x);
		return this;
	}

	public Position addY(int y) {
		setY(this.y + y);
		return this;
	}
	
	public Position transform(int diffX, int diffY, int diffZ) {
		return new Position(x + diffX, y + diffY, z + diffZ);
	}

	public List<Position> getArea(int radius) {
		List<Position> positionList = new ArrayList<>();

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= +radius; y++) {
				Position position = transform(x, y, z);
				positionList.add(position);
			}
		}

		return positionList;
	}

	public Vector2i toVector() {
		return new Vector2i(x, y);
	}

	/**
	 * Checks if this location is within range of another.
	 *
	 * @param other
	 *            The other location.
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinDistance(Position other) {
		if (z != other.z)
			return false;
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}
	public boolean isViewableFrom(Position other) {
		if (this.getZ() != other.getZ())
			return false;
		Position p = Misc.delta(this, other);
		return p.x <= 14 && p.x >= -15 && p.y <= 14 && p.y >= -15;
	}
	/**
	 * Checks if the position is within distance of another.
	 *
	 * @param other
	 *            The other position.
	 * @param distance
	 *            The distance.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean isWithinDistance(Position other, int distance) {
		return isWithinDistance(other.x, other.y, other.z, distance);
	}

	public boolean isWithinDistance(int x, int y, int z, int distance) {
		int deltaX = Math.abs(this.x - x);
		int deltaY = Math.abs(this.y - y);
		return deltaX <= distance && deltaY <= distance && this.z == z;
	}

	/**
	 * Checks if this location is within interaction range of another.
	 *
	 * @param other
	 *            The other location.
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinInteractionDistance(Position other) {
		if (z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 2 && deltaX >= -3 && deltaY <= 2 && deltaY >= -3;
	}

	/**
	 * Checks if the position is within distance of another.
	 *
	 * @param other
	 *            The other position.
	 * @param distance
	 *            A position object containing the difference in x and y.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean isWithinDistance(Position other, Position distance) {
		int deltaX = Math.abs(x - other.x);
		int deltaY = Math.abs(y - other.y);
		return deltaX <= distance.x && deltaY <= distance.y && z == other.getZ();
	}

	/**
	 * Gets the distance between this position and another position. Only X and
	 * Y are considered (i.e. 2 dimensions).
	 *
	 * @param other
	 *            The other position.
	 * @return The distance.
	 */
	public int getDistance(Position other) {
		return getDistance(other.x, other.y);
	}

	public int getDistance(int x, int y) {
		int deltaX = this.x - x;
		int deltaY = this.y - y;
		return (int) Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
	}

	public Position getDelta(Position position) {
		return new Position(x - position.x, y - position.y);
	}

	/**
	 * Checks if {@code position} has the same values as this position.
	 *
	 * @param position
	 *            The position to check.
	 * @return The values of {@code position} are the same as this position's.
	 */
	public boolean sameAs(Position position) {
		if(position == null)
			return false;
		return sameAs(position.x, position.y, position.z);
	}

	public boolean sameAs(int x, int y, int z) {
		return this.x == x && this.y == y && this.z == z;
	}

	public double distanceToPoint(int pointX, int pointY) {
		return Math.sqrt(Math.pow(x - pointX, 2) + Math.pow(y - pointY, 2));
	}
	/**
	 * Check if given position is located in a different region
	 *
	 * @param position
	 *            the position to be compared
	 * @return true in case position belongs to another region.
	 */
	public boolean isDifferentRegion(Position position) {
		int deltaX = position.getX() - getRegionX() * 8;
		int deltaY = position.getY() - getRegionY() * 8;
		return (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY > 88);

	}

    /**
	 * Returns if the entity's block is within distance of the other entity's
	 * block.
	 *
	 * @param other
	 * @param size
	 * @param otherSize
	 * @return if is within distance
	 */
	public boolean isWithinDiagonalDistance(Position other, int size, int otherSize) {
		int e_offset_x = size - 1;
		int e_offset_y = size - 1;

		int o_offset_x = otherSize - 1;
		int o_offset_y = otherSize - 1;

		boolean inside_entity = (other.getX() <= x + e_offset_x && other.getX() >= (x)) && (other.getY() <= y + e_offset_y && other.getY() >= (y));

		boolean inside_other = (x <= other.getX() + o_offset_x && x >= (other.getX()) && (y <= other.getY() + o_offset_y && y >= (other.getY())));

		return inside_entity || inside_other;
	}


	@Override
	public String toString() {
		return "Position values: [x, y, z] - [" + x + ", " + y + ", " + z + "].";
	}

	public String compactString(){
		return x+", "+y+", "+z;
	}

	@Override
	public int hashCode() {
		return z << 30 | x << 15 | y;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Position)) {
			return false;
		}
		Position position = (Position) other;
		return position.x == x && position.y == y && position.z == z;
	}



	public Position copy() {
		return new Position(x, y, z);
	}
	
	@Override
	public Position clone() {
		return copy();
	}

	public boolean is(Position position, boolean ignorePlane) {
		if (position == null) {
			return false;
		}
		return position.x == x && position.y == y && (ignorePlane || position.z == z);
	}

	public boolean inside(int west, int south, int east, int north) {
		return x >= west && x <= east && y >= south && y <= north;
	}

	public boolean inside(Area... areas) {
		if (areas == null) {
			return false;
		}
		for (Area area : areas) {
			if (inside(area)) {
				return true;
			}
		}
		return false;
	}

	public boolean inside(Area area) {
		return x >= area.getWest() && x <= area.getEast() && y >= area.getSouth() && y <= area.getNorth();
	}

	public Direction getDirection(Position position) {
		Vector2i delta = getDelta(position).toVector();
		delta = delta.toIntegerNormalized();
		return Direction.getDirection(delta);
	}

	/**
	 * Returns the delta coordinates. Note that the returned position is not an
	 * actual position, instead it's values represent the delta values between
	 * the two arguments.
	 * @param a the first position.
	 * @param b the second position.
	 * @return the delta coordinates contained within a position.
	 */
	public static Position delta(Position a, Position b) {
		return new Position(b.x - a.x, b.y - a.y);
	}

	/**
	 * Gets the x coordinate of the region.
	 *
	 * @return the region x coordinate.
	 */
	public int getTopLeftRegionX() {
		return x / 8 - 6;
	}

	/**
	 * Gets the y coordinate of the region.
	 *
	 * @return the region y coordinate.
	 */
	public int getTopLeftRegionY() {
		return y / 8 - 6;
	}
	/**
	 * Returns the base local x coordinate.
	 *
	 * @return The base local x coordinate.
	 */
	public int getBaseLocalX() {
		return getTopLeftRegionX() * 8;
	}

	/**
	 * Returns the base local y coordinate.
	 *
	 * @return The base local y coordinate.
	 */
	public int getBaseLocalY() {
		return getTopLeftRegionY() * 8;
	}

	public Position randomize(int radius) {
		return clone().add(Misc.random(-radius, radius), Misc.random(-radius, radius));
	}

	/**
	 * @author Graham (original = https://github.com/apollo-rsps/apollo/blob/kotlin-experiments/game/src/main/java/org/apollo/game/model/Position.java)
	 *
	 * Gets the longest horizontal or vertical delta between the two positions.
	 *
	 * @param other The other position.
	 * @return The longest horizontal or vertical delta.
	 */
	public int getLongestDelta(Position other) {
		int deltaX = Math.abs(getX() - other.getX());
		int deltaY = Math.abs(getY() - other.getY());
		return Math.max(deltaX, deltaY);
	}


	/**
	 * Gets the x coordinate of the central region.
	 *
	 * @return The x coordinate of the central region.
	 */
	public int getCentralRegionX() {
		return getX() / 8;
	}

	/**
	 * Gets the y coordinate of the central region.
	 *
	 * @return The y coordinate of the central region.
	 */
	public int getCentralRegionY() {
		return getY() / 8;
	}

	/**
	 * Returns the {@link RegionCoordinates} of the {@link Region} this position is inside.
	 *
	 * @return The region coordinates.
	 */
	public RegionCoordinates getRegionCoordinates() {
		return RegionCoordinates.fromPosition(this);
	}

	/**
	 * Creates a square boundary with sides of length 2*r.
	 */
	public List<Boundary> createSquareBoundary(int radius) {
		List<Boundary> boundaries = new ArrayList<>();

		// Top and bottom 
		boundaries.add(new Boundary(x-radius, x+radius, y-radius, y+radius));
		
		return boundaries;
	}

    @NotNull
    public Position divide(int divisor) {
        return new Position(x/divisor, y/divisor, z);
    }

    public int pack() {
    	return z << 28 | y << 14 | x;
	}
}