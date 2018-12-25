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
public class Superhero {
    
    private String name;
    private String superpower;

    public Superhero() {}
    public Superhero(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuperpower() {
        return superpower;
    }

    public void setSuperpower(String superpower) {
        this.superpower = superpower;
    }
    
    

    @Override
    public String toString() {
        return  name + " " + superpower;
    }
    
    
}
