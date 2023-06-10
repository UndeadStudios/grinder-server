package com.grinder.game.model.commands.impl

import com.grinder.game.content.minigame.castlewars.CastleWars
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.passedTime
import com.grinder.game.message.impl.ItemContainerActionMessage
import com.grinder.game.model.Boundary
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.InstancedBossArea
import com.grinder.game.model.areas.UntypedInstancedBossArea
import com.grinder.game.model.areas.godwars.GodChamberArea
import com.grinder.game.model.areas.impl.DuelFightArena
import com.grinder.game.model.areas.impl.KalphiteLair
import com.grinder.game.model.areas.impl.PublicMinigameLobby
import com.grinder.game.model.areas.instanced.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.commands.Command
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Zach S <zach></zach>@findzach.com>
 * @since 12/17/2020
 */
class SpawnGameModeFindCommand(


    private val FORBIDDEN_SPAWN_GAMEMODE_ITEM_IDS: IntArray = intArrayOf(
        //ItemID.BLOOD_MONEY,
        ItemID.LONG_BONE,
        ItemID.CURVED_BONE,
        ItemID.VOTING_TICKET,
        ItemID.HYDRO_CAPE,
        ItemID.DRAGON_CHAINBODY_2,
        15731,
        15732,
        15733,
        15734,
        15735,
        15736,
        15737,
        15738,
        15739,
        15740,
        15741,
        15742,
        15743,
        15744,
        15745,
        15746,
        15747,
        15748,
        15749,
        15750,
        15261,
        14158,
        22124,
        15720,
        15722,
        15724,
        15725,
        15726,
        15727,
        15208,
        15209,
        15215,
        15696,
        617,
        6964,
        8890,
        996,
        997,
        998,
        999,
        1000,
        1001,
        1002,
        1003,
        1004,

        23497, // Hydro tokens
        15696, // Game mode
        15697, // Game mode
        15716, // Game mode
        15717, // Game mode
        15718, // Game mode
        15719, // Game mode
        13319, // Victory cape game mode
        15031, // Voting ticket
        8322,
        8465,
        15198,
        15199,
        14158,
        14159,
        14160,
        14161,
        14162,
        ItemID.TOKKUL,
        ItemID.ARCHERY_TICKET,
        ItemID.NUMULITES,
        ItemID.FIRE_CAPE,
        ItemID.FIRE_CAPE_2,
        ItemID.FIRE_CAPE_3,
        ItemID.INFERNAL_CAPE,
        ItemID.WARRIOR_GUILD_TOKEN,
        ItemID.DRAGON_DEFENDER,
        ItemID.SURVIVAL_TOKEN,
        ItemID.CHRISTMAS_CRACKER,
        ItemID.BANDAGES,
        15729, // torva whip broken
        /*
        Voting store items being
        */
        757,
        15031,
        8465,
        8322,
        6199,
        11738,
        15366,
        15168,
        15169,
        15170,
        15437,
        15381,
        15382,
        15383,
        15384,
        15385,
        15251,
        15252,
        15253,
        15199,
        14160,
        14161,
        20405,
        20408,
        23108,
        23303,
        23306,
        2581,
        12596,
        23249,
        19994,
        2577,
        21214,
        12887,
        12888,
        12889,
        12890,
        12891,
        12892,
        12893,
        12894,
        12895,
        12896,
        13104,
        20116,
        20119,
        20122,
        20125,
        20113,
        13679,
        20251,
        20254,
        20263,
        1037,
        13663,
        13664,
        13665,
        1961,
        12357,
        1419,
        4083,
        4566,
        4567,
        4565,
        5608,
        5609,
        5607,
        9470,
        9472,
        9946,
        6818,
        12397,
        12393,
        12395,
        12430,
        8652,
        8963,
        8956,
        8995,
        2643,
        981,
        11280,
        7668,
        7927,
        20050,
        23297,
        19970,
        12432,
        7918,
        12516,
        10883,
        9920,
        9005,
        9006,
        12375,
        12377,
        12379,
        10394,
        10392,
        10396,
        10398,
        7675,
        7676,
        6665,
        6666,
        23389,
        6549,
        1052,
        1907,
        15274,
        20355,
        20439,
        20436,
        20442,
        20433,
        12363,
        12365,
        12367,
        12369,
        23270,
        12518,
        12520,
        12522,
        12524,
        // End of voting store items

        // Begin limited store items
        15193,
        15194,
        15196,
        15300,
        15301,
        15302,
        15303,
        15304,
        15305,
        15372,
        15373,
        15374,
        15405,
        15399,
        15400,
        15401,
        15402,
        15403,
        15404,
        15345,
        15349,
        15350,
        15351,
        15308,
        15306,
        15307,
        15309,
        15414,
        15416,
        15406,
        15408,
        15410,
        15426,
        15428,
        15418,
        15420,
        15422,
        15424,
        // End of limited store items
        ItemID.SNOWBALL
    )

) : Command {

    override fun getSyntax(): String {
        return "[itemName]"
    }

    override fun getDescription(): String {
        return "Find and spawn items by name search."
    }

    override fun execute(player: Player, command: String, parts: Array<String>) {

        if (player.BLOCK_ALL_BUT_TALKING || player.isAccountFlagged || player.isInTutorial) {
            return;
        }
        if (player.area is UntypedInstancedBossArea || player.area is InstancedBossArea) {
            player.packetSender.sendMessage("You can't do this within instances!", 1000)
            return
        }
        if (AreaManager.inWilderness(player)) {
            player.packetSender.sendMessage("You can't do this in the Wilderness!")
            return
        }
        if (player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You can't do this in the Wilderness!")
            return
        }
        if (player.area != null && player.area is ZulrahShrine) {
            player.packetSender.sendMessage("You can't do this in this area.")
            return
        }
        if (player.area != null && player.area is KalphiteLair && player.position.z == 0) {
            player.packetSender.sendMessage("You can't do this in this area.")
            return
        }
        if (player.area != null && player.area is VorkathArea) {
            player.packetSender.sendMessage("You can't do this in this area.")
            return
        }
        if (player.area != null && player.area is GodChamberArea<*>) {
            player.packetSender.sendMessage("You can't do this in this area.")
            return
        }
        if (player.area != null && player.area is CerberusArea) {
            player.packetSender.sendMessage("You can't do this in this area.")
            return
        }
        if (AreaManager.inside(player.position, Boundary(2240, 2302, 2563, 2622))) {
            player.packetSender.sendMessage("You can't do this in this area.")
            return
        }
        if (AreaManager.inside(player.position, Boundary(2963, 3000, 4368, 4400))) { // Corp Area
            player.packetSender.sendMessage("You can't do this in this area.")
            return
        }
        if (player.area != null && player.area is DuelFightArena || player.dueling.inDuel()) {
            player.packetSender.sendMessage("You can't do this in the Duel Arena!")
            return
        }
        if (player.area != null && player.area is CastleWars) {
            player.packetSender.sendMessage("You can't do this in the Castle Wars Minigame!")
            return
        }
        if (player.busy() || player.interfaceId > 0) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.status === PlayerStatus.BANKING) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.status === PlayerStatus.TRADING) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.status === PlayerStatus.DUELING) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.status === PlayerStatus.DICING) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.status === PlayerStatus.SHOPPING) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.status === PlayerStatus.PRICE_CHECKING) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.getBoolean(
                Attribute.HAS_PENDING_RANDOM_EVENT,
                false
            ) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
        ) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.isInTutorial) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.combat.isInCombat) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.combat.isUnderAttack) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.minigame != null) {
            player.packetSender.sendMessage("You can't do this while playing Minigames!")
            return
        }
        if (player.area != null && player.area is FightCaveArea) {
            player.packetSender.sendMessage("You can't do this in the Fight Caves!")
            return
        }
        if (player.area != null && player.area is AquaisNeigeArea) {
            player.packetSender.sendMessage("You can't do this in the Aquais Neige!")
            return
        }
        if (player.area != null && player.area is PublicMinigameLobby) {
            player.packetSender.sendMessage("You can't do this here!")
            return
        }
        if (AreaManager.inWilderness(player) || player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You cannot do this action while staying in the Wilderness.")
            return
        }

        val itemName = java.lang.String.join(" ", *Arrays.copyOfRange(parts, 1, parts.size))
        val foundItems: MutableList<Item> = ArrayList()
        if (itemName.length < 4) {
            player.sendMessage("The item input name must be 4 or more characters minimum.")
            return
        }

        var spawnTimer = 60
        when {
            PlayerUtil.isDiamondMember(player) -> {
                spawnTimer = 0
            }
            PlayerUtil.isTitaniumMember(player) -> {
                spawnTimer = 10
            }
            PlayerUtil.isPlatinumMember(player) -> {
                spawnTimer = 20
            }
            PlayerUtil.isLegendaryMember(player) -> {
                spawnTimer = 25
            }
            PlayerUtil.isAmethystMember(player) -> {
                spawnTimer = 30
            }
            PlayerUtil.isTopazMember(player) -> {
                spawnTimer = 35
            }
            PlayerUtil.isRubyMember(player) -> {
                spawnTimer = 40;
            }
            PlayerUtil.isBronzeMember(player) -> {
                spawnTimer = 50;
            }
        }
        // Timer
