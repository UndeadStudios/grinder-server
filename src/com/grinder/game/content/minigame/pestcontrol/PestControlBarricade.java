package com.grinder.game.content.minigame.pestcontrol;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.MapInstance;
import com.grinder.util.ObjectID;

/**
 * @author  Minoroin / TealWool#0873 (https://www.rune-server.ee/members/minoroin/)
 * @since   20/11/2021
 * @version 1.0
 */

public class PestControlBarricade {

    private final Position position;
    private final Direction rotation;
    private final PestControlBarricadePart partFirst;
    private final PestControlBarricadePart partSecond;
    private final PestControlBarricadePart partThird;
    private final PestControlBarricadePart partForth;

    PestControlBarricade(final PestControlInstance instance, final Position position, final Direction rotation, PestControlBarricadeState state) {
        this.position = position;
        this.rotation = rotation;

        if (rotation == Direction.WEST) {
            partFirst = new PestControlBarricadePart(instance, ObjectID.BARRICADE_8, ObjectID.BARRICADE_11, ObjectID.BARRICADE_14, position, 0, 9, state);
            partSecond = new PestControlBarricadePart(instance, ObjectID.BARRICADE_6, ObjectID.BARRICADE_9, ObjectID.BARRICADE_12, new Position(position.getX(), position.getY()-1), 0, 0, state);
            partThird = new PestControlBarricadePart(instance, ObjectID.BARRICADE_6, ObjectID.BARRICADE_9, ObjectID.BARRICADE_12, new Position(position.getX(), position.getY()-2), 0, 0, state);
            partForth = new PestControlBarricadePart(instance, ObjectID.BARRICADE_7, ObjectID.BARRICADE_10, ObjectID.BARRICADE_13, new Position(position.getX(), position.getY()-3), 3, 9, state);
        }
        else if (rotation == Direction.EAST) {
            partFirst = new PestControlBarricadePart(instance, ObjectID.BARRICADE_8, ObjectID.BARRICADE_11, ObjectID.BARRICADE_14, position, 2, 9, state);
            partSecond = new PestControlBarricadePart(instance, ObjectID.BARRICADE_6, ObjectID.BARRICADE_9, ObjectID.BARRICADE_12, new Position(position.getX(), position.getY()+1), 2, 0, state);
            partThird = new PestControlBarricadePart(instance, ObjectID.BARRICADE_6, ObjectID.BARRICADE_9, ObjectID.BARRICADE_12, new Position(position.getX(), position.getY()+2), 2, 0, state);
            partForth = new PestControlBarricadePart(instance, ObjectID.BARRICADE_7, ObjectID.BARRICADE_10, ObjectID.BARRICADE_13, new Position(position.getX(), position.getY()+3), 1, 9, state);
        } else {
            //Direction.South
            partFirst = new PestControlBarricadePart(instance, ObjectID.BARRICADE_8, ObjectID.BARRICADE_11, ObjectID.BARRICADE_14, position, 3, 9, state);
            partSecond = new PestControlBarricadePart(instance, ObjectID.BARRICADE_6, ObjectID.BARRICADE_9, ObjectID.BARRICADE_12, new Position(position.getX()+1, position.getY()), 3, 0, state);
            partThird = new PestControlBarricadePart(instance, ObjectID.BARRICADE_6, ObjectID.BARRICADE_9, ObjectID.BARRICADE_12, new Position(position.getX()+2, position.getY()), 3, 0, state);
            partForth = new PestControlBarricadePart(instance, ObjectID.BARRICADE_7, ObjectID.BARRICADE_10, ObjectID.BARRICADE_13, new Position(position.getX()+3, position.getY()), 2, 9, state);
        }
    }

    public PestControlBarricadePart[] getParts() {
        PestControlBarricadePart[] parts = new PestControlBarricadePart[4];

        parts[0] = partFirst;
        parts[1] = partSecond;
        parts[2] = partThird;
        parts[3] = partForth;

        return parts;
    }

}
