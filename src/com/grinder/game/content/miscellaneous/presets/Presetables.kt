package com.grinder.game.content.miscellaneous.presets



import com.grinder.game.GameConstants
import com.grinder.game.content.minigame.castlewars.CastleWars
import com.grinder.game.content.pvp.bountyhunter.BountyHunterManager
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.impl.magic.SpellCasting
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.Boundary
import com.grinder.game.model.Skill
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.InstancedBossArea
import com.grinder.game.model.areas.UntypedInstancedBossArea
import com.grinder.game.model.areas.godwars.GodChamberArea
import com.grinder.game.model.areas.impl.DuelFightArena
import com.grinder.game.model.areas.impl.KalphiteLair
import com.grinder.game.model.areas.impl.PublicMinigameLobby
import com.grinder.game.model.areas.impl.WildernessArea
import com.grinder.game.model.areas.instanced.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueOptions
import com.grinder.game.model.interfaces.syntax.impl.ChangePresetName
import com.grinder.game.model.interfaces.syntax.impl.CreatePreset
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.bank.Banking
import com.grinder.util.Misc
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A class for handling `Presetable` sets.
 *
 * Holy, this became messy quickly. Sorry about that.
 *
 * @author Professor Oak
 */
object Presetables {
    /**
     * The max amount of premade/custom presets.
     */
    const val MAX_PRESETS = 10

    /**
     * The presets interface id.
     */
    public const val INTERFACE_ID = 92000

