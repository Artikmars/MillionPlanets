package com.artamonov.millionplanets.utils;

import com.artamonov.millionplanets.model.Module;
import com.artamonov.millionplanets.model.User;

public class Utils {
    private static User fighter = new User();
    private static User trader = new User();
    private static User rs = new User();

    public static Module getCurrentModuleInfo(int position) {

        switch (position) {
            case 0:
                return new Module("Light Laser", "mk I", 25, 25, 0);
            case 1:
                return new Module("Medium Laser", "mk II", 20, 20, 5000);
            case 2:
                return new Module("Heavy Laser", "mk III", 30, 30, 10000);
        }
        return null;
    }

    public static User getCurrentShipInfo(int position) {

        switch (position) {
            case 0:
                fighter.setHp(50);
                fighter.setShield(100);
                fighter.setCargo(10);
                fighter.setJump(10);
                fighter.setFuel(20);
                fighter.setScanner_capacity(15);
                fighter.setWeaponSlots(3);
                fighter.setShipPrice(0);
                fighter.setShip("Fighter");
                fighter.setShipClass("Warrior");
                return fighter;
            case 1:
                trader.setHp(100);
                trader.setShield(50);
                trader.setCargo(150);
                trader.setJump(25);
                trader.setFuel(50);
                trader.setScanner_capacity(30);
                trader.setWeaponSlots(1);
                trader.setShipPrice(50000);
                trader.setShip("Trader");
                trader.setShipClass("Nightfall");
                return trader;
            case 2:
                rs.setHp(150);
                rs.setShield(50);
                rs.setCargo(75);
                rs.setJump(75);
                rs.setFuel(150);
                rs.setScanner_capacity(100);
                rs.setWeaponSlots(2);
                rs.setShipPrice(100000);
                rs.setShip("Research Spaceship");
                rs.setShipClass("Interstellar");
                return rs;
        }
        return null;
    }

    public static int getShipFuelInfo(String ship) {
        switch (ship) {
            case "Fighter":
                return 20;
            case "Trader":
                return 50;
            case "Research Spaceship":
                return 150;
        }
        return 0;
    }

    public static int getWeaponIdByName(String name) {
        if (name == null) {
            return -1;
        }
        switch (name) {
            case "Light Laser":
                return 0;
            case "Medium Laser":
                return 1;
            case "Heavy Laser":
                return 2;
        }
        return -1;
    }
}
