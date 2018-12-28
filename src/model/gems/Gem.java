/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.gems;

import model.Oponent;
import model.Place;
import model.User;

/**
 *
 * @author alu2017454
 */
public class Gem {
    private String name;
    private User user;
    private Oponent oponent; //the owner can be a User or an Enemy (both subclasses of Oponent)
    private Place place;

    public Gem() {}
    
    public Gem(String name){
        this.name = name;
    }
    public Gem(String name, User user, Oponent oponent, Place place) {
        this.name = name; 
        this.user = user; 
        this.oponent = oponent; 
        this.place = place; 
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user; 
    }

    public Oponent getOponent() {
        return oponent;
    }

    public void setOponent(Oponent oponent) {
        this.oponent = oponent; 
    }
    
    public String getOponentName(){
        if(oponent != null){
            return oponent.getName();
        }
        return " ";
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place; 
    }

    @Override
    public String toString() {
        return "Gem{" + "name=" + name + ", user=" + user.getName() + ", oponent=" + getOponentName() + ", place=" + place.getName() + '}';
   
    }
    
    
    
    
}
