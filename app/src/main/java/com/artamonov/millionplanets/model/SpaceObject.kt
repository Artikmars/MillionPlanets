package com.artamonov.millionplanets.model

open class SpaceObject(
    var name: String? = null,
    var distance: Int = 0,
    var planetClass: String? = null,
    var planetSectors: Long = 0,
    var planetSectorsPrice: Long = 0,
    var ironAmount: Long = 0,
    var debrisIronAmount: Long = 0,
    var planetSize: String? = null,
    var resourceName: String? = null,
    var price_buy_iron: Long = 0,
    var price_sell_iron: Long = 0
) : User()