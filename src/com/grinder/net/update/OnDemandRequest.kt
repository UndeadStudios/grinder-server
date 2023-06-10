package com.grinder.net.update

import com.grinder.net.update.OnDemandRequest.Priority

/**
 * Represents a single 'on-demand' request.
 *
 * @author Graham
 *
 * @param fs       The file system.
 * @param folder   The folder.
 * @param priority The [Priority].
 */
class OnDemandRequest(val fs: Int,
                      val folder: Int,
                      val priority: Priority
) : Comparable<OnDemandRequest> {

    /**
     * An enumeration containing the different request priorities.
     *
     * @param value The integer value.
     */
    enum class Priority(private val value: Int) {

        /**
         * High priority - used when a player is in-game and data is required immediately.
         */
        HIGH(0),

        /**
         * Medium priority - used while loading extra resources when the client is not logged in.
         */
        MEDIUM(1),

        /**
         * Low priority - used when a file is not required urgently (such as when serving the rest of the cache whilst
         * the player is in-game).
         */
        LOW(2);

        /**
         * Compares this Priority with the specified other Priority.
         *
         * Used as an ordinal-independent variant of [.compareTo].
         *
         * @param other The other Priority.
         * @return 1 if this Priority is greater than `other`, 0 if they are equal, otherwise -1.
         */
        fun compareWith(other: Priority): Int {
            return value.compareTo(other.value)
        }

        /**
         * Converts the priority to an integer.
         *
         * @return The integer value.
         */
        fun toInteger(): Int {
            return value
        }

        companion object {
            /**
             * Converts the integer value to a Priority.
             *
             * @param value The integer value.
             * @return The priority.
             * @throws IllegalArgumentException If the value is outside of the range 1-3 inclusive.
             */
            fun valueOf(value: Int): Priority {
                return when (value) {
                    0 -> HIGH
                    1 -> MEDIUM
                    2 -> LOW
                    else -> throw IllegalArgumentException("Priority out of range - received $value.")
                }
            }
        }
    }

    /**
     * Gets the [Priority].
     *
     * @return The Priority.
     */
    override fun compareTo(other: OnDemandRequest): Int {
        return priority.compareWith(other.priority)
    }
}