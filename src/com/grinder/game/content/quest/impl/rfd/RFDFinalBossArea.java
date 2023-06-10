package com.grinder.game.content.quest.impl.rfd;

import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.InstancedArea;

import java.util.Optional;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class RFDFinalBossArea extends InstancedArea {

    public RFDFinalBossArea() {
        super(new Boundary(1889, 1910, 5345, 5366));
    }

    @Override
    public void process(Agent agent) {
    }

    @Override
    public void defeated(Player player, Agent agent) {
        if(agent.isNpc()) {
            NPC n = agent.getAsNpc();
            if(!RFDBossWave.isRFDBoss(n.getId())) {
                return;
            }
            player.getPacketSender().sendMessage("Please wait for the next round.");
            QuestManager.increaseStage(player, "Recipe for Disaster");
            if(QuestManager.getStage(player, "Recipe for Disaster") == 12) {
                QuestManager.increaseStage(player, "Recipe for Disaster");
            } else {
                RecipeForDisaster.spawnMonster(player);
            }
        }
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
        return false;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return true;
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return false;
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
}
