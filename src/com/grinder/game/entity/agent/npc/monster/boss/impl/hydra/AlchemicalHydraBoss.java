package com.grinder.game.entity.agent.npc.monster.boss.impl.hydra;

import com.grinder.game.World;
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
import com.grinder.game.entity.agent.movement.task.MovementTask;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.VentObject.Chemical;
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.instanced.HydraArea;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.Priority;
import com.grinder.util.debug.DebugListener;
import com.grinder.util.oldgrinder.Area;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.*;

/**
 * Represents the alchemical hydra {@link Boss}.
 *
 * See https://oldschool.runescape.wiki/w/Alchemical_Hydra
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-31
 */
public class AlchemicalHydraBoss extends Boss implements AttackProvider, DebugListener {

    public final static Area UNCLIPPED = Area.of(1357, 10258, 1376, 10277);

    private Position localReferencePoint;
    private HydraAttack attackType = Misc.random(HydraAttack.MAGIC, HydraAttack.RANGED);
    private HydraAttack lastNonSpecialAttackType = null;
    private HydraState state = HydraState.POISON;
    private VentObject[] vents;

    private Chemical sprayed = Chemical.NONE;

    private boolean dummy;
    private int attackSwitch;
    private int lastAttackSwitch;
    private int specialCount;
    private int ventDelay;

    long wallDelta;

    int transformDurationInGameTicks;
    int fireWallTicks;

    public AlchemicalHydraBoss(final Position position){
        super(NpcID.ALCHEMICAL_HYDRA, position);
        localReferencePoint = AlchemicalHydraInstance.LOCAL_REFERENCE_POINT;
        attackSwitch = 3;
        ventDelay = 10;
        removeRespawnMessage = true;
        specialCount = Misc.random(3, 5);
        createVentObjects();
        setRace(MonsterRace.HYDRA);
        getCombat().subscribe(event -> {
            if (event instanceof IncomingHitQueued)
                onIncomingHitQueued(((IncomingHitQueued) event).getHit());
            else if (event instanceof IncomingHitApplied)
                onIncomingHitApplied(((IncomingHitApplied) event).getHit());
            return false;
        });
    }

    /**
     * !! ONLY USE THIS FOR SPAWNING A DUMMY HYDRA !!
     */
    public AlchemicalHydraBoss(int npcId, final Position position) {
        super(npcId, position);
        dummy = true;
    }

    private void createVentObjects(){
        vents = new VentObject[] {
                new VentObject(Chemical.BLUE, localReferencePoint.transform(34, 24, 0)),
                new VentObject(Chemical.GREEN, localReferencePoint.transform(43, 24, 0)),
                new VentObject(Chemical.RED, localReferencePoint.transform(43, 15, 0))
        };
    }

    private void changeStage() {
        state.renderDeath(this);

        if(state != HydraState.ENRAGED)
            state = HydraState.values()[state.ordinal()+1];

        if(state == HydraState.ENRAGED) {
            sprayed = Chemical.NONE;
            specialCount = 3; // start off with 3, then change to 9
        }
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
                    attackType.sequence(AlchemicalHydraBoss.this, target);
                else
                    super.sequence(actor, target);
            }

