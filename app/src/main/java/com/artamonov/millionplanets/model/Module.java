package com.artamonov.millionplanets.model;

public class Module {
    private String name;
    private int price;
    private boolean isBought;
    private String moduleClass;
    private int damageHP;
    private int slots;
    private String type;

    public Module(String name, String type, String moduleClass, int damageHP, int cost) {
        this.moduleClass = moduleClass;
        this.type = type;
        this.damageHP = damageHP;
        price = cost;
        this.name = name;
    }

    public Module(String name, int slots, int price) {
        this.name = name;
        this.slots = slots;
        this.price = price;
    }

    public String getType() {
        return type;
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
