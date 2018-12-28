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
                        move();
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
     * Case L: login
     */
    public static void login() throws SQLException, MarvelException {
        String username = input[1];
        String password = input[2];
        //check username and password for login
        userLogged = manager.userLogin(username, password);
        System.out.println("Welcome, "+ userLogged.getName());
        System.out.println("Place: "+ userLogged.getPlace().getName());
        System.out.println("Place: "+ userLogged.getPlace().getDescription());
        System.out.println("---");
        
        //obtain enemies in user place
        List<String> enemiesHere = manager.getEnemies(userLogged.getPlace().getName());
        if(enemiesHere.isEmpty()){
            System.out.println("There is nobody here");
        }
        else{
            for(String s: enemiesHere){
                System.out.print(s + ", ");
            }
        }
        System.out.println("\n---");
        
        //obtain gems in user place
        currentFreeGems = manager.getGemsByPlace(userLogged.getName(), userLogged.getPlace().getName());
        if(currentFreeGems.isEmpty()){
            System.out.println("There are no gems here");
        }
        else{
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
    }
    
    /**
     * 
     */
    public static void getGem() {
        
    }
    
    /**
     * 
     */
    public static void battle() {
        
    }
    
    /**
     * 
     */
    public static void move() {
        
    }
    
    /**
     * 
     */
    public static void delete() {
        
    }
    
    /**
     * 
     */
    public static void ranking() {
        
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
}
