package com.artamonov.millionplanets.inventory

import com.artamonov.millionplanets.model.User

interface InventoryActivityView {

    fun updateCargoCapacityCounter(user: User)

    fun setProgressBar()

    fun updateData()
}
