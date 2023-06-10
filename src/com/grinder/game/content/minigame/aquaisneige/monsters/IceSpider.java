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
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.MELEE;


public final class IceSpider extends AquaisNeigeNpc implements AttackProvider {

    public IceSpider(int npcId, Position position) {

        super(npcId, position);
        setRace(MonsterRace.SPIDER);
    }

    @NotNull
    @Override
    public BossAttack generateAttack() {
        BossAttack attack = new BossAttack(this);
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
        return HitTemplate.builder(type).buildAsStream();
    }
}
