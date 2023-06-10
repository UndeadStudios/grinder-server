package com.grinder.game.model.areas.constructed;

import com.grinder.game.model.area.RegionCoordinates;

public class ConstructedChunk {
    private final RegionCoordinates coordinate;
    private final RegionCoordinates copyFromCoordinate;

    private final int orientation, newZ, fromZ;

    public ConstructedChunk(RegionCoordinates coordinate, int newZ, RegionCoordinates copyFromCoordinate, int fromZ, int orientation) {
        this.coordinate = coordinate;
        this.copyFromCoordinate = copyFromCoordinate;
        this.orientation = orientation;
        this.newZ = newZ;
        this.fromZ = fromZ;
    }

    /**
     * Gets the coordinate the chunk is created in.
     * @return coordinate that we created
     */
    public RegionCoordinates getCoordinate() {
        return coordinate;
    }

    public int getChunkId() {
        return coordinate.getX() << 16 | coordinate.getY();
    }

    public int getCopyFromChunkId() {
        return copyFromCoordinate.getX() << 16 | copyFromCoordinate.getY();
    }

    public int getOrientation() {
        return orientation;
    }

    public int getFromX() {
        return copyFromCoordinate.getX();
    }

    public int getFromY() {
        return copyFromCoordinate.getY();
    }

    public int getNewZ() {
        return newZ;
    }

    public int getFromZ() {
        return fromZ;
    }
}
