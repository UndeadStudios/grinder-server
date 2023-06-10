package com.grinder.game.entity.agent.combat.attack.special;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.special.magic.*;
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.special.melee.*;
import com.grinder.game.entity.agent.combat.attack.special.ranged.*;
import com.grinder.game.entity.agent.combat.attack.weapon.Weapon;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.RestoreSpecialAttackTask;
import com.grinder.util.ItemID;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.STAFF;
import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.CROSSBOW;
import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.ELDER_MAUL;
import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.HALBERD;
import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.MACE;
import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.SHORTBOW;
import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.SPEAR;
import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.WARHAMMER;
import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.*;
import static com.grinder.util.ItemID.*;

/**
 * Holds constants that hold data for all of the special attacks that can be
 * used.
 *
 * 1214 d2h spec gfx
 *
 * @author lare96
 */
public enum SpecialAttackType {
    // Melee
    ENERGY_DRAIN(new int[]{4151, 12773, 12774, 15352, 15353, 15354, 15355, 15356, 15357, 15370, 20405, 15720, 15751}, 50, new AbyssalWhipSpecialAttack(), WHIP),
    BINDING_TENTACLE(new int[]{12006}, 50, new AbyssalTentacleSpecialAttack(), WHIP),
    DRAGON_TENTACLE(new int[]{15155, 15722}, 100, new DragonWhipSpecialAttack(), WHIP),
    GOLDEN_WINDS(new int[]{15156}, 50, new GoldWhipSpecialAttack(), WHIP),
    FREEZE(new int[]{15153}, 75, new IndigoWhipSpecialAttack(), WHIP),
    ENERGY_SMASH(new int[]{15154}, 50, new PimpzWhipSpecialAttack(), WeaponInterface.PIMPZ_WHIP),
    IMPALER(new int[]{15158}, 100, new WhiteWhipSpecialAttack(), WHIP),
    FEAR(new int[]{15164}, 100, new RoseWhipSpecialAttack(), WHIP),
    SUDDEN_DRAINAGE(new int[]{15157}, 100, new UnknownWhipSpecialAttack(), WHIP),
    MOTHER_NATURE(new int[]{15163}, 100, new ForestWhipSpecialAttack(), WHIP),
    BANANA_WHIP(new int[]{15165}, 100, new BananaWhipSpecialAttack(), WHIP),
    // TODO: BULWARK
    // TODO: MORRIGANS JAVELIN
    POWER_OF_DEATH(new int[] {11791, 12904, STAFF_OF_LIGHT, STAFF_OF_BALANCE}, 100, NonAttackSpecialAttacks.staffOfTheDeadEffect(), BLADED_STAFF),
    BACKSTAB(new int[]{8872, 8874, 8876, 8878}, 75, new BoneDaggerSpecialAttack(), WeaponInterface.BONE_DAGGER),
    SUNDER(new int[]{10887, 15237}, 50, new BarrelchestAnchorSpecialAttack(), WeaponInterface.BARRELCHEST_ANCHOR),
    SEVER(new int[]{4587, 15345, 15346, 15347, 15348, 15349, 15350, 15351, 20000}, 55, new DragonScimitarSpecialAttack(), SCIMITAR),
    CLEAVE(new int[]{1305, 15306, 15307, 15308, 15309}, 25, new DragonLongswordSpecialAttack(), LONGSWORD),
    WILD_STAB(new int[]{DRAGON_SWORD, 15717}, 40, new DragonSwordSpecialAttack(), SWORD),
    SHATTER(new int[]{1434}, 25, new DragonMaceSpecialAttack(), MACE),
    FAVOUR_OF_THE_WAR_GOD(new int[]{11061}, 100, new AncientMaceSpecialAttack(), MACE),
    SMASH(new int[]{13576, 26710}, 50, new DragonWarhammerSpecialAttack(), WARHAMMER),
    DRAGON_PICKAXE(new int[]{ItemID.DRAGON_PICKAXE, DRAGON_PICKAXE_2, DRAGON_PICKAXE_3, INFERNAL_PICKAXE, INFERNAL_PICKAXE_UNCHARGED_, _3RD_AGE_PICKAXE, 23680, 23682 }, 100, NonAttackSpecialAttacks.dragonPickaxeEffect(), PICKAXE),
    DRAGON_AXE(new int[]{ItemID.DRAGON_AXE, INFERNAL_AXE, INFERNAL_AXE_UNCHARGED_, 23673, 23675, }, 100, NonAttackSpecialAttacks.dragonAxeEffect(), BATTLEAXE),
    DRAGON_BATTLEAXE(new int[]{ItemID.DRAGON_BATTLEAXE, DRAGON_BATTLEAXE_2}, 100, NonAttackSpecialAttacks.dragonBattleAxeEffect(), BATTLEAXE),
    EXCALIBUR(new int[]{ItemID.EXCALIBUR}, 100, NonAttackSpecialAttacks.excaliburEffect(), LONGSWORD),
    WEAKEN(new int[]{6746, 15716}, 100, new DarkLightSpecialAttack(), LONGSWORD),
    ARCLIGHT(new int[]{19675}, 100, new DarkLightSpecialAttack(), WeaponInterface.ARCLIGHT),
    SARADOMINS_LIGHNING(new int[]{11838, 12808, ItemID.SARADOMINS_BLESSED_SWORD}, 100, new SaradominSwordSpecialAttack(), WeaponInterface.SARADOMIN_SWORD),
    THE_JUDGEMENT(new int[]{11802, 15166, 20368, 20593}, 50, new ArmadylGodswordSpecialAttack(), GODSWORD),
    FLAME_OF_THE_GODS(new int[]{15160}, 50, new DragonGodswordSpecialAttack(), GODSWORD),
    HEALING_BLADE(new int[]{11806, 20372}, 50, new SaradominGodswordSpecialAttack(), GODSWORD),
    WARSTRIKE(new int[]{11804, 20370}, 50, new BandosGodswordSpecialAttack(), GODSWORD),
    ICE_CLEAVE(new int[]{11808, 20374}, 50, new ZamorakGodswordSpecialAttack(), GODSWORD),
    BLOOD_SACRIFICE(new int[]{26233}, 50, new AncientGodswordSpecialAttack(), GODSWORD),

