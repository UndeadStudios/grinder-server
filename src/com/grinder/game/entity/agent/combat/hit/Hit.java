package com.grinder.game.entity.agent.combat.hit;

import com.grinder.game.entity.agent.combat.formula.CombatSnapshot;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.formula.CombatFormulaType;
import com.grinder.game.entity.agent.combat.formula.CombatFormulas;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.damage.DamageListener;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.sound.Sound;
import kotlin.ranges.IntRange;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents a pending hit.
 *
 * @author Professor Oak
 * @author Stan van der Bend
 */
public final class Hit {

    public static final int MAX_DAMAGES = 4;
    public static final int MIN_DAMAGES = 0;
    private final Agent attacker;
    private final Agent target;
    private final AttackContext context;

    private final CombatSnapshot snapshot;
    private final CombatSnapshot targetSnapshot;

    private final AttackType attackType;
    private final AttackStrategy<?> method;
    private double maxMultiplier = 1.0D;
    private Damage[] damages;

    private Graphic successGraphic, failedGraphic;
    private Sound successSound, failedSound;
    private Consumer<Agent> onSuccess, onFailed;

    private IntRange damageRange;

    private boolean checkAccuracy;
    private boolean checkStrength;
    private boolean ignorePoisonEffects;
    private boolean retaliatedHit;

    private boolean handleAfterHitEffects;
    private boolean accurate;
    private int totalDamage;
    private int delay;
    private boolean negateDamages;

    private double recoilPercentage;
    private int recoilExtraDamage;
    private boolean rewardingExperience = true;

    /**
     * Constructs a {@link Hit} with a total of 1 hit.
     **/
    public Hit(Agent attacker, Agent target, AttackStrategy<?> method, boolean checkAccuracy, int delay) {
    	this(attacker, target, method, checkAccuracy, 1, delay);
    }

    /**
     * Constructs a {@link Hit} with a total of 1 hit.
     **/
    public Hit(Agent attacker, Agent target, AttackStrategy<?> method, boolean checkAccuracy, int delay, double maxMultiplier) {
        this(attacker, target, method, method.type(), checkAccuracy, 1, delay, maxMultiplier);
    }

    /**
     * Constructs a {@link Hit} with a total of {hitAmount} damages.
     **/
    public Hit(Agent attacker, Agent target, AttackStrategy<?> method, boolean checkAccuracy, int hitAmount, int delay) {
    	this(attacker, target, method, method.type(), checkAccuracy, hitAmount, delay);
    }

    public Hit(Agent attacker, Agent target, AttackStrategy<?> method, HitTemplate template) {
        this(attacker, target, method, template, true);
    }

    public Hit(Agent attacker, Agent target, AttackStrategy<?> method, HitTemplate template, boolean prepareHits) {
        this(attacker, target, method, template, prepareHits, 1.0D);
    }

    public Hit(Agent attacker, Agent target, AttackStrategy<?> method, HitTemplate template, boolean prepareHits, double maxMultiplier) {
        this.attacker = attacker;
        this.target = target;
        this.method = method;
        this.maxMultiplier = maxMultiplier;
        retaliatedHit = attacker.getCombat().isRetaliatedAttack();
        successGraphic = template.successGraphic().orElse(null);
        failedGraphic = template.failedGraphic().orElse(null);
        successSound = template.successSound().orElse(null);
        failedSound = template.failedSound().orElse(null);
        onSuccess = template.onSuccess();
        onFailed = template.onFailed();
        damageRange = template.damageRange().orElse(null);
        attackType = template.type();
        checkAccuracy = !template.ignoreAttackStats();
        checkStrength = !template.ignoreStrengthStats();
        ignorePoisonEffects = template.ignorePoisonEffects();
        delay = template.delay();
        handleAfterHitEffects = true;
        context = new AttackContext(attacker.getCombat(),
                target, method, attackType,
                template.ignorePrayer(),
                template.ignoreAmmunitionEffects(),
                template.defenceStat(),
                template.attackStat()
        );
        snapshot = CombatSnapshot.create(attacker, context, attackType);
        targetSnapshot = CombatSnapshot.create(target, context, attackType);
        if(prepareHits) {
            createHits(template.amount());
        }
    }

