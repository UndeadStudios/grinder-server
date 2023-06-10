package com.grinder.game.model.interfaces.dialogue

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.dialogue.impl.DialogueChat
import com.grinder.game.model.interfaces.syntax.EnterSyntax
import com.grinder.util.Misc
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Stan van der Bend on 28/10/2017.
 *
 * This class was written with the Builder pattern in mind.
 *
 * project: runeworld
 * package: runeworld.model.builder.external
 */
class DialogueBuilder {

    val type: DialogueType
    private val dialogue: Array<String?>
    private val items = arrayOfNulls<String>(3)
    private var optionTitle = "Choose an option."
    private var statementTitle = ""
    private var optionHandler: DialogueOptionHandler? = null
    private var expression: DialogueExpression? = null
    var previous: DialogueBuilder? = null
        private set
    var next: DialogueBuilder? = null
        private set
    private var action: Consumer<Player>? = null
    private var postAction: Consumer<Player>? = null
    private var npcId = -1

    /**
     * Create a new [DialogueBuilder].
     *
     * With [DialogueBuilder.type] set to [DialogueType.NPC_STATEMENT],
     * [DialogueBuilder.expression] set to [DialogueExpression.DEFAULT]
     * and [DialogueBuilder.dialogue] initialized with 4 as the array's length.
     */
    constructor() {
        type = DialogueType.NPC_STATEMENT
        expression = DialogueExpression.DEFAULT
        dialogue = arrayOfNulls(4)
    }

    /**
     * Create a new [DialogueBuilder]
     *
     * @param type  The [DialogueType] of the generated [Dialogue].
     */
    constructor(type: DialogueType) : super() {
        this.type = type
        dialogue = arrayOfNulls(if (type == DialogueType.OPTION) 5 else 4)
        if (type == DialogueType.OPTION) optionHandler = DialogueOptionHandler()
    }

    constructor(chat: DialogueChat) : this(chat.type()) {
        setNpcChatHead(chat.npcId())
        setExpression(chat.animation())
        setText(*chat.dialogue())
        if (chat.item() != null) setItem(chat.mediaID, 200)
    }

    private constructor(type: DialogueType, dialogueBuilder: DialogueBuilder) : this(type) {
        previous = dialogueBuilder
        npcId = previous!!.npcId
        if (type == DialogueType.NPC_STATEMENT) {
            expression = previous!!.expression
        }
    }

    /**
     * Creates a new [Dialogue] and starts it in through the [DialogueManager].
     *
     * @param player the [Player] to whom this dialogue will be parsed.
     */
    fun start(player: Player) {
        if (Objects.isNull(previous)) DialogueManager.start(player, build(player)) else previous!!.start(player)
    }

    fun setNpcChatHead(id: Int): DialogueBuilder {
        npcId = id
        return this
    }

    fun setExpression(expression: DialogueExpression?): DialogueBuilder {
        this.expression = expression
        return this
    }

    fun setItem(itemId: Int, zoom: Int, name: String?): DialogueBuilder {
        items[0] = itemId.toString()
        items[1] = zoom.toString()
        items[2] = Misc.formatText(name)
        return this
    }

    fun setItem(itemId: Int, zoom: Int): DialogueBuilder {
        return setItem(itemId, zoom, ItemDefinition.forId(itemId).name)
    }

    fun setAction(action: Consumer<Player>?): DialogueBuilder {
        this.action = action
        return this
    }

    fun setPostAction(action: Consumer<Player>?): DialogueBuilder {
        this.postAction = action
        return this
    }

    fun setText(vararg lines: String?): DialogueBuilder {
        for (i in dialogue.indices) if (i < lines.size && Objects.isNull(dialogue[i])) {
            dialogue[i] = lines[i]
        }
        return this
    }

    fun setNext(next: DialogueBuilder?): DialogueBuilder {
        this.next = next
        return this
    }

    fun add(): DialogueBuilder {
        val next = DialogueBuilder(type, this)
        setNext(next)
        return next
    }

    fun add(type: DialogueType): DialogueBuilder {
        val next = DialogueBuilder(type, this)
        setNext(next)
        return next
    }

    fun add(builder: DialogueBuilder): DialogueBuilder {
        builder.previous = this
        setNext(builder)
        return builder
    }

    fun add(dialogue: DialogueChat): DialogueBuilder {
        val builder = DialogueBuilder()
        builder.setNpcChatHead(dialogue.npcId())
        builder.setExpression(dialogue.animation())
        builder.setText(*dialogue.dialogue())
        if (dialogue.item() != null) builder.setItem(dialogue.mediaID, 200)
        setNext(builder)
        return builder
    }

