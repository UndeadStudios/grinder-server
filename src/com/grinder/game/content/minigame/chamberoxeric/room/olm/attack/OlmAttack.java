package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public interface OlmAttack {

    void execute(NPC npc, Player target);
}
