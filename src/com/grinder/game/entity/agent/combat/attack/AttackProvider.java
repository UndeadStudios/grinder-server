package com.grinder.game.entity.agent.combat.attack;

import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.sound.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a helper interface for configuring {@link Attack}.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-13
 */
public interface AttackProvider {

    /**
     * Gets the duration (in game ticks) of the attack.
     *
     * @param type the {@link AttackType} used by some agent.
     *
     * @return an integer value representing the delay
     *         until the next attack in game ticks.
     */
    int fetchAttackDuration(AttackType type);

    /**
     * Each sequenced attack is required to have an attack {@link Animation}.
     *
     * @param type the {@link AttackType} to provide an {@link Animation} for.
     *
     * @return {@link Animation} marking the start of an {@link AttackStrategy}
     */
    Animation getAttackAnimation(AttackType type);

    /**
     * Represents a stream of {@link HitTemplate templates} that
     * are used to create {@link Hit hits} during combat.
     *
     * @param type the {@link AttackType} to fetch {@link Hit hits} for.
     *
     * @return a {@link Stream<HitTemplate>} that may be empty.
     */
    Stream<HitTemplate> fetchHits(AttackType type);

    @NotNull
    default Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
        return Stream.empty();
    }

    default Optional<Graphic> fetchAttackGraphic(AttackType type) {
        return Optional.empty();
    }

    default Optional<String> fetchTextAboveHead(AttackType type) {
        return Optional.empty();
    }

    default Optional<Sound> fetchAttackSound(AttackType type) {
        return Optional.empty();
    }
}
