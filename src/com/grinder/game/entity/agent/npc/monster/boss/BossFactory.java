package com.grinder.game.entity.agent.npc.monster.boss;

import com.grinder.game.content.minigame.fightcave.monsters.TzTokJad;
import com.grinder.game.definition.NpcSpawnDefinition;
import com.grinder.game.entity.agent.npc.monster.boss.impl.*;
import com.grinder.game.entity.agent.npc.monster.boss.impl.arzinian.*;
import com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast.CorporealBeastBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth.DagannothPrimeBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth.DagannothRexBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth.DagannothSupremeBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl.KreeArraBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.GeneralGraardorBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.CommanderZilyanaBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak.KrilTsutsarothBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.AlchemicalHydraBoss;
import com.grinder.game.model.Position;
import com.grinder.util.NpcID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.grinder.util.NpcID.*;

/**
 * Used to generate new {@link Boss} instances.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-11
 */
public final class BossFactory {

    private static final Logger LOGGER = LogManager.getLogger(BossFactory.class.getSimpleName());

    /**
     * Generates an optional {@link Boss}.
     *
     * @param spawnDefinition the {@link NpcSpawnDefinition} that contains the position and id.
     * @return {@link #generate(int, Position)}
     */
    public static Optional<Boss> generate(final NpcSpawnDefinition spawnDefinition){
        final int npcId = spawnDefinition.getId();
        final Position position = spawnDefinition.getPosition();
        return generate(npcId, position);
    }