    /**
     * Pre-made sets by the server which everyone can use.
     */
	@JvmField
	val GLOBAL_PRESETS = arrayOfNulls<Presetable>(MAX_PRESETS)
    /**
     * Opens the specified preset for a player.
     *
     * @param player
     * @param preset
     */
    /**
     * Opens the presets interface for a player.
     *
     * @param player
     */
    @JvmOverloads
    fun open(player: Player, preset: Presetable? = player.currentPreset) {


        if (player.BLOCK_ALL_BUT_TALKING || player.isAccountFlagged || player.isInTutorial) {
            return;
        }
        if (player.area is UntypedInstancedBossArea || player.area is InstancedBossArea) {
            player.packetSender.sendMessage("You can't use presets within instanced areas!", 1000)
            return
        }
        if (AreaManager.inWilderness(player)) {
            player.packetSender.sendMessage("You are not allowed to use presets while in the Wilderness!")
            return
        }
        if (player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You are not allowed to use presets while in the Wilderness!")
            return
        }
        if (player.area != null && player.area is ZulrahShrine) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is KalphiteLair && player.position.z == 0) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is VorkathArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is GodChamberArea<*>) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is CerberusArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (AreaManager.inside(player.position, Boundary(2240, 2302, 2563, 2622))) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (AreaManager.inside(player.position, Boundary(2963, 3000, 4368, 4400))) { // Corp Area
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is DuelFightArena || player.dueling.inDuel()) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Duel Arena area!")
            return
        }
        if (player.area != null && player.area is CastleWars) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Castle Wars area!")
            return
        }
        if (player.busy()) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.BANKING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.TRADING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.DUELING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.DICING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.SHOPPING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.PRICE_CHECKING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.getBoolean(
                Attribute.HAS_PENDING_RANDOM_EVENT,
                false
            ) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
        ) {
            player.packetSender.sendMessage("You're not able to do this right now while having a pending random event.")
            return
        }
        if (player.isInTutorial) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.combat.isInCombat) {
            player.packetSender.sendMessage("You're not able to do this right now while your in combat.")
            return
        }
        if (player.combat.isUnderAttack) {
            player.packetSender.sendMessage("You're not able to do this right now while your in combat.")
            return
        }
        if (player.area != null && player.area is FightCaveArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Fight Caves!")
            return
        }
        if (player.area != null && player.area is AquaisNeigeArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Aquais Neige!")
            return
        }
        if (player.area != null && player.area is PublicMinigameLobby) {
            player.packetSender.sendMessage("You are not allowed to use presets in the minigame lobby!")
            return
        }
        if (AreaManager.inWilderness(player) || player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You are not allowed to use presets while staying in the Wilderness.")
            return
        }
        if (player.minigame != null) {
            if (!player.minigame.canUsePresets()) {
                player.sendMessage("You cannot use presets while in a minigame.")
                return
            }
        }
        if (preset != null) {

            // Send name
            player.packetSender.sendString(92002, "PvP Presets - " + preset.name)

            // Send stats
            player.packetSender.sendString(92007, Integer.toString(preset.stats[3])) // Hitpoints
            player.packetSender.sendString(92008, Integer.toString(preset.stats[0])) // Attack
            player.packetSender.sendString(92009, Integer.toString(preset.stats[2])) // Strength
            player.packetSender.sendString(92010, Integer.toString(preset.stats[1])) // Defence
            player.packetSender.sendString(92011, Integer.toString(preset.stats[4])) // Ranged
            player.packetSender.sendString(92012, "X") // Prayer
            player.packetSender.sendString(92013, Integer.toString(preset.stats[6])) // Magic

            // Send spellbook
            player.packetSender.sendString(
                92014,
                "@yel@" + Misc.formatText(preset.spellbook.name.lowercase(Locale.getDefault()))
            )
        } else {

            // Reset name
            player.packetSender.sendString(92002, "PvP Presets")

            // Reset stats
            for (i in 0..6) {
                player.packetSender.sendString(92007 + i, "")
            }

            // Reset spellbook
            player.packetSender.sendString(92014, "")
        }

        // Send inventory
        for (i in 0..27) {

            // Get item..
            var item: Item? = null
            if (preset != null) {
                if (i < preset.inventory.size) {
                    item = preset.inventory[i]
                }
            }

            // If it isn't null, send it. Otherwise, send empty slot.
            if (item != null) {
                player.packetSender.sendItemOnInterface(92015 + i, item.id, item.amount)
            } else {
                player.packetSender.sendItemOnInterface(92015 + i, -1, 1)
            }
        }

        // Send equipment
        for (i in 0..13) {
            player.packetSender.sendItemOnInterface(92044 + i, -1, 1)
        }
        if (preset != null) {
            Arrays.stream(preset.equipment).filter { t: Item -> !Objects.isNull(t) && t.isValid }
                .forEach { t: Item ->
                    player.packetSender.sendItemOnInterface(
                        92044 + t.definition.equipmentType.slot, t.id, t.amount
                    )
                }
        }

        // Send all available global presets
        for (i in 0 until MAX_PRESETS) {
            player.packetSender.sendString(
                92070 + i,
                if (GLOBAL_PRESETS[i] == null) "Empty" else GLOBAL_PRESETS[i]!!.name
            )
        }

        // Send all available player presets
        for (i in 0 until MAX_PRESETS) {
            player.packetSender.sendString(
                92082 + i,
                if (player.presets[i] == null) "Empty" else player.presets[i].name
            )
        }

        // Send on death toggle
        player.packetSender.sendConfig(987, if (player.isOpenPresetsOnDeath) 0 else 1)

        // Send interface
        player.packetSender.sendInterface(INTERFACE_ID)

        player.packetSender.sendSound(73)

        // Update current preset
        player.currentPreset = preset
    }

    /**
     * Edits a preset.
     *
     * @param player
     * The player.
     * @param index
     * The preset(to edit)'s index
     */
    private fun edit(player: Player, index: Int) {

        if (player.BLOCK_ALL_BUT_TALKING || player.isAccountFlagged || player.isInTutorial) {
            return;
        }
        if (player.area is UntypedInstancedBossArea || player.area is InstancedBossArea) {
            player.packetSender.sendMessage("You can't use presets within instanced areas!", 1000)
            return
        }
        if (AreaManager.inWilderness(player)) {
            player.packetSender.sendMessage("You are not allowed to use presets while in the Wilderness!")
            return
        }
        if (player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You are not allowed to use presets while in the Wilderness!")
            return
        }
        if (player.area != null && player.area is ZulrahShrine) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is KalphiteLair && player.position.z == 0) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is VorkathArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is GodChamberArea<*>) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is CerberusArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (AreaManager.inside(player.position, Boundary(2240, 2302, 2563, 2622))) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (AreaManager.inside(player.position, Boundary(2963, 3000, 4368, 4400))) { // Corp Area
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is DuelFightArena || player.dueling.inDuel()) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Duel Arena area!")
            return
        }
        if (player.area != null && player.area is CastleWars) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Castle Wars area!")
            return
        }
        if (player.busy()) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.BANKING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.TRADING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.DUELING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.DICING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.SHOPPING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.PRICE_CHECKING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.getBoolean(
                Attribute.HAS_PENDING_RANDOM_EVENT,
                false
            ) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
        ) {
            player.packetSender.sendMessage("You're not able to do this right now while having a pending random event.")
            return
        }
        if (player.isInTutorial) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.combat.isInCombat) {
            player.packetSender.sendMessage("You're not able to do this right now while your in combat.")
            return
        }
        if (player.combat.isUnderAttack) {
            player.packetSender.sendMessage("You're not able to do this right now while your in combat.")
            return
        }
        if (player.area != null && player.area is FightCaveArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Fight Caves!")
            return
        }
        if (player.area != null && player.area is AquaisNeigeArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Aquais Neige!")
            return
        }
        if (player.area != null && player.area is PublicMinigameLobby) {
            player.packetSender.sendMessage("You are not allowed to use presets in the minigame lobby!")
            return
        }
        if (AreaManager.inWilderness(player) || player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You are not allowed to use presets while staying in the Wilderness.")
            return
        }
        if (player.presets[index] == null) {
            player.packetSender.sendMessage("Global configured presets cannot be edited.")
            return
        }
        DialogueManager.start(player, 11)
        player.dialogueOptions = object : DialogueOptions() {
            override fun handleOption(player: Player, option: Int) {
                when (option) {
                    1 -> {
                        player.enterSyntax = ChangePresetName(index)
                        player.packetSender.sendEnterInputPrompt("Enter a new name for your preset below.")
                    }
                    2 -> {

                        // Update items
                        val inventory = player.inventory.copyValidItemsArray()
                        val equipment = player.equipment.copyValidItemsArray()
                        for (t in Misc.concat(inventory, equipment)) {
                            if (t.definition.isNoted) {
                                player.packetSender
                                    .sendMessage("You cannot create presets which contain noted items.")
                                return
                            }
                        }
                        player.presets[index].inventory = inventory
                        player.presets[index].equipment = equipment
                        player.packetSender
                            .sendMessage("The preset's items have been updated to match your current setup.")
                        open(player)
                    }
                    3 -> {

                        // Fetch stats
                        val stats = IntArray(7)
                        var i = 0
                        while (i < stats.size) {
                            if (i == 5) {
                                i++
                                continue
                            }
                            stats[i] = player.skillManager.getMaxLevel(Skill.values()[i])
                            i++
                        }

                        // Update stats
                        player.presets[index].stats = stats

                        // Update spellbook
                        player.presets[index].spellbook = player.spellbook
                        player.packetSender
                            .sendMessage("The preset's stats have been updated to match your current setup.")
                        open(player)
                    }
                    4 -> {

                        // Delete
                        player.presets[index] = null
                        player.currentPreset = null
                        player.packetSender.sendMessage("The selected preset has been deleted.")
                        open(player)
                    }
                    5 -> player.packetSender.sendInterfaceRemoval()
                }
            }
        }
    }

    /**
     * Loads a preset.
     *
     * @param player
     * The player.
     * @param preset
     * The preset to load.
     */
    private fun load(player: Player, preset: Presetable) {

        if (player.BLOCK_ALL_BUT_TALKING || player.isAccountFlagged || player.isInTutorial) {
            return;
        }
        if (player.area is UntypedInstancedBossArea || player.area is InstancedBossArea) {
            player.packetSender.sendMessage("You can't use presets within instanced areas!", 1000)
            return
        }
        if (AreaManager.inWilderness(player)) {
            player.packetSender.sendMessage("You are not allowed to use presets while in the Wilderness!")
            return
        }
        if (player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You are not allowed to use presets while in the Wilderness!")
            return
        }
        if (player.area != null && player.area is ZulrahShrine) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is KalphiteLair && player.position.z == 0) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is VorkathArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is GodChamberArea<*>) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is CerberusArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (AreaManager.inside(player.position, Boundary(2240, 2302, 2563, 2622))) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (AreaManager.inside(player.position, Boundary(2963, 3000, 4368, 4400))) { // Corp Area
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return
        }
        if (player.area != null && player.area is DuelFightArena || player.dueling.inDuel()) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Duel Arena area!")
            return
        }
        if (player.area != null && player.area is CastleWars) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Castle Wars area!")
            return
        }
        if (player.busy()) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.BANKING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.TRADING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.DUELING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.DICING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.SHOPPING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.status === PlayerStatus.PRICE_CHECKING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return
        }
        if (player.getBoolean(
                Attribute.HAS_PENDING_RANDOM_EVENT,
                false
            ) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
        ) {
            player.packetSender.sendMessage("You're not able to do this right now while having a pending random event.")
            return
        }
        if (player.isInTutorial) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return
        }
        if (player.combat.isInCombat) {
            player.packetSender.sendMessage("You're not able to do this right now while your in combat.")
            return
        }
        if (player.combat.isUnderAttack) {
            player.packetSender.sendMessage("You're not able to do this right now while your in combat.")
            return
        }
        if (player.area != null && player.area is FightCaveArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Fight Caves!")
            return
        }
        if (player.area != null && player.area is AquaisNeigeArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Aquais Neige!")
            return
        }
        if (player.area != null && player.area is PublicMinigameLobby) {
            player.packetSender.sendMessage("You are not allowed to use presets in the minigame lobby!")
            return
        }
        if (AreaManager.inWilderness(player) || player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You are not allowed to use presets while staying in the Wilderness.")
            return
        }
        if (player.minigame != null) {
            if (!player.minigame.canUsePresets()) {
                player.sendMessage("You cannot use presets while in a minigame.")
                return
            }
        }

