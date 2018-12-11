package com.artamonov.millionplanets.model;

public class ObjectModel {
    Integer sumXY;
    private String type;
    private String name;
    private Integer distance;
    private Integer x;
    private Integer y;

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
