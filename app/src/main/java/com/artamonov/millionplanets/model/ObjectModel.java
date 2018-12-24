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
