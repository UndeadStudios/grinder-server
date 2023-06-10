package com.grinder.game.model.areas.impl;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.Area;
import com.grinder.util.Misc;

import java.util.Optional;

public class GodZillaArea extends Area {

    public GodZillaArea() {
        super(
                new Boundary(3393, 3445, 4672, 4795), // Keelow tournament zone
                new Boundary(3446, 3455, 4671, 4718), // Keelow tournament zone1
                new Boundary(3452, 3455, 4719, 4742), // Keelow tournament zone2
                new Boundary(3446, 3455, 4753, 4799)); // Keelow tournament zone3
    }

    @Override
    public void enter(Agent agent) {
        super.enter(agent);
    }

    @Override
    public void leave(Agent agent) {
        super.leave(agent);
    }

    @Override
    public void process(Agent agent) {
    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Agent agent, Agent target) {
        return true;
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        player.getPacketSender().sendMessage("You can't trade over here.");
        return false;
    }

    @Override
    public boolean isMulti(Agent agent) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        player.moveTo(new Position(3446 + Misc.getRandomInclusive(5), 4719 + Misc.getRandomInclusive(13), 0));
        return true;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
    }

    @Override
    public void defeated(Player player, Agent agent) {
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSafeForHardcore() {
        return false;
    }
}
