package com.grinder.game.content.minigame.pestcontrol;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;
/**
 * @author  Minoroin / TealWool#0873 (https://www.rune-server.ee/members/minoroin/)
 * @since   20/11/2021
 * @version 1.0
 */
public class PestControlDoorsManager {

    protected static PestControlDoorState doorState[] = {PestControlDoorState.CLOSED, PestControlDoorState.CLOSED, PestControlDoorState.CLOSED};

    private final int WEST_DOOR = 0;
    private final int SOUTH_DOOR = 1;
    private final int EAST_DOOR = 2;

    //needs breaking door by npcs with door hp and fixing door

    protected void openSouthDoor(Position base) {
        //if (doorState[SOUTH_DOOR] != PestControlDoorState.BROKEN && doorState[SOUTH_DOOR] != PestControlDoorState.HALF_BROKEN) {
            doorState[SOUTH_DOOR] = PestControlDoorState.OPENED;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_120, base.transform(32, 25, 0), 0, 3);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(32, 25, 0), 0, 3);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(32, 24, 0), 0, 0);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_122, base.transform(33, 25, 0), 0, 3);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(33, 25, 0), 0, 3);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(33, 24, 0), 0, 2);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        //}
    }

    protected void closeSouthDoor(Position base) {
        //if (doorState[SOUTH_DOOR] != PestControlDoorState.BROKEN && doorState[SOUTH_DOOR] != PestControlDoorState.HALF_BROKEN) {
            doorState[SOUTH_DOOR] = PestControlDoorState.CLOSED;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(32, 24, 0), 0, 0);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(32, 24, 0), 0, 0);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_120, base.transform(32, 25, 0), 0, 3);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(33, 24, 0), 0, 2);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(33, 24, 0), 0, 2);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_122, base.transform(33, 25, 0), 0, 3);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        //}
    }

    protected void openWestDoor(Position base) {
        //if (doorState[WEST_DOOR] != PestControlDoorState.BROKEN && doorState[WEST_DOOR] != PestControlDoorState.HALF_BROKEN) {
            doorState[WEST_DOOR] = PestControlDoorState.OPENED;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_120, base.transform(19, 33, 0), 0, 0);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(19, 33, 0), 0, 0);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(18, 33, 0), 0, 1);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_122, base.transform(19, 32, 0), 0, 0);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(19, 32, 0), 0, 0);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(18, 32, 0), 0, 3);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        //}
    }

    protected void closeWestDoor(Position base) {
        //if (doorState[WEST_DOOR] != PestControlDoorState.BROKEN && doorState[WEST_DOOR] != PestControlDoorState.HALF_BROKEN) {
            doorState[WEST_DOOR] = PestControlDoorState.CLOSED;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(18, 33, 0), 0, 1);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(18, 33, 0), 0, 1);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_120, base.transform(19, 33, 0), 0, 0);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(18, 32, 0), 0, 3);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(18, 32, 0), 0, 3);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_122, base.transform(19, 32, 0), 0, 0);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        //}
    }

    protected void openEastDoor(Position base) {
        //if (doorState[EAST_DOOR] != PestControlDoorState.BROKEN && doorState[EAST_DOOR] != PestControlDoorState.HALF_BROKEN) {
            doorState[EAST_DOOR] = PestControlDoorState.OPENED;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_122, base.transform(46, 33, 0), 0, 2);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(46, 33, 0), 0, 2);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(47, 33, 0), 0, 1);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_120, base.transform(46, 32, 0), 0, 2);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(46, 32, 0), 0, 2);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(47, 32, 0), 0, 3);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        //}
    }

    protected void closeEastDoor(Position base) {
        //if (doorState[EAST_DOOR] != PestControlDoorState.BROKEN && doorState[EAST_DOOR] != PestControlDoorState.HALF_BROKEN) {
            doorState[EAST_DOOR] = PestControlDoorState.CLOSED;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(47, 33, 0), 0, 1);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(47, 33, 0), 0, 1);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_122, base.transform(46, 33, 0), 0, 2);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(47, 32, 0), 0, 3);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(47, 32, 0), 0, 3);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_120, base.transform(46, 32, 0), 0, 2);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        //}
    }

    public void breakEastDoor(Position base) {
        if (doorState[EAST_DOOR] == PestControlDoorState.OPENED || doorState[EAST_DOOR] == PestControlDoorState.CLOSED) {
            doorState[EAST_DOOR] = PestControlDoorState.HALF_BROKEN;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_122, base.transform(46, 33, 0), 0, 2);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(46, 33, 0), 0, 2);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(47, 33, 0), 0, 1);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(47, 33, 0), 0, 1);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(47, 33, 0), 0, 1);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_120, base.transform(46, 32, 0), 0, 2);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(46, 32, 0), 0, 2);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(47, 32, 0), 0, 3);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(47, 32, 0), 0, 3);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(47, 32, 0), 0, 3);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        }
        else if (doorState[EAST_DOOR] == PestControlDoorState.HALF_BROKEN) {
            doorState[EAST_DOOR] = PestControlDoorState.BROKEN;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(47, 33, 0), 0, 1);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(47, 33, 0), 0, 1);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_132, base.transform(47, 34, 0), 22, 3);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(47, 32, 0), 0, 3);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(47, 32, 0), 0, 3);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_133, base.transform(47, 31, 0), 22, 1);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        }
    }

    public void breakWestDoor(Position base) {
        if (doorState[WEST_DOOR] == PestControlDoorState.OPENED || doorState[WEST_DOOR] == PestControlDoorState.CLOSED) {
            doorState[WEST_DOOR] = PestControlDoorState.HALF_BROKEN;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_120, base.transform(19, 33, 0), 0, 0);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(19, 33, 0), 0, 0);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(18, 33, 0), 0, 1);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(18, 33, 0), 0, 1);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(18, 33, 0), 0, 1);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_122, base.transform(19, 32, 0), 0, 0);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(19, 32, 0), 0, 0);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(18, 32, 0), 0, 3);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(18, 32, 0), 0, 3);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(18, 32, 0), 0, 3);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        }
        else if (doorState[WEST_DOOR] == PestControlDoorState.HALF_BROKEN) {
            doorState[WEST_DOOR] = PestControlDoorState.BROKEN;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(18, 33, 0), 0, 1);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(18, 33, 0), 0, 1);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_132, base.transform(18, 34, 0), 22, 3);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(18, 32, 0), 0, 3);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(18, 32, 0), 0, 3);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_133, base.transform(18, 31, 0), 22, 1);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        }
    }

    public void breakSouthDoor(Position base) {
        if (doorState[SOUTH_DOOR] == PestControlDoorState.OPENED || doorState[SOUTH_DOOR] == PestControlDoorState.CLOSED) {
            doorState[SOUTH_DOOR] = PestControlDoorState.HALF_BROKEN;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(32, 24, 0), 0, 0);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(32, 24, 0), 0, 0);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_120, base.transform(32, 25, 0), 0, 3);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(32, 25, 0), 0, 3);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(32, 24, 0), 0, 0);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(33, 24, 0), 0, 2);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(33, 24, 0), 0, 2);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_122, base.transform(33, 25, 0), 0, 3);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(33, 25, 0), 0, 3);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(33, 24, 0), 0, 2);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        }
        else if (doorState[SOUTH_DOOR] == PestControlDoorState.HALF_BROKEN) {
            doorState[SOUTH_DOOR] = PestControlDoorState.BROKEN;

            DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(32, 24, 0), 0, 0);
            CollisionManager.removeObjectClipping(leftDoor);
            leftDoor = DynamicGameObject.createPublic(-1, base.transform(32, 24, 0), 0, 0);
            World.addObject(leftDoor);

            leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_133, base.transform(31, 24, 0), 22, 2);
            World.addObject(leftDoor);
            CollisionManager.addObjectClipping(leftDoor);

            DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(33, 24, 0), 0, 2);
            CollisionManager.removeObjectClipping(rightDoor);
            rightDoor = DynamicGameObject.createPublic(-1, base.transform(33, 24, 0), 0, 2);
            World.addObject(rightDoor);

            rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_132, base.transform(34, 24, 0), 22, 0);
            World.addObject(rightDoor);
            CollisionManager.addObjectClipping(rightDoor);
        }
    }

    protected void repairWestDoor(Player player, Position base) {
        Inventory inventory = player.getInventory();
        if (inventory.contains(ItemID.HAMMER) && inventory.contains(ItemID.LOGS)) {
            inventory.delete(ItemID.LOGS, 1);
            player.getPacketSender().sendAreaPlayerSound(1728, 5, 1, 0);
            if (doorState[WEST_DOOR] == PestControlDoorState.BROKEN) {
                doorState[WEST_DOOR] = PestControlDoorState.HALF_BROKEN;
                DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_132, base.transform(18, 34, 0), 22, 3);
                CollisionManager.removeObjectClipping(leftDoor);
                leftDoor = DynamicGameObject.createPublic(-1, base.transform(18, 34, 0), 22, 3);
                World.addObject(leftDoor);

                leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(18, 33, 0), 0, 1);
                World.addObject(leftDoor);
                CollisionManager.addObjectClipping(leftDoor);

                DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_133, base.transform(18, 31, 0), 22, 1);
                CollisionManager.removeObjectClipping(rightDoor);
                rightDoor = DynamicGameObject.createPublic(-1, base.transform(18, 31, 0), 22, 1);
                World.addObject(rightDoor);

                rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(18, 32, 0), 0, 3);
                World.addObject(rightDoor);
                CollisionManager.addObjectClipping(rightDoor);
            }
            else if (doorState[WEST_DOOR] == PestControlDoorState.HALF_BROKEN) {
                doorState[WEST_DOOR] = PestControlDoorState.OPENED;
                DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(18, 33, 0), 0, 1);
                CollisionManager.removeObjectClipping(leftDoor);
                leftDoor = DynamicGameObject.createPublic(-1, base.transform(18, 33, 0), 0, 1);
                World.addObject(leftDoor);

                leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(18, 33, 0), 0, 1);
                World.addObject(leftDoor);
                CollisionManager.addObjectClipping(leftDoor);

                DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(18, 32, 0), 0, 3);
                CollisionManager.removeObjectClipping(rightDoor);
                rightDoor = DynamicGameObject.createPublic(-1, base.transform(18, 32, 0), 0, 3);
                World.addObject(rightDoor);

                rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(18, 32, 0), 0, 3);
                World.addObject(rightDoor);
                CollisionManager.addObjectClipping(rightDoor);
            }
        } else {
            player.sendMessage("You need a hammer and logs to repair that!");
        }

    }

    protected void repairEastDoor(Player player, Position base) {
        Inventory inventory = player.getInventory();
        if (inventory.contains(ItemID.HAMMER) && inventory.contains(ItemID.LOGS)) {
            inventory.delete(ItemID.LOGS, 1);
            player.getPacketSender().sendAreaPlayerSound(1728, 5, 1, 0);
            if (doorState[EAST_DOOR] == PestControlDoorState.BROKEN) {
                doorState[EAST_DOOR] = PestControlDoorState.HALF_BROKEN;

                DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_132, base.transform(47, 34, 0), 22, 3);
                CollisionManager.removeObjectClipping(leftDoor);
                leftDoor = DynamicGameObject.createPublic(-1, base.transform(47, 34, 0), 22, 3);
                World.addObject(leftDoor);

                leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(47, 33, 0), 0, 1);
                World.addObject(leftDoor);
                CollisionManager.addObjectClipping(leftDoor);

                DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_133, base.transform(47, 31, 0), 22, 1);
                CollisionManager.removeObjectClipping(rightDoor);
                rightDoor = DynamicGameObject.createPublic(-1, base.transform(47, 31, 0), 22, 1);
                World.addObject(rightDoor);

                rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(47, 32, 0), 0, 3);
                World.addObject(rightDoor);
                CollisionManager.addObjectClipping(rightDoor);
            }
            else if (doorState[EAST_DOOR] == PestControlDoorState.HALF_BROKEN) {
                doorState[EAST_DOOR] = PestControlDoorState.OPENED;

                DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(47, 33, 0), 0, 1);
                CollisionManager.removeObjectClipping(leftDoor);
                leftDoor = DynamicGameObject.createPublic(-1, base.transform(47, 33, 0), 0, 1);
                World.addObject(leftDoor);

                leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(47, 33, 0), 0, 1);
                World.addObject(leftDoor);
                CollisionManager.addObjectClipping(leftDoor);

                DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(47, 32, 0), 0, 3);
                CollisionManager.removeObjectClipping(rightDoor);
                rightDoor = DynamicGameObject.createPublic(-1, base.transform(47, 32, 0), 0, 3);
                World.addObject(rightDoor);

                rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(47, 32, 0), 0, 3);
                World.addObject(rightDoor);
                CollisionManager.addObjectClipping(rightDoor);
            }
        } else {
            player.sendMessage("You need a hammer and logs to repair that!");
        }

    }

    protected void repairSouthDoor(Player player, Position base) {
        Inventory inventory = player.getInventory();
        if (inventory.contains(ItemID.HAMMER) && inventory.contains(ItemID.LOGS)) {
            inventory.delete(ItemID.LOGS, 1);
            player.getPacketSender().sendAreaPlayerSound(1728, 5, 1, 0);
            if (doorState[SOUTH_DOOR] == PestControlDoorState.BROKEN) {
                doorState[SOUTH_DOOR] = PestControlDoorState.HALF_BROKEN;

                DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_133, base.transform(31, 24, 0), 22, 2);
                CollisionManager.removeObjectClipping(leftDoor);
                leftDoor = DynamicGameObject.createPublic(-1, base.transform(31, 24, 0), 22, 2);
                World.addObject(leftDoor);

                leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(32, 24, 0), 0, 0);
                World.addObject(leftDoor);
                CollisionManager.addObjectClipping(leftDoor);

                DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_132, base.transform(34, 24, 0), 22, 0);
                CollisionManager.removeObjectClipping(rightDoor);
                rightDoor = DynamicGameObject.createPublic(-1, base.transform(34, 24, 0), 22, 0);
                World.addObject(rightDoor);

                rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(33, 24, 0), 0, 2);
                World.addObject(rightDoor);
                CollisionManager.addObjectClipping(rightDoor);
            }
            else if (doorState[SOUTH_DOOR] == PestControlDoorState.HALF_BROKEN) {
                doorState[SOUTH_DOOR] = PestControlDoorState.OPENED;

                DynamicGameObject leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_130, base.transform(32, 24, 0), 0, 0);
                CollisionManager.removeObjectClipping(leftDoor);
                leftDoor = DynamicGameObject.createPublic(-1, base.transform(32, 24, 0), 0, 0);
                World.addObject(leftDoor);

                leftDoor = DynamicGameObject.createPublic(ObjectID.GATE_121, base.transform(32, 24, 0), 0, 0);
                World.addObject(leftDoor);
                CollisionManager.addObjectClipping(leftDoor);

                DynamicGameObject rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_129, base.transform(33, 24, 0), 0, 2);
                CollisionManager.removeObjectClipping(rightDoor);
                rightDoor = DynamicGameObject.createPublic(-1, base.transform(33, 24, 0), 0, 2);
                World.addObject(rightDoor);

                rightDoor = DynamicGameObject.createPublic(ObjectID.GATE_123, base.transform(33, 24, 0), 0, 2);
                World.addObject(rightDoor);
                CollisionManager.addObjectClipping(rightDoor);
            }
        } else {
            player.sendMessage("You need a hammer and logs to repair that!");
        }

    }

    public PestControlDoorState getEastDoorState() {
        return doorState[EAST_DOOR];
    }

    public PestControlDoorState getWestDoorState() {
        return doorState[WEST_DOOR];
    }

    public PestControlDoorState getSouthDoorState() {
        return doorState[SOUTH_DOOR];
    }
}
