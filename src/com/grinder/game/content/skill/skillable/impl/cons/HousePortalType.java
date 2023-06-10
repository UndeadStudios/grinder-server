package com.grinder.game.content.skill.skillable.impl.cons;

import com.grinder.game.content.skill.skillable.impl.magic.Teleporting.TeleportLocation;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;

import java.util.Iterator;

/**
 * @author Simplex
 * @since Mar 27, 2020
 */
public enum HousePortalType {
    VARROCK(1, TeleportLocation.VARROCK.getPosition().clone(), 25,
            new int[][]{{563, 100}, {554, 100/*fire*/}, {556, 300 /*Air*/}}, new int[]{13615, 13622, 13629}),
    LUMBRIDGE(2, TeleportLocation.LUMBRIDGE.getPosition().clone(), 31,
            new int[][]{{563, 100}, {557, 100/*EARTH*/}, {556, 300}}, new int[]{13616, 13623, 13630}),
    FALADOR(3, TeleportLocation.FALADOR.getPosition().clone(), 37,
            new int[][]{{563, 100}, {555, 100/*water*/}, {556, 300}}, new int[]{13617, 13624, 13631}),
    CAMELOT(4, TeleportLocation.CAMELOT.getPosition().clone(), 45,
            new int[][]{{563, 200}, {556, 500}}, new int[]{13618, 13625, 13632}),
    ARDOUGNE(5, TeleportLocation.ARDOUGNE.getPosition().clone(), 51,
            new int[][]{{563, 200}, {555, 200}}, new int[]{13619, 13626, 13633}),
    YANILLE(6, TeleportLocation.YANILLE.getPosition().clone(), 58,
            new int[][]{{563, 200}, {557, 200}}, new int[]{13620, 13627, 13634}),
    KHARYLL(7, TeleportLocation.CANIFIS.getPosition().clone(), 66,
            new int[][]{{563, 200}, {565, 100}}, new int[]{13621, 13628, 13635}),
    EMPTY(-1, null, -1, null, null),
    ;
    private Position destination;
    private int[][] requiredItems;
    private int[] objects;
    private int magicLevel, type;

    private HousePortalType(int id, Position destination, int magicLevel, int[][] requiredItems, int[] objects) {
        this.type = id;
        this.destination = destination;
        this.requiredItems = requiredItems;
        this.objects = objects;
        this.magicLevel = magicLevel;
    }

    public Position getDestination() {
        return destination;
    }

    public static HousePortalType forType(int type) {
        for (HousePortalType p : values())
            if (p.type == type)
                return p;
        return null;
    }

    public static HousePortalType forObjectId(int objectId) {
        for (HousePortalType p : values()) {
            for (int i : p.objects)
                if (i == objectId)
                    return p;
        }
        return null;
    }

    public int[] getObjects() {
        return objects;
    }

    public String canBuild(Player p) {
        if (requiredItems == null) {
            boolean found = false;
            int[] myTiles = Construction.getCurrentChunk(p);
            Iterator<HousePortal> it = p.getHouse().getHousePortals().iterator();
            while (it.hasNext()) {
                HousePortal portal = it.next();
                if (portal.getRoomX() == myTiles[0] - 1
                        && portal.getRoomY() == myTiles[1] - 1
                        && portal.getRoomZ() == (p.isInHouseDungeon() ? 4 : p.getPosition().getZ())
                        && portal.getId() == p.getHouse().getPortalSelected()) {
                    it.remove();
                    found = true;
                    break;
                }
            }
            if (!found) {
                p.getPacketSender().sendInterfaceRemoval();
                return "Can't remove that, doesn't exist.";
            } else {
                //p.getPacketSender().sendObjectsRemoval(myTiles[0]-1, myTiles[1]-1, p.isInHouseDungeon() ? 4 : p.getPosition().getZ());
                //Construction.placeAllFurniture(p, myTiles[0]-1, myTiles[1]-1, p.isInHouseDungeon() ? 4 : p.getPosition().getZ());
                p.getPacketSender().sendInterfaceRemoval();
                return null;
            }
        }
        for (int i = 0; i < requiredItems.length; i++) {
            if (!p.getInventory().contains(requiredItems[i][0]))
                return "You don't have the required items to build this.";
            else if (!p.getInventory().contains(requiredItems[i][1]))
                return "You don't have the required items to build this.";
        }
        if (p.getSkillManager().getCurrentLevel(Skill.MAGIC) < magicLevel)
            return "You need a magic level of " + magicLevel + " to build this";
        build(p);
        return null;
    }

    public void build(Player p) {
        for (int i = 0; i < requiredItems.length; i++) {
            p.getInventory().delete(requiredItems[i][0], requiredItems[i][1]);
        }
        int[] myTiles = Construction.getCurrentChunk(p);
        boolean found = false;
        for (HousePortal portal : p.getHouse().getHousePortals()) {
            if (portal.getRoomX() == myTiles[0] - 1
                    && portal.getRoomY() == myTiles[1] - 1
                    && portal.getRoomZ() == (p.isInHouseDungeon() ? 4 : p.getPosition().getZ())
                    && portal.getId() == p.getHouse().getPortalSelected()) {
                portal.setType(type);
                found = true;
            }
        }
        if (!found) {
            HousePortal portal = new HousePortal();
            portal.setId(p.getHouse().getPortalSelected());
            portal.setRoomX(myTiles[0] - 1);
            portal.setRoomY(myTiles[1] - 1);
            portal.setRoomZ(p.isInHouseDungeon() ? 4 : p.getPosition().getZ());
            portal.setType(type);
            p.getHouse().getHousePortals().add(portal);
        }
        //p.getPacketSender().sendObjectsRemoval(myTiles[0]-1, myTiles[1]-1, p.isInHouseDungeon() ? 4 : p.getPosition().getZ());
        //Construction.placeAllFurniture(p, myTiles[0]-1, myTiles[1]-1, p.isInHouseDungeon() ? 4 : p.getPosition().getZ());
        p.getPacketSender().sendInterfaceRemoval();
    }
}
