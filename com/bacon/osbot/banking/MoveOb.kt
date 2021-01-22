package com.bacon.osbot.banking


class MoveOb11(
    val inv: EQ,
    val eq: EQ,
    val bank: EQ,
    val note: Boolean,
    val moves: Int,
    val moveslist: List<Triple<Actions, Int, Int>>,
    val x: Int,
    val open: Boolean,
    val invOpen: Boolean,
    val openCounter: Int,
    val noteCounter: Int,
    val unCounter: Int,
) : Comparable<MoveOb11?> {
    enum class Direction {
        too_much, too_little, wear, take_off
    }


    fun dup(
        invC: EQ = inv,
        eqC: EQ = eq,
        bankC: EQ = bank,
        noteC: Boolean = note,
        movesC: Int = 0,
        moveslistC: List<Triple<Actions, Int, Int>> = emptyList(),
        xC: Int = x,
        openC: Boolean = open,
        invOpenC: Boolean = invOpen,
        openCounterC: Int = 0,
        noteCounterC: Int = 0,
        unCounterC: Int = 0,
    ): MoveOb11 {
        val newmove = moveslist.toMutableList()
        newmove.addAll(moveslistC)
        return MoveOb11(
            (invC),
            (eqC),
            (bankC),
            noteC,
            (moves + movesC),
            newmove,
            xC,
            openC, invOpenC,
            (openCounterC + openCounter),
            (noteCounterC + noteCounter),
            (unCounterC + unCounter)
        )
    }

    override fun toString(): String {
        return (" ${inv} ${eq} ${bank} ${note} ${moves} ${moveslist} ${x} ${open} $openCounter $noteCounter ")


    }

    var count = -1
    fun count(): Int {
        if (count == -1) {
            count = inv.items.sumBy { s -> if (stackableQ(s.first) || notedQ(s.first)) 1 else s.second }

        }


        return count

    }

    fun addBank(hm: EQ, id: Int, amount: Int): EQ {
        val copy = EQ(hm)
        val min = amount
        val newid = getNNotedMaybe(id)
        val amountInBank = hm[newid] ?: 0
        copy[newid] = amountInBank + min
        return copy
    }

    fun addInv(hm: EQ, id: Int, amount: Int, note: Boolean): EQ {
        val copy = EQ(hm)
        val newid = if (note) {
            getNotedMaybe(id)
        } else {
            getNNotedMaybe(id)
        }
        val amountInInv = (hm[newid] ?: 0) + amount
        copy[newid] = amountInInv
        return copy
    }

    fun removeBank(
        hm: EQ,
        id: Int,
        amountWithdraw: Int
    ): Pair<EQ, Int> {
        val newid = getNNotedMaybe(id)
        return remove(hm, newid, amountWithdraw)
    }

    fun removeInv(
        hm: EQ,
        id: Int,
        amountDeposit: Int
    ): Pair<EQ, Int> {
        return remove(hm, id, amountDeposit)
    }

    private fun remove(
        hm: EQ,
        id: Int,
        amountwithdraw: Int
    ): Pair<EQ, Int> {
        val copy = EQ(hm)

        val oldValue = hm[id]!!

        val newValue = oldValue - amountwithdraw
        return if (newValue > 0) {
            copy[id] = newValue
            Pair(copy, amountwithdraw)
        } else {
            copy.remove(id)
            Pair(copy, oldValue)
        }
    }


    fun withdraw_helper(
        item: Int,
        amount: Int,
        note: Boolean,
        bank: EQ,
        inv: EQ
    ): Pair<EQ, EQ> {
        val bank = removeBank(bank, item, amount)
        val inv = addInv(inv, item, bank.second, note)
        return Pair(bank.first, inv)
    }

    fun deposit_helper(
        item: Int,
        amount: Int,
        note: Boolean,
        bank: EQ,
        inv: EQ
    ): Pair<EQ, EQ> {

        val inv = removeInv(inv, item, amount)
        val bank = addBank(bank, item, inv.second)

        return Pair(bank, inv.first)
    }


    fun uneq(item: Int): MoveOb11 {


        var x = openclosebank(Type.wear).openInv(Type.Un)

        val itemAmount = eq[item]!!
        val invN = addInv(inv, item, itemAmount, false)
        val eqN = EQ(eq)
        eqN.remove(item)
        return x.dup(
            invC = invN,
            eqC = eqN,
            movesC = 1,
            moveslistC = listOf(Triple(Actions.takeoff, item, 0)),

            )
    }

    private fun openInv(type: Type): MoveOb11 {
        if (type == Type.Un) {
            if (invOpen) {

                return this.dup(
                    movesC = 1,
                    invOpenC = false,
                    moveslistC = listOf(Triple(Actions.openEq, 0, 0)),
                    unCounterC = 1
                )
            }
            return this
        }
        if (type == Type.In) {
            if (!invOpen) {
                return this.dup(
                    movesC = 1,
                    moveslistC = listOf(Triple(Actions.openINv, 0, 0)),
                    invOpenC = true,
                    unCounterC = 1
                )
            }
            return this
        }
        return this

    }

    fun eq(item: Int): MoveOb11 {

        val x = openclosebank(Type.wear).openInv(Type.In)
        var itemTakeOff: List<Pair<Int, Int>> = emptyList()


        val check = test[item]

        itemTakeOff = when (check) {
            0.toChar() -> emptyList()
            4.toChar() -> eq.items.filter { s -> test[s.first] == 4.toChar() || test[s.first] == 10.toChar() || test[s.first] == 6.toChar() }
                .toList()


            else -> {
                eq.items.filter { s -> test[s.first] == check }.toList()
            }
        }


        val amount = if (stackableQ(item)) {
            Int.MAX_VALUE
        } else {
            1
        }
        val invPair: Pair<EQ, Int> = removeInv(inv, item, amount)
        val invN = itemTakeOff.fold(invPair.first) { acc, e -> addInv(acc, e.first, e.second, false) }
        val eqN = EQ(eq)
        itemTakeOff.forEach { s ->
            eqN.remove(s.first)
        }
        eqN[item] = invPair.second





        return x.dup(
            invC = invN,
            eqC = eqN,
            movesC = 1,
            moveslistC = listOf(Triple(Actions.wear, item, 0)),

            )
    }


    fun depositAll(type: Deposit): MoveOb11 {
        if (type == Deposit.BOTH) {
            return this.depositAll(Deposit.INV).depositAll(Deposit.EQ)
        }
        val returnBank = when (type) {
            Deposit.EQ -> eq.items.fold(bank) { acc, e -> addBank(acc, e.first, e.second) }
            Deposit.INV -> inv.items.fold(bank) { acc, e -> addBank(acc, e.first, e.second) }
            Deposit.BOTH -> TODO()
        }





        return when (type) {
            Deposit.EQ -> this.dup(
                eqC = EQ(),
                bankC = returnBank,
                movesC = 1,
                moveslistC = listOf(Triple(Actions.depositAlleq, 0, 0)),

                )
            Deposit.INV -> this.dup(
                invC = EQ(),
                bankC = returnBank,
                movesC = 1,
                moveslistC = listOf(Triple(Actions.invdepositAll, 0, 0)),

                )
            Deposit.BOTH -> TODO()
        }


    }


    enum class Type {
        wi, wear, Un, In
    }

    val NoteThing = listOf(Triple(Actions.note, 0, 0))
    val closeThing = listOf(Triple(Actions.close, 0, 0))
    fun noteHelper(type: Type, id: Int): MoveOb11 {
        if (type == Type.wi && !stackableQ(id) && !(notedQ(id) == this.note)) {
            return this.dup(noteC = !this.note, movesC = 1, moveslistC = NoteThing, noteCounterC = 1)


        }
        return this
    }

    fun openclosebank(type: Type): MoveOb11 {
        if (type == Type.wear) {
            if (this.open) {
                return this.dup(movesC = 1, moveslistC = closeThing, openC = false, openCounterC = 1, noteC = false)
            }
            return this
        }
        if (!this.open) {
            return this.dup(movesC = 1, moveslistC = closeThing, openC = true, noteC = false)
        }
        return this
    }


    fun withdraw(item: Int, amount: Int): MoveOb11 {
        var newX = x
        var mov = 1
        when (amount) {
            1, 5, 10, x, Int.MAX_VALUE -> {

            }
            else -> {
                mov = 2
                newX = amount
            }
        }

        val testOpen = openclosebank(Type.wi)
        val testNote = testOpen.noteHelper(Type.wi, item)
        val with = withdraw_helper(item, amount, testNote.note, bank, inv)

        return testNote.dup(
            invC = with.second,
            bankC = with.first,
            movesC = mov,
            moveslistC = listOf(Triple(Actions.withdraw, (item), amount)),
            xC = newX,

            )
    }


    fun deposit(item: Int, amount: Int): MoveOb11 {
        var newX = x
        var mov = 1
        when (amount) {
            1, 5, 10, x, Int.MAX_VALUE -> {
            }
            else -> {
                mov = 2
                newX = amount
            }
        }

        val testOpen = openclosebank(Type.wi)
        val depo = deposit_helper(item, amount, testOpen.note, bank, inv)

        return testOpen.dup(
            invC = depo.second,
            bankC = depo.first,
            movesC = mov,
            moveslistC = listOf(Triple(Actions.deposit, item, amount)),
            xC = newX,

            )
    }


    private fun prosess(
        goal_inv: List<Triple<Int, Int, Int>>,
        goal_eqp: List<Triple<Int, Int, Int>>,
        set_Of_goals: Set<Int>
    ): List<Triple<Direction, Int, Int>> {
        val allId = set_Of_goals.toMutableSet()
        allId.addAll(inv.items.map { s -> s.first })
        allId.addAll(eq.items.map { s -> s.first })
        val returnOb = mutableListOf<Triple<Direction, Int, Int>>()


        val too_much = Direction.too_much
        val too_little = Direction.too_little
        val wear = Direction.wear
        val takeOff = Direction.take_off

        val _i = goal_inv.toMutableList()
        val _e = goal_eqp.toMutableList()

        allId.forEach { s ->
            val goal_i = _i.firstOrNullREMove(s)
            val goal_e = _e.firstOrNullREMove(s)
            val i = inv[s]
            val e = eq[s]
            var mask = 0
            if (goal_i != null) {
                mask += 1
            }
            if (goal_e != null) {
                mask += 2
            }
            if (i != null) {
                mask += 4
            }
            if (e != null) {
                mask += 8
            }
            when (mask) {
                1 -> {
                    //in inv goal but not the inv
                    returnOb.add(Triple(too_little, s, goal_i!!.third))
                }
                2 -> {
                    //in inv goal but not the inv or eq
                    returnOb.add(Triple(too_little, s, goal_e!!.third))
                }
                3 -> {
                    //in both inv goal and eq goal
                    returnOb.add(Triple(too_little, s, goal_e!!.third + goal_i!!.third))
                    returnOb.add(Triple(too_little, s, goal_e.third))
                    returnOb.add(Triple(too_little, s, goal_i.third))
                }
                4 -> {
                    //in inv and should not be
                    returnOb.add(Triple(too_much, s, i!!))
                }
                5 -> {
                    //in inv and in goal
                    if (goal_i!!.third > i!!) {
                        returnOb.add(Triple(too_little, s, goal_i.third - i))
                    } else if (goal_i.third < i) {
                        returnOb.add(Triple(too_much, s, i - goal_i.third))
                    }

                }
                6 -> {
                    //in eq goal and in the inv
                    if (goal_e!!.third == i!!) {
                        returnOb.add(Triple(wear, s, goal_e.third))
                    } else if (goal_e.third > i) {
                        returnOb.add(Triple(too_little, s, goal_e.third - i))
                    } else if (goal_e.third < i) {
                        returnOb.add(Triple(too_much, s, i - goal_e.third))
                    }

                }
                7 -> {
//                    in (inv and eq) goals and has items in inv
                    val total = goal_e!!.third + goal_i!!.third

                    if (stackableQ(s)) {
                        if (goal_e.third == i!!) {
                            returnOb.add(Triple(wear, s, goal_e.third))
                        } else if (goal_e.third < i) {
                            returnOb.add(Triple(too_much, s, i - goal_e.third))
                        } else if (goal_e.third > i) {
                            returnOb.add(Triple(too_little, s, goal_e.third - i))
                        }


                    } else {
                        if (total == i!!) {
                            returnOb.add(Triple(wear, s, goal_e.third))
                        } else if (total > i) {
                            returnOb.add(Triple(wear, s, goal_e.third))
                            returnOb.add(Triple(too_much, s, i - total))
                        } else if (total < i) {
                            returnOb.add(Triple(wear, s, goal_e.third))
                            returnOb.add(Triple(too_little, s, total - i))
                        }

                    }


                }
                8, 9 -> {
//                    wearing it but it should be taken off
                    returnOb.add(Triple(takeOff, s, e!!))

                }

                10 -> {
                    if (stackableQ(s)) {
                        if (e!! != goal_e!!.third) {
                            returnOb.add(Triple(takeOff, s, e))
                        }
                    }
                }
                11 -> {
                    if (stackableQ(s)) {
                        if (e!! != goal_e!!.third) {
                            returnOb.add(Triple(takeOff, s, e))
                        }
                    }

                    returnOb.add(Triple(too_little, s, goal_i!!.third))


                }
                12 -> {
                    returnOb.add(Triple(takeOff, s, e!!))
                    returnOb.add(Triple(too_much, s, i!!))

                }
                13 -> {
                    returnOb.add(Triple(takeOff, s, e!!))
                    if (goal_i!!.third > i!!) {
                        returnOb.add(Triple(too_little, s, goal_i.third - i))
                    } else if (goal_i.third < i) {
                        returnOb.add(Triple(too_much, s, i - goal_i.third))
                    }
                }
                14 -> {
                    if (stackableQ(s)) {
                        if (e!! != goal_e!!.third) {
                            returnOb.add(Triple(takeOff, s, e))
                        }
                    }

                    returnOb.add(Triple(too_much, s, i!!))


                }
                15 -> {
                    if (stackableQ(s)) {
                        if (e!! != goal_e!!.third) {
                            returnOb.add(Triple(takeOff, s, e))
                        }
                    }
                    if (goal_i!!.third > i!!) {
                        returnOb.add(Triple(too_little, s, goal_i.third - i))
                    } else if (goal_i.third < i) {
                        returnOb.add(Triple(too_much, s, i - goal_i.third))
                    }

                }
            }


        }

        val groups = returnOb.groupBy { s -> s.first }
        val returnlist = mutableListOf<Triple<Direction, Int, Int>?>()


        val eqset = goal_eqp.map { t -> t.first }.toSet()
        if (groups[Direction.wear] != null) {
            returnlist.add(groups[Direction.wear]!!.maxByOrNull { s -> s.third })
        }
        if (groups[Direction.too_much] != null) {
            returnlist.add(groups[Direction.too_much]!!.maxByOrNull { s -> s.third })
        }
        if (groups[Direction.too_little] != null) {
            returnlist.add(groups[Direction.too_little]!!.filter { s -> notedQ(s.second) }.maxByOrNull { s -> s.third })
            returnlist.add(groups[Direction.too_little]!!.filter { s -> !notedQ(s.second) }
                .maxByOrNull { s -> s.third })
            returnlist.add(groups[Direction.too_little]!!.filter { s -> eqset.contains(s.second) }
                .maxByOrNull { s -> s.third })
        }
        if (groups[Direction.take_off] != null) {
            returnlist.add(groups[Direction.take_off]!!.maxByOrNull { s -> s.third })
        }

        if (!checkgoaleq(goal_eqp)){
            var randomDeposit =
                inv.items.filter { s -> !eqset.contains(s.first) && !notedQ(s.first) && !stackableQ(s.first) }
                    .maxByOrNull { s -> s.second }

            if (randomDeposit != null) {
                returnlist.add(
                    Triple(too_much, randomDeposit.first, randomDeposit.second)
                )
            }
        }

        return returnlist.filterNotNull()

    }


    fun nextFilter(goal_eqp: List<Triple<Int, Int, Int>>): Boolean {
        if (openCounter == 1 && open && !checkgoaleq(goal_eqp)) {
            return false
        }

        return openCounter != 2 && noteCounter != 2 && unCounter != 3 && (count() <= 28)

    }


    fun makeMakeNext(
        goal_inv: List<Triple<Int, Int, Int>>,
        goal_eqp: List<Triple<Int, Int, Int>>,
        set_Of_goals: Set<Int>
    ): List<MoveOb11> {
        val returnls = mutableListOf<MoveOb11>()
        val t: List<Triple<Direction, Int, Int>> = prosess(goal_inv, goal_eqp, set_Of_goals)

        t.forEachIndexed { i, triple ->
            try {
                returnls.add(
                    when (triple.first) {
                        Direction.too_much -> deposit(triple.second, triple.third)
                        Direction.too_little -> withdraw(triple.second, triple.third)
                        Direction.wear -> eq(triple.second)
                        Direction.take_off -> uneq(triple.second)
                    }
                )
            }catch ( e:Exception){

            }
        }
        return returnls.filter { nextFilter(goal_eqp) }
    }

    fun checkgoal(goal_inv: List<Triple<Int, Int, Int>>, goal_eqp: List<Triple<Int, Int, Int>>): Boolean {

        if (!checkgoalInv(goal_inv)) {
            return false
        }

        if (!checkgoaleq(goal_eqp)) {
            return false
        }


        return true

    }


    var checkinv =0
    fun checkgoalInv(goal_inv: List<Triple<Int, Int, Int>>): Boolean {
        if (checkinv ==0) {
            checkinv = 1
            if (inv.items.any { s ->
                    !goal_inv.containsKey(s.first)

                }) return false
            if (goal_inv.isEmpty() && inv.items.isNotEmpty()) {
                return false
            }

            if (goal_inv.toList().any { s ->

                    val testItem = inv[s.first]
                    if (testItem == null) {
                        true
                    } else {
                        testItem !in s.second..s.third
                    }

                }) return false
            checkinv = 2
        }

        return checkinv==2

    }
    var checkeq =0
    fun checkgoaleq(goal_eqp: List<Triple<Int, Int, Int>>): Boolean {

        if (checkeq ==0){
            checkeq=1
            if (eq.items.any {

                        s ->
                    !goal_eqp.containsKey(s.first)

                }) return false

            if (goal_eqp.any { s ->
                    val testItem = eq[s.first]
                    if (testItem == null) {
                        true
                    } else {
                        testItem !in s.second..s.third
                    }
                }) return false
            if (goal_eqp.isEmpty() && eq.items.isNotEmpty()) {
                return false
            }
            checkeq=2
        }


        return checkeq==2

    }


    override fun hashCode(): Int {
        return eq.hashCode() * 7 + inv.hashCode() * 13 + bank.hashCode() * 17 + if (open) 0 else 97
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: MoveOb11?): Int {
        return if (other!!.moves < this.moves) 1 else -1

    }

}

private fun MutableList<Triple<Int, Int, Int>>.firstOrNullREMove(key: Int): Triple<Int, Int, Int>? {
    var returnvalue: Triple<Int, Int, Int>? = null
    forEachIndexed { i, T ->

        if (T.first == key) {
            this.removeAt(i)
            return T
        }
    }

    return returnvalue
}

private fun List<Triple<Int, Int, Int>>.containsKey(key: Int): Boolean {
    return this.any { s -> (s as Triple<*, *, *>).first == key }
}

