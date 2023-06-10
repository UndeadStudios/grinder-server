package com.grinder.game.model.areas.impl;

import com.grinder.game.content.minigame.motherlodemine.MotherlodeMine;
import com.grinder.game.content.minigame.motherlodemine.npcs.Miner;
import com.grinder.game.content.minigame.motherlodemine.npcs.ProspectorPercy;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaListener;

import java.util.Optional;

/**
 * @author L E G E N D
 * @date 2/14/2021
 * @time 1:29 AM
 * @discord L E G E N D#4380
 */
public class MotherlodeMineArea extends Area implements AreaListener {

    public MotherlodeMineArea() {
        super(new Boundary(3716, 3779, 5627, 5697));
    }

    @Override
    public void process(Agent agent) {
        if (agent instanceof ProspectorPercy) {
            ((ProspectorPercy) agent).process();
        }
        if (agent instanceof Miner) {
            ((Miner) agent).process();
        }
    }


    @Override
    public void defeated(Player player, Agent agent) {
        player.getMotherlodeMine().setUpperFloor(false);
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {

    }

    @Override
    public void enter(Agent agent) {
        if (agent.isPlayer()) {
            super.enter(agent);
            MotherlodeMine.onEnter(agent.getAsPlayer());
        }
    }

    @Override
    public void leave(Agent agent) {
        if (agent.isPlayer()) {
            super.leave(agent);
            MotherlodeMine.onLeave(agent.getAsPlayer());
        }
    }

    @Override
    public boolean isMulti(Agent agent) {
        return false;
    }

    @Override
    public boolean canTeleport(Player player) {
        player.getMotherlodeMine().setUpperFloor(false);
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return false;
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        return false;
    }

    @Override
    public boolean isCannonProhibited() {
        return true;
    }
}
