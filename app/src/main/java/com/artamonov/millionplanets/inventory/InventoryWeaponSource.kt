package com.artamonov.millionplanets.inventory

import com.artamonov.millionplanets.model.Weapon

interface InventoryWeaponSource {

    fun setEnabled(position: Int, boolean: Boolean)

    fun getWeaponList(): List<Weapon>

    fun get(position: Int): Long

    fun getCount(): Int

    fun isInstalled(position: Int): Boolean
}
