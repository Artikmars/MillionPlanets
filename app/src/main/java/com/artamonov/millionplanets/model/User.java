package com.artamonov.millionplanets.model;

public class User {
    private int x;
    private int y;

    private int sumXY;
    private String ship;
    private int hp;
    private int shield;
    private int cargo;
    private int scanner_capacity;
    private int money;
    private String email;
    private String nickname;
    private int fuel;
    private String type;
    private String moveToObjectName;
    private int moveToObjectDistance;
    private String moveToObjectType;

    public User(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public User() {

    }

    public String getMoveToObjectName() {
        return moveToObjectName;
    }

    public void setMoveToObjectName(String moveToObjectName) {
        this.moveToObjectName = moveToObjectName;
    }

    public int getMoveToObjectDistance() {
        return moveToObjectDistance;
    }

    public void setMoveToObjectDistance(int moveToObjectDistance) {
        this.moveToObjectDistance = moveToObjectDistance;
    }

    public String getMoveToObjectType() {
        return moveToObjectType;
    }

    public void setMoveToObjectType(String moveToObjectType) {
        this.moveToObjectType = moveToObjectType;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public int getSumXY() {
        return sumXY;
    }

    public void setSumXY(int sumXY) {
        this.sumXY = sumXY;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getShip() {
        return ship;
    }

    public void setShip(String ship) {
        this.ship = ship;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public int getCargo() {
        return cargo;
    }

    public void setCargo(int cargo) {
        this.cargo = cargo;
    }

    public int getScanner_capacity() {
        return scanner_capacity;
    }

    public void setScanner_capacity(int scanner_capacity) {
        this.scanner_capacity = scanner_capacity;
    }

}
