package com.artamonov.millionplanets.model

import java.io.Serializable

class User : Serializable {
    var x: Int = 0
    var y: Int = 0

    var sumXY: Int = 0
    var ship: String? = null
    var hp: Int = 0
    var shield: Int = 0
    var cargo: Int = 0
    var scanner_capacity: Int = 0
    var jump: Int = 0
    var shipPrice: Int = 0
    var shipClass: String? = null
    var weaponSlots: Int = 0

    var money: Int = 0
    var resource_iron: Int = 0
    var sectors: Int = 0
    var email: String? = null
    var nickname: String? = null
    var fuel: Int = 0
    var type: String? = null
    var moveToObjectName: String? = null
    var moveToObjectDistance: Int = 0
    var moveToObjectType: String? = null

    constructor(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    constructor()
}
