package com.grinder.game.content.skill.skillable.impl.cons;

import com.google.gson.annotations.Expose;
import com.grinder.game.model.Position;

import java.util.ArrayList;

/**
 * Contains all savable Player-owned-house related vars.
 *
 * @author Simplex
 * @since Apr 17, 2020
 */
public class House {

    @Expose HouseStyle style = HouseStyle.WHITEWASHED_STONE;

    public HouseStyle getStyle() {
        return style;
    }

    public void setStyle(HouseStyle style) {
        this.style = style;
    }

    @Expose boolean hasPOH = false;

    public boolean hasHouse() {
        return hasPOH;
    }

    public void setHasHouse(boolean b) {
        hasPOH = b;
    }

    private int buildFurnitureId;

    private int buildFurnitureX;

    private int buildFurnitureY;

    private Position constructionBuildPosition;

    private HouseRoom[][][] dungeonHouseRooms = new HouseRoom[4][13][13];

    public void setDungeonHouseRooms(HouseRoom[][][] dungeonHouseRooms) {
        this.dungeonHouseRooms = dungeonHouseRooms;
    }

    public HouseRoom[][][] getDungeonRooms() {
        return dungeonHouseRooms;
    }

    private HouseRoom[][][] houseHouseRooms = new HouseRoom[4][13][13];

    public void setHouseRooms(HouseRoom[][][] houseHouseRooms) {
        this.houseHouseRooms = houseHouseRooms;
    }

    public Position getConstructionBuildPosition() {
        return constructionBuildPosition;
    }

    public void setConstructionBuildPosition(Position constructionBuildPosition) {
        this.constructionBuildPosition = constructionBuildPosition;
    }

    public int getBuildFurnitureId() {
        return this.buildFurnitureId;
    }

    public void setBuildFuritureId(int buildFuritureId) {
        this.buildFurnitureId = buildFuritureId;
    }

    public int getBuildFurnitureX() {
        return this.buildFurnitureX;
    }

    public void setBuildFurnitureX(int buildFurnitureX) {
        this.buildFurnitureX = buildFurnitureX;
    }

    public int getBuildFurnitureY() {
        return this.buildFurnitureY;
    }

    public void setBuildFurnitureY(int buildFurnitureY) {
        this.buildFurnitureY = buildFurnitureY;
    }

    public HouseRoom[][][] getHouseHouseRooms() {
        return houseHouseRooms;
    }

    public ArrayList<HousePortal> getHousePortals() {
        return housePortals;
    }

    private ArrayList<HousePortal> housePortals = new ArrayList<>();

    public ArrayList<HouseFurniture> getFurniture(HouseRoom[][][] houseRooms) {
        ArrayList<HouseFurniture> furniture = new ArrayList<>();
        for (int z = 0; z < houseRooms.length; z++) {
            for (int x = 0; x < houseRooms[z].length; x++) {
                for (int y = 0; y < houseRooms[z][x].length; y++) {
                    HouseRoom houseRoom = houseRooms[z][x][y];
                    if (houseRoom == null)
                        continue;
                    furniture.addAll(houseRoom.getFurniture());
                }
            }
        }
        return furniture;
    }

    public ArrayList<HouseFurniture> getSurfaceFurniture() {
        return getFurniture(houseHouseRooms);
    }

    public ArrayList<HouseFurniture> getDungeonFurniture() {
        return getFurniture(dungeonHouseRooms);
    }

    public ArrayList<HouseFurniture> getAllFurniture() {
        ArrayList<HouseFurniture> furniture = new ArrayList<>();
        furniture.addAll(getSurfaceFurniture());
        furniture.addAll(getDungeonFurniture());
        return furniture;
    }

    private int portalSelected = -1;

    public int getPortalSelected() {
        return portalSelected;
    }

}
