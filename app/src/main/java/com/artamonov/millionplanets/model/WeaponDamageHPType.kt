package com.artamonov.millionplanets.model

import androidx.annotation.LongDef

object WeaponDamageHPType {
    const val LIGHT_LASER = 25L
    const val MEDIUM_LASER = 40L
    const val HEAVY_LASER = 60L
    const val MILITARY_LASER = 80L
    const val HEAVY_MILITARY_LASER = 100L
    const val LIGHT_GUN = 50L
    const val HEAVY_GUN = 120L
    const val MILITARY_GUN = 160L
    const val HEAVY_MILITARY_GUN = 200L

    @LongDef(LIGHT_LASER, MEDIUM_LASER, HEAVY_LASER, MILITARY_LASER, HEAVY_MILITARY_LASER,
            LIGHT_GUN, HEAVY_GUN, MILITARY_GUN, HEAVY_MILITARY_GUN)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationWeaponDamageHPType
}