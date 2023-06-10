package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl.lefthand;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class AutoHealOlmAttack implements OlmAttack {
    @Override
    public void execute(NPC npc, Player target) {

        OlmCOXRoom room = target.getCOX().getParty().olm;

        TaskManager.submit(new Task(1) {

            int tick = 0;
            @Override
            protected void execute() {

                if(tick == 1) {
                    room.leftHandObject.performAnimation(OlmConfiguration.FLASHING_INFINITY_LEFT_HAND);
                }

                if(tick >= 3) {
                    room.leftHandNPC.heal(8);
                }

                if(tick++ == 15) {
                    room.leftHandObject.performAnimation(OlmConfiguration.LEFT_HAND);
                    stop();
                }
                stop();
            }
        });

    }
}
