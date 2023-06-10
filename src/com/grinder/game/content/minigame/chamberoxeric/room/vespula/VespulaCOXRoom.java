package com.grinder.game.content.minigame.chamberoxeric.room.vespula;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.COXRoom;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.NPCDeathTask;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class VespulaCOXRoom extends COXRoom {

    private static final int VESPULA = 7530;

    private static final int PORTAL = 7533;

    private static final Position VESPULA_SPAWN = new Position(3279, 5289);

    private static final Position PORTAL_SPAWN = new Position(3282, 5301);

    private static final Item MEDIVAEMIA_BLOSSOM = new Item(20892);

    private static final Animation TAKE = new Animation(832);

    private Player p;

    public VespulaCOXRoom(Player p) {
        this.p = p;
        init();
    }

    public VespulaCOXRoom() {

    }

    @Override
    public void init() {

        int height = p.getPosition().getZ();

        Position vespulaPos = VESPULA_SPAWN.clone().transform(0, 0, height + 2);

        VespulaNPC vespula = new VespulaNPC(VESPULA, vespulaPos);

        World.getNpcAddQueue().add(vespula);

        p.instance.addAgent(vespula);

        Position portalPos = PORTAL_SPAWN.clone().transform(0, 0, height + 2);

        NPC portal = NPCFactory.INSTANCE.create(PORTAL, portalPos);

        World.getNpcAddQueue().add(portal);

        p.instance.addAgent(portal);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 30068:
                if (player.getInventory().isFull()) {
                    player.getPacketSender().sendMessage("You don't have any inventory space.");
                    return true;
                }
                player.getInventory().add(MEDIVAEMIA_BLOSSOM);
                player.performAnimation(TAKE);
                return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int type) {
        switch (npc.getId()) {
            case 7535:
                if (!player.getInventory().contains(MEDIVAEMIA_BLOSSOM)) {
                    player.getPacketSender().sendMessage("You don't have any medivaemia blossom to feed the lux");
                    return true;
                }
                int healAmount = 20;

                if (npc.getHitpoints() + 20 > npc.getMaxHitpoints()) {
                    healAmount = npc.getMaxHitpoints() - npc.getHitpoints();
                }

                if (healAmount > 0) {
                    npc.setHitpoints(npc.getHitpoints() + 20);
                    player.getPacketSender().sendMessage("You heal the lux grub.");
                    player.getInventory().delete(MEDIVAEMIA_BLOSSOM);
                    player.performAnimation(TAKE);
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcDeath(Player player, NPC npc) {
        switch (npc.getId()) {
            case PORTAL:
                if (player.instance == null) {
                    break;
                }
                for (NPC n : player.instance.npcs) {
                    if (n.getId() == VESPULA || n.getId() == VespulaNPC.LUX_GRUB || n.getId() == 7538) {
                        TaskManager.submit(new NPCDeathTask(n, 1, true));
                    }
                }
                return true;
        }
        return false;
    }
}
