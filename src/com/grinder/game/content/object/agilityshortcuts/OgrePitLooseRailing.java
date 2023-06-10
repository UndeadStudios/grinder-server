package com.grinder.game.content.object.agilityshortcuts;

import com.grinder.game.content.skill.skillable.impl.agility.Agility;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.ObjectActions;
import com.grinder.util.ObjectID;

public class OgrePitLooseRailing {

    static {
        /*ObjectActions.INSTANCE.onClick(new int[]{ObjectID.LOOSE_RAILING_3}, action -> {
            Player player = action.getPlayer();
            GameObject object = action.getObject();
            if (player.getX() == action.getX()) {
                Agility.handleObstacle(player, Agility.Obstacles.OGRE_PEN_RAIL_WEST, object);
            } else {
                Agility.handleObstacle(player, Agility.Obstacles.OGRE_PEN_RAIL_EAST, object);
            }
            return true;
        });*/

    }
}
