package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.minigame.Party;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Coordinate;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;
import com.grinder.util.oldgrinder.BitMask;
import com.grinder.util.oldgrinder.EquipSlot;
import com.grinder.util.oldgrinder.StreamHandler;

import java.util.stream.Stream;

import static com.grinder.game.content.minigame.castlewars.CastleWars.zamorakParty;
import static com.grinder.game.content.minigame.castlewars.CastleWars.saradominParty;
import static com.grinder.game.content.minigame.castlewars.CastleWars.droppedFlag;
import static com.grinder.game.content.minigame.castlewars.CastleWars.flagState;
import static com.grinder.game.content.minigame.castlewars.CastleWars.getTeamID;
import static com.grinder.game.content.minigame.castlewars.CastleWars.inSaraSafeRoom;
import static com.grinder.game.content.minigame.castlewars.CastleWars.inZammySafeRoom;
import static com.grinder.game.content.minigame.castlewars.CastleWars.teamPoints;
import static com.grinder.game.content.minigame.castlewars.CastleWars.setUpdateInterface;
import static com.grinder.game.content.minigame.castlewars.CastleWarsConstants.*;

public class FlagManager {
    public static void captureFlag(Player player, int standID, Party standParty) {
        if(saradominParty.memberCount() <= 0 || zamorakParty.memberCount() <= 0) {
            player.sendMessage("There must be players in both teams in order to capture the flag.");
            return;
        }
        if (player.getCurrentParty() == standParty) {
            if (standParty == saradominParty) {
                if (isHoldingFlag(player, zamorakParty)) {
                    player.cwGameCaptures++;
                    player.castleWarsCaptures++;
                    removeHoldingFlag(player);
                    returnFlagToStand(zamorakParty);
                    teamPoints[SARADOMIN_TEAM]++;
                    setUpdateInterface(true);
                    return;
                } else {
                    player.sendMessage("Saradomin won't let you take his standard!");
                    return;
                }
            } else if (standParty == zamorakParty) {
                if (isHoldingFlag(player, saradominParty)) {
                    player.cwGameCaptures++;
                    player.castleWarsCaptures++;
                    removeHoldingFlag(player);
                    returnFlagToStand(saradominParty);
                    teamPoints[ZAMORAK_TEAM]++;
                    setUpdateInterface(true);
                    return;
                } else {
                    player.sendMessage("Zamorak won't let you take his standard!");
                    return;
                }
            }
        } else if (standID == OBJECT_SARADOMIN_FLAG_STAND || standID == OBJECT_ZAMORAK_FLAG_STAND) {
            if (isHoldingFlag(player)) {
                dropFlag(player);
            }
            if (canHoldFlag(player)) {
                if (prepareFlagHolder(player)) {
                    holdFlag(player, standParty);
                    removeFlagFromStand(standParty);
                    if (standParty == saradominParty) {
                        flagState[SARADOMIN_TEAM] = FlagState.TAKEN;
                    } else if (standParty == zamorakParty) {
                        flagState[ZAMORAK_TEAM] = FlagState.TAKEN;
                    }
                    //ArrowPointerManager.setEntityPointer(standParty, player.playerId);

                    ArrowPointerManager.showPointer(player.getCurrentParty());
                    setUpdateInterface(true);
                }
            } else {
                player.sendMessage("You don't have enough inventory space to hold the flag!");
            }
        }
    }

    private static boolean canHoldFlag(Player player) {
        Item playerWeapon = player.getEquipment().get(EquipSlot.WEAPON);
        Item playerShield = player.getEquipment().get(EquipSlot.SHIELD);

        int requiredSlots = 0;

        if (playerWeapon != null) {
            requiredSlots++;
        }
        if (playerShield != null) {
            requiredSlots++;
        }
        return player.getInventory().countFreeSlots() >= requiredSlots;
    }

