package com.artamonov.millionplanets.model

import java.io.Serializable

class User : Serializable {
    var x: Long = 544
    var y: Long = 553

    var sumXY: Long = 1097
    var ship: String? = null
    var hp: Long = 0
    var shield: Long = 0
    var damage: List<Long>? = null
    var weapon: List<Long>? = listOf(0)
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

    constructor(ship: String?, shield: Long, damage: List<Long>?, weapon: List<Long>?, cargo: Long, scanner_capacity: Long, jump: Long, shipPrice: Long, shipClass: String?, weaponSlots: Long, fuel: Long, type: String?) {
        this.ship = ship
        this.shield = shield
        this.damage = damage
        this.weapon = weapon
        this.cargo = cargo
        this.scanner_capacity = scanner_capacity
        this.jump = jump
        this.shipPrice = shipPrice
        this.shipClass = shipClass
        this.weaponSlots = weaponSlots
        this.fuel = fuel
        this.type = type
    }

    constructor(
        cargo: Long,
        fuel: Long,
        hp: Long,
        jump: Long,
        scannerCapacity: Long,
        shield: Long,
        shipPrice: Long,
        ship: String?,
        shipClass: String?,
        weapon: List<Long>,
        weaponSlots: Long
    ) {
        this.ship = ship
        this.shield = shield
        this.weapon = weapon
        this.cargo = cargo
        this.scanner_capacity = scannerCapacity
        this.jump = jump
        this.shipPrice = shipPrice
        this.shipClass = shipClass
        this.weaponSlots = weaponSlots
        this.fuel = fuel
        this.hp = hp
    }
}
