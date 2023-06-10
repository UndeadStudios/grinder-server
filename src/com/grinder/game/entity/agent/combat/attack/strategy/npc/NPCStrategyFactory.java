package com.grinder.game.entity.agent.combat.attack.strategy.npc;

import com.grinder.game.content.minigame.chamberoxeric.room.icedemon.IceDemonCombat;
import com.grinder.game.content.minigame.chamberoxeric.room.mutadiles.npc.LargeMutadileCombat;
import com.grinder.game.content.minigame.chamberoxeric.room.mutadiles.npc.SmallMutadileCombat;
import com.grinder.game.content.minigame.chamberoxeric.room.vasanistirio.VasaNistirioCombat;
import com.grinder.game.content.minigame.chamberoxeric.room.vespula.VespulaCombat;
import com.grinder.game.content.minigame.chamberoxeric.room.vespula.VespulaPortalCombat;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.strategy.PoisonAttackWrapper;
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.strategy.npc.monster.*;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.util.NpcID;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-04
 */
public class NPCStrategyFactory {

    /**
     * Assigns a {@link AttackStrategy} to the specified {@link NPC}.
     *
     * @param npc the {@link NPC} to assign a {@link AttackStrategy} to.
     */
    public static void assignAttackStrategy(NPC npc) {

        if (npc instanceof Boss)
            return;
        if (npc == null) {
            return;
        }

        switch (npc.getId()) {

            case 7563:
                npc.setAttackStrategy(new LargeMutadileCombat());
                break;

            case 7562:
                npc.setAttackStrategy(new SmallMutadileCombat());
                break;

            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 7402:
            case 7279:
            case 7403:
                npc.setAttackStrategy(new AberrantSpectreAttack());
                break;

            case 406: // cave crawler
            case 407:
            case 408:
            case 409:
            case 480: // cave slime
            case NpcID.CHASM_CRAWLER:
                npc.setAttackStrategy(new PoisonAttackWrapper(AttackType.MELEE, 85, PoisonType.MILD));
                break;

            case 1049:
            case 1050:
            case 1051:
            case NpcID.CAVE_ABOMINATION:
                npc.setAttackStrategy(new CaveHorrorAttack());
                break;

            case NpcID.ABYSSAL_DEMON_415:
            case NpcID.ABYSSAL_DEMON_416:
            case NpcID.ABYSSAL_DEMON_7241:
            case NpcID.GREATER_ABYSSAL_DEMON:
                npc.setAttackStrategy(new AbyssalDemonAttack(npc));
                break;

            case NpcID.DUST_DEVIL:
            case NpcID.DUST_DEVIL_7249:
            case NpcID.CHOKE_DEVIL:
            case NpcID.SMOKE_DEVIL:
            case NpcID.NUCLEAR_SMOKE_DEVIL:
                npc.setAttackStrategy(new DustDevilAttack());
                break;

            case 6614:
                npc.setAttackStrategy(new FareedCombat());
                break;

            case 270:// bronze dragon
            case 271:
            case 7253:
            case 272:// iron dragon
            case 273:
            case 7254:
            case 139:// steel dragon
            case 274:
            case 275:
            case 7255:
                npc.setAttackStrategy(new MetalDragonAttack());
                break;
            case 2919:
            case 8089:
            case NpcID.ADAMANT_DRAGON:
            case NpcID.RUNE_DRAGON:
            case NpcID.ADAMANT_DRAGON_8090:
            case NpcID.RUNE_DRAGON_8091:
                npc.setAttackStrategy(new MithrilDragonAttack());
                break;
//            case 7307:
//                npc.setAttackStrategy(new WildernessWizardTrio.WizardMageBossAttackStrategy());
//                break;
            case 1556:
                npc.setAttackStrategy(new FireWizardAttack());
                break;
            case 1557:
                npc.setAttackStrategy(new WaterWizardAttack());
                break;
            case 1558:
                npc.setAttackStrategy(new EarthWizardAttack());
                break;
            case 510:
            case 512:
            case 2056:
            case 2057:
            case 2058:
            case 2059:
            case NpcID.MELZAR_THE_MAD:
                npc.setAttackStrategy(new DarkWizardAttack());
                break;
            case 1559:
            case 3097:
            case 3443:
                npc.setAttackStrategy(new AirWizardAttack());
                break;
            case 465:
            case 466:
            case 467:
            case 468:
                npc.setAttackStrategy(new SkeletalWyvernAttack());
                break;
            case NpcID.ANCIENT_WYVERN:
                npc.setAttackStrategy(new AncientWyvernAttack());
                break;
            case 3164:
            case 3169:
            case 3170:
            case 3171:
            case 3172:
            case 3173:
            case 3175:
            case NpcID.AVIANSIE_3176:
            case NpcID.AVIANSIE_3177:
            case NpcID.AVIANSIE_3178:
            case NpcID.AVIANSIE_3179:
            case NpcID.AVIANSIE_3180:
            case NpcID.AVIANSIE_3181:
            case NpcID.AVIANSIE_3182:
            case NpcID.AVIANSIE_3183:
                npc.setAttackStrategy(new AviansieAttack());
                break;
            case 5947:
            case 5961:
            case 5963:
                npc.getMotion().update(MovementStatus.DISABLED);
                npc.setAttackStrategy(new SpinolypAttack());
                break;
            case 476:
                npc.getMotion().update(MovementStatus.DISABLED);
                break;
            case NpcID.SCORPIAS_OFFSPRING_6616:
                npc.setAttackStrategy(new ScorpiaOffSpringAttack());
                break;
            case 5272:
            case 5273:
            case 5274:
                npc.setAttackStrategy(new MonkeyArcherAttack());
                break;
            case 443:
            case 444:
            case 445:
            case 446:
            case 447:
            case 7396:
            case 7422:
            case 7423:
            case 7604:
            case 7605:
            case 7606:
                npc.setAttackStrategy(MagicAttackStrategy.INSTANCE);
                npc.setAttackStrategy(new CombatSpellAttack(CombatSpellType.FIRE_BLAST.getSpell()));
                break;

            case 7309:
                npc.setAttackStrategy(MagicAttackStrategy.INSTANCE);
                npc.setAttackStrategy(new CombatSpellAttack(CombatSpellType.BLOOD_BLITZ.getSpell()));
                break;

            case 419:
            case 420:
            case NpcID.COCKATHRICE:
                npc.setAttackStrategy(new CockatriceAttack());
                break;
            case 4884:
                npc.setAttackStrategy(new GelatinnothMother());
                break;

            case 417:
            case 418:
            case NpcID.MONSTROUS_BASILISK:
            case 9283:
            case 9284:
            case 9285:
            case 9286:
            case NpcID.MONSTROUS_BASILISK_9287:
            case NpcID.MONSTROUS_BASILISK_9288:
                npc.setAttackStrategy(new BasaliskAttack());
                break;

/*            case NpcID.DARK_BEAST:
            case NpcID.DARK_BEAST_7250:
            case NpcID.NIGHT_BEAST:
                npc.setAttackStrategy(new DarkBeastAttack());
                break;*/
            case 3428:
            case 3431:
                npc.setAttackStrategy(new ElfArcherAttack());
                break;
            case 931:
            case 932:
            case 933:
            case 934:
            case 935:
            case NpcID.THROWER_TROLL_4135:
            case NpcID.THROWER_TROLL_4136:
            case NpcID.THROWER_TROLL_4137:
            case NpcID.THROWER_TROLL_4138:
            case NpcID.THROWER_TROLL_4139:
                npc.setAttackStrategy(new ThrowerTrollAttack());
                break;
            case 1724:
            case 1725:
            case 1726:
            case 1727:
            case 1728:
            case 1729:
            case 1730:
            case 1731:
            case 1732:
            case 1733:
                npc.setAttackStrategy(new DefilerAttack());
                break;
            case 1714:
            case 1715:
            case 1716:
            case 1717:
            case 1718:
            case 1719:
            case 1720:
            case 1721:
            case 1722:
            case 1723:
                npc.setAttackStrategy(new TorcherAttack());
                break;
            case NpcID.BATTLE_MAGE:
            case NpcID.ZAMORAK_WIZARD:
                npc.setAttackStrategy(new ZamorakWizardAttack());
                break;
            case NpcID.BATTLE_MAGE_1611:
            case NpcID.SARADOMIN_WIZARD:
            case 2212:
            case 2209:
                npc.setAttackStrategy(new SaradominWizardAttack());
                break;
            case NpcID.TZHAARMEJ:
            case NpcID.TZHAARMEJ_2155:
            case NpcID.TZHAARMEJ_2156:
            case NpcID.TZHAARMEJ_2157:
            case NpcID.TZHAARMEJ_2158:
            case NpcID.TZHAARMEJ_2159:
            case NpcID.TZHAARMEJ_2160:
                npc.setAttackStrategy(new TzhaarMageAttack());
                break;
            case NpcID.TORTURED_GORILLA_7150:
            case NpcID.TORTURED_GORILLA_7151:
                npc.setAttackStrategy(new TorturedGorillaAttackStrategy(npc));
                break;
            case NpcID.BATTLE_MAGE_1612:
                npc.setAttackStrategy(new GuthixWizardAttack());
                break;
            case NpcID.AHRIM_THE_BLIGHTED:
                npc.setAttackStrategy(MagicAttackStrategy.INSTANCE);
                npc.setAttackStrategy(new CombatSpellAttack(CombatSpellType.FIRE_WAVE.getSpell()));
                break;
            case NpcID.KARIL_THE_TAINTED:
            case 7538:
                npc.setAttackStrategy(RangedAttackStrategy.INSTANCE);
                npc.getCombat().setAmmunition(Ammunition.BOLT_RACK);
                npc.getCombat().setRangedWeapon(RangedWeapon.KARILS_CROSSBOW);
                break;
            case 7420:
                npc.setAttackStrategy(RangedAttackStrategy.INSTANCE);
                npc.getCombat().setAmmunition(Ammunition.RUNE_ARROW);
                npc.getCombat().setRangedWeapon(RangedWeapon.MAGIC_LONGBOW);
                break;
            case 7585:
                npc.setAttackStrategy(new IceDemonCombat());
                break;
            case 7565:
            case 7568:
                npc.setAttackStrategy(new VasaNistirioCombat());
                break;
            case 7530:
                npc.setAttackStrategy(new VespulaCombat());
                break;
            case 7533:
                npc.setAttackStrategy(new VespulaPortalCombat());
                break;
            case NpcID.GUARD_6056:
            case NpcID.ARCHER_4096:
            case NpcID.ARCHER_4097:
            case NpcID.ARCHER_3301:
            case NpcID.RANGER_7472:
            case NpcID.ARCHER:
                npc.setAttackStrategy(RangedAttackStrategy.INSTANCE);
                npc.getCombat().setAmmunition(Ammunition.IRON_ARROW);
                npc.getCombat().setRangedWeapon(RangedWeapon.OAK_LONGBOW);
                break;
            case NpcID.ELDER_CHAOS_DRUID:
                npc.setAttackStrategy(MagicAttackStrategy.INSTANCE);
                npc.setAttackStrategy(new CombatSpellAttack(CombatSpellType.WIND_WAVE.getSpell()));
                break;
            case NpcID.CRAZY_ARCHAEOLOGIST:
                npc.setAttackStrategy(new CrazyArchaeologistAttack());
                break;
            case NpcID.MONKEY_GUARD:
            case NpcID.MONKEY_GUARD_5275:
            case NpcID.MONKEY_GUARD_5276:
            case NpcID.MONKEY_GUARD_6811:
            case NpcID.MONKEY_GUARD_7122:
            case NpcID.MONKEY_GUARD_7123:
                npc.setAttackStrategy(new MonkeyGuardAttack());
                break;
        }

        if (npc.fetchDefinition().getName() != null && npc.fetchDefinition().getName().toLowerCase().contains("dragon")
                || npc.fetchDefinition().getId() == NpcID.ELVARG_6349) {
            if (!npc.fetchDefinition().getName().toLowerCase().contains("baby")) {
                npc.setAttackStrategy(new ChromaticDragonAttack());
            }
        }
        if (npc.fetchDefinition().getName() != null && npc.fetchDefinition().getName().toLowerCase().contains("dragon")) {
            if (npc.fetchDefinition().getName().toLowerCase().contains("brutal")) {
                npc.setAttackStrategy(new BrutalDragonAttack());
            }
        }

        // If they haven't been given a combat attackStrategy yet and they're a bot,
        // Simply use the their bot handler's choice of attackStrategy.
        if (npc.getAttackStrategy() == null) {
            if (npc.getBotHandler() != null) {
                npc.setAttackStrategy(npc.getBotHandler().getMethod());
            }
        }
    }
}
