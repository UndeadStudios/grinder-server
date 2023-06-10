package com.grinder.game.model.areas.impl;

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.Area;
import com.grinder.util.Misc;

import java.util.Optional;

public class BoxingRingArea extends Area {

    public BoxingRingArea() {
        super(new Boundary(2590, 2594, 4772, 4777));
    }

    @Override
    public void enter(Agent agent) {
        if (agent != null && agent.isPlayer()) {
            super.enter(agent);
        }
        if (agent != null && agent.isPlayer()) {
            Player player = agent.getAsPlayer();
            player.getPacketSender().sendInteractionOption("Attack", 2, true);
        }
    }

    @Override
    public void leave(Agent agent) {
        if (agent != null && agent.isPlayer()) {
            Player player = agent.getAsPlayer();
            super.leave(agent);
            player.getPacketSender().sendInteractionOption("null", 2, true);
            player.resetAttributes();
            PrayerHandler.resetAll(player);
        }
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
        return false;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        player.moveTo(new Position(2595, 4774 + Misc.getRandomInclusive(1), 0));
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
        return true;
    }
}
