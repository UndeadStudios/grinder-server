package com.grinder.game.content.skill.skillable.impl.hunter.birdhouse;

import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.util.Misc;

/**
 * @author Zach (zach@findzach.com)
 * @since 12/21/2020
 * <p>
 * All you need to do is add a BirdHouseSpot element and it will load the object/clipping and operate properly in-game.
 */
public enum BirdHouseSpot {
    VERDANT_VALLEY(30565, new Position(3763, 3755, 0), Direction.SOUTH),
    VERDANT_VALLEY_2(30565, new Position(3768, 3761, 0), Direction.SOUTH)
    //HOME(new Position(3091, 3501), Direction.EAST),
    //MEMBER_ZONE(new Position(3684, 2965), Direction.SOUTH),
    ;

    public final int objectId;
    private final Position hotSpotPos;
    private final Direction faceDirection;

    /**
     * The BirdHouse Configuration
     * @param hotSpotPosition - The position of the object
     * @param faceDirection - The direction the object is
     */
    BirdHouseSpot(int objectId, Position hotSpotPosition, Direction faceDirection) {
        this.objectId = objectId;
        this.hotSpotPos = hotSpotPosition;
        this.faceDirection = faceDirection;
    }

    public final Position getHotSpotPos() {
        return hotSpotPos;
    }

    public Direction getFaceDirection() {
        return faceDirection;
    }


    public final String getCleanName() {
        return Misc.capitalizeWords(name().toLowerCase().replaceAll("_", " "));
    }

    /**
     * // specify what currentBirdSpot is or remove from doc
     * @param currentBirdSpot
     * @return NULL if we cannot locate the birdhouse/space at this position
     */
    public static BirdHouseSpot getSpotFromPos(Position currentBirdSpot) {
        for (BirdHouseSpot spot : BirdHouseSpot.values()) {
            if (spot.hotSpotPos.is(currentBirdSpot, false)) {
                return spot;
            }
        }
        return null;
    }


}
