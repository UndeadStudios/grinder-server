package com.grinder.game.content.minigame.warriorsguild.rooms.shotput;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;

/**
 * @author L E G E N D
 */
enum ShotType {
    _18(ItemID._18LB_SHOT, ObjectID.SHOT_18LB, new Position(2861, 3553, 1)),
    _22(ItemID._22LB_SHOT, ObjectID.SHOT_22LB, new Position(2861, 3547, 1));

    public final int itemId;
    public final int objectId;
    public final Position position;

    ShotType(int itemId, int objectId, Position position) {
        this.itemId = itemId;
        this.objectId = objectId;
        this.position = position;
    }

    public static ShotType find(Player player) {
        return (player.getPosition().getDistance(_22.position) <= player.getPosition().getDistance(_18.position) ? _22 : _18);
    }
}