            @Override
            public boolean canAttack(Boss actor, @NotNull Agent target) {
                return super.canAttack(actor, target) && attackType.canAttack(AlchemicalHydraBoss.this, target);
            }
        };
    }

    @Override
    public NpcDefinition fetchDefinition() {
        return getNpcTransformationId() == -1
                ? super.fetchDefinition()
                : NpcDefinition.forId(getNpcTransformationId());
    }


    @Override
    public boolean skipNextCombatSequence() {
        return dummy || super.skipNextCombatSequence()
//                || fireWallTicks > 0
//                || (attackType == HydraAttack.FIREWALL && !getPosition().isWithinDistance(getSpawnPosition(), 1))
                || (getAnimation() != null && transformDurationInGameTicks > 0 && HydraState.isTransformAnim(getAnimation().getId()));
    }

    @NotNull
    @Override
    public MonsterRetreatPolicy getRetreatPolicy() {
        return MonsterRetreatPolicy.NEVER;
    }

    @Override
    public boolean defenceModifier() {
        return false;
    }

    @Override
    public int getBlockAnim() {
        return -1;
    }

    @Override
    public int getMaxHit(AttackType type) {

        if(sprayed != Chemical.NONE && sprayed.getAffectedState() != state)
            return (int) (super.getMaxHit(type) * 1.2);

        return super.getMaxHit(type);
    }

    @Override
    public void sequence() {

        if(dummy){
            super.sequence();
            return;
        }

        if(getOwner() == null || !(getOwner().getArea() instanceof HydraArea)){

            World.getNpcRemoveQueue().add(this);

            return;
        }

        super.sequence();

        if(--ventDelay == 5){

            createVentObjects();

            for(final VentObject vent : vents){

                final Optional<Chemical> sprayed = vent.sprayedChemical(this);

                if(sprayed.isPresent()){

                    final Chemical chemical = sprayed.get();

                    if(this.sprayed == chemical)
                        continue;

                    if(state == HydraState.ENRAGED)
                        break;

                    this.sprayed = chemical;

                    if(chemical.getAffectedState() == state)
                        Optional.ofNullable(getOwner()).ifPresent(player -> player.sendMessage("The chemicals neutralise the Alchemical Hydra's defences!"));
                    else
                        Optional.ofNullable(getOwner()).ifPresent(player -> player.sendMessage("The chemicals boost the Alchemical Hydra's strength!"));

                    say("Roaaaaaaaaaaar!");
                }
            }

        } else if (ventDelay <= 0){

            for(final VentObject vent : vents)
                vent.getObject().performAnimation(new Animation(8280));

            ventDelay = 10;
        }

        if (fireWallTicks > 0)
            fireWallTicks--;

        if (transformDurationInGameTicks > 0) {
            switch (--transformDurationInGameTicks) {
                case 1:
                    getCombat().reset(false); // setting this to true will reset damage map, reset damage map = no loot for player
                    break;
                case 0:
                    setNpcTransformationId(getState().getTransformId());
                    performAnimation(new Animation(HydraState.values()[getState().ordinal() - 1].getAnimation() + 1, 5));
                    break;
            }
        }
    }

    @Override
    public void randomizeAttack() {
        if (--specialCount == 0) {
            specialCount = state == HydraState.ENRAGED ? 9 : 5;

            final List<HydraAttack> possibleAttacks = Arrays.stream(HydraAttack.values())
                    .filter(hydraAttack -> hydraAttack.getType() == SPECIAL)
                    .filter(attack -> attack.isApplicable(this))
                    .collect(Collectors.toList());

            if (getCombat().hasTarget()) {

                final Agent target = getCombat().getTarget();

                possibleAttacks.removeIf(hydraAttack -> !hydraAttack.canAttack(this, target));
            }

            if (possibleAttacks.isEmpty())
                attackSwitch = 1;
            else {
                lastNonSpecialAttackType = attackType;
                lastAttackSwitch = attackSwitch;
                attackType = Misc.randomElement(possibleAttacks);
                attackSwitch = 2; // cause next attack to swap
            }
        } else if (attackType.getType() == SPECIAL) {
            attackType = lastNonSpecialAttackType;
            attackSwitch = lastAttackSwitch;
        }
        if (--attackSwitch <= 0) {
            attackType = attackType == HydraAttack.MAGIC ? HydraAttack.RANGED : HydraAttack.MAGIC;
            attackSwitch = state.getAttackCount();
        }
        getBossAttack().setType(attackType.getType());

        if (attackType == HydraAttack.FIREWALL) {
            if (!getPosition().sameAs(getSpawnPosition())){
                if(!getMotion().isMoving()) {
                    getCombat().reset(false);

                    getMotion().start(new MovementTask<>(Priority.HIGHEST, this) {
                        @Override
                        public void sequence() {
                            if (getPosition().sameAs(getSpawnPosition())) {
                                getMotion().impairMovement(5);
                                getCombat().setNextAttackDelay(0);
                                stop();
                                return;
                            } else
                                getCombat().extendNextAttackDelay(1);
                            getMotion().resetTargetFollowing();
                            if (!getMotion().isMoving())
                                getMotion().traceTo(getSpawnPosition());
                        }
                    }, true);
                }
            }
        }
    }

    private void onIncomingHitQueued(Hit hit){
        final AttackContext details = hit.getAttackDetails();

        if(!details.used(RANGED) && !details.usedAny(WeaponFightType.OFFENSIVE_STAB))
            hit.multiplyDamage(0.70D);

        if(state != HydraState.ENRAGED) {
            if (sprayed.getAffectedState() != state) {
                hit.multiplyDamage(0.25D);
                hit.getAttacker().ifPlayer(player -> player.sendMessage("The Alchemical Hydra's defences partially absorb your attack!"));
            }
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

    @Override
    public void respawn() { }

    @NotNull
    @Override
    public AttackTypeProvider attackTypes() {
        return attackType.getType();
    }

    @Override
    public int fetchAttackDuration(AttackType type) {
        return 6;
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return attackType == HydraAttack.FIREWALL ? 20 : 4;
    }

    @Override
    public int maxTargetsHitPerAttack(@NotNull AttackType type) {
        return 3;
    }

    @Override
    public Animation getAttackAnimation(AttackType type) {

        if(attackType == HydraAttack.FIREWALL)
            return attackType.getAttackAnimation(type);

        if (state.getAttackAnimations().length > (attackSwitch - 1))
            return state.getAttackAnimations()[attackSwitch == 0 ? 0 : attackSwitch - 1];

        return attackType.getAttackAnimation(type);
    }

    @NotNull
    @Override
    public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
        // When the heads are destroyed, only send a single projectile.
        if (state != HydraState.POISON && (type == RANGED
            || state != HydraState.LIGHTNING && type == MAGIC)) {
            return attackType.fetchProjectiles(type).limit(1);
        }
        return attackType.fetchProjectiles(type);
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        // If there are 2 heads on the side, it will hit twice.
        if (type == RANGED || type == MAGIC) {
            int hitAmount = state == HydraState.POISON || state == HydraState.LIGHTNING && type == MAGIC ? 2 : 1;
            return HitTemplate.builder(type).setHitAmount(hitAmount).buildAsStream();
        }
        return attackType.fetchHits(type);
    }

   public void setLocalReferencePoint(Position localReferencePoint) {
        this.localReferencePoint = localReferencePoint;
    }

    public Position getLocalReferencePoint() {
        return localReferencePoint;
    }

    public HydraState getState() {
        return state;
    }

    @Override
    public String[] lines() {
        if (getCombat().getNextAttackTimer(false) == null) {
            return new String[0];
        }
        return new String[]{
                "state = "+state,
                "ticks = "+getCombat().getNextAttackTimer(false).ticks(),
                "attack = "+attackType,
                "fire = "+fireWallTicks,
                "trans = "+ transformDurationInGameTicks,
                "switch = "+attackSwitch,
                "spec = "+specialCount,
                "vents = "+ventDelay
        };
    }
}
