package com.artamonov.millionplanets.utils

import android.app.Activity
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Module
import com.artamonov.millionplanets.model.Weapon
import com.artamonov.millionplanets.model.WeaponDamageHPType
import com.artamonov.millionplanets.model.WeaponPriceType
import com.artamonov.millionplanets.model.PlanetType.DESERT
import com.artamonov.millionplanets.model.PlanetType.EARTH
import com.artamonov.millionplanets.model.PlanetType.FOREST
import com.artamonov.millionplanets.model.PlanetType.ICE
import com.artamonov.millionplanets.model.PlanetType.MOUNTAIN
import com.artamonov.millionplanets.model.PlanetType.OCEAN
import com.artamonov.millionplanets.model.PlanetType.TOXIC
import com.artamonov.millionplanets.model.ResourceType.DIAMOND
import com.artamonov.millionplanets.model.ResourceType.FOOD
import com.artamonov.millionplanets.model.ResourceType.FUEL
import com.artamonov.millionplanets.model.ResourceType.GOLD
import com.artamonov.millionplanets.model.ResourceType.IRON
import com.artamonov.millionplanets.model.ResourceType.PETROL
import com.artamonov.millionplanets.model.ResourceType.URANIUM
import com.artamonov.millionplanets.model.ResourceType.WATER
import com.artamonov.millionplanets.model.ResourceType.WOOD
import com.artamonov.millionplanets.model.SpaceshipType.FIGHTER
import com.artamonov.millionplanets.model.SpaceshipType.RESEARCH_SPACESHIP
import com.artamonov.millionplanets.model.SpaceshipType.TRADER
import com.artamonov.millionplanets.model.WeaponClassType.GUN
import com.artamonov.millionplanets.model.WeaponClassType.LASER
import com.artamonov.millionplanets.model.WeaponGenerationType.MK_1
import com.artamonov.millionplanets.model.WeaponType.HEAVY_GUN
import com.artamonov.millionplanets.model.WeaponType.HEAVY_LASER
import com.artamonov.millionplanets.model.WeaponType.HEAVY_MILITARY_GUN
import com.artamonov.millionplanets.model.WeaponType.HEAVY_MILITARY_LASER
import com.artamonov.millionplanets.model.WeaponType.LIGHT_GUN
import com.artamonov.millionplanets.model.WeaponType.LIGHT_LASER
import com.artamonov.millionplanets.model.WeaponType.MEDIUM_GUN
import com.artamonov.millionplanets.model.WeaponType.MEDIUM_LASER
import com.artamonov.millionplanets.model.WeaponType.MILITARY_GUN
import com.artamonov.millionplanets.model.WeaponType.MILITARY_LASER
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar

private val fighter = User()
    private val trader = User()
    private val rs = User()

fun Activity.showSnackbarError(stringMsg: String?) {
    stringMsg?.let { Snackbar.make(findViewById(android.R.id.content), stringMsg, LENGTH_SHORT)
            .show() } }

    fun Number.getCurrentModuleInfo(): Module? {

        when (this.toInt()) {
            0 -> return Module(LIGHT_LASER, LASER, MK_1, WeaponDamageHPType.LIGHT_LASER,
                    WeaponPriceType.LIGHT_LASER)
            1 -> return Module(MEDIUM_LASER, LASER, MK_1, WeaponDamageHPType.MEDIUM_LASER,
                    WeaponPriceType.MEDIUM_LASER)
            2 -> return Module(HEAVY_LASER, LASER, MK_1, WeaponDamageHPType.HEAVY_LASER,
                    WeaponPriceType.HEAVY_LASER)
            3 -> return Module(MILITARY_LASER, LASER, MK_1, WeaponDamageHPType.MILITARY_LASER,
                    WeaponPriceType.MILITARY_LASER)
            4 -> return Module(HEAVY_MILITARY_LASER, LASER, MK_1,
                    WeaponDamageHPType.HEAVY_MILITARY_LASER, WeaponPriceType.HEAVY_MILITARY_LASER)
            5 -> return Module(LIGHT_GUN, GUN, MK_1, WeaponDamageHPType.LIGHT_GUN,
                    WeaponPriceType.LIGHT_GUN)
            6 -> return Module(MEDIUM_GUN, GUN, MK_1, WeaponDamageHPType.MILITARY_LASER,
                    WeaponPriceType.MEDIUM_LASER)
            7 -> return Module(HEAVY_GUN, GUN, MK_1, WeaponDamageHPType.HEAVY_GUN,
                    WeaponPriceType.HEAVY_GUN)
            8 -> return Module(MILITARY_GUN, GUN, MK_1, WeaponDamageHPType.MILITARY_GUN,
                    WeaponPriceType.MILITARY_GUN)
            9 -> return Module(HEAVY_MILITARY_GUN, GUN, MK_1, WeaponDamageHPType.HEAVY_MILITARY_GUN,
                    WeaponPriceType.HEAVY_LASER)
        }
        return null
    }

    fun Long.getResourceItemNameById(): String? {

        when (this.toInt()) {
            0 -> return DIAMOND
            1 -> return FUEL
            2 -> return FOOD
            3 -> return GOLD
            4 -> return IRON
            5 -> return PETROL
            6 -> return WOOD
            7 -> return URANIUM
            8 -> return WATER
        }
        return null
    }

fun Int.getResourceItemName(): String? {

    when (this) {
        0 -> return DIAMOND
        1 -> return FUEL
        2 -> return FOOD
        3 -> return GOLD
        4 -> return IRON
        5 -> return PETROL
        6 -> return WOOD
        7 -> return URANIUM
        8 -> return WATER
    }
    return null
}

