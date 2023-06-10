package com.grinder.game.entity.agent.npc.monster.pestcontrol;

import com.grinder.game.collision.CollisionPolicy;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.entity.agent.npc.monster.MonsterEvents;
import com.grinder.game.entity.agent.npc.monster.MonsterExtKt;
import com.grinder.game.entity.agent.npc.monster.impl.AlKharidWarrior;
import com.grinder.game.entity.updating.UpdateBlock;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class Spinner extends Monster {
    private NPC portalToHeal;
    public Spinner(int id, @NotNull Position position) {
        super(id, position);
        TaskManager.submit(Misc.random(5, 25), () -> {
            healPortal();
        });
    }

    public void setPortal(NPC portalToHeal) {
        this.portalToHeal = portalToHeal;
    }

    private void healPortal() {
        if (getHitpoints() >= 0) {
            if (!getCombat().isUnderAttack() && !getCombat().isInCombat()) { // Do not heal when under attack or in combat
                if (portalToHeal.getHitpoints() >= 0 && !portalToHeal.isDying() && portalToHeal.isAlive() &&
                        (portalToHeal.getId() == 1739 || portalToHeal.getId() == 1740 || portalToHeal.getId() == 1741 || portalToHeal.getId() == 1742)) {
                    int damage = Misc.random(10, 25);
                    performAnimation(new Animation(3907));
                    portalToHeal.heal(damage);
                    portalToHeal.getBlockSet().add(UpdateBlock.Companion.createUpdateFirstHitBlock(portalToHeal, new Damage(damage, DamageMask.YELLOW)));
                    //getAsPlayer().getPacketSender().sendAreaSound(getPosition(), 3812, 7);
                }
            }
            TaskManager.submit(Misc.random(5, 25), () -> {
                healPortal();
            });
        }
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return 1;
    }
}
