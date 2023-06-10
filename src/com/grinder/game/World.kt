package com.grinder.game

import com.google.common.base.Preconditions
import com.grinder.Server
import com.grinder.game.content.clan.GlobalClanChatManager
import com.grinder.game.content.minigame.blastfurnace.BlastFurnace
import com.grinder.game.content.pvp.bountyhunter.PlayerKillRewardManager
import com.grinder.game.entity.Entity
import com.grinder.game.entity.EntityType
import com.grinder.game.entity.`object`.ClippedMapObjects
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.AgentList
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionProcess
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.PlayerSaving
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.bot.BotPlayer
import com.grinder.game.entity.grounditem.ItemOnGround
import com.grinder.game.entity.updating.sync.ClientSynchronizer
import com.grinder.game.entity.updating.sync.SequentialClientSynchronizer
import com.grinder.game.model.Position
import com.grinder.game.model.StaffLogRelay.broadcastLogs
import com.grinder.game.model.TileGraphic
import com.grinder.game.model.area.Region
import com.grinder.game.model.area.RegionRepository
import com.grinder.game.model.areas.InstanceManager
import com.grinder.game.model.areas.constructed.ConstructedChunk
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.PacketConfiguration
import com.grinder.util.CharacterBackup
import com.grinder.util.benchmark.SimpleBenchMarker
import com.grinder.util.tools.DupeDetector.checkOverhead
import net.runelite.cache.NpcManager
import net.runelite.cache.fs.Store
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer
import java.util.stream.Stream

/**
 * The world keeps track of registered entities and handles all of the game processes.
 *
 * @author Professor Oak
 * @author lare96
 * @author Stan van der Bend
 */
object World {

    private val logger = LogManager.getLogger(World::class.java.simpleName)

    /**
     * A queue of jobs to be executed at the start of the [World.cycle].
     */
    private val gameThreadJobs = ConcurrentLinkedQueue<() -> Unit>()

    private val synchronizer: ClientSynchronizer = SequentialClientSynchronizer()

    @JvmStatic
    val packetMetaData = PacketConfiguration()

    @JvmStatic
    val regions: RegionRepository = RegionRepository.mutable()

    @JvmStatic var tick = 0
    @JvmStatic val players = AgentList<Player>(WorldConstants.MAXIMUM_PLAYERS)
    @JvmStatic val usernameMap = HashMap<String, Player>(WorldConstants.MAXIMUM_PLAYERS)
    @JvmStatic val playerRemovalQueue: Queue<Player> = ConcurrentLinkedQueue()

    @JvmStatic val staffMembers = HashMap<PlayerRights, HashSet<Player>>()

    @JvmStatic val botPlayerLoginQueue: Queue<BotPlayer> = ConcurrentLinkedQueue()

    @JvmStatic val npcs = AgentList<NPC>(WorldConstants.MAXIMUM_NPCS)
    @JvmStatic val npcAddQueue: Queue<NPC> = ConcurrentLinkedQueue()
    @JvmStatic val npcRemoveQueue: Queue<NPC> = ConcurrentLinkedQueue()

    @JvmStatic val constructedMapChunks: List<HashMap<Integer, ConstructedChunk>> = ArrayList()

    @JvmStatic val groundItems: List<ItemOnGround> = LinkedList()
    //@JvmStatic val objects: List<GameObject> = LinkedList()

    @JvmStatic var startPosition: Position = GameConstants.DEFAULT_POSITION.clone()

    lateinit var filestore: Store
    lateinit var npcManager: NpcManager

    @Throws(IOException::class)
    fun initFileStore() {
        filestore = Store(WorldConstants.OLDSCHOOL_STORE_PATH.toFile())
        filestore.load()
        npcManager = NpcManager(filestore)
        npcManager.load()
    }

    @JvmStatic
    fun submitGameThreadJob(function: Function0<Unit>) {
        gameThreadJobs.offer(function)
    }

