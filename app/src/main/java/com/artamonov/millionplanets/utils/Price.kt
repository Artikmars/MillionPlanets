package com.artamonov.millionplanets.utils

object Price {

    fun getPlayerBuyPrice(resourceId: Int?): Int {

        when (resourceId) {
            0 -> return 100
            1 -> return 150
            2 -> return 200
            3 -> return 250
            4 -> return 300
            5 -> return 350
            6 -> return 400
            7 -> return 450
            8 -> return 500
        }
        return 0
    }

    fun getPlayerSellPrice(resourceId: Long?): Long {

        when (resourceId?.toInt()) {
            0 -> return 50
            1 -> return 75
            2 -> return 100
            3 -> return 125
            4 -> return 150
            5 -> return 175
            6 -> return 200
            7 -> return 225
            8 -> return 250
        }

        return 0
    }
}