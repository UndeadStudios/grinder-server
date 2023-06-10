package com.grinder.game.model;

import com.grinder.game.collision.CollisionManager;

import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-19
 */
public class PositionUtil {

    public static Optional<Position> findFreeTileEnclosing(final Position center){

        for(final Direction direction : Direction.values()){

            final Position position = center.copy().move(direction);

            if(!CollisionManager.blocked(position))
                return Optional.of(position);
        }
        return Optional.empty();
    }

    public static Position[] getTilesEnclosing(Position position, int radius) {
        return getTilesEnclosing(position.getX(), position.getY(), radius);
    }

    private static Position[] getTilesEnclosing(int x, int y, int size) {
        if (size <= 1)
            return new Position[]{new Position(x, y)};

        final Position[] border = new Position[4 * (size - 1)];
        border[0] = new Position(x, y);

        int j = 0;

        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < (i < 3 ? size - 1 : size - 2); k++) {
                if (i == 0) x++;
                else if (i == 1) y++;
                else if (i == 2) x--;
                else y--;
                border[(++j)] = new Position(x, y);
            }
        }
        return border;
    }

    private static Position[] getOutlineNoCorner(int x, int y, int size) {

        if (size <= 1)
            return new Position[]{new Position(x, y)};

        final int initialX = x, initialY = y;

        final Position[] border = new Position[4 * (size - 1) + 6];

        border[0] = new Position(x, y);

        int j = 0;

        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < (i < 3 ? size - 1 : size - 2); k++) {
                if (i == 0) x++;
                else if (i == 1) y++;
                else if (i == 2) x--;
                else y--;
                // Cut the diagonals from the corners
                if (i == 1 && k == 1) x--;
                if (i == 2 && k == 1) y--;
                border[(++j)] = new Position(x, y);
            }
        }

        border[++j] = new Position(initialX, initialY - 1);
        border[++j] = new Position(initialX + 1, initialY - 1);
        border[++j] = new Position(initialX + 2, initialY + 1);
        border[++j] = new Position(initialX + 1, initialY + 2);
        border[++j] = new Position(initialX - 1, initialY);
        border[++j] = new Position(initialX - 1, initialY + 1);

        return border;
    }

    public static Position[] getOutlineNoCorner(Position position, Direction direction, int height, int width) {
        return getOutlineNoCorner(position.getX(), position.getY(), direction.getId(), height, width);
    }

    public static Position[] getOutlineNoCorner(Position position, int direction, int height, int width) {
        return getOutlineNoCorner(position.getX(), position.getY(), direction, height, width);
    }
    public static Position[] getOutlineNoCorner(int x, int y, int rotation, int height, int width) {

        int size = (width * height) / 2 + 1;

//        System.out.println("Center = "+x+", "+y+", "+width+", "+height+", "+size);
        if (size <= 1)
            return new Position[] { new Position(x, y) };

        final Position[] positions;

        // Square
        if (width == height) {

            final boolean even = width % 2 == 0;
            final int center = (int) Math.floor(width / 2.0);

            int west = even ? x - 1 : (x - center) - 1;
            int south = even ? y - 1 : (y - center) - 1;
            int east = even ? x + width : (x + center) + 1;
            int north = even ? y + height : (y + center) + 1;

            if(!even){
                Direction direction = Direction.valueOf(rotation);

                final boolean isDiagonal = direction.isDiagonal();
                if(isDiagonal){

                    final double multiY = direction.isParent(Direction.SOUTH) ? -1 : 1;
                    final double multiX = direction.isParent(Direction.WEST) ? -1 : 1;
                    final Direction[] pair = direction.getAsPair();

                    for(final Direction parent : pair){
                        west += (multiX*parent.getX());
                        south += (multiY*parent.getY());
                        east += (multiX*parent.getX());
                        north += (multiY*parent.getY());
                    }
                } else {
                    west++;south++;east++;north++;
                }
            }

            int outlineSize = 2 * (east-west+north-south);

            if(even)
                outlineSize -= 4;

            positions = new Position[outlineSize];
            int i = 0;

            if(!even){
                positions[i++] = new Position(west, north-1);
                positions[i++] = new Position(west, south+1);
                positions[i++] = new Position(east-1, south);
                positions[i++] = new Position(west+1, south);
            }

            for (int nextX = west + 1; nextX < east; nextX++){
                positions[i++] = new Position(nextX, north);
                positions[i++] = new Position(nextX, south);
            }
            for (int nextY = south + 1; nextY < north; nextY++){
                positions[i++] = new Position(east, nextY);
                positions[i++] = new Position(west, nextY);
            }

            return positions;
        } else {

            int outlineSize = 2 * (width + height);

            positions = new Position[outlineSize];

            int i = 0;

            final boolean invert = (rotation & 1) == 0;
            final int horizontalLength = invert ? width : height;
            final int verticalLength = invert ? height : width;

            for (int nextX = x; nextX < x + horizontalLength; nextX++){
                positions[i++] = new Position(nextX, y + verticalLength);
                positions[i++] = new Position(nextX, y - 1);
            }

            for (int nextY = y; nextY < y + verticalLength; nextY++){
                positions[i++] = new Position(x - 1, nextY);
                positions[i++] = new Position(x + horizontalLength, nextY);
            }

            if(invert){

                if(width > size || height > size){
                    final Position[] borderNoDiagonal = getOutlineNoCorner(x, y, size);
                    final Position[] combined = new Position[borderNoDiagonal.length + outlineSize];
                    System.arraycopy(borderNoDiagonal, 0, combined, 0, borderNoDiagonal.length);
                    System.arraycopy(positions, 0, combined, 0, outlineSize);
                    return combined;
                }
            }
        }
        return positions;
    }
}
