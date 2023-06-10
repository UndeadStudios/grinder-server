package com.grinder.game.content.minigame.aquaisneige.monsters;

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.util.Priority;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;


public final class IceGiant extends AquaisNeigeNpc implements AttackProvider {

    private final static Animation MELEE_ANIMATION = new Animation(4672, Priority.HIGHEST);

    public IceGiant(int npcId, Position position) {

        super(npcId, position);
        setRace(MonsterRace.GIANT);
        setSize(2);
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        return HitTemplate.builder(type).setDelay(0).buildAsStream();
    }

    @NotNull
    @Override
    public OutOfRangePolicy attackRangePolicy(AttackType type) {
            return OutOfRangePolicy.TRACE_TO_TARGET;
    }

    @Override
    public Animation getAttackAnimation(AttackType type) {
        return MELEE_ANIMATION;
    }

}
