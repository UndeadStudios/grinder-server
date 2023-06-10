package com.grinder.game.content.minigame.warriorsguild.npcs;


import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;

import static com.grinder.util.NpcID.LORELAI;
import static com.grinder.util.NpcID.RORY;

/**
 * @author L E G E N D
 */
public final class Rory extends WarriorGuildNpc {

    public Rory(int id, Position position) {
        super(id, position);
        setupOverheadChat(33, "I'm Hungry", "Rawr", "Mooom");
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{RORY}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                makeNpcDialogue(null, "Ahh I see you've met Rory. As a young Cyclops he's", "not ready for combat yet so he keeps me company.").
                        setNpcChatHead(LORELAI).
                        start(player);
                player.setPositionToFace(action.getNpc().getPosition().transform(1, 0, 0));
            }
            return true;
        });
    }
}