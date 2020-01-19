package com.artamonov.millionplanets.inventory

import com.artamonov.millionplanets.model.Item

interface InventoryCargoSource {

    fun getCargoList(): List<Item>?

    fun get(position: Int): Item

    fun getCount(): Int

    fun getItemAmount(position: Int): Long?
}
