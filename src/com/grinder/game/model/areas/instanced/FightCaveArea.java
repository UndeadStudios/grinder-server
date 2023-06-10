package com.grinder.game.model.areas.instanced;

import com.grinder.game.content.minigame.fightcave.FightCave;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.InstancedArea;

import java.util.Optional;

public final class FightCaveArea extends InstancedArea {

    public FightCaveArea(FightCave handler) {
        super(new Boundary(2368, 2431, 5056, 5119));
    }

    public FightCaveArea() {
        super();
    }

    @Override
    public void process(Agent agent) {
    }

    @Override
    public void destroy() {
        super.destroy();
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
    public boolean canTeleport(Player player) {
      FightCave.setWave(player,1);
        player.getFightCave().updateLastSession();
        player.getFightCave().clear();
        FightCave.reset(player);
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return !attacker.isPlayer() || !target.isPlayer();
    }

    @Override
    public boolean canTrade(Player player, Player target) {
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
        FightCave.onPlayerDeath(player);
        return true;
    }

    @Override
    public void defeated(Player player, Agent agent) {
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
    }


    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        if (players.size() > 0)
            FightCave.onNpcDeath(players.get(0), npc);
        return false;
    }

    @Override
    public boolean isSafeForHardcore() {
        return true;
    }

    @Override
    public boolean isCannonProhibited() {
        return true;
    }
}
