package com.grinder.game.content.minigame.chamberoxeric.room.mystics;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.COXRoom;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class MysticsCOXRoom extends COXRoom {

    private static final Position[] SPAWNS = {
            new Position(3347, 5260),
            new Position(3346, 5270),
            new Position(3337, 5269),
    };

    private Player p;

    public MysticsCOXRoom(Player p) {
        this.p = p;
        init();
    }

    @Override
    public void init() {
        int height = p.getPosition().getZ();

        for (int i = 0; i < SPAWNS.length; i++) {
            Position pos = SPAWNS[i].clone().transform(0, 0, height + 1);

            NPC mystic = NPCFactory.INSTANCE.create(7604 + i, pos);

            World.getNpcAddQueue().add(mystic);

            p.instance.addAgent(mystic);
        }
    }
}
