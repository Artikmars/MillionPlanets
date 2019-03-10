package com.artamonov.millionplanets.model;

public class Module {
    private String name;
    private int price;
    private boolean isBought;
    private String moduleClass;
    private int damageHP;
    private int damageShield;
    private int slots;

    public Module(String name, String moduleClass, int damageHP, int damageShield, int cost) {
        this.moduleClass = moduleClass;
        this.damageHP = damageHP;
        this.damageShield = damageShield;
        price = cost;
        this.name = name;
    }

    public Module(String name, int slots, int price) {
        this.name = name;
        this.slots = slots;
        this.price = price;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public String getModuleClass() {
        return moduleClass;
    }

    public void setModuleClass(String moduleClass) {
        this.moduleClass = moduleClass;
    }

    public int getDamageHP() {
        return damageHP;
    }

    public void setDamageHP(int damageHP) {
        this.damageHP = damageHP;
    }

    public int getDamageShield() {
        return damageShield;
    }

    public void setDamageShield(int damageShield) {
        this.damageShield = damageShield;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }
}
