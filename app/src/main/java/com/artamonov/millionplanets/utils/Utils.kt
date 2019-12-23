package com.artamonov.millionplanets.utils

import com.artamonov.millionplanets.model.Module
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Weapon

object Utils {
    private val fighter = User()
    private val trader = User()
    private val rs = User()

    fun getCurrentModuleInfo(position: Long): Module? {

        when (position.toInt()) {
            0 -> return Module("Light Laser", "LASER", "mk I", 25, 0)
            1 -> return Module("Medium Laser", "LASER", "mk II", 40, 5000)
            2 -> return Module("Heavy Laser", "LASER", "mk III", 60, 12000)
            3 -> return Module("Military Laser", "LASER", "ft I", 80, 15000)
            4 -> return Module("Heavy Military Laser", "LASER", "ft II", 100, 20000)
            5 -> return Module("Light Gun", "GUN", "mk I", 50, 2500)
            6 -> return Module("Medium Gun", "GUN", "mk II", 80, 5000)
            7 -> return Module("Heavy Gun", "GUN", "mk III", 120, 7500)
            8 -> return Module("Military Gun", "GUN", "ft I", 160, 10000)
            9 -> return Module("Heavy Military Gun", "GUN", "ft II", 200, 12000)
        }
        return null
    }

    fun getResourceItemName(position: Long): Item? {

        when (position.toInt()) {
            0 -> return Item("Iron")
            1 -> return Item("Fuel")
            2 -> return Item("Food")
            3 -> return Item("Water")
        }
        return null
    }

    fun getCurrentModuleInfo(position: Int): Module? {

        when (position) {
            1 -> return Module("Light Laser", "LASER", "mk I", 25, 0)
            2 -> return Module("Medium Laser", "LASER", "mk II", 40, 5000)
            3 -> return Module("Heavy Laser", "LASER", "mk III", 60, 12000)
            4 -> return Module("Military Laser", "LASER", "ft I", 80, 15000)
            5 -> return Module("Heavy Military Laser", "LASER", "ft II", 100, 20000)
            6 -> return Module("Light Gun", "GUN", "mk I", 50, 2500)
            7 -> return Module("Medium Gun", "GUN", "mk II", 80, 5000)
            8 -> return Module("Heavy Gun", "GUN", "mk III", 120, 7500)
            9 -> return Module("Military Gun", "GUN", "ft I", 160, 10000)
            10 -> return Module("Heavy Military Gun", "GUN", "ft II", 200, 12000)
        }
        return null
    }

    fun getAllWeapons(): MutableList<Module> {
        val weapons: MutableList<Module> = mutableListOf()
        weapons.add(0, Module("Light Laser", "LASER", "mk I", 25, 0))
        weapons.add(1, Module("Medium Laser", "LASER", "mk II", 40, 5000))
        weapons.add(2, Module("Heavy Laser", "LASER", "mk III", 60, 12000))
        weapons.add(3, Module("Military Laser", "LASER", "ft I", 80, 15000))
        weapons.add(4, Module("Heavy Military Laser", "LASER", "ft II", 100, 20000))
        weapons.add(5, Module("Light Gun", "GUN", "mk I", 50, 2500))
        weapons.add(6, Module("Medium Gun", "GUN", "mk II", 80, 5000))
        weapons.add(7, Module("Heavy Gun", "GUN", "mk III", 120, 7500))
        weapons.add(8, Module("Military Gun", "GUN", "ft I", 160, 10000))
        weapons.add(9, Module("Heavy Military Gun", "GUN", "ft II", 200, 12000))
        return weapons
    }

    fun getCurrentShipInfo(position: Int): User? {

        when (position) {
            0 -> {
                fighter.cargoCapacity = 10
                fighter.fuel = 20
                fighter.jump = 10
                fighter.hp = 50
                fighter.shield = 100
                fighter.scanner_capacity = 15
                fighter.ship = "Fighter"
                fighter.shipPrice = 0
                fighter.shipClass = "Warrior"
                fighter.weaponSlots = 3
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
                trader.ship = "Trader"
                trader.shipClass = "Nightfall"
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
                rs.ship = "Research Spaceship"
                rs.shipClass = "Interstellar"
                return rs
            }
        }
        return null
    }

    fun getCurrentShipInfo(shipName: String): User {

        when (shipName) {
            "Fighter" -> {
                fighter.hp = 50
                fighter.shield = 100
                fighter.cargoCapacity = 10
                fighter.jump = 10
                fighter.fuel = 20
                fighter.scanner_capacity = 15
                fighter.weaponSlots = 3
                fighter.shipPrice = 0
                fighter.ship = "Fighter"
                fighter.shipClass = "Warrior"
                return fighter
            }
            "Trader" -> {
                trader.hp = 100
                trader.shield = 50
                trader.cargoCapacity = 150
                trader.jump = 25
                trader.fuel = 50
                trader.scanner_capacity = 30
                trader.weaponSlots = 1
                trader.shipPrice = 50000
                trader.ship = "Trader"
                trader.shipClass = "Nightfall"
                return trader
            }
            "Research Spaceship" -> {
                rs.hp = 150
                rs.shield = 50
                rs.cargoCapacity = 75
                rs.jump = 75
                rs.fuel = 150
                rs.scanner_capacity = 100
                rs.weaponSlots = 2
                rs.shipPrice = 100000
                rs.ship = "Research Spaceship"
                rs.shipClass = "Interstellar"
                return rs
            }
        }
        return fighter
    }

    fun getShipFuelInfo(ship: String): Long {
        when (ship) {
            "Fighter" -> return 20L
            "Trader" -> return 50L
            "Research Spaceship" -> return 150L
        }
        return 0L
    }

    fun getPlanetPricesByType(planetType: String): Long {
        when (planetType) {
            "agrocultural" -> {
            }
            "water" -> {
            }
        }
        return 0L
    }

    fun getFilteredInstalledWeaponList(weapon: List<Weapon>): List<Weapon> {
        return weapon.filter { it.isWeaponInstalled == true }
    }
}
