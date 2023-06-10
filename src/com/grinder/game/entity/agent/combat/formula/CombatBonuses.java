package com.grinder.game.entity.agent.combat.formula;

import com.grinder.game.content.item.charging.impl.EtherBracelet;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerEquipment;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackStyle;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponEffectType;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.MagicSpellbook;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.util.oldgrinder.EquipSlot;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.grinder.game.entity.agent.combat.attack.AttackType.*;
import static com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType.*;
import static com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType.*;
import static com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType.POWERSHOT;
import static com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType.SNAPSHOT;
import static com.grinder.util.ItemID.*;

/**
 * This class contains all relevant bonuses that apply to an {@link Agent} during combat calculations.
 *
 * @see CombatFormulaType for implementations using the bonuses laid out in this class.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-17
 */
public final class CombatBonuses {

    /**
     * Create a new {@link CombatBonuses} instance for the specified {@link Agent}.
     *
     * @param agent     the {@link Agent} for whom the bonuses apply.
     * @param typeUsed  the {@link AttackType} used by the agent in the combat turn.
     * @return a new {@link CombatBonuses} instance.
     */
    public static CombatBonuses create(final Agent agent, AttackType typeUsed){
        if(agent instanceof Player)
            return new CombatBonuses(agent.prayersUsed(), agent.getCombat().getFightType(), agent.getCombat().styleUsed(), typeUsed, agent.getAsPlayer().getEquipment());
        else
            return new CombatBonuses(agent.prayersUsed(), typeUsed, agent.getAsNpc());
    }

    private final PrayerType[] prayersUsed;
    private final int stanceStrengthBonus;
    private final int stanceAccuracyBonus;
    private final int stanceDefenceBonus;
    private double armourStrengthBonus = 1.0D;
    private double armourAccuracyBonus = 1.0D;

    private Function<CombatSnapshot, Double> defenceRollModifier = details -> 1.0D;
    private Function<AttackContext, Double>  primaryAccuracyModifier = details -> 1.0D;
    private Function<CombatSnapshot, Double>  secondaryAccuracyModifier = details -> 1.0D;
    private BiFunction<Agent, CombatSnapshot, Double> tertiaryAccuracyModifier = (attacker, targetSnapshot) -> etherBraceletAccuracy(attacker, targetSnapshot);
    private Function<AttackContext, Double>  primarySpecialDamageBonus = details -> 1.0D;
    private Function<CombatSnapshot, Double>  secondarySpecialDamageBonus = snapshot -> 1.0D;
    private BiFunction<Agent, CombatSnapshot, Double>  tertiarySpecialDamageBonus = (agent, snapshot)  -> 1.0D;
    private Function<AttackContext, Double>  prayerStrengthAdjustment = details -> 1.0D;
    private Function<AttackContext, Double>  prayerAccuracyAdjustment = details -> 1.0D;
    private Function<AttackContext, Double>  magicStrengthBonus = details -> 1.0D;
    private Function<AttackContext, Double>  magicMaxHitMultiplier = details -> 1.0D;
    private Function<CombatSnapshot, OptionalInt> customMaxHit = snapshot -> OptionalInt.empty();

    private WeaponEffectType itemEffect = null;

    /**
     * Create a new {@link CombatBonuses} instance.
     *
     * @param prayersUsed   the {@link PrayerType}s activated during the combat turn.
     * @param styleUsed     the {@link AttackStyle} used in the combat turn.
     * @param typeUsed      the {@link AttackType} used in the combat turn.
     */
    private CombatBonuses(PrayerType[] prayersUsed, AttackStyle styleUsed, AttackType typeUsed) {
        this.prayersUsed = prayersUsed;
        this.stanceStrengthBonus = styleUsed.getStanceStrengthBonus(typeUsed);
        this.stanceAccuracyBonus = styleUsed.getStanceAccuracyBonus(typeUsed);
        this.stanceDefenceBonus = styleUsed.getStanceDefenceBonus();
    }