    PENANCE(new int[]{13263}, 50, new AbyssalBludgeonSpecialAttack(), WeaponInterface.ABYSSAL_BLUDGEON),

    // Multiple hits
    SWEEP(new int[]{DRAGON_HALBERD,
            23987, // crystal halberd
            CRYSTAL_HALBERD_1_10, CRYSTAL_HALBERD_1_10_I_,
            CRYSTAL_HALBERD_2_10, CRYSTAL_HALBERD_2_10_I_,
            CRYSTAL_HALBERD_3_10, CRYSTAL_HALBERD_3_10_I_,
            CRYSTAL_HALBERD_4_10, CRYSTAL_HALBERD_4_10_I_,
            CRYSTAL_HALBERD_5_10, CRYSTAL_HALBERD_5_10_I_,
            CRYSTAL_HALBERD_6_10, CRYSTAL_HALBERD_6_10_I_,
            CRYSTAL_HALBERD_7_10, CRYSTAL_HALBERD_7_10_I_,
            CRYSTAL_HALBERD_8_10, CRYSTAL_HALBERD_8_10_I_,
            CRYSTAL_HALBERD_9_10, CRYSTAL_HALBERD_9_10_I_,
            CRYSTAL_HALBERD_FULL, CRYSTAL_HALBERD_FULL_I_
    }, 30, new SweepSpecialAttack(), HALBERD),

