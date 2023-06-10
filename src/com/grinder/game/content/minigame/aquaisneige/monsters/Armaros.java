package com.grinder.game.content.minigame.aquaisneige.monsters;

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeAttack;
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.util.Misc;
import com.grinder.util.Priority;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.*;
import static com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.*;


public class Armaros extends AquaisNeigeNpc implements AttackProvider {

    public Armaros(int id, Position position) {

        super(id, position);
        setRace(MonsterRace.HYDRA);
    }

    @NotNull
    @Override
    protected AttackTypeProvider attackTypes() {
        return builder()
                .add(ONE_FOURTH, RANGED)
                .add(TWO_THIRD, MELEE)
                .build();
    }

    @Override
    public int fetchAttackDuration(AttackType type) {
        return 6;
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        if (type == MELEE)
            return 1;
        else
            return 8;
    }

    @Override
    protected boolean checkAttackRangeForTypeExclusion(@NotNull AttackType type) {
        return true;
    }

    @NotNull
    @Override
    public OutOfRangePolicy attackRangePolicy(AttackType type) {
        if (type == RANGED)
            return OutOfRangePolicy.EXCLUDE_TYPE;
        else
            return OutOfRangePolicy.TRACE_TO_TARGET;
    }

    @Override
    public BossAttack generateAttack() {
        var attack = new AquaisNeigeAttack(this);
        attack.setType(MELEE);
        return attack;
    }

    @Override
    public int getMaxHit(AttackType type) {
        if (type == RANGED)
            return 24;
        else if (type == MELEE)
            return 31;
        else
            return super.getMaxHit(type);
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        final HitTemplateBuilder builder = HitTemplate.builder(type).setDelay(type == RANGED ? 2 : 0);

        if (Misc.random(4) == 1 && getHitpoints() < fetchDefinition().getHitpoints()) {
            // if (getHitpoints() < fetchDefinition().getHitpoints() - 20 ) {
            startHealing();
            getCombat().extendNextAttackDelay(6);
            healNearby();
            //}
        }


        return builder.buildAsStream();
    }

    public void startHealing() {
        heal();
    }

    @Override
    public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
        if (type == RANGED) {
            return ProjectileTemplate
                    .builder(1223)
                    .setSourceSize(2)
                    .setSourceOffset(2)
                    .setStartHeight(42)
                    .setEndHeight(32)
                    .setCurve(25)
                    .setSpeed(40)
                    .setDelay(25)
                    .buildAsStream();
        }
        return Stream.empty();
    }

    public void healNearby() {
        var position = getPosition();

        for (var npc : getOwner().getLocalNpcs()) {
            if (npc instanceof AquaisNeigeNpc)
                if (position.isWithinDistance(npc.getPosition(), 5) && !npc.isDying() && npc.getHitpoints() > 0) {
                    if (npc.getHitpoints() < npc.getMaxHitpoints())
                        ((AquaisNeigeNpc) npc).heal();
                }
        }
    }

}
