package com.grinder.game.entity.agent.npc

import com.grinder.game.content.minigame.blastfurnace.npcs.*
import com.grinder.game.content.minigame.motherlodemine.npcs.Dwarf
import com.grinder.game.content.minigame.motherlodemine.npcs.Mercy
import com.grinder.game.content.minigame.motherlodemine.npcs.Miner
import com.grinder.game.content.minigame.motherlodemine.npcs.ProspectorPercy
import com.grinder.game.content.minigame.warriorsguild.npcs.*
import com.grinder.game.content.minigame.warriorsguild.npcs.shops.Anton
import com.grinder.game.content.minigame.warriorsguild.npcs.shops.Lidio
import com.grinder.game.content.minigame.warriorsguild.npcs.shops.Lilly
import com.grinder.game.entity.agent.npc.monster.impl.FlaxKeeper
import com.grinder.game.entity.agent.npc.monster.impl.MakeOverMage
import com.grinder.game.content.miscellaneous.rugmerchant.RugMerchant
import com.grinder.game.entity.agent.npc.monster.impl.Sheep
import com.grinder.game.entity.agent.npc.monster.impl.SkrachUglogwee
import com.grinder.game.entity.agent.npc.monster.boss.BossFactory
import com.grinder.game.entity.agent.npc.monster.impl.*
import com.grinder.game.entity.agent.npc.monster.pestcontrol.*
import com.grinder.game.entity.agent.npc.slayer.*
import com.grinder.game.model.FacingDirection
import com.grinder.game.model.Position
import com.grinder.util.NpcID.*

/**
 * TODO: add documentation.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-22
 */
object NPCFactory {