//        if (!PlayerUtil.isDiamondMember(player)) {
//            if (!player.passedTime(Attribute.SPAWN_TIEMR, spawnTimer.toLong(), TimeUnit.SECONDS, message = true)) {
//                player.sendMessage("@red@Players with member's rank have the time reduced based on their rank.")
//                return;
//            }
//        }
        for (itemDefinition in ItemDefinition.definitions.values) {
            if (itemDefinition.name.toLowerCase().contains(itemName)) {

                // Skip noted items
                if (itemDefinition.isNoted) continue

                // Only allow platinum members and above to spawn up to revision 189# items. Platinum members can spawn the newest items too
                if (itemDefinition.id >= 24670 && !PlayerUtil.isPlatinumMember(player))
                    continue

                if (itemDefinition.id in 15750..16200)
                    continue

                // Forbidden items list by item IDs
                if(Arrays.stream(FORBIDDEN_SPAWN_GAMEMODE_ITEM_IDS).anyMatch { item -> item == itemDefinition.id })
                    continue

                // Forbidden items list by item names
                if (itemDefinition.name.contains(" tome") || itemDefinition.name.contains(" box")
                    || itemDefinition.name.contains(" box")
                    || itemDefinition.name.contains(" bond")
                    || itemDefinition.name.contains(" bonemeal")
                    || itemDefinition.name.contains(" set")
                    || itemDefinition.name.toLowerCase().contains("null")
                    //|| itemDefinition.name.contains(" defender")
                    || itemDefinition.name.toLowerCase().contains("castlewars ")
                    || itemDefinition.name.toLowerCase().contains("decorative ")
                    || itemDefinition.name.contains(" lamp")
                    || itemDefinition.name.toLowerCase().contains("max cape")
                    || itemDefinition.name.toLowerCase().contains("infernal ")
                    || itemDefinition.name.toLowerCase().contains("casket")
                    || itemDefinition.name.toLowerCase().contains("clue ")
                    || itemDefinition.name.contains(" key")
                    || itemDefinition.name.toLowerCase().contains("realism ")
                    || itemDefinition.name.toLowerCase().contains("ensouled  ")
                    || itemDefinition.name.toLowerCase().contains("one life ")
                    || itemDefinition.name.toLowerCase().contains("ironman")
                    || (itemDefinition.name.toLowerCase().contains("token") && !itemDefinition.name.equals("Platinum token"))
                    //|| itemDefinition.name.toLowerCase().contains("bones")
                    || itemDefinition.name.contains(" (broken)")
                    || itemDefinition.name.contains(" (damaged)")
                    || itemDefinition.name.contains(" (corrupted)")
                    || itemDefinition.name.contains(" (l)")
                    || itemDefinition.name.contains(" (c)")
                    || itemDefinition.name.toLowerCase().contains("void")
                    || itemDefinition.name.toLowerCase().contains(" yell")
                    || itemDefinition.name.contains("Colorful")
                  //  || itemDefinition.name.toLowerCase().contains(" bone")
                ) continue


                foundItems.add(Item(itemDefinition.id, 1))
            }
        }

        // Process command
        player.packetSender.sendScrollbarHeight(56303, foundItems.size / 10 * 52 + if (foundItems.size % 10 == 0) 0 else 52)
        player.packetSender.sendInterfaceItems(56304, foundItems)
        player.packetSender.sendInterface(56300)
        player.packetSender.sendString(56305, "@gre@ Searching for @whi@[" + itemName + "] @gre@Found " + foundItems.size + " total results")
    }

    override fun canUse(player: Player): Boolean {
        return player.gameMode.isSpawn || PlayerUtil.isDeveloper(player)
    }


    companion object {
        @JvmStatic
        fun handleInterfaceClick(player: Player, message: ItemContainerActionMessage) {
            if (PlayerRights.HIGH_STAFF.contains(player.rights) || player.username.toLowerCase() == "lou"
                    || player.username.toLowerCase() == "3lou 55"
                    || player.username.toLowerCase() == "mod grinder" || player.gameMode.isSpawn) {

                val itemId = message.itemId;
                val clickType = message.opcode

                var amt = 0;
                when (clickType) {
                    145 -> amt = 1
                    117 -> amt = 5
                    43 -> amt = 10
                    129 -> amt = 100
                    135 -> player.sendMessage("<img=745> @red@Your account has not unlocked this feature.")
                }

                //player.sendDevelopersMessage("@red@[Dev] Spawning ItemID: $itemId")
                player.inventory.add(Item(itemId, amt))
            }
        }
    }
}