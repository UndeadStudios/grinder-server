package com.grinder.game.content.minigame.aquaisneige.monsters;

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.MELEE;


public final class IceFiend extends AquaisNeigeNpc implements AttackProvider {

    public IceFiend(int npcId, Position position) {

        super(npcId, position);
        setRace(MonsterRace.DEMON);
        setSize(2);
    }

    @NotNull
    @Override
    public BossAttack generateAttack() {
        BossAttack attack = new BossAttack(this) {
            @NotNull
            @Override
            public Hit[] createHits(@NotNull Boss actor, @NotNull Agent target) {
                final Hit[] hits = super.createHits(actor, target);
                int prayerLevel = getOwner().getSkillManager().getCurrentLevel(Skill.PRAYER);
                if (prayerLevel > 0) {
                    for (final Hit hit : hits) {
                        getOwner().getSkillManager().setCurrentLevel(Skill.PRAYER, prayerLevel - hit.getTotalDamage(), true);
                    }
                }
                return hits;
            }
        };
        attack.setType(MELEE);
        return attack;
    }

    @NotNull
    @Override
    public OutOfRangePolicy attackRangePolicy(AttackType type) {
        return OutOfRangePolicy.TRACE_TO_TARGET;
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        return HitTemplate.builder(type).setDelay(0).buildAsStream();
    }
}
