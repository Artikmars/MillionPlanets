package com.artamonov.millionplanets.utils

import androidx.annotation.NonNull

enum class WeaponType {
    LASER,
    GUN;

    companion object {
        fun convert(@NonNull weaponType: String): WeaponType? {
            if (weaponType.equals("laser", ignoreCase = true)) {
                return LASER
            } else if (weaponType.equals("gun", ignoreCase = true)) {
                return GUN
            }
            return null
        }
    }
}
