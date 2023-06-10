package com.grinder.game.model.areas.constructed;

public class Palette {

    public static final int DIRECTION_NORMAL = 0;
    public static final int DIRECTION_CW_0 = 0;
    public static final int DIRECTION_CW_90 = 1;
    public static final int DIRECTION_CW_180 = 2;
    public static final int DIRECTION_CW_270 = 3;

    public static class PaletteTile {

        private int x;
        private int y;
        private int z;
        private int rot;

        public PaletteTile(int x, int y) {
            this(x, y, 0);
        }

        public PaletteTile(int x, int y, int z) {
            this(x, y, z, DIRECTION_NORMAL);
        }

        public PaletteTile(int x, int y, int z, int rot) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rot = rot;
        }

        public int getX() {
            return x / 8;
        }

        public int getY() {
            return y / 8;
        }

        public int getZ() {
            return z & 3;
        }

        public int getRotation() {
            return rot & 3;
        }

    }

    private PaletteTile[][][] tiles = new PaletteTile[13][13][5];

    public PaletteTile getTile(int x, int y, int z) {
        return tiles[x][y][z];
    }

    public void setTile(int x, int y, int z, PaletteTile tile) {
        tiles[x][y][z] = tile;
    }

}
