package com.grinder.game.model.areas.impl

import com.google.common.collect.ArrayListMultimap
import com.grinder.Server
import com.grinder.game.World
import com.grinder.game.World.regions
import com.grinder.game.content.clan.ClanChat
import com.grinder.game.content.miscellaneous.TravelSystem
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.impl.*
import com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast.CorporealBeastBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl.FlightKilisa
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl.FlockleaderGeerin
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl.KreeArraBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl.WingmanSkree
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.GeneralGraardorBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.SergeantGrimspike
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.SergeantSteelwill
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.SergeantStrongstack
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.Bree
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.CommanderZilyanaBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.Growler
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.Starlight
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak.BalfrugKreeyath
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak.KrilTsutsarothBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak.TstanonKarlak
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak.ZaklnGritch
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.decInt
import com.grinder.game.model.Animation
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import com.grinder.game.model.area.Region
import com.grinder.game.model.areas.ClanInstancedBossArea
import com.grinder.game.model.areas.InstancedArea
import com.grinder.game.model.areas.UntypedInstancedBossArea
import com.grinder.game.model.areas.instanced.SkeletonHellhoundInstance
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import java.util.concurrent.atomic.AtomicInteger

enum class BossInstances(val boss: (Int) -> Boss,
                         val mobs: List<(Boss, Int) -> Monster> = emptyList(),
                         val onEnter: (Player) -> Unit = fun(_) {},
                         val boundary: Boundary = boss(0).position.createSquareBoundary(30)[0]) {
    ICE_TROLL(
            boss = { IceTrollKingBoss(6356, Position(2856, 3809, it + 1)) },
        mobs = listOf(
            { boss, it -> IceTrollKingBoss.IceWolf(boss as IceTrollKingBoss, Position(2860, 3803, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceWolf(boss as IceTrollKingBoss, Position(2858, 3801, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceWarrior(boss as IceTrollKingBoss, Position(2852, 3800, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceWarrior(boss as IceTrollKingBoss, Position(2849, 3802, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceWarrior(boss as IceTrollKingBoss, Position(2844, 3811, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceSpider(boss as IceTrollKingBoss, Position(2845, 3813, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceSpider(boss as IceTrollKingBoss, Position(2847, 3818, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceSpider(boss as IceTrollKingBoss, Position(2853, 3819, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceSpider(boss as IceTrollKingBoss, Position(2858, 3820, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceWarrior(boss as IceTrollKingBoss, Position(2864, 3816, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceSpider(boss as IceTrollKingBoss, Position(2866, 3811, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceWarrior(boss as IceTrollKingBoss, Position(2868, 3810, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceWarrior(boss as IceTrollKingBoss, Position(2868, 3804, 1 + it)) },
            { boss, it -> IceTrollKingBoss.IceWarrior(boss as IceTrollKingBoss, Position(2867, 3800, 1 + it)) }
        ),
            boundary = Boundary(2815, 2895, 3775, 3845)),
    /*ICE_QUEEN(
            boss = { IceQueenBoss(4922, Position(2866, 9954, it)) },
        mobs = listOf(
            { boss, it -> IceQueenBoss.IceWarrior(boss as IceQueenBoss, Position(2871, 9960, it)) },
            { boss, it -> IceQueenBoss.IceWarrior(boss as IceQueenBoss, Position(2860, 9961, it)) },
            { boss, it -> IceQueenBoss.IceWarrior(boss as IceQueenBoss, Position(2858, 9952, it)) },
            { boss, it -> IceQueenBoss.IceWarrior(boss as IceQueenBoss, Position(2874, 9945, it)) },
            { boss, it -> IceQueenBoss.IceWarrior(boss as IceQueenBoss, Position(2865, 9939, it)) }
        ),
        boundary = Boundary(2855, 2877, 9919, 9964)),*/
    KALPHITE_QUEEN(
            boss = { KalphiteQueenBoss(6500, Position(3473, 9495, it)) },
            mobs = listOf(
                    { boss, it -> KalphiteQueenBoss.KalphiteSoldier(boss as KalphiteQueenBoss, Position(3488, 9489, it)) },
                    { boss, it -> KalphiteQueenBoss.KalphiteSoldier(boss as KalphiteQueenBoss, Position(3492, 9489, it)) },
                    { boss, it -> KalphiteQueenBoss.KalphiteSoldier(boss as KalphiteQueenBoss, Position(3479, 9494, it)) }
            ),
            boundary = Boundary(3465, 9494, 9480, 9518)),
    SEA_TROLL(
            boss = { SeaTrollQueenBoss(4315, Position(2504, 3902, it)) },
            boundary = Boundary(2497, 2510, 3892, 3900)),
    //KAMIL(
    //        boss = { KamilBoss(3458, Position(2857, 3731, it)) }),
    CORP(
            boss = { CorporealBeastBoss(319, Position(2984, 4383, it + 2)) },
            boundary = Boundary(2974, 2998, 4371, 4396)),
    MUTANT_TARN(
            boss = { MutantTarnBoss(6477, Position(3148, 4652, it)) },
        mobs = listOf(
            { boss, it -> MutantTarnBoss.TarnBigTerrorDog(boss as MutantTarnBoss, Position(3144, 4646, it)) },
            { boss, it -> MutantTarnBoss.TarnBigTerrorDog(boss as MutantTarnBoss, Position(3150, 4646, it)) },
            { boss, it -> MutantTarnBoss.TarnBigTerrorDog(boss as MutantTarnBoss, Position(3144, 4645, it)) },
            { boss, it -> MutantTarnBoss.TarnSpider(boss as MutantTarnBoss, Position(3144, 4648, it)) }
            ),
                    boundary = Boundary(3138, 3155, 4642, 4661)),
    BLACK_KNIGHT_TITAN(
            boss = { BlackKnightTitanBoss(4067, Position(2565, 9511, it), true) },
        mobs = listOf(
        { boss, it -> BlackKnightTitanBoss.BKTChaosDruidWarrior(boss as BlackKnightTitanBoss, Position(2579, 9503, it)) },
            { boss, it -> BlackKnightTitanBoss.BKTChaosDruidWarrior(boss as BlackKnightTitanBoss, Position(2575, 9499, it)) },
            { boss, it -> BlackKnightTitanBoss.BKTChaosDruidWarrior(boss as BlackKnightTitanBoss, Position(2586, 9497, it)) },
            { boss, it -> BlackKnightTitanBoss.BKTGiantBat(boss as BlackKnightTitanBoss, Position(2578, 9508, it)) },
            { boss, it -> BlackKnightTitanBoss.BlackKnightNonCombat(boss as BlackKnightTitanBoss, Position(2569, 9509, it)) },
            { boss, it -> BlackKnightTitanBoss.BKTBlackKnight(boss as BlackKnightTitanBoss, Position(2564, 9514, it)) },
            { boss, it -> BlackKnightTitanBoss.BKTBlackKnight(boss as BlackKnightTitanBoss, Position(2564, 9508, it)) }
        ),
        boundary = Boundary(2559, 2573, 9504, 9518)),
    JUNGLE_DEMON(
            boss = { JungleDemonBoss(1443, Position(2715, 9178, 1 + it)) },
            mobs = listOf(
                    { _, it -> JungleDemonBoss(1443, Position(2728, 9192, 1 + it)) },
                    { _, it -> JungleDemonBoss(1443, Position(2712, 9200, 1 + it)) },
                    { _, it -> JungleDemonBoss(1443, Position(2702, 9185, 1 + it)) }),
            boundary = Boundary(2689, 2743, 9157, 9213)
    ),
    /*SEA_SNAKE(
            boss = { GiantSeaSnakeBoss(1101, Position(2460, 4784, it)) },
            mobs = listOf(
                    { _, it -> GiantSeaSnakeBoss(1101, Position(2457, 4781, it)) },
                    { _, it -> GiantSeaSnakeBoss(1101, Position(2465, 4778, it)) }),
            boundary = Boundary(2452, 2476, 4769, 4791)
    ),*/
    KRIL(
            boss = { KrilTsutsarothBoss(3129, Position(2927, 5325, it + 2), true) },
            mobs = listOf(
                    { boss, _ -> ZaklnGritch(boss as KrilTsutsarothBoss) },
                    { boss, _ -> TstanonKarlak(boss as KrilTsutsarothBoss) },
                    { boss, _ -> BalfrugKreeyath(boss as KrilTsutsarothBoss) }),
            onEnter = { p ->
                if (p.inventory.contains(11942))
                    p.inventory.delete(Item(11942))
                else
                    p.decInt(Attribute.ZAMORAK_KILL_COUNT, if (PlayerUtil.isMember(p)) 5 else 20)
            },
            boundary = Boundary(2918, 2936, 5318, 5331)
    ),
    ZILYANA(
            boss = { CommanderZilyanaBoss(2205, Position(2898, 5265, it), true) },
            mobs = listOf(
                    { boss, _ -> Bree(boss as CommanderZilyanaBoss) },
                    { boss, _ -> Growler(boss as CommanderZilyanaBoss) },
                    { boss, _ -> Starlight(boss as CommanderZilyanaBoss) }),
            onEnter = { p ->
                if (p.inventory.contains(11942))
                    p.inventory.delete(Item(11942))
                else
                    p.decInt(Attribute.SARADOMIN_KILL_COUNT, if (PlayerUtil.isMember(p)) 5 else 20) },
            boundary = Boundary(2889, 2907, 5258, 5275)
    ),
    GRAARDOR(
            boss = { GeneralGraardorBoss(2215, Position(2872, 5363, it + 2), true) },
            mobs = listOf(
                    { boss, _ -> SergeantGrimspike(boss as GeneralGraardorBoss) },
                    { boss, _ -> SergeantSteelwill(boss as GeneralGraardorBoss) },
                    { boss, _ -> SergeantStrongstack(boss as GeneralGraardorBoss) }),
            onEnter = { p ->
                if (p.inventory.contains(11942))
                    p.inventory.delete(Item(11942))
                else
                    p.decInt(Attribute.BANDOS_KILL_COUNT, if (PlayerUtil.isMember(p)) 5 else 20) },
            boundary = Boundary(2864, 2876, 5351, 5369)
    ),
    KREE(
            boss = { KreeArraBoss(3162, Position(2828, 5302, it + 2), true) },
            mobs = listOf(
                    { boss, _ -> FlightKilisa(boss as KreeArraBoss) },
                    { boss, _ -> FlockleaderGeerin(boss as KreeArraBoss) },
                    { boss, _ -> WingmanSkree(boss as KreeArraBoss) }),
            onEnter = { p ->
                if (p.inventory.contains(11942))
                    p.inventory.delete(Item(11942))
                else
                    p.decInt(Attribute.ARMADYL_KILL_COUNT, if (PlayerUtil.isMember(p)) 5 else 20) },
            boundary =  Boundary(2824, 2842, 5296, 5308)
    )

    ;

    /**
     * Creates an [UntypedInstancedBossArea] containing the [boss] at the player's height.
     */
    fun createInstance(player: Player, height: Int): InstancedArea? {

        val b = boss(height)

        var npcExists = false

        regions.forRegion(height, boundary) { region: Region? ->
            run {
                if (region != null) {
                    if (region.npcs.filterNotNull().any { it.position.z == height && it.id == b.id })
                        npcExists = true
                }
            }
        }

        if (npcExists) {
            println("Returned null in BossInstance.createInstance()")
            return null
        }


        val area = UntypedInstancedBossArea(b, boundary)
        area.enter(player)

        // Add any other mobs in the area
        mobs.forEach {
            val mob = it(b, height)
            World.npcAddQueue.add(mob)
            area.enter(mob)
        }

        return area
    }

    /**
     * Checks if a [ClanInstancedBossArea] of the correct type owned by the given clan exists.
     */
    fun clanInstanceExists(clan: ClanChat): Boolean {
        val instances = clanInstances[clan.index]
        instances ?: return false

        val instance = instances.find { it.instanceType == this }
        instance ?: return false

        return if (!instance.active) {
            clanInstances.remove(clan.index, instance)
            false
        } else {
            true
        }
    }

    /**
     * Get an existing [ClanInstancedBossArea] or create a new one and add it to [clanInstances].
     */
    fun getOrCreateClanInstance(player: Player): ClanInstancedBossArea {

        if (clanInstanceExists(player.currentClanChat))
            return clanInstances[player.currentClanChat.index]!!.find { it.instanceType == this }!!

        val height = getHeight(player.currentClanChat)
        val b = boss(height)
        b.spawnPosition.z =height
        b.position.z =height
        val area = ClanInstancedBossArea(b, boundary, player.currentClanChat, this)

        // Add any other mobs in the area
        mobs.forEach {
            val mob = it(b, height)
            World.npcAddQueue.add(mob)
        }

        clanInstances.put(player.currentClanChat.index, area)
        return area
    }

    companion object {

        /**
         *  Cost of creating a personal instance.
         */
        const val PERSONAL_INSTANCE_COST = 10_000_000

        /**
         *  Cost of creating a personal instance.
         */
        const val IRONMAN_INSTANCE_COST = 2_500_000

        /**
         *  Cost of creating a personal instance.
         */
        const val CLAN_INSTANCE_COST = 20_000_000

        /**
         * Map of all [ClanInstancedBossArea] indexed by the owning [ClanChat] index.
         */
        val clanInstances: ArrayListMultimap<Int, ClanInstancedBossArea> = ArrayListMultimap.create()

        private val playerInstances = HashMap<String, Int>()

        private val heightIndex = AtomicInteger(4)

        /**
         * Calculate the unique height of the instanced, based off account id.
         */
        private fun nextInstanceHeightOffset(player: Player): Int {
            return playerInstances.getOrPut(player.username!!) {
                val currentHeight = heightIndex.get()
                if (currentHeight.toLong() + 4L >= Integer.MAX_VALUE)
                    Server.getLogger().error("Next instance height exceeds max instance height {${Integer.MAX_VALUE}}!")
                else
                    heightIndex.set(currentHeight + 4)
                currentHeight
            }
        }

        fun getCost(player :Player) : Int{
            return if(player.gameMode.isAnyIronman)
                IRONMAN_INSTANCE_COST
            else
                PERSONAL_INSTANCE_COST
        }

        /**
         * Calculate the unique height of the clan instanced, based off clan index.
         */
        fun getHeight(clan: ClanChat) =  4 * ((clan.index + 1) * 2)

        /**
         * Begin the dialogue with options to create an instance of not.
         */
        fun instanceDialogue(player: Player, position: Position, instance: BossInstances, moveTo: Boolean) {

            val dialogue = DialogueBuilder(DialogueType.OPTION)
            dialogue.firstOption("Enter the boss room.") {
                if (instance.name == "JUNGLE_DEMON") {
                    it.performAnimation(Animation(844))
                    it.packetSender.sendInterfaceRemoval()
                    TaskManager.submit(object : Task(2, player, false) {
                        override fun execute() {
                            it.moveTo(position.copy())
                            instance.onEnter(it)
                            it.removeInterfaces()
                            stop()
                        }
                    })
               // } else if (instance.name == "BLACK_KNIGHT_TITAN") {
               //     Agility.handleObstacle(player, gameObject)
                } else {
                    it.moveTo(position.copy())
                    instance.onEnter(it)
                    it.removeInterfaces()
                }
            }
            if (player.gameMode.isAnyIronman) {
                dialogue.secondOption("Create personal instance (${(IRONMAN_INSTANCE_COST / 1e6).toInt()}m)") {
                        createPersonalInstance(it, IRONMAN_INSTANCE_COST, moveTo, position, instance)
                    }
            } else {
                dialogue.secondOption("Create personal instance (${(PERSONAL_INSTANCE_COST / 1e6).toInt()}m)") {
                        createPersonalInstance(it, PERSONAL_INSTANCE_COST, moveTo, position, instance)
                    }
                // Option to join / create a clan isntance if the player is in a clan
                if (player.currentClanChat != null) {
                    // An instance already exists - can be joined
                    if (instance.clanInstanceExists(player.currentClanChat)) {
                        dialogue.thirdOption("Join clan instance ") {
                            it.statement("Clan instances are currently disabled.")
//                            TeleportHandler.teleport(it, getClanPosition(position, player), TeleportType.XERIC_TALISMAN, false, true)
//                            {
//                                instance.onEnter(it)
//                                instance.getOrCreateClanInstance(it).add(it)
//                            }
                        }
                    } else {
                        dialogue.thirdOption("Create clan instance (${(CLAN_INSTANCE_COST / 1e6).toInt()}m)") {
                            it.statement("Clan instances are currently disabled.")
//                            if (player.removeInventoryItem(Item(995, CLAN_INSTANCE_COST))) {
//                                val destination = getClanPosition(position, player)
//                                if (moveTo) {
//                                    TravelSystem.fadeTravel(it,
//                                            screenMessage = "Creating a boss instance room..",
//                                            destination = destination)
//                                    {
//                                        instance.onEnter(it)
//                                        instance.getOrCreateClanInstance(it).add(it)
//                                    }
//                                } else
//                                    TeleportHandler.teleport(it,
//                                            destination,
//                                            TeleportType.XERIC_TALISMAN,
//                                            false,
//                                            true)
//                                    {
//                                        instance.onEnter(it)
//                                        instance.getOrCreateClanInstance(it).add(it)
//                                    }
//                            } else
//                                it.statement("You do not have enough coins.")
                        }
                    }
                }
            }
            dialogue.addCancel()
                    .start(player)
        }

        private fun getClanPosition(position: Position, player: Player) =
                position.copy().add(0, 0, getHeight(player.currentClanChat))

        fun createPersonalInstance(player: Player, cost: Int, moveTo: Boolean, position: Position, instance: BossInstances) {
            if (player.removeInventoryItem(Item(995, cost))) {
                val height = nextInstanceHeightOffset(player)
                val destination =  position.copy().add(0, 0, height)
                if (moveTo) {
                    player.performAnimation(Animation(2239))
                    player.packetSender.sendInterfaceRemoval()
                    TravelSystem.fadeTravel(player,
                            screenMessage = "Creating a boss instance room..",
                            destination = destination)
                    {
                        TaskManager.submit(object : Task(2, player, false) {
                            override fun execute() {
                                player.resetAnimation()
                                stop()
                            }
                        })
                        createAndEnterInstance(instance, player, height)
                        TaskManager.submit(object : Task(3, player, false) {
                            override fun execute() {
                                player.resetAnimation()
                                stop()
                            }
                        })
                    }
                } else {
                    TeleportHandler.teleport(
                        player,
                        destination,
                        TeleportType.XERIC_TALISMAN,
                        false,
                        true
                    ) { createAndEnterInstance(instance, player, height) }
                }
            } else
                player.statement("You do not have enough coins.")
        }

        private fun createAndEnterInstance(instance: BossInstances, player: Player, height: Int) {
            instance.onEnter(player)
            instance.createInstance(player, height)
        }

        /**
         * Begin the dialogue for creating a skeleton hellhound instance.
         */
        fun skeletonHellhoundDialogue(player: Player, position: Position, moveTo: Boolean) {
            DialogueBuilder(DialogueType.OPTION)
                    .firstOption("Enter the boss room.") {
                        it.moveTo(position.copy())
                        it.removeInterfaces()
                        // TODO: Make the gate open, walk player, then close
                    }
                    .secondOption("Create personal instance (${(PERSONAL_INSTANCE_COST / 1e6).toInt()}m)") {
                        if (player.removeInventoryItem(Item(995, PERSONAL_INSTANCE_COST))) {
                            val height = nextInstanceHeightOffset(player)
                            if (moveTo) {
                                player.performAnimation(Animation(2239))
                                player.packetSender.sendInterfaceRemoval()
                                TravelSystem.fadeTravel(player,
                                        screenMessage = "Creating a boss instance room..",
                                        destination = position.copy().add(0, 0, height))
                                {
                                    TaskManager.submit(object : Task(2, player, false) {
                                        override fun execute() {
                                            player.resetAnimation()
                                            stop()
                                        }
                                    })
                                    SkeletonHellhoundInstance(height).enter(player)
                                }
                            } else
                                TeleportHandler.teleport(player,
                                        position.copy().add(0, 0, height),
                                        TeleportType.XERIC_TALISMAN,
                                        false,
                                        true)
                                { SkeletonHellhoundInstance(height).enter(player) }
                        } else
                            player.statement("You do not have enough coins.")
                    }.start(player)
        }

    }

}
