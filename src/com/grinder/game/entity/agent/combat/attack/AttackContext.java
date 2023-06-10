package com.grinder.game.entity.agent.combat.attack;

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.Combat;
import com.grinder.game.entity.agent.combat.PlayerCombat;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.Weapon;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponType;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.entity.agent.combat.misc.CombatEquipment;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.MagicSpellbook;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.oldgrinder.EquipSlot;
import com.grinder.util.timing.TimerKey;
import com.grinder.util.timing.TimerRepository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType.PROTECT_FROM_MELEE;
import static com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType.PROTECT_FROM_MISSILES;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-12
 */
public final class AttackContext {

    private final AttackType typeUsed;
    private final AttackStyle styleUsed;
    private final AttackStrategy<?> strategy;
    private final WeaponFightType fightTypeUsed;
    private final WeaponType weaponType;
    private final Weapon weapon;
    private final RangedWeapon rangedWeapon;
    private final Ammunition ammunitionUsed;
    private CombatSpell spellUsed;
    private CombatSpellType spellTypeUsed;
    private final PrayerType[] attackerPrayersUsed;
    private final PrayerType[] targetPrayersUsed;
    private final SpecialAttackType specialAttackUsed;
    private final boolean player;
    private final boolean fightingNpc;
    private final boolean fightingInWild;
    private final boolean fightingInMulti;
    private final boolean slayerTasked;
    private final boolean defilerSetEffectActivated;
    private final boolean ammunitionEffect;
    private final boolean shatterEffect;
    private final boolean ignorePrayer;
    private final List<MonsterRace> targetRaces;
    private final TimerRepository timerRepository;
    private final MagicSpellbook spellbookUsed;
    private final Equipment attackerEquipment;
    private final Equipment targetEquipment;
    private final int attackStat;
    private final int defenceStat;

    public AttackContext(Combat<?> combat, Agent target) {
        this(combat, target, combat.determineStrategy());
    }

    public AttackContext(Combat<?> combat, Agent target, AttackStrategy<?> strategy) {
        this(combat, target, strategy, strategy.type());
    }

    public AttackContext(Combat<?> combat, Agent target, AttackStrategy<?> strategy, AttackType type) {
        this(combat, target, strategy, type, false);
    }

    public AttackContext(Combat<?> combat, Agent target, AttackStrategy<?> strategy, AttackType type, boolean ignorePrayer) {
        this(combat, target, strategy, type, ignorePrayer, false, -1, -1);
    }

    public AttackContext(Combat<?> combat, Agent target, AttackStrategy<?> strategy, AttackType type,
                         boolean ignorePrayer,
                         boolean ignoreAmmunitionEffects,
                         int defenceStat,
                         int attackStat
    ) {
        this.strategy = strategy;
        this.ignorePrayer = ignorePrayer;
        this.attackStat = attackStat;
        this.defenceStat = defenceStat;
        typeUsed = type;
        if (combat.getActor() instanceof Player) {
            attackerEquipment = ((Player) combat.getActor()).getEquipment().copy();
        } else
            attackerEquipment = null;
        if (target instanceof Player) {
            targetEquipment = ((Player) target).getEquipment().copy();
        } else
            targetEquipment = null;
        weapon = combat.getWeapon();
        spellUsed = combat.getSpell();
        rangedWeapon = combat.getRangedWeapon();
        ammunitionUsed = combat.getAmmunition();
        ammunitionEffect = !ignoreAmmunitionEffects && ammunitionUsed != null && (Misc.random(95) <= ammunitionUsed.effectOccurrenceChance(target instanceof Player, true) || ammunitionUsed.handleZaryteSpec(combat.getActor()) || MonsterRace.Companion.isRace(target, MonsterRace.COMBAT_DUMMY));
        shatterEffect = combat.getActor().getAttributes().bool(Attribute.SHATTER_EFFECT);
        if (shatterEffect)
            combat.getActor().getAttributes().boolAttr(Attribute.SHATTER_EFFECT, false).reset();
        styleUsed = combat.styleUsed();
        fightTypeUsed = combat.getFightType();
        attackerPrayersUsed = combat.getActor().prayersUsed();
        targetPrayersUsed = target.prayersUsed();
        slayerTasked = combat instanceof PlayerCombat && SlayerManager.isFightingTaskedMonster(((PlayerCombat) combat).getActor());
        targetRaces = MonsterRace.Companion.getRacesFor(target);
        player = combat instanceof PlayerCombat;
        defilerSetEffectActivated = EquipmentUtil.isWearingVeracSet(combat.getActor()) && Misc.randomChance(25f);
        fightingNpc = target instanceof NPC;
        fightingInWild = AreaManager.inWilderness(target);
        fightingInMulti = AreaManager.inMulti(target);
        specialAttackUsed = combat instanceof PlayerCombat && combat.getActor().getAsPlayer().isSpecialActivated() ? combat.getActor().getAsPlayer().getSpecialAttackType() : null;
        spellTypeUsed = combat instanceof PlayerCombat ? ((PlayerCombat) combat).getCastSpellType() : null;
        spellbookUsed = combat instanceof PlayerCombat ? combat.getActor().getAsPlayer().getSpellbook() : MagicSpellbook.NORMAL;
        timerRepository = combat.getActor().getTimerRepository();
        weaponType = Optional.ofNullable(weapon)
                .map(Weapon::getId)
                .map(WeaponType.Companion::getWeaponTypeFor)
                .orElse(WeaponType.UNDEFINED);
    }

