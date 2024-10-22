package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.GameData;
import model.UserData;
import dataaccess.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import requests.CreateUserRequest;
import results.CreateUserResult;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // I want this next function to return a username field and an authToken field, maybe I will fix this when I make a
    // response class?

    public CreateUserResult createUser(CreateUserRequest user) throws DataAccessException {
        return dataAccess.createUser(user.username(), user.password(), user.email());
    }

    // I want this function to return a username field and an authtoken field

    public UserData loginUser(String username, String password) throws DataAccessException {
        UserData user = dataAccess.getUser(username);

        if (!Objects.equals(user.password(), password)) {
            throw new DataAccessException("Incorrect Password for Username");
        }

        return user;
    }

    // I want this function check my authtoken first and then delete my authtoken

    public Object logoutUser(String authToken) throws DataAccessException {
        return 0;
    }

    // I want this function to check to see if my authToken is valid, and then it should return a list of games

    public Collection<GameData> listGames (String authToken) throws DataAccessException {
        try {
            // Call getAuth and try to retrieve the AuthData
            dataAccess.getAuth(authToken);
            return dataAccess.listGames();
        } catch (DataAccessException e) {
            System.out.println("Error retrieving authentication data: " + e.getMessage());
        }

        return Collections.emptyList();
    }

    // I want this function to check my auth token, check for a game and then add a new game to the game list

    public Object createGame(String authToken, String gameName) throws DataAccessException {
        return 0;
    }

    // This function first checks to see if our auth token is valid, then it checks for the desired game, if the
    // game exists, then it will check to see if there is already a player for the desired team. If all goes well
    // it will let the user join the game and update the game

    public Object joinGame(String authToken, ChessGame.TeamColor teamColor, int gameID) throws DataAccessException {
        return 0;
    }

    // This function calls the clear function for each of our dataaccess objects and cleans out the database

    public Object clear() throws DataAccessException {
        return 0;
    }


}