    /**
     * Executed every [GameConstants.WORLD_CYCLE_PERIOD] millis.
     */
    fun cycle(benchMarker: SimpleBenchMarker) {

        staffMembers.values.forEach(Consumer { list: HashSet<Player> -> list.removeIf { obj: Player? -> Objects.isNull(obj) } })
        sequenceGameThreadJobs()
        benchMarker.mark("sequenceGameThreadJobs()")

        TaskManager.sequence()
        benchMarker.mark("TaskManager.process()")

        sequenceMiscellaneousTasks()
        benchMarker.mark("sequenceMiscellaneousTasks()")

        for (player in players) {
            if (player != null) {
                val session = player.session
                session?.handlePendingPackets()
            }
        }
        benchMarker.mark("players -> handlePendingPackets()")

        sequenceMiscellaneousTasks2()
        benchMarker.mark("sequenceMiscellaneousTasks2()")

        sequenceBotPlayerLoginQueue()
        benchMarker.mark("sequencePlayerLoginQueue(size = " + botPlayerLoginQueue.size + ")")

        sequencePlayerLogoutQueue()
        benchMarker.mark("sequencePlayerLogoutQueue(size = " + playerRemovalQueue.size + ")")

        sequenceAddNpcQueue()
        benchMarker.mark("sequenceAddNpcQueue(size = " + npcAddQueue.size + ")")

        sequenceRemoveNpcQueue()
        benchMarker.mark("sequenceRemoveNpcQueue(size = " + npcRemoveQueue.size + ")")

        synchronizer.synchronize(benchMarker, players, npcs)



        InstanceManager.process()
        benchMarker.mark("InstanceManager.process()")

        try {
            broadcastLogs()
            checkOverhead()
        } catch (e: Exception) {
            Server.getLogger().error("Exception occured in Staff Logs Relaying or Dupe Detector!", e)
        }
        tick++
    }

    private fun sequenceGameThreadJobs() {
        while (true) {
            val job = gameThreadJobs.poll() ?: break
            try {
                job.invoke()
            } catch (e: Exception) {
                logger.error("Error executing game-thread job.", e)
            }
        }
    }

    private fun sequenceRemoveNpcQueue() {
        for (i in 0 until GameConstants.QUEUED_LOOP_THRESHOLD) {
            val next = npcRemoveQueue.poll() ?: break
            remove(next)
        }
    }

    private fun sequenceAddNpcQueue() {
        for (i in 0 until GameConstants.QUEUED_LOOP_THRESHOLD) {
            val next = npcAddQueue.poll() ?: break
            addNpc(next)
        }
    }

    private fun sequenceMiscellaneousTasks() {
        try {
            PlayerUtil.configureDoubleRewardsAndXP()
            PlayerUtil.configureTripleVotingRewards();
            PlayerKillRewardManager.resetKills()
           // MonsterAggressionProcess.evaluateTargetMap()
            BlastFurnace.process()
        } catch (e: Exception) {
            logger.error("Something went wrong while processing miscellaneous tasks!", e)
        }
    }

    private fun sequenceMiscellaneousTasks2() {
        try {
            GlobalClanChatManager.sequence()
            CharacterBackup.sequence()
        } catch (e: Exception) {
            logger.error("Something went wrong while processing miscellaneous tasks 2 !", e)
        }
    }

    /**
     * @apiNote  this is no longer used for queuing player logins, just for bots now.
     */
    private fun sequenceBotPlayerLoginQueue() {
        for (i in 0 until GameConstants.QUEUED_LOOP_THRESHOLD) {
            val next = botPlayerLoginQueue.poll() ?: break
            addPlayer(next)
        }
    }

    private fun sequencePlayerLogoutQueue() {
        val logoutIterator = playerRemovalQueue.iterator()
        while (logoutIterator.hasNext()) {
            val player = logoutIterator.next()
            player.onLogout()
            remove(player)
            logoutIterator.remove()
        }
    }

    /**
     * Spawns the specified [Entity], which must not be a [Player]
     * or an [NPC], which have their own register methods.
     *
     * @param entity The Entity.
     */
    @JvmStatic
    fun spawn(entity: Entity) {
        val type = entity.entityType
        Preconditions.checkArgument(type !== EntityType.PLAYER && type !== EntityType.NPC, "Cannot spawn a Mob.")
        if (entity is GameObject) {
            addObject(entity)
        } else {
            val region = regions.fromPosition(entity.position)
            region.addEntity(entity)
        }
    }

    @JvmStatic
    fun spawn(tileGraphic: TileGraphic) {
        val graphic = tileGraphic.graphic
        val spawnPosition = tileGraphic.position

        val positionArea = spawnPosition.getArea(10)
        val uniqueRegions: MutableSet<Region> = mutableSetOf<Region>()

        for(position in positionArea) {
            val region = World.regions.fromPosition(position)
            uniqueRegions.add(region)
        }

        uniqueRegions.forEach { region ->
            region?.players?.forEach { player ->
                player.packetSender.sendGraphic(graphic, spawnPosition)
            }
        }
    }


    @JvmStatic
    fun addObject(obj: GameObject) {
        val tile = obj.position
        val chunk = regions.fromPosition(tile)

        val oldObj = chunk.getDynamicGameObjects(tile.z).firstOrNull { it.collide(obj) }

        if (oldObj != null) {
            chunk.removeEntity(oldObj)
        }

        chunk.addEntity(obj)
    }

