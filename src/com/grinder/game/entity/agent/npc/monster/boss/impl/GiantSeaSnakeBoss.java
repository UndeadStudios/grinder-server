package com.grinder.game.entity.agent.npc.monster.boss.impl;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.monster.MonsterEvents;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion;
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.util.Misc;
import com.grinder.util.Priority;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.MELEE;
import static com.grinder.game.entity.agent.combat.attack.AttackType.RANGED;
import static com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy.*;

/**
 * TODO: add documentation
 *
 * @author 2012 (wrote the original code)
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
public class GiantSeaSnakeBoss extends Boss implements AttackProvider {

    private static int aliveMinions;
    private static int aliveSnakes;

    private int underWaterTicksRemaining;

    public GiantSeaSnakeBoss(int npcId, final Position position) {
        super(npcId, position);
        getMotion().update(MovementStatus.DISABLED);
        onEvent(monsterEvents -> {
            if (monsterEvents == MonsterEvents.ADDED) {
                aliveMinions = 0;
                if (position.getZ() == 0) //Dont need to keep track w/ Instances because they don't respawn
                aliveSnakes++;
            }
            if (monsterEvents == MonsterEvents.REMOVED) {
                if (position.getZ() == 0)
                aliveSnakes--;
            }
            return null;
        });
    }

    private void spawnMinions() {
        aliveMinions += SEA_SNAKELING_COUNT;
        IntStream.range(0, SEA_SNAKELING_COUNT)
                .mapToObj(id -> new SeaSnakeling(this))
                .forEach(SeaSnakeling::spawn);
    }

    @Override
    protected boolean checkAttackRangeForTypeExclusion(@NotNull AttackType type) {
        return type == MELEE;
    }

    @Override
    public boolean skipRespawnSequence() {
        return aliveSnakes > 0 || getPosition().getZ() > 0;
    }

    @Override
    public boolean skipProjectileClipping() {
        return true;
    }

    @NotNull
    @Override
    public MonsterRetreatPolicy getRetreatPolicy() {
        return MonsterRetreatPolicy.NEVER;
    }

    @NotNull
    @Override
    public Pair<Boolean, String> immuneToAttack(@NotNull AttackType type) {

        if (aliveMinions > 0)
            return new Pair<>(true, KILL_SNAKELINGS_FIRST_MESSAGE);

        return super.immuneToAttack(type);
    }

    @Override
    public void preSequence() {

        if (underWaterTicksRemaining > 0) {
            underWaterTicksRemaining--;

            if (underWaterTicksRemaining - 3 == 0) {
                say("*Splash!*");
            }
            if (underWaterTicksRemaining - 2 == 0) {
                setVisible(false);
            }
            if (underWaterTicksRemaining == 0) {
                setVisible(true);
                forceAnimation(new Animation(4042, Priority.HIGH));
                getCombat().extendNextAttackDelay(5);
            }
        }
        super.preSequence();
    }

    @Override
    public boolean skipNextCombatSequence() {
        if (underWaterTicksRemaining > 0)
            return true;
        return super.skipNextCombatSequence();
    }

    @NotNull
    @Override
    protected AttackTypeProvider attackTypes() {
        return AttackType.equalChances(AttackType.MELEE, AttackType.RANGED);
    }

    @NotNull
    @Override
    public BossAttack generateAttack() {
        return new BossAttack(this) {

            @Override
            public void postHitAction(@NotNull Boss actor, @NotNull Agent target) {
                if (Misc.randomChance(5F)) {
                    target.getCombat().resetCombatWith(actor);
                    actor.performAnimation(new Animation(4039));
                    underWaterTicksRemaining = Misc.random(4, 6);
                } else if (Misc.randomChance(1F)) {
                    if (aliveMinions == 0) {
                        spawnMinions();
                    }
                }
            }
        };
    }

    @Override
    public int maxTargetsHitPerAttack(@NotNull AttackType type) {
        return 4;
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return type == AttackType.MELEE ? 2 : 20;
    }

    @Override
    public int fetchAttackDuration(AttackType type) {
        return type == AttackType.MELEE ? 3 : 4;
    }

    @Override
    public Animation getAttackAnimation(AttackType type) {
        return type == AttackType.MELEE ? new Animation(4038) : new Animation(4041);
    }

    @NotNull
    @Override
    public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
        return type == MELEE ? Stream.empty() : ProjectileTemplate
                .builder(1665)
                .setSourceSize(1)
                .setSourceOffset(1)
                .setStartHeight(50)
                .setEndHeight(30)
                .setCurve(280)
                .setSpeed(15)
                .setDelay(40)
                .buildAsStream();
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        return HitTemplate.builder(type)
                .setDelay(type == RANGED ? 2 : 0)
                .buildAsStream();
    }

    @Override
    public Optional<String> fetchTextAboveHead(AttackType type) {
        if (Misc.randomChance(5F))
            return Optional.of(Misc.randomString(CHATS_ABOVE_HEAD));
        else
            return Optional.empty();
    }

    private static final String KILL_SNAKELINGS_FIRST_MESSAGE = "You must kill all the snakelings first before attacking the the Giant Snakes!";

    private static final String[] CHATS_ABOVE_HEAD = {
            "Ugh disgusting fleshy creature!",
            "This is our swamp!",
            "We will take you deep down! HAHA",
            "Your weapons wont work!",
            "ALL AT ONCE NOW BROTHERS!",
            "Foolish and unslimmy! Eh!",};

    private static final int SEA_SNAKELING_COUNT = 4;
    private static final int SEA_SNAKELING_NPC_ID = 1098;
    private static final String SEA_SNAKELING_CHAT_ABOVE_HEAD = "Sss";
    private static final Position SEA_SNAKELING_BASE_POSITION = new Position(2462, 4780, 0);

    public static class SeaSnakeling extends BossMinion<GiantSeaSnakeBoss> {
        SeaSnakeling(GiantSeaSnakeBoss bossNPC) {
            super(bossNPC,
                    SEA_SNAKELING_NPC_ID,
                    SEA_SNAKELING_BASE_POSITION.transform(0, 0, bossNPC.getPosition().getZ()).randomize(2),
                    NO_RESPAWN,
                    REMOVE_WHEN_BOSS_REMOVED,
                    ATTACK_PREFERRED_OPPONENT);
            fetchDefinition().setAggressive(true);

            onEvent(monsterEvents -> {

                if (monsterEvents == MonsterEvents.REMOVED) {
                    aliveMinions--;
                } else if (monsterEvents == MonsterEvents.ADDED) {
                    getCombat().setAutoRetaliate(true);
                    say(SEA_SNAKELING_CHAT_ABOVE_HEAD);
                }
                return null;
            });
        }
    }
}
