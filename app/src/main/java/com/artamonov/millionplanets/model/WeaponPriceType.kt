package com.artamonov.millionplanets.model

import androidx.annotation.IntDef

object WeaponPriceType {
    const val LIGHT_LASER = 0
    const val MEDIUM_LASER = 5000
    const val HEAVY_LASER = 12000
    const val MILITARY_LASER = 15000
    const val HEAVY_MILITARY_LASER = 20000
    const val LIGHT_GUN = 2500
    const val HEAVY_GUN = 7500
    const val MILITARY_GUN = 10000

    @IntDef(LIGHT_LASER, MEDIUM_LASER, HEAVY_LASER, MILITARY_LASER, HEAVY_MILITARY_LASER,
            LIGHT_GUN, HEAVY_GUN, MILITARY_GUN)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationWeaponPriceType
}