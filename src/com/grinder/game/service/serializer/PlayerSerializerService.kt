package com.grinder.game.service.serializer

import com.grinder.game.World
import com.grinder.game.model.Position
import com.grinder.game.service.Service
import com.grinder.net.session.PlayerSession
import com.grinder.net.codec.login.LoginRequest

/**
 * A [Service] that is responsible for encoding and decoding player data.
 *
 * @author Tom <rspsmods@gmail.com>
 */
abstract class PlayerSerializerService : Service {

    private lateinit var startTile: Position

    final override fun init() {
        startTile = World.startPosition
        initSerializer()
    }

    override fun postLoad() {
    }

    override fun bindNet() {
    }

    override fun terminate() {
    }

    fun configureNewPlayer(client: PlayerSession, request: LoginRequest) {
//        client.attr.put(NEW_ACCOUNT_ATTR, true)
//
//        client.passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt(16))
//        client.tile = startTile
    }

    abstract fun initSerializer()

    abstract fun loadClientData(client: PlayerSession, request: LoginRequest): PlayerLoadResult

    abstract fun saveClientData(client: PlayerSession): Boolean
}