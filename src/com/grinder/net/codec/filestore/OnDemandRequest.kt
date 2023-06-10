package com.grinder.net.codec.filestore

/**
 * Represents 'on-demand' request.
 *
 * @author Tom <rspsmods@gmail.com>
 * @author Stan van der Bend
 */
data class OnDemandRequest(
        val index: Int,
        val archive: Int,
        val priority: OnDemandPriority
) : Comparable<OnDemandRequest> {
    override fun compareTo(other: OnDemandRequest) = priority.compareWith(other.priority)
}
