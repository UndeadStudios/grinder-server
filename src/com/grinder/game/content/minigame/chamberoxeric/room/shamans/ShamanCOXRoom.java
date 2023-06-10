package com.grinder.game.content.minigame.chamberoxeric.room.shamans;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.COXRoom;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class ShamanCOXRoom extends COXRoom {

    private static final int SHAMAN = 7573;

    private static final Position[] SPAWNS = {
            new Position(3317, 5259),
            new Position(3309, 5259),
            new Position(3306, 5268),
            new Position(3318, 5269),
            new Position(3312, 5265),
    };

    private Player p;

    public ShamanCOXRoom(Player p) {
        this.p = p;
        init();
    }

    @Override
    public void init() {
        int height = p.getPosition().getZ();

        int shamans = 2;

        int size = p.getCurrentClanChat().players().size();

        shamans += size;

        if(shamans > 4) {
            shamans = 4;
        }

        for(int i = 0; i < shamans; i++) {
            Position pos = SPAWNS[i].clone().transform(0,0,height);

            NPC shaman = NPCFactory.INSTANCE.create(SHAMAN, pos);

            World.getNpcAddQueue().add(shaman);

            p.instance.addAgent(shaman);
        }

    }
}
