package com.grinder.game.model.item

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.container.bank.BankUtil
import com.grinder.game.task.TaskManager
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

private val path = Paths.get("data", "attributable_items")

private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

private val restore = true

fun checkAccount(player: Player){

    path.toFile().also {
        if(!it.exists()) {
            if(!it.mkdir()){
                System.err.println("Could not create directory '$path'")
            }
        }
    }

    val file = path.resolve(player.username+".json").toFile()

    if(restore){
        restoreToAccount(player, file)
    } else {
        removeFromAccount(player, file)
    }

}

fun restoreToAccount(player: Player, file: File){

    if(!file.exists())
        return

    val reader = file.reader()
    val jsonObject = JsonParser().parse(reader).asJsonObject
    reader.close()

    Files.delete(file.toPath())

    val inventory = gson.fromJson(jsonObject.get("inventory").asJsonArray, Array<AttributableItem>::class.java)
    val equipment = gson.fromJson(jsonObject.get("equipment").asJsonArray, Array<AttributableItem>::class.java)
    val banks = gson.fromJson(jsonObject.get("banks").asJsonArray, Array<Array<AttributableItem>>::class.java)

    val items = ArrayList<AttributableItem>()
    items.addAll(inventory)
    items.addAll(equipment)
    for(bank in banks)
        items.addAll(bank)

    for(item in items)
        item.attributes = HashMap(item.attributes)

    for(item in items)
        BankUtil.addToBank(player, item, false)

    TaskManager.submit(5) {
        DialogueBuilder(DialogueType.STATEMENT)
                .setText("${items.size} items that were temporarily removed from your account",
                        "have been added to your bank :).")
                .start(player)
    }
}
fun removeFromAccount(player: Player, file: File){

    if(file.exists())
        return

    if(!file.createNewFile()){
        System.err.println("Could not create save file at '$file'")
        return
    }

    val attributableInventoryItems = ArrayList<AttributableItem>()

    for ((i, item) in player.inventory.items.withIndex()) {
        if(item is AttributableItem){
            attributableInventoryItems.add(item)
            player.inventory.items[i] = Item(-1, 0)
        }
    }

    val attributableBankItems = Array(player.banks?.size?:0){ tab ->
        val bank = player.banks[tab]
        val list = ArrayList<AttributableItem>()
        if(bank != null) {
            for ((i, item) in bank.items.withIndex()) {
                if (item is AttributableItem) {
                    list.add(item)
                    bank.items[i] = Item(-1, 0)
                }
            }
        }
        return@Array list
    }

    val attributableEquipmentItems = ArrayList<AttributableItem>()

    for((i, item) in player.equipment.items.withIndex()){
        if(item is AttributableItem){
            attributableEquipmentItems.add(item)
            player.equipment.items[i] = Item(-1, 0)
        }
    }

    player.inventory.refreshItems()
    player.equipment.refreshItems()
    // bank will refresh whenever a player opens it

    val jsonObject = JsonObject()
    jsonObject.add("inventory", gson.toJsonTree(attributableInventoryItems))
    jsonObject.add("equipment", gson.toJsonTree(attributableEquipmentItems))
    jsonObject.add("banks", gson.toJsonTree(attributableBankItems))

    val writer = file.bufferedWriter()
    gson.toJson(jsonObject, writer)
    writer.flush()
    writer.close()

    val totalRemovedItems = attributableEquipmentItems.count() + attributableInventoryItems.count() + attributableBankItems.sumBy { it.count { item -> item.amount > 0 } }

    if(totalRemovedItems > 0) {
        TaskManager.submit(5) {
            DialogueBuilder(DialogueType.STATEMENT)
                    .setText("$totalRemovedItems items are temporarily removed from your account",
                            "these will be returned to you soon.",
                            "Please be patient while we are investigating an issue,",
                            "sorry for the inconvenience!")
                    .start(player)
        }
    }
}
