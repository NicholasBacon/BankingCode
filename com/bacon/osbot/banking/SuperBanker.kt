package com.bacon.osbot.banking


import com.bacon.osbot.banking.Actions.*
import com.bacon.osbot.banking.trash.MoveOb11
import org.osbot.rs07.api.Bank
import org.osbot.rs07.api.ui.Tab
import org.osbot.rs07.api.util.ItemContainer
import org.osbot.rs07.event.Event
import org.osbot.rs07.script.MethodProvider
import java.util.concurrent.*
import kotlin.collections.HashMap


class SuperBanker(
    var mp: MethodProvider?
) {


    fun findGoal(
        start: MoveOb11,
        goal_inv: List<Triple<Int, Int, Int>>,
        goal_eqp: List<Triple<Int, Int, Int>>
    ): List<Triple<Actions, Int, Int>> {

        val set_Of_goals = goal_inv.map { s -> s.first }.toMutableSet()
        var total = 0


        var y = listOf<MoveOb11>(start)
        for (i in 0 .. 11) {
            val set1 = ConcurrentHashMap<Int, MoveOb11>()
            y.forEach { s ->
                s.makeMakeNextfirstturn(goal_eqp).forEach { t ->
                    val hashed = t.hashCode()
                    val testItem = set1[hashed]
                    if (testItem == null || t.moves < testItem.moves) {
                        set1[hashed] = t
                    }

                }
            }
            y = set1.values.toList()
        }
        var x = listOf<MoveOb11>(
            start,
            start.depositAll(Deposit.BOTH),
            start.depositAll(Deposit.EQ),
            start.depositAll(Deposit.INV),
            *y.toTypedArray()
        )
        y= emptyList()


        set_Of_goals.addAll(goal_eqp.map { s -> s.first })
        for (i in 0..100) {
            val any = x.filter { s -> s.checkgoal(goal_inv, goal_eqp) }.map { s -> s.openclosebank(MoveOb11.Type.wear) }
                .minByOrNull { s -> s.moves }
            if (any != null) {

                return any.moveslist
            }


            val set = ConcurrentHashMap<Int, MoveOb11>()

            x.parallelStream().forEach { s ->
                s.makeMakeNext(goal_inv, goal_eqp, set_Of_goals).forEach { t ->
                    val hashed = t.hashCode()
                    val testItem = set[hashed]
                    if (testItem == null || t.moves < testItem.moves) {
                        set[hashed] = t
                    }

                }
            }
            x = set.values.toList()

            total += x.size
        }
        return emptyList()
    }

    fun shouldBank(goal_inv: List<Triple<Int, Int, Int>>, goal_eqp: List<Triple<Int, Int, Int>>): Boolean {
        val invItems = EQ(* itemtoHm(mp!!.getInventory()).toList().toTypedArray())
        val eqItems = EQ(* itemtoHm(mp!!.getEquipment()).toList().toTypedArray())

        val start = MoveOb11(
            invItems,
            eqItems,
            EQ(), false,
            0, emptyList(), 0, true, true, 0, 0, 0
        )
        return !start.checkgoal(goal_inv, goal_eqp)

    }


    fun makeOb(): MoveOb11 {
        val bankItems = EQ(* itemtoHm(mp!!.getBank()).toList().toTypedArray())
        val invItems = EQ(* itemtoHm(mp!!.getInventory()).toList().toTypedArray())
        val eqItems = EQ(* itemtoHm(mp!!.getEquipment()).toList().toTypedArray())

        val x = mp!!.getWidgets().get(12, 33).interactActions[0].split(": ")[1].replace(",", "").toInt()
        val start = MoveOb11(
            invItems,
            eqItems,
            bankItems, false,
            0, emptyList(), x, true, true, 0, 0, 0
        )

        return start
    }

    fun itemtoHm(items: ItemContainer): HashMap<Int, Int> {
        return items.items.filterNotNull().groupBy { s -> s.id }.map { s ->

            Pair(s.key, s.value.sumBy { s -> s.amount })

        }.associate { s -> s.first to s.second } as HashMap<Int, Int>

    }

    fun bank(goal_inv: List<Triple<Int, Int, Int>>, goal_eqp: List<Triple<Int, Int, Int>>): Boolean {

        return !mp!!.execute(object : Event() {
            var listOFAction = emptyList<Triple<Actions, Int, Int>>().toMutableList()
            override fun onStart() {
                if (!getBank().isOpen) {
                    getBank().open()
                }
                sleep(1000)
                listOFAction = findGoal(makeOb(), goal_inv, goal_eqp).toMutableList()
                if (listOFAction.isEmpty()) {
                    setFinished()
                    setFailed()
                }

            }

            override fun execute(): Int {
                if (listOFAction.isEmpty()) {
                    setFinished()
                    return 0
                }

                val thisActoion = listOFAction[0]
                listOFAction.removeAt(0)
                println(thisActoion)
                when (thisActoion.first) {
                    deposit -> getBank().deposit(thisActoion.second, thisActoion.third)
                    depositAlleq -> getBank().depositWornItems()
                    wear -> {
                        getInventory().getItem(thisActoion.second).interact("Wear", "Wield")
                        return MethodProvider.random(600, 1000)
                    }

                    takeoff -> {
                        getEquipment().unequip(
                            getEquipment().getSlotForItemIds(thisActoion.second),
                            thisActoion.second
                        )
                        return MethodProvider.random(600, 1000)
                    }

                    withdraw -> getBank().withdraw(getNNotedMaybe(thisActoion.second), thisActoion.third)
                    invdepositAll -> getBank().depositAll()
                    close -> if (getBank().isOpen) {
                        getBank().close()
                    } else {
                        getBank().open()
                    }
                    note -> if (getBank().isBankModeEnabled(Bank.BankMode.WITHDRAW_NOTE)) {
                        getBank().enableMode(Bank.BankMode.WITHDRAW_ITEM)
                    } else {
                        getBank().enableMode(Bank.BankMode.WITHDRAW_NOTE)
                    }
                    openEq -> getTabs().open(Tab.EQUIPMENT)
                    openINv -> getTabs().open(Tab.INVENTORY)
                }
                return MethodProvider.random(1000, 3500)
            }
        }).hasFailed()
    }


}



