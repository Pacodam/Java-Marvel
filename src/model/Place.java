/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author alu2017454
 */
public class Place {
    private String name;
    private String description;
    private Place north;
    private Place south;
    private Place east;
    private Place west;

    
    public Place () {}
    
    public Place(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Place getNorth() {
        return north;
    }

    public void setNorth(Place north) {
        this.north = north;
    }

    public Place getSouth() {
        return south;
    }

    public void setSouth(Place south) {
        this.south = south;
    }

    public Place getEast() {
        return east;
    }

    public void setEast(Place east) {
        this.east = east;
    }

    public Place getWest() {
        return west;
    }

    public void setWest(Place west) {
        this.west = west;
    }
    
    
}
