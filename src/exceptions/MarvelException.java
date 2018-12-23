/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author alu2017454
 */
public class MarvelException extends Exception {
    
    public static final int WRONG_COMMAND = 0;
    public static final int INCORRECT_NUM_ARGS = 1;
    public static final int USER_EXISTS = 2;
    public static final int HERO_NO_EXISTS = 3;
    public static final int WRONG_US_PASS = 4;
    public static final int NOT_LOGGED = 5;
    public static final int MOVE_UNALLOWED = 6;
    public static final int NO_GEM_NAME = 7;
    public static final int GAME_FINISHED = 8;
    public static final int ENEMY_NO_EXISTS_HERE = 9;
    public static final int DELETE_ABORT = 10;
    
    private int code;
    
    private final List<String> messages = Arrays.asList(
        "[ Wrong command ]",
        "[ Wrong number of arguments ]",
        "[ User already exists ]",
        "[ There isn't a superhero with that name ]",
        "[ Username or password is incorrect ]",
        "[ You are not logged in ]",
        "[ You can't move in that direction ]",
        "[ Here there is no gem with that name ]",
        "[ You already finish your game ]",
        "[ Here there is no enemy with that name ]",
        "[ Delete aborted. Your password is wrong ]");
           
            
    public MarvelException(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return messages.get(code);
    }
    
    
}
