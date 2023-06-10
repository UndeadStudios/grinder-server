package com.grinder.game.service.host

import com.grinder.net.codec.login.LoginRequest

data class HostLookupServiceRequest(val login: LoginRequest)