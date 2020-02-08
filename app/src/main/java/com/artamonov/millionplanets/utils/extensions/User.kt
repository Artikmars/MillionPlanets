package com.artamonov.millionplanets.utils.extensions

import com.artamonov.millionplanets.model.User

class User

fun User.getCurrentCargoCapacity() = this.cargo?.sumBy { it.itemAmount!!.toInt() }!!.toLong()
