package com.artamonov.millionplanets.model

import androidx.annotation.LongDef

object WeaponPriceType {
    const val LIGHT_LASER = 0L
    const val MEDIUM_LASER = 5000L
    const val HEAVY_LASER = 12000L
    const val MILITARY_LASER = 15000L
    const val HEAVY_MILITARY_LASER = 20000L
    const val LIGHT_GUN = 2500L
    const val HEAVY_GUN = 7500L
    const val MILITARY_GUN = 10000L

    @LongDef(LIGHT_LASER, MEDIUM_LASER, HEAVY_LASER, MILITARY_LASER, HEAVY_MILITARY_LASER,
            LIGHT_GUN, HEAVY_GUN, MILITARY_GUN)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationWeaponPriceType
}