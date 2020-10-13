package com.artamonov.millionplanets.model

import androidx.annotation.StringDef

object SpaceshipType {
    const val FIGHTER = "Fighter"
    const val TRADER = "Trader"
    const val RESEARCH_SPACESHIP = "Research Spaceship"

    @StringDef(FIGHTER, TRADER, RESEARCH_SPACESHIP)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationSpaceshipType
}