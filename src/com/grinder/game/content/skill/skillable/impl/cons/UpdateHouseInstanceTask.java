package com.grinder.game.content.skill.skillable.impl.cons;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.instanced.HouseInstance;
import com.grinder.game.task.Task;

/**
 * Send house instance for the first time.
 *
 * @author Simplex
 * @since Apr 03, 2020
 */
public class UpdateHouseInstanceTask extends Task {

    private Player player;
    int cycle = 0, x = -1, y = -1;
    boolean inArea, dungeon;
    HouseInstance houseInstance;

    public UpdateHouseInstanceTask(Player player, HouseInstance houseInstance) {
        super(1, player, true);

        this.player = player;
        this.houseInstance = houseInstance;
        this.dungeon = player.isInHouseDungeon();
        this.inArea = player.getArea() != null && AreaManager.inside(player.getPosition(), player.getArea());

        // add player to the instance
        if (player.getArea() != houseInstance) {
            player.setArea(houseInstance);
            houseInstance.add(player);
        }
    }

    private void sendPalette() {
        player.getPacketSender().constructMapRegion(
                dungeon ? houseInstance.getDungeonPalette() : houseInstance.getSurfacePalette());
    }

    @Override
    protected void execute() {
        switch (cycle++) {
            case 0:
                // create house tile palettes if the owner is joining the instance
                if(player == houseInstance.getHouseOwner()) {
                    if(player.isInHouseDungeon()) {
                        Construction.generateDungeonPalette(player);
                    } else {
                        Construction.generateSurfacePalette(player);
                    }
                }

                // move to construction area
                player.moveTo(new Position(ConstructionUtils.MIDDLE_X, ConstructionUtils.MIDDLE_Y, houseInstance.getZ()));

                // remove chatbox interface for entering house
                player.getPacketSender().sendInterfaceRemoval();

                // Set the enter house interface
                player.getPacketSender().sendMapState(2);
                player.getPacketSender().sendWalkableInterface(128640);
                player.getPacketSender().sendBlackScreen();

                // temporarily block input
                player.BLOCK_ALL_BUT_TALKING = true;

                // if player is already in the instance, send new palette
                // otherwise, we need to wait until the player has moved
                // to the construction area.
                if(inArea) {
                    sendPalette();
                }

                // Move to the position in the house where they have updated
                // the palette from, otherwise send them to the center.
                if (player.getHouse().getConstructionBuildPosition() != null) {
                    player.moveTo(player.getHouse().getConstructionBuildPosition().clone());
                    player.getHouse().setConstructionBuildPosition(null);
                } else {
                    HouseFurniture portal = Construction.findFirstEntrancePortal(player);
                    x = ConstructionUtils.BASE_X + ((portal.getRoomX() + 1) * 8);
                    y = ConstructionUtils.BASE_Y + ((portal.getRoomY() + 1) * 8);
                }
                break;

            case 1:

                // If the player was not in the area they will have now entered
                // the region and the palette may be set.
                if(!inArea) {
                    sendPalette();
                }

                // Recenter after constructing new region
                if (x != -1 && y != -1) {
                    player.moveTo(new Position(x + 2, y + 3));
                }
                break;
            case 2:
                // Write all furniture to the client, in the future this
                // could be done with configs.
                if (dungeon)
                    Construction.placeAllFurniture(player, 0);
                else {
                    Construction.placeAllFurniture(player, 0);
                    Construction.placeAllFurniture(player, 1);
                }

                // clean up
                player.getPacketSender().sendWalkableInterface(-1);
                player.getPacketSender().sendInterfaceRemoval();
                player.getPacketSender().sendMapState(0);

                // add player to the house instance once updated
                player.BLOCK_ALL_BUT_TALKING = false;
                this.stop();
                break;
        }
    }
}
