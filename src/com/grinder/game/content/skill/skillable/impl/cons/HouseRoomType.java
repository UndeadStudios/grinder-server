package com.grinder.game.content.skill.skillable.impl.cons;

/**
 * Each room represents an 8x8 chunk of map found at the coordinates provided.
 *
 * @author Simplex
 * @since Mar 27, 2020
 */
public enum HouseRoomType {
    EMPTY(1864, 5696, ConstructionUtils.EMPTY, 1, 0, -1, new boolean[]{true, true, true, true}),
    BUILDABLE(1864, 5696, ConstructionUtils.BUILDABLE, 1, 0, -1, new boolean[]{true, true, true, true}),
    GARDEN(1856, 5704, ConstructionUtils.GARDEN, 1, 1000, 128651, new boolean[]{true, true, true, true}),
    PARLOUR(1856, 5752, ConstructionUtils.PARLOUR, 1, 1000, 128647, new boolean[]{true, true, true, false}),
    KITCHEN(1872, 5752, ConstructionUtils.KITCHEN, 5, 5000, 128655, new boolean[]{true, true, false, false}),
    DINING_ROOM(1888, 5752, ConstructionUtils.DINING_ROOM, 10, 5000, 128659, new boolean[]{true, true, true, false}),
    WORKSHOP(1856, 5736, ConstructionUtils.WORKSHOP, 15, 10000, 128663, new boolean[]{false, true, false, true}),
    BEDROOM(1904, 5752, ConstructionUtils.BEDROOM, 20, 10000, 128667, new boolean[]{true, true, false, false}),
    SKILL_ROOM(1864, 5744, ConstructionUtils.SKILL_ROOM, 25, 15000, 128671, new boolean[]{true, true, true, true}),
    QUEST_HALL_DOWN(1912, 5744, ConstructionUtils.QUEST_HALL_DOWN, 35, 0, -1, new boolean[]{true, true, true, true}),
    SKILL_HALL_DOWN(1880, 5744, ConstructionUtils.SKILL_HALL_DOWN, 25, 0, -1, new boolean[]{true, true, true, true}),
    GAMES_ROOM(1896, 5728, ConstructionUtils.GAMES_ROOM, 30, 25000, 128675, new boolean[]{true, true, true, false}),
    COMBAT_ROOM(1880, 5728, ConstructionUtils.COMBAT_ROOM, 32, 25000, 128679, new boolean[]{true, true, true, false}),
    QUEST_ROOM(1896, 5744, ConstructionUtils.QUEST_ROOM, 35, 25000, 128683, new boolean[]{true, true, true, true}),
    MENAGERY(1912, 5696, ConstructionUtils.MENAGERY, 37, 30000, 128687, new boolean[]{true, true, true, true}),
    STUDY(1888, 5736, ConstructionUtils.STUDY, 40, 50000, 128691, new boolean[]{true, true, true, false}),
    CUSTOME_ROOM(1904, 5704, ConstructionUtils.COSTUME_ROOM, 42, 50000, 128695, new boolean[]{false, true, false, false}),
    CHAPEL(1872, 5736, ConstructionUtils.CHAPEL, 45, 50000, 128699, new boolean[]{true, true, false, false}),
    PORTAL_ROOM(1864, 5728, ConstructionUtils.PORTAL_ROOM, 50, 100000, 128703, new boolean[]{false, true, false, false}),
    FORMAL_GARDEN(1872, 5704, ConstructionUtils.FORMAL_GARDEN, 55, 75000, 128707, new boolean[]{true, true, true, true}),
    THRONE_ROOM(1904, 5736, ConstructionUtils.THRONE_ROOM, 60, 150000, 128711, new boolean[]{false, true, false, false}),
    OUBLIETTE(1904, 5720, ConstructionUtils.OUBLIETTE, 65, 150000, 128715, new boolean[]{true, true, true, true}),
    PIT(1896, 5072, ConstructionUtils.PIT, 70, 10000, 128735, new boolean[]{true, true, true, true}),
    DUNGEON_STAIR_ROOM(1872, 5720, ConstructionUtils.DUNGEON_STAIR_ROOM, 70, 7500, 128731, new boolean[]{true, true, true, true}),
    TREASURE_ROOM(1912, 5728, ConstructionUtils.TREASURE_ROOM, 75, 250000, 128739, new boolean[]{false, true, false, false}),
    CORRIDOR(1888, 5720, ConstructionUtils.CORRIDOR, 70, 7500, 128723, new boolean[]{false, true, false, true}),
    JUNCTION(1856, 5720, ConstructionUtils.JUNCTION, 70, 7500, 128727, new boolean[]{true, true, true, true}),
    ROOF(1896, 5712, ConstructionUtils.ROOF, 0, 0, -1, new boolean[]{true, true, true, true}),
    DUNGEON_EMPTY(1880, 5696, ConstructionUtils.DUNGEON_EMPTY, 0, 0, -1, new boolean[]{true, true, true, true}),
    SUPERIOR_GARDEN(1896, 5696, ConstructionUtils.SUPERIOR_GARDEN, 65, 75000, 128719, new boolean[]{true, true, true, true}),
    ACHIEVEMENT_GALLERY_ROOM(1928, 5760, ConstructionUtils.ACHIEVEMENT_GALLERY, 80, 200000, 128743, new boolean[]{false, true, false, true});

