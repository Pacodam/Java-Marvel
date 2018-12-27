/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import exceptions.MarvelException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Enemy;
import model.Oponent;
import model.Place;
import model.Superhero;
import model.User;
import model.gems.Gem;

public class MarvelDAO {
    
    private Connection connection;
       
    
    public void insertNewUser(User u) throws SQLException {
        System.out.println(u.getName());
        System.out.println(u.getPass());
        System.out.println(u.getLevel());
        PreparedStatement ps = connection.prepareStatement("insert into user values (?, ?, ?, ?, ?, ?)");
        ps.setString(1, u.getName());
        ps.setString(2, u.getPass());
        ps.setInt(3, u.getLevel());
        ps.setString(4, u.getSuperhero().getName());
        ps.setString(5, u.getPlace().getName());
        ps.setInt(6, u.getPoints());
        ps.executeUpdate();
        ps.close();
    }
    
    public void insertNewGems(List<Gem> newGems) throws SQLException {
        for(Gem g: newGems){
            PreparedStatement ps = connection.prepareStatement("insert into gem values (?, ?, ?, ?)");
            ps.setString(1, g.getName());
            ps.setString(2, g.getUser().getName());
            //A gem can have a null owner at the beginning
            if(g.getOponent() != null){
              ps.setString(3, g.getOponent().getName());
            }
            else{
               ps.setNull(3, java.sql.Types.VARCHAR);
            }
            ps.setString(4, g.getPlace().getName());
            ps.executeUpdate();
            ps.close();
        }
    }
    
    public List<Enemy> getEnemies() throws SQLException, MarvelException {
        List<Enemy> allEnemies = new ArrayList<>();
        Statement st = connection.createStatement();
        String select = "select * from enemy";
        ResultSet rs = st.executeQuery(select);
        while (rs.next()) {
            Enemy e = new Enemy();
            fillEnemy( e, rs);
            allEnemies.add( e);
        }
        rs.close();
        st.close();
        return allEnemies;
    }
    
     public void fillEnemy(Enemy e, ResultSet rs) throws SQLException, MarvelException {
        e.setName(rs.getString("name"));
        e.setDebility(rs.getString("debility"));
        e.setLevel(rs.getInt("level"));
        e.setPlace(getPlaceByName(rs.getString("place")));
    }
    
    public  String[] getNameOfPlaces() throws SQLException{
        List<Place> allPlaces = selectAllPlaces();
        String[] placesName = new String[allPlaces.size()];
        for(int i = 0; i < allPlaces.size(); i++){
            placesName[i] = allPlaces.get(i).getName();
        }
        return placesName;
    }
    /**
     * Returns a list of all places in BBDD.
     * @return List place
     * @throws SQLException 
     */
    public List<Place> selectAllPlaces() throws SQLException{
        List<Place> allPlaces = new ArrayList<>();
        List<String> north = new ArrayList<>();
        List<String> south = new ArrayList<>();
        List<String> east = new ArrayList<>();
        List<String> west = new ArrayList<>();
        Statement st = connection.createStatement();
        String select = "select * from place";
        ResultSet rs = st.executeQuery(select);
        while (rs.next()) {
            Place p = new Place();
            fillPlace(p, rs);
            allPlaces.add(p);
            north.add(rs.getString("north"));
            south.add(rs.getString("south"));
            east.add(rs.getString("east"));
            west.add(rs.getString("west"));
        }
        rs.close();
        st.close();
        addDirections(allPlaces, north, south, east, west);
       
        return allPlaces;
    }
    
    public static void addDirections(List<Place> places, List<String> north, List<String> south, List<String> east, List<String> west){
       for(int i = 0; i < places.size(); i++){
            Place actual = places.get(i);
            String n = north.get(i);
            String s = south.get(i);
            String e = east.get(i);
            String w = west.get(i);
            for(Place p: places){
                if(p.getName().equals(n)){
                    actual.setNorth(p);
                }
                if(p.getName().equals(s)){
                    actual.setSouth(p);
                }
                if(p.getName().equals(e)){
                    actual.setEast(p);
                }
                if(p.getName().equals(w)){
                    actual.setWest(p);
                }
            }
            
       }
    }
    /**
     * Adds name and description to a place object
     * @param p
     * @param rs
     * @throws SQLException 
     */
    public void fillPlace(Place p, ResultSet rs) throws SQLException {
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
    }
           
    /**
     * Returns a place with all its attributes (included directions Places)
     * @param name
     * @return
     * @throws MarvelException
     * @throws SQLException 
     */  
    public Place getPlaceByName(String name) throws MarvelException, SQLException {
        List<Place> allPlaces = selectAllPlaces();
        for(Place p: allPlaces){
            if(p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param heroName String
     * @return
     * @throws SQLException
     * @throws MarvelException 
     */
    public Superhero getSuperHeroByName(String heroName) throws SQLException, MarvelException {
        Superhero aux = new Superhero(heroName);
        if(!existSuperHero(aux)){
            throw new MarvelException(MarvelException.HERO_NO_EXISTS);
        }
        Statement st = connection.createStatement();
        String select = "select * from superhero where name ='"+heroName+"'";
        ResultSet rs = st.executeQuery(select);
        Superhero s = new Superhero();
        if(rs.next()){
          s.setName(rs.getString("name"));
          s.setSuperpower(rs.getString("superpower"));
        }
        rs.close();
        st.close();
        return s;
    }
    
    private boolean existSuperHero(Superhero s) throws SQLException {
        Statement st = connection.createStatement();
        String query = "select * from superhero where name='" + s.getName() + "';";
        ResultSet rs = st.executeQuery(query);
        boolean exist = rs.next();
        rs.close();
        st.close();
        return exist;
    }
    
    /**
     * If there is an username in database with name passed 
     * by parameter, throws MarvelException.USER_EXISTS
     * @param username String
     * @throws SQLException
     * @throws MarvelException 
     */
    public void checkUsername(String username) throws SQLException, MarvelException {
       
        Statement st = connection.createStatement();
        String select = "select count(username) from user where username ='"+username+"'";
        ResultSet rs = st.executeQuery(select);
        rs.last();                 
        int counter = rs.getInt(1);
        if(counter == 1){
            throw new MarvelException(MarvelException.USER_EXISTS);
        }
        rs.close();
        st.close();
    }
    
                                 //option v view superheroes
    
    public List<Superhero> selectAllHeroes() throws SQLException {
        
        List<Superhero> allHeroes = new ArrayList<>();
        Statement st = connection.createStatement();
        String select = "select * from superhero";
        ResultSet rs = st.executeQuery(select);
        while(rs.next()) {
            Superhero s = new Superhero();
            fillHero(s, rs);
            allHeroes.add(s);
        }
        rs.close();
        st.close();
        return allHeroes;
    }
    
    public void fillHero(Superhero s, ResultSet rs) throws SQLException {
        s.setName(rs.getString("name"));
        s.setSuperpower(rs.getString("superpower"));
    }
    
    
    
    public void connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/marvel";
        String user = "root";
        String password = "";
        connection = DriverManager.getConnection(url, user, password);
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}

