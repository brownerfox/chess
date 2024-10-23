package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import dataaccess.DataAccessException;
import requests.CreateUserRequest;
import results.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // I want this next function to return a username field and an authToken field, maybe I will fix this when I make a
    // response class?

    public CreateUserResult createUser(CreateUserRequest user) throws ServiceException, DataAccessException {
        UserData newUser = dataAccess.createUser(user.username(), user.password(), user.email());

        AuthData authData = dataAccess.createAuth(user.username());

        return new CreateUserResult(newUser.username(), authData.authToken());
    }

    // I want this function to return a username field and an authtoken field

    public LogInResult loginUser(String username, String password) throws DataAccessException {
        UserData user = dataAccess.getUser(username);

        if (!(Objects.equals(user.password(), password))) {
            throw new DataAccessException("Error: unauthorized");
        }

        return new LogInResult(username, dataAccess.createAuth(user.username()).authToken());
    }

    // I want this function check my authtoken first and then delete my authtoken

    public Object logoutUser(String authToken) throws DataAccessException {
        dataAccess.deleteAuth(authToken);

        return new LogOutResult();
    }

    // I want this function to check to see if my authToken is valid, and then it should return a list of games

    public ListGamesResult listGames (String authToken) throws DataAccessException {

        // Call getAuth and try to retrieve the AuthData
        dataAccess.getAuth(authToken);

        return new ListGamesResult(dataAccess.listGames());
    }

    // I want this function to check my auth token, check for a game and then add a new game to the game list

    public CreateGameResult createGame(String authToken, String gameName) throws DataAccessException {
        dataAccess.getAuth(authToken);

        return new CreateGameResult(dataAccess.createGame(gameName));
    }

    // This function first checks to see if our auth token is valid, then it checks for the desired game, if the
    // game exists, then it will check to see if there is already a player for the desired team. If all goes well
    // it will let the user join the game and update the game

    public JoinGameResult joinGame(String authToken, String teamColor, int gameID) throws ServiceException, DataAccessException {
        GameData newGame;
        AuthData authData = dataAccess.getAuth(authToken);

        GameData game = dataAccess.getGame(gameID);

        if (Objects.equals(teamColor, "WHITE")) {
            if (game.whiteUsername() == null) {
                newGame = new GameData(gameID, authData.username(), game.blackUsername(), game.gameName(), game.game());
            } else {
                throw new ServiceException("");
            }
        } else {
            if (game.blackUsername() == null) {
                newGame = new GameData(gameID, game.whiteUsername(), authData.username(), game.gameName(), game.game());
            } else {
                throw new ServiceException("");
            }
        }

        dataAccess.updateGame(newGame);

        return new JoinGameResult();
    }

    // This function calls the clear function for each of our dataaccess objects and cleans out the database

    public Object clear() throws DataAccessException {
        dataAccess.clear();

        return new ClearResult();
    }


}
