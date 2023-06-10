package com.grinder.net.codec.database

import java.sql.Timestamp
import java.util.HashMap

/**
 * @author Harrison, Alias: Hc747, Contact: harrisoncole05@gmail.com
 * @author Stan van der Bend
 * @version 2.0
 * @since 4/9/17
 */
class QueryBuilder {
    //TODO: DELETE
    //TODO: SELECT

    private val payload = HashMap<String, String>()
    private var table: String? = null
    private var command: String? = null
    private var where: String? = null
    private var limit: String? = null

    fun table(table: String): QueryBuilder {
        this.table = table
        return this
    }

    fun command(command: String): QueryBuilder {
        this.command = command
        return this
    }

    fun where(where: String): QueryBuilder {
        this.where = where
        return this
    }

    fun limit(limit: Int): QueryBuilder {
        this.limit = Integer.toString(limit)
        return this
    }

    fun kv(k: String, v: String): QueryBuilder {
        payload[k] = String.format("\"%s\"", v)
        return this
    }

    fun kv(k: String, v: Int): QueryBuilder {
        payload[k] = v.toString()
        return this
    }

    fun kv(k: String, v: Long): QueryBuilder {
        payload[k] = v.toString()
        return this
    }
    fun kv(k: String, v: Timestamp): QueryBuilder {
        return kv(k, v.toString())
    }
    fun kv(k: String, online: Boolean): QueryBuilder {
        payload[k] = (if(online) 1 else 0).toString()
        return this
    }

    fun k(k: String): QueryBuilder {
        return kv(k, "")
    }

    override fun toString(): String {
        validate(table, "Table must be specified.")
        validate(command, "Command must be specified.")

        val builder = StringBuilder()

        builder.append(command)

        when (command!!.toLowerCase()) {
            "select" -> {

                builder.append(" * ")
                        .append("FROM").append(" ")
                        .append(table)
            }
            "insert" -> {

                builder.append(" ")
                        .append("INTO")
                        .append(" ")
                        .append(table)
                        .append(" ")
                        .append("(")

                for ((key) in payload)
                    builder.append(key).append(",")

                builder.deleteCharAt(builder.lastIndexOf(","))
                        .append(")")

                builder.append(" ")
                        .append("VALUES")
                        .append(" ")
                        .append("(")

                for ((_, value) in payload)
                    builder.append(value).append(",")

                builder.deleteCharAt(builder.lastIndexOf(","))
                        .append(")")
            }
            "update" -> {

                builder.append(" ")
                        .append(table)
                        .append(" ")
                        .append("SET")
                        .append(" ")

                for ((key, value) in payload)
                    builder.append(key)
                            .append(" = ")
                            .append(value)
                            .append(",")

                builder.deleteCharAt(builder.lastIndexOf(","))
            }

            "delete" -> {

                builder.append(" ")
                        .append("FROM")
                        .append(" ")
                        .append(table)

                for (k in payload.keys)
                    builder.append(" ").append(k).append(",")

                builder.deleteCharAt(builder.lastIndexOf(","))
            }

            "onClick" -> {

                if (payload.isEmpty()) {
                    builder.append(" ").append("*")
                } else {

                    for (k in payload.keys)
                        builder.append(" ").append(k).append(",")

                    builder.deleteCharAt(builder.lastIndexOf(","))

                }

                builder.append(" ")
                        .append("FROM")
                        .append(" ")
                        .append(table)
            }

            else -> throw IllegalArgumentException(String.format("Unsupported SQL Operation: %s", command))
        }

        if (where != null && "" != where)
            builder.append(" ").append("WHERE").append(" ").append(where)

        if (limit != null && "" != limit)
            builder.append(" ").append("LIMIT").append(" ").append(limit)

        builder.append(";")

        return builder.toString()
    }

    fun build(): String {
        return toString()
    }


    companion object {

        val INSERT = "INSERT"
        val UPDATE = "UPDATE"
        val SELECT = "SELECT"
        val DELETE = "DELETE"

        private fun validate(input: String?, error: String) {
            if (input == null || input == "")
                throw IllegalArgumentException(error)
        }
    }
}
