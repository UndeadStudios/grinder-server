package com.grinder.game.content.minigame.chamberoxeric.room.tekton;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.COXRoom;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class TektonCOXRoom extends COXRoom {

    public static final Position PASSAGE_ENTRY = new Position(3311, 5305, 1);

    private Player p;

    public TektonCOXRoom(Player p) {
        this.p = p;
        init();
    }

    @Override
    public void init() {

        int height = p.getPosition().getZ();

        Position pos = TektonNPC.HAMMERING_POSITION.clone().transform(0, 0, height);

        NPC tekton = new TektonNPC(p, TektonNPC.TEKTON_HAMMERING, pos);

        World.getNpcAddQueue().add(tekton);

        p.instance.addAgent(tekton);
    }
}
