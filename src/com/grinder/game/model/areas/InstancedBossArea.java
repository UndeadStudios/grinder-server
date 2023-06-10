package com.grinder.game.model.areas;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;

import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 19/10/2019
 */
public class InstancedBossArea extends InstancedArea {

    public InstancedBossArea(Boundary... boundaries) {
        super(boundaries);
    }

    @Override
    public void process(Agent agent) { }
    @Override
    public void defeated(Player player, Agent agent) { }
    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) { }
    @Override
    public boolean isMulti(Agent agent) {
        return true;
    }
    @Override
    public boolean canTeleport(Player player) {
        return true;
    }
    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return !(attacker.isPlayer() && target.isPlayer());
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
