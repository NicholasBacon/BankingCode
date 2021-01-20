package com.bacon.osbot.banking
class EQ {

    var items: List<Pair<Int, Int>>

    constructor() {
        items = emptyList()

    }

    constructor(eq: EQ) {
        items = eq.items
    }

    constructor(vararg items: Pair<Int, Int>) {

        this.items = items.toList()
    }


    operator fun get(item: Int): Int? {
        return items.firstOrNull { it.first == item }?.second
    }

    fun remove(item: Int) {
        items = items.filter { it.first != item }
    }


    operator fun set(item: Int, value: Int) {

        items = items.filter { it.first != item }.toMutableList().addreturn(Pair(item, value))

    }

    fun add(pair: Pair<Int, Int>) {
        items = items.toMutableList().addreturn(pair)
    }

    override fun hashCode(): Int {
        return items.sumBy { s -> s.hashCode() }
    }
    private fun <E> MutableList<E>.addreturn(pair: E): List<E> {
        this.add(pair)
        return this
    }
}

