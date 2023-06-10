package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster;

import com.google.common.collect.ImmutableMap;
import com.grinder.game.World;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitQueued;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.model.*;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.sound.Sound;
import com.grinder.util.Priority;
import com.grinder.util.random.RandomGen;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class TorturedGorillaAttackStrategy implements AttackStrategy {

    private AttackType protectionPrayer;

    /**
     * The attack styles this gorilla can switch to.
     */
    private static final AttackType[] ATTACK_STYLES = {AttackType.MELEE, AttackType.RANGED, AttackType.MAGIC};

    /**
     * Represents the gorilla id which holds the melee overhead.
     */
    private static final int PROTECT_MELEE = 7144;

    /**
     * Represents the gorilla id which holds the ranged overhead.
     */
    private static final int PROTECT_RANGED = 7145;

    /**
     * Represents the gorilla id which holds the magic overhead.
     */
    private static final int PROTECT_MAGIC = 7146;

    /**
     * The random generator to generate random values.
     */
    private static final RandomGen RANDOM = new RandomGen();

    /**
     * Determines howmuch damage this gorilla has taken.
     */
    private int damageTaken;

    /**
     * Determines howmany times in a row this gorilla has dealt a unsuccessful hit.
     */
    private int unsuccessful_hits;

    /**
     * The current attack type this gorilla is using.
     */
    private @NotNull AttackType current = RANDOM.random(ATTACK_STYLES);

    /**
     * The mapping which holds every gorilla with the correct attack type overhead.
     */
    private static final ImmutableMap<AttackType, Integer> GORILLA_BY_PROT = ImmutableMap.of(
            AttackType.MELEE, PROTECT_MELEE,
            AttackType.RANGED, PROTECT_RANGED,
            AttackType.MAGIC, PROTECT_MAGIC
    );

    /**
     * Constructs a new {@link TorturedGorillaAttackStrategy} instance.
     * @param gorilla   the gorilla to create this instance for.
     */
    public TorturedGorillaAttackStrategy(NPC gorilla) {
        switch(gorilla.getId()) {
            case PROTECT_MELEE:
                protectionPrayer = AttackType.MELEE;
                break;
            case PROTECT_RANGED:
                protectionPrayer = AttackType.RANGED;
                break;
            case PROTECT_MAGIC:
                protectionPrayer = AttackType.MAGIC;
                break;
        }
        gorilla.getCombat().subscribe(event -> {

            if(event instanceof IncomingHitQueued){

                final IncomingHitQueued incomingHitQueued = (IncomingHitQueued) event;
                final Hit hit = incomingHitQueued.getHit();

                if(hit.getAttackType() == protectionPrayer) {
                    hit.setAccurate(false);
                    hit.setTotalDamage(0);
                }
            }

            if(event instanceof IncomingHitApplied){

                final IncomingHitApplied incomingHitApplied = (IncomingHitApplied) event;
                final Hit hit = incomingHitApplied.getHit();

                if(hit.getTotalDamage() < 1 || hit.getAttackType() == protectionPrayer)
                    return false;

                damageTaken += hit.getTotalDamage();
            }
            return false;
        });
    }


    /**
     * Transforms this gorillas attack styles to a new style which is currently
     * _not_ being used.
     */
    private void switchAttackStyle() {
        if(RANDOM.inclusive(100) < 50) {
            current = AttackType.SPECIAL;
            return;
        }

        current = RANDOM.random(Arrays.stream(ATTACK_STYLES).filter(a -> !a.equals(current)).collect(Collectors.toList()));
    }
    /**
     * Gets the next random gorilla id with a different overhead by excluding
     * the current gorilla id.
     * @param type   the last attack type that successfully dealt damage and this gorilla is gonna protect for.
     * @return the new id this gorilla is gonna transform into.
     */
    private int getGorillaByAttackType(AttackType type) {
        return GORILLA_BY_PROT.get(type);
    }

    @Override
    public void animate(@NotNull Agent actor) {
        switch(type()) {
            case MELEE:
                actor.performAnimation(new Animation(7226));
                break;
            case RANGED:
                actor.performAnimation(new Animation(7240));
                break;
            case MAGIC:
                actor.performAnimation(new Animation(7238));
                break;
            case SPECIAL:
                actor.performAnimation(new Animation(7228));
                break;
        }
    }

    @Override
    public void sequence(@NotNull Agent actor, @NotNull Agent target) {
        switch(type()) {
            case MELEE:
                target.getAsPlayer().getPacketSender().sendSound(3350);
                break;
            case RANGED:
                target.getAsPlayer().getPacketSender().sendSound(3191);
                final ProjectileTemplate ranged_projectileTemplate = ProjectileTemplate.builder(1302)
                        .setStartHeight(64)
                        .setEndHeight(0)
                        .setCurve(0)
                        .setDelay(45)
                        .setSourceSize(1)
                        .setSpeed(30)
                        .build();
                Projectile ranged_projectile = new Projectile(actor.getCenterLocation(), target.getPosition(), ranged_projectileTemplate);

                final HitTemplate rangedHitTemplate = HitTemplate
                        .builder(AttackType.RANGED)
                        .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE)
                        .setSuccessOrFailedGraphic(new Graphic(1303, Priority.HIGH))
                        .setDelay(2)
                        .build();

                Hit rangedHit = new Hit(actor, target, this, rangedHitTemplate);

                ranged_projectile.sendProjectile();

                /*ranged_projectile.onArrival(() -> {
                    target.getAsPlayer().getPacketSender().sendSound(3528);
                });*/
                break;
            case MAGIC:
                final ProjectileTemplate magic_projectileTemplate = ProjectileTemplate.builder(1304)
                        .setStartHeight(64)
                        .setEndHeight(0)
                        .setCurve(0)
                        .setDelay(45)
                        .setSourceSize(1)
                        .setDepartureSound(new Sound(3529, 15))
                        .setArrivalSound(new Sound(3528))
                        .setSpeed(30)
                        .build();
                Projectile magic_projectile = new Projectile(actor.getCenterLocation(), target.getPosition(), magic_projectileTemplate);

                final HitTemplate magicHitTemplate = HitTemplate
                        .builder(AttackType.MAGIC)
                        .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                        .setSuccessOrFailedGraphic(new Graphic(1305, Priority.HIGH))
                        .setSuccessOrFailedSound(new Sound(3528))
                        .setDelay(2)
                        .build();

                Hit magicHit = new Hit(actor, target, this, magicHitTemplate);

                magic_projectile.sendProjectile();
                break;
            case SPECIAL:
                target.getAsPlayer().getPacketSender().sendAreaSound(actor.getPositionToFace(), 1444, 7);
                final ProjectileTemplate boulderCrash = ProjectileTemplate.builder(856)
                        .setStartHeight(250)
                        .setEndHeight(8)
                        .setCurve(0)
                        .setDelay(40)
                        .setSourceSize(1)
                        .setSourceOffset(160)
                        .setSpeed(135)
                        .build();

                Position start = target.getPosition().clone().add(1, 1);
                Position end = target.getPosition().clone();
                Projectile projectile = new Projectile(start, end, boulderCrash);

                projectile.sendProjectile();
                projectile.onArrival(() -> {
                    World.spawn(new TileGraphic(end, new Graphic(305, 20, GraphicHeight.LOW)));
                    target.getAsPlayer().getPacketSender().sendAreaSound(end, 1442, 7);
                    if(target.getPosition().sameAs(end)) {
                        //target.getCombat().queue(hit);
                        target.getCombat()
                                .queue(new Damage(target.getHitpoints() / 3, DamageMask.REGULAR_HIT));
                    }
                });

                break;
        }
    }

    @Override
    public void postHitAction(@NotNull Agent actor, @NotNull Agent target) {
        if(current.equals(AttackType.SPECIAL)) {
            current = RANDOM.random(ATTACK_STYLES);
        }
    }

    @Override
    public void postHitEffect(@NotNull Hit hit) {
        if (type() == AttackType.RANGED) {
            hit.getTarget().performGraphic(new Graphic(1303, Priority.HIGH));
        }
        if(hit.getTotalDamage() > 0) {
            unsuccessful_hits = 0;
            return;
        }

        unsuccessful_hits++;

        if(unsuccessful_hits > 2) {
            switchAttackStyle();
            unsuccessful_hits = 0;
        }
    }

    @Override
    public int duration(Agent actor) {
        return actor.getBaseAttackSpeed();
    }

    @Override
    public int requiredDistance(@NotNull Agent actor) {
        return current.equals(AttackType.MELEE) ? 1 : 4;
    }

    @Override
    public boolean canAttack(@NotNull Agent actor, @NotNull Agent target) {
        return true;
    }

    @Override
    public Hit[] createHits(@NotNull Agent actor, @NotNull Agent target) {
        Hit hit = new Hit(actor, target, this,true, type() == AttackType.MELEE ? 0 : 2);
        return type().equals(AttackType.SPECIAL) ? new Hit[]{  } : new Hit[]{ hit };
    }

    @Override
    public AttackType type() {
        return current;
    }

    @Override
    public void postIncomingHitEffect(@NotNull Hit hit) {

    }
}