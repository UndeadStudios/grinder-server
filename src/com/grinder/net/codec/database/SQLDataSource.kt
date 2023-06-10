package com.grinder.net.codec.database


/**
 * @author Harrison, Alias: Hc747, Contact: harrisoncole05@gmail.com
 * @author Stan van der Bend
 * @version 2.0
 * @since 5/9/17
 */
enum class SQLDataSource(val configFileLocation: String, val isEnabled: Boolean = false) {
    WEBSITE("./data/database/website.properties"),
    STAFF_PANEL("./data/database/staffpanel.properties"),
}
