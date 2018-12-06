package com.artamonov.millionplanets.model;

public class User {
    private String position;
    private String ship;
    private String hull;
    private String shield;
    private String cargo;
    private String scanner_capacity;

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

    public String getScanner_capacity() {
        return scanner_capacity;
    }

    public void setScanner_capacity(String scanner_capacity) {
        this.scanner_capacity = scanner_capacity;
    }
}