    public static HouseRoomType forID(int id) {
        for (HouseRoomType rd : values()) {
            if (rd.id == id)
                return rd;
        }
        return null;
    }

    private int x, y, cost, levelToBuild, id, roomBuilderButton;
    private boolean[] doors;

    private HouseRoomType(int x, int y, int id, int levelToBuild, int cost,
                          int roomBuilderButton, boolean[] doors) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.levelToBuild = levelToBuild;
        this.cost = cost;
        this.doors = doors;
        this.roomBuilderButton = roomBuilderButton;
    }

    public int getRoomBuilderButton() {
        return roomBuilderButton;
    }

    public boolean[] getDoors() {
        return doors;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCost() {
        return cost;
    }

    public int getLevelToBuild() {
        return levelToBuild;
    }

    public int getId() {
        return id;
    }

    public static int getFirstElegibleRotation(HouseRoomType rd, int from) {
        for (int rot = 0; rot < 4; rot++) {
            boolean[] door = rd.getRotatedDoors(rot);
            if (from == 0 && door[2])
                return rot;
            if (from == 1 && door[3])
                return rot;
            if (from == 2 && door[0])
                return rot;
            if (from == 3 && door[1])
                return rot;
        }
        return -1;
    }
    public static int getNextEligibleRotationClockWise(HouseRoomType rd, int from, int currentRot) {
        if(rd == HouseRoomType.WORKSHOP)
            return 0;
        for (int rot = currentRot+1; rot < currentRot+4; rot++) {
            int rawt = (rot > 3 ? (rot - 4) : rot);
            boolean[] door = rd.getRotatedDoors(rawt);
            if (from == 0 && door[2])
                return rawt;
            if (from == 1 && door[3])
                return rawt;
            if (from == 2 && door[0])
                return rawt;
            if (from == 3 && door[1])
                return rawt;
        }
        return currentRot;
    }
    public static int getNextEligibleRotationCounterClockWise(HouseRoomType rd, int from, int currentRot) {
        if(rd == HouseRoomType.WORKSHOP)
            return 0;
        for (int rot = currentRot-1; rot > currentRot-4; rot--) {
            int rawt = (rot < 0 ? (rot + 4) : rot);
            boolean[] door = rd.getRotatedDoors(rawt);
            if (from == 0 && door[2])
                return rawt;
            if (from == 1 && door[3])
                return rawt;
            if (from == 2 && door[0])
                return rawt;
            if (from == 3 && door[1])
                return rawt;
        }
        return -1;
    }

    public boolean[] getRotatedDoors(int rotation) {
        if (rotation == 0)
            return doors;
        if (rotation == 1) {
            boolean[] newDoors = new boolean[4];
            if (doors[0])
                newDoors[3] = true;
            if (doors[1])
                newDoors[0] = true;
            if (doors[2])
                newDoors[1] = true;
            if (doors[3])
                newDoors[2] = true;
            return newDoors;
        }
        if (rotation == 2) {
            boolean[] newDoors = new boolean[4];
            if (doors[0])
                newDoors[2] = true;
            if (doors[1])
                newDoors[3] = true;
            if (doors[2])
                newDoors[0] = true;
            if (doors[3])
                newDoors[1] = true;
            return newDoors;
        }
        if (rotation == 3) {
            boolean[] newDoors = new boolean[4];
            if (doors[0])
                newDoors[1] = true;
            if (doors[1])
                newDoors[2] = true;
            if (doors[2])
                newDoors[3] = true;
            if (doors[3])
                newDoors[0] = true;
            return newDoors;
        }
        return null;
    }

    @Override
    public String toString() {
        String name = name().toLowerCase();
        name = name.replaceAll("_", " ");
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

}