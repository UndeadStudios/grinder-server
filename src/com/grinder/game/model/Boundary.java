package com.grinder.game.model;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.GameObject;
import com.grinder.util.Misc;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a rectangular boundary.
 *
 * @author Professor Oak
 */
public class Boundary {

    private final int x;
    private final int x2;
    private final int y;
    private final int y2;

    public Boundary(int x, int x2, int y, int y2) {
        this.x = x;
        this.x2 = x2;
        this.y = y;
        this.y2 = y2;
    }

    public int getX() {
        return x;
    }

    public int getX2() {
        return x2;
    }

    public int getY() {
        return y;
    }

    public int getY2() {
        return y2;
    }

    public boolean contains(Position p) {
        return p.getX() >= x && p.getX() <= x2 && p.getY() >= y && p.getY() <= y2;
    }

    public Position getRandomPosition() {
        return new Position(Misc.random(x, x2), Misc.random(y, y2));
    }

    public Position getCenterPosition() {
        return new Position((x+x2)/2, (y+y2)/2);
    }

    public Stream<GameObject> objectStream(int plane){
        final HashSet<GameObject> gameObjects = new HashSet<>();
        for(int i = x; i < x2; i++){
            for(int j = y; j < y2; j++){
                final List<GameObject> localObjects = ClippedMapObjects.getObjectsAt(new Position(i, j , plane));
                if(localObjects == null)
                    continue;
                gameObjects.addAll(localObjects);
            }
        }
        return gameObjects.stream();
    }

    /**
     * Creates a square boundary centred at the position with sides of length 2*radius.
     */
    public static List<Boundary> squareAt(Position pos, int radius) {
       return pos.createSquareBoundary(radius);
    }

    public static boolean inside(Player p, Position position, int radius) {
        List<Boundary> list = squareAt(position, radius);

        for(Boundary b : list) {
            if(b.inside(p)) {
                return true;
            }
        }
        return false;
    }

    public boolean inside(Position position) {
        return position.getX() >= x && position.getX() <= x2
                && position.getY() >= y && position.getY() <= y2;
    }

    public boolean inside(Player p) {
        return inside(p.getPosition().clone());
    }
}
