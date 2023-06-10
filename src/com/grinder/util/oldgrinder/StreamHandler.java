package com.grinder.util.oldgrinder;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-07
 */
public class StreamHandler {

    public static void displayItemOnInterface(final Player player, final int childId, final int itemId, final int slot, final int amount) {
        player.getPacketSender().sendItemOnInterface(childId, itemId, slot, amount);
    }

    public static void showInterface(final Player player, final int interfaceId) {
        player.getPacketSender().sendInterface(interfaceId);
    }

    public static void removePlayerHint(final Player player) {
        player.getPacketSender().sendEntityHintRemoval(true);
    }

    public static void createObjectHints(final Player player, int x, int y, int z, int hint) {
        player.getPacketSender().sendPositionalHint(new Position(x, y, z), hint);
    }

    public static void requestInputMessage(final Player player, String message, int promptCodeID, boolean onlyNumbers, boolean inputTypeUpdate) {


    }

    public static void animateObject(final Player player, final int objectX, final int objectY, final int animationID, final int objectType, final int objectFace) {
        player.getPacketSender().sendObjectAnimation(objectX, objectY, objectType, objectFace, new Animation(animationID));
    }
    public static void ReplaceObject2(final Player c, final int objectX, final int objectY, final int newObjectID, final int objectFace, final int objectType) {

        final Position position = new Position(objectX, objectY, c.getPosition().getZ());

        c.getPacketSender().sendObjectRemoval(position, objectType, objectFace);

        if (newObjectID != -1)
            c.getPacketSender().sendObject(position, newObjectID, objectType, objectFace);

    }

}
