package com.grinder.util.collection

fun <E> MutableList<E>.addMany(vararg elements : List<E>): MutableList<E> {
    elements.forEach { addAll(it) }
    return this
}