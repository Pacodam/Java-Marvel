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
public class User extends Oponent {
    
    private String pass;
    private Superhero superhero;
    private int points;
    
    
    public User(String name, String pass, Superhero superhero, Place place){
        super(name, 1, place);
        this.pass = pass;
        this.superhero = superhero;
        this.points = 0;    
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