    /**
     * Create a new {@link CombatBonuses} instance for {@link NPC agents}.
     *
     * @param prayersUsed   the {@link PrayerType}s activated during the combat turn.
     * @param npc           the {@link NPC} who is the subject of the bonuses.
     */
    private CombatBonuses(PrayerType[] prayersUsed, AttackType typeUsed, NPC npc) {
        this.prayersUsed = prayersUsed;
        stanceStrengthBonus = 9;
        stanceAccuracyBonus = 9;
        stanceDefenceBonus = 9;
        customMaxHit = snapshot -> OptionalInt.of(npc.getMaxHit(typeUsed));
    }

    /**
     * Create a new {@link CombatBonuses} instance for {@link Player} agents.
     *
     * @param prayersUsed   the {@link PrayerType}s activated during the combat turn.
     * @param styleUsed     the {@link AttackStyle} used in the combat turn.
     * @param typeUsed      the {@link AttackType} used in the combat turn.
     * @param equipmentWorn the {@link Equipment} worn in the combat turn.
     */
    private CombatBonuses(PrayerType[] prayersUsed, WeaponFightType fightType, AttackStyle styleUsed, AttackType typeUsed, Equipment equipmentWorn) {
        this(prayersUsed, styleUsed, typeUsed);
        armourStrengthBonus = CombatModifier.INSTANCE.armourStrengthBonus(fightType, typeUsed, equipmentWorn);
        armourAccuracyBonus = CombatModifier.INSTANCE.armourAccuracyBonus(fightType, typeUsed, equipmentWorn);
        primarySpecialDamageBonus = CombatModifier.INSTANCE::primaryDamageMultiplier;
        primaryAccuracyModifier = CombatModifier.INSTANCE::primaryAccuracyMultiplier;
        secondarySpecialDamageBonus = CombatModifier.INSTANCE::secondaryDamageMultiplier;
        secondaryAccuracyModifier = CombatModifier.INSTANCE::secondaryAccuracyMultiplier;
        tertiarySpecialDamageBonus = CombatModifier.INSTANCE::tertiaryDamageMultiplier;
        tertiaryAccuracyModifier = CombatModifier.INSTANCE::tertiaryAccuracyMultiplier;
        prayerStrengthAdjustment = this::determinePrayerStrengthAdjustment;
        prayerAccuracyAdjustment = this::determinePrayerAccuracyAdjustment;
        itemEffect = WeaponEffectType.findEffect(equipmentWorn, typeUsed).orElse(null);
        if (typeUsed == MELEE)
            customMaxHit = determineMeleeMxHitFormula();
        else if(typeUsed == RANGED)
            customMaxHit = determineRangedMaxHitFormula(equipmentWorn);
        else if(typeUsed == MAGIC) {
            customMaxHit = determineMagicMaxHitFormula(equipmentWorn);
            magicMaxHitMultiplier = determineMagicMaxHitMultiplier(equipmentWorn);
            magicStrengthBonus = determineMagicStrengthBonus(equipmentWorn);
        }
    }

    @NotNull
    private Function<CombatSnapshot, OptionalInt> determineMeleeMxHitFormula() {
        return snapshot -> OptionalInt.empty();
    }

    @NotNull
    private Function<CombatSnapshot, OptionalInt> determineRangedMaxHitFormula(Equipment equipmentWorn) {
        return snapshot -> {
            if(snapshot.getContext().usedAny(SNAPSHOT, POWERSHOT, SOULSHOT, CHAINHIT) || equipmentWorn.contains(OGRE_BOW)){
                final int rangedLevel = snapshot.getRangedLevel();
                final double effectiveLevel = rangedLevel + 10;
                final double equipmentBonus = snapshot.getStats().getStrengthBonus(snapshot.getContext());
                int maximumHit = Math.toIntExact(Math.round(0.5 + (effectiveLevel * (equipmentBonus + 64.0) / 640.0)));
                if(equipmentWorn.contains(RUNE_THROWNAXE)
                        || (snapshot.getContext().isFightingPlayer()
                        && (equipmentWorn.containsAny(SNAPSHOT.getIdentifiers()))))
                    maximumHit++;
                return OptionalInt.of(maximumHit);
            }
            return OptionalInt.empty();
        };
    }

