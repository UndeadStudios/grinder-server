package com.grinder.game.content.minigame.castlewars;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.minigame.Party;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.object.*;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.concurrent.TimeUnit;

import static com.grinder.game.content.minigame.castlewars.CastleWars.*;
import static com.grinder.game.content.minigame.castlewars.CastleWarsConstants.*;

public class CastleWarsDoorsManager {

    protected static void repairDoor(Party party) {
        if (party == CastleWars.zamorakParty) {
            doorHealth[ZAMORAK_TEAM] = MAX_DOOR_HEALTH;
            closeDoubleDoor(CastleWars.zamorakParty);
        } else if (party == CastleWars.saradominParty) {
            doorHealth[SARADOMIN_TEAM] = MAX_DOOR_HEALTH;
            closeDoubleDoor(CastleWars.saradominParty);
        }
        setUpdateInterface(true);
    }

    protected static void closeSmallDoor(Party party) {

        if (party == CastleWars.zamorakParty) {
            CastleWars.smallDoorState[ZAMORAK_TEAM] = CastleWars.DoorState.CLOSED;
            World.addObject(DynamicGameObject.createPublic(-1, new Position(2385, 3134, 0), 0, 3));
            DynamicGameObject door = DynamicGameObject.createPublic(4467, new Position(2384, 3134, 0), 0, 2);
            World.addObject(door);
            CollisionManager.addObjectClipping(door);
        } else if (party == CastleWars.saradominParty) {
            CastleWars.smallDoorState[SARADOMIN_TEAM] = DoorState.CLOSED;
            World.addObject(DynamicGameObject.createPublic(-1, new Position(2414, 3073, 0), 0, 1));
            DynamicGameObject door = DynamicGameObject.createPublic(4465, new Position(2415, 3073, 0), 0, 0);
            World.addObject(door);
            CollisionManager.addObjectClipping(door);
        }
        setUpdateInterface(true);
    }

    protected static void openSmallDoor(Party party) {
        if (party == CastleWars.zamorakParty) {
            CastleWars.smallDoorState[ZAMORAK_TEAM] = DoorState.OPENED;
            DynamicGameObject door = DynamicGameObject.createPublic(4467, new Position(2384, 3134, 0), 0, 2);
            CollisionManager.removeObjectClipping(door);
            door = DynamicGameObject.createPublic(-1, new Position(2384, 3134, 0), 0, 2);
            World.addObject(door);
            World.addObject(DynamicGameObject.createPublic(4468, new Position(2385, 3134, 0), 0, 3));
        } else if (party == CastleWars.saradominParty) {
            CastleWars.smallDoorState[SARADOMIN_TEAM] = DoorState.OPENED;
            DynamicGameObject door = DynamicGameObject.createPublic(4465, new Position(2415, 3073, 0), 0, 0);
            CollisionManager.removeObjectClipping(door);
            door = DynamicGameObject.createPublic(-1, new Position(2415, 3073, 0), 0, 0);
            World.addObject(door);
            World.addObject(DynamicGameObject.createPublic(4466, new Position(2414, 3073, 0), 0, 1));

        }
        setUpdateInterface(true);
    }

    protected static void openDoubleDoor(Party party) {

        if (party == CastleWars.zamorakParty) {
            bigDoorState[ZAMORAK_TEAM] = DoorState.OPENED;
            DynamicGameObject door1 = DynamicGameObject.createPublic(4428, new Position(2372, 3119, 0), 0, 1);
            DynamicGameObject door2 = DynamicGameObject.createPublic(4427, new Position(2373, 3119, 0), 0, 1);
            CollisionManager.removeObjectClipping(door1);
            CollisionManager.removeObjectClipping(door2);
            door1 = DynamicGameObject.createPublic(-1, new Position(2372, 3119, 0), 0, 1);
            door2 = DynamicGameObject.createPublic(-1, new Position(2373, 3119, 0), 0, 1);

            World.addObject(door1);
            World.addObject(door2);

            World.addObject(DynamicGameObject.createPublic(4429, new Position(2372, 3119, 0), 0, 0));
            World.addObject(DynamicGameObject.createPublic(4430, new Position(2373, 3119, 0), 0, 2));
        } else if (party == CastleWars.saradominParty) {
            bigDoorState[SARADOMIN_TEAM] = DoorState.OPENED;
            DynamicGameObject door1 = DynamicGameObject.createPublic(4423, new Position(2426, 3088, 0), 0, 3);
            DynamicGameObject door2 = DynamicGameObject.createPublic(4424, new Position(2427, 3088, 0), 0, 3);
            CollisionManager.removeObjectClipping(door1);
            CollisionManager.removeObjectClipping(door2);
            door1 = DynamicGameObject.createPublic(-1, new Position(2426, 3088, 0), 0, 3);
            door2 = DynamicGameObject.createPublic(-1, new Position(2427, 3088, 0), 0, 3);
            World.addObject(door1);
            World.addObject(door2);

            World.addObject(DynamicGameObject.createPublic(4425, new Position(2427, 3088, 0), 0, 2));
            World.addObject(DynamicGameObject.createPublic(4426, new Position(2426, 3088, 0), 0, 0));
        }

    }


