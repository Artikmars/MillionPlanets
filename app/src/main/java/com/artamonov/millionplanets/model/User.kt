package com.artamonov.millionplanets.model

import java.io.Serializable

class User : Serializable {

    var cargoCapacity: Long = 0
    var cargo: MutableList<Item>? = mutableListOf()
    var email: String? = null
    var fuel: Long = 0
    var jump: Long = 0
    var hp: Long = 0
    var money: Long = 0
    var moveToObjectDistance: Long = 0
    var locationName: String? = null
    var locationType: String? = null
    var nickname: String? = null
    var resource_iron: Long = 0
    var scanner_capacity: Long = 0
    var sectors: Long = 0
    var shield: Long = 0
    var ship: String? = null
    var shipClass: String? = null
    var shipPrice: Long = 0
    var sumXY: Long = 1097
    var type: String? = null
    var weapon: List<Weapon>? = listOf()
    var weaponSlots: Long = 0

    var x: Long = 544
    var y: Long = 553

    constructor(x: Long, y: Long) {
        this.x = x
        this.y = y
    }

    constructor()

    constructor(
        ship: String?,
        shield: Long,
        weapon: List<Weapon>?,
        cargoCapacity: Long,
        scanner_capacity: Long,
        jump: Long,
        shipPrice: Long,
        shipClass: String?,
        weaponSlots: Long,
        fuel: Long,
        type: String?
    ) {
        this.ship = ship
        this.shield = shield
        this.weapon = weapon
        this.cargoCapacity = cargoCapacity
        this.scanner_capacity = scanner_capacity
        this.jump = jump
        this.shipPrice = shipPrice
        this.shipClass = shipClass
        this.weaponSlots = weaponSlots
        this.fuel = fuel
        this.type = type
    }

    constructor(
        cargoCapacity: Long,
        fuel: Long,
        hp: Long,
        jump: Long,
        scannerCapacity: Long,
        shield: Long,
        shipPrice: Long,
        ship: String?,
        shipClass: String?,
        weapon: List<Weapon>?,
        weaponSlots: Long
    ) {
        this.ship = ship
        this.shield = shield
        this.weapon = weapon
        this.cargoCapacity = cargoCapacity
        this.scanner_capacity = scannerCapacity
        this.jump = jump
        this.shipPrice = shipPrice
        this.shipClass = shipClass
        this.weaponSlots = weaponSlots
        this.fuel = fuel
        this.hp = hp
    }
}
