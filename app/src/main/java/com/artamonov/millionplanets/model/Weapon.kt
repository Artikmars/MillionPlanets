package com.artamonov.millionplanets.model

class Weapon {
    constructor(weaponId: Long?, isWeaponInstalled: Boolean?) {
        this.weaponId = weaponId
        this.isWeaponInstalled = isWeaponInstalled
    }

    constructor(weaponId: Long?) {
        this.weaponId = weaponId
    }

    constructor()

    var weaponId: Long? = null
    var isWeaponInstalled: Boolean? = null
}