    SPEAR_WALL(new int[]{22610}, 50, new VestaSpearSpecialAttack(), SPEAR),
    FEINT(new int[]{22613, 23615}, 25, new VestaLongswordSpecialAttack(), LONGSWORD),
    PUNCTURE(new int[]{1215, 1231, 5680, 5698}, 25, new DragonDaggerSpecialAttack(), WeaponInterface.DRAGON_DAGGER),
    ABYSSAL_PUNCTURE(new int[]{13271, 13265, 13267, 13269}, 50, new AbyssalDaggerSpecialAttack(), WeaponInterface.DRAGON_DAGGER),
    UNLEASH(new int[]{22731, 22734, 22737, 22740, 22743}, 1, new DragonHastaSpecialAttack(), HASTA),
    DUALITY(new int[]{22804}, 25, new DragonKnifeSpecialAttack(), WeaponInterface.KNIFE),
    DUALITY_P(new int[]{22806, 22808, 22810}, 25, new DragonKnifePoisonSpecialAttack(), WeaponInterface.KNIFE),
    LIQUIFY(new int[]{11037}, 75, new BrineSabreSpecialAttack(), BRINESABRE),

    /**
     * See {@link GraniteMaulSpecialAttack#drainAmount(Agent)} for the drain amount
     */
    QUICK_SMASH(new int[]{4153, 12848, 20557, 24225, 24227}, 60, new GraniteMaulSpecialAttack(), WeaponInterface.GRANITE_MAUL),

    SLICE_AND_DICE(new int[]{13652, 15250, 15760, 20784, 26708}, 50, new DragonClawsSpecialAttack(), CLAWS),

    IMPALE(new int[]{ItemID.RUNE_CLAWS}, 25, new RuneClawSpecialAttack(), CLAWS),

    SNAPSHOT(new int[]{861, 12788, 15433, 20558, 10284}, 55, new MagicShortbowSpecialAttack(), SHORTBOW),

    POWERSHOT(new int[]{859, 10284}, 55, new MagicLongbowSpecialAttack(), WeaponInterface.LONGBOW),

    SOULSHOT(new int[]{ItemID.SEERCULL}, 100, new SeercullSpecialAttack(), WeaponInterface.SEERCULL),
    CHAINHIT(new int[]{RUNE_THROWNAXE}, 10, new RuneThrowingAxeSpecialAttack(), THROWNAXE),

    DESCENT_OF_DARKNESS_OR_DRAGONS(new int[]{11235, 15223, 12765, 12766, 12767, 12768, 20408}, 55, new DarkBowSpecialAttack(), WeaponInterface.DARK_BOW),
    DESCENT_OF_DARKNESS_OR_DRAGONS_2(new int[]{15804}, 55, new DragonHunterBowSpecialAttack(), DRAGON_HUNTER_BOW),
    ARMADYL_EYE(new int[]{11785, 15225}, 40, new ArmadylCrossbowSpecialAttack(), CROSSBOW),
    EVOKE(new int[]{ZARYTE_CROSSBOW}, 75, new ZaryteCrossbowSpecialAttack(), CROSSBOW),
    SNIPE(new int[]{8880}, 75, new DorgeshuunSpecialAttack(), DORGRESHUUN),
    TOXIC_SIPHON(new int[]{12926, 15834}, 50, new ToxicBlowpipeSpecialAttack(), BLOWPIPE),
    ANNIHILATE(new int[]{21902}, 60, new DragonCrossbowSpecialAttack(), CROSSBOW),
    CONCENTRATED_SHOT(new int[]{19481, 15022, 26712}, 65, new BallistaSpecialAttack(), WeaponInterface.BALLISTA),
    SHIELD_BASH(new int[]{ItemID.DINHS_BULWARK}, 100, player -> {}, MAUL),

    MOMENTUM_THROW(new int[]{ItemID.DRAGON_THROWNAXE}, 25, new DragonThrownaxeSpecialAttack(), THROWNAXE),

    SHOVE(new int[]{
            DRAGON_SPEAR, DRAGON_SPEAR_P_, DRAGON_SPEAR_P_PLUS_, DRAGON_SPEAR_P_PLUS_PLUS_,
            ZAMORAKIAN_SPEAR,
            ZAMORAKIAN_HASTA
    }, 25, new ShoveSpecialAttack(), SPEAR),

