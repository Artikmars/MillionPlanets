package com.artamonov.millionplanets.model

import androidx.annotation.StringDef

object SpaceObjectType {
    const val PLANET = "planet"
    const val USER = "user"
    const val FUEL = "fuel"
    const val DEBRIS = "debris"
    const val METEORITE_FIELD = "meteorite_field"

    @StringDef(PLANET, USER, FUEL, DEBRIS, METEORITE_FIELD)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationSpaceObjectType
}