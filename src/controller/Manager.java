/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import exceptions.MarvelException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.Enemy;
import model.Oponent;
import model.Place;
import model.Superhero;
import model.User;
import model.gems.Gem;
import persistence.MarvelDAO;

/**
 *
 * @author alu2017454
 */
public class Manager {
    
    private static MarvelDAO marvelDAO;
    
    private static Oponent newUser;
    
    public Manager() {
        marvelDAO = new MarvelDAO();  
    }
    
    
  public List<String> getGemsByPlace(String place)  throws SQLException, MarvelException {
       marvelDAO.connect();
       List<Gem> gemsHere = marvelDAO.getGemsByPlace();
       marvelDAO.disconnect();
       
      
  }
  public List<String> getEnemies(String place) throws SQLException, MarvelException {
      marvelDAO.connect();
      List<Enemy> allEnemies = marvelDAO.getEnemies();
      marvelDAO.disconnect();
      List<String> enemies = new ArrayList<String>();
      for(Enemy e: allEnemies){
          if(e.getPlace().getName().equals(place)){
              enemies.add(e.getName());
          }
      }
      return enemies;
  }
  
  public User userLogin(String username, String password) throws SQLException, MarvelException{
      //check if username and password is correct
      marvelDAO.connect();
      User u = marvelDAO.loginCheck(username, password);
      marvelDAO.disconnect();
      return u;
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
      //get Superhero, if name passed exists
      Superhero s = marvelDAO.getSuperHeroByName(nameHeroe);
      //we get the Place named "New York"
      Place newYork = marvelDAO.getPlaceByName("New York");
      //a new player is created
      if(newYork != null){
        newUser = new User(username, password, 1,  s, newYork);
      }
      //we get the enemies
      List<Enemy> allEnemies = marvelDAO.getEnemies();
      
      //we create the new gem pack for the player (using a separated method)
      List<Gem> gemsPack = createGems(allEnemies);
   
      //finally, we can save into the bbdd 1) the new player, 2)the new gems pack
      marvelDAO.insertNewUser((User) newUser);
      marvelDAO.insertNewGems(gemsPack);
      
      marvelDAO.disconnect();
  }
  
  public static List<Gem> createGems(List<Enemy> allEnemies) throws MarvelException, SQLException {
      List<Gem> gemsPack = new ArrayList<>();
      String[] gems = {"Mind Gem", "Power Gem", "Reality Gem", "Soul Gem", "Space Gem", "Time Gem"};
      //why not do directly with Places?
      String[] places = marvelDAO.getNameOfPlaces();
      List<String> placesFilled = new ArrayList<>();
      placesFilled.add(newUser.getPlace().getName());
      for(int i = 0; i < gems.length; i++){
          gemsPack.add(new Gem(gems[i]));
      }
      List<Enemy> enemiesForRand = new ArrayList<>();
      for(Gem g: gemsPack){
          String newPlace = null;
          do{
              newPlace = places[randGen(0, places.length-1)];
          }while(placesFilled.contains(newPlace));
          placesFilled.add(newPlace);
          g.setPlace(marvelDAO.getPlaceByName(newPlace));
          g.setUser((User) newUser);
          //If there is more than 1 villain, we use random
          for(Enemy e: allEnemies){
              if(e.getPlace().getName().equals(newPlace)){
                  enemiesForRand.add(e);
              }
           }
           if(!enemiesForRand.isEmpty()){
             int rand = randGen(0, enemiesForRand.size()-1);
             g.setOponent(enemiesForRand.get(rand)); 
             enemiesForRand.clear();
           }
      }
      return gemsPack;
  }
  
   // String grupo = grupos[aleatorio(0,grupos.length-1)];
    public static int randGen(int min, int max){
        Random rand = new Random();
        int v = rand.nextInt((max - min) + 1) + min;
        return v;
    }
          
}
