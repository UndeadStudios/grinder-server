package com.grinder.util.tools

import com.grinder.Server
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.countQuantityInAccount
import com.grinder.game.entity.getLong
import com.grinder.game.entity.setLong
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.punishment.PunishmentManager
import com.grinder.game.model.punishment.PunishmentType
import com.grinder.util.DiscordBot
import com.grinder.util.ItemID
import java.util.concurrent.TimeUnit

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   21/04/2020
 * @version 1.0
 */
object DupeDetector {

    private val triggerIdAmountMap = HashMap<Int, Int>()
    private var overhead = 0L

    init {

        // Rare equipment
        triggerIdAmountMap[ItemID.TWISTED_BOW] = 70
        triggerIdAmountMap[ItemID.TWISTED_BOW_2] = 70

        triggerIdAmountMap[ItemID.DRAGONFIRE_SHIELD] = 149
        triggerIdAmountMap[ItemID.DRAGONFIRE_SHIELD_2] = 149
        triggerIdAmountMap[ItemID.DRAGONFIRE_SHIELD_3] = 149

        triggerIdAmountMap[ItemID.DRAGONFIRE_WARD] = 80
        triggerIdAmountMap[ItemID.DRAGONFIRE_WARD_CHARGED] = 80

        triggerIdAmountMap[ItemID.ELITE_VOID_ROBE] = 100
        triggerIdAmountMap[ItemID.ELITE_VOID_ROBE_BROKEN_] = 100

        triggerIdAmountMap[ItemID.ELITE_VOID_TOP] = 100
        triggerIdAmountMap[ItemID.ELITE_VOID_TOP_BROKEN_] = 100

        triggerIdAmountMap[22547] = 100 // Craw's bow
        triggerIdAmountMap[22549] = 100 // Craw's bow
        triggerIdAmountMap[22550] = 100 // Craw's bow

        triggerIdAmountMap[22542] = 100 // Viggora's chainmace
        triggerIdAmountMap[22545] = 100 // Viggora's chainmace

        triggerIdAmountMap[22552] = 100 // Thammaron's sceptre
        triggerIdAmountMap[22555] = 100 // Thammaron's sceptre

        triggerIdAmountMap[21816] = 100 // Bracelet of Ethereum
        triggerIdAmountMap[21817] = 100 // Bracelet of Ethereum
        triggerIdAmountMap[21818] = 100 // Bracelet of Ethereum

        triggerIdAmountMap[22323] = 100 // Sanguinesti staff
        triggerIdAmountMap[22481] = 100 // Sanguinesti staff

        triggerIdAmountMap[ItemID._3RD_AGE_BOW] = 51
        triggerIdAmountMap[ItemID._3RD_AGE_BOW_2] = 51

        triggerIdAmountMap[ItemID.INFERNAL_CAPE] = 50
        triggerIdAmountMap[ItemID.INFERNAL_CAPE_2] = 50
        triggerIdAmountMap[ItemID.INFERNAL_CAPE_BROKEN_] = 50

        triggerIdAmountMap[ItemID.DRAGON_CLAWS] = 500
        triggerIdAmountMap[ItemID.DRAGON_CLAWS_2] = 500
        triggerIdAmountMap[ItemID.DRAGON_CLAWS_3] = 500

        triggerIdAmountMap[ItemID.ELDER_MAUL] = 85
        triggerIdAmountMap[ItemID.ELDER_MAUL_2] = 85
        triggerIdAmountMap[ItemID.ELDER_MAUL_3] = 85
        triggerIdAmountMap[ItemID.ELDER_MAUL_4] = 85
        triggerIdAmountMap[ItemID.ELDER_MAUL_5] = 85

        triggerIdAmountMap[ItemID.GHRAZI_RAPIER] = 80
        triggerIdAmountMap[22483] = 80 // Rapier
        triggerIdAmountMap[22325] = 80 // Scythe of vitur
        triggerIdAmountMap[22486] = 80 // Scythe of vitur

        triggerIdAmountMap[ItemID.AMULET_OF_FURY] = 1000
        triggerIdAmountMap[ItemID.AMULET_OF_FURY_2] = 1000

        triggerIdAmountMap[ItemID.AMULET_OF_ETERNAL_GLORY] = 49
        triggerIdAmountMap[ItemID.AMULET_OF_ETERNAL_GLORY_2] = 49

        triggerIdAmountMap[ItemID.FIRE_CAPE] = 250
        triggerIdAmountMap[ItemID.FIRE_CAPE_2] = 250
        triggerIdAmountMap[ItemID.FIRE_CAPE_3] = 250
        triggerIdAmountMap[ItemID.FIRE_CAPE_BROKEN_] = 250

        triggerIdAmountMap[ItemID.ARMADYL_GODSWORD] = 250
        triggerIdAmountMap[ItemID.ARMADYL_GODSWORD_2] = 250
        triggerIdAmountMap[ItemID.ARMADYL_GODSWORD_3] = 250

        triggerIdAmountMap[22322] = 100 // Avernic defender

        triggerIdAmountMap[ItemID.KODAI_WAND] = 70
        triggerIdAmountMap[ItemID.KODAI_WAND_2] = 70

        triggerIdAmountMap[ItemID.HEAVY_BALLISTA] = 70
        triggerIdAmountMap[ItemID.HEAVY_BALLISTA_2] = 70

        triggerIdAmountMap[ItemID.BARROWS_GLOVES] = 500
        triggerIdAmountMap[15155] = 75 // Dragon whip
        triggerIdAmountMap[15153] = 75 // Indigo whip
        triggerIdAmountMap[15156] = 75 // Gold whip
        triggerIdAmountMap[15158] = 75 // White whip
        triggerIdAmountMap[15152] = 75 // Lava blade
        triggerIdAmountMap[15022] = 50 // Corrupt heavy ballista
        triggerIdAmountMap[15023] = 50 // Corrupt twisted bow
        triggerIdAmountMap[15024] = 50 // Corrupt spirit shield
        triggerIdAmountMap[15025] = 50 // Corrupt spirit shield

        triggerIdAmountMap[ItemID.MAX_CAPE] = 100
        triggerIdAmountMap[ItemID.MAX_CAPE_2] = 100
        triggerIdAmountMap[ItemID.MAX_CAPE_3] = 100

        triggerIdAmountMap[ItemID.ABYSSAL_WHIP] = 750
        triggerIdAmountMap[ItemID.ABYSSAL_WHIP_2] = 750

        triggerIdAmountMap[ItemID.DARK_BOW] = 750
        triggerIdAmountMap[ItemID.DARK_BOW_2] = 750

        triggerIdAmountMap[ItemID.ARCANE_SPIRIT_SHIELD] = 80
        triggerIdAmountMap[ItemID.ARCANE_SPIRIT_SHIELD_2] = 80

        triggerIdAmountMap[ItemID.ELYSIAN_SPIRIT_SHIELD] = 80
        triggerIdAmountMap[ItemID.ELYSIAN_SPIRIT_SHIELD_2] = 80
        triggerIdAmountMap[ItemID.ELYSIAN_SPIRIT_SHIELD_3] = 80

        triggerIdAmountMap[15798] = 50
        triggerIdAmountMap[15799] = 50

        triggerIdAmountMap[15802] = 55
        triggerIdAmountMap[15803] = 55

        triggerIdAmountMap[ItemID.SPECTRAL_SPIRIT_SHIELD] = 80
        triggerIdAmountMap[ItemID.SPECTRAL_SPIRIT_SHIELD_2] = 80

        triggerIdAmountMap[ItemID.PRIMORDIAL_BOOTS] = 500
        triggerIdAmountMap[ItemID.PRIMORDIAL_BOOTS_2] = 500

        triggerIdAmountMap[ItemID.ETERNAL_BOOTS] = 500
        triggerIdAmountMap[ItemID.ETERNAL_BOOTS_2] = 500

        triggerIdAmountMap[ItemID.PEGASIAN_BOOTS] = 500
        triggerIdAmountMap[ItemID.PEGASIAN_BOOTS_2] = 500

        triggerIdAmountMap[ItemID.TOME_OF_FIRE_EMPTY_] = 500
        triggerIdAmountMap[ItemID.TOME_OF_FIRE_EMPTY_2] = 500
        triggerIdAmountMap[ItemID.TOME_OF_FIRE] = 500


        //Rares
        triggerIdAmountMap[ItemID.BLUE_PARTYHAT] = 50
        triggerIdAmountMap[ItemID.BLUE_PARTYHAT_2] = 50

        triggerIdAmountMap[ItemID.RED_PARTYHAT] = 50
        triggerIdAmountMap[ItemID.RED_PARTYHAT_2] = 50

        triggerIdAmountMap[ItemID.GREEN_PARTYHAT] = 50
        triggerIdAmountMap[ItemID.GREEN_PARTYHAT_2] = 50

        triggerIdAmountMap[ItemID.YELLOW_PARTYHAT] = 50
        triggerIdAmountMap[ItemID.YELLOW_PARTYHAT_2] = 50

        triggerIdAmountMap[ItemID.BLACK_PARTYHAT] = 50
        triggerIdAmountMap[ItemID.RAINBOW_PARTYHAT] = 50
        triggerIdAmountMap[ItemID.BLACK_HWEEN_MASK] = 50

        triggerIdAmountMap[ItemID.RED_HALLOWEEN_MASK] = 50
        triggerIdAmountMap[ItemID.RED_HALLOWEEN_MASK_2] = 50

        triggerIdAmountMap[ItemID.GREEN_HALLOWEEN_MASK] = 50
        triggerIdAmountMap[ItemID.GREEN_HALLOWEEN_MASK_2] = 50

        triggerIdAmountMap[ItemID.BLUE_HALLOWEEN_MASK] = 50
        triggerIdAmountMap[ItemID.BLUE_HALLOWEEN_MASK_2] = 50

        triggerIdAmountMap[ItemID.SANTA_HAT] = 50
        triggerIdAmountMap[ItemID.SANTA_HAT_2] = 50



        // Cash
        triggerIdAmountMap[ItemID.PLATINUM_TOKEN] = 1_000_000_000
        triggerIdAmountMap[ItemID.BLOOD_MONEY] = 179_999_999

        // Other

        triggerIdAmountMap[ItemID.REVENANT_ETHER] = 1_000_000

        triggerIdAmountMap[ItemID.RUNE_ARROW] = 25_000_000
        triggerIdAmountMap[ItemID.RUNE_ARROW_P_PLUS_PLUS_] = 25_000_000

        triggerIdAmountMap[ItemID.DRAGON_ARROW] = 25_000_000
        triggerIdAmountMap[ItemID.DRAGON_ARROW_P_PLUS_PLUS_] = 25_000_000

        triggerIdAmountMap[ItemID.AMETHYST_ARROW] = 500_000
        triggerIdAmountMap[ItemID.AMETHYST_ARROW_P_PLUS_PLUS_] = 500_000

        triggerIdAmountMap[ItemID.AIR_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.FIRE_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.WATER_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.BODY_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.MIND_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.COSMIC_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.CHAOS_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.DEATH_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.BLOOD_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.ASTRAL_RUNE] = 50_000_000
        triggerIdAmountMap[ItemID.NATURE_RUNE] = 50_000_000
        triggerIdAmountMap[21880] = 50_000_000 // Wrath rune

        triggerIdAmountMap[ItemID.MANTA_RAY] = 1_000_000
        triggerIdAmountMap[ItemID.MANTA_RAY_2] = 1_000_000

        triggerIdAmountMap[ItemID.ANGLERFISH] = 100_000
        triggerIdAmountMap[ItemID.ANGLERFISH_2] = 100_000

        triggerIdAmountMap[ItemID.MYSTERY_BOX] = 5000

        triggerIdAmountMap[22124] = 8_000 // Superior dragon bones

        triggerIdAmountMap[15200] = 256 // Premium mystery boxes
        triggerIdAmountMap[15205] = 256 // Premium mystery boxes
        triggerIdAmountMap[11738] = 256 // Premium mystery boxes
        triggerIdAmountMap[15206] = 256 // Premium mystery boxes
        triggerIdAmountMap[15202] = 256 // Premium mystery boxes
        triggerIdAmountMap[15201] = 128 // Premium mystery boxes
        triggerIdAmountMap[15203] = 101 // Premium mystery boxes
        triggerIdAmountMap[15204] = 50 // Premium mystery boxes

        triggerIdAmountMap[ItemID.PARTYHAT_SET] = 49
        triggerIdAmountMap[ItemID.PARTYHAT_SET_2] = 49

        triggerIdAmountMap[ItemID.HALLOWEEN_MASK_SET] = 49
        triggerIdAmountMap[ItemID.HALLOWEEN_MASK_SET_2] = 49

        triggerIdAmountMap[15263] = 256 // Premium item sets
        triggerIdAmountMap[15266] = 256 // Premium item sets
        triggerIdAmountMap[15210] = 256 // Premium item sets
        triggerIdAmountMap[15211] = 256 // Premium item sets
        triggerIdAmountMap[15212] = 256 // Premium item sets
        triggerIdAmountMap[21049] = 50 // Premium item sets
        triggerIdAmountMap[15213] = 256 // Premium item sets
        triggerIdAmountMap[15214] = 256 // Premium item sets
        triggerIdAmountMap[15208] = 256 // Premium item sets
        triggerIdAmountMap[15209] = 256 // Premium item sets
        triggerIdAmountMap[15219] = 256 // Premium item sets
        triggerIdAmountMap[15265] = 256 // Premium item sets


        triggerIdAmountMap[7779] = 101 // XP Tomes
        triggerIdAmountMap[7782] = 101 // XP Tomes
        triggerIdAmountMap[7796] = 101 // XP Tomes
        triggerIdAmountMap[15272] = 101 // XP Tomes

        triggerIdAmountMap[ItemID.BANDITS_BREW] = 49
        triggerIdAmountMap[ItemID.BANDITS_BREW_2] = 49

        triggerIdAmountMap[ItemID.OLD_SCHOOL_BOND] = 25

        triggerIdAmountMap[ItemID.BONECRUSHER] = 25
        triggerIdAmountMap[ItemID.LOOTING_BAG] = 25
        triggerIdAmountMap[ItemID.RUNE_POUCH] = 25

        triggerIdAmountMap[ItemID.LAMP] = 99
        triggerIdAmountMap[ItemID.LAMP_2] = 99

        triggerIdAmountMap[ItemID.COMBAT_LAMP] = 49

        triggerIdAmountMap[ItemID.AGILITY_ARENA_TICKET] = 50_000

        triggerIdAmountMap[ItemID.VOTING_TICKET] = 5_000

        triggerIdAmountMap[2542] = 27 // Prayer scrolls
        triggerIdAmountMap[2544] = 25 // Prayer scrolls
        triggerIdAmountMap[2543] = 25 // Prayer scrolls



    }

