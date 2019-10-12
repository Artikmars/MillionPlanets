package com.artamonov.millionplanets.model

import java.io.Serializable

class User : Serializable {
    var x: Long = 0
    var y: Long = 0

    var sumXY: Long = 0
    var ship: String? = null
    var hp: Long = 0
    var shield: Long = 0
    var damage: List<Long>? = null
    var weaponType: List<String>? = null
    var cargo: Long = 0
    var scanner_capacity: Long = 0
    var jump: Long = 0
    var shipPrice: Long = 0
    var shipClass: String? = null
    var weaponSlots: Long = 0

    var money: Long = 0
    var resource_iron: Long = 0
    var sectors: Long = 0
    var email: String? = null
    var nickname: String? = null
    var fuel: Long = 0
    var type: String? = null
    var moveToObjectName: String? = null
    var moveToObjectDistance: Long = 0
    var moveToObjectType: String? = null

    constructor(x: Long, y: Long) {
        this.x = x
        this.y = y
    }

    constructor()
}