    HAMMER_BLOW(new int[]{21742}, 60, new GraniteHammerSpecialAttack(), ELDER_MAUL),
    STATIUS_WARHAMMER_SMASH(new int[] {22622}, 35, new StatiusWarhammerSpecialAttack(), WARHAMMER),
    POWERSTAB(new int[]{DRAGON_2H_SWORD, DRAGON_2H_SWORD_2, DRAGON_2H_SWORD_3}, 60, new Dragon2hSwordSpecialAttack(), TWO_HANDED_SWORD),
    FISHSTABBER(new int[]{DRAGON_HARPOON, DRAGON_HARPOON_2, INFERNAL_HARPOON}, 100, NonAttackSpecialAttacks.harpoonEffect(), DAGGER),
    INVOCATE(new int[] {ELDRITCH_NIGHTMARE_STAFF}, 55, new EldritchNightmareStaffSpecialAttack(), BLADED_STAFF),
    IMMOLATE(new int[] {VOLATILE_NIGHTMARE_STAFF}, 55, new VolatileNightmareStaffSpecialAttack(), BLADED_STAFF)

    ;

    /**
     * The weapon ID's that perform this special when activated.
     */
    private final int[] identifiers;

    /**
     * The amount of special energy this attack will drain.
     */
    private final int customDrainAmount;

    /**
     * The combat type used when performing this special attack.
     */
    private final SpecialAttack strategy;

    /**
     * The weapon interface used by the identifiers.
     */
    private final WeaponInterface weaponType;

    private Consumer<Player> action;

    /**
     * Create a new {@link SpecialAttackType}.
     *  @param identifiers    the weapon ID's that perform this special when activated.
     * @param attackStrategy the combat type used when performing this special attack.
     * @param weaponType     the weapon interface used by the identifiers.
     */
    SpecialAttackType(int[] identifiers, int drainAmount, SpecialAttack attackStrategy, WeaponInterface weaponType) {
        this.identifiers = identifiers;
        this.customDrainAmount = drainAmount;
        this.strategy = attackStrategy;
        this.weaponType = weaponType;
    }
    SpecialAttackType(int[] identifiers, int drainAmount, Consumer<Player> action, WeaponInterface weaponType) {
        this(identifiers, drainAmount, (SpecialAttack) null, weaponType);
        this.action = action;
    }

    /**
     * Drains the special bar for the argued {@link Agent}.
     *
     * @param agent  the agent who's special bar will be drained.
     * @param amount the amount of energy to drain from the special bar.
     */
    public static void drain(Agent agent, int amount) {

        agent.decrementSpecialPercentage(amount);
        agent.setSpecialActivated(false);

        if (!agent.isRecoveringSpecialAttack()) {
            TaskManager.submit(new RestoreSpecialAttackTask(agent));
        }

        if (agent.isPlayer() && agent != null) {
            Player p = agent.getAsPlayer();
            p.getPacketSender().sendSpecialAttackState(p.isSpecialActivated());

            // Process Achievements
            AchievementManager.processFor(AchievementType.ALWAYS_SPECIAL, p);
            AchievementManager.processFor(AchievementType.CONSECUTIVE_BLOWS, p);
            AchievementManager.processFor(AchievementType.BLOWING_HITS, p);

            // Increase points
            p.getPoints().increase(AttributeManager.Points.SPECIAL_ATTACKS_USED, 1); // Increase points

            SpecialAttackType.updateBar(p, true);
        }
    }

    public static void updateBar(Player player) {
        updateBar(player, true);
    }

