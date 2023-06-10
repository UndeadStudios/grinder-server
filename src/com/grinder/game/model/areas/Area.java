package com.grinder.game.model.areas;

import com.grinder.game.entity.Entity;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.net.packet.interaction.PacketInteraction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Area extends PacketInteraction implements AreaListener {

    private final AtomicInteger playerCount = new AtomicInteger(0);

    private final List<Boundary> boundaries;
    private final Boundary circumScribedBoundary;

    public Area(Boundary... boundaries) {
        if (boundaries.length == 1) {
            this.boundaries = Collections.singletonList(boundaries[0]);
            circumScribedBoundary = boundaries[0];
        } else {
            this.boundaries = Arrays.asList(boundaries);
            int topX = Integer.MIN_VALUE;
            int topY = Integer.MIN_VALUE;
            int bottomY = Integer.MAX_VALUE;
            int bottomX = Integer.MAX_VALUE;
            for (Boundary boundary : boundaries) {
                if (boundary.getX() < bottomX) bottomX = boundary.getX();
                if (boundary.getY() < bottomY) bottomY = boundary.getY();
                if (boundary.getX2() > topX) topX = boundary.getX2();
                if (boundary.getY2() > topY) topY = boundary.getY2();
            }
//            System.out.println(getClass().getSimpleName()+": ["+bottomX+".."+topX+"] ["+bottomY+".."+topY+"]");
            circumScribedBoundary = new Boundary(bottomX, topX, bottomY, topY);
        }

    }

    public boolean contains(Entity entity) {
        return contains(entity.getPosition());
    }

    public boolean contains(Position position) {
        if (circumScribedBoundary.contains(position)) {
            for (Boundary boundary : boundaries) {
                if (boundary.contains(position))
                    return true;
            }
        }
        return false;
    }

    @Override
    public void enter(Agent agent) {
        if (agent instanceof Player) {
            playerCount.incrementAndGet();
        }
    }

    @Override
    public void leave(Agent agent) {
        if (agent instanceof Player) {
            int newCount = playerCount.decrementAndGet();
            if (newCount < 0) {
                playerCount.set(0);
                System.err.println("Area " + super.toString() +" has a negative player count : NOT GOOD!");
            }
        }
    }

    @Override
    public List<Boundary> boundaries() {
        return boundaries;
    }

    @Override
    public boolean isSafeForHardcore() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /**
     * Gets the object that may be in this region.
     *
     * @param player The Player looking for the object.
     * @param object The objectId being interacted.
     * @param pos    The position of the object.
     * @return GameObject.
     */
    public Optional<GameObject> getObject(Player player, int object, Position pos) {
        return ClippedMapObjects.findObject(object, pos);
    }

    /**
     * Determines if a cannon is prohibited from being placed in the area.
     *
     * @return
     */
    public boolean isCannonProhibited() {
        return false;
    }

    public boolean hasPlayers() {
        return playerCount.get() != 0;
    }

    public int amountOfPlayers() { return playerCount.get(); }
}
