package com.grinder.game.model.area;

import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.Area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Lesik
 * Region sections of 64x64 tiles
 */
public class RegionSection {

    private final List<Area> areaList = new ArrayList<>();

    public final Map<Boundary, Area> areaMap = new HashMap<>();

    private final int x, y;

    public RegionSection(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position toWorldPosition() {
        return new Position(x << 6, y << 6);
    }

    public int getId() {
        return x << 8 | y;
    }


    public void addArea(Area area, Boundary boundary) {
        //areaList.add(area);
        areaMap.put(boundary, area);
    }

    public List<Area> getAreaList() {
        return areaList;
    }
}