    fun setOptionTitle(title: String): DialogueBuilder {
        optionTitle = title
        return this
    }
    fun setStatementTitle(title: String): DialogueBuilder {
        statementTitle = title
        return this
    }
    fun option(index: Int, text: String?, consumer: Consumer<Player>?): DialogueBuilder {
        optionHandler!!.addAction(index, consumer)
        if (index < dialogue.size) dialogue[index] = text
        return this
    }

    fun lastOption(text: String?, consumer: Consumer<Player>?): DialogueBuilder {
        return option(optionHandler!!.actionCount(), text, consumer)
    }

    fun firstOption(text: String?, consumer: Consumer<Player>?): DialogueBuilder {
        return option(0, text, consumer)
    }

    fun secondOption(text: String?, consumer: Consumer<Player>?): DialogueBuilder {
        return option(1, text, consumer)
    }

    fun thirdOption(text: String?, consumer: Consumer<Player>?): DialogueBuilder {
        return option(2, text, consumer)
    }

    fun fourthOption(text: String?, consumer: Consumer<Player>?): DialogueBuilder {
        return option(3, text, consumer)
    }

    fun fifthOption(text: String?, consumer: Consumer<Player>?): DialogueBuilder {
        return option(4, text, consumer)
    }

    fun addOptions(options: List<Pair<String, Consumer<Player>>>) = addOptions(*options.toTypedArray())

    fun addOptions(vararg options: Pair<String, Consumer<Player>>): DialogueBuilder {
        var builder = this
        var nextDialogueBuilder: DialogueBuilder
        val builderMap = HashMap<DialogueBuilder, DialogueBuilder>()
        for(page in options.asIterable().chunked(4)){
            var lastIndex = 0
            page.forEachIndexed { index, pair ->
                builder.option(index, pair.first, pair.second)
                lastIndex++
            }
            nextDialogueBuilder = DialogueBuilder(DialogueType.OPTION)
            builderMap[builder] = nextDialogueBuilder
            builder = nextDialogueBuilder
        }
        if( builderMap.entries.size > 1) {
            builderMap.entries.forEachIndexed { index, mutableEntry ->
                val previous = mutableEntry.key
                val next = mutableEntry.value
                if (next.optionHandler!!.actionCount() > 0)
                    previous.lastOption("Next", next.startConsumer())
                else
                    previous.lastOption("First", this.startConsumer())
            }
        }
        return this
    }

    private fun startConsumer() = Consumer<Player> { start(it) }

    @JvmOverloads
    fun addCancel(customText: String? = "Never mind."): DialogueBuilder {
        var validDialogues = Math.toIntExact(Arrays.stream(dialogue).filter { obj: String? -> Objects.nonNull(obj) }.count())
        if (validDialogues >= dialogue.size) validDialogues = dialogue.size
        return option(validDialogues, customText, Consumer { player1: Player -> player1.packetSender.sendInterfaceRemoval() })
    }

    fun build(player: Player): Dialogue {
        player.dialogueOptions = optionHandler
        player.setDialogueBuilder(this)
        return object : Dialogue() {
            override fun type(): DialogueType {
                return type
            }

            override fun animation(): DialogueExpression {
                return if (Objects.nonNull(expression)) expression!! else DialogueExpression.DEFAULT
            }

            override fun dialogue(): Array<String> {
                return dialogue.filterNotNull().toTypedArray()
            }

            override fun nextDialogue(): Dialogue? {
                return if (Objects.nonNull(next)) next!!.build(player) else null
            }

            override fun npcId(): Int {
                return if (type == DialogueType.NPC_STATEMENT) if (npcId == -1) if (player.interactingEntity != null && player.interactingEntity is NPC) player.interactingEntity.asNpc.fetchDefinition().id else 1 else npcId else super.npcId()
            }

            override fun preAction(player: Player) {
                action?.accept(player)
            }

            override fun postAction(player: Player) {
                postAction?.accept(player)
            }

            override fun title(): String {
                return when (type) {
                    DialogueType.OPTION -> optionTitle
                    DialogueType.TITLED_STATEMENT_NO_CONTINUE -> statementTitle
                    else -> super.title()
                }
            }
            override fun item(): Array<String> {
                return items.map { it ?: "" }.toTypedArray()
            }
        }
    }
}
