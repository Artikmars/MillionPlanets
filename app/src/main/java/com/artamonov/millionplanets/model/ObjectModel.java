package com.artamonov.millionplanets.model;

public class ObjectModel {
    Integer sumXY;
    private String type;
    private String name;
    private Integer distance;
    private Integer x;
    private Integer y;
    private String planetClass;
    private String planetSectors;
    private String planetSize;
    private String resourceName;
    private int price_buy_iron;
    private int price_sell_iron;

    private int ironAmount;

    public int getIronAmount() {
        return ironAmount;
    }

    public void setIronAmount(int ironAmount) {
        this.ironAmount = ironAmount;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public int getPrice_buy_iron() {
        return price_buy_iron;
    }

    public void setPrice_buy_iron(int price_buy_iron) {
        this.price_buy_iron = price_buy_iron;
    }

    public int getPrice_sell_iron() {
        return price_sell_iron;
    }

    public void setPrice_sell_iron(int price_sell_iron) {
        this.price_sell_iron = price_sell_iron;
    }

    public String getPlanetClass() {
        return planetClass;
    }

    public void setPlanetClass(String planetClass) {
        this.planetClass = planetClass;
    }

    public String getPlanetSectors() {
        return planetSectors;
    }

    public void setPlanetSectors(String planetSectors) {
        this.planetSectors = planetSectors;
    }

    public String getPlanetSize() {
        return planetSize;
    }

    public void setPlanetSize(String planetSize) {
        this.planetSize = planetSize;
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

    public Integer getSumXY() {
        return sumXY;
    }

    public void setSumXY(Integer sumXY) {
        this.sumXY = sumXY;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
