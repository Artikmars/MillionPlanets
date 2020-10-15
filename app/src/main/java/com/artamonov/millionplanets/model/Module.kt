package com.artamonov.millionplanets.model

class Module(
    var name: String,
    var type: String? = null,
    var moduleClass: String? = null,
    var damageHP: Long? = 0,
    var price: Long? = 0,
    var isBought: Boolean? = false,
    var slots: Long? = 0
)