    fun check(player: Player?) {
        if(player == null)
            return
        if(!player.rights.anyMatch(PlayerRights.DEVELOPER, PlayerRights.CO_OWNER, PlayerRights.OWNER) && !player.username.equals("3lou 55") && !player.username.equals("Mod Grinder") && !player.gameMode.isSpawn) {
            val now = System.nanoTime()
            val time = TimeUnit.NANOSECONDS.toMillis(now)
            val lastTime = player.getLong(Attribute.LAST_DUPE_TIME)
            if(time - lastTime > 10_000) {
                player.setLong(Attribute.LAST_DUPE_TIME, time)
                for ((id, thresholdAmount) in triggerIdAmountMap) {
                    val inAccount = player.countQuantityInAccount(id)
                    if (inAccount >= thresholdAmount) {
                        val name = ItemDefinition.forId(id).name ?: ""
                        DiscordBot.INSTANCE.sendModMessage("Player **${player.username}** was auto-banned due to quantity of item **$name** ($inAccount) exceeding $thresholdAmount @everyone!")
                        PunishmentManager.submit(player.username, PunishmentType.BAN, PunishmentType.IP_BAN)
                    }
                }
                overhead += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - now)
            }
        }
    }

    fun checkOverhead(){
        if(overhead > 40){
            Server.getLogger().warn("Overhead of Dupe Detector was $overhead ms this cycle!")
        }
        overhead = 0
    }
}