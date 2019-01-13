/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marvel;

import controller.Manager;
import exceptions.MarvelException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import model.Place;
import model.Superhero;
import model.User;
import model.gems.Gem;

/**
 *
 * @author alu2017454
 */
public class Marvel {
        
    private static Manager manager;
    private static String[] input; //the line currently readed
    
    private static User userLogged;
    private static List<String> currentDirections;
    private static List<String> currentFreeGems;
    
    public static void main(String[] args) {
        
        manager = new Manager();
        //here starts the input data from standard input
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        System.out.println(       "***MARVEL STUCOM JAVA***");
        System.out.println("Please, paste data and press return:");
        do{
           try{
             line = br.readLine();
             input = line.split(" ");
             if(input.length == 0){
                 System.out.println("You entered no data");
             }
             else{
                switch(input[0].toLowerCase()){
                    case "r":   //registro. length 4, no login
                        testLength(4);
                        registry();
                        break;
                    case "v":   //ver superheroes, length 1 , no login
                        testLength(1);
                        viewHeroes();
                        break;
                    case "l":   //login, length 3, no login
                        testLength(3);
                        login();
                        break;
                    case "g":   //obtener gema, length 3 (gema con espacio), login
                        testLength(3);
                        testLogin();
                        getGem();
                        break;
                    case "b":   //lucha contra villano, length 2, login
                        testLength(2);
                        testLogin();
                        battle();
                        break;
                    case "n":
                    case "s":
                    case "e":
                    case "w":   //desplazamiento, length 1, login
                        testLength(1);
                        testLogin();
                        if(!testGameFinished()){
                           move();
                        }
                        break;
                    case "d": //borrar usuario, length 2, login
                        testLength(2);
                        testLogin();
                        delete();
                        break;
                    case "k": //ranking, length 1, no login
                        testLength(1);
                        ranking();
                        break;
                    case "x":   //Exits , length 1, no login
                        testLength(1);
                        //exit();
                        break;
                    default:
                        throw new MarvelException(MarvelException.WRONG_COMMAND);
                }
             }
            }catch (IOException | MarvelException | SQLException  ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }  
        }while(!input[0].equalsIgnoreCase("x"));   
    }

    /**
     * Registration of new player
     * @throws exceptions.MarvelException
     * @throws java.sql.SQLException
     */
    public static void registry() throws MarvelException, SQLException {
        String username = input[1];
        String password = input[2];
        String nameSuperHeroe = input[3];
        manager.registryUser(username, password, nameSuperHeroe);
        System.out.println("User registered");
        
    }
    
    /**
     * Case V: view superheroes
     */
    public static void viewHeroes() throws SQLException {
        List<Superhero> allHeroes = manager.getSuperheroes();
        if(allHeroes.isEmpty()){
            System.out.println("There is no superheroes on the bbdd");
        }
        else{
            System.out.println("- SuperHeroes -");
            for(Superhero s: allHeroes){
                System.out.println(s.toString());
            }
        }
    }
   
    
    /**
     * Case L: login. 
     */
    public static void login() throws SQLException, MarvelException {
        String username = input[1];
        String password = input[2];
        //check username and password for login, we receive a Player object
        //when user logs on the game, his gems owned are included on the User created
        userLogged = manager.userLogin(username, password);
        System.out.println("Welcome, "+ userLogged.getName());
        showPlaceInfo();
    }
    
    /**
     * 
     */
    public static void getGem() throws SQLException, MarvelException {
       
        String gem = input[1] + " " + input[2];
        //the manager will add the new gem to the user in the database, or return an exception if any problem is found
        manager.getFreeGem(userLogged, gem);
        System.out.println("You have got the gem");
        //time to check if the player owns the 6 gems already
        if(userLogged.getGemsOwned().size() < 6){
            System.out.println("YOU WINN!! YOU HAVE ALL GEMS!!");
            userLogged.setGameFinished(true);
        }
        
        
    }
    
    /**
     * 
     */
    public static void battle() {
        
    }
    
    /**
     * 
     */
    public static void move() throws MarvelException, SQLException {
        String direction = input[0];
        if(!currentDirections.contains(direction.toUpperCase())){
            throw new MarvelException(MarvelException.MOVE_UNALLOWED);
        }
        System.out.println("Moving to " + direction + "...");
        userLogged = manager.moveUser(direction, userLogged);
        userLogged.getPlace().getName();
        showPlaceInfo();
        manager.updateUserPlace(userLogged);
    }
    
    public static void delete() {
        
    }
    
    /**
     * 
     */
    public static void ranking() {
        
    }
    
    public static void showPlaceInfo() throws SQLException, MarvelException{
        System.out.println("Place: "+ userLogged.getPlace().getName());
        System.out.println("Place: "+ userLogged.getPlace().getDescription());
        System.out.println("---");
        //obtain enemies in user place
        List<String> enemiesHere = manager.getEnemies(userLogged.getPlace().getName());
        if(enemiesHere.isEmpty()){
            System.out.println("There is nobody here");
        }
        else{
            System.out.println("- Enemies -");
            for(String s: enemiesHere){
                System.out.println(s);
            }
        }
        System.out.println("\n---");
        //obtain gems in user place
        currentFreeGems = manager.getGemsByPlace(userLogged);
        if(currentFreeGems.isEmpty()){
            System.out.println("There are no gems here");
        }
        else{
            System.out.println("- Free gems -");
            for(String s: currentFreeGems){
                System.out.print(s + ", ");
            }
        }
        System.out.println("\n---");
        //obtain possible moving directions
        System.out.println("You can go:");
        currentDirections = manager.getPlacesToGo(userLogged.getPlace());
        for(String p: currentDirections){
            System.out.print(p + ", ");
        }
         System.out.println("\n---");
    }
    
    /**
     * Test the required length for the input order.
     * @param length
     * @throws MarvelException - Incorrect number of arguments.
     */
    public static void testLength(int length) throws MarvelException{
        if(input.length != length){
            throw new MarvelException(MarvelException.INCORRECT_NUM_ARGS);
        }
    }
    
    /**
     * Test if user is already logged.
     * @throws MarvelException - User not logged
     */
    public static void testLogin() throws MarvelException {
        if(userLogged == null){
            throw new MarvelException(MarvelException.NOT_LOGGED);
        }
    }
    
    /**
     * Test if logged user has finished his game or not
     * @return boolean
     */
    public static boolean testGameFinished(){
        if(userLogged != null && userLogged.isGameFinished()){
            System.out.println("You already finished your game");
            return true;
        }
        return false;
    }
}