    public boolean used(final int itemId) {
        return this.attackerEquipment != null && attackerEquipment.contains(itemId);
    }

    public boolean used(final PrayerType prayerType) {
        return Stream.of(attackerPrayersUsed).anyMatch(prayerTypeUsed -> prayerType == prayerTypeUsed);
    }

    public boolean used(final WeaponInterface weapon) {
        return this.weapon != null && this.weapon.uses(weapon);
    }

    public boolean used(final RangedWeapon weapon) {
        return typeUsed == AttackType.RANGED && rangedWeapon == weapon;
    }

    public boolean used(final Ammunition ammunition) {
        return typeUsed == AttackType.RANGED && ammunitionUsed == ammunition;
    }

    public boolean used(final CombatSpellType spell) {
        return typeUsed == AttackType.MAGIC && spell.getSpell() == spellUsed;
    }

    public boolean used(final AttackType type) {
        return typeUsed == type;
    }

    public boolean used(final AttackStyle style) {
        return styleUsed == style;
    }

    public boolean used(final WeaponFightType fightType) {
        return fightTypeUsed == fightType;
    }

    public boolean used(final WeaponType weaponType) {
        return this.weaponType == weaponType;
    }

    public boolean used(final SpecialAttackType specialAttackType) {
        return specialAttackUsed == specialAttackType;
    }

    public boolean used(final MagicSpellbook spellbook) {
        return spellbookUsed == spellbook;
    }

    public boolean usedAny(final PrayerType... prayerTypes) {
        return Stream.of(prayerTypes).anyMatch(this::used);
    }

    public boolean usedAny(final WeaponInterface... weapons) {
        return Stream.of(weapons).anyMatch(this::used);
    }

    public boolean usedAny(final RangedWeapon... weapons) {
        return Stream.of(weapons).anyMatch(this::used);
    }

    public boolean usedAny(final Ammunition... ammunitions) {
        return Stream.of(ammunitions).anyMatch(this::used);
    }

    public boolean usedAny(final int... itemIds) {
        return IntStream.of(itemIds).anyMatch(this::used);
    }

    public boolean usedAny(final CombatSpellType... spells) {
        return Stream.of(spells).anyMatch(this::used);
    }

    public boolean usedAny(final AttackStyle... styles) {
        return Stream.of(styles).anyMatch(this::used);
    }

    public boolean usedAny(final AttackType... types) {
        return Stream.of(types).anyMatch(this::used);
    }

    public boolean usedAny(final WeaponFightType... fightTypes) {
        return Stream.of(fightTypes).anyMatch(this::used);
    }

