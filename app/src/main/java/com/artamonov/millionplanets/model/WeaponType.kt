package com.artamonov.millionplanets.model

import androidx.annotation.StringDef

object WeaponType {
    const val LIGHT_LASER = "LIGHT_LASER"
    const val MEDIUM_LASER = "MEDIUM_LASER"
    const val HEAVY_LASER = "HEAVY_LASER"
    const val MILITARY_LASER = "MILITARY_LASER"
    const val HEAVY_MILITARY_LASER = "HEAVY_MILITARY_LASER"
    const val LIGHT_GUN = "LIGHT_GUN"
    const val MEDIUM_GUN = "MEDIUM_GUN"
    const val HEAVY_GUN = "HEAVY_GUN"
    const val MILITARY_GUN = "MILITARY_GUN"
    const val HEAVY_MILITARY_GUN = "HEAVY_MILITARY_GUN"

    @StringDef(LIGHT_LASER, MEDIUM_LASER, HEAVY_LASER, MILITARY_LASER, HEAVY_MILITARY_LASER,
            LIGHT_GUN, MEDIUM_GUN, HEAVY_GUN, MILITARY_GUN, HEAVY_MILITARY_GUN)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationWeaponType
}