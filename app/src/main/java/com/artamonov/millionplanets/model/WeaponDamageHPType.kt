package com.artamonov.millionplanets.model

import androidx.annotation.IntDef

object WeaponDamageHPType {
    const val LIGHT_LASER = 25
    const val MEDIUM_LASER = 40
    const val HEAVY_LASER = 60
    const val MILITARY_LASER = 80
    const val HEAVY_MILITARY_LASER = 100
    const val LIGHT_GUN = 50
    const val HEAVY_GUN = 120
    const val MILITARY_GUN = 160
    const val HEAVY_MILITARY_GUN = 200

    @IntDef(LIGHT_LASER, MEDIUM_LASER, HEAVY_LASER, MILITARY_LASER, HEAVY_MILITARY_LASER,
            LIGHT_GUN, HEAVY_GUN, MILITARY_GUN, HEAVY_MILITARY_GUN)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationWeaponDamageHPType
}