//    fun getCargoItemByResourceId(user: User, resourceId: Int) : Item {
//        return user.cargo?.filter { it.itemId ==
//                resourceId.toLong() }!![0]
//    }

    fun getAllWeapons(): MutableList<Module> {
        val weapons = mutableListOf<Module>()
        weapons.add(0, Module(LIGHT_LASER, LASER, MK_1, WeaponDamageHPType.LIGHT_LASER,
                WeaponPriceType.LIGHT_LASER))
        weapons.add(1, Module(MEDIUM_LASER, LASER, MK_1, WeaponDamageHPType.MEDIUM_LASER,
                WeaponPriceType.MEDIUM_LASER))
        weapons.add(2, Module(HEAVY_LASER, LASER, MK_1, WeaponDamageHPType.HEAVY_LASER,
                WeaponPriceType.HEAVY_LASER))
        weapons.add(3, Module(MILITARY_LASER, LASER, MK_1, WeaponDamageHPType.MILITARY_LASER,
                WeaponPriceType.MILITARY_LASER))
        weapons.add(4, Module(HEAVY_MILITARY_LASER, LASER, MK_1,
                WeaponDamageHPType.HEAVY_MILITARY_LASER, WeaponPriceType.HEAVY_MILITARY_LASER))
        weapons.add(5, Module(LIGHT_GUN, GUN, MK_1, WeaponDamageHPType.LIGHT_GUN,
                WeaponPriceType.LIGHT_GUN))
        weapons.add(6, Module(MEDIUM_GUN, GUN, MK_1, WeaponDamageHPType.MILITARY_LASER,
                WeaponPriceType.MEDIUM_LASER))
        weapons.add(7, Module(HEAVY_GUN, GUN, MK_1, WeaponDamageHPType.HEAVY_GUN,
                WeaponPriceType.HEAVY_GUN))
        weapons.add(8, Module(MILITARY_GUN, GUN, MK_1, WeaponDamageHPType.MILITARY_GUN,
                WeaponPriceType.MILITARY_GUN))
        weapons.add(9, Module(HEAVY_MILITARY_GUN, GUN, MK_1, WeaponDamageHPType.HEAVY_MILITARY_GUN,
                WeaponPriceType.HEAVY_LASER))
        return weapons
    }

    fun getCurrentShipInfo(position: Int): User? {

        val weaponList = mutableListOf<Weapon>()
        weaponList.add(Weapon(0, true))
        when (position) {
            0 -> {
                fighter.cargoCapacity = 10
                fighter.fuel = 20
                fighter.jump = 10
                fighter.hp = 50
                fighter.shield = 100
                fighter.scanner_capacity = 15
                fighter.ship = FIGHTER
                fighter.shipPrice = 0
                fighter.shipClass = "Warrior"
                fighter.weaponSlots = 3
                fighter.weapon = weaponList
                return fighter
            }
            1 -> {
                trader.hp = 100
                trader.shield = 50
                trader.cargoCapacity = 150
                trader.jump = 25
                trader.fuel = 50
                trader.scanner_capacity = 30
                trader.weaponSlots = 1
                trader.shipPrice = 50000
                trader.ship = TRADER
                trader.shipClass = "Nightfall"
                trader.weapon = weaponList

                return trader
            }
            2 -> {
                rs.hp = 150
                rs.shield = 50
                rs.cargoCapacity = 75
                rs.jump = 75
                rs.fuel = 150
                rs.scanner_capacity = 100
                rs.weaponSlots = 2
                rs.shipPrice = 100000
                rs.ship = RESEARCH_SPACESHIP
                rs.shipClass = "Interstellar"
                rs.weapon = weaponList

                return rs
            }
        }
        return null
    }

    fun getCurrentShipInfo(shipName: String): User {

        when (shipName) {
            FIGHTER -> {
                fighter.hp = 50
                fighter.shield = 100
                fighter.cargoCapacity = 10
                fighter.jump = 10
                fighter.fuel = 20
                fighter.scanner_capacity = 15
                fighter.weaponSlots = 3
                fighter.shipPrice = 0
                fighter.ship = FIGHTER
                fighter.shipClass = "Warrior"
                return fighter
            }
            TRADER -> {
                trader.hp = 100
                trader.shield = 50
                trader.cargoCapacity = 150
                trader.jump = 25
                trader.fuel = 50
                trader.scanner_capacity = 30
                trader.weaponSlots = 1
                trader.shipPrice = 50000
                trader.ship = TRADER
                trader.shipClass = "Nightfall"
                return trader
            }
            RESEARCH_SPACESHIP -> {
                rs.hp = 150
                rs.shield = 50
                rs.cargoCapacity = 75
                rs.jump = 75
                rs.fuel = 150
                rs.scanner_capacity = 100
                rs.weaponSlots = 2
                rs.shipPrice = 100000
                rs.ship = RESEARCH_SPACESHIP
                rs.shipClass = "Interstellar"
                return rs
            }
        }
        return fighter
    }

    fun getShipFuelInfo(ship: String): Long {
        when (ship) {
            FIGHTER -> return 20L
            TRADER -> return 50L
            RESEARCH_SPACESHIP -> return 150L
        }
        return 0L
    }

    fun String?.getResourceTypeAmountByPlanetType(): Int {

        when (this) {
            DESERT -> {
                return 9
            }
            EARTH -> {
                return 9
            }
            FOREST -> {
                return 9
            }
            ICE -> {
                return 9
            }
            MOUNTAIN -> {
                return 9
            }
            OCEAN -> {
                return 9
            }
            TOXIC -> {
                return 9
            }
        }
        return 9
    }

    fun getFilteredInstalledWeaponList(weapon: List<Weapon>): List<Weapon> {
        return weapon.filter { it.isWeaponInstalled == true }
    }
