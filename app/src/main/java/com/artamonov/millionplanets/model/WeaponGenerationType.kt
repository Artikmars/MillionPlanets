package com.artamonov.millionplanets.model

import androidx.annotation.StringDef

object WeaponGenerationType {

    const val MK_1 = "MK_1"
    const val MK_2 = "MK_2"
    const val MK_3 = "MK_3"

    @StringDef(MK_1, MK_2, MK_3)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationWeaponGenerationType
}