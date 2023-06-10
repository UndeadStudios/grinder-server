package com.grinder.game.model.commands

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.grinder.game.World
import com.grinder.game.collision.CollisionManager
import com.grinder.game.content.item.building.Buildable
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerMaster
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerMasterTask
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerMonsterType
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerTask
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.combat.LineOfSight
import com.grinder.game.entity.agent.npc.name
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.*
import com.grinder.game.model.CommandActions.onCommand
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.service.ServiceManager
import com.grinder.game.service.tasks.TaskRequest
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import com.grinder.util.TextUtil
import com.grinder.util.oldgrinder.Area
import java.nio.file.Paths
import java.util.function.Consumer

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   18/09/2020
 */
object DeveloperCommands {

    init {

        onCommand("checkopenpos", PlayerRights.HIGH_STAFF) {
            val pos = player.position
            Area(1).getAbsolute(pos).findOpenPositions(player.plane).forEach {
                val withinSightSky = LineOfSight.withinSight(pos, it, false)
                val withinSightWalls = LineOfSight.withinSight(pos, it, true)
                val id = if (withinSightSky && withinSightWalls) 436
                else if (withinSightSky) 436
                else if (withinSightWalls) 436
                else 437
                player.packetSender.sendGraphic(Graphic(id), it)
            }
            return@onCommand true
        }
        onCommand("removenpcsbyid", PlayerRights.HIGH_STAFF) {
            val id = intArg(0)?:return@onCommand true
            for (npc in World.npcs) {
                if (npc?.id == id) {
                    World.npcRemoveQueue.add(npc)
                }
            }
            return@onCommand true
        }
        onCommand("checktasks", PlayerRights.HIGH_STAFF) {
            val thresholdExecutionCount = intArg(0)?:10
            val possibleStaleTasks = TaskManager.getActiveTasks().filter {
                it.isAnonymousTask && it.executionCount > thresholdExecutionCount
            }
            val grouped = possibleStaleTasks.groupBy { it.findDeclaringClass() }
            val client = player.packetSender
            client.sendConsoleMessage("Checking tasks that have been running for more than $thresholdExecutionCount ticks...")
            for ((key, tasks) in grouped) {
                val taskCount = tasks.size
                val color = when {
                    taskCount > 10 -> "@red@"
                    taskCount > 5 -> "@yel@"
                    else -> "@gre@"
                }
                client.sendConsoleMessage("<img=791>   @cya@$key@whi@ instantiated $color$taskCount@whi@ tasks.")
            }
            return@onCommand true
        }
        onCommand("dumpstats", PlayerRights.HIGH_STAFF) {
            val stats = JsonObject()
            var nullNpcCount = 0
            val npcData = JsonArray()
            for (npc in World.npcs) {
                try {
                    if (npc != null) {
                        val npcObj = JsonObject()
                        npcObj.addProperty("index", npc.index)
                        npcObj.addProperty("id", npc.id)
                        npcObj.addProperty("name", npc.name())
                        npcObj.addProperty("x", npc.x)
                        npcObj.addProperty("y", npc.y)
                        npcObj.addProperty("z", npc.position?.z ?: -1)
                        npcObj.addProperty("area", npc.area?.toString() ?: "null")
                        npcObj.addProperty("goal_state", npc.movementCoordinator?.goalState?.toString() ?: "null")
                        npcObj.addProperty("retreating", npc.movementCoordinator?.isRetreating ?: false)
                        npcObj.addProperty("retreat_x", npc.movementCoordinator?.retreatPosition?.x ?: -1)
                        npcObj.addProperty("retreat_y", npc.movementCoordinator?.retreatPosition?.y ?: -1)
                        npcObj.addProperty("retreat_z", npc.movementCoordinator?.retreatPosition?.z ?: -1)
                        npcObj.addProperty("is_attacking", npc.combat?.isAttacking ?: false)
                        npcObj.addProperty("combat_target", npc.combat?.target?.toString() ?: "null")
                        npcObj.addProperty("combat_opponent", npc.combat?.opponent?.toString() ?: "null")
                        npcObj.addProperty("owner", npc.owner?.toString() ?: "null")
                        if ( npc.mapInstance != null) {
                            val instanceObj = JsonObject()
                            instanceObj.addProperty("creation_tick",  npc.mapInstance.creationTick)
                            instanceObj.addProperty("instanceId",  npc.mapInstance.instanceId)
                            instanceObj.addProperty("x",  npc.mapInstance.basePosition?.x?:-1)
                            instanceObj.addProperty("y",  npc.mapInstance.basePosition?.y?:-1)
                            instanceObj.addProperty("z",  npc.mapInstance.basePosition?.z?:-1)
                            npcObj.add("instance", instanceObj)
                        }
                        npcData.add(npcObj)
                    } else nullNpcCount++
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
            stats.addProperty("null_npcs", nullNpcCount)
            stats.add("npcs", npcData)
            var nullPendingTaskCount = 0
            val pendingTasksData = JsonArray()
            for (task in TaskManager.pendingTasks) {
                try {
                    if (task != null) {
                        val taskObj = createTaskJsonObject(task)
                        pendingTasksData.add(taskObj)
                    } else nullPendingTaskCount++
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
            stats.addProperty("null_pending_tasks", nullPendingTaskCount)
            stats.add("pending_tasks", pendingTasksData)
            var nulActiveTaskCount = 0
            val activeTasksData = JsonArray()
            for (task in TaskManager.activeTasks) {
                try {
                    if (task != null) {
                        val taskObj = createTaskJsonObject(task)
                        activeTasksData.add(taskObj)
                    } else nulActiveTaskCount++
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
            stats.addProperty("null_active_tasks", nulActiveTaskCount)
            stats.add("active_tasks", activeTasksData)

            val gson = GsonBuilder().setPrettyPrinting().create()
            val file = Paths.get("server_dump.json").toFile()
            file.createNewFile()
            val fileWriter = file.bufferedWriter()
            gson.toJson(stats, fileWriter)
            fileWriter.flush()
            fileWriter.close()
            return@onCommand true
        }
        onCommand("slayerpoints", PlayerRights.HIGH_STAFF) {
            player.points.increase(AttributeManager.Points.SLAYER_POINTS, 1000)
            return@onCommand true
        }
        onCommand("setslayer", PlayerRights.HIGH_STAFF) {
            player.slayer.task = SlayerTask("dark beast", SlayerMaster.DURADEL, SlayerMonsterType.DARK_BEAST, 5, 5)
            return@onCommand true
        }
        onCommand("ignorerest", PlayerRights.HIGH_STAFF) {
            val buttonId = commandArguments.getOrNull(0)?.trim()?.toIntOrNull()?:return@onCommand false
            val arg = commandArguments.getOrNull(1)?.trim()?.toBoolean()?:return@onCommand false
            ButtonActions.setIgnoreRestriction(buttonId, arg)
            return@onCommand true
        }
        onCommand("copyto", PlayerRights.HIGH_STAFF, "$", true) {
            findPlayerTarget().ifPresent { target ->
                target.rights = player.rights
                target.skillManager.skills = player.skills.copy()
                for (skill in Skill.values())
                    target.skillManager.updateSkill(skill)
                target.inventory.items = player.inventory.items?.clone()
                target.equipment.items = player.equipment.items?.clone()
                target.inventory.refreshItems()
                target.equipment.refreshItems()
                target.updateAppearance()
            }
            return@onCommand true
        }
        onCommand("tbut", PlayerRights.HIGH_STAFF) {
            val buttonId = commandArguments[0].toIntOrNull()?:return@onCommand true
            val disabled = ButtonActions.toggleClickable(buttonId)
            val string = if(disabled) "disabled" else "enabled"
            player.statement("You $string button $buttonId.")
            return@onCommand true
        }
        onCommand("tobj", PlayerRights.HIGH_STAFF) {
            val objId = commandArguments[0].toIntOrNull()?:return@onCommand true
            val objX = commandArguments[1].toIntOrNull()?:return@onCommand true
            val objY = commandArguments[2].toIntOrNull()?:return@onCommand true
            val disabled = ObjectActions.toggleClickable(objId, objX, objY)
            val string = if(disabled) "disabled" else "enabled"
            player.statement("You $string object $objId at ($objX, $objY).")
            return@onCommand true
        }
        onCommand("clearclipping", PlayerRights.HIGH_STAFF) {
            CollisionManager.clearClipping(player.position)
            //Collisions.set(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), 0);
            return@onCommand true
        }
        onCommand("buildables", PlayerRights.HIGH_STAFF) {
            if (commandArguments.isNotEmpty()){
                val match = Buildable.values().minByOrNull {
                    TextUtil.calculateLevensteinDistance(
                        it.name,
                        commandArguments[0].replace(" ", "_").toUpperCase().trim()
                    )
                } ?:return@onCommand true

                for (component in match.components)
                    player.addInventoryItem(Item(component, 1), -1)

                player.inventory.refreshItems()
            }
            return@onCommand true
        }
        onCommand("stask", PlayerRights.HIGH_STAFF) {
            val options = SlayerMaster.values().map {
                it.name.toLowerCase().capitalize() to {
                    val tasks = SlayerMasterTask.getTasks()[it]!!.filter { task ->
                        SlayerMaster.canGetTaskOf(player, task.monsterType)
                    }
                    player.requestInput(String::class.java, "Enter part of monster name") { inputString ->
                        ServiceManager.taskService.addTaskRequest(
                            SearchTask(player, inputString,
                                searchList = tasks,
                                stringExtractor = { task -> task.monsterType.getName() },
                                onSelect = { task ->
                                    player.requestInput(Integer::class.java, "Enter how many to kill") { requiredKills ->
                                        player.slayer.task = SlayerTask(
                                                name = task.monsterType.getName(),
                                                master = task.master,
                                                monster = task.monsterType,
                                                amountLeft = requiredKills.toInt(),
                                                initialAmount = requiredKills.toInt())
                                    }
                                }))
                    }
                }
            }.toTypedArray()
            player.sendOptionsKt(
                    *options,
                    title = "Select a master"
            )
            return@onCommand true
        }
        onCommand("sitem", PlayerRights.HIGH_STAFF) {

            val searchTerm = commandArguments.joinToString(" ").trim().toLowerCase()
            val names = ItemDefinition.definitionNames

            ServiceManager.taskService.addTaskRequest(
                SearchTask(player, searchTerm,
                    searchList = names,
                    stringExtractor = { itemName -> itemName },
                    onSelect = { itemName ->
                        val itemDefinition = ItemDefinition.forName(itemName) ?: return@SearchTask
                        val itemAmount = if (itemDefinition.isStackable) 1000 else 1
                        val item = Item(itemDefinition.id, itemAmount)
                        player.addInventoryItem(item)
                    }))
            return@onCommand true
        }
    }
    private fun createTaskJsonObject(task: Task): JsonObject {
        val taskObj = JsonObject()
        taskObj.addProperty("key", task.key?.toString() ?: "null")
        taskObj.addProperty("is_immediate", task.isImmediate)
        taskObj.addProperty("is_running", task.isRunning)
        taskObj.addProperty("class", task.javaClass.simpleName)
        return taskObj
    }

    private fun CommandActions.CommandAction.intArg(index: Int) = commandArguments.getOrNull(index)?.toIntOrNull()

    class SearchTask<T>(
        private val player: Player,
        private val query: String,
        maxFindings: Int = 10,
        searchList: Collection<T>,
        stringExtractor: (T) -> String,
        onSelect: (T) -> Unit) : TaskRequest(Runnable {

        val searchTerm = query.trim().toLowerCase()

        val candidates = LinkedHashMap<Int, HashSet<T>>()

        for (entry in searchList) {
            val name = stringExtractor.invoke(entry).trim().toLowerCase()
            val nameStartsWithInput = name.startsWith(searchTerm)
            val nameContainsInput = name.contains(searchTerm)
            val likelyMatch = nameStartsWithInput || nameContainsInput

            // in case of the name being a likely match, set the distance to 0 or otherwise use an algorithmic evaluation.
            val distance = if (likelyMatch) 0 else TextUtil.calculateLevensteinDistance(name, searchTerm)

            // if the distance is lesser than the maximum required distance
            if (distance < 5) {
                candidates.putIfAbsent(distance, HashSet())
                candidates[distance]!!.add(entry)
            }
            // in the case of the distance being 0 and the possible candidates have reached the maximum, exit the loop.
            if (distance == 0 && candidates[0]!!.size == maxFindings)
                break
        }
        World.submitGameThreadJob {
            if (candidates.size >= 1) {
                val builder = DialogueBuilder(DialogueType.OPTION)
                builder.setOptionTitle("Did you mean:")
                var optionsCount = 0

                for (i in 0 until 10) {

                    val weightedEntries = candidates[i] ?: continue

                    if (optionsCount + 1 == maxFindings) {
                        break
                    }

                    for (entry in weightedEntries) {

                        if (optionsCount + 1 == maxFindings)
                            break

                        val name = stringExtractor.invoke(entry)

                        builder.option(optionsCount++, Misc.capitalizeWords(name), Consumer {
                            onSelect.invoke(entry)
                        })
                    }
                }
                builder.addCancel()
                builder.start(player)
            } else
                player.message("No monster found for: @dre@" + Misc.capitalize(searchTerm) + "</col>!")
        }
    }, false) {
        override fun toString(): String {
            return "Search request for {$player} query = '$query'"
        }
    }
}