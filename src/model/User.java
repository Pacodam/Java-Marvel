/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import persistence.MarvelDAO;

/**
 *
 * @author alu2017454
 */
public class User {
    private String username;
    private String pass;
    private int level;
    private Superhero superhero;
    private Place place;
    private int points;
    
    
    public User(String username, String pass, Superhero superhero, Place place){
        this.username = username;
        this.pass = pass;
        this.level = 1;
        this.superhero = superhero;
        this.place = place;
        this.points = 0;    
    }
    
}
