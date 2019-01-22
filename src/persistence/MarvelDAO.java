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
import model.Rank;
import model.Superhero;
import model.User;
import model.gems.Gem;

public class MarvelDAO {
    
    private Connection connection;
    
    /**
     * Query to obtain rankings
     *  select username, superhero, count(owner) as 'gems', level, points
         from user inner join gem on username = owner
         group by username
         order by gems desc
     * @return
     * @throws SQLException 
     */
    public List<Rank> getRankings() throws SQLException{
        List<Rank> ranking = new ArrayList<>();
        Statement st = connection.createStatement();
        String query = "select username, superhero, count(owner) as 'gems', level, points"
                + " from user inner join gem on username = owner"
                + " group by username"
                + " order by gems desc";
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            Rank r = new Rank();
            fillRank(r, rs);
            ranking.add(r);
        }
        rs.close();
        st.close();
        return ranking;        
    }
    
    /**
     * Fill Rank objects
     * @param r
     * @param rs
     * @throws SQLException 
     */
    public void fillRank(Rank r, ResultSet rs) throws SQLException{
        r.setUser(rs.getString("username"));
        r.setSuperhero(rs.getString("superhero"));
        r.setNumGems(rs.getInt("gems"));
        r.setLevel(rs.getInt("level"));
        r.setPoints(rs.getInt("points"));
    }
      
    /**
     * Pass check for deleting
     * @param u User
     * @param pass String
     * @throws SQLException
     * @throws MarvelException 
     */
    public void checkPass(User u, String pass) throws SQLException, MarvelException{
        Statement st = connection.createStatement();
        String query = "select * from user where username='" + u.getName() + "';";
        ResultSet rs = st.executeQuery(query);
        boolean exist = rs.next();
        if(!rs.getString("pass").equals(pass)){
           throw new MarvelException(MarvelException.DELETE_ABORT);
        }  
        rs.close();
        st.close();
    }
    
    /**
     * Deletes user from database
     * @param u User
     * @param pass String
     * @throws SQLException
     * @throws MarvelException 
     */
    public void deleteUser(User u, String pass) throws SQLException, MarvelException{
        checkPass(u, pass);
        Statement st = connection.createStatement();
        connection.setAutoCommit(false);
        try{
            String deleteUser = "delete from user where username = '"+ u.getName()+"'";
            String deleteGems = "delete from gem where user = '"+u.getName()+"'";
            st.executeUpdate(deleteUser);
            st.executeUpdate(deleteGems);
            connection.commit(); 
        }catch(SQLException err){
            connection.rollback();
            throw new SQLException();
        }finally {
            connection.setAutoCommit(true);
            st.close();
        }  
    }
    
    /**
     * Update User, Enemy and gems after battle
     * @param u User
     * @param e Enemy
     * @param g List Gem
     * @throws SQLException 
     */
    public void updateBattle(User u, Enemy e, List<Gem> g) throws SQLException{
        
        Statement st = connection.createStatement();
        connection.setAutoCommit(false);
        try{
            String updateUser = "update user set points ='" + u.getPoints() + "', level = "
                    + "'"+ u.getLevel() + "' where username='"+ u.getName() + "'";
            String updateEnemy = "update enemy set place ='" + e.getPlace().getName() + 
                    "' where name='"+ e.getName() + "'";
            st.executeUpdate(updateUser);
            st.executeUpdate(updateEnemy);
            //updateGems(g, u.getName());
            connection.commit();
        }catch(SQLException err){
            connection.rollback();
            throw new SQLException();
        }finally {
            connection.setAutoCommit(true);
        }      
    }
    
    /*
    public void updateGems(List<Gem> newGems, String user) throws SQLException {
        for(Gem g: newGems){
            String update = "update gem set owner = '"+ g.getOponentName() +"', place = '"+user.getPlace().getName()+
                "' where name = '"+gem+"' and user = '"+user.getName()+"'";
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
    */
    
    /**
     * Selects all gems by User
     * @param u user
     * @return List Gem
     * @throws SQLException
     * @throws MarvelException 
     */
    public List<Gem> selectAllGems(User u) throws SQLException, MarvelException{
        List<Gem> gems = new ArrayList<>();
        Statement st = connection.createStatement();
        String select = "select * from gem where user = '" + u.getName() + "'";
        ResultSet rs = st.executeQuery(select);
        while (rs.next()) {
            Gem g = new Gem();
            fillGem2(u, g, rs);
            gems.add(g);
        }
        rs.close();
        st.close();
        return gems;
    }

    /**
     * Return all places where Enemy passed is not there
     * @param e Enemy
     * @return List String
     * @throws SQLException 
     */
    public List<String> allPlacesNamesNoEnemy(Enemy e) throws SQLException {
        List<String> placesNames = new ArrayList<>();
        Statement st = connection.createStatement();
        String query = "select name from place where name != '" + e.getPlace().getName()+"'";
        ResultSet rs = st.executeQuery(query);
        while(rs.next()){
            placesNames.add(rs.getString("name"));
        }
        return placesNames;
    }
    
    /**
     * When the player gets a new gem, database gem is updated
     * @param user User
     * @param gem String
     */
    public void updateGems(User user, String gem) throws SQLException{
        Statement st = connection.createStatement();
        String username = user.getName();
        String update = "update gem set owner = '"+user.getName()+"', place = '"+user.getPlace().getName()+
                "' where name = '"+gem+"' and user = '"+user.getName()+"'";
        st.executeUpdate(update);
        st.close();
    }
    
    /**
     * Updates the place of the User
     * @param user User
     * @throws SQLException 
     */
    public void updateUserPlace(User user) throws SQLException{
        Statement st = connection.createStatement();
        String username = user.getName();
        String newPlace = user.getPlace().getName();
        String update = "update user set place='" + newPlace + "' where username='"+ username + "'";
        st.executeUpdate(update);
        st.close();
    }
    
    /**
     * returns Gem list from a given Place 
     * @param player User
     * @return list Gem
     */
    public List<Gem> getGemsByPlace(User player) throws SQLException, MarvelException{
        //basta con pasar al userrrr
        List<Gem> gems = new ArrayList<>();
        Statement st = connection.createStatement();
        String query = "select * from gem where place='" + player.getPlace().getName() + "' and user='" + player.getName() + "';";
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            Gem gem = new Gem();
            fillGem1(gem, rs, player.getName(), player.getPlace().getName());
            gems.add(gem);
        }
        rs.close();
        st.close();
        return gems;
    }
    
    /**
     * Fill gem
     * @param gem Gem
     * @param rs ResultSet
     * @param username String
     * @param place Place
     * @throws SQLException
     * @throws MarvelException 
     */
    public void fillGem1(Gem gem, ResultSet rs, String username, String place) throws SQLException, MarvelException{
        gem.setName(rs.getString("name"));
        gem.setUser(userByName(username));
        String owner = rs.getString("owner");
        if(owner == null){
          gem.setOponent(null);
        }
        else if(owner.equals(username)){
            gem.setOponent(userByName(username));
        }
        else{
           gem.setOponent(enemyByName(userByName(username),owner)) ;
        }
        gem.setPlace(getPlaceByName(place));
    }
    
    /**
     * Returns User from a query by a username
     * @param username String
     * @return User
     * @throws SQLException
     * @throws MarvelException 
     */
    public User userByName(String username) throws SQLException, MarvelException{
        Statement st = connection.createStatement();
        String query = "select * from user where username='" + username + "';";
        ResultSet rs = st.executeQuery(query);
        boolean exist = rs.next();
        User u = new User();
        fillUser(rs, u);
        rs.close();
        st.close();
        return u;
    }
    
    /**
     * Returns Enemy from a query by his name
     * @param userLogged
     * @param name
     * @return
     * @throws SQLException
     * @throws MarvelException 
     */
    public Enemy enemyByName(User userLogged, String name) throws SQLException, MarvelException {
        Statement st = connection.createStatement();
        String query = "select * from enemy where name='" + name + "';";
        ResultSet rs = st.executeQuery(query);
        boolean exist = rs.next();
        Enemy e = new Enemy();
        if(exist){
           fillEnemy(e, rs);
        }
        rs.close();
        st.close();
        //we add gems owned by enemy
        e.setGemsOwned(currentGemsOwnedByEnemy(userLogged, e));
        return e;
    }
    
    public List<Gem> currentGemsOwnedByEnemy(User userLogged, Enemy e) throws SQLException, MarvelException{
        List<Gem> gemsOwned = new ArrayList<>();
        Statement st = connection.createStatement();
        //System.out.println("user name: "+ userLogged.getName() + " enemy " + e.getName());
        String query = "select * from gem where user = '" + userLogged.getName() + "' and owner ='" + e.getName() + "';";
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            Gem g = new Gem();
            fillGem3(userLogged, e, g, rs);
            gemsOwned.add(g);
            //System.out.println("gem name: " + g.getName());
        }
        rs.close();
        st.close();
        return gemsOwned;
    }
    
    public void fillGem3(User userLogged, Enemy e, Gem g, ResultSet rs) throws SQLException, MarvelException{
        g.setName(rs.getString("name"));
        g.setUser(userLogged);
        g.setOponent(e);
        g.setPlace(getPlaceByName(rs.getString("place")));
    }
    
    
    public User loginCheck(String username, String password) throws SQLException, MarvelException{
        Statement st = connection.createStatement();
        String query = "select * from user where username='" + username + "';";
        ResultSet rs = st.executeQuery(query);
        boolean exist = rs.next();
        if(!exist){
            throw new MarvelException(MarvelException.WRONG_US_PASS);
        }
        else if(!rs.getString("pass").equals(password)){
             throw new MarvelException(MarvelException.WRONG_US_PASS);
        }
        User u = new User();
        fillUser(rs, u);
        //here we add to the user object the gems owned
        u.setGemsOwned(currentGemsOwned(u));
        rs.close();
        st.close();
        return u;
    }
    
    public List<Gem> currentGemsOwned(User u) throws SQLException, MarvelException{
        List<Gem> gemsOwned = new ArrayList<>();
        Statement st = connection.createStatement();
        String query = "select * from gem where owner ='" + u.getName() + "';";
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            Gem g = new Gem();
            fillGem2(u, g, rs);
            gemsOwned.add(g);
        }
        rs.close();
        st.close();
        return gemsOwned;
    }
    
    public void fillGem2(User u, Gem gem, ResultSet rs) throws SQLException, MarvelException{
        String user = rs.getString("user");
        gem.setName(rs.getString("name"));
        gem.setUser(userByName(user));
        String owner = rs.getString("owner");
        if(owner == null){
          gem.setOponent(null);
        }
        else if(owner.equals(user)){
            gem.setOponent(userByName(user));
        }
        else{
           gem.setOponent(enemyByName(u,owner)) ;
        }
        gem.setPlace(getPlaceByName(rs.getString("place")));
    }
    
    /**
     * Creates User object
     * @param rs ResultSet
     * @param u User
     * @throws SQLException
     * @throws MarvelException 
     */
    public void fillUser(ResultSet rs, User u) throws SQLException, MarvelException{
        u.setName(rs.getString("username"));
        u.setPass(rs.getString("pass"));
        u.setLevel(rs.getInt("level"));
        u.setSuperhero(getSuperHeroByName(rs.getString("superhero")));
        u.setPlace(getPlaceByName(rs.getString("place")));
        u.setPoints(rs.getInt("points"));
    }
     
    
    
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

