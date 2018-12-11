package com.artamonov.millionplanets.model;

public class User {
    private String position;
    private Integer x;
    private Integer y;

    private Integer sumXY;
    private String ship;
    private String hull;
    private String shield;
    private String cargo;
    private Integer scanner_capacity;

    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String fuel;

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public Integer getSumXY() {
        return sumXY;
    }

    public void setSumXY(Integer sumXY) {
        this.sumXY = sumXY;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getShip() {
        return ship;
    }

    public void setShip(String ship) {
        this.ship = ship;
    }

    public String getHull() {
        return hull;
    }

    public void setHull(String hull) {
        this.hull = hull;
    }

    public String getShield() {
        return shield;
    }

    public void setShield(String shield) {
        this.shield = shield;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Integer getScanner_capacity() {
        return scanner_capacity;
    }

    public void setScanner_capacity(Integer scanner_capacity) {
        this.scanner_capacity = scanner_capacity;
    }

}
