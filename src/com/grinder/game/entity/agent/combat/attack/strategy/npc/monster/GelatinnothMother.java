package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dexter Morgan
 * <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class GelatinnothMother implements AttackStrategy<NPC> {

    private MotherType type = MotherType.WHITE;

    private int hits;

    private enum MotherType {

        WHITE(4884, 0),

        BLUE(4884, 1),

        ORANGE(4888, 2),

        BROWN(4889, 3),

        RED(4887, 4),

        GREEN(4885, 5),
        ;

        private int id;

        private int type;

        MotherType(int id, int type) {
            this.id = id;
            this.type = type;
        }

        private static final MotherType[] VALUES = values();
    }


    @Override
    public int duration(@NotNull NPC actor) {
        return 5;
    }

    @Override
    public int requiredDistance(@NotNull Agent actor) {
        return 6;
    }

    @NotNull
    @Override
    public Hit[] createHits(@NotNull NPC actor, @NotNull Agent target) {
        Hit hit = new Hit(actor, target, this, true, 2);
        hits++;
        if (hits == 2) {
            int next = type.ordinal() + 1;
            if (next >= MotherType.VALUES.length) {
                next = 0;
            }
            hits = 0;
            MotherType nextType = MotherType.VALUES[next];
            type = nextType;
            actor.setNpcTransformationId(type.id);
        }
        return new Hit[]{hit};
    }

    @Nullable
    @Override
    public AttackType type() {
        return AttackType.MAGIC;
    }

    @Override
    public void animate(@NotNull NPC actor) {
    }

    @Override
    public void sequence(@NotNull NPC actor, @NotNull Agent target) {
        if (target.isPlayer()) {
            Player p = target.getAsPlayer();

            switch (type) {
                case ORANGE:
                    if (!p.getCombat().isMeleeAttack()) {
                        p.getCombat().reset(false);
                    }
                    break;
                case GREEN:
                    if (!p.getCombat().isRangeAttack()) {
                        p.getCombat().reset(false);
                    }
                    break;
                case WHITE:
                    isUsingSpell(p, "air");
                    break;
                case BLUE:
                    isUsingSpell(p, "water");
                    break;
                case BROWN:
                    isUsingSpell(p, "earth");
                    break;
                case RED:
                    isUsingSpell(p, "fire");
                    break;
            }
        }
    }

    private void isUsingSpell(Player p, String spellType) {
        CombatSpell spell = null;

        if (p.getCombat().getCastSpell() != null) {
            spell = p.getCombat().getCastSpell();
        }

        if (p.getCombat().getAutocastSpell() != null) {
            spell = p.getCombat().getAutocastSpell();
        }

        if(spell == null) {
            return;
        }

        CombatSpellType type = CombatSpellType.FOR_ID.get(spell.spellId());

        if(!type.name().toLowerCase().contains(spellType)) {
            p.getCombat().reset(false);
        }
    }

    @Override
    public void postHitAction(@NotNull NPC actor, @NotNull Agent target) {
    }

    @Override
    public boolean canAttack(@NotNull NPC actor, @NotNull Agent target) {
        return true;
    }

    @Override
    public void postHitEffect(@NotNull Hit hit) {
    }

    @Override
    public void postIncomingHitEffect(@NotNull Hit hit) {
    }
}
