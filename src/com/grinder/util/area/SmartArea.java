package com.grinder.util.area;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.model.Position;
import com.grinder.util.oldgrinder.Area;

import java.util.HashSet;
import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-20
 */
public class SmartArea {

    private final HashSet<Position> blockedCoordinates = new HashSet<>();

    private final Area area;

    public SmartArea(Area area) {
        this.area = area;
    }

    public Optional<Position> getReachableRandomPosition(Position start, int xLength, int yLength){

        Optional<Position> result = getOpenRandomPosition();

        while(result.isPresent() && !CollisionManager.canMove(start, result.get(), xLength, yLength)){
            blackList(result.get());
            result = getOpenRandomPosition();
        }

        return result;
    }

    private Optional<Position> getOpenRandomPosition(){

        Position position = area.getRandomPosition();

        if(blockedCoordinates.size() >= tileCount())
            return Optional.empty();

        while(blockedCoordinates.contains(position)){
            position = area.getRandomPosition();
        }

        return Optional.of(position);
    }

    private void blackList(Position position){
        blockedCoordinates.add(position);
    }

    private int tileCount(){
        int xLength = Math.abs(area.getWest()-area.getEast());
        int yLength = Math.abs(area.getSouth()-area.getNorth());
        return xLength * yLength;
    }

    public Area getArea() {
        return area;
    }
}
