package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.npc.NPC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class FareedCombat implements AttackStrategy<NPC> {
    @Override
    public int duration(@NotNull NPC actor) {
        return 5;
    }

    @Override
    public int requiredDistance(@NotNull Agent actor) {
        return 3;
    }

    @NotNull
    @Override
    public Hit[] createHits(@NotNull NPC actor, @NotNull Agent target) {
        Hit hit = new Hit(actor, target, this, true, 1);
        return new Hit[]{hit};
    }

    @Nullable
    @Override
    public AttackType type() {
        return AttackType.MELEE;
    }

    @Override
    public void animate(@NotNull NPC actor) {
    }

    @Override
    public void sequence(@NotNull NPC actor, @NotNull Agent target) {
    }

    @Override
    public void postHitAction(@NotNull NPC actor, @NotNull Agent target) {
    }

    @Override
    public void postHitEffect(@NotNull Hit hit) {
    }

    @Override
    public void postIncomingHitEffect(@NotNull Hit hit) {
    }

    @Override
    public boolean canAttack(@NotNull NPC actor, @NotNull Agent target) {
        return true;
    }
}