    public Hit(Agent attacker,
               Agent target,
               AttackStrategy<?> method,
               AttackType attackType,
               boolean checkAccuracy,
               int hitAmount,
               int delay,
               double maxMultiplier) {
        this.attacker = attacker;
        this.target = target;
        this.method = method;
        this.attackType = attackType;
        this.checkAccuracy = checkAccuracy;
        this.delay = delay;
        this.maxMultiplier = maxMultiplier;
        checkStrength = true;
        handleAfterHitEffects = true;
        retaliatedHit = attacker.getCombat().isRetaliatedAttack();
        context = new AttackContext(attacker.getCombat(), target, method, attackType, false);
        snapshot = CombatSnapshot.create(attacker, context, attackType);
        targetSnapshot = CombatSnapshot.create(target, context, attackType);
        createHits(hitAmount);
    }

    public Hit(Agent attacker, Agent target, AttackStrategy<?> method, AttackType attackType, boolean checkAccuracy, int hitAmount, int delay) {
        this.attacker = attacker;
        this.target = target;
        this.method = method;
        this.attackType = attackType;
        this.checkAccuracy = checkAccuracy;
        this.delay = delay;
        checkStrength = true;
        handleAfterHitEffects = true;
        retaliatedHit = attacker.getCombat().isRetaliatedAttack();
        context = new AttackContext(attacker.getCombat(), target, method, attackType, false);
        snapshot = CombatSnapshot.create(attacker, context, attackType);
        targetSnapshot = CombatSnapshot.create(target, context, attackType);
        createHits(hitAmount);
    }

    public void createHits(int amount) {
        if (context.skipAccuracy())
            skipAccuracyCheck();
        damages = prepareHits(amount);
    }

    public void multiplyDamage(double mod) {

        for(final Damage damage : damages)
            damage.multiplyDamage(mod);

        updateTotalDamage();
    }

    public void setTotalDamage(int totalDamage) {

        for (final Damage damage : damages)
            damage.set(totalDamage);

        updateTotalDamage();
    }

    public void updateTotalDamage() {
        totalDamage = 0;
        for (final Damage damage : damages)
            totalDamage += damage.getValue();
    }

    public void setDamageListener(DamageListener damageListener) {
        for(final Damage damage : damages){
            damage.setDamageListener(damageListener);
        }
    }

    public boolean negateDamages(){
        return negateDamages || isSplash() && (attacker instanceof Player);
    }

    public boolean isValid(){
        return target != null && attacker != null && target.canBeTargeted() && attacker.isAlive();
    }

    public int[] getSkills() {

        if (attacker instanceof NPC)
            return new int[]{};

        return ((Player) attacker)
                .getCombat()
                .getFightType()
                .getStyle()
                .skill(attackType);
    }

    private Damage[] prepareHits(int hitAmount) {
        // Check the hit amounts.
        if (hitAmount > MAX_DAMAGES)
            throw new IllegalArgumentException("Illegal number of damages! The maximum number of damages per turn is 4.");
        else if (hitAmount < MIN_DAMAGES)
            throw new IllegalArgumentException("Illegal number of damages! The minimum number of damages per turn is 0.");

        if (attacker == null || target == null)
            return null;

        final Damage[] damages = new Damage[hitAmount];

        for (int i = 0; i < damages.length; i++) {
            //Was the hit accurate?
            accurate = !checkAccuracy || CombatFormulas.rollAccuracy(snapshot, targetSnapshot, attackType);

            if (context.isFighting(MonsterRace.COMBAT_DUMMY))
                accurate = true;

            // Warriors guild
            if (context.used(AttackType.RANGED) || context.used(AttackType.MAGIC)) {
                if (context.isFightingAny(MonsterRace.WARRIORS_GUILD) && !context.isFightingAny(MonsterRace.WARRIORS_GUILD)) {
                    accurate = false;
                }
            }

            Damage damage;
            if(!accurate)
                damage = Damage.createBlockedHit();
            else if(!checkStrength) {
                if(damageRange.getStart().equals(damageRange.getEndInclusive()))
                    damage = Damage.create(damageRange.getStart());
                else
                    damage = Damage.create(damageRange.getStart(), damageRange.getEndInclusive());
            } else {
                int baseDamage = context.isFighting(MonsterRace.COMBAT_DUMMY) ? getMaxHit() : CombatFormulas.generateHit(snapshot, targetSnapshot, attackType, maxMultiplier);
                damage = attacker.getCombat().modifyHitDamage(context, target, baseDamage);
                if(damageRange != null) {
                    damage.set(Math.max(damage.getValue(), damageRange.getStart()));
                    damage.set(Math.min(damage.getValue(), damageRange.getEndInclusive()));
                }
            }
            //Update total damage
            totalDamage += damage.getValue(); //The total damage this QueueableHit will deal, for calculating amount of experience to give the attacker.
            damage.flagAgents(attacker, target);
            damages[i] = damage;
        }
        return damages;
    }