    public boolean usedAny(final WeaponType... weaponTypes) {
        return Stream.of(weaponTypes).anyMatch(this::used);
    }

    public boolean usedAny(final SpecialAttackType... specialAttackTypes) {
        return Stream.of(specialAttackTypes).anyMatch(this::used);
    }

    public boolean usedAny(final MagicSpellbook... spellbooks) {
        return Stream.of(spellbooks).anyMatch(this::used);
    }

    public AttackStrategy<?> getStrategy() {
        return strategy;
    }

    public boolean isFightingSlayerTask() {
        return slayerTasked || isFighting(MonsterRace.UNDEAD_COMBAT_DUMMY);
    }

    public boolean isFightingPlayer() {
        return !fightingNpc;
    }

    public boolean isFightingNpc() {
        return fightingNpc;
    }

    public boolean isFightingInWilderness() {
        return fightingInWild;
    }

    public boolean isFightingInMulti() {
        return fightingInMulti;
    }

    public boolean isFightingAny(MonsterRace... races) {
        for (MonsterRace race : races) {
            if (targetRaces.contains(race))
                return true;
        }
        return false;
    }

    public boolean isFighting(final MonsterRace race) {
        return targetRaces.contains(race);
    }

    public boolean isPlayer() {
        return player;
    }

    public boolean applyProtectivePVPDamageReduction() {
        if (!fightingNpc) {
            for (PrayerType prayerType : targetPrayersUsed) {
                if ((used(AttackType.MELEE) && prayerType == PROTECT_FROM_MELEE)
                        || (used(AttackType.RANGED) && prayerType == PROTECT_FROM_MISSILES)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean applyProtectivePVMDamageReduction() {
        if (fightingNpc) {
            for (PrayerType prayerType : targetPrayersUsed) {
                if ((used(AttackType.MELEE) && prayerType == PROTECT_FROM_MELEE)
                        || (used(AttackType.RANGED) && prayerType == PROTECT_FROM_MISSILES)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getFixedSpellMaxHit(Equipment equipment) {
        if (used(AttackType.MAGIC)) {
            if (spellTypeUsed != null) {
                final int baseMaxHit = spellTypeUsed.getBaseMaxHit();
                if (spellTypeUsed.isBoltSpell() && equipment.containsAtSlot(EquipSlot.HANDS, ItemID.CHAOS_GAUNTLETS))
                    return baseMaxHit + 3;
                else if(spellTypeUsed.isGodSpell() && timerRepository.has(TimerKey.CHARGE_SPELL_EFFECT))
                    return baseMaxHit + 10;
                else
                    return baseMaxHit;
            }
        }
        return 0;
    }

    public void setSpell(CombatSpellType type) {
        this.spellTypeUsed = type;
        this.spellUsed = type.getSpell();
    }

    public OptionalInt attackStat() {
        return attackStat == -1
                ? OptionalInt.empty()
                : OptionalInt.of(attackStat);
    }

    public OptionalInt defenceStat() {
        return defenceStat == -1
                ? OptionalInt.empty()
                : OptionalInt.of(defenceStat);
    }

    public RangedWeapon getRangedWeapon() {
        return rangedWeapon;
    }

    public WeaponFightType getFightType() {
        return fightTypeUsed;
    }

    public AttackType getTypeUsed() {
        return typeUsed;
    }

    public Equipment getAttackerEquipment() {
        return attackerEquipment;
    }

    public Equipment getTargetEquipment() {
        return targetEquipment;
    }

    public Ammunition getAmmunitionUsed() {
        return ammunitionUsed;
    }

    public boolean isIgnorePrayer() {
        return ignorePrayer;
    }

    public boolean hasAmmunitionEffect() {
        return ammunitionEffect;
    }

    public boolean isShatterEffect() {
        return shatterEffect;
    }

    public boolean isDefilerSetEffectActivated() {
        return defilerSetEffectActivated;
    }

    public boolean skipAccuracy() {
        return defilerSetEffectActivated || usedAny(SpecialAttackType.GOLDEN_WINDS);
    }
}
