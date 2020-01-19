package com.artamonov.millionplanets.model

import androidx.annotation.StringDef

object ResourceType {
    const val DIAMOND = "Diamond"
    const val FUEL = "Fuel"
    const val FOOD = "Food"
    const val GOLD = "Gold"
    const val IRON = "Iron"
    const val PETROL = "Petrol"
    const val URANIUM = "Uranium"
    const val WATER = "Water"
    const val WOOD = "Wood"

    @StringDef(DIAMOND, FUEL, FOOD, GOLD, IRON, PETROL, WOOD, URANIUM, WATER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AnnotationResourceType
}