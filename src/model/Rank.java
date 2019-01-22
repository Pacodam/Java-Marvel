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
public class Rank {
    
    private String user;
    private String superhero;
    private int numGems;
    private int level;
    private int points;
    
    public Rank() {}

    public void setUser(String user) {
        this.user = user;
    }

    public void setSuperhero(String superhero) {
        this.superhero = superhero;
    }

    public void setNumGems(int numGems) {
        this.numGems = numGems;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return  user + "    " + superhero + "   " + numGems + "    " + level + "    " + points;
    }
    
    
}
