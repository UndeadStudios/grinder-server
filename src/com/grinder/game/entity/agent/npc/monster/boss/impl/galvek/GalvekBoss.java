package com.grinder.game.entity.agent.npc.monster.boss.impl.galvek;

import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitQueued;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy;
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.debug.DebugListener;
import com.grinder.util.oldgrinder.Area;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.*;
import static com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekAttack.*;
import static com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekConstants.*;

/**
 * Represents the galvek wilderness {@link Boss}.
 * This was not meant to be 1:1 with osrs, but in actuality is very close.
 *
 */
public class GalvekBoss extends Boss implements AttackProvider, DebugListener {

    private Position localReferencePoint;
    public boolean tsunamiActive = false;
    public boolean fireTrapsActive = false;
    private GalvekAttack attackType = Misc.random(MAGIC_AIR_ATTACK_SITTING, RANGE_RED_ATTACK_SITTING);
    private GalvekAttack lastNonSpecialAttackType = null;
    private GalvekPhase phase;
    public Iterator<GalvekPhase> phases = GalvekPhase.VALUES.iterator();
    private int attackSwitch;
    private int lastAttackSwitch;
    private int specialCount;
    public int transformTotalDurationInGameTicks;



    public GalvekBoss(final Position position){
        super(NpcID.GALVEK_FIRE, position);
        attackSwitch = 3;
        //removeRespawnMessage = false;
        specialCount = Misc.random(3, 5);
        phase = phases.next();
        setLocalReferencePoint(position);
        setRace(MonsterRace.DRAGON);
       // getMotion().update(MovementStatus.DISABLED);
        getCombat().subscribe(event -> {
            if (event instanceof IncomingHitQueued)
                onIncomingHitQueued(((IncomingHitQueued) event).getHit());
            else if (event instanceof IncomingHitApplied)
                onIncomingHitApplied(((IncomingHitApplied) event).getHit());
            return false;
        });
    }

    private void changeStage() {
        phase.onPhaseDeath(this);

        // Last phase, no need to change
        if(phase != GalvekPhase.EARTH) {
            phase = phases.next();
        }

        // Get a new attack that matches our new phase
        attackType = attackType.getRandomAttack(this);


//        if(phase == GalvekPhase.EARTH) {
//            specialCount = 3;
//        }
    }

    @Override
    public boolean skipProjectileClipping() {
        return true;
    }

    @NotNull
    @Override
    public BossAttack generateAttack() {
        return new BossAttack(this){

            @Override
            public void sequence(@NotNull Boss actor, @NotNull Agent target) {
                if(type() == SPECIAL)
                    attackType.sequence(GalvekBoss.this, target);
                else
                    super.sequence(actor, target);

            }

            @Override
            public boolean canAttack(Boss actor, @NotNull Agent target) {
                return super.canAttack(actor, target) && attackType.canAttack(GalvekBoss.this, target);
            }
        };
    }

    @Override
    public void onDeath() {
        // Make sure we change into the dead one at the end.
        // Dont need to set anims because the npc has different
        // idle animations
        setNpcTransformationId(NpcID.GALVEK_EARTH_DEAD);
    }

    @Override
    public NpcDefinition fetchDefinition() {
        return getNpcTransformationId() == -1
                ? super.fetchDefinition()
                : NpcDefinition.forId(getNpcTransformationId());
    }


    @Override
    public boolean skipNextCombatSequence() {
        return super.skipNextCombatSequence()
                  || tsunamiActive
                || transformTotalDurationInGameTicks > 0;
    }

    @Override
    public boolean defenceModifier() {
        return false;
    }

    @Override
    public int getBlockAnim() {
        return ((phase == GalvekPhase.WATER || phase == GalvekPhase.AIR) ? standAnimFloating : standAnimSitting).getId();
    }

    @Override
    public int getMaxHit(AttackType type) {
        // change the max hit here for special conditions if needed
        return super.getMaxHit(type);
    }



    @Override
    public void sequence() {

        super.sequence();

        // You can decrement the transformTotalDurationInGameTicks here if you want and then
        // run certain code on certain ticks. Atm we are not decrementing transformTotalDurationInGameTicks here
        // and it will only get set to zero when the transform is actually done. Should use a boolean but leaving
        // as an integer in case we need to use this functionality again.
        /*
        if (transformTotalDurationInGameTicks > 0) {
            switch (--transformTotalDurationInGameTicks) {
                case 1:
                    getCombat().reset(false); // setting this to true will reset damage map, reset damage map = no loot for player
                    break;
            }
        }*/
    }

