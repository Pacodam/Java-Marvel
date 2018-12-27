/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author MSI
 */
public abstract class Oponent {
    
    private String name;
    private int level;
    private Place place;

    public Oponent() {}
    public Oponent(String name, int level, Place place) {
        this.name = name;
        this.level = level;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "Oponent{" + "name=" + name + ", level=" + level + ", place=" + place + '}';
    }
    
    
    
    
}
