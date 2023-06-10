package com.grinder.util;

import com.grinder.game.entity.Entity;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.PositionUtil;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-06-05
 */
public class DistanceUtil {
    /**
     * Checks if the attacker is in a certain distance from the target
     */
    public static boolean isWithinDistance(Position start, int firstSize, Position destine, int secondSize, int distance) {

        if (start.getZ() != destine.getZ())
            return false;

        final int x = start.getX();
        final int y = start.getY();
        final int x2 = destine.getX();
        final int y2 = destine.getY();

        if (getManhattanDistance(x, y, x2, y2) <= distance)
            return true;

        final int rotation = Direction.getDirection(destine, start).getId();

        final Position[] firstBorder = PositionUtil.getOutlineNoCorner(start.getX(), start.getY(), rotation, firstSize, firstSize);
        final Position[] secondBorder = PositionUtil.getTilesEnclosing(destine, secondSize);

        for (final Position first : firstBorder) {
            for (final Position second : secondBorder) {
                if (getManhattanDistance(first, second) <= distance)
                    return true;
            }
        }

        return false;
    }

    /**
     * Checks if the attacker is in a certain distance from the target
     */
    public static boolean isWithinDistance(Agent entity, Agent other, int req) {
        if (entity == null || other == null) {
            return false;
        }

        if (entity.getPosition().getZ() != other.getPosition().getZ()) {
            return false;
        }

        int x = entity.getPosition().getX();
        int y = entity.getPosition().getY();
        int x2 = other.getPosition().getX();
        int y2 = other.getPosition().getY();

        if (getManhattanDistance(x, y, x2, y2) <= req) {
            return true;
        }

        Position[] a = PositionUtil.getTilesEnclosing(entity.getPosition(), entity.getSize());
        Position[] b = PositionUtil.getTilesEnclosing(other.getPosition(), other.getSize());

        for (Position i : a) {
            for (Position k : b) {
                if (getManhattanDistance(i, k) <= req) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets Manhattan distance
     */
    private static int getManhattanDistance(int x, int y, int x2, int y2) {
        return Math.abs(x - x2) + Math.abs(y - y2);
    }

    /**
     * Returns the manhattan distance between 2 positions.
     */
    public static int getManhattanDistance(Position pos, Position other) {
        return getManhattanDistance(pos.getX(), pos.getY(), other.getX(), other.getY());
    }

    private static int getChebyshevDistance(int x, int y, int x2, int y2) {
        return Math.max(Math.abs(x - x2), Math.abs(y - y2));
    }

    public static int getChebyshevDistance(final Position first, final Position second){
        return getChebyshevDistance(first.getX(), first.getY(), second.getX(), second.getY());
    }

    public static int calculateDistance(final Entity entity, final Entity other) {
        if(other instanceof NPC && other.getSize() > 1){
            return calculateDistance(entity.getPosition(), other);
        }
        if(entity instanceof NPC && entity.getSize() > 1){
            return calculateDistance(other.getPosition(), entity);
        }
        return calculateDistance(entity.getPosition(), other);
    }

    public static int calculateDistance(final Position source, final Position destine, int size) {

        if(size > 1){
            final double otherOffset = size / 2.0;
            final double otherX = source.getX() + otherOffset;
            final double otherY = source.getY() + otherOffset;
            final double x = destine.getX() + 0.5;
            final double y = destine.getY() + 0.5;
            final double dx = x - otherX;
            final double dy = y - otherY;
            return Math.max(0, Math.toIntExact(
                    Math.round(Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0)) - otherOffset)));
//            return source.getDistance(other.getCenterPosition());
        }

        return Misc.distanceBetween(source, destine);
    }

    private static int calculateDistance(final Position source, final Entity other) {

        final int otherSize = other.getSize();

        if(otherSize > 1){
            final Position otherPosition = other.getPosition();
            final double otherOffset = otherSize / 2.0;
            final double otherX = otherPosition.getX() + otherOffset;
            final double otherY = otherPosition.getY() + otherOffset;
            final double x = source.getX() + 0.5;
            final double y = source.getY() + 0.5;
            final double dx = x - otherX;
            final double dy = y - otherY;
            return Math.max(0, Math.toIntExact(
                    Math.round(Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0)) - otherOffset)));
//            return source.getDistance(other.getCenterPosition());
        }

        return Misc.distanceBetween(source, other.getPosition());
    }
}
