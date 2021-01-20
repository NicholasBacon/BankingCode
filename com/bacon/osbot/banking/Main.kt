package com.bacon.osbot.banking

import com.bacon.osbot.banking.trash.MoveOb11
import com.bacon.osbot.banking.trash.time


fun main() {
        //test code
    var ginv = mutableListOf<Triple<Int, Int, Int>>()
    var geq =mutableListOf<Triple<Int, Int, Int>>()
    val inv = EQ()
    val bank = EQ()


    ginv.add(Triple(1,1, 1))
    ginv.add(Triple(2,1, 1))
    ginv.add(Triple(3,1, 1))
    ginv.add(Triple(4,1, 1))
    ginv.add(Triple(5,1, 1))
    ginv.add(Triple(6,1, 1))
    ginv.add(Triple(7,1, 1))
    ginv.add(Triple(8,1, 1))
    ginv.add(Triple(9,1, 1))
    ginv.add(Triple(10,1, 1))
    ginv.add(Triple(11,1, 1))
    ginv.add(Triple(12,1, 1))
    ginv.add(Triple(13,1, 1))
    ginv.add(Triple(14,1, 1))
    ginv.add(Triple(15,1, 1))
    ginv.add(Triple(16,1, 1))
    ginv.add(Triple(17,1, 1))
    ginv.add(Triple(18,1, 1))
    ginv.add(Triple(19,1, 1))
    ginv.add(Triple(20,1, 1))
    ginv.add(Triple(21,1, 1))
    ginv.add(Triple(22,1, 1))
    ginv.add(Triple(23,1, 1))
//    ginv.add(Triple(19,1, 1))
//    ginv.add(Triple(20,1, 1))
//    ginv.add(Triple(21,1, 1))
//    ginv.add(Triple(22,1, 1))
//    ginv.add(Triple(23,1, 1))
    geq.add(Triple( 5553,1, 1))
    geq.add(Triple( 5554,1, 1))
    geq.add(Triple( 5555,1, 1))
    geq.add(Triple( 5556,1, 1))
    geq.add(Triple( 5557,1, 1))



    inv[5553] = 1
    inv[5554] = 1
    inv[5555] = 1
    inv[5556] = 1
    inv[5557] = 1
//    inv[5556] = 28
//    inv[5557] = 29
//    inv[5556] = 30
//    inv[5557] = 29

    var t = EQ()
    t.add(Pair(11804, 1))
    t.add(Pair(11832, 1))


//    inv[91] = 14
//    inv[225] = 14


    bank[314] = 1000
    bank[3140] = 1000
    bank[91] = 1000
    bank[225] = 1000
    bank[5553] = 1000
    bank[5554] = 1000
    bank[5555] = 1000
    bank[5556] = 1000
    bank[5557] = 1000

    bank[0] = 1000
    bank[1] = 1000
    bank[2] = 1000
    bank[3] = 1000
    bank[4] = 1000
    bank[5] = 1000
    bank[6] = 1000
    bank[8] = 1000
    bank[10] = 1000
    bank[12] = 1000
    bank[14] = 1000
    bank[15] = 1000
    bank[16] = 1000
    bank[17] = 1000
    bank[18] = 1000
    bank[19] = 1000
    bank[20] = 1000
    bank[21] = 1000
    bank[22] = 1000
    bank[23] = 1000
    bank[24] = 1000
    bank[25] = 1000


    var g = SuperBanker(null)
    var x = System.currentTimeMillis()
    println(
        g.findGoal(MoveOb11(inv, t, bank, false, 0, emptyList(), 1, true, true, 0, 0, 0), ginv, geq)
    )

    println("${System.currentTimeMillis() - x}")
    println(time)


}