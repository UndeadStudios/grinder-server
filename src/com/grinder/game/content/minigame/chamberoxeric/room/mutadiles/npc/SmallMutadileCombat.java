package com.grinder.game.content.minigame.chamberoxeric.room.mutadiles.npc;

import com.grinder.game.content.minigame.warriorsguild.drops.Misc;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class SmallMutadileCombat implements AttackStrategy<Agent> {
    public static final Animation MELEE_ATTACK = new Animation(7420);

    private static final Animation RANGE_ATTACK = new Animation(7421);
    private enum SmallMutadileAttack {

        MELEE,

        RANGE,

    }

    private SmallMutadileAttack attack = SmallMutadileAttack.MELEE;

    @Override
    public int duration(@NotNull Agent actor) {
        return actor.getBaseAttackSpeed();
    }

    @Override
    public int requiredDistance(@NotNull Agent actor) {
        return 10;
    }

    @NotNull
    @Override
    public Hit[] createHits(@NotNull Agent actor, @NotNull Agent target) {
        return new Hit[]{};
    }

    @Nullable
    @Override
    public AttackType type() {
        return AttackType.MELEE;
    }

    @Override
    public void animate(@NotNull Agent actor) {
    }

    @Override
    public void postHitAction(@NotNull Agent actor, @NotNull Agent target) {
        if (!target.isPlayer()) {
            return;
        }

        Player p = target.getAsPlayer();

        if (actor.getPosition().isWithinDistance(target.getPosition(), 1)) {
            if (Misc.random(2) == 1) {
                attack = SmallMutadileAttack.MELEE;
            } else {
                attack = SmallMutadileAttack.RANGE;
            }
        } else {
            attack = SmallMutadileAttack.RANGE;
        }

        switch (attack) {
            case MELEE:
                int damage = 35;
                if (PrayerHandler.hasProtectionPrayer(p, AttackType.MELEE)) {
                    damage = 0;
                }
                actor.performAnimation(MELEE_ATTACK);
                target.getCombat().queue(Damage.create(0, damage));
                break;
            case RANGE:
                actor.performAnimation(RANGE_ATTACK);

                new Projectile(actor.getPosition(), target.getPosition(), 0, 1291, 30, 40, 21, 21, 0, 3, 0).sendProjectile();
                TaskManager.submit(new Task(3) {
                    @Override
                    protected void execute() {
                        int damage = 35;
                        if (PrayerHandler.hasProtectionPrayer(p, AttackType.RANGED)) {
                            damage = 0;
                        }
                        p.getCombat().queue(Damage.create(0, damage));
                        stop();
                    }
                });
                break;
        }

    }

    @Override
    public void sequence(@NotNull Agent actor, @NotNull Agent target) {
    }

    @Override
    public void postHitEffect(@NotNull Hit hit) {
    }

    @Override
    public void postIncomingHitEffect(@NotNull Hit hit) {
    }

    @Override
    public boolean canAttack(@NotNull Agent actor, @NotNull Agent target) {
        if (actor.isNpc()) {
            NPC npc = actor.getAsNpc();

            if (npc instanceof MutadileNPC) {
                MutadileNPC n = (MutadileNPC) npc;

                if (n.state == MutadileState.HEALING) {
                    return false;
                }
            }
        }
        return true;
    }
}
