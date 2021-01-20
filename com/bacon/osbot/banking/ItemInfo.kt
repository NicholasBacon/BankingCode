package com.bacon.osbot.banking

import java.io.File
import java.nio.file.Paths

val test= CharArray(30000)
fun toSet(fileName: String,int: Int): BooleanArray {
    val x = File(fileName).useLines { it.toList() }.map { s -> s.toInt() }.toList()
    val maxItem = x.maxOrNull()!!
    val returnArray = BooleanArray(maxItem + 1)
    x.forEach { s ->
        if (int!=0){
            test[s]= int.toChar()
        }
        returnArray[s] = true
    }
    return returnArray
}

fun BooleanArray.contains(item: Int): Boolean {
    if (item in this.indices) {
        return this[item]
    }
    return false
}


fun toMap(fileName: String): Map<Int, Int> = File(fileName).useLines { it.toList() }.associate { s ->
    val x = s.split(" ")
    x[0].toInt() to x[1].toInt()
}
 val DIRECTORY = Paths.get(
    System.getProperty("user.home"),
    "OSBot",
    "Data",
    "Banking",
).toString()



val noted = toSet("$DIRECTORY/Noted",0)
val noteable = toSet("$DIRECTORY/Noteable",0)
val stackable = toSet("$DIRECTORY/Stackable",0)
val H2 = toSet("$DIRECTORY/eq/2H",4)
val FEET = toSet("$DIRECTORY/eq/FEET",5)
val WEAPON = toSet("$DIRECTORY/eq/WEAPON",6)
val RING = toSet("$DIRECTORY/eq/RING",7)
val ARROWS = toSet("$DIRECTORY/eq/ARROWS",8)
val LEGS = toSet("$DIRECTORY/eq/LEGS",9)
val SHIELD = toSet("$DIRECTORY/eq/SHIELD",10)
val HANDS = toSet("$DIRECTORY/eq/HANDS",11)
val CAPE = toSet("$DIRECTORY/eq/CAPE",12)
val HAT = toSet("$DIRECTORY/eq/HAT",13)
val CHEST = toSet("$DIRECTORY/eq/CHEST",14)
val AMULET = toSet("$DIRECTORY/eq/AMULET",15)

val badnote_NNtn = toMap("$DIRECTORY/Badnote")
val badnote_Ntnn = badnote_NNtn.entries.associate { (k, v) -> v to k }

fun notedQ(id: Int): Boolean = noted.contains(id)
fun stackableQ(id: Int): Boolean = stackable.contains(id)


fun getNNotedMaybe(id: Int): Int {
    if (noted.contains(id)) {

        return badnote_Ntnn[id] ?: id - 1
    }
    return id
}

fun getNotedMaybe(id: Int): Int {
    if (!noted.contains(id) && noteable.contains(id)) {
        return badnote_NNtn[id] ?: id + 1
    }
    return id
}


