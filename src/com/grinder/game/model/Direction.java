package com.grinder.game.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.grinder.game.collision.TileFlags;
import com.grinder.game.entity.agent.Agent;
import com.grinder.util.Misc;
import com.grinder.util.math.Vector2i;

import java.util.EnumSet;
import java.util.Optional;

import static com.grinder.util.Misc.abs;

/**
 * The enumerated type whose elements represent the movement directions.
 *
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public enum Direction {
    /**
     * No movement.
     */
    NONE(-1, 0, 0, -1),
    /**
     * North movement.
     */
    NORTH(1, 0, 1, 6),

    /**
     * North east movement.
     */
    NORTH_EAST(2, 1, 1, 5),

    /**
     * East movement.
     */
    EAST(4, 1, 0, 3),

    /**
     * South east movement.
     */
    SOUTH_EAST(7, 1, -1, 0),

    /**
     * South movement.
     */
    SOUTH(6, 0, -1, 1),

    /**
     * South west movement.
     */
    SOUTH_WEST(5, -1, -1, 2),

    /**
     * West movement.
     */
    WEST(3, -1, 0, 4),

    /**
     * North west movement.
     */
    NORTH_WEST(0, -1, 1, 7);

    public static Direction[] CARDINAL = new Direction[] { NORTH, EAST, SOUTH, WEST };

    public static Direction[] ORDINAL = new Direction[] { NORTH_WEST, NORTH_EAST, SOUTH_EAST, SOUTH_WEST };

    public static Direction[] VALID_DIRECTIONS = new Direction[] {NORTH, NORTH_EAST, NORTH_WEST, EAST, WEST, SOUTH, SOUTH_EAST, SOUTH_WEST};

    /**
     * The identification of this direction.
     */
    private final int id;

    /**
     * The {@code x} movement of this direction.
     */
    private final int x;

    /**
     * The {@code y} movement of this direction.
     */
    private final int y;

    /**
     * The opposite {@link #id} direction of the current direction.
     */
    private final int opposite;

    /**
     * Flag if this direction is diagonal.
     */
    private final boolean diagonal;

    /**
     * Caches our enumerated values.
     */
    public static final ImmutableSet<Direction> VALUES = Sets.immutableEnumSet(EnumSet.allOf(Direction.class));

    private final Vector2i directionVector;

    /**
     * An empty direction array.
     */
    public static final Direction[] EMPTY_DIRECTION_ARRAY = new Direction[0];

    /**
     * Creates a new {@link Direction}.
     *
     * @param id       the identification of this direction.
     * @param x        the {@code x} movement of this direction.
     * @param y        the {@code y} movement of this direction.
     * @param opposite the opposite {@link #id} direction.
     */
    Direction(int id, int x, int y, int opposite) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.opposite = opposite;
        this.diagonal = name().contains("_");
        directionVector = new Vector2i(x, y);
    }


    public boolean inFront(Position first, Position second, int width) {
        if(directionVector.getX() == 0) {
            return second.getY() - first.getY() == directionVector.getY()
                        && abs(second.getX() - first.getX()) <= width;
        }

        if(directionVector.getY() == 0) {
            return second.getX() - first.getX() == directionVector.getX()
                    && abs(second.getY() - first.getY()) <= width;
        }

        return false;
    }


    /**
     * Gets the identification of this direction.
     *
     * @return the identification of this direction.
     */
    public final int getId() {
        return id;
    }

    /**
     * Gets the {@code x} movement of this direction.
     *
     * @return the {@code x} movement of this direction.
     */
    public final int getX() {
        return x;
    }

    /**
     * Gets the {@code y} movement of this direction.
     *
     * @return the {@code y} movement of this direction.
     */
    public final int getY() {
        return y;
    }

    /**
     * Gets the {@code opposite} direction of this direction.
     *
     * @return the {@code opposite} direction of this direction.
     */
    public final int getOppositeId() {
        return opposite;
    }

    public Direction getOpposite(){
        return valueOf(opposite);
    }

    /**
     * Gets the {@code diagonal} flag from this direction.
     *
     * @return diagonal flag.
     */
    public final boolean isDiagonal() {
        return diagonal;
    }

    public static boolean isDiagonalDirection(Direction direction) {
        switch (direction) {
            case NORTH_EAST :
            case NORTH_WEST :
            case SOUTH_EAST :
            case SOUTH_WEST :
                return true;
        }
        return false;
    }
    /**
     * Returns a {@link Direction} wrapped in an {@link Optional}
     * for the specified {@code id}.
     *
     * @param id The game object orientation id.
     * @return The optional game object orientation.
     */
    public static Direction valueOf(int id) {
        if (id == 0)
            return NORTH_WEST;
        if (id == 1)
            return NORTH;
        if (id == 2)
            return NORTH_EAST;
        if (id == 3)
            return WEST;
        if (id == 4)
            return EAST;
        if (id == 5)
            return SOUTH_WEST;
        if (id == 6)
            return SOUTH;
        if (id == 7)
            return SOUTH_EAST;
        return NONE;
    }

    /**
     * Gets a random {@link Direction}.
     *
     * @return random direction.
     */
    public static Direction getRandomValidDirection() {
        return Misc.random(NORTH, NORTH_EAST, NORTH_WEST, EAST, WEST, SOUTH, SOUTH_EAST, SOUTH_WEST);
    }

    /**
     * Creates a direction from the differences between X and Y.
     *
     * @param dx The difference between two X coordinates.
     * @param dy The difference between two Y coordinates.
     * @return The direction.
     */
    public static Direction fromDeltas(int dx, int dy) {
        return getDirection(dx, dy);
    }

    public static Direction getDirection(int dx, int dy) {
        if (dx < 0) {
            if (dy < 0) {
                return SOUTH_WEST;
            } else if (dy > 0) {
                return NORTH_WEST;
            } else {
                return WEST;
            }
        } else if (dx > 0) {
            if (dy < 0) {
                return SOUTH_EAST;
            } else if (dy > 0) {
                return NORTH_EAST;
            } else {
                return EAST;
            }
        } else {
            if (dy < 0) {
                return SOUTH;
            } else if (dy > 0) {
                return NORTH;
            } else {
                return NONE;
            }
        }
    }

    /**
     * Creates a direction from the differences between X and Y.
     *
     * @param delta The delta position
     * @return The direction.
     */
    public static Direction fromDeltas(Position delta) {
        int dx = delta.getX();
        int dy = delta.getY();
        return getDirection(dx, dy);
    }

    public int[] getDirectionDelta() {
        switch (this) {
            case NORTH:
                return new int[]{0, 1};
            case NORTH_EAST:
                return new int[]{1, 1}; // TODO: check
            case EAST:
                return new int[]{1, 0};
            case SOUTH_EAST:
                return new int[]{1, -1}; // TODO: check
            case SOUTH:
                return new int[]{0, -1};
            case SOUTH_WEST:
                return new int[]{-1, -1}; // TODO: check
            case WEST:
                return new int[]{-1, 0};
            case NORTH_WEST:
                return new int[]{-1, 1}; // TODO: check
            default:
                return new int[]{0, 0};
        }
    }

    public static Direction getDirectionFacingPosition(Position position, Agent source) {
        Position delta = position.getDelta(source.getPosition());
        Vector2i direction = delta.toVector().toIntegerNormalized();
        return getDirection(direction);
    }
    public static Direction getDirection(Position position, Position from) {
        // TODO Auto-generated method stub
        return from.getDirection(position);
    }
    public static Direction getDirection(int direction) {
        if(direction < 0)
            return NONE;

        if (direction < values().length - 1)
            return values()[direction + 1];

        return NONE;
    }
    public static Direction getDirection(Vector2i directionVector) {
        for (Direction direction : values()) {
            Vector2i vector = direction.getDirectionVector();
            if (vector.getX() == directionVector.getX() && vector.getY() == directionVector.getY()) {
                return direction;
            }
        }
        return NONE;
    }
    public static boolean isDiagonal(Direction direction) {
        switch (direction) {
            case NORTH_EAST :
            case NORTH_WEST :
            case SOUTH_EAST :
            case SOUTH_WEST :
                return true;
        }
        return false;
    }

    public Vector2i getDirectionVector() {
        return directionVector;
    }

    public boolean isPerpendicular() {
        switch (this) {
            case NORTH:
            case EAST:
            case SOUTH:
            case WEST:
                return true;
            default:
                return false;
        }
    }

    public int getDirectionMask() {
        switch (this){
            case NORTH:return DirectionMask.NORTH;
            case NORTH_EAST:return DirectionMask.NORTH | DirectionMask.EAST;
            case NORTH_WEST:return DirectionMask.NORTH | DirectionMask.WEST;
            case SOUTH:return DirectionMask.SOUTH;
            case SOUTH_EAST:return DirectionMask.SOUTH | DirectionMask.EAST;
            case SOUTH_WEST:return DirectionMask.SOUTH | DirectionMask.WEST;
            case EAST: return DirectionMask.EAST;
            case WEST: return DirectionMask.WEST;
            case NONE:
                default:
                    return DirectionMask.NONE;
        }
    }

    public boolean isParent(final Direction direction){
        if(direction == EAST || direction == WEST){
            switch (this) {
                case NORTH_EAST:
                case SOUTH_EAST:
                    return direction == EAST;
                case NORTH_WEST:
                case SOUTH_WEST:
                    return direction == WEST;
            }
        } else if(direction == NORTH || direction == SOUTH) {
            switch (this) {
                case NORTH_EAST:
                case NORTH_WEST:
                    return direction == NORTH;
                case SOUTH_EAST:
                case SOUTH_WEST:
                    return direction == SOUTH;
            }
        }
        return direction == NONE;
    }

    public Direction getParent() {
        switch (this){
            case NORTH_EAST: return Misc.random(NORTH, EAST);
            case NORTH_WEST: return Misc.random(NORTH, WEST);
            case SOUTH_EAST: return Misc.random(SOUTH, EAST);
            case SOUTH_WEST: return Misc.random(SOUTH, WEST);
            case NONE:
            default:
                return this;
        }
    }
    public Direction[] getAsPair() {
        switch (this){
            case NORTH_EAST: return new Direction[]{NORTH, EAST};
            case NORTH_WEST: return new Direction[]{NORTH, WEST};
            case SOUTH_EAST: return new Direction[]{SOUTH, EAST};
            case SOUTH_WEST: return new Direction[]{SOUTH, WEST};
            case NONE:
            default:
                return new Direction[]{NONE, NONE};
        }
    }

    public int flag() {
        switch (this) {
            case NORTH:
                return TileFlags.NORTH;
            case NORTH_EAST:
                return TileFlags.NORTH_EAST;
            case EAST:
                return TileFlags.EAST;
            case SOUTH_EAST:
                return TileFlags.SOUTH_EAST;
            case SOUTH:
                return TileFlags.SOUTH;
            case SOUTH_WEST:
                return TileFlags.SOUTH_WEST;
            case WEST:
                return TileFlags.WEST;
            case NORTH_WEST:
                return TileFlags.NORTH_WEST;
            default:
                return 0;
        }
    }

    public Direction vertical() {
        switch (y) {
            case 1:
                return NORTH;
            case -1:
                return SOUTH;
            default:
                return NONE;
        }
    }

    public Direction horizontal() {
        switch (x) {
            case 1:
                return EAST;
            case -1:
                return WEST;
            default:
                return NONE;
        }
    }

    public int getNpcWalkValue(){
        switch (this) {
            case NONE: return -1;
            case NORTH_WEST: return 0;
            case NORTH: return 1;
            case NORTH_EAST: return 2;
            case WEST: return 3;
            case EAST: return 4;
            case SOUTH_WEST: return 5;
            case SOUTH: return 6;
            case SOUTH_EAST: return 7;
        }
        return -1;
    }

    private static final int[] WALK_X = new int[] { -1, 0, 1, -1, 1, -1, 0, 1 };
    private static final int[] WALK_Y = new int[] { -1, -1, -1, 0, 0, 1, 1, 1 };

    public int getClientWalkMask(){
        for (int i = 0; i < WALK_X.length; i++) {
            if (getX() == WALK_X[i] && getY() == WALK_Y[i]) {
                return i;
            }
        }
        /*switch (this){
            case SOUTH_WEST: return 0;  // -1,  -1
            case SOUTH: return 1;       //  0,  -1
            case SOUTH_EAST: return 2;  // +1,  -1
            case WEST: return 3;        // -1,   0
            case EAST: return 4;        // +1,   0
            case NORTH_WEST: return 5;  // -1,  +1
            case NORTH: return 6;       //  0,  +1
            case NORTH_EAST: return 7;  // +1,  +1
        }*/
        return -1;
    }

    private static final int[] RUN_X = new int[] { -2, -1, 0, 1, 2, -2, 2, -2, 2, -2, 2, -2, -1, 0, 1, 2 };
    private static final int[] RUN_Y = new int[] { -2, -2, -2, -2, -2, -1, -1, 0, 0, 1, 1, 2, 2, 2, 2, 2 };

    public int getClientRunMask(Direction runDir){
        int x = getX() + runDir.getX();
        int y = getY() + runDir.getY();
        for (int i = 0; i < RUN_X.length; i++) {
            if (x == RUN_X[i] && y == RUN_Y[i]) {
                return i;
            }
        }
        return -1;

       /* switch (this){
                                                //  x    y
            case SOUTH_WEST:                    // -1,  -1
                switch (runDir){
                    case SOUTH_WEST: return 0;  // -2,  -2
                    case SOUTH: return 1;       // -1,  -2
                    case SOUTH_EAST: return 2;  //  0,  -2
                    case WEST: return 5;        // -2,  -1
                    case EAST: return -1;       //  0,  -1  <- not configured in client
                    case NORTH_WEST: return 7;  // -2,   0
                    case NORTH: return -1;      // -1,   0  <- not configured in client
                    case NORTH_EAST: return -1; //  0,   0  <- not configured in client
                }
                break;
            case SOUTH:                         //  0,  -1
                switch (runDir){
                    case SOUTH_WEST: return 1;  // -1,  -2
                    case SOUTH: return 2;       //  0,  -2
                    case SOUTH_EAST: return 3;  // +1,  -2
                    case WEST: return -1;       // -1,  -1  <- not configured in client
                    case EAST: return -1;       // +1,  -1  <- not configured in client
                    case NORTH_WEST: return -1; // -1,   0  <- not configured in client
                    case NORTH: return -1;      //  0,   0  <- not configured in client
                    case NORTH_EAST: return -1; // +1,   0  <- not configured in client
                }
                break;
            case SOUTH_EAST:                    // +1,  -1
                switch (runDir){
                    case SOUTH_WEST: return 2;  //  0,  -2
                    case SOUTH: return 3;       // +1,  -2
                    case SOUTH_EAST: return 4;  // +2,  -2
                    case WEST: return -1;       //  0,  -1  <- not configured in client
                    case EAST: return 6;        // +2,  -1
                    case NORTH_WEST: return -1; //  0,   0  <- not configured in client
                    case NORTH: return -1;      // +1,   0  <- not configured in client
                    case NORTH_EAST: return 8;  // +2,   0
                }
                break;
            case WEST:                          // -1,   0
                switch (runDir){
                    case SOUTH_WEST: return 5;  // -2,  -1
                    case SOUTH: return -1;      // -1,  -1  <- not configured in client
                    case SOUTH_EAST: return -1; //  0,  -1  <- not configured in client
                    case WEST: return 7;        // -2,   0
                    case EAST: return -1;       //  0,   0  <- not configured in client
                    case NORTH_WEST: return 9;  // -2,  +1
                    case NORTH: return -1;      // -1,  +1  <- not configured in client
                    case NORTH_EAST: return -1; //  0,  +1  <- not configured in client
                }
                break;
            case EAST:                          // +1,   0
                switch (runDir){
                    case SOUTH_WEST: return -1; //  0,  -1  <- not configured in client
                    case SOUTH: return -1;      // +1,  -1  <- not configured in client
                    case SOUTH_EAST: return 6;  // +2,  -1
                    case WEST: return -1;       //  0,   0  <- not configured in client
                    case EAST: return 8;        // +2,   0
                    case NORTH_WEST: return -1; //  0,  +1  <- not configured in client
                    case NORTH: return -1;      // +1,  +1  <- not configured in client
                    case NORTH_EAST: return 10; // +2,  +1
                }
                break;
            case NORTH_WEST:                    // -1,  +1
                switch (runDir){
                    case SOUTH_WEST: return 7;  // -2,   0
                    case SOUTH: return -1;      // -1,   0  <- not configured in client
                    case SOUTH_EAST: return -1; //  0,   0  <- not configured in client
                    case WEST: return 9;        // -2,  +1
                    case EAST: return -1;       //  0,  +1  <- not configured in client
                    case NORTH_WEST: return 11; // -2,  +2
                    case NORTH: return 12;      // -1,  +2
                    case NORTH_EAST: return 13; //  0,  +2
                }
                break;
            case NORTH:                         //  0,  +1
                switch (runDir){
                    case SOUTH_WEST: return -1; // -1,   0  <- not configured in client
                    case SOUTH: return -1;      //  0,   0  <- not configured in client
                    case SOUTH_EAST: return -1; // +1,   0  <- not configured in client
                    case WEST: return -1;       // -1,  +1  <- not configured in client
                    case EAST: return -1;       // +1,  +1  <- not configured in client
                    case NORTH_WEST: return 12; // -1,  +2
                    case NORTH: return 13;      //  0,  +2
                    case NORTH_EAST: return 14; // +1,  +2
                }
                break;
            case NORTH_EAST:                    // +1,  +1
                switch (runDir){
                    case SOUTH_WEST: return -1; //  0,   0  <- not configured in client
                    case SOUTH: return -1;      // +1,   0  <- not configured in client
                    case SOUTH_EAST: return 8;  // +2,   0
                    case WEST: return -1;       //  0,  +1  <- not configured in client
                    case EAST: return 10;       // +2,  +1
                    case NORTH_WEST: return 13; //  0,  +2
                    case NORTH: return 14;      // +1,  +2
                    case NORTH_EAST: return 15; // +2,  +2
                }
                break;
        }
        return -1;*/
    }

    public int toInteger() {
        return id;
    }

    public static class DirectionMask {
        public static final int NONE = 0;
        public static final int NORTH = 0x1;
        public static final int EAST = 0x2;
        public static final int SOUTH = 0x4;
        public static final int WEST = 0x8;
    }

    public int getForceMovementMask() {
        switch(this) {
            default: case NORTH: return 0;
            case EAST: return 1;
            case SOUTH: return 2;
            case WEST: return 3;
        }
    }
}