    protected static void closeDoubleDoor(Party party) {

        if (party == CastleWars.zamorakParty) {
            bigDoorState[ZAMORAK_TEAM] = CastleWars.DoorState.CLOSED;
            World.addObject(DynamicGameObject.createPublic(-1, new Position(2372, 3119, 0), 0, 0));
            World.addObject(DynamicGameObject.createPublic(-1, new Position(2373, 3119, 0), 0, 2));

            DynamicGameObject door1 = DynamicGameObject.createPublic(4427, new Position(2373, 3119, 0), 0, 1);
            DynamicGameObject door2 = DynamicGameObject.createPublic(4428, new Position(2372, 3119, 0), 0, 1);
            World.addObject(door1);
            World.addObject(door2);
            CollisionManager.addObjectClipping(door1);
            CollisionManager.addObjectClipping(door2);
        } else if (party == CastleWars.saradominParty) {
            bigDoorState[SARADOMIN_TEAM] = CastleWars.DoorState.CLOSED;
            World.addObject(DynamicGameObject.createPublic(-1, new Position(2426, 3088, 0), 0, 0));
            World.addObject(DynamicGameObject.createPublic(-1, new Position(2427, 3088, 0), 0, 2));

            DynamicGameObject door1 = DynamicGameObject.createPublic(4423, new Position(2426, 3088, 0), 0, 3);
            DynamicGameObject door2 = DynamicGameObject.createPublic(4424, new Position(2427, 3088, 0), 0, 3);
            World.addObject(door1);
            World.addObject(door2);
            CollisionManager.addObjectClipping(door1);
            CollisionManager.addObjectClipping(door2);

        }

        setUpdateInterface(true);
    }

    protected static void attackDoubleDoor(final Player player, final Party party) {
        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
            return;
        }
        if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == -1) {
            player.sendMessage("You need a weapon to open the door.");
            return;
        }
        final Position startPosition = player.getPosition();

        TaskManager.submit(new Task(4, true) {
            @Override
            protected void execute() {
                if (party == zamorakParty) {
                    if (startPosition.getY() == 3120) {
                        player.setPositionToFace(new Position(player.getPosition().getX(), player.getPosition().getY() - 2));
                    } else {
                        player.setPositionToFace(new Position(player.getPosition().getX(), player.getPosition().getY() + 2));
                    }
                } else {
                    if (startPosition.getY() == 3088) {
                        player.setPositionToFace(new Position(player.getPosition().getX(), player.getPosition().getY() - 2));
                    } else {
                        player.setPositionToFace(new Position(player.getPosition().getX(), player.getPosition().getY() + 2));
                    }
                }
                if (!player.getPosition().sameAs(startPosition)) {
                    stop();
                    return;
                }
                int damage = (player.getSkills().getLevel(Skill.STRENGTH)/4);
                if (damage < 1) {
                    damage = 1;
                }
                doorHealth[party.getTeamID()]-=damage;
                if (doorHealth[party.getTeamID()] <= 0) {
                    breakDoubleDoor(party);

                    setUpdateInterface(true);
                    stop();
                } else {
                    player.performAnimation(new Animation(player.getAttackAnim()));
                }
                // attack sound
            }
        });

    }

    private static void breakDoubleDoor(Party party) {
        if (party == CastleWars.zamorakParty) {
            bigDoorState[ZAMORAK_TEAM] = DoorState.BROKEN;
            doorHealth[ZAMORAK_TEAM] = 0;
            DynamicGameObject door1 = DynamicGameObject.createPublic(4428, new Position(2372, 3119, 0), 0, 1);
            DynamicGameObject door2 = DynamicGameObject.createPublic(4427, new Position(2373, 3119, 0), 0, 1);
            CollisionManager.removeObjectClipping(door1);
            CollisionManager.removeObjectClipping(door2);
            door1 = DynamicGameObject.createPublic(-1, new Position(2372, 3119, 0), 0, 1);
            door2 = DynamicGameObject.createPublic(-1, new Position(2373, 3119, 0), 0, 1);

            World.addObject(door1);
            World.addObject(door2);

            World.addObject(DynamicGameObject.createPublic(4433, new Position(2372, 3119, 0), 0, 0));
            World.addObject(DynamicGameObject.createPublic(4434, new Position(2373, 3119, 0), 0, 2));
        } else if (party == CastleWars.saradominParty) {
            bigDoorState[SARADOMIN_TEAM] = DoorState.BROKEN;
            doorHealth[SARADOMIN_TEAM] = 0;
            DynamicGameObject door1 = DynamicGameObject.createPublic(4423, new Position(2426, 3088, 0), 0, 3);
            DynamicGameObject door2 = DynamicGameObject.createPublic(4424, new Position(2427, 3088, 0), 0, 3);
            CollisionManager.removeObjectClipping(door1);
            CollisionManager.removeObjectClipping(door2);
            door1 = DynamicGameObject.createPublic(-1, new Position(2426, 3088, 0), 0, 3);
            door2 = DynamicGameObject.createPublic(-1, new Position(2427, 3088, 0), 0, 3);
            World.addObject(door1);
            World.addObject(door2);

            World.addObject(DynamicGameObject.createPublic(4432, new Position(2426, 3088, 0), 0, 0));
            World.addObject(DynamicGameObject.createPublic(4431, new Position(2427, 3088, 0), 0, 2));
        }
        setUpdateInterface(true);
    }
}
