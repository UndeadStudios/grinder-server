package com.grinder.game.content.minigame.pestcontrol;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.MapInstance;
/**
 * @author  Minoroin / TealWool#0873 (https://www.rune-server.ee/members/minoroin/)
 * @since   20/11/2021
 * @version 1.0
 */
public class PestControlBarricadesManager {

    private PestControlBarricade eastTopBarricade;
    private PestControlBarricade eastBottomBarricade;

    private PestControlBarricade southEastPortalEastBarricade;
    private PestControlBarricade southEastPortalNorthBarricade;

    private PestControlBarricade southBarricade;

    private PestControlBarricade southWestPortalNorthBarricade;
    private PestControlBarricade southWestPortalWestBarricade;

    private PestControlBarricade westPortalBarricade;

    private Position base = new Position(2624, 2560);

    PestControlBarricadesManager(final PestControlInstance instance) {
        eastTopBarricade = new PestControlBarricade(instance, base.transform(49, 30, 0), Direction.EAST, PestControlBarricadeState.REPAIRED);
        eastBottomBarricade = new PestControlBarricade(instance, base.transform(52, 24, 0), Direction.EAST, PestControlBarricadeState.HALF_BROKEN);
        southEastPortalEastBarricade = new PestControlBarricade(instance, base.transform(52, 14, 0), Direction.WEST, PestControlBarricadeState.BROKEN);
        southEastPortalNorthBarricade = new PestControlBarricade(instance, base.transform(42, 18, 0), Direction.SOUTH, PestControlBarricadeState.REPAIRED);
        southBarricade = new PestControlBarricade(instance, base.transform(32, 15, 0), Direction.SOUTH, PestControlBarricadeState.HALF_BROKEN);
        southWestPortalNorthBarricade = new PestControlBarricade(instance, base.transform(23, 18, 0), Direction.SOUTH, PestControlBarricadeState.REPAIRED);
        southWestPortalWestBarricade = new PestControlBarricade(instance, base.transform(13, 11, 0), Direction.EAST, PestControlBarricadeState.BROKEN);
        westPortalBarricade = new PestControlBarricade(instance, base.transform(12, 31, 0), Direction.WEST, PestControlBarricadeState.HALF_BROKEN);
    }

    protected PestControlBarricade[] getBarricades() {
        PestControlBarricade[] barricades = new PestControlBarricade[8];

        barricades[0] = eastTopBarricade;
        barricades[1] = eastBottomBarricade;
        barricades[2] = southEastPortalEastBarricade;
        barricades[3] = southEastPortalNorthBarricade;
        barricades[4] = southBarricade;
        barricades[5] = southWestPortalNorthBarricade;
        barricades[6] = southWestPortalWestBarricade;
        barricades[7] = westPortalBarricade;

        return barricades;
    }

    protected boolean repairBarricade(Player player, GameObject object) {
        for(int i=0; i<getBarricades().length; i++) {
            PestControlBarricadePart[] parts = getBarricades()[i].getParts();
            for (int part=0; part<parts.length; part++) {
                if (parts[part].getPosition().sameAs(object.getPosition())) {
                    parts[part].repairBarricade(player);
                    return true;
                }
            }
        }
        return false;
    }

}
