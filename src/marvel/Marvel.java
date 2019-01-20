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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.Enemy;
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
    private static Enemy enemy;  //for battles
    private static List<Gem> allGems; 
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
        allGems = manager.selectAllGems(userLogged);
        System.out.println("Welcome, "+ userLogged.getName());
        showPlaceInfo();
        
        for(Gem g: allGems){
            System.out.println(" proof " + g.toString());
        }
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
    public static void battle() throws SQLException, MarvelException {
        enemy = manager.getEnemyHere(userLogged, input[1]);
        System.out.println("- Fight begins -");
        //the number of attacks of each opponent
        int attkUser = userLogged.getLevel();
        //if user superpower equals enemy debility, user increases 1 attack
        if(userLogged.getSuperhero().getSuperpower().equals(enemy.getDebility())){
            attkUser++;
        }
        int attkEnem = enemy.getLevel();
        //number of attk winned by each opponent
        int userWin = 0;
        int enemyWin = 0;
        while(attkUser > 0  || attkEnem > 0){
            
            //result gets 1 if wins player, 2 if wins enemy, 0 if nobody wins
            //we used attack method for this
            
            int result = attack(attkUser, attkEnem);
            
            //the attacks are minored by one, only if attack it's > 0
            if(attkUser > 0){ attkUser--;}
            if(attkEnem > 0) { attkEnem--;}
  
            switch(result){
                case 1:
                    System.out.println("You win");
                    userWin++;
                    break;
                case 2:
                    System.out.println(enemy.getName() + " wins");
                    enemyWin++;
                    break;
                case 0:
                    System.out.println("Nobody win");
            } 
        }
        System.out.println("- FIGHT FINISHED -");
        //the method winner does the rest 
        winner(userWin, enemyWin);
          
    }
    
    public static void winner(int userWin, int enemyWin) throws SQLException, MarvelException{
        //wins stats results
        System.out.print(userLogged.getName() + ": "+ userWin + " wins. - ");
        System.out.print(enemy.getName() + ": "+ enemyWin + " wins.\n");
       
        /*     Si gana el usuario (su número de victorias es mayor a la del villano) 
        y al villano no le quedan ataques, el usuario ganará 5 puntos. 
        En caso de que el villano tuviese gemas, las perdería y quedarían libres
        en el lugar donde se encuentran. El villano huiría a un lugar diferente. */
       
        /*      Si el jugador ha ganado, al subir los puntos se comprobará 
        si ha llegado a 50. Si es así, subirá de nivel y se reiniciará el 
        contador de puntos a 0, o con los puntos sobrantes en caso de superar los 50.
        En caso de que pierda se debe tener en cuenta que los puntos nunca 
        pueden ser negativos. */

        if(userWin > enemyWin){
            System.out.println(userLogged.getName() + " win");
            
             //TODO: with allGems ¿do we really need getGemsOwned() method?
             //if enemy got gems, he loses them
            for(Gem g: allGems){
                if(g.getOponentName().equals(enemy.getName())){
                    g.setOponent(null);
                }
            }
            //this can be deleted...
            if(enemy.getGemsOwned().size() > 0){
               System.out.println("The enemy has lost their gems");
               for(Gem g: enemy.getGemsOwned()){
                   System.out.println(g.getName());
               }
               enemy.setGemsOwned(new ArrayList<Gem>());
            }
            
            //update of user stats
            userLogged.setPoints(userLogged.getPoints() + 5);
            System.out.println("You win 5 points.");
            System.out.println("Your points: "+ userLogged.getPoints());
            if(userLogged.getPoints() >= 50){
                int diff = userLogged.getLevel() - 50;
                userLogged.setLevel(userLogged.getLevel() + 1);
                System.out.println("INCREASED LEVEL! Your level now is " + userLogged.getLevel() );
                userLogged.setPoints(diff);
                System.out.println("Your points now: " + userLogged.getPoints());
            }
           
            //enemy goes to another place (random)
            enemy.setPlace(manager.newPlaceForEnemy(enemy));
            System.out.println(enemy.getName() + " has disappeared");
             
        }
         /*Si gana el villano y al usuario no le quedan ataques, el usuario
        pierde 2 puntos y en caso de tener gemas, pasarían a ser propiedad 
        del villano. El villano se desplazaría a otro lugar y se llevaría 
        las gemas con él. */
        
        else if(enemyWin > userWin){
            System.out.println(enemy.getName() + " win");
            
            for(Gem g: allGems){
                if(g.getOponentName().equals(userLogged.getName())){
                    g.setOponent(enemy);
                }
            }
            //TODO
            //the gems from player goes to enemy POSSIBLE DELETE
            if(userLogged.getGemsOwned().size() > 0){
                System.out.println("The enemy has stolen your gems");
                List<Gem> gemsPlayer = userLogged.getGemsOwned();
                for(Gem g: gemsPlayer){
                  enemy.addGem(g);
                  System.out.println(g.getName());
                }
                userLogged.setGemsOwned(new ArrayList<Gem>()); 
            }
           
            
            //update of user stats
            if(userLogged.getPoints() >= 2){
                userLogged.setPoints(userLogged.getPoints() - 2);
            }
            
            //enemy goes to another place
            enemy.setPlace(manager.newPlaceForEnemy(enemy)); 
            System.out.println(enemy.getName() + " has disappeared");
        }
        
         /*En caso de empate, sin ataques por parte de ninguno de los lados,
        el villano huiría a otro lugar y no afectaría ni a gemas ni a puntos
        de nadie. */
        else{
            System.out.println("Nobody win");
            //enemy goes to another place (random)
            enemy.setPlace(manager.newPlaceForEnemy(enemy));
            System.out.println(enemy.getName() + " has disappeared");
        }
        
        for(Gem g: allGems){
            System.out.println(" proof 2 " + g.toString());
        }
        
        //new information is saved into database (enemy, user, gem tables)
        manager.saveDataAfterBattle(userLogged, enemy, allGems);
        
    }
    
    public static int attack(int attkUser, int attkEnem){
        System.out.println("You got " + attkUser + " attacks");
        System.out.println(enemy.getName() + " got "+ attkEnem + " attacks");
        String[] options = {"spock","scissor","paper","rock","lizard"};
        String op1 = options[randomGen(0, options.length-1)];
        System.out.println("Player attack: "+ op1);
        String op2 = options[randomGen(0, options.length-1)];
        System.out.println(enemy.getName() + " attack: " + op2);
        return manager.pppGame(op1, op2);
    }
    
    public static int randomGen(int min, int max){
        Random rand = new Random();
        int value = rand.nextInt((max - min) + 1) + min;
        return value;
    }

    
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
    
    /**
     * Deletes a user
     * @throws SQLException
     * @throws MarvelException 
     */
    public static void delete() throws SQLException, MarvelException {
        manager.deleteUser(userLogged, input[1]);
        System.out.println("User deleted");
        userLogged = null;
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


/* public class ThreadManager implements runnable
   private HotelManager;
  private int milis;
En esta clase es donde estará la lógica del thread. 

luego cuando en main hago threadManager.run() y lo instancio. Podria instanciar
25 threads.

enum
public enum CrewSercices{
  MANTENIMIENTO, ETC
public static CrewServices selectService(String extra){
   switch(extra.toUppercase(){
      case("MANTENIMIENTO"):
             RETURN crewServices.MANTENIMIENTO;
case ...
*/