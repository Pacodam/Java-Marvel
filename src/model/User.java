/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import model.gems.Gem;



/**
 *
 * @author alu2017454
 */
public class User extends Oponent {
    
    private String pass;
    private Superhero superhero;
    private int points;
    private List<Gem> gemsOwned;
    private boolean gameFinished;
    
    
    public User() {}
    
    public User(String name, String pass, int level, Superhero superhero, Place place){
        super(name, level, place);
        this.pass = pass;
        this.superhero = superhero;
        this.points = 0;
    }

    public List<Gem> getGemsOwned() {
        return gemsOwned;
    }

    public void setGemsOwned(List<Gem> gemsOwned) {
        this.gemsOwned = gemsOwned;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }
    
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Superhero getSuperhero() {
        return superhero;
    }

    public void setSuperhero(Superhero superhero) {
        this.superhero = superhero;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    
    
    
}
