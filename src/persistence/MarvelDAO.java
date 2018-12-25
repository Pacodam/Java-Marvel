/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Superhero;

public class MarvelDAO {
    
    private Connection connection;
    
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
        String password = "root";
        connection = DriverManager.getConnection(url, user, password);
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}

