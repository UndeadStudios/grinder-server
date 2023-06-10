package com.grinder.game.content.skill.skillable.impl.mining

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.miscellaneous.randomevent.RandomEvents.triggerRefreshments
import com.grinder.game.content.skill.skillable.DefaultSkillable
import com.grinder.game.content.skill.skillable.impl.mining.GemType.Companion.generateGemType
import com.grinder.game.content.skill.task.SkillMasterType
import com.grinder.game.content.skill.task.SkillTaskManager
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.model.Skill
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.TimedObjectReplacementTask
import com.grinder.util.Misc
import java.util.*

/**
 * @author Zach (zach@findzach.com)
 * @since 12/17/2020 - Converted to Kotlin & Adjusted for GrinderScape
 */
class Mining(private val player: Player, val rockObject: GameObject) : DefaultSkillable() {

    private val EMPTY_ROCK = 2704

    val pickaxeType: Optional<PickaxeType>?
        get() {
           return findPickaxe(player)
        }

    val rock: RockType
        get() {
            return getRock(rockObject)
        }


    override fun start(player: Player) {
        player.packetSender.sendMessage("You swing your pickaxe at the rock..")
        super.start(player)
    }

    override fun startAnimationLoop(player: Player?) {
    }

    override fun startGraphicsLoop(player: Player?) {
    }

    override fun startSoundLoop(player: Player?) {
    }

    override fun allowFullInventory(): Boolean {
        TODO("Not yet implemented")
    }

    override fun finishedCycle(player: Player?) {

        if (Misc.getRandomInclusive(200) == 1) {
            triggerRefreshments(player)
            cancel(player)
            return
        }
        // Add ores..
        // Add ores..
        if (rock == RockType.ESSENCE) {
            if (player!!.skillManager.getMaxLevel(Skill.MINING) < 30) {
                player.inventory.add(1436, 1)
            } else {
                player.inventory.add(7936, 1)
            }
            player.packetSender.sendAreaPlayerSound(Sounds.ROCK_MINED_SOUND)
            player.skillManager.addExperience(Skill.MINING, rock.xpReward as Int)
            return
        }

        // Task
        AchievementManager.processFor(AchievementType.HEAVY_WORK, player)
        if (rock.oreId == 440) {
            AchievementManager.processFor(AchievementType.WET_WORK, player)
        }
        if (rock.oreId == 453) {
            AchievementManager.processFor(AchievementType.MINING_THE_ESSENTIALS, player)
        }

        player!!.packetSender.sendMessage("You get some ores.")

        player!!.packetSender.sendAreaPlayerSound(Sounds.ROCK_MINED_SOUND)

        // Add exp..


        // Add exp..
        val infernalPickaxe = (player.equipment.contains(13243) || player!!.inventory.contains(13243)) && Misc.getRandomInclusive(4) == 1

        /*
        if (infernalPickaxe && pickaxe.get().id == 13243 and rock.oreId != 453 && rock !== RockType.GEM) {
            when (rock.oreId) {
                436, 438, 11360, 11361, 11362, 11363 -> {
                    player!!.inventory.add(2349, 1)
                    player!!.skillManager.addExperience(Skill.SMITHING, 6)
                }
                668 -> {
                    player!!.inventory.add(9467, 1)
                    player!!.skillManager.addExperience(Skill.SMITHING, 16)
                }
                440, 11364, 11365 -> {
                    player!!.inventory.add(2351, 1)
                    player!!.skillManager.addExperience(Skill.SMITHING, 19)
                }
                442, 11368, 11369 -> {
                    player!!.inventory.add(2355, 1)
                    player!!.skillManager.addExperience(Skill.SMITHING, 22)
                }
                444, 11370, 11371 -> {
                    player!!.inventory.add(2357, 1)
                    player!!.skillManager.addExperience(Skill.SMITHING, 70)
                }
                447, 11372, 11373 -> {
                    player!!.inventory.add(2359, 1)
                    player!!.skillManager.addExperience(Skill.SMITHING, 65)
                }
                449, 11374, 11375 -> {
                    player!!.inventory.add(2361, 1)
                    player!!.skillManager.addExperience(Skill.SMITHING, 82)
                }
                451, 11376, 11377 -> {
                    player!!.inventory.add(2363, 1)
                    player!!.skillManager.addExperience(Skill.SMITHING, 110)
                }
            }
            if (player!!.equipment.contains(13243)) {
                player!!.itemDegradationManager.degrade(DegradingType.SKILLING, -1)
            } else {
                player!!.itemDegradationManager.degradeInventoryItems(DegradingType.SKILLING, -1, 13243)
            }
            player!!.performGraphic(Graphic(86))
        } else {

         */
            if (rock === RockType.GEM) {
                player!!.inventory.add(generateGemType().uncutId, 1)
            } else {
                player!!.inventory.add(rock.oreId, 1)
            }


        player!!.skillManager.addExperience(Skill.MINING, rock.xpReward as Int)

        SkillTaskManager.perform(player, rock.oreId, 1, SkillMasterType.MINING)

        if (Misc.getRandomInclusive(rock.cycles + 5) > 2) {
            return
        }
        // Despawn object and respawn it after a short period of time..
        // Despawn object and respawn it after a short period of time..
        TaskManager.submit(TimedObjectReplacementTask(rockObject,
                DynamicGameObject.createPublic(EMPTY_ROCK, rockObject.getPosition(), rockObject.getObjectType(), rockObject.getFace()),
                rock.respawnTimer))

        // Stop skilling..

        // Stop skilling..
        cancel(player)

    }

    /**
     * Finds the rock that we are working with
     *
     * @return the RockType
     * @see RockType
     */
    fun getRock(gameObject: GameObject): RockType {
        for (rock in RockType.values()) {
            for (objectId in rock.objects) {
                if (objectId == gameObject.id) {
                    return rock;
                }
            }
        }
        return RockType.NO_ORES;
    }

    fun findPickaxe(player: Player): Optional<PickaxeType>? {
        var pickaxe: Optional<PickaxeType> = Optional.empty()
        for (a in PickaxeType.values()) {
            if (player.equipment.items[EquipmentConstants.WEAPON_SLOT].id == a.id
                    || player.inventory.contains(a.id)) {

                // If we have already found a pickaxe,
                // don't select others that are worse or can't be used
                if (pickaxe.isPresent) {
                    if (player.skillManager.getMaxLevel(Skill.MINING) < a.requiredLevel) {
                        continue
                    }
                    if (a.requiredLevel < pickaxe.get().requiredLevel) {
                        continue
                    }
                }
                pickaxe = Optional.of(a)
            }
        }
        return pickaxe
    }


    override fun loopRequirements(): Boolean {
        TODO("Not yet implemented")
    }

    override fun cyclesRequired(player: Player?): Int {
        TODO("Not yet implemented")
    }
}