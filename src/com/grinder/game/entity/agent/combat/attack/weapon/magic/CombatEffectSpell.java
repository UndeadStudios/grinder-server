package com.grinder.game.entity.agent.combat.attack.weapon.magic;

import java.util.Optional;

import com.grinder.game.content.skill.skillable.impl.magic.Spell;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.projectile.Projectile;

/**
 * A {@link Spell} implementation primarily used for spells that have effects
 * when they hit the player.
 *
 * @author lare96
 */
public abstract class CombatEffectSpell extends CombatSpell {

    private final boolean dealingDamage;

    protected CombatEffectSpell(boolean dealingDamage) {
        this.dealingDamage = dealingDamage;
    }

    @Override
    public void finishCast(Agent cast, Agent castOn, boolean accurate,
                           int damage) {
        if (accurate) {
            spellEffect(cast, castOn);
            
            if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getCombat().skullByAttacking(castOn.getAsPlayer());
            }
        }
    }

    @Override
    public Optional<Item[]> equipmentRequired(Player player) {
        return Optional.empty();
    }

    @Override
    public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
        return Optional.empty();
    }

    @Override
    public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
        return Optional.empty();
    }

    /**
     * The effect that will take place once the spell hits the target.
     *
     * @param cast   the entity casting the spell.
     * @param castOn the entity being hit by the spell.
     */
    public abstract void spellEffect(Agent cast, Agent castOn);

    public boolean isDealingDamage() {
        return dealingDamage;
    }
}
