package com.artamonov.millionplanets.model

import androidx.annotation.StringDef

object WeaponClassType {
    const val LASER = "LASER"
    const val GUN = "GUN"

    @StringDef(LASER, GUN)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationWeaponClassType
}