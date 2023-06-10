package com.grinder.game.entity.agent.combat.hit;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.formula.CombatFormula;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.sound.Sound;
import kotlin.ranges.IntRange;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents a template for {@link Hit}s which in turn
 * create {@link Damage} that can be applied to an {@link Agent}.
 *
 * TODO: add support for https://oldschool.runescape.wiki/w/Magical_melee
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
public interface HitTemplate {

    /**
     * Override the original max damage calculation if present.
     *
     * @return an {@link Optional} {@link IntRange} specifying the min-max damage.
     */
    Optional<IntRange> damageRange();

    /**
     * Represents the number of {@link Hit}s generated
     * by this {@link HitTemplate}.
     *
     * @return the number of hits to be applied.
     */
    int amount();

    /**
     * Represents the number of ticks before
     * the first {@link Hit} is applied.
     *
     * @return the number of ticks before the first hit.
     */
    int delay();

    int defenceStat();

    int attackStat();

    /**
     * Determines whether the {@link Hit}s generated
     * by this {@link HitTemplate} should take {@link CombatFormula#calculateMaxAttackRoll}
     * and {@link CombatFormula#calculateMaxDefenceRoll}
     * into account during {@link Damage} calculations.
     *
     * @return {@code true} if this hit should do attack and defence rolls,
     *          {@code false} if this hit should always apply damage.
     */
    boolean ignoreAttackStats();

    boolean ignoreStrengthStats();

    boolean ignorePrayer();

    boolean ignoreAmmunitionEffects();

    boolean ignorePoisonEffects();

    /**
     * The {@link AttackType} of the {@link Hit}s generated
     * by this {@link HitTemplate}.
     *
     * @see Damage#getDamageMask()
     *
     * @return the attack type used to generate hits.
     */
    AttackType type();

    Optional<Graphic> successGraphic();

    Optional<Graphic> failedGraphic();

    Optional<Sound> successSound();

    Optional<Sound> failedSound();

    Consumer<Agent> onSuccess();

    Consumer<Agent> onFailed();

    /**
     * A static helper method to create a new {@link HitTemplateBuilder}.
     *
     * @param type the {@link AttackType} used by the source.
     *
     * @return a new builder of the specified type.
     */
    static HitTemplateBuilder builder(AttackType type) {
        return new HitTemplateBuilder(type);
    }
}
