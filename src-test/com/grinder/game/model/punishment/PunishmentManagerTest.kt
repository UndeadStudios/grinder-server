package com.grinder.game.model.punishment

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.bot.BotPlayer
import com.grinder.game.service.ServiceManager
import com.grinder.util.DiscordBot
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PunishmentManagerTest {

    private val testIP = "12345678"
    private val testMac = "some_mac_address"

    lateinit var data: ByteArray
    lateinit var player: Player

    @BeforeEach
    fun setup(){

        val file = PunishmentManager.PATH.toFile()
        if(!file.exists())
            file.createNewFile()
        data = file.readBytes()

        ServiceManager.taskService.postLoad()
        PunishmentManager.load()
        DiscordBot.ENABLED = false

        player = BotPlayer("Stan", World.startPosition.clone())
        player.hostAddress = testIP
        player.macAddress = testMac
        World.players.add(player)
    }

    @Test
    fun testBan(){

        PunishmentManager.submit("Stan", PunishmentType.BAN)

        ServiceManager.taskService.waitTillCompleted(100)

        var foundPunishment = false
        for(punishment in PunishmentManager.findPunishmentsForName("Stan")){
            if(punishment.targetName == "Stan" && punishment.punishmentType == PunishmentType.BAN){
                foundPunishment = true
            }
        }

        assert(foundPunishment)
    }

    @Test
    fun testIPBan(){

        PunishmentManager.submit("Stan", PunishmentType.IP_BAN)

        ServiceManager.taskService.waitTillCompleted(100)

        var foundPunishment = false
        for(punishment in PunishmentManager.findPunishmentsForName("Stan")){
            if(punishment.targetName == "Stan"
                    && punishment.identifier == testIP
                    && punishment.punishmentType == PunishmentType.IP_BAN){
                foundPunishment = true
            }
        }

        assert(foundPunishment)
    }


    @Test
    fun testMacBan(){

        PunishmentManager.submit("Stan", PunishmentType.MAC_BAN)

        ServiceManager.taskService.waitTillCompleted(100)

        var foundPunishment = false
        for(punishment in PunishmentManager.findPunishmentsForName("Stan")){
            if(punishment.targetName == "Stan"
                    && punishment.identifier == testMac
                    && punishment.punishmentType == PunishmentType.MAC_BAN){
                foundPunishment = true
            }
        }

        assert(foundPunishment)
    }

    @AfterEach
    fun tearDown(){
        PunishmentManager.PATH.toFile().writeBytes(data)
    }
}