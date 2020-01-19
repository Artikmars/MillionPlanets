package com.artamonov.millionplanets.model

import androidx.annotation.StringDef

object NumberPickerDialogType {
    const val MARKET_PLAYER_SELLS = "MARKET_PLAYER_SELLS"
    const val MARKET_PLAYER_BUYS = "MARKET_PLAYER_BUYS"
    const val INVENTORY = "INVENTORY"

    @StringDef(MARKET_PLAYER_SELLS, MARKET_PLAYER_BUYS, INVENTORY)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationNumberPickerDialogType
}