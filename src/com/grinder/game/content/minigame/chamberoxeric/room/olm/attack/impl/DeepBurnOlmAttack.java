package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.ArrayList;
import java.util.Collections;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class DeepBurnOlmAttack implements OlmAttack {
    @Override
    public void execute(NPC npc, Player target) {
        ArrayList<Player> players = npc.getLocalPlayers();

        if(players.size() == 0) {
            return;
        }

        OlmCOXRoom room = target.getCOX().getParty().olm;

        room.olmNPC.attackTimer = 6;
        room.olmNPC.performGreatOlmAttack(room);

        TaskManager.submit(new Task(2) {

            int ticks = 0;
            @Override
            protected void execute() {

                Collections.shuffle(players);

                Player victim = players.get(0);

                victim.say("Burn with me!");

                victim.getCombat().queue(Damage.create(5));

                for(Skill s : Skill.values()) {
                    int lvl = victim.getSkillManager().getCurrentLevel(s);

                    victim.getSkillManager().setCurrentLevel(s, lvl - 2, true);
                }

                if(ticks++ == 3) {
                    stop();
                }
            }
        });
    }
}
