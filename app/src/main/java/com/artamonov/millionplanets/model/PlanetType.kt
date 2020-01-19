package com.artamonov.millionplanets.model

import androidx.annotation.StringDef

object PlanetType {
    const val DESERT = "desert"
    const val EARTH = "earth"
    const val FOREST = "forest"
    const val ICE = "ice"
    const val MOUNTAIN = "mountain"
    const val OCEAN = "ocean"
    const val TOXIC = "toxic"

    @StringDef(DESERT, EARTH, FOREST, ICE, MOUNTAIN, OCEAN, TOXIC)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationPlanetType
}