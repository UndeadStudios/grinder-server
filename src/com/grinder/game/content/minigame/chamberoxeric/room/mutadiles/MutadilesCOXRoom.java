package com.grinder.game.content.minigame.chamberoxeric.room.mutadiles;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.COXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.mutadiles.npc.MutadileNPC;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Position;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class MutadilesCOXRoom extends COXRoom {

    private static final int WATER_MUTADILE = 7561;

    private static final int SMALL_MUTADILE = 7562;

    private static final int LARGE_MUTADILE = 7563;

    private static final Position WATER_MUTADILE_SPAWN = new Position(3317, 5334);

    private static final Position SMALL_MUTADILE_SPAWN = new Position(3307, 5325);

    private static final Position LARGE_MUTADILE_SPAWN = new Position(3307, 5328);

    public MeatTree meatTree;

    private NPC waterMutadile;

    private Player p;

    public MutadilesCOXRoom(Player p) {
        this.p = p;
        this.meatTree = new MeatTree();
        init();
    }

    public MutadilesCOXRoom() {

    }

    @Override
    public void init() {

        int height = p.getPosition().getZ() + 1;

        Position meatTreePos = MeatTree.MEAT_TREE_SPAWN.clone().transform(0, 0, height);

        StaticGameObject tree = StaticGameObjectFactory.produce(MeatTree.ALIVE_MEAT_TREE, meatTreePos, 10, 0);

        ObjectManager.add(tree, true);

        p.instance.addObject(tree);

        Position waterMutadilePos = WATER_MUTADILE_SPAWN.clone().transform(0, 0, height);

        NPC waterMutadile = new MutadileNPC(WATER_MUTADILE, waterMutadilePos, meatTree);

        World.getNpcAddQueue().add(waterMutadile);

        this.waterMutadile = waterMutadile;

        Position mutadileSpawn = SMALL_MUTADILE_SPAWN.clone().transform(0, 0, height);

        NPC smallMutadile = NPCFactory.INSTANCE.create(SMALL_MUTADILE, mutadileSpawn);

        World.getNpcAddQueue().add(smallMutadile);

        p.instance.addAgent(waterMutadile);
        p.instance.addAgent(smallMutadile);
    }

    private void spawnLargeMutadile() {
        World.remove(waterMutadile);

        Position largeMutadilePos = LARGE_MUTADILE_SPAWN.clone().transform(0, 0, p.getPosition().getZ());

        NPC largeMutadile = new MutadileNPC(LARGE_MUTADILE, largeMutadilePos, meatTree);

        World.getNpcAddQueue().add(largeMutadile);

        p.instance.addAgent(largeMutadile);
    }

    @Override
    public boolean handleNpcDeath(Player player, NPC npc) {
        switch (npc.getId()) {
            case SMALL_MUTADILE:
                player.getCOX().getParty().mutadiles.spawnLargeMutadile();
                return true;
        }
        return false;
    }
}
