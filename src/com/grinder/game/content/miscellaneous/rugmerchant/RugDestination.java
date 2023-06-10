package com.grinder.game.content.miscellaneous.rugmerchant;

import com.google.common.base.CaseFormat;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

/**
 * @author L E G E N D
 * @date 2/16/2021
 * @time 4:47 AM
 * @discord L E G E N D#4380
 */
public enum RugDestination {
    UZER(new Position(3470, 3113, 0)),
    NORTH_POLLNIVNEACH("Pollnivneach", new Position(3349, 3002, 0)),
    SOUTH_POLLNIVNEACH("Pollnivneach", new Position(3352, 2941, 0)),
    BEDABIN_CAMP(new Position(3181, 3045, 0)),
    SHANTAY_PASS(new Position(3309, 3110, 0)),
    SOPHANEM(new Position(3286, 2813, 0)),
    MENAPHOS(new Position(3246, 2813, 0)),
    NARDAH(new Position(3401, 2915, 0));

    private final Position destination;
    private final String name;

    RugDestination(Position destination) {
        this(null, destination);
    }

    RugDestination(String name, Position destination) {
        this.destination = destination;
        this.name = name;
    }

    public GameObject getObject() {
        for (var direction : Direction.values()) {
            var positionToCheckAt = destination.transform(direction.getX(), direction.getY(), 0);
            var object = ClippedMapObjects.findObject(ObjectID.RUG_12, new Position(positionToCheckAt.getX(), positionToCheckAt.getY()));
            if (object.isPresent()) {
                return object.get();
            }
        }
        return null;
    }

    public Position getPosition() {
        return destination;
    }

    public String getName() {
        if (name == null) {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name().replace("_", " "));
        }
        return name;
    }

    public static RugDestination getClosest(Player player) {
        var currentDestination = RugDestination.SHANTAY_PASS;
        var position = player.getPosition();
        for (var destination : RugDestination.values()) {
            if (Misc.getDistance(destination.getPosition(), position) < Misc.getDistance(currentDestination.getPosition(), position)) {
                currentDestination = destination;
            }
        }
        return currentDestination;
    }
}