    /**
     * !IMPORTANT! Be careful not to invoke this after npc pre processing.
     */
    fun addNpc(next: NPC) {
        val success = npcs.add(next)
        if (success) {
            val region = regions.fromPosition(next.position)
            region.addEntity(next)
        } else {
            logger.warn("Failed to register npc (capacity reached): [count=" + npcs.size() + "]")
        }
    }

    /**
     * Adds the [player] to the [players] list and to the [Region]
     */
    fun addPlayer(player: Player) {
        val success = players.add(player)
        if (success) {
            usernameMap[player.username.toLowerCase()] = player;
            val region : Region = regions.fromPosition(player.position)
            region.addEntity(player)
            if (player is BotPlayer) {
                player.onLoginComplete()
            }
        } else {
            logger.warn("Failed to register player bot (capacity reached): [count=" + players.size() + "]")
        }
    }


    /**
     * De-spawns the specified [Entity], which must not be a [Player]
     * or an [NPC], which have their own un-register methods.
     *
     * @param entity The Entity.
     */
    @JvmStatic
    fun deSpawn(entity: Entity) {
        val type = entity.entityType
        Preconditions.checkArgument(type !== EntityType.PLAYER && type !== EntityType.NPC, "Cannot spawn a Mob {$entity}.")
        val region = regions.fromPosition(entity.position)
        region.removeEntity(entity)
    }

    @JvmStatic
    fun deSpawnAllAt(position: Position, vararg types: EntityType) {
        val region = regions.fromPosition(position)
        val entityIterator = region
                .getEntities<Entity>(position, *types)
                .iterator()
        while (entityIterator.hasNext()) {
            val entity = entityIterator.next()
            region.removeEntity(entity)
            entityIterator.remove()
        }
    }

    @JvmStatic
    fun isSpawned(gameObject: GameObject): Boolean {
        val position = gameObject.position
        val region = regions.fromPosition(position)
        return region.getEntities<Entity>(position,
                EntityType.STATIC_OBJECT,
                EntityType.DYNAMIC_OBJECT
        ).contains(gameObject)
    }

    @JvmStatic
    fun remove(npc: NPC) {
        val region = regions.fromPosition(npc.position)
        region.removeEntity(npc)
        npcs.remove(npc)
    }

    @JvmStatic
    fun remove(player: Player) {
        val region = regions.fromPosition(player.position)
        region.removeEntity(player)
        val rights = player.rights
        if (rights.isStaff) {
            val staffPlayers = staffMembers[rights]
            staffPlayers?.remove(player)
        }
        players.remove(player)
        usernameMap.remove(player.username.toLowerCase());
    }

    fun playAreaSound(sound: AreaSound?) {
        //TODO: implement
        System.err.println("World.playAreaSound -> UNIMPLEMENTED!")
    }

    /**
     * Saves all players in the game.
     */
    fun savePlayers() {
        players.forEach(Consumer { player: Player? -> PlayerSaving.save(player) })
    }

    @JvmStatic
    fun countPlayersOnline(): Int {
        return players.size()
    }

    @JvmStatic
    fun playerStream(): Stream<Player> {
        return players.stream().filter { obj: Player? -> Objects.nonNull(obj) }
    }

    private fun npcStream(): Stream<NPC> {
        return npcs.stream().filter { obj: NPC? -> Objects.nonNull(obj) }
    }

    fun findNpc(position: Position?): Optional<NPC> {
        return regions.fromPosition(position)
                .getEntities<Entity>(position, EntityType.NPC)
                .stream()
                .findAny()
                .map { obj: Entity -> obj.asNpc }
    }

    @JvmStatic
    fun findNpcById(id: Int): Optional<NPC> {
        return npcStream().filter { npc: NPC -> npc.fetchDefinition().id == id }.findAny()
    }

    @JvmStatic
    fun findNpcById(id: Int, height: Int): Optional<NPC> {
        return npcStream().filter { npc: NPC -> npc.fetchDefinition().id == id && npc.position.z == height }.findAny()
    }

    @JvmStatic
    fun findPlayerByName(username: String?): Optional<Player> {
        return Optional.ofNullable(usernameMap[username?.toLowerCase()]);
        //return playerStream().filter { p: Player -> p.username.equals(Misc.formatText(username), ignoreCase = true) }.findAny()
    }

    @JvmStatic
    fun findObject(player: Player, objectId: Int, position: Position) : Optional<GameObject> {
        var obj = player.farming.findFarmingObject(position)
        if (obj.isEmpty)
            obj = player.area?.getObject(player, objectId, position)?: Optional.empty()
        if (obj.isEmpty)
            obj = ClippedMapObjects.findObject(objectId, position.clone().setZ(position.z % 4))
        //if (obj.isEmpty)
           // obj = player.getLocalObject(objectId, position)?: Optional.empty()
        return obj
    }
}