    private static boolean prepareFlagHolder(Player player) {
        int slotsNeeded = 0;
        Item playerWeapon = player.getEquipment().get(EquipSlot.WEAPON);
        Item playerShield = player.getEquipment().get(EquipSlot.SHIELD);
        if (playerWeapon != null) {
            slotsNeeded++;
        }
        if (playerShield != null) {
            slotsNeeded++;
        }
        if (slotsNeeded > 0) {
            if (player.getInventory().countFreeSlots() <= slotsNeeded) {
                player.sendMessage("not enough space in your inventory!");
                return false;
            }
        }

        if (playerWeapon != null) {
            if (player.getInventory().countFreeSlots() >= 1) {
                player.getInventory().add(playerWeapon);
                player.getEquipment().reset(EquipSlot.WEAPON);
            } else {
                return false;
            }
        }
        if (playerShield != null) {
            if (player.getInventory().countFreeSlots() >= 1) {
                player.getInventory().add(playerShield);
                player.getEquipment().reset(EquipSlot.SHIELD);
            } else {
                return false;
            }
        }

        return true;
    }

    private static void holdFlag(Player player, Party flagParty) {
        Item playerWeapon = player.getEquipment().get(EquipSlot.WEAPON);

        if (playerWeapon != null) {
            player.getInventory().add(playerWeapon);
            player.getEquipment().reset(EquipSlot.WEAPON);
        }
        player.getEquipment().get(EquipSlot.WEAPON).setId(flagParty == saradominParty ? ITEM_SARADOMIN_FLAG : ITEM_ZAMORAK_FLAG);
        player.getEquipment().get(EquipSlot.WEAPON).setAmount(1);
        EquipmentBonuses.update(player);
        player.getEquipment().refreshItems();
        WeaponInterfaces.INSTANCE.assign(player);
        player.updateAppearance();
    }

    private static void removeFlagFromStand(Party team) {
        if (team == zamorakParty) {
            World.addObject(DynamicGameObject.createPublic(OBJECT_ZAMORAK_STAND, new Position(2370, 3133, 3)));
        } else if (team == saradominParty) {
            World.addObject(DynamicGameObject.createPublic(OBJECT_SARADOMIN_STAND, new Position(2429, 3074, 3)));
        }
    }

    public static void returnFlagToStand(Party party) {
        if (party == saradominParty) {
            World.addObject(DynamicGameObject.createPublic(OBJECT_SARADOMIN_FLAG_STAND, new Position(2429, 3074, 3)));
            flagState[SARADOMIN_TEAM] = FlagState.SAFE;
            ArrowPointerManager.removePointers(saradominParty);
            ArrowPointerManager.hidePointer(TeamManager.getOppositeTeam(saradominParty));
        } else if (party == zamorakParty) {
            World.addObject(DynamicGameObject.createPublic(OBJECT_ZAMORAK_FLAG_STAND, new Position(2370, 3133, 3)));
            flagState[ZAMORAK_TEAM] = FlagState.SAFE;
            ArrowPointerManager.removePointers(zamorakParty);
            ArrowPointerManager.hidePointer(TeamManager.getOppositeTeam(zamorakParty));
        }
        setUpdateInterface(true);
    }

    public static boolean isHoldingFlag(Player player) {
        return (player.getEquipment().get(EquipSlot.WEAPON).getId() == ITEM_SARADOMIN_FLAG || player.getEquipment().get(EquipSlot.WEAPON).getId() == ITEM_ZAMORAK_FLAG);
    }

    protected static boolean isHoldingFlag(Player player, Party flagParty) {
        if (flagParty == saradominParty) {
            return player.getEquipment().contains(new Item(ITEM_SARADOMIN_FLAG));
        } else if (flagParty == zamorakParty) {
            return player.getEquipment().contains(new Item(ITEM_ZAMORAK_FLAG));
        }
        return false;
    }

    protected static void removeHoldingFlag(Player player) {
        player.getEquipment().reset(EquipSlot.WEAPON);
        EquipmentBonuses.update(player);
        player.getEquipment().refreshItems();
        WeaponInterfaces.INSTANCE.assign(player);
        player.updateAppearance();
    }

