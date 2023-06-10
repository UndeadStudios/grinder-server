package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.collision.CollisionManager
import com.grinder.game.content.miscellaneous.pickables.PickableType
import com.grinder.game.content.miscellaneous.pickables.Pickables
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.onEventEvery
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.event.impl.PlayerLoginEvent
import com.grinder.game.model.Animation
import com.grinder.game.model.NPCActions
import com.grinder.game.model.PlayerActions
import com.grinder.game.model.Position
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.NpcID
import org.apache.commons.lang.time.DateUtils
import java.util.*
import java.util.function.Consumer

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 1:11 AM
 * @discord L E G E N D#4380
 */
class FlaxKeeper(id: Int, position: Position) : Monster(id, position) {

    init {
        onEventEvery(13..16, MonsterEvents.PRE_SEQUENCE) {
            pickFlax()
        }
    }

    private fun pickFlax() {
        findRandomFlax().ifPresent { flaxObject: GameObject ->
            motion.enqueuePathToWithoutCollisionChecks(flaxObject.x, flaxObject.y)
            TaskManager.submit(1) {
                if (position == flaxObject.position) {
                    performAnimation(Animation(827))
                    TaskManager.submit(1) {
                        Pickables.rollRemove(flaxObject, PickableType.FLAX)
                    }
                }
            }
        }
    }

    private fun findRandomFlax(): Optional<GameObject> {
        val checkOtherSide = Misc.randomBoolean()
        val farAway = Misc.random(3) % 3 == 0
        if (farAway) {
            for (x in position.x + 20 downTo position.x - Misc.randomInclusive(1, 20) + 1) {
                for (y in position.y + (if (checkOtherSide) 20 else 0) downTo position.y - Misc.randomInclusive(
                    1,
                    20
                ) + 1) {
                    val position = Position(x, y, position.z)
                    if (position == getPosition()) {
                        continue
                    }
                    val flax = Pickables.getFlax(position)
                    if (flax.isPresent) {
                        if (CollisionManager.canMove(getPosition(), face.ordinal)) {
                            return flax
                        }
                    }
                }
            }
        }
        for (x in position.x - (if (checkOtherSide) 20 else 0) until position.x + Misc.randomInclusive(1, 20)) {
            for (y in position.y - (if (checkOtherSide) 20 else 0) until position.y + Misc.randomInclusive(1, 20)) {
                val position = Position(x, y, position.z)
                if (position == getPosition()) {
                    continue
                }
                val flax = Pickables.getFlax(position)
                if (flax.isPresent) {
                    if (CollisionManager.canMove(getPosition(), face.ordinal)) {
                        return flax
                    }
                }
            }
        }
        return Optional.empty()
    }

    override fun attackRange(type: AttackType) = 0

    companion object {

        init {
            PlayerActions.onEvent(PlayerLoginEvent::class) {
                if (!player.isNewPlayer) {
                    if (!hasUsedDailyFlax(player))
                        player.sendMessage("<img=779><col=ff0000> You have bowstrings waiting to be converted from flax from the Flax Keeper.")

                    player.sendMessage("<img=779><col=ff0000> You have battlestaves waiting to be collected from Zaff.")
                }
            }
        }

        private fun exchange(player: Player) {
            val flaxAmount = getFlaxDailyAmount(player)
            DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.FLAX_KEEPER)
                .setText(
                    "Hello. Do you want to exchange some flax banknotes",
                    "for bowstring? I'll exchange $flaxAmount for you today, and",
                    "you can come back tomorrow for more."
                )
                .setNext(
                    DialogueBuilder(DialogueType.OPTION)
                        .setOptionTitle("Swap $flaxAmount flax notes for bowstring?")
                        .firstOption("Agree.", Consumer {
                            if (player.inventory.countFreeSlots() > 0) {
                                DialogueBuilder()
                                    .setText("The flax keeper exchanges your flax for bowstrings.")
                                    .start(player)
                                player.lastDailyFlax = System.currentTimeMillis()
                                player.inventory.delete(ItemID.FLAX_2, flaxAmount)
                                player.inventory.add(ItemID.BOW_STRING_2, flaxAmount)
                            } else {
                                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.FLAX_KEEPER)
                                    .setText(
                                        "Umm... Actually, you seem to have so much stuff that",
                                        "you can't hold the bowstring."
                                    )
                                    .start(player)
                            }
                        })
                        .secondOption("Decline.", Consumer { `$`: Player? -> DialogueManager.start(player, -1) })
                ).start(player)
        }

        private fun hasUsedDailyFlax(player: Player): Boolean {
            val current = Date()
            val last = Date()
            current.time = System.currentTimeMillis()
            last.time = player.lastDailyFlax
            return DateUtils.isSameDay(current, last)
        }

        private fun getFlaxDailyAmount(player: Player?): Int {
            return if (PlayerUtil.isTopazMember(player)) 400 else 250
        }

        init {
            NPCActions.onClick(NpcID.FLAX_KEEPER) { action ->
                val player: Player = action.player
                val flaxAmount = getFlaxDailyAmount(player)
                if (hasUsedDailyFlax(player)) {
                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.FLAX_KEEPER)
                        .setText(
                            "Hello again. I'm afraid I won't be able to exchange any",
                            "more flax for bowstrings today. Try me again",
                            "tomorrow."
                        )
                        .start(player)
                    return@onClick true
                }
                if (action.type === NPCActions.ClickAction.Type.FIRST_OPTION) {
                    if (player.inventory.contains(Item(ItemID.FLAX_2, flaxAmount))) {
                        exchange(player)
                    } else {
                        DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.FLAX_KEEPER)
                            .setText(
                                "Hello. Do you want to exchange some flax banknotes",
                                "for bowstring? I'll exchange $flaxAmount for you today, and",
                                "you can come back tomorrow for more."
                            )
                            .setNext(
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I didn't actually bring $flaxAmount flax banknotes with me.")
                                    .setText(
                                        "Oh. Well if you fetch that many flax banknotes, I'll",
                                        "swap them for bowstrings for you."
                                    )
                            ).start(player)
                    }
                } else if (action.type === NPCActions.ClickAction.Type.SECOND_OPTION) {
                    if (player.getInventory()
                            .contains(Item(ItemID.FLAX_2, flaxAmount))
                    ) {
                        exchange(player)
                    } else {
                        DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.FLAX_KEEPER)
                            .setText(
                                "Today I can exchange $flaxAmount flax banknotes for",
                                "bowstrings, if you bring me enough flax banknotes."
                            )
                            .start(player)
                    }
                }
                true
            }
        }
    }
}