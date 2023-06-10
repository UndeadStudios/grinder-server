package com.grinder.game.content.minigame.chamberoxeric.room.vasanistirio;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.Hit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class VasaNistirioCombat implements AttackStrategy<Agent> {

    @Override
    public void animate(@NotNull Agent actor) {
   }

    @Override
    public void sequence(@NotNull Agent actor, @NotNull Agent target) {
    }

    @Override
    public void postHitAction(@NotNull Agent actor, @NotNull Agent target) {
    }

    @Override
    public void postHitEffect(@NotNull Hit hit) {
    }

    @Override
    public void postIncomingHitEffect(@NotNull Hit hit) {
    }

    @Override
    public int duration(@NotNull Agent actor) {
        return 5;
    }

    @Override
    public int requiredDistance(@NotNull Agent actor) {
        return 8;
    }

    @Override
    public boolean canAttack(@NotNull Agent actor, @NotNull Agent target) {
        return false;
    }

    @NotNull
    @Override
    public Hit[] createHits(@NotNull Agent actor, @NotNull Agent target) {
        return new Hit[0];
    }

    @Nullable
    @Override
    public AttackType type() {
        return AttackType.RANGED;
    }
}