    public static void pickupFlag(Player player, int objectID, int objectX, int objectY, int height, Party playerParty) {
        if(inZammySafeRoom(player) || inSaraSafeRoom(player)){
            return;
        }
        if (playerParty.getTeamID() == SARADOMIN_TEAM && objectID == OBJECT_SARADOMIN_FLAG) {
            if (getCastleTeam(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()) == playerParty) {
                GameObject obj = ObjectManager.findDynamicObjectAt(objectID, new Position(objectX, objectY, height)).orElse(null);
                if (obj != null) {
                    ObjectManager.remove(obj, true);
                }

                returnFlagToStand(playerParty);
                return;
            }

            zamorakParty.getPlayers().stream().forEach((p) -> {
                StreamHandler.createObjectHints(p, 0, 0, 0, 0);
            });
        }
        if (playerParty.getTeamID() == ZAMORAK_TEAM && objectID == OBJECT_ZAMORAK_FLAG) {
            if (getCastleTeam(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()) == playerParty) {
                GameObject obj = ObjectManager.findDynamicObjectAt(objectID, new Position(objectX, objectY, height)).orElse(null);
                if (obj != null) {
                    ObjectManager.remove(obj, true);
                }
                StreamHandler.createObjectHints(player, 0, 0, 0, 0);
                returnFlagToStand(playerParty);
                return;
            }

            saradominParty.getPlayers().stream().forEach((p) -> {
                StreamHandler.createObjectHints(p, 0, 0, 0, 0);
            });
        }

        if (canHoldFlag(player)) {
            if (prepareFlagHolder(player)) {
                Party flagParty = getFlagParty(objectID);
                if (flagParty == null) {
                    return;
                }
                int teamID = getTeamID(flagParty);
                if (teamID != -1) {
                    Coordinate droppedCoordinate = droppedFlag[teamID];
                    if (droppedCoordinate != null) {
                        if (objectX == droppedCoordinate.getX() && objectY == droppedCoordinate.getY() && height == droppedCoordinate.getH()) {
                            holdFlag(player, flagParty);
                            GameObject obj = ObjectManager.findDynamicObjectAt(objectID, new Position(objectX, objectY, height)).orElse(null);
                            if (obj != null) {
                                ObjectManager.remove(obj, true);
                            }
                            flagState[teamID] = FlagState.TAKEN;
                            //ArrowPointerManager.setEntityPointer(flagParty, player.playerId);
                            ArrowPointerManager.showPointer(TeamManager.getOppositeTeam(flagParty));
                            droppedFlag[teamID] = null;
                            setUpdateInterface(true);
                        }
                    }
                    StreamHandler.createObjectHints(player, 0, 0, 0, 0);
                }
            }
        } else {
            player.sendMessage("You don't have enough inventory space to hold the flag!");
        }
    }

    public static void removeDroppedFlag(Party team) {
        int teamID = getTeamID(team);
        if (teamID != -1) {
            Coordinate droppedCoordinate = droppedFlag[teamID];
            if (droppedCoordinate != null) {
                int objectX = droppedCoordinate.getX();
                int objectY = droppedCoordinate.getY();
                int height = droppedCoordinate.getH();
                /*
                RSObject flagObject = Region.getMapObject(objectX, objectY, height);
                if (flagObject != null) {
                    int positionFlag = flagObject.id;
                    if (positionFlag == CastleWarsConstants.OBJECT_ZAMORAK_FLAG || positionFlag == CastleWarsConstants.OBJECT_SARADOMIN_FLAG) {
                        ObjectManager.remove(ObjectManager.findDynamicObjectAt(objectID, new Position(objectX, objectY, height)).get(), true);

                        ObjectHandler.removeSpawn(new RegionObject(flagObject.id, objectX, objectY, height, FACE_DROPPED_FLAG, 11), null, true);
                        droppedFlag[teamID] = null;
                        setUpdateInterface(true);
                    }
                }*/
            }
        }
    }