/*		if (!preset.isGlobal() && !PlayerUtil.isDeveloper(player)) {
			player.getPacketSender().sendMessage("Custom presets temporarily disabled.");
			return;
		}*/
        val oldCbLevel = player.skillManager.calculateCombatLevel()


        // Check if we can load..
        if (player.area is WildernessArea) {
            if (!PlayerUtil.isDeveloper(player)) {
                player.packetSender.sendMessage("You can't load a preset while in the wilderness!")
                return
            }
        }
        if (player.dueling.inDuel()) {
            player.packetSender.sendMessage("You can't load a preset during a duel!")
            return
        }
        if (System.currentTimeMillis() - player.lastPresetLoad < TimeUnit.MINUTES.toMillis(1)) {
            player.packetSender.sendMessage("You can only load a preset once every one minute.")
            return
        }
        // Send valuable items in inventory/equipment to bank
        var sent = false
        for (item in Misc.concat(player.inventory.copiedItems, player.equipment.copiedItems)) {
            if (!item.isValid) {
                continue
            }
            var spawnable = false
            for (i in GameConstants.ALLOWED_SPAWNS) {
                if (item.id == i) {
                    spawnable = true
                    break
                }
            }
            if (!spawnable) {
                val bankTabId = Banking.getTabContainingItemOrDefault(player, item)
                val bankTab = player.getBank(bankTabId)
                if (bankTab.countFreeSlots() <= 0 && !bankTab.contains(item.id)) {
                    //player.sendMessage("The preset couldn't load because your bank is currently full and the items cannot be deposited.")
                    player.sendMessage("You don't have enough free slots in your main bank tab to do that.")
                    return
                }

                // Check if main tab has space
                if (player.getBank(0).countFreeSlots() <= 0) {
                    player.sendMessage("You don't have enough free slots in your main bank tab to do that.")
                    return
                }
                player.getBank(Banking.getTabContainingItemOrDefault(player, item.id)).add(item, false, false)
                //BankUtil.addToBank(player, item, false)
                //Banking.depositItems(player, player.inventory, true)
                //Banking.depositItems(player, player.equipment, true)
                sent = true
            }
        }
        if (sent) {
            player.packetSender.sendMessage("The items you had on you have been sent to your bank.")
        }

        // Close!
        player.packetSender.sendInterfaceRemoval()
        player.packetSender.sendSound(59)
        // Check for the preset's valuable items and see if the player has them.
        if (!preset.isGlobal && !player.gameMode.isSpawn) {
            val nonSpawnables: MutableList<Item> = ArrayList()

            // Get all the valuable items in this preset and check if player has
            // them..
            for (item in Misc.concat(preset.inventory, preset.equipment)) {
                if (item == null) continue
                var spawnable = false
                for (i in GameConstants.ALLOWED_SPAWNS) {
                    if (item.id == i) {
                        spawnable = true
                        break
                    }
                }

                if (!spawnable) {
                    nonSpawnables.add(item)
                    val inventoryAmt = player.inventory.getAmount(item.id)
                    val equipmentAmt = player.equipment.getAmount(item.id)
                    val bankAmt =
                        player.getBank(Banking.getTabContainingItemOrDefault(player, item.id)).getAmount(item.id)
                    val totalAmt = inventoryAmt + equipmentAmt + bankAmt
                    val preset_amt = preset.getAmount(item.id)
                    if (!player.gameMode.isSpawn) {
                    if (totalAmt < preset_amt) {
                        player.packetSender.sendMessage(
                            "You have a missing item "
                                    + item.definition.name + " in your inventory, equipment, or bank.");
                        return
                    }
                }
            }
        }


        player.inventory.resetItems().refreshItems()
        player.equipment.resetItems().refreshItems()

            // Delete valuable items from the proper place
            // Not from inventory/equipment, they will be reset anyway.
            if (!player.gameMode.isSpawn) {
            for (item in nonSpawnables) {
                if (player.inventory.contains(item)) {
                    player.inventory.delete(item)
                } else if (player.equipment.contains(item)) {
                    player.equipment.delete(item)
                } else {
                        player.getBank(Banking.getTabContainingItemOrDefault(player, item.id)).delete(item)
                    }
                }
            }
        }

        player.inventory.resetItems().refreshItems()
        player.equipment.resetItems().refreshItems()

        // Add inventory
        Arrays.stream(preset.inventory).filter { t: Item -> !Objects.isNull(t) && t.isValid }
            .forEach { t: Item? -> player.inventory.add(t) }

        // Set equipment
        Arrays.stream(preset.equipment).filter { t: Item -> !Objects.isNull(t) && t.isValid }
            .forEach { t: Item -> player.equipment.setItem(t.definition.equipmentType.slot, t.clone()) }

        // Set magic spellbook
        player.spellbook = preset.spellbook
        SpellCasting.setSpellToCastAutomatically(player, null)

        // Set levels
        var totalExp: Long = 0
        for (i in preset.stats.indices) {
            if (i == 5) {
                continue
            }
            val skill = Skill.values()[i]
            val level = preset.stats[i]
            player.skillManager.setCurrentLevel(skill, level, true).setMaxLevel(skill, level, true)
                .setExperience(skill, SkillUtil.calculateExperienceForLevel(level))
            totalExp += SkillUtil.calculateExperienceForLevel(level).toLong()
        }

        // Update prayer tab with prayer info
        player.packetSender.sendString(
            687, player.skillManager.getCurrentLevel(Skill.PRAYER).toString() + "/"
                    + player.skillManager.getMaxLevel(Skill.PRAYER)
        )

        // Send total level
        player.packetSender.sendString(31200, "" + player.skillManager.countTotalLevel())

        // Send combat level
        val newCbLevel = player.skillManager.calculateCombatLevel()
        val combatLevel = "Combat level: $newCbLevel"
        player.packetSender.sendString(19000, combatLevel).sendString(5858, combatLevel)
        if (newCbLevel != oldCbLevel) {
            BountyHunterManager.disassemblePairIfPresent(player)
        }

        // Send new spellbook
        player.packetSender.sendTabInterface(6, player.spellbook.interfaceId)
        // player.getPacketSender().sendConfig(709, PrayerHandler.canUse(player,
        // PrayerData.PRESERVE, false) ? 1 : 0);
        // player.getPacketSender().sendConfig(711, PrayerHandler.canUse(player,
        // PrayerData.RIGOUR, false) ? 1 : 0);
        // player.getPacketSender().sendConfig(713, PrayerHandler.canUse(player,
        // PrayerData.AUGURY, false) ? 1 : 0);
        player.resetAttributes()
        player.packetSender.sendMessage("Your selected preset has been succesfully loaded.")
        player.packetSender.sendTotalExp(totalExp)
        player.lastPresetLoad = System.currentTimeMillis()
    }

    /**
     * Handles a clicked button on the interface.
     *
     * @param player
     * @param button
     * @return
     */
	@JvmStatic
	fun handleButton(player: Player, button: Int): Boolean {
        if (player.interfaceId != INTERFACE_ID) {
            return false
        }

        if (player.BLOCK_ALL_BUT_TALKING || player.isAccountFlagged || player.isInTutorial) {
            return false
        }
        if (player.area is UntypedInstancedBossArea || player.area is InstancedBossArea) {
            player.packetSender.sendMessage("You can't use presets within instanced areas!", 1000)
            return false
        }
        if (AreaManager.inWilderness(player)) {
            player.packetSender.sendMessage("You are not allowed to use presets while in the Wilderness!")
            return false
        }
        if (player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You are not allowed to use presets while in the Wilderness!")
            return false
        }
        if (player.area != null && player.area is ZulrahShrine) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return false
        }
        if (player.area != null && player.area is KalphiteLair && player.position.z == 0) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return false
        }
        if (player.area != null && player.area is VorkathArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return false
        }
        if (player.area != null && player.area is GodChamberArea<*>) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return false
        }
        if (player.area != null && player.area is CerberusArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return false
        }
        if (AreaManager.inside(player.position, Boundary(2240, 2302, 2563, 2622))) {
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return false
        }
        if (AreaManager.inside(player.position, Boundary(2963, 3000, 4368, 4400))) { // Corp Area
            player.packetSender.sendMessage("You are not allowed to use presets in this area.")
            return false
        }
        if (player.area != null && player.area is DuelFightArena || player.dueling.inDuel()) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Duel Arena area!")
            return false
        }
        if (player.area != null && player.area is CastleWars) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Castle Wars area!")
            return false
        }
        if (player.busy()) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return false
        }
        if (player.status === PlayerStatus.BANKING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return false
        }
        if (player.status === PlayerStatus.TRADING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return false
        }
        if (player.status === PlayerStatus.DUELING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return false
        }
        if (player.status === PlayerStatus.DICING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return false
        }
        if (player.status === PlayerStatus.SHOPPING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return false
        }
        if (player.status === PlayerStatus.PRICE_CHECKING) {
            player.packetSender.sendMessage("You're not able to do this action right now.")
            return false
        }
        if (player.getBoolean(
                Attribute.HAS_PENDING_RANDOM_EVENT,
                false
            ) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
        ) {
            player.packetSender.sendMessage("You're not able to do this right now while having a pending random event.")
            return false
        }
        if (player.isInTutorial) {
            player.packetSender.sendMessage("You're not able to do this right now.")
            return false
        }
        if (player.combat.isInCombat) {
            player.packetSender.sendMessage("You're not able to do this right now while your in combat.")
            return false
        }
        if (player.combat.isUnderAttack) {
            player.packetSender.sendMessage("You're not able to do this right now while your in combat.")
            return false
        }
        if (player.area != null && player.area is FightCaveArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Fight Caves!")
            return false
        }
        if (player.area != null && player.area is AquaisNeigeArea) {
            player.packetSender.sendMessage("You are not allowed to use presets in the Aquais Neige!")
            return false
        }
        if (player.area != null && player.area is PublicMinigameLobby) {
            player.packetSender.sendMessage("You are not allowed to use presets in the minigame lobby!")
            return false
        }
        if (AreaManager.inWilderness(player) || player.wildernessLevel > 0) {
            player.packetSender.sendMessage("You are not allowed to use presets while staying in the Wilderness.")
            return false
        }
        when (button) {
            92060 -> {
                player.isOpenPresetsOnDeath = !player.isOpenPresetsOnDeath
                player.packetSender.sendConfig(987, if (player.isOpenPresetsOnDeath) 0 else 1)
                return true
            }
            92061 -> {
                if (player.currentPreset == null) {
                    player.packetSender.sendMessage("You haven't selected any preset yet.")
                    return true
                }
                if (player.currentPreset.isGlobal) {
                    player.packetSender.sendMessage("Global presets cannot be modified. You can only edit your own presets.")
                    return true
                }
                edit(player, player.currentPreset.index)
                return true
            }
            92064 -> {
                if (player.currentPreset == null) {
                    player.packetSender.sendMessage("You haven't selected any preset yet.")
                    return true
                }
                load(player, player.currentPreset)
                return true
            }
        }

        // Global presets selection
        if (button in 92070..92079) {
            val index = button - 92070
            if (GLOBAL_PRESETS[index] == null) {
                player.packetSender.sendMessage("That preset that is selected is currently unavailable.")
                return true
            }

            // Check if already in set, no need to re-open
            if (player.currentPreset != null && player.currentPreset === GLOBAL_PRESETS[index]) {
                return true
            }
            open(player, GLOBAL_PRESETS[index])
            return true
        }

        // Custom presets selection
        if (button in 92082..92091) {
            val index = button - 92082
            if (player.presets[index] == null) {
                DialogueManager.start(player, 10)
                player.dialogueOptions = object : DialogueOptions() {
                    override fun handleOption(player: Player, option: Int) {
                        if (option == 1) {
                            player.enterSyntax = CreatePreset(index)
                            player.packetSender.sendEnterInputPrompt("Enter a name for your preset below.")
                        } else {
                            player.packetSender.sendInterfaceRemoval()
                        }
                    }
                }
                return true
            }

            // Check if already in set, no need to re-open
            if (player.currentPreset != null && player.currentPreset === player.presets[index]) {
                return true
            }
            open(player, player.presets[index])
        }
        return false
    }
}