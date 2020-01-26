package com.artamonov.millionplanets.model

import androidx.annotation.StringDef

object NumberPickerDialogType {
    const val GET_FUEL = "GET_FUEL"
    const val INVENTORY = "INVENTORY"
    const val MARKET_PLAYER_BUYS = "MARKET_PLAYER_BUYS"
    const val MARKET_PLAYER_SELLS = "MARKET_PLAYER_SELLS"

    @StringDef(GET_FUEL, INVENTORY, MARKET_PLAYER_BUYS, MARKET_PLAYER_SELLS)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationNumberPickerDialogType
}