    fun create(id: Int, position: Position): NPC {

        if (RockCrab.isCrab(id))
            return RockCrab(id, position)

        if(DwarvenMiner.isDwarvenMiner(id))
            return DwarvenMiner(id,position)

        when (id) {
            BANSHEE, TWISTED_BANSHEE, SCREAMING_BANSHEE, SCREAMING_TWISTED_BANSHEE -> return Banshee(id, position)
            GARGOYLE, GARGOYLE_1543, MARBLE_GARGOYLE, MARBLE_GARGOYLE_7408, GARGOYLE_413 -> return Gargoyle(id, position)
            NECHRYAEL, NECHRYAEL_11, NECHRYARCH -> return Nechryael(id, position)
            ROCKSLUG, ROCKSLUG_422, GIANT_ROCKSLUG -> return Rockslug(id, position)
            DESERT_LIZARD, DESERT_LIZARD_460, DESERT_LIZARD_461 -> return DesertLizard(id, position)
            DUST_DEVIL, SMOKE_DEVIL, CHOKE_DEVIL, DUST_DEVIL_7249, NUCLEAR_SMOKE_DEVIL -> return DustDevil(id, position)
            BIGREDJAPAN -> return RedJapan(id, position)
            RAVAGER, RAVAGER_1705, RAVAGER_1706, RAVAGER_1707, RAVAGER_1708 -> return  Ravager(id, position)
            SPLATTER, SPLATTER_1690, SPLATTER_1691, SPLATTER_1692, SPLATTER_1693 -> return Splatter(id, position)
            SPINNER, SPINNER_1710, SPINNER_1711, SPINNER_1712, SPINNER_1713 -> return Spinner(id, position)
            SHIFTER, SHIFTER_1695, SHIFTER_1696, SHIFTER_1697, SHIFTER_1698, SHIFTER_1699, SHIFTER_1700, SHIFTER_1701, SHIFTER_1702, SHIFTER_1703 -> return Shifter(id, position)
            DEFILER, DEFILER_1725, DEFILER_1726, DEFILER_1727, DEFILER_1728, DEFILER_1729, DEFILER_1730, DEFILER_1731, DEFILER_1732, DEFILER_1733 -> return Defiler(id, position)
            TORCHER, TORCHER_1715, TORCHER_1716, TORCHER_1717, TORCHER_1718, TORCHER_1719, TORCHER_1720, TORCHER_1721, TORCHER_1722, TORCHER_1723 -> return Torcher(id, position)
            BRAWLER, BRAWLER_1735, BRAWLER_1736, BRAWLER_1737, BRAWLER_1738 -> return Brawler(id, position)
//            TUROTH, TUROTH_427, TUROTH_428, TUROTH_429, TUROTH_430, TUROTH_431, TUROTH_432, 10397 -> return Turoth(id, position)
            COW, COW_2791, COW_CALF, COW_2793, COW_CALF_2794 -> return Cow(id, position)
            DUCK, DUCK_1839, DUCKLING, DUCKLINGS, DUCK_2003, DRAKE -> return Duck(id, position)
            SHEEP_1178, SHEEP_2693, SHEEP_2694, SHEEP_2695, SHEEP_2699, SHEEP_2786, SHEEP_2787 -> return Sheep(
                id,
                position
            )
            MAKEOVER_MAGE, MAKEOVER_MAGE_1307 -> return MakeOverMage(id, position)
            SKRACH_UGLOGWEE_4853 -> return SkrachUglogwee(id, position)
            AL_KHARID_WARRIOR -> return AlKharidWarrior(id, position)
            BARRICADE -> return BarricadeEntity(id, position)
//            KURASK, KURASK_410, KURASK_411, KING_KURASK -> return Kurask(id, position)
            GHOMMAL -> return Ghommal(id, position)
            HARRALLAK_MENAROUS -> return Harrallak(id, position)
            AJJAT -> return Ajjat(id, position)
            LORELAI -> return Lorelai(id, position)
            RORY -> return Rory(id, position)
            GAMFRED -> return Gamfred(id, position)
            KAMFREENA -> return Kamfreena(id, position)
            SHANOMI -> return Shanomi(id, position)
            LIDIO -> return Lidio(id, position)
            LILLY -> return Lilly(id, position)
            ANTON -> return Anton(id, position)
            SLOANE -> return Sloane(id, position)
            JIMMY -> return Jimmy(id, position)
            REF, REF_6074 -> return Ref(id, position)
            PROSPECTOR_PERCY -> return ProspectorPercy(id, position)
            MINER_5606, MINER_5813, MINER_6565, MINER_6570 -> return Miner(id, position)
            MERCY-> return Mercy(id,position)
            DWARF_7721 -> return Dwarf(id, position)
            RUG_MERCHANT,RUG_MERCHANT_18, RUG_MERCHANT_19, RUG_MERCHANT_20, RUG_MERCHANT_22 -> return RugMerchant(id, position)
            FLAX_KEEPER -> return FlaxKeeper(id, position)
            DUMPY, DUMPY_7387 -> return Dumpy(id,position)
            THUMPY -> return Thumpy(id,position)
            NUMPTY -> return Numpty (id,position)
            ORDAN -> return Ordan(id,position)
            JORZIK -> return Jorzik(id,position)
            BLAST_FURNACE_FOREMAN -> return BlastFurnaceForeman(id,position)
        }

        val optionalBoss = BossFactory.generate(id, position)
        return if (optionalBoss.isPresent)
            optionalBoss.get()
        else
            NPC(id, position)
    }

    /**
     * Gets the facing position from direction
     *
     * @param position the npc [Position]
     * @param facingDirection the [FacingDirection]
     *
     * @return the [Position] to face
     */
    fun getFacingPosition(position: Position, facingDirection: FacingDirection): Position {
        return when (facingDirection) {
            FacingDirection.EAST -> position.clone().setX(position.x + 1)
            FacingDirection.NORTH -> position.clone().setY(position.y + 1)
            FacingDirection.SOUTH -> position.clone().setY(position.y - 1)
            FacingDirection.WEST -> position.clone().setX(position.x - 1)
        }
    }

}
