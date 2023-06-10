package com.grinder.game.content.minigame.motherlodemine.npcs;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;

/**
 * @author L E G E N D
 * @date 2/14/2021
 * @time 3:58 AM
 * @discord L E G E N D#4380
 */
public final class Miner extends NPC {

    private int timer;

    public Miner(int id, Position position) {
        super(id, position);
        setArea(AreaManager.MOTHERLODE_MINE_AREA);
    }

    public void process() {
        if (++timer % 50 == 0) {
            say("Pay-Dirt");
        }
    }
}
