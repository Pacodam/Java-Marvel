/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import exceptions.MarvelException;
import java.sql.SQLException;
import java.util.List;
import model.Place;
import model.Superhero;
import persistence.MarvelDAO;

/**
 *
 * @author alu2017454
 */
public class Manager {
    
    private static MarvelDAO marvelDAO;
    
    public Manager() {
        marvelDAO = new MarvelDAO();  
    }
    
    
  public List<Superhero> getSuperheroes() throws SQLException {
      marvelDAO.connect();
      List<Superhero> allHeroes = marvelDAO.selectAllHeroes();
      marvelDAO.disconnect();
      return allHeroes; 
  }
  
  public void registryUser(String username, String password, String nameHeroe) throws SQLException, MarvelException {
      marvelDAO.connect();
      //check if user is already registered
      marvelDAO.checkUsername(username);
      Superhero s = marvelDAO.getSuperHeroByName(nameHeroe);
      Place p = marvelDAO.getPlaceByName("New York");
      
      marvelDAO.disconnect();
  }
          
}
