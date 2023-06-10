package com.grinder.game.content.minigame.motherlodemine.npcs;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;

import static com.grinder.util.NpcID.MERCY;

/**
 * @author L E G E N D
 * @date 2/14/2021
 * @time 8:45 AM
 * @discord L E G E N D#4380
 */
public class Mercy extends NPC {

    public Mercy(int id, Position position) {
        super(id, position);
        setArea(AreaManager.MOTHERLODE_MINE_AREA);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{MERCY}, action -> {
            var player = action.getPlayer();

            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(MERCY)
                    .setText("Why, hello there, young'un.",
                            "Did my boy Percy say you could come up here?")
                    .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("Yes, I had to pay him to get access to this area.")
                            .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setText("Ah, that's my Percy alright. I raised him well."))
                    ).start(player);
            return true;
        });
    }
}