    public static void dropFlag(Player player) {
        int wearingFlg = player.getEquipment().get(EquipSlot.WEAPON).getId();
        Party flagParty = getFlagParty(wearingFlg);

        int spawnX = player.getX();
        int spawnY = player.getY();
        int spawnHeight = player.getZ();

        if (!isHoldingFlag(player, flagParty)) {
            return;
        }

        removeHoldingFlag(player);

        if (flagState[flagParty.getTeamID()] == FlagState.TAKEN) {
            if (player.inCastleWars()) {

                // If the flag is being dropped outside castle area return it to
                // stand.
                if (!inGameCoordinates(new Position(spawnX, spawnY, spawnHeight)) || inSaraSafeRoom(spawnX, spawnY, spawnHeight) || inZammySafeRoom(spawnX, spawnY, spawnHeight)) {
                    returnFlagToStand(flagParty);
                } else {
                    boolean spawned = spawnDroppedFlag(wearingFlg, spawnX, spawnY, spawnHeight);
                    if (spawned) {
                        ArrowPointerManager.setFlagPointer(flagParty, new Coordinate(spawnX, spawnY, spawnHeight));
                        ArrowPointerManager.showPointer(TeamManager.getOppositeTeam(flagParty));
                    } else {
                        ArrowPointerManager.removePointers(flagParty);
                        ArrowPointerManager.hidePointer(TeamManager.getOppositeTeam(flagParty));
                    }
                }
            } else {// If player is not at castle wars area just return flag to stand.
                returnFlagToStand(flagParty);
            }
        }
    }

    private static boolean inGameCoordinates(Position position) {
        if (position == null) {
            return false;
        }
        return (position.getX() >= 2368 && position.getX() <= 2431 && (position.getY() >= 3072 && position.getY() <= 3135) || (position.getY() >= 9480 && position.getY() <= 9534));
    }

    private static Position findAvailableSpot(int srcX, int srcY, int height, int minRange, int maxRange) {
        for (int distance = minRange; distance < maxRange; distance++) {
            Position foundSpot = findAvailableSpot(srcX, srcY, height, distance);
            if (foundSpot != null)
                return foundSpot;
        }
        return null;
    }

    private static Position findAvailableSpot(int srcX, int srcY, int height, int distance) {
        int initialX = srcX - distance;
        int initialY = srcY - distance;
        int finalX = srcX + distance;
        int finalY = srcY + distance;

        return CastleWars.findAvailableSpot(new Boundary(initialX, finalX, initialY, finalY), height);
    }

    private static boolean spawnDroppedFlag(int flagID, int x, int y, int h) {

        if (flagID == ITEM_ZAMORAK_FLAG) {
            World.addObject(DynamicGameObject.createPublic(OBJECT_ZAMORAK_FLAG, new Position(x, y, h)));
            flagState[ZAMORAK_TEAM] = FlagState.DROPPED;
            droppedFlag[ZAMORAK_TEAM] = new Coordinate(x, y, h);
        } else if (flagID == ITEM_SARADOMIN_FLAG) {
            World.addObject(DynamicGameObject.createPublic(OBJECT_SARADOMIN_FLAG, new Position(x, y, h)));
            flagState[SARADOMIN_TEAM] = FlagState.DROPPED;
            droppedFlag[SARADOMIN_TEAM] = new Coordinate(x, y, h);
        }
        setUpdateInterface(true);
        return true;
    }

    protected static Party getFlagParty(int flagID) {
        if (flagID == ITEM_SARADOMIN_FLAG) {
            return saradominParty;
        }
        if (flagID == OBJECT_SARADOMIN_FLAG) {
            return saradominParty;
        }
        if (flagID == ITEM_ZAMORAK_FLAG) {
            return zamorakParty;
        }
        if (flagID == OBJECT_ZAMORAK_FLAG) {
            return zamorakParty;
        }
        return null;
    }

    protected static Party getCastleTeam(int x, int y, int h) {
        if (x == 2385 && y == 3134 && h == 0) {
            return null;
        }
        if (x == 2414 && y == 3073 && h == 0) {
            return null;
        }
        if ((x == 2372 || x == 2373) && y == 3118 && h == 0) {
            return null;
        }
        if ((x == 2427 || x == 2428) && y == 3089 && h == 0) {
            return null;
        }
        if (x >= 2368 && x <= 2385 && y >= 3118 && y <= 3135) {
            return zamorakParty;
        }
        if (x >= 2414 && x <= 2431 && y >= 3072 && y <= 3092) {
            return saradominParty;
        }

        return null;
    }
}
