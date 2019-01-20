/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;
import model.gems.Gem;

/**
 *
 * @author alu2017454
 */
public class Enemy extends Oponent {
    
    private String debility;
    
    private List<Gem> gemsOwned;
    
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

    public List<Gem> getGemsOwned() {
        return gemsOwned;
    }

    public void setGemsOwned(List<Gem> gemsOwned) {
        this.gemsOwned = gemsOwned;
    }
    
    public void addGem(Gem gem){
        this.gemsOwned.add(gem);
    }
    
    

    @Override
    public String toString() {
        return "Name: "+ super.getName() + "- Debility:" + debility + " - Level: " + super.getLevel();
    }
    
    
    
    
    
    

  
    
}
