package com.grinder.game.content.minigame.fightcave;

import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionTolerancePolicy;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy;
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy;
import com.grinder.game.model.*;
import com.grinder.game.model.sound.Sound;
import com.grinder.util.Misc;
import com.grinder.util.Priority;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.MELEE;

/**
 * @author L E G E N D
 */
public class FightCaveNpc extends Boss implements AttackProvider {

    public FightCaveNpc(int npcId, @NotNull Position position) {
        super(npcId, position);
        setRace(MonsterRace.TZHAAR);
    }

    @Override
    public int getBaseAttackSpeed() {
        return fetchDefinition().getAttackSpeed();
    }

    @Override
    public void sequence() {
        sequenceProperty().set(true);
        super.sequence();
    }

    @Override
    public int attackRange(AttackType type) {
        return type == MELEE ? 1 : 64;
    }

    @Override
    protected boolean checkAttackRangeForTypeExclusion(@NotNull AttackType type) {
        return true;
    }


    public void heal() {
        heal(0, 15);
    }

    public void heal(int min, int max) {
        super.heal(Misc.random(min, max + 1));
        getOwner().playSound(new Sound(167, 0));
        performGraphic(new Graphic(444, 0, GraphicHeight.HIGH, Priority.MEDIUM));
    }

    @Override
    public MonsterAggressionTolerancePolicy getAggressionTolerancePolicy() {
        return MonsterAggressionTolerancePolicy.NEVER;
    }

    @Override
    public int fetchAttackDuration(AttackType type) {
        return 4;
    }

    @Override
    public Animation getAttackAnimation(AttackType type) {
        final var definitions = fetchDefinition();
        if (type == AttackType.MAGIC) {
            return new Animation(definitions.getMagicAnim());
        } else if (type == AttackType.RANGED) {
            return new Animation(definitions.getRangeAnim());
        } else {
            return new Animation(definitions.getAttackAnim());
        }
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        HitTemplateBuilder builder = new HitTemplateBuilder(type);
        getOwner().playSound(new Sound(Misc.random(2841, 2845)));
        return builder.buildAsStream();
    }

    @Override
    public MonsterRetreatPolicy getRetreatPolicy() {
        return MonsterRetreatPolicy.NEVER;
    }

    @Override
    public OutOfRangePolicy attackRangePolicy(AttackType type) {
        return OutOfRangePolicy.TRACE_TO_TARGET;
    }

    @Override
    protected int maxTargetsHitPerAttack(AttackType type) {
        return 1;
    }

    @Override
    public BossAttack generateAttack() {
        if (super.bossAttack == null) {
            super.setBossAttack(new FightCaveAttack(this));
        }
        setPreferredAttackType(MELEE);
        return super.bossAttack;
    }

    @Override
    protected AttackTypeProvider attackTypes() {
        return MELEE;
    }
}
