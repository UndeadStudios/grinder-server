package com.grinder.game.model.areas;

import com.grinder.game.World;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.MonsterEvent;
import com.grinder.game.entity.agent.npc.monster.MonsterEvents;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;

import java.util.Optional;

/**
 * @author Jack Barnett (https://www.rune-server.ee/members/raccas/)
 */
public class UntypedInstancedBossArea extends InstancedArea {

    public final Boss boss;

    public UntypedInstancedBossArea(Boss boss, Boundary... boundaries) {
        super(boundaries);
        this.boss = boss;
        World.getNpcAddQueue().add(boss);
        add(boss);
/*        boss.onEvent(monsterEvent -> {
            if(monsterEvent == MonsterEvents.REMOVED){
                logger.info("boss was removed, destroying instance.");
                destroy();
            }
            return null;
        });*/
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
