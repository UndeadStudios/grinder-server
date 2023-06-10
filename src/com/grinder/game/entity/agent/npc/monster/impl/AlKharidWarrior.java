package com.grinder.game.entity.agent.npc.monster.impl;

import com.grinder.game.collision.CollisionPolicy;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.event.CombatEvent;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.task.TaskManager;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

/**
 * @author L E G E N D
 */
public class AlKharidWarrior extends Monster {


    public AlKharidWarrior(int id, @NotNull Position position) {
        super(id, position);
        getCombat().subscribe(this::onCombatEvent);
    }

    private boolean attacked;

    private boolean onCombatEvent(CombatEvent event) {
        if (!(event instanceof IncomingHitApplied)) {
            return false;
        }
        attack(((IncomingHitApplied) event).getHit().getAttacker().getAsPlayer());
        return true;
    }


   public void attack(Player player) {
        if (attacked)
            return;
        var warriors = AgentUtil.getNPCsInProximity(player, 20, CollisionPolicy.NONE).filter(npc -> npc instanceof AlKharidWarrior).collect(Collectors.toList());

        for (var npc : warriors) {
            var warrior = (AlKharidWarrior) npc;
            if (warrior.getPosition().getDistance(player.getPosition()) <= 50) {
                if (warrior.getCombat().canBeReachedInLongRange(player,25,true)) {
                    warrior.say("Brother, I will help thee with this infidel!");
                    warrior.getCombat().initiateCombat(player, true);
                    warrior.attacked = true;
                    loseAggro(warrior);
                }

            }
        }
    }

    public void loseAggro(AlKharidWarrior warrior) {
        TaskManager.submit(20, () -> {
            if (warrior.getCombat().getTarget() == null) {
                warrior.getCombat().resetTarget();
                warrior.attacked = false;
            }
        });
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return 1;
    }
}