    @NotNull
    private Function<CombatSnapshot, OptionalInt> determineMagicMaxHitFormula(Equipment equipmentWorn) {
        return snapshot -> {
            final AttackContext context = snapshot.getContext();
            final double magicLevel = snapshot.getMagicLevel();
            int maxHit;
            if (context.used(MAGIC_DART) && equipmentWorn.contains(SLAYERS_STAFF_E_) && context.isFightingSlayerTask())
                maxHit = (int) (13 + Math.floor(magicLevel/6.0)); // If the Slayer's staff (e) is used while on task, its max hit is even higher.
            else if(context.used(MAGIC_DART))
                maxHit = (int) (10 + Math.floor(magicLevel/10.0));
            else if(context.used(CombatSpellType.TRIDENT_OF_THE_SEAS))
                maxHit = (int) (Math.floor(magicLevel/3.0) - 5);
            else if(context.used(CombatSpellType.TRIDENT_OF_THE_SWAMP))
                maxHit = (int) (Math.floor(magicLevel/3.0) - 2);
            else if(context.used(INVOCATE)){
                maxHit = 38;
                if (magicLevel >= 75)
                    maxHit = (int) (Math.floor((magicLevel - 75) / 2.0)) + 26;
            } else if(context.used(IMMOLATE)){
                maxHit = 50;
                if (magicLevel >= 75)
                    maxHit = (int) (Math.floor((magicLevel - 75) / 1.5)) + 38;
            } else
                maxHit = context.getFixedSpellMaxHit(equipmentWorn);
            if (maxHit == 0) return OptionalInt.empty();
            else return OptionalInt.of(maxHit);
        };
    }

    @NotNull
    private Function<AttackContext, Double> determineMagicMaxHitMultiplier(Equipment equipmentWorn) {
        return details -> {
            if (details.used(DEMON_AGONY)) {
                if (details.isFighting(MonsterRace.DEMON)) {
                    return 1.50;
                }
            }
            if (details.used(UNDEAD_BASH)) {
                if (details.isFighting(MonsterRace.UNDEAD)) {
                    return 1.35;
                }
            }

            return 1.0;
        };
    }

