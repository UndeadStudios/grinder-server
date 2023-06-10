package com.grinder.game.model.areas;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;

import java.util.List;
import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-18
 */
public interface AreaListener {

    void process(Agent agent);

    void enter(Agent agent);

    void leave(Agent agent);

    void defeated(Player player, Agent agent);

    void onPlayerRightClick(Player player, Player rightClicked, int option);

    boolean isMulti(Agent agent);

    boolean isSafeForHardcore();

    boolean canTeleport(Player player);

    boolean canAttack(Agent attacker, Agent target);

    boolean canTrade(Player player, Player target);

    boolean canDrink(Player player, int itemId);

    boolean canEat(Player player, int itemId);

    boolean dropItemsOnDeath(Player player, Optional<Player> killer);

    boolean handleObjectClick(Player player, GameObject obj, int actionType);

    boolean handleDeath(Player player, Optional<Player> killer);

    boolean handleDeath(NPC npc);

    List<Boundary> boundaries();
}