    /**
     * Updates the special bar with the amount of special energy the argued
     * {@link Player} has.
     *
     * @param player the player who's special bar will be updated.
     */
    public static void updateBar(Player player, boolean updateComponents) {

        final Weapon weapon = player.getCombat().getWeapon();

        if(weapon == null || weapon.getSpecialBar() == -1 || weapon.getSpecialMeter() == -1)
            return;

        int specialCheck = 10;
        int specialBar = player.getCombat().getWeapon().getSpecialMeter();
        int specialAmount = player.getSpecialPercentage() / 10;

        if (updateComponents){
            for (int i = 0; i < 10; i++) {
                player.getPacketSender().sendInterfaceComponentMoval(specialAmount >= specialCheck ? 500 : 0, 0,
                        --specialBar);
                specialCheck--;
            }
        }
        player.getPacketSender()
                .updateSpecialAttackOrb()
                .sendString(player.getCombat().getWeapon().getSpecialMeter(),
                player.isSpecialActivated() ? ("@yel@ Special Attack (" + player.getSpecialPercentage() + "%)")
                        : ("@bla@ Special Attack (" + player.getSpecialPercentage() + "%)"), true);
    }

    /**
     * Assigns special bars to the attack style interface if needed.
     *
     * @param player the player to assign the special bar for.
     */
    public static void assign(Player player) {
        if (player.getCombat().getWeapon().getSpecialBar() == -1) {
            if(player.isSpecialActivated())
                player.setSpecialActivatedAndSendState(false);
            player.setSpecialAttackType(null);
            SpecialAttackType.updateBar(player);
            return;
        }

        for (SpecialAttackType c : SpecialAttackType.values()) {
            if (player.getCombat().uses(c.getWeaponType())) {
                if (Arrays.stream(c.getIdentifiers())
                        .anyMatch(id -> player.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId() == id)) {
                    player.getPacketSender().sendInterfaceDisplayState(player.getCombat().getWeapon().getSpecialBar(),
                            false);
                    player.setSpecialAttackType(c);
                    return;
                }
            }
        }

        player.getPacketSender().sendInterfaceDisplayState(player.getCombat().getWeapon().getSpecialBar(), true);
        player.setSpecialAttackType(null);
        player.setSpecialActivatedAndSendState(false);
    }


    public static void activate(Player player) {

        // Make sure the player has a valid special attack
        if (player.getSpecialAttackType() == null) {
            return;
        }

        // Duel, disabled special attacks?
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_SPECIAL_ATTACKS.ordinal()]) {
            DialogueManager.sendStatement(player, "Special attacks have been disabled in this duel!");
            return;
        }

        // Check if player has already activated special attack,
        // If that's the case - turn if off.
        if (player.isSpecialActivated()) {
            player.setSpecialActivatedAndSendState(false);
            SpecialAttackType.updateBar(player, true);
        } else {

            // Get the special attack..
            final SpecialAttackType spec = player.getSpecialAttackType();

            // Set special attack activated
            player.setSpecialActivatedAndSendState(true);

            // Update special bar
            SpecialAttackType.updateBar(player, true);

            if(spec.strategy != null)
                spec.strategy.onActivated(player);

            Optional.ofNullable(spec.action).ifPresent(playerConsumer -> playerConsumer.accept(player));
        }

        if (player.getInterfaceId() == EquipmentBonuses.INTERFACE_ID) {
            EquipmentBonuses.update(player);
        }
    }

    /**
     * Gets the weapon ID's that perform this special when activated.
     *
     * @return the weapon ID's that perform this special when activated.
     */
    public int[] getIdentifiers() {
        return identifiers;
    }

    /**
     * Gets the amount of special energy this attack will drain.
     *
     * @return the amount of special energy this attack will drain.
     */
    public int getCustomDrainAmount() {
        return customDrainAmount;
    }

    public int getDrainAmount(Agent actor){

        if(strategy != null)
            return strategy.drainAmount(actor);

        return getCustomDrainAmount();
    }

    /**
     * Gets the combat type used when performing this special attack.
     *
     * @return the combat type used when performing this special attack.
     */
    public AttackStrategy<Agent> getStrategy() {
        return Objects.requireNonNullElse(strategy, MeleeAttackStrategy.INSTANCE);
    }

    /**
     * Gets the weapon interface used by the identifiers.
     *
     * @return the weapon interface used by the identifiers.
     */
    public WeaponInterface getWeaponType() {
        return weaponType;
    }

}
