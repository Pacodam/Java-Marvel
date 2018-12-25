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
import model.Place;
import model.Superhero;

public class MarvelDAO {
    
    private Connection connection;
        
    public List<Place> selectAllPlaces() throws SQLException{
        
        
    }
    public Place getPlaceByName(String name) throws MarvelException, SQLException {
        Place aux = new Place(name);
        Statement st = connection.createStatement();
        String select = "select * from place where name ='"+name+"'";
        ResultSet rs = st.executeQuery(select);
        Place p = new Place();
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setNorth(rs.getString("north"));
        rs.close();
        st.close();
        return p;
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
        s.setName(rs.getString("name"));
        s.setSuperpower(rs.getString("superpower"));
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

