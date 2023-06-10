package com.grinder.game.model.areas.impl;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.content.minigame.warriorsguild.WarriorsGuild;
import com.grinder.game.content.minigame.warriorsguild.rooms.Jimmy;
import com.grinder.game.content.minigame.warriorsguild.rooms.catapult.Catapult;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaListener;

import java.util.Optional;

public class WarriorsGuildArea extends Area implements AreaListener {

    public WarriorsGuildArea() {
        super(
                new Boundary(2830, 2876, 3533, 3556), // Upper Floor
                new Boundary(2904, 2941, 9956, 9974)  // Basement
        );
    }

    @Override
    public void process(Agent agent) {
        if (agent.isPlayer()) {
            WarriorsGuild.process(agent.getAsPlayer());
        }
        if (agent instanceof WarriorGuildNpc) {
            ((WarriorGuildNpc) agent).process();
        }
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
        if (player.getWarriorsGuild().isInside()) {
            player.sendMessage("You cannot teleport out of here, you must leave.");
            return false;
        }
        if (Catapult.isShieldEquipped(player) || Jimmy.isHoldingKeg(player)) {
            player.sendMessage("You can't teleport right now! Please finish what you're doing first.");
            return false;
        }

        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return !attacker.isPlayer() || !target.isPlayer();
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
        return false;
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

    @Override
    public boolean isSafeForHardcore() {
        return false;
    }
}
