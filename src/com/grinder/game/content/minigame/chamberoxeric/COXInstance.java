package com.grinder.game.content.minigame.chamberoxeric;

import com.grinder.game.content.minigame.chamberoxeric.room.COXMap;
import com.grinder.game.content.minigame.chamberoxeric.room.COXPassage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.instance.PlayerInstance;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXInstance extends PlayerInstance {

    @Override
    public void handlePlayerDeath(Player p) {
        COXPassage.move(p, COXMap.START.position);
    }

    @Override
    public boolean isSafe(Player p) {
        return true;
    }

    @Override
    public boolean canTeleport(Player p) {
        return false;
    }

    @Override
    public void onLogout(Player p) {
        COXManager.withdrawPrivateStorage(p);

        COXManager.clearRaidItems(p);

        p.moveTo(COXManager.EXIT);
    }

    @Override
    public boolean inMulti(Player p) {
        return true;
    }

    @Override
    public boolean doesNPCRespawn(NPC npc) {
        return false;
    }
}