    @NotNull
    private Function<AttackContext, Double> determineMagicStrengthBonus(Equipment equipmentWorn) {
        return details -> {
            double stackedBonus = 1.0D;
            stackedBonus += (0.02 * EquipmentUtil.countMatches(equipmentWorn, "ancestral"));
            stackedBonus += (0.02 * EquipmentUtil.countMatches(equipmentWorn, "virtus"));
            if(EquipmentUtil.hasImbuedGodCape(equipmentWorn))
                stackedBonus += 0.02;
            if(equipmentWorn.contains(AHRIMS_STAFF) || equipmentWorn.contains(AHRIMS_STAFF_100) || equipmentWorn.contains(AHRIMS_STAFF_75) || equipmentWorn.contains(AHRIMS_STAFF_50) || equipmentWorn.contains(AHRIMS_STAFF_25))
                stackedBonus += 0.05;
            if(equipmentWorn.contains(15877))
                stackedBonus += 0.03;
            if(equipmentWorn.contains(15879))
                stackedBonus += 0.03;
            if(equipmentWorn.contains(15881))
                stackedBonus += 0.03;
            if(equipmentWorn.contains(KODAI_WAND))
                stackedBonus += 0.15;
            if(equipmentWorn.contains(STAFF_OF_BALANCE) || equipmentWorn.contains(STAFF_OF_LIGHT))
                stackedBonus += 0.15;
            if(equipmentWorn.contains(22647)) // Zuriels staff
                stackedBonus += 0.10;
            if(equipmentWorn.contains(ELDRITCH_NIGHTMARE_STAFF) || equipmentWorn.contains(VOLATILE_NIGHTMARE_STAFF) || equipmentWorn.contains(HARMONISED_NIGHTMARE_STAFF) || equipmentWorn.contains(NIGHTMARE_STAFF))
                stackedBonus += 0.15;
            if(equipmentWorn.contains(HOSIDIOUS_BANE_STAFF))
                stackedBonus += 0.08;
            else if(equipmentWorn.contains(SMOKE_BATTLESTAFF) && details.used(MagicSpellbook.NORMAL))
                stackedBonus += 0.10;
            else if(equipmentWorn.contains(STAFF_OF_THE_DEAD) || equipmentWorn.contains(TOXIC_STAFF_OF_THE_DEAD))
                stackedBonus += 0.15;
            if(equipmentWorn.containsAny(OCCULT_NECKLACE, OCCULT_NECKLACE_OR_))
                stackedBonus += 0.10;
            if(equipmentWorn.containsAny(TORMENTED_BRACELET, TORMENTED_BRACLET_OR, 15746, 15861))
                stackedBonus += 0.05;
            if(equipmentWorn.containsAny(15863))
                stackedBonus += 0.05;
            if(EquipmentUtil.isWearingEliteVoidSet(equipmentWorn, AttackType.MAGIC))
                stackedBonus += 0.025;
            if(EquipmentUtil.isWearingSuperiorVoidSet(equipmentWorn, AttackType.MAGIC))
                stackedBonus += 0.035;
            return stackedBonus;
        };
    }
    /**
     * The following prayer adjustments apply to the accuracy variable
     * in melee, magic and ranged combat calculations.
     *
     * @see #getPrayerMagicAdjustment() for the magic prayer adjustment.
     *
     * @return a double value that acts a multiplier
     */
    private double determinePrayerAccuracyAdjustment(AttackContext details){

        if(details.used(MELEE)){
            for(PrayerType prayerType : prayersUsed){
                switch (prayerType){
                    case CLARITY_OF_THOUGHT:return 1.05;
                    case IMPROVED_REFLEXES:return 1.10;
                    case INCREDIBLE_REFLEXES:
                    case CHIVALRY: return 1.15;
                    case PIETY:return 1.20;
                }
            }
        } else if(details.used(RANGED)){
            for(PrayerType prayerType : prayersUsed){
                switch (prayerType) {
                    case SHARP_EYE: return 1.05;
                    case HAWK_EYE: return 1.10;
                    case EAGLE_EYE: return 1.15;
                    case RIGOUR: return 1.20;
                }
            }
        } else if(details.used(MAGIC))
            return getPrayerMagicAdjustment();

        return 1.0;
    }

    /**
     * The following prayer adjustments apply to the strength variable
     * in melee and ranged combat calculations.
     *
     * @return a double value that acts a multiplier
     */
    private double determinePrayerStrengthAdjustment(AttackContext details){

        if(details.used(MELEE)){
            for(PrayerType prayerType : prayersUsed){
                if(prayerType == PrayerType.BURST_OF_STRENGTH) return 1.05;
                else if(prayerType == PrayerType.SUPERHUMAN_STRENGTH) return 1.10;
                else if(prayerType == PrayerType.ULTIMATE_STRENGTH) return 1.15;
                else if(prayerType == PrayerType.CHIVALRY) return 1.18;
                else if(prayerType == PrayerType.PIETY) return 1.25;
            }
        } else if(details.used(RANGED)){
            for(PrayerType prayerType : prayersUsed){
                if(prayerType == PrayerType.SHARP_EYE) return 1.05;
                else if(prayerType == PrayerType.HAWK_EYE) return 1.10;
                else if(prayerType == PrayerType.EAGLE_EYE) return 1.15;
                else if(prayerType == PrayerType.RIGOUR) return 1.23;
            }
        }

        return 1.0;
    }

    /**
     * The following prayer adjustments apply to the defence variable
     * in melee and ranged combat calculations.
     *
     * @return a double value that acts a multiplier
     */
    public double getPrayerDefenceAdjustment(){
        for(PrayerType prayerType : prayersUsed){
            switch (prayerType){
                case THICK_SKIN:return 1.05;
                case ROCK_SKIN:return 1.10;
                case STEEL_SKIN:return 1.15;
                case CHIVALRY: return 1.20;
                case PIETY:
                case RIGOUR:
                case AUGURY: return 1.25;
            }
        }
        return 1.0;
    }