    public boolean canBeQueued(){
        return target != null && target.isAlive() && target.canBeTargeted();
    }

    private boolean isSplash(){
        return !accurate && attackType == AttackType.MAGIC;
    }

    AttackStrategy<?> getCombatMethod() {
        return method;
    }

    boolean handleAfterHitEffects() {
        return handleAfterHitEffects;
    }

    public boolean isIgnorePoisonEffects() {
        return ignorePoisonEffects;
    }

    int getAndDecrementDelay() {
        return delay--;
    }

    public void extendDelay(int amountBy){
        delay += amountBy;
    }

    public Hit setHandleAfterHitEffects(boolean handleAfterHitEffects) {
        this.handleAfterHitEffects = handleAfterHitEffects;
        return this;
    }

    public void multiplyAccuracy(double multiplier){
        snapshot.getBonuses().setSecondaryAccuracyModifier(snap -> multiplier);
    }

    public void multiplyDefenceRoll(double multiplier){
        snapshot.getBonuses().setDefenceRollModifier(snap -> multiplier);
    }

    public void setNegateDamages(boolean negateDamages) {
        this.negateDamages = negateDamages;
    }

    public Agent getAttacker() {
        return attacker;
    }

    public Agent getTarget() {
        return target;
    }

    public Damage[] getDamages() {
        return damages;
    }

    public boolean isAccurate() {
        return accurate;
    }

    public int getTotalDamage() {
        return totalDamage;
    }

	public AttackType getAttackType() {
		return attackType;
	}

    public AttackContext getAttackDetails() {
        return context;
    }

    public Optional<Graphic> findGraphic(){
        return Optional.ofNullable(isAccurate() && (attackType != AttackType.MAGIC || totalDamage > 0) ? successGraphic : failedGraphic);
    }

    public Optional<Sound> findSound(){
        return Optional.ofNullable(isAccurate() ? successSound : failedSound);
    }

    public Optional<Consumer<Agent>> findConsumer(){
        return Optional.ofNullable(isAccurate() ? onSuccess : onFailed);
    }

    public double getRecoilPercentage() {
        return recoilPercentage;
    }

    public void setRecoilPercentage(double recoilPercentage) {
        this.recoilPercentage = recoilPercentage;
    }

    public int getRecoilExtraDamage() {
        return recoilExtraDamage;
    }

    public void setRecoilExtraDamage(int recoilExtraDamage) {
        this.recoilExtraDamage = recoilExtraDamage;
    }

    public int getTotalRecoilDamage(){
        return (int) (Math.ceil(totalDamage*recoilPercentage) +recoilExtraDamage);
    }

    public void skipAccuracyCheck() { this.checkAccuracy = false; }

    public void setCheckAccuracy(boolean checkAccuracy) {
        this.checkAccuracy = checkAccuracy;
    }

    public void setCheckStrength(boolean checkStrength) {
        this.checkStrength = checkStrength;
    }

    public int getMaxHit(){
        final CombatFormulaType formulaType = CombatFormulaType.getFormula(attackType);
        return (int) Math.floor(formulaType.calculateStrength(snapshot, targetSnapshot) * maxMultiplier);
    }

    public void setDamageRange(IntRange damageRange) {
        this.damageRange = damageRange;
    }

    public AttackContext getContext() {
        return context;
    }

    public void setAccurate(boolean accurate) {
        this.accurate = accurate;
    }

    public boolean isRewardingExperience() {
        return rewardingExperience;
    }

    public void setRewardingExperience(boolean rewardingExperience) {
        this.rewardingExperience = rewardingExperience;
    }

    public boolean isRetaliatedHit() {
        return retaliatedHit;
    }

    public boolean missed() {
        return !accurate;
    }
}
