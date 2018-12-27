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
public class Enemy extends Oponent {
    
    private String debility;
    private Place place;
    
    public Enemy() {}
    
    public Enemy(String name, String debility, int level, Place place){
        super(name, level, place);
        this.debility = debility;
    }

    public String getDebility() {
        return debility;
    }

    public void setDebility(String debility) {
        this.debility = debility;
    }
    
    
    
    

  
    
}
