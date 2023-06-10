package com.grinder.game.content.minigame.pestcontrol;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.MapInstance;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;

/**
 * @author  Minoroin / TealWool#0873 (https://www.rune-server.ee/members/minoroin/)
 * @since   20/11/2021
 * @version 1.0
 */

public class PestControlBarricadePart {
    private final Position position;
    private final int rotation;
    private final int type;
    private PestControlBarricadeState state;
    private final int fixedId;
    private final int halfBrokenId;
    private final int brokenId;

    PestControlBarricadePart(final PestControlInstance instance, final int fixedId, final int halfBrokenId, final int brokenId, final Position position, final int rotation, final int type, PestControlBarricadeState state) {
        this.position = position;
        this.rotation = rotation;
        this.type = type;
        this.state = state;
        this.fixedId = fixedId;
        this.halfBrokenId = halfBrokenId;
        this.brokenId = brokenId;
    }

    public void hitBarricade() {
        if (state == PestControlBarricadeState.REPAIRED) {
            state = PestControlBarricadeState.HALF_BROKEN;
            DynamicGameObject barricade = DynamicGameObject.createPublic(halfBrokenId, position, type, rotation);
            World.addObject(barricade);
        } else {
            state = PestControlBarricadeState.BROKEN;
            DynamicGameObject barricade = DynamicGameObject.createPublic(halfBrokenId, position, type, rotation);
            CollisionManager.removeObjectClipping(barricade);

            barricade = DynamicGameObject.createPublic(brokenId, position, type, rotation);
            World.addObject(barricade);
            barricade = DynamicGameObject.createPublic(brokenId, position, 22, rotation);
            World.addObject(barricade);
        }
    }

    public void repairBarricade(Player player) {
        Inventory inventory = player.getInventory();
        if (inventory.contains(ItemID.HAMMER) && inventory.contains(ItemID.LOGS)) {
            inventory.delete(ItemID.LOGS, 1);
            player.setPositionToFace(position);
            player.getPacketSender().sendSound(1728);

            player.performAnimation(new Animation(3931));
            if (state == PestControlBarricadeState.HALF_BROKEN) {
                state = PestControlBarricadeState.REPAIRED;
                DynamicGameObject barricade = DynamicGameObject.createPublic(fixedId, position, type, rotation);
                World.addObject(barricade);
            } else {
                state = PestControlBarricadeState.HALF_BROKEN;
                DynamicGameObject barricade = DynamicGameObject.createPublic(halfBrokenId, position, 22, rotation);
                World.addObject(barricade);
                barricade = DynamicGameObject.createPublic(halfBrokenId, position, type, rotation);
                World.addObject(barricade);
            }
        } else {
            player.sendMessage("You need a hammer and logs to repair that!");
        }
    }

    public Position getPosition() {
        return position;
    }

    protected int getRotation() {
        return rotation;
    }

    public PestControlBarricadeState getState() {
        return state;
    }
}
