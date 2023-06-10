package com.grinder.game.content.minigame.chamberoxeric.room.icedemon;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.COXRoom;
import com.grinder.game.content.minigame.chamberoxeric.skills.COXWoodcutting;
import com.grinder.game.content.skill.skillable.impl.woodcutting.AxeType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class IceDemonCOXRoom extends COXRoom {

    private static final int UNLIT_BRAZIER = 29747;

    private static final int LIT_BRAZIER = 29748;

    private static final int FROZEN_DEMON = 7584;

    private static final int UNFROZEN_DEMON = 7585;

    private static final int ICE_FIEND = 7586;

    private static final Item KINDLING = new Item(20799);

    private static final Animation LIGHT = new Animation(896);

    private static final Position FROZEN_DEMON_SPAWN = new Position(3271, 5365);

    private static final Position[] ICE_FIEND_SPAWNS = {
            new Position(3275, 5363),
            new Position(3273, 5362),
            new Position(3275, 5368),
            new Position(3273, 5369),
    };

    private ArrayList<NPC> iceFiends = new ArrayList<>();

    private int iceStorm;

    private Player p;

    private NPC frozenDemon;

    private NPC iceDemon;

    public IceDemonCOXRoom(Player p) {
        this.p = p;
        iceStorm = 500;
        init();
    }

    public IceDemonCOXRoom() {

    }

    private void chopSapling(Player p, GameObject object) {
        if (p.getCOX().skillTask != null) {
            return;
        }

        if (p.getInventory().countFreeSlots() == 0) {
            p.getPacketSender().sendMessage("You don't have any inventory space to cut the tree.");
            return;
        }

        AxeType axe = COXWoodcutting.getAxe(p);

        if (axe == null) {
            p.getPacketSender().sendMessage("You need a woodcutting axe to chop down the tree.");
            return;
        }

        p.performAnimation(axe.getAnimation());

        int amount = 1 + (p.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) / 12);

        Task task = new Task(5) {
            @Override
            protected void execute() {
                if (p.getPosition().getDistance(object.getPosition()) > 2) {
                    stop();
                    return;
                }

                if (p.getInventory().countFreeSlots() == 0) {
                    stop();
                    return;
                }

                p.performAnimation(axe.getAnimation());

                p.getInventory().add(KINDLING.getId(), amount);
                p.getSkillManager().addExperience(Skill.WOODCUTTING, 12 * amount);
            }

            @Override
            public void onStop() {
                p.getCOX().skillTask = null;
            }
        };

        p.getCOX().skillTask = task;

        TaskManager.submit(task);
    }

    private void lightBrazier(Player p, GameObject object) {
        if (!p.getInventory().contains(ItemID.TINDERBOX)) {
            p.getPacketSender().sendMessage("You need a tinderbox to light the brazier.");
            return;
        }

        if (!p.getInventory().contains(KINDLING)) {
            p.getPacketSender().sendMessage("You need kindling to light the brazier.");
            return;
        }

        ObjectManager.add(StaticGameObjectFactory.produce(LIT_BRAZIER, object.getPosition(), 10, 0), true);

        TaskManager.submit(new Task(100) {
            @Override
            protected void execute() {
                ObjectManager.add(StaticGameObjectFactory.produce(UNLIT_BRAZIER, object.getPosition(), 10, 0), true);
                stop();
            }
        });

        dispellIceStorm(p);
    }

    private void dispellIceStorm(Player p) {
        if (p.getCOX().getParty().iceDemon.iceStorm < 0) {
            p.getPacketSender().sendMessage("The ice storm has been dispelled!");
            return;
        }

        int amount = p.getInventory().getAmount(KINDLING.getId());

        if (amount == 0) {
            p.getPacketSender().sendMessage("You have no kindling to add to the fire.");
            return;
        }

        p.getCOX().points += amount * 2;

        p.performAnimation(LIGHT);

        p.getInventory().delete(KINDLING.getId(), amount);

        p.getCOX().getParty().iceDemon.iceStorm -= amount * 3;

        if (p.getCOX().getParty().iceDemon.iceStorm < 1) {
            for (NPC n : p.getCOX().getParty().iceDemon.iceFiends) {
                World.getNpcRemoveQueue().add(n);
                p.instance.removeAgent(n);
            }

            if (p.getCOX().getParty().iceDemon.frozenDemon != null) {

                NPC frozen = p.getCOX().getParty().iceDemon.frozenDemon;

                World.getNpcRemoveQueue().add(frozen);
                p.instance.removeAgent(frozen);

                NPC demon = NPCFactory.INSTANCE.create(UNFROZEN_DEMON, p.getCOX().getParty().iceDemon.frozenDemon.getPosition().clone());

                World.getNpcAddQueue().add(demon);

                p.instance.addAgent(demon);

                p.getCOX().getParty().iceDemon.iceDemon = demon;
            }
        }
    }

    @Override
    public void init() {

        int height = p.getPosition().getZ();

        Position position = FROZEN_DEMON_SPAWN.clone().transform(0, 0, height);

        NPC demon = NPCFactory.INSTANCE.create(FROZEN_DEMON, position);

        frozenDemon = demon;

        World.getNpcAddQueue().add(demon);

        p.instance.addAgent(frozenDemon);

        int iceFiends = 1;

        int teamSize = p.getCurrentClanChat().players().size();

        if (teamSize > iceFiends) {
            iceFiends = teamSize;
        }

        if (iceFiends > 4) {
            iceFiends = 4;
        }

        for (int i = 0; i < iceFiends; i++) {

            Position pos = ICE_FIEND_SPAWNS[i].clone().transform(0, 0, height);

            NPC iceFiend = NPCFactory.INSTANCE.create(ICE_FIEND, pos);

            this.iceFiends.add(iceFiend);

            p.instance.addAgent(iceFiend);

            World.getNpcAddQueue().add(iceFiend);
        }
    }
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 29763:
                chopSapling(player, object);
                return true;
            case UNLIT_BRAZIER:
                lightBrazier(player, object);
                return true;
            case LIT_BRAZIER:
                dispellIceStorm(player);
                return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int type) {
        switch (npc.getId()) {
            case 7584:
                int health = player.getCOX().getParty().iceDemon.iceStorm;

                player.getPacketSender().sendMessage("The Ice Demon storm is at: " + health + " health.");
                return true;
        }
        return false;
    }
}
