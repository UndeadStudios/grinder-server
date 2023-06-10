package com.grinder.game.service

import com.grinder.game.service.captcha.CaptchaService
import com.grinder.game.service.host.HostLookupService
import com.grinder.game.service.logging.LoggingService
import com.grinder.game.service.login.LoginService
import com.grinder.game.service.search.SearchService
import com.grinder.game.service.tasks.TaskService
import com.grinder.game.service.update.UpdateService

/**
 * Manages all [Service] implementations used by the game.
 *
 * @author Stan van der Bend
 */
object ServiceManager {

    /**
     * [Service] for handling login requests.
     */
    val loginService = LoginService()

    /**
     * [Service] for handling requests for the file-server.
     */
    val updateService = UpdateService()

    /**
     * [Service] for looking up whether a host is TOR, or a VPN.
     */
    val hostLookUpService = HostLookupService()

    /**
     * [Service] for handling captcha requests.
     */
    val captchaService = CaptchaService()

    /**
     * [Service] for performing search tasks (e.g. searching through a drop table).
     */
    val searchService = SearchService()

    /**
     * [Service] for managing game logs.
     */
    val loggingService = LoggingService()

    /**
     * [Service] for offloading arbitrary [tasks][Runnable] to a different thread.
     */
    val taskService = TaskService()

    /**
     * Executes at the startup of the game, before any of the game assets are loaded.
     * Invokes [Service.init] for all [services][Service] contained within scope.
     */
    fun init(){
        loginService.init()
        hostLookUpService.init()
    }

    /**
     * Executes at the startup of the game, after all the game assets are loaded.
     * Invokes [Service.postLoad] for all [services][Service] contained within scope.
     */
    fun postLoad(){
        updateService.postLoad()
        searchService.postLoad()
        loggingService.postLoad()
        taskService.postLoad()
    }
}