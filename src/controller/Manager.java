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
import model.Rank;
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
  
 /**
  * Get rankings
  * @return List Rank
  * @throws SQLException 
  */
  public List<Rank> getRankings() throws SQLException{
      List<Rank> rankings;
      marvelDAO.connect();
      rankings = marvelDAO.getRankings();
      marvelDAO.disconnect();
      return rankings;
  }
  
  /**
   * Delete a user
   * @param user User
   * @param password String
   * @throws SQLException
   * @throws MarvelException 
   */
  public void deleteUser(User user, String password) throws SQLException, MarvelException{
      marvelDAO.connect();
      marvelDAO.deleteUser(user, password);
      marvelDAO.disconnect();
      
  }
  
  /**
   * Persistence after battle finished
   * @param uL User
   * @param e Enemy
   * @param g Gem
   * @throws SQLException
   * @throws MarvelException 
   */
  public void saveDataAfterBattle(User uL, Enemy e, List<Gem> g) throws SQLException, MarvelException{
         
      marvelDAO.connect();
      marvelDAO.updateBattle(uL, e, g);
      marvelDAO.disconnect();
  }
  
  /**
   * Generates a random new place for Enemy
   * @param enemy Enemy
   * @return Place
   * @throws SQLException
   * @throws MarvelException 
   */
  public Place newPlaceForEnemy(Enemy enemy) throws SQLException, MarvelException{
      marvelDAO.connect();
      List<String> placesName = marvelDAO.allPlacesNamesNoEnemy(enemy);
      String s = placesName.get(randomGen(0, placesName.size()));
      Place p = marvelDAO.getPlaceByName(s);
      marvelDAO.disconnect();
      
      return p;
  }
  
  /**
   * Random generator
   * @param min int
   * @param max int
   * @return int
   */
   public static int randomGen(int min, int max){
        Random rand = new Random();
        int value = rand.nextInt((max - min) + 1) + min;
        return value;
    }
  
  /**
     * Receives two spock/scissor/paper/rock/lizard options, returns 1 if wins
     * option1, 2 in the other case, or 0 if nobody wins
     * @param opt1 String
     * @param opt2 String
     * @return int
     */
    public int pppGame(String opt1, String opt2){
        switch(opt1){
            case "spock":
                if(opt2.equals("scissor") || opt2.equals("lizard")){return 1; }
                if(opt2.equals("paper") || opt2.equals("rock")){ return 2;}
                break;
            case "scissor":
                if(opt2.equals("lizard") || opt2.equals("paper")){return 1; }
                if(opt2.equals("spock") || opt2.equals("rock")){ return 2;}
                break;
            case "paper":
                if(opt2.equals("rock") || opt2.equals("spock")){return 1; }
                if(opt2.equals("paper") || opt2.equals("scissor")){ return 2;}
                break;
            case "rock":
                if(opt2.equals("scissor") || opt2.equals("lizard")){return 1; }
                if(opt2.equals("paper") || opt2.equals("spock")){ return 2;}
                break;
            case "lizard":
                if(opt2.equals("paper") || opt2.equals("spock")){return 1; }
                if(opt2.equals("scissor") || opt2.equals("rock")){ return 2;}
        }
        return 0;
    }
  
  /**
   * Gets enemy by name present in Player space 
   * @param userLogged User
   * @param enemyName String
   * @return Enemy
   * @throws SQLException
   * @throws MarvelException 
   */
  public Enemy getEnemyHere(User userLogged, String enemyName) throws SQLException, MarvelException{
      marvelDAO.connect();
      Enemy e = marvelDAO.enemyByName(userLogged, enemyName);
      marvelDAO.disconnect();
      if(e.getPlace() == null){
          throw new MarvelException(MarvelException.ENEMY_NO_EXISTS_HERE);
      }
      else if(!e.getPlace().getName().equals(userLogged.getPlace().getName())){
          throw new MarvelException(MarvelException.ENEMY_NO_EXISTS_HERE);
      }
      return e;
  }
  
  /**
   * Get free Gem
   * @param player User
   * @param gem String
   * @throws SQLException
   * @throws MarvelException 
   */
  public void getFreeGem(User player, String gem) throws SQLException, MarvelException{
      marvelDAO.connect();
      //first we get gems in player actual place (all of them)
      List<Gem> gemsInPlace = marvelDAO.getGemsByPlace(player);
      //1) we check the name of the gem and check if it is free
      boolean existsGem = false;
      boolean gemIsFree = false;
      for(Gem g: gemsInPlace){
          if(g.getName().equals(gem)){
              existsGem = true;
              if(g.getOponent()== null) {
                  gemIsFree = true;
                  player.getGemsOwned().add(g);
              }
          }  
      }
      //2) we check if some error must be throwed
      if(!existsGem || !gemIsFree){
          throw new MarvelException(MarvelException.NO_GEM_NAME);
      } 
      else{
          //3. Update database gem table
          marvelDAO.updateGems(player, gem);
      }
       marvelDAO.disconnect();
  }
  
  
  /**
   * Persistence to new User place
   * @param user User
   * @throws SQLException 
   */
  public void updateUserPlace(User user) throws SQLException{
      marvelDAO.connect();
      marvelDAO.updateUserPlace(user);
      marvelDAO.disconnect();
      
  }
  
  /**
   * Moves user
   * @param direction String
   * @param user User
   * @return User
   * @throws MarvelException
   * @throws SQLException 
   */
  public User moveUser(String direction, User user) throws MarvelException, SQLException {
      marvelDAO.connect();
      switch(direction.toLowerCase()){
            case "n":{
                Place newPlace = marvelDAO.getPlaceByName(user.getPlace().getNorth().getName());
                user.setPlace(newPlace);
                break;
            }
            case "s":{
                Place newPlace = marvelDAO.getPlaceByName(user.getPlace().getSouth().getName());
                user.setPlace(newPlace);
                break;
            }
            case "e":{
                Place newPlace = marvelDAO.getPlaceByName(user.getPlace().getEast().getName());
                user.setPlace(newPlace);
                break;
            }
            case "w":{
                Place newPlace = marvelDAO.getPlaceByName(user.getPlace().getWest().getName());
                user.setPlace(newPlace);
                break;
            }
        }
       marvelDAO.disconnect();
      return user;
  }
  
  /**
   * Get possible places to move
   * @param place List
   * @return 
   */
  public List<String> getPlacesToGo(Place place){
      List<String> directions = new ArrayList<>();
      if(place.getNorth() != null){
          directions.add("N");
          //directions.add(place.getNorth().getName());
      }
      if(place.getSouth() != null){
          directions.add("S");
          //directions.add(place.getSouth().getName());
      }
      if(place.getEast() != null){
          directions.add("E");
          //directions.add(place.getEast().getName());
      }
      if(place.getWest() != null){
          directions.add("W");
          //directions.add(place.getWest().getName());
      }
      return directions;
  }
    
  /**
   * Retuns all free gems in a given Place
   * @param player User
   * @return List String
   * @throws SQLException
   * @throws MarvelException 
   */
  public List<String> getGemsByPlace(User player)  throws SQLException, MarvelException {
       marvelDAO.connect();
       List<Gem> gems = marvelDAO.getGemsByPlace(player);
       marvelDAO.disconnect();
       List<String> gemsHere = new ArrayList<>();
       //we only want gems not owned by anyone
       for(Gem g: gems){
           if(g.getOponent() == null){
               gemsHere.add(g.getName());
           }  
       }
       return gemsHere;
  }
  
  /**
   * Gets all enemies present in a given place
   * @param place String
   * @return List String
   * @throws SQLException
   * @throws MarvelException 
   */
  public List<String> getEnemies(String place) throws SQLException, MarvelException {
      marvelDAO.connect();
      List<Enemy> allEnemies = marvelDAO.getEnemies();
      marvelDAO.disconnect();
      List<String> enemies = new ArrayList<String>();
      for(Enemy e: allEnemies){
          if(e.getPlace().getName().equals(place)){
              enemies.add(e.toString());
          }
      }
      return enemies;
  }
  
  /**
   * Selects all gems
   * @param u User
   * @return List Gem
   * @throws SQLException
   * @throws MarvelException 
   */
  public List<Gem> selectAllGems(User u) throws SQLException, MarvelException{
      marvelDAO.connect();
      List<Gem> gems = marvelDAO.selectAllGems(u);
      marvelDAO.disconnect();
      return gems;
  }
  
  /**
   * Login user 
   * @param username String
   * @param password String
   * @return User
   * @throws SQLException
   * @throws MarvelException 
   */
  public User userLogin(String username, String password) throws SQLException, MarvelException{
      //check if username and password is correct
      marvelDAO.connect();
      User u = marvelDAO.loginCheck(username, password);
      if(u.getGemsOwned().size() == 6){
          u.setGameFinished(true);
      }
      marvelDAO.disconnect();
      return u;
  }
  
  /**
   * Gets all Superhero
   * @return List Superhero
   * @throws SQLException 
   */
  public List<Superhero> getSuperheroes() throws SQLException {
      marvelDAO.connect();
      List<Superhero> allHeroes = marvelDAO.selectAllHeroes();
      marvelDAO.disconnect();
      return allHeroes; 
  }
  
  /**
   * Registration of a new User
   * @param username String
   * @param password String
   * @param nameHeroe String
   * @throws SQLException
   * @throws MarvelException 
   */
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
  
  /**
   * Creation of Gems
   * @param allEnemies List Enemy
   * @return List Gem
   * @throws MarvelException
   * @throws SQLException 
   */
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
