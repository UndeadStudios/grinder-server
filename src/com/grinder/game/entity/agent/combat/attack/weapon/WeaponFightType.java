package com.grinder.game.entity.agent.combat.attack.weapon;

import com.grinder.game.entity.agent.combat.attack.AttackMode;
import com.grinder.game.entity.agent.combat.attack.AttackStyle;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.model.sound.Sounds;

import java.util.HashSet;
import java.util.Set;

/**
 * A collection of constants that each represent a different fighting type.
 *
 * @author lare96
 */
public enum WeaponFightType {

    STAFF_BASH(401, 2555, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    STAFF_POUND(406, 2555, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    STAFF_FOCUS(406, 2555, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),

    HUNTING_KNIFE_POUND(7328, 2548, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    HUNTING_KNIFE_PUMMEL(7328, 2548, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    HUNTING_KNIFE_BLOCK(7328, 2548, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),


    WARHAMMER_POUND(401, 2498, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    WARHAMMER_PUMMEL(401, 2497, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    WARHAMMER_BLOCK(401, 2498, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),

    TORAG_WARHAMMER_POUND(2068, 1330, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    TORAG_WARHAMMER_PUMMEL(2068, 1330, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    TORAG_WARHAMMER_BLOCK(2068, 1330, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),

    BIRTHDAY_CAKE_POUND(7275, 2518, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    BIRTHDAY_CAKE_PUMMEL(7275, 2518, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    BIRTHDAY_CAKE_BLOCK(7275, 2518, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),

    BIRTHDAY_BALLOON_POUND(7541, 2257, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    BIRTHDAY_BALLOON_PUMMEL(7541, 2257, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    BIRTHDAY_BALLOON_BLOCK(7541, 2257, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),

    SHADOW_TWOHANDEDSWORD_CHOP(407, 2503, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    SHADOW_TWOHANDEDSWORD_SLASH(407, 2503, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    SHADOW_TWOHANDEDSWORD_SMASH(406, 2502, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    SHADOW_TWOHANDEDSWORD_BLOCK(407, 2503, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),

    BRINESABRE_CHOP(390, 3551, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    BRINESABRE_SLASH(390, 3551, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    BRINESABRE_LUNGE(386, 3552, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    BRINESABRE_BLOCK(390, 3551, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),

    BARRELCHEST_POUND(5865, 3454, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    BARRELCHEST_PUMMEL(5865, 3454, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    BARRELCHEST_BLOCK(5865, 3454, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    WARHAMMER_CASKET_POUND(401, -1, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    WARHAMMER_CASKET_PUMMEL(401, -1, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    WARHAMMER_CASKET_BLOCK(401, -1, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    MAUL_POUND(2661, 2520, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    MAUL_PUMMEL(2661, 2520, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    MAUL_BLOCK(2661, 2520, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    TENDERISER_POUND(401, 2567, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    TENDERISER_PUMMEL(401, 2567, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    TENDERISER_BLOCK(401, 2567, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    ELDER_MAUL_POUND(7516, 2520, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    ELDER_MAUL_PUMMEL(7516, 2520, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    ELDER_MAUL_BLOCK(7516, 2520, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    GRANITE_MAUL_POUND(1665, 2714, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    GRANITE_MAUL_PUMMEL(1665, 2714, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    GRANITE_MAUL_BLOCK(1665, 2714, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    SCYTHE_REAP(414, 2562, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    SCYTHE_CHOP(382, 2562, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    SCYTHE_JAB(2066, 2524, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.CONTROLLED),
    SCYTHE_BLOCK(382, 2562, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    SCYTHE_VITUR_REAP(8056, 2524, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    SCYTHE_VITUR_CHOP(8056, 2524, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    SCYTHE_VITUR_JAB(8056, 2524, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.CONTROLLED),
    SCYTHE_VITUR_BLOCK(8056, 2524, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    BATTLEAXE_CHOP(395, 2498, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    BATTLEAXE_HACK(395, 2498, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    BATTLEAXE_SMASH(401, 2497, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    BATTLEAXE_BLOCK(395, 2498, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    LEAF_BLADED_BATTLEAXE_CHOP(7004, 2498, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    LEAF_BLADED_BATTLEAXE_HACK(7004, 2498, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    LEAF_BLADED_BATTLEAXE_SMASH(3852, 2497, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    LEAF_BLADED_BATTLEAXE_BLOCK(7004, 2498, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    GREATAXE_CHOP(2062, 1321, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    GREATAXE_HACK(2062, 1321, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    GREATAXE_SMASH(2066, 1316, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    GREATAXE_BLOCK(2062, 1321, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    CROSSBOW_ACCURATE(7552, 2695, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    CROSSBOW_RAPID(7552, 2695, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    CROSSBOW_LONGRANGE(7552, 2695, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),
    DOGRESHUUN_ACCURATE(7552, 1081, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    DOGRESHUUN_RAPID(7552, 1081, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    DOGRESHUUN_LONGRANGE(7552, 1081, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),

    KARILS_CROSSBOW_ACCURATE(2075, 2695, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    KARILS_CROSSBOW_RAPID(2075, 2695, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    KARILS_CROSSBOW_LONGRANGE(2075, 2695, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),

    BALLISTA_ACCURATE(7218, 2699, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    BALLISTA_RAPID(7218, 2699, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    BALLISTA_LONGRANGE(7218, 2699, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),
    
    BLOWPIPE_ACCURATE(5061, 2696, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    BLOWPIPE_RAPID(5061, 2696, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    BLOWPIPE_LONGRANGE(5061, 2696, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),

    ABYSSAL_BLUDGEON_CHOP(7054, 1309, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    ABYSSAL_BLUDGEON_SLASH(7054, 1309, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    ABYSSAL_BLUDGEON_SMASH(7054, 1316, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    ABYSSAL_BLUDGEON_BLOCK(7054, 1309, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),

    /*
    //BOFA
    BOFA_ACCURATE(426, 1352, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    BOFA_RAPID(426, 1352, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    BOFA_LONGRANGE(426, 1352, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),
    */


    // Add weapon sounds

    SHORTBOW_ACCURATE(426, 2693, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    SHORTBOW_RAPID(426, 2693, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    SHORTBOW_LONGRANGE(426, 2693, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),
    CRYSTALBOW_ACCURATE(426, 1352, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    CRYSTALBOW_RAPID(426, 1352, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    CRYSTALBOW_LONGRANGE(426, 1352, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),
    LONGBOW_ACCURATE(426, 2695, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    LONGBOW_RAPID(426, 2695, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    LONGBOW_LONGRANGE(426, 2695, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),
    DAGGER_STAB(400, 2549, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.ACCURATE),
    DAGGER_LUNGE(386, 2501, 43, 1, EquipmentBonuses.ATTACK_STAB, AttackStyle.AGGRESSIVE),
    DAGGER_SLASH(377, 2548, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.AGGRESSIVE),
    DAGGER_BLOCK(400, 2549, 43, 3, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    BONE_DAGGER_STAB(400, 2549, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.ACCURATE),
    BONE_DAGGER_LUNGE(400, 2549, 43, 1, EquipmentBonuses.ATTACK_STAB, AttackStyle.AGGRESSIVE),
    BONE_DAGGER_SLASH(377, 2548, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.AGGRESSIVE),
    BONE_DAGGER_BLOCK(400, 2549, 43, 3, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    DRAGON_DAGGER_STAB(376, 2549, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.ACCURATE),
    DRAGON_DAGGER_LUNGE(376, 2549, 43, 1, EquipmentBonuses.ATTACK_STAB, AttackStyle.AGGRESSIVE),
    DRAGON_DAGGER_SLASH(377, 2548, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.AGGRESSIVE),
    DRAGON_DAGGER_BLOCK(376, 2549, 43, 3, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    SWORD_STAB(386, 2549, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.ACCURATE),
    SWORD_LUNGE(386, 2549, 43, 1, EquipmentBonuses.ATTACK_STAB, AttackStyle.AGGRESSIVE),
    SWORD_SLASH(390, 2548, 43, 2, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    SWORD_BLOCK(386, 2549, 43, 3, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    SCIMITAR_CHOP(390, 2500, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    SARADOMIN_SWORD_CHOP(7046, 3847, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    SARADOMIN_SWORD_SLASH(7045, 3847, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    SARADOMIN_SWORD_SMASH(7054, 3846, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    SARADOMIN_SWORD_BLOCK(7055, 3847, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    SCIMITAR_SLASH(390, 2500, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    SCIMITAR_LUNGE(412, 2501, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    SCIMITAR_BLOCK(390, 2500, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    LONGSWORD_CHOP(390, 2500, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    LONGSWORD_SLASH(390, 2500, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    LONGSWORD_LUNGE(412, 2501, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    LONGSWORD_BLOCK(390, 2500, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    RAPIER_STAB(8145, 2501, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.ACCURATE),
    RAPIER_LUNGE(8145, 2501, 43, 1, EquipmentBonuses.ATTACK_STAB, AttackStyle.AGGRESSIVE),
    RAPIER_SLASH(390, 2500, 43, 2, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    RAPIER_BLOCK(8145, 2501, 43, 3, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    MACE_POUND(401, 2508, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    MACE_PUMMEL(401, 2508, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    MACE_SPIKE(412, 2509, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    MACE_BLOCK(401, 2508, 43, 3, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    INQ_MACE_POUND(4503, 2508, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    INQ_MACE_PUMMEL(4503, 2508, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    INQ_MACE_SPIKE(400, 2509, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    INQ_MACE_BLOCK(4503, 2508, 43, 3, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    KNIFE_ACCURATE(7617, 2696, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    KNIFE_RAPID(7617, 2696, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    KNIFE_LONGRANGE(7617, 2696, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),
    OBBY_RING_ACCURATE(2614, 2706, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    OBBY_RING_RAPID(2614, 2706, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    OBBY_RING_LONGRANGE(2614, 2706, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),
    SPEAR_LUNGE(2080, 2562, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    SPEAR_SWIPE(2081, 2556, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.CONTROLLED),
    SPEAR_POUND(2082, 2556, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.CONTROLLED),
    SPEAR_BLOCK(2080, 2556, 43, 3, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    DRAGON_LANCE_LUNGE(8288, 2562, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    DRAGON_LANCE_SWIPE(8288, 2562, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.CONTROLLED),
    DRAGON_LANCE_POUND(8289, 2524, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.CONTROLLED),
    DRAGON_LANCE_BLOCK(8290, 2555, 43, 3, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    TWOHANDEDSWORD_CHOP(407, 2503, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    TWOHANDEDSWORD_SLASH(407, 2503, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    TWOHANDEDSWORD_SMASH(406, 2502, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    TWOHANDEDSWORD_BLOCK(407, 2503, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    GODSWORD_CHOP(7046, 3847, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    GODSWORD_SLASH(7045, 3847, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    GODSWORD_SMASH(7054, 3846, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    GODSWORD_BLOCK(7055, 3847, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    VERACS_FLAIL_POUND(2062, 1323, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    VERACS_FLAIL_PUMMEL(2062, 1323, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    VERACS_FLAIL_SPIKE(2062, 1324, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    VERACS_FLAIL_BLOCK(2062, 1323, 43, 3, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    VIGGORA_POUND(245, 2379, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    VIGGORA_PUMMEL(245, 2379, 43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    VIGGORA_SPIKE(245, 2379, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    VIGGORA_BLOCK(245, 2379, 43, 3, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    PICKAXE_SPIKE(401, 2498, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.ACCURATE),
    PICKAXE_IMPALE(401, 2498, 43, 1, EquipmentBonuses.ATTACK_STAB, AttackStyle.AGGRESSIVE),
    PICKAXE_SMASH(401, 2497, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    PICKAXE_BLOCK(400, 2498, 43, 3, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    CLAWS_CHOP(393, 2517, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    CLAWS_SLASH(393, 2517, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    CLAWS_LUNGE(393, 2517, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    CLAWS_BLOCK(393, 2517, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    BOXING_CHOP(3678, 1330, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    BOXING_SLASH(3678, 1330, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    BOXING_LUNGE(3678, 1330, 43, 2, EquipmentBonuses.ATTACK_SLASH, AttackStyle.CONTROLLED),
    BOXING_BLOCK(3678, 1330, 43, 3, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    HALBERD_JAB(428, 2562, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    HALBERD_SWIPE(440, 2524, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    HALBERD_FEND(428, 2562, 43, 2, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    HASTA_LUNGE(428, 2562, 43, 0, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    HASTA_SWIPE(440, 2556, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.CONTROLLED),
    HASTA_POUND(429, 2555, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.CONTROLLED),
    HASTA_BLOCK(428, 2562, 43, 3, EquipmentBonuses.ATTACK_STAB, AttackStyle.DEFENSIVE),
    UNARMED_PUNCH(422, 2566, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    UNARMED_KICK(423,2565,  43, 1, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    UNARMED_BLOCK(422, 2566, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE),
    WHIP_FLICK(1658, 2720, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.ACCURATE),
    WHIP_LASH(1658, 2720, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.CONTROLLED),
    WHIP_DEFLECT(1658, 2720, 43, 2, EquipmentBonuses.ATTACK_SLASH, AttackStyle.DEFENSIVE),
    PIMPZ_WHIP_FLICK(1658, 2720, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    PIMPZ_WHIP_LASH(1658, 2720, 43, 1, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    PIMPZ_WHIP_DEFLECT(1658, 2720, 43, 2, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    THROWNAXE_ACCURATE(7617, 2706, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    THROWNAXE_RAPID(7617, 2706, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    THROWNAXE_LONGRANGE(7617, 2706, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),

    CHIN_SHORT(7618, Sounds.CHINCHOMPA_ATK_SOUND, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    CHIN_MED(7618, Sounds.CHINCHOMPA_ATK_SOUND, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    CHIN_LONG(7618, Sounds.CHINCHOMPA_ATK_SOUND, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),

    LIZARD_SCORCH(-1, Sounds.LIZARD_SCORCH, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    LIZARD_FLARE(-1, Sounds.LIZARD_FLARE, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    LIZARD_BLAZE(-1, Sounds.LIZARD_BLAZE, 43, 2, EquipmentBonuses.ATTACK_MAGIC, AttackStyle.DEFENSIVE),

    SALAMANDER_SCORCH(-1, Sounds.SALAMANDER_SCORCH, 43, 0, EquipmentBonuses.ATTACK_SLASH, AttackStyle.AGGRESSIVE),
    SALAMANDER_FLARE(-1, Sounds.SALAMANDER_FLARE, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    SALAMANDER_BLAZE(-1, Sounds.SALAMANDER_BLAZE, 43, 2, EquipmentBonuses.ATTACK_MAGIC, AttackStyle.DEFENSIVE),

    DART_ACCURATE(7554, 2696, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    DART_RAPID(7554, 2696, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    DART_LONGRANGE(7554, 2696, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),

    JAVELIN_ACCURATE(7617, 2696, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    JAVELIN_RAPID(7617, 2696, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    JAVELIN_LONGRANGE(7617, 2696, 43, 3, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),

    DARKBOW_ACCURATE(426, 3734, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    DARKBOW_RAPID(426, 3734, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    DARKBOW_LONGRANGE(426, 3734, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),

    DRAGON_HUNTER_BOW_ACCURATE(426, 2695, 43, 0, EquipmentBonuses.ATTACK_RANGE, AttackStyle.ACCURATE),
    DRAGON_HUNTER_BOW_RAPID(426, 2695, 43, 1, EquipmentBonuses.ATTACK_RANGE, AttackStyle.AGGRESSIVE),
    DRAGON_HUNTER_BOW_LONGRANGE(426, 2695, 43, 2, EquipmentBonuses.ATTACK_RANGE, AttackStyle.DEFENSIVE),


    CANE_POUND(4505, 2696, 43, 3, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.ACCURATE),
    CANE_PUMMEL(4505, 3734, 43, 0, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.AGGRESSIVE),
    CANE_SPIKE(4503, 3734, 43, 1, EquipmentBonuses.ATTACK_STAB, AttackStyle.CONTROLLED),
    CANE_BLOCK(4505, 3734, 43, 2, EquipmentBonuses.ATTACK_CRUSH, AttackStyle.DEFENSIVE);

    public final static WeaponFightType[] OFFENSIVE_CRUSH;
    public final static WeaponFightType[] OFFENSIVE_STAB;

    static {

        final Set<WeaponFightType> offensiveCrush = new HashSet<>();
        final Set<WeaponFightType> offensiveStab = new HashSet<>();

        for(final WeaponFightType weaponFightType : values()){

            if(weaponFightType.bonusType == EquipmentBonuses.ATTACK_CRUSH)
                offensiveCrush.add(weaponFightType);

            if(weaponFightType.bonusType == EquipmentBonuses.ATTACK_STAB)
                offensiveStab.add(weaponFightType);

        }

        OFFENSIVE_CRUSH = offensiveCrush.toArray(new WeaponFightType[]{});
        OFFENSIVE_STAB = offensiveStab.toArray(new WeaponFightType[]{});
    }

    private final int animation;
    private final int sound;
    private final int parentId;
    private final int childId;
    private final int bonusType;
    private final AttackStyle style;

    /**
     * Create a new {@link WeaponFightType}.
     *
     * @param animation  the animation this fight type holds.
     * @param sound      the attack sound of this weapon.
     * @param parentId   the parent config id.
     * @param childId    the child config id.
     * @param bonusType  the bonus type.
     * @param style      the fighting style.
     */
    WeaponFightType(int animation, int sound, int parentId, int childId, int bonusType, AttackStyle style) {
        this.animation = animation;
        this.sound = sound;
        this.parentId = parentId;
        this.childId = childId;
        this.bonusType = bonusType;
        this.style = style;
    }

    /**
     * Gets the animation this fight type holds.
     *
     * @return the animation.
     */
    public int getAnimation() {
        return animation;
    }
    
    /**
     * Gets the sound this fight type holds.
     *
     * @return the animation.
     */
    public int getSound() {
        return sound;
    }

    /**
     * Gets the parent config id.
     *
     * @return the parent id.
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * Gets the child config id.
     *
     * @return the child id.
     */
    public int getChildId() {
        return childId;
    }

    /**
     * Gets the bonus type.
     *
     * @return the bonus type.
     */
    public int getBonusType() {
        return bonusType;
    }

    /**
     * Gets the fighting style.
     *
     * @return the fighting style.
     */
    public AttackStyle getStyle() {
        return style;
    }

    /**
     * Determines the corresponding bonus for this fight type.
     *
     * @return the corresponding bonus for this fight type.
     */
    public int getCorrespondingBonus() {
        switch (bonusType) {
            case EquipmentBonuses.ATTACK_CRUSH:
                return EquipmentBonuses.DEFENCE_CRUSH;
            case EquipmentBonuses.ATTACK_MAGIC:
                return EquipmentBonuses.DEFENCE_MAGIC;
            case EquipmentBonuses.ATTACK_RANGE:
                return EquipmentBonuses.DEFENCE_RANGE;
            case EquipmentBonuses.ATTACK_SLASH:
                return EquipmentBonuses.DEFENCE_SLASH;
            case EquipmentBonuses.ATTACK_STAB:
                return EquipmentBonuses.DEFENCE_STAB;
            default:
                return EquipmentBonuses.DEFENCE_CRUSH;
        }
    }

    public AttackMode getMode(){
        final AttackMode[] values = AttackMode.values();
        if(bonusType < 0 || bonusType >= values.length) {
            System.err.println("WeaponFightType: Invalid bonus type {"+bonusType+"} for {"+name()+"}!");
            return AttackMode.STAB;
        }
        return values[bonusType];
    }
    
}