    /**
     * The following prayer adjustments apply to the accuracy and defence variable
     * in magic combat calculations.
     *
     * @return a double value that acts a multiplier
     */
    public double getPrayerMagicAdjustment(){
        for(PrayerType prayerType : prayersUsed){
            switch (prayerType){ // Increased by 0.5 for all below
                case MYSTIC_WILL:return 1.10;
                case MYSTIC_LORE:return 1.15;
                case MYSTIC_MIGHT:return 1.20;
                case AUGURY: return 1.30;
            }
        }
        return 1.0;
    }

    public double getStanceStrengthBonus(){
        return stanceStrengthBonus;
    }

    public double getStanceAccuracyBonus(){
        return stanceAccuracyBonus;
    }

    public double getStanceDefenceBonus(){
        return stanceDefenceBonus;
    }

    public double getArmourStrengthBonus() {
        return armourStrengthBonus;
    }

    public double getArmourAccuracyBonus() {
        return armourAccuracyBonus;
    }

    public Function<CombatSnapshot, Double> getDefenceRollModifier() {
        return defenceRollModifier;
    }

    public Function<AttackContext, Double> getPrimaryAccuracyModifier() {
        return primaryAccuracyModifier;
    }

    public Function<CombatSnapshot, Double> getSecondaryAccuracyModifier() {
        return secondaryAccuracyModifier;
    }

    public BiFunction<Agent, CombatSnapshot, Double> getTertiaryAccuracyModifier() {
        return tertiaryAccuracyModifier;
    }

    public Function<AttackContext, Double> getPrimarySpecialDamageBonus() {
        return primarySpecialDamageBonus;
    }

    public Function<CombatSnapshot, Double> getSecondarySpecialDamageBonus() {
        return secondarySpecialDamageBonus;
    }

    public BiFunction<Agent, CombatSnapshot, Double> getTertiarySpecialDamageBonus() {
        return tertiarySpecialDamageBonus;
    }

    public Function<AttackContext, Double> getPrayerAccuracyAdjustment() {
        return prayerAccuracyAdjustment;
    }

    public Function<AttackContext, Double> getPrayerStrengthAdjustment() {
        return prayerStrengthAdjustment;
    }

    public Function<AttackContext, Double> getMagicStrengthBonus() {
        return magicStrengthBonus;
    }

    public Function<AttackContext, Double> getMagicMaxHitMultiplier() {
        return magicMaxHitMultiplier;
    }

    public Function<CombatSnapshot, OptionalInt> getCustomMaxHit() {
        return customMaxHit;
    }

    public WeaponEffectType getItemEffect() {
        return itemEffect;
    }

    public void setDefenceRollModifier(Function<CombatSnapshot, Double> defenceRollModifier) {
        this.defenceRollModifier = defenceRollModifier;
    }

    public void setSecondaryAccuracyModifier(Function<CombatSnapshot, Double> secondaryAccuracyModifier) {
        this.secondaryAccuracyModifier = secondaryAccuracyModifier;
    }

    private double etherBraceletAccuracy(Agent attacker, CombatSnapshot targetSnapshot) {
        final Agent target = targetSnapshot.getAgent();
        if(target.isPlayer() && AreaManager.inWilderness(target) && attacker.isNpc() && attacker.getAsNpc().fetchDefinition().getName().toLowerCase().contains("revenant")) {
            final Player playerTarget = target.getAsPlayer();
            if(playerTarget.getEquipment().contains(EtherBracelet.CHARGED)
                    && EtherBracelet.INSTANCE.getCharges(playerTarget) > 0) {
                EtherBracelet.INSTANCE.decrementCharges(playerTarget, playerTarget.getEquipment().get(EquipSlot.HANDS));
                return 0.0;
            }
        }
        return 1.0;
    }
}
