package com.grinder.game.model.areas.impl;

import com.grinder.game.content.minigame.blastfurnace.BlastFurnace;

import com.grinder.game.content.minigame.blastfurnace.npcs.BlastFurnaceNpc;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaListener;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 9:24 AM
 * @discord L E G E N D#4380
 */
public class BlastFurnaceArea extends Area implements AreaListener {


    public BlastFurnaceArea() {
        super(new Boundary(1933, 1958, 4954, 4976));
    }

    public static Set<NPC> npcs = new HashSet<>();
    public static Set<Player> players = new HashSet<>();

    public Optional<NPC> searchAnyNpc(Predicate<? super NPC> filter) {
        for (NPC e : npcs) {
            if (e == null)
                continue;
            if (filter.test(e))
                return Optional.of(e);
        }
        return Optional.empty();
    }

    @Override
    public void process(Agent agent) {
        if(agent instanceof BlastFurnaceNpc){
            ((BlastFurnaceNpc)agent).process();
        }
        if(agent.isPlayer()){
            BlastFurnace.check(agent.getAsPlayer());
        }
    }

    @Override
    public void enter(Agent agent) {
        if(agent instanceof Player){

            super.enter(agent);
            BlastFurnace.onEnter(agent.getAsPlayer());
            players.add(agent.getAsPlayer());
        }
        if (agent.isNpc()) {
            npcs.add(agent.getAsNpc());
        }
    }

    @Override
    public void leave(Agent agent) {
        if(agent instanceof  Player){

            super.leave(agent);
            BlastFurnace.onLeave(agent.getAsPlayer());
            players.remove(agent.getAsPlayer());
        }
        if (agent.isNpc()) {
            npcs.remove(agent.getAsNpc());
        }
    }

    @Override
    public void defeated(Player player, Agent agent) {

    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {

    }

    @Override
    public boolean isMulti(Agent agent) {
        return false;
    }

    @Override
    public boolean canTeleport(Player player) {
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
}