    @Override
    public void randomizeAttack() {
        if (--specialCount == 0) {
            specialCount = phase == GalvekPhase.EARTH ? 9 : 5;

            final List<GalvekAttack> possibleSpecialAttacks = Arrays.stream(GalvekAttack.VALUES)
                    .filter(hydraAttack -> hydraAttack.getType() == SPECIAL)
                   //.filter(hydraAttack ->  (hydraAttack == TSUNAMI_TEST && !tsunamiActive) || (hydraAttack.getType() == SPECIAL && hydraAttack != TSUNAMI_TEST))
                    .filter(attack -> attack.isApplicable(this))
                    .collect(Collectors.toList());

            if (getCombat().hasTarget()) {

                final Agent target = getCombat().getTarget();

                possibleSpecialAttacks.removeIf(hydraAttack -> !hydraAttack.canAttack(this, target));
            }

            if (possibleSpecialAttacks.isEmpty())
                attackSwitch = 1;
            else {
                lastNonSpecialAttackType = attackType;
                lastAttackSwitch = attackSwitch;
                attackType = Misc.randomElement(possibleSpecialAttacks);
                attackSwitch = 2; // cause next attack to swap
            }
        } else if (attackType.getType() == SPECIAL) {
            attackType = lastNonSpecialAttackType;
            attackSwitch = lastAttackSwitch;
        }
        if (--attackSwitch <= 0) {
            attackType = attackType.getRandomAttack(this);
            attackSwitch = phase.getAttackCount();
        }
        getBossAttack().setType(attackType.getType());

    }

    private void onIncomingHitQueued(Hit hit){
        final AttackContext details = hit.getAttackDetails();

        // TODO These are just random examples, but we can edit our damage like this
        // Comment these out if we don't want them.

        // Stronger against range or stab attacks
        if(!details.used(RANGED) && !details.usedAny(WeaponFightType.OFFENSIVE_STAB))
            hit.multiplyDamage(0.70D);

        // Final phase absorbs 25% of damage for example
        if(phase == GalvekPhase.EARTH) {
            hit.multiplyDamage(0.25D);
            hit.getAttacker().ifPlayer(player -> player.sendMessage("Galvek's defences partially absorb your attack!"));
        }
    }

    private void onIncomingHitApplied(Hit hit) {
        final int health = getHitpoints();

        if(health <= hit.getTotalDamage()){
            changeStage();
            hit.setTotalDamage(health);
            setHitpoints(0);
            return;
        }

        final int maxHealth = fetchDefinition().getHitpoints();
        final boolean above75 = health > (maxHealth * 0.75),
                above50 = health > (maxHealth * 0.5),
                above25 = health > (maxHealth * 0.25);

        final int healthPostDamage = health - hit.getTotalDamage();

        if ((above75 && healthPostDamage <= (maxHealth * 0.75))
                || (above50 && healthPostDamage <= (maxHealth * 0.5))
                || (above25 && healthPostDamage <= (maxHealth * 0.25)))
            changeStage();
    }

    @NotNull
    @Override
    public AttackTypeProvider attackTypes() {
        return attackType.getType();
    }

    @Override
    public int fetchAttackDuration(AttackType type) {
        return 7;
    }

    @Override
    public OutOfRangePolicy attackRangePolicy(AttackType type) { return OutOfRangePolicy.TRACE_TO_TARGET; }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return (attackType == MELEE_ATTACK_FLOATING || attackType == MELEE_ATTACK_SITTING) ? 1 : 20;
    }

    @Override
    public int maxTargetsHitPerAttack(@NotNull AttackType type) {
        return 3;
    }

    @Override
    public Animation getAttackAnimation(AttackType type) {
        return attackType.getAttackAnimation(type);
    }

    @NotNull
    @Override
    public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
        return attackType.fetchProjectiles(type);
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        return attackType.fetchHits(type);
    }

   public void setLocalReferencePoint(Position localReferencePoint) {
        this.localReferencePoint = localReferencePoint;
    }

    public Position getLocalReferencePoint() {
        return localReferencePoint;
    }

    public GalvekPhase getPhase() {
        return phase;
    }

    public void setAttackType(GalvekAttack attackType) {
        this.attackType = attackType;
    }

    public GalvekAttack getAttackType() {
        return attackType;
    }

    @Override
    public String[] lines() {
        if (getCombat().getNextAttackTimer(false) == null) {
            return new String[0];
        }
        return new String[]{
                "state = "+ phase,
                "ticks = "+getCombat().getNextAttackTimer(false).ticks(),
                "attack = "+attackType,
                "waves = "+ tsunamiActive,
                "trans = "+ transformTotalDurationInGameTicks,
                "switch = "+attackSwitch,
                "spec = "+specialCount
        };
    }
}
