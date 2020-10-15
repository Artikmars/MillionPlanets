package com.artamonov.millionplanets.utils

import java.util.Random

object RandomUtils {

    private const val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"
    fun getRandomDebrisName(sizeOfRandomString: Int): String {
        val random = Random()
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString) sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }

    val randomCoordinate: Int
        get() {
            val r = Random()
            return r.nextInt(600 - 500) + 500
        }

    val randomDebrisIron: Int
        get() {
            val r = Random()
            val min = 100
            val max = 300
            return r.nextInt(max - min + 1) + min
        }
}