    /**
     * Generates an optional {@link Boss}.
     *
     * @param npcId     the id of the boss.
     * @param position  the {@link Position} to spawn the boss at.
     * @return an {@link Optional} that is either empty in case of invalid coordinates/npc id,
     *          or contains a new {@link Boss} instance.
     */
    public static Optional<Boss> generate(int npcId, Position position) {

        switch (npcId) {
            case GALVEK_FIRE:
                return Optional.of(new GalvekBoss(position));
            case ALCHEMICAL_HYDRA:
                return Optional.of(new AlchemicalHydraBoss(npcId, position));
            case CYRISUS:
                return Optional.of(new Cyrisus3462(npcId, position));
            case CYRISUS_3463:
                return Optional.of(new Cyrisus3463(npcId, position));
            case CYRISUS_3464:
                return Optional.of(new Cyrisus3464(npcId, position));
            case ARZINIAN_AVATAR_OF_MAGIC:
            case ARZINIAN_AVATAR_OF_MAGIC_1234:
            case ARZINIAN_AVATAR_OF_MAGIC_1235:
                return Optional.of(new AvatarOfMagicBoss(npcId, position));
            case ARZINIAN_AVATAR_OF_RANGING:
            case ARZINIAN_AVATAR_OF_RANGING_1231:
            case ARZINIAN_AVATAR_OF_RANGING_1232:
                return Optional.of(new AvatarOfRangingBoss(npcId, position));
            case BARRELCHEST:
                return Optional.of(new BarrelChestBoss(npcId, position));
            case BLACK_KNIGHT_TITAN:
            case BLACK_KNIGHT_TITAN_HARD:
            case BKT_9350:
                return Optional.of(new BlackKnightTitanBoss(npcId, position, false));
            case CALLISTO:
                return Optional.of(new CallistoBoss(npcId, position));
            case THE_MIMIC_8633:
                return Optional.of(new MimicBoss());
            case CERBERUS:
                return Optional.of(new CerberusBoss(npcId, position));
            case CHAOS_ELEMENTAL:
            case CHAOS_ELEMENTAL_6505:
                return Optional.of(new ChaosElementalBoss(npcId, position));
            case CHAOS_FANATIC:
                return Optional.of(new ChaosFanaticBoss(npcId, position));
            case CORPOREAL_BEAST:
            case CORPOREAL_BEAST_9347:
                return Optional.of(new CorporealBeastBoss(npcId, position));
            case COMMANDER_ZILYANA_6493: // wilderness boss variant
                return Optional.of(new CommanderZilyanaBoss(npcId, position, false));
            case DAGANNOTH_PRIME:
                return Optional.of(new DagannothPrimeBoss(npcId, position, false));
            case DAGANNOTH_PRIME_6497: // wilderness boss variant
                return Optional.of(new DagannothPrimeBoss(npcId, position, true));
            case DAGANNOTH_REX:
                return Optional.of(new DagannothRexBoss(npcId, position, false));
            case DAGANNOTH_REX_6498: // wilderness boss variant
                return Optional.of(new DagannothRexBoss(npcId, position, true));
            case DAGANNOTH_SUPREME:
                return Optional.of(new DagannothSupremeBoss(npcId, position, false));
            case DAGANNOTH_SUPREME_6496: // wilderness boss variant
                return Optional.of(new DagannothSupremeBoss(npcId, position, true));
            case DEMONIC_GORILLA:
            case DEMONIC_GORILLA_7145:
            case DEMONIC_GORILLA_7146:
            case DEMONIC_GORILLA_7147:
            case DEMONIC_GORILLA_7148:
            case DEMONIC_GORILLA_7149:
                return Optional.of(new DemonicGorilla(npcId, position));
            case GENERAL_GRAARDOR_6494: // wilderness boss variant
                return Optional.of(new GeneralGraardorBoss(npcId, position, false));
            case GIANT_MOLE:
            case GIANT_MOLE_6499:
                return Optional.of(new GiantMoleBoss(npcId, position));
            case GIANT_SEA_SNAKE:
                return Optional.of(new GiantSeaSnakeBoss(npcId, position));
            case ICE_QUEEN:
                return Optional.of(new IceQueenBoss(npcId, position));
            case CHRONOZON:
                return Optional.of(new ChronozonBoss(npcId, position));
            case ICE_TROLL_KING:
            case ICE_TROLL_KING_6356:
            case ICE_TROLL_KING_HARD:
                return Optional.of(new IceTrollKingBoss(npcId, position));
            case JUNGLE_DEMON:
            case JUNGLE_DEMON_HARD:
                return Optional.of(new JungleDemonBoss(npcId, position));
            case JUNGLE_DEMON_6382:
                return Optional.of(new JungleDemonBossSpirit(npcId, position));
            case ANCIENT_WIZARD_7309:
                return Optional.of(new AncientWizardMinion(npcId, position));
            case KALPHITE_QUEEN_963:
            case KALPHITE_QUEEN_965:
            case KALPHITE_QUEEN_4303:
            case KALPHITE_QUEEN_6500:
            case KALPHITE_QUEEN_6501:
                return Optional.of(new KalphiteQueenBoss(npcId, position));
            case KAMIL:
            case KAMIL_HARD:
                return Optional.of(new KamilBoss(npcId, position));
            case KAMIL_6345:
                return Optional.of(new KamilBossSpirit(npcId, position));
            case KING_BLACK_DRAGON:
            case KING_BLACK_DRAGON_2642:
            case KING_BLACK_DRAGON_6502:
                return Optional.of(new KingBlackDragonBoss(npcId, position));
            case 9020:
                return Optional.of(new MerodachBoss(npcId, position));
            case KREEARRA_6492: // wilderness boss variant
                return Optional.of(new KreeArraBoss(npcId, position, false));
            case KRIL_TSUTSAROTH_6495: // wilderness boss variant
                return Optional.of(new KrilTsutsarothBoss(npcId, position, false));
            case NpcID.LIZARDMAN_SHAMAN:
            case NpcID.LIZARDMAN_SHAMAN_6767:
            case NpcID.LIZARDMAN_SHAMAN_7573:
            case NpcID.LIZARDMAN_SHAMAN_7574:
            case NpcID.LIZARDMAN_SHAMAN_7744:
            case NpcID.LIZARDMAN_SHAMAN_7745:
            case NpcID.CORRUPT_LIZARDMAN_HARD:
            case NpcID.LIZARDMAN_SHAMAN_8565:
                return Optional.of(new LizardmanShaman(npcId, position));
            case MUTANT_TARN:
            case MUTANT_TARN_9346:
                return Optional.of(new MutantTarnBoss(npcId, position));
            case GLOD:
            case GLOD_6358:
            case GLOD_HARD:
                return Optional.of(new GlodBoss(position));
            case SCORPIA:
                return Optional.of(new ScorpiaBoss(npcId, position));
            case SEA_TROLL_QUEEN:
                return Optional.of(new SeaTrollQueenBoss(npcId, position));
            case SLASH_BASH:
                return Optional.of(new SlashBashBoss(npcId, position, false));
            case THE_UNTOUCHABLE:
                return Optional.of(new TheUntouchableBoss(npcId, position));
            case VENENATIS:
            case VENENATIS_6610:
                return Optional.of(new VenenatisBoss(npcId, position));
            case VETION:
                return Optional.of(new VetionBoss(npcId, position));
            case TZTOKJAD:
                return Optional.of(new TzTokJad(position));
        }
        return Optional.empty